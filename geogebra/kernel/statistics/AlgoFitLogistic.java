package geogebra.kernel.statistics;

/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.statistics.RegressionMath;


/** 
 * @author Hans-Petter Ulven
 * @version 19.11.08	(19 nov.)
 * 
 *
 * 
 * Fits c/(1+aexp(-bx))  to a list of points.
 * Adapted from:
 *    	Nonlinear regression algorithms are well known, see:
 *      	mathworld.wolfram.com/NonlinearLeastSquaresFitting.html 
 * 			ics.forth.gr/~lourakis/levmar
 * 			Damping Parameter in Marquardt's Method, Hans Bruun Nielsen, IMM-Rep 1999-05
 * 
 * The problem is to find the best initial values for the parameters,
 * little information available on this problem...
 *  
 *		Bjørn Ove Thue, the norwegian translator and programmer of 
 *		the norwegian version of WxMaxima, was kind enough to give me
 *		his idea:
 *		Make the assumption that the first and last point are
 *		close to the solution curve.
 *		Calculate c and a from those points, with b as parameter,
 *		iterate to a good value for b,
 *		and do the final nonlinear regression iteration with all three parameters.
 * 
 * 	Constraints:
 * 		<List of points> should have at least 3 points.
 *		The first and last datapoint should not be too far from the solution curve.
 * 
 * 	Problems:
 * 		Non-linear regression is difficult, and the choice
 * 		of initial values for the parameters are highly critical.
 * 		The algorithm here might converge to a local minimum.
 * 		I am not sure if the algorithm in some cases might diverge or oscillate?
 * 		(Then it would stop after MAXITERATIONS and give a bad fit.)
 *  
 *  Possible future solution:
 *  	Make more commands where you give both a list and suggestions for the parameters?
 */


public final class AlgoFitLogistic extends AlgoElement{
	
    //Tuning of noisefilter, Levenberg-Marquardt iteration, debug, rounding off errors
	private final static double	LMFACTORDIV		=	3.0d;	
	private final static double LMFACTORMULT	=	2.0d;
	private final static int	MAXITERATIONS	=	200;
	private final static double EPSILONFIND		=	1E-6d;
	private final static double EPSILONREG		=	1E-14d;
	private final static double EPSSING			=	1E-20d;
	private final static boolean DEBUG			=	false;		//set false when finished
	
	// Properties
	private static geogebra.main.Application app=	null;
	private static geogebra.kernel.Kernel 	 k  =   null;
    private static double 		a,b,c;				//c/(1+a*exp(-bx))
    private static double[] 	xd,yd;				//datapoints
    private static int      	size;				//of xd and yd
    private static int			iterations;			//LM iterations
    private static boolean  	error           =   false;	//general error flag	
    private static RegressionMath regMath		=	null;	//pointer to det33
    
	//GeoGebra obligatory:
    private static final long serialVersionUID  =   1L;
    private GeoList         geolist;                        //input
    private GeoFunction     geofunction;                    //output
    
    public AlgoFitLogistic(Construction cons, String label, GeoList geolist) {
        super(cons);
        app=kernel.getApplication();
        k=app.getKernel();
        this.geolist=geolist;
        geofunction=new GeoFunction(cons);
        setInputOutput();
        compute();
        geofunction.setLabel(label);
    }//Constructor
    
    protected String getClassName() {return "AlgoFitLogistic";}
        
    protected void setInputOutput(){
        input=new GeoElement[1];
        input[0]=geolist;
        output=new GeoElement[1];
        output[0]=geofunction;
        setDependencies();
    }//setInputOutput()
    
    public GeoFunction getFitLogistic() {return geofunction;}
    
