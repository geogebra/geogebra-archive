package geogebra.gui.view.spreadsheet;


import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OneVariableStatsDialog extends JDialog 
implements ActionListener, View   {
	 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	private SpreadsheetView spView;
	private MyTable spreadsheetTable;
	private OneVariableStatsDialog statDialog;
	
	private GeoList dataListAll, dataListSelected;
	private ArrayList<Integer> selectedColumns;
	ArrayList<String> dataTitles ;
	
	
	private JButton btnClose, btnOptions, btnExport, btnDisplay;
	private JCheckBox cbShowData;
	private ComboStatPanel comboStatPanel, comboStatPanel2;;
	private DataPanel dataPanel;
	private JSplitPane displayPanel;
	private JPanel cardPanel;
	
	private boolean showDataPanel = false;
	private boolean isIniting;
	
	public static final Color TABLE_GRID_COLOR = Color.GRAY;
	public static final Color TABLE_HEADER_COLOR = new Color(240,240,240);   
	
	
	
	/*************************************************
	 * Construct the dialog
	 */
	public OneVariableStatsDialog(SpreadsheetView spView, Application app){
		super(app.getFrame(),false);
		
		isIniting = true;
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		this.spView = spView;
		this.spreadsheetTable = spView.getTable();
		statDialog = this;
		
		
		//load data from currently selected columns
		selectedColumns = spreadsheetTable.getSelectedColumnsList();		
		loadDataLists();
		
		
		// create panels with the default plots
		comboStatPanel = new ComboStatPanel(ComboStatPanel.PLOT_HISTOGRAM);
		comboStatPanel2 = new ComboStatPanel(ComboStatPanel.PLOT_STATISTICS);
		
		dataPanel = new DataPanel(app, this, dataListAll);
		dataPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		// dataPanel.ensureTableFill();
		
		// init the GUI
		initGUI();
		updateFonts();
		btnClose.requestFocus();
		
		
		// attach this view to the kernel
		attachView();
		isIniting = false;
		
	} //END  constructor
	
	public void removeGeos(){
		dataListAll.remove();
		dataListAll = null;
		dataListSelected.remove();
		dataListSelected = null;
		comboStatPanel.removeGeos();
		comboStatPanel2.removeGeos();
	}
	
	
	
	//=================================================
	//       Load Data
	//=================================================
	
	
	private void loadDataLists(){
				
		GeoList tempGeo = (GeoList) spreadsheetTable.getCellRangeProcessor()
				.createListFromColumn(selectedColumns.get(0), true, false, true, false, GeoElement.GEO_CLASS_NUMERIC);
		String text = tempGeo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
		tempGeo.remove();
		

		if(dataListAll == null){
			dataListAll = new GeoList(cons);
			dataListAll.setAuxiliaryObject(true);
			//dataListAll.setLabel("dataListAll");
			dataListAll.setLabel(null);
		}
		
		if(dataListSelected == null){
			dataListSelected = new GeoList(cons);
			dataListSelected.setAuxiliaryObject(true);
			//dataListSelected.setLabel("dataListSelected");
			dataListSelected.setLabel(null);
		}

		try {
			dataListAll = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)dataListAll, text, true, false);

			dataListSelected = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)dataListSelected, text, true,false);		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		
		// selection has changed, so the spreadsheet columns need to be re-selected
		resetSpreadsheetSelection();

	}

	public void updateDataSelection(Boolean[] selectionList) {

		StringBuilder sb = new StringBuilder();
		sb.append("Sort[{");
		for(int i=0; i < dataListAll.size(); ++i){
			if(selectionList[i] == true){
				sb.append(dataListAll.get(i).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false));
				sb.append(",");
			}
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append("}]");

		try {
			dataListSelected = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)dataListSelected, sb.toString(), true,false);
		} catch (Exception e) {
			e.printStackTrace();
		}	

		//Application.debug(dataListSelected.toDefinedValueString());
		updatePlots();
		
	}


	public String getDataTitle(int index){
		
		String title = "";
		int column = this.selectedColumns.get(index);
		
		GeoElement geo = RelativeCopy.getValue(spreadsheetTable, column, 0);
		if(geo != null && geo.isGeoText())
			title = geo.toDefinedValueString();
		else
			title = app.getCommand("Column") + " " + GeoElement.getSpreadsheetColumnName(column);
		
		return title;

	}
	
	
	public void resetSpreadsheetSelection(){
		spreadsheetTable.setSelection(selectedColumns.get(0), 0, selectedColumns.get(0), 0);
		
	}
	
	
	//=================================================
	//       Create GUI
	//=================================================
	
	
	private void initGUI() {

		try {
			setTitle(app.getPlain("One Variable Statistics"));	
			
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
			
			btnDisplay = new JButton(app.getPlain("Plots"));
			btnDisplay.addActionListener(this);
			
			JPanel centerButtonPanel = new JPanel(new FlowLayout());
			centerButtonPanel.add(btnOptions);
			centerButtonPanel.add(btnDisplay);
			centerButtonPanel.add(btnExport);
			
			
			cbShowData = new JCheckBox(app.getPlain("Show Data"));
			cbShowData.setSelected(showDataPanel);
			cbShowData.addActionListener(this);
			JPanel leftButtonPanel = new JPanel(new FlowLayout());
			leftButtonPanel.add(cbShowData);
			
			JPanel buttonPanel = new JPanel(new BorderLayout());
			buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
		//	buttonPanel.add(centerButtonPanel, BorderLayout.CENTER);
			buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
			// END button panel
			
			
			//===========================================
			// card panels: options, export and display
			
			final JSplitPane comboPanelSplit = new JSplitPane(
					JSplitPane.VERTICAL_SPLIT, comboStatPanel, comboStatPanel2);
			// this.setPreferredSize(new Dimension(400,300));
			displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					dataPanel, comboPanelSplit);
			
			JPanel exportPanel = new JPanel();
			exportPanel.add(new JLabel("export panel"));
			
			JPanel optionsPanel = new JPanel();
			optionsPanel.add(new JLabel("options panel"));
			
			
			cardPanel = new JPanel(new CardLayout());
			cardPanel.add("displayPanel", displayPanel);
			cardPanel.add("optionsPanel", optionsPanel);
			cardPanel.add("exportPanel", exportPanel);
			
			
			//============================================
			// main panel
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(cardPanel, BorderLayout.CENTER);
			mainPanel.add(buttonPanel, BorderLayout.SOUTH);
			((CardLayout)cardPanel.getLayout()).show(cardPanel, "displayPanel");
			
			
			this.getContentPane().add(mainPanel);
			this.getContentPane().setPreferredSize(new Dimension(500,500));
			//setResizable(false);
			pack();
			
			comboPanelSplit.setDividerLocation(0.65);
			setShowDataPanel(showDataPanel);
			
			setLocationRelativeTo(app.getFrame());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private void setShowDataPanel(boolean showDataPanel){
		
		this.showDataPanel = showDataPanel;
		
		if (showDataPanel) {
			if(displayPanel == null){
				Application.debug("splitpane null");
			}
			displayPanel.setLeftComponent(dataPanel);
			displayPanel.setDividerLocation(100);
			displayPanel.setDividerSize(4);
		} else {
			displayPanel.setLeftComponent(null);
			displayPanel.setLastDividerLocation(displayPanel.getDividerLocation());
			displayPanel.setDividerLocation(0);
			displayPanel.setDividerSize(0);
		}

	}


	//=================================================
	//      Event Handlers and Updates
	//=================================================

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		if(source == cbShowData){
			setShowDataPanel(cbShowData.isSelected());
		}
		if(source == btnClose){
			setVisible(false);
		}

		if(source == btnExport){
			((CardLayout)cardPanel.getLayout()).show(cardPanel, "exportPanel");
		}

		if(source == btnOptions){
			((CardLayout)cardPanel.getLayout()).show(cardPanel, "optionsPanel");
		}

		if(source == btnDisplay){
			((CardLayout)cardPanel.getLayout()).show(cardPanel, "displayPanel");
		}

		btnClose.requestFocus();
	}




	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		if(isVisible){
			spView.setColumnSelect(true);
			if(!isIniting)
				this.handleSpreadsheetSelectionChange();	

		}else{
			spView.setColumnSelect(false);
			removeGeos();

		}
	}



	public void handleDataPanelSelectionChange(Boolean[] selectionList){
		updateDataSelection(selectionList);

		// TODO why does this mess up the dataPanel when clicking a checkbox?
		//resetSpreadsheetSelection();
	}

	
	public void handleSpreadsheetSelectionChange(){
		if( !spreadsheetTable.getSelectedColumnsList().equals(selectedColumns)){
			updateDataList();
		}
		dataPanel.loadDataTable(this.dataListAll);
	}

	public void updateDataList(){

		selectedColumns = spreadsheetTable.getSelectedColumnsList();	
		loadDataLists();
		updatePlots();

	}

	public void updatePlots(){	
		comboStatPanel.updatePlot();
		comboStatPanel2.updatePlot();
	}


	public boolean isInDataColumn(GeoElement geo){

		Point location = geo.getSpreadsheetCoords();
		return location != null && selectedColumns.contains(location.x); 

	}

	


	
	
	

	public void updateFonts() {

		Font font = app.getPlainFont();

		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;

		setFont(font);

		comboStatPanel.updateStatTableFonts(font);
		comboStatPanel2.updateStatTableFonts(font);
		dataPanel.updateFonts(font);
	}




	//=====================================================
	// ComboStatPanel class
	//=====================================================


	private class ComboStatPanel extends JPanel{

		// plot types
		private static final int PLOT_HISTOGRAM = 0;
		private static final int PLOT_BOXPLOT = 1;
		private static final int PLOT_DOTPLOT = 2;
		//private static final int PLOT_NORMALQUANTILE = 3;
		private static final int PLOT_STATISTICS = 3;
		private int numPlots = 4;
		//private GeoElement[] plotGeos;

		private int plotIndex = PLOT_HISTOGRAM;

		private int numClasses = 6;
		private JPanel numClassesPanel;
		private JSlider sliderNumClasses; 

		private JComboBox cbPlotTypes;
		private JPanel statDisplayPanel;

		private PlotPanel plotPanel;
		private StatPanel statPanel;



		/***************************************** 
		 * Construct a ComboStatPanel
		 */
		private  ComboStatPanel(int selectedPlotIndex){

			this.plotIndex = selectedPlotIndex;
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
					updatePlot();
					btnClose.requestFocus();
				}
			});


			numClassesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			numClassesPanel.add(sliderNumClasses);
			numClassesPanel.add(new JLabel(app.getPlain("classes" + ": ")));
			numClassesPanel.add(lblNumClasses);



			String[] plotNames = new String[numPlots]; 
			plotNames[PLOT_HISTOGRAM] = app.getCommand("Histogram");
			plotNames[PLOT_BOXPLOT] = app.getCommand("Boxplot");
			plotNames[PLOT_DOTPLOT] = app.getCommand("DotPlot");
			plotNames[PLOT_STATISTICS] = app.getPlain("Statistics");
			//plotNames[PLOT_NORMALQUANTILE] = app.getCommand("NormalQuantile");

			cbPlotTypes = new JComboBox(plotNames);
			cbPlotTypes.setSelectedIndex(plotIndex);
			cbPlotTypes.addActionListener(new ActionListener() {       
				public void actionPerformed(ActionEvent e)
				{
					plotIndex = cbPlotTypes.getSelectedIndex();
					updatePlot();
					btnClose.requestFocus();

				}
			});      


			JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			plotTypePanel.add(cbPlotTypes);
			plotTypePanel.add(numClassesPanel);

			JPanel controlPanel = new JPanel(new BorderLayout());
			controlPanel.add(plotTypePanel,BorderLayout.WEST);
			//controlPanel.add(numClassesPanel,BorderLayout.CENTER);

			plotPanel = new PlotPanel(app);
			plotPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));


			statPanel = new StatPanel(app, statDialog, dataListSelected);
			statPanel.setBorder(plotPanel.getBorder());

			statDisplayPanel = new JPanel(new CardLayout());
			statDisplayPanel.add("plotPanel", plotPanel);
			statDisplayPanel.add("statPanel", statPanel);
			statDisplayPanel.setBackground(plotPanel.getBackground());

			//plotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

			this.setLayout(new BorderLayout());
			this.add(controlPanel,BorderLayout.NORTH);
			this.add(statDisplayPanel,BorderLayout.CENTER);

			/*
			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEmptyBorder(2, 2, 2, 2), 
					BorderFactory.createLineBorder(Color.BLACK)));
			 */

			this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));


			updatePlot();

		}

		public void removeGeos(){
			statPanel.removeGeos();
			plotPanel.removeGeos();
		}
		
		

		public void updatePlot(){
			numClassesPanel.setVisible(false);
			switch(plotIndex){
			case PLOT_HISTOGRAM:
				plotPanel.updateHistogram( dataListSelected, numClasses);
				numClassesPanel.setVisible(true);
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
				break;	
			case PLOT_BOXPLOT:
				plotPanel.updateBoxPlot( dataListSelected);
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
				break;
			case PLOT_DOTPLOT:
				plotPanel.updateDotPlot( dataListSelected);
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
				break;
			case PLOT_STATISTICS:
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "statPanel");
				statPanel.updateTable();
				break;
			}

		}


		public void updateStatTableFonts(Font font){
			statPanel.updateFonts(font);
		}



		// END class ComboStatPanel 
	}	//========================================================





	//=================================================
	//      View Implementation
	//=================================================
	
	public void add(GeoElement geo) {
		//System.out.println("add: " + geo.toString());
		if (!isIniting && isInDataColumn(geo)) {	
			//loadDataLists();
			//comboStatPanel.updatePlot();
			//comboStatPanel2.updatePlot();
		}
	}

	public void clearView() {
	}

	public void remove(GeoElement geo) {
		//System.out.println("removed: " + geo.toString());
		if (!isIniting && isInDataColumn(geo)) {	
			//loadDataLists();
			//comboStatPanel.updatePlot();
			//comboStatPanel2.updatePlot();
		}
	}

	public void rename(GeoElement geo) {
	}

	public void repaintView() {
	}

	public void reset() {	
	}

	public void update(GeoElement geo) {

		if (!isIniting && isInDataColumn(geo)) {
			//comboStatPanel.updatePlot();
			//comboStatPanel2.updatePlot();
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
