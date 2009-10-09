package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometrySphere extends Geometry {
	

	

	public GeometrySphere(GeometryRenderer geometryRenderer) {
		super(geometryRenderer);
	}

	public void init() {
		
		geometryRenderer.preInit(this);
		
		sphere(8,8,POINT3D_RADIUS);
		
		geometryRenderer.postInit();

	}
	
	public int getType(){
		return GL.GL_QUADS;
	}


	
}