    protected final void compute() {
        size=geolist.size();
        error=false;				//General flag
        if(!geolist.isDefined() || (size<3) ) {	//Direction-algo needs two flanks, 3 in each.
            geofunction.setUndefined();
            errorMsg("List not properly defined or too small (5 points needed).");
            return;
        }else{
        	getPoints();		//sorts the points on x while getting them!
            doReg();
            if(!error){
            	//a=2.0d;c=3.0d;b=0.5d;
                MyDouble A=new MyDouble(kernel,a);
                MyDouble B=new MyDouble(kernel,-b);
                MyDouble C=new MyDouble(kernel,c);
                MyDouble ONE=new MyDouble(kernel,1.0d);
                FunctionVariable X=new FunctionVariable(kernel);
                ExpressionValue expr=new ExpressionNode(kernel,B,ExpressionNode.MULTIPLY,X);
                				expr=new ExpressionNode(kernel,expr,ExpressionNode.EXP,null);
                				expr=new ExpressionNode(kernel,A,ExpressionNode.MULTIPLY,expr);
                				expr=new ExpressionNode(kernel,ONE,ExpressionNode.PLUS,expr);
                ExpressionNode node=new ExpressionNode(kernel,C,ExpressionNode.DIVIDE,expr);
                Function f=new Function(node,X);
                geofunction.setFunction(f); 
                geofunction.setDefined(true);
            }else{
                geofunction.setUndefined();
                return;  
            }//if error in regression   
        }//if error in parameters
    }//compute()
    

    
/// ============= IMPLEMENTATION =============================================================///
    public final static  void doReg(){
    	regMath=k.getRegressionMath();    	
    	//runTest();				/////////////////////////////////////Comment out when testing done /////////////////////////////		
        findParameters();       //Find initial parameters a,b,c,d
        Logistic_Reg();            //Run LM nonlinear iteration
    }//doReg()
    
    public final static void findParameters() {
        double err,err_old,diff;
        double lambda=0.01d;
        double k=0.0d;
        //19.11 double beta=0.0d;
        k=0.001;
        
        														//debug("findParameters():\n================");
        
        //Remember some values:
        x1=xd[0];y1=yd[0];x2=xd[size-1];y2=yd[size-1];    
        ymult=y1*y2;e1=Math.exp(x1);e2=Math.exp(x2);emult=e1*e2;ydiff=y1-y2;
        
        err_old=beta2(xd,yd,k);
        k=k+lambda;
        err=err_old+1; //to start off the while:
        while(Math.abs(err-err_old)>EPSILONFIND){
            err=beta2(xd,yd,k);
            diff=err-err_old;
            if(err<err_old){
                lambda=lambda*5;        //going right way:-)
                err_old=err;
                err=err+1;          //to keep going...
            }else{
                k=k-lambda;             //go back and try again
                lambda=lambda/5;
            }//if progress
            k+=lambda;
            											//debug("b, error-error_old: "+k+"  ,  "+diff);
        }//while reduction in error
        //Set params for final iteration:
        b=k;        //next routine uses c,a,b...
        a=a(x1,y1,x2,y2,k);
        c=c(x1,y1,x2,y2,k);
        												//debug("\nfindParameters()finished with:\n"+a+" b= "+b+" c= "+c);
        												//debug("Sum sq. errors: "+beta2(xd,yd,a,b,c)+"\n----------------------------------------");
    }//findParameters()

