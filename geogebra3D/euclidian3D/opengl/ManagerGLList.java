package geogebra3D.euclidian3D.opengl;

import java.nio.FloatBuffer;

import geogebra3D.euclidian3D.EuclidianView3D;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;


/**
 * 
 * Manager using GL lists
 * 
 * @author ggb3D
 *
 */

public class ManagerGLList extends Manager {


	
	// GL 
	private GLUtessellator tesselator;
	

	/** common constructor
	 * @param gl
	 * @param glu 
	 * @param view3D 3D view
	 */
	public ManagerGLList(GL2 gl, GLU glu, EuclidianView3D view3D) {
		super(gl,glu,view3D);
	}

	
	/////////////////////////////////////////////
	// LISTS METHODS
	/////////////////////////////////////////////


	private int genLists(int nb){
		return gl.glGenLists(nb);
	}
	
	
	
	
	
	/////////////////////////////////////////////
	// GEOMETRY METHODS
	/////////////////////////////////////////////

	
	public int startNewList(){
		// generates a new list
		int ret = genLists(1);
		
		gl.glNewList(ret, GLlocal.GL_COMPILE);
		
		return ret;
	}
	
	private void newList(int index){
		gl.glNewList(index, GLlocal.GL_COMPILE);
	}
	
	
	
	public void endList(){
		
		gl.glEndList();
	}
	
	public void startGeometry(int type){
		gl.glBegin(type);
	}
	
	public void endGeometry(){
		gl.glEnd();
	}

	

	
	
	/////////////////////////////////////////////
	// POLYGONS METHODS
	/////////////////////////////////////////////

	
	/** start a new polygon 
	 * @param nx normal x coordinate
	 * @param ny normal y coordinate
	 * @param nz normal z coordinate
	 * @return gl index
	 */
	public int startPolygon(float nx, float ny, float nz){
		
		// generates a new list
		int ret = genLists(1);
		
		//Application.debug("ret = "+ret);
		
		// if ret == 0, there's no list
		if (ret == 0)
			return 0;
		
	    RendererTesselCallBack tessCallback = new RendererTesselCallBack(gl, glu);
	    
	    tesselator = glu.gluNewTess();

	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_VERTEX, tessCallback);// vertexCallback);
	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_BEGIN, tessCallback);// beginCallback);
	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_END, tessCallback);// endCallback);
	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_ERROR, tessCallback);// errorCallback);
	    glu.gluTessCallback(tesselator, GLU.GLU_TESS_COMBINE, tessCallback);// combineCallback);

	    
	    newList(ret);
	    
    	//normal(nx, ny, nz);
    	
	    glu.gluTessBeginPolygon(tesselator, null);
	    glu.gluTessBeginContour(tesselator);
	    
	    glu.gluTessNormal(tesselator,nx,ny,nz);
		normal(nx, ny, nz);
		texture(0,0);
		/*
		newList(ret);
		
		gl.glBegin(GLlocal.GL_TRIANGLES);
		normal(nx, ny, nz);
		*/
	    
	    return ret;

		
	}
	
	
	
    /**
     * ends the current polygon
     */
    public void endPolygon(){
    	
    	
	    glu.gluTessEndContour(tesselator);
	    glu.gluTessEndPolygon(tesselator);
	    gl.glEndList();
	    
	    glu.gluDeleteTess(tesselator);
	    
    	
    	//endGeometry(null);
        
	
    }
    
    
    /** remove the polygon from gl memory
     * @param index
     */
    public void remove(int index){
    	
    	gl.glDeleteLists(index, 1);
    	
    }

	
 	
	
	
	/////////////////////////////////////////////
	// DRAWING METHODS
	/////////////////////////////////////////////

	public void draw(int index){
		gl.glCallList(index);
	}
	
	

	
	protected void texture(float x, float y){
		
		gl.glTexCoord2f(x,y);
		
	}

	
	protected void normal(float x, float y, float z){
		
		gl.glNormal3f(x,y,z); 
		
	}
	
	
	protected void vertex(float x, float y, float z){
		
		gl.glVertex3f(x,y,z); 
		
	}
	
	protected void vertices(FloatBuffer v, int count){
		v.rewind();
		gl.glEnableClientState(GLlocal.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GLlocal.GL_FLOAT, 0, v);
		gl.glDrawArrays(GLlocal.GL_TRIANGLES, 0, 3);
		gl.glDisableClientState(GLlocal.GL_VERTEX_ARRAY);
	}
	
	
	protected void color(float r, float g, float b){
		gl.glColor3f(r,g,b);
	}
	
	protected void color(float r, float g, float b, float a){
		gl.glColor4f(r,g,b,a);
	}
	
	/////////////////////////////////////////////
	// POLYGONS DRAWING METHODS
	/////////////////////////////////////////////

	
	public void addVertexToPolygon(double x, double y, double z){
		
		
		double[] point = {x,y,z};
		glu.gluTessVertex(tesselator, point, 0, point);
		
		
		//vertex((float) x, (float) y,(float)  z);
	}
	
	

	


}
