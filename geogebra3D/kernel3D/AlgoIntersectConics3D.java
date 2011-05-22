/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * AlgoTangentLine.java
 *
 * Created on 30. August 2001, 21:37
 */

package geogebra3D.kernel3D;

import geogebra.Matrix.CoordMatrixUtil;
import geogebra.Matrix.CoordSys;
import geogebra.Matrix.Coords;
import geogebra.euclidian.EuclidianConstants;
import geogebra.kernel.AlgoIntersectAbstract;
import geogebra.kernel.AlgoIntersectConics;
import geogebra.kernel.AlgoIntersectLineConic;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.AlgoIntersectND;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoLineND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;



/**
 *
 * @author  Markus
 * @version 
 */
public class AlgoIntersectConics3D extends AlgoIntersectND {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private GeoConicND A, B;  // input
    private GeoPoint3D [] P, D;     // output  
    
    /** 2d description of g when included in conic coord sys */
    private GeoLine g2d;
    /** 2d points created by using AlgoIntersectLineConic.intersectLineConic */
    private GeoPoint[] points2d;
        
    /**
     * 
     * @param cons
     * @param label
     * @param A 
     * @param B 
     */
    AlgoIntersectConics3D(Construction cons, String label, GeoConicND A, GeoConicND B) {
        this(cons, A,B);
        GeoElement.setLabels(label, P);            
    }
    
    /**
     * 
     * @param cons
     * @param labels
     * @param A 
     * @param B 
     */
    AlgoIntersectConics3D(Construction cons, String [] labels, GeoConicND A, GeoConicND B) {
        this(cons, A,B);
        GeoElement.setLabels(labels, P);            
    }
    
    public String getClassName() {
        return "AlgoIntersectConics";
    }

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_INTERSECT;
    }

    
    /**
     * 
     * @param cons
     * @param A 
     * @param B 
     */
    AlgoIntersectConics3D(Construction cons, GeoConicND A, GeoConicND B) {
        super(cons);
        this.A = A;
        this.B = B;                
        

        P  = new GeoPoint3D[4];
        D  = new GeoPoint3D[4];
        
        //helper algo
        g2d = new GeoLine(cons);
        points2d = new GeoPoint[4];
               
        for (int i=0; i < 4; i++) {
            P[i] = new GeoPoint3D(cons); 
            D[i] = new GeoPoint3D(cons);    
            points2d[i] = new GeoPoint(cons);   
        }
        
        setInputOutput(); // for AlgoElement
        

                
        compute();                      
    }   
    
    // for AlgoElement
    public void setInputOutput() {
        input = new GeoElement[2];
        input[0] = A;
        input[1] = B;
        
        output = P;            
        noUndefinedPointsInAlgebraView();
        setDependencies(); // done by AlgoElement
    }    
    

    protected final GeoPoint3D [] getIntersectionPoints() {
        return P;
    }
    
    protected GeoPoint3D [] getLastDefinedIntersectionPoints() {
        return D;
    }
    
    protected void setCoords(GeoPointND destination, GeoPointND source){
    	destination.setCoords(source.getCoordsInD(3),false);
    }
    

    /**
     * 
     * @return first conic input
     */
    GeoConicND getA() { return A; }
    
    
    /**
     * 
     * @return second conic input
     */
    GeoConicND getB() { return B; }

    
    public final String toString() {
    	return app.getPlain("IntersectionPointOfAB",A.getLabel(),B.getLabel());
    }

    
    

	protected void compute() {
		
		CoordSys csA = A.getCoordSys();
		CoordSys csB = B.getCoordSys();
		
		//check if coord sys are incident
		Coords cross = csA.getNormal().crossProduct(csB.getNormal());
		if (!cross.equalsForKernel(0,  Kernel.MIN_PRECISION)){	//line intersection
			Coords[] intersection = CoordMatrixUtil.intersectPlanes(A.getCoordSys().getMatrixOrthonormal(), B.getCoordSys().getMatrixOrthonormal());
			Coords op = csA.getNormalProjection(intersection[0])[1];
			Coords dp = csA.getNormalProjection(intersection[1])[1];		
			g2d.setCoords(dp.getY(), -dp.getX(), -dp.getY()*op.getX() +dp.getX()*op.getY());
			AlgoIntersectLineConic.intersectLineConic(g2d, A, points2d);
			//Application.debug(points2d[0]+"\n"+points2d[1]);
			
			P[0].setCoords(csA.getPoint(points2d[0].x, points2d[0].y), false);
			checkIsOnB(P[0]);
			P[1].setCoords(csA.getPoint(points2d[1].x, points2d[1].y), false);
			checkIsOnB(P[1]);
			
			P[2].setUndefined();P[3].setUndefined();
			/*
			Coords[] p = o.projectPlaneThruV(cs.getMatrixOrthonormal(), d);
			Coords p2d = new Coords(3);
			p2d.setX(p[1].getX());p2d.setY(p[1].getY());p2d.setZ(p[1].getW());
			// check if intersect point is on conic
			if (c.isOnFullConic(p2d, Kernel.MIN_PRECISION))
				P[0].setCoords(p[0], false);
			else
				setPointsUndefined();
				*/
		}else{//line parallel to conic coord sys
			Coords op = csA.getNormalProjection(csB.getOrigin())[1];
			if (!Kernel.isZero(op.getZ())){//coord sys strictly parallel
				setPointsUndefined(); //TODO infinite points ?
			}else{//coord sys included
				setPointsUndefined();
				//AlgoIntersectConics.in
				/*
				g2d.setCoords(dp.getY(), -dp.getX(), -dp.getY()*op.getX() +dp.getX()*op.getY());
				AlgoIntersectLineConic.intersectLineConic(g2d, c, points2d);
				//Application.debug(points2d[0]+"\n"+points2d[1]);
				P[0].setCoords(cs.getPoint(points2d[0].x, points2d[0].y), false);
				checkIsOnLine(P[0]);
				P[1].setCoords(cs.getPoint(points2d[1].x, points2d[1].y), false);
				checkIsOnLine(P[1]);
				*/
			}
		}
				
	}
	
	private void checkIsOnB(GeoPoint3D p){
		if (!p.isDefined())
			return;
		
		Coords pp = B.getCoordSys().getNormalProjection(p.getCoords())[1];
		Coords pp2d = new Coords(3);
		pp2d.setX(pp.getX());pp2d.setY(pp.getY());pp2d.setZ(pp.getW());
		if (!B.isOnFullConic(pp2d,Kernel.MIN_PRECISION))
			p.setUndefined();
			
	}
	
	
	
	private void setPointsUndefined(){
        for (int i=0; i < 4; i++) 
            P[i].setUndefined();                  
        
	}
}
