package geogebra3D.euclidian3D;

import java.awt.Color;
import java.util.Iterator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;


import geogebra.Application;
import geogebra3D.euclidian3D.GL.Renderer;

public class EuclidianRenderer3D extends Renderer {
	
	
	private DrawList3D drawList3D;
	private GL gl;
	EuclidianView3D view;
	
	
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
        
        
        viewOrtho(gl);
        
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
    private void viewOrtho(GL gl)                                        // Set Up An Ortho View
    {
    	//TODO change viewport when resized
    	gl.glViewport(0,0,EuclidianGLDisplay.DEFAULT_WIDTH,EuclidianGLDisplay.DEFAULT_HEIGHT);
    	
    	gl.glMatrixMode(GL.GL_PROJECTION);
    	gl.glLoadIdentity();
    	double ratio = 1;
    	double w = ((double) EuclidianGLDisplay.DEFAULT_WIDTH)/ratio;
    	double h = ((double) EuclidianGLDisplay.DEFAULT_HEIGHT)/ratio;
    	gl.glOrtho(0,w,0,h,-1000,1000);
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
