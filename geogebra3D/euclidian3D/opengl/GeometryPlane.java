package geogebra3D.euclidian3D.opengl;

import java.awt.Color;

import javax.media.opengl.GL;





public class GeometryPlane extends Geometry {
	

	

	public GeometryPlane(Manager manager) {
		super(manager,NORMAL_ON,TEXTURE_OFF,COLOR_ON);
	}

	public void init() {
		
		manager.preInit(this);
		
		manager.startListAndGeometry(this);

		
		float r = 0.5f;
		float g = 0.5f;
		float b = 0.5f;
		float a = 0.25f;
		
		float size = 2f;
		float size2 = 1f;

		normal(0,0,1);
		
		// center
		color(r,g,b,a);
		vertex(size, size, 0);
		vertex(-size, size, 0);
		vertex(-size, -size, 0);
		vertex(size, -size, 0);
		
		// fading edges
		color(r,g,b,a);
		vertex(-size, size, 0);
		vertex(size, size, 0);
		color(r,g,b,0);
		vertex(size+size2, size+size2, 0);
		vertex(-size-size2, size+size2, 0);
	
		color(r,g,b,a);
		vertex(size, size, 0);
		vertex(size, -size, 0);
		color(r,g,b,0);
		vertex(size+size2, -size-size2, 0);
		vertex(size+size2, size+size2, 0);

		color(r,g,b,a);
		vertex(size, -size, 0);
		vertex(-size, -size, 0);
		color(r,g,b,0);
		vertex(-size-size2, -size-size2, 0);
		vertex(size+size2, -size-size2, 0);
	
		color(r,g,b,a);
		vertex(-size, -size, 0);
		vertex(-size, size, 0);
		color(r,g,b,0);
		vertex(-size-size2, size+size2, 0);
		vertex(-size-size2, -size-size2, 0);
	
		
		
		manager.endListAndGeometry();

	
	}
	
	
	
	public int create(Color color, float alpha, float size){
			
		manager.preInit(this);
		
		manager.startListAndGeometry(this);

		
		float r = color.getRed()/255f;
		float g = color.getGreen()/255f;
		float b = color.getBlue()/255f;
		float a = alpha;
		
		float size2 = size/2f;

		normal(0,0,1);
		
		// center
		color(r,g,b,a);
		vertex(size, size, 0);
		vertex(-size, size, 0);
		vertex(-size, -size, 0);
		vertex(size, -size, 0);
		
		// fading edges
		color(r,g,b,a);
		vertex(-size, size, 0);
		vertex(size, size, 0);
		color(r,g,b,0);
		vertex(size+size2, size+size2, 0);
		vertex(-size-size2, size+size2, 0);
	
		color(r,g,b,a);
		vertex(size, size, 0);
		vertex(size, -size, 0);
		color(r,g,b,0);
		vertex(size+size2, -size-size2, 0);
		vertex(size+size2, size+size2, 0);

		color(r,g,b,a);
		vertex(size, -size, 0);
		vertex(-size, -size, 0);
		color(r,g,b,0);
		vertex(-size-size2, -size-size2, 0);
		vertex(size+size2, -size-size2, 0);
	
		color(r,g,b,a);
		vertex(-size, -size, 0);
		vertex(-size, size, 0);
		color(r,g,b,0);
		vertex(-size-size2, size+size2, 0);
		vertex(-size-size2, -size-size2, 0);
	
		
		
		manager.endListAndGeometry();
		
		return getIndex();
	}
	
	public int getType(){
		return GL.GL_QUADS;
	}


	public int getNb(){
		return 1;
	}
	
}
