package geogebra3D.euclidian3D;

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

import com.sun.opengl.util.BufferUtil;


import geogebra.Application;
import geogebra.kernel.GeoElement;


public class EuclidianRenderer3D implements GLEventListener {
	
	// openGL variables
	private GLU glu= new GLU();
	public GLCanvas canvas;
	private GL gl;
	protected GLUquadric quadric;
	
	// other
	private DrawList3D drawList3D;
	
	EuclidianView3D view;
	
	private int mouseX, mouseY;
	private boolean waitForPick = false;
	
	
	public EuclidianRenderer3D(EuclidianView3D view){
		super();
		
		
		
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
        //   int[] buffer = new int[BUFSIZE]; // Set Up A Selection Buffer
        IntBuffer selectBuffer = BufferUtil.newIntBuffer(BUFSIZE);
        int hits; // The Number Of Objects That We Selected
        gl.glSelectBuffer(BUFSIZE, selectBuffer); // Tell OpenGL To Use Our Array For Selection
        
        
        // The Size Of The Viewport. [0] Is <x>, [1] Is <y>, [2] Is <length>, [3] Is <width>
        int[] viewport = new int[4];
        gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);      
        //System.out.println("viewport= "+viewport[0]+","+viewport[1]+","+viewport[2]+","+viewport[3]);
        
        
        Dimension dim = canvas.getSize();
        //System.out.println("dimension= "+dim.width +","+dim.height);
        
        
        
        /*
        viewport[0]=(int) view.left;
        viewport[1]=(int) view.bottom;
        viewport[2]=(int) (view.right-view.left);
        viewport[3]=(int) (view.top-view.bottom);
        */
        
        // Puts OpenGL In Selection Mode. Nothing Will Be Drawn.  Object ID's and Extents Are Stored In The Buffer.
        gl.glRenderMode(GL.GL_SELECT);
        gl.glInitNames(); // Initializes The Name Stack
        gl.glPushName(0); // Push 0 (At Least One Entry) Onto The Stack
        
        
        // This Creates A Matrix That Will Zoom Up To A Small Portion Of The Screen, Where The Mouse Is.

        
        gl.glMatrixMode(GL.GL_PROJECTION);
        //gl.glPushMatrix();
        gl.glLoadIdentity();
      
        /* create 5x5 pixel picking region near cursor location */

        //glu.gluPickMatrix((double) mouseX, (double) (470 - mouseY),  5.0, 5.0, viewport, 0);
        //mouseY+=30; //TODO understand this offset
        //glu.gluPickMatrix((double) mouseX, (double) (viewport[3] - mouseY), 5.0, 5.0, viewport, 0);
        glu.gluPickMatrix((double) mouseX, (double) (dim.height - mouseY), 5.0, 5.0, viewport, 0);
        //gl.glOrtho(0.0, 8.0, 0.0, 8.0, -0.5, 2.5);
        gl.glOrtho(view.left,view.right,view.bottom,view.top,view.front,view.back);
    	gl.glMatrixMode(GL.GL_MODELVIEW);
        
		//drawing not hidden parts
    	GeoElement[] geos = new GeoElement[BUFSIZE];
        int loop = 0;
		for (Iterator iter = drawList3D.iterator(); iter.hasNext();) {
			Drawable3D d = (Drawable3D) iter.next();
			loop++;
			gl.glLoadName(loop);
			d.draw(this);	
			geos[loop]=d.getGeoElement();
			//Application.debug("--"+gl.glRenderMode(GL.GL_RENDER));
		}
        
        //gl.glPopMatrix();
        //gl.glFlush();
        
        hits = gl.glRenderMode(GL.GL_RENDER); // Switch To Render Mode, Find Out How Many
       

        Application.debug("hits("+mouseX+","+mouseY+") = "+hits);
        
        
        int[] buffer = new int[BUFSIZE];
        selectBuffer.get(buffer);
        int names, ptr = 0;
        for (int i = 0; i < hits; i++)
        { /* for each hit */
        	
          names = buffer[ptr];
          
          //System.out.println(" number of names for hit = " + names);
          ptr++;
          //System.out.println(" z1 is " + buffer[ptr]);
          ptr++;
          //System.out.println(" z2 is " + buffer[ptr]);
          ptr++;
          
          for (int j = 0; j < names; j++)
          { /* for each name */
        	Application.debug("the name is " + buffer[ptr]+" -- geo["+buffer[ptr]+"] = " + geos[buffer[ptr]].getLabel());
            ptr++;
          }
          //System.out.println();
        }
    	
        waitForPick = false;
    }
    
    
    
    
    
    //////////////////////////////////
    // intializations
    
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
