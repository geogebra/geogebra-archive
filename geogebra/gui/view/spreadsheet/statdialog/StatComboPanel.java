package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class to dynamically display plots and statistics in coordination with the
 * StatDialog class.
 * 
 * @author G.Sturr
 * 
 */
public class StatComboPanel extends JPanel implements ActionListener, StatPanelInterface{

	// ggb fields
	private Application app;
	private StatDialog statDialog;
	private StatGeo statGeo;

	// stat dialog mode
	private int mode;

	// one variable plot types
	public static final int PLOT_HISTOGRAM = 0;
	public static final int PLOT_BOXPLOT = 1;
	public static final int PLOT_DOTPLOT = 2;
	public static final int PLOT_NORMALQUANTILE = 3;
	public static final int PLOT_FREQUENCYTABLE = 4;
	public static final int PLOT_STEMPLOT = 5;
	public static final int PLOT_ONEVAR_INFERENCE = 6;

	public static final int PLOT_ZTEST = 7;
	public static final int PLOT_ZINT = 8;
	public static final int PLOT_TTEST = 9;
	public static final int PLOT_TINT = 10;
	

	// two variable plot types
	public static final int PLOT_SCATTERPLOT = 30;
	public static final int PLOT_RESIDUAL = 31;
	public static final int PLOT_REGRESSION_INFERENCE= 32;

	// multi variable plot types
	public static final int PLOT_MULTIBOXPLOT = 50;
	public static final int PLOT_MULTIVARSTATS = 51;
	public static final int PLOT_ANOVA= 52;
	public static final int PLOT_TWOVAR_INFERENCE = 53;
	
	public static final int PLOT_TTEST_2MEANS = 54;
	public static final int PLOT_TTEST_PAIRED = 55;
	public static final int PLOT_TINT_2MEANS = 56;
	public static final int PLOT_TINT_PAIRED = 57;
	
	// currently selected plot type
	private int selectedPlot;

	
	// plot reference vars
	protected static HashMap<Integer, String> plotMap;
	private HashMap<String, Integer> plotMapReverse;

	
	private StatPanelSettings settings;


	// geos
	private GeoList regressionAnalysisList;
	private ArrayList<GeoElement> plotGeoList;
	private GeoElement histogram, dotPlot, boxPlotTitles, frequencyPolygon, normalCurve;


	// display panels 	
	private JPanel statDisplayPanel;
	private TwoVarInferencePanel twoVarInferencePanel;
	private OneVarInferencePanel oneVarInferencePanel;
	private JPanel metaPlotPanel, northTitlePanel, southTitlePanel;
	private PlotPanelEuclidianView plotPanel;
	private ANOVATable anovaTable;
	private MultiVarStatPanel multiVarStatPanel;
	private LinearRegressionPanel regressionPanel;

	private JLabel imageContainer;
	private StatTable regressionAnalysisTable;

	// control panel
	private JPanel controlPanel;
	private JPanel controlCards;
	private boolean hasControlPanel;
	private JComboBox cbDisplayType;


	// options button and sidebar panel
	private OptionsPanel optionsPanel; 
	private JToggleButton optionsButton;

	// numClasses panel 
	private int numClasses = 6;
	private JPanel numClassesPanel;
	private JSlider sliderNumClasses; 

	// manual classes panel
	private JToolBar manualClassesPanel;
	private JLabel lblStart;
	private JLabel lblWidth;
	private MyTextField fldStart;
	private MyTextField fldWidth;
	private JLabel lblNumClasses;

	// model prediction panel
	private JPanel southPanel;	
	private JPanel predictionPanel;
	private JLabel lblEvaluate;
	private MyTextField fldInputX;
	private JLabel lblOutputY;
	private boolean showSouthPanel;

	// stemplot adjustment panel
	private JToolBar stemAdjustPanel;
	private JLabel lblAdjust;
	private JButton minus;
	private JButton none;
	private JButton plus;
	private int stemPlotAdjustment = 0;
	private JPanel imagePanel;
	private JComboBox cbInferenceType;
	private JPanel inferencePanel;

