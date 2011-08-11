package geogebra.main.settings;

import java.awt.Color;

/**
 * Settings for an euclidian view. To which view these settings are associated
 * is determined in {@link Settings}.
 */
public class EuclidianSettings extends AbstractSettings {
	/**
	 * Color of the euclidian view's background.
	 */
	private Color backgroundColor;
	
	/**
	 * Color of the axes.
	 */
	private Color axesColor;
	
	/**
	 * Color of the grid lines.
	 */
	private Color gridColor;

	/**
	 * Line style of axes.
	 */
	private int axesLineStyle;
	
	/**
	 * Line style of grid.
	 */
	private int gridLineStyle;
	
	/**
	 * Various distances between lines of the grid.
	 */
	private double[] gridDistances;
	
	/**
	 * Change background color.
	 * @param col 
	 */
	public void setBackground(Color col) {
		if(!col.equals(backgroundColor)) {
			backgroundColor = col;
			settingChanged();
		}
	}
	
	/**
	 * @return background color
	 */
	public Color getBackground() {
		return backgroundColor;
	}
	
	/**
	 * Change axes color.
	 * @param col
	 */
	public void setAxesColor(Color col) {
		if(!col.equals(axesColor)) {
			axesColor = col;
			settingChanged();
		}
	} 
	
	/**
	 * @return axes color
	 */
	public Color getAxesColor() {
		return axesColor;
	}

	/**
	 * Change grid color.
	 * @param col
	 */
	public void setGridColor(Color col) {
		if(!col.equals(gridColor)) {
			gridColor = col;
			settingChanged();
		}
	}
	
	/**
	 * @return color of the grid
	 */
	public Color getGridColor() {
		return gridColor;
	}

	/**
	 * Change line style of axes.
	 * @param style
	 */
	public void setAxesLineStyle(int style) {
		if(axesLineStyle != style) {
			axesLineStyle = style;
			settingChanged();
		}
	}
	
	/**
	 * @return line style of axes
	 */
	public int getAxesLineStyle() {
		return axesLineStyle;
	}

	/**
	 * Change line style of grid.
	 * @param style
	 */
	public void setGridLineStyle(int style) {
		if(gridLineStyle != style) {
			gridLineStyle = style;
			settingChanged();
		}
	}
	
	/**
	 * @return line style of grid
	 */
	public int getGridLineStyle() {
		return gridLineStyle;
	}

	/**
	 * Change grid distances.
	 * @param dists
	 */
	public void setGridDistances(double[] dists) {
		boolean changed = false;
		
		if(gridDistances == null) {
			changed = true;
		} else if(gridDistances.length != dists.length) {
			changed = true;
		} else {
			for(int i = 0; i < dists.length; ++i) {
				if(dists[i] != gridDistances[i]) {
					changed = true;
					break;
				}
			}
		}
		
		if(changed) {
			gridDistances = dists;
			settingChanged();
		}
	}
	
	/**
	 * @return grid distances
	 */
	public double[] getGridDistances() {
		return gridDistances;
	}
	
	// TODO add more settings here
}
