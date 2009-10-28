package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;

/**
 * 
 * Renderer that redraws all primitives each time
 * 
 * @author ggb3D
 *
 */


public class GeometryRendererGLList extends GeometryRenderer {
	

	
	/**
	 * creates the direct geometryRenderer, linked to the GL
	 * @param gl
	 */
	public GeometryRendererGLList(GL gl) {
		super(gl);
	}

	
	
	public void preInit(Geometry geometry){
		
		int index = gl.glGenLists(geometry.getNb());
		geometry.setIndex(index);

	}
	
	
	public void startGeometry(Geometry geometry, int index){
		gl.glNewList(geometry.getIndex()+index, GL.GL_COMPILE);
		gl.glBegin(geometry.getType());
	}
	
	public void endGeometry(Geometry geometry){
		gl.glEnd();
		gl.glEndList();
	}
	
	
	
	
	public void draw(Geometry geometry, int index){

		gl.glCallList(geometry.getIndex()+index);
		
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
	
	
	protected void color(float r, float g, float b){
		gl.glColor3f(r,g,b);
	}

}
