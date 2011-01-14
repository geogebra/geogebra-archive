package geogebra3D.euclidian3D.opengl;

import java.nio.FloatBuffer;

import geogebra.Matrix.Coords;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.kernel.GeoFunctionNVar;
import geogebra3D.euclidian3D.plots.SurfaceMesh;

/** Class for drawing surfaces.
 * @author matthieu
 *
 */
public class PlotterSurface {
	
	/** manager */
	private Manager manager;

	/** index */
	private int index;
	
	/** 2-var function */
	private Functional2Var functional2Var;
	
	private GeoFunctionNVar function;
	
	/** domain for plotting */
	private float uMin, uMax, vMin, vMax;
	
	/** number of plotting */
	private int uNb, vNb ;
	
	/** delta for plotting */
	private float du, dv;
	
	/** fading values */
	private float uMinFade, uMaxFade, vMinFade, vMaxFade;
	private float uMinFadeNb, uMaxFadeNb, vMinFadeNb, vMaxFadeNb;
	
	
	/** texture coord for out (alpha = 0) */
	static final private float TEXTURE_FADE_OUT = 0.75f;
	/** texture coord for in (alpha = 1) */
	static final private float TEXTURE_FADE_IN = 0f;
	
	/** default constructor
	 * @param manager
	 */
	public PlotterSurface(Manager manager){
		this.manager = manager;
	}
	
	
	////////////////////////////////////
	// START AND END
	////////////////////////////////////
	
	/**
	 * start new surface
	 */
	public void start(){
		index = manager.startNewList();
	}
	
	/**
	 * start new surface
	 * @param function 
	 */
	public void start(Functional2Var function){
		index = manager.startNewList();
		this.functional2Var = function;
		uMinFade = 0; vMinFade = 0;
		uMaxFade = 0; vMaxFade = 0;
		
	}
	
	/**
	 * start new surface
	 * @param function 
	 */
	public void start(GeoFunctionNVar function){
		index = manager.startNewList();
		this.function = function;
		uMinFade = 0; vMinFade = 0;
		uMaxFade = 0; vMaxFade = 0;
		
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
	
	
	
	/** set fading frontiers for u parameter
	 * @param min
	 * @param max
	 */
	public void setUFading(float min, float max){
		this.uMinFade = min;
		this.uMaxFade = max;
	}
	
	/** set fading frontiers for v parameter
	 * @param min
	 * @param max
	 */
	public void setVFading(float min, float max){
		this.vMinFade = min;
		this.vMaxFade = max;
	}
	
	public void drawTriangle(Coords p1, Coords p2, Coords p3){
		manager.startGeometry(Manager.TRIANGLE_STRIP);
		
		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		
		manager.vertex(p1);
		manager.vertex(p3);
		manager.vertex(p2);
		manager.endGeometry();
	}
	/** 
	 * draw part of the surface
	 */
	public void draw(){
		manager.startGeometry(Manager.QUADS);
		
		
		du = (uMax-uMin)/uNb;
		dv = (vMax-vMin)/vNb;
		
		/*
		uMinFadeNb = uNb*uMinFade/(uMax-uMin);
		uMaxFadeNb = uNb*uMaxFade/(uMax-uMin);
		vMinFadeNb = vNb*vMinFade/(vMax-vMin);
		vMaxFadeNb = vNb*vMaxFade/(vMax-vMin);
		*/
		uMinFadeNb = uMinFade/du;
		uMaxFadeNb = uMaxFade/du;
		vMinFadeNb = vMinFade/dv;
		vMaxFadeNb = vMaxFade/dv;
	
		//Application.debug("vMin, vMax, dv="+vMin+", "+vMax+", "+dv);

		for (int ui=0; ui<uNb; ui++){
			
			for (int vi=0; vi<vNb; vi++){			
				
				drawQuad(ui, vi);
	
			}
			
		}
		
		manager.endGeometry();
	}
	
	/** 
	 * draw part of the surface
	 */
	public void draw(SurfaceMesh tree){
		
		FloatBuffer b1 = tree.getVertices();
		FloatBuffer b2 = tree.getNormals();
		int cnt = tree.getTriangleCount();
		manager.startGeometry(Manager.TRIANGLES);
		
		/*TODO use fading texture
		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		*/
		manager.texture(0, 0);
		
		float[] f = new float[9]; float[] n = new float[9];
		b1.rewind(); b2.rewind();
		for(int i = 0; i < cnt; i++) {
			b1.get(f);b2.get(n);
			manager.normal(n[0],n[1],n[2]);
			manager.vertex(f[0],f[1],f[2]);
			manager.normal(n[3],n[4],n[5]);
			manager.vertex(f[3],f[4],f[5]);
			manager.normal(n[6],n[7],n[8]);
			manager.vertex(f[6],f[7],f[8]);
		}
		manager.endGeometry();
	}
	
	
	/** draws a disc
	 * @param center
	 * @param v1
	 * @param v2
	 * @param radius
	 */
	public void disc(Coords center, Coords v1, Coords v2, double radius){
		manager.startGeometry(Manager.TRIANGLE_FAN);

		int longitude = 60;
		
		Coords vn;
		
    	float dt = (float) 1/longitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	
    	

    	manager.texture(0, 0);
    	manager.normal(v1.crossProduct(v2));
    	manager.vertex(center);  
    	
    	float u=1, v=0;
		vn = (Coords) v1.mul(u).add(v2.mul(v));
		manager.vertex(center.add(vn.mul(radius)));  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.cos ( i * da ); 
    		v = (float) Math.sin ( i * da ); 

    		vn = (Coords) v1.mul(u).add(v2.mul(v));
    		manager.vertex(center.add(vn.mul(radius)));
    	} 		
		

		manager.endGeometry();
	}
	
	
	
