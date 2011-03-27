package geogebra.gui.view.spreadsheet;


import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

public class StatDialog extends JDialog  implements ActionListener, View, Printable   {


	// ggb 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	private SpreadsheetView spView;
	private MyTable spreadsheetTable;
	private StatDialog statDialog;	
	private StatGeo statGeo;


	// modes
	public static final int MODE_ONEVAR = 0;
	public static final int MODE_REGRESSION = 1;
	public static final int MODE_MULTIVAR = 2;
	private int mode;


	// data collections
	private GeoList statList;
	private GeoElement geoRegression;
	private GeoList dataListAll, dataListSelected;

	private ArrayList<String> dataTitles ;
	private Object dataSource;


	// flags
	private boolean showDataPanel = false;
	private boolean showStatPanel = false;
	private boolean showComboPanel2 = false;
	private boolean isIniting;
	private Dimension defaultDialogDimension;
	private boolean leftToRight = true;

	// colors
	public static final Color TABLE_GRID_COLOR = Color.GRAY;
	public static final Color TABLE_HEADER_COLOR = new Color(240,240,240);   
	public static final Color HISTOGRAM_COLOR = new Color(0,0,255); // blue with alpha 0.25   
	public static final Color BOXPLOT_COLOR = new Color(204,0,0);  // rose with alpha 0.25 
	public static final Color DOTPLOT_COLOR = new Color(0,204,204); // blue-green   
	public static final Color REGRESSION_COLOR = Color.RED;    


	// regression types
	public static final int REG_NONE = 0;
	public static final int REG_LINEAR = 1;
	public static final int REG_LOG = 2;
	public static final int REG_POLY = 3;
	public static final int REG_POW = 4;
	public static final int REG_EXP = 5;
	public static final int REG_SIN = 6;
	public static final int REG_LOGISTIC = 7;

	public static final int regressionTypes = 8;
	private int regressionMode = REG_NONE;
	private int regressionOrder = 2;
	private String[] regressionLabels, regCmd;
	private String regEquation;


	// oneVar title panel objects
	private JLabel lblOneVarTitle;
	private MyTextField fldOneVarTitle;

	// stat and data panel objects
	private JLabel statisticsHeader;
	private JPanel statPanel;
	private StatDataPanel dataPanel;
	private StatTable statTable;

	// regression panel objects
	private JLabel lblRegression, lblRegEquation, lblEqn;
	private JLabel lblTitleX, lblTitleY;
	private MyTextField fldTitleX, fldTitleY;
	private JComboBox cbRegression, cbPolyOrder;
	private JToggleButton btnToggleXY;
	private JLabel lblEvaluate;
	private MyTextField fldInputX;
	private JLabel lblOutputY;


	// plot display objects 
	private StatComboPanel comboStatPanel, comboStatPanel2;;

	// button panel objects
	private JButton btnClose, btnPrint;
	private PopupMenuButton btnOptions;
	private StatDialogOptionsPanel dialogOptionsPanel;


	// main GUI panel objects
	private JSplitPane statDataPanel, displayPanel, comboPanelSplit; 
	private JPanel cardPanel, buttonPanel;
	private int defaultDividerSize;
	private RegressionPanel regressionPanel;





	//=================================
	// getters/setters

	public GeoElement getRegressionModel() {
		return geoRegression;
	}
	public void setRegressionModel(GeoElement regressionModel) {
		this.geoRegression = regressionModel;
	}


	public StatGeo getStatGeo() {
		if(statGeo == null)
			statGeo = new StatGeo(app);
		return statGeo;
	}


	public int getRegressionOrder() {
		return regressionOrder;
	}


	public void setRegressionMode(int regressionMode) {
		this.regressionMode = regressionMode;
	}

	public int getRegressionMode() {
		return regressionMode;
	}

	public void setRegressionOrder(int regressionOrder) {
		this.regressionOrder = regressionOrder;
	}

	public Application getApp() {
		return app;
	}

	public void setLeftToRight(boolean leftToRight) {
		this.leftToRight = leftToRight;
	}

