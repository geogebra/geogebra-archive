package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;

/**
 * Class that manage all geometry objects
 * 
 * @author ggb3D
 *
 */
public class GeometryManager {
	
	/** direct rendering */
	static final int TYPE_DIRECT = 0;
	
	private GeometryRenderer geometryRenderer;
	
	/** geometry : sphere */
	public GeometrySphere sphere;
	/** geometry : cylinder */
	public GeometryCylinder cylinder;
	
	
	/** create a manager for geometries
	 * @param gl 
	 * @param type type of rendering (direct/gl lists/vbos)
	 */
	public GeometryManager(GL gl, int type){
		
		// creating geometryRenderer regarding type
		switch(type){
		case TYPE_DIRECT:
			geometryRenderer = new GeometryRendererDirect(gl);
			break;
		}
		
		// creating geometries
		sphere = new GeometrySphere(geometryRenderer);
		cylinder = new GeometryCylinder(geometryRenderer);
		
		
	}

}
