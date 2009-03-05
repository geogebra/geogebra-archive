package geogebra3D.euclidian3D;





import geogebra3D.Matrix.Ggb3DMatrix;
import geogebra3D.Matrix.Ggb3DMatrix4x4;

import java.awt.Color;
import java.awt.Dimension;
import java.nio.IntBuffer;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLUtessellator;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;



/**
 * 
 * Used for openGL display.
 * <p>
 * It provides:
 * <ul>
 * <li> methods for displaying {@link Drawable3D}, with painting parameters </li>
 * <li> methods for picking object </li>
 * </ul>
 * 
 * @author ggb3D
 * 
 * 
 * 
 */
public class EuclidianRenderer3D implements GLEventListener {
	
	// openGL variables
	private GLU glu= new GLU();
	
	/** canvas usable for a JPanel */
	public GLCanvas canvas;
	

	private GLCapabilities caps;
	private GL gl;
	private GLUquadric quadric;
	private FPSAnimator animator;
	
	private IntBuffer selectBuffer;
	private static int BUFSIZE = 512;
	private static int MOUSE_PICK_WIDTH = 3;
	
	
	// other
	private DrawList3D drawList3D;
	
	private EuclidianView3D m_view3D;
	
	// for drawing
	private Ggb3DMatrix4x4 m_drawingMatrix; //matrix for drawing
	
	/** no dash. */
	static public double[][] DASH_NONE = null;
	/** simple dash: 0.1-(0.1), ... */
	static public double[][] DASH_SIMPLE = {{0.1,0.1}};
	/** dotted dash: 0.03-(0.1), ... */
	static public double[][] DASH_DOTTED = {{0.03,0.1}};
	/** dotted/dashed dash: 0.1-(0.1)-0.03-(0.1), ... */
	static public double[][] DASH_DOTTED_DASHED = {DASH_SIMPLE[0],DASH_DOTTED[0]};
	
	private double[][] m_dash = DASH_NONE; // dash is composed of couples {length of line, length of hole}
	private double m_dash_factor; // for unit factor
	
	
	private double m_thickness;
	
	
	/** no arrows */
	static final public int ARROW_TYPE_NONE=0;
	/** simple arrows */
	static final public int ARROW_TYPE_SIMPLE=1;
	private int m_arrowType=ARROW_TYPE_NONE;
	
	private double m_arrowLength, m_arrowWidth;
	
	
	// for picking
	private int mouseX, mouseY;
	private boolean waitForPick = false;
	
