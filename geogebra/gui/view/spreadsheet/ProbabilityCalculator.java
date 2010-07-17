package geogebra.gui.view.spreadsheet;



import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Construction;
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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

public class ProbabilityCalculator extends JDialog implements View, ActionListener, FocusListener   {
	 
	private Application app;
	private Kernel kernel; 
	private ProbabilityCalculator probDialog;
	
	// supported distributions
	private static final int DIST_NORMAL = 0;
	private static final int DIST_STUDENT = 1;
	private static final int DIST_CHISQUARE = 2;
	private static final int DIST_BINOMIAL = 3;
	
	// parms
	private int numDist = 4;
	private int selectedDist = DIST_STUDENT;
	private String[] distLabels; 
	private String[][] parmLabels;
	private int numParms = 3;
	private double[] selectedParms ;
	private double xMin, xMax, yMin, yMax;
	private double low = -1, high = 1, prob = 0;
	
	
	// gui
	private JButton btnClose, btnOptions, btnExport, btnDisplay;
	private JComboBox comboDistribution;
	private JTextField[] fldParmArray;
	private JTextField fldLow,fldHigh,fldResult;
	private JLabel[] lblParmArray;
	private PlotPanel plotPanel;
	
	
	// GeoElements
	private GeoPoint lowPoint,highPoint;
	private GeoFunction pdf;
	private GeoElement integral;
	
	
	// flags
	private boolean isIniting;
	private boolean isSettingAxisPoints = false;
	
	private static final Color COLOR_PDF = new Color(0, 153, 153);
	
	
	
	/*************************************************
	 * Construct the dialog
	 */
	public ProbabilityCalculator(SpreadsheetView spView, Application app) {
		super(app.getFrame(),false);
		
		isIniting = true;
		this.app = app;	
		kernel = app.getKernel();
		probDialog = this;
		
		// init variables
		selectedParms = new double[4];
		setLabels();
		
		// init the GUI
		initGUI();
		updateFonts();
		updateGUI();
		
		attachView();
		btnClose.requestFocus();
		isIniting = false;
			
	} //============= END  constructor
	
	
	
	
	public void removeGeos(){
		plotPanel.removeGeos();
	}
	
	
	
