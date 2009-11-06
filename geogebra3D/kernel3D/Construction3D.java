package geogebra3D.kernel3D;

import java.awt.Color;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoAxis;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra3D.euclidian3D.EuclidianView3D;

/**
 * @author ggb3D
 * 
 * Construction for 3D stuff
 *
 */
public class Construction3D extends Construction {

	
	private Kernel3D kernel3D;
	
	// axis objects
	private GeoAxis3D xAxis3D, yAxis3D, zAxis3D;
	private String xAxis3DLocalName, yAxis3DLocalName, zAxis3DLocalName;

	
	/** default constructor
	 * @param kernel3D current kernel
	 */
	public Construction3D(Kernel3D kernel3D) {
		super(kernel3D);
		
		this.kernel3D = kernel3D;
		
		/*
		Application.debug("geoTable :\n"+geoTable);
		
		GeoElement ret = (GeoElement) geoTable.get("xAxis3D");
		Application.debug("get xAxis3D : "+ret);
		*/
		
	}
	
	
	protected void initAxis(){
		super.initAxis();
		
		
		xAxis3D = new GeoAxis3D(this,GeoAxis3D.X_AXIS_3D);
		yAxis3D = new GeoAxis3D(this,GeoAxis3D.Y_AXIS_3D);
		zAxis3D = new GeoAxis3D(this,GeoAxis3D.Z_AXIS_3D);
		
	}
	
	
	public GeoAxis3D getXAxis3D(){
		return xAxis3D;
	}
	public GeoAxis3D getYAxis3D(){
		return yAxis3D;
	}
	public GeoAxis3D getZAxis3D(){
		return zAxis3D;
	}

	/**
	 * creates the ConstructionDefaults consDefaults
	 */
	protected void newConstructionDefaults(){
		consDefaults = new ConstructionDefaults3D(this);
	}
	
	
	
	
	protected void initGeoTable() {
		geoTable.clear();	//TODO call super method	
		
				
		// add axes labels both in English and current language
		geoTable.put("xAxis3D", xAxis3D);
		geoTable.put("yAxis3D", yAxis3D);
		geoTable.put("zAxis3D", zAxis3D);
		
		if (xAxis3DLocalName != null) {
			geoTable.put(xAxis3DLocalName, xAxis3D);
			geoTable.put(yAxis3DLocalName, yAxis3D);
			geoTable.put(zAxis3DLocalName, zAxis3D);
		}	
			
	}

	public void updateLocalAxesNames() {	
		
		super.updateLocalAxesNames();
		
		
		geoTable.remove(xAxis3DLocalName);
		geoTable.remove(yAxis3DLocalName);
		geoTable.remove(zAxis3DLocalName);

		Application app = getKernel().getApplication();
		xAxis3DLocalName = app.getPlain("xAxis3D");
		yAxis3DLocalName = app.getPlain("yAxis3D");
		zAxis3DLocalName = app.getPlain("zAxis3D");
		
		geoTable.put(xAxis3DLocalName, xAxis3D);
		geoTable.put(yAxis3DLocalName, yAxis3D);	
		geoTable.put(zAxis3DLocalName, zAxis3D);	
		
		
		
		
	}
}
