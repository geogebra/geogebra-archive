package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.ExpressionNode;
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
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
	//	private StatGeo statGeo;
	private ProbabilityCalculator probDialog;

	// continuous distribution identifiers
	private static final int DIST_NORMAL = 0;
	private static final int DIST_STUDENT = 1;
	private static final int DIST_CHISQUARE = 2;
	private static final int DIST_F = 3;
	private static final int DIST_CAUCHY = 4;
	private static final int DIST_EXPONENTIAL = 5;
	private static final int DIST_GAMMA = 6;
	private static final int DIST_WEIBULL = 7;
	private static final int continuousDistCount = 8;

	// discrete distribution identifiers
	private static final int DIST_BINOMIAL = 8;
	private static final int DIST_PASCAL = 9;
	private static final int DIST_HYPERGEOMETRIC = 10;
	private static final int DIST_POISSON = 11;
	private static final int totalDistCount = 12;

	// selected distribution modes
	private int selectedDiscreteDist = DIST_BINOMIAL;  
	private int selectedContinuousDist = DIST_NORMAL; 
	private int selectedDist = DIST_NORMAL;  // default: startup with normal distribution
	private boolean isContinuous = true;

	// distribution constants 
	private String[] distLabels; 
	private String[][] parameterLabels;
	private String[] inverseCmd, cmd;
	private int[] parmCount;
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
	private JLabel lblBetween;
	private JPanel mainPanel;
	private JPanel notAvailablePanel;
	

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
	private double low = 0, high = 1, probability;

	private ArrayList<GeoElement> pointList;

	private JLabel lblProbOf;

	private JLabel lblEndProbOf;


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

		probDialog = this;

		// init variables
		initDistributionConstants();
		plotGeoList = new ArrayList<GeoElement>();

		initGUI();

		//	createGeoElements();
		attachView();
		isIniting = false;
		//	updateAll(); 

	} 
	/**************** end constructor ****************/






	//=================================================
	//       Create GUI
	//=================================================


	private void initGUI() {

		try {		

			//TODO remove this temporary panel
			notAvailablePanel = createNotAvailablePanel();
			
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
			mainPanel = new JPanel(new BorderLayout());		
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
		comboDistribution.setMaximumRowCount(totalDistCount);
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

		fieldPanel.add(lblProbOf);
		fieldPanel.add(fldLow);
		fieldPanel.add(lblBetween);
		fieldPanel.add(fldHigh);
		fieldPanel.add(lblEndProbOf);
		fieldPanel.add(fldResult);


		// put all sub-panels together and return the result 
		JPanel probPanel = new JPanel(new BorderLayout());
		probPanel.add(fieldPanel,BorderLayout.CENTER);
		probPanel.add(cbPanel,BorderLayout.WEST);

		return probPanel;

	}

	/**
	 * Creates a panel with message "not available"
	 */
	private JPanel createNotAvailablePanel(){

		JLabel imageContainer = new JLabel();
		String message = "\\text{" + app.getPlain("NotAvailable") + "}";
		imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, message, 
				app.getPlainFont(), false, Color.BLACK, null));

		JPanel imagePanel = new JPanel(new BorderLayout());
		imagePanel.setBorder(BorderFactory.createEmptyBorder());
		imagePanel.setBackground(Color.WHITE);
		imagePanel.setAlignmentX(SwingConstants.CENTER);
		imagePanel.setAlignmentY(SwingConstants.CENTER);
		imageContainer.setHorizontalAlignment(SwingConstants.CENTER);
		imagePanel.add(imageContainer, BorderLayout.CENTER);

		return imagePanel;

	}





	//=================================================
	//       Plotting
	//=================================================

	private void createGeoElements(){
		
		this.removeGeos();

		String expr;

		// create list of parameters
		parmList = (GeoList) createGeoFromString("{}");
		double[] parms = defaultParameterMap.get(selectedDist);
		for(int i=0; i < parms.length; i++){
			parmList.add(new GeoNumeric(cons,parms[i]));
			//System.out.println("parms:" + i + " " + selectedParameters[i]);
		}



		//create low point
		expr = "Point[" + app.getPlain("xAxis") + "]";
		lowPoint = (GeoPoint) createGeoFromString(expr);
		lowPoint.setObjColor(COLOR_POINT);
		lowPoint.setPointSize(4);
		lowPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);


		//create high point
		highPoint = (GeoPoint) createGeoFromString(expr);
		highPoint.setObjColor(COLOR_POINT);
		highPoint.setPointSize(4);
		highPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);

		pointList = new ArrayList<GeoElement>();
		pointList.add(lowPoint);
		pointList.add(highPoint);
		
		// Set the axis points so they are not equal. This needs to be done 
		// before the integral geo is created.
		setXAxisPoints();

		// create density curve
		expr = buildDensityCurveExpression(selectedContinuousDist);
		densityCurve = createGeoFromString(expr);
		densityCurve.setObjColor(COLOR_PDF);
		densityCurve.setLineThickness(3);
		densityCurve.setFixed(true);



		// create discrete bar graph and associated lists
		createDiscreteLists();
		expr = "BarChart[" + discreteValueList.getLabel() + "," + discreteProbList.getLabel() + "]";
		discreteGraph = createGeoFromString(expr);
		discreteGraph.setObjColor(COLOR_PDF);
		discreteGraph.setAlphaValue(0.0f);
		discreteGraph.setLineThickness(2);
		discreteGraph.setFixed(true);


		// create integral
		expr = "Integral[" + densityCurve.getLabel() + ", x(" + lowPoint.getLabel() 
		+ "), x(" + highPoint.getLabel() + ") , false ]";



		if(hasIntegral ){
			integral  = createGeoFromString(expr);
			integral.setObjColor(COLOR_PDF);
			integral.setAlphaValue(0.25f);
		}


		// create discrete interval bar graph and associated lists
		expr = "Take[" + discreteProbList.getLabel()  + ", x(" 
		+ lowPoint.getLabel() + ")+1, x(" + highPoint.getLabel() + ")+1]";
		intervalProbList  = (GeoList) createGeoFromString(expr);


		expr = "Take[" + discreteValueList.getLabel()  + ", x(" 
		+ lowPoint.getLabel() + ")+1, x(" + highPoint.getLabel() + ")+1]";
		intervalValueList  = (GeoList) createGeoFromString(expr);

		expr = "BarChart[" + intervalValueList.getLabel() + "," + intervalProbList.getLabel() + "]";

		//System.out.println(text);
		discreteIntervalGraph  = createGeoFromString(expr);
		discreteIntervalGraph.setObjColor(Color.blue);
		discreteIntervalGraph.setAlphaValue(0.5f);
		discreteIntervalGraph.updateCascade();

		hideAllGeosFromViews();
		//labelAllGeos();
		hideToolTips();

	}


	/**
	 * Calculates and sets the plot dimensions, the axes intervals and the point
	 * capture style for the the currently selected distribution.
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
		plotSettings.showYAxis = true;
		plotSettings.isEdgeAxis[0] = false;
		plotSettings.isEdgeAxis[1] = true;
		plotSettings.forceXAxisBuffer = true;

		if(isContinuous){
			plotSettings.pointCaptureStyle = EuclidianView.POINT_CAPTURING_OFF;
			plotSettings.xAxesIntervalAuto = true;
			plotPanel.setPlotSettings(plotSettings);
		}
		else
		{	// discrete axis points should jump from point to point 
			plotSettings.pointCaptureStyle = EuclidianView.POINT_CAPTURING_ON_GRID;
			//TODO --- need an adaptive setting here for when we have too many intervals
			plotSettings.xAxesInterval = 1;
			plotSettings.xAxesIntervalAuto = false;
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
	 * Returns the parameter values from the parmList geo
	 */
	private double[] getCurrentParameters(){

		double [] parms = new double[parmList.size()];
		for(int i = 0; i< parmList.size(); i++){
			parms[i] = ((GeoNumeric)parmList.get(i)).getDouble();
		}
		return parms;
	}



	private double getDiscreteMax(){
		return evaluateExpression("Max[" + discreteProbList.getLabel() + "]");
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

			if(isContinuous){	

				// build gegebra strings for high and low probabilities
				// e.g. "Normal[ parms[0] , parms[1] , high ]"

				StringBuilder partialExpr = new StringBuilder();
				partialExpr.append(cmd[selectedDist]);
				partialExpr.append("[");
				for(int i=0; i < parmCount[selectedDist]; i++){
					partialExpr.append(parms[i]);
					partialExpr.append(",");
				}
				StringBuilder highExpr = new StringBuilder(partialExpr.toString());
				highExpr.append(high + "]");
				StringBuilder lowExpr = partialExpr.append(low + "]");

				//System.out.println(highExpr.toString());
				//System.out.println(lowExpr.toString());

				// calculate the probability
				if(probMode == PROB_LEFT)
					prob = evaluateExpression(highExpr.toString());	
				else if(probMode == PROB_RIGHT)
					prob = 1 - evaluateExpression(lowExpr.toString());
				else
					prob = evaluateExpression(highExpr.toString()) - evaluateExpression(lowExpr.toString());

			}else{
				//TODO --- handle discrete cases with functions rather than direct sums?
				if(selectedDist == DIST_BINOMIAL)
					prob = evaluateExpression("Sum[" + intervalProbList.getLabel() + "]");
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

		double result = 0;

		// retrieve the parameter values from the parmList geo
		double [] parms = new double[parmList.size()];
		for(int i = 0; i< parmList.size(); i++){
			parms[i] = ((GeoNumeric)parmList.get(i)).getDouble();
		}

		try {
			// build geogebra string for calculating inverse prob 
			// e.g. "InverseNormal[ parms[0] , parms[1] , prob ]"
			StringBuilder sb = new StringBuilder();
			sb.append(inverseCmd[selectedDist]);
			sb.append("[");
			for(int i=0; i < parmCount[selectedDist]; i++){
				sb.append(parms[i]);
				sb.append(",");
			}
			sb.append(prob);
			sb.append("]");

			// evaluate the expression
			result = evaluateExpression(sb.toString());

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



	//=================================================
	//       Geo Handlers
	//=================================================

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

	private void labelAllGeos(){
		for(int i= 0; i < plotGeoList.size(); i++ ){
			plotGeoList.get(i).setLabel("xPrb" + i);
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


	private double evaluateFunction(String expr, double x){

		GeoFunction tempGeo;
		tempGeo = kernel.getAlgebraProcessor().evaluateToFunction(expr, false);	
		double result = tempGeo.evaluate(x);
		tempGeo.remove();

		return result;
	}


	private double evaluateExpression(String expr){

		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	
		double result = nv.getDouble();

		return result;
	}


	//=================================================
	//      Event Handlers 
	//=================================================


	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		if(isVisible){
			if(!isIniting){
				updateAll();
			}
		}else{
			app.setMoveMode();
			removeGeos();
			//detachView();
		}
	}


	// TODO: fonts for controls are not updated yet
	public void updateFonts() {

		Font font = app.getPlainFont();

		//	int size = font.getSize();
		//if (size < 12) size = 12; // minimum size
		//double multiplier = (size)/12.0;

		setFont(font);

	}

	public void actionPerformed(ActionEvent e) {
		if(isIniting) return;
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
		if(isIniting) return;
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
			lblBetween.setText(app.getMenu("LessThanOrEqualToX"));
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
		comboDistribution.removeActionListener(this);
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


		if( selectedDist == DIST_GAMMA
				|| selectedDist == DIST_WEIBULL
				|| selectedDist == DIST_EXPONENTIAL){
			mainPanel.remove(plotPanel);
			mainPanel.add(notAvailablePanel,BorderLayout.CENTER);
			this.repaint();
		}else{
			mainPanel.remove(notAvailablePanel);
			mainPanel.add(plotPanel, BorderLayout.CENTER);

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

			this.repaint();
		}

		comboDistribution.addActionListener(this);
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

	// Handles user point changes in the EV plot panel 
	public void update(GeoElement geo) {
		double[] coords = new double[2];;
		if(!isSettingAxisPoints && !isIniting){
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




	public void setLabels(){

		setTitle(app.getMenu("ProbabilityCalculator"));	
		distPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Distribution")));
		probPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Probability")));
		setLabelArrays();

		comboProbType.removeAllItems();
		comboProbType.addItem(app.getMenu("IntervalProb"));
		comboProbType.addItem(app.getMenu("LeftProb"));
		comboProbType.addItem(app.getMenu("RightProb"));
		
		lblBetween.setText(app.getMenu("XBetween"));   // <= X <=
		lblEndProbOf.setText(app.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(app.getMenu("ProbabilityOf"));
		
		comboDistribution.setModel(new DefaultComboBoxModel(distLabels));
		comboDistribution.removeActionListener(this);
		comboDistribution.setSelectedIndex(selectedDist);
		comboDistribution.addActionListener(this);
		
	}


	private void setLabelArrays(){

		parameterLabels = new String[totalDistCount][4];
		distLabels = new String[totalDistCount];

		distLabels[DIST_NORMAL] = app.getMenu("Distribution.Normal");
		parameterLabels[DIST_NORMAL][0] = app.getMenu("Mean");
		parameterLabels[DIST_NORMAL][1] = app.getMenu("StandardDeviation.short");

		distLabels[DIST_STUDENT] = app.getMenu("Distribution.StudentT");
		parameterLabels[DIST_STUDENT][0] = app.getMenu("DegreesOfFreedom.short");

		distLabels[DIST_CHISQUARE] = app.getMenu("Distribution.ChiSquare");	
		parameterLabels[DIST_CHISQUARE][0] = app.getMenu("DegreesOfFreedom.short");

		distLabels[DIST_F] = app.getMenu("Distribution.F");
		parameterLabels[DIST_F][0] = app.getMenu("DegreesOfFreedom1.short");
		parameterLabels[DIST_F][1] = app.getMenu("DegreesOfFreedom2.short");

		distLabels[DIST_EXPONENTIAL] = app.getMenu("Distribution.Exponential");
		parameterLabels[DIST_EXPONENTIAL][0] = app.getMenu("Mean");

		distLabels[DIST_CAUCHY] = app.getMenu("Distribution.Cauchy");
		parameterLabels[DIST_CAUCHY][0] = app.getMenu("Median");
		parameterLabels[DIST_CAUCHY][1] = app.getMenu("Scale");

		distLabels[DIST_WEIBULL] = app.getMenu("Distribution.Weibull");
		parameterLabels[DIST_WEIBULL][0] = app.getMenu("Shape");
		parameterLabels[DIST_WEIBULL][1] = app.getMenu("Scale");

		distLabels[DIST_GAMMA] = app.getMenu("Distribution.Gamma");
		parameterLabels[DIST_GAMMA][0] = app.getMenu("Alpha.short");
		parameterLabels[DIST_GAMMA][1] = app.getMenu("Beta.short");


		distLabels[DIST_BINOMIAL] = app.getMenu("Distribution.Binomial");
		parameterLabels[DIST_BINOMIAL][0] = app.getMenu("Binomial.number");
		parameterLabels[DIST_BINOMIAL][1] = app.getMenu("Binomial.probability");

		distLabels[DIST_PASCAL] = app.getMenu("Distribution.Pascal");
		parameterLabels[DIST_PASCAL][0] = app.getMenu("Binomial.number");
		parameterLabels[DIST_PASCAL][1] = app.getMenu("Binomial.probability");

		distLabels[DIST_POISSON] = app.getMenu("Distribution.Poisson");
		parameterLabels[DIST_POISSON][0] = app.getMenu("Mean");

		distLabels[DIST_HYPERGEOMETRIC] = app.getMenu("Distribution.Hypergeometric");
		parameterLabels[DIST_HYPERGEOMETRIC][0] = app.getMenu("Hypergeometric.population");
		parameterLabels[DIST_HYPERGEOMETRIC][1] = app.getMenu("Hypergeometric.number");
		parameterLabels[DIST_HYPERGEOMETRIC][2] = app.getMenu("Hypergeometric.sample");


	}



	private void initDistributionConstants(){

		cmd = new String[continuousDistCount];
		cmd[DIST_NORMAL] = "Normal";
		cmd[DIST_STUDENT] = "TDistribution";
		cmd[DIST_CHISQUARE] = "ChiSquared";
		cmd[DIST_F] = "FDistribution";
		cmd[DIST_CAUCHY] = "Cauchy";
		cmd[DIST_EXPONENTIAL] = "Exponential";
		cmd[DIST_GAMMA] = "Gamma";
		cmd[DIST_WEIBULL] = "Weibull";

		inverseCmd = new String[continuousDistCount];
		inverseCmd[DIST_NORMAL] = "InverseNormal";
		inverseCmd[DIST_STUDENT] = "InverseTDistribution";
		inverseCmd[DIST_CHISQUARE] = "InverseChiSquare";
		inverseCmd[DIST_F] = "InverseFDistribution";
		inverseCmd[DIST_CAUCHY] = "InverseCauchy";
		inverseCmd[DIST_EXPONENTIAL] = "InverseExponential";
		inverseCmd[DIST_GAMMA] = "InverseGamma";
		inverseCmd[DIST_WEIBULL] = "InverseWeibull";

		parmCount = new int[continuousDistCount];
		parmCount[DIST_NORMAL] = 2;
		parmCount[DIST_STUDENT] = 1;
		parmCount[DIST_CHISQUARE] = 1;
		parmCount[DIST_F] = 2;
		parmCount[DIST_CAUCHY] = 2;
		parmCount[DIST_EXPONENTIAL] = 1;
		parmCount[DIST_GAMMA] = 2;
		parmCount[DIST_WEIBULL] = 2;


		// Create the default parameter map that provides default parameter values
		// for each distribution type. 
		defaultParameterMap = new HashMap<Integer,double[]>();

		defaultParameterMap.put(DIST_NORMAL, new double[] {0, 1}); // mean = 0, sigma = 1
		defaultParameterMap.put(DIST_STUDENT, new double[] {10}); // df = 10
		defaultParameterMap.put(DIST_CHISQUARE, new double[] {6}); // df = 6

		defaultParameterMap.put(DIST_F, new double[] {5,2}); // df1 = 5, df2 = 2
		defaultParameterMap.put(DIST_EXPONENTIAL, new double[] {6}); // df = 6
		defaultParameterMap.put(DIST_GAMMA, new double[] {6,6}); // df = 6
		defaultParameterMap.put(DIST_CAUCHY, new double[] {0,1}); // median = 0, scale = 1
		defaultParameterMap.put(DIST_WEIBULL, new double[] {5,1}); // shape = 5, scale = 1

		defaultParameterMap.put(DIST_BINOMIAL, new double[] {20, 0.5}); // n = 20, p = 0.5
		defaultParameterMap.put(DIST_PASCAL, new double[] {20, 0.5}); // n = 20, p = 0.5
		defaultParameterMap.put(DIST_POISSON, new double[] {4}); // mean = 4
		defaultParameterMap.put(DIST_HYPERGEOMETRIC, new double[] {60, 10, 20}); // pop = 60, n = 10, sample = 20

		this.maxParameterCount = 3;

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
		String k, mean, sigma, v, v2, median, scale, shape;
		// retrieve the parameter values from the parmList geo
		double [] parms = getCurrentParameters();

		switch(type){

		case DIST_NORMAL:
			//double mean = parms[0];
			//double sigma = parms[1];

			mean = "Element[" + parmList.getLabel() + ",1]";
			sigma = "Element[" + parmList.getLabel() + ",2]";

			expr = "Normal[" + mean + "," + sigma + ", x]";

			//expr =  "1 / sqrt(2 Pi " + sigma + "^2) exp(-((x - " + mu + ")^2 / 2 " + sigma + "^2))";
			break;

		case DIST_STUDENT:
			//double v = parms[0];
			v = "Element[" + parmList.getLabel() + ",1]";
			expr =  "TDistribution[" + v + ",x]";
			break;

		case DIST_CHISQUARE:
			//double k = parms[0];
			k = "Element[" + parmList.getLabel() + ",1]";
			expr = "1 / (2^(" + k + " / 2) gamma(" + k + " / 2)) x^(" + k + " / 2 - 1) exp(-(x / 2))";
			break;

		case DIST_F:
			v = "Element[" + parmList.getLabel() + ",1]";
			v2 = "Element[" + parmList.getLabel() + ",2]";
			expr = "FDistribution[" + v + "," + v2 + ",x]";
			break;

		case DIST_CAUCHY:			
			median = "Element[" + parmList.getLabel() + ",1]";
			scale = "Element[" + parmList.getLabel() + ",2]";
			expr = "Cauchy[" + median + "," + scale + ",x]";
			break;

		case DIST_EXPONENTIAL:
			//double k = parms[0];
			k = "Element[" + parmList.getLabel() + ",1]";
			expr = "1 / (2^(" + k + " / 2) gamma(" + k + " / 2)) x^(" + k + " / 2 - 1) exp(-(x / 2))";
			break;

		case DIST_GAMMA:
			//double k = parms[0];
			k = "Element[" + parmList.getLabel() + ",1]";
			expr = "1 / (2^(" + k + " / 2) gamma(" + k + " / 2)) x^(" + k + " / 2 - 1) exp(-(x / 2))";
			break;

		case DIST_WEIBULL:
			// weibullPDF(x) = shape / scale (x / scale)^(shape - 1) e^(-(x / scale)^shape)
			shape = "Element[" + parmList.getLabel() + ",1]";
			scale = "Element[" + parmList.getLabel() + ",2]";
			expr = shape + "/"  +  scale + " * (x /" + scale + ")^( " + shape + "- 1) e^(-(x /" + scale + ")^ " + shape + ")";
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
		String n, p, s, mean;

		switch(selectedDiscreteDist){

		case DIST_BINOMIAL:	
			n = "Element[" + parmList.getLabel() + ",1]";
			p = "Element[" + parmList.getLabel() + ",2]";

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[BinomialDist[" + n + "," + p + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k], false ";
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);

			break;

		case DIST_PASCAL:	
			n = "Element[" + parmList.getLabel() + ",1]";
			p = "Element[" + parmList.getLabel() + ",2]";

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[Pascal[" + n + "," + p + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k], false";
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);

			break;


		case DIST_POISSON:	
			mean = "Element[" + parmList.getLabel() + ",1]";

			n = "Element[" + parmList.getLabel() + ",1] + 4*sqrt(" +  "Element[" + parmList.getLabel() + ",1]"  + ")";

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[Poisson[" + mean + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k], false";
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);

			break;


		case DIST_HYPERGEOMETRIC:	
			p = "Element[" + parmList.getLabel() + ",1]";  // population size
			n = "Element[" + parmList.getLabel() + ",2]";  // n
			s = "Element[" + parmList.getLabel() + ",3]";  // sample size

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[HyperGeometric[" + p + "," + n + "," + s + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k], false" ;
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
	 *   
	 * @param distType
	 * @param parms
	 * @return
	 */
	private double[] getPlotDimensions(){

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;

		// retrieve the parameter values from the parmList geo
		double [] parms = getCurrentParameters();
		double mean, sigma, v, v2, k, median, scale, shape, mode, n, p, pop, sample, variance;	

		switch(selectedDist){

		case DIST_NORMAL:
			mean = parms[0];
			sigma = parms[1];
			xMin = mean - 5*sigma;
			xMax = mean + 5*sigma;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(mean);	
			break;

		case DIST_STUDENT:
			v = parms[0];
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;

		case DIST_CHISQUARE:
			k = parms[0];		
			xMin = 0;
			xMax = 4*k;
			yMin = 0;
			if(k>2)
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(k-2);	
			else
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;

		case DIST_F:
			v = parms[0];
			v2 = parms[1];
			mode = v2/v *(v-2)/(v2+2);
			variance = 2*v*v*(v + v2 - 2)/(v2*(v-2)*(v-2)*(v-4));
			xMin = 0;
			xMax = mode + 5 * Math.sqrt(variance);
			yMin = 0;
			if(v>2)
				yMax = 1.2*((GeoFunction)densityCurve).evaluate(mode);	
			else
				yMax = 1.2;	
			break;

		case DIST_CAUCHY:
			median = parms[0];
			scale = parms[1];	
			// TODO --- better estimates
			xMin = median - 6*scale;
			xMax = median + 6*scale;
			yMin = 0;
			yMax = 1.2* (1/(Math.PI*scale)); // Cauchy amplitude = 1/(pi*scale)

			break;


		case DIST_EXPONENTIAL:
			k = parms[0];		
			xMin = 0;
			xMax = 4*k;
			yMin = 0;
			if(k>2)
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(k-2);	
			else
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;


		case DIST_GAMMA:
			k = parms[0];		
			xMin = 0;
			xMax = 4*k;
			yMin = 0;
			if(k>2)
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(k-2);	
			else
				yMax = 1.2* ((GeoFunction)densityCurve).evaluate(0);	
			break;


		case DIST_WEIBULL:
			shape = parms[0];	
			scale = parms[1];	
			median = scale*Math.pow(Math.log(2), 1/shape);
			xMin = 0;
			xMax = 2*median;
			yMin = 0;
			// mode for shape >1
			if(shape > 1){
				mode = scale*Math.pow(1 - 1/shape,1/shape);
				yMax = 1.2*((GeoFunction)densityCurve).evaluate(mode);
			}else{
				yMax = 4;
			}

			break;


		case DIST_BINOMIAL:
			n = parms[0];
			p = parms[1];
			xMin = -1;
			xMax = n + 1;
			yMin = 0;	
			yMax = 1.2* getDiscreteMax();
			break;


		case DIST_PASCAL:
			n = parms[0];
			p = parms[1];
			xMin = -1;
			xMax = n + 1;
			yMin = 0;	
			yMax = 1.2* getDiscreteMax();
			break;

		case DIST_POISSON:
			mean = parms[0];
			xMin = -1;
			xMax = mean + 4*Math.sqrt(mean) ;
			yMin = 0;	
			yMax = 1.2* getDiscreteMax();
			break;

		case DIST_HYPERGEOMETRIC:
			pop = parms[0];
			n = parms[1];
			sample = parms[2];

			xMin = -1;
			xMax = n + 1;
			yMin = 0;	
			yMax = 1.2* getDiscreteMax();
			break;

		}



		double[] d = {xMin, xMax, yMin, yMax};
		return d;
	}




	//=================================================
	//       Create GeoElement
	//=================================================


	private GeoElement createGeoFromString(String text ){
		return createGeoFromString(text, null, false);
	}
	private GeoElement createGeoFromString(String text, String label){
		return createGeoFromString(text, null, false);
	}
	private GeoElement createGeoFromString(String text, String label, boolean suppressLabelCreation ){

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

			plotGeoList.add(geos[0]);
			//	geos[0].setLabel("xPrb" + plotGeoList.size());
			//	System.out.println(geos[0].getLabel() + " : " + geos[0].getCommandDescription());
			return geos[0];

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getGeoString(GeoElement geo){
		return geo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
	}


}
