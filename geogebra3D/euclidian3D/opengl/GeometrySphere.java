package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometrySphere extends Geometry {
	

	

	public GeometrySphere(GeometryRenderer geometryRenderer, boolean hasTexture) {
		super(geometryRenderer,NORMAL_ON,hasTexture,COLOR_OFF);
	}

	public void init() {
		
		geometryRenderer.preInit(this);
		
		geometryRenderer.startGeometry(this);
		sphere(8,8,POINT3D_RADIUS);
		geometryRenderer.endGeometry(this);

	
	}
	
	public int getType(){
		return GL.GL_QUADS;
	}


	public int getNb(){
		return 1;
	}
	
}
