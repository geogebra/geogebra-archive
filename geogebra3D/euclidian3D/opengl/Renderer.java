package geogebra3D.euclidian3D.opengl;





import geogebra.Matrix.GgbMatrix;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.euclidian.EuclidianView;
import geogebra.main.Application;
import geogebra3D.euclidian3D.DrawList3D;
import geogebra3D.euclidian3D.Drawable3D;
import geogebra3D.euclidian3D.EuclidianController3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.euclidian3D.Hits3D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
import com.sun.opengl.util.j2d.TextRenderer;


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
public class Renderer implements GLEventListener {
	
	// openGL variables
	private GLU glu= new GLU();
	//private GLUT glut = new GLUT();
	private TextRenderer textRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 16));
	/** default text scale factor */
	private static final float DEFAULT_TEXT_SCALE_FACTOR = 0.8f;

	/** matrix changing Y-direction to Z-direction */
	//private double[] matrixYtoZ = {1,0,0,0, 0,0,1,0, 0,1,0,0, 0,0,0,1}; 
	
	/** canvas usable for a JPanel */
	public GLCanvas canvas;
	//public GLJPanel canvas;

	private GLCapabilities caps;
	private GL gl;
	private GLUquadric quadric;
	private FPSAnimator animator;
	
	/** for polygon tesselation */
	private GLUtessellator tobj;
	
	private IntBuffer selectBuffer;
	private int BUFSIZE = 512;
	private static int MOUSE_PICK_WIDTH = 3;
	
	Drawable3D[] drawHits;
	int pickingLoop;
	
	// other
	private DrawList3D drawList3D;
	
	private EuclidianView3D view3D;
	
	// for drawing
	private GgbMatrix4x4 m_drawingMatrix; //matrix for drawing
	
	
	///////////////////
	//primitives
	//private RendererPrimitives primitives;
	
	///////////////////
	//geometries
	private Manager geometryManager;
	
    ///////////////////
	// textures
	protected Textures textures;

	
	
	
	///////////////////
	//dash
	
    /** opengl organization of the dash textures */
    private int[] texturesDash;
    
    /** number of dash styles */
    private int DASH_NUMBER = 3;
    
	/** no dash. */
	static public int DASH_NONE = -1;
	
	/** simple dash: 1-(1), ... */
	static public int DASH_SIMPLE = 0;
	
	/** dotted dash: 1-(3), ... */
	static public int DASH_DOTTED = 1;
		
	/** dotted/dashed dash: 7-(4)-1-(4), ... */
	static public int DASH_DOTTED_DASHED = 2;
	
	/** description of the dash styles */
	static private boolean[][] DASH_DESCRIPTION = {
		{true, false}, // DASH_SIMPLE
		{true, false, false, false}, // DASH_DOTTED
		{true,true,true,true, true,true,true,false, false,false,false,true, false,false,false,false} // DASH_DOTTED_DASHED
	};
	
	
	/** # of the dash */
	private int dash = DASH_NONE; 
	
	/** scale factor for dash */
	private float dashScale = 1f;
	
	
	/////////////////////
	// spencil attributes
	
	/** current drawing color {r,g,b} */
	private Color color; 
	/** current alpha blending */
	private double alpha;
	/** current text color {r,g,b} */
	private Color textColor; 
	
	private double thickness;
	
	
	
	///////////////////
	// arrows
	
	/** no arrows */
	static final public int ARROW_TYPE_NONE=0;
	/** simple arrows */
	static final public int ARROW_TYPE_SIMPLE=1;
	private int m_arrowType=ARROW_TYPE_NONE;
	
	private double m_arrowLength, m_arrowWidth;
	
	
	///////////////////
	// dilation
	
	private static final int DILATION_NONE = 0;
	private static final int DILATION_HIGHLITED = 1;
	private int dilation = DILATION_NONE;
	private double[] dilationValues = {
			1,  // DILATION_NONE
			1.3 // DILATION_HIGHLITED
	};
	
	
	///////////////////
	// for picking
	
	private int mouseX, mouseY;
	private boolean waitForPick = false;
	private boolean doPick = false;
	public static final int PICKING_MODE_OBJECTS = 0;
	public static final int PICKING_MODE_LABELS = 1;
	private int pickingMode = PICKING_MODE_OBJECTS;
	
	/**
	 * creates a renderer linked to an {@link EuclidianView3D} 
	 * @param view the {@link EuclidianView3D} linked to 
	 */
	public Renderer(EuclidianView3D view){
		super();
		
	    caps = new GLCapabilities();
	    
	    //anti-aliasing
    	caps.setSampleBuffers(true);caps.setNumSamples(4);    	
    	//caps.setSampleBuffers(false);
       
        //avoid flickering
    	caps.setDoubleBuffered(true);	    
    	
    	//canvas
	    canvas = new GLCanvas(caps);
	    //canvas = new GLJPanel(caps);

	    
        
        
	    canvas.addGLEventListener(this);
	    
	    //animator : 60 frames per second
	    
	    
	    animator = new FPSAnimator( canvas, 60 );
        animator.setRunAsFastAsPossible(true);	  
        //animator.setRunAsFastAsPossible(false);	  
        animator.start();
        

        //link to 3D view
		this.view3D=view;
		
		
		
	}
	
	
	/**
	 * set the list of {@link Drawable3D} to be drawn
	 * @param dl list of {@link Drawable3D}
	 */
	public void setDrawList3D(DrawList3D dl){
		drawList3D = dl;
	}
	
	
	
	/**
	 * re-calc the display immediately
	 */
	public void display(){
	
		canvas.display();
	}
	
	
	
	/** sets if openGL culling is done or not
	 * @param flag
	 */
	public void setCulling(boolean flag){
		if (flag)
			gl.glEnable(GL.GL_CULL_FACE);
		else
			gl.glDisable(GL.GL_CULL_FACE);
	}
	
	
	private void drawTransp(){
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		getTextures().setTexture(Textures.FADING);
		
		gl.glDisable(GL.GL_CULL_FACE);
		drawList3D.drawTransp(this);
		//drawList3D.drawTranspClosed(this);

		
		//TODO improve this !
		
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_FRONT); drawList3D.drawTranspClosed(this);//draws inside parts  
		gl.glCullFace(GL.GL_BACK); drawList3D.drawTranspClosed(this);//draws outside parts 
		
		
		
		
		gl.glDisable(GL.GL_TEXTURE_2D);
		
		
	}
	
	
	
	private void drawNotTransp(){
		
		getTextures().setTexture(Textures.FADING);

        gl.glEnable(GL.GL_BLEND);
		
		gl.glDisable(GL.GL_CULL_FACE);
        drawList3D.drawNotTransparentSurfaces(this);

		
		//TODO improve this !
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_FRONT); drawList3D.drawNotTransparentSurfacesClosed(this);//draws inside parts  
		gl.glCullFace(GL.GL_BACK); drawList3D.drawNotTransparentSurfacesClosed(this);//draws outside parts 
		
		
		
		
		
	}
	
	/**
	 * 
	 * openGL method called when the display is to be computed.
	 * <p>
	 * First, it calls {@link #doPick()} if a picking is to be done.
	 * Then, for each {@link Drawable3D}, it calls:
	 * <ul>
	 * <li> {@link Drawable3D#drawHidden(EuclidianRenderer3D)} to draw hidden parts (dashed segments, lines, ...) </li>
	 * <li> {@link Drawable3D#drawTransp(EuclidianRenderer3D)} to draw transparent objects (planes, spheres, ...) </li>
	 * <li> {@link Drawable3D#drawSurfacesForHiding(EuclidianRenderer3D)} to draw in the z-buffer objects that hides others (planes, spheres, ...) </li>
	 * <li> {@link Drawable3D#drawTransp(EuclidianRenderer3D)} to re-draw transparent objects for a better alpha-blending </li>
	 * <li> {@link Drawable3D#draw(EuclidianRenderer3D)} to draw not hidden parts (dash-less segments, lines, ...) </li>
	 * </ul>
	 */
    public void display(GLAutoDrawable gLDrawable) {
    	
    	//Application.debug("display");

    	//double displayTime = System.currentTimeMillis();
        
        gl = gLDrawable.getGL();
        
        
        
        //picking        
        if(waitForPick){
        	doPick();
        	//Application.debug("doPick");
        	//return;
        }
        
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        
        

  

        
        
        //start drawing
        viewOrtho();
        
        
        //update 3D controller
        ((EuclidianController3D) view3D.getEuclidianController()).processMouseMoved();
        
        // update 3D view
        geometryManager.update();
        view3D.update();
        view3D.updateDrawablesNow();

        // update 3D drawables
        drawList3D.updateAll();

    	
    	
        
        //init drawing matrix to view3D toScreen matrix
        gl.glLoadMatrixd(view3D.getToScreenMatrix().get(),0);
        
        
 


        //drawing the cursor
        view3D.drawCursor(this);
        
         
        
        
        
        //primitives.enableVBO(gl);
        
        //drawing hidden part
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glEnable(GL.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts     
        //gl.glDisable(GL.GL_BLEND);
        drawList3D.drawHiddenNotTextured(this);
        gl.glEnable(GL.GL_TEXTURE_2D);
        drawList3D.drawHiddenTextured(this);
        /*
        gl.glDisable(GL.GL_CULL_FACE);
        gl.glEnable(GL.GL_BLEND);
        getTextures().setTexture(Textures.FADING);
        drawList3D.drawNotTransparentSurfaces(this);
        */
        drawNotTransp();
        //gl.glEnable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_TEXTURE_2D);
        gl.glDisable(GL.GL_ALPHA_TEST);       


        
        
         
       

        
        //gl.glEnable(GL.GL_BLEND);
        //gl.glDisable(GL.GL_CULL_FACE);
        
        
        //drawing transparents parts
        gl.glDepthMask(false);
        drawTransp();      
        gl.glDepthMask(true);

        
        
        //drawing labels
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts     
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_BLEND);
        drawList3D.drawLabel(this);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_ALPHA_TEST);       

        
        
        //drawing hiding parts
        gl.glColorMask(false,false,false,false); //no writing in color buffer		
        gl.glCullFace(GL.GL_FRONT); //draws inside parts    
        drawList3D.drawClosedSurfacesForHiding(this); //closed surfaces back-faces
        gl.glDisable(GL.GL_CULL_FACE);
        drawList3D.drawSurfacesForHiding(this); //non closed surfaces
        gl.glColorMask(true,true,true,true);


        
 

        //re-drawing transparents parts for better transparent effect
        //TODO improve it !
        gl.glDepthMask(false);
        gl.glEnable(GL.GL_BLEND);
        drawTransp();             
        gl.glDepthMask(true);

        
        //drawing hiding parts
        gl.glColorMask(false,false,false,false); //no writing in color buffer		
        gl.glDisable(GL.GL_BLEND);
        gl.glCullFace(GL.GL_BACK); //draws inside parts
        gl.glEnable(GL.GL_CULL_FACE);
        drawList3D.drawClosedSurfacesForHiding(this); //closed surfaces front-faces
        gl.glColorMask(true,true,true,true);
        
        
        
        
        //re-drawing transparents parts for better transparent effect
        //TODO improve it !
        gl.glDepthMask(false);
        gl.glEnable(GL.GL_BLEND);
        drawTransp();           
        gl.glDepthMask(true);

        

       
        //drawing not hidden parts
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glDisable(GL.GL_BLEND);
        drawList3D.draw(this);
  
        
        
        //primitives.disableVBO(gl);
     
     
        

        
        
        
        
        //FPS
        /*
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_DEPTH_TEST);

    	drawFPS();
    	gl.glEnable(GL.GL_DEPTH_TEST);
    	gl.glEnable(GL.GL_LIGHTING);
        */

    	
    	
        gLDrawable.swapBuffers(); //TODO
        
        /*
        gl.glReadBuffer(GL.GL_FRONT);
        FloatBuffer buffer = FloatBuffer.allocate(3);
        gl.glReadPixels(mouseX, (top-bottom) -mouseY, 1, 1, GL.GL_RGB, GL.GL_FLOAT, buffer);
        float[] pixels = new float[3];
        pixels = buffer.array();
        //System.out.println("mouse ("+mouseX+","+((top-bottom)-mouseY)+"):"+pixels[0]+" "+pixels[1]+" "+pixels[2]);
        //Application.debug("display : "+((int) (System.currentTimeMillis()-displayTime)));
        */
        
        if (needExportImage){
        	setExportImage();
        	needExportImage=false;
        	//notify();
        }
    }    
    
    boolean needExportImage=false;
    
    public void needExportImage(){
    	needExportImage = true;
    	display();
    	
    	/*
    	try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    }
    
    BufferedImage bi;
    
    private void setExportImage(){
    	
        gl.glReadBuffer(GL.GL_FRONT);
        int width = right-left;
        int height = top-bottom;
        FloatBuffer buffer = FloatBuffer.allocate(3*width*height);
        gl.glReadPixels(0, 0, width, height, GL.GL_RGB, GL.GL_FLOAT, buffer);
        float[] pixels = buffer.array();
        
        bi = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
   	
        int i =0;
        for (int y=height-1; y>=0 ; y--)
        	for (int x =0 ; x<width ; x++){
        		int r = (int) (pixels[i]*255);
        		int g = (int) (pixels[i+1]*255);
        		int b = (int) (pixels[i+2]*255);
        		bi.setRGB(x, y, ( (r << 16) | (g << 8) | b));
        		i+=3;
        	}
        bi.flush();
    }

    public BufferedImage getExportImage(){
    	
    	
    	return bi;
    }
    
    
    
    
    
    
    
    
    /**
     * openGL method called when the canvas is reshaped.
     */
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
    {
      GL gl = drawable.getGL();
      
      //Application.debug("reshape\n x = "+x+"\n y = "+y+"\n w = "+w+"\n h = "+h);
      

      viewOrtho(x,y,w,h);

      
      view3D.setWaitForUpdate();
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
    
    
    
    /** sets the color of the text
     * @param c color of the text
     */
    public void setTextColor(Color c){
    	
    	textColor = c;
    	
	
    }
    
    
    /**
     * sets the material used by the pencil
     * 
     * @param c the color of the pencil
     * @param alpha the alpha value for blending
     */
    public void setColor(Color c, double alpha){

    	
    	color = c;
    	this.alpha = alpha;
 
    	gl.glColor4f(((float) c.getRed())/256f,
    							((float) c.getGreen())/256f,
    							((float) c.getBlue())/256f,
    							(float) alpha);
    	
    	
    	
    }
    
    
    /** return (r,g,b) current color
     * @return (r,g,b) current color
     */
    public Color getColor(){
    	return color;
    }
    
    /** return current alpha
     * @return current alpha
     */
    public double getAlpha(){
    	return alpha;
    }
    
    

    
    /**
     * sets the thickness used by the pencil.
     * 
     * @param a_thickness the thickness
     */
    public void setThickness(double a_thickness){
    	this.thickness = a_thickness;
    }
    
    
    /**
     * gets the current thickness of the pencil.
     * 
     * @return the thickness
     */
    public double getThickness(){
    	return thickness;
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
    
    
    
    
    //layer
    /**
     * sets the layer to l. Use gl.glPolygonOffset( ).
     * @param l the layer
     */
    public void setLayer(float l){
    	
    	// 0<=l<10
    	// l2-l1>=1 to see something
    	gl.glPolygonOffset(-l*0.05f, -l*10);
    }
    
    
    
    
    
    
    //drawing matrix
    
    /**
     * sets the matrix in which coord sys the pencil draws.
     * 
     * @param a_matrix the matrix
     */
    public void setMatrix(GgbMatrix4x4 a_matrix){
    	m_drawingMatrix=a_matrix;
    }
    
    
    /**
     * gets the matrix describing the coord sys used by the pencil.
     * 
     * @return the matrix
     */
    public GgbMatrix4x4 getMatrix(){
    	return m_drawingMatrix;
    }
    
    
    /**
     * sets the drawing matrix to openGL.
     * same as initMatrix(m_drawingMatrix)
     */
    public void initMatrix(){
    	initMatrix(m_drawingMatrix);
    }
    
    /**
     * sets a_drawingMatrix to openGL.
     * @param a_drawingMatrix the matrix
     */
    private void initMatrix(GgbMatrix a_drawingMatrix){
    	initMatrix(a_drawingMatrix.get());
    }   
    
    
    /**
     * sets a_drawingMatrix to openGL.
     * @param a_drawingMatrix the matrix
     */
    private void initMatrix(double[] a_drawingMatrix){
    	gl.glPushMatrix();
		gl.glMultMatrixd(a_drawingMatrix,0);
    }     
    

    
    /**
     * turn off the last drawing matrix set in openGL.
     */
    public void resetMatrix(){
    	gl.glPopMatrix();
    }
    
    
    
    ////////////////////////////////////////////
    //
    // TEXTURES
    //
    ////////////////////////////////////////////
    

    /*
    private void initTextures(){
    	
    	gl.glEnable(GL.GL_TEXTURE_2D);
    	
    	
    	
    	// dash textures
    	texturesDash = new int[DASH_NUMBER];
        gl.glGenTextures(DASH_NUMBER, texturesDash, 0);
        for(int i=0; i<DASH_NUMBER; i++)
        	initDashTexture(texturesDash[i],DASH_DESCRIPTION[i]);
         
        
        
        gl.glDisable(GL.GL_TEXTURE_2D);
    }
    
    
    // dash
    
    private void initDashTexture(int n, boolean[] description){
    	
        //int sizeX = 1; 
        //int sizeY = description.length;
        int sizeX = description.length; 
        int sizeY = 1;
        
        byte[] bytes = new byte[4*sizeX*sizeY];
        
        // if description[i]==true, then texture is white opaque, else is transparent
        for (int i=0; i<sizeX; i++)
        	if (description[i])      		
        		bytes[4*i+0]=
        			bytes[4*i+1]= 
        				bytes[4*i+2]= 
        					bytes[4*i+3]= (byte) 255;
              
        ByteBuffer buf = ByteBuffer.wrap(bytes);

        gl.glBindTexture(GL.GL_TEXTURE_2D, n);
        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER,GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER,GL.GL_NEAREST);
        
        
        //TODO use gl.glTexImage1D
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,  4, sizeX, sizeY, 0, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf);
        
    }
    */

    
    /**
     * sets the dash used by the pencil.
     * 
     * @param dash # of the dash, see EuclidianView, ...
     */
    public void setDash(int dash){
    	
    	
    	switch (dash) {
		case EuclidianView.LINE_TYPE_DOTTED:
			this.dash=DASH_DOTTED;
			dashScale = 0.08f;
			break;

		case EuclidianView.LINE_TYPE_DASHED_SHORT:
			this.dash=DASH_SIMPLE;
			dashScale = 0.08f;
			break;

		case EuclidianView.LINE_TYPE_DASHED_LONG:
			this.dash=DASH_SIMPLE;
			dashScale = 0.04f;
			break;

		case EuclidianView.LINE_TYPE_DASHED_DOTTED:
			this.dash=DASH_DOTTED_DASHED;
			dashScale = 0.04f;
			break;

		default: // EuclidianView.LINE_TYPE_FULL
			this.dash = DASH_NONE;
		}
    	
    }
    
    
    
    
    ///////////////////////////////////////////////////////////
    //drawing geometries
    
    
    public Manager getGeometryManager(){
    	return geometryManager;
    }
    
    
	/////////////////////////////////////////////
	// TEXTURES METHODS
	/////////////////////////////////////////////

	
	/**
	 * @return textures manager
	 */
	public Textures getTextures(){
		return textures;
	}
    
    
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
    		drawSegment(a_x1, a_x2, dash!=DASH_NONE);
    	break;
    	case ARROW_TYPE_SIMPLE:
    		double x3=a_x2-m_arrowLength/(m_drawingMatrix.getUnit(GgbMatrix4x4.X_AXIS)*view3D.getScale());
    		double thickness = getThickness();
    		setThickness(m_arrowWidth);
    		drawCone(x3,a_x2);
    		setThickness(thickness);
    		if (x3>a_x1)
    			drawSegment(a_x1, x3, dash!=DASH_NONE);
    		break;
    	}

    } 
    
    
    
    private void drawSegment(double a_x1, double a_x2, boolean dashed){


    	initMatrix(m_drawingMatrix.segmentX(a_x1, a_x2));
    	
    	/*
    	if (dashed){
    		gl.glBindTexture(GL.GL_TEXTURE_2D, texturesDash[dash]);


    		gl.glMatrixMode(GL.GL_TEXTURE);
    		gl.glLoadIdentity();
    		float b = (float) (dashScale*(a_x2-a_x1)*m_drawingMatrix.getUnit(GgbMatrix4x4.X_AXIS)*view3D.getScale());
    		float a = 0.75f/b-0.5f;
    		
    		gl.glScalef(b,1f,1f);
    		gl.glTranslatef(a,0f,0f);
    		
    		gl.glMatrixMode(GL.GL_MODELVIEW);
    	}
    	*/
    	
    	double s = thickness*dilationValues[dilation]/view3D.getScale();
    	gl.glScaled(1,s,s);
    	
        //	primitives.segment(gl, (int) thickness);
       	geometryManager.cylinder.draw();
    	
    	
    	resetMatrix();

    }
   
    
    /** 
     * draws a segment from x=0 to x=1 according to current drawing matrix.
     */
    public void drawSegment(){
    	drawSegment(0,1);
    }
    
    
    
    
    
    
    /**
     * draws "coordinates segments" from the point origin of the drawing matrix to the axes
     * @param axisX color of the x axis
     * @param axisY color of the y axis
     * @param axisZ color of the z axis
     */
    public void drawCoordSegments(Color axisX, Color axisY, Color axisZ){
    	
    	GgbMatrix4x4 drawingMatrixOld = m_drawingMatrix;
    	Color colorOld = getColor();
    	double alphaOld = getAlpha();
 

    	GgbMatrix4x4 matrix = new GgbMatrix4x4();
    	matrix.setOrigin(m_drawingMatrix.getOrigin());
    	matrix.set(3,4,0); //sets the origin's altitude to 0
    	
    	// z-segment
    	double altitude = m_drawingMatrix.getOrigin().get(3);//altitude du point
 
    	matrix.setVx((GgbVector) EuclidianView3D.vz.mul(altitude));
    	
    	if(altitude>0){
    		matrix.setVy(EuclidianView3D.vx);
    		matrix.setVz(EuclidianView3D.vy);
    	}else{
    		matrix.setVy(EuclidianView3D.vy);
    		matrix.setVz(EuclidianView3D.vx);
    	}
    	
    	setColor(axisZ, 1);
    	setMatrix(matrix);
    	drawSegment();
    	resetMatrix();
    	
    	
    	
    	
    	// x-segment  	
    	double x = m_drawingMatrix.getOrigin().get(1);//x-coord of the point
    	
    	matrix.setVx((GgbVector) EuclidianView3D.vx.mul(-x));
    	
    	if(x>0){
    		matrix.setVy(EuclidianView3D.vz);
    		matrix.setVz(EuclidianView3D.vy);
    	}else{
    		matrix.setVy(EuclidianView3D.vy);
    		matrix.setVz(EuclidianView3D.vz);
    	}
    	
    	setColor(axisX, 1);
    	setMatrix(matrix);
    	drawSegment();
    	resetMatrix();
    	
    	
    	// y-segment  	
    	double y = m_drawingMatrix.getOrigin().get(2);//y-coord of the point
    	
    	matrix.setVx((GgbVector) EuclidianView3D.vy.mul(-y));
    	
    	if(y>0){
    		matrix.setVy(EuclidianView3D.vx);
    		matrix.setVz(EuclidianView3D.vz);
    	}else{
    		matrix.setVy(EuclidianView3D.vz);
    		matrix.setVz(EuclidianView3D.vx);
    	}
    	
    	setColor(axisY, 1);
    	setMatrix(matrix);
    	drawSegment();
    	resetMatrix();
    	
    	
    	
    	
    	// reset the drawing matrix and color
    	setMatrix(drawingMatrixOld);
    	setColor(colorOld, alphaOld);
    	
    }
    
    
    
    
    /** 
     * draws a ray (half-line) according drawing matrix.
     */
    /*
    public void drawRay(){
    	//TODO use frustum
    	drawSegment(0,21);
    }  
    */
    
    
    
    
    
    
    
    /** draws a cone from x=x1 to x=x2 according to current drawing matrix.
     * @param a_x1 x-coordinate of the basis
     * @param a_x2 x-coordinate of the top
     */
    public void drawCone(double a_x1, double a_x2){
    	
    	
    	initMatrix(m_drawingMatrix.segmentX(a_x1, a_x2));
		double s = thickness*dilationValues[dilation]/view3D.getScale();
		gl.glScaled(1,s,s);
		geometryManager.cone.draw();
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
    
    
    /**
     * draws a plane
     */
    public void drawPlane(){
    	initMatrix();
    	geometryManager.plane.draw();
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
    	
    	GgbMatrix4x4 matrix = new GgbMatrix4x4();
     	
    	matrix.set(getMatrix());
    	for (int i=nYmin; i<=nYmax; i++){
    		setMatrix(matrix.translateY(i*a_dy));
        	drawSegment(xmin, xmax);
    	}
    	setMatrix(matrix);
    	
    	matrix.set(getMatrix());
    	GgbMatrix4x4 matrix2 = matrix.mirrorXY();
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
    	double s = radius/view3D.getScale();
    	gl.glScaled(s,s,s);
    	//primitives.point(gl,size);
    	geometryManager.point.draw();
    	//primitives.drawSphere(gl,radius/view3D.getScale(),16,16);
    	resetMatrix();
    }
    
    /**
     * draws a point according to current drawing matrix.
     * 
     * @param radius radius of the point
     */
    public void drawPoint(int size){
    	initMatrix();
    	double s = size*dilationValues[dilation]/view3D.getScale();
    	gl.glScaled(s,s,s);
    	geometryManager.point.draw();
    	resetMatrix();
    	
    	
    }
    

 
    
    
    
    /** draws a 2D cross cursor 
     */    
    public void drawCursorCross2D(){
    	
    	gl.glDisable(GL.GL_LIGHTING);
    	initMatrix();
    	geometryManager.cursor.draw();
		resetMatrix();
    	gl.glEnable(GL.GL_LIGHTING);
   	
    }
    
    /** draws a 3D cross cursor
     */    
    public void drawCursorCross3D(){
    	
    	gl.glDisable(GL.GL_LIGHTING);
    	initMatrix();
    	//gl.glScalef(10f, 10f, 10f);
    	geometryManager.cursor.draw(GeometryCursor.TYPE_CROSS3D);
		resetMatrix();
    	gl.glEnable(GL.GL_LIGHTING);
   	
    } 
    
    /** draws a cylinder cursor 
     */    
    public void drawCursorCylinder(){
 
    	gl.glDisable(GL.GL_LIGHTING);  
     	initMatrix();
    	geometryManager.cursor.draw(GeometryCursor.TYPE_CYLINDER);
    	resetMatrix();
    	gl.glEnable(GL.GL_LIGHTING);

    	
    }
    
    
    

    
    /** draws a diamond cursor  
     *
     */    
    public void drawCursorDiamond(){
 
    	gl.glDisable(GL.GL_LIGHTING);  
    	initMatrix();
    	geometryManager.cursor.draw(GeometryCursor.TYPE_DIAMOND);
    	resetMatrix();  	
    	gl.glEnable(GL.GL_LIGHTING);

    	
    }
    
   
    
   
    
    
    /**
     * set the tesselator to start drawing a new polygon
     * @param cullFace says if the faces have to be culled
     */
    public int startPolygon(float nx, float ny, float nz){
    	
    	return geometryManager.startPolygon(nx,ny,nz);
 
    }
    
    
    /** add the (x,y) point as a new vertex for the current polygon
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void addToPolygon(double x, double y){
    	addToPolygon(x, y, 0);
    }
    
    //TODO remove this
    /** add the (x,y,z) point as a new vertex for the current polygon
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     */
    public void addToPolygon(double x, double y, double z){
    	geometryManager.addVertexToPolygon(x, y, z);
    }    
    
    

    
    /**
     * end of the current polygon
     * @param cullFace says if the faces have been culled
     */
    public void endPolygon(){
    	
    	geometryManager.endPolygon();
        
    }
    
    
    public void removePolygon(int index){
    	if (geometryManager!=null)
    		geometryManager.remove(index);
    }
   
    
    
    public void drawPolygon(int index){
    	geometryManager.draw(index);
    }
    
    /**
     * draw a circle with center (x,y) and radius R
     * @param x x coord of the center
     * @param y y coord of the center
     * @param R radius
     */
    public void drawCircle(double x, double y, double R){

    	initMatrix();
    	drawCircleArcDashedOrNot((float) x, (float) y, (float) R, 0, 2f * (float) Math.PI, dash!=0);
    	resetMatrix();
    }
    
    
    
    /**
     * draw a circle with center (x,y) and radius R
     * @param x x coord of the center
     * @param y y coord of the center
     * @param R radius
     * @param startAngle starting angle for the arc
     * @param endAngle ending angle for the arc
     * @param dash says if the circle is dashed
     */
    private void drawCircleArcDashedOrNot(float x, float y, float R, float startAngle, float endAngle, boolean dash){
    	
    	if (!dash)
    		drawCircleArcNotDashed(x, y, R, startAngle, endAngle);
    	else
    		drawCircleArcNotDashed(x,y,R, startAngle, endAngle);
    }
    
    /**
     * draw a dashed circle with center (x,y) and radius R
     * @param x x coord of the center
     * @param y y coord of the center
     * @param R radius
     * @param startAngle starting angle for the arc
     * @param endAngle ending angle for the arc
     */
    
    /*
    private void drawCircleArcDashed(float x, float y, float R, float startAngle, float endAngle){
    	
    	m_dash_factor = 1/(R*m_drawingMatrix.getUnit(Ggb3DMatrix4x4.X_AXIS));
    	for(double l1=startAngle; l1<endAngle;){
    		double l2=l1;
    		for(int i=0; (i<m_dash.length)&&(l1<endAngle); i++){
    			l2=l1+m_dash_factor*m_dash[i][0];
    			if (l2>endAngle) l2=endAngle;
    			//Application.debug("l1,l2="+l1+","+l2);
    			drawCircleArcNotDashed(x,y,R,(float) l1, (float) l2);
    			l1=l2+m_dash_factor*m_dash[i][1];
    		}	
    	} 	
    }  
    */ 
    
    /**
     * draw a dashed circle with center (x,y) and radius R
     * @param x x coord of the center
     * @param y y coord of the center
     * @param R radius
     * @param startAngle starting angle for the arc
     * @param endAngle ending angle for the arc
     */
    private void drawCircleArcNotDashed(float x, float y, float R, float startAngle, float endAngle){
    	int nsides = 16; //TODO use thickness

    	int rings = (int) (60*(endAngle-startAngle)) +2;
    	drawTorusArc(x, y, R, startAngle, endAngle, nsides, rings);
    }

    
    
    
    
    
    

    
    
    /**
     * draw a torus arc (using getThickness() for thickness)
     * @param x x coord of the center of the torus
     * @param y y coord of the center of the torus
     * @param R radius of the torus
     * @param startAngle starting angle for the arc
     * @param endAngle ending angle for the arc
     * @param nsides number of sides in a ring
     * @param rings number of rings
     */
    private void drawTorusArc(float x, float y, float R, float startAngle, float endAngle, int nsides, int rings) {
    	
    	float r = (float) getThickness()/100;
    	
        float ringDelta = (endAngle-startAngle) / rings;
        float sideDelta = 2.0f * (float) Math.PI / nsides;
        float theta = startAngle; 
        float cosTheta = (float) Math.cos(theta); 
        float sinTheta = (float) Math.sin(theta);
        for (int i = rings - 1; i >= 0; i--) {
          float theta1 = theta + ringDelta;
          float cosTheta1 = (float) Math.cos(theta1);
          float sinTheta1 = (float) Math.sin(theta1);
          gl.glBegin(GL.GL_QUAD_STRIP);
          float phi = 0.0f;
          for (int j = nsides; j >= 0; j--) {
            phi += sideDelta;
            float cosPhi = (float) Math.cos(phi);
            float sinPhi = (float) Math.sin(phi);
            float dist = R + r * cosPhi;
            gl.glNormal3f(cosTheta1 * cosPhi, sinTheta1 * cosPhi, -sinPhi);
            gl.glVertex3f(x+cosTheta1 * dist, y+sinTheta1 * dist, r * -sinPhi);
            gl.glNormal3f(cosTheta * cosPhi, sinTheta * cosPhi, -sinPhi);
            gl.glVertex3f(x+cosTheta * dist, y+sinTheta * dist, r * -sinPhi);
          }
          gl.glEnd();
          theta = theta1;
          cosTheta = cosTheta1;
          sinTheta = sinTheta1;
        }
      }

   
    ///////////////////////////////////////////////////////////
    //drawing primitives TODO use VBO
    
    
    private void drawCylinder(double radius, int latitude){
     	
    	gl.glScaled(1, radius, radius);

    	float dt = (float) 1/latitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	gl.glBegin(GL.GL_QUADS); 
    	
    	for( int i = 0; i < latitude + 1 ; i++ ) { 
    		float y0 = (float) Math.sin ( i * da ); 
    		float z0 = (float) Math.cos ( i * da ); 
    		float y1 = (float) Math.sin ( (i+1) * da ); 
    		float z1 = (float) Math.cos ( (i+1) * da ); 

    		gl.glTexCoord2f(0,i*dt);
    		gl.glNormal3f(0,y0,z0); 
    		gl.glVertex3f(0,y0,z0); 


    		gl.glTexCoord2f(1,i*dt);
    		gl.glNormal3f(1,y0,z0); 
    		gl.glVertex3f(1,y0,z0); 

    		gl.glTexCoord2f(1,(i+1)*dt);
    		gl.glNormal3f(1,y1,z1); 
    		gl.glVertex3f(1,y1,z1); 

    		gl.glTexCoord2f(0,(i+1)*dt);
    		gl.glNormal3f(0,y1,z1); 
    		gl.glVertex3f(0,y1,z1); 

    		
    	} 
    	gl.glEnd();  
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
    
    
    
    
    
    /** draws the text s
     * @param x x-coord
     * @param y y-coord
     * @param s text
     * @param colored says if the text has to be colored
     */
    public void drawText(float x, float y, String s, boolean colored){
    	
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();
        
    	gl.glMatrixMode(GL.GL_MODELVIEW);
    	
    	
    	initMatrix();
    	initMatrix(view3D.getUndoRotationMatrix());
    	

    	textRenderer.begin3DRendering();

    	if (colored)
    		textRenderer.setColor(textColor);
    	
     
        float textScaleFactor = DEFAULT_TEXT_SCALE_FACTOR/((float) view3D.getScale());
    	
    	
        if (x<0)
        	x=x-(s.length()-0.5f)*8; //TODO adapt to police size
        
    	textRenderer.draw3D(s,
                x*textScaleFactor,//w / -2.0f * textScaleFactor,
                y*textScaleFactor,//h / -2.0f * textScaleFactor,
                0,
                textScaleFactor);
    	
        textRenderer.end3DRendering();
     
 
    	
        resetMatrix(); //initMatrix(m_view3D.getUndoRotationMatrix());
    	resetMatrix(); //initMatrix();
    	
    }
    
    
    
    
    
    
    
    
    /////////////////////////
    // FPS
    
	double displayTime = 0;
	int nbFrame = 0;
	double fps = 0;
    
    private void drawFPS(){
    	
    	if (displayTime==0)
    		displayTime = System.currentTimeMillis();
    	
    	nbFrame++;
    	
    	double newDisplayTime = System.currentTimeMillis();
    	
    	//Application.debug("delay = "+ (newDisplayTime-displayTime));
    	
    	//displayTime = System.currentTimeMillis();
    	if (newDisplayTime > displayTime+1000){
    		
    		//Application.debug("nbFrame = "+nbFrame);
    		/*
    	fps = 0.9*fps + 0.1*1000/(displayTime-displayTimeOld);
    	if (fps>100)
    		fps=100;
    		 */

    		fps = 1000*nbFrame/(newDisplayTime - displayTime);
    		displayTime = newDisplayTime;
    		nbFrame = 0;
    	}
    	
    	
    	
    	
        gl.glMatrixMode(GL.GL_TEXTURE);
        gl.glLoadIdentity();
        
    	gl.glMatrixMode(GL.GL_MODELVIEW);
    	
    	gl.glPushMatrix();
    	gl.glLoadIdentity();
    	
    	
    	textRenderer.begin3DRendering();

    	
    	textRenderer.setColor(Color.BLACK);
    	

    	
        
    	textRenderer.draw3D("FPS="+ ((int) fps),left,bottom,0,1);
    	
        textRenderer.end3DRendering();
        
        gl.glPopMatrix();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //////////////////////////////////////
    // picking
    
    /**
     * sets the mouse locations to (x,y) and asks for picking.
     * 
     * @param x x-coordinate of the mouse
     * @param y y-coordinate of the mouse
     */
    public void setMouseLoc(int x, int y, int pickingMode){
    	mouseX = x;
    	mouseY = y;
    	
    	this.pickingMode = pickingMode;
    	
    	// on next rending, a picking will be done : see doPick()
    	waitForPick = true;
    	
    	//thread = new Thread(picking);
       
    	//thread.setPriority(Thread.MIN_PRIORITY);
    	
    	//thread.start();
        //return thread;
    	
    }
    

    
    /**
     * does the picking to sets which objects are under the mouse coordinates.
     */
    public void doPick(){
    	
    	//double pickTime = System.currentTimeMillis();

    	BUFSIZE = (drawList3D.size()+EuclidianView3D.DRAWABLES_NB)*2+1;
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
        gl.glOrtho(left,right,bottom,top,front,back);
    	gl.glMatrixMode(GL.GL_MODELVIEW);
    	
        
    	

    	gl.glDisable(GL.GL_ALPHA_TEST);
    	gl.glDisable(GL.GL_BLEND);
    	gl.glDisable(GL.GL_LIGHTING);
       	gl.glDisable(GL.GL_TEXTURE);
       	//gl.glDisable(GL.GL_DEPTH_TEST);
       	//gl.glColorMask(false,false,false,false);
       	//gl.glDisable(GL.GL_NORMALIZE);
    	
    	drawHits = new Drawable3D[BUFSIZE];
  
    	//primitives.enableVBO(gl);
    	
        // picking objects
        pickingLoop = 0;
        drawList3D.drawForPicking(this);
        int labelLoop = pickingLoop;
        
        if (pickingMode == PICKING_MODE_LABELS){
        	// picking labels
        	drawList3D.drawLabelForPicking(this);
        }

        //primitives.disableVBO(gl);
        
        hits = gl.glRenderMode(GL.GL_RENDER); // Switch To Render Mode, Find Out How Many
             
        //hits are stored
        Hits3D hits3D = new Hits3D();
        hits3D.init();
        //view3D.getHits().init();
        
        //String s="doPick (labelLoop = "+labelLoop+")";
        
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
        	num = selectBuffer.get(ptr);
        	//((Hits3D) view3D.getHits()).addDrawable3D(drawHits[num],num>labelLoop);
        	hits3D.addDrawable3D(drawHits[num],num>labelLoop);
        	//s+="\n("+num+") "+drawHits[num].getGeoElement().getLabel();
        	drawHits[num].zPickMin = zMin;
        	drawHits[num].zPickMax = zMax;
        	ptr++;
          }
          
          
        }
        
        //Application.debug(s);
        
        // sets the GeoElements in view3D
        //((Hits3D) view3D.getHits()).sort();
        hits3D.sort();
        view3D.setHits(hits3D);
        //Application.debug(hits3D.toString());
       
        waitForPick = false;
        
        
        //Application.debug("pick : "+((int) (System.currentTimeMillis()-pickTime)));
        
        gl.glEnable(GL.GL_LIGHTING);
    }
    
    
    public void glLoadName(int loop){
    	gl.glLoadName(loop);
    }
    
    public void pick(Drawable3D d){
    	pickingLoop++;
    	gl.glLoadName(pickingLoop);
    	d.drawForPicking(this);	
    	drawHits[pickingLoop] = d;
    }
    
    public void pickLabel(Drawable3D d){
    	pickingLoop++;
    	gl.glLoadName(pickingLoop);
    	d.drawLabelForPicking(this);	
    	drawHits[pickingLoop] = d;
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
        
        // Check For VBO support
        final boolean VBOsupported = gl.isFunctionAvailable("glGenBuffersARB") &&
                gl.isFunctionAvailable("glBindBufferARB") &&
                gl.isFunctionAvailable("glBufferDataARB") &&
                gl.isFunctionAvailable("glDeleteBuffersARB");
        Application.debug("vbo supported : "+VBOsupported);
        

        
        //TODO use gl lists / VBOs
        //geometryManager = new GeometryManager(gl,GeometryManager.TYPE_DIRECT);
        geometryManager = new ManagerGLList(gl,glu,view3D);
        
        
        
        
        
        
        //light
        /*
        float pos[] = { 1.0f, 1.0f, 1.0f, 0.0f };
        //float pos[] = { 0.0f, 0.0f, 1.0f, 0.0f };
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, pos, 0);        
        gl.glEnable(GL.GL_LIGHTING);     
        gl.glEnable(GL.GL_LIGHT0);
        */
        
        
        
        
        float[] lightAmbient0 = {0.1f, 0.1f, 0.1f, 1.0f};
        float[] lightDiffuse0 = {1.0f, 1.0f, 1.0f, 1.0f};
        //float[] lightPosition0 = {1.0f, 1.0f, 1.0f, 0.0f};
        float[] lightPosition0 = {-1.0f, 0.5f, 1.0f, 0.0f};
        float[] lightSpecular0 = {0f, 0f, 0f, 1f};
       
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, lightAmbient0, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuse0, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition0, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, lightSpecular0, 0);
        gl.glEnable(GL.GL_LIGHT0);
        
        
        /*
        float[] lightAmbient1 = {0.1f, 0.1f, 0.1f, 1.0f};
        float[] lightDiffuse1 = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] lightPosition1 = {-1.0f, -1.0f, -1.0f, 0.0f};
        float[] lightSpecular1 = {0f, 0f, 0f, 1f};
       
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbient1, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuse1, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPosition1, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, lightSpecular1, 0);
        gl.glEnable(GL.GL_LIGHT1);      
        */
        
  
        gl.glShadeModel(GL.GL_SMOOTH);
        gl.glLightModeli(GL.GL_LIGHT_MODEL_TWO_SIDE,GL.GL_TRUE);
        gl.glLightModelf(GL.GL_LIGHT_MODEL_TWO_SIDE,GL.GL_TRUE);
  
        
        gl.glEnable(GL.GL_LIGHTING);
   
        //common enabling
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);

        //gl.glPolygonOffset(1.0f, 2f);

        gl.glEnable(GL.GL_CULL_FACE);
        
        //blending
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
        gl.glEnable(GL.GL_BLEND);	
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);  
        
        gl.glAlphaFunc(GL.GL_NOTEQUAL, 0);//pixels with alpha=0 are not drawn
        
        //using glu quadrics
        quadric = glu.gluNewQuadric();// Create A Pointer To The Quadric Object (Return 0 If No Memory) (NEW)
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);          // Create Smooth Normals (NEW)
        glu.gluQuadricTexture(quadric, true);                    // Create Texture Coords (NEW)
        
        //projection type
        //viewOrtho(gl); 
        
        
        //normal anti-scaling
        gl.glEnable(GL.GL_NORMALIZE);
        //gl.glEnable(GL.GL_RESCALE_NORMAL);
        
        
        
        //material
        gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
        //gl.glColorMaterial(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE);
        gl.glEnable(GL.GL_COLOR_MATERIAL);

        
        //textures
        textures = new Textures(gl);
        

    }

    
    
    
    
    
    
    
    
    //projection mode
    
	int left = 0; int right = 640;
	int bottom = 0; int top = 480;
	//int front = -1000; int back = 1000;
	int front = -1000; int back = 1000;
	
	
	public int getLeft(){ return left;	}
	public int getRight(){ return right;	}
	public int getBottom(){ return bottom;	}
	public int getTop(){ return top;	}
	public int getFront(){ return front;	}
	public int getBack(){ return back;	}
	
	
	
	
	/** for a line described by (o,v), return the min and max parameters to draw the line
	 * @param minmax initial interval
	 * @param o origin of the line
	 * @param v direction of the line
	 * @return interval to draw the line
	 */
	public double[] getIntervalInFrustum(double[] minmax, GgbVector o, GgbVector v){
		
		
		
		double left = (getLeft() - o.get(1))/v.get(1);
		double right = (getRight() - o.get(1))/v.get(1);		
		updateIntervalInFrustum(minmax, left, right);
		
		double top = (getTop() - o.get(2))/v.get(2);
		double bottom = (getBottom() - o.get(2))/v.get(2);
		updateIntervalInFrustum(minmax, top, bottom);
		
		double front = (getFront() - o.get(3))/v.get(3);
		double back = (getBack() - o.get(3))/v.get(3);
		updateIntervalInFrustum(minmax, front, back);
			
		
		/*
		Application.debug("intersection = ("+left+","+right+
				")/("+top+","+bottom+")/("+front+","+back+")"+
				"\ninterval = ("+minmax[0]+","+minmax[1]+")");
		*/
				
		
		return minmax;
	}
	
	/** return the intersection of intervals [minmax] and [v1,v2]
	 * @param minmax initial interval
	 * @param v1 first value
	 * @param v2 second value
	 * @return intersection interval
	 */
	private double[] updateIntervalInFrustum(double[] minmax, double v1, double v2){
		
		if (v1>v2){
			double v = v1;
			v1 = v2; v2 = v;
		}
		
		if (v1>minmax[0])
			minmax[0] = v1;
		if (v2<minmax[1])
			minmax[1] = v2;
		
		return minmax;
	}
   
	
    /**
     * Set Up An Ortho View regarding left, right, bottom, front values
     * 
     */
    private void viewOrtho()                                      
    {

    	gl.glViewport(0,0,right-left,top-bottom);
    	
    	gl.glMatrixMode(GL.GL_PROJECTION);
    	gl.glLoadIdentity();

    	gl.glOrtho(left,right,bottom,top,front,back);
    	gl.glMatrixMode(GL.GL_MODELVIEW);
    	
    	
    	
    }
    
    
	
    /**
     * Set Up An Ortho View after setting left, right, bottom, front values
     * @param x left
     * @param y bottom
     * @param w width
     * @param h height
     * 
     */
    private void viewOrtho(int x, int y, int w, int h){
    	left=x-w/2;
    	bottom=y-h/2;
    	right=left+w;
    	top = bottom+h;
    	
    	/*
    	Application.debug("viewOrtho:"+
    			"\n left="+left+"\n right="+right+
    			"\n top="+top+"\n bottom="+bottom+
    			"\n front="+front+"\n back="+back
    	);
    	*/
    	
    	viewOrtho();
    }
   
    




    private void viewPerspective(GL gl)                                  // Set Up A Perspective View
    {
        gl.glMatrixMode(GL.GL_PROJECTION);                               // Select Projection
        gl.glPopMatrix();                                                // Pop The Matrix
        gl.glMatrixMode(GL.GL_MODELVIEW);                                // Select Modelview
        gl.glPopMatrix();                                                // Pop The Matrix
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    
    
    


}
