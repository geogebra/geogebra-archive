package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

/**
 * 
 * Creates geos for use in plot panels and provides updates to plot panel settings 
 * based on these geos.
 * 
 */
public class StatGeo   {

	private Application app;
	private Kernel kernel; 
	private Construction cons;

	private double xMinData, xMaxData, yMinData, yMaxData;		
	private String[] regCmd = new String[StatDialog.regressionTypes];


	public static final int TABLE_ONE_VAR = 0;
	public static final int TABLE_TWO_VAR = 1;
	public static final int TABLE_REGRESSION = 2;



	/*************************************************
	 * Constructs a GeoPlot instance
	 */
	public StatGeo(Application app){

		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();

		regCmd[StatDialog.REG_NONE] = "";
		regCmd[StatDialog.REG_LINEAR] = "FitLine";
		regCmd[StatDialog.REG_LOG] = "FitLog";
		regCmd[StatDialog.REG_POLY] = "FitPoly";
		regCmd[StatDialog.REG_POW] = "FitPow";
		regCmd[StatDialog.REG_EXP] = "FitExp";
		regCmd[StatDialog.REG_SIN] = "FitSin";
		regCmd[StatDialog.REG_LOGISTIC] = "FitLogistic";		

	}


	//=================================================
	//       Create GeoElement
	//=================================================


	public GeoElement createGeoFromString(String text ){
		return createGeoFromString(text, null);
	}

