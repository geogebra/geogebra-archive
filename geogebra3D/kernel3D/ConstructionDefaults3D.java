package geogebra3D.kernel3D;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Color;



/**
 * 3D subclass for {@link ConstructionDefaults}
 *
 * @author ggb3D
 *
 */
public class ConstructionDefaults3D extends ConstructionDefaults {

	
	// DEFAULT GeoElement types	
	/** default point 3D type */	
	public static final int DEFAULT_POINT3D_FREE = 3010;
	/** default dependant point 3D type */	
	public static final int DEFAULT_POINT3D_DEPENDENT = 3011;
	/** default point 3D on path type */	
	public static final int DEFAULT_POINT3D_ON_PATH = 3012;
	/** default point 3D in region type */	
	public static final int DEFAULT_POINT3D_IN_REGION = 3013;

	
	/** default plane 3D type */	
	public static final int DEFAULT_PLANE3D = 3100;
	/** default polygon 3D type */	
	public static final int DEFAULT_POLYGON3D = 3101;
	
	
	/** default polyhedron type */
	public static final int DEFAULT_POLYHEDRON = 3200;
	/** default quadric type */
	public static final int DEFAULT_QUADRIC = 3201;

	
	
	
	
	
	
	// DEFAULT COLORs
	// polygon 3D
	//private static final Color colPolygon3D = ConstructionDefaults.colPolygon;	
	/** default color for 3D polygons */
	private static final Color colPolygon3D = colPolygon;
	/** default alpha for 3D polygons*/
	public static final float DEFAULT_POLYGON3D_ALPHA = 0.5f;

	// plane 
	/** default color for 3D planes */
	private static final Color colPlane3D = new Color(0, 255, 128);
	/** default alpha for 3D planes*/
	public static final float DEFAULT_PLANE3D_ALPHA = 0.5f;
	
	// polyhedrons	
	/** default color for polyhedrons */
	private static final Color colPolyhedron = colPolygon;//new Color(153, 51, 0);

	// quadrics 
	/** default alpha for quadrics*/
	public static final float DEFAULT_QUADRIC_ALPHA = 0.75f;
	/** default color for quadrics */
	private static final Color colQuadric = new Color(255, 0, 128);
	
	
	// axes TODO use gui
	public static final Color colXAXIS = Color.red;
	public static final Color colYAXIS = Color.green;
	public static final Color colZAXIS = Color.blue;
	
	

	
	
	
	
	
	/**
	 * default constructor
	 * @param cons construction
	 */
	public ConstructionDefaults3D(Construction cons) {
		super(cons);
		Application.debug("ConstructionDefaults3D");
	}
	
	
	
	
	
	
	protected void createDefaultGeoElements() {
		super.createDefaultGeoElements();
		
		
		// free point
		GeoPoint3D freePoint = new GeoPoint3D(cons);	
		freePoint.setPointSize(EuclidianView.DEFAULT_POINT_SIZE);
		freePoint.setPointStyle(EuclidianView.POINT_STYLE_DOT);
		freePoint.setLocalVariableLabel("Point3D" + strFree);
		freePoint.setObjColor(colPoint);
		defaultGeoElements.put(DEFAULT_POINT3D_FREE, freePoint);
		
		// dependent point
		GeoPoint3D depPoint = new GeoPoint3D(cons);	
		depPoint.setPointSize(EuclidianView.DEFAULT_POINT_SIZE);
		depPoint.setPointStyle(EuclidianView.POINT_STYLE_DOT);
		depPoint.setLocalVariableLabel("Point3D" + strDependent);
		depPoint.setObjColor(colDepPoint);
		defaultGeoElements.put(DEFAULT_POINT3D_DEPENDENT, depPoint);
		
		// point on path
		GeoPoint3D pathPoint = new GeoPoint3D(cons);	
		pathPoint.setPointSize(EuclidianView.DEFAULT_POINT_SIZE);
		pathPoint.setPointStyle(EuclidianView.POINT_STYLE_DOT);
		pathPoint.setLocalVariableLabel("Point3DOn");
		pathPoint.setObjColor(colPathPoint);
		defaultGeoElements.put(DEFAULT_POINT3D_ON_PATH, pathPoint);
		
		// point in region
		GeoPoint3D regionPoint = new GeoPoint3D(cons);	
		regionPoint.setPointSize(EuclidianView.DEFAULT_POINT_SIZE);
		regionPoint.setPointStyle(EuclidianView.POINT_STYLE_DOT);
		regionPoint.setLocalVariableLabel("Point3DInRegion");
		regionPoint.setObjColor(colRegionPoint);
		defaultGeoElements.put(DEFAULT_POINT3D_IN_REGION, regionPoint);

		
		// plane
		GeoPlane3D plane = new GeoPlane3D(cons);	
		plane.setLocalVariableLabel("Plane3D");
		plane.setObjColor(colPlane3D);
		plane.setAlphaValue(DEFAULT_PLANE3D_ALPHA);
		defaultGeoElements.put(DEFAULT_PLANE3D, plane);
		
		// polygon
		GeoPolygon3D polygon = new GeoPolygon3D(cons, null, null, false);	
//		polygon.setLocalVariableLabel(app.getPlain("Polygon"));
		polygon.setLocalVariableLabel("Polygon3D");
		polygon.setObjColor(colPolygon3D);
		polygon.setAlphaValue(DEFAULT_POLYGON3D_ALPHA);
		polygon.setLayer(2);
		defaultGeoElements.put(DEFAULT_POLYGON3D, polygon);

		
		
		// polyhedron
		GeoPolyhedron polyhedron = new GeoPolyhedron(cons, null, null);	
		polyhedron.setLocalVariableLabel("Polyhedron");
		polyhedron.setObjColor(colPolyhedron);
		polyhedron.setAlphaValue(DEFAULT_POLYGON3D_ALPHA);
		defaultGeoElements.put(DEFAULT_POLYHEDRON, polyhedron);
		
		// quadric
		GeoQuadric quadric = new GeoQuadric(cons);	
		quadric.setLocalVariableLabel("Quadric");
		quadric.setObjColor(colQuadric);
		quadric.setAlphaValue(DEFAULT_QUADRIC_ALPHA);
		defaultGeoElements.put(DEFAULT_QUADRIC, quadric);
	


	}
	
	
	
	public int getDefaultType(GeoElement geo){

		switch (geo.getGeoClassType()) {
		case GeoElement3D.GEO_CLASS_POINT3D:
			if (geo.isIndependent()) {
				return DEFAULT_POINT3D_FREE;
			} else {
				GeoPoint3D p = (GeoPoint3D) geo;
				if (p.hasPath())
					return DEFAULT_POINT3D_ON_PATH;	
				else if (p.hasRegion())
					return DEFAULT_POINT3D_IN_REGION;				
				else
					return DEFAULT_POINT3D_DEPENDENT;
			}
		
		case GeoElement3D.GEO_CLASS_PLANE3D: 
			return DEFAULT_PLANE3D;
		case GeoElement3D.GEO_CLASS_POLYGON3D: 
			return DEFAULT_POLYGON3D;
			
			
		case GeoElement3D.GEO_CLASS_POLYHEDRON:
			return DEFAULT_POLYHEDRON;
		case GeoElement3D.GEO_CLASS_QUADRIC:
			return DEFAULT_QUADRIC;
			
		default:
			return super.getDefaultType(geo);
		}
	}
	
	
	
	
	
	
	
	
	
	
	

}
