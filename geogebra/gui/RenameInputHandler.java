package geogebra.gui;

import geogebra.Application;
import geogebra.MyError;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;

public class RenameInputHandler implements InputHandler {
	private GeoElement geo;

	private boolean storeUndo;

	private Application app;

	private static String[] invalidFunctionNames = { "gamma", "x", "y", "abs",
			"sgn", "sqrt", "exp", "log", "ln", "cos", "sin", "tan", "acos",
			"asin", "atan", "cosh", "sinh", "tanh", "acosh", "asinh", "atanh",
			"floor", "ceil", "round", "min", "max" };

	public RenameInputHandler(Application app, GeoElement geo, boolean storeUndo) {
		this.app = app;
		this.geo = geo;
		this.storeUndo = storeUndo;
	}

	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	public boolean processInput(String inputValue) {
		if (inputValue == null || inputValue.equals(geo.getLabel()))
			return false;
		try {
			if (!checkName(geo, inputValue)) {
				app.showError("InvalidInput");
				return false;
			}

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
			app.showError("InvalidInput");
		} catch (MyError err) {
			app.showError(err);
		}
		return false;
	}

	// check if name is valid for geo
	private boolean checkName(GeoElement geo, String name) {
		if (geo.isGeoFunction()) {
			for (int i = 0; i < invalidFunctionNames.length; i++) {
				if (invalidFunctionNames[i].equals(name))
					return false;
			}
		}

		return true;
	}
}
