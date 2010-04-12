package geogebra3D.euclidian3D.opengl;

import geogebra.main.Application;

import java.awt.Color;

import javax.media.opengl.GL;





public class GeometrySphere extends Geometry {
	

	/** coords of the center */
	private float x, y, z; 
	

	public GeometrySphere(Manager manager) {
		super(manager,NORMAL_ON,TEXTURE_OFF,COLOR_ON);
	}

	public void init() {
		
	}
	
	public int getType(){
		return GL.GL_QUADS;
	}


	public int getNb(){
		return 1;
	}
	
	
	/** create a new sphere
	 * @param x center x-coord
	 * @param y center y-coord
	 * @param z center z-coord
	 * @param radius radius
	 * @param color color
	 * @param alpha alpha
	 * @return index of the new geometry
	 */
	public int create(float x, float y, float z,
			float radius,
			Color color, float alpha){
		
		manager.preInit(this);
		
		manager.startListAndGeometry(this);

		
		float r = color.getRed()/255f;
		float g = color.getGreen()/255f;
		float b = color.getBlue()/255f;
		float a = alpha;
		
		
		// set color
		color(r,g,b,a);
		
		//creates the sphere
		setCenter(x, y, z);
		
		int nb = (int) (10*radius*manager.getView3D().getScale()/100)+5;
		sphere(nb,4*nb,radius);
		//sphere((int) (radius),(int) (4*radius),1);

	
		
		
		manager.endListAndGeometry(this);
		
		return getIndex();
	}
	
	
	private void setCenter(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	protected void vertex(float x, float y, float z){
		//does the translation with center coords
		super.vertex(this.x + x, this.y + y, this.z + z);
	}

	
}
