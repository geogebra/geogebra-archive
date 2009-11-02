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
	static final int TYPE_GLLIST = 1;
	
	private GeometryRenderer geometryRenderer;
	
	/** geometry : point */
	public GeometrySphere point;
	/** geometry : cylinder */
	public GeometryCylinder cylinder;
	/** geometry : cone */
	public GeometryCone cone;
	/** geometry : cursor */
	public GeometryCursor cursor;
	/** geometry : plane */
	public GeometryPlane plane;
	
	
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
		case TYPE_GLLIST:
			geometryRenderer = new GeometryRendererGLList(gl);
			break;	
		}
		
		// creating geometries
		point = new GeometrySphere(geometryRenderer,false);
		cylinder = new GeometryCylinder(geometryRenderer,true);
		cone = new GeometryCone(geometryRenderer,true);
		cursor = new GeometryCursor(geometryRenderer);
		plane = new GeometryPlane(geometryRenderer);
		
		
	}

}
