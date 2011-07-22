package geogebra.gui.view.probcalculator;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.view.spreadsheet.statdialog.PlotPanelEuclidianView;
import geogebra.gui.view.spreadsheet.statdialog.PlotSettings;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.ExpressionNode;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

/**
 * Dialog that displays the graphs of various probability density functions with
 * interactive controls for calculating interval probabilities.
 * 
 * @author G. Sturr
 * 
 */
public class ProbabilityCalculator extends JPanel implements View, ActionListener, FocusListener   {

	// enable/disable integral ---- use for testing
	private boolean hasIntegral = true; 

	//ggb
	private Application app;
	private Construction cons;
	private Kernel kernel; 
	private ProbabilityManager probManager;


	// selected distribution mode
	private int selectedDist = ProbabilityManager.DIST_NORMAL;  // default: startup with normal distribution


	// distribution constants 
	private String[][] parameterLabels;
	private final static int maxParameterCount = 3; // maximum number of parameters allowed for a distribution


	// GUI 
	private JButton btnClose, btnOptions, btnExport, btnDisplay;
	private JComboBox comboDistribution;
	private JComboBox comboProbType;
	private JTextField[] fldParmeterArray;
	private JTextField fldLow,fldHigh,fldResult;
	private JLabel[] lblParmeterArray;
	private JComponent distPanel;
	private JPanel probPanel;

	private PlotPanelEuclidianView plotPanel;
	private PlotSettings plotSettings;
	private JLabel lblBetween, lblProbOf, lblEndProbOf,lblProb , lblDist;
	private JSplitPane sp;


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
	protected static final int PROB_INTERVAL = 0;
	protected static final int PROB_LEFT = 1;
	protected static final int PROB_RIGHT = 2;
	private int probMode = PROB_INTERVAL;

	//interval values and current probability
	private double low = 0, high = 1, probability;

	private ArrayList<GeoElement> pointList;



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

		plotGeoList = new ArrayList<GeoElement>();

		initGUI();
		isIniting = false;


