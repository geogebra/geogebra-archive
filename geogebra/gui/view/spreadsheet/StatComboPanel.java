package geogebra.gui.view.spreadsheet;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



//=====================================================
// ComboStatPanel class
//=====================================================


public class StatComboPanel extends JPanel{

	// one variable plot types
	public static final int PLOT_HISTOGRAM = 0;
	public static final int PLOT_BOXPLOT = 1;
	public static final int PLOT_DOTPLOT = 2;
	private static final int PLOT_NORMALQUANTILE = 4;
	private static final int PLOT_FREQUENCYTABLE = 5;
	private static final int PLOT_STEMPLOT = 6;

	// two variable plot types
	public static final int PLOT_SCATTERPLOT = 7;
	public static final int PLOT_RESIDUAL = 8;
	public static final int PLOT_REGRESSION_ANALYSIS= 9;

	// multi variable plot types
	public static final int PLOT_MULTIBOXPLOT = 10;
	public static final int PLOT_MULTIVARSTATS = 11;
	public static final int PLOT_ANOVA= 12;
	public static final int PLOT_TWOVAR_INFERENCE= 13;


	// plot reference vars
	private HashMap<Integer, String> plotMap;
	private HashMap<String, Integer> plotMapReverse;
	private int plotIndex;


	private int numClasses = 6;
	private JPanel numClassesPanel;
	private JSlider sliderNumClasses; 

	private JComboBox cbPlotTypes;
	private JPanel statDisplayPanel;
	private TwoVarInferencePanel twoVarInferencePanel;
	private PlotPanel plotPanel;
	private StatTable statTable;
	private JLabel lblRegCmd;

	private Application app;
	private StatDialog statDialog;
	private StatGeo statGeo;
	private GeoList dataListSelected;
	private GeoList regressionAnalysisList;

	private StatTable regressionAnalysisTable;

	private int mode;

	private ArrayList<GeoElement> plotGeoList;
	private GeoElement plotTitleX, plotTitleY;
	private PopupMenuButton optionsButton;
	private HistogramOptionsPanel histogramOptionsPanel;

	private JLabel imageContainer;
	private int stemPlotAdjustment = 0;
	private JPanel controlCards;
	private JPanel controlPanel;

	private boolean hasControlPanel;
	private JPanel regressionPanel;
	private JToolBar manualClassesPanel;
	private JLabel lblStart;
	private JLabel lblWidth;
	private MyTextField fldStart;
	private MyTextField fldWidth;
	private JLabel lblNumClasses;


	private StatPanelSettings settings;
	private JLabel lblEvaluate;
	private MyTextField fldInputX;
	private JLabel lblOutputY;
	private JToolBar stemAdjustPanel;
	private JLabel lblAdjust;
	private JButton minus;
	private JButton none;
	private JButton plus;
	private MultiVarStatPanel multiVarStatPanel;


	private DefaultComboBoxModel modelInference;
	private JComboBox cbInferenceType;
	private JPanel twoVarOptionsPanel;
	private ANOVAPanel anovaPanel;






