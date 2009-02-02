package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;

public abstract class GeoCoordSys1D extends GeoCoordSys implements PathOn {
	

	public GeoCoordSys1D(Construction c){
		super(c,1);
	}
	
	public GeoCoordSys1D(Construction c, Ggb3DVector O, Ggb3DVector V){
		this(c);
		setCoord(O,V);
	}
	
	
	public GeoCoordSys1D(Construction c, GeoPoint3D O, GeoPoint3D I){
		this(c);
		setCoord(O,I);
	}	
	
	
	/** set the matrix to [V O] */
	public void setCoord(Ggb3DVector a_O, Ggb3DVector a_V){
		setOrigin(a_O);
		setVx(a_V);
		
		updateDrawingMatrix();
	}
	
	
	/** set coords to origin O and vector (I-O) */
	public void setCoord(GeoPoint3D O, GeoPoint3D I){
		
		Ggb3DVector vO = O.getCoords();
		Ggb3DVector vI = I.getCoords();
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
	public Ggb3DVector getPoint(double lambda){
		Ggb3DVector v=new Ggb3DVector(new double[] {lambda,1});
		Ggb3DVector r=getMatrix().mul(v);		
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
		Ggb3DVector v = P.getInhomCoords();
		Ggb3DVector p = new Ggb3DVector(4);
		Ggb3DVector[] project = v.projectLine(getMatrix().getColumn(2).subVector(1, 3), getMatrix().getColumn(1).subVector(1, 3));
		

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
	
	

	
	
	
	
	public Ggb3DMatrix getMovingMatrix(Ggb3DMatrix toScreenMatrix){
		
		Ggb3DMatrix ret = toScreenMatrix.mul(getMatrix4x4());
		
		Ggb3DVector V = ret.getColumn(1); //gets direction vector of the path
		Ggb3DVector Vn1 = new Ggb3DVector(4); 
		Ggb3DVector Vn2 = new Ggb3DVector(4);
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
