package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbVector;

/**
 * 3D brush, drawing circular-section curves.
 * 
 * @author mathieu
 *
 */
public class Brush {
	
	// private class describing a section
	private class Section{
		/** center and clock vectors */
		private GgbVector center, clockU, clockV;
		
		/** thickness = radius of the section */
		private float thickness;
		
		/**
		 * first section constructor
		 * @param point
		 * @param thickness
		 */
		public Section(GgbVector point, float thickness){
			this.center = point;
			this.thickness = thickness;
		}
		
		/**
		 * second section constructor
		 * @param s
		 * @param point
		 * @param thickness
		 */
		public Section(Section s, GgbVector point, float thickness){
			this(point,thickness);
			GgbVector[] vn = center.sub(s.center).completeOrthonormal();
			clockU = vn[0]; clockV = vn[1];
			s.clockU = clockU; s.clockV = clockV;
		}
		
		/**
		 * return the normal vector for parameters u,v
		 * @param u
		 * @param v
		 * @return the normal vector
		 */
		public GgbVector[] getNormalAndPosition(double u, double v){
			GgbVector vn = (GgbVector) clockV.mul(v).add(clockU.mul(u));
			GgbVector pos = (GgbVector) vn.mul(thickness).add(center);
			return new GgbVector[] {vn,pos};
		}

	}
	
	
	/** thickness for drawing 3D lines*/
	public static final float LINE3D_THICKNESS = 0.3f;

	
	/** manager */
	private Manager manager;
	
	/** index */
	private int index;
	
	/** start and end sections*/
	private Section start, end;
	
	//texture
	/** start and end textures values */
	private float textureStart, textureEnd;
	/** type of texture */
	private int textureType;
	static final private int TEXTURE_LINEAR_ONCE = 0; 
	
	//level of detail
	/** number of rules */
	private int latitude;
	
	
	/** default constructor
	 * @param manager
	 */
	public Brush(Manager manager){
		this.manager = manager;
	}


	////////////////////////////////////
	// START AND END
	////////////////////////////////////
	
	/**
	 * start new curve
	 * @param latitude number of rules
	 */
	public void start(int latitude){
		index = manager.startNewList();
		this.latitude = latitude;
		
	}
	
	
	/** end curve
	 * @return gl index of the curve
	 */
	public int end(){
		manager.endList();
		return index;
	}
	
	////////////////////////////////////
	// SIMPLE DRAWING METHODS
	////////////////////////////////////

	/** start new curve part
	 * @param point
	 * @param thickness
	 */
	public void down(GgbVector point, float thickness){
		
		start = new Section(point,thickness);
		end = null;
	}
	
	/** move to point and draw curve part
	 * @param point
	 * @param thickness
	 */
	public void moveTo(GgbVector point, float thickness){
		// update start and end sections
		if (end==null){
			end = new Section(start, point, thickness);
		}
		
		// draw curve part
		manager.startGeometry(Manager.QUAD_STRIP);
    	float dt = (float) 1/latitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	float u, v;    	
    	for( int i = 0; i <= latitude  ; i++ ) { 
    		u = (float) Math.sin ( i * da ); 
    		v = (float) Math.cos ( i * da ); 
    		draw(start,u, v, getTextureX(0),i*dt); //bottom of the tube rule
    		draw(end,u, v, getTextureX(1),i*dt); //top of the tube rule
    	} 

		manager.endGeometry();
	}
	
	
	/** draws a section point
	 * 
	 */
	private void draw(Section s, double u, double v, float textureX, float textureY){
		
		GgbVector[] vectors = s.getNormalAndPosition(u, v);
		manager.normal((float) vectors[0].getX(), 
				(float) vectors[0].getY(), 
				(float) vectors[0].getZ());		
		manager.texture(textureX,textureY);
		manager.vertex((float) vectors[1].getX(), 
				(float) vectors[1].getY(), 
				(float) vectors[1].getZ());		
		
	}
	
	

	////////////////////////////////////
	// TEXTURE
	////////////////////////////////////
	
	/** sets a "once-part-curve" texture
	 * @param start
	 * @param end
	 */
	public void setLinearOnceTexture(float start, float end){
		textureStart = start;
		textureEnd = end;
		textureType = TEXTURE_LINEAR_ONCE;
	}
	
	/** calculate texture x coords for 0 and 1 positions
	 * @param n number of repetitions per unit
	 * @param unit
	 * @param length of the cylinder
	 * @param posZero position of the "center" of the cylinder
	 * @param valZero texture coord for the "center"
	 */	
	public void setLinearOnceTexture(int n, float unit, float length, float posZero, float valZero){

		//maxima : f(x):=a*x+b;solve([f(posZero/length)=0.25,f(unit/length)-f(0)=n],[a,b]);
		float a, b;
		a=(length*n)/unit;
		b=(unit*valZero-n*posZero)/unit;
		float start = b;
		float end = a+b;
		setLinearOnceTexture(start, end);
	}

	/** return texture x coord regarding position
	 * @param pos
	 * @return texture x coord regarding position
	 */
	private float getTextureX(float pos){
		switch(textureType){
		case TEXTURE_LINEAR_ONCE:
		default:
			return textureStart*(1f-pos)+textureEnd*pos;

		}
	}

	

}