    public final static void Logistic_Reg(){
 
        double 	lambda			=	0.0d;          	//LM-damping coefficient
        double 	multfaktor		=	LMFACTORMULT;	// later?: divfaktor=LMFACTORDIV;
        double 	residual,
        	   	old_residual	=	beta2(xd,yd,a,b,c);
        double 	diff			=	-1.0d;         	//negative to start it off
        
        double 	da=EPSILONREG,db=EPSILONREG,dc=EPSILONREG;     //Something larger than eps, to get started...
        double 	b1,b2,b3;                     //At*beta
        double 	m11,m12,m13,
        		m21,m22,m23,
                m31,m32,m33,				//At*A
                n;                               //singular check
        double x,y;
        double dfa,dfb,dfc,beta,
               newa,newb,newc;
        iterations=0;
        //****checked up to here
        // LM: optimal startlambda
        b1=b2=b3=0.0d;
        m11=m22=m33=0.0d;
        for(int i=0;i<size;i++) {
            x=xd[i];y=yd[i];
            beta=beta(x,y,a,b,c);
            dfa=df_a(x,a,b,c);     dfb=df_b(x,a,b,c);    dfc=df_c(x,a,b);
            //b=At*beta
            b1+=beta*dfa; b2+=beta*dfb;  b3+=beta*dfc;
            //m=At*A
            m11+=dfa*dfa;       //only need diagonal
            m22+=dfb*dfb;    
            m33+=dfc*dfc;   
        }//for all datapoints
 
        double startfaktor=Math.max(Math.max(m11,m22),m33);           
        lambda=startfaktor*0.001;   //heuristic...                      (Set to zero if no LM)                                  
                                                                        //debug("Startlambda: "+lambda);
                                                                    

        while(Math.abs(da)+Math.abs(db)+Math.abs(dc)>EPSILONREG){//or while(Math.abs(diff)>EPSILON) ?
            iterations++;                                                //debug(""+iterations+"   : \n---------------");
            if((iterations>MAXITERATIONS)||(error)){                //From experience: >200 gives nothing more...
            	errorMsg("More than "+MAXITERATIONS+" iterations...");
            	break;
            }
            b1=b2=b3=0.0d;
            m11=m12=m13=
            m21=m22=m23=
            m31=m32=m33=0.0d;
            for(int i=0;i<size;i++) {
                x=xd[i];y=yd[i];
                beta=beta(x,y,a,b,c);
                dfa=df_a(x,a,b,c);     dfb=df_b(x,a,b,c);    dfc=df_c(x,a,b);
                //b=At*beta
                b1+=beta*dfa; b2+=beta*dfb;  b3+=beta*dfc;
                //m=At*A
                m11+=dfa*dfa+lambda; 	m12+=dfa*dfb;       	m13+=dfa*dfc;
                                		m22+=dfb*dfb+lambda;  	m23+=dfb*dfc;
                                                    			m33+=dfc*dfc+lambda;
            }//for all datapoints

            //Symmetry:
            m21=m12;         
            m31=m13;            m32=m23;
            
            n=regMath.det33(m11,m12,m13,m21,m22,m23,m31,m32,m33);

            if(Math.abs(n)<EPSSING){           // Not singular?
                error=true;
            	errorMsg("Singular matrix...");
                da=db=dc=0.0d;                  //to stop it all...
            }else{
                da=regMath.det33(
                		b1,m12,m13,
                        b2,m22,m23,
                        b3,m32,m33)/n;
                db=regMath.det33(
                		m11,b1,m13,
                		m21,b2,m23,
                		m31,b3,m33)/n;
                dc=regMath.det33(
                		m11,m12,b1,
                        m21,m22,b2,
                        m31,m32,b3)/n;
                newa=a+da;newb=b+db;newc=c+dc;			        //remember this and update later if ok
                residual=beta2(xd,yd,newa,newb,newc);
                diff=residual-old_residual;                     //debug("Residual difference: "+diff+"    lambda: "+lambda);

                if(residual<old_residual) {						// (Set to true if no LM)
                    lambda=lambda/LMFACTORDIV;   //going well :-)     but don't overdo it...
                    old_residual=residual;
                    multfaktor=LMFACTORMULT;               //reset this!
                    a=newa;b=newb;c=newc;
                }else{
                    lambda=lambda*multfaktor;  // not going well :-(
                    multfaktor*=2;              //LM drives hard...
                }//if going the right way
                
            }//if(error)-else
            
            													//debug(""+da+"\t"+db+"\t"+dc+"\n"+a+"\t"+b+"\t"+c);
            
        }//while(|da|+|db|+|dc|>epsilonreg)

        
        System.out.println("AlgoFitLogistic: Sum Errors Squared= "+beta2(xd,yd,a,b,c));	//Info

    }//Logistic_Reg()
 
    // --- The Logistic Function and its derivates --- //
    
    //Variables in calcultions that tries to prevent rounding off errors:
    private static double x1,y1,x2,y2,ymult,e1,e2,emult,ydiff;
    
    /** Logistic function f(x)=c/(1+ae^(-bx))  */
    private final static double f(double x,double a,double b, double c) {
        return df_c(x,a,b)*c;
    }//f(x,a,b,c)

    //  Adjusted f, used in findParameters(), when a and c are calculated from first and last datapoint
    //  Also tries to avoid rounding off errors
    private final static double f(double x,double k){  //k=b
        double e1k=Math.pow(e1,k),e2k=Math.pow(e2,k);
        double efrac=Math.pow(emult/Math.exp(x),k);
        return  ymult*(e1k-e2k)/(y2*e1k-y1*e2k+ydiff*efrac);
    }//f(x,k)

    // df/dc=1/(1+ae^(-bx))  
    private final static double df_c(double x,double a,double b){
        return (1.0d/(1.0d+a*Math.exp(-b*x)));
    }//simple(x,a,b)
    
    // df/da
    private final static double df_a(double x,double a,double b,double c){
        double df_c=df_c(x,a,b);
        return df_c*df_c*Math.exp(-b*x)*(-c);
    }//df_a(x,a,b,c)
   
    // df/db
    private final static double df_b(double x,double a,double b,double c){
        double df_c=df_c(x,a,b);
        return df_c*df_c*Math.exp(-b*x)*x*a*c;
    }//df_b(x,a,b,c)
    
    /// --- Error calculations --- ///
    // beta = yd-f(xd,yd,a,b,c)
    private final static double beta(double x,double y,double a,double b,double c){
        return y-f(x,a,b,c);
    }//beta(x,y,a,b,c)
    
