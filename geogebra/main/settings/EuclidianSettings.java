package geogebra.main.settings;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.Unicode;

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
	double[] gridDistances = { 2, 2, Math.PI/6 };
	
	private double[] axisCross = {0,0};
	private boolean[] positiveAxes = {false, false};
	private boolean[] drawBorderAxes = {false,false};
	NumberValue xminObject, xmaxObject, yminObject, ymaxObject;
	
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
	
	protected boolean[] showAxes = { true, true, true };
	private boolean showAxesCornerCoords = true;
	
	protected boolean[] showAxesNumbers = { true, true, true };

	protected String[] axesLabels = { null, null, null };

	protected String[] axesUnitLabels = { null, null, null };

	protected boolean[] piAxisUnit = { false, false, false };

	protected int[] axesTickStyles = { EuclidianView.AXES_TICK_STYLE_MAJOR,
			EuclidianView.AXES_TICK_STYLE_MAJOR };

	// for axes labeling with numbers
	protected boolean[] automaticAxesNumberingDistances = { true, true, true };

	protected double[] axesNumberingDistances = { 2, 2, 2 };

	// distances between grid lines
	protected boolean automaticGridDistance = true;

	private double xZero;

	private double yZero;

	private double xscale;

	private double yscale;

	private double scaleRatio;

	private double invXscale;

	private double invYscale;
	
	/*
	 * change visibility of axes
	 */
	public void setShowAxis(int axis, boolean flag){
		boolean changed = flag != showAxes[axis];
		
		if(changed) {
			showAxes[axis] = flag;
			settingChanged();
		}
			
	}
	
	/**
	 * says if the axis is shown or not
	 * @param axis id of the axis
	 * @return if the axis is shown
	 */
	public boolean getShowAxis(int axis){
		return showAxes[axis];
	}

	/**
	 * sets the axis label to axisLabel
	 * @param axis
	 * @param axisLabel
	 */
	public void setAxisLabel(int axis, String axisLabel){
		boolean changed = false;
		if (axisLabel != null && axisLabel.length() == 0) {
			changed = axesLabels[axis] != null;
			axesLabels[axis] = null;
		}
		else {
			axesLabels[axis] = axisLabel;
			changed = axesLabels[axis] != axisLabel;
		}
		
		if(changed) {
			settingChanged();
		}

	}
	
	public String[] getAxesLabels() {
		return axesLabels;
	}

	public String[] getAxesUnitLabels() {
		return axesUnitLabels;
	}

	public void setAxesUnitLabels(String[] axesUnitLabels) {
		this.axesUnitLabels = axesUnitLabels;

		// check if pi is an axis unit
		for (int i = 0; i < 2; i++) {
			piAxisUnit[i] = axesUnitLabels[i] != null
					&& axesUnitLabels[i].equals(Unicode.PI_STRING);
		}
		//setAxesIntervals(xscale, 0);
		//setAxesIntervals(yscale, 1);
		
		settingChanged();
	}

	public void setShowAxisNumbers(int axis, boolean showAxisNumbers){
		showAxesNumbers[axis]=showAxisNumbers;
	}

	public boolean[] getShowAxisNumbers(){
		return showAxesNumbers;
	}

	public double[] getAxesNumberingDistances() {
		return axesNumberingDistances;
	}

	/**
	 * 
	 * @param dist
	 * @param axis 0 for xAxis, 1 for yAxis
	 */
	public void setAxesNumberingDistance(double dist, int axis) {
		
		axesNumberingDistances[axis] = dist;
		
		setAutomaticAxesNumberingDistance(false, axis);
		
		settingChanged();
	}

	public void setAutomaticAxesNumberingDistance(boolean flag, int axis) {
		automaticAxesNumberingDistances[axis] = flag;
		//if (axis == 0)
		//	setAxesIntervals(xscale, 0);
		//else
		//	setAxesIntervals(yscale, 1);
	}

	public int[] getAxesTickStyles() {
		return axesTickStyles;
	}
	
	public void setAxisTickStyle(int axis, int tickStyle){
		
		if (axesTickStyles[axis] != tickStyle) {
			axesTickStyles[axis] = tickStyle;
			settingChanged();
		}
	}

	public double[] getAxesCross() {
		return axisCross;
	}

	public void setAxisCross(int axis, double cross) {
		if (axisCross[axis] != cross) {
			axisCross[axis] = cross;
			settingChanged();
		}
	}
	
	public boolean[] getPositiveAxes() {
		return positiveAxes;
	}

	// for xml handler
	public void setPositiveAxis(int axis, boolean isPositiveAxis) {
		positiveAxes[axis] = isPositiveAxis;
	}
	
	/**
	 * @return the xminObject
	 */
	public GeoNumeric getXminObject() {
		return (GeoNumeric) xminObject;
	}

	/**
	 * @param xminObjectNew the xminObject to set
	 */
	public void setXminObject(NumberValue xminObjectNew) {
		this.xminObject = xminObjectNew;
		settingChanged();
	}

	/**
	 * @return the xmaxObject
	 */
	public GeoNumeric getXmaxObject() {
		return (GeoNumeric) xmaxObject;
	}

	/**
	 * @param xmaxObjectNew the xmaxObject to set
	 */
	public void setXmaxObject(NumberValue xmaxObjectNew) {
		this.xmaxObject = xmaxObjectNew;
		settingChanged();
	}

	/**
	 * @return the yminObject
	 */
	public GeoNumeric getYminObject() {
		return (GeoNumeric) yminObject;
	}

	/**
	 * @param yminObjectNew the yminObject to set
	 */
	public void setYminObject(NumberValue yminObjectNew) {
		this.yminObject = yminObjectNew;
		settingChanged();
	}

	/**
	 * @return the ymaxObject
	 */
	public GeoNumeric getYmaxObject() {
		return (GeoNumeric) ymaxObject;
	}

	/**
	 * @param ymaxObjectNew the ymaxObject to set
	 */
	public void setYmaxObject(NumberValue ymaxObjectNew) {
		this.ymaxObject = ymaxObjectNew;
		settingChanged();
	}

	/**
	 * Returns x coordinate of axes origin.
	 */
	public double getXZero() {
		return xZero;
	}

	/**
	 * Returns y coordinate of axes origin.
	 */
	public double getYZero() {
		return yZero;
	}
	
	/**
	 * Returns xscale of this view. The scale is the number of pixels in screen
	 * space that represent one unit in user space.
	 */
	public double getXscale() {
		return xscale;
	}

	/**
	 * Returns the yscale of this view. The scale is the number of pixels in
	 * screen space that represent one unit in user space.
	 */
	public double getYscale() {
		return yscale;
	}

	public void setCoordSystem(double xZero, double yZero, double xscale,
			double yscale) {
		if (Double.isNaN(xscale) || xscale < Kernel.MAX_DOUBLE_PRECISION || xscale > Kernel.INV_MAX_DOUBLE_PRECISION)
			return;
		if (Double.isNaN(yscale) || yscale < Kernel.MAX_DOUBLE_PRECISION || yscale > Kernel.INV_MAX_DOUBLE_PRECISION)
			return;

		this.xZero = xZero;
		this.yZero = yZero;
		this.xscale = xscale;
		this.yscale = yscale;
		scaleRatio = yscale / xscale;
		invXscale = 1.0d / xscale;
		invYscale = 1.0d / yscale;
		
		// set transform for my coord system:
		// ( xscale 0 xZero )
		// ( 0 -yscale yZero )
		// ( 0 0 1 )
		// not needed in Settings
		//coordTransform.setTransform(xscale, 0.0d, 0.0d, -yscale, xZero, yZero);

		// real world values
		// not needed in Settings
		//setRealWorldBounds();
		
	}
	

	
	// TODO add more settings here
}
