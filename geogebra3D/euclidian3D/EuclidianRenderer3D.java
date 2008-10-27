package geogebra3D.euclidian3D;

import geogebra3D.kernel3D.GeoElement3D;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.nio.IntBuffer;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.sun.opengl.util.BufferUtil;
import com.sun.opengl.util.FPSAnimator;


public class EuclidianRenderer3D implements GLEventListener {
	
	// openGL variables
	private GLU glu= new GLU();
	public GLCanvas canvas;
	//public GLJPanel canvas;
	private GLCapabilities caps;
	private GL gl;
	protected GLUquadric quadric;
	private FPSAnimator animator;
	
	// other
	private DrawList3D drawList3D;
	
	EuclidianView3D view;
	
	private int mouseX, mouseY;
	private boolean waitForPick = false;
	
	
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
		
		this.view=view;
	}
	
	
	public void setDrawList3D(DrawList3D dl){
		drawList3D = dl;
	}
	
	
	
    public void display(GLAutoDrawable gLDrawable) {
    	
        gl = gLDrawable.getGL();
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        
        
        //picking
        if(waitForPick)
        	doPick();
        
        //start drawing
        viewOrtho();
        
        view.update();
        
        

        //drawing hidden parts	
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.drawHidden(this);	
		}
       
        
		//drawing picked parts
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
		
		
		//drawing not hidden parts
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			d.draw(this);	
		}

		
		
		
		
		gLDrawable.swapBuffers(); //TODO



    }    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
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

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
        boolean deviceChanged)
    {
    }    
    
    
    
    
    
    //material
    public void setMaterial(Color c, float alpha){
    	//TODO use alpha value
    	float color[] = { ((float) c.getRed())/256f, ((float) c.getGreen())/256f, ((float) c.getBlue())/256f, alpha };
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, color, 0);
    }
    
    
    
    //transformation matrix
    public void setMatrix(double[] m){
    	gl.glPushMatrix();
		gl.glLoadMatrixd(m,0);
    }
    
    public void resetMatrix(){
    	gl.glPopMatrix();
    }
    
    
    
    //drawing primitives
    //TODO use glLists
    public void drawSphere(float radius){
    	glu.gluSphere(quadric, radius, 16, 16);
    }

    
    public void drawCylinder(float radius){
    	gl.glRotatef(90f, 0.0f, 1.0f, 0.0f); //switch z-axis to x-axis
    	glu.gluCylinder(quadric, radius, radius, 1.0f, 8, 1);
    }
    
    
    public void drawTriangle(){    	
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
    }
    
    
    public void drawQuad(){    	
        gl.glBegin(GL.GL_QUADS);	
        
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);	
        gl.glVertex3f(1.0f, 0.0f, 0.0f);	
        gl.glVertex3f(1.0f, 1.0f, 0.0f);	
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
                
        gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glVertex3f(0.0f, 0.0f, 0.0f);	
        gl.glVertex3f(0.0f, 1.0f, 0.0f);
        gl.glVertex3f(1.0f, 1.0f, 0.0f);	
        gl.glVertex3f(1.0f, 0.0f, 0.0f);	
        
        gl.glEnd();				
    }
    
    
    
    //////////////////////////////////////
    // picking
    public void setMouseLoc(int x, int y){
    	mouseX = x;
    	mouseY = y;
    	waitForPick = true;
    }
    
    
    public void doPick(){
    	
    	int BUFSIZE = 512;
        IntBuffer selectBuffer = BufferUtil.newIntBuffer(BUFSIZE); // Set Up A Selection Buffer
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
      
        
        /* create 5x5 pixel picking region near cursor location */
        //glu.gluPickMatrix((double) mouseX, (double) (470 - mouseY),  5.0, 5.0, viewport, 0);
        //mouseY+=30; //TODO understand this offset
        //glu.gluPickMatrix((double) mouseX, (double) (viewport[3] - mouseY), 5.0, 5.0, viewport, 0);
        glu.gluPickMatrix((double) mouseX, (double) (dim.height - mouseY), 5.0, 5.0, viewport, 0);
        gl.glOrtho(view.left,view.right,view.bottom,view.top,view.front,view.back);
    	gl.glMatrixMode(GL.GL_MODELVIEW);
        
		//drawing not hidden parts
    	//Drawable3D[] drawHits = new Drawable3D[BUFSIZE];
    	GeoElement3D[] geos = new GeoElement3D[BUFSIZE];
        int loop = 0;
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			loop++;
			gl.glLoadName(loop);
			d.drawForPicking(this);	
			geos[loop] = (GeoElement3D) d.getGeoElement();
			
			/*
			geos[loop].setWasHighlighted();
			geos[loop].setWillBeHighlighted(false);
			*/
			

		}
        
        
        hits = gl.glRenderMode(GL.GL_RENDER); // Switch To Render Mode, Find Out How Many
       

        //Application.debug("hits("+mouseX+","+mouseY+") = "+hits);
        
        
        //hits are stored in EuclidianView3D
        view.hits.clear();
        
        int[] buffer = new int[BUFSIZE];
        selectBuffer.get(buffer);
        int names, ptr = 0;
        for (int i = 0; i < hits; i++) { 
        	
          names = buffer[ptr];
          
          //System.out.println(" number of names for hit = " + names);
          ptr++;
          //System.out.println(" z1 is " + buffer[ptr]);
          ptr++;
          //System.out.println(" z2 is " + buffer[ptr]);
          ptr++;
          
          for (int j = 0; j < names; j++){ 
        	//Application.debug("the name is " + buffer[ptr]+" -- drawHits["+buffer[ptr]+"] = " + geos[buffer[ptr]].getLabel());
        	//geos[buffer[ptr]].setWillBeHighlighted(true); //geos picked will be highlighted
        	view.hits.add(geos[buffer[ptr]]);
        	ptr++;
          }
          //System.out.println();
        }
        
        //update highlighting      
        /*
        for (int i=1;i<=loop;i++)
        	geos[i].updateHighlighted(true);
        	*/
        
    	
        waitForPick = false;
        view.waitForPick = true;
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
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, pos, 0);        
        gl.glEnable(GL.GL_LIGHTING);     
        gl.glEnable(GL.GL_LIGHT0);
        
        //common enabling
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        
        //blending
        gl.glEnable(GL.GL_BLEND);	
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
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
    	gl.glViewport(0,0,(int) (view.right-view.left),(int) (view.top-view.bottom));
    	
    	gl.glMatrixMode(GL.GL_PROJECTION);
    	gl.glLoadIdentity();

    	gl.glOrtho(view.left,view.right,view.bottom,view.top,view.front,view.back);
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