	public GeoElement createGeoFromString(String text, String label ){

		try {

			boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();

			if(label == null)
				cons.setSuppressLabelCreation(true);
			//Application.debug(text);
			GeoElement[] geos = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptionHandling(text, false);	

			if(label != null)
				geos[0].setLabel(label);

			// add the geo to our view and remove it from EV
			/*
			geos[0].addView(ev);
			ev.add(geos[0]);
			geos[0].removeView(app.getEuclidianView());
			app.getEuclidianView().remove(geos[0]);
			 */


			// set visibility
			geos[0].setEuclidianVisible(true);
			//geos[0].setAlgebraVisible(false);		
			geos[0].setAuxiliaryObject(true);
			geos[0].setLabelVisible(false);

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


	//==========================================================
	
	public GeoText createXTitle(String titleString){
		GeoText geo = null;
		String	text = '\"' + titleString + '\"';
		geo  = (GeoText) createGeoFromString(text);
		return geo;		
	}
	
	
	

	public GeoList  createBasicStatList(GeoList dataList, int mode){

		GeoList statList = null;
		String label = dataList.getLabel();	      //getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);  
		String text = "";
		
		switch(mode){
		case TABLE_ONE_VAR:

			String[][]statMap1 = { 
					{app.getMenu("Length.short") ,"Length"},
					{app.getMenu("Mean") ,"Mean"},
					{app.getMenu("StandardDeviation.short") ,"SD"},
					{app.getMenu("SampleStandardDeviation.short") ,"SampleSD"},
					{app.getMenu("Sum") ,"Sum"},
					{null , null},
					{app.getMenu("Minimum.short") ,"Min"},
					{app.getMenu("LowerQuartile.short") ,"Q1"},
					{app.getMenu("Median") ,"Median"},
					{app.getMenu("UpperQuartile.short") ,"Q3"},
					{app.getMenu("Maximum.short") ,"Max"}
			};

			text = createStatListString(statMap1, label);			
			break;


		case TABLE_TWO_VAR:

			String[][]statMap2 = {
					{app.getMenu("Length.short") ,"Length"},
					{app.getMenu("MeanX") ,"MeanX"},
					{app.getMenu("MeanY") ,"MeanY"},
					{app.getMenu("CorrelationCoefficient.short") ,"PMCC"},
					{app.getMenu("Sxx") ,"Sxx"},
					{app.getMenu("Syy") ,"Syy"},
					{app.getMenu("Sxy") ,"Sxy"},
			};

			text = createStatListString(statMap2, label);	
			break;
	

		}

		//System.out.println(text);	

		try {

			statList = (GeoList) createGeoFromString(text,"statList");

		} catch (Exception ex) {
			Application.debug("Creating list failed with exception: " + ex);
		}	

		return statList;

	}


	public GeoList  createRegressionAnalysisList(GeoList dataList, GeoElement regressionModel){

		GeoList list = null;
		String q = "\"";
		String expr = "";
		String args = "";
		
		if(regressionModel == null)
			expr = "{{" + q + q + "," + q + q + "}}";
		
		else
		{
			args = dataList.getLabel() + "," + regressionModel.getLabel();	
			expr = "{";			
			expr += "{" + q + app.getMenu("RSquare") + q + "," + "RSquare["  + args + "]},";
			expr += "{" + q + app.getMenu("SumSquaredErrors") + q + "," + "SumSquaredErrors["  + args + "]}";
			expr += "}";
		}

		//System.out.println(expr);	
		list = (GeoList) createGeoFromString(expr,"regAnalysis");

		return list;

	}





	private String createStatListString(String[][] statMap, String geoLabel){

		String text = "";
		String nameStr = "";
		String cmdStr = "";

		// generate a list of lists
		// e.g. { {name, cmd[]}, {name, cmd[]} }
		text += "{";
		for(int i = 0 ; i < statMap.length; ++ i){

			nameStr = statMap[i][0];
			cmdStr = statMap[i][1];

			// create an interior list 
			// e.g. { "Mean" , Mean[geoLabel] }
			if(cmdStr == null){
				text += "{ \"\", \"\"}";

			}else{
				text += "{";
				text += "\"" + nameStr + "\",";
				text += cmdStr + "[" + geoLabel + "]";
				text += "}";
			}


			// add comma delimiter
			if(i<statMap.length - 1)
				text += ",";
		}
		text += "}";

		return text; 
	}




	//=================================================
	//       Plots and Updates
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
	}



	public GeoElement createPDF(String expr){

		return createGeoFromString(expr,"pdf");

	}


	public PlotSettings updatePDF(String expr, double xMin, double xMax, double yMin, double yMax){

		PlotSettings ps = new PlotSettings();	
		// set view parameters	
		ps.xMinEV = xMin;
		ps.xMaxEV = xMax;
		ps.yMinEV = yMin;
		ps.yMaxEV = yMax;
		ps.showYAxis = false;
		ps.forceXAxisBuffer = true;
		ps.isEdgeAxis[0] = false;
		ps.isEdgeAxis[1] = false;

		/*
		setEVParams();
		ev.addMouseListener(ec);
		ev.addMouseMotionListener(ec);
		ev.addMouseWheelListener(ec);
		 */

		return ps;

	}



	public GeoElement createHistogram(GeoList dataList, int numClasses){

		GeoElement geo;
		String label = dataList.getLabel();	
		double barWidth = (xMaxData - xMinData)/(numClasses - 1); 

		String text = "BarChart[" + label + "," + Double.toString(barWidth) + "]";
		geo = createGeoFromString(text);
		geo.setObjColor(StatDialog.HISTOGRAM_COLOR);
		geo.setAlphaValue(0.25f);
		return geo;	
	}


	public PlotSettings updateHistogram(GeoList dataList, int numClasses){	

		PlotSettings ps = new PlotSettings();	
		setDataMinMax(dataList);	

		double barWidth = (xMaxData - xMinData)/(numClasses - 1);  
		double freqMax = getFrequencyTableMax(dataList, barWidth);
		double buffer = .25*(xMaxData - xMinData);
		ps.xMinEV = xMinData - buffer;  
		ps.xMaxEV = xMaxData + buffer;
		ps.yMinEV = -1.0;
		ps.yMaxEV = 1.1 * freqMax;
		ps.showYAxis = false;
		ps.forceXAxisBuffer = true;

		return ps;

	}


	public GeoElement createBoxPlot(GeoList dataList){

		String label = dataList.getLabel();	
		GeoElement geo;

		String	text = "BoxPlot[1,0.5," + label + "]";
		geo  = createGeoFromString(text);
		geo.setObjColor(StatDialog.BOXPLOT_COLOR);
		geo.setAlphaValue(0.25f);
		return geo;		
	}

	public PlotSettings updateBoxPlot(GeoList dataList){

		PlotSettings ps = new PlotSettings();

		setDataMinMax(dataList);		
		double buffer = .25*(xMaxData - xMinData);
		ps.xMinEV = xMinData - buffer;
		ps.xMaxEV = xMaxData + buffer;
		ps.yMinEV = -1.0;
		ps.yMaxEV = 2;
		ps.showYAxis = false;
		ps.forceXAxisBuffer = true;

		return ps;

	}


	public GeoElement createDotPlot(GeoList dataList){

		String label = dataList.getLabel();	
		GeoElement geo;

		String text = "DotPlot[" + label + "]";
		geo  = createGeoFromString(text);
		geo.setObjColor(StatDialog.DOTPLOT_COLOR);
		geo.setAlphaValue(0.25f);

		return geo;	
	}


	public PlotSettings updateDotPlot(GeoList dataList, GeoElement dotPlot){

		PlotSettings ps = new PlotSettings();

		setDataMinMax(dataList);
		String label = dataList.getLabel();	

		double buffer = .25*(xMaxData - xMinData);		
		ps.xMinEV = xMinData - buffer;
		ps.xMaxEV = xMaxData + buffer;
		ps.yMinEV = -1.0;
		ps.yMaxEV = evaluateExpression("Max[y(" + dotPlot.getLabel() +  ")]") + 1;
		ps.showYAxis = false;
		ps.forceXAxisBuffer = true;

		return ps;

	}


	public GeoElement createScatterPlot(GeoList dataList){

		// copy the dataList geo
		GeoList geo = new GeoList(cons);
		geo.setAuxiliaryObject(true);
		geo.setLabel(null);

		for(int i=0; i<dataList.size(); ++i)
			geo.add(dataList.get(i));	

		// set visibility
		geo.setEuclidianVisible(true);	
		geo.setAuxiliaryObject(true);
		geo.setLabelVisible(false);	

		return geo;

	}

	public PlotSettings updateScatterPlot(GeoList dataList){

		PlotSettings ps = new PlotSettings();	
		setDataMinMax(dataList, true);	

		double xBuffer = .25*(xMaxData - xMinData);
		ps.xMinEV = xMinData - xBuffer;
		ps.xMaxEV = xMaxData + xBuffer;

		double yBuffer = .25*(yMaxData - yMinData);
		ps.yMinEV = yMinData - yBuffer;
		ps.yMaxEV = yMaxData + yBuffer;

		ps.showYAxis = true;
		ps.forceXAxisBuffer = false;

		return ps;		
	}



	public GeoElement createRegressionPlot(GeoList dataList, int regType, int order){

		GeoElement geo = null;

		if (regType == StatDialog.REG_NONE) return geo;

		String label = dataList.getLabel();	

		String text = regCmd[regType] + "[" + label + "]";
		if(regType == StatDialog.REG_POLY)
			text = regCmd[regType] + "[" + label + "," + order + "]";

		geo  = createGeoFromString(text);
		geo.setObjColor(StatDialog.REGRESSION_COLOR);

		if(regType == StatDialog.REG_LINEAR)	
			((GeoLine)geo).setToExplicit();	

		return geo;

	}


	public void updateRegressionPlot(PlotSettings ps, GeoList dataList){

		setDataMinMax(dataList, true);

		double xBuffer = .25*(xMaxData - xMinData);
		ps.xMinEV = xMinData - xBuffer;
		ps.xMaxEV = xMaxData + xBuffer;

		double yBuffer = .25*(yMaxData - yMinData);
		ps.yMinEV = yMinData - yBuffer;
		ps.yMaxEV = yMaxData + yBuffer;

		ps.showYAxis = true;
		ps.forceXAxisBuffer = false;

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
}
