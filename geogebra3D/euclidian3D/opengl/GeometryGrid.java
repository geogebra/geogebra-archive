package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;

import java.awt.Color;

import javax.media.opengl.GL;





public class GeometryGrid extends Geometry {
	

	

	public GeometryGrid(Manager manager) {
		super(manager,NORMAL_ON,TEXTURE_ON,COLOR_ON);
	}

	public void init() {}
	
	
	
	public int create(Color color, float alpha, 
			float xmin, float xmax, float ymin, float ymax,
			float dx, float dy, float thickness){
			
		manager.preInit(this);
		
		manager.startList(this);

		
		float r = color.getRed()/255f;
		float g = color.getGreen()/255f;
		float b = color.getBlue()/255f;
		float a = alpha;
		
		
		int latitude = 8;
		int nb = 2;
		
		// along (Ox)
		float[] textureCoords = Textures.linear(nb, thickness*100, xmax-xmin, -xmin, 0.125f);
		int nYmin = (int) (ymin/dy); int nYmax = (int) (ymax/dy);
		
		int i=nYmin;
		manager.startGeometry(this);
		color(r,g,b,a);
		cylinder(new GgbVector(new double[] {xmin,dy*i,0,1}), 
				new GgbVector(new double[] {xmax,dy*i,0,1}),
				latitude, thickness, textureCoords[0], textureCoords[1]);
		manager.endGeometry(this);
		
		GgbVector v = new GgbVector(new double[] {0,dy,0,0});
		i++;
		for (;i<=nYmax;i++){
			manager.startGeometry(this);
			color(r,g,b,a);
			manager.translateCylinder(v);
			cylinder(latitude);
			manager.endGeometry(this);
		}
		
		
		// along (Oy)
		textureCoords = Textures.linear(nb, thickness*100, ymax-ymin, -ymin, 0.125f);
		int nXmin = (int) (xmin/dx); int nXmax = (int) (xmax/dx);
		
		i=nXmin;
		manager.startGeometry(this);
		color(r,g,b,a);
		cylinder(new GgbVector(new double[] {dx*i,ymin,0,1}), 
				new GgbVector(new double[] {dx*i,ymax,0,1}),
				latitude, thickness, textureCoords[0], textureCoords[1]);
		manager.endGeometry(this);
		
		v = new GgbVector(new double[] {dx,0,0,0});
		i++;
		for (;i<=nXmax;i++){
			manager.startGeometry(this);
			color(r,g,b,a);
			manager.translateCylinder(v);
			cylinder(latitude);
			manager.endGeometry(this);
		}

		
		
		
		manager.endList(this);
		
		return getIndex();
	}
	
	public int getType(){
		return GL.GL_QUAD_STRIP;
	}


	public int getNb(){
		return 1;
	}
	
}
