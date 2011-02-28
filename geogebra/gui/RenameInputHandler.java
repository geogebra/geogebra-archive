package geogebra.gui;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.MyError;
import geogebra.util.Unicode;

import java.util.Locale;

public class RenameInputHandler implements InputHandler {
	private GeoElement geo;

	private boolean storeUndo;

	private Application app;

	private static String[] invalidFunctionNames = { "gamma", "x", "y", "abs",
			"sgn", "sqrt", "exp", "log", "ln", "ld", "lg", "cos", "sin", "tan",
			"acos", "arcos", "arccos", "asin", "arcsin", "atan", "arctan", 
			"cosh", "sinh", "tanh", "acosh", "arcosh", "arccosh", "asinh",
			"arcsinh", "atanh", "arctanh", "atan2", "erf",
			"floor", "ceil", "round", "random", "conjugate", "arg",
			"sec", "csc", "cosec", "cot", "sech", "csch", "coth", Unicode.IMAGINARY };

	public RenameInputHandler(Application app, GeoElement geo, boolean storeUndo) {
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
			Kernel kernel = app.getKernel();
			String newLabel = kernel.getAlgebraProcessor().parseLabel(
					inputValue);

			// is there a geo with this name?
			Construction cons = geo.getConstruction();
			GeoElement existingGeo = kernel.lookupLabel(newLabel);						
			
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
		
		name = name.toLowerCase(Locale.US);
		if (geo.isGeoFunction()) {
			for (int i = 0; i < invalidFunctionNames.length; i++) {
				if (invalidFunctionNames[i].equals(name))
					return false;
			}
		}

		return true;
	}
}
