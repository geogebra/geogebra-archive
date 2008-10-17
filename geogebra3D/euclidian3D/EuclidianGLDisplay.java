package geogebra3D.euclidian3D;

import geogebra3D.euclidian3D.GL.GLDisplay;
import geogebra3D.euclidian3D.GL.Renderer;

public class EuclidianGLDisplay extends GLDisplay {

	public EuclidianGLDisplay(int w, int h) {
		super(w, h);		
	}

	public void addRenderer(EuclidianRenderer3D renderer3D) {
		addGLEventListener( (Renderer) renderer3D);
		
	}
	

}