	private JLabel lblTitleX, lblTitleY;
	private MyTextField fldTitleX, fldTitleY;
	
	
	

	/***************************************** 
	 * Constructs a ComboStatPanel
	 */
	public  StatComboPanel( StatDialog statDialog, int defaultPlotIndex, int mode, boolean hasControlPanel){

		this.statDialog = statDialog;
		this.app = statDialog.getApp();
		this.mode = mode;
		this.statGeo = statDialog.getStatGeo();
		this.selectedPlot = defaultPlotIndex;
		this.hasControlPanel=hasControlPanel;
		plotGeoList = new ArrayList<GeoElement>();

		createPlotMap();

		createGUI();
		setLabels();
		updatePlot(true);

	}



	//==============================================
	//              GUI
	//==============================================


	private void createGUI(){

		// create settings
		settings = new StatPanelSettings();


		// create options button and panel
		optionsPanel= new OptionsPanel(app, settings);
		optionsButton = new JToggleButton();
		//optionsButton.setIcon(app.getImageIcon("document-properties.png"));
		optionsButton.setFocusable(false);
		//optionsButton.setIcon(app.getImageIcon("tool.png"));
		optionsButton.addActionListener(this);



		// create control panel 
		if(hasControlPanel){

			// create sub-control panels
			createDisplayTypeComboBox();
			createNumClassesPanel();
			createManualClassesPanel();
			createStemPlotAdjustmentPanel();
			JPanel emptyControl = new JPanel(new BorderLayout());
			emptyControl.add(new JLabel("  "));

			// put sub-control panels into a card layout
			controlCards = new JPanel(new CardLayout());
			controlCards.add("numClassesPanel", numClassesPanel);
			controlCards.add("manualClassesPanel", manualClassesPanel);
			controlCards.add("stemAdjustPanel", stemAdjustPanel);
			controlCards.add("blankPanel", emptyControl);

			// control panel
			controlPanel = new JPanel(new BorderLayout());
			controlPanel.add(flowPanel(cbDisplayType),BorderLayout.WEST);
			controlPanel.add(controlCards,BorderLayout.CENTER);
			controlPanel.add(flowPanelRight(optionsButton),BorderLayout.EAST);
		}


		// create display panels 

		plotPanel = new PlotPanelEuclidianView(app.getKernel());
		//plotPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		//settings.plotPanel = plotPanel;

		northTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		southTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		northTitlePanel.setBackground(plotPanel.getBackground());
		southTitlePanel.setBackground(plotPanel.getBackground());
		lblTitleX = new JLabel();
		lblTitleY = new JLabel();
		fldTitleX = new MyTextField(app.getGuiManager(),20);
		fldTitleY = new MyTextField(app.getGuiManager(),20);
		fldTitleX.setEditable(false);
		fldTitleX.setBorder(BorderFactory.createEmptyBorder());
		fldTitleY.setEditable(false);
		fldTitleY.setBorder(BorderFactory.createEmptyBorder());
		fldTitleX.setBackground(Color.white);
		fldTitleY.setBackground(Color.white);


		metaPlotPanel = new JPanel(new BorderLayout());
		metaPlotPanel.add(plotPanel, BorderLayout.CENTER);
		metaPlotPanel.add(northTitlePanel, BorderLayout.NORTH);
		metaPlotPanel.add(southTitlePanel, BorderLayout.SOUTH);

		createImagePanel();

		if(mode == statDialog.MODE_ONEVAR){
			oneVarInferencePanel =new OneVarInferencePanel(app, statDialog);
		}

		else if(mode == statDialog.MODE_REGRESSION){
			regressionPanel = new LinearRegressionPanel(app, statDialog);
		}

		else if(mode == statDialog.MODE_MULTIVAR){
			twoVarInferencePanel =new TwoVarInferencePanel(app, statDialog);
			multiVarStatPanel = new MultiVarStatPanel(app, statDialog, -1);
			anovaTable = new ANOVATable(app, statDialog);
		}



		// put display panels into a card layout

		statDisplayPanel = new JPanel(new CardLayout());
		statDisplayPanel.setBackground(plotPanel.getBackground());

		statDisplayPanel.add("plotPanel", metaPlotPanel);
		statDisplayPanel.add("imagePanel", new JScrollPane(imagePanel));

		if(mode == statDialog.MODE_ONEVAR){
			statDisplayPanel.add("oneVarInferencePanel", oneVarInferencePanel);
		}
		else if(mode == statDialog.MODE_REGRESSION){
			statDisplayPanel.add("regressionPanel", regressionPanel);
		}
		else if(mode == statDialog.MODE_MULTIVAR){
			statDisplayPanel.add("twoVarInferencePanel", twoVarInferencePanel);
			statDisplayPanel.add("multiVarStatPanel", multiVarStatPanel);
			statDisplayPanel.add("anovaPanel", anovaTable);	
		}



		// create options panel

		optionsPanel= new OptionsPanel(app, settings);
		optionsPanel.addPropertyChangeListener("settings", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updatePlot(true);
			}
		});
		optionsPanel.setVisible(false);



		// create south panel  (just holds prediction panel for now)

		southPanel= new JPanel(new BorderLayout());	
		showSouthPanel = false;
		createPredictonPanel();
		southPanel.add(predictionPanel);


		// =======================================
		// put all the panels together

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setLayout(new BorderLayout());
		if(hasControlPanel){
			mainPanel.add(controlPanel,BorderLayout.NORTH);
		}
		mainPanel.add(statDisplayPanel,BorderLayout.CENTER);		
		mainPanel.add(optionsPanel,BorderLayout.EAST);
		//mainPanel.add(southPanel,BorderLayout.SOUTH);

		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.CENTER);
		//	this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		this.setBorder(BorderFactory.createEmptyBorder());
		controlPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlShadow));

	}


	/**
	 * Sets the labels to the current language
	 */
	public void setLabels(){

		createPlotMap();
		createDisplayTypeComboBox();
		lblNumClasses.setText(app.getMenu("Classes") + ": ");
		lblStart.setText(app.getMenu("Start") + ": ");
		lblWidth.setText(app.getMenu("Width") + ": ");
		if(mode == statDialog.MODE_REGRESSION){

			lblTitleX.setText(app.getMenu("Column.X") + ": ");
			lblTitleY.setText(app.getMenu("Column.Y") + ": ");

			lblEvaluate.setText(app.getMenu("Evaluate")+ ": ");
		}
		lblAdjust.setText(app.getMenu("Adjustment")+ ": ");

		optionsButton.setText(app.getMenu("Options"));
		optionsPanel.setLabels();

	}



	/**
	 * Creates the JComboBox that selects display type
	 */
	private void createDisplayTypeComboBox(){

		if(cbDisplayType == null){
			cbDisplayType = new JComboBox();
			cbDisplayType.setRenderer(new MyRenderer());
			
			
		}else{
			cbDisplayType.removeActionListener(this);
			cbDisplayType.removeAllItems();
		}
		
		switch(mode){

		case StatDialog.MODE_ONEVAR:
			cbDisplayType.addItem(plotMap.get(PLOT_HISTOGRAM));
			cbDisplayType.addItem(plotMap.get(PLOT_BOXPLOT));
			cbDisplayType.addItem(plotMap.get(PLOT_DOTPLOT));
			cbDisplayType.addItem(plotMap.get(PLOT_STEMPLOT));
		//	cbDisplayType.addItem(plotMap.get(PLOT_FREQUENCYTABLE));
			cbDisplayType.addItem(plotMap.get(PLOT_NORMALQUANTILE));
			cbDisplayType.addItem(MyRenderer.SEPARATOR);
			cbDisplayType.addItem(plotMap.get(PLOT_ZTEST));
			cbDisplayType.addItem(plotMap.get(PLOT_TTEST));
			cbDisplayType.addItem(MyRenderer.SEPARATOR);
			cbDisplayType.addItem(plotMap.get(PLOT_ZINT));
			cbDisplayType.addItem(plotMap.get(PLOT_TINT));
			break;

		case StatDialog.MODE_REGRESSION:
			cbDisplayType.addItem(plotMap.get(PLOT_SCATTERPLOT));
			cbDisplayType.addItem(plotMap.get(PLOT_RESIDUAL));
			cbDisplayType.addItem(plotMap.get(PLOT_REGRESSION_INFERENCE));
			break;

		case StatDialog.MODE_MULTIVAR:
			cbDisplayType.addItem(plotMap.get(PLOT_MULTIBOXPLOT));
			cbDisplayType.addItem(plotMap.get(PLOT_MULTIVARSTATS));
			cbDisplayType.addItem(MyRenderer.SEPARATOR);
			cbDisplayType.addItem(plotMap.get(PLOT_ANOVA));
			cbDisplayType.addItem(plotMap.get(PLOT_TTEST_2MEANS));
			cbDisplayType.addItem(plotMap.get(PLOT_TTEST_PAIRED));
			cbDisplayType.addItem(MyRenderer.SEPARATOR);
			cbDisplayType.addItem(plotMap.get(PLOT_TINT_2MEANS));
			cbDisplayType.addItem(plotMap.get(PLOT_TINT_PAIRED));
			break;
		}

		cbDisplayType.setSelectedItem(plotMap.get(selectedPlot));
		cbDisplayType.addActionListener(this);
		cbDisplayType.setMaximumRowCount(cbDisplayType.getItemCount());

	}



	/**
	 * Creates a title panels for the scatterplot 
	 */
	private void updateTitlePanels(){

		if(selectedPlot == this.PLOT_SCATTERPLOT){
			southTitlePanel.add(lblTitleX);
			southTitlePanel.add(fldTitleX);
			northTitlePanel.add(lblTitleY);
			northTitlePanel.add(fldTitleY);
		}else{
			southTitlePanel.removeAll();
			northTitlePanel.removeAll();
		}
		southTitlePanel.revalidate();
		northTitlePanel.revalidate();

	}


	/**
	 * Creates a display panel to hold an image, e.g. tabletext
	 */
	private void createImagePanel(){

		imagePanel = new JPanel(new BorderLayout());
		imagePanel.setBorder(BorderFactory.createEmptyBorder());
		imagePanel.setBackground(Color.WHITE);
		imageContainer = new JLabel();
		imagePanel.setAlignmentX(SwingConstants.CENTER);
		imagePanel.setAlignmentY(SwingConstants.CENTER);
		imageContainer.setHorizontalAlignment(SwingConstants.CENTER);
		imagePanel.add(imageContainer, BorderLayout.CENTER);

	}


	/**
	 * Creates a control panel for adjusting the number of histogram classes
	 */
	private void createNumClassesPanel(){

		lblNumClasses = new JLabel();
		final JTextField fldNumClasses = new JTextField(""+numClasses);
		fldNumClasses.setEditable(false);
		fldNumClasses.setOpaque(true);
		fldNumClasses.setColumns(2);
		fldNumClasses.setHorizontalAlignment(JTextField.CENTER);
		fldNumClasses.setBackground(Color.WHITE);

		sliderNumClasses = new JSlider(JSlider.HORIZONTAL, 3, 20, numClasses);
		Dimension d = sliderNumClasses.getPreferredSize();
		d.width = 80;
		sliderNumClasses.setPreferredSize(d);
		sliderNumClasses.setMinimumSize(new Dimension(50,d.height));


		sliderNumClasses.setMajorTickSpacing(1);
		sliderNumClasses.setSnapToTicks(true);
		sliderNumClasses.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				JSlider slider = (JSlider) evt.getSource();
				numClasses = slider.getValue();
				fldNumClasses.setText(("" + numClasses));
				updatePlot(true);
				//btnClose.requestFocus();
			}
		});

		numClassesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		numClassesPanel.add(sliderNumClasses);
		numClassesPanel.add(lblNumClasses);
		numClassesPanel.add(fldNumClasses);

	}



	/**
	 * Creates a panel to evaluate the regression model for a given x value
	 */
	private void createPredictonPanel(){
		predictionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		lblEvaluate = new JLabel();	
		fldInputX = new MyTextField(app.getGuiManager());
		fldInputX.setColumns(4);
		lblOutputY = new JLabel();

		predictionPanel.add(lblEvaluate);
		predictionPanel.add(new JLabel("x = "));
		predictionPanel.add(fldInputX);
		predictionPanel.add(new JLabel("y = "));
		predictionPanel.add(lblOutputY);
	}



	/**
	 * Creates a control panel to adjust the stem plot
	 */
	private void createStemPlotAdjustmentPanel(){

		lblAdjust = new JLabel();
		minus = new JButton("-1");
		none = new JButton("0");
		plus = new JButton("+1");
		minus.addActionListener(this);
		none.addActionListener(this);
		plus.addActionListener(this);
		none.setSelected(true);
		stemAdjustPanel = new JToolBar();
		stemAdjustPanel.setFloatable(false);
		stemAdjustPanel.add(minus);
		stemAdjustPanel.add(none);
		stemAdjustPanel.add(plus);

	}


	/**
	 * Creates a control panel for manually setting classes
	 */
	private void createManualClassesPanel(){

		lblStart = new JLabel();
		lblWidth = new JLabel();

		fldStart = new MyTextField(app.getGuiManager());
		Dimension d = fldStart.getMaximumSize();
		d.height = fldStart.getPreferredSize().height;
		fldStart.setMaximumSize(d);
		fldStart.addActionListener(this);
		fldStart.setText("" + (int)settings.classStart);

		fldWidth = new MyTextField(app.getGuiManager());
		fldWidth.setMaximumSize(d);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);
		fldWidth.addActionListener(this);
		fldWidth.setText("" + (int)settings.classWidth);

		manualClassesPanel = new JToolBar();
		manualClassesPanel.setFloatable(false);
		manualClassesPanel.add(lblStart);
		manualClassesPanel.add(fldStart);
		manualClassesPanel.add(lblWidth);
		manualClassesPanel.add(fldWidth);

	}

	/*
	private void createTwoSampleSelectionPanel(){

		modelTitle1 = new DefaultComboBoxModel();
		modelTitle2 = new DefaultComboBoxModel();
		cbTitle1 = new JComboBox(modelTitle1);
		cbTitle2 = new JComboBox(modelTitle2);
		lblTitle1 = new JLabel();
		lblTitle2 = new JLabel();

		lblTitle1.setText("1: ");
		lblTitle2.setText("2: ");

		modelTitle1.removeAllElements();
		modelTitle2.removeAllElements();
		String[] dataTitles = statDialog.getDataTitles();
		if(dataTitles!= null){
			for(int i=0; i < dataTitles.length; i++){
				modelTitle1.addElement(dataTitles[i]);
				modelTitle2.addElement(dataTitles[i]);
			}
		}


		JToolBar p = new JToolBar();

		p.add(flowPanel(lblTitle1,cbTitle1));
		p.add(flowPanel(lblTitle2,cbTitle2));
		twoSampleSelectionPanel =new JPanel(new BorderLayout());
		twoSampleSelectionPanel.add(p);
	}
	 */


	/**
	 * Creates two hash maps for JComboBox selections, 
	 * 1) plotMap:  Key = integer display type, Value = JComboBox menu string  
	 * 2) plotMapReverse: Key = JComboBox menu string, Value = integer display type    
	 */
	private void createPlotMap(){
		if(plotMap == null)
			plotMap = new HashMap<Integer,String>();

		plotMap.clear();
		plotMap.put(PLOT_HISTOGRAM, app.getMenu("Histogram"));
		plotMap.put(PLOT_BOXPLOT, app.getMenu("Boxplot"));
		plotMap.put(PLOT_DOTPLOT, app.getMenu("DotPlot"));
		plotMap.put(PLOT_NORMALQUANTILE, app.getMenu("NormalQuantilePlot"));
		plotMap.put(PLOT_FREQUENCYTABLE, app.getMenu("FrequencyTable"));
		plotMap.put(PLOT_STEMPLOT, app.getMenu("StemPlot"));
		plotMap.put(PLOT_ONEVAR_INFERENCE, app.getMenu("OneVariableInference"));
		
		plotMap.put(PLOT_TTEST, app.getMenu("TMeanTest"));
		plotMap.put(PLOT_TINT, app.getMenu("TMeanInterval"));
		plotMap.put(PLOT_ZTEST, app.getMenu("ZMeanTest"));
		plotMap.put(PLOT_ZINT, app.getMenu("ZMeanInterval"));
		

		plotMap.put(PLOT_SCATTERPLOT, app.getMenu("Scatterplot"));
		plotMap.put(PLOT_RESIDUAL, app.getMenu("ResidualPlot"));
		plotMap.put(PLOT_REGRESSION_INFERENCE, app.getMenu("RegressionInference"));

		plotMap.put(PLOT_MULTIBOXPLOT, app.getMenu("StackedBoxPlots"));
		plotMap.put(PLOT_MULTIVARSTATS, app.getMenu("Statistics"));
		plotMap.put(PLOT_ANOVA, app.getMenu("ANOVA"));
		plotMap.put(PLOT_TWOVAR_INFERENCE, app.getMenu("TwoVariableInference"));
		
		plotMap.put(PLOT_TTEST_2MEANS, app.getMenu("TTestDifferenceOfMeans"));
		plotMap.put(PLOT_TTEST_PAIRED, app.getMenu("TTestPairedDifferences"));
		plotMap.put(PLOT_TINT_2MEANS, app.getMenu("TEstimateDifferenceOfMeans"));
		plotMap.put(PLOT_TINT_PAIRED, app.getMenu("TEstimatePairedDifferences"));
		
		// REVERSE PLOT MAP
		plotMapReverse = new HashMap<String, Integer>();
		for(Integer key: plotMap.keySet()){
			plotMapReverse.put(plotMap.get(key), key);
		}

	}

	

	//==============================================
	//              DISPLAY UPDATE
	//==============================================

	public void updatePlot(boolean doCreate){

		GeoList dataListSelected = statDialog.getStatDialogController().getDataSelected();
		
		GeoElement geo;
		String underConstruction = "\\text{" + app.getPlain("NotAvailable") + "}";
		if(hasControlPanel)
			((CardLayout)controlCards.getLayout()).show(controlCards, "blankPanel");	

		if(doCreate)
			clearPlotGeoList();

		optionsButton.setVisible(true);
		southPanel.setVisible(false);
		updateTitlePanels();

		switch(selectedPlot){

		case PLOT_HISTOGRAM:			
			if(doCreate){
				if(histogram != null)
					histogram.remove();
				histogram = statGeo.createHistogram( dataListSelected, numClasses, settings, false);
				plotGeoList.add(histogram);
			}
			if(frequencyPolygon != null)
				frequencyPolygon.remove();
			if(settings.hasOverlayPolygon){
				frequencyPolygon = statGeo.createHistogram( dataListSelected, numClasses, settings, true);
				plotGeoList.add(frequencyPolygon);
			}
			if(normalCurve != null)
				normalCurve.remove();
			if(settings.hasOverlayNormal){
				normalCurve = statGeo.createNormalCurveOverlay(dataListSelected);
				plotGeoList.add(normalCurve);
			}


			plotPanel.setPlotSettings(statGeo.getHistogramSettings( dataListSelected, histogram, settings));

			if(hasControlPanel)
				if(settings.useManualClasses)
					((CardLayout)controlCards.getLayout()).show(controlCards, "manualClassesPanel");	
				else
					((CardLayout)controlCards.getLayout()).show(controlCards, "numClassesPanel");	


			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;	

		case PLOT_BOXPLOT:
			if(doCreate)
				plotGeoList.add(statGeo.createBoxPlot( dataListSelected));
			plotPanel.setPlotSettings(statGeo.getBoxPlotSettings( dataListSelected));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;

		case PLOT_DOTPLOT:
			if(doCreate){
				if(dotPlot != null)
					dotPlot.remove();
				dotPlot = statGeo.createDotPlot( dataListSelected);
				plotGeoList.add(dotPlot);
			}
				plotPanel.setPlotSettings(statGeo.updateDotPlot(dataListSelected, dotPlot));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;

		case PLOT_STEMPLOT:
			String latex = statGeo.getStemPlotLatex( dataListSelected, settings.stemAdjust);
			imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, latex, app.getPlainFont(), true, Color.BLACK, null));
			optionsButton.setVisible(false);
			if(hasControlPanel)
				((CardLayout)controlCards.getLayout()).show(controlCards, "stemAdjustPanel");

			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "imagePanel");
			break;

		case PLOT_FREQUENCYTABLE:
			imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, underConstruction, app.getPlainFont(), true, Color.BLACK, null));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "imagePanel");
			optionsButton.setVisible(false);
			break;

		case PLOT_NORMALQUANTILE:
			if(doCreate){
				plotGeoList.add(statGeo.createNormalQuantilePlot( dataListSelected));
			}
				plotPanel.setPlotSettings(statGeo.updateNormalQuantilePlot(dataListSelected));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;


		case PLOT_ZTEST:
		case PLOT_TTEST:
		case PLOT_ZINT:
		case PLOT_TINT:
			oneVarInferencePanel.setSelectedPlot(selectedPlot);
			oneVarInferencePanel.updatePanel();
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "oneVarInferencePanel");
			optionsButton.setVisible(false);
			//	if(hasControlPanel)
			//((CardLayout)controlCards.getLayout()).show(controlCards, "twoSampleSelectionPanel");
			break;



		case PLOT_SCATTERPLOT:
			if(doCreate)
				plotGeoList.add(statGeo.createScatterPlot(dataListSelected));
			plotPanel.setPlotSettings(statGeo.getScatterPlotSettings(dataListSelected, settings));

			fldTitleX.setText(statDialog.getDataTitles()[0]);
			fldTitleY.setText(statDialog.getDataTitles()[1]);

			if(statDialog.getRegressionModel()!=null){
				plotGeoList.add(statDialog.getRegressionModel());  
			}
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");

			break;


		case PLOT_RESIDUAL:
			if(doCreate)
				plotGeoList.add(statGeo.createResidualPlot(dataListSelected, statDialog.getRegressionMode(), statDialog.getRegressionOrder()));
			if(statDialog.getRegressionMode() != statDialog.REG_NONE)
				plotPanel.setPlotSettings(statGeo.getResidualPlotSettings(dataListSelected, plotGeoList.get(plotGeoList.size()-1), settings));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;


		case PLOT_REGRESSION_INFERENCE:
			//	regressionAnalysisList = statGeo.createRegressionAnalysisList(dataListSelected, statDialog.getRegressionModel() );
			regressionPanel.updateRegressionPanel();
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "regressionPanel");

			break;

		case PLOT_MULTIBOXPLOT:
			if(doCreate){
				plotGeoList.add(statGeo.createMultipleBoxPlot( dataListSelected));
			}
			plotPanel.setPlotSettings(statGeo.getMultipleBoxPlotSettings( dataListSelected));
			if(boxPlotTitles != null)
				boxPlotTitles.remove();
			boxPlotTitles = statGeo.createBoxPlotTitles(statDialog, plotPanel.getPlotSettings());
			plotGeoList.add(boxPlotTitles);
			
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			optionsButton.setVisible(false);
			break;

		case PLOT_MULTIVARSTATS:
			multiVarStatPanel.updatePanel();
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "multiVarStatPanel");
			optionsButton.setVisible(false);
			break;

		case PLOT_ANOVA:
			//imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, underConstruction, app.getPlainFont(), true, Color.BLACK, null));
			//((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "imagePanel");

			anovaTable.updatePanel();
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "anovaPanel");

			optionsButton.setVisible(false);
			break;

			
		case PLOT_TTEST_2MEANS:
		case PLOT_TTEST_PAIRED:
		case PLOT_TINT_2MEANS:
		case PLOT_TINT_PAIRED:	
			twoVarInferencePanel.setSelectedPlot(selectedPlot);
			twoVarInferencePanel.updatePanel();
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "twoVarInferencePanel");
			optionsButton.setVisible(false);
			//	if(hasControlPanel)
			//((CardLayout)controlCards.getLayout()).show(controlCards, "twoSampleSelectionPanel");
			break;

		default:
			//System.out.println(plotMap.get(plotIndex));
		}


		if(doCreate){
			for(GeoElement listGeo:plotGeoList){
				// add the geo to our view and remove it from EV		
				listGeo.addView(plotPanel);
				plotPanel.add(listGeo);
				listGeo.removeView(app.getEuclidianView());
				app.getEuclidianView().remove(listGeo);
			}
		}
	}




	//============================================================
	//            Action Event Handlers
	//============================================================


	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if(source instanceof JTextField)
			doTextFieldActionPerformed(source);

		else if(source == minus || source == plus || source == none){
			minus.setSelected(source == minus);
			none.setSelected(source == none);
			plus.setSelected(source == plus);
			if(source == minus) settings.stemAdjust=-1;
			if(source == none) settings.stemAdjust=0;
			if(source == plus) settings.stemAdjust=1;
			updatePlot(true);
		}

		else if(source == optionsButton){
			/*
			optionsPanel.reInit(selectedPlot);
			OptionsDialog dlg = new OptionsDialog(new JFrame(), app.getMenu("Options"));
			
			
			Point p = optionsButton.getLocationOnScreen();
			Dimension dlgDim  = dlg.getPreferredSize();
			dlgDim.height = statDialog.getHeight() - 100;
			p.x = statDialog.getX() + statDialog.getWidth() - dlgDim.width + 20;
			p.y = p.y + optionsButton.getHeight() + 20;
			
			dlg.setLocation(p);
			dlg.setVisible(true);
			*/
			
			optionsPanel.reInit(selectedPlot);
			optionsPanel.setVisible(optionsButton.isSelected());
			
		}

		else if(source == cbDisplayType){
			if(cbDisplayType.getSelectedItem().equals(MyRenderer.SEPARATOR)){
				cbDisplayType.setSelectedItem(plotMap.get(selectedPlot));
			}
			else{
			selectedPlot = plotMapReverse.get(cbDisplayType.getSelectedItem());
			updatePlot(true);
			}
		}


	}

	private void doTextFieldActionPerformed(Object source){

		if(source == fldStart){
			double val = Double.parseDouble(fldStart.getText());
			settings.classStart = val;
		}
		else if(source == fldWidth){
			double val = Double.parseDouble(fldWidth.getText());
			settings.classWidth = val;
		}
		updatePlot(true);
	}



	//============================================================
	//            Other Event Handlers
	//============================================================

	public void clearPlotGeoList(){

		for(GeoElement geo : plotGeoList){
			if(geo != null){
				geo.remove();
				geo = null;
			}
		}
		plotGeoList.clear();
	}

	public void removeGeos(){
		
		clearPlotGeoList();
	}


	public void detachView(){
		//plotPanel.detachView();

	}

	public void updateFonts(){
			
	}
	
	public void attachView() {
		plotPanel.attachView();

	}



	//============================================================
	//            Utilities
	//============================================================

	private JPanel flowPanel(JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		//	p.setBackground(Color.white);
		return p;
	}

	private JPanel flowPanelRight(JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		//	p.setBackground(Color.white);
		return p;
	}


	private JPanel boxXPanel(JComponent... comp){
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		//	p.setBackground(Color.white);
		return p;
	}



	public void updateFonts(Font font) {
		// TODO Auto-generated method stub
		
	}

	public void updatePanel() {
		// TODO Auto-generated method stub
		
	}


	

	public class OptionsDialog extends JDialog implements ActionListener {
		public OptionsDialog(JFrame parent, String title) {
			super(parent, title, true);
			optionsPanel.setVisible(true);
			getContentPane().add(optionsPanel, BorderLayout.CENTER);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			pack(); 
		}
		public void actionPerformed(ActionEvent e) {
			setVisible(false); 
			dispose(); 
		}

	}


	//============================================================
	//           ComboBox Renderer with SEPARATOR
	//============================================================
	
	class MyRenderer extends JLabel implements ListCellRenderer {
		
		public static final String SEPARATOR = "SEPARATOR";
	    JSeparator separator;

	    public MyRenderer() {
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