	//=================================================
	//       Create GUI
	//=================================================
	
	
	private void initGUI() {

		try {
			setTitle(app.getPlain("Probability Calculator"));	
			
			//===========================================
			// button panel
			
			btnClose = new JButton(app.getPlain("Close"));
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
				
			
			//===========================================
			// control panel
			
			JPanel distPanel = this.createDistributionPanel();
			distPanel.setBorder(BorderFactory.createTitledBorder("Distribution"));
				
			JPanel probPanel = this.createProbPanel();
			probPanel.setBorder(BorderFactory.createTitledBorder("Probability"));
			
			JPanel controlPanel = new JPanel();
			controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
			controlPanel.add(distPanel);
			controlPanel.add(probPanel);
			controlPanel.add(buttonPanel);
			controlPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			
		
			//===========================================
			// plot panel
	
			plotPanel = new PlotPanel(app);
			
			plotPanel.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2),
					BorderFactory.createBevelBorder(BevelBorder.LOWERED))); 
					
			
			setDefaultParms(selectedDist);
			plotPDF(selectedDist);
			plotPanel.setPreferredSize(new Dimension(350,300));
			
			
			//============================================
			// main panel
			
			JPanel mainPanel = new JPanel(new BorderLayout());		
			mainPanel.add(plotPanel, BorderLayout.CENTER);		
			mainPanel.add(controlPanel, BorderLayout.SOUTH);
			mainPanel.setBorder(BorderFactory.createEmptyBorder(2, 2,2,2));
			
			this.getContentPane().add(mainPanel);
			this.getContentPane().setPreferredSize(new Dimension(450,450));
			//setResizable(false);
			pack();
			

			setLocationRelativeTo(app.getFrame());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private JPanel createDistributionPanel(){
		
		JPanel dp = new JPanel(new BorderLayout());
		comboDistribution = new JComboBox(distLabels);
		comboDistribution.setSelectedIndex(selectedDist);
		comboDistribution.addActionListener(new ActionListener() {       
			public void actionPerformed(ActionEvent e)
			{
				selectedDist = comboDistribution.getSelectedIndex();
				setDefaultParms(selectedDist);
				probDialog.plotPDF(selectedDist);
				updateGUI();
				btnClose.requestFocus();
			}
		});      
		
		
		// parm panel
		//=======================================
		JPanel parmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		parmPanel.setAlignmentX(0.0f);
		
		parmPanel.add(comboDistribution);
		
		lblParmArray = new JLabel[4];
		fldParmArray = new JTextField[4];
		
		for(int i = 0; i < numParms; ++i){
			lblParmArray[i] = new JLabel();
			fldParmArray[i] = new MyTextField(app.getGuiManager());
			fldParmArray[i].setColumns(6);
			fldParmArray[i].addActionListener(this);
			fldParmArray[i].addFocusListener(this);
			
			parmPanel.add(lblParmArray[i]);
			parmPanel.add(fldParmArray[i]);

		}
		
		dp.add(parmPanel, BorderLayout.WEST);
		
		return dp;
	}


	
	private JPanel createProbPanel(){

		JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	
		JLabel lbl1 = new JLabel("P( ");
		
		fldLow = new MyTextField(app.getGuiManager());
		fldLow.setColumns(6);
		fldLow.addActionListener(this);
		fldLow.addFocusListener(this);
		
		JLabel lbl2 = new JLabel(" < X < ");
		
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

		parmLabels = new String[numDist][numDist];
		distLabels = new String[numDist];

		distLabels[DIST_NORMAL] = app.getPlain("Normal");
		parmLabels[DIST_NORMAL][0] = app.getPlain("mean");
		parmLabels[DIST_NORMAL][1] = app.getPlain("sigma");

		distLabels[DIST_STUDENT] = app.getPlain("Student");
		parmLabels[DIST_STUDENT][0] = app.getPlain("df");

		distLabels[DIST_CHISQUARE] = app.getPlain("ChiSquare");	
		parmLabels[DIST_CHISQUARE][0] = app.getPlain("df");

		distLabels[DIST_BINOMIAL] = app.getPlain("Binomial");
		parmLabels[DIST_BINOMIAL][0] = app.getPlain("n");
		parmLabels[DIST_BINOMIAL][1] = app.getPlain("p");


	}

	
	
	//=================================================
	//       Plotting
	//=================================================
	
	private void plotPDF(int distType){
		plotPDF(distType, selectedParms, xMin, xMax, yMin,yMax);	
	}
	
	private void plotPDF(int distType, double[]parms, double xMin, double xMax, double yMin, double yMax){
			
		double[] d = getPlotDimensions(distType, parms);
		
		if(lowPoint != null)
			lowPoint.remove();
		lowPoint = null;
		if(highPoint != null)
			highPoint.remove();
		highPoint = null;
		
		if(integral != null)
			integral.remove();
		integral = null;
		
			if(pdf != null)
			pdf.remove();
		pdf = null;
		
		
		plotPanel.removeGeos();
		plotPanel.setAutoRemoveGeos(false);
		
		xMin = d[0];
		xMax = d[1];
		yMin = d[2];
		yMax = d[3];
	
		pdf = (GeoFunction) plotPanel.createPDF(buildExpressionPDF(distType,parms), xMin, xMax, yMin, yMax);	
		pdf.setObjColor(COLOR_PDF);
		pdf.setLineThickness(3);
		
		createXAxisPoints();
		createIntegral();
		
		plotPanel.setAutoRemoveGeos(true);
	
	}
	
	private void createXAxisPoints(){
		
		String text = "Point[xAxis]";
		lowPoint = (GeoPoint) plotPanel.createGeoFromString(text);
		lowPoint.setObjColor(COLOR_PDF);
		
		text = "Point[xAxis]";
		highPoint = (GeoPoint) plotPanel.createGeoFromString(text);
		highPoint.setObjColor(COLOR_PDF);
	
		setXAxisPoints();	
	}
	
	
	private void createIntegral(){
		
		String text = "Integral[" + pdf.getLabel() + ", x(" + lowPoint.getLabel() 
						+ "), x(" + highPoint.getLabel() + ")]";
		//System.out.println(text);
		integral  = plotPanel.createGeoFromString(text);
		integral.setObjColor(COLOR_PDF);
		integral.setAlphaValue(0.25f);
		
	}
	
	
	public void setXAxisPoints(){
		System.out.println(low);
		isSettingAxisPoints = true;
		lowPoint.setCoords(low, 0.0, 1.0);
		lowPoint.updateCascade();
		highPoint.setCoords(high, 0.0, 1.0);
		highPoint.updateCascade();
		plotPanel.getMyEuclidianView().repaint();
		isSettingAxisPoints = false;
	}
	
	private String buildExpressionPDF(int type, double[]parms){
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
			

		}
	
		return expr;
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

		//double[] d = new double[4];
		double xMin = 0, xMax = 0, yMin = 0, yMax = 0;
		
		switch(distType){

		case DIST_NORMAL:
			double mean = parms[0];
			double sigma = parms[1];
			xMin = mean - 5*sigma;
			xMax = mean + 5*sigma;
			yMin = 0;
			yMax = 1.2* evaluateFunction(this.buildExpressionPDF(distType, parms), mean);
	
			break;


		case DIST_STUDENT:

			double v = parms[0];
			xMin = -5;
			xMax = 5;
			yMin = 0;
			yMax = 1.2* evaluateFunction(this.buildExpressionPDF(distType, parms), 0);
			
			break;

		case DIST_CHISQUARE:

			double k = parms[0];		
			xMin = 0;
			xMax = 4*k;
			yMin = 0;
			if(k>2)
				yMax = 1.2* evaluateFunction(this.buildExpressionPDF(distType, parms), k-2);
			else
				yMax = 1.2* evaluateFunction(this.buildExpressionPDF(distType, parms), 0);
			break;
		}
		
		double[] d = {xMin, xMax, yMin, yMax};

		return d;


	}

	
	private void setDefaultParms(int distType ){
		
		switch(distType){

		case DIST_NORMAL:
			selectedParms[0] = 0; // mean
			selectedParms[1]= 1;  // sigma
			break;

		case DIST_STUDENT:
			selectedParms[0] = 10; // df
			break;
						
		case DIST_CHISQUARE:
			selectedParms[0] = 6; // df
			break;

		}
	
	}


	private double calcProb(int distType, double[] parms, double low, double high){
		
		String exprHigh = "";
		String exprLow = "";
		double prob = 0;

		try {
			switch(distType){
			
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
			}
			
			prob = evaluateExpression(exprHigh) - evaluateExpression(exprLow);
			
		} catch (Exception e) {		
			e.printStackTrace();
		}
		
		return prob;
	}
	
	
	

	//=================================================
	//      Event Handlers and Updates
	//=================================================

	

	private void updateGUI() {
		
		kernel.setTemporaryPrintDecimals(6);
				
		for(int i = 0; i < numParms; ++i ){
			
			if(parmLabels[selectedDist][i] == null){
				lblParmArray[i].setVisible(false);
				fldParmArray[i].setVisible(false);

			}else{
				lblParmArray[i].setVisible(true);
				fldParmArray[i].setVisible(true);
				lblParmArray[i].setText(parmLabels[selectedDist][i]);
				fldParmArray[i].removeActionListener(this);
				fldParmArray[i].setText("" + kernel.format(selectedParms[i]));
				fldParmArray[i].setCaretPosition(0);
				fldParmArray[i].addActionListener(this);
			}
		}
		
		this.fldLow.setText("" + kernel.format(low));
		this.fldHigh.setText("" + kernel.format(high));
		this.fldResult.setText("" + kernel.format(prob));
		
		//setXAxisPoints();
		kernel.restorePrintAccuracy();
	}
	
	

	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		if(isVisible){
		
		}else{
			removeGeos();
			detachView();
		}
	}



	public void updateFonts() {

		Font font = app.getPlainFont();

		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;

		setFont(font);

	}

	
	private void updatePlot(){
		plotPDF(selectedDist);
		updateProb();
	}

	
	private void updateProb(){		
		prob = calcProb(selectedDist, selectedParms, low, high);
	}
	

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();	
		if(source == btnClose){
			setVisible(false);
		}
		else if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
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

			for(int i=0; i< numParms; ++i)
				if (source == fldParmArray[i]) {
					selectedParms[i] = value;
					//validateParms(selectedParms);
					updatePlot();
				}
			
			if(source == fldLow){
				low = value;
				this.setXAxisPoints();
				
			}
			if(source == fldHigh){
				high = value;
				this.setXAxisPoints();
			}
					
			updateProb();
			updateGUI();

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}
	

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		//doActionPerformed(e.getSource());
		doTextFieldActionPerformed((JTextField)(e.getSource()));
		updateGUI();
	}




	//=================================================
	//      View Implementation
	//=================================================
	
	public void add(GeoElement geo) {
		
	}

	public void clearView() {
	}

	public void remove(GeoElement geo) {
		
	}

	public void rename(GeoElement geo) {
	}

	public void repaintView() {
	}

	public void reset() {	
	}

	public void update(GeoElement geo) {
		double[] coords = new double[2];;
		if(!isSettingAxisPoints){
			if(geo.equals(lowPoint)){	
				low = lowPoint.getInhomX();
				updateProb();
				updateGUI();
			}
			if(geo.equals(highPoint)){	
				high = highPoint.getInhomX();
				updateProb();
				updateGUI();
			}
		}
	}
	
	
	public void updateAuxiliaryObject(GeoElement geo) {
	}

	public void attachView() {
		//clearView();
		//kernel.notifyAddAll(this);
		kernel.attach(this);		
	}

	public void detachView() {
		kernel.detach(this);
		//clearView();
		//kernel.notifyRemoveAll(this);		
	}

	
}
