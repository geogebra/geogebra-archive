package geogebra3D.euclidian3D.opengl;

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
	
	/** number of plotting */
	private int uNb, vNb ;
	
	/** delta for plotting */
	private float du, dv;
	
	/** fading value */
	private float uFade, vFade;
	
	
	/** texture coord for out (alpha = 0) */
	static final private float TEXTURE_FADE_OUT = 0.75f;
	/** texture coord for in (alpha = 1) */
	static final private float TEXTURE_FADE_IN = -1f;
	
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
	
	
	/** set number of plot for u
	 * @param n
	 */
	public void setNbU(int n){
		this.uNb = n;
	}
	
	/** set number of plot for v
	 * @param n
	 */
	public void setNbV(int n){
		this.vNb = n;
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
		
		
		du = (uMax-uMin)/uNb;
		dv = (vMax-vMin)/vNb;
	
		//Application.debug("vMin, vMax, dv="+vMin+", "+vMax+", "+dv);

		for (int ui=0; ui<uNb; ui++){
			
			for (int vi=0; vi<uNb; vi++){			
				
				drawQuad(ui, vi);
	
			}
			
		}
		
		manager.endGeometry();
	}
	
	private void drawQuad(int ui, int vi){
		
		float u = uMin+ui*du;
		float v = vMin+vi*dv;

		drawTNV(u, v);
		drawTNV(u+du, v);
		drawTNV(u+du, v+dv);
		drawTNV(u, v+dv);
		
	}
	
	private void drawTNV(float u, float v){
		
		float uT = getTextureCoord(u, uMin, uMax, uFade);
		float vT = getTextureCoord(v, vMin, vMax, vFade);				
			
		manager.texture(uT, vT);
		manager.normal(function.evaluateNormal(u, v));
		manager.vertex(function.evaluatePoint(u, v));
	}
	
	private float getTextureCoord(float x,float xMin,float xMax,float xFade){
		if (xFade!=0){
			float t;
			if (x<(xMax+xMin)/2){
				t=(x-xMin)/xFade;
			}else{
				t=(xMax-x)/xFade;
			}
			return TEXTURE_FADE_OUT*(1-t)+TEXTURE_FADE_IN*t;
		}else
			return 0;
	}
	
	

}