	public int getMode(){
		return mode;
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

		defaultDialogDimension = new Dimension(700,500);


		//===========================================
		//load data from the data source (based on currently selected geos)

		boolean dataOK = setDataSource();
		if(dataOK){
			loadDataLists();
		}else{
			//TODO is this needed ?
			//dispose();
			return;  
		}


		//================================================
		// Create two StatCombo panels with default plots.
		// StatCombo panels display various plots and tables
		// selected by a comboBox.

		switch(mode){

		case MODE_ONEVAR:
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_HISTOGRAM, dataListSelected, mode, true);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_BOXPLOT, dataListSelected, mode, true);
			break;

		case MODE_REGRESSION:
			//showComboPanel2 = true;
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_SCATTERPLOT, dataListSelected, mode, true);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_RESIDUAL, dataListSelected, mode, true);
			break;

		case MODE_MULTIVAR:
			showComboPanel2 = true;
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_MULTIBOXPLOT, dataListSelected, mode, true);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_MULTIVARSTATS, dataListSelected, mode, true);
			break;		

		}


		//================================================
		// Create a statList and StatPanel.
		// StatPanels display basic statistics for the current data set

		if(mode == statDialog.MODE_ONEVAR){
			statList = getStatGeo().createBasicStatList(dataListSelected,mode);
			statTable = new StatTable(app, statList, mode);
		}
		else if(mode == statDialog.MODE_REGRESSION){
			statList = getStatGeo().createBasicStatList(dataListSelected,mode,geoRegression);
			statTable = new StatTable(app, statList, mode);
		}


		//================================================
		// Create a DataPanel.
		// Data panels display the current data set(s) and allow temporary editing. 
		// Edited data is used by the statTable and statCombo panels. 

		if(mode != statDialog.MODE_MULTIVAR){
			dataPanel = new StatDataPanel(app, this, dataListAll, mode);
			dataPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}


		//================================================
		// Init the GUI and attach this view to the kernel

		initGUI();
		updateFonts();
		btnClose.requestFocus();
		attachView();	
		isIniting = false;
		setLabels();
		updateGUI();
		pack();

	} 

	/**  END StatDialog constructor  */



	//=================================================
	//       Data Handling
	//=================================================


	/**
	 * Sets the data source. Returns false if data is invalid. Data may come
	 * from either a selected GeoList or the currently selected spreadsheet cell
	 * range.
	 */
	private boolean setDataSource(){
		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		boolean success = true;

		try {
			GeoElement geo = app.getSelectedGeos().get(0);
			if(geo.isGeoList()){
				// TODO: handle validation for a geoList source
				dataSource = geo;
			} else {
				ArrayList<CellRange> rangeList = spreadsheetTable.selectedCellRanges;			
				if(mode == MODE_ONEVAR){
					success = cr.isOneVarStatsPossible(rangeList);
				}
				else if(mode == MODE_REGRESSION){
					success = cr.isCreatePointListPossible(rangeList);
				}
				else if(mode == MODE_MULTIVAR){
					success = cr.isMultiVarStatsPossible(rangeList);
				}

				if(success)
					dataSource = (ArrayList<CellRange>) rangeList.clone();		
			}

		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		return success;
	}



	/**
	 * Loads two GeoLists: (1) all data (2) selected data
	 * Data can come from either a GeoList or a range of spreadsheet cells. 
	 */
	private void loadDataLists(){

		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		String text = "";

		boolean scanByColumn = true;
		boolean isSorted = false;
		boolean copyByValue = false;
		boolean doStoreUndo = false;


		//=======================================
		// create/update dataListAll 

		if(dataListAll != null){
			dataListAll.remove();
		}


		if(dataSource instanceof GeoList){
			//dataListAll = dataSource;
			text = ((GeoList)dataSource).getLabel();
			if(isSorted)
				text = "Sort[" + text + "]";
			//text = ((GeoList)dataSource).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);

		}else{

			switch (mode){

			case MODE_ONEVAR:
				dataListAll = (GeoList) cr.createList(
						(ArrayList<CellRange>) dataSource, 
						scanByColumn,
						copyByValue, 
						isSorted, 
						doStoreUndo, 
						GeoElement.GEO_CLASS_NUMERIC);

				break;

			case MODE_REGRESSION:
				dataListAll = (GeoList) cr.createPointList(
						(ArrayList<CellRange>) dataSource, 
						copyByValue, 
						leftToRight,
						isSorted, 
						doStoreUndo);

				break;

			case MODE_MULTIVAR:
				text = cr.createColumnMatrixExpression((ArrayList<CellRange>) dataSource); 							
				dataListAll = new GeoList(cons);
				try {
					dataListAll = (GeoList) kernel.getAlgebraProcessor()
					.changeGeoElementNoExceptionHandling((GeoElement)dataListAll, text, true, false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
				break;

			}

		}	

		if(dataListAll != null){
			dataListAll.setAuxiliaryObject(true);
			dataListAll.setLabel("dataListAll");
		}


		//=======================================
		// create/update dataListSelected

		if(dataListSelected == null){
			dataListSelected = new GeoList(cons);			
		}
		dataListSelected.setAuxiliaryObject(true);
		dataListSelected.setLabel("dataListSelected");


		try {			
			dataListSelected.clear();
			for(int i=0; i<dataListAll.size(); ++i)
				dataListSelected.add(dataListAll.get(i));		
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		if( !isIniting && dataPanel != null){
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


	/**
	 * Gets the data titles from the source cells.
	 */
	public String[] getDataTitles(){

		CellRangeProcessor cr = spreadsheetTable.getCellRangeProcessor();
		String[] title = null;

		switch(mode){

		case MODE_ONEVAR:

			title = new String[1];		

			if(dataSource instanceof GeoList){
				title[0] = ((GeoList) dataSource).getLabel();

			}else{

				CellRange range = ((ArrayList<CellRange>)dataSource).get(0);
				if(range.isColumn()) {
					GeoElement geo = RelativeCopy.getValue(spreadsheetTable, range.getMinColumn(), range.getMinRow());
					if(geo != null && geo.isGeoText())
						title[0] = geo.toDefinedValueString();
					else
						title[0]= app.getCommand("Column") + " " + 
						GeoElement.getSpreadsheetColumnName(range.getMinColumn());		

				}else{
					title[0] = app.getMenu("Untitled");
				}
			}

			break;

		case MODE_REGRESSION:
			if(dataSource instanceof GeoList){
				//TODO -- handle geolist data source titles
				//title[0] = ((GeoList) dataSource).getLabel();
			}else{
				title = cr.getPointListTitles((ArrayList<CellRange>)dataSource, leftToRight);
			}
			break;

		case MODE_MULTIVAR:
			if(dataSource instanceof GeoList){
				//TODO -- handle geolist data source titles
				//title[0] = ((GeoList) dataSource).getLabel();
			}else{
				title = cr.getColumnTitles((ArrayList<CellRange>)dataSource);
			}
			break;

		}

		return title;
	}


	public void swapXY(){
		leftToRight = !leftToRight;
		updateDialog();
	}


	//=================================================
	//       GUI
	//=================================================


	private void initGUI() {

		if(mode == MODE_ONEVAR)
			showStatPanel = true;

		//===========================================
		// button panel

		btnClose = new JButton();
		btnClose.addActionListener(this);

		btnPrint = new JButton();
		btnPrint.addActionListener(this);

		JPanel rightButtonPanel = new JPanel(new FlowLayout());
		rightButtonPanel.add(btnPrint);
		rightButtonPanel.add(btnClose);

		JPanel centerButtonPanel = new JPanel(new FlowLayout());


		JPanel leftButtonPanel = new JPanel(new FlowLayout());
		createOptionsButton();
		//leftButtonPanel.add(cbShowData);
		//leftButtonPanel.add(cbShowCombo2);
		leftButtonPanel.add(btnOptions);

		buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
		buttonPanel.add(centerButtonPanel, BorderLayout.CENTER);
		buttonPanel.add(rightButtonPanel, BorderLayout.EAST);
		// END button panel


		//===========================================
		// statData panel

		if(mode != statDialog.MODE_MULTIVAR){
			statisticsHeader = new JLabel();
			statisticsHeader.setHorizontalAlignment(JLabel.LEFT);		
			statisticsHeader.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(),	
					BorderFactory.createEmptyBorder(2,5,2,2)));

			// put it all into the stat panel
			statPanel = new JPanel(new BorderLayout());

			statPanel.add(statisticsHeader, BorderLayout.NORTH);
			statPanel.add(statTable, BorderLayout.CENTER);
			statTable.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			statPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			dataPanel.setBorder(statPanel.getBorder());

			statDataPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statPanel, null);
			statDataPanel.setResizeWeight(0.5);

		}


		// create a plotComboPanel		
		comboPanelSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, comboStatPanel, comboStatPanel2);


		JPanel plotComboPanel = new JPanel(new BorderLayout());
		plotComboPanel.add(comboPanelSplit, BorderLayout.CENTER);
		if(mode == MODE_ONEVAR){
			JPanel oneVarTitlePanel = createOneVarTitlePanel();
			//plotComboPanel.add(oneVarTitlePanel, BorderLayout.SOUTH);
			buttonPanel.add(oneVarTitlePanel, BorderLayout.NORTH);
		}
		if(mode == MODE_REGRESSION){
			regressionPanel = new RegressionPanel(app, this);
			//plotComboPanel.add(regressionPanel, BorderLayout.SOUTH);
			buttonPanel.add(regressionPanel, BorderLayout.NORTH);
		}


		// display panel
		if(mode != MODE_MULTIVAR){
			displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statDataPanel, plotComboPanel);
			displayPanel.setDividerLocation(150);
		}else{
			displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					null, plotComboPanel);
		}


		cardPanel = new JPanel(new CardLayout());
		cardPanel.add("displayPanel", displayPanel);


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

		// grab the default divider size 
		defaultDividerSize = comboPanelSplit.getDividerSize();

		setShowComboPanel2(showComboPanel2);
		updateStatDataPanelVisibility();
		setLocationRelativeTo(app.getFrame());

	}





	public void setLabels(){

		switch(mode){
		case MODE_ONEVAR:
			setTitle(app.getMenu("OneVariableStatistics"));	
			lblOneVarTitle.setText(app.getMenu("DataTitle") + ": ");
			statisticsHeader.setText(app.getMenu("Statistics"));
			statTable.updateTable();
			break;
		case MODE_REGRESSION:
			setTitle(app.getMenu("RegressionAnalysis"));	
			statisticsHeader.setText(app.getMenu("Statistics"));
			regressionPanel.setLabels();
			statTable.updateTable();
			break;

		case MODE_MULTIVAR:
			setTitle(app.getMenu("MultiVariableStatistics"));	
			break;
		}

		btnClose.setText(app.getMenu("Close"));
		btnPrint.setText(app.getMenu("Print"));	
		btnOptions.setText(app.getMenu("Options"));

		comboStatPanel.setLabels();

		dialogOptionsPanel.setLabels();

	}



	private JPanel createOneVarTitlePanel(){

		// components
		lblOneVarTitle = new JLabel();
		fldOneVarTitle = new MyTextField(app.getGuiManager());
		fldOneVarTitle.setColumns(30);

		// panels
		JPanel OneVarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		OneVarPanel.add(lblOneVarTitle);
		OneVarPanel.add(fldOneVarTitle);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(OneVarPanel, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEtchedBorder());

		return mainPanel;
	}






	private void createOptionsButton(){

		// create options drop down button
		dialogOptionsPanel= new StatDialogOptionsPanel(app, this);
		dialogOptionsPanel.setShowCombo2(this.showComboPanel2);
		dialogOptionsPanel.setShowData(this.showDataPanel);
		dialogOptionsPanel.setShowStats(this.showStatPanel);


		btnOptions = new PopupMenuButton();
		btnOptions.setKeepVisible(true);
		btnOptions.setStandardButton(true);
		btnOptions.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(10));
		//optionsButton.setText(app.getMenu("Options"));
		btnOptions.addPopupMenuItem(dialogOptionsPanel);
		btnOptions.setDownwardPopup(false);
		//ImageIcon ptCaptureIcon = app.getImageIcon("tool.png");
		//	optionsButton.setIconSize(new Dimension(ptCaptureIcon.getIconWidth(),18));
		//	optionsButton.setIcon(ptCaptureIcon);



		//dialogOptionsPanel.setShowStats(true);

		dialogOptionsPanel.addPropertyChangeListener("cbShowData", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				showDataPanel = (Boolean) evt.getNewValue();
				updateStatDataPanelVisibility();
			}
		});

		dialogOptionsPanel.addPropertyChangeListener("cbShowCombo2", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setShowComboPanel2((Boolean) evt.getNewValue());
			}
		});

		dialogOptionsPanel.addPropertyChangeListener("cbShowStats", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				showStatPanel = (Boolean) evt.getNewValue();
				updateStatDataPanelVisibility();
			}
		});

	}



	//=================================================
	//      Handlers for Component Visibility
	//=================================================

	private void setShowComboPanel2(boolean showComboPanel2){

		this.showComboPanel2 = showComboPanel2;

		if (showComboPanel2) {
			if(comboPanelSplit == null){
				//Application.debug("splitpane null");
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


	private void updateStatDataPanelVisibility(){

		if(mode == MODE_MULTIVAR) return;

		if(showDataPanel){
			if(statDataPanel.getBottomComponent() == null){
				statDataPanel.setBottomComponent(dataPanel);
				statDataPanel.resetToPreferredSizes();				
			}
		}else{
			if(statDataPanel.getBottomComponent() != null){
				statDataPanel.setBottomComponent(null);
				statDataPanel.resetToPreferredSizes();
			}
		}

		if(showStatPanel){
			if(statDataPanel.getTopComponent() == null){
				statDataPanel.setTopComponent(statPanel);
				statDataPanel.resetToPreferredSizes();
			}
		}else{
			if(statDataPanel.getTopComponent() != null){
				statDataPanel.setTopComponent(null);
				statDataPanel.resetToPreferredSizes();
			}
		}

		// hide/show divider
		if(showDataPanel &&  showStatPanel)
			statDataPanel.setDividerSize(defaultDividerSize);	
		else
			statDataPanel.setDividerSize(0);


		// hide/show statData panel 	
		if(showDataPanel ||  showStatPanel){
			if(displayPanel.getLeftComponent() == null){ 
				displayPanel.setLeftComponent(statDataPanel);
				//displayPanel.resetToPreferredSizes();
				displayPanel.setDividerLocation(displayPanel.getLastDividerLocation());
				displayPanel.setDividerSize(defaultDividerSize);				
			}

		}else{ // statData panel is empty, so hide it	
			displayPanel.setLastDividerLocation(displayPanel.getDividerLocation());
			displayPanel.setLeftComponent(null);
			displayPanel.setDividerSize(0);
		}
	}




	//=================================================
	//      Event Handlers and Updates
	//=================================================

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if(source == btnClose){
			setVisible(false);
		}


		else if(source == btnPrint){
			new geogebra.export.PrintPreview(app, this, PageFormat.LANDSCAPE);
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


	public void setRegressionGeo(){

		if(geoRegression != null){
			geoRegression.remove();
		}

		geoRegression = statGeo.createRegressionPlot(dataListSelected, regressionMode, regressionOrder);
		geoRegression.removeView(app.getEuclidianView());
		geoRegression.setAuxiliaryObject(true);
		app.getEuclidianView().remove(geoRegression);
		geoRegression.setLabel("regressionModel");
		this.updateAllComboPanels(true);
	}


	private void updateGUI(){

		if(isIniting) return;


	}



	public void updateDialog(){

		//selectedColumns = spreadsheetTable.getSelectedColumnsList();
		removeGeos();
		boolean dataOK = setDataSource();
		if(dataOK){
			loadDataLists();

			updateAllComboPanels(true);
			if(mode == MODE_ONEVAR){
				fldOneVarTitle.setText(getDataTitles()[0]);
			}
			if(mode == MODE_REGRESSION){
				setRegressionGeo();
				if(regressionPanel != null)
					regressionPanel.updateRegressionPanel();
			}
		}else{
			//TODO --- handle bad data	
		}

	}

	public void updateAllComboPanels(boolean doCreateGeo){
		//loadDataLists();	
		comboStatPanel.updateData(dataListSelected);
		comboStatPanel2.updateData(dataListSelected);
		comboStatPanel.updatePlot(doCreateGeo);
		comboStatPanel2.updatePlot(doCreateGeo);

		if(mode == statDialog.MODE_ONEVAR){
			statList.remove();
			statList = statGeo.createBasicStatList(dataListSelected, mode);
			statTable.updateData(statList);
		}
		else if(mode == statDialog.MODE_REGRESSION){
			statList.remove();
			statList = statGeo.createBasicStatList(dataListSelected, mode,geoRegression);
			statTable.updateData(statList);

		}
	}


	public void updateFonts() {

		Font font = app.getPlainFont();

		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;

		setFont(font);

		comboStatPanel.updateStatTableFonts(font);
		comboStatPanel2.updateStatTableFonts(font);
		if(mode != MODE_MULTIVAR){
			dataPanel.updateFonts(font);
			statTable.updateFonts(font);
		}
	}

	/**
	 * Removes all geos maintained by this dialog and its child components
	 */
	public void removeGeos(){

		if(dataListAll != null)
			dataListAll.remove();

		if(dataListSelected != null)
			dataListSelected.remove();

		if(statList != null)
			statList.remove();

		if(geoRegression != null)
			geoRegression.remove();

		if(statTable != null)
			statTable.removeGeos();

		if(dataPanel != null)
			dataPanel.removeGeos();

		if(comboStatPanel != null)
			comboStatPanel.removeGeos();

		if(comboStatPanel2 != null)
			comboStatPanel2.removeGeos();

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


	//=================================================
	//      Printing
	//=================================================

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {		
		if (pageIndex > 0)
			return (NO_SUCH_PAGE);
		else {
			Graphics2D g2d = (Graphics2D) g;
			AffineTransform oldTransform = g2d.getTransform();

			g2d.translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());

			// construction title
			int y = 0;
			Construction cons = kernel.getConstruction();
			String title = cons.getTitle();
			if (!title.equals("")) {
				Font titleFont = app.getBoldFont().deriveFont(Font.BOLD,
						app.getBoldFont().getSize() + 2);
				g2d.setFont(titleFont);
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getAscent();
				g2d.drawString(title, 0, y);
			}

			// construction author and date
			String author = cons.getAuthor();
			String date = cons.getDate();
			String line = null;
			if (!author.equals("")) {
				line = author;
			}
			if (!date.equals("")) {
				if (line == null)
					line = date;
				else
					line = line + " - " + date;
			}

			if (line != null) {
				g2d.setFont(app.getPlainFont());
				g2d.setColor(Color.black);
				// Font fn = g2d.getFont();
				FontMetrics fm = g2d.getFontMetrics();
				y += fm.getHeight();
				g2d.drawString(line, 0, y);
			}
			if (y > 0) {
				g2d.translate(0, y + 20); // space between title and drawing
			}


			//scale the dialog so that it fits on one page.
			double xScale = pageFormat.getImageableWidth() / this.getWidth();
			double yScale = (pageFormat.getImageableHeight() - (y+20)) / this.getHeight();
			double scale = Math.min(xScale, yScale);

			buttonPanel.setVisible(false);
			this.paint(g2d,scale);
			buttonPanel.setVisible(true);

			System.gc();
			return (PAGE_EXISTS);
		}
	}

	/**
	 * Paint the dialog with given scale factor (used for printing).
	 */
	public void paint(Graphics graphics, double scale) {

		Graphics2D g2 = (Graphics2D)graphics;
		g2.scale(scale, scale);
		super.paint(graphics);	

	}


}
