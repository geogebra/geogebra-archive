package geogebra.kernel;

/**
 * @author Markus Hohenwarter
 */
public interface Traceable {
	
	public boolean getTrace();
	public boolean getSpreadsheetTrace();
	public void setTrace(boolean flag);
	public void setSpreadsheetTrace(boolean flag);
	public void updateRepaint();

}
