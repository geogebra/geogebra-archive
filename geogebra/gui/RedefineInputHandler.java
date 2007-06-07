package geogebra.gui;

import geogebra.Application;
import geogebra.MyError;
import geogebra.kernel.GeoElement;

public class RedefineInputHandler implements InputHandler {

	private GeoElement geo;
	private Application app;

	public RedefineInputHandler(Application app, GeoElement geo) {
		this.geo = geo;
		this.app = app;
	}
	
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	public boolean processInput(String inputValue) {
		if (inputValue == null)
			return false;
		try {
			GeoElement newGeo = app.getKernel().getAlgebraProcessor().changeGeoElement(
					geo, inputValue, true);
			app.doAfterRedefine(newGeo);
			return newGeo != null;
		} catch (Exception e) {
			app.showError("ReplaceFailed");			
		} catch (MyError err) {
			app.showError(err);			
		} 
		return false;
	}
}
