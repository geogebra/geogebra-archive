package geogebra.kernel.statistics;

/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoPolynomialFromCoordinates;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.RegressionMath;


/** 
 * Fits a polynomial with given degree to list of points.
 * Adapted from AlgoFitLine and AlgoPolynomialFromCoordinates
 * (Borcherds)
 * @author Hans-Petter Ulven
 * @version 06.04.08
 */
public class AlgoFitPoly extends AlgoElement{

    private static final long serialVersionUID  =   1L;
    private GeoList         geolist;                        //input
    private NumberValue     degree;                         //input
    private GeoFunction     geofunction;                    //output
    private GeoElement      geodegree;
    
    public AlgoFitPoly(Construction cons, String label, GeoList geolist,NumberValue degree) {
        super(cons);
        this.geolist=geolist;
        this.degree=degree;
        geodegree=degree.toGeoElement();
        geofunction=new GeoFunction(cons);
        setInputOutput();
        compute();
        geofunction.setLabel(label);
    }//Constructor
    
    protected String getClassName() {return "AlgoFitPoly";}
        
    protected void setInputOutput(){
        input=new GeoElement[2];
        input[0]=geolist;
        input[1]=geodegree;
        output=new GeoElement[1];
        output[0]=geofunction;
        setDependencies();
    }//setInputOutput()
    
    public GeoFunction getFitPoly() {return geofunction;}
    
    protected final void compute() {
        int size=geolist.size();
        int par;
        boolean regok=true;
        double[] cof=null;
        if(!geolist.isDefined() || (size<1) ) {
            geofunction.setUndefined();
            return;
        }else{
            par=(int)Math.round(degree.getDouble());
            switch(par){
                case RegressionMath.QUAD:   
                    regok=RegressionMath.doQuad(geolist);
                    if(regok){
                        cof=new double[3];
                        cof[0]=RegressionMath.getP1();
                        cof[1]=RegressionMath.getP2();
                        cof[2]=RegressionMath.getP3();
                    }//else: ->                   
                    break;
                case RegressionMath.CUBIC:
                    regok=RegressionMath.doCubic(geolist);
                    if(regok){           
                        cof=new double[4];
                        cof[0]=RegressionMath.getP1();
                        cof[1]=RegressionMath.getP2();
                        cof[2]=RegressionMath.getP3();
                        cof[3]=RegressionMath.getP4();                        
                    }//else: ->
                    break;
                case RegressionMath.QUART:  
                    regok=RegressionMath.doQuart(geolist);
                    if(regok){
                        cof=new double[5];
                        cof[0]=RegressionMath.getP1();
                        cof[1]=RegressionMath.getP2();
                        cof[2]=RegressionMath.getP3();
                        cof[3]=RegressionMath.getP4();
                        cof[4]=RegressionMath.getP5();
                    }//else: ->
                    break;
                default: RegressionMath.doLinear(geolist);
                    regok=RegressionMath.doLinear(geolist);
                    if(regok){
                        cof=new double[2];
                        cof[0]=RegressionMath.getP1();
                        cof[1]=RegressionMath.getP2();
                    }//else: ->
            }//switch
            if(!regok){
                geofunction.setUndefined();
                return;  
            }else{
                geofunction.setFunction(AlgoPolynomialFromCoordinates.buildPolyFunctionExpression(cons.getKernel(),cof));
            }//if error in regression   
        }//if error in parameters
    }//compute()
    
}// class AlgoFitPoly



