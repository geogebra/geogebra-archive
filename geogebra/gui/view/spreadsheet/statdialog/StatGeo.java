package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.kernel.AlgoBoxPlot;
import geogebra.kernel.AlgoClasses;
import geogebra.kernel.AlgoDependentListExpression;
import geogebra.kernel.AlgoDotPlot;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoFrequencyPolygon;
import geogebra.kernel.AlgoFunctionAreaSums;
import geogebra.kernel.AlgoHistogram;
import geogebra.kernel.AlgoListElement;
import geogebra.kernel.AlgoListMax;
import geogebra.kernel.AlgoListMin;
import geogebra.kernel.AlgoNormalQuantilePlot;
import geogebra.kernel.AlgoResidualPlot;
import geogebra.kernel.AlgoStemPlot;
import geogebra.kernel.AlgoText;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoFunctionable;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.FunctionVariable;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.statistics.AlgoFitExp;
import geogebra.kernel.statistics.AlgoFitLineY;
import geogebra.kernel.statistics.AlgoFitLog;
import geogebra.kernel.statistics.AlgoFitLogistic;
import geogebra.kernel.statistics.AlgoFitPoly;
import geogebra.kernel.statistics.AlgoFitPow;
import geogebra.kernel.statistics.AlgoFitSin;
import geogebra.kernel.statistics.AlgoMean;
import geogebra.kernel.statistics.AlgoStandardDeviation;
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

	public static final int TABLE_ONE_VAR = 0;
	public static final int TABLE_TWO_VAR = 1;
	public static final int TABLE_REGRESSION = 2;

	private boolean histogramRight;

	/*************************************************
	 * Constructs a GeoPlot instance
	 */
	public StatGeo(Application app){

		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();


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

/*
	private double evaluateExpression(String expr){

		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	

		return nv.getDouble();
	}*/



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

		//String label = dataList.getLabel();
		dataBounds = new double[4];

		if(isMatrix){
			
			GeoNumeric index = new GeoNumeric(cons, 1);
			AlgoListElement le = new AlgoListElement(cons, dataList, index);
			GeoList list = (GeoList) le.getGeoElements()[0];
			AlgoListMax maxAlgo = new AlgoListMax(cons, list);
			AlgoListMin minAlgo = new AlgoListMin(cons, list);
			cons.removeFromConstructionList(minAlgo);
			cons.removeFromConstructionList(maxAlgo);
			
			GeoNumeric maxGeo = (GeoNumeric) maxAlgo.getGeoElements()[0];
			GeoNumeric minGeo = (GeoNumeric) minAlgo.getGeoElements()[0];
			
			//dataBounds[0] = this.evaluateExpression("Min[ Element[" + label + ", 1] ]");
			//dataBounds[1] = this.evaluateExpression("Max[ Element[" + label + ", 1] ]");
			dataBounds[0] = ((GeoNumeric)minAlgo.getGeoElements()[0]).getDouble();
			dataBounds[1] = ((GeoNumeric)maxAlgo.getGeoElements()[0]).getDouble();
			
			//System.out.println(s + ":  " + dataBounds[0] + "--------" + dataBounds[0] );
			double min, max;
			for(int i = 1; i < dataList.size(); i++){
				//min = this.evaluateExpression("Min[ Element[" + label + "," + (i+1) + "]]");
				//max = this.evaluateExpression("Max[ Element[" + label + "," + (i+1) + "]]");
				
				index.setValue(i);
				index.updateCascade();
				min = minGeo.getDouble();
				max = maxGeo.getDouble();
				
				dataBounds[0] = Math.min(dataBounds[0], min);
				dataBounds[1] = Math.max(dataBounds[1], max);
			}
		}


		else if(isPointList){
			ExpressionNode enX = new ExpressionNode(kernel, dataList, ExpressionNode.XCOORD, null);
			ExpressionNode enY = new ExpressionNode(kernel, dataList, ExpressionNode.YCOORD, null);
			AlgoDependentListExpression listX = new AlgoDependentListExpression(cons, enX);
			AlgoDependentListExpression listY = new AlgoDependentListExpression(cons, enY);
			
			AlgoListMax maxX = new AlgoListMax(cons, (GeoList)listX.getGeoElements()[0]);
			AlgoListMax maxY = new AlgoListMax(cons, (GeoList)listY.getGeoElements()[0]);
			AlgoListMin minX = new AlgoListMin(cons, (GeoList)listX.getGeoElements()[0]);
			AlgoListMin minY = new AlgoListMin(cons, (GeoList)listY.getGeoElements()[0]);
			
			cons.removeFromConstructionList(listX);
			cons.removeFromConstructionList(listY);
			cons.removeFromConstructionList(maxX);
			cons.removeFromConstructionList(maxY);
			cons.removeFromConstructionList(minX);
			cons.removeFromConstructionList(minY);
			dataBounds[0] = ((GeoNumeric)minX.getGeoElements()[0]).getDouble();
			dataBounds[1] = ((GeoNumeric)maxX.getGeoElements()[0]).getDouble();
			dataBounds[2] = ((GeoNumeric)minY.getGeoElements()[0]).getDouble();
			dataBounds[3] = ((GeoNumeric)maxY.getGeoElements()[0]).getDouble();
			
			//dataBounds[0] = this.evaluateExpression("Min[x(" + label + ")]");
			//dataBounds[1] = this.evaluateExpression("Max[x(" + label + ")]");
			//dataBounds[2] = this.evaluateExpression("Min[y(" + label + ")]");
			//dataBounds[3] = this.evaluateExpression("Max[y(" + label + ")]");
		}else{
			AlgoListMax max = new AlgoListMax(cons, dataList);
			AlgoListMin min = new AlgoListMin(cons, dataList);
			cons.removeFromConstructionList(min);
			cons.removeFromConstructionList(max);

			dataBounds[0] = ((GeoNumeric)min.getGeoElements()[0]).getDouble();
			dataBounds[1] = ((GeoNumeric)max.getGeoElements()[0]).getDouble();
			//dataBounds[0] = this.evaluateExpression("Min[" + label + "]");
			//dataBounds[1] = this.evaluateExpression("Max[" + label + "]");
		}

		xMinData = dataBounds[0];
		xMaxData = dataBounds[1];
		yMinData = dataBounds[2];
		yMaxData = dataBounds[3];

	}



	public GeoElement createHistogram(GeoList dataList, int numClasses, StatPanelSettings settings, boolean isFrequencyPolygon){

		GeoElement geo;
		getDataBounds(dataList);
		double classWidth = (xMaxData - xMinData)/(numClasses); 
		
		AlgoElement al, al2;
		if(settings.useManualClasses){
			classWidth = settings.classWidth;
			al = new AlgoClasses(cons, dataList, new GeoNumeric(cons, settings.classStart), new GeoNumeric(cons, settings.classWidth), null);
		}else{
			al = new AlgoClasses(cons, dataList, null, null, new GeoNumeric(cons, numClasses));
		}
		cons.removeFromConstructionList(al);
		
		double density = -1;
		if(settings.type == StatPanelSettings.TYPE_RELATIVE)
			density = 1.0*classWidth/dataList.size();
		if(settings.type == StatPanelSettings.TYPE_NORMALIZED)
			density = 1.0/dataList.size();

		if(isFrequencyPolygon)
			al2 = new AlgoFrequencyPolygon(cons, new GeoBoolean(cons, settings.isCumulative), 
					(GeoList)al.getGeoElements()[0], dataList, new GeoBoolean(cons, true), new GeoNumeric(cons, density));
		else
			al2 = new AlgoHistogram(cons, new GeoBoolean(cons, settings.isCumulative), 
					(GeoList)al.getGeoElements()[0], dataList, new GeoBoolean(cons, true), new GeoNumeric(cons, density),histogramRight);
		cons.removeFromConstructionList(al2);


		geo = al2.getGeoElements()[0];
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
		//String label = dataList.getLabel();	
		//String text = "Normal[Mean[" + label + "],SD[" + label + "],x]";
		
		AlgoMean mean = new AlgoMean(cons, dataList);
		AlgoStandardDeviation sd = new AlgoStandardDeviation(cons, dataList);
		
		cons.removeFromConstructionList(mean);
		cons.removeFromConstructionList(sd);
		
		GeoElement meanGeo = mean.getGeoElements()[0];
		GeoElement sdGeo = sd.getGeoElements()[0];
		
		FunctionVariable x = new FunctionVariable(kernel);
		
		ExpressionNode normal = new ExpressionNode(kernel, x, ExpressionNode.MINUS, meanGeo);
		normal = new ExpressionNode(kernel, normal, ExpressionNode.DIVIDE, sdGeo);
		normal = new ExpressionNode(kernel, normal, ExpressionNode.POWER, new MyDouble(kernel,2.0));
		normal = new ExpressionNode(kernel, normal, ExpressionNode.DIVIDE, new MyDouble(kernel,-2.0));
		normal = new ExpressionNode(kernel, normal, ExpressionNode.EXP, null);
		normal = new ExpressionNode(kernel, normal, ExpressionNode.DIVIDE, new MyDouble(kernel,Math.sqrt(2*Math.PI)));
		normal = new ExpressionNode(kernel, normal, ExpressionNode.DIVIDE, sdGeo);
		
		Function f = new Function(normal, x);		
		geo = new GeoFunction(cons, f);

		//Application.debug(text);
		//geo = createGeoFromString(text);
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
		
		//String label = dataList.getLabel();	
		GeoElement geo;

		//String	text = "BoxPlot[1,0.5," + label + "]";
		//geo  = createGeoFromString(text);
		
		AlgoBoxPlot boxPlot = new AlgoBoxPlot(cons, new MyDouble(kernel, 1d), new MyDouble(kernel, 0.5), dataList);
		cons.removeFromConstructionList(boxPlot);
		geo = boxPlot.getGeoElements()[0];
		
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

	public GeoElement[] createMultipleBoxPlot(GeoList dataList){

		//String label = dataList.getLabel();	
		GeoElement geo;

		// Sequence[BoxPlot[k, 0.33333, Element[mm, k]], k, 1, Length[mm]]
		//String len = "Length[" + label + "]";
		//String	text = "Sequence[BoxPlot[k, 1/3, Element[" + label + "," + len + "-k+1]], k, 1," + len + "]";
		//geo  = createGeoFromString(text);
		
		//AlgoListLength len = new AlgoListLength(cons, dataList);
		//GeoNumeric num = (GeoNumeric) len.getGeoElements()[0];
		//cons.removeFromConstructionList(len);
		
		int length = dataList.size();
		
		GeoElement[] ret = new GeoElement[length];
		
		for (int i = 0 ; i < length ; i++) {
			AlgoBoxPlot bp = new AlgoBoxPlot(cons, new GeoNumeric(cons, i+1), new GeoNumeric(cons, 1d/3d), (GeoList)dataList.get((length-1)-i));
			cons.removeFromAlgorithmList(bp);
			ret[i] = bp.getGeoElements()[0];
			ret[i].setObjColor(StatDialog.BOXPLOT_COLOR);
			ret[i].setAlphaValue(0.25f);
			
		}
		
		return ret;		
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

	public GeoElement[] createBoxPlotTitles(StatDialog statDialog, PlotSettings ps){

		String[] dataTitles = statDialog.getDataTitles();	
		
		int length = dataTitles.length;
		GeoElement[] ret = new GeoElement[length];

		for (int i = 0; i < dataTitles.length; i++){
			//sb.append("Text[\"  " + dataTitles[dataTitles.length - i - 1] + "\", (" + ps.xMin + "," + (i+1) + ")],");
			GeoPoint p = new GeoPoint(cons, ps.xMin, i+1d, 1d);
			GeoText t = new GeoText(cons, ""+dataTitles[dataTitles.length - i - 1]);
			AlgoText text = new AlgoText(cons, t, p, null, null);
			cons.removeFromAlgorithmList(text);
			ret[i] = text.getGeoElements()[0];
			ret[i].setBackgroundColor(Color.WHITE);
			ret[i].setObjColor(Color.BLACK);
		}
		return ret;		
	}


	public GeoElement createDotPlot(GeoList dataList){

		//String label = dataList.getLabel();	
		//GeoElement geo;

		//String text = "DotPlot[" + label + "]";
		//geo  = createGeoFromString(text);
		
		AlgoDotPlot dp = new AlgoDotPlot(cons, dataList);
		cons.removeFromConstructionList(dp);
		GeoElement geo = dp.getGeoElements()[0];
		
		geo.setObjColor(StatDialog.DOTPLOT_COLOR);
		geo.setAlphaValue(0.25f);

		return geo;	
	}


	public PlotSettings updateDotPlot(GeoList dataList, GeoElement dotPlot){

		PlotSettings ps = new PlotSettings();

		getDataBounds(dataList);
		//String label = dataList.getLabel();	

		double buffer = .25*(xMaxData - xMinData);		
		ps.xMin = xMinData - buffer;
		ps.xMax = xMaxData + buffer;
		ps.yMin = -1.0;
		
		ExpressionNode en = new ExpressionNode(kernel, dotPlot, ExpressionNode.YCOORD, null);
		AlgoDependentListExpression list = new AlgoDependentListExpression(cons, en);
		
		AlgoListMax max = new AlgoListMax(cons, (GeoList)list.getGeoElements()[0]);
		
		cons.removeFromConstructionList(list);
		cons.removeFromConstructionList(max);

		//ps.yMax = evaluateExpression("Max[y(" + dotPlot.getLabel() +  ")]") + 1;
		ps.yMax = ((GeoNumeric)max.getGeoElements()[0]).getDouble() + 1;
		ps.showYAxis = false;
		ps.forceXAxisBuffer = true;

		return ps;

	}

	
	public GeoElement createNormalQuantilePlot(GeoList dataList){

		//String label = dataList.getLabel();	
		//GeoElement geo;

		//String text = "NormalQuantilePlot[" + label + "]";
		//geo  = createGeoFromString(text);
		
		AlgoNormalQuantilePlot qp = new AlgoNormalQuantilePlot(cons, dataList);
		cons.removeFromConstructionList(qp);
		GeoElement geo = qp.getGeoElements()[0];		
		
		geo.setObjColor(StatDialog.NQPLOT_COLOR);
		geo.setAlphaValue(0.25f);

		return geo;	
	}


	public PlotSettings updateNormalQuantilePlot(GeoList dataList){

		PlotSettings ps = new PlotSettings();

		getDataBounds(dataList);
		String label = dataList.getLabel();	

		double buffer = .25*(xMaxData - xMinData);		
		ps.xMin = xMinData - buffer;
		ps.xMax = xMaxData + buffer;
		ps.yMin = -4.0;
		ps.yMax = 4.0;
		ps.showYAxis = true;
		ps.isEdgeAxis[1]=true;
		ps.forceXAxisBuffer = false;

		return ps;

	}
	
	
	

	public GeoElement createScatterPlot(GeoList dataList){

		// copy the dataList geo
		GeoList geo = new GeoList(cons);
		geo.setAuxiliaryObject(true);
		//geo.setLabel("scatterPlotPointList");

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



	public GeoElement createRegressionPlot(GeoList dataList, int regType, int order, boolean residual){

		boolean regNone = false;
		
		AlgoElement algo;
		
		switch (regType) {
		case StatDialog.REG_LOG:
			algo = new AlgoFitLog(cons, dataList);
			break;
		case StatDialog.REG_POLY:
			algo = new AlgoFitPoly(cons, dataList, new MyDouble(kernel, order));
			break;
		case StatDialog.REG_POW:
			algo = new AlgoFitPow(cons, dataList);
			break;
		case StatDialog.REG_EXP:
			algo = new AlgoFitExp(cons, dataList);
			break;
		case StatDialog.REG_SIN:
			algo = new AlgoFitSin(cons, dataList);
			break;
		case StatDialog.REG_LOGISTIC:
			algo = new AlgoFitLogistic(cons, dataList);
			break;
		case StatDialog.REG_NONE:
			regNone = true;
			// fall through to linear
		case StatDialog.REG_LINEAR:
		default:
			algo = new AlgoFitLineY(cons, dataList);
			break;
		
		}
		
		cons.removeFromConstructionList(algo);
		GeoElement geo = algo.getGeoElements()[0];
		
		if (residual) {
			AlgoResidualPlot algoRP = new AlgoResidualPlot(cons, dataList, (GeoFunctionable) geo);
			geo = algoRP.getGeoElements()[0];
			geo.setObjColor(StatDialog.DOTPLOT_COLOR);
			geo.setAlphaValue(0.25f);
		} else {

			// set geo options
			geo.setObjColor(StatDialog.REGRESSION_COLOR);
			if(regType == StatDialog.REG_LINEAR)	
				((GeoLine)geo).setToExplicit();	
	
			// hide the dummy geo
			if(regNone) geo.setEuclidianVisible(false);
		}

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
/*
	public GeoElement createResidualPlot(GeoList dataList, int regType, int order){

		GeoElement geo = null;

		if (regType == StatDialog.REG_NONE){
			return new GeoList(cons);
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

	}*/

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

		//String label = dataList.getLabel();	

		//String	text = "StemPlot[" + label + "," + adjustment + "]";
		//tempGeo  = createGeoFromString(text);
		
		AlgoStemPlot sp = new AlgoStemPlot(cons, dataList, new GeoNumeric(cons, adjustment));
		GeoElement tempGeo = sp.getGeoElements()[0];
		cons.removeFromConstructionList(sp);
		
		String latex = tempGeo.getLaTeXdescription();
		tempGeo.remove();

		return latex;		
	}

}
