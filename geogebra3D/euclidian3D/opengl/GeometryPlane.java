package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometryPlane extends Geometry {
	

	

	public GeometryPlane(GeometryRenderer geometryRenderer) {
		super(geometryRenderer,NORMAL_ON,TEXTURE_OFF,COLOR_OFF);
	}

	public void init() {
		
		geometryRenderer.preInit(this);
		
		geometryRenderer.startGeometry(this);

		float size = 2.25f;
		vertex(size, size, 0);
		vertex(-size, size, 0);
		vertex(-size, -size, 0);
		vertex(size, -size, 0);


		
		geometryRenderer.endGeometry(this);

	
	}
	
	public int getType(){
		return GL.GL_QUADS;
	}


	public int getNb(){
		return 1;
	}
	
}
