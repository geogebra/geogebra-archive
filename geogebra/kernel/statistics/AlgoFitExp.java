package geogebra.kernel.statistics;

/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


import geogebra.util.RegressionMath;
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



/** 
 * Fits an a*e^(b*x) to a list of pints.
 * Adapted from AlgoFitLine and AlgoPolynomialFromCoordinates
 * (Borcherds)
 * @author Hans-Petter Ulven
 * @version 06.04.08
 */
public class AlgoFitExp extends AlgoElement{

    private static final long serialVersionUID  =   1L;
    private GeoList         geolist;                        //input
    private GeoFunction     geofunction;                    //output

    
    public AlgoFitExp(Construction cons, String label, GeoList geolist) {
        super(cons);
        this.geolist=geolist;
        geofunction=new GeoFunction(cons);
        setInputOutput();
        compute();
        geofunction.setLabel(label);
    }//Constructor
    
    protected String getClassName() {return "AlgoFitExp";}
        
    protected void setInputOutput(){
        input=new GeoElement[1];
        input[0]=geolist;
        output=new GeoElement[1];
        output[0]=geofunction;
        setDependencies();
    }//setInputOutput()
    
    public GeoFunction getFitExp() {return geofunction;}
    
    protected final void compute() {
        int size=geolist.size();
        boolean regok=true;
        double a,b;
        if(!geolist.isDefined() || (size<1) ) {
            geofunction.setUndefined();
            return;
        }else{
            regok=RegressionMath.doExp(geolist);
            if(regok){
                a=RegressionMath.getP1();
                b=RegressionMath.getP2();
                MyDouble A=new MyDouble(kernel,a);
                MyDouble B=new MyDouble(kernel,b);
                MyDouble E=new MyDouble(kernel,Math.E);
                FunctionVariable X=new FunctionVariable(kernel);
                ExpressionValue expr=new ExpressionNode(kernel,B,ExpressionNode.MULTIPLY,X);
                expr=new ExpressionNode(kernel,E,ExpressionNode.POWER,expr);
                ExpressionNode node=new ExpressionNode(kernel,A,ExpressionNode.MULTIPLY,expr);
                Function f=new Function(node,X);
                geofunction.setFunction(f);
                geofunction.setDefined(true);
            }else{
                geofunction.setUndefined();
                return;  
            }//if error in regression   
        }//if error in parameters
    }//compute()
    
}// class AlgoFitExp