package geogebra.euclidian;

import geogebra.kernel.GeoElement;

import java.awt.Graphics2D;

/**
 * @author Markus Hohenwarter
 */
public interface Previewable {
	
	public void updatePreview(); 
	public void updateMousePos(double x, double y);
	public void drawPreview(Graphics2D g2);
	public void disposePreview();
	
}
