package geogebra.gui.view.spreadsheet;



import geogebra.euclidian.EuclidianView;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
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
	 
	//ggb
	private Application app;
	private Kernel kernel; 
	private StatGeo statGeo;
	private ProbabilityCalculator probDialog;
	
	// modes for continuous distributions
	private static final int DIST_NORMAL = 0;
	private static final int DIST_STUDENT = 1;
	private static final int DIST_CHISQUARE = 2;
	
	// modes for discrete distributions
	private static final int DIST_BINOMIAL = 3;
	
	private int distCount = 4;
	
	
	// variables for the selected distribution
	private int selectedDist = DIST_BINOMIAL;  // default: startup with binomial distribution
	private String[] distLabels; 
	private String[][] parameterLabels;
	private double[] selectedParameters;
	private int maxParameterCount = 3;
	
	
	
	/** high and low values of the probability interval */
	private double low, high;
	
	private double intervalProbability;
	
	// discrete vars
	private double[] discreteProbabilities;
	private double discreteMax;
	
	
	// GUI 
	private JButton btnClose, btnOptions, btnExport, btnDisplay;
	private JComboBox comboDistribution;
	private JTextField[] fldParmeterArray;
	private JTextField fldLow,fldHigh,fldResult;
	private JLabel[] lblParmeterArray;
	private PlotPanel plotPanel;
	
	
	// GeoElements
	private GeoPoint lowPoint,highPoint;
	private GeoElement pdf;
	private GeoElement integral;
	private ArrayList<GeoElement> plotGeoList;
	
	// flags
	private boolean isIniting;
	private boolean isSettingAxisPoints = false;
	private GeoElement intervalPlot;
	private JComponent distPanel;
	private JPanel probPanel;
	
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
		statGeo = new StatGeo(app);
		probDialog = this;
		
		// init variables
		selectedParameters = new double[maxParameterCount];
		plotGeoList = new ArrayList<GeoElement>();
		setDefaults(selectedDist);
		
		initGUI();
		
		// initGUI creates a new EV, so we may need to give it time to setup
		// before adding geos into it
		SwingUtilities.invokeLater(new Runnable(){ 
			public void run() { 
				updateAll(); }
			});
			
		attachView();
		isIniting = false;
			
	} 
	

	private void updateAll(){
		// init the GUI
		
		updateFonts();
		updatePlot();
		updateGUI();
		btnClose.requestFocus();
		isIniting = false;
	}




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
			for(GeoElement listGeo:plotGeoList){
				hideGeoFromViews(listGeo);
			}
	}
	
	private void hideGeoFromViews(GeoElement geo){
		// add the geo to our view and remove it from EV		
		geo.addView(plotPanel);
		plotPanel.add(geo);
		geo.removeView(app.getEuclidianView());
		app.getEuclidianView().remove(geo);

		// turn off tooltips
		geo.setTooltipMode(GeoElement.TOOLTIP_OFF);

	}
	
	private void hideToolTips(){
		for(GeoElement listGeo:plotGeoList){
			listGeo.setTooltipMode(GeoElement.TOOLTIP_OFF);
		}
}
	
	
	
	
	
	//=================================================
	//       Create GUI
	//=================================================
	
	
	private void initGUI() {

		try {		
			
			//==========================
			// button panel
			
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
				
			
			//==========================
			// control panel
			
			distPanel = this.createDistributionPanel();	
			probPanel = this.createProbabilityPanel();
			
			
			JPanel controlPanel = new JPanel();
			controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
			controlPanel.add(distPanel);
			controlPanel.add(probPanel);
			controlPanel.add(buttonPanel);
			controlPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
			
			//==========================
			// create the plot panel (extension of EuclidianView)

			plotPanel = new PlotPanel(app.getKernel());
			plotPanel.setMouseEnabled(true);
			plotPanel.setMouseMotionEnabled(true);
			
			plotPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED))); 
				
			plotPanel.setPreferredSize(new Dimension(350,300));
			
			
			
			
			//==========================
			// main panel
			
			JPanel mainPanel = new JPanel(new BorderLayout());		
			mainPanel.add(plotPanel, BorderLayout.CENTER);		
			mainPanel.add(controlPanel, BorderLayout.SOUTH);
			mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2,2,2));
			
			this.getContentPane().add(mainPanel);
			this.getContentPane().setPreferredSize(new Dimension(450,450));
			//setResizable(false);
			
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
		
		
		// parameter panel
		//=======================================
		JPanel parmeterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		parmeterPanel.setAlignmentX(0.0f);
		
		parmeterPanel.add(comboDistribution);
		
		lblParmeterArray = new JLabel[maxParameterCount];
		fldParmeterArray = new JTextField[maxParameterCount];
		
		for(int i = 0; i < maxParameterCount; ++i){
			lblParmeterArray[i] = new JLabel();
			fldParmeterArray[i] = new MyTextField(app.getGuiManager());
			fldParmeterArray[i].setColumns(6);
			fldParmeterArray[i].addActionListener(this);
			fldParmeterArray[i].addFocusListener(this);
			
			parmeterPanel.add(lblParmeterArray[i]);
			parmeterPanel.add(fldParmeterArray[i]);

		}
		
		JPanel distPanel = new JPanel(new BorderLayout());
		distPanel.add(parmeterPanel, BorderLayout.WEST);
		
		return distPanel;
	}


	
	private JPanel createProbabilityPanel(){

		JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
		JLabel lbl1 = new JLabel("P( ");
		
		fldLow = new MyTextField(app.getGuiManager());
		fldLow.setColumns(6);
		fldLow.addActionListener(this);
		fldLow.addFocusListener(this);
		
		JLabel lbl2 = new JLabel(" \u2264 X \u2264 ");
		
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
		fieldPanel.add(lbl2);
		fieldPanel.add(fldHigh);
		fieldPanel.add(lbl3);
		fieldPanel.add(fldResult);
		
		JPanel probPanel = new JPanel(new BorderLayout());
		probPanel.add(fieldPanel,BorderLayout.WEST);
		
		return probPanel;

	}
	
	
	private void setLabels(){
		
		setTitle(app.getPlain("Probability Calculator"));	
		distPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Distribution")));
		probPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Probability")));
		setLabelArrays();
		
	}

	private void setLabelArrays(){
		
		parameterLabels = new String[distCount][distCount];
		distLabels = new String[distCount];

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
	
	private void plotPDF(int distType){
		
		double xMin, xMax, yMin, yMax;
		
		removeGeos();	

		
		// create new pdf
		String expr = buildPDFExpression(distType,selectedParameters);
		pdf = statGeo.createGeoFromString(expr);
		plotGeoList.add(pdf);

		// get the plot window dimensions
		double[] d = getPlotDimensions(distType, selectedParameters);
		xMin = d[0]; xMax = d[1]; yMin = d[2]; yMax = d[3];


		PlotSettings ps = new PlotSettings();		
		ps.xMin = xMin;
		ps.xMax = xMax;
		ps.yMin = yMin;
		ps.yMax = yMax;
		ps.showYAxis = false;
		ps.isEdgeAxis[0] = false;
		ps.isEdgeAxis[1] = false;
		ps.forceXAxisBuffer = true;


		switch(distType){

		case DIST_NORMAL:
		case DIST_STUDENT:
		case DIST_CHISQUARE:
			ps.pointCaptureStyle = EuclidianView.POINT_CAPTURING_OFF;
			ps.xAxesIntervalAuto = true;
			plotPanel.setPlotSettings(ps);
			pdf.setObjColor(COLOR_PDF);
			pdf.setLineThickness(3);
			pdf.setFixed(true);
			createXAxisPoints();
			createIntegral();

			break;

		case DIST_BINOMIAL:
			ps.pointCaptureStyle = EuclidianView.POINT_CAPTURING_ON_GRID;
			ps.xAxesInterval = 1;
			ps.xAxesIntervalAuto = false;
			plotPanel.setPlotSettings(ps);
			pdf.setObjColor(COLOR_PDF);
			pdf.setAlphaValue(0.0f);
			pdf.setLineThickness(2);
			pdf.setFixed(true);
			createXAxisPoints();
			createIntervalPlot();
			break;

		}	
		
		
		hideAllGeosFromViews();
		hideToolTips();
			
		setXAxisPoints();
		
	}
	
	private void createXAxisPoints(){
		
		String text = "Point[xAxis]";
		lowPoint = (GeoPoint) statGeo.createGeoFromString(text);
		plotGeoList.add(lowPoint);
		lowPoint.setObjColor(COLOR_POINT);
		lowPoint.setPointSize(4);
		lowPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);
		
		text = "Point[xAxis]";
		highPoint = (GeoPoint) statGeo.createGeoFromString(text);
		plotGeoList.add(highPoint);
		highPoint.setObjColor(COLOR_POINT);
		highPoint.setPointSize(4);
		highPoint.setPointStyle(EuclidianView.POINT_STYLE_TRIANGLE_NORTH);
	
	}

	public void setXAxisPoints(){

		isSettingAxisPoints = true;
		lowPoint.setCoords(low, 0.0, 1.0);
		lowPoint.updateCascade();
		highPoint.setCoords(high, 0.0, 1.0);
		highPoint.updateCascade();
		plotPanel.repaint();
		isSettingAxisPoints = false;
	}


	private void createIntegral(){

		String text = "Integral[" + pdf.getLabel() + ", x(" + lowPoint.getLabel() 
		+ "), x(" + highPoint.getLabel() + ")]";
		//System.out.println(text);
		integral  = statGeo.createGeoFromString(text);
		plotGeoList.add(integral);
		integral.setObjColor(COLOR_PDF);
		integral.setAlphaValue(0.25f);

	}

	
	private void createIntervalPlot(){

		StringBuilder probList = new StringBuilder();
		StringBuilder numList = new StringBuilder();
		
		probList.append("Take[{");
		numList.append("Take[{");
		for (int i = 0; i < discreteProbabilities.length; ++i){
			probList.append(discreteProbabilities[i]);
			numList.append(i);
			if(i < discreteProbabilities.length-1){
				probList.append(",");
				numList.append(",");
			}
		}
		probList.append("}, x(" + lowPoint.getLabel() + ")+1, x(" + highPoint.getLabel() + ")+1]");
		numList.append("}, x(" + lowPoint.getLabel() + ")+1, x(" + highPoint.getLabel() + ")+1]");
		

		String text = "BarChart[" + numList + "," + probList + "]";
		//System.out.println(text);
		intervalPlot  = statGeo.createGeoFromString(text);
		plotGeoList.add(intervalPlot);
		intervalPlot.setObjColor(Color.blue);
		intervalPlot.setAlphaValue(0.5f);
		intervalPlot.updateCascade();
		hideGeoFromViews(intervalPlot);

	}

	
	
	private String buildPDFExpression(int type, double[]parms){
		String expr = "";

		switch(type){

		case DIST_NORMAL:
			double mean = parms[0];
			double sigma = parms[1];
			expr = "Normal[" + mean + "," + sigma + ", x]";
			
			//expr =  "1 / sqrt(2 Pi " + sigma + "^2) exp(-((x - " + mu + ")^2 / 2 " + sigma + "^2))";
			break;

		case DIST_STUDENT:
			double v = parms[0];
			expr =  "gamma((" + v + " + 1) / 2) / (sqrt(" + v + " Pi) gamma(" + v + " / 2)) (1 + x^2 / " + v + ")^(-((" + v + " + 1) / 2))";
			break;

		case DIST_CHISQUARE:
			double k = parms[0];		
			expr = "1 / (2^(" + k + " / 2) gamma(" + k + " / 2)) x^(" + k + " / 2 - 1) exp(-(x / 2))";
			break;
			

		case DIST_BINOMIAL:	
			this.calcDiscreteProbList(DIST_BINOMIAL, parms);
			
			StringBuilder probList = new StringBuilder();
			StringBuilder numList = new StringBuilder();
			for (int i = 0; i < discreteProbabilities.length; ++i){
				probList.append(discreteProbabilities[i]);
				numList.append(i);
				if(i < discreteProbabilities.length-1){
					probList.append(",");
					numList.append(",");
				}
			}
			expr = "BarChart[{" + numList + "},{" + probList + "}]";
			
			//Application.debug(expr);		
			break;
			
		}
	
		return expr;
	}


	private void calcDiscreteProbList(int distType, double[] parms){

		String expr = "";
		discreteMax = 0;
		switch(distType){

		case DIST_BINOMIAL:
			double n = parms[0];
			double p = parms[1];
			discreteProbabilities = new double[(int) n+1];
			for (int k = 0; k <=n; ++k){
				expr = "BinomialCoefficient[" + n + "," + k + "]*" + p + "^" + k + "*(1-" + p + ")^(" + (n - k) + ")";
				//Application.debug(expr);
				discreteProbabilities[k] = evaluateExpression(expr);
			}
			
			break;

		}
	}
	
	
	private double getDiscreteMax(){
		double discreteMax = 0;
		for (int k = 0; k < discreteProbabilities.length; ++k){
			discreteMax = Math.max(discreteProbabilities[k], discreteMax);
		}
		return discreteMax;
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

	
	
	private double[] getPlotDimensions(int distType, double[] parms ){

		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;
		
		switch(distType){

		case DIST_NORMAL:
			double mean = parms[0];
			double sigma = parms[1];
			xMin = mean - 5*sigma;
			xMax = mean + 5*sigma;
			yMin = 0;
			yMax = 1.2* evaluateFunction(this.buildPDFExpression(distType, parms), mean);
	
			break;


		case DIST_STUDENT:

			double v = parms[0];
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = 1.2* evaluateFunction(this.buildPDFExpression(distType, parms), 0);

			break;

		case DIST_CHISQUARE:

			double k = parms[0];		
			xMin = 0;
			xMax = 4*k;
			yMin = 0;
			if(k>2)
				yMax = 1.2* evaluateFunction(this.buildPDFExpression(distType, parms), k-2);
			else
				yMax = 1.2* evaluateFunction(this.buildPDFExpression(distType, parms), 0);
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

	
	private void setDefaults(int distType ){
		
		switch(distType){

		case DIST_NORMAL:
			selectedParameters[0] = 0; // mean
			selectedParameters[1]= 1;  // sigma
			low = -1;
			high = 1;
			break;

		case DIST_STUDENT:
			selectedParameters[0] = 10; // df
			low = -1;
			high = 1;
			break;
						
		case DIST_CHISQUARE:
			selectedParameters[0] = 6; // df
			low = 0;
			high = 6;
			break;

		case DIST_BINOMIAL:
			selectedParameters[0] = 20; // n
			selectedParameters[1] = .5; //p 
			low = 7;
			high = 13;
			break;

			
		}
	
	}





	private double calcProbInterval(int distType, double[] parms, double low, double high){

		String exprHigh = "";
		String exprLow = "";
		double prob = 0;

		try {
			switch(distType){

			case DIST_NORMAL:
				exprHigh = "Normal[" + parms[0] + "," + parms[1] + "," + high + "]";
				exprLow = "Normal[" + parms[0] + "," + parms[1] + "," + low + "]";
				prob = evaluateExpression(exprHigh) - evaluateExpression(exprLow);
				break;

			case DIST_STUDENT:
				exprHigh = "TDistribution[" + parms[0] + "," + high + "]";
				exprLow = "TDistribution[" + parms[0]  + "," + low + "]";
				prob = evaluateExpression(exprHigh) - evaluateExpression(exprLow);
				break;

			case DIST_CHISQUARE:
				exprHigh = "ChiSquared[" + parms[0] + "," + high + "]";
				exprLow = "ChiSquared[" + parms[0]  + "," + low + "]";
				prob = evaluateExpression(exprHigh) - evaluateExpression(exprLow);
				break;

			case DIST_BINOMIAL:
				//this.calcDiscreteProbList(distType, parms);
				prob = 0;
				for (int k = (int) low; k <= (int)high; ++k){
					prob += discreteProbabilities[k];
				}				
				break;

			}


		} catch (Exception e) {		
			e.printStackTrace();
		}

		return prob;
	}





	private boolean validateProbFields(){
		
		boolean succ = true;
		succ = low <= high;
		if( selectedDist == DIST_BINOMIAL){
			low = Math.round(low);
			high = Math.round(high);
			succ = low >= 0;
			succ = high <= selectedParameters[0];
			
		}
		return succ;	
			
	}
	

	//=================================================
	//      Event Handlers and Updates
	//=================================================

	

	private void updateGUI() {
		
		kernel.setTemporaryPrintDecimals(6);
				
		for(int i = 0; i < maxParameterCount; ++i ){
			
			if(parameterLabels[selectedDist][i] == null){
				lblParmeterArray[i].setVisible(false);
				fldParmeterArray[i].setVisible(false);

			}else{
				
				lblParmeterArray[i].setVisible(true);
				lblParmeterArray[i].setText(parameterLabels[selectedDist][i]);
				
				fldParmeterArray[i].setVisible(true);
				fldParmeterArray[i].removeActionListener(this);
				fldParmeterArray[i].setText("" + kernel.format(selectedParameters[i]));
				fldParmeterArray[i].setCaretPosition(0);
				fldParmeterArray[i].addActionListener(this);
			}
		}
		
		this.fldLow.setText("" + kernel.format(low));
		this.fldHigh.setText("" + kernel.format(high));
		this.fldResult.setText("" + kernel.format(intervalProbability));
		
		//setXAxisPoints();
		kernel.restorePrintAccuracy();
	}
	
	

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



	public void updateFonts() {

		Font font = app.getPlainFont();

	//	int size = font.getSize();
		//if (size < 12) size = 12; // minimum size
		//double multiplier = (size)/12.0;

		setFont(font);

	}

	
	private void updatePlot(){
		plotPDF(selectedDist);
		updateIntervalProbability();
		setXAxisPoints();
	}

	
	private void updateIntervalProbability(){		
		intervalProbability = calcProbInterval(selectedDist, selectedParameters, low, high);
		
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
			selectedDist = comboDistribution.getSelectedIndex();
			setDefaults(selectedDist);
			updatePlot();
			updateGUI();
			btnClose.requestFocus();
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

			for(int i=0; i< maxParameterCount; ++i)
				if (source == fldParmeterArray[i]) {
					selectedParameters[i] = value;
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
					
			updateIntervalProbability();
			updateGUI();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}
	

	public void focusGained(FocusEvent arg0) {}

	public void focusLost(FocusEvent e) {
		//doActionPerformed(e.getSource());
		doTextFieldActionPerformed((JTextField)(e.getSource()));
		updateGUI();
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
