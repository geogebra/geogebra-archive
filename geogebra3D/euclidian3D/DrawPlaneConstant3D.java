package geogebra3D.euclidian3D;




import geogebra.Matrix.GgbCoordSys;
import geogebra.Matrix.GgbMatrix4x4;
import geogebra.Matrix.GgbVector;
import geogebra.kernel.kernel3D.GeoPlane3D;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.euclidian3D.opengl.Textures;

import java.awt.Color;




/**
 * Class for drawing 3D constant planes.
 * @author matthieu
 *
 */
public class DrawPlaneConstant3D extends DrawPlane3D {


	
	
	/**
	 * Common constructor
	 * @param a_view3D
	 * @param a_plane3D
	 */
	public DrawPlaneConstant3D(EuclidianView3D a_view3D, GeoPlane3D a_plane3D){
		
		super(a_view3D, a_plane3D);
	}
	
	
	
	protected void updateForView(){
		
	}
	
	

}
