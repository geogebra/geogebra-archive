package geogebra3D.euclidian3D.opengl;

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

	
	
	
	/** geometry : point */
	public GeometrySphere point;
	/** geometry : cylinder */
	public GeometryCylinder cylinder;
	/** geometry : cone */
	public GeometryCone cone;
	/** geometry : cursor */
	public GeometryCursor cursor;
	/** geometry : plane */
	public GeometryPlane plane;
	
	
	/** create a manager for geometries
	 * @param gl 
	 * @param glu 
	 */
	public Manager(GL gl, GLU glu){
		
		this.gl = gl;
		this.glu = glu;
		
		
		// creating geometries
		point = new GeometrySphere(this,false);
		cylinder = new GeometryCylinder(this,true);
		cone = new GeometryCone(this,true);
		cursor = new GeometryCursor(this);
		plane = new GeometryPlane(this);
		
		
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
	
	abstract public int newPlane(Color color, float alpha, float size);


	
	

}
