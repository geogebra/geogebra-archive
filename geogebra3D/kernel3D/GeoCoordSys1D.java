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

public abstract class GeoCoordSys1D extends GeoCoordSys implements Path {
	

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
	
	
	/** set coords to origin O and vector (I-O).
	 * If I (or O) is infinite, I is used as direction vector.
	 * @param O origin point
	 * @param I unit point*/
	public void setCoord(GeoPoint3D O, GeoPoint3D I){
		
		if (I.isInfinite())
			if (O.isInfinite())
				setUndefined(); //TODO infinite line
			else
				setCoord(O.getCoords(),I.getCoords());
		else
			if (O.isInfinite())
				setCoord(I.getCoords(),O.getCoords());
			else
				setCoord(O.getCoords(),I.getCoords().sub(O.getCoords()));
		
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
		if (P.getWillingCoords()!=null){
			if(P.getWillingDirection()!=null){
				//project willing location using willing direction
				Ggb3DVector[] project = P.getWillingCoords().projectOnLineWithDirection(
						getOrigin(),
						getVx(),
						P.getWillingDirection());

				t = project[1].get(1);
			}else{
				//project current point coordinates
				Ggb3DVector[] project = P.getWillingCoords().projectOnLineWithDirection(
						getOrigin(),
						getVx(),
						P.getWillingCoords().sub(getOrigin()).crossProduct(getVx()).crossProduct(getVx()));			
				t = project[1].get(1);	
			}
		}else{
			//project current point coordinates
			Application.debug("project current point coordinates");
			Ggb3DVector[] project = P.getCoords().projectOnLineWithDirection(
					getOrigin(),
					getVx(),
					P.getCoords().sub(getOrigin()).crossProduct(getVx()).crossProduct(getVx()));			
			t = project[1].get(1);	
		}
		

		if (t<getMinParameter())
			t=getMinParameter();
		else if (t>getMaxParameter())
			t=getMaxParameter();

		
		
		
		
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

	
	

	////////////////////////////////////
	//
	
	/** return true if x is a valid coordinate (eg 0<=x<=1 for a segment)
	 * @param x coordinate
	 * @return true if x is a valid coordinate (eg 0<=x<=1 for a segment)
	 */
	abstract public boolean isValidCoord(double x);
	
	
	
	
	
	

	
}
