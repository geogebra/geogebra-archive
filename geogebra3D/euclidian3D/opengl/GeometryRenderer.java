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
	 * @param glType type of geometry (GL_QUADS, ...)
	 */
	abstract public void preInit(int glType);
	
	
	/** method used at end of Geometry.init() method
	 * 
	 */
	abstract public void postInit();
	
	
	/** draw the geometry
	 * @param geometry
	 */
	abstract public void draw(Geometry geometry);
	
	
	
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


}
