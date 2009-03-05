package geogebra3D.kernel3D;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra3D.Matrix.Ggb3DVector;

public class GeoPolygon3D extends GeoCoordSys2D {

	private GeoPoint3D[] points;
	private double[][] vertex;
	//GeoPoint[] points2D;
	//GeoPolygon poly2D;
	
	private boolean defined = false;		

		
	public GeoPolygon3D(Construction c, GeoPoint3D[] points) {
		super(c);
		setPoints(points);
		setAlphaValue(ConstructionDefaults3D.DEFAULT_POLYGON3D_ALPHA);
		//Application.debug("alpha="+getAlphaValue());
		//setCoordSys();
	}
	
	public void setPoints(GeoPoint3D[] points) {
		this.points=points;
		vertex = new double[points.length][3];

		//create the associated GeoPolygon
		//poly2D = (GeoPolygon)this.getKernel().Polygon(null, points2D)[0];
	}
	
	
	
	/**
	 * set the coord sys according to points.
	 */
	public void setCoordSys(){
		this.resetCoordSys();
		for(int i=0;(!this.isMadeCoordSys())&&(i<points.length);i++)
			this.addPointToCoordSys(points[i].getCoords(),true);
		
		//if there's no coord sys, the polygon is undefined
		if (!isMadeCoordSys()){
			setUndefined();
			return;
		}else
			setDefined();		
	}
	
	
	
	/**
	 * set the vertex #i to (x,y) coords
	 * @param i number of the vertex
	 * @param x first coord
	 * @param y second coord
	 */
	public void setVertex(int i, double x, double y){
		vertex[i][0]=x;
		vertex[i][1]=y;
		vertex[i][2]=0;
	}
	
	
	/**
	 * update all vertices, according to points and coord sys
	 */
	public void updateVertices(){	
		
		if (!isDefined())
			return;
		
		for(int i=0;i<points.length;i++){
			//project the point on the coord sys
			Ggb3DVector[] project=points[i].getCoords().projectPlane(this.getMatrix4x4());
			
			//check if the vertex lies on the coord sys
			if (!Kernel.isEqual(project[1].get(3), 0, Kernel.STANDARD_PRECISION)){
				setUndefined();
				return;
			}
			
			//set the vertex
			setVertex(i,project[1].get(1), project[1].get(2));
		}
	}
	
	/** return the vertices list 
	 * @return the vertices list */
	public double[][] getVertices(){
		return vertex;
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


	public boolean isEqual(GeoElement Geo) {
		// TODO Auto-generated method stub
		return false;
	}

	public void set(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	
	
	public void setUndefined() {
		defined = false;
	}

	public boolean isDefined() {
		return defined;
	}

	public void setDefined() {
		defined = true;
	}

	
	
	
	
	/** to be able to fill it with an alpha value */
	public boolean isFillable() {
		return true;
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
