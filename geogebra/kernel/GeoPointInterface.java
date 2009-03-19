package geogebra.kernel;

import java.util.Locale;


/**
 * 
 * @author ggb3D
 *
 * interface for stuff common to 2D and 3D points
 *
 */

public interface GeoPointInterface {

	
	/** Returns whether this point has changeable numbers as coordinates */
	public boolean hasChangeableCoordParentNumbers();

	public void setLabel(String string);

	public boolean isLabelSet();

	public String getLabel();

	public boolean isInfinite();

	public boolean showInEuclidianView();

	public void remove();

	

}
