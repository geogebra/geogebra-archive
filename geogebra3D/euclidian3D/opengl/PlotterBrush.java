package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbVector;
import geogebra.Matrix.GgbVector3D;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra3D.euclidian3D.CurveTree;
import geogebra3D.kernel3D.GeoCurveCartesian3DInterface;

import java.awt.Color;

/**
 * 3D brush, drawing circular-section curves.
 * 
 * @author mathieu
 *
 */
public class PlotterBrush {

	/** thickness for drawing 3D lines*/
	public static final float LINE3D_THICKNESS = 0.5f;

	
	/** manager */
	private Manager manager;
	
	/** index */
	private int index;
	
	/** start and end sections*/
	private PlotterBrushSection start, end;
	
	/** current thickness */
	private float thickness;
	private int lineThickness;
	
	/** view scale */
	private float scale;
	
	/** global length of the curve */
	private float length;
	
	
	//color
	/** color r, g, b, a */
	private float red, green, blue, alpha;
	/** says if it's colored */
	private boolean hasColor;
	
	
	//texture
	/** start and end textures values */
	private float texturePosZero, textureValZero;
	/** textures coords */
	private float[] textureX = new float[2];
	private float[] textureY = new float[2];
	/** type of texture */
	static final private int TEXTURE_CONSTANT_0 = 0; 
	static final private int TEXTURE_ID = 1;
	static final private int TEXTURE_AFFINE = 2;
	static final private int TEXTURE_LINEAR = 3;
	private int textureTypeX = TEXTURE_ID;
	private int textureTypeY = TEXTURE_CONSTANT_0;
	
	static final private float TEXTURE_AFFINE_FACTOR = 0.05f; 
	
	// arrows	
	/** no arrows */
	static final public int ARROW_TYPE_NONE=0;
	/** simple arrows */
	static final public int ARROW_TYPE_SIMPLE=1;
	private int arrowType=ARROW_TYPE_NONE;
	/** length and width of the arrow */
	static private float ARROW_LENGTH = 3f;
	static private float ARROW_WIDTH = ARROW_LENGTH/4f;
	
	
	// ticks	
	/** no ticks */
	static final public boolean TICKS_OFF=false;
	/** with ticks */
	static final public boolean TICKS_ON=true;
	/** has ticks ? */
	private boolean ticks = TICKS_OFF;
	/** distance between two ticks */
	private float ticksDistance;
	/** offset for origin of the ticks (0: start of the curve, 1: end of the curve) */
	private float ticksOffset;
	
	
	//for GeoCartesianCurve
	/** curve */
	GeoCurveCartesian3DInterface curve;
	
