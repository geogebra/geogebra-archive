package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbVector;

import java.awt.Color;

import javax.media.opengl.GL;





/**
 * Class for drawing segments
 * @author mathieu
 *
 */
public class GeometrySegment extends Geometry {
	

	

	public GeometrySegment(Manager manager) {
		super(manager,NORMAL_ON,TEXTURE_ON,COLOR_ON);
	}

	public void init() {}
	
	
	
	public int create(Color color, 
			GgbVector p1, GgbVector p2,
			float thickness, float scale,
			float posZero){
			
		manager.preInit(this);
		
		manager.startList(this);

		
		float r = color.getRed()/255f;
		float g = color.getGreen()/255f;
		float b = color.getBlue()/255f;
		float a = 1f;
		
		
		int latitude = 8;
		int nb = 4;
		
		float[] textureCoords = Textures.linear(nb, 100/scale, (float) p2.distance(p1), 
				posZero, 0.125f);

		manager.startGeometry(this);
		color(r,g,b,a);
		cylinder(p1,p2,
				latitude, thickness/scale*LINE3D_THICKNESS, textureCoords[0], textureCoords[1]);
		manager.endGeometry(this);
		
		
		
		
		
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
