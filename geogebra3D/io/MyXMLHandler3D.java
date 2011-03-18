package geogebra3D.io;

import geogebra.io.MyXMLHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.kernelND.GeoPlaneND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra3D.Application3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.GeoPoint3D;
import geogebra3D.kernel3D.Kernel3D;

import java.util.LinkedHashMap;



/**
 * Class extending MyXMLHandler for 3D 
 * 
 * @author ggb3D
 * 
 *
 */
public class MyXMLHandler3D extends MyXMLHandler {

	/** See Kernel3D for using the constructor
	 * @param kernel
	 * @param cons
	 */
	public MyXMLHandler3D(Kernel kernel, Construction cons) {
		super(kernel, cons);
	}
	
	
	
	
	// ====================================
	// <euclidianView3D> only used in 3D
	// ====================================
	/** only used in MyXMLHandler3D
	 * @param eName
	 * @param attrs
	 */
	protected void startEuclidianView3DElement(String eName, LinkedHashMap<String, String> attrs) {
		
		boolean ok = true;
		EuclidianView3D ev = ((Application3D) app).getEuclidianView3D();

		switch (eName.charAt(0)) {
		
		case 'a':
			if (eName.equals("axesColor")) {
				//ok = handleAxesColor(ev, attrs);
				break;
			} else if (eName.equals("axis")) {
				ok = handleAxis(ev, attrs);
				//Application.debug("ok = "+ok);
				break;
			}

			/*
		case 'b':
			if (eName.equals("bgColor")) {
				ok = handleBgColor(ev, attrs);
				break;
			}
			*/

		case 'c':
			if (eName.equals("coordSystem")) {
				ok = handleCoordSystem3D(ev, attrs);
				break;
			}

		case 'g':
			if (eName.equals("grid")) {
				ok = handleGrid(ev, attrs);
				break;
			} 
			/*
			else if (eName.equals("gridColor")) {
				ok = handleGridColor(ev, attrs);
				break;
			}
			 */
			
		case 'p':
			if (eName.equals("plate")) {
				ok = handlePlate(ev, attrs);
				break;
			} else if (eName.equals("plane")) {
				ok = handlePlane(ev, attrs);
				break;
			}
			
			/*

		case 's':
			if (eName.equals("size")) {
				ok = handleEvSize(ev, attrs);
				break;
			}
			*/

		default:
			System.err.println("unknown tag in <euclidianView3D>: " + eName);
		}

		if (!ok)
			System.err.println("error in <euclidianView3D>: " + eName);
	}
	
	
	
	protected void startGeoElement(String eName, LinkedHashMap<String, String> attrs) {
		if (geo == null) {
			System.err.println("no element set for <" + eName + ">");
			return;
		}

		boolean ok = true;
		switch (eName.charAt(0)) {
		case 'f':
			if (eName.equals("fading")) {
				ok = handleFading(attrs);
				break;
			}
		
		default:
			super.startGeoElement(eName, attrs);
		}

		if (!ok) {
			System.err.println("error in <element>: " + eName);
		}
	}
	
	
	
	
	private boolean handleCoordSystem3D(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		try {
			double xZero = Double.parseDouble((String) attrs.get("xZero"));
			double yZero = Double.parseDouble((String) attrs.get("yZero"));
			double zZero = Double.parseDouble((String) attrs.get("zZero"));
			
			double scale = Double.parseDouble((String) attrs.get("scale"));
			// TODO yScale, zScale

			double xAngle = Double.parseDouble((String) attrs.get("xAngle"));
			double zAngle = Double.parseDouble((String) attrs.get("zAngle"));
			

			ev.setScale(scale);
			ev.setXZero(xZero);ev.setYZero(yZero);ev.setZZero(zZero);
			ev.setRotXYinDegrees(zAngle, xAngle);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	
	private boolean handleFading(LinkedHashMap<String, String> attrs) {
		try {
			float fading = Float.parseFloat((String) attrs.get("val"));			
			((GeoPlaneND) geo).setFading(fading);			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	
	
	/** handles plane attributes for EuclidianView3D
	 * @param ev
	 * @param attrs
	 * @return true if all is done ok
	 * @deprecated
	 */
	protected boolean handlePlane(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		
		return handlePlate(ev, attrs);
		/*
		// <plane show="false"/>
		try {
			String strShowPlane = (String) attrs.get("show");

			// show the plane
			if (strShowPlane != null) {
				boolean showPlane = parseBoolean(strShowPlane);
				ev.setShowPlane(showPlane);
			}
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
		*/
	}
	
	
	/** handles plane attributes (show plate) for EuclidianView3D
	 * @param ev
	 * @param attrs
	 * @return true if all is done ok
	 */
	protected boolean handlePlate(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		try {
			String strShowPlate = (String) attrs.get("show");

			// show the plane
			if (strShowPlate != null) {
				boolean showPlate = parseBoolean(strShowPlate);
				ev.setShowPlate(showPlate);
			}
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	
	/** handles plane attributes (show grid) for EuclidianView3D
	 * @param ev
	 * @param attrs
	 * @return true if all is done ok
	 */
	protected boolean handleGrid(EuclidianView3D ev, LinkedHashMap<String, String> attrs) {
		try {
			String strShowGrid = (String) attrs.get("show");

			// show the plane
			if (strShowGrid != null) {
				boolean showGrid = parseBoolean(strShowGrid);
				ev.setShowGrid(showGrid);
			}
			return true;
		} catch (Exception e) {
			//e.printStackTrace();
			return false;
		}
	}
	
	/** create absolute start point (coords expected) */
	protected GeoPointND handleAbsoluteStartPoint(LinkedHashMap<String, String> attrs) {
		double x = Double.parseDouble((String) attrs.get("x"));
		double y = Double.parseDouble((String) attrs.get("y"));
		double z = Double.parseDouble((String) attrs.get("z"));
		double w = Double.parseDouble((String) attrs.get("w"));
		GeoPoint3D p = new GeoPoint3D(cons);
		p.setCoords(x, y, z, w);
		return p;
	}
	
	


	

}
