package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometryCone extends Geometry {
	

	

	public GeometryCone(GeometryRenderer geometryRenderer, boolean hasTexture) {
		super(geometryRenderer,NORMAL_ON,hasTexture,COLOR_OFF);
	}

	public void init() {
		
		geometryRenderer.preInit(this);
		
		
		geometryRenderer.startGeometry(this);
		cone(8,LINE3D_THICKNESS);
		geometryRenderer.endGeometry(this);
		
		
	}
	
	public int getType(){
		return GL.GL_QUADS;
	}

	
	public int getNb(){
		return 1;
	}
	
}
