package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianController;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
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
	private static final int PLOT_NORMALQUANTILE = 3;
	
	// two variable plot types
	public static final int PLOT_SCATTERPLOT = 5;
	public static final int PLOT_RESIDUAL = 6;
	public static final int PLOT_REGRESSION_ANALYSIS= 7;
	
	// plot reference vars
	private HashMap<Integer, String> plotMap;
	private HashMap<String, Integer> plotMapReverse;
	private int plotIndex;
	
	
	private int numClasses = 6;
	private JPanel numClassesPanel;
	private JSlider sliderNumClasses; 

	private JComboBox cbPlotTypes;
	private JPanel statDisplayPanel;
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
	
	
	/***************************************** 
	 * Construct a ComboStatPanel
	 */
	public  StatComboPanel( StatDialog statDialog, int defaultPlotIndex, GeoList dataListSelected, int mode){
		
		this.dataListSelected = dataListSelected;
		this.statDialog = statDialog;
		this.app = statDialog.getApp();
		this.mode = mode;
		this.statGeo = statDialog.getStatGeo();
		this.plotIndex = defaultPlotIndex;
		
		plotGeoList = new ArrayList<GeoElement>();
		
		createPlotMap();
			
		createGUI();
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
		
		createNumClassesPanel();
		createPlotTypeComboBox();
		
		JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		plotTypePanel.add(cbPlotTypes);
		plotTypePanel.add(numClassesPanel);
	
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(plotTypePanel,BorderLayout.WEST);
	
		
		
		// =======================================
		// create a plot panel 
		plotPanel = new PlotPanel(app.getKernel());
		plotPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	
		
		// =======================================
		//create regression analysis panel
		lblRegCmd = new JLabel("label");
		//regressionAnalysisList = statDialog.getStatGeo().createRegressionAnalysisList(dataListSelected, null);
		regressionAnalysisList = statDialog.getStatGeo().createBasicStatList(dataListSelected,mode);		
		regressionAnalysisTable = new StatTable(app, regressionAnalysisList, mode);
		JPanel regressionPanel = new JPanel(new BorderLayout());
		regressionPanel.add(lblRegCmd, BorderLayout.NORTH);
		regressionPanel.add(regressionAnalysisTable, BorderLayout.CENTER);
		regressionPanel.setBorder(BorderFactory.createEmptyBorder());

		
		// =======================================
		// put these panels in a card layout
		statDisplayPanel = new JPanel(new CardLayout());
		statDisplayPanel.add("plotPanel", plotPanel);
		statDisplayPanel.add("regressionPanel", regressionPanel);
		statDisplayPanel.setBackground(plotPanel.getBackground());

		

		// =======================================
		// put it all together
		
		this.setLayout(new BorderLayout());
		this.add(controlPanel,BorderLayout.NORTH);
		this.add(statDisplayPanel,BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
	}
	
	
	
	private void createPlotTypeComboBox(){

		cbPlotTypes = new JComboBox();

		switch(mode){

		case StatDialog.MODE_ONEVAR:
			cbPlotTypes.addItem(plotMap.get(PLOT_HISTOGRAM));
			cbPlotTypes.addItem(plotMap.get(PLOT_BOXPLOT));
			cbPlotTypes.addItem(plotMap.get(PLOT_DOTPLOT));
			break;

		case StatDialog.MODE_TWOVAR:
			cbPlotTypes.addItem(plotMap.get(PLOT_SCATTERPLOT));
			cbPlotTypes.addItem(plotMap.get(PLOT_RESIDUAL));
			cbPlotTypes.addItem(plotMap.get(PLOT_REGRESSION_ANALYSIS));
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
		
		final JTextField lblNumClasses = new JTextField(""+numClasses);
		lblNumClasses.setEditable(false);
		lblNumClasses.setOpaque(true);
		lblNumClasses.setColumns(2);
		lblNumClasses.setHorizontalAlignment(JTextField.CENTER);
		lblNumClasses.setBackground(Color.WHITE);

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
				lblNumClasses.setText(("" + numClasses));
				updatePlot(true);
				//btnClose.requestFocus();
			}
		});


		numClassesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		numClassesPanel.add(sliderNumClasses);
		numClassesPanel.add(new JLabel(app.getMenu("Classes" + ": ")));
		numClassesPanel.add(lblNumClasses);
	 
	}
	

	
	private void createPlotMap(){
		plotMap = new HashMap<Integer,String>();
		
		plotMap.put(PLOT_HISTOGRAM, app.getMenu("Histogram"));
		plotMap.put(PLOT_BOXPLOT, app.getMenu("Boxplot"));
		plotMap.put(PLOT_DOTPLOT, app.getMenu("DotPlot"));
		
		plotMap.put(PLOT_SCATTERPLOT, app.getMenu("Scatterplot"));
		plotMap.put(PLOT_RESIDUAL, app.getMenu("ResidualPlot"));
		plotMap.put(PLOT_REGRESSION_ANALYSIS, app.getMenu("Regression Analysis"));
			
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
		numClassesPanel.setVisible(false);
		
		if(doCreate)
			clearPlotGeoList();
		
		
		switch(plotIndex){

		case PLOT_HISTOGRAM:			
			if(doCreate)
				plotGeoList.add(statGeo.createHistogram( dataListSelected, numClasses));
			plotPanel.setPlotSettings(statGeo.updateHistogram( dataListSelected, plotGeoList.get(plotGeoList.size()-1)));
			numClassesPanel.setVisible(true);
			
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

	
	
	
	
} // END class ComboStatPanel 


