package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometryPoint extends Geometry {
	

	

	public GeometryPoint(Manager manager, boolean hasTexture) {
		super(manager,NORMAL_ON,hasTexture,COLOR_OFF);
	}

	public void init() {
		
		manager.preInit(this);
		
		manager.startListAndGeometry(this);
		sphere(2,6,POINT3D_RADIUS);
		manager.endListAndGeometry();

	
	}
	
	public int getType(){
		return GL.GL_QUADS;
	}


	public int getNb(){
		return 1;
	}
	
}