	/**
	 * creates a renderer linked to an {@link EuclidianView3D} 
	 * @param view the {@link EuclidianView3D} linked to 
	 */
	public EuclidianRenderer3D(EuclidianView3D view){
		super();
		
	    caps = new GLCapabilities();
	    
	    //anti-aliasing
    	caps.setSampleBuffers(true);
        caps.setNumSamples(4);    	
        
        //avoid flickering
    	caps.setDoubleBuffered(true);	    
    	
    	//canvas
	    canvas = new GLCanvas(caps);
	    //canvas = new GLJPanel(caps);
        //canvas.setOpaque(false);
	    canvas.addGLEventListener(this);
	    
	    //animator : 60 frames per second
	    animator = new FPSAnimator( canvas, 60 );
        animator.setRunAsFastAsPossible(true);	  
        animator.start();
		
		this.m_view3D=view;
	}
	
	
	/**
	 * set the list of {@link Drawable3D} to be drawn
	 * @param dl list of {@link Drawable3D}
	 */
	public void setDrawList3D(DrawList3D dl){
		drawList3D = dl;
	}
	
	
	/**
	 * 
	 * openGL method called when the display is to be computed.
	 * <p>
	 * First, it calls {@link #doPick()} if a picking is to be done.
	 * Then, for each {@link Drawable3D}, it calls:
	 * <ul>
	 * <li> {@link Drawable3D#drawHidden(EuclidianRenderer3D)} to draw hidden parts (dashed segments, lines, ...) </li>
	 * <li> {@link Drawable3D#drawPicked(EuclidianRenderer3D)} to show objects that are picked (highlighted) </li>
	 * <li> {@link Drawable3D#drawTransp(EuclidianRenderer3D)} to draw transparent objects (planes, spheres, ...) </li>
	 * <li> {@link Drawable3D#drawHiding(EuclidianRenderer3D)} to draw in the z-buffer objects that hides others (planes, spheres, ...) </li>
	 * <li> {@link Drawable3D#drawTransp(EuclidianRenderer3D)} to re-draw transparent objects for a better alpha-blending </li>
	 * <li> {@link Drawable3D#draw(EuclidianRenderer3D)} to draw not hidden parts (dash-less segments, lines, ...) </li>
	 * </ul>
	 */
    public void display(GLAutoDrawable gLDrawable) {
    	
        gl = gLDrawable.getGL();
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        
        //picking
        if(waitForPick)
        	doPick();
        
        //start drawing
        viewOrtho();
        
        m_view3D.update();
        
        //init drawing matrix to view3D toScreen matrix
        gl.glLoadMatrixd(m_view3D.getToScreenMatrix().get(),0);

        //drawing hidden part
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawHidden(this);	
		}
     
        
		//drawing picked parts
		setMaterial(new Color(0f,0f,0f),0.75f);
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawPicked(this);	
		}
       
        

		//drawing transparents parts
		//gl.glDisable(GL.GL_CULL_FACE);
		gl.glDepthMask(false);
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawTransp(this);	
		}
		gl.glDepthMask(true);
		
		
		//drawing hiding parts
		gl.glClear(GL.GL_DEPTH_BUFFER_BIT); //clear depth buffer
        gl.glColorMask(false,false,false,false); //no writing in color buffer		
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawHiding(this);	
		}
		gl.glColorMask(true,true,true,true);
		//gl.glEnable(GL.GL_CULL_FACE);
		
		
		//re-drawing transparents parts for better transparent effect
		//TODO improve it !
		gl.glDepthMask(false);
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawTransp(this);	
		}
		gl.glDepthMask(true);
		
		
		
		
		//drawing not hidden parts
		//gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glDisable(GL.GL_BLEND);
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.draw(this);	
		}
		//gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glEnable(GL.GL_BLEND);

		
		
		gLDrawable.swapBuffers(); //TODO



    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * openGL method called when the canvas is reshaped.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
    {
      GL gl = drawable.getGL();
      
      //TODO change this
      gl.glViewport(0, 0, w, h);
      gl.glMatrixMode(GL.GL_PROJECTION);
      gl.glLoadIdentity();
      gl.glOrtho(0.0, 8.0, 0.0, 8.0, -0.5, 2.5);
      gl.glMatrixMode(GL.GL_MODELVIEW);
      gl.glLoadIdentity();
    }

    /**
     * openGL method called when the display change.
     * empty method
     */
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
        boolean deviceChanged)
    {
    }    
    
    
    
    
    
    
    
    
    
    
    
    
    ///////////////////////////////////////////////////
    //
    // pencil methods
    //
    /////////////////////////////////////////////////////
    
    
    /**
     * sets the material used by the pencil
     * 
     * @param c the color of the pencil
     * @param alpha the alpha value for blending
     */
    public void setMaterial(Color c, double alpha){
    	//TODO use alpha value
    	float color[] = { ((float) c.getRed())/256f, ((float) c.getGreen())/256f, ((float) c.getBlue())/256f, (float) alpha };
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, color, 0);
    }
    
    
    /**
     * sets the dash used by the pencil.
     * 
     * @param a_dash description of the dash, see {@link #DASH_NONE}, {@link #DASH_SIMPLE}, ...
     */
    public void setDash(double[][] a_dash){
    	m_dash = a_dash;
    }
    
    /**
     * sets the thickness used by the pencil.
     * 
     * @param a_thickness the thickness
     */
    public void setThickness(double a_thickness){
    	m_thickness = a_thickness;
    }
    
    
    /**
     * gets the current thickness of the pencil.
     * 
     * @return the thickness
     */
    public double getThickness(){
    	return m_thickness;
    }
    
    //arrows
    
    /**
     * sets the type of arrow used by the pencil.
     * 
     * @param a_arrowType type of arrow, see {@link #ARROW_TYPE_NONE}, {@link #ARROW_TYPE_SIMPLE}, ... 
     */
    public void setArrowType(int a_arrowType){
    	m_arrowType = a_arrowType;
    } 
    
    /**
     * sets the width of the arrows painted by the pencil.
     * 
     * @param a_arrowWidth the width of the arrows
     */
    public void setArrowWidth(double a_arrowWidth){
    	m_arrowWidth = a_arrowWidth;
    } 
    
    
    /**
     * sets the length of the arrows painted by the pencil.
     * 
     * @param a_arrowLength the length of the arrows
     */
    public void setArrowLength(double a_arrowLength){
    	m_arrowLength = a_arrowLength;
    } 
    
    
    /**
     * sets the matrix in which coord sys the pencil draws.
     * 
     * @param a_matrix the matrix
     */
    public void setMatrix(Ggb3DMatrix4x4 a_matrix){
    	m_drawingMatrix=a_matrix;
    }
    
    
    /**
     * gets the matrix describing the coord sys used by the pencil.
     * 
     * @return the matrix
     */
    public Ggb3DMatrix4x4 getMatrix(){
    	return m_drawingMatrix;
    }
    
    
    /**
     * sets the drawing matrix to openGL.
     * same as initMatrix(m_drawingMatrix)
     */
    private void initMatrix(){
    	initMatrix(m_drawingMatrix);
    }
    
    /**
     * sets a_drawingMatrix to openGL.
     * @param a_drawingMatrix the matrix
     */
    private void initMatrix(Ggb3DMatrix a_drawingMatrix){
    	gl.glPushMatrix();
		gl.glMultMatrixd(a_drawingMatrix.get(),0);
    }    
    
    /**
     * turn off the last drawing matrix set in openGL.
     */
    private void resetMatrix(){
    	gl.glPopMatrix();
    }
    
    
    
    
    

    
    
    
    
    ///////////////////////////////////////////////////////////
    //drawing geometries
    
    /**
     * draws a segment from x=x1 to x=x2 according to drawing matrix
     * 
     * @param a_x1 start of the segment
     * @param a_x2 end of the segment
     * 
     */
    public void drawSegment(double a_x1, double a_x2){

    	switch(m_arrowType){
    	case ARROW_TYPE_NONE:
    	default:
    		drawSegmentDashedOrNot(a_x1, a_x2, m_dash!=null);
    	break;
    	case ARROW_TYPE_SIMPLE:
    		double x3=a_x2-m_arrowLength/m_drawingMatrix.getUnit(Ggb3DMatrix4x4.X_AXIS);
    		double thickness = getThickness();
    		setThickness(m_arrowWidth);
    		drawCone(x3,a_x2);
    		setThickness(thickness);
    		if (x3>a_x1)
    			drawSegmentDashedOrNot(a_x1, x3, m_dash!=null);
    		break;
    	}

    } 
    
    
    /**
     * 
     * draws a segment from x=x1 to x=x2 according to drawing matrix, dashed or not according to the a_dash value.
     * 
     * @param a_x1 start of the segment
     * @param a_x2 end of the segment
     * @param a_dash true if the segment is to be dashed
     */
    private void drawSegmentDashedOrNot(double a_x1, double a_x2, boolean a_dash){
    	if (a_dash)
    		drawSegmentDashed(a_x1, a_x2);
    	else
    		drawSegmentNotDashed(a_x1, a_x2);
    }
    
    
    /**
     * 
     * draws a not dashed segment from x=x1 to x=x2 according to drawing matrix.
     * 
     * @param a_x1 start of the segment
     * @param a_x2 end of the segment
     */
    private void drawSegmentNotDashed(double a_x1, double a_x2){
    	initMatrix(m_drawingMatrix.segmentX(a_x1, a_x2));
    	drawCylinder(m_thickness);
    	resetMatrix();
    } 
   
    
    /**
     * 
     * draws a dashed segment from x=x1 to x=x2 according to drawing matrix.
     * 
     * @param a_x1 start of the segment
     * @param a_x2 end of the segment
     */
    private void drawSegmentDashed(double a_x1, double a_x2){
		
    	m_dash_factor = 1/m_drawingMatrix.getUnit(Ggb3DMatrix4x4.X_AXIS);
    	for(double l1=a_x1; l1<a_x2;){
    		double l2=l1;
    		for(int i=0; (i<m_dash.length)&&(l1<a_x2); i++){
    			l2=l1+m_dash_factor*m_dash[i][0];
    			if (l2>a_x2) l2=a_x2;
    			drawSegmentNotDashed(l1,l2);
    			l1=l2+m_dash_factor*m_dash[i][1];
    		}	
    	} 	
    	
    } 
    
    
    
    /** 
     * draws a segment from x=0 to x=1 according to current drawing matrix.
     */
    public void drawSegment(){
    	drawSegment(0,1);
    }
    
    
    
    /** 
     * draws a line according to current drawing matrix.
     */
    public void drawLine(){
    	//TODO use frustum
    	drawSegment(-20,21);
    }  
    
    
    /** 
     * draws a ray (half-line) according drawing matrix.
     */
    public void drawRay(){
    	//TODO use frustum
    	drawSegment(0,21);
    }  
    
    
    
    
    
    
    
    /** draws a cone from x=x1 to x=x2 according to current drawing matrix.
     * @param a_x1 x-coordinate of the basis
     * @param a_x2 x-coordinate of the top
     */
    public void drawCone(double a_x1, double a_x2){
    	initMatrix(m_drawingMatrix.segmentX(a_x1, a_x2));
    	drawCone(m_thickness);
    	resetMatrix();
    } 
 
    
    
    
    
    

    
    /** draws a quad according to current drawing matrix.  
     * @param a_x1 x-coordinate of the top-left corner
     * @param a_y1 y-coordinate of the top-left corner
     * @param a_x2 x-coordinate of the bottom-right corner
     * @param a_y2 y-coordinate of the bottom-right corner
     */
    public void drawQuad(double a_x1, double a_y1, double a_x2, double a_y2){
    	initMatrix(m_drawingMatrix.quad(a_x1, a_y1, a_x2, a_y2));
    	drawQuad();
    	resetMatrix();
    }
    
    /** draws a grid according to current drawing matrix.
     * @param a_x1 x-coordinate of the top-left corner
     * @param a_y1 y-coordinate of the top-left corner
     * @param a_x2 x-coordinate of the bottom-right corner
     * @param a_y2 y-coordinate of the bottom-right corner
     * @param a_dx distance between two x-lines
     * @param a_dy distance between two y-lines
     */
    public void drawGrid(double a_x1, double a_y1, 
    		double a_x2, double a_y2, 
    		double a_dx, double a_dy){
    	
    	double xmin, xmax;
    	if (a_x1<a_x2){
    		xmin=a_x1;
    		xmax=a_x2;
    	}else{
    		xmin=a_x2;
    		xmax=a_x1;
    	}
    	
    	double ymin, ymax;
    	if (a_y1<a_y2){
    		ymin=a_y1;
    		ymax=a_y2;
    	}else{
    		ymin=a_y2;
    		ymax=a_y1;
    	}
    	
    	int nXmin= (int) Math.ceil(xmin/a_dx);
    	int nXmax= (int) Math.floor(xmax/a_dx);
    	int nYmin= (int) Math.ceil(ymin/a_dy);
    	int nYmax= (int) Math.floor(ymax/a_dy);
    	
    	//Application.debug("n = "+nXmin+","+nXmax+","+nYmin+","+nYmax);
    	
    	Ggb3DMatrix4x4 matrix = new Ggb3DMatrix4x4();
     	
    	matrix.set(getMatrix());
    	for (int i=nYmin; i<=nYmax; i++){
    		setMatrix(matrix.translateY(i*a_dy));
        	drawSegment(xmin, xmax);
    	}
    	setMatrix(matrix);
    	
    	matrix.set(getMatrix());
    	Ggb3DMatrix4x4 matrix2 = matrix.mirrorXY();
    	for (int i=nXmin; i<=nXmax; i++){
    		setMatrix(matrix2.translateY(i*a_dx));
        	drawSegment(ymin, ymax);
    	}
    	setMatrix(matrix);     		

    }  
    
    
    /**
     * draws a sphere according to current drawing matrix.
     * 
     * @param radius radius of the sphere
     */
    public void drawSphere(float radius){
    	initMatrix();
    	glu.gluSphere(quadric, radius, 16, 16);
    	resetMatrix();
    }
    
  
    
    /**
     * draws a polygon according to current drawing matrix.
     * 
     * @param points coordinates of the vertices
     */
    public void drawPolygon(double[][] points){    	
    	initMatrix();
    	
    	gl.glDisable(GL.GL_CULL_FACE);
    	
    	
	    EuclidianRenderer3DTesselCallBack tessCallback = new EuclidianRenderer3DTesselCallBack(gl, glu);

	    
	    GLUtessellator tobj = glu.gluNewTess();

	    glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
	    glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
	    glu.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);// endCallback);
	    glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
	    glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

	    gl.glShadeModel(GL.GL_SMOOTH);
	    glu.gluTessBeginPolygon(tobj, null);
	    glu.gluTessBeginContour(tobj);
	    
	    for (int i=0;i<points.length;i++)
	    	glu.gluTessVertex(tobj, points[i], 0, points[i]);

	    glu.gluTessEndContour(tobj);
	    glu.gluTessEndPolygon(tobj);
	    
	    glu.gluDeleteTess(tobj);
        
	   	gl.glEnable(GL.GL_CULL_FACE);
      
        resetMatrix();
    }
    

    
    
    
    ///////////////////////////////////////////////////////////
    //drawing primitives TODO use glLists
    


    
    private void drawCylinder(double a_thickness){
    	gl.glRotatef(90f, 0.0f, 1.0f, 0.0f); //switch z-axis to x-axis
    	glu.gluCylinder(quadric, a_thickness, a_thickness, 1.0f, 8, 1);
    }
    
    
    private void drawCone(double a_thickness){
    	gl.glRotatef(90f, 0.0f, 1.0f, 0.0f); //switch z-axis to x-axis
    	glu.gluCylinder(quadric, a_thickness, 0, 1.0f, 8, 1);
    }   
    
    
    
    public void drawTriangle(){    	
    	initMatrix();
    	
        gl.glBegin(GL.GL_TRIANGLES);	
        
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);	
        gl.glVertex3f(1.0f, 0.0f, 0.0f);	
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
                
        gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);	
        gl.glVertex3f(0.0f, 1.0f, 0.0f);	
        gl.glVertex3f(1.0f, 0.0f, 0.0f);        	
        
        gl.glEnd();	
        
        resetMatrix();
    }
    
    
    
    
    private void drawQuad(){    	
 
       	gl.glDisable(GL.GL_CULL_FACE);
 	
    	
        gl.glBegin(GL.GL_QUADS);	
        
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);	
        gl.glVertex3f(1.0f, 0.0f, 0.0f);	
        gl.glVertex3f(1.0f, 1.0f, 0.0f);	
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
              
        gl.glEnd();		
        
	   	gl.glEnable(GL.GL_CULL_FACE);
       
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //////////////////////////////////////
    // picking
    
    /**
     * sets the mouse locations to (x,y) and asks for picking.
     * 
     * @param x x-coordinate of the mouse
     * @param y y-coordinate of the mouse
     */
    public void setMouseLoc(int x, int y){
    	mouseX = x;
    	mouseY = y;
    	waitForPick = true;
    }
    
    
    /**
     * does the picking to sets which objects are under the mouse coordinates.
     */
    public void doPick(){
    	
    	
    	selectBuffer = BufferUtil.newIntBuffer(BUFSIZE); // Set Up A Selection Buffer
        int hits; // The Number Of Objects That We Selected
        gl.glSelectBuffer(BUFSIZE, selectBuffer); // Tell OpenGL To Use Our Array For Selection
        
        
        // The Size Of The Viewport. [0] Is <x>, [1] Is <y>, [2] Is <length>, [3] Is <width>
        int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);      
        //System.out.println("viewport= "+viewport[0]+","+viewport[1]+","+viewport[2]+","+viewport[3]);
        
        
        Dimension dim = canvas.getSize();
        //System.out.println("dimension= "+dim.width +","+dim.height);
        
        
        
        
        // Puts OpenGL In Selection Mode. Nothing Will Be Drawn.  Object ID's and Extents Are Stored In The Buffer.
        gl.glRenderMode(GL.GL_SELECT);
        gl.glInitNames(); // Initializes The Name Stack
        gl.glPushName(0); // Push 0 (At Least One Entry) Onto The Stack
        
        
        // This Creates A Matrix That Will Zoom Up To A Small Portion Of The Screen, Where The Mouse Is.

        
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
      
        
        /* create MOUSE_PICK_WIDTH x MOUSE_PICK_WIDTH pixel picking region near cursor location */
        //glu.gluPickMatrix((double) mouseX, (double) (470 - mouseY),  5.0, 5.0, viewport, 0);
        //mouseY+=30; //TODO understand this offset
        //glu.gluPickMatrix((double) mouseX, (double) (viewport[3] - mouseY), 5.0, 5.0, viewport, 0);
        glu.gluPickMatrix((double) mouseX, (double) (dim.height - mouseY), MOUSE_PICK_WIDTH, MOUSE_PICK_WIDTH, viewport, 0);
        gl.glOrtho(m_view3D.left,m_view3D.right,m_view3D.bottom,m_view3D.top,m_view3D.front,m_view3D.back);
    	gl.glMatrixMode(GL.GL_MODELVIEW);
        
		//drawing not hidden parts
    	Drawable3D[] drawHits = new Drawable3D[BUFSIZE];
    	//GeoElement3D[] geos = new GeoElement3D[BUFSIZE];
        int loop = 0;
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			loop++;
			gl.glLoadName(loop);
			d.drawForPicking(this);	
			//geos[loop] = (GeoElement3D) d.getGeoElement();
			drawHits[loop] = d;

		}

        hits = gl.glRenderMode(GL.GL_RENDER); // Switch To Render Mode, Find Out How Many
             
        //hits are stored
        m_view3D.getHits().init();
        
        int names, ptr = 0;
        float zMax, zMin;
        int num;
        for (int i = 0; i < hits; i++) { 
        	     
          names = selectBuffer.get(ptr);  
          ptr++; // min z    
          zMin = getDepth(ptr);
          ptr++; // max z
          zMax = getDepth(ptr);           
          
          ptr++;
          
          for (int j = 0; j < names; j++){ 
           	//view.hits.add(geos[buffer[ptr]]);
        	//geos[buffer[ptr]].zPick = z;
        	num = selectBuffer.get(ptr);
        	((Hits3D) m_view3D.getHits()).addDrawable3D(drawHits[num]);
        	drawHits[num].zPickMin = zMin;
        	drawHits[num].zPickMax = zMax;
        	ptr++;
          }          
        }
        
        
        ((Hits3D) m_view3D.getHits()).sort();
        
        waitForPick = false;
        m_view3D.waitForPick = true;
    }
    
    
    /** returns the depth between 0 and 2, in double format, from an integer offset 
     *  lowest is depth, nearest is the object
     *  
     *  @param ptr the integer offset
     * */
    private float getDepth(int ptr){
     	
    	float depth = (float) selectBuffer.get(ptr)/0x7fffffff;
    	if (depth<0)
    		depth+=2;
    	return depth;
    	
    	
    }
    
    
    
    
    
    
    
    
    
    
    //////////////////////////////////
    // initializations
    
    /** Called by the drawable immediately after the OpenGL context is
     * initialized for the first time. Can be used to perform one-time OpenGL
     * initialization such as setup of lights and display lists.
     * @param gLDrawable The GLAutoDrawable object.
     */
    public void init(GLAutoDrawable drawable) {
    	
        gl = drawable.getGL();
        
        
        //light
        float pos[] = { 1.0f, 1.0f, 1.0f, 0.0f };
        //float pos[] = { 0.0f, 0.0f, 1.0f, 0.0f };
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, pos, 0);        
        gl.glEnable(GL.GL_LIGHTING);     
        gl.glEnable(GL.GL_LIGHT0);
        
        //common enabling
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
        //gl.glPolygonOffset(1.0f, 2f);

        gl.glEnable(GL.GL_CULL_FACE);
        
        //blending
        gl.glEnable(GL.GL_BLEND);	
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);  
        
        //using glu quadrics
        quadric = glu.gluNewQuadric();// Create A Pointer To The Quadric Object (Return 0 If No Memory) (NEW)
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);          // Create Smooth Normals (NEW)
        glu.gluQuadricTexture(quadric, true);                    // Create Texture Coords (NEW)
        
        //projection type
        //viewOrtho(gl); 
        
        
        //normal anti-scaling
        gl.glEnable(GL.GL_NORMALIZE);
        //gl.glEnable(GL.GL_RESCALE_NORMAL);
         
    }

    
    
    
    //projection mode
    private void viewOrtho()                                        // Set Up An Ortho View
    {
    	//TODO change viewport when resized
    	//gl.glViewport(0,0,EuclidianGLDisplay.DEFAULT_WIDTH,EuclidianGLDisplay.DEFAULT_HEIGHT);
    	gl.glViewport(0,0,(int) (m_view3D.right-m_view3D.left),(int) (m_view3D.top-m_view3D.bottom));
    	
    	gl.glMatrixMode(GL.GL_PROJECTION);
    	gl.glLoadIdentity();

    	gl.glOrtho(m_view3D.left,m_view3D.right,m_view3D.bottom,m_view3D.top,m_view3D.front,m_view3D.back);
    	gl.glMatrixMode(GL.GL_MODELVIEW);
    	
    	
    }

    private void viewPerspective(GL gl)                                  // Set Up A Perspective View
    {
        gl.glMatrixMode(GL.GL_PROJECTION);                               // Select Projection
        gl.glPopMatrix();                                                // Pop The Matrix
        gl.glMatrixMode(GL.GL_MODELVIEW);                                // Select Modelview
        gl.glPopMatrix();                                                // Pop The Matrix
    }
    
    
    

}