		updateAll();
		attachView();

	} 
	/**************** end constructor ****************/






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
			distPanel.setBorder(BorderFactory.createEtchedBorder());
			probPanel.setBorder(BorderFactory.createEtchedBorder());

			probPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2),
					BorderFactory.createEtchedBorder())); 
			distPanel.setBorder(probPanel.getBorder());




			JPanel controlPanel = new JPanel();
			controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));	
			controlPanel.add(distPanel);
			controlPanel.add(probPanel);

			// TODO: Rounding button, Print ??? , remove Close button
			//controlPanel.add(buttonPanel);
			//controlPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

			controlPanel.setBorder(BorderFactory.createEmptyBorder());


			// create the plot panel (extension of EuclidianView)
			//======================================================
			plotPanel = new PlotPanelEuclidianView(app.getKernel());
			plotPanel.setMouseEnabled(true);
			plotPanel.setMouseMotionEnabled(true);

			plotPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED))); 

			plotPanel.setBorder(BorderFactory.createEmptyBorder());

			//plotPanel.setPreferredSize(new Dimension(500,500));

			// put the sub-panels together into the main panel
			//=====================================================

			Dimension pref = controlPanel.getPreferredSize();
			controlPanel.setMinimumSize(pref);
			//	Dimension max = controlPanel.getMaximumSize();
			//	max.height = pref.height;
			//	controlPanel.setMaximumSize(max);



			sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, plotPanel, controlPanel);
			sp.setResizeWeight(0);
			sp.setBorder(BorderFactory.createEmptyBorder());

			this.setLayout(new BorderLayout());
			this.add(sp, BorderLayout.CENTER);


			/*
			JPanel main = new JPanel(new BorderLayout());
			main.add(plotPanel, BorderLayout.CENTER);
			main.add(controlPanel, BorderLayout.WEST);
			this.setLayout(new BorderLayout());
			this.add(main, BorderLayout.CENTER);

			 */
			setLabels();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}






	private JPanel createDistributionPanel(){

		setLabelArrays();
		comboDistribution = new JComboBox();
		comboDistribution.setRenderer(new ListSeparatorRenderer());
		comboDistribution.setMaximumRowCount(ProbabilityManager.totalDistCount+1);
		//setComboDistribution();
		comboDistribution.addActionListener(this);
		lblDist = new JLabel();

		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cbPanel.add(lblDist);
		cbPanel.add(comboDistribution);



		// create parameter panel
		JPanel parameterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//parameterPanel.setAlignmentX(0.0f);
		//	parameterPanel.add(comboDistribution);

		lblParmeterArray = new JLabel[maxParameterCount];
		fldParmeterArray = new JTextField[maxParameterCount];


		for(int i = 0; i < maxParameterCount; ++i){
			lblParmeterArray[i] = new JLabel();
			fldParmeterArray[i] = new MyTextField(app.getGuiManager());
			fldParmeterArray[i].setColumns(6);
			fldParmeterArray[i].addActionListener(this);
			fldParmeterArray[i].addFocusListener(this);

			Box hBox = Box.createHorizontalBox();
			hBox.add(Box.createRigidArea(new Dimension(3,0)));
			hBox.add(lblParmeterArray[i]);
			hBox.add(Box.createRigidArea(new Dimension(3,0)));
			hBox.add(fldParmeterArray[i]);
			parameterPanel.add(hBox);

		}

		// put the parameter panel in WEST of a new JPanel and return the result
		JPanel distPanel = new JPanel();
		distPanel.setLayout(new BoxLayout(distPanel,BoxLayout.Y_AXIS));
		distPanel.add(cbPanel);
		distPanel.add(parameterPanel);

		return distPanel;
	}



	private JPanel createProbabilityPanel(){

		// create probability mode JComboBox and put it in a JPanel
		comboProbType = new JComboBox();
		comboProbType.addActionListener(this);
		lblProb = new JLabel();

		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cbPanel.add(lblProb);
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
		JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

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
		probPanel.setLayout(new BoxLayout(probPanel,BoxLayout.Y_AXIS));
		probPanel.add(cbPanel);
		probPanel.add(fieldPanel);


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

	/**
	 * Creates the required GeoElements for the currently selected distribution type
	 */
	private void createGeoElements(){

		this.removeGeos();

		String expr;

		// create list of parameters
		parmList = (GeoList) createGeoFromString("{}");
		double[] parms = ProbabilityManager.getDefaultParameterMap().get(selectedDist);
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


		if(probManager.isDiscrete(selectedDist)){   
			
			// discrete distribution 
			// ====================================================
			
			// create discrete bar graph and associated lists
			createDiscreteLists();
			expr = "BarChart[" + discreteValueList.getLabel() + "," + discreteProbList.getLabel() + "]";
			discreteGraph = createGeoFromString(expr);
			discreteGraph.setObjColor(COLOR_PDF);
			discreteGraph.setAlphaValue(opacityDiscrete);
			discreteGraph.setLineThickness(thicknessBarChart);
			discreteGraph.setFixed(true);


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
			discreteIntervalGraph.setObjColor(COLOR_PDF_FILL);
			discreteIntervalGraph.setAlphaValue(opacityDiscreteInterval);
			discreteGraph.setLineThickness(thicknessBarChart);
			discreteIntervalGraph.updateCascade();

			
		}else{ 
			
			// continuous distribution
			// ====================================================
			
			// create density curve
			expr = buildDensityCurveExpression(selectedDist);
			densityCurve = createGeoFromString(expr);
			densityCurve.setObjColor(COLOR_PDF);
			densityCurve.setLineThickness(thicknessCurve);
			densityCurve.setFixed(true);


			// create integral
			expr = "Integral[" + densityCurve.getLabel() + ", x(" + lowPoint.getLabel() 
			+ "), x(" + highPoint.getLabel() + ") , false ]";


			if(hasIntegral ){
				integral  = createGeoFromString(expr);
				integral.setObjColor(COLOR_PDF_FILL);
				integral.setAlphaValue(opacityIntegral);
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
			plotSettings.xAxesInterval = 1;
			plotSettings.xAxesIntervalAuto = false;
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

		isSettingAxisPoints = false;
	}


	private TreeSet tempSet;

	private HashMap<Integer, String> distributionMap;

	private HashMap<String, Integer> reverseDistributionMap;	
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


	/**
	 * Returns an interval probability for the currently selected distribution and probability mode.
	 * If mode == PROB_INTERVAL then P(low <= X <= high) is returned.
	 * If mode == PROB_LEFT then P(low <= X) is returned.
	 * If mode == PROB_RIGHT then P(X <= high) is returned.
	 */
	private double intervalProbability(){

		// retrieve the parameter values from the parmList geo
		double [] parms = getCurrentParameters();

		return probManager.intervalProbability(low, high, selectedDist, parms, probMode);
	}



	/**
	 * Returns an inverse probability for a selected distribution.
	 * @param prob 
	 */
	private double inverseProbability(double prob){

		double[] parms = getCurrentParameters();

		return probManager.inverseProbability(selectedDist, prob, parms);
	}


	/**
	 * TODO: get this to work!
	 * @return
	 */
	private boolean validateProbFields(){

		boolean succ = true;
		succ = low <= high;
		if( selectedDist == ProbabilityManager.DIST_BINOMIAL){
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


	public void updateFonts() {
		Font font = app.getPlainFont();
		setFont(font);
		setFontRecursive(this,font);
		lblDist.setFont(app.getItalicFont());
		lblProb.setFont(app.getItalicFont());

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
		if(source == btnClose){
			setVisible(false);

		}
		else if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}	


		else if(source == comboDistribution){
			comboDistribution.removeActionListener(this);
			if(comboDistribution.getSelectedItem().equals(ListSeparatorRenderer.SEPARATOR)){
				comboDistribution.setSelectedItem(distributionMap.get(selectedDist));
			}
			else{
				selectedDist = this.reverseDistributionMap.get(comboDistribution.getSelectedItem());
				updateDistribution();
				updatePlot();
				updateProbabilityType();
				updateGUI();
			}
			comboDistribution.addActionListener(this);
			this.fldParmeterArray[0].requestFocus();
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
			if(source == fldResult ){
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


	public void updateAll(){
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


	private void updateDistribution(){

	
		// reset the distributions
		/*
		 * TODO: currently this is done simply by removing all the geos and
		 * creating new ones, is there a better way?
		 */
		
		createGeoElements();

		
		// update
		if(probManager.isDiscrete(selectedDist)){
			discreteGraph.update();
			discreteIntervalGraph.update();
			
		}else{
			densityCurve.update();
			if(hasIntegral)
				integral.update();
		}
		
		this.repaint();

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
		removeGeos();
		kernel.detach(this);
		//plotPanel.detachView();
		//clearView();
		//kernel.notifyRemoveAll(this);		
	}




	public void setLabels(){

		//setTitle(app.getMenu("ProbabilityCalculator"));	
		//distPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Distribution")));
		//probPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Probability")));
		setLabelArrays();

		lblDist.setText(app.getMenu("Distribution") + ": ");
		lblProb.setText(app.getMenu("Probability") + ": ");

		comboProbType.removeAllItems();
		comboProbType.addItem(app.getMenu("IntervalProb"));
		comboProbType.addItem(app.getMenu("LeftProb"));
		comboProbType.addItem(app.getMenu("RightProb"));

		lblBetween.setText(app.getMenu("XBetween"));   // <= X <=
		lblEndProbOf.setText(app.getMenu("EndProbabilityOf") + " = ");
		lblProbOf.setText(app.getMenu("ProbabilityOf"));


		setComboDistribution();



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

		comboDistribution.addItem(ListSeparatorRenderer.SEPARATOR);

		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_BINOMIAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_PASCAL));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_POISSON));
		comboDistribution.addItem(distributionMap.get(ProbabilityManager.DIST_HYPERGEOMETRIC));

		comboDistribution.setSelectedIndex(selectedDist);
		comboDistribution.addActionListener(this);

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

		// build geogebra string for creating a density curve wih list values as parameters
		// e.g." Normal[Element[list1,1],Element[list1,2],x]""

		StringBuilder sb = new StringBuilder();
		sb.append(ProbabilityManager.getCommand()[type]);
		sb.append("[");
		for(int i=1; i <= ProbabilityManager.getParmCount()[type]; i++){
			sb.append("Element[" + parmList.getLabel() + "," + i + "]");
			sb.append(",");
		}
		sb.append("x]");

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

		case ProbabilityManager.DIST_PASCAL:	
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


		case ProbabilityManager.DIST_POISSON:	
			mean = "Element[" + parmList.getLabel() + ",1]";

			n = "Element[" + parmList.getLabel() + ",1] + 6*sqrt(" +  "Element[" + parmList.getLabel() + ",1]"  + ")";

			expr = "Sequence[k,k,0," + n + "]";
			discreteValueList = (GeoList) createGeoFromString(expr);

			expr = "Sequence[Poisson[" + mean + ",";
			expr += "Element[" + discreteValueList.getLabel() + ",k], false";
			expr +=	"],k,1," + n + "+ 1 ]";

			//System.out.println(expr);
			discreteProbList = (GeoList) createGeoFromString(expr);

			break;


		case ProbabilityManager.DIST_HYPERGEOMETRIC:	
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
	 */
	private double[] getPlotDimensions(){

		// retrieve the parameter values from the parmList geo
		double [] parms = getCurrentParameters();

		return probManager.getPlotDimensions(selectedDist, parms, densityCurve);

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


	//============================================================
	//           ComboBox Renderer with SEPARATOR
	//============================================================

	class ListSeparatorRenderer extends JLabel implements ListCellRenderer {

		public static final String SEPARATOR = "SEPARATOR";
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




}
