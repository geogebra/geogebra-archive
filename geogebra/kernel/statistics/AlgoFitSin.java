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


/** 
 * @author Hans-Petter Ulven
 * @version 16.11.08	(16 nov.)
 * 			17.11.08:	sorting list, noisekiller (nearmaxmin() ),
 * 			19.11.08:   some more testing, small fixes
 * 
 * Fits a+b*sin(c*x+d)  to a list of points.
 * Adapted from:
 *    	Nonlinear regression algorithms are well known, see:
 *      	mathworld.wolfram.com/NonlinearLeastSquaresFitting.html 
 * 			ics.forth.gr/~lourakis/levmar
 * 			Damping Parameter in Marquardt's Method, Hans Bruun Nielsen, IMM-Rep 1999-05
 * The problem is more to find best initial values for parameters,
 * and here I was on my own, little information available on this problem...
 * Experiments showed me:
 *		c and d are most critical
 *  	If a,b and c are good, d is not that critical
 *  	If c and d are good, a and b are not that critical
 *  This led me to this algorithm:
 * 		I 	a=average of y-data
 * 		  	b=(maxy-miny)/2
 * 		II 	period=2*|x_first_max - x_first_min|
 *          c=2pi/period
 *          The first two extremums are found by my
 *          "direction-changing-algorithm" (Ulven nov-08)
 *		III simple iteration of d in <-pi,pi> to find a good d
 *			(Critical if c is a bit off!)
 *		IV  Simplified Levenberg-Marquardt method.
 *          (Could be optimized if/when I am able to understand
 *           the mathematics behind it and be able to check if
 *           this is of any value.)
 * 
 * 	Constraints:
 * 		<List of points> should have at least 6 points. 
 * 		There should also be three points on the row steadily increasing
 * 		or decreasing(y1<=y2<=y3 or y1>=y2>=y3)on each side/flank of the first extremum.
 * 		The points should cover at least two extremums of the function.
 * 		If not, the resulting function will not be a good fit.
 * 	Problems:
 * 		Non-linear regression is difficult, and the choice
 * 		of initial values for the parameters are highly critical.
 * 		The algorithm here might converge to a local minimum.
 * 		I am not sure if the algorithm in some cases might diverge or oscillate?
 * 		(Then it would stop after MAXITERATIONS and give a bad fit.)
 */


public class AlgoFitSin extends AlgoElement{
	
    //Tuning of noisefilter, Levenberg-Marquardt iteration, debug, rounding off errors
	private final static double NOISEKILLER		=	0.5D;	//Kill local extremums inside a+/-noisekiller*b
	private final static double	LMFACTORDIV		=	3.0d;	
	private final static double LMFACTORMULT	=	2.0d;
	private final static int	MAXITERATIONS	=	200;
	private final static double EPSILON			=	1E-14d;
	private final static double EPSSING			=	1E-20d;
	private final static boolean DEBUG			=	false;		//set false when finished
	
	// Properties
	private static geogebra.main.Application app=	null;
	private static geogebra.kernel.Kernel 	 k  =   null;
    private static double 		a,b,c,d;			//a+bsin(cx+d)
    private static double[] 	xd,yd;				//datapoints
    private static int      	size;				//data arrays
    private static int			iterations;			//LM iterations
    private static boolean  	error           =   false;	//general error flag	
    private static RegressionMath regMath		=	null;	//pointer to detXX
    
	//GeoGebra obligatory:
    private static final long serialVersionUID  =   1L;
    private GeoList         geolist;                        //input
    private GeoFunction     geofunction;                    //output
    
    public AlgoFitSin(Construction cons, String label, GeoList geolist) {
        super(cons);
        app=kernel.getApplication();
        k=app.getKernel();
        this.geolist=geolist;
        geofunction=new GeoFunction(cons);
        setInputOutput();
        compute();
        geofunction.setLabel(label);
    }//Constructor
    
    protected String getClassName() {return "AlgoFitSin";}
        
    protected void setInputOutput(){
        input=new GeoElement[1];
        input[0]=geolist;
        output=new GeoElement[1];
        output[0]=geofunction;
        setDependencies();
    }//setInputOutput()
    
