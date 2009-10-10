package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometryCone extends Geometry {
	

	

	public GeometryCone(GeometryRenderer geometryRenderer) {
		super(geometryRenderer);
	}

	public void init() {
		
		geometryRenderer.preInit(this);
		
		cone(8,LINE3D_THICKNESS);
		
		geometryRenderer.postInit();

	}
	
	public int getType(){
		return GL.GL_QUADS;
	}

	
}