	/** draws an ellipse
	 * @param center
	 * @param v1
	 * @param v2
	 * @param a 
	 * @param b 
	 */
	public void ellipse(Coords center, Coords v1, Coords v2, double a, double b){
		manager.startGeometry(Manager.TRIANGLE_FAN);
		
		int longitude = 60;
		
		Coords m;
		
    	float dt = (float) 1/longitude;
    	float da = (float) (2*Math.PI *dt) ; 
    	
    	manager.texture(0, 0);
    	manager.normal(v1.crossProduct(v2));
    	manager.vertex(center);  
    	
    	float u=1, v=0;
		m = v1.mul(a*u).add(v2.mul(b*v));
		manager.vertex(center.add(m));  	
    	
    	for( int i = 1; i <= longitude  ; i++ ) { 
    		u = (float) Math.cos ( i * da ); 
    		v = (float) Math.sin ( i * da ); 
    		
     		m = v1.mul(a*u).add(v2.mul(b*v));
    		manager.vertex(center.add(m));
    	} 
    	
    	manager.endGeometry();
    	
	}
	
	private Coords calcNormal(float x, float y, float z){
		double[] n = new double[3];
		Coords v0 = new Coords(x,y,z,0);
		Coords v1 = function.evaluatePoint(x+1e-9,y);
		Coords v2 = function.evaluatePoint(x,y+1e-9);
		
		return v1.sub(v0).crossProduct(v2.sub(v0)).normalized();
	}
	
	public void drawTriangle(FloatBuffer d){
		manager.startGeometry(Manager.TRIANGLE_STRIP);
		
		float uT = getTextureCoord(1, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(1, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		float[] f = new float[9];
		
		d.get(f);
		manager.vertex(f[0],f[1],f[2]);
		manager.vertex(f[3],f[4],f[5]);
		manager.vertex(f[6],f[7],f[8]);
		manager.endGeometry();
		d.flip();
		manager.endGeometry();
	}
	
	private void drawQuad(int ui, int vi){
		

		drawTNV(ui, vi);
		drawTNV(ui+1, vi);
		drawTNV(ui+1, vi+1);
		drawTNV(ui, vi+1);
		
	}
	
	private void drawTNV(int ui, int vi){
		
		float uT = getTextureCoord(ui, uNb, uMinFadeNb, uMaxFadeNb);
		float vT = getTextureCoord(vi, vNb, vMinFadeNb, vMaxFadeNb);	
		manager.texture(uT, vT);
		
		float u = uMin+ui*du;
		float v = vMin+vi*dv;			
		manager.normal(functional2Var.evaluateNormal(u, v));
		manager.vertex(functional2Var.evaluatePoint(u, v));
	}
	
	private float getTextureCoord(int i, int n, float fadeMin, float fadeMax){
		
		float t;
	
		if (fadeMin!=0){
			if (i<=n/2){
				t=i/fadeMin;
				return TEXTURE_FADE_OUT*(1-t)+TEXTURE_FADE_IN*t;
			}
		}
			

		if (fadeMax!=0){
			if (i>=n/2){
				t=(n-i)/fadeMax;
				return TEXTURE_FADE_OUT*(1-t)+TEXTURE_FADE_IN*t;
			}
		}

		return TEXTURE_FADE_IN;
	}
	
	

}