    public GeoFunction getFitSin() {return geofunction;}
    
    protected final void compute() {
        size=geolist.size();
        error=false;				//General flag
        if(!geolist.isDefined() || (size<5) ) {	//Direction-algo needs two flanks, 3 in each.
            geofunction.setUndefined();
            errorMsg("List not properly defined or too small (5 points needed).");
            return;
        }else{
        	regMath = k.getRegressionMath();
        	getPoints();		//toDo: sort the points on x while getting them!
            doReg();
            if(!error){
                MyDouble A=new MyDouble(kernel,a);
                MyDouble B=new MyDouble(kernel,b);
                MyDouble C=new MyDouble(kernel,c);
                MyDouble D=new MyDouble(kernel,d);
                FunctionVariable X=new FunctionVariable(kernel);
                ExpressionValue expr=new ExpressionNode(kernel,C,ExpressionNode.MULTIPLY,X);
                				expr=new ExpressionNode(kernel,expr,ExpressionNode.PLUS,D);
                				expr=new ExpressionNode(kernel,expr,ExpressionNode.SIN,null);
                				expr=new ExpressionNode(kernel,B,ExpressionNode.MULTIPLY,expr);
               
                ExpressionNode node=new ExpressionNode(kernel,A,ExpressionNode.PLUS,expr);
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
    public final static void doReg(){
    	//runTest();				//comment out when final
        findParameters();       //Find initial parameters a,b,c,d
        sinus_Reg();            //Run LM nonlinear iteration
    }//doReg()

    
    public final static void findParameters() {
        double y;
        int		xmax_abs=0,xmin_abs=0;	//update in case changes=0 later (few-data-case)
        size=xd.length;
        double sum=0.0d,    max=yd[0],  min=max;
        //Find a and b:
        for(int i=0;i<size;i++){
            y=yd[i];
            sum+=y;
            if(y>max){max=y;xmax_abs=i;}
            if(y<min){min=y;xmin_abs=i;}
        }//for
        a=sum/size;                                    
        b=(max-min)/2.0d;                             //debug("a= "+a+" b= "+b+" max= "+max+" min= "+min);
       
        //find c:
        //This time first and second local max/min, between rise and fall and vv
        //Observation: last y in a rise or decrease *is* the local extremum!
        int xmax=xmax_abs,xmin=xmin_abs;	//Keep absolute max/min in case changes=0 later      
        int state=0;             //undecided so far...
        int current=0;
        int changes=0;          //undecided so far...
        
        for(int i=2;i<size;i++) {
            y=yd[i];
            current=direction(yd[i-2],yd[i-1],y);
            /* current=direction5(yd[i-4],yd[i-3],yd[i-2],yd[i-1],yd[i]); */
            if( (current==1) || (current==-1) ){                 //steady up or steady down
                //do state bookkeeping
                if(state==0){        //just started:
                    state=current;      //set first state
                }else {             //we are on our way...
                    if((current!=state)&&(current!=0)){ 	//update eventual change
                    	if(nearmaxmin(y,a,b,state,current,max,min)){//kill noise
                    		changes++;state=current;
                    	}//if near 
                    }//if change
                }//if steady up or down

                //Two changes enough. (Must check before updating extremums.)
                if(changes>=2){                             //debug("Two changes on "+i);
                    break;                      
                }//if changes>=2
            
                //update extremums so far                
                if(current==1){         //steady up
                    max=y;xmax=i;           //last is max so far
                }else if(current==-1){  //steady down
                    min=y;xmin=i;           //last is min so far
                }//update extremums
            }else{	//not steady, 
                
            }//if steady up or down
             
            //debug("i: "+i);
            //debug("state: "+state+" current: "+current+" changes: "+changes+" max: "+max+" min: "+min+" xmax: "+xmax+" xmin: "+xmin);
        }//for all data
                
        double period;
        if(changes<=1){xmax=xmax_abs;xmin=xmin_abs;}	//Few points, safe to assume only one period
        period=Math.abs(xd[xmax]-xd[xmin])*2;
        c=2*Math.PI/period;

        //debug("changes:          firstmax:        firstmin:     xmax:     xmin:");
        //debug(changes+"   "+max+"   "+min+"     "+xmax+"     "+xmin);
        //debug("period: "+period+" c: "+c);            
        
        //Find d  
        // (d=0 might go well, but to be on the safe side...100 iterations should be enough?
        // Could also use pi/2=c*xmax+d, but iteration is more robust in bad cases.
        // If a,b and c are a bit off, d should be good!
        d=-Math.PI;
        double deltad=Math.PI*2*0.01;
        double err=0.0d;
        double bestd=0.0d;
        double old_err=beta(xd,yd,a,b,c,d);
        for(int i=0;i<100;i++) {            
            d+=deltad;
            err=beta(xd,yd,a,b,c,d);      //Without squaring is ok...
            if(err<old_err) {
                old_err=err;
                bestd=d;                    //System.out.println("d-iteration: error= "+error+"   d: "+d);
            }//if new min                                         
        }//for: d-iteration 
        d=bestd;                                         
                                            debug("Parameters:  a= "+a+" b= "+b+" c= "+c+" d "+d);
    }//findParameters()
    
    public final static void sinus_Reg(){
        double lambda=0.0d;          //LM-damping coefficient
        double multfaktor=LMFACTORMULT;	// later?: divfaktor=LMFACTORDIV;
        double residual,old_residual=beta2(xd,yd,a,b,c,d);
        double diff=-1;         //negative to start it off
        
        double da=EPSILON,db=EPSILON,dc=EPSILON,dd=EPSILON;     //Something larger than eps, to get started...
        double b1,b2,b3,b4;                     //At*beta
        double m11,m12,m13,m14,m21,m22,m23,m24,
                m31,m32,m33,m34,m41,m42,m43,m44, //At*A
                n;                               //singular check
        double x,y;
        double dfa,dfb,dfc,dfd,beta,
                newa,newb,newc,newd;
        iterations=0;
        
        // LM: optimal startlambda
        b1=b2=b3=b4=0.0d;
        m11=m22=m33=m44=0.0d;
        for(int i=0;i<size;i++) {
            x=xd[i];y=yd[i];
            beta=beta(x,y,a,b,c,d);
            dfa=df_a();     dfb=df_b(x,c,d);    dfc=df_c(x,b,c,d);     dfd=df_d(x,b,c,d);
            //b=At*beta
            b1+=beta*dfa; b2+=beta*dfb;  b3+=beta*dfc;                b4+=beta*dfd;
            //m=At*A
            m11+=dfa*dfa;       //only need diagonal
            m22+=dfb*dfb;    
            m33+=dfc*dfc;   
            m44+=dfd*dfd;
        }//for all datapoints
        double startfaktor=Math.max(Math.max(Math.max(m11,m22),m33),m44);           
        lambda=startfaktor*0.001;   //heuristic...                                  
                                                                        //debug("Startlambda: "+lambda);

        while(Math.abs(da)+Math.abs(db)+Math.abs(dc)+Math.abs(dd)>EPSILON){//or while(Math.abs(diff)>EPSILON) ?
            iterations++;
                                                                        //debug(""+iterations+"   : ");
            if((iterations>MAXITERATIONS)||(error)){                //From experience: >200 gives nothing more...
            	errorMsg("More than "+MAXITERATIONS+" iterations...");
            	break;
            }
            b1=b2=b3=b4=0.0d;
            m11=m12=m13=m14=m21=m22=m23=m24=m31=m32=m33=m34=m41=m42=m43=m44=0.0d;
            for(int i=0;i<size;i++) {
                x=xd[i];y=yd[i];
                beta=beta(x,y,a,b,c,d);
                dfa=df_a();     dfb=df_b(x,c,d);    dfc=df_c(x,b,c,d);     dfd=df_d(x,b,c,d);
                //b=At*beta
                b1+=beta*dfa; b2+=beta*dfb;  b3+=beta*dfc;                b4+=beta*dfd;
                //m=At*A
                m11+=dfa*dfa+lambda;   m12+=dfa*dfb;       m13+=dfa*dfc;       m14+=dfa*dfd;
                                m22+=dfb*dfb+lambda;       m23+=dfb*dfc;       m24+=dfb*dfd;
                                                    m33+=dfc*dfc+lambda;       m34+=dfc*dfd;
                                                                        m44+=dfd*dfd+lambda;
            }//for all datapoints

            //Symmetry:
            m21=m12;         
            m31=m13;            m32=m23;
            m41=m14;            m42=m24;            m43=m34;
            
            n=regMath.det44(m11,m12,m13,m14,m21,m22,m23,m24,m31,m32,m33,m34,m41,m42,m43,m44);

            if(Math.abs(n)<EPSSING){           // Not singular?
                error=true;
            	errorMsg("Singular matrix...");
                da=db=dc=dd=0;                  //to stop it all...
            }else{
                da=regMath.det44(b1,m12,m13,m14,
                         b2,m22,m23,m24,
                         b3,m32,m33,m34,
                         b4,m42,m43,m44)/n;
                db=regMath.det44(m11,b1,m13,m14,
                         m21,b2,m23,m24,
                         m31,b3,m33,m34,
                         m41,b4,m43,m44)/n;
                dc=regMath.det44(m11,m12,b1,m14,
                         m21,m22,b2,m24,
                         m31,m32,b3,m34,
                         m41,m42,b4,m44)/n;
                dd=regMath.det44(
                        m11,m12,m13,b1,
                        m21,m22,m23,b2,
                        m31,m32,m33,b3,
                        m41,m42,m43,b4)/n;
                
                newa=a+da;newb=b+db;newc=c+dc;newd=d+dd;        //remember this
                residual=beta2(xd,yd,newa,newb,newc,newd);      //debug("ChiSqError: +"+residual);
                diff=residual-old_residual;                     //debug("Residual difference: "+diff+"    lambda: "+lambda);
                // uten lambda...
                if(residual<old_residual) {
                    lambda=lambda/LMFACTORDIV;   //going well :-)     but don't overdo it...
                    old_residual=residual;
                    multfaktor=LMFACTORMULT;               //reset this!
                    a=newa;b=newb;c=newc;d=newd;
                }else{
                    lambda=lambda*multfaktor;  // not going well :-(
                    multfaktor*=2;              //LM drives hard...
                }//if going the right way
                
            }//if(error)-else
            
            //Justify d to intervall <-pi,pi>

            debug(
                ""+da+"\t"+db+"\t"+dc+"\t"+dd+"\n"+
                ""+a+"\t"+b+"\t"+c+"\t"+d+"\n"
            );//out
            
        }//while(da+db+dc>eps)
        
        //Reduce d to interval <-pi,pi>
        double reduction=Math.PI*2;
        while(Math.abs(d)>Math.PI){
             if(d>Math.PI){d-=reduction;}
             if(d<-Math.PI){d+=reduction;}              //debug("justifying: "+d);
        }//while not in i <-pi,pi>
        System.out.println("AlgoFitSin: Sum Errors Squared= "+beta2(xd,yd,a,b,c,d));	//Info
    }//sinus_Reg()
 

    /* sin(Cx+D)  */
    private final static double sin(double x,double c,double d){
        return Math.sin(c*x+d);
    }//sin(x,b,c,d)
    
    /* cos(Cx+D) */
    private final static double cos(double x,double c,double d){
        return Math.cos(c*x+d);
    }//cos(x,b,c,d)

    /* f(x)=A+Bsin(Cx+D) */
    private final static double f(double x, double a,double b,double c,double d){
        return a+b*sin(x,c,d);
    }//f(x,a,b,c,d)

    /* Partial derivative of f to a */
    private final static double df_a(){
        return 1.0d;
    }//df_a()
    
    /* Partial derivative of f to b: sin(cx+d) */
    private final static double df_b(double x,double c,double d) {
        return sin(x,c,d);
    }//df_b(x,b,c,d)
    
    /* Partial derivative of f to c: cos(cx+d)*B*x */
    private final static double df_c(double x,double b,double c,double d){
        return cos(x,c,d)*b*x;
    }//df_c(x,b,c,d)
    
    /* Partial derivative of f to d: Bcos(cx+d) */
    private final static double df_d(double x,double b,double c,double d){
        return cos(x,c,d)*b;
    }//df_d(x,b,c,d)
    
    /* Difference to be reduced */
    private final static double beta(double x,double y,double a,double b,double c,double d){
        return y-f(x,a,b,c,d);
    }//beta(x,a,b,c,d)    
    
    /* Sum of quadratic errors */
    public final static double beta2(double[] x,double[] y,double a,double b,double c,double d){
        double sum=0.0d,beta;
        int n=x.length;
        for(int i=0;i<n;i++){
            beta=beta(x[i],y[i],a,b,c,d);                //System.out.printf("\n%20s %20s %20s %20s",y[i],f(x[i],a,b,c,d),beta,beta*beta);
            sum+=beta*beta;                              //System.out.println("    "+sum);
        }//for all datapoints                      
                                                        //System.out.println("Errorcalc: "+sum);
        return sum;
    }//beta(x,y,a,b,c,d)

    // Sum of errors (absolute values)
    private final static double beta(double[] x,double[] y,double a,double b,double c,double d){
        double sum=0.0d;
        int n=x.length;
        for(int i=0;i<n;i++){
            sum+=Math.abs(beta(x[i],y[i],a,b,c,d));                //System.out.println("Sum: "+sum);
        }//for all datapoints                      
                                                        //System.out.println("Errorcalc: "+sum);
        return sum;
    }//beta(x,y,a,b,c,d)    
    
    // 3 yd's on the row: up=1, down=-1, uncertain=0 
    private final static int direction(double y1,double y2,double y3){
        if( (y3>y2)&&(y2>y1) ){         //rising!
            return 1;
        }else if( (y1>y2)&& (y2>y3) ){ //all under a
            return -1;
        }else{                              //some over, som under...
            return 0;
        }//if
    }//direction()
    
    //getPoints and sort them
    private final   void getPoints(){

    	//problem bothering the gui: GeoList newlist=k.Sort("tmp_{FitSin}",geolist);
    	double[] xlist=null,ylist=null;
        double xy[]=new double[2];
        GeoElement geoelement;
        //GeoList		newlist;
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
        /* old code:
        for(int i=0;i<size;i++){
            geoelement=geolist.get(i);
            if(geoelement.isGeoPoint()) {
                ((GeoPoint)geoelement).getInhomCoords(xy);
                xlist[i]=xy[0]; ylist[i]=xy[1];
            }else{
                error=true;
                xlist[i]=0.0d;  ylist[i]=0.0d;
            }//if
        }//for all points
        */
        //does not work: kernel.getConstruction().removeFromConstructionList(newlist);
        
        xd=xlist;yd=ylist;
        if(error){errorMsg("getPoints(): Wrong list format...");}
    }//getPoints()    

    //Noisekiller
    private final static boolean nearmaxmin(double y,double a,double b,int state, int current,double max,double min){
    	double k=NOISEKILLER;
    	if( (state==1) && (current==-1) ){  //A real max-change?
    		if(max>a+k*b){return true;} else{return false;}	
    	}else if ( (state==-1) && (current==1) ){  //A real min-change?
    		if(min<a-k*b){return true;} else {return false;}
    	}else{
    		return true;//ok, outside a+/-k*b
    	}//if
    }//nearmaxmin(y,a,b)
    
    private final static void errorMsg(String s){
    	System.err.println("\nFitSin:  ");		
    	System.err.println(s);
    }//errorMsg(String)        

    private final static void debug(String s) {
        if(DEBUG) {
            System.out.print("\nFitSin:   ");
            System.out.println(s);
        }//if()
    }//debug()
    


/// =============== To comment out when final =============================================== ///
/* //SNIP START---------------------------------
    ///// ----- Test Interface ----- /////
    
	private static String info="";
	private static boolean rantest=false;		//only run once
    public static void runTest(){
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
        ex8();
        ex9();
        ex10();
        ex11();
        ex12();
        ex13();
        ex14();
        ex15();
      }//if !rantest
   }//runtest
    
    private  final static void ex1(){
        info="Eksakt: 4+3sin(2*x+1)";
        size=9;
        double[] x=new double[size];
        double[] y=new double[size];
        double[] f={4.0,3.0,2.0,1.0};               
        xd		=	new double[size];
        yd		=	new double[size];		
        for(int i=0;i<size;i++){
            x[i]=i*0.3d;
            y[i]=4.0d+3.0*Math.sin(2.0*x[i]+1.0);
        }//for
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);             
        time();report(xd,yd,f);
    }//ex1()        
    
    
    private  final static void ex2(){
        info="Minimalt testeksempel";
        size= 9;
        double[] x={0,3,6,9,12,15,18,21,24};
        double[] y={128,90,139,178,147,106,135,177,161};
        double[] fasit={139.78166616018288, 40.81425276514424,   0.4948321574038634,  -2.9237223002711956};
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,fasit);
    }//exX()

    private final static void ex3(){        
        info="Exercise 3.206 in a 3mx math book, Temperatures in Stavanger, Norway, Jan 2001";
        size=16;
        double[] x={1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31};
        double[] y={2.15,4.65,5.85,4.25,0.9,0.65,-0.85,-1.55,-3.15,-0.5,1.55,2.35,4.8,2.9,1.7,-1.7};
        double[] fasit={0.8755240325,3.543122157,0.3184110922,-0.0721783207};
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,fasit);
    }//exX()
        
    private final static void ex4(){
        info="Eksempel side 99";
        size=13;
        double[] x={0,2,4,6,8,10,12,14,16,18,20,22,24};
        double[] y={165,260,269,182,76,50,132,233,265,193,95,61,138};
        double[] f={164.29422284012418,109.435835813277,0.51183903033813989,-0.06825629673297486};
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()
    
    private final static void ex5(){
        info="Eksempel 3.30";
        size=13;
        double[] x={0,2,4,6,8,10,12,14,16,18,20,22,24};
        double[] y={270,295,182,72,48,121,239,290,198,85,58,127,241};
        double[] f={165.9,123.3,0.514,0.882,165.9};
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()       
    
    private final static void ex6(){
        info="Eksempel 3.31";
        size=12;
        double[] x={1,2,3,4,5,6,7,8,9,10,11,12};
        double[] y={-0.4,-0.5,1.9,6.3,12.5,13.6,17.4,15.9,13.2,6.4,4.1,-2.7};
        double[] f={7.9,9.2,0.56,-2.4}; 
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()  
    
    private final static void ex7(){
        info="Eksempel 3.32";
        size=9;
        double[] x={0,3,6,9,12,15,18,2,24};
        double[] y={3,9,23,37,43,37,23,9,3};
        double[] f={23,20,0.26,-1.6};  
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()  
    
    private final static void ex8(){
        info="Eksempel 3.130";
        size=7;
        double[] x={0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0};
        double[] y={2, 3.0, 2.1, 1.0, 1.7, 2.9, 2.4};
        double[] f={1.98,1,2.99,0.03}; 
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()  
    
    private final static void ex9(){
        info="Eksemepl 3.131";
        size=12;
        double[] x={0,2,4,6,8,10,12,14,16,18,20,22};
        double[] y={219,208,127,47,42,118,205,220,152,68,52,124};
        double[] f={133,92.7,0.51,1.1};
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX() 
    
    private final static void ex10(){
        info="Eksemepl 3.132";
        size=12;
        double[] x={ 0, 2, 4,6, 8,10,12,14,16,18,20,22};
        double[] y={47,51,28,8,21,26,48,61,37,15,13,23};
        double[] f={32,21.8,0.5,1.22};
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX() 
    
    private final static void ex11(){
        info="Eksemepl 3.230";
        size=12;
        double[] x={    1,    2,    3,   4,  5,  6,   7,   8,  9, 10, 11,  12};
        double[] y={-15.9,-14.2,-10.6,-4.1,2.9,9.7,12.4,10.2,4.8,1.9,-9.2,-14};
        double[] f={-1.98,14,0.53,-2.26};
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()
    
    private final static void ex12(){
        info="Eksempel 3.231";
        size=12;
        double[] x={1,2,3,4,5,6,7,8,9,10,11,12};
        double[] y={40.5,76.0,126.0,178.0,220.2,249.6,245.8,215.8,144.3,86.4,51.2,35.2};
        double[] f={144,108,0.55,-1.85};        
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()
    
    private final static void ex13(){
        info="Ex 4.23 Ascheoug";
        size=12;
        double[] x={1,2,3,4,5,6,7,8,9,10,11,12};
        double[] y={3,-0.7,4.6,8.2,10,15.6,15.2,16,12,7.7,3.6,0.3};
        double[] f={8.7371117271314,7.6300508011209,0.58780329880075,-2.5808559805165};        
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()

    private final static void ex14(){
        info="Ex 4.G Ascheoug";
        size=5;
        double[] x={0,1,3.5,5,8};
        double[] y={6.8,6.8,3.3,2,4.2};
        double[] f={4.3,2.6,0.6,1.4};     
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()
    
    private final static void ex15(){
        info="Ex 4 s191 Ascheoug";
        size=14;
        double[] x={0,2,3,6,7,9,12,13,15,16,19,21,22,24};
        double[] y={114,94,96,140,156,167,123,103,83,88,141,164,161,131};
        double[] f={127,38.3,0.496,-2.69};     
		xd		=	new double[size];
		yd		=	new double[size];      
        System.arraycopy(x,0,xd,0,size);System.arraycopy(y,0,yd,0,size);     
        time();report(xd,yd,f);
    }//exX()
    
    private static void report(double[] x,double[] y,double[] f){
        double fe,se;

        System.out.println("\n"+info);
        System.out.println("                "+hfill(20,"a")+hfill(20,"b")+hfill(20,"c")+hfill(20,"d"));
        System.out.println("Facit:          "+hfill(20,""+f[0])+hfill(20,""+f[1])+hfill(20,""+f[2])+hfill(20,""+f[3]));
        System.out.println("FitSin:         "+hfill(20,""+a)+hfill(20,""+b)+hfill(20,""+c)+hfill(20,""+d));
        System.out.println(hfill(95,ntegn(95,'-')));
        System.out.println("Diff:           "+hfill(20,""+(a-f[0]))+hfill(20,""+(b-f[1]))+hfill(20,""+(c-f[2]))+hfill(20,""+(d-f[3])));

        fe=beta2(x,y,f[0],f[1],f[2],f[3]);
        se=beta2(x,y,a,b,c,d);
        
        System.out.println("\nFasit SumError2:  "+fe);
        System.out.println( "SinReg SumError2: "+se);
        
        System.out.println("\nIterations:      "+iterations);
        if(se>fe*1.1){
            System.out.println("*****************************ERROR? ***************************");
        }else if(se<fe){
            System.out.println("/////////////////// GOOD \\\\\\\\\\\\\\\\\\\\\\\\");
        }else{
            System.out.println("*************************** COULD BE BETTER? *******************");
        }//if
            
        System.out.println();
    }//report(....)
    
    private final static void time(){
        java.util.Date date=new java.util.Date();
        long start,slutt;
        start=date.getTime();
        findParameters();
        sinus_Reg();
        if(error) {System.out.println("*** Error in running doReg()! ***");}
        date=new java.util.Date();slutt=date.getTime();
        System.out.println("Time used: "+(slutt-start));
    }//time()
    
    private final static String hfill(int n,String s) {             //s får lengde n, blanke henges på
        String ny="";
        int l=s.length();
        int diff=n-l;
        if(l>n) {               //må korte av:
            ny=s.substring(0,n);
        }else{                  //må fylle på:
            ny=s+ntegn(diff,' ');
        }//if        
        return ny;
    }//hfill(n,s)
    private final static String ntegn(int n,char c){
        String s="";
        for(int i=0;i<n;i++){s=s+c;}
        return s;
    }//blanke(n)
*/ //SNIP END--------------------------------------
    
}// class AlgoFitSin
