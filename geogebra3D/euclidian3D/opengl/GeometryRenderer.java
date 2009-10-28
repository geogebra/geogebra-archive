package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;

/**
 * Class that render a geometry, implemented for direct mode, glList mode, VBO mode
 * 
 * @author ggb3D
 *
 */


public abstract class GeometryRenderer {
	
	protected GL gl;
	
	/**
	 * creates the geometryRenderer, linked to the GL
	 * @param gl
	 */
	public GeometryRenderer(GL gl){
		
		this.gl = gl;
		
	}
	
	
	
	
	
	/** method used at start of Geometry.init() method
	 * @param geometry 
	 */
	abstract public void preInit(Geometry geometry);
	
	
	/** starting new geometry
	 * @param geometry 
	 * @param index index of the new geometry
	 */
	abstract public void startGeometry(Geometry geometry, int index);
	
	public void startGeometry(Geometry geometry){
		startGeometry(geometry, 0);
	}
	
	
	/** ending new geometry
	 * @param geometry 
	 */
	abstract public void endGeometry(Geometry geometry);


	
	
	/** draw the geometry
	 * @param geometry
	 */
	public void draw(Geometry geometry){
		draw(geometry,0);
	}
	
	
	/** draw the indexed geometry
	 * @param geometry
	 * @param index index of the geometry
	 */
	abstract public void draw(Geometry geometry, int index);
	
	
	
	/** creates a vertex at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	abstract protected void vertex(float x, float y, float z);
	
	
	/** creates a normal at coordinates (x,y,z)
	 * @param x x coord
	 * @param y y coord
	 * @param z z coord
	 */
	abstract protected void normal(float x, float y, float z);
	
	
	/** creates a texture at coordinates (x,y)
	 * @param x x coord
	 * @param y y coord
	 */
	abstract protected void texture(float x, float y);


	/** creates a color (r,g,b)
	 * @param r red
	 * @param g green
	 * @param b blue
	 * 
	 */
	abstract protected void color(float r, float g, float b);

	
	
}
