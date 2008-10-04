package geogebra3D.euclidian3D.GL;

/*
 * Lesson03.java
 *
 * Created on July 14, 2003, 12:35 PM
 */


import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;



/** Port of the NeHe OpenGL Tutorial (Lesson 3: Colors)
 * to Java using the Jogl interface to OpenGL.  Jogl can be obtained
 * at http://jogl.dev.java.net/
 *
 * @author Kevin Duling (jattier@hotmail.com)
 */
public class Renderer implements GLEventListener {
    protected GLU glu = new GLU();
    protected GLUquadric quadric;
    
    float rotation = 0f;
    
    
    
    
    
    
     
    
    float red[] = { 0.8f, 0.1f, 0.0f, 0.75f };
    float green[] = { 0.0f, 0.8f, 0.2f, 0.25f };
    float blue[] = { 0.2f, 0.2f, 1.0f, 1f };

    

    /** Called by the drawable to initiate OpenGL rendering by the client.
     * After all GLEventListeners have been notified of a display event, the
     * drawable will swap its buffers if necessary.
     * @param gLDrawable The GLAutoDrawable object.
     */
    public void display(GLAutoDrawable gLDrawable) {
    }    
    
    
    



    /** Called when the display mode has been changed.  <B>!! CURRENTLY UNIMPLEMENTED IN JOGL !!</B>
     * @param gLDrawable The GLAutoDrawable object.
     * @param modeChanged Indicates if the video mode has changed.
     * @param deviceChanged Indicates if the video device has changed.
     */
    public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged) {
    }
    
    

    

    /** Called by the drawable immediately after the OpenGL context is
     * initialized for the first time. Can be used to perform one-time OpenGL
     * initialization such as setup of lights and display lists.
     * @param gLDrawable The GLAutoDrawable object.
     */
    public void init(GLAutoDrawable drawable) {
    	
         
    	//drawable.setAutoSwapBufferMode(false);
        GL gl = drawable.getGL();
        
        /*
        gl.glViewport(0,0,800,600);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0,800,0,600,-100,100);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        */
        
        
        //light
        float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, pos, 0);        
        gl.glEnable(GL.GL_LIGHTING);     
        gl.glEnable(GL.GL_LIGHT0);
        
        //common enabling
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        
        
        
        //blending
        gl.glEnable(GL.GL_BLEND);	
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);  
         
        
        
        //gl.glEnable(GL.GL_TEXTURE_2D);                             // Enable Texture Mapping
        //gl.glShadeModel(GL.GL_SMOOTH);                             // Enable Smooth Shading
        //gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);// Really Nice Perspective Calculations

         
        
        
        //using glu quadrics
        quadric = glu.gluNewQuadric();// Create A Pointer To The Quadric Object (Return 0 If No Memory) (NEW)
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);          // Create Smooth Normals (NEW)
        glu.gluQuadricTexture(quadric, true);                    // Create Texture Coords (NEW)
        
         
    }


    /** Called by the drawable during the first repaint after the component has
     * been resized. The client can update the viewport and view volume of the
     * window appropriately, for example by a call to
     * GL.glViewport(int, int, int, int); note that for convenience the component
     * has already called GL.glViewport(int, int, int, int)(x, y, width, height)
     * when this method is called, so the client may not have to do anything in
     * this method.
     * @param gLDrawable The GLAutoDrawable object.
     * @param x The X Coordinate of the viewport rectangle.
     * @param y The Y coordinate of the viewport rectanble.
     * @param width The new width of the window.
     * @param height The new height of the window.
     */
    public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
    	
        final GL gl = gLDrawable.getGL();

        if (height <= 0) // avoid a divide by zero error!
            height = 1;
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }
    
    
    
    
}
