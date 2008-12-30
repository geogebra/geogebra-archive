package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.kernel.linalg.GgbMatrix;
import geogebra.kernel.linalg.GgbMatrix4x4;
import geogebra.kernel.linalg.GgbVector;
import geogebra.main.Application;

public abstract class GeoCoordSys1D extends GeoCoordSys implements PathOn {
	

	public GeoCoordSys1D(Construction c){
		super(c,1);
	}
	
	public GeoCoordSys1D(Construction c, GgbVector O, GgbVector V){
		this(c);
		setCoord(O,V);
	}
	
	
	public GeoCoordSys1D(Construction c, GeoPoint3D O, GeoPoint3D I){
		this(c);
		setCoord(O,I);
	}	
	
	
	/** set the matrix to [V O] */
	public void setCoord(GgbVector a_O, GgbVector a_V){
		setOrigin(a_O);
		setVx(a_V);
		
		updateDrawingMatrix();
	}
	
	
	/** set coords to origin O and vector (I-O) */
	public void setCoord(GeoPoint3D O, GeoPoint3D I){
		
		GgbVector vO = O.getCoords();
		GgbVector vI = I.getCoords();
		setCoord(vO,vI.sub(vO));
		
	}
	
	
	
	

	
	/** returns matrix corresponding to segment joining l1 to l2, using getLineThickness() */
	/*
	public GgbMatrix getSegmentMatrix(double l1, double l2){
		
	
		
		return GgbMatrix4x4.subSegmentX(getMatrix4x4(), l1, l2);
	}	
*/
	
	
	
	//public GeoPoint3D getPoint(double lambda){
	/** returns the point at position lambda on the coord sys */
	public GgbVector getPoint(double lambda){
		GgbVector v=new GgbVector(new double[] {lambda,1});
		GgbVector r=getMatrix().mul(v);		
		//r.SystemPrint();
		return r;
		//return new GeoPoint3D(getConstruction(), "M", r);
	}


	/** returns cs unit */
	public double getUnit(){
		return getMatrix().getColumn(1).norm();
	}


	
	
	
	// Path1D interface
	public boolean isPath1D(){
		return true;
	}
	
	public void pointChanged(GeoPoint3D P){
		//project P on line
		GgbVector v = P.getInhomCoords();
		GgbVector p = new GgbVector(4);
		GgbVector[] project = v.projectLine(getMatrix().getColumn(2).subVector(1, 3), getMatrix().getColumn(1).subVector(1, 3));
		

		if(!P.hasGeoElement2D()){
			p.set(project[0]);
			p.set(4, 1);
			P.setCoords(p,false); //avoid new pointChanged computation
			// set path parameter		
			PathParameters pps = P.getPathParameters(1);
			pps.setT(project[1].get(1));
		}else{								
			GeoPoint P2D = (GeoPoint) P.getGeoElement2D();
			P2D.setCoords(project[1].get(1), 0, 1); //use 2D algo	
			P2D.updateRepaint();//TODO remove this (or not)
			P.setCoords(getPoint(P2D.getPathParameter().getT()),false); //avoid new pointChanged computation
		}

	}
	
	
	public void pathChanged(GeoPoint3D P){
		if(!P.hasGeoElement2D()){
			PathParameters pps = P.getPathParameters(1);
			P.setCoords(getPoint(pps.getT()),false);
		}else{
			GeoPoint P2D = (GeoPoint) P.getGeoElement2D();
			P.setCoords(getPoint(P2D.getPathParameter().getT()),false);
		}
	}
	
	
	public boolean isOnPath(GeoPoint3D P, double eps){
		return false; //TODO
	}

	
	
	public Path getPath2D(){
		return null;
	}
	
	

	
	
	
	
	public GgbMatrix getMovingMatrix(GgbMatrix toScreenMatrix){
		
		GgbMatrix ret = toScreenMatrix.mul(getMatrix4x4());
		
		GgbVector V = ret.getColumn(1); //gets direction vector of the path
		GgbVector Vn1 = new GgbVector(4); 
		GgbVector Vn2 = new GgbVector(4);
		if (V.get(1)!=0){
			Vn1.set(1,-V.get(2));
			Vn1.set(2,V.get(1));
			Vn1.normalize();
		}else{
			Vn1.set(1, 1.0);
		}
		Vn2 = V.crossProduct(Vn1);
		Vn2.normalize();	
		
		ret.set(Vn1, 2);ret.set(Vn2, 3);
		
		return ret;
	}
	
	
	

	
}
