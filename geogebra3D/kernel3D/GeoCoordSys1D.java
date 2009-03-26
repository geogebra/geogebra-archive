package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPointInterface;
import geogebra.kernel.Path;
import geogebra.kernel.PathParameter;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;
import geogebra3D.Matrix.Ggb3DVector;
import geogebra3D.euclidian3D.EuclidianView3D;

public abstract class GeoCoordSys1D extends GeoCoordSys implements Path3D {
	

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
	public void setCoordFromPoints(Ggb3DVector a_O, Ggb3DVector a_I){
		 setCoord(a_O,a_I.sub(a_O));
	}
	
	/** set the matrix to [V O] */
	public void setCoord(Ggb3DVector a_O, Ggb3DVector a_V){
		setOrigin(a_O);
		setVx(a_V);
		
		setMadeCoordSys();
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


	
	
	
	// Path3D interface
	public boolean isPath(){
		return true;
	}
	
	public void pointChanged(GeoPointInterface PI){
		
		GeoPoint3D P = (GeoPoint3D) PI;

		
		
		//project P on line


		double t = 0;
		if (P.getMouseLoc()!=null && P.getMouseDirection()!=null){
			Ggb3DVector[] project = P.getMouseLoc().projectOnLineWithDirection(
					getOrigin(),
					getVx(),
					P.getMouseDirection());
			
			t = project[1].get(1);

			if (t<getMinParameter())
				t=getMinParameter();
			else if (t>getMaxParameter())
				t=getMaxParameter();

		}
		
		
		
		// set path parameter		
		PathParameter pp = P.getPathParameter();
		
		
		pp.setT(t);
		
		//udpate point using pathChanged
		pathChanged(P);
		
		

	}
	
	
	public void pathChanged(GeoPointInterface PI){
		
		GeoPoint3D P = (GeoPoint3D) PI;
		
		
		PathParameter pp = P.getPathParameter();
		P.setCoords(getPoint(pp.getT()),false);

	}
	
	
	public boolean isOnPath(GeoPointInterface PI, double eps){
		return false; //TODO
	}

	
	

	
	
	
	/*
	public Ggb3DMatrix4x4 getMovingMatrix(Ggb3DMatrix4x4 toScreenMatrix){
		
		Ggb3DMatrix4x4 ret = toScreenMatrix.mul(getMatrix4x4());
		
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
	*/
	
	
	

	
}
