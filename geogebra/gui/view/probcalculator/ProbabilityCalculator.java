package geogebra.gui.view.probcalculator;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.view.spreadsheet.statdialog.PlotPanelEuclidianView;
import geogebra.gui.view.spreadsheet.statdialog.PlotSettings;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.AlgoBarChart;
import geogebra.kernel.AlgoDependentNumber;
import geogebra.kernel.AlgoIntegralDefinite;
import geogebra.kernel.AlgoPointOnPath;
import geogebra.kernel.AlgoTake;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoAxis;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialog that displays the graphs of various probability density functions with
 * interactive controls for calculating interval probabilities.
 * 
 * @author G. Sturr
 * 
 */
public class ProbabilityCalculator extends JPanel implements View, ActionListener, FocusListener, ChangeListener   {

	// enable/disable integral ---- use for testing
	private boolean hasIntegral = true; 

	//ggb fields
	private Application app;
	private Construction cons;
	private Kernel kernel; 
	private ProbabilityManager probManager;
	private ProbabiltyCalculatorStyleBar styleBar;


	// selected distribution mode
	private int selectedDist = ProbabilityManager.DIST_NORMAL;  // default: startup with normal distribution


	// distribution fields 
	private String[][] parameterLabels;
	private final static int maxParameterCount = 3; // maximum number of parameters allowed for a distribution
	private double[] parameters;
	private boolean isCumulative = false;

	// maps for the distribution ComboBox 
	private HashMap<Integer, String> distributionMap;
	private HashMap<String, Integer> reverseDistributionMap;


	// GeoElements
	private ArrayList<GeoElement> plotGeoList;
	private GeoPoint lowPoint, highPoint;
	private GeoElement densityCurve, integral;
	private GeoElement discreteGraph, discreteIntervalGraph;
	private GeoList discreteValueList, discreteProbList, intervalProbList, intervalValueList;
	//private GeoList parmList;
	private ArrayList<GeoElement> pointList;

	// label prefix for geos
	private static final String labelPrefix = "probcalc";


	// GUI elements
	private JComboBox comboDistribution, comboProbType;
	private JTextField[] fldParameterArray;
	private JTextField fldLow,fldHigh,fldResult;
	private JLabel[] lblParameterArray;
	private JLabel lblBetween, lblProbOf, lblEndProbOf,lblProb , lblDist;

	private JSlider[] sliderArray;
	private ListSeparatorRenderer comboRenderer;


	// GUI layout panels
	private JPanel controlPanel, distPanel, probPanel, tablePanel;
	private JSplitPane mainSplitPane, plotSplitPane;;	
	private int defaultDividerSize;  

	private PlotPanelEuclidianView plotPanel;
	private PlotSettings plotSettings;

	private ProbabilityTable table;


	// initing
	private boolean isIniting;
	private boolean isSettingAxisPoints = false;


	// probability calculation modes
	protected static final int PROB_INTERVAL = 0;
	protected static final int PROB_LEFT = 1;
	protected static final int PROB_RIGHT = 2;
	private int probMode = PROB_INTERVAL;


	//interval values
	private double low = 0, high = 1;

	// current probability result
	private double probability;


	// rounding 
	private int printDecimals = 4,  printFigures = -1;

	// valid prob interval flag
	boolean validProb;

	// colors
	private static final Color COLOR_PDF = GeoGebraColorConstants.DARKBLUE;
	private static final Color COLOR_PDF_FILL = GeoGebraColorConstants.BLUE;  
	private static final Color COLOR_POINT = Color.BLACK;

	private static final float opacityIntegral = 0.6f; 
	private static final float opacityDiscrete = 0.0f; // entire bar chart
	private static final float opacityDiscreteInterval = 0.6f; // bar chart interval
	private static final int thicknessCurve = 4;
	private static final int thicknessBarChart = 3;




	/*************************************************
	 * Construct the dialog
	 */
	public ProbabilityCalculator(Application app) {

		isIniting = true;
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();

		probManager = new ProbabilityManager(app, this);
		plotSettings = new PlotSettings();	
		plotGeoList = new ArrayList<GeoElement>();

		createLayoutPanels();
		buildLayout();
		isIniting = false;


		//setProbabilityCalculator(selectedDist, null, isCumulative);

		attachView();

	} 
	/**************** end constructor ****************/




	public void setProbabilityCalculator(int distributionType, double[] parameters, boolean isCumulative){

		this.selectedDist = distributionType;
		this.isCumulative = isCumulative;
		this.parameters = parameters;
		if(parameters == null)
			this.parameters = probManager.getDefaultParameterMap().get(selectedDist);	

		//this.buildLayout();
		//isIniting = true;
		updateAll();
		//isIniting = false;
	}


	/**
	 * @return The style bar for this view.
	 */
	public ProbabiltyCalculatorStyleBar getStyleBar() {
		if(styleBar == null) {
			styleBar = new ProbabiltyCalculatorStyleBar(app, this);
		}

		return styleBar;
	}



	//=================================================
	//       Getters/Setters
	//=================================================

	public ProbabilityManager getProbManager() {
		return probManager;
	}


	public double getLow() {
		return low;
	}
	public double getHigh() {
		return high;
	}

	public int getProbMode() {
		return probMode;
	}

	public boolean isCumulative() {
		return isCumulative;
	}
	public void setCumulative(boolean isCumulative) {
		this.isCumulative = isCumulative;
	}
	public int getPrintDecimals() {
		return printDecimals;
	}
	public int getPrintFigures() {
		return printFigures;
	}
	public PlotSettings getPlotSettings() {
		return plotSettings;
	}
	public void setPlotSettings(PlotSettings plotSettings) {
		this.plotSettings = plotSettings;
	}
	//=================================================
	//       GUI
	//=================================================


