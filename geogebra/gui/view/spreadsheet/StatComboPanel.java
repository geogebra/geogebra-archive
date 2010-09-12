package geogebra.gui.view.spreadsheet;

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
import java.util.HashMap;

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
	public static final int PLOT_STATISTICS_ONEVAR = 4;
	
	
	// two variable plot types
	public static final int PLOT_SCATTERPLOT = 5;
	public static final int PLOT_RESIDUAL = 6;
	public static final int PLOT_STATISTICS_TWOVAR= 7;
	
	
	// plot reference vars
	private HashMap<Integer, String> plotMap;
	private HashMap<String, Integer> plotMapReverse;
	private int plotIndex;
	
	
	private int numClasses = 6;
	private JPanel numClassesPanel;
	private JSlider sliderNumClasses; 

	private JComboBox cbPlotTypes;
	private JPanel statDisplayPanel;
	private StatPlotPanel plotPanel;
	private StatTablePanel statPanel;

	private Application app;
	private StatDialog statDialog;
	private GeoList dataListSelected;
	
	private int mode;

	
	
	/***************************************** 
	 * Construct a ComboStatPanel
	 */
	public  StatComboPanel( StatDialog statDialog, int defaultPlotIndex, GeoList dataListSelected, int mode){
		
		this.dataListSelected = dataListSelected;
		this.statDialog = statDialog;
		this.app = statDialog.getApp();
		this.mode = mode;
		
		this.plotIndex = defaultPlotIndex;
		createPlotMap();
			
		createGUI();
		updatePlot(true);

	}
	
	
	private void createGUI(){
		
		// ======================================
		// create the control panel  
		// This holds the plot chooser combo box 
		// and other plot-dependent controls
		
		createNumClassesPanel();
		createPlotTypeComboBox();
		
		JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		plotTypePanel.add(cbPlotTypes);
		plotTypePanel.add(numClassesPanel);
	
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(plotTypePanel,BorderLayout.WEST);
	
		
		
		// =======================================
		// create a plot panel and a stat table panel,
		// then put them in a card layout
		
		plotPanel = new StatPlotPanel(app);
		plotPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

		//statPanel = new StatTablePanel(app, dataListSelected, mode);
		//statPanel.setBorder(plotPanel.getBorder());

		statDisplayPanel = new JPanel(new CardLayout());
		statDisplayPanel.add("plotPanel", plotPanel);
		//statDisplayPanel.add("statPanel", statPanel);
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
		//	cbPlotTypes.addItem(plotMap.get(PLOT_STATISTICS_ONEVAR));
			break;

		case StatDialog.MODE_TWOVAR:
			cbPlotTypes.addItem(plotMap.get(PLOT_SCATTERPLOT));
			cbPlotTypes.addItem(plotMap.get(PLOT_RESIDUAL));
		//	cbPlotTypes.addItem(plotMap.get(PLOT_STATISTICS_TWOVAR));
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

		sliderNumClasses = new JSlider(JSlider.HORIZONTAL, 2, 20, numClasses);
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
		plotMap.put(PLOT_STATISTICS_ONEVAR, app.getMenu("Statistics"));
		plotMap.put(PLOT_HISTOGRAM, app.getMenu("Histogram"));
		
		plotMap.put(PLOT_SCATTERPLOT, app.getMenu("Scatterplot"));
		plotMap.put(PLOT_RESIDUAL, app.getMenu("ResidualPlot"));
		plotMap.put(PLOT_STATISTICS_TWOVAR, app.getMenu("ResidualPlot"));
			
		plotMapReverse = new HashMap<String, Integer>();
		for(Integer key: plotMap.keySet()){
			plotMapReverse.put(plotMap.get(key), key);
		}
		
	}
	
	
	
	

	public void removeGeos(){
		//statPanel.removeGeos();
		
		if(dataListSelected != null)
			dataListSelected.remove();

		plotPanel.removeGeos();
	}
	
	public void detachView(){
		plotPanel.detachView();
		
	}

	public void updateData(GeoList dataList){
		dataListSelected = dataList;
	}
	
	
	public void updatePlot(boolean doCreate){
		numClassesPanel.setVisible(false);
		switch(plotIndex){
		case PLOT_HISTOGRAM:
			plotPanel.updateHistogram( dataListSelected, numClasses, doCreate);
			numClassesPanel.setVisible(true);
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;	
		case PLOT_BOXPLOT:
			plotPanel.updateBoxPlot( dataListSelected, doCreate);
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;
		case PLOT_DOTPLOT:
			plotPanel.updateDotPlot( dataListSelected, doCreate);
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;
		case PLOT_STATISTICS_ONEVAR:
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "statPanel");
			//statPanel.updateTable();
			statPanel.updateData(dataListSelected);
			break;
			
		case PLOT_SCATTERPLOT:

			plotPanel.updateScatterPlot( dataListSelected, doCreate);
			plotPanel.setAutoRemoveGeos(false);
			plotPanel.updateRegressionPlot(dataListSelected, doCreate, statDialog.getRegressionMode(), statDialog.getRegressionOrder());
			statDialog.setRegEquation(plotPanel.getRegEquation());
			plotPanel.setAutoRemoveGeos(true);
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;
			
		case PLOT_STATISTICS_TWOVAR:
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "statPanel");
			//statPanel.updateTable();
			statPanel.updateData(dataListSelected);
			break;
			
			default:
				//System.out.println(plotMap.get(plotIndex));
		}

	}


	public void updateStatTableFonts(Font font){
		//statPanel.updateFonts(font);
	}

	public void attachView() {
		plotPanel.attachView();
		
	}

	
	
	
	
}
	// END class ComboStatPanel 
	//========================================================


