package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

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
		
		/** direction from last point */
		private GgbVector direction;
		
		/** normal (for caps) */
		private GgbVector normal = null;
		
		/** normal deviation along direction */
		private double normalDevD = 0;
		private double normalDevN = 1;
		
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
			
			direction = center.sub(s.center);

			if (center.equalsForKernel(s.center, Kernel.STANDARD_PRECISION)){
				if (this.thickness<s.thickness)
					normal = s.direction;
				else 
					normal = (GgbVector) s.direction.mul(-1);
				s.normal = normal;
				//keep last direction
				direction = s.direction;
			}else{
				//calc normal deviation
				double dt = thickness-s.thickness;
				if (dt!=0){
					double l = direction.norm();
					double h = Math.sqrt(l*l+dt*dt);
					normalDevD = -dt/h;
					normalDevN = l/h;
				
					//normalDevD = 0.0000; normalDevN = 1;
					
					s.normalDevD = normalDevD;
					s.normalDevN = normalDevN;
					//Application.debug("normalDev="+normalDevD+","+normalDevN);
				}
				
				//calc new clocks				
				direction.normalize();
				s.direction = direction;
				normal = null;
				s.normal = null;
				GgbVector[] vn = direction.completeOrthonormal();
				//if (s.clockU==null){
					s.clockU = vn[0]; s.clockV = vn[1];
				//}
			}
			clockU = s.clockU; clockV = s.clockV;
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
			if (normal!=null)
				return new GgbVector[] {normal,pos};
			else
				if (normalDevD!=0){
					//Application.debug("normalDev="+normalDevD+","+normalDevN);
					return new GgbVector[] {(GgbVector) vn.mul(normalDevN).add(direction.mul(normalDevD)),pos};
				}else
					return new GgbVector[] {vn,pos};
		}

	}
	
	
	/** thickness for drawing 3D lines*/
	public static final float LINE3D_THICKNESS = 0.5f;

	
	/** manager */
	private Manager manager;
	
	/** index */
	private int index;
	
	/** start and end sections*/
	private Section start, end;
	
	/** current thickness */
	private float thickness;
	private int lineThickness;
	
	/** view scale */
	private float scale;
	
	/** global length of the curve */
	private float length;
	
	
	//texture
	/** start and end textures values */
	private float textureStart, textureEnd;
	/** textures coords */
	private float[] textureX = new float[2];
	private float[] textureY = new float[2];
	/** type of texture */
	private int textureType;
	static final private int TEXTURE_LINEAR_NO_TRANSF = 0; 
	static final private int TEXTURE_LINEAR_ONCE = 1; 
	
	// arrows	
	/** no arrows */
	static final public int ARROW_TYPE_NONE=0;
	/** simple arrows */
	static final public int ARROW_TYPE_SIMPLE=1;
	private int arrowType=ARROW_TYPE_NONE;
	/** length and width of the arrow */
	static private float ARROW_LENGTH = 30f;
	static private float ARROW_WIDTH = 8f;
	
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
	public void down(GgbVector point){
		
		start = new Section(point,thickness);
		end = null;
	}
	
	/** move to point and draw curve part
	 * @param point
	 * @param thickness
	 */
	public void moveTo(GgbVector point){
		// update start and end sections
		if (end==null){
			end = new Section(start, point, thickness);
		}else{
			start = end;
			end = new Section(start, point, thickness);
		}
		
		// draw curve part
		manager.startGeometry(Manager.QUAD_STRIP);
		//manager.startGeometry(Manager.TRIANGLE_STRIP);
    	float dt = (float) 1/latitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	float u, v;    	
    	for( int i = 0; i <= latitude  ; i++ ) { 
    		u = (float) Math.sin ( i * da ); 
    		v = (float) Math.cos ( i * da ); 
    		setTextureY(i*dt);
    		draw(start,u, v, 0); //bottom of the tube rule
    		draw(end,u, v, 1); //top of the tube rule
    	} 

		manager.endGeometry();
	}
	
	
	
	/** draws a section point
	 * 
	 */
	private void draw(Section s, double u, double v, int texture){
		
		GgbVector[] vectors = s.getNormalAndPosition(u, v);
		manager.normal(
				(float) vectors[0].getX(), 
				(float) vectors[0].getY(), 
				(float) vectors[0].getZ());		
		manager.texture(getTextureX(textureX[texture]),textureY[texture]);
		manager.vertex(
				(float) vectors[1].getX(), 
				(float) vectors[1].getY(), 
				(float) vectors[1].getZ());		
		
	}
	


	////////////////////////////////////
	// GEOMETRY DRAWING METHODS
	////////////////////////////////////
	
	/** segment curve
	 * @param p1 
	 * @param p2
	 */
	public void segment(GgbVector p1, GgbVector p2){
		
		length = (float) p1.distance(p2);
		if (Kernel.isEqual(length, 0, Kernel.STANDARD_PRECISION))
			return;
		
		down(p1);
		
		switch(arrowType){
		case ARROW_TYPE_NONE:
		default:
			setTextureX(0, 1);
			moveTo(p2);
			break;
		case ARROW_TYPE_SIMPLE:
			float arrowPos = ARROW_LENGTH/length * thickness;
			GgbVector arrowBase = (GgbVector) start.center.mul(arrowPos).add(p2.mul(1-arrowPos));
			setTextureX(0, 0.5f);
			moveTo(arrowBase);
			textureType = TEXTURE_LINEAR_NO_TRANSF;
			setTextureX(0, 0);
			setThickness(thickness*ARROW_WIDTH);
			moveTo(arrowBase);
			setThickness(0);
			moveTo(p2);
			break;
		}
		
	}
	
	////////////////////////////////////
	// THICKNESS
	////////////////////////////////////

	/** set the current thickness of the brush
	 * @param thickness
	 * @param scale 
	 */
	public void setThickness(int thickness, float scale){
		
		this.lineThickness = thickness;
		this.scale = scale;
		
		setThickness(lineThickness*LINE3D_THICKNESS/scale);
		
	}
	
	public void setThickness(float thickness){
		this.thickness = thickness;
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
		case TEXTURE_LINEAR_NO_TRANSF:
		default:
			return pos;
		case TEXTURE_LINEAR_ONCE:
			return textureStart*(1f-pos)+textureEnd*pos;

		}
	}
	
	private void setTextureX(float x1, float x2){
		this.textureX[0] = x1;
		this.textureX[1] = x2;
	}
	
	private void setTextureY(float y1){
		this.textureY[0] = y1;
		this.textureY[1] = y1;
	}

	
	
	
	
	////////////////////////////////////
	// ARROWS
	////////////////////////////////////
	
    /**
     * sets the type of arrow used by the pencil.
     * @param arrowType type of arrow, see {@link #ARROW_TYPE_NONE}, {@link #ARROW_TYPE_SIMPLE}, ... 
     */
    public void setArrowType(int arrowType){
    	this.arrowType = arrowType;
    } 
	

}
