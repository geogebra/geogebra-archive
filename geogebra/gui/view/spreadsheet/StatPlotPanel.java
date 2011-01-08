package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JPanel;

/**
 * 
 * Creates a JPanel with an extended instance of EuclidianView and methods for 
 * creating geos in the panel.
 * 
 * @author gsturr 2010-6-30
 *
 */
public class StatPlotPanel extends JPanel implements ComponentListener {
	
	// ggb 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	
	
	private ArrayList<GeoElement> plotGeoCollection;
	private GeoElement plotGeo;
	//private GeoList boundList, freqList;
	
	private boolean isAutoRemoveGeos = true;	
	private double xMinData, xMaxData, yMinData, yMaxData;
	
	
	private double xMinEV, xMaxEV, yMinEV, yMaxEV;
	private boolean showYAxis = false;
	private boolean showArrows = false;
	private boolean forceXAxisBuffer = false;
	private boolean[] isEdgeAxis = {true,true};
	
	
	// new EuclidianView instance 
	private MyEuclidianView ev;
	private EuclidianController ec;
	
	
	private String[] regCmd = new String[StatDialog.regressionTypes];
	
	
	private String regressionEqn;
	public String getRegressionEqn(){
		return regressionEqn;
	}

	private GeoElement regModel;
	
	
	

	public GeoElement getRegModel() {
		return regModel;
	}


	public void setRegModel(GeoElement regModel) {
		this.regModel = regModel;
	}


	/*************************************************
	 * Construct the panel
	 */
	public StatPlotPanel(Application app){
		
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		
		plotGeoCollection = new ArrayList<GeoElement>();
		
		// create an instance of EuclideanView
		ec = new EuclidianController(kernel);
		boolean[] showAxes = { true, true };
		boolean showGrid = false;
		ev = new MyEuclidianView(ec, showAxes, showGrid);
		
		ev.removeMouseListener(ec);
		ev.removeMouseMotionListener(ec);
		ev.removeMouseWheelListener(ec);
		ev.setAxesCornerCoordsVisible(false);
		
		ev.setAntialiasing(true);
		ev.updateFonts();
		ev.setPreferredSize(new Dimension(300,200));
		ev.setSize(new Dimension(300,200));
		ev.updateSize();
		
		this.setLayout(new BorderLayout());
		this.add(ev, BorderLayout.CENTER);
		
		this.addComponentListener(this);
		
		
		regCmd[StatDialog.REG_NONE] = "";
		regCmd[StatDialog.REG_LINEAR] = "FitLine";
		regCmd[StatDialog.REG_LOG] = "FitLog";
		regCmd[StatDialog.REG_POLY] = "FitPoly";
		regCmd[StatDialog.REG_POW] = "FitPow";
		regCmd[StatDialog.REG_EXP] = "FitExp";
		regCmd[StatDialog.REG_SIN] = "FitSin";
		regCmd[StatDialog.REG_LOGISTIC] = "FitLogistic";
		
		
	}

	
	public void removeGeos(){
		//Application.debug("=========== remove plot geos");
		
		if(plotGeo != null){
			plotGeo.remove();
			plotGeo = null;
		}
		
		for(GeoElement geo : plotGeoCollection){
			if(geo != null)
				geo.remove();
		}
		plotGeoCollection.clear();
		
	}
	
	public void setAutoRemoveGeos(boolean isAutoRemoveGeos){
		this.isAutoRemoveGeos = isAutoRemoveGeos;
	}
	

	public void setEVParams(){
		
		ev.setShowAxis(EuclidianView.AXIS_Y, showYAxis, false);
		
		if(showArrows){
			ev.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		}else{
			ev.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_FULL);
		}
		
		ev.setDrawBorderAxes(isEdgeAxis);
		
		// ensure that the axis labels are shown
		// by forcing a fixed pixel height below the x-axis
		
		if(forceXAxisBuffer){
			double pixelOffset = 30 * app.getSmallFont().getSize()/12.0;
			double pixelHeight = this.getHeight(); 
			yMinEV = - pixelOffset * yMaxEV / (pixelHeight + pixelOffset);
		}

		ev.setRealWorldCoordSystem(xMinEV, xMaxEV, yMinEV, yMaxEV);
	