	private void createLayoutPanels() {

		try {		

			// control panel 
			//======================================================
			distPanel = this.createDistributionPanel();	
			probPanel = this.createProbabilityPanel();
			//distPanel.setBorder(BorderFactory.createEtchedBorder());
			//probPanel.setBorder(BorderFactory.createEtchedBorder());

			//probPanel.setBorder(BorderFactory.createCompoundBorder(
			//		BorderFactory.createEmptyBorder(2, 2, 2, 2),
			//		BorderFactory.createEtchedBorder())); 
			//distPanel.setBorder(probPanel.getBorder());


			Box vBox = Box.createVerticalBox();
			vBox.add(distPanel);
			vBox.add(probPanel);

			controlPanel = new JPanel(new BorderLayout());
			controlPanel.add(vBox, BorderLayout.NORTH);
			controlPanel.setBorder(BorderFactory.createEmptyBorder());

			controlPanel.setMinimumSize(controlPanel.getPreferredSize());



			// plot panel (extension of EuclidianView)
			//======================================================
			plotPanel = new PlotPanelEuclidianView(app.getKernel());
			plotPanel.setMouseEnabled(true);
			plotPanel.setMouseMotionEnabled(true);

			plotPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED))); 

			plotPanel.setBorder(BorderFactory.createEmptyBorder());

			plotPanel.setBorder(BorderFactory.createEmptyBorder());


			// table panel
			//======================================================
			table = new ProbabilityTable(app, this);
			table.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, SystemColor.controlShadow));
			tablePanel = new JPanel(new BorderLayout());
			tablePanel.add(table, BorderLayout.CENTER);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void buildLayout(){

		this.removeAll();


		plotSplitPane = new JSplitPane();
		plotSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		plotSplitPane.setLeftComponent(plotPanel);
		plotSplitPane.setResizeWeight(1);
		defaultDividerSize = plotSplitPane.getDividerSize();

		JScrollPane scroller = new JScrollPane(controlPanel);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, plotSplitPane, scroller);
		mainSplitPane.setResizeWeight(1);
		mainSplitPane.setBorder(BorderFactory.createEmptyBorder());

		this.setLayout(new BorderLayout());
		this.add(mainSplitPane, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(0,2,2,2));

		setLabels();
	}




	private void addRemoveTable(boolean showTable){
		if(showTable){
			plotSplitPane.setRightComponent(tablePanel);
			plotSplitPane.setDividerSize(defaultDividerSize);
		}else{
			plotSplitPane.setRightComponent(null);
			plotSplitPane.setDividerSize(0);
		}
	}

	private ListSeparatorRenderer getComboRenderer(){
		if (comboRenderer == null)
			comboRenderer = new ListSeparatorRenderer();
		return comboRenderer;

	}


	private JPanel createDistributionPanel(){

		setLabelArrays();
		comboDistribution = new JComboBox();
		comboDistribution.setRenderer(getComboRenderer());
		comboDistribution.setMaximumRowCount(ProbabilityManager.distCount+1);
		//setComboDistribution();
		comboDistribution.addActionListener(this);
		lblDist = new JLabel();

		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//	cbPanel.add(lblDist);
		cbPanel.add(comboDistribution);



		// create parameter panel
		JPanel parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//parameterPanel.setAlignmentX(0.0f);
		//	parameterPanel.add(comboDistribution);

		lblParameterArray = new JLabel[maxParameterCount];
		fldParameterArray = new JTextField[maxParameterCount];
		sliderArray = new JSlider[maxParameterCount];

		for(int i = 0; i < maxParameterCount; ++i){
			lblParameterArray[i] = new JLabel();
			fldParameterArray[i] = new MyTextField(app.getGuiManager());
			fldParameterArray[i].setColumns(6);
			fldParameterArray[i].addActionListener(this);
			fldParameterArray[i].addFocusListener(this);
			sliderArray[i] = new JSlider();
			sliderArray[i].setPreferredSize(fldParameterArray[i].getPreferredSize());
			sliderArray[i].addChangeListener(this);


			Box hBox = Box.createHorizontalBox();
			hBox.add(Box.createRigidArea(new Dimension(3,0)));
			hBox.add(lblParameterArray[i]);
			hBox.add(Box.createRigidArea(new Dimension(3,0)));
			JPanel labelPanel = new JPanel(new BorderLayout());
			labelPanel.add(hBox, BorderLayout.NORTH);

			JPanel fldSliderPanel = new JPanel(new BorderLayout());
			fldSliderPanel.add(fldParameterArray[i], BorderLayout.NORTH);
			fldSliderPanel.add(sliderArray[i], BorderLayout.SOUTH);

			JPanel fullParmPanel = new JPanel(new BorderLayout());
			fullParmPanel.add(labelPanel, BorderLayout.WEST);
			fullParmPanel.add(fldSliderPanel, BorderLayout.EAST);

			parameterPanel.add(fullParmPanel);

		}

		// put the parameter panel in WEST of a new JPanel and return the result
		JPanel distPanel = new JPanel(new BorderLayout());
		distPanel.setLayout (new FlowLayout(FlowLayout.LEFT));
		distPanel.add(cbPanel);
		distPanel.add(parameterPanel);

		return distPanel;
	}



	private JPanel createProbabilityPanel(){

		// create probability mode JComboBox and put it in a JPanel
		comboProbType = new JComboBox();
		comboProbType.setRenderer(getComboRenderer());
		comboProbType.addActionListener(this);
		lblProb = new JLabel();

		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//	cbPanel.add(lblProb);
		cbPanel.add(comboProbType);


		lblProbOf = new JLabel();
		lblBetween = new JLabel();   // <= X <=
		lblEndProbOf = new JLabel();
		fldLow = new MyTextField(app.getGuiManager());
		fldLow.setColumns(6);
		fldLow.addActionListener(this);
		fldLow.addFocusListener(this);

		fldHigh = new MyTextField(app.getGuiManager());
		fldHigh.setColumns(6);
		fldHigh.addActionListener(this);
		fldHigh.addFocusListener(this);

		fldResult = new MyTextField(app.getGuiManager());
		fldResult.setColumns(6);
		fldResult.addActionListener(this);
		fldResult.addFocusListener(this);

		// create panel to hold the entry fields
		JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		//	fieldPanel.add(cbPanel);

		Box hBox = Box.createHorizontalBox();
		hBox.add(lblProbOf);
		hBox.add(Box.createRigidArea(new Dimension(3,0)));
		hBox.add(fldLow);
		hBox.add(Box.createRigidArea(new Dimension(3,0)));
		hBox.add(lblBetween);
		hBox.add(Box.createRigidArea(new Dimension(3,0)));
		hBox.add(fldHigh);
		hBox.add(Box.createRigidArea(new Dimension(3,0)));
		hBox.add(lblEndProbOf);
		fieldPanel.add(hBox);
		fieldPanel.add(fldResult);


		// put all sub-panels together and return the result 
		//	JPanel probPanel = new JPanel(new BorderLayout());
		//	probPanel.add(fieldPanel,BorderLayout.CENTER);
		//	probPanel.add(cbPanel,BorderLayout.WEST);

		JPanel probPanel = new JPanel();
		probPanel.setLayout (new FlowLayout(FlowLayout.LEFT));
		probPanel.add(cbPanel);
		probPanel.add(fieldPanel);


		return probPanel;

	}




	//=================================================
	//       Plotting
	//=================================================

	
	/**
	 * Creates the required GeoElements for the currently selected distribution
	 * type and parameters. 
	 */
	private void createGeoElements(){

		this.removeGeos();

		String expr;
	
		//create low point

		GeoAxis path = (GeoAxis)kernel.lookupLabel(app.getPlain("xAxis"));

		AlgoPointOnPath algoLow = new AlgoPointOnPath(cons, (Path)path, 0d, 0d);
		cons.removeFromConstructionList(algoLow);

		lowPoint = (GeoPoint) algoLow.getGeoElements()[0];

		lowPoint.setObjColor(COLOR_POINT);
		lowPoint.setPointSize(4);
		lowPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);
		plotGeoList.add(lowPoint);

		
		// create high point

		AlgoPointOnPath algoHigh = new AlgoPointOnPath(cons, (Path)path, 0d, 0d);
		cons.removeFromConstructionList(algoHigh);

		highPoint = (GeoPoint) algoHigh.getGeoElements()[0];

		highPoint.setObjColor(COLOR_POINT);
		highPoint.setPointSize(4);
		highPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);
		plotGeoList.add(highPoint);

		pointList = new ArrayList<GeoElement>();
		pointList.add(lowPoint);
		pointList.add(highPoint);

		// Set the axis points so they are not equal. This needs to be done 
		// before the integral geo is created.
		setXAxisPoints();


		if(probManager.isDiscrete(selectedDist)){   

			// discrete distribution 
			// ====================================================

			// create discrete bar graph and associated lists
			createDiscreteLists();
			//expr = "BarChart[" + discreteValueList.getLabel() + "," + discreteProbList.getLabel() + "]";

			AlgoBarChart algoBarChart = new AlgoBarChart(cons, discreteValueList, discreteProbList);
			cons.removeFromConstructionList(algoBarChart);

			//discreteGraph = createGeoFromString(expr);
			discreteGraph = algoBarChart.getGeoElements()[0];
			discreteGraph.setObjColor(COLOR_PDF);
			discreteGraph.setAlphaValue(opacityDiscrete);
			discreteGraph.setLineThickness(thicknessBarChart);
			discreteGraph.setFixed(true);
			plotGeoList.add(discreteGraph);


			// create discrete interval bar graph and associated lists
			//expr = "Take[" + discreteProbList.getLabel()  + ", x(" 
			//+ lowPoint.getLabel() + ")+1, x(" + highPoint.getLabel() + ")+1]";
			//intervalProbList  = (GeoList) createGeoFromString(expr);

			MyDouble one = new MyDouble(kernel, 1d);

			ExpressionNode low = new ExpressionNode(kernel, lowPoint, ExpressionNode.XCOORD, null);
			ExpressionNode high = new ExpressionNode(kernel, highPoint, ExpressionNode.XCOORD, null);				
			ExpressionNode lowPlusOne = new ExpressionNode(kernel, low, ExpressionNode.PLUS, one);
			ExpressionNode highPlusOne = new ExpressionNode(kernel, high, ExpressionNode.PLUS, one);				

			AlgoDependentNumber xLow = new AlgoDependentNumber(cons, lowPlusOne, false);
			cons.removeFromConstructionList(xLow);
			AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, highPlusOne, false);
			cons.removeFromConstructionList(xHigh);

			AlgoTake take2 = new AlgoTake(cons, (GeoList)discreteProbList, (GeoNumeric)xLow.getGeoElements()[0], (GeoNumeric)xHigh.getGeoElements()[0]);
			cons.removeFromConstructionList(take2);

			intervalProbList = (GeoList) take2.getGeoElements()[0];

			//expr = "Take[" + discreteValueList.getLabel()  + ", x(" 
			//+ lowPoint.getLabel() + ")+1, x(" + highPoint.getLabel() + ")+1]";
			//intervalValueList  = (GeoList) createGeoFromString(expr);

			AlgoTake take = new AlgoTake(cons, (GeoList)discreteValueList, (GeoNumeric) xLow.getGeoElements()[0], (GeoNumeric) xHigh.getGeoElements()[0]);
			cons.removeFromConstructionList(take);


			intervalValueList = (GeoList) take.getGeoElements()[0];


			//expr = "BarChart[" + intervalValueList.getLabel() + "," + intervalProbList.getLabel() + "]";
			//discreteIntervalGraph  = createGeoFromString(expr);

			AlgoBarChart barChart = new AlgoBarChart(cons, intervalValueList, intervalProbList);
			cons.removeFromConstructionList(barChart);

			discreteIntervalGraph = barChart.getGeoElements()[0];

			//System.out.println(text);
			discreteIntervalGraph.setObjColor(COLOR_PDF_FILL);
			discreteIntervalGraph.setAlphaValue(opacityDiscreteInterval);
			discreteIntervalGraph.setLineThickness(thicknessBarChart);
			discreteIntervalGraph.updateCascade();
			plotGeoList.add(discreteIntervalGraph);


		}else{ 

			// continuous distribution
			// ====================================================

			// create density curve
			expr = buildDensityCurveExpression(selectedDist);

			densityCurve = createGeoFromString(expr, null, true);

			cons.removeFromConstructionList(densityCurve.getParentAlgorithm());

			densityCurve.setObjColor(COLOR_PDF);
			densityCurve.setLineThickness(thicknessCurve);
			densityCurve.setFixed(true);



			if(hasIntegral ){
				GeoBoolean f = new GeoBoolean(cons);
				f.setValue(false);

				ExpressionNode low = new ExpressionNode(kernel, lowPoint, ExpressionNode.XCOORD, null);
				ExpressionNode high = new ExpressionNode(kernel, highPoint, ExpressionNode.XCOORD, null);				

				AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low, false);
				cons.removeFromConstructionList(xLow);
				AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high, false);
				cons.removeFromConstructionList(xHigh);

				AlgoIntegralDefinite algoIntegral = new AlgoIntegralDefinite(cons, (GeoFunction)densityCurve, (NumberValue) xLow.getGeoElements()[0], (NumberValue) xHigh.getGeoElements()[0], f);
				cons.removeFromConstructionList(algoIntegral);

				integral = algoIntegral.getGeoElements()[0];
				integral.setObjColor(COLOR_PDF_FILL);
				integral.setAlphaValue(opacityIntegral);
				integral.setEuclidianVisible(true);
				plotGeoList.add(integral);
			}

		}

		hideAllGeosFromViews();
		//labelAllGeos();
		hideToolTips();

	}


	/**
	 * Calculates and sets the plot dimensions, the axes intervals and the point
	 * capture style for the the currently selected distribution.
	 */
	protected void updatePlotSettings(){

		double xMin, xMax, yMin, yMax;

		// get the plot window dimensions
		double[] d = getPlotDimensions();
		xMin = d[0]; xMax = d[1]; yMin = d[2]; yMax = d[3];

		//System.out.println(d[0] + "," + d[1] + "," + d[2] + "," + d[3]);

		if(plotSettings == null)
			plotSettings = new PlotSettings();	

		plotSettings.xMin = xMin;
		plotSettings.xMax = xMax;
		plotSettings.yMin = yMin;
		plotSettings.yMax = yMax;

		// don't show the y-axis for continuous case (edge axis looks bad)
		plotSettings.showYAxis = probManager.isDiscrete(selectedDist);
		//plotSettings.showYAxis = true;


		plotSettings.isEdgeAxis[0] = false;
		plotSettings.isEdgeAxis[1] = false;
		plotSettings.forceXAxisBuffer = true;

		if(probManager.isDiscrete(selectedDist)){
			// discrete axis points should jump from point to point 
			plotSettings.pointCaptureStyle = EuclidianView.POINT_CAPTURING_ON_GRID;
			//TODO --- need an adaptive setting here for when we have too many intervals
			plotSettings.gridInterval[0] = 1;
			plotSettings.gridIntervalAuto = false;
			plotSettings.xAxesIntervalAuto = true;
		}
		else
		{	
			plotSettings.pointCaptureStyle = EuclidianView.POINT_CAPTURING_OFF;
			plotSettings.xAxesIntervalAuto = true;
			plotPanel.setPlotSettings(plotSettings);
		}	

		plotPanel.setPlotSettings(plotSettings);

	}


	/**
	 * Adjusts the interval control points to match to the current low and high
	 * values. The low and high values are changeable from the input fields, so
	 * this method is called after a field change.
	 */
	public void setXAxisPoints(){

		isSettingAxisPoints = true;

		lowPoint.setCoords(low, 0.0, 1.0);
		highPoint.setCoords(high, 0.0, 1.0);
		plotPanel.repaint();
		GeoElement.updateCascade(pointList, getTempSet());
		tempSet.clear();
		
		if(probManager.isDiscrete(selectedDist))
			table.setSelectionByRowValue((int)low, (int)high);

		isSettingAxisPoints = false;
	}


	private TreeSet tempSet;
	private TreeSet getTempSet() {
		if (tempSet == null) {
			tempSet = new TreeSet();
		}
		return tempSet;
	}


	


	/**
	 * Returns an interval probability for the currently selected distribution and probability mode.
	 * If mode == PROB_INTERVAL then P(low <= X <= high) is returned.
	 * If mode == PROB_LEFT then P(low <= X) is returned.
	 * If mode == PROB_RIGHT then P(X <= high) is returned.
	 */
	private double intervalProbability(){

		return probManager.intervalProbability(low, high, selectedDist, parameters, probMode);
	}



	/**
	 * Returns an inverse probability for a selected distribution.
	 * @param prob 
	 */
	private double inverseProbability(double prob){
		
		return probManager.inverseProbability(selectedDist, prob, parameters);
	}


	/**
	 * TODO: get this to work!
	 * @return
	 */
	private boolean isValidInterval(double xLow, double xHigh){

		if (xHigh < xLow) return false;

		boolean isValid = true;
		

		switch (selectedDist){

		case ProbabilityManager.DIST_BINOMIAL:
			isValid = xLow >= 0 && xHigh<= parameters[0];  // 0 <= x <= n
			break;

		case ProbabilityManager.DIST_PASCAL: 
			isValid = xLow >= 0;   // 0 <= x 
			break;

			
		case ProbabilityManager.DIST_POISSON: 
		case ProbabilityManager.DIST_CHISQUARE:
		case ProbabilityManager.DIST_EXPONENTIAL:
			if(probMode != PROB_LEFT)
				isValid = xLow >= 0;   // 0 <= x 
			break;
			
			
		case ProbabilityManager.DIST_F:	
			if(probMode != PROB_LEFT)
				isValid = xLow > 0;   // 0 <= x 
			break;

		case ProbabilityManager.DIST_HYPERGEOMETRIC: 
			isValid = xLow >= 0 && xHigh<= parameters[2];  // 0 <= x <= sample size
			break;
		}


		return isValid;	

	}


	//=================================================
	//      Event Handlers 
	//=================================================


	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		setFontRecursive(this,font);
		lblDist.setFont(app.getItalicFont());
		lblProb.setFont(app.getItalicFont());
		plotPanel.updateFonts();
		table.updateFonts(font);

	}

	public void setFontRecursive(Container c, Font font) {
		Component[] components = c.getComponents();
		for(Component com : components) {
			com.setFont(font);
			if(com instanceof Container) 
				setFontRecursive((Container) com, font);
		}
	}


	public void actionPerformed(ActionEvent e) {
		if(isIniting) return;
		Object source = e.getSource();	


		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}	


		if(source == comboDistribution){
			comboDistribution.removeActionListener(this);
			if(comboDistribution.getSelectedItem() != null) 
				if( comboDistribution.getSelectedItem().equals(ListSeparatorRenderer.SEPARATOR)){
					comboDistribution.setSelectedItem(distributionMap.get(selectedDist));
				}
				else if(selectedDist != this.reverseDistributionMap.get(comboDistribution.getSelectedItem())){
					selectedDist = this.reverseDistributionMap.get(comboDistribution.getSelectedItem());
					parameters = probManager.getDefaultParameterMap().get(selectedDist);
					this.setProbabilityCalculator(selectedDist, parameters, isCumulative);
				}
			comboDistribution.addActionListener(this);
			
			this.requestFocus();
		}

		else if (source == comboProbType) {					
			updateProbabilityType();
		}

		//btnClose.requestFocus();
	}


	private void doTextFieldActionPerformed(JTextField source) {
		if(isIniting) return;
		try {
			String inputText = source.getText().trim();
			//Double value = Double.parseDouble(source.getText());

			NumberValue nv;
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(inputText, false);		
			double value = nv.getDouble();

			if(source == fldLow){
				if(isValidInterval(value,high)){
					low = value;
					setXAxisPoints();
				}else{
					updateGUI();
				}

			}

			else if(source == fldHigh){
				if(isValidInterval(low,value)){
					high = value;
					setXAxisPoints();
				}else{
					updateGUI();
				}
			}

			// handle inverse probability
			else if(source == fldResult ){
				if(value < 0 || value > 1){
					updateGUI();
				}else{
					if(probMode == PROB_LEFT){
						high = inverseProbability(value);
					}
					if(probMode == PROB_RIGHT){
						low = inverseProbability(1-value);
					}
					setXAxisPoints();
				}
			}

			else 
				// handle parm entry
				for(int i=0; i< parameters.length; ++i)
					if (source == fldParameterArray[i]) {
						parameters[i] = value;
						// TODO
						//validateParms(selectedParms);
						//updatePlot();
						updateAll();
					}

			updateIntervalProbability();
			updateGUI();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}


	public void setInterval(double low, double high){
		fldHigh.removeActionListener(this);
		fldLow.removeActionListener(this);
		this.low = low;
		this.high = high;
		fldLow.setText(""+low);
		fldHigh.setText(""+high);
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();
		fldHigh.addActionListener(this);
		fldLow.addActionListener(this);
	}


	public void focusGained(FocusEvent arg0) {}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField)(e.getSource()));
		updateGUI();
	}



	//=================================================
	//      Update Methods
	//=================================================


	public void updateAll(){
		updateFonts();
		updateDistribution();
		updatePlot();
		updateProbabilityType();
		updateGUI();
		//this.requestFocus();

	}


	private void updateGUI() {

		// set visibility and text of the parameter labels and fields
		for(int i = 0; i < maxParameterCount; ++i ){

			boolean hasParm = i < probManager.getParmCount()[selectedDist];

			lblParameterArray[i].setVisible(hasParm);
			fldParameterArray[i].setVisible(hasParm);

			// hide sliders for now ... need to work out slider range for each parm (tricky)
			sliderArray[i].setVisible(false);

			if(hasParm){
				// set label
				lblParameterArray[i].setVisible(true);
				lblParameterArray[i].setText(parameterLabels[selectedDist][i]);
				// set field
				fldParameterArray[i].removeActionListener(this);
				fldParameterArray[i].setText("" + format( parameters[i]));
				fldParameterArray[i].setCaretPosition(0);
				fldParameterArray[i].addActionListener(this);
			}
		}

		// set low/high interval field values 
		fldLow.setText("" + format(low));
		fldLow.setCaretPosition(0);
		fldHigh.setText("" + format(high));
		fldHigh.setCaretPosition(0);
		fldResult.setText("" + format(probability));
		fldResult.setCaretPosition(0);

		// set distribution combo box
		comboDistribution.removeActionListener(this);
		if(comboDistribution.getSelectedItem() != distributionMap.get(selectedDist))
			comboDistribution.setSelectedItem(distributionMap.get(selectedDist));
		comboDistribution.addActionListener(this);

	}


	private void updatePlot(){
		updatePlotSettings();
		updateIntervalProbability();
		updateDiscreteTable();
		setXAxisPoints();
	}


	private void updateIntervalProbability(){
		probability = intervalProbability();
		if(probManager.isDiscrete(selectedDist))
			this.discreteIntervalGraph.updateCascade();
		else
			this.integral.updateCascade();
	}

	
	private void updateProbabilityType(){

		if(isIniting) return;	

		boolean isDiscrete = probManager.isDiscrete(selectedDist);

		probMode = comboProbType.getSelectedIndex();
		this.getPlotDimensions();

		if(probMode == PROB_INTERVAL){
			lowPoint.setEuclidianVisible(true);
			highPoint.setEuclidianVisible(true);			
			fldLow.setVisible(true);
			fldHigh.setVisible(true);
			lblBetween.setText(app.getMenu("XBetween"));

			low = plotSettings.xMin + 0.4*(plotSettings.xMax -plotSettings.xMin);
			high = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);

		}

		else if(probMode == PROB_LEFT){
			lowPoint.setEuclidianVisible(false);
			highPoint.setEuclidianVisible(true);
			fldLow.setVisible(false);
			fldHigh.setVisible(true);
			lblBetween.setText(app.getMenu("XLessThanOrEqual"));

			if(isDiscrete)
				low = ((GeoNumeric)discreteValueList.get(0)).getDouble();
			else		
				low = plotSettings.xMin - 1; // move offscreen so the integral looks complete

			high = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);
		}

		else if(probMode == PROB_RIGHT){
			lowPoint.setEuclidianVisible(true);
			highPoint.setEuclidianVisible(false);
			fldLow.setVisible(true);
			fldHigh.setVisible(false);
			lblBetween.setText(app.getMenu("LessThanOrEqualToX"));

			if(isDiscrete)
				high = ((GeoNumeric)discreteValueList.get(discreteValueList.size()-1)).getDouble();
			else
				high = plotSettings.xMax + 1; // move offscreen so the integral looks complete

			low = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);
		}

		// make result field editable for inverse probability calculation  
		if(probMode != PROB_INTERVAL){
			fldResult.setBackground(fldLow.getBackground());
			fldResult.setBorder(fldLow.getBorder());
			fldResult.setEditable(true);
			fldResult.setFocusable(true);

		}else{

			fldResult.setBackground(this.getBackground());
			fldResult.setBorder(BorderFactory.createEmptyBorder());
			fldResult.setEditable(false);
			fldResult.setFocusable(false);

		}

		if(isDiscrete){
			high = Math.round(high);
			low = Math.round(low);
		}
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();	
	}


	protected void updateDistribution(){


		// reset the distributions
		/*
		 * TODO: currently this is done simply by removing all the geos and
		 * creating new ones, is there a better way?
		 */

		
		createGeoElements();
		//setSliderDefaults();

		// update
		if(probManager.isDiscrete(selectedDist)){
			discreteGraph.update();
			discreteIntervalGraph.update();

			updateDiscreteTable();

			addRemoveTable(true);

			//this.fldParameterArray[0].requestFocus();


		}else{
			addRemoveTable(false);

			densityCurve.update();
			if(hasIntegral)
				integral.update();
		}

		this.repaint();

	}


	private void updateDiscreteTable(){
		if(!probManager.isDiscrete(selectedDist))
			return;

		int firstX = (int) ((GeoNumeric)discreteValueList.get(0)).getDouble();
		int lastX = (int) ((GeoNumeric)discreteValueList.get(discreteValueList.size()-1)).getDouble();
		table.setTable(selectedDist, parameters, firstX, lastX, isCumulative);
	}


	protected void updatePrintFormat(int printDecimals, int printFigures){
		this.printDecimals = printDecimals;
		this.printFigures = printFigures;
		updateGUI();
		updateDiscreteTable();
	}



	//=================================================
	//      View Implementation
	//=================================================

	public void add(GeoElement geo) {}
	public void clearView() {
		//Application.debug("prob calc clear view");
		//this.removeGeos();
		//plotPanel.clearView();
	}
	public void remove(GeoElement geo) {}
	public void rename(GeoElement geo) {}
	public void repaintView() {}
	public void reset() {
		//Application.debug("prob calc reset");
		//updateAll();
	}
	public void setMode(int mode) {} 
	public void updateAuxiliaryObject(GeoElement geo) {}

	// Handles user point changes in the EV plot panel 
	public void update(GeoElement geo) {
		double[] coords = new double[2];;
		if(!isSettingAxisPoints && !isIniting){
			if(geo.equals(lowPoint)){	
				if(isValidInterval(lowPoint.getInhomX(), high)){
					low = lowPoint.getInhomX();
					updateIntervalProbability();
					updateGUI();
					if(probManager.isDiscrete(selectedDist))
						table.setSelectionByRowValue((int)low, (int)high);
				}else{
					setXAxisPoints();
				}
			}
			if(geo.equals(highPoint)){
				if(isValidInterval(low, highPoint.getInhomX())){
					high = highPoint.getInhomX();
					updateIntervalProbability();
					updateGUI();
					if(probManager.isDiscrete(selectedDist))
						table.setSelectionByRowValue((int)low, (int)high);
				}else{
					setXAxisPoints();
				}
			}
		}
	}


	public void attachView() {
		//clearView();
		//kernel.notifyAddAll(this);
		kernel.attach(this);		
	}

	public void detachView() {
		removeGeos();
		kernel.detach(this);
		//plotPanel.detachView();
		//clearView();
		//kernel.notifyRemoveAll(this);		
	}


	public void setLabels(){

		distPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Distribution")));
		probPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Probability")));
		setLabelArrays();

		lblDist.setText(app.getMenu("Distribution") + ": ");
		lblProb.setText(app.getMenu("Probability") + ": ");

		comboProbType.removeActionListener(this);
		comboProbType.removeAllItems();
		comboProbType.addItem(app.getMenu("IntervalProb"));
		comboProbType.addItem(app.getMenu("LeftProb"));
		comboProbType.addItem(app.getMenu("RightProb"));
		comboProbType.addActionListener(this);

		lblBetween.setText(app.getMenu("XBetween"));   // <= X <=
		lblEndProbOf.setText(app.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(app.getMenu("ProbabilityOf"));

		setComboDistribution();

		table.setLabels();

	}


	private void setLabelArrays(){

		distributionMap = probManager.getDistributionMap();
		reverseDistributionMap = probManager.getReverseDistributionMap();
		parameterLabels = ProbabilityManager.getParameterLabelArray(app);
	}


	private void setComboDistribution(){

		comboDistribution.removeActionListener(this);
		comboDistribution.removeAllItems();
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_NORMAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_STUDENT));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_CHISQUARE));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_F));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_EXPONENTIAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_CAUCHY));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_WEIBULL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_GAMMA));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_LOGNORMAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_LOGISTIC));

		comboDistribution.addItem(ListSeparatorRenderer.SEPARATOR);

		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_BINOMIAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_PASCAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_POISSON));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_HYPERGEOMETRIC));

		comboDistribution.setSelectedItem(distributionMap.get(selectedDist));
		comboDistribution.addActionListener(this);

	}






	//=================================================
	//       Create GeoElement
	//=================================================


	private GeoElement createGeoFromString(String text ){
		return createGeoFromString(text, null, false);
	}

	private GeoElement createGeoFromString(String text, String label, boolean suppressLabelCreation ){

		try {

			// create the geo
			// ================================
			boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();
			if(suppressLabelCreation)
				cons.setSuppressLabelCreation(true);

			// workaround for eg CmdNormal -> always creates undo point
			boolean oldEnableUndo = cons.isUndoEnabled();
			cons.setUndoEnabled(false);

			GeoElement[] geos = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptions(text, false);	

			cons.setUndoEnabled(oldEnableUndo);


			if(suppressLabelCreation)
				cons.setSuppressLabelCreation(oldSuppressLabelMode);

			// set the label
			// ================================
			if(label != null)
				geos[0].setLabel(label);
			else
				setProbCalcGeoLabel(geos[0]);


			// set visibility
			// ================================
			geos[0].setEuclidianVisible(true);	
			geos[0].setAuxiliaryObject(true);
			geos[0].setLabelVisible(false);


			// put the geo in our list 
			// ================================
			plotGeoList.add(geos[0]);
			//	System.out.println(geos[0].getLabel() + " : " + geos[0].getCommandDescription());
			return geos[0];

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Sets the label of a geo created by ProbabilityCalculator
	 * @param geo
	 */
	private void setProbCalcGeoLabel(GeoElement geo){
		//geo.setLabel(labelPrefix + plotGeoList.size());
		//int r = (int) (Math.random()*1e6);
		//geo.setLabel("pc" + r);
	}


	private String getGeoString(GeoElement geo){
		return geo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
	}




	//=================================================
	//       Geo Handlers
	//=================================================

	public void removeGeos(){
		if(pointList != null)
			pointList.clear();
		clearPlotGeoList();
		plotPanel.clearView();
	}


	private void clearPlotGeoList(){
		
		for(GeoElement geo : plotGeoList){
			if(geo != null){
				geo.setFixed(false);
				geo.remove();
			}
		}
		plotGeoList.clear();
	}


	private void hideAllGeosFromViews(){
		for(GeoElement geo:plotGeoList){
			hideGeoFromViews(geo);
		}
	}

	private void hideGeoFromViews(GeoElement geo){
		// add the geo to our view and remove it from EV		
		geo.addView(plotPanel);
		plotPanel.add(geo);
		geo.removeView(app.getEuclidianView());
		app.getEuclidianView().remove(geo);
	}

	private void hideToolTips(){
		for(GeoElement geo:plotGeoList){
			geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
		}
	}




	/**
	 * Builds a string that can be used by the algebra processor to
	 * create a GeoFunction representation of a given density curve.
	 * 
	 * @param distType
	 * @param parms
	 * @return
	 */
	private String buildDensityCurveExpression(int type){
		String expr = "";
		
		// build geogebra string for creating a density curve with list values as parameters
		// e.g." Normal[0,1,x] "

		StringBuilder sb = new StringBuilder();
		sb.append(ProbabilityManager.getCommand()[type]);
		sb.append("[");
		for(int i=0; i < parameters.length; i++){
			sb.append(parameters[i] + ",");
		}
		if(isCumulative)
			sb.append("x, true]");
		else
			sb.append("x, false]");

		return sb.toString();
	}



	/**
	 * Creates two GeoLists, discreteProbList and discreteValueList, that store
	 * the probabilities and values of the currently selected discrete
	 * distribution.
	 */
	private String createDiscreteLists(){

		String expr = "";
		String n, p, s, mean;

		switch(selectedDist){

		case ProbabilityManager.DIST_BINOMIAL:	
			n = "" + parameters[0];
			p = "" + parameters[1];

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[BinomialDist[" + n + "," + p + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k]," + isCumulative;
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);

			break;

		case ProbabilityManager.DIST_PASCAL:	
			n = "" + parameters[0];
			p = "" + parameters[1];

			String n2 = "InversePascal[" + n + "," + p +  ", 0.999]";
			expr = "Sequence[k,k,0," + n2 + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[Pascal[" + n + "," + p + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k]," + isCumulative;
			expr +=	"],k,1," + n2 + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);

			break;


		case ProbabilityManager.DIST_POISSON:
			
			mean = "" + parameters[0];
			n = "" + (parameters[0] + 6*Math.sqrt(parameters[0]));

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[Poisson[" + mean + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k]," + isCumulative;
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);

			break;


		case ProbabilityManager.DIST_HYPERGEOMETRIC:	
			p = "" + parameters[0];  // population size
			n = "" + parameters[1];  // n
			s = "" + parameters[2];  // sample size

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[HyperGeometric[" + p + "," + n + "," + s + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k]," + isCumulative;
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);

			break;


		}

		return expr;
	}



	/**
	 * Returns the appropriate plot dimensions for a given distribution and parameter set. 
	 * Plot dimensions are returned as an array of double: {xMin, xMax, yMin, yMax} 	 
	 */
	private double[] getPlotDimensions(){

		return probManager.getPlotDimensions(selectedDist, parameters, densityCurve, isCumulative);

	}


	/**
	 * Redefines the density curve ... not currently used
	 */
	private void resetDensityCurve(){

		densityCurve.remove();
		densityCurve = createGeoFromString(buildDensityCurveExpression(selectedDist));

		densityCurve.setObjColor(COLOR_PDF);
		densityCurve.setLineThickness(3);
		densityCurve.setFixed(true);
		hideGeoFromViews(densityCurve);

	}




	//============================================================
	//           ComboBox Renderer with SEPARATOR
	//============================================================

	class ListSeparatorRenderer extends JLabel implements ListCellRenderer {

		public static final String SEPARATOR = "---";
		JSeparator separator;

		public ListSeparatorRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(JSeparator.HORIZONTAL);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			String str = (value == null) ? "" : value.toString();
			if (SEPARATOR.equals(str)) {
				return separator;
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText(str);
			return this;
		}
	}


	//============================================================
	//           Number Format
	//============================================================

	/**
	 * Formats a number string using local format settings
	 */
	public String format(double x){

		// override the default decimal place setting
		if(printDecimals >= 0)
			kernel.setTemporaryPrintDecimals(printDecimals);
		else
			kernel.setTemporaryPrintFigures(printFigures);

		// get the formatted string
		String result = kernel.format(x);

		// restore the default decimal place setting
		kernel.restorePrintAccuracy();

		return result;
	}



	//============================================================
	//           Sliders
	//============================================================


	private void setSliderDefaults(){
		for(int i = 0; i < probManager.getParmCount()[selectedDist]; i++){
			// TODO: this is breaking the discrete distributions
			//sliderArray[i].setValue((int) probManager.getDefaultParameterMap().get(selectedDist)[i]);
		}
	}


	public void stateChanged(ChangeEvent e) {
		if(isIniting) return;

		JSlider source = (JSlider)e.getSource();
		for(int i = 0 ; i < maxParameterCount; i++){
			if(source == sliderArray[i]){

				fldParameterArray[i].setText("" + sliderArray[i].getValue());
				doTextFieldActionPerformed(fldParameterArray[i]);

				System.out.println(sliderArray[i].getValue());
			}
		}

	}



	//============================================================
	//           XML
	//============================================================

	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb) {

		if(selectedDist == -1) return;

		sb.append("<probabilityCalculator>\n");
		sb.append("\t<distribution");

		sb.append(" type=\"");
		sb.append(selectedDist);
		sb.append("\"");


		sb.append(" isCumulative=\"");
		sb.append(isCumulative  ? "true" : "false" );
		sb.append("\"");

		sb.append(" parameters" + "=\"");
		for (int i = 0 ; i < parameters.length ; i++) {
			sb.append(parameters[i]);
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("\"");

		sb.append("/>\n");
		sb.append("</probabilityCalculator>\n");
	}


}
