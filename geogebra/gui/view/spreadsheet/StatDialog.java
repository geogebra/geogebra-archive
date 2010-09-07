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
import javax.swing.JSplitPane;

public class StatDialog extends JDialog 
implements ActionListener, View   {
	
	
	// ggb components
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	private SpreadsheetView spView;
	private MyTable spreadsheetTable;
	private StatDialog statDialog;
		
	// modes
	public static final int MODE_ONEVAR =  0;
	public static final int MODE_TWOVAR =  1;
	private int mode;
		
	// data collections
	private GeoList dataListAll, dataListSelected;
	ArrayList<String> dataTitles ;
	private Object dataSource;
	
	
	// GUI objects
	private JButton btnClose, btnOptions, btnExport, btnDisplay;
	private JCheckBox cbShowData, cbShowCombo2;
	private JComboBox comboRegression;
	private StatComboPanel comboStatPanel, comboStatPanel2;;
	private StatDataPanel dataPanel;
	private StatTablePanel statPanel;
	private JSplitPane statDataPanel; 
	private JSplitPane displayPanel;	
	private JSplitPane comboPanelSplit;
	private JPanel cardPanel;
		
	
	// flags
	private boolean showDataPanel = false;
	private boolean showComboPanel2 = false;
	private boolean isIniting;
	private Dimension defaultDialogDimension;
		
	// colors
	public static final Color TABLE_GRID_COLOR = Color.GRAY;
	public static final Color TABLE_HEADER_COLOR = new Color(240,240,240);   
	public static final Color HISTOGRAM_COLOR = new Color(0,0,255); // blue with alpha 0.25   
	public static final Color BOXPLOT_COLOR = new Color(204,0,0);  // rose with alpha 0.25 
	public static final Color DOTPLOT_COLOR = new Color(0,204,204); // blue-green   
	public static final Color REGRESSION_COLOR = Color.BLACK;    

	// regression
	public static final int REG_NONE = 0;
	public static final int REG_LINEAR = 1;
	public static final int REG_LOG = 2;
	
	//public static final int REG_LOGISTIC = 3;
	public static final int REG_POW = 4;
	public static final int REG_EXP = 5;
	public static final int REG_SIN = 6;
	public static final int REG_POLY2 = 3;
	public static final int REG_POLY3 = 8;
	public static final int REG_POLY4 = 9;
	public static final int REG_POLY5 = 10;
	public static final int REG_POLY6 = 11;
	public static final int regressionTypes = 4;
	
	
	
	
	
	private int regressionMode = REG_NONE;
	public int getRegressionMode() {
		return regressionMode;
	}

	private String[] regressionLabels;
	private String [] regCmd;
	

	public String[] getRegCmd() {
		return regCmd;
	}
	
	public Application getApp() {
		return app;
	}


	/*************************************************
	 * Construct the dialog
	 */
	public StatDialog(SpreadsheetView spView, Application app, int mode){
		super(app.getFrame(),false);

		isIniting = true;
		this.app = app;
		this.spView = spView;
		this.spreadsheetTable = spView.getTable();
		this.mode = mode;
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		statDialog = this;
			
		defaultDialogDimension = new Dimension(600,500);
	
	
		//===========================================
		//load data from the data source (based on currently selected geos)
		
		setDataSource();
		loadDataLists();


		//================================================
		// Create two StatCombo panels with default plots.
		// StatCombo panels display various plots and tables
		// selected by a comboBox.
		
		switch(mode){

		case MODE_ONEVAR:
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_HISTOGRAM, dataListSelected, mode);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_BOXPLOT, dataListSelected, mode);
			break;

		case MODE_TWOVAR:
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_SCATTERPLOT, dataListSelected, mode);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_RESIDUAL, dataListSelected, mode);
			break;		
		}

		
		//================================================
		// Create a StatPanel.
		// StatPanels display basic statistics for the current data set
		
		statPanel = new StatTablePanel(app, dataListSelected, mode);
		statPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		//statPanel.updateData(dataListSelected);

		
		//================================================
		// Create a DataPanel.
		// Data panels display the current data set(s) and allow temporary editing. 
		// Edited data is used by the statTable and statCombo panels. 
		
		dataPanel = new StatDataPanel(app, this, dataListAll, mode);
		dataPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	
		
		
		//================================================
		// Init the GUI and attach this view to the kernel
		
		initGUI();
		updateFonts();
		btnClose.requestFocus();
		attachView();	
		isIniting = false;

	} 
	// END StatDialog constructor





	public void removeGeos(){
		
		if(dataListAll != null)
			dataListAll.remove();
		dataListAll = null;
		
		if(dataListSelected != null)
			dataListSelected.remove();
		dataListSelected = null;
		
		statPanel.removeGeos();
		dataPanel.removeGeos();
		comboStatPanel.removeGeos();
		comboStatPanel2.removeGeos();
	}
	
	
	
	//=================================================
	//       Load Data
	//=================================================
	
	/** 
	 * Determines if the data source is from GeoList or a cell range.
	 */
	private void setDataSource(){
		
		GeoElement geo = app.getSelectedGeos().get(0);
		if(geo.isGeoList()){
			dataSource = geo;
		} else {
			ArrayList<CellRange> cr = spreadsheetTable.selectedCellRanges;
			dataSource = (ArrayList<CellRange>) cr.clone();		
		}
	}
	
	
	
	/**
	 * Loads two GeoLists: (1) all data (2) selected data
	 * Data can come from either a GeoList or a range of spreadsheet cells. 
	 */
	private void loadDataLists(){

		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		String text = "";
		
		boolean isSorted = true;
		boolean copyByValue = false;
		boolean scanByColumn = true;
		boolean doStoreUndo = false;
		
		
		//=======================================
		// create a string to represent the data 
		
		if(dataSource instanceof GeoList){
			//dataListAll = dataSource;
			text = ((GeoList)dataSource).getLabel();
			if(isSorted)
				text = "Sort[" + text + "]";
			//text = ((GeoList)dataSource).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);

		}else{
			GeoList tempGeo = null; 
			switch (mode){

			case MODE_ONEVAR:

				tempGeo = (GeoList) cr.createList(
						(ArrayList<CellRange>) dataSource, 
						scanByColumn,
						copyByValue, 
						isSorted, 
						doStoreUndo, 
						GeoElement.GEO_CLASS_NUMERIC);
				break;

			case MODE_TWOVAR:
				
				tempGeo = (GeoList) cr.createPointList(
						(ArrayList<CellRange>) dataSource, 
						scanByColumn,
						copyByValue, 
						isSorted, 
						doStoreUndo);
				break;
			}
			
			//text = tempGeo.toDefinedValueString();
			text = tempGeo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
			tempGeo.remove();
		}	
		//System.out.println(text);		
		
		
		
		//===================================
		// create the data lists
		
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

		
		
		//===================================
		// load data into the lists
				
		try {			
			dataListAll = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)dataListAll, text, true, false);
				
			for(int i=0; i<dataListAll.size(); ++i)
				dataListSelected.add(dataListAll.get(i));		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		if(!isIniting){
			dataPanel.updateDataTable(this.dataListAll);
		}
	
	}

	
	
	/**
	 * Add/remove elements from the selected data list. 
	 * Called by the data panel on checkbox click.
	 */
	public void updateSelectedDataList(int index, boolean doAdd) {

		GeoElement geo = dataListAll.get(index);
		
		if(doAdd){
			dataListSelected.add(geo);
		}else{
			dataListSelected.remove(geo);
		}
		
		dataListSelected.updateCascade();
		updateAllComboPanels(false);
		//Application.debug("updateSelectedList: " + index + doAdd);
		
	}



	public String[] getDataTitles(){
		
		String[] title = null;

		switch(mode){

		case MODE_ONEVAR:

			title = new String[1];		

			if(dataSource instanceof GeoList){
				title[0] = app.getPlain("untitled");

			}else{

				CellRange cr = ((ArrayList<CellRange>)dataSource).get(0);
				if(cr.isColumn()) {
					GeoElement geo = RelativeCopy.getValue(spreadsheetTable, cr.getMinColumn(), cr.getMinRow());
					if(geo != null && geo.isGeoText())
						title[0] = geo.toDefinedValueString();
					else
						title[0]= app.getCommand("Column") + " " + 
						GeoElement.getSpreadsheetColumnName(cr.getMinColumn());		

				}else{
					title[0] = app.getPlain("untitled");
				}
			}
			
			break;

		case MODE_TWOVAR:
			//TODO -- get actual titles, handling ctrl-select
			title = new String[2];	
			title[0] = app.getMenu("Column.X");
			title[1] = app.getMenu("Column.Y");
			break;

		}

		return title;
	}


	
	//=================================================
	//       GUI
	//=================================================
	
	
	private void initGUI() {

		try {
			switch(mode){
			case MODE_ONEVAR:
				setTitle(app.getMenu("OneVariableStatistics"));	
				break;
			case MODE_TWOVAR:
				setTitle(app.getMenu("TwoVariableStatistics"));	
				break;

			}
			
			//===========================================
			// button panel
			
			btnClose = new JButton(app.getMenu("Close"));
			btnClose.addActionListener(this);
			JPanel rightButtonPanel = new JPanel(new FlowLayout());
			rightButtonPanel.add(btnClose);
			
			
			btnOptions = new JButton(app.getMenu("Options"));
			btnOptions.addActionListener(this);
			
			btnExport = new JButton(app.getMenu("Export"));
			btnExport.addActionListener(this);
			
			btnDisplay = new JButton(app.getMenu("Plots"));
			btnDisplay.addActionListener(this);
			
			createRegressionComboBox();
			
			JPanel centerButtonPanel = new JPanel(new FlowLayout());
			//centerButtonPanel.add(btnOptions);
			//centerButtonPanel.add(btnDisplay);
			//centerButtonPanel.add(btnExport);
			centerButtonPanel.add(comboRegression);
			
			
			cbShowData = new JCheckBox(app.getMenu("ShowData"));
			cbShowData.setSelected(showDataPanel);
			cbShowData.addActionListener(this);
			
			cbShowCombo2 = new JCheckBox(app.getMenu("ShowPlot2"));
			cbShowCombo2.setSelected(showComboPanel2);
			cbShowCombo2.addActionListener(this);
			
			
			JPanel leftButtonPanel = new JPanel(new FlowLayout());
			leftButtonPanel.add(cbShowData);
			leftButtonPanel.add(cbShowCombo2);
			
			
			JPanel buttonPanel = new JPanel(new BorderLayout());
			buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
			buttonPanel.add(centerButtonPanel, BorderLayout.CENTER);
			buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
			// END button panel
			
			
			

			//===========================================
			// statData panel
			
			
			statPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			dataPanel.setBorder(statPanel.getBorder());
			
			statDataPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statPanel, dataPanel);
			
			//statDataPanel.setLayout(new BoxLayout(statDataPanel, BoxLayout.Y_AXIS));
			//statDataPanel.add(new JLabel(""), BorderLayout.NORTH);
			//statDataPanel.add(statPanel, BorderLayout.NORTH);
			//statDataPanel.add(dataPanel, BorderLayout.SOUTH);
			
			
			//===========================================
			// display panels: options, export and display  (options/export not used yet)
			
			
				
			comboPanelSplit = new JSplitPane(
					JSplitPane.VERTICAL_SPLIT, comboStatPanel, comboStatPanel2);
			displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statDataPanel, comboPanelSplit);
			displayPanel.setDividerLocation(150);
			
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
			
			
			getContentPane().add(mainPanel);
			getContentPane().setPreferredSize(defaultDialogDimension);
			setResizable(true);
			pack();
			
			comboPanelSplit.setDividerLocation(0.5);
			setShowComboPanel2(showComboPanel2);
			setShowDataPanel(showDataPanel);
			
			setLocationRelativeTo(app.getFrame());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void setLabels(){
		
		regressionLabels = new String[regressionTypes];
		
		regressionLabels[REG_NONE] = app.getPlain("None");
		regressionLabels[REG_LINEAR] = app.getPlain("Linear");
		regressionLabels[REG_LOG] = app.getPlain("Log");
		regressionLabels[REG_POLY2] = app.getPlain("Poly2");
			
		
	}
	
	
	
	private void createRegressionComboBox(){
		
		regressionLabels = new String[regressionTypes];
		
		regressionLabels[REG_NONE] = app.getPlain("None");
		regressionLabels[REG_LINEAR] = app.getPlain("Linear");
		regressionLabels[REG_LOG] = app.getPlain("Log");
		regressionLabels[REG_POLY2] = app.getPlain("Poly2");
		
		comboRegression = new JComboBox(regressionLabels);
		comboRegression.addActionListener(this);
		
	}
	
	
	
	

	private void setShowComboPanel2(boolean showComboPanel2){
		
		this.showComboPanel2 = showComboPanel2;
		
		if (showComboPanel2) {
			if(comboPanelSplit == null){
				Application.debug("splitpane null");
			}
			comboPanelSplit.setBottomComponent(comboStatPanel2);
			comboPanelSplit.setDividerLocation(200);
			comboPanelSplit.setDividerSize(4);
		} else {
			comboPanelSplit.setBottomComponent(null);
			comboPanelSplit.setLastDividerLocation(comboPanelSplit.getDividerLocation());
			comboPanelSplit.setDividerLocation(0);
			comboPanelSplit.setDividerSize(0);
		}

	}

	
	private void setShowDataPanel(boolean showDataPanel){
		
		this.showDataPanel = showDataPanel;
		
		if (showDataPanel) {
			if(statDataPanel == null){
				Application.debug("splitpane null");
			}
			statDataPanel.setBottomComponent(dataPanel);
			statDataPanel.setDividerLocation(200);
			statDataPanel.setDividerSize(4);
		} else {
			statDataPanel.setBottomComponent(null);
			statDataPanel.setLastDividerLocation(statDataPanel.getDividerLocation());
			statDataPanel.setDividerLocation(0);
			statDataPanel.setDividerSize(0);
		}

	}
	
	

	public int getMode(){
		return mode;
	}

	
	//=================================================
	//      Event Handlers and Updates
	//=================================================

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();
		
		if(source == cbShowData){
			setShowDataPanel(cbShowData.isSelected());
		}
		
		else if(source == cbShowCombo2){
			setShowComboPanel2(cbShowCombo2.isSelected());
		}
		
		
		else if(source == btnClose){
			setVisible(false);
		}

		else if(source == btnExport){
			((CardLayout)cardPanel.getLayout()).show(cardPanel, "exportPanel");
		}

		else if(source == btnOptions){
			((CardLayout)cardPanel.getLayout()).show(cardPanel, "optionsPanel");
		}

		else if(source == btnDisplay){
			((CardLayout)cardPanel.getLayout()).show(cardPanel, "displayPanel");
		}

		else if(source == comboRegression){
			regressionMode = comboRegression.getSelectedIndex();
			this.updateAllComboPanels(true);
		}
		
		
		
		btnClose.requestFocus();
	}




	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		if(isVisible){
			//Application.debug("statDialog visible");
			//spView.setColumnSelect(true);
			this.attachView();
			if(!isIniting)
				updateDialog();
			
		}else{
			//Application.debug("statDialog not visible");
			//spView.setColumnSelect(false);
			removeGeos();		
			this.detachView();		
		}
	}


	
	public void updateDialog(){

		//selectedColumns = spreadsheetTable.getSelectedColumnsList();
		removeGeos();
		setDataSource();
		loadDataLists();
		updateAllComboPanels(true);

	}

	public void updateAllComboPanels(boolean doCreateGeo){
		//loadDataLists();	
		comboStatPanel.updateData(dataListSelected);
		comboStatPanel2.updateData(dataListSelected);
		comboStatPanel.updatePlot(doCreateGeo);
		comboStatPanel2.updatePlot(doCreateGeo);
		statPanel.updateData(dataListSelected);
		
	}



	public boolean isInDataSource(GeoElement geo){
		// TODO handle case of GeoList data source
			if(dataSource instanceof GeoList){
				return geo.equals(((GeoList)dataSource));
			}else{
		
			Point location = geo.getSpreadsheetCoords();
			boolean isCell = (location != null && location.x < SpreadsheetView.MAX_COLUMNS && location.y < SpreadsheetView.MAX_ROWS);
			
			if(isCell){	
				//Application.debug("---------> is cell:" + geo.toString());
				for(CellRange cr: (ArrayList<CellRange>)dataSource)
					if(cr.contains(geo)) return true;		
		
				//Application.debug("---------> is not in data source:" + geo.toString());
			}
			}
			
			return false;
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
		statPanel.updateFonts(font);
	}




	//=================================================
	//      View Implementation
	//=================================================
	
	public void add(GeoElement geo) {
		//System.out.println("add: " + geo.toString());
	//	if (!isIniting && isInDataColumn(geo)) {	
			//loadDataLists();
			//comboStatPanel.updatePlot();
			//comboStatPanel2.updatePlot();
	//	}
	}

	public void clearView() {
	}

	public void remove(GeoElement geo) {
		//System.out.println("removed: " + geo.toString());
	//	if (!isIniting && isInDataColumn(geo)) {	
			//loadDataLists();
			//comboStatPanel.updatePlot();
			//comboStatPanel2.updatePlot();
	//	}
	}

	public void rename(GeoElement geo) {
	}

	public void repaintView() {
	}

	public void reset() {
		//removeGeos();
	}
	
	public void setMode(int mode) {
		// ignore..
	}

	public void update(GeoElement geo) {

		//Application.debug("------> update:" + geo.toString());
		if (!isIniting && isInDataSource(geo)) {
			Application.debug("---------> is in data source:" + geo.toString());
			//removeGeos();
			//this.loadDataLists();
			updateAllComboPanels(true);	
			
		}
			
	}

	public void updateAuxiliaryObject(GeoElement geo) {
	}

	public void attachView() {
		//clearView();
		//kernel.notifyAddAll(this);
		kernel.attach(this);
		comboStatPanel.attachView();
		comboStatPanel2.attachView();
	}

	public void detachView() {
		kernel.detach(this);
		comboStatPanel.detachView();
		comboStatPanel2.detachView();
		//clearView();
		//kernel.notifyRemoveAll(this);		
	}

	
	
	
}
