package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.Coords;
import geogebra.main.Application;
import geogebra3D.euclidian3D.EuclidianView3D;

import java.awt.Color;
import java.nio.FloatBuffer;

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
	static final int TRIANGLE_FAN = GL.GL_TRIANGLE_FAN;
	static final int QUAD_STRIP = GL.GL_QUAD_STRIP;
	static final int QUADS = GL.GL_QUADS;
	static final int TRIANGLES = GL.GL_TRIANGLES;

	/** color factor for highlighting */
	private float colorFactor;
	
	// GL 
	protected GL gl;
	protected GLU glu;

	
	
	// geometries
	/** geometry : cursor */
	public PlotterCursor cursor;
	/** geometry : view buttons */
	private PlotterViewButtons viewButtons;
	/** brush */
	private PlotterBrush brush;
	/** surfaces */
	private PlotterSurface surface;
	/** text */
	private PlotterText text;
	
	//geogebra stuff
	private EuclidianView3D view3D;
	
	//coords stuff
	/** when drawing a cylinder, clock vectors to describe a circle */
	private Coords clockU = null;
	private Coords clockV = null;
	private Coords cylinderStart = null;
	private Coords cylinderEnd = null;
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
		
		brush = new PlotterBrush(this);
		surface = new PlotterSurface(this);
		
		text = new PlotterText(this);
		

		cursor = new PlotterCursor(this);
		viewButtons = new PlotterViewButtons(this);
		
		//geogebra
		this.view3D = view3D;
		
		
	}
	
	public PlotterViewButtons getViewButtons(){
		return viewButtons;
	}
	
	public PlotterBrush getBrush(){
		return brush;
	}
	
	
	public PlotterSurface getSurface(){
		return surface;
	}
	
	public PlotterText getText(){
		return text;
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
	
	/**
	 * update manager stuff
	 */
	public void update(){
		//color factor for highlighting
		colorFactor = ((float) (Math.sin(System.currentTimeMillis()/200.0)+1))/2f;
		//colorFactor = (float) Math.abs(Math.sin(System.currentTimeMillis()/300.0))*2;
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

	/**
	 * @return glu context
	 */
	public GLU getGLU(){
		return glu;
	}

	
	/////////////////////////////////////////////
	// LIST METHODS
	/////////////////////////////////////////////
	
	
	
	
	abstract public int startNewList();
	

	
	abstract public void endList();

	
	abstract public void startGeometry(int type);
	
	
	abstract public void endGeometry();
	
	abstract public int startPolygon(float nx, float ny, float nz);

	
	abstract public void endPolygon();
	

	
	
	
	
	
	/////////////////////////////////////////////
	// DRAWING METHODS
	/////////////////////////////////////////////

	
	abstract public void draw(int index);
	abstract public void remove(int index);

	/** creates a vertex at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	abstract protected void vertex(float x, float y, float z);
	
	/** creates a vertex at coordinates v
	 * @param v
	 */
	protected void vertex(Coords v){
		vertex((float) v.getX(),(float) v.getY(),(float) v.getZ());
	}

	
	/** creates a vetices at the specified coordinates
	 * @param v an array of x,y and z values of vertices
	 */
	abstract protected void vertices(FloatBuffer v, int count);
	
	/** creates a normal at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	abstract protected void normal(float x, float y, float z);
	
	/** creates a normal at coordinates n
	 * @param n
	 */
	protected void normal(Coords n){
		normal((float) n.getX(),(float) n.getY(),(float) n.getZ());
	}
	
	
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
	public void setCylinder(Coords p1, Coords p2, 
			double thickness,
			float textureStart, float textureEnd){
		cylinderStart = p1;
		cylinderEnd = p2;
		cylinderThickness = thickness;
		this.textureStart = textureStart;
		this.textureEnd = textureEnd;
		Coords[] vn = p2.sub(p1).completeOrthonormal();
		clockU = vn[0]; clockV = vn[1];
	}
	
	
	/** translate the current cylinder
	 * @param v
	 */
	public void translateCylinder(Coords v){
		cylinderStart = (Coords) cylinderStart.add(v);
		cylinderEnd = (Coords) cylinderEnd.add(v);
	}
	
	
	/** create a cylinder rule (for quad strip)
	 * @param u
	 * @param v
	 * @param texturePos 
	 */
	public void cylinderRule(double u, double v, double texturePos){
		
		//normal vector
		Coords vn = (Coords) clockV.mul(v).add(clockU.mul(u));
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
	// POLYGONS DRAWING METHODS
	/////////////////////////////////////////////

	
	abstract public void addVertexToPolygon(double x, double y, double z);
	
	
	

	
	
	/////////////////////////////////////////////
	// COLOR METHODS
	/////////////////////////////////////////////

	
	/** return the color for highlighting object
	 * @param c
	 * @param amplitude amplitude of the variation of color
	 * @return the color for highlighting object
	 */
	public Coords getHigthlighting(Coords color, Coords colorHighlighted){
				
		return color.mul(colorFactor).add(colorHighlighted.mul(1-colorFactor));
	}
	
	

}
