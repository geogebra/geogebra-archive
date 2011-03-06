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

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

public class StatDialog extends JDialog 
implements ActionListener, View, Printable   {


	// ggb 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	private SpreadsheetView spView;
	private MyTable spreadsheetTable;


	private StatDialog statDialog;	
	private StatGeo statGeo;

	public StatGeo getStatGeo() {
		if(statGeo == null)
			statGeo = new StatGeo(app);
		return statGeo;
	}


	// modes
	public static final int MODE_ONEVAR = 0;
	public static final int MODE_REGRESSION = 1;
	public static final int MODE_MULTIVAR = 2;
	private int mode;


	// data collections
	private GeoList statList;
	private GeoElement regressionModel;

	public GeoElement getRegressionModel() {
		return regressionModel;
	}
	public void setRegressionModel(GeoElement regressionModel) {
		this.regressionModel = regressionModel;
	}


	private GeoList dataListAll, dataListSelected;
	private ArrayList<String> dataTitles ;
	private Object dataSource;


	// GUI objects
	private JLabel lblRegression, lblRegEquation;
	private JTextField tfRegression;
	private JButton btnClose, btnPrint;

	private JComboBox cbRegression, cbPolyOrder;
	private StatComboPanel comboStatPanel, comboStatPanel2;;
	private StatDataPanel dataPanel;
	private StatTable statTable;
	private JSplitPane statDataPanel; 
	private JSplitPane displayPanel;	
	private JSplitPane comboPanelSplit;
	private JPanel cardPanel, buttonPanel;


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
	public static final int REG_POLY = 3;
	public static final int REG_POW = 4;
	public static final int REG_EXP = 5;
	public static final int REG_SIN = 6;
	public static final int REG_LOGISTIC = 7;

	public static final int regressionTypes = 8;
	private int regressionMode = REG_NONE;
	private int regressionOrder;
	private String[] regressionLabels;
	private String [] regCmd;
	private String regEquation;
	private JLabel lblEqn;
	private JLabel lblTitleX, lblTitleY;
	private MyTextField fldTitleX, fldTitleY;
	private JLabel lblOutputY;
	private PopupMenuButton btnOptions;
	private StatDialogOptionsPanel dialogOptionsPanel;
	private JLabel lblOneVarTitle;
	private MyTextField fldOneVarTitle;
	private JLabel statisticsHeader;


	public String getRegEquation() {
		return regEquation;
	}
	public int getRegressionOrder() {
		return regressionOrder;
	}
	public int getRegressionMode() {
		return regressionMode;
	}

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
			statList = getStatGeo().createBasicStatList(dataListSelected,mode,regressionModel);
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
	// END StatDialog constructor





	public void removeGeos(){

		if(dataListAll != null)
			dataListAll.remove();

		if(dataListSelected != null)
			dataListSelected.remove();

		if(statList != null)
			statList.remove();

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
	//       Load Data
	//=================================================

	/** 
	 * Determines if the data source is from GeoList or a cell range.
	 */
	private boolean setDataSource(){

		boolean success = true;

		try {
			GeoElement geo = app.getSelectedGeos().get(0);
			if(geo.isGeoList()){
				dataSource = geo;
			} else {
				ArrayList<CellRange> cr = spreadsheetTable.selectedCellRanges;
				dataSource = (ArrayList<CellRange>) cr.clone();		
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

			case MODE_REGRESSION:

				tempGeo = (GeoList) cr.createPointList(
						(ArrayList<CellRange>) dataSource, 
						scanByColumn,
						copyByValue, 
						isSorted, 
						doStoreUndo);
				break;


			case MODE_MULTIVAR:

				text = cr.createColumnMatrixExpression((ArrayList<CellRange>) dataSource); 				
				//System.out.println(expr);
				break;


			}

			//text = tempGeo.toDefinedValueString();
			if(mode != MODE_MULTIVAR){
				text = tempGeo.getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false);
				tempGeo.remove();
			}


		}	
		//	System.out.println(text);		



		//===================================
		// create the data lists

		if(dataListAll == null){
			dataListAll = new GeoList(cons);
			dataListAll.setAuxiliaryObject(true);
			dataListAll.setLabel("dataListAll");
			//dataListAll.setLabel(null);	
		}

		if(dataListSelected == null){
			dataListSelected = new GeoList(cons);
			dataListSelected.setAuxiliaryObject(true);
			dataListSelected.setLabel("dataListSelected");
			//dataListSelected.setLabel(null);
		}



		//===================================
		// load data into the lists

		try {			
			dataListAll = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)dataListAll, text, true, false);

			dataListSelected.clear();
			for(int i=0; i<dataListAll.size(); ++i)
				dataListSelected.add(dataListAll.get(i));		
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		if(!isIniting){
			if(dataPanel != null)
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
				title[0] = ((GeoList) dataSource).getLabel();

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

		case MODE_REGRESSION:
			//TODO -- get actual titles, handling ctrl-select
			title = new String[2];	
			title[0] = app.getMenu("Untitled");
			title[1] = app.getMenu("Untitled");
			break;



		case MODE_MULTIVAR:
			//TODO -- get actual titles, handling ctrl-select
			title = new String[2];	
			title[0] = app.getMenu("Untitled");
			title[1] = app.getMenu("Untitled");
			break;

		}


		return title;
	}



	//=================================================
	//       GUI
	//=================================================


	private void initGUI() {


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
		//centerButtonPanel.add(btnOptions);
		//centerButtonPanel.add(btnDisplay);
		//centerButtonPanel.add(btnExport);


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
			JPanel statPanel = new JPanel(new BorderLayout());

			statPanel.add(statisticsHeader, BorderLayout.NORTH);
			statPanel.add(statTable, BorderLayout.CENTER);
			statTable.setBorder(BorderFactory.createLineBorder(Color.GRAY));

			statPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			dataPanel.setBorder(statPanel.getBorder());

			statDataPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statPanel, dataPanel);

			//statDataPanel.setLayout(new BoxLayout(statDataPanel, BoxLayout.Y_AXIS));
			//statDataPanel.add(new JLabel(""), BorderLayout.NORTH);
			//statDataPanel.add(statPanel, BorderLayout.NORTH);
			//statDataPanel.add(dataPanel, BorderLayout.SOUTH);
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
			JPanel regressionPanel = createRegressionPanel();
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
		setShowComboPanel2(showComboPanel2);
		setShowDataPanel(showDataPanel);

		setLocationRelativeTo(app.getFrame());

	}


	private void setRegressionLabels(){	
		regressionLabels[REG_NONE] = app.getMenu("None");
		regressionLabels[REG_LINEAR] = app.getMenu("Linear");
		regressionLabels[REG_LOG] = app.getMenu("Log");
		regressionLabels[REG_POLY] = app.getMenu("Polynomial");
		regressionLabels[REG_POW] = app.getMenu("Power");
		regressionLabels[REG_EXP] = app.getMenu("Exponential");
		regressionLabels[REG_SIN] = app.getMenu("Sin");
		regressionLabels[REG_LOGISTIC] = app.getMenu("Logistic");	
	}

	public void setLabels(){


		switch(mode){
		case MODE_ONEVAR:
			setTitle(app.getMenu("OneVariableStatistics"));	
			lblOneVarTitle.setText(app.getMenu("Name") + ": ");
			statisticsHeader.setText(app.getMenu("Statistics"));
			break;
		case MODE_REGRESSION:
			setTitle(app.getMenu("RegressionAnalysis"));	
			regressionLabels = new String[regressionTypes];
			setRegressionLabels();
			lblRegression.setText(app.getMenu("RegressionModel")+ ":");
			lblEqn.setText(app.getMenu("Equation")+ ":");
			lblTitleX.setText(app.getMenu("Column.X") + ": ");
			lblTitleY.setText(app.getMenu("Column.Y") + ": ");
			statisticsHeader.setText(app.getMenu("Statistics"));
			break;

		case MODE_MULTIVAR:
			setTitle(app.getMenu("MultiVariableStatistics"));	
			break;
		}

		btnClose.setText(app.getMenu("Close"));
		btnPrint.setText(app.getMenu("Print"));	
		btnOptions.setText(app.getMenu("Options"));


	}




	private JPanel createRegressionPanel(){

		// components
		String[] orders = {"2","3","4","5","6","7","8","9"};
		cbPolyOrder = new JComboBox(orders);
		cbPolyOrder.setSelectedIndex(0);
		regressionOrder = 2;
		cbPolyOrder.addActionListener(this);

		regressionLabels = new String[regressionTypes];
		setRegressionLabels();
		cbRegression = new JComboBox(regressionLabels);
		cbRegression.addActionListener(this);

		lblTitleX = new JLabel();
		lblTitleY = new JLabel();

		fldTitleX = new MyTextField(app.getGuiManager());
		fldTitleY = new MyTextField(app.getGuiManager());
		fldTitleX.setColumns(15);
		fldTitleY.setColumns(15);

		lblRegression = new JLabel();
		lblRegEquation = new JLabel();
		lblEqn = new JLabel();
		//tfRegression = new JTextField();
		//tfRegression.setColumns(30);



		// panels
		JPanel titleXPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titleXPanel.add(lblTitleX);
		titleXPanel.add(fldTitleX);

		//JPanel titleYPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titleXPanel.add(lblTitleY);
		titleXPanel.add(fldTitleY);

		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.add(titleXPanel, BorderLayout.CENTER);
		//titlePanel.add(titleYPanel, BorderLayout.SOUTH);

		JPanel modelTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		modelTypePanel.add(lblRegression);
		modelTypePanel.add(cbRegression);
		modelTypePanel.add(cbPolyOrder);
		modelTypePanel.add(lblRegEquation);
		//regressionPanel.setBackground(Color.white);

		JPanel eqnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		eqnPanel.add(lblEqn);
	//	eqnPanel.add(lblRegEquation);
		//eqnPanel.setBackground(Color.white);


		//evalPanel.add(lblRegEquation);
	//	JPanel eqnEvalPanel = new JPanel(new BorderLayout());
	//	eqnEvalPanel.add(eqnPanel, BorderLayout.NORTH);


		JScrollPane eqnScroller = new JScrollPane(modelTypePanel);
		eqnScroller.setBorder(BorderFactory.createEmptyBorder());
		JPanel regressionPanel = new JPanel(new BorderLayout());
		//regressionPanel.add(modelTypePanel,BorderLayout.NORTH);
		regressionPanel.add(eqnScroller,BorderLayout.SOUTH);


		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(titlePanel, BorderLayout.NORTH);
		mainPanel.add(regressionPanel, BorderLayout.CENTER);
		//mainPanel.add(evalPanel, BorderLayout.SOUTH);
		//mainPanel.setBackground(Color.white);
		mainPanel.setBorder(BorderFactory.createEtchedBorder());


		return mainPanel;

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
		dialogOptionsPanel= new StatDialogOptionsPanel(app);

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



		dialogOptionsPanel.setShowStats(true);

		dialogOptionsPanel.addPropertyChangeListener("cbShowData", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setShowDataPanel((Boolean) evt.getNewValue());
			}
		});

		dialogOptionsPanel.addPropertyChangeListener("cbShowCombo2", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				setShowComboPanel2((Boolean) evt.getNewValue());
			}
		});

		dialogOptionsPanel.addPropertyChangeListener("cbShowStats", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				//TODO	setShowStats((Boolean) evt.getNewValue());
			}
		});

	}




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


	private void setShowDataPanel(boolean showDataPanel){

		if(mode == MODE_MULTIVAR) return;

		this.showDataPanel = showDataPanel;

		if (showDataPanel) {
			if(statDataPanel == null){
				//Application.debug("splitpane null");
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

		if(source == btnClose){
			setVisible(false);
		}


		else if(source == btnPrint){
			new geogebra.export.PrintPreview(app, this, PageFormat.LANDSCAPE);
		}

		else if(source == cbRegression){
			regressionMode = cbRegression.getSelectedIndex();
			setRegressionModel();
		}

		else if(source == cbPolyOrder){
			regressionOrder = cbPolyOrder.getSelectedIndex() + 2;
			setRegressionModel();
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


	private void setRegressionModel(){

		if(regressionModel != null)
			regressionModel.remove();

		regressionModel = statGeo.createRegressionPlot(dataListSelected, regressionMode, regressionOrder);
		regressionModel.removeView(app.getEuclidianView());
		app.getEuclidianView().remove(regressionModel);
		setRegEquation();
		this.updateAllComboPanels(true);
	}


	public void setRegEquation(){

		// get the LaTeX string for the regression equation 
		String eqn;

		if(regressionMode == StatDialog.REG_NONE){
			eqn = "";}


		else if(regressionMode == REG_LINEAR){

			((GeoLine)regressionModel).setToExplicit();	
			eqn = regressionModel.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, true);

		}else{

			eqn = "y = " + regressionModel.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, true);		
		}

		// create an icon with the LaTeX string	
		ImageIcon icon = (ImageIcon) lblRegEquation.getIcon();

		if(icon == null)
			icon = new ImageIcon();

		if(this.regressionMode == REG_NONE)
			icon = null;
		else
			icon = GeoGebraIcon.createLatexIcon(app, eqn, this.getFont(), false, Color.black, null);


		// set the label icon with our equation string
		lblRegEquation.setIcon(icon);
		lblRegEquation.revalidate();

		updateGUI();
	}


	private void updateGUI(){

		if(isIniting) return;

		if(mode == MODE_REGRESSION)
			cbPolyOrder.setVisible(regressionMode == REG_POLY);	

		repaint();		
	}



	public void updateDialog(){

		//selectedColumns = spreadsheetTable.getSelectedColumnsList();
		removeGeos();
		setDataSource();
		loadDataLists();
		updateAllComboPanels(true);
		if(mode == MODE_ONEVAR){
			fldOneVarTitle.setText(getDataTitles()[0]);
		}
		if(mode == MODE_REGRESSION){
			fldTitleX.setText(getDataTitles()[0]);
			fldTitleY.setText(getDataTitles()[1]);
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
			statList = statGeo.createBasicStatList(dataListSelected, mode,regressionModel);
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
