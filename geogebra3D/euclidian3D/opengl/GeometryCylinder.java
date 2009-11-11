package geogebra3D.euclidian3D.opengl;

import javax.media.opengl.GL;





public class GeometryCylinder extends Geometry {
	
	

	public GeometryCylinder(Manager manager, boolean hasTexture) {
		super(manager,NORMAL_ON,hasTexture,COLOR_OFF);
	}

	public void init() {
		
		manager.preInit(this);
		
		manager.startGeometry(this);
		cylinder(8,LINE3D_THICKNESS);
		manager.endGeometry(this);

	
	}
	
	public int getType(){
		return GL.GL_QUADS;
	}

	
	public int getNb(){
		return 1;
	}
	
}
