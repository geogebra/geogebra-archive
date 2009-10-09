package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometryCylinder extends Geometry {
	
	/** radius for drawing 3D points*/
	private static final float POINT3D_RADIUS = 1.4f;	

	

	public GeometryCylinder(GeometryRenderer geometryRenderer) {
		super(geometryRenderer);
	}

	public void init() {
		
		geometryRenderer.preInit(this);
		
		cylinder(8,LINE3D_THICKNESS);
		
		geometryRenderer.postInit();

	}
	
	public int getType(){
		return GL.GL_QUADS;
	}

	
}