    // beta = yd-f(x,b) for use in findParameters(). (a and c calculated)
    public final static double beta(double x,double y,double b){
        return y-f(x,b);
    }//bet(x,y,b)
    
    //Sum of squared errors, using last a,b and c
    public final static double beta2(double[] x,double[] y,double a,double b,double c){
        double sum=0.0d,beta;
        for(int i=0;i<size;i++){
            beta=beta(x[i],y[i],a,b,c);
            sum+=beta*beta;   
        }//for all datapoints                      
        									//debug("Sum Squared Errors: "+sum);
        return sum;
    }//beta2(x,y,a,b,c)    
    
    //Sum of squared errors, using b(=k). a and c are calculated from first and last datapoint.
    private final static double beta2(double[] x,double[] y,double k){
        double beta=0.0d,sum=0.0d;
    	for(int i=0;i<size;i++){
            beta=beta(xd[i],yd[i],k);
            sum+=beta*beta;
        }//for all data
    	return sum;
    }//beta2(x,y,k)    
    
    /// --- Bjørn Ove Thue's trick --- ///
    // c as function of first and last point
    private final static double c(double x1,double y1, double x2,double y2,double b){
        return y1*y2*(Math.exp(b*x1)-Math.exp(b*x2))/(y2*Math.exp(b*x1)-y1*Math.exp(b*x2));
    }//c(x1,y1,x2,y2,k)
    
    /** a as function of first and last point */
    private final static double a(double x1,double y1,double x2,double y2,double b){
        return Math.exp(b*(x1+x2))*(y1-y2)/(y2*Math.exp(b*x1)-y1*Math.exp(b*x2));
    }//a(x1,y1,x2,y2,b)
    
    private final   void getPoints(){

    	//problem bothering the gui: GeoList newlist=k.Sort("tmp_{FitLogistic}",geolist);
    	double[] xlist=null,ylist=null;
        double xy[]=new double[2];
        GeoElement geoelement;
        GeoList		newlist;
        //This is code duplication of AlgoSort, but for the time being:
        Class geoClass=geolist.get(0).getClass();
        java.util.TreeSet sortedSet;
        sortedSet=new java.util.TreeSet(GeoPoint.getComparatorX());
        for (int i=0;i<size;i++){
        	geoelement=geolist.get(i);
        	if(geoelement.getClass().equals(geoClass)){
        		sortedSet.add(geoelement);
        	}else{
        		error=true;
        	}//if point
        }//for all points
        java.util.Iterator iter=sortedSet.iterator();
        int i=0;
        xlist=new double[size];    ylist=new double[size];
        while(iter.hasNext()) {
        	geoelement=(GeoElement)iter.next();
            ((GeoPoint)geoelement).getInhomCoords(xy);        	
        	xlist[i]=xy[0];ylist[i]=xy[1];
        	i++;
        }//while iterating
        
        xd=xlist;yd=ylist;
        if(error){errorMsg("getPoints(): Wrong list format...");}
    }//getPoints()    
    
    private final static void errorMsg(String s){
    	System.err.println("\nFitLogistic:  ");		
    	System.err.println(s);
    }//errorMsg(String)        

