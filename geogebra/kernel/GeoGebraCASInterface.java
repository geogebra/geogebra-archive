package geogebra.kernel;

import geogebra.cas.CASgeneric;
import geogebra.cas.CASparser;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ValidExpression;

/*
 * needed for minimal applets
 */
public interface GeoGebraCASInterface {

	public int getCurrentCASstringType();

	public String evaluateRaw(String geoStr) throws Throwable;

	public CASgeneric getCurrentCAS();

	public String evaluateGeoGebraCAS(ValidExpression evalVE);

	public CASparser getCASparser();

	public int getTimeout();

	public boolean isStructurallyEqual(ValidExpression inputVE, String newInput);

	public void setTimeout(int t);

	public boolean isCommandAvailable(Command cmd);

}