	/***************************************** 
	 * Construct a ComboStatPanel
	 */
	public  StatComboPanel( StatDialog statDialog, int defaultPlotIndex, GeoList dataListSelected, int mode, boolean hasControlPanel){

		this.dataListSelected = dataListSelected;
		this.statDialog = statDialog;
		this.app = statDialog.getApp();
		this.mode = mode;
		this.statGeo = statDialog.getStatGeo();
		this.plotIndex = defaultPlotIndex;
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

		// ======================================
		// Create the control panel
		// This holds a comboBox for choosing plots
		// and other plot-dependent controls

		if(hasControlPanel){
			createNumClassesPanel();
			createManualClassesPanel();
			createPlotTypeComboBox();
			createStemPlotAdjustmentPanel();
			createTwoVarOptionsPanel();
			// put the bar control panels in a card layout
			JPanel blank = new JPanel(new BorderLayout());
			blank.add(new JLabel("  "));
			controlCards = new JPanel(new CardLayout());
			controlCards.add("numClassesPanel", numClassesPanel);
			controlCards.add("manualClassesPanel", manualClassesPanel);
			controlCards.add("stemAdjustPanel", stemAdjustPanel);
			//	controlCards.add("twoVarOptionsPanel", twoVarOptionsPanel);
			controlCards.add("blankPanel", blank);

			JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			plotTypePanel.add(cbPlotTypes);
			//plotTypePanel.add(controlCards);

			JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			createOptionsButton();
			optionsPanel.add(optionsButton);
			controlPanel = new JPanel(new BorderLayout());
			controlPanel.add(plotTypePanel,BorderLayout.WEST);
			controlPanel.add(controlCards,BorderLayout.CENTER);
			controlPanel.add(optionsPanel,BorderLayout.EAST);
		}


		// =======================================
		// create a plot panel 
		plotPanel = new PlotPanel(app.getKernel());
		plotPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));


		// =======================================
		//create regression analysis panel
		if(mode == statDialog.MODE_REGRESSION)
			createRegressionAnalysisPanel();

		// =======================================
		//create image panel
		JPanel imagePanel = new JPanel(new BorderLayout());
		imagePanel.setBorder(BorderFactory.createEmptyBorder());
		imagePanel.setBackground(Color.WHITE);
		imageContainer = new JLabel();
		imagePanel.setAlignmentX(SwingConstants.CENTER);
		imagePanel.setAlignmentY(SwingConstants.CENTER);
		imageContainer.setHorizontalAlignment(SwingConstants.CENTER);
		imagePanel.add(imageContainer, BorderLayout.CENTER);



		// =======================================
		// put these panels in a card layout
		
		statDisplayPanel = new JPanel(new CardLayout());
		
		statDisplayPanel.add("plotPanel", plotPanel);
		statDisplayPanel.add("imagePanel", new JScrollPane(imagePanel));
		
		if(mode == statDialog.MODE_REGRESSION)

			statDisplayPanel.add("regressionPanel", regressionPanel);
		

		if(mode == statDialog.MODE_MULTIVAR){

			twoVarInferencePanel =new TwoVarInferencePanel(app, dataListSelected, statDialog);
			JScrollPane scroller = new JScrollPane(twoVarInferencePanel);
			statDisplayPanel.add("twoVarInferencePanel", scroller);

			multiVarStatPanel = new MultiVarStatPanel(app, dataListSelected, statDialog);
			statDisplayPanel.add("multiVarStatPanel", multiVarStatPanel);

			anovaPanel = new ANOVAPanel(app, dataListSelected, statDialog);
			statDisplayPanel.add("anovaPanel", anovaPanel);
		}
		statDisplayPanel.setBackground(plotPanel.getBackground());



		// =======================================
		// put it all together

		this.setLayout(new BorderLayout());
		if(hasControlPanel){
			this.add(controlPanel,BorderLayout.NORTH);
		}
		this.add(statDisplayPanel,BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

	}



	private void createRegressionAnalysisPanel(){

		//	lblRegCmd = new JLabel("label");

		lblEvaluate = new JLabel();	
		fldInputX = new MyTextField(app.getGuiManager());
		fldInputX.setColumns(4);
		lblOutputY = new JLabel();

		JPanel evalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		evalPanel.add(lblEvaluate);
		evalPanel.add(new JLabel("x = "));
		evalPanel.add(fldInputX);
		evalPanel.add(new JLabel("y = "));
		evalPanel.add(lblOutputY);

		//regressionAnalysisList = statDialog.getStatGeo().createRegressionAnalysisList(dataListSelected, null);
		regressionAnalysisList = statDialog.getStatGeo().createBasicStatList(dataListSelected,mode);		
		regressionAnalysisTable = new StatTable(app, regressionAnalysisList, mode);

		regressionPanel = new JPanel(new BorderLayout());
		regressionPanel.add(evalPanel, BorderLayout.NORTH);
		regressionPanel.add(regressionAnalysisTable, BorderLayout.CENTER);
		regressionPanel.setBorder(BorderFactory.createEmptyBorder());

	}

	
	private void createPlotTypeComboBox(){

		cbPlotTypes = new JComboBox();

		switch(mode){

		case StatDialog.MODE_ONEVAR:
			cbPlotTypes.addItem(plotMap.get(PLOT_HISTOGRAM));
			cbPlotTypes.addItem(plotMap.get(PLOT_BOXPLOT));
			cbPlotTypes.addItem(plotMap.get(PLOT_DOTPLOT));
			cbPlotTypes.addItem(plotMap.get(PLOT_STEMPLOT));
			cbPlotTypes.addItem(plotMap.get(PLOT_FREQUENCYTABLE));
			cbPlotTypes.addItem(plotMap.get(PLOT_NORMALQUANTILE));

			break;

		case StatDialog.MODE_REGRESSION:
			cbPlotTypes.addItem(plotMap.get(PLOT_SCATTERPLOT));
			cbPlotTypes.addItem(plotMap.get(PLOT_RESIDUAL));
			cbPlotTypes.addItem(plotMap.get(PLOT_REGRESSION_ANALYSIS));
			break;

		case StatDialog.MODE_MULTIVAR:
			cbPlotTypes.addItem(plotMap.get(PLOT_MULTIBOXPLOT));
			cbPlotTypes.addItem(plotMap.get(PLOT_MULTIVARSTATS));
			cbPlotTypes.addItem(plotMap.get(PLOT_ANOVA));
			cbPlotTypes.addItem(plotMap.get(PLOT_TWOVAR_INFERENCE));
			break;


		}

		cbPlotTypes.setSelectedItem(plotMap.get(plotIndex));
		cbPlotTypes.addActionListener(new ActionListener() {       
			public void actionPerformed(ActionEvent e)
			{
				plotIndex = plotMapReverse.get(cbPlotTypes.getSelectedItem());
				updatePlot(true);
				//btnClose.requestFocus();
			}
		});      

	}


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


	private void setLabels(){

		lblNumClasses.setText(app.getMenu("Classes" + ": "));
		lblStart.setText(app.getMenu("Start") + ": ");
		lblWidth.setText(app.getMenu("Width") + ": ");
		if(mode == statDialog.MODE_REGRESSION){
			lblEvaluate.setText(app.getMenu("Evaluate")+ ": ");
		}
		lblAdjust.setText(app.getMenu("Adjustment")+ ": ");


		modelInference.removeAllElements();
		modelInference.addElement(app.getMenu("Difference of Means T Test"));	
		modelInference.addElement(app.getMenu("Difference of Means T Estimate"));
		modelInference.addElement(app.getMenu("Paired T Test"));


	}



	private void createStemPlotAdjustmentPanel(){

		StemAdjustListener listener = new StemAdjustListener();
		lblAdjust = new JLabel();
		minus = new JButton("-1");
		none = new JButton("0");
		plus = new JButton("+1");
		minus.addActionListener(listener);
		none.addActionListener(listener);
		plus.addActionListener(listener);
		none.setSelected(true);
		stemAdjustPanel = new JToolBar();
		stemAdjustPanel.setFloatable(false);
		stemAdjustPanel.add(minus);
		stemAdjustPanel.add(none);
		stemAdjustPanel.add(plus);

	}

	class StemAdjustListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			minus.setSelected(source == minus);
			none.setSelected(source == none);
			plus.setSelected(source == plus);
			if(source == minus) settings.stemAdjust=-1;
			if(source == none) settings.stemAdjust=0;
			if(source == plus) settings.stemAdjust=1;
			updatePlot(true);
		}


	}



	private void createManualClassesPanel(){

		lblStart = new JLabel();
		lblWidth = new JLabel();
		fldStart = new MyTextField(app.getGuiManager());
		Dimension d = fldStart.getMaximumSize();
		d.height = fldStart.getPreferredSize().height;
		fldStart.setMaximumSize(d);
		fldWidth = new MyTextField(app.getGuiManager());
		fldWidth.setMaximumSize(d);
		fldStart.setColumns(4);
		fldWidth.setColumns(4);

		manualClassesPanel = new JToolBar();
		manualClassesPanel.setFloatable(false);
		manualClassesPanel.add(lblStart);
		manualClassesPanel.add(fldStart);
		manualClassesPanel.add(lblWidth);
		manualClassesPanel.add(fldWidth);

	}

	private void createTwoVarOptionsPanel(){

		modelInference = new DefaultComboBoxModel();
		cbInferenceType = new JComboBox(modelInference);
		twoVarOptionsPanel = flowPanel(cbInferenceType);

	}

	private JPanel flowPanel(JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		p.setBackground(Color.white);
		return p;
	}


	private void createOptionsButton(){

		// create options drop down button
		settings = new StatPanelSettings();
		histogramOptionsPanel= new HistogramOptionsPanel(app, settings);

		optionsButton = new PopupMenuButton();
		optionsButton.setKeepVisible(true);
		optionsButton.setStandardButton(true);
		optionsButton.addPopupMenuItem(histogramOptionsPanel);

		ImageIcon ptCaptureIcon = app.getImageIcon("tool.png");
		optionsButton.setIconSize(new Dimension(ptCaptureIcon.getIconWidth(),18));
		optionsButton.setIcon(ptCaptureIcon);


		histogramOptionsPanel.addPropertyChangeListener("settings", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updatePlot(true);
				//System.out.println(evt.getPropertyName() + "----------------" );
			}
		});

	}




	private void createPlotMap(){
		plotMap = new HashMap<Integer,String>();

		plotMap.put(PLOT_HISTOGRAM, app.getMenu("Histogram"));
		plotMap.put(PLOT_BOXPLOT, app.getMenu("Boxplot"));
		plotMap.put(PLOT_DOTPLOT, app.getMenu("DotPlot"));
		plotMap.put(PLOT_NORMALQUANTILE, app.getMenu("NormalQuantilePlot"));
		plotMap.put(PLOT_FREQUENCYTABLE, app.getMenu("FrequencyTable"));
		plotMap.put(PLOT_STEMPLOT, app.getMenu("StemPlot"));

		plotMap.put(PLOT_SCATTERPLOT, app.getMenu("Scatterplot"));
		plotMap.put(PLOT_RESIDUAL, app.getMenu("ResidualPlot"));
		plotMap.put(PLOT_REGRESSION_ANALYSIS, app.getMenu("RegressionAnalysis"));

		plotMap.put(PLOT_MULTIBOXPLOT, app.getMenu("StackedBoxPlots"));
		plotMap.put(PLOT_MULTIVARSTATS, app.getMenu("Statistics"));
		plotMap.put(PLOT_ANOVA, app.getMenu("ANOVA"));
		plotMap.put(PLOT_TWOVAR_INFERENCE, app.getMenu("TwoVariableInference"));

		plotMapReverse = new HashMap<String, Integer>();
		for(Integer key: plotMap.keySet()){
			plotMapReverse.put(plotMap.get(key), key);
		}

	}


	//==============================================
	//              UPDATE
	//==============================================

	public void updatePlot(boolean doCreate){

		GeoElement geo;
		String underConstruction = "not\\;available";
		if(hasControlPanel)
			((CardLayout)controlCards.getLayout()).show(controlCards, "blankPanel");	

		if(doCreate)
			clearPlotGeoList();

		optionsButton.setVisible(true);

		switch(plotIndex){

		case PLOT_HISTOGRAM:			
			if(doCreate)
				plotGeoList.add(statGeo.createHistogram( dataListSelected, numClasses));
			plotPanel.setPlotSettings(statGeo.updateHistogram( dataListSelected, plotGeoList.get(plotGeoList.size()-1)));

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
			plotPanel.setPlotSettings(statGeo.updateBoxPlot( dataListSelected));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;

		case PLOT_DOTPLOT:
			if(doCreate)
				plotGeoList.add(statGeo.createDotPlot( dataListSelected));
			plotPanel.setPlotSettings(statGeo.updateDotPlot(dataListSelected, plotGeoList.get(plotGeoList.size()-1)));
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
			imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, underConstruction, app.getPlainFont(), true, Color.BLACK, null));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "imagePanel");
			optionsButton.setVisible(false);
			break;


		case PLOT_SCATTERPLOT:
			if(doCreate)
				plotGeoList.add(statGeo.createScatterPlot(dataListSelected));
			plotPanel.setPlotSettings(statGeo.updateScatterPlot(dataListSelected));
			if(statDialog.getRegressionModel()!=null){
				if(doCreate)
					plotGeoList.add(statGeo.createRegressionPlot(dataListSelected, statDialog.getRegressionMode(), statDialog.getRegressionOrder()));

			}
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;


		case PLOT_RESIDUAL:
			if(doCreate)
				plotGeoList.add(statGeo.createResidualPlot(dataListSelected, statDialog.getRegressionMode(), statDialog.getRegressionOrder()));
			plotPanel.setPlotSettings(statGeo.updateScatterPlot(dataListSelected));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;


		case PLOT_REGRESSION_ANALYSIS:
			regressionAnalysisList = statGeo.createRegressionAnalysisList(dataListSelected, statDialog.getRegressionModel() );
			regressionAnalysisTable.updateData(regressionAnalysisList);
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "regressionPanel");

			break;

		case PLOT_MULTIBOXPLOT:
			if(doCreate)
				plotGeoList.add(statGeo.createMultipleBoxPlot( dataListSelected));
			plotPanel.setPlotSettings(statGeo.updateMultipleBoxPlot( dataListSelected));
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			optionsButton.setVisible(false);
			break;

		case PLOT_MULTIVARSTATS:
			multiVarStatPanel.updateMultiVarStatPanel();
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "multiVarStatPanel");
			optionsButton.setVisible(false);
			break;

		case PLOT_ANOVA:
			//imageContainer.setIcon(GeoGebraIcon.createLatexIcon(app, underConstruction, app.getPlainFont(), true, Color.BLACK, null));
			//((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "imagePanel");

			anovaPanel.updateANOVAPanel();
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "anovaPanel");
			
			optionsButton.setVisible(false);
			break;

		case PLOT_TWOVAR_INFERENCE:
			twoVarInferencePanel.updateTwoVarPanel();
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "twoVarInferencePanel");
			optionsButton.setVisible(false);
			//if(hasControlPanel)
			//((CardLayout)controlCards.getLayout()).show(controlCards, "twoVarOptionsPanel");
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




	public void clearPlotGeoList(){

		for(GeoElement geo : plotGeoList){
			if(geo != null)
				geo.remove();
		}
		plotGeoList.clear();
	}

	public void removeGeos(){
		//statPanel.removeGeos();

		if(dataListSelected != null)
			dataListSelected.remove();

		clearPlotGeoList();
	}


	public void detachView(){
		//plotPanel.detachView();

	}

	public void updateData(GeoList dataList){
		dataListSelected = dataList;
	}

	public void updateStatTableFonts(Font font){
		//statPanel.updateFonts(font);
	}

	public void attachView() {
		plotPanel.attachView();

	}



} 


