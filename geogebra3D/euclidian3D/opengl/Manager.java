package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbVector;
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
	
	/** geometries types */
	static final int TRIANGLE_STRIP = GL.GL_TRIANGLE_STRIP;
	static final int QUAD_STRIP = GL.GL_QUAD_STRIP;
	
	
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
	/** geometry : segment */
	public GeometrySegment segment;
	/** geometry : plane */
	protected GeometryPlane plane;
	/** geometry : grid */
	protected GeometryGrid grid;
	/** geometry : sphere */
	public GeometrySphere sphere;
	/** brush */
	private Brush brush;
	
	
	//geogebra stuff
	private EuclidianView3D view3D;
	
	//coords stuff
	/** when drawing a cylinder, clock vectors to describe a circle */
	private GgbVector clockU = null;
	private GgbVector clockV = null;
	private GgbVector cylinderStart = null;
	private GgbVector cylinderEnd = null;
	private double cylinderThickness;
	private float textureStart, textureEnd;
	
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
		segment = new GeometrySegment(this);
		plane = new GeometryPlane(this);
		grid = new GeometryGrid(this);
		sphere = new GeometrySphere(this);
		
		brush = new Brush(this);
		
		//geogebra
		this.view3D = view3D;
		
		
	}
	
	
	public Brush getBrush(){
		return brush;
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
	// GL METHODS
	/////////////////////////////////////////////
	
	/**
	 * @return gl context
	 */
	public GL getGL(){
		return gl;
	}



	
	/////////////////////////////////////////////
	// GEOMETRY METHODS
	/////////////////////////////////////////////
	
	
	/** method used at start of Geometry.init() method
	 * @param geometry 
	 */
	abstract public void preInit(Geometry geometry);
	
	
	abstract public int startNewList();
	
	/** starting new geometry
	 * @param geometry 
	 * @param index index of the new geometry
	 */
	public void startListAndGeometry(Geometry geometry, int index){
		startList(geometry, index);
		startGeometry(geometry);
	}
	
	
	
	public void startListAndGeometry(Geometry geometry){
		startListAndGeometry(geometry, 0);
	}
	
	
	/** ending new geometry
	 * @param geometry 
	 */
	public void endListAndGeometry(){
		endGeometry();
		endList();
	}

	abstract public void startList(Geometry geometry, int index);
	
	public void startList(Geometry geometry){
		startList(geometry, 0);
	}
	
	abstract public void endList();

	
	abstract public void startGeometry(int type);
	
	abstract public void startGeometry(Geometry geometry);
	
	abstract public void endGeometry();
	
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
	// COORDS METHODS
	/////////////////////////////////////////////

	/** set a cylinder coords regarding vector direction
	 * @param p1
	 * @param p2
	 * @param thickness 
	 * @param textureStart 
	 * @param textureEnd 
	 */
	public void setCylinder(GgbVector p1, GgbVector p2, 
			double thickness,
			float textureStart, float textureEnd){
		cylinderStart = p1;
		cylinderEnd = p2;
		cylinderThickness = thickness;
		this.textureStart = textureStart;
		this.textureEnd = textureEnd;
		GgbVector[] vn = p2.sub(p1).completeOrthonormal();
		clockU = vn[0]; clockV = vn[1];
	}
	
	
	/** translate the current cylinder
	 * @param v
	 */
	public void translateCylinder(GgbVector v){
		cylinderStart = (GgbVector) cylinderStart.add(v);
		cylinderEnd = (GgbVector) cylinderEnd.add(v);
	}
	
	
	/** create a cylinder rule (for quad strip)
	 * @param u
	 * @param v
	 * @param texturePos 
	 */
	public void cylinderRule(double u, double v, double texturePos){
		
		//normal vector
		GgbVector vn = (GgbVector) clockV.mul(v).add(clockU.mul(u));
		normal((float) vn.getX(), (float) vn.getY(), (float) vn.getZ());
		
		//bottom vertex
		texture(textureStart,(float) texturePos);
		vertex((float) (cylinderStart.getX()+cylinderThickness*vn.getX()), 
				(float) (cylinderStart.getY()+cylinderThickness*vn.getY()),  
				(float) (cylinderStart.getZ()+cylinderThickness*vn.getZ()));
		//top vertex
		texture(textureEnd,(float) texturePos);
		vertex((float) (cylinderEnd.getX()+cylinderThickness*vn.getX()), 
				(float) (cylinderEnd.getY()+cylinderThickness*vn.getY()),  
				(float) (cylinderEnd.getZ()+cylinderThickness*vn.getZ()));
	}
	
	
	/////////////////////////////////////////////
	// SEGMENT METHODS
	/////////////////////////////////////////////
	
	public int newSegment(Color color, 
			GgbVector p1, GgbVector p2,
			float thickness,
			float scale,
			float posZero){
		return segment.create(color, p1, p2, thickness, scale, posZero);
	}
	
	/////////////////////////////////////////////
	// POLYGONS DRAWING METHODS
	/////////////////////////////////////////////

	
	abstract public void addVertexToPolygon(double x, double y, double z);
	
	
	
	/////////////////////////////////////////////
	// PLANE AND GRID METHODS
	/////////////////////////////////////////////

	
	abstract public int newPlane(Color color, float alpha, float size);
	
	abstract public int newGrid(Color color, float alpha, 
			float xmin, float xmax, float ymin, float ymax, 
			float dx, float dy, 
			float thickness);
	

	/////////////////////////////////////////////
	// SPHERE METHODS
	/////////////////////////////////////////////

	abstract public int newSphere(float x, float y, float z,
			float radius, Color color, float alpha);

	
	

}
