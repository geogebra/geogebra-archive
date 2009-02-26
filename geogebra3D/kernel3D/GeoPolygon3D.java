package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DVector;

public class GeoPolygon3D extends GeoCoordSys2D {

	GeoPoint3D[] points;
	GeoPoint[] points2D;
	GeoPolygon poly2D;
		
	public GeoPolygon3D(Construction c, GeoPoint3D[] points) {
		super(c);
		setPoints(points);
	}
	
	public void setPoints(GeoPoint3D[] points) {
		this.points=points;
		
		//TODO verify that points are coplanar
		// create a 2D coord sys 
		this.setCoord(points[0], points[1], points[2]);
		
		//project 3Dpoint in the 2D coord sys
		points2D = new GeoPoint[points.length];
		Algo3Dto2D algo;
		for(int i=0;i<points.length;i++){
			algo = new Algo3Dto2D(this.cons,points[i],this);
			points2D[i]= (GeoPoint) algo.getGeo();
		}

		//create the associated GeoPolygon
		poly2D = (GeoPolygon)this.getKernel().Polygon(null, points2D)[0];
	}
	
	
	//return the point2D list
	public double[][] getPoints2D()
	{
		double [][] p = new double[points2D.length][3];
		for(int i=0;i<points2D.length;i++){
			p[i][0]=points2D[i].inhomX;
			p[i][1]=points2D[i].inhomY;
			p[i][2]=0;
		}
		return p;
	}
	
	
	
	
	public GeoElement copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getGeoClassType() {
		return GEO_CLASS_POLYGON3D;
	}

	protected String getTypeString() {
		return "Polygon3D";
	}

	public boolean isDefined() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void setUndefined() {
		// TODO Auto-generated method stub

	}

	protected boolean showInAlgebraView() {
		// TODO Auto-generated method stub
		return true;
	}

	protected boolean showInEuclidianView() {
		// TODO Auto-generated method stub
		return true;
	}

	public String toValueString() {
		// TODO Auto-generated method stub
		return "todo-toValueString";
	}

	protected String getClassName() {
		return "GeoPolygon3D";
	}

}
