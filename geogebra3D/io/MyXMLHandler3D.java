package geogebra3D.io;

import java.util.LinkedHashMap;

import geogebra.euclidian.EuclidianView;
import geogebra.io.MyXMLHandler;
import geogebra.kernel.Construction;
import geogebra3D.Application3D;
import geogebra3D.euclidian3D.EuclidianView3D;
import geogebra3D.kernel3D.Kernel3D;



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
	public MyXMLHandler3D(Kernel3D kernel, Construction cons) {
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
		/*
		case 'a':
			if (eName.equals("axesColor")) {
				ok = handleAxesColor(ev, attrs);
				break;
			} else if (eName.equals("axis")) {
				ok = handleAxis(ev, attrs);
				break;
			}

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

			/*
		case 'e':
			if (eName.equals("evSettings")) {
				ok = handleEvSettings(ev, attrs);
				break;
			}

		case 'g':
			if (eName.equals("grid")) {
				ok = handleGrid(ev, attrs);
				break;
			} else if (eName.equals("gridColor")) {
				ok = handleGridColor(ev, attrs);
				break;
			}
		case 'l':
			if (eName.equals("lineStyle")) {
				ok = handleLineStyle(ev, attrs);
				break;
			}

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
			ev.setRotXYinDegrees(zAngle, xAngle, true);
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	

}
