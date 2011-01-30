package geogebra.gui.view.spreadsheet;



import geogebra.euclidian.EuclidianView;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;


/**
 * Dialog that displays the graphs of various probability density functions with
 * interactive controls for calculating interval probabilities.
 * 
 * @author G. Sturr
 * 
 */
public class ProbabilityCalculator extends JDialog implements View, ActionListener, FocusListener   {

	// enable/disable integral ---- use for testing
	private boolean hasIntegral = true; 

	//ggb
	private Application app;
	private Construction cons;
	private Kernel kernel; 
	private StatGeo statGeo;
	private ProbabilityCalculator probDialog;

	// continuous distributions
	private static final int DIST_NORMAL = 0;
	private static final int DIST_STUDENT = 1;
	private static final int DIST_CHISQUARE = 2;
	private static final int continuousDistCount = 3;

	// discrete distributions
	private static final int DIST_BINOMIAL = 3;
	private static final int totalDistCount = 4;


	// distribution modes and labels
	private int selectedDiscreteDist = DIST_BINOMIAL;  
	private int selectedContinuousDist = DIST_NORMAL; 
	private int selectedDist = DIST_NORMAL;  // default: startup with normal distribution
	private boolean isContinuous = true;


	// strings for labels
	private String[] distLabels; 
	private String[][] parameterLabels;

	// default parameter values
	private HashMap<Integer, double[]> defaultParameterMap;
	private int maxParameterCount; // initialized with defaultParameterMap



	// GUI 
	private JButton btnClose, btnOptions, btnExport, btnDisplay;
	private JComboBox comboDistribution;
	private JComboBox comboProbType;
	private JTextField[] fldParmeterArray;
	private JTextField fldLow,fldHigh,fldResult;
	private JLabel[] lblParmeterArray;
	private JComponent distPanel;
	private JPanel probPanel;
	private PlotPanel plotPanel;
	private PlotSettings plotSettings;
	private JLabel intervalLabel;


	// GeoElements
	private ArrayList<GeoElement> plotGeoList;
	private GeoPoint lowPoint, highPoint;
	private GeoElement densityCurve, integral;
	private GeoElement discreteGraph, discreteIntervalGraph;
	private GeoList discreteValueList, discreteProbList, intervalProbList, intervalValueList;
	private GeoList parmList;


	// initing
	private boolean isIniting;
	private boolean isSettingAxisPoints = false;


	// probability modes
	private static final int PROB_INTERVAL = 0;
	private static final int PROB_LEFT = 1;
	private static final int PROB_RIGHT = 2;
	private int probMode = PROB_INTERVAL;

	//interval values and current probability
	private double low, high, probability;

	// colors
	private static final Color COLOR_PDF = new Color(0, 0, 255);  //blue
	private static final Color COLOR_POINT = Color.BLACK;



	/*************************************************
	 * Construct the dialog
	 */
	public ProbabilityCalculator(SpreadsheetView spView, Application app) {
		super(app.getFrame(),false);
		isIniting = true;
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		statGeo = new StatGeo(app);
		probDialog = this;

		// init variables
		createDefaultParameterMap();
		plotGeoList = new ArrayList<GeoElement>();

		initGUI();

		// initGUI creates a new EV, so we may need to give it time to setup
		// before adding geos into it
		SwingUtilities.invokeLater(new Runnable(){ 
			public void run() { 
				createGeoElements();
				updateAll(); 
			}
		});

		attachView();
		isIniting = false;

	} 
	/**************** end constructor ****************/



	public void removeGeos(){	
		clearPlotGeoList();
	}

