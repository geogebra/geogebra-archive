package geogebra.gui.view.spreadsheet;

import geogebra.kernel.AlgoFunctionAreaSums;
import geogebra.kernel.AlgoResidualPlot;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.Color;

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
	private double[] dataBounds;

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
		return createGeoFromString(text, null, false);
	}
	public GeoElement createGeoFromString(String text, String label){
		return createGeoFromString(text, null, false);
	}
	public GeoElement createGeoFromString(String text, String label, boolean suppressLabelCreation ){

		try {

			boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();

			if(suppressLabelCreation)
				cons.setSuppressLabelCreation(true);
			//	Application.debug(text);
			GeoElement[] geos = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptions(text, false);	

			if(label != null)
				geos[0].setLabel(label);

			// set visibility
			geos[0].setEuclidianVisible(true);	
			geos[0].setAuxiliaryObject(true);
			geos[0].setLabelVisible(false);

			if(suppressLabelCreation)
				cons.setSuppressLabelCreation(oldSuppressLabelMode);

			return geos[0];

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



	public GeoElement redefineGeoFromString(GeoElement geo, String newValue){

		try {

			geo = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(geo, newValue, true, false);

			// set visibility
			geo.setEuclidianVisible(true);
			geo.setAuxiliaryObject(true);
			geo.setLabelVisible(false);

			return geo;

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
		return createBasicStatList(dataList, mode, null);
	}

	public GeoList  createBasicStatList(GeoList dataList, int mode, GeoElement regFunction){

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
					{app.getMenu("Sum2") ,"SigmaXX"},
					{null , null},
					{app.getMenu("Minimum.short") ,"Min"},
					{app.getMenu("LowerQuartile.short") ,"Q1"},
					{app.getMenu("Median") ,"Median"},
					{app.getMenu("UpperQuartile.short") ,"Q3"},
					{app.getMenu("Maximum.short") ,"Max"},

			};

			text = createStatListString(statMap1, label);			
			break;


		case TABLE_TWO_VAR:

			String[][]statMap2 = {
					{app.getMenu("Length.short") ,"Length"},
					{app.getMenu("MeanX") ,"MeanX"},
					{app.getMenu("MeanY") ,"MeanY"},
					//TODO add SDX etc cmds
					//{app.getMenu("Sx") ,"SampleSDx"},
					//{app.getMenu("Sy") ,"SampleSDY"},
					{app.getMenu("CorrelationCoefficient.short") ,"PMCC"},
					{app.getMenu("Spearman.short") ,"Spearman"},
					{app.getMenu("Sxx") ,"Sxx"},
					{app.getMenu("Syy") ,"Syy"},
					{app.getMenu("Sxy") ,"Sxy"},
					{null , null},
					//TODO --- these cmds won't work, why?
					//{app.getMenu("RSquare") ,"RSquare"}
					//{app.getMenu("SSE") ,"SumSquaredErrors"}
			};

			if(regFunction != null)
				text = createStatListString(statMap2, label, regFunction.getLabel());
			else
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
		return createStatListString(statMap, geoLabel, null);
	}
	private String createStatListString(String[][] statMap, String geoLabel, String regFunctionLabel){

		//TODO use stringbuilder

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

				if(cmdStr.equals("RSquare") || cmdStr.equals("SumSquaredErrors")){
					if(regFunctionLabel != null)
						text += cmdStr + "[" + geoLabel + "," + regFunctionLabel + "]";
					text += cmdStr + "[" + geoLabel + "]";
				}else{
					text += cmdStr + "[" + geoLabel + "]";
				}

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

	private void getDataBounds(GeoList dataList){
		getDataBounds( dataList, false, false);
	}
	private void getDataBounds(GeoList dataList, boolean isPointList){
		getDataBounds( dataList, true, false);
	}
	private void getDataBounds(GeoList dataList, boolean isPointList, boolean isMatrix){

		String label = dataList.getLabel();
		dataBounds = new double[4];

		if(isMatrix){
			String s = dataList.get(0).getCommandDescription();
			dataBounds[0] = this.evaluateExpression("Min[" + s + "]");
			dataBounds[1] = this.evaluateExpression("Max[" + s + "]");
			//System.out.println(s + ":  " + dataBounds[0] + "--------" + dataBounds[0] );
			double min, max;
			for(int i = 1; i < dataList.size(); i++){
				s = dataList.get(i).getCommandDescription();
				min = this.evaluateExpression("Min[" + s + "]");
				max = this.evaluateExpression("Max[" + s + "]");
				dataBounds[0] = Math.min(dataBounds[0], min);
				dataBounds[1] = Math.max(dataBounds[1], max);
			}
		}


		else if(isPointList){
			dataBounds[0] = this.evaluateExpression("Min[x(" + label + ")]");
			dataBounds[1] = this.evaluateExpression("Max[x(" + label + ")]");
			dataBounds[2] = this.evaluateExpression("Min[y(" + label + ")]");
			dataBounds[3] = this.evaluateExpression("Max[y(" + label + ")]");
		}else{
			dataBounds[0] = this.evaluateExpression("Min[" + label + "]");
			dataBounds[1] = this.evaluateExpression("Max[" + label + "]");
		}

		xMinData = dataBounds[0];
		xMaxData = dataBounds[1];
		yMinData = dataBounds[2];
		yMaxData = dataBounds[3];

	}



	public GeoElement createHistogram(GeoList dataList, int numClasses, StatPanelSettings settings, boolean isFrequencyPolygon){

		GeoElement geo;
		String label = dataList.getLabel();	
		String classes;
		getDataBounds(dataList);
		double classWidth = (xMaxData - xMinData)/(numClasses); 

		if(settings.useManualClasses){
			classWidth = settings.classWidth;
			classes = "Classes[" + label + "," + settings.classStart + "," + settings.classWidth + "]";
		}else{
			classes = "Classes[" + label + "," + numClasses + "]";
		}
		//Application.debug(classWidth);

		double density = -1;
		if(settings.type == StatPanelSettings.TYPE_RELATIVE)
			density = 1.0*classWidth/dataList.size();
		if(settings.type == StatPanelSettings.TYPE_NORMALIZED)
			density = 1.0/dataList.size();

		String text;
		if(isFrequencyPolygon)
			text = "FrequencyPolygon[" + settings.isCumulative + "," + classes + "," +  label + ",true," + density + "]";
		else
			text = "Histogram[" + settings.isCumulative + "," + classes + "," +  label + ",true," + density + "]";

		//Application.debug(text);
		geo = createGeoFromString(text);
		if(isFrequencyPolygon){
			geo.setObjColor(Color.BLACK);
		}else{
			geo.setObjColor(StatDialog.HISTOGRAM_COLOR);
			geo.setAlphaValue(0.25f);
		}
		return geo;	
	}



	public GeoElement createNormalCurveOverlay(GeoList dataList){

		GeoElement geo;
		String label = dataList.getLabel();	
		String text = "Normal[Mean[" + label + "],SD[" + label + "],x]";

		//Application.debug(text);
		geo = createGeoFromString(text);
		geo.setObjColor(Color.BLACK);

		return geo;	
	}



	public PlotSettings getHistogramSettings(GeoList dataList, GeoElement histogram, StatPanelSettings settings){	

		PlotSettings ps = new PlotSettings();	
		getDataBounds(dataList);	

		double freqMax = ((AlgoFunctionAreaSums)histogram.getParentAlgorithm()).getFreqMax();
		//if(settings.type == StatPanelSettings.TYPE_RELATIVE)
		//freqMax = 1.0;

		if(settings.useManualClasses){
			double[] leftBorder = ((AlgoFunctionAreaSums)histogram.getParentAlgorithm()).getLeftBorder();
			xMinData = leftBorder[0];
			xMaxData = leftBorder[leftBorder.length-1];
		}

		yMinData = 0.0;
		yMaxData = freqMax;

		ps = setXYBounds(ps, settings, .2, .1);

		//	double buffer = .25*(xMaxData - xMinData);
		//	ps.xMin = xMinData - buffer;  
		//	ps.xMax = xMaxData + buffer;
		//	ps.yMin = -1.0;
		//	ps.yMax = 1.1 * freqMax;
		ps.showYAxis = true;
		ps.isEdgeAxis[0] = false;
		ps.isEdgeAxis[1] = true;
		ps.isPositiveOnly[1] = true;
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

	public PlotSettings getBoxPlotSettings(GeoList dataList){

		PlotSettings ps = new PlotSettings();

		getDataBounds(dataList);

		double buffer = .25*(xMaxData - xMinData);
		ps.xMin = xMinData - buffer;
		ps.xMax = xMaxData + buffer;
		ps.yMin = -1.0;
		ps.yMax = 2;
		ps.showYAxis = false;
		ps.forceXAxisBuffer = true;

		return ps;

	}

	public GeoElement createMultipleBoxPlot(GeoList dataList){

		String label = dataList.getLabel();	
		GeoElement geo;

		// Sequence[BoxPlot[k, 0.33333, Element[mm, k]], k, 1, Length[mm]]
		String len = "Length[" + label + "]";
		String	text = "Sequence[BoxPlot[k, 1/3, Element[" + label + "," + len + "-k+1]], k, 1," + len + "]";
		geo  = createGeoFromString(text);
		geo.setObjColor(StatDialog.BOXPLOT_COLOR);
		geo.setAlphaValue(0.25f);
		return geo;		
	}

	public PlotSettings getMultipleBoxPlotSettings(GeoList dataList){

		PlotSettings ps = new PlotSettings();

		getDataBounds(dataList, false,true);		
		double buffer = .25*(xMaxData - xMinData);
		ps.xMin = xMinData - buffer;
		ps.xMax = xMaxData + buffer;
		ps.yMin = -1.0;
		ps.yMax = dataList.size()+1;
		ps.showYAxis = false;
		ps.forceXAxisBuffer = true;

		return ps;

	}

	public GeoElement createBoxPlotTitles(StatDialog statDialog, PlotSettings ps){

		String[] dataTitles = statDialog.getDataTitles();	
		GeoElement geo;

		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (int i = 0; i < dataTitles.length; i++){
			sb.append("Text[\"  " + dataTitles[dataTitles.length - i - 1] + "\", (" + ps.xMin + "," + (i+1) + ")],");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("}");
		geo  = createGeoFromString(sb.toString());
		geo.setBackgroundColor(Color.WHITE);
		geo.setObjColor(Color.BLACK);
		//((GeoList)geo).setFontStyle(Font.BOLD);
		return geo;		
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

		getDataBounds(dataList);
		String label = dataList.getLabel();	

		double buffer = .25*(xMaxData - xMinData);		
		ps.xMin = xMinData - buffer;
		ps.xMax = xMaxData + buffer;
		ps.yMin = -1.0;
		ps.yMax = evaluateExpression("Max[y(" + dotPlot.getLabel() +  ")]") + 1;
		ps.showYAxis = false;
		ps.forceXAxisBuffer = true;

		return ps;

	}


	public GeoElement createScatterPlot(GeoList dataList){

		// copy the dataList geo
		GeoList geo = new GeoList(cons);
		geo.setAuxiliaryObject(true);
		geo.setLabel("scatterPlotPointList");

		for(int i=0; i<dataList.size(); ++i)
			geo.add(dataList.get(i));	

		// set visibility
		geo.setEuclidianVisible(true);	
		geo.setAuxiliaryObject(true);
		geo.setLabelVisible(false);	
		geo.setObjColor(StatDialog.DOTPLOT_COLOR);
		geo.setAlphaValue(0.25f);

		return geo;


	}

	public PlotSettings getScatterPlotSettings(GeoList dataList, StatPanelSettings settings){

		PlotSettings ps = new PlotSettings();	
		getDataBounds(dataList, true);	

		ps = setXYBounds(ps, settings);

		ps.showYAxis = true;
		ps.forceXAxisBuffer = false;
		ps.isEdgeAxis[0] = true;
		ps.isEdgeAxis[1] = true;
		ps.isPositiveOnly[0] = true;
		ps.isPositiveOnly[1] = true;
		return ps;		

	}



	public GeoElement createRegressionPlot(GeoList dataList, int regType, int order){

		// if regression mode = none a dummy linear model is 
		// created with visibility set to false
		boolean regNone = regType == StatDialog.REG_NONE;
		if(regNone) regType = StatDialog.REG_LINEAR;

		// create the geo
		String label = dataList.getLabel();	
		String text = regCmd[regType] + "[" + label + "]";
		if(regType == StatDialog.REG_POLY)
			text = regCmd[regType] + "[" + label + "," + order + "]";
		GeoElement geo  = createGeoFromString(text);	

		// set geo options
		geo.setObjColor(StatDialog.REGRESSION_COLOR);
		if(regType == StatDialog.REG_LINEAR)	
			((GeoLine)geo).setToExplicit();	

		// hide the dummy geo
		if(regNone) geo.setEuclidianVisible(false);

		return geo;

	}



	public void updateRegressionPlot(PlotSettings ps, GeoList dataList){

		getDataBounds(dataList, true);

		double xBuffer = .25*(xMaxData - xMinData);
		ps.xMin = xMinData - xBuffer;
		ps.xMax = xMaxData + xBuffer;

		double yBuffer = .25*(yMaxData - yMinData);
		ps.yMin = yMinData - yBuffer;
		ps.yMax = yMaxData + yBuffer;

		ps.showYAxis = true;
		ps.forceXAxisBuffer = false;

	}

	public GeoElement createResidualPlot(GeoList dataList, int regType, int order){

		GeoElement geo = null;

		if (regType == StatDialog.REG_NONE){
			return createGeoFromString("{}");
		}

		String label = dataList.getLabel();	

		String regFcn = regCmd[regType] + "[" + label + "]";
		if(regType == StatDialog.REG_POLY)
			regFcn = regCmd[regType] + "[" + label + "," + order + "]";

		String text = "ResidualPlot[" + label + "," + regFcn + "]";
		geo  = createGeoFromString(text);
		geo.setObjColor(StatDialog.DOTPLOT_COLOR);
		geo.setAlphaValue(0.25f);

		return geo;

	}

	public PlotSettings getResidualPlotSettings(GeoList dataList, GeoElement residualPlot, StatPanelSettings settings){

		PlotSettings ps = new PlotSettings();	

		getDataBounds(dataList, true);	

		double[] residualBounds = ((AlgoResidualPlot)residualPlot.getParentAlgorithm()).getResidualBounds();
		yMaxData = Math.max(Math.abs(residualBounds[0]),Math.abs(residualBounds[1]));
		yMinData = -yMaxData;

		ps = setXYBounds(ps, settings);

		ps.showYAxis = true;
		ps.forceXAxisBuffer = false;
		ps.isEdgeAxis[0] = false;
		ps.isEdgeAxis[1] = true;
		ps.isPositiveOnly[0] = true;
		ps.isPositiveOnly[1] = false;
		return ps;

	}

	private PlotSettings setXYBounds(PlotSettings ps, StatPanelSettings settings){
		return setXYBounds( ps,  settings, .2, .2);
	}

	private PlotSettings setXYBounds(PlotSettings ps, StatPanelSettings settings, double xBufferScale, double yBufferScale){

		if(settings.isAutomaticWindow){

			double xBuffer = xBufferScale*(xMaxData - xMinData);
			settings.xMin = xMinData - xBuffer;
			settings.xMax = xMaxData + xBuffer;

			double yBuffer = yBufferScale*(yMaxData - yMinData);
			settings.yMin = yMinData - yBuffer;
			settings.yMax = yMaxData + yBuffer;
		}

		ps.xMin = settings.xMin;
		ps.xMax = settings.xMax;
		ps.yMin = settings.yMin;
		ps.yMax = settings.yMax;
		ps.xAxesInterval = settings.xInterval;
		ps.yAxesInterval = settings.yInterval;


		ps.showGrid = settings.showGrid;
		return ps;
	}


	public String getStemPlotLatex(GeoList dataList, int adjustment){

		String label = dataList.getLabel();	
		GeoElement tempGeo;

		String	text = "StemPlot[" + label + "," + adjustment + "]";
		tempGeo  = createGeoFromString(text);
		String latex = tempGeo.getLaTeXdescription();
		tempGeo.remove();

		return latex;		
	}

}
