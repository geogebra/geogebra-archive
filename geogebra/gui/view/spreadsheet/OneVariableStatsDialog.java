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
	//private ArrayList<Integer> selectedColumns;
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
	
	private Object dataSource;
	
	
	
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
		
		dataSource = new ArrayList<CellRange>();
		//load data from current selection
	//	selectedColumns = spreadsheetTable.getSelectedColumnsList();
		setDataSource();
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
		
		if(dataSource instanceof GeoList){
			//dataListAll = dataSource;
			text = ((GeoList)dataSource).getLabel();
			if(isSorted)
				text = "Sort[" + text + "]";
			//text = ((GeoList)dataSource).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
		}else{
			GeoList tempGeo = (GeoList) spreadsheetTable.getCellRangeProcessor()
			.createList((ArrayList<CellRange>) dataSource, true, copyByValue, isSorted, false, GeoElement.GEO_CLASS_NUMERIC);
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

			dataListSelected = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)dataListSelected, text, true,false);		
			
			
			//dataListAll.setFixed(true);
			//dataListSelected.setFixed(true);
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		if(!isIniting){
			dataPanel.loadDataTable(this.dataListAll);
		}
		
		
		
		
		// selection has changed, so the spreadsheet columns need to be re-selected
		//resetSpreadsheetSelection();

	}

	
	public void updateSelectedDataList2(int index, boolean doAdd) {
		
		GeoElement geo = dataListAll.get(index);
		if(doAdd)
			dataListSelected.add(geo);
		else
			dataListSelected.remove(geo);
	dataListSelected.updateCascade();
	updateAllComboPanels(false);
	
	
	}
	
	
	
	
	public void updateSelectedDataList(Boolean[] selectionList) {

		// create a string for the selected data
		StringBuilder sb = new StringBuilder();
		sb.append("Sort[{");
		for(int i=0; i < dataListAll.size(); ++i){
			if(selectionList[i] == true){
				sb.append(dataListAll.get(i).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false));
				sb.append(",");
			}
		}
		
		// remove last comma
		if(dataListAll.size() > 0) {
			sb.deleteCharAt(sb.length()-1);
		}
		
		sb.append("}]");

	
		
		comboStatPanel.removeGeos();
		comboStatPanel2.removeGeos();
		
		
		try {
			dataListSelected = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)dataListSelected, sb.toString(), true,false);
		} catch (Exception e) {
			e.printStackTrace();
		}	

		Application.debug(dataListSelected.toDefinedValueString());
		updateAllComboPanels(false);
		
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
			//spView.setColumnSelect(true);
			if(!isIniting)
				//this.handleSpreadsheetSelectionChange();
				updateDialog();
			

		}else{
			//Application.printStacktrace("hide statDialog");
			//spView.setColumnSelect(false);
			removeGeos();
			
			//this.detachView();
			
		}
	}



	public void handleDataPanelSelectionChange(Boolean[] selectionList){
		updateSelectedDataList(selectionList);

		// TODO why does this mess up the dataPanel when clicking a checkbox?
		//resetSpreadsheetSelection();
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
		comboStatPanel.updatePlot(doCreateGeo);
		comboStatPanel2.updatePlot(doCreateGeo);
	}


	public boolean isInDataColumn(GeoElement geo){

	//	Point location = geo.getSpreadsheetCoords();
	//	return location != null && selectedColumns.contains(location.x); 
		return false;
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
					updatePlot(true);
					//btnClose.requestFocus();
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
					updatePlot(true);
					//btnClose.requestFocus();

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


			updatePlot(true);

		}

		public void removeGeos(){
			statPanel.removeGeos();
			plotPanel.removeGeos();
		}
		
		public void detachView(){
			plotPanel.detachView();
			
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
			case PLOT_STATISTICS:
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "statPanel");
				statPanel.updateTable();
				break;
			}

		}


		public void updateStatTableFonts(Font font){
			statPanel.updateFonts(font);
		}

		public void attachView() {
			plotPanel.attachView();
			
		}

		

		// END class ComboStatPanel 
	}	//========================================================





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

	//	if (!isIniting && isInDataColumn(geo)) {
			//comboStatPanel.updatePlot();
			//comboStatPanel2.updatePlot();
	//	}
		//Application.debug("------> update:" + geo.toString());
		if (isInDataSource(geo)) {
			//Application.debug("---------> is in data source:" + geo.toString());
			//removeGeos();
			//this.loadDataLists();
			updateAllComboPanels(false);
			
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
