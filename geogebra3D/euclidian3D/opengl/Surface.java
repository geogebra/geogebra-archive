package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbVector;
import geogebra3D.kernel3D.GeoFunction2VarInterface;

/** Class for drawing surfaces.
 * @author matthieu
 *
 */
public class Surface {
	

	/** manager */
	private Manager manager;

	/** index */
	private int index;
	
	/** 2-var function */
	private GeoFunction2VarInterface function;
	
	/** domain for plotting */
	private float uMin, uMax, vMin, vMax;
	
	/** delta for plotting */
	private float du, dv;
	
	
	/** default constructor
	 * @param manager
	 */
	public Surface(Manager manager){
		this.manager = manager;
	}
	
	
	////////////////////////////////////
	// START AND END
	////////////////////////////////////
	
	/**
	 * start new surface
	 * @param function 
	 */
	public void start(GeoFunction2VarInterface function){
		index = manager.startNewList();
		this.function = function;
		
	}
	
	
	/** end surface
	 * @return gl index of the surface
	 */
	public int end(){
		manager.endList();
		return index;
	}
	

	////////////////////////////////////
	// DRAWING METHODS
	////////////////////////////////////

	
	/** set domain for u parameter
	 * @param min
	 * @param max
	 */
	public void setU(float min, float max){
		this.uMin = min;
		this.uMax = max;
	}
	
	/** set domain for v parameter
	 * @param min
	 * @param max
	 */
	public void setV(float min, float max){
		this.vMin = min;
		this.vMax = max;
	}	
	
	
	/** set delta plot for u
	 * @param delta
	 */
	public void setDeltaU(float delta){
		this.du = delta;
	}
	
	/** set delta plot for v
	 * @param delta
	 */
	public void setDeltaV(float delta){
		this.dv = delta;
	}	
	
	
	
	
	/** 
	 * draw part of the surface
	 */
	public void draw(){
		manager.startGeometry(Manager.QUADS);
		
		
		
		for (float u=uMin; u<uMax; u+=du){
			for (float v=vMin; v<vMax; v+=dv){
				
				drawNormalAndVertex(u, v);
				drawNormalAndVertex(u+du, v);
				drawNormalAndVertex(u+du, v+dv);
				drawNormalAndVertex(u, v+dv);
				
				/*
				manager.normal(function.evaluateNormal(u, v));
				manager.vertex(function.evaluatePoint(u, v));
				
				manager.normal(function.evaluateNormal(u, v+dv));
				manager.vertex(function.evaluatePoint(u, v+dv));
				

				manager.normal(function.evaluateNormal(u+du, v+dv));
				manager.vertex(function.evaluatePoint(u+du, v+dv));


				manager.normal(function.evaluateNormal(u+du, v));
				manager.vertex(function.evaluatePoint(u+du, v));
				*/
				

			}
		}
		
		manager.endGeometry();
	}
	
	
	
	private void drawNormalAndVertex(float u, float v){
		manager.normal(function.evaluateNormal(u, v));
		manager.vertex(function.evaluatePoint(u, v));
	}
	
	

}
