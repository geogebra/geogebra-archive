package geogebra3D.euclidian3D.opengl;

import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 * Class that manage all geometry objects
 * 
 * @author ggb3D
 *
 */
abstract public class Manager {
	
	/** direct rendering */
	static final int TYPE_GLLIST = 1;
	
	
	
	// GL 
	protected GL gl;
	protected GLU glu;

	
	
	// geometries
	/** geometry : point */
	public GeometryPoint point;
	/** geometry : cylinder */
	public GeometryCylinder cylinder;
	/** geometry : cone */
	public GeometryCone cone;
	/** geometry : cursor */
	public GeometryCursor cursor;
	/** geometry : plane */
	public GeometryPlane plane;
	/** geometry : sphere */
	public GeometrySphere sphere;
	
	
	//geogebra stuff
	private EuclidianView3D view3D;
	
	
	/** create a manager for geometries
	 * @param gl 
	 * @param glu 
	 * @param view3D 3D view
	 */
	public Manager(GL gl, GLU glu, EuclidianView3D view3D){
		
		this.gl = gl;
		this.glu = glu;
		
		
		// creating geometries
		point = new GeometryPoint(this,false);
		cylinder = new GeometryCylinder(this,true);
		cone = new GeometryCone(this,true);
		cursor = new GeometryCursor(this);
		plane = new GeometryPlane(this);
		sphere = new GeometrySphere(this);
		
		//geogebra
		this.view3D = view3D;
		
		
	}
	
	/////////////////////////////////////////////
	// GEOGEBRA METHODS
	/////////////////////////////////////////////
	
	/** return the 3D view
	 * @return the 3D view
	 */
	public EuclidianView3D getView3D(){
		return view3D;
	}
	
	/////////////////////////////////////////////
	// GEOMETRY METHODS
	/////////////////////////////////////////////
	
	
	/** method used at start of Geometry.init() method
	 * @param geometry 
	 */
	abstract public void preInit(Geometry geometry);
	
	
	/** starting new geometry
	 * @param geometry 
	 * @param index index of the new geometry
	 */
	abstract public void startGeometry(Geometry geometry, int index);
	
	
	
	public void startGeometry(Geometry geometry){
		startGeometry(geometry, 0);
	}
	
	
	/** ending new geometry
	 * @param geometry 
	 */
	abstract public void endGeometry(Geometry geometry);


	
	
	abstract public int startPolygon(float nx, float ny, float nz);

	
	abstract public void endPolygon();
	

	
	
	
	
	
	/////////////////////////////////////////////
	// DRAWING METHODS
	/////////////////////////////////////////////

	
	abstract public void draw(int index);
	abstract public void remove(int index);

	/** draw the geometry
	 * @param geometry
	 */
	public void draw(Geometry geometry){
		draw(geometry,0);
	}
	
	
	/** draw the indexed geometry
	 * @param geometry
	 * @param index index of the geometry
	 */
	abstract public void draw(Geometry geometry, int index);
	
	
	
	/** creates a vertex at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	abstract protected void vertex(float x, float y, float z);
	
	
	/** creates a normal at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	abstract protected void normal(float x, float y, float z);
	
	
	/** creates a texture at coordinates (x,y)
	 * @param x x coord
	 * @param y y coord
	 */
	abstract protected void texture(float x, float y);


	/** creates a color (r,g,b)
	 * @param r red
	 * @param g green
	 * @param b blue
	 * 
	 */
	abstract protected void color(float r, float g, float b);
	
	/** creates a color (r,g,b,a)
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a blue alpha
	 * 
	 */
	abstract protected void color(float r, float g, float b, float a);
	
	
	/////////////////////////////////////////////
	// POLYGONS DRAWING METHODS
	/////////////////////////////////////////////

	
	abstract public void addVertexToPolygon(double x, double y, double z);
	
	
	
	/////////////////////////////////////////////
	// PLANE METHODS
	/////////////////////////////////////////////

	
	abstract public int newPlane(Color color, float alpha, float size);
	

	/////////////////////////////////////////////
	// SPHERE METHODS
	/////////////////////////////////////////////

	abstract public int newSphere(float x, float y, float z,
			float radius, Color color, float alpha);

	
	

}
