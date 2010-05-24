package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbVector;
import geogebra.kernel.Kernel;



/** class describing the section of the brush
 * 
 * @author matthieu
 *
 */
public class PlotterBrushSection {


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
	public PlotterBrushSection(GgbVector point, float thickness){
		this(point, thickness, null, null);
	}
	
	/**
	 * first section constructor
	 * @param point
	 * @param thickness
	 * @param clockU 
	 * @param clockV 
	 */
	public PlotterBrushSection(GgbVector point, float thickness, GgbVector clockU, GgbVector clockV){
		this.center = point;
		this.thickness = thickness;
		this.clockU = clockU;
		this.clockV = clockV;
	}	
	
	/**
	 * second section constructor
	 * @param s
	 * @param point
	 * @param thickness
	 * @param updateClock 
	 */
	public PlotterBrushSection(PlotterBrushSection s, GgbVector point, float thickness, boolean updateClock){
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
			double dt = this.thickness-s.thickness;
			if (dt!=0){
				double l = direction.norm();
				double h = Math.sqrt(l*l+dt*dt);
				normalDevD = -dt/h;
				normalDevN = l/h;
			
				//normalDevD = 0.0000; normalDevN = 1;
				
				s.normalDevD = normalDevD;
				s.normalDevN = normalDevN;
				//Application.debug("dt="+dt+",normalDev="+normalDevD+","+normalDevN);
			}
			
			direction.normalize();
			s.direction = direction;
			normal = null;
			s.normal = null;
			
			//calc new clocks				
			if (updateClock){
				GgbVector[] vn = direction.completeOrthonormal();
				s.clockU = vn[0]; s.clockV = vn[1];
			}

		}
		clockU = s.clockU; clockV = s.clockV;
		
		//Application.debug("direction=\n"+direction.toString());
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
				//return new GgbVector[] {vn,pos};
				return new GgbVector[] {(GgbVector) vn.mul(normalDevN).add(direction.mul(normalDevD)),pos};
			}else
				return new GgbVector[] {vn,pos};
	}
	
	
	/**
	 * @return the center of the section
	 */
	public GgbVector getCenter(){
		return center;
	}
	
	
	////////////////////////////////////
	// FOR 3D CURVE
	////////////////////////////////////
	
	
	/**
	 * first section constructor
	 * @param point
	 * @param thickness
	 * @param direction
	 */
	public PlotterBrushSection(GgbVector point, GgbVector direction, float thickness){
		this.center = point;
		this.thickness = thickness;
		this.direction = direction;
		GgbVector[] vn = direction.completeOrthonormal();
		clockU = vn[0]; clockV = vn[1];
		
	}	
	
	/**
	 * first section constructor
	 * @param point
	 * @param thickness
	 * @param direction
	 */
	public PlotterBrushSection(PlotterBrushSection s, GgbVector point, GgbVector direction, float thickness){
		
		this.center = point;
		this.thickness = thickness;
		this.direction = direction;
		
		clockV = direction.crossProduct(s.clockU).normalized(); 
		//normalize it to avoid little errors propagation
		// TODO truncate ?
		clockU = clockV.crossProduct(direction).normalized();
		
	}	
	
}
