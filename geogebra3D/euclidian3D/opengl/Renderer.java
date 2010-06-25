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
	//textures
	private Textures textures;

	
	
	
	/////////////////////
	// pencil attributes
	
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
		getTextures().loadTexture(Textures.FADING);
		
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
		
		getTextures().loadTexture(Textures.FADING);

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
    	
    	if (!view3D.isStarted()) return;
    	
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

    	
        
        
        
        //draw face-to screen parts (labels, ...)
        //drawing labels
        //gl.glEnable(GL.GL_CULL_FACE);
        //gl.glCullFace(GL.GL_BACK);
        gl.glEnable(GL.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts     
        gl.glDisable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_BLEND);
        gl.glEnable(GL.GL_TEXTURE_2D);
        //gl.glDisable(GL.GL_BLEND);
        drawList3D.drawLabel(this);
        //gl.glEnable(GL.GL_LIGHTING);
        //gl.glDisable(GL.GL_ALPHA_TEST);     
        gl.glDisable(GL.GL_TEXTURE_2D);
    	
        
        
        
        //init drawing matrix to view3D toScreen matrix
        gl.glPushMatrix();
        gl.glLoadMatrixd(view3D.getToScreenMatrix().get(),0);
        
        
 


        //drawing the cursor
        //gl.glEnable(GL.GL_BLEND);
        gl.glEnable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_ALPHA_TEST);       
        gl.glEnable(GL.GL_CULL_FACE);
        //gl.glCullFace(GL.GL_FRONT);
        view3D.drawCursor(this);
        
         
        
        
        
        //primitives.enableVBO(gl);
        
        //drawing hidden part
        //gl.glEnable(GL.GL_CULL_FACE);
        gl.glEnable(GL.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts     
        //gl.glDisable(GL.GL_BLEND);
        drawList3D.drawHiddenNotTextured(this);
        gl.glEnable(GL.GL_TEXTURE_2D);
        drawList3D.drawHiddenTextured(this);
        drawNotTransp();
        gl.glEnable(GL.GL_CULL_FACE);
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
        //gl.glCullFace(GL.GL_BACK);
        //gl.glEnable(GL.GL_ALPHA_TEST);  //avoid z-buffer writing for transparent parts     
        //gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_BLEND);
        //drawList3D.drawLabel(this);
        //gl.glEnable(GL.GL_LIGHTING);
        //gl.glDisable(GL.GL_ALPHA_TEST);       

        
        
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
        gl.glDisable(GL.GL_LIGHTING);
        gl.glDisable(GL.GL_DEPTH_TEST);

        
        
        gl.glPopMatrix();
    	
    	/*
        setColor(color.BLACK, 1);
        GgbVector v = new GgbVector(1, 0, 0, 1);
        view3D.toScreenCoords3D(v);
    	geometryManager.getText().draw3D(gl, (float) v.getX(), (float) v.getY(), (float) v.getZ());
    	
    	*/
    	
    	//drawFPS();
    	gl.glEnable(GL.GL_DEPTH_TEST);
    	gl.glEnable(GL.GL_LIGHTING);

    	
    	
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
    
    
    

    
    
    
    //////////////////////////////////////
    // EXPORT IMAGE
    //////////////////////////////////////
      
    
    private boolean needExportImage=false;
    
    
    /**
     * says that an export image is needed, and call immediate display
     */
    public void needExportImage(){
    	needExportImage = true;
    	display();
    	
    }
    
    private BufferedImage bi;
    
    /**
     * creates an export image (and store it in BufferedImage bi)
     */
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

    /**
     * @return a BufferedImage containing last export image created
     */
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
    
    
    
    
    
    ///////////////////////////////////////////////////////////
    //drawing geometries
    
    
    public Manager getGeometryManager(){
    	return geometryManager;
    }
    
    
    ///////////////////////////////////////////////////////////
    //textures
    
    
    public Textures getTextures(){
    	return textures;
    }
    
    

    
    
    
    
    
    
    
    
    
    
    
    
    
    

 
    
    
    
    /** draws a 2D cross cursor 
     */    
    public void drawCursorCross2D(){
    	
    	gl.glDisable(GL.GL_LIGHTING);
    	initMatrix();
    	geometryManager.draw(geometryManager.cursor.getIndex(PlotterCursor.TYPE_CROSS2D));
		resetMatrix();
    	gl.glEnable(GL.GL_LIGHTING);
   	
    }
    
    /** draws a 3D cross cursor
     */    
    public void drawCursorCross3D(){
    	
    	gl.glDisable(GL.GL_LIGHTING);
    	initMatrix();
    	geometryManager.draw(geometryManager.cursor.getIndex(PlotterCursor.TYPE_CROSS3D));
		resetMatrix();
    	gl.glEnable(GL.GL_LIGHTING);
   	
    } 
    
    /** draws a cylinder cursor 
     */    
    public void drawCursorCylinder(){
 
    	gl.glDisable(GL.GL_LIGHTING);  
     	initMatrix();
    	geometryManager.draw(geometryManager.cursor.getIndex(PlotterCursor.TYPE_CYLINDER));
    	resetMatrix();
    	gl.glEnable(GL.GL_LIGHTING);

    	
    }
    
    
    

    
    /** draws a diamond cursor  
     *
     */    
    public void drawCursorDiamond(){
 
    	gl.glDisable(GL.GL_LIGHTING);  
    	initMatrix();
    	geometryManager.draw(geometryManager.cursor.getIndex(PlotterCursor.TYPE_DIAMOND));
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
    
     
    /*
    public void drawText(GgbVector v, 
    		float xOffset, float yOffset,
    		PlotterTextLabel label){
    	
    	view3D.toScreenCoords3D(v);
    	
    	drawText(
    			(float) v.getX() + xOffset,
    			(float) v.getY() + yOffset,
    			(float) v.getZ(), 
    			label);
    	
    	
    }
    */
    
    public void drawText(//float x, float y, float z,
    		PlotterTextLabel label){
    
    	
    	label.draw3D(gl, view3D.getToScreenMatrix());
    
    }
    
    private void drawText(float x, float y, float z,
    		String s, boolean colored){
    
    	geometryManager.getText().create(s, 14);
    	geometryManager.getText().draw3D(gl, x,y,z);
    
    }
    
   
    
    private void drawText0(float x, float y, float z,
    		String s, boolean colored){
    

    	gl.glMatrixMode(GL.GL_TEXTURE);
    	gl.glLoadIdentity();

    	
    	gl.glMatrixMode(GL.GL_MODELVIEW);


    	//initMatrix();
    	/*
    	initMatrix(view3D.getUndoRotationMatrix());
    	*/


    	textRenderer.begin3DRendering();

    	if (colored)
    		textRenderer.setColor(textColor);


    	
    	//float textScaleFactor = DEFAULT_TEXT_SCALE_FACTOR/((float) view3D.getScale());

/*
    	if (x<0)
    		x=x-(s.length()-0.5f)*8; //TODO adapt to police size

    	textRenderer.draw3D(s,
    			x*textScaleFactor,//w / -2.0f * textScaleFactor,
    			y*textScaleFactor,//h / -2.0f * textScaleFactor,
    			0,
    			textScaleFactor);
    			*/
    	
    	textRenderer.draw3D(s,
    			x,y,z,
    			1f);

    	textRenderer.end3DRendering();


/*
    	resetMatrix(); //initMatrix(m_view3D.getUndoRotationMatrix());
    	*/
    	//resetMatrix(); //initMatrix();
    	
    
    }
    
    /* draws the text s
     * @param x x-coord
     * @param y y-coord
     * @param s text
     * @param colored says if the text has to be colored
     *
    public void drawText(float x, float y, String s, boolean colored){
    	
    	
    	//if (true)    		return;
    	
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
    
    
    
    */
    
    
    
    
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
    	gl.glPushMatrix();
        gl.glLoadMatrixd(view3D.getToScreenMatrix().get(),0);
        drawList3D.drawForPicking(this);
        gl.glPopMatrix();

        int labelLoop = pickingLoop;
        
        if (pickingMode == PICKING_MODE_LABELS){
        	// picking labels
        	gl.glEnable(GL.GL_ALPHA_TEST);
        	gl.glEnable(GL.GL_BLEND);
           	gl.glEnable(GL.GL_TEXTURE_2D);
           	
        	drawList3D.drawLabelForPicking(this);
        	
           	gl.glDisable(GL.GL_TEXTURE_2D);
           	gl.glDisable(GL.GL_BLEND);
           	gl.glDisable(GL.GL_ALPHA_TEST);
            
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
    	
    	
    	//Application.printStacktrace("");
        
        gl = drawable.getGL();
        
        
        // check openGL version
        Application.debug("openGL version : "+ gl.glGetString(GL.GL_VERSION));
        
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
        
        
        //gl.glPolygonMode(GL.GL_FRONT, GL.GL_LINE);gl.glPolygonMode(GL.GL_BACK, GL.GL_LINE);

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