		//Application.debug("setEVParms");
		ev.repaint();
	}
	
	
	public void setCoordSystem(double xMin, double xMax, double yMin, double yMax){
		xMinEV = xMin;
		xMaxEV = xMax;
		yMinEV = yMin;
		yMaxEV = yMax;
	}

	
	

	public EuclidianView getMyEuclidianView(){
		return ev;
	}
	
	public void attachView(){
		ev.attachView();
	}

	
	public void detachView(){
		kernel.detach(ev);
	}
	
	
	


	
	//=================================================
	//       Create GeoElement
	//=================================================

	
	public GeoElement createGeoFromString(String text ){
		return createGeoFromString(text, null);
	}
	
	
	public GeoElement createGeoFromString(String text, String label ){
		
		
		//Application.debug("geo creation text: " + text);
		
		if(isAutoRemoveGeos){
			removeGeos();
		}
			
		try {
				
			boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();
			
			if(label == null)
				cons.setSuppressLabelCreation(true);
			//Application.debug(text);
			GeoElement[] geos = kernel.getAlgebraProcessor()
				.processAlgebraCommandNoExceptions(text, false);	
			
			if(label != null)
				geos[0].setLabel(label);
			
			// add the geo to our view and remove it from EV		
			geos[0].addView(ev);
			ev.add(geos[0]);
			geos[0].removeView(app.getEuclidianView());
			app.getEuclidianView().remove(geos[0]);
				
			// set visibility
			geos[0].setEuclidianVisible(true);
			//geos[0].setAlgebraVisible(false);		
			geos[0].setAuxiliaryObject(true);
			geos[0].setLabelVisible(false);
			
			plotGeoCollection.add(geos[0]);
			
			if(label == null)
				cons.setSuppressLabelCreation(oldSuppressLabelMode);
		
			return geos[0];
				
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	


	private double evaluateExpression(String expr){

		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	

		return nv.getDouble();
	}

	

	
	//=================================================
	//       Plots
	//=================================================
	
	private void setDataMinMax(GeoList dataList){
		  setDataMinMax( dataList, false);
	}
	private void setDataMinMax(GeoList dataList, boolean isPointList){
	
		double x, y; // temp vars
		
		GeoNumeric geo;	
		xMinData = Double.MAX_VALUE;
		xMaxData = Double.MIN_VALUE;

		if(!isPointList){
			for (int i = 0; i < dataList.size(); ++i){
				x = ((GeoNumeric) ((GeoList)dataList).get(i)).getDouble();
				if(xMinData > x) xMinData = x;
				if(xMaxData < x) xMaxData = x;
			}
		}else{

			yMinData = Double.MAX_VALUE;
			yMaxData = Double.MIN_VALUE;

			for (int i = 0; i < dataList.size(); ++i){
				x = ((GeoPoint)(dataList.get(i))).getInhomX();
				y = ((GeoPoint)(dataList.get(i))).getInhomY();
				if(xMinData > x) xMinData = x;
				if(xMaxData < x) xMaxData = x;
				if(yMinData > y) yMinData = y;
				if(yMaxData < y) yMaxData = y;
			}	
		}

		
		// Application.debug(yMinData + "  " + yMaxData);	
		
		/*
		String label = dataList.getLabel();	
		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Min[" + label + "]", false);		
		xMinData = nv.getDouble();
		
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Max[" + label + "]", false);		
		xMaxData = nv.getDouble();
		*/
		
	}
	
	
	
	public GeoElement createPDF(String expr, double xMin, double xMax, double yMin, double yMax){
		
		// set view parameters	
		xMinEV = xMin;
		xMaxEV = xMax;
		yMinEV = yMin;
		yMaxEV = yMax;
		showYAxis = false;
		forceXAxisBuffer = true;
		
		setEVParams();
		ev.addMouseListener(ec);
		ev.addMouseMotionListener(ec);
		ev.addMouseWheelListener(ec);
		
		// create function
		String text = expr;
		return createGeoFromString(text,"pdf");
	
	}
	
	
	
	
	public void updateHistogram(GeoList dataList, int numClasses, boolean doCreate){
		
		setDataMinMax(dataList);
		
		//Application.debug(xMinData + "  " + xMaxData);	
		//Application.debug(dataList.toDefinedValueString());	
		
		String label = dataList.getLabel();	
		String text = "";
		
		
		// Set view parameters
		double barWidth = (xMaxData - xMinData)/(numClasses - 1);  
		double freqMax = getFrequencyTableMax(dataList, barWidth);
		double buffer = .25*(xMaxData - xMinData);
		xMinEV = xMinData - buffer;  
		xMaxEV = xMaxData + buffer;
		yMinEV = -1.0;
		yMaxEV = 1.1 * freqMax;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();
		
		//System.out.println(yMaxEV + "," + freqMax + ", "  + barWidth + "," + xMinData);
		
		// Create histogram	
	//	if(doCreate){
			text = "BarChart[" + label + "," + Double.toString(barWidth) + "]";
			plotGeo = createGeoFromString(text);
			plotGeo.setObjColor(StatDialog.HISTOGRAM_COLOR);
			plotGeo.setAlphaValue(0.25f);
	//	}
				
		//System.out.println(text);
	}
	

	public void updateBoxPlot(GeoList dataList, boolean doCreate){

		setDataMinMax(dataList);
		String label = dataList.getLabel();	
		String text = "";
		
		// Set view parameters	
		double buffer = .25*(xMaxData - xMinData);
		xMinEV = xMinData - buffer;
		xMaxEV = xMaxData + buffer;
		yMinEV = -1.0;
		yMaxEV = 2;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();
		
		// create boxplot
		GeoElement tempGeo;
		if(doCreate){
			text = "BoxPlot[1,0.5," + label + "]";
			tempGeo  = createGeoFromString(text);
			tempGeo.setObjColor(StatDialog.BOXPLOT_COLOR);
			tempGeo.setAlphaValue(0.25f);
		}
				
	}
	
	
	public void updateDotPlot(GeoList dataList, boolean doCreate){

		setDataMinMax(dataList);
		String label = dataList.getLabel();	
		String text = "";
	
		double buffer = .25*(xMaxData - xMinData);	
			
		GeoElement tempGeo = null;
		if(doCreate){
			text = "DotPlot[" + label + "]";
			tempGeo  = createGeoFromString(text);
			tempGeo.setObjColor(StatDialog.DOTPLOT_COLOR);
			tempGeo.setAlphaValue(0.25f);
		}
		
		
		// set view parameters		
		xMinEV = xMinData - buffer;
		xMaxEV = xMaxData + buffer;
		yMinEV = -1.0;
		yMaxEV = evaluateExpression("Max[y(" + tempGeo.getLabel() +  ")]") + 1;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();	
			
	}
	
	
	
	public void updateScatterPlot(GeoList dataList, boolean doCreate){

		removeGeos();
		
		setDataMinMax(dataList, true);
		
		GeoList tempGeo = new GeoList(cons);
		tempGeo.setAuxiliaryObject(true);
		tempGeo.setLabel(null);
		
		for(int i=0; i<dataList.size(); ++i)
			tempGeo.add(dataList.get(i));	
		
		
		// add the geo to our view and remove it from EV		
		tempGeo.addView(ev);
		ev.add(tempGeo);
		tempGeo.removeView(app.getEuclidianView());
		app.getEuclidianView().remove(tempGeo);
			
		// set visibility
		tempGeo.setEuclidianVisible(true);
		//geos[0].setAlgebraVisible(false);		
		tempGeo.setAuxiliaryObject(true);
		tempGeo.setLabelVisible(false);
			
		
		plotGeoCollection.add(tempGeo);
		
		/*
		setDataMinMax(dataList, true);
		String label = dataList.getLabel();	
		String text = "";
		
		GeoElement tempGeo = null;
		if(doCreate){
			
			text = label;
			
			//text = "{ FitLine[" + label + "] ," + label + "}";
			
			tempGeo  = createGeoFromString(text);
			tempGeo.setObjColor(StatDialog.DOTPLOT_COLOR);
			tempGeo.setAlphaValue(0.25f);
		}	
		
		*/
		
		
		// set view parameters	
		double xBuffer = .25*(xMaxData - xMinData);
		xMinEV = xMinData - xBuffer;
		xMaxEV = xMaxData + xBuffer;
		
		double yBuffer = .25*(yMaxData - yMinData);
		yMinEV = yMinData - yBuffer;
		yMaxEV = yMaxData + yBuffer;
		
		showYAxis = true;
		forceXAxisBuffer = false;
		setEVParams();
		
		
	}
	
	
	public void updateRegressionPlot(GeoList dataList, boolean doCreate, int regType, int order){
			
		if (regType == StatDialog.REG_NONE) return;

		setDataMinMax(dataList, true);
		String label = dataList.getLabel();	
		String text = regCmd[regType] + "[" + label + "]";

		if(regType == StatDialog.REG_POLY)
			text = regCmd[regType] + "[" + label + "," + order + "]";

		GeoElement tempGeo = null;
		// Regression cmds do not update with changes in data list
		// so we must create a new geo on every update.
		//	if(doCreate){
		tempGeo  = createGeoFromString(text);
		tempGeo.setObjColor(StatDialog.REGRESSION_COLOR);
		//	}

		if(regType == StatDialog.REG_LINEAR){	
			((GeoLine)tempGeo).setToExplicit();	
			regressionEqn = tempGeo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, true);

		}else if(regType == StatDialog.REG_NONE){
			regressionEqn = "";

		}else{
			regressionEqn = "y = " + tempGeo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, true);		
		}

		regModel = tempGeo;

		
		// set view parameters	
		double xBuffer = .25*(xMaxData - xMinData);
		xMinEV = xMinData - xBuffer;
		xMaxEV = xMaxData + xBuffer;
		
		double yBuffer = .25*(yMaxData - yMinData);
		yMinEV = yMinData - yBuffer;
		yMaxEV = yMaxData + yBuffer;
		
		showYAxis = true;
		forceXAxisBuffer = false;
		setEVParams();
			
	}
	
	
	
	
	//=================================================
	//       Frequency Table 
	//=================================================
	
	// get frequency table max
	// edited version of code in BarChart algo
	// TODO -- maybe we need a FrequencyTable[] command? ... then this is not needed
	
	private double getFrequencyTableMax(GeoList list1, double n){

		//Application.debug(list1.toDefinedValueString());
			
		double [] yval; // y value (= min) in interval 0 <= i < N
		double [] leftBorder; // leftBorder (x val) of interval 0 <= i < N
		GeoElement geo;	

		double mini = Double.MAX_VALUE;
		double maxi = Double.MIN_VALUE;
		int minIndex = -1;
		int maxIndex = -1;

		double step = n ;   //n.getDouble();
		int rawDataSize = list1.size();

		if (step < 0 || Kernel.isZero(step) || rawDataSize < 2)
		{
			return 0;
		}


		// find max and min
		for (int i = 0; i < rawDataSize; i++) {
			geo = list1.get(i);
			if (!geo.isGeoNumeric()) {
				return 0;
			}
			double val = ((GeoNumeric)geo).getDouble();

			if (val > maxi) {
				maxi = val;
				maxIndex = i;
			}
			if (val < mini) {
				mini = val;
				minIndex = i;
			}
		}

		if (maxi == mini || maxIndex == -1 || minIndex == -1) {
			return 0;
		}

		double totalWidth = maxi - mini;
		double noOfBars = totalWidth / n;    //n.getDouble();
		double gap = 0;

		int N = (int)noOfBars + 2;
		gap = ((N-1) * step - totalWidth) / 2.0;
		
		/*
		System.out.println("==========================");
		System.out.println("N " + N);
		System.out.println("n " + n);
		System.out.println("step " + step);
		System.out.println("gap " + gap);
		*/
		
		
		NumberValue a = (new GeoNumeric(cons,mini - gap));
		NumberValue b = (new GeoNumeric(cons,maxi + gap));

		yval = new double[N];
		leftBorder = new double[N];


		// fill in class boundaries
		//double width = (maxi-mini)/(double)(N-2);
		for (int i=0; i < N; i++) {
			leftBorder[i] = mini - gap + step * i;
		}
		
		
		// zero frequencies
		for (int i=0; i < N; i++) yval[i] = 0; 	

		// work out frequencies in each class
		double datum;

		for (int i=0; i < list1.size() ; i++) {
			geo = list1.get(i);
			if (geo.isGeoNumeric())	datum = ((GeoNumeric)geo).getDouble(); 
			else {  return 0; }

			// fudge to make the last boundary eg 10 <= x <= 20
			// all others are 10 <= x < 20
			double oldMaxBorder = leftBorder[N-1];
			leftBorder[N-1] += Math.abs(leftBorder[N-1] / 100000000);

			// check which class this datum is in
			for (int j=1; j < N; j++) {
				//System.out.println("left border " +leftBorder[j]);
				if (datum < leftBorder[j]) 
				{
					//System.out.println(datum + " " + j);
					yval[j-1]++;
					break;
				}
			}

			leftBorder[N-1] = oldMaxBorder;
		}

		double freqMax = 0.0;
		for(int k = 0; k < yval.length; ++k){
			//System.out.println(leftBorder[k] + "  : " + yval[k]);
				
			if(yval[k] > freqMax)
				freqMax = yval[k];
		}
		
		//System.out.println(freqMax);
		
		return freqMax;
		
	}


	
	
	//==================================================
	//       Component Listener  (for resizing our EV)
	//=================================================
	
	public void componentHidden(ComponentEvent arg0) {	
	}
	public void componentMoved(ComponentEvent arg0) {
	}
	public void componentResized(ComponentEvent arg0) {
		// make sure that we force a pixel buffer under the x-axis 
		setEVParams();
	}
	public void componentShown(ComponentEvent arg0) {
	}



}
