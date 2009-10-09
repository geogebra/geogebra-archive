package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometryCylinder extends Geometry {
	
	/** radius for drawing 3D points*/
	private static final float POINT3D_RADIUS = 1.4f;	

	

	public GeometryCylinder(GeometryRenderer geometryRenderer) {
		super(geometryRenderer);
	}

	public void init() {
		
		geometryRenderer.preInit(GL.GL_QUADS);
		
		cylinder(8,LINE3D_THICKNESS);
		
		geometryRenderer.postInit();

	}

	
}
