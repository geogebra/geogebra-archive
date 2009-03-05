package geogebra3D.kernel3D;

import java.awt.Color;

import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;



/**
 * 3D subclass for {@link ConstructionDefaults}
 *
 * @author ggb3D
 *
 */
public class ConstructionDefaults3D extends ConstructionDefaults {

	// DEFAULT COLORs
	// polygon 3D
	//private static final Color colPolygon3D = ConstructionDefaults.colPolygon;	
	/** default alpha for 3D polygons*/
	public static final float DEFAULT_POLYGON3D_ALPHA = 0.5f;

	// plane 
	//private static final Color colPolygon3D = ConstructionDefaults.colPolygon;	
	/** default alpha for planes*/
	public static final float DEFAULT_PLANE_ALPHA = 0.5f;
	
	// quadrics 
	//private static final Color colPolygon3D = ConstructionDefaults.colPolygon;	
	/** default alpha for planes*/
	public static final float DEFAULT_QUADRIC_ALPHA = 0.5f;
	
	/**
	 * default constructor
	 * @param cons construction
	 */
	public ConstructionDefaults3D(Construction cons) {
		super(cons);
	}

}
