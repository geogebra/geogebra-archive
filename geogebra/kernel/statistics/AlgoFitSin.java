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
 * 		and cover at least two extremums of the function.
 * 		At least three points should be monotonous before
 * 		each extremum.
 * 	Problems:
 * 		Non-linear regression is difficult, and the choice
 * 		of initial values for the parameters are highly critical.
 * 		The algorithm here might converge to a local minimum.
 * 		I am not sure if the algorithm in some cases might diverge or oscillate?
 * 		(Then it would stop after MAXITERATIONS and give an unpredictable answer.)
 */


public class AlgoFitSin extends AlgoElement{
	
    //Tuning of Levenberg-Marquardt iteration, debug
	private final static double	LMFACTORDIV		=	3.0d;	
	private final static double LMFACTORMULT	=	2.0d;
	private final static int	MAXITERATIONS	=	200;
	private final static double EPSILON			=	1E-14d;
	private final static double EPSSING			=	1E-20d;
	private final static boolean DEBUG			=	false;		//set false when finished
	
	// Properties
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
        if(!geolist.isDefined() || (size<6) ) {	//Direction-algo needs two flanks, 3 in each.
            geofunction.setUndefined();
            errorMsg("List not properlydefined");
            return;
        }else{
        	regMath = kernel.getRegressionMath();
        	getPoints();
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
        findParameters();       //Find initial parameters a,b,c,d
        sinus_Reg();            //Run LM nonlinear iteration
    }//doReg()

    
    public final static void findParameters() {
        double y;
        size=xd.length;
        double sum=0.0d,    max=yd[0],  min=max;
        //Find a and b:
        for(int i=0;i<size;i++){
            y=yd[i];
            sum+=y;
            if(y>max){max=y;}
            if(y<min){min=y;}
        }//for
        a=sum/size;                                    
        b=(max-min)/2.0d;                             debug("a= "+a+" b= "+b+" max= "+max+" min= "+min);
       
        //find c:
        //This time first and second local max/min, between rise and fall and vv
        //Observation: last y in a rise or decrease *is* the local extremum!
        max=min=-1.0d;
        int xmax=0,xmin=0;      
        int state=0;             //undecided so far...
        int current=0;
        int changes=0;          //undecided so far...
        
        for(int i=2;i<size;i++) {
            y=yd[i];
            current=direction(yd[i-2],yd[i-1],y);
            if( (current==1) || (current==-1) ){                 //steady up or steady down
                //do state bookkeeping
                if(state==0){        //just started:
                    state=current;      //set first state
                }else {             //we are on our way...
                    if(current!=state){ changes++;state=current;}//if change
                }//if steady up or down

                //Must check before updating extremums
                if(changes>=2){                             debug("Two changes on "+i);
                    break;                      
                }//if changes>=2
            
                //find first and second extremum                
                if(current==1){         //steady up
                    max=y;xmax=i;           //last is max so far
                }else if(current==-1){  //steady down
                    min=y;xmin=i;           //last is min so far
                }else{
                    //not steady...
                }//if 
                
            }//if up or down
             
            debug("i: "+i);
            debug("state: "+state+" current: "+current+" changes: "+changes+" max: "+max+" min: "+min+" xmax: "+xmax+" xmin: "+xmin);
        }//for all data
                
        double period=Math.abs(xd[xmax]-xd[xmin])*2;
        c=2*Math.PI/period;

        debug("changes:          firstmax:        firstmin:     xmax:     xmin:");
        debug(changes+"   "+max+"   "+min+"     "+xmax+"     "+xmin);
        debug("period: "+period+" c: "+c);            
        
        //Find d  
        // (d=0 might go well, but to be on the safe side...100 iterations should be enough?
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
        }//for: d-iterasjon 
        d=bestd;                                         
                                            debug("Parameters:  a= "+a+" b= "+b+" c= "+c+" d "+d);
    }//findParameters()
    
    public final static void sinus_Reg(){
        double lambda=0.0d;          //LM-damping coefficient
        double multfaktor=LMFACTORMULT;	// later?: divfaktor=LMFACTORDIV;
        double residual,old_residual=beta2(xd,yd,a,b,c,d);
        double diff=-1;         //negativ to start it off
        
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
                                                                        debug("Startlambda: "+lambda);

        while(Math.abs(da)+Math.abs(db)+Math.abs(dc)+Math.abs(dd)>EPSILON){//or while(Math.abs(diff)>EPSILON) ?
            iterations++;
                                                                        debug(""+iterations+"   : ");
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
                residual=beta2(xd,yd,newa,newb,newc,newd);      debug("ChiSqError: +"+residual);
                diff=residual-old_residual;                     debug("Residual difference: "+diff+"    lambda: "+lambda);
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
             if(d<-Math.PI){d+=reduction;}              debug("justifying: "+d);
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
        if( (y3>=y2)&&(y2>=y1) ){         //rising!
            return 1;
        }else if( (y1>=y2)&& (y2>=y3) ){ //all under a
            return -1;
        }else{                              //some over, som under...
            return 0;
        }//if
    }//direction()

    //
    private final   void getPoints(){
    	double[] xlist=null,ylist=null;
        double xy[]=new double[2];
        GeoElement geoelement;
        xlist=new double[size];    ylist=new double[size];
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
        xd=xlist;yd=ylist;
        if(error){errorMsg("getPoints(): Wrong list format...");}
    }//getPoints()    
    
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
/*SNIP START---------------------------------

    ///// ----- Test Interface ----- /////
    
    public final static void setXY(double[] x,double[] y){
        xd=x;yd=y;
    }//setXY()

    public final static double getA(){return a;}
    public final static double getB(){return b;}
    public final static double getC(){return c;}    
    public final static double getD(){return d;}
    public final static int getIterations(){return iterations;}
    //+ beta2(xd,yd,a,b,c,d) sum squared errors
    

    

*/ //SNIP END--------------------------------------
    
}// class AlgoFitSin