	//level of detail
	/** number of rules */
	private int latitude;
	
	
	/** default constructor
	 * @param manager
	 */
	public PlotterBrush(Manager manager){
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
		hasColor = false;
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
	 */
	public void down(GgbVector point){
		
		down(point,null,null);
	}
	
	/** start new curve part
	 * @param point
	 */
	private void down(GgbVector point, GgbVector clockU, GgbVector clockV){
		
		start = new PlotterBrushSection(point,thickness, clockU, clockV);
		end = null;
	}
	
	/** move to point and draw curve part
	 * @param point
	 */
	public void moveTo(GgbVector point){
		// update start and end sections
		if (end==null){
			end = new PlotterBrushSection(start, point, thickness,true);
		}else{
			start = end;
			end = new PlotterBrushSection(start, point, thickness,false);
		}
		
		join();
	}
	
	
	/** move to point and draw curve part
	 * @param point
	 */
	private void moveTo(GgbVector point, GgbVector clockU, GgbVector clockV){

		if (end!=null)
			start = end;
		
		end = new PlotterBrushSection(point, thickness, clockU, clockV);
		
		join();
	}	
	
	/**
	 * join start to end
	 */
	public void join(){
		
		// draw curve part
		manager.startGeometry(Manager.QUAD_STRIP);
		if(hasColor)
			manager.color(red, green, blue, alpha);
		//manager.startGeometry(Manager.TRIANGLE_STRIP);
    	float dt = (float) 1/latitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	float u, v;    	
    	for( int i = 0; i <= latitude  ; i++ ) { 
    		u = (float) Math.sin ( i * da ); 
    		v = (float) Math.cos ( i * da ); 
    		setTextureY(i*dt);
    		//Application.debug("i="+i);
    		draw(start,u, v, 0); //bottom of the tube rule
    		draw(end,u, v, 1); //top of the tube rule
    	}
    	
		manager.endGeometry();
	}
	
	
	
	/** draws a section point
	 * 
	 */
	private void draw(PlotterBrushSection s, double u, double v, int texture){
		
		GgbVector[] vectors = s.getNormalAndPosition(u, v);
		
		//Application.debug(vectors[0].toString());
		
		/*
		manager.normal(
				(float) vectors[0].getX(), 
				(float) vectors[0].getY(), 
				(float) vectors[0].getZ());	*/
		manager.normal(vectors[0]);
		manager.texture(
				getTexture(textureX[texture],textureTypeX),
				getTexture(textureY[texture],textureTypeY));
		manager.vertex(vectors[1]);
		/*
		manager.vertex(
				(float) vectors[1].getX(), 
				(float) vectors[1].getY(), 
				(float) vectors[1].getZ());		
				*/
		
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
			setTextureX(0,1);
			moveTo(p2);
			break;
		case ARROW_TYPE_SIMPLE:
			float factor = (12+lineThickness)*LINE3D_THICKNESS/scale;
			float arrowPos = ARROW_LENGTH/length * factor;
			GgbVector arrowBase = (GgbVector) start.getCenter().mul(arrowPos).add(p2.mul(1-arrowPos));
			
			setTextureX(0);
			if (hasTicks()){
				GgbVector d = p2.sub(p1).normalized();
				float thickness = this.thickness;
				
				float i = ticksOffset*length-((int) (ticksOffset*length/ticksDistance))*ticksDistance;
				float ticksDelta = thickness;
				float ticksThickness = 4*thickness;
				if (i<=ticksDelta)
					i+=ticksDistance;

				for(;i<=length*(1-arrowPos);i+=ticksDistance){
					
					GgbVector p1b=(GgbVector) p1.add(d.mul(i-ticksDelta));
					GgbVector p2b=(GgbVector) p1.add(d.mul(i+ticksDelta));
					
					setTextureType(TEXTURE_AFFINE);
					setTextureX(i/length);
					moveTo(p1b);
					setThickness(ticksThickness);
					setTextureType(TEXTURE_CONSTANT_0);
					moveTo(p1b);
					moveTo(p2b);
					setThickness(thickness);
					moveTo(p2b);
					
				}
			}
			
			setTextureType(TEXTURE_AFFINE);
			setTextureX(1-arrowPos);
			moveTo(arrowBase);
			
			
			textureTypeX = TEXTURE_ID;
			setTextureX(0,0);
			setThickness(factor*ARROW_WIDTH);
			moveTo(arrowBase);
			setThickness(0);
			moveTo(p2);
			break;
		}
	}
	
	
	/** draws a circle
	 * @param center
	 * @param v1
	 * @param v2
	 * @param radius
	 */
	public void circle(GgbVector center, GgbVector v1, GgbVector v2, double radius){
		
		
		length=(float) (2*Math.PI*radius); //TODO use integer to avoid bad dash cycle connection
		
		int longitude = 60;

		
		GgbVector vn1;
		GgbVector vn2 = v1.crossProduct(v2);
		
    	float dt = (float) 1/longitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	float u=0, v=1;
    	
    	setTextureX(0);
		vn1 = (GgbVector) v1.mul(u).add(v2.mul(v));
		down((GgbVector) center.add(vn1.mul(radius)),vn1,vn2);  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.sin ( i * da ); 
    		v = (float) Math.cos ( i * da ); 
    		
    		setTextureX(i*dt);
    		vn1 = (GgbVector) v1.mul(u).add(v2.mul(v));
    		moveTo((GgbVector) center.add(vn1.mul(radius)),vn1,vn2);
    	} 
    	
	}

	
	////////////////////////////////////
	// 3D CURVE DRAWING METHODS
	////////////////////////////////////
	
	private boolean firstCurvePoint = false;
	private GgbVector previousPosition;
	private GgbVector previousTangent;
	
	/**
	 * Starts drawing a curve
	 */
	private void startDrawingCurve(){
		firstCurvePoint = true;
		setTextureType(PlotterBrush.TEXTURE_LINEAR);
	}
	
	/** adds the point with the specified position and tangent to the curve currently being drawn.
	 * @param position
	 * @param tangent
	 */
	public void addPointToCurve(GgbVector position, GgbVector tangent){
		if(firstCurvePoint){
			end = new PlotterBrushSection(position, tangent, thickness);
			firstCurvePoint=false;
		}
		else {
			if(discontinuityPassed(position)) {
				startDrawingCurve();				//start drawing a new segment
				addPointToCurve(position,tangent);
				return;
			} else {
				start = end;
				end = new PlotterBrushSection(start,position,tangent,thickness);
	
				setTextureX(1);
				join();
			}
		}
		previousPosition = position;
		previousTangent  = tangent;
	}
	
	/** adds the point with the specified position and tangent to the curve currently being drawn.
	 * @param p the point's position vector
	 * @param t the tangent at the point
	 */
	public void addPointToCurve3D(GgbVector3D p, GgbVector3D t){
		GgbVector position = new GgbVector(p.getX(),p.getY(),p.getZ(),0);
		GgbVector tangent = new GgbVector(t.getX(),t.getY(),t.getZ(),0);
		if(firstCurvePoint){
			end = new PlotterBrushSection(position, tangent, thickness);
			firstCurvePoint=false;
		}
		else {
			if(discontinuityPassed(position)) {
				startDrawingCurve();				//start drawing a new segment
				addPointToCurve(position,tangent);
				return;
			} else {
				start = end;
				end = new PlotterBrushSection(start,position,tangent,thickness);
	
				setTextureX(1);
				join();
			}
		}
		previousPosition = position;
		previousTangent  = tangent;
	}
	
	/** A test used to judge if the curve has passed over a discontinuity since
	 *  the last point was added.
	 * @param position the position of the new point (pos2)
	 * @return true iff (pos2-pos1)/||pos2-pos1|| . tangent1 < CurveTree.discontinuityThreshold
	 */
	private boolean discontinuityPassed(GgbVector position) {
		GgbVector dir = position.sub(previousPosition).normalized();
		
		if(dir.dotproduct(previousTangent)<CurveTree.discontinuityThreshold)
			return true;
		return false;
	}
	
	/** draws the curve defined by tree, in the viewing volume of a sphere
	 *  with radius r centered at the origin
	 * @param tree
	 * @param radius the radius of a sphere bounding the viewing volume
	 */
	public void draw(CurveTree tree, double radius){
		
		tree.setRadius(radius);
		
		startDrawingCurve();
		
		//draw the start point if visible and defined
		tree.drawStartPointIfVisible(this);
		
		tree.beginRefinement(this);
		
		//draw the end point if visible and defined
		tree.drawEndPointIfVisible(this);
	}
	
	////////////////////////////////////
	// THICKNESS
	////////////////////////////////////

	/** set the current thickness of the brush, using integer for thickness (see {@link GeoElement#getLineThickness()}}
	 * @param thickness
	 * @param scale 
	 */
	public void setThickness(int thickness, float scale){
		
		this.lineThickness = thickness;
		this.scale = scale;
		
		setThickness(lineThickness*LINE3D_THICKNESS/scale);
		
	}
	
	/** set the current thickness of the brush
	 * @param thickness
	 */
	public void setThickness(float thickness){
		this.thickness = thickness;
	}
	
	
	////////////////////////////////////
	// COLOR
	////////////////////////////////////

	/** sets the current color
	 * @param color
	 * @param alpha
	 */
	public void setColor(Color color, float alpha){
		this.red = color.getRed()/255f;
		this.green = color.getGreen()/255f;
		this.blue = color.getBlue()/255f;
		this.alpha = alpha;
		hasColor = true;
	}
	
	/** sets the current color (alpha set to 1)
	 * @param color
	 */
	public void setColor(Color color){
		setColor(color,1);
	}
	
	

	////////////////////////////////////
	// TEXTURE
	////////////////////////////////////
	

	
	/** set affine texture zero position
	 * @param posZero position of the "center" of the cylinder
	 * @param valZero texture coord for the "center"
	 */	
	public void setAffineTexture(float posZero, float valZero){

		//maxima : f(x):=a*x+b;solve([f(posZero)=valZero,f(unit)-f(0)=n],[a,b]);

		texturePosZero = posZero;
		textureValZero = valZero;
		setTextureType(TEXTURE_AFFINE);
	}
	
	
	/** sets the type of texture
	 * @param type
	 */
	public void setTextureType(int type){
		textureTypeX = type;
	}

	/** return texture coord regarding position and type
	 * @param pos
	 * @return texture coord
	 */
	private float getTexture(float pos, int textureType){
		switch(textureType){
		case TEXTURE_ID:
		default:
			return pos;
		case TEXTURE_CONSTANT_0:
			return 0f;
		case TEXTURE_AFFINE:
			//float factor = (int) (TEXTURE_AFFINE_FACTOR*length*scale); //TODO integer for cycles
			float factor =  (TEXTURE_AFFINE_FACTOR*length*scale);
			return factor*(pos-texturePosZero)+textureValZero;
		case TEXTURE_LINEAR:
			return TEXTURE_AFFINE_FACTOR*scale*pos;

		}
	}
	
	private void setTextureX(float x0, float x1){
		this.textureX[0] = x0;
		this.textureX[1] = x1;
	}
	
	private void setTextureX(float x){
		setTextureX(textureX[1],x);
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
	
	////////////////////////////////////
	// TICKS
	////////////////////////////////////
	
    /**
     * sets the type of arrow used by the pencil.
     * @param ticks 
     */
    public void setTicks(boolean ticks){
    	this.ticks = ticks;
    } 
    
    /**
     * sets the distance between two ticks
     * @param distance
     */
    public void setTicksDistance(float distance){
    	this.ticksDistance = distance;
    }
    
    /**
     * sets the offset for origin of the ticks (0: start of the curve, 1: end of the curve)
     * @param offset
     */
    public void setTicksOffset(float offset){
    	this.ticksOffset = offset;
    }
    
    /**
     * @return true if it draws ticks
     */
    public boolean hasTicks(){
    	return ticks && (ticksDistance>0);
    }
	

}