	private void clearPlotGeoList(){
		for(GeoElement geo : plotGeoList){
			if(geo != null)
				geo.remove();
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




	//=================================================
	//       Create GUI
	//=================================================


	private void initGUI() {

		try {		

			// create the button panel for the bottom of the dialog
			//=====================================================
			btnClose = new JButton(app.getMenu("Close"));
			btnClose.addActionListener(this);
			JPanel rightButtonPanel = new JPanel(new FlowLayout());
			rightButtonPanel.add(btnClose);


			btnOptions = new JButton(app.getPlain("Options"));
			btnOptions.addActionListener(this);

			btnExport = new JButton(app.getPlain("Export"));
			btnExport.addActionListener(this);

			JPanel centerButtonPanel = new JPanel(new FlowLayout());
			centerButtonPanel.add(btnOptions);
			centerButtonPanel.add(btnExport);		

			JPanel buttonPanel = new JPanel(new BorderLayout());
			//	buttonPanel.add(centerButtonPanel, BorderLayout.CENTER);
			buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
			// END button panel



			// create the control panel 
			//======================================================
			distPanel = this.createDistributionPanel();	
			probPanel = this.createProbabilityPanel();


			JPanel controlPanel = new JPanel();
			controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
			controlPanel.add(distPanel);
			controlPanel.add(probPanel);
			controlPanel.add(buttonPanel);
			controlPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));



			// create the plot panel (extension of EuclidianView)
			//======================================================
			plotPanel = new PlotPanel(app.getKernel());
			plotPanel.setMouseEnabled(true);
			plotPanel.setMouseMotionEnabled(true);

			plotPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED))); 

			plotPanel.setPreferredSize(new Dimension(350,300));



			// put the sub-panels together into the main panel
			//=====================================================
			JPanel mainPanel = new JPanel(new BorderLayout());		
			mainPanel.add(plotPanel, BorderLayout.CENTER);		
			mainPanel.add(controlPanel, BorderLayout.SOUTH);
			mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2,2,2));

			this.getContentPane().add(mainPanel);
			//this.getContentPane().setPreferredSize(new Dimension(450,450));

			setLabels();
			pack();
			setLocationRelativeTo(app.getFrame());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private JPanel createDistributionPanel(){

		setLabelArrays();
		comboDistribution = new JComboBox(distLabels);
		comboDistribution.setSelectedIndex(selectedDist);
		comboDistribution.addActionListener(this);


		// create parameter panel
		JPanel parameterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		parameterPanel.setAlignmentX(0.0f);
		parameterPanel.add(comboDistribution);

		lblParmeterArray = new JLabel[maxParameterCount];
		fldParmeterArray = new JTextField[maxParameterCount];

		for(int i = 0; i < maxParameterCount; ++i){
			lblParmeterArray[i] = new JLabel();
			fldParmeterArray[i] = new MyTextField(app.getGuiManager());
			fldParmeterArray[i].setColumns(6);
			fldParmeterArray[i].addActionListener(this);
			fldParmeterArray[i].addFocusListener(this);

			parameterPanel.add(lblParmeterArray[i]);
			parameterPanel.add(fldParmeterArray[i]);

		}

		// put the parameter panel in WEST of a new JPanel and return the result
		JPanel distPanel = new JPanel(new BorderLayout());
		distPanel.add(parameterPanel, BorderLayout.WEST);

		return distPanel;
	}



	private JPanel createProbabilityPanel(){

		// create probability mode JComboBox and put it in a JPanel
		comboProbType = new JComboBox();
		comboProbType.addActionListener(this);
		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cbPanel.add(comboProbType);

		// create panel to hold the entry fields
		JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		JLabel lbl1 = new JLabel("P( ");

		fldLow = new MyTextField(app.getGuiManager());
		fldLow.setColumns(6);
		fldLow.addActionListener(this);
		fldLow.addFocusListener(this);

		intervalLabel = new JLabel(" \u2264 X \u2264 ");   // <= X <=

		fldHigh = new MyTextField(app.getGuiManager());
		fldHigh.setColumns(6);
		fldHigh.addActionListener(this);
		fldHigh.addFocusListener(this);

		JLabel lbl3 = new JLabel(" ) = ");

		fldResult = new MyTextField(app.getGuiManager());
		fldResult.setColumns(6);
		fldResult.addActionListener(this);
		fldResult.addFocusListener(this);

		fieldPanel.add(lbl1);
		fieldPanel.add(fldLow);
		fieldPanel.add(intervalLabel);
		fieldPanel.add(fldHigh);
		fieldPanel.add(lbl3);
		fieldPanel.add(fldResult);


		// put all sub-panels together and return the result 
		JPanel probPanel = new JPanel(new BorderLayout());
		probPanel.add(fieldPanel,BorderLayout.CENTER);
		probPanel.add(cbPanel,BorderLayout.WEST);

		return probPanel;

	}


	private void setLabels(){

		setTitle(app.getPlain("Probability Calculator"));	
		distPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Distribution")));
		probPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Probability")));
		setLabelArrays();

		comboProbType.removeAllItems();
		comboProbType.addItem(app.getMenu("IntervalProb"));
		comboProbType.addItem(app.getMenu("LeftProb"));
		comboProbType.addItem(app.getMenu("RightProb"));
	}


	private void setLabelArrays(){

		parameterLabels = new String[totalDistCount][totalDistCount];
		distLabels = new String[totalDistCount];

		distLabels[DIST_NORMAL] = app.getMenu("Distribution.Normal");
		parameterLabels[DIST_NORMAL][0] = app.getMenu("Mean");
		parameterLabels[DIST_NORMAL][1] = app.getMenu("StandardDeviation.short");

		distLabels[DIST_STUDENT] = app.getMenu("Distribution.StudentT");
		parameterLabels[DIST_STUDENT][0] = app.getMenu("DegreesOfFreedom.short");

		distLabels[DIST_CHISQUARE] = app.getMenu("Distribution.ChiSquare");	
		parameterLabels[DIST_CHISQUARE][0] = app.getMenu("DegreesOfFreedom.short");

		distLabels[DIST_BINOMIAL] = app.getMenu("Distribution.Binomial");
		parameterLabels[DIST_BINOMIAL][0] = app.getMenu("Binomial.number");
		parameterLabels[DIST_BINOMIAL][1] = app.getMenu("Binomial.probability");

	}




	//=================================================
	//       Plotting
	//=================================================

	private void createGeoElements(){

		String expr;

		// create list of parameters
		parmList = (GeoList) statGeo.createGeoFromString("{}");
		double[] parms = defaultParameterMap.get(selectedDist);
		for(int i=0; i < parms.length; i++){
			parmList.add(new GeoNumeric(cons,parms[i]));
			//System.out.println("parms:" + i + " " + selectedParameters[i]);
		}
		plotGeoList.add(parmList);		

		// create density curve
		expr = buildDensityCurveExpression(selectedContinuousDist);
		densityCurve = statGeo.createGeoFromString(expr);
		densityCurve.setObjColor(COLOR_PDF);
		densityCurve.setLineThickness(3);
		densityCurve.setFixed(true);
		plotGeoList.add(densityCurve);


		// create discrete bar graph and associated lists
		createDiscreteLists();
		expr = "BarChart[" + discreteValueList.getLabel() + "," + discreteProbList.getLabel() + "]";
		discreteGraph = statGeo.createGeoFromString(expr);
		discreteGraph.setObjColor(COLOR_PDF);
		discreteGraph.setAlphaValue(0.0f);
		discreteGraph.setLineThickness(2);
		discreteGraph.setFixed(true);
		plotGeoList.add(discreteGraph);


		//create low point
		String text = "Point[y=0]";
		lowPoint = (GeoPoint) statGeo.createGeoFromString(text);
		plotGeoList.add(lowPoint);
		lowPoint.setObjColor(COLOR_POINT);
		lowPoint.setPointSize(4);
		lowPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);


		//create high point
		text = "Point[y=0]";
		highPoint = (GeoPoint) statGeo.createGeoFromString(text);
		plotGeoList.add(highPoint);
		highPoint.setObjColor(COLOR_POINT);
		highPoint.setPointSize(4);
		highPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);


		// create integral
		text = "Integral[" + densityCurve.getLabel() + ", x(" + lowPoint.getLabel() 
		+ "), x(" + highPoint.getLabel() + ")]";

		//text = "Integral[" + densityCurve.getLabel() + ",1,2]";


		//System.out.println(text);

		if(hasIntegral ){
			integral  = statGeo.createGeoFromString(text);
			plotGeoList.add(integral);
			integral.setObjColor(COLOR_PDF);
			integral.setAlphaValue(0.25f);
		}


		// create discrete interval bar graph and associated lists
		expr = "Take[" + discreteProbList.getLabel()  + ", x(" 
		+ lowPoint.getLabel() + ")+1, x(" + highPoint.getLabel() + ")+1]";
		intervalProbList  = (GeoList) statGeo.createGeoFromString(expr);

		expr = "Take[" + discreteValueList.getLabel()  + ", x(" 
		+ lowPoint.getLabel() + ")+1, x(" + highPoint.getLabel() + ")+1]";
		intervalValueList  = (GeoList) statGeo.createGeoFromString(expr);

		text = "BarChart[" + intervalValueList.getLabel() + "," + intervalProbList.getLabel() + "]";

		//System.out.println(text);
		discreteIntervalGraph  = statGeo.createGeoFromString(text);
		plotGeoList.add(discreteIntervalGraph);
		discreteIntervalGraph.setObjColor(Color.blue);
		discreteIntervalGraph.setAlphaValue(0.5f);
		discreteIntervalGraph.updateCascade();

		hideAllGeosFromViews();
		hideToolTips();

	}


	/**
	 * Calculates and sets the plot dimensions, axes intervals and point capture style for the
	 * the currently selected distribution. 
	 */
	private void updatePlotSettings(){

		double xMin, xMax, yMin, yMax;

		//TODO: does this belong here or in getPlotDimensions?
		parmList.updateCascade();

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
		plotSettings.showYAxis = false;
		plotSettings.isEdgeAxis[0] = false;
		plotSettings.isEdgeAxis[1] = false;
		plotSettings.forceXAxisBuffer = true;

		switch(selectedDist){

		case DIST_NORMAL:
		case DIST_STUDENT:
		case DIST_CHISQUARE:
			plotSettings.pointCaptureStyle = EuclidianView.POINT_CAPTURING_OFF;
			plotSettings.xAxesIntervalAuto = true;
			plotPanel.setPlotSettings(plotSettings);
			break;

		case DIST_BINOMIAL:
			plotSettings.pointCaptureStyle = EuclidianView.POINT_CAPTURING_ON_GRID;
			plotSettings.xAxesInterval = 1;
			plotSettings.xAxesIntervalAuto = false;
			break;

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
		lowPoint.updateCascade();
		highPoint.setCoords(high, 0.0, 1.0);
		highPoint.updateCascade();
		plotPanel.repaint();
		isSettingAxisPoints = false;
	}

	/**
	 * Returns the parameter values from the parmList geo
	 */
	private double[] getCurrentParameters(){

		double [] parms = new double[parmList.size()];
		for(int i = 0; i< parmList.size(); i++){
			parms[i] = ((GeoNumeric)parmList.get(i)).getDouble();
		}
		return parms;
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

		// retrieve the parameter values from the parmList geo
		double [] parms = getCurrentParameters();

		switch(type){

		case DIST_NORMAL:
			//double mean = parms[0];
			//double sigma = parms[1];

			String mean = "Element[" + parmList.getLabel() + ",1]";
			String sigma = "Element[" + parmList.getLabel() + ",2]";

			expr = "Normal[" + mean + "," + sigma + ", x]";

			//expr =  "1 / sqrt(2 Pi " + sigma + "^2) exp(-((x - " + mu + ")^2 / 2 " + sigma + "^2))";
			break;

		case DIST_STUDENT:
			//double v = parms[0];
			String v = "Element[" + parmList.getLabel() + ",1]";
			expr =  "gamma((" + v + " + 1) / 2) / (sqrt(" + v + " Pi) gamma(" + v + " / 2)) (1 + x^2 / " + v + ")^(-((" + v + " + 1) / 2))";
			break;

		case DIST_CHISQUARE:
			//double k = parms[0];
			String k = "Element[" + parmList.getLabel() + ",1]";
			expr = "1 / (2^(" + k + " / 2) gamma(" + k + " / 2)) x^(" + k + " / 2 - 1) exp(-(x / 2))";
			break;

		}

		return expr;
	}



	/**
	 * Creates two GeoLists, discreteProbList and discreteValueList, that store
	 * the probabilities and values of the currently selected discrete
	 * distribution.
	 */
	private String createDiscreteLists(){

		String expr = "";

		switch(selectedDiscreteDist){

		case DIST_BINOMIAL:	
			String n = "Element[" + parmList.getLabel() + ",1]";
			String p = "Element[" + parmList.getLabel() + ",2]";

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) statGeo.createGeoFromString(expr);
			expr = "Sequence[Binomial[" + n + ",k ]*" + p + "^k *(1-" + p + ")^(" + n + "- k), k, 1," + n + "+ 1 ]";
			//expr = "Sequence[Pascal[" + n + "," + p + ",Element[" + discreteValueList.getLabel() + ",k]],k,1," + n + "+ 1 ]";
			discreteProbList = (GeoList) statGeo.createGeoFromString(expr);

			break;

		}

		return expr;
	}



	private double getDiscreteMax(){
		return evaluateExpression("Max[" + discreteProbList.getLabel() + "]");
	}






	/**
	 * Returns the appropriate plot dimensions for a given distribution and parameter set. 
	 * Plot dimensions are returned as an array of double: {xMin, xMax, yMin, yMax} 	 
	 *   
	 * @param distType
	 * @param parms
	 * @return
	 */
	private double[] getPlotDimensions(){

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;

		// retrieve the parameter values from the parmList geo
		double [] parms = getCurrentParameters();

		switch(selectedDist){

		case DIST_NORMAL:
			double mean = parms[0];
			double sigma = parms[1];
			xMin = mean - 5*sigma;
			xMax = mean + 5*sigma;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(mean);	
			break;

		case DIST_STUDENT:
			double v = parms[0];
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;

		case DIST_CHISQUARE:
			double k = parms[0];		
			xMin = 0;
			xMax = 4*k;
			yMin = 0;
			if(k>2)
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(k-2);	
			else
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;

		case DIST_BINOMIAL:
			double n = parms[0];
			double p = parms[1];
			xMin = -1;
			xMax = n + 1;
			yMin = 0;	
			yMax = 1.2* getDiscreteMax();
			break;
		}

		double[] d = {xMin, xMax, yMin, yMax};
		return d;
	}



	/**
	 * Creates the default parameter map. This provides default parameter values
	 * for each distribution type.
	 */
	private void createDefaultParameterMap(){

		defaultParameterMap = new HashMap<Integer,double[]>();

		defaultParameterMap.put(DIST_NORMAL, new double[] {0, 1}); // mean = 0, sigma = 1
		defaultParameterMap.put(DIST_STUDENT, new double[] {10}); // df = 10
		defaultParameterMap.put(DIST_CHISQUARE, new double[] {6}); // df = 6
		defaultParameterMap.put(DIST_BINOMIAL, new double[] {20, 0.5}); // n = 20, p = 0.5

		this.maxParameterCount = 2;
	}




	/**
	 * Returns an interval probability for the currently selected distribution and probability mode.
	 * If mode == PROB_INTERVAL then P(low <= X <= high) is returned.
	 * If mode == PROB_LEFT then P(low <= X) is returned.
	 * If mode == PROB_RIGHT then P(X <= high) is returned.
	 */
	private double intervalProbability(){

		String exprHigh = "";
		String exprLow = "";
		double prob = 0;

		// retrieve the parameter values from the parmList geo
		double [] parms = getCurrentParameters();

		try {
			switch(selectedDist){

			case DIST_NORMAL:
				exprHigh = "Normal[" + parms[0] + "," + parms[1] + "," + high + "]";
				exprLow = "Normal[" + parms[0] + "," + parms[1] + "," + low + "]";		
				break;

			case DIST_STUDENT:
				exprHigh = "TDistribution[" + parms[0] + "," + high + "]";
				exprLow = "TDistribution[" + parms[0]  + "," + low + "]";
				break;

			case DIST_CHISQUARE:
				exprHigh = "ChiSquared[" + parms[0] + "," + high + "]";
				exprLow = "ChiSquared[" + parms[0]  + "," + low + "]";
				break;

			case DIST_BINOMIAL:
				prob = evaluateExpression("Sum[" + intervalProbList.getLabel() + "]");
				break;

			}

			if(isContinuous){
				if(probMode == PROB_LEFT)
					prob = evaluateExpression(exprHigh);	
				else if(probMode == PROB_RIGHT)
					prob = 1 - evaluateExpression(exprLow);
				else
					prob = evaluateExpression(exprHigh) - evaluateExpression(exprLow);

			}else{

			}



		} catch (Exception e) {		
			e.printStackTrace();
		}

		return prob;
	}

	/**
	 * Returns an inverse probability for a selected continuous distribution.
	 * @param prob
	 *            -- left sided probability
	 */
	private double inverseProbability(double prob){

		String expr = "";
		double result = 0;

		// retrieve the parameter values from the parmList geo
		double [] parms = new double[parmList.size()];
		for(int i = 0; i< parmList.size(); i++){
			parms[i] = ((GeoNumeric)parmList.get(i)).getDouble();
		}


		try {
			switch(selectedDist){

			case DIST_NORMAL:
				expr = "InverseNormal[" + parms[0] + "," + parms[1] + "," + prob + "]";
				result = evaluateExpression(expr);
				break;

			case DIST_STUDENT:
				expr = "InverseTDistribution[" + parms[0] + "," + prob + "]";
				result = evaluateExpression(expr);
				break;

			case DIST_CHISQUARE:
				expr = "InverseChiSquared[" + parms[0] + "," + prob + "]";
				result = evaluateExpression(expr);
				break;

			}


		} catch (Exception e) {		
			e.printStackTrace();
		}

		return result;
	}


	/**
	 * TODO: get this to work!
	 * @return
	 */
	private boolean validateProbFields(){

		boolean succ = true;
		succ = low <= high;
		if( selectedDist == DIST_BINOMIAL){
			low = Math.round(low);
			high = Math.round(high);
			succ = low >= 0;
			//	succ = high <= selectedParameters[0];

		}
		return succ;	

	}

	private double evaluateFunction(String expr, double x){

		GeoFunction tempGeo;
		tempGeo = kernel.getAlgebraProcessor().evaluateToFunction(expr, false);	

		return tempGeo.evaluate(x);
	}


	private double evaluateExpression(String expr){

		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	

		return nv.getDouble();
	}



	//=================================================
	//      Event Handlers 
	//=================================================


	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		if(isVisible){
			if(!isIniting)
				updateAll();
		}else{
			app.setMoveMode();
			removeGeos();
			//detachView();
		}
	}


	// TODO: control fonts are not updated yet
	public void updateFonts() {

		Font font = app.getPlainFont();

		//	int size = font.getSize();
		//if (size < 12) size = 12; // minimum size
		//double multiplier = (size)/12.0;

		setFont(font);

	}

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();	
		if(source == btnClose){
			setVisible(false);

		}
		else if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}	

		else if (source == comboDistribution) {
			updateDistribution();
			updatePlot();
			updateProbabilityType();
			updateGUI();
			btnClose.requestFocus();
		}

		else if (source == comboProbType) {					
			updateProbabilityType();
		}

		//btnClose.requestFocus();
	}


	private void doTextFieldActionPerformed(JTextField source) {

		try {
			String inputText = source.getText().trim();
			//Double value = Double.parseDouble(source.getText());

			NumberValue nv;
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(inputText, false);		
			double value = nv.getDouble();

			for(int i=0; i< parmList.size(); ++i)
				if (source == fldParmeterArray[i]) {
					((GeoNumeric)parmList.get(i)).setValue(value); 
					//validateParms(selectedParms);
					updatePlot();
				}

			if(source == fldLow){
				low = value;
				setXAxisPoints();

			}
			if(source == fldHigh){
				high = value;
				setXAxisPoints();
			}

			// handle inverse probability
			if(source == fldResult && isContinuous){
				if(probMode == PROB_LEFT){
					high = inverseProbability(value);
				}
				if(probMode == PROB_RIGHT){
					low = inverseProbability(1-value);
				}
				setXAxisPoints();
			}

			updateIntervalProbability();
			updateGUI();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}


	public void focusGained(FocusEvent arg0) {}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField)(e.getSource()));
		updateGUI();
	}



	//=================================================
	//      Update Methods
	//=================================================


	private void updateAll(){
		updateFonts();
		updateDistribution();
		updatePlot();
		updateProbabilityType();
		updateGUI();
		btnClose.requestFocus();
		isIniting = false;
	}


	private void updateGUI() {

		// override the default decimal place setting
		kernel.setTemporaryPrintDecimals(5);

		// set the visibility and text of the parameter labels and fields
		for(int i = 0; i < maxParameterCount; ++i ){
			if(parameterLabels[selectedDist][i] == null){
				lblParmeterArray[i].setVisible(false);
				fldParmeterArray[i].setVisible(false);
			}else{
				// labels
				lblParmeterArray[i].setVisible(true);
				lblParmeterArray[i].setText(parameterLabels[selectedDist][i]);
				// fields
				fldParmeterArray[i].setVisible(true);
				fldParmeterArray[i].removeActionListener(this);
				fldParmeterArray[i].setText("" + kernel.format( ((GeoNumeric)parmList.get(i)).getDouble() ));
				//fldParmeterArray[i].setCaretPosition(0);
				fldParmeterArray[i].addActionListener(this);
			}
		}

		// set probability field values 
		fldLow.setText("" + kernel.format(low));
		fldHigh.setText("" + kernel.format(high));
		fldResult.setText("" + kernel.format(probability));

		// restore the default decimal place setting
		kernel.restorePrintAccuracy();
	}


	private void updatePlot(){
		updatePlotSettings();
		updateIntervalProbability();
		setXAxisPoints();
	}


	private void updateIntervalProbability(){		
		probability = intervalProbability();
	}

	private void updateProbabilityType(){

		if(isIniting) return;	

		probMode = comboProbType.getSelectedIndex();
		this.getPlotDimensions();

		if(probMode == PROB_INTERVAL){
			lowPoint.setEuclidianVisible(true);
			highPoint.setEuclidianVisible(true);			
			fldLow.setVisible(true);
			fldHigh.setVisible(true);
			intervalLabel.setText(" \u2264 X \u2264 ");

			low = plotSettings.xMin + 0.4*(plotSettings.xMax -plotSettings.xMin);
			high = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);

		}

		else if(probMode == PROB_LEFT){
			lowPoint.setEuclidianVisible(false);
			highPoint.setEuclidianVisible(true);
			fldLow.setVisible(false);
			fldHigh.setVisible(true);
			intervalLabel.setText(" X \u2264 ");
			if(isContinuous)
				low = plotSettings.xMin - 1; // move offscreen so the integral looks complete
			else
				low = ((GeoNumeric)discreteValueList.get(0)).getDouble();
			high = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);
		}

		else if(probMode == PROB_RIGHT){
			lowPoint.setEuclidianVisible(true);
			highPoint.setEuclidianVisible(false);
			fldLow.setVisible(true);
			fldHigh.setVisible(false);
			intervalLabel.setText(" \u2264 X ");
			if(isContinuous)
			high = plotSettings.xMax + 1; // move offscreen so the integral looks complete
			else
				high = ((GeoNumeric)discreteValueList.get(discreteValueList.size()-1)).getDouble();
			low = plotSettings.xMin + 0.6*(plotSettings.xMax -plotSettings.xMin);
		}

		// make result field editable for inverse probability calculation  
		if(isContinuous && !(probMode == PROB_INTERVAL)){
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

		if(!isContinuous){
			high = Math.round(high);
			low = Math.round(low);
		}
		setXAxisPoints();
		updateIntervalProbability();
		updateGUI();	
	}


	private void updateDistribution(){

		// update the selection variables
		selectedDist = comboDistribution.getSelectedIndex();
		isContinuous = selectedDist < continuousDistCount ;

		if(isContinuous)
			selectedContinuousDist = selectedDist;
		else
			selectedDiscreteDist = selectedDist;

		// reset the distributions
		/*
		 * TODO: currently this is done simply by removing all the geos and
		 * creating new ones, is there a better way?
		 */
		removeGeos();
		createGeoElements();

		// set visibility
		densityCurve.setEuclidianVisible(isContinuous);
		if(hasIntegral)
			integral.setEuclidianVisible(isContinuous);
		discreteGraph.setEuclidianVisible(!isContinuous);
		discreteIntervalGraph.setEuclidianVisible(!isContinuous);

		// update 
		densityCurve.update();
		if(hasIntegral)
			integral.update();
		discreteGraph.update();
		discreteIntervalGraph.update();
	}


	/**
	 * Redefines the density curve ... not currently used
	 */
	private void resetDensityCurve(){

		densityCurve.remove();
		densityCurve = statGeo.createGeoFromString(buildDensityCurveExpression(selectedDist));

		densityCurve.setObjColor(COLOR_PDF);
		densityCurve.setLineThickness(3);
		densityCurve.setFixed(true);
		plotGeoList.add(densityCurve);
		hideGeoFromViews(densityCurve);

	}









	//=================================================
	//      View Implementation
	//=================================================

	public void add(GeoElement geo) {}
	public void clearView() {}
	public void remove(GeoElement geo) {}
	public void rename(GeoElement geo) {}
	public void repaintView() {}
	public void reset() {}
	public void setMode(int mode) {} 
	public void updateAuxiliaryObject(GeoElement geo) {}

	public void update(GeoElement geo) {
		double[] coords = new double[2];;
		if(!isSettingAxisPoints){
			if(geo.equals(lowPoint)){	
				low = lowPoint.getInhomX();
				updateIntervalProbability();
				updateGUI();
			}
			if(geo.equals(highPoint)){	
				high = highPoint.getInhomX();
				updateIntervalProbability();
				updateGUI();
			}
		}
	}


	public void attachView() {
		//clearView();
		//kernel.notifyAddAll(this);
		kernel.attach(this);		
	}

	public void detachView() {
		kernel.detach(this);
		//plotPanel.detachView();
		//clearView();
		//kernel.notifyRemoveAll(this);		
	}


}