    private final static void debug(String s) {
        if(DEBUG) {
            System.out.print("\nAlgoFitLogistic:   ");
            System.out.println(s);
        }//if()
    }//debug()
 
/* //SNIP START==========================CUT=======================================================

/// --- Test routines:  --- ///
    
    //8.143 and 8.243 demands LM damping factor!

    private static String 		info=	"";
    private static double[]		f	=	new double[3];

	private static boolean rantest=false;		//only run once
	
    private final static void runTest(){
      if(!rantest){
    	rantest=true;
        System.out.println("/// --- Testing AlgoFitLogistic ---///\n");
        ex1();
        ex2();
        ex3();
        ex4();
        ex5();
        ex6();
        ex7();
      }//if !rantest
    }//runtest
    
    

    private  final static void ex1(){
        info="Eksakt: 20/(1+10*e^(x/2))";
        size=5;				//important!, shortcuts compute()!	
        double[] x		=	new double[size];	
        double[] y		=	new double[size];
        		xd		=	new double[size];
        		yd		=	new double[size];
        double[] facit	=	{10,0.5,20};			
        for(int i=0;i<size;i++){
            x[i]=i*1.0d;
            y[i]=20.0d/(1+10*Math.exp(-x[i]/2));
        }//for
        System.arraycopy(x,0,xd,0,size);
        System.arraycopy(y,0,yd,0,size);
        System.arraycopy(facit,0,f,0,3);
        
        time();report(x,y,f);
    }//ex1()

    private  final static void ex2(){
        info="Mobiltelefoner: Ex p 324";
        size=9;	
        double[] x		=	{0,2,4,6,8,10,12,14,17};
        double[] y		=	{180.6,234.4,368.5,981.3,1676.7,2663.5,3593.2,4295.0,5210.6};
        		xd		=	new double[size];
        		yd		=	new double[size];
        double[] facit	=	{42.51562594,0.3593258321,5634.291187};			
        
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);System.arraycopy(facit,0,f,0,3);
        
        time();report(x,y,f);
    }//exX()

    private  final static void ex3(){
        info="Sinus 8.42";
        size=6;	
        double[] x		=	{0,4,8,12,16,18};
        double[] y		=	{20,40,70,90,125,140};
        		xd		=	new double[size];
        		yd		=	new double[size];
        double[] facit	=	{7.40511504,0.161990112,194.619089};			
        
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);System.arraycopy(facit,0,f,0,3);
        
        time();report(x,y,f);
    }//exX()

    private  final static void ex4(){
        info="Sinus 8.43";
        size=6;	
        double[] x		=	{0,5,10,15,20,25};	
        double[] y		=	{20,21.6,23.0,24.3,25.3,26.2};
        		xd		=	new double[size];
        		yd		=	new double[size];
        double[] facit	=	{0.487,0.0514,29.7};
        
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);System.arraycopy(facit,0,f,0,3);
        
        time();report(x,y,f);
    }//exX()


    private  final static void ex5(){
        info="ex 8.142";
        size=4;	
        double[] x		=	{0,2,3,5};	
        double[] y		=	{10,16,19,22};
        		xd		=	new double[size];
        		yd		=	new double[size];
        double[] facit	=	{1.44,0.53,24.3};
        
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);System.arraycopy(facit,0,f,0,3);
        
        time();report(x,y,f);
    }//exX()
    
    private  final static void ex6(){
        info="Ex 8.143";
        size=8;	
        double[] x		=	{0,10,20,25,30,35,40,50};
        double[] y		=	{1,7,46,100,172,236,272,296};
        		xd		=	new double[size];
        		yd		=	new double[size];
        double[] facit	=	{299,0.2,300};
        
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);System.arraycopy(facit,0,f,0,3);
        
        time();report(x,y,f);
    }//exX()

    private  final static void ex7(){
        info="Ex 8.243";
        size=17;	
        double[] x		=	{0,10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160};
        double[] y		=	{3929,5308,7240,9638,12866,17069,23192,31443,38558,50156,62948,75995,91072,105711,122775,131669,150697};
        		xd		=	new double[size];
        		yd		=	new double[size];
        double[] facit	=	{47.9,0.03,201286};			
        
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);System.arraycopy(facit,0,f,0,3);
        
        time();report(x,y,f);
    }//exX()
         
    private final static void report(double[] x,double[] y,double[] f){

        double fe,se;

        System.out.println("\n"+info);
        System.out.println("                     a                   b                   c");
        System.out.println("Facit:          "+f[0]+"        "+f[1]+"        "+f[2]);
        System.out.println("FitSin:         "+a+   "    "+b+   "    "+c);
        System.out.println("---------------------------------------------------------------------------------------------");
        System.out.println("Diff:           "+(a-f[0])+"    "+(b-f[1])+"    "+(c-f[2]));

        fe=beta2(x,y,f[0],f[1],f[2]);
        se=beta2(x,y,a,b,c);
        System.out.println("Fasit  SumError2: "+fe);
        System.out.println("SinReg SumError2: "+se);
        
        System.out.println("\nIterations: "+iterations);
        if(se>fe*1.1){
            System.out.println("*********************** ERROR?  IS THIS OKAY?????? ***************************************");
        }else if(se<fe){
            System.out.println("*** Good! ****");
        }else{
            System.out.println("*** Could be better? ***");
        }//if
            
        System.out.println("\n");
    }//report(....)
    
    private final  static void time(){
        java.util.Date date=new java.util.Date();
        long start,slutt;
        start=date.getTime();
        findParameters();       //Find initial parameters a,b,c,d
        Logistic_Reg();            //Run LM nonlinear iteration
        if(error) {System.out.println("*** Error in running doReg()! ***");}
        date=new java.util.Date();slutt=date.getTime();
        System.out.println("Time used: "+(slutt-start));
    }//time()
        

*/ //SNIP END==========================================================================================================
    
}// class AlgoFitLogistic
