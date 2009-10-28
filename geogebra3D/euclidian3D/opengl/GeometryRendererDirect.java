package geogebra3D.euclidian3D.opengl;

import geogebra3D.euclidian3D.Drawable3D;

import java.util.Iterator;

import javax.media.opengl.GL;

/**
 * 
 * Renderer that redraws all primitives each time
 * 
 * TODO remove
 * 
 * @author ggb3D
 *
 */


public class GeometryRendererDirect extends GeometryRenderer {
	
	/** texture-normal-vertex description */
	float[] tnv;
	
	/** current geometry */
	private Geometry geometry;
	
	/**
	 * creates the direct geometryRenderer, linked to the GL
	 * @param gl
	 */
	public GeometryRendererDirect(GL gl) {
		super(gl);
	}

	
	
	public void preInit(Geometry geometry){
		
		this.geometry = geometry;
		this.geometry.tnvList = new GeometryVertexList();
		
	}
	
	public void startGeometry(Geometry geometry, int index){
		
	}
	
	public void endGeometry(Geometry geometry){
		
	}
	
	
	
	public void draw(Geometry geometry, int index){

		gl.glBegin(geometry.getType());
		
		for (Iterator<float[]> tnvi = geometry.tnvList.iterator(); tnvi.hasNext();){
			tnv = tnvi.next();
			gl.glTexCoord2f(tnv[0],tnv[1]);
			gl.glNormal3f(tnv[2],tnv[3],tnv[4]); 
			gl.glVertex3f(tnv[5],tnv[6],tnv[7]); 
		}
			
		gl.glEnd();
		
	}
	

	
	protected void texture(float x, float y){
		
		tnv = new float[8];
		tnv[0] = x; tnv[1] = y;
		
	}

	
	protected void normal(float x, float y, float z){
		
		tnv[2] = x; tnv[3] = y; tnv[4] = z;
		
	}
	
	
	protected void vertex(float x, float y, float z){
		
		tnv[5] = x; tnv[6] = y; tnv[7] = z;
		geometry.tnvList.add(tnv);
		
	}
	
	protected void color(float r, float g, float b){
		//TODO ?
	}
	
	


}
