package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometryCone extends Geometry {
	

	

	public GeometryCone(Manager manager, boolean hasTexture) {
		super(manager,NORMAL_ON,hasTexture,COLOR_OFF);
	}

	public void init() {
		
		manager.preInit(this);
		
		
		manager.startListAndGeometry(this);
		cone(8,LINE3D_THICKNESS);
		manager.endListAndGeometry();
		
		
	}
	
	public int getType(){
		return GL.GL_QUADS;
	}

	
	public int getNb(){
		return 1;
	}
	
}
