package geogebra3D.euclidian3D.opengl;

import geogebra.Matrix.GgbVector;
import geogebra.main.Application;
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
	
	
	/** fading value */
	private float uFade, vFade;
	
	/** texture coords */
	private int uT0, uT1, vT0, vT1;
	
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
		uFade = 0; vFade = 0;
		uT0 = 0; uT1 = 0; vT0 = 0; vT1 = 0;
		
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
	
	
	
	/** set fading frontiers
	 * @param u
	 * @param v
	 */
	public void setFading(float u, float v){
		this.uFade = u;
		this.vFade = v;
	}
	
	
	/** 
	 * draw part of the surface
	 */
	public void draw(){
		manager.startGeometry(Manager.QUADS);
		
		
		float du = this.du, dv = this.dv;
			
		for (float u=uMin; u<uMax; ){

			if (uFade!=0){
				if (u==uMin){
					uT0 = 1; uT1 = 0;
					this.du = uFade;
				}else if (u>=uMax-uFade){
					uT0 = 0; uT1 = 1;
					this.du = uMax - u;
				}else{
					uT0 = 0; uT1 = 0;
					if (u+du>uMax-uFade)
						this.du = uMax-uFade - u;
					else
						this.du = du;
				}
			}
			
			for (float v=vMin; v<vMax; ){			

				if (vFade!=0){
					if (v==vMin){
						vT0 = 1; vT1 = 0;
						this.dv = vFade;
					}else if (v>=vMax-vFade){
						vT0 = 0; vT1 = 1;
						this.dv = vMax - v;
					}else{
						vT0 = 0; vT1 = 0;
						if (v+dv>vMax-vFade)
							this.dv = vMax-vFade - v;
						else
							this.dv = dv;
					}
				}
				
				drawQuad(u, v);
				
				
				
				v+=this.dv;

				
			}
			
			u+=this.du;
		}
		
		manager.endGeometry();
	}
	
	private void drawQuad(float u, float v){

		manager.texture(uT0, vT0);
		drawNormalAndVertex(u, v);
		manager.texture(uT1, vT0);
		drawNormalAndVertex(u+du, v);
		manager.texture(uT1, vT1);
		drawNormalAndVertex(u+du, v+dv);
		manager.texture(uT0, vT1);
		drawNormalAndVertex(u, v+dv);
		
	}
	
	private void drawNormalAndVertex(float u, float v){
		manager.normal(function.evaluateNormal(u, v));
		manager.vertex(function.evaluatePoint(u, v));
	}
	
	

}
