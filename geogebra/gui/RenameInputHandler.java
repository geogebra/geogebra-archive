package geogebra.gui;

import geogebra.GeoGebraApplicationBase;
import geogebra.MyError;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

public class RenameInputHandler implements InputHandler {
	private GeoElement geo;

	private boolean storeUndo;

	private GeoGebraApplicationBase app;

	private static String[] invalidFunctionNames = { "gamma", "x", "y", "abs",
			"sgn", "sqrt", "exp", "log", "ln", "ld", "lg", "cos", "sin", "tan", "acos",
			"asin", "atan", "cosh", "sinh", "tanh", "acosh", "asinh", "atanh",
			"floor", "ceil", "round", "min", "max", "random" };

	public RenameInputHandler(GeoGebraApplicationBase app, GeoElement geo, boolean storeUndo) {
		this.app = app;
		this.geo = geo;
		this.storeUndo = storeUndo;
	}

	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	public boolean processInput(String inputValue) {
		GeoElement geo = this.geo;
		
		if (inputValue == null || inputValue.equals(geo.getLabel()))
			return false;
		
		if (!checkName(geo, inputValue)) {
			app.showError(app.getError("InvalidInput") + ":\n" + inputValue);
			return false;
		}

		try {
			String newLabel = app.getKernel().getAlgebraProcessor().parseLabel(
					inputValue);

			// is there a geo with this name?
			Construction cons = geo.getConstruction();
			GeoElement existingGeo = cons.lookupLabel(newLabel);						
			
			if (existingGeo != null) {
				// rename this geo too:
				String tempLabel = existingGeo.getIndexLabel(newLabel);
				existingGeo.rename(tempLabel);				
			}					

			if (geo.rename(newLabel) && storeUndo) {
				app.storeUndoInfo();
			}

			return true;
		} catch (Exception e) {
			app.showError(app.getError("InvalidInput") + ":\n" + inputValue);
		} catch (MyError err) {
			app.showError(app.getError("InvalidInput") + ":\n" + inputValue);
		}
		return false;
	}

	// check if name is valid for geo
	private boolean checkName(GeoElement geo, String name) {
		if (name == null) return true;
		
		name = name.toLowerCase();
		if (geo.isGeoFunction()) {
			for (int i = 0; i < invalidFunctionNames.length; i++) {
				if (invalidFunctionNames[i].equals(name))
					return false;
			}
		}

		return true;
	}
}
