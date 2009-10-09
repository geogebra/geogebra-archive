package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;

/**
 * 
 * Renderer that redraws all primitives each time
 * 
 * @author ggb3D
 *
 */


public class GeometryRendererDirect extends GeometryRenderer {
	
	/**
	 * creates the direct geometryRenderer, linked to the GL
	 * @param gl
	 */
	public GeometryRendererDirect(GL gl) {
		super(gl);
	}

	
	
	public void preInit(int glType){
		gl.glBegin(glType);
	}
	
	
	public void postInit(){
		gl.glEnd();
	}
	
	
	public void draw(Geometry geometry){
		//redraws the geometry
		geometry.init();
	}
	

	
	
	protected void vertex(float x, float y, float z){
		gl.glVertex3f(x,y,z); 
	}
	
	
	protected void normal(float x, float y, float z){
		gl.glNormal3f(x,y,z); 
	}
	
	
	protected void texture(float x, float y){
		gl.glTexCoord2f(x,y);
	}


}
