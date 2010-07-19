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
	//private ArrayList<Integer> selectedColumns;
	ArrayList<String> dataTitles ;
	private Object dataSource;
	

	
	// GUI objects
	private JButton btnClose, btnOptions, btnExport, btnDisplay;
	private JCheckBox cbShowData;
	private StatComboPanel comboStatPanel, comboStatPanel2;;
	private StatDataPanel dataPanel;
	private JSplitPane displayPanel;
	private JPanel cardPanel;
	
	
	// flags
	private boolean showDataPanel = false;
	private boolean isIniting;
	
	
	// colors
	public static final Color TABLE_GRID_COLOR = Color.GRAY;
	public static final Color TABLE_HEADER_COLOR = new Color(240,240,240);   
	public static final Color HISTOGRAM_COLOR = new Color(0,0,255); // blue with alpha 0.25   
	public static final Color BOXPLOT_COLOR = new Color(204,0,0);  // rose with alpha 0.25 
	public static final Color DOTPLOT_COLOR = new Color(0,204,204); // blue-green   


	/*************************************************
	 * Construct the dialog
	 */
	public StatDialog(SpreadsheetView spView, Application app, int mode){
		super(app.getFrame(),false);

		isIniting = true;
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		this.spView = spView;
		this.spreadsheetTable = spView.getTable();
		statDialog = this;
		this.mode = mode;
	
		//	selectedColumns = spreadsheetTable.getSelectedColumnsList();
		
		//load data from current selection
		setDataSource();
		loadDataLists();


		// create two StatCombo panels with default plots
		switch(mode){

		case MODE_ONEVAR:
			comboStatPanel = new StatComboPanel(app, StatComboPanel.PLOT_HISTOGRAM, dataListSelected, mode);
			comboStatPanel2 = new StatComboPanel(app, StatComboPanel.PLOT_STATISTICS_ONEVAR, dataListSelected, mode);
			break;

		case MODE_TWOVAR:
			comboStatPanel = new StatComboPanel(app, StatComboPanel.PLOT_SCATTERPLOT, dataListSelected, mode);
			comboStatPanel2 = new StatComboPanel(app, StatComboPanel.PLOT_STATISTICS_TWOVAR, dataListSelected, mode);
			break;		
		}


		// create a data panel
		dataPanel = new StatDataPanel(app, this, dataListAll, mode);
		dataPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));


		// init the GUI
		initGUI();
		updateFonts();
		btnClose.requestFocus();


		// attach this view to the kernel
		attachView();
		
		
		isIniting = false;

	} //END  StatDialog constructor





	public void removeGeos(){
		if(dataListAll != null)
			dataListAll.remove();
		dataListAll = null;
		if(dataListSelected != null)
			dataListSelected.remove();
		dataListSelected = null;
		dataPanel.removeGeos();
		comboStatPanel.removeGeos();
		comboStatPanel2.removeGeos();
	}
	
	
	
	//=================================================
	//       Load Data
	//=================================================
	
	private void setDataSource(){
		
		GeoElement geo = app.getSelectedGeos().get(0);
		if(geo.isGeoList()){
			dataSource = geo;
		} else {
			ArrayList<CellRange> cr = spreadsheetTable.selectedCellRanges;
			dataSource = (ArrayList<CellRange>) cr.clone();		
		}
	}
	

	private void loadDataLists(){

		String text = "";
		
		boolean isSorted = true;
		boolean copyByValue = false;
		boolean scanByColumn = true;
		boolean doStoreUndo = false;
		
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

				tempGeo = (GeoList) spreadsheetTable
				.getCellRangeProcessor().createList(
						(ArrayList<CellRange>) dataSource, scanByColumn,
						copyByValue, isSorted, doStoreUndo, GeoElement.GEO_CLASS_NUMERIC);
				break;

			case MODE_TWOVAR:
				//copyByValue = true;	
				tempGeo = (GeoList) spreadsheetTable
				.getCellRangeProcessor().createPointList(
						(ArrayList<CellRange>) dataSource, scanByColumn,
						copyByValue, isSorted, doStoreUndo);
				break;


			}
			//text = tempGeo.toDefinedValueString();
			text = tempGeo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
			tempGeo.remove();
		}

	
		//System.out.println(text);
		
		/*			
		GeoList tempGeo = (GeoList) spreadsheetTable.getCellRangeProcessor()
				.createListFromColumn(selectedColumns.get(0), true, false, true, false, GeoElement.GEO_CLASS_NUMERIC);
		String text = tempGeo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
		tempGeo.remove();
		*/
		
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
			
			//dataListAll.setFixed(false);
			//dataListSelected.setFixed(false);
			
			dataListAll = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)dataListAll, text, true, false);

		//	dataListSelected = (GeoList) kernel.getAlgebraProcessor()
		//	.changeGeoElementNoExceptionHandling((GeoElement)dataListSelected, text, true,false);		
			
			
			for(int i=0; i<dataListAll.size(); ++i)
				dataListSelected.add(dataListAll.get(i));
			
			
			//dataListAll.setFixed(true);
			//dataListSelected.setFixed(true);
			
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
		Application.debug("updateSelectedList: " + index + doAdd);
		
	}



	public String getDataTitle(int index){
		
		String title = "";
		/*
		int column = this.selectedColumns.get(index);
		
		GeoElement geo = RelativeCopy.getValue(spreadsheetTable, column, 0);
		if(geo != null && geo.isGeoText())
			title = geo.toDefinedValueString();
		else
			title = app.getCommand("Column") + " " + GeoElement.getSpreadsheetColumnName(column);
		*/
		
		return title;

	}
	
	
	public void resetSpreadsheetSelection(){
		//spreadsheetTable.setSelection(selectedColumns.get(0), 0, selectedColumns.get(0), 0);
		
	}
	
	
	//=================================================
	//       Create GUI
	//=================================================
	
	
	private void initGUI() {

		try {
			switch(mode){
			case MODE_ONEVAR:
				setTitle(app.getPlain("One Variable Statistics"));	
				break;
			case MODE_TWOVAR:
				setTitle(app.getPlain("Two Variable Statistics"));	
				break;

			}
			
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
			this.getContentPane().setPreferredSize(new Dimension(450,500));
			//setResizable(false);
			pack();
			
			comboPanelSplit.setDividerLocation(0.5);
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
			displayPanel.setDividerLocation(200);
			displayPanel.setDividerSize(4);
		} else {
			displayPanel.setLeftComponent(null);
			displayPanel.setLastDividerLocation(displayPanel.getDividerLocation());
			displayPanel.setDividerLocation(0);
			displayPanel.setDividerSize(0);
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
			//spView.setColumnSelect(true);
			if(!isIniting)
				updateDialog();
			
		}else{
			//Application.printStacktrace("hide statDialog");
			//spView.setColumnSelect(false);
			removeGeos();		
			//this.detachView();		
		}
	}



	
	public void handleSpreadsheetSelectionChange(){
	//	if( !spreadsheetTable.getSelectedColumnsList().equals(selectedColumns)){
	//		updateDataList();
	//	}
	//	dataPanel.loadDataTable(this.dataListAll);
	}

	
	public void updateDialog(){

		//selectedColumns = spreadsheetTable.getSelectedColumnsList();
		removeGeos();
		setDataSource();
		loadDataLists();
		updateAllComboPanels(true);

	}

	public void updateAllComboPanels(boolean doCreateGeo){
		comboStatPanel.updateData(dataListSelected);
		comboStatPanel2.updateData(dataListSelected);
		comboStatPanel.updatePlot(doCreateGeo);
		comboStatPanel2.updatePlot(doCreateGeo);
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

	public void update(GeoElement geo) {

		//Application.debug("------> update:" + geo.toString());
		if (!isIniting && isInDataSource(geo)) {
			//Application.debug("---------> is in data source:" + geo.toString());
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
