package geogebra.gui.view.spreadsheet.statdialog;


import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.view.spreadsheet.CellRange;
import geogebra.gui.view.spreadsheet.CellRangeProcessor;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.gui.view.spreadsheet.RelativeCopy;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToggleButton;

public class StatDialog extends JDialog  implements ActionListener, View, Printable   {

	// ggb 
	private Application app;
	private Kernel kernel; 
	private SpreadsheetView spView;	
	private StatGeo statGeo;
	private StatDialogController sdc;
	

	public StatDialogController getStatDialogController() {
		return sdc;
	}

	// modes
	public static final int MODE_ONEVAR = 0;
	public static final int MODE_REGRESSION = 1;
	public static final int MODE_MULTIVAR = 2;
	private int mode;

	// data collections
	//protected GeoElement geoRegression;
	//protected GeoList dataAll, dataSelected;

	

	// flags
	private boolean showDataPanel = false;
	private boolean showStatPanel = false;
	private boolean showComboPanel2 = false;
	protected boolean isIniting;
	protected boolean leftToRight = true;

	// colors
	public static final Color TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;
	public static final Color TABLE_HEADER_COLOR = new Color(240,240,240);   
	public static final Color HISTOGRAM_COLOR = new Color(0,0,255); // blue with alpha 0.25   
	public static final Color BOXPLOT_COLOR = new Color(204,0,0);  // rose with alpha 0.25 
	public static final Color DOTPLOT_COLOR = new Color(0,204,204); // blue-green   
	public static final Color NQPLOT_COLOR =  new Color(0,0,255); // blue with alpha 0.25     
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
	

	// oneVar title panel objects
	private JLabel lblOneVarTitle;
	private MyTextField fldOneVarTitle;

	// stat and data panel objects
	protected JLabel statisticsHeader;
	protected JPanel statPanel;
	protected DataPanel dataPanel;
	protected StatTable statTable;
	protected RegressionPanel regressionPanel;

	// plot display objects 
	protected StatComboPanel comboStatPanel, comboStatPanel2;;

	// button panel objects
	private JButton btnClose, btnPrint;
	private PopupMenuButton btnOptions;
	private StatDialogOptionsPanel dialogOptionsPanel;

	// main GUI panel objects
	private JSplitPane statDataPanel, displayPanel, comboPanelSplit; 
	private JPanel cardPanel, buttonPanel;
	private int defaultDividerSize;
	private NumberFormat nf;
	private int numDigits = 4;
	private Dimension defaultDialogDimension;


	//=================================
	// getters/setters

	public NumberFormat getNumberFormat() {
		// temporary number format
		//TODO ----- use ggb format?
		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setGroupingUsed(false);
		nf.setMaximumFractionDigits(numDigits);
		// -----------------------
		return nf;
	}
	public void setNumDigits(int numDigits) {
		this.numDigits = numDigits;
	}
	
	
	public GeoElement getRegressionModel() {
		return sdc.getRegressionModel();
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
		sdc.setLeftToRight(leftToRight);
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
		this.kernel = app.getKernel();
		this.spView = spView;
		this.mode = mode;
		
		sdc = new StatDialogController(app, spView, this);
		
		defaultDialogDimension = new Dimension(700,500);
		
		boolean dataOK = sdc.setDataSource();
		if(dataOK){
			//load data from the data source (based on currently selected geos)
			sdc.loadDataLists();
		}else{
			//TODO is dispose needed ?
			//dispose();
			return;  
		}
		
		createGUI();
		
	} 

	/*************************************************  
	 * END StatDialog constructor  */


	private void createGUI(){
		
		GeoList dataAll = sdc.getDataAll();
		GeoList dataSelected = sdc.getDataSelected();
		
		
		// Create two StatCombo panels with default plots.
		// StatCombo panels display various plots and tables
		// selected by a comboBox.
		//================================================
		switch(mode){

		case MODE_ONEVAR:
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_HISTOGRAM, dataSelected, mode, true);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_BOXPLOT, dataSelected, mode, true);
			break;

		case MODE_REGRESSION:
			//showComboPanel2 = true;
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_SCATTERPLOT, dataSelected, mode, true);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_RESIDUAL, dataSelected, mode, true);
			break;

		case MODE_MULTIVAR:
			showComboPanel2 = true;
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_MULTIBOXPLOT, dataSelected, mode, true);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_MULTIVARSTATS, dataSelected, mode, true);
			break;		

		}


		
		// Create a StatPanel to display basic statistics for the current data set
		//================================================
		if(mode == MODE_ONEVAR){
			statTable = new StatTable(app, this, mode);
			statTable.evaluateStatTable(dataSelected);
		}
		
		else if(mode == MODE_REGRESSION){
			statTable = new StatTable(app, this, mode);
			statTable.evaluateStatTable(dataSelected);
		}


		
		// Create a DataPanel.
		// Data panels display the current data set(s) and allow temporary editing. 
		// Edited data is used by the statTable and statCombo panels. 
		//================================================
		if(mode != MODE_MULTIVAR){
			dataPanel = new DataPanel(app, this, dataAll, mode);
			dataPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}


	
		// Init the GUI and attach this view to the kernel
		//================================================
		initGUI();
		updateFonts();
		btnClose.requestFocus();
		attachView();	
		isIniting = false;
		setLabels();
		updateGUI();
		pack();

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

		if(mode != MODE_MULTIVAR){
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

		GeoList dataAll = sdc.getDataAll();
		GeoList dataSelected = sdc.getDataSelected();
		
		switch(mode){
		case MODE_ONEVAR:
			setTitle(app.getMenu("OneVariableStatistics"));	
			lblOneVarTitle.setText(app.getMenu("DataTitle") + ": ");
			statisticsHeader.setText(app.getMenu("Statistics"));
			statTable.evaluateStatTable(dataSelected);
			break;
		case MODE_REGRESSION:
			setTitle(app.getMenu("RegressionAnalysis"));	
			statisticsHeader.setText(app.getMenu("Statistics"));
			regressionPanel.setLabels();
			statTable.evaluateStatTable(dataSelected);
			break;

		case MODE_MULTIVAR:
			setTitle(app.getMenu("MultiVariableStatistics"));	
			break;
		}

		btnClose.setText(app.getMenu("Close"));
		btnPrint.setText(app.getMenu("Print"));	
		btnOptions.setText(app.getMenu("Show"));

		setLabelsRecursive(this.getContentPane()); 
		
	}

	
	public void setLabelsRecursive(Container c) {
		
	    Component[] components = c.getComponents();
	    for(Component com : components) {
	    	if(com instanceof StatPanelInterface){
	    		System.out.println(c.getClass().getSimpleName());
	    		((StatPanelInterface)com).setLabels();
	    	}
	    	else if(com instanceof Container) 
	        	setLabelsRecursive((Container) com);
	    }
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
		
		setLabels();
		updateFonts();
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
			//if(!isIniting)
			//updateDialog();

		}else{
			//Application.printStacktrace("statDialog not visible");
			//spView.setColumnSelect(false);
			sdc.removeStatGeos();		
			this.detachView();		
		}
	}


	
	
	
	
	private void updateGUI(){

		if(isIniting) return;
		if(mode == StatDialog.MODE_ONEVAR){
			fldOneVarTitle.setText(sdc.getDataTitles()[0]);
		}

	}




	public void updateFonts() {

		Font font = app.getPlainFont();
		setFont(font);
		setFontRecursive(this.getContentPane(),font);

	}

	
	public void setFontRecursive(Container c, Font font) {
	    Component[] components = c.getComponents();
	    for(Component com : components) {
	    	com.setFont(font);
	        if(com instanceof Container) 
	            setFontRecursive((Container) com, font);
	    }
	    this.pack();
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
		sdc.handleRemovedDataGeo(geo);

	}

	public void rename(GeoElement geo) {}
	public void repaintView() {}
	public void updateAuxiliaryObject(GeoElement geo) {}
	
	public void reset() {
		//removeGeos();
	}

	public void setMode(int mode) {}

	public void update(GeoElement geo) {
		//Application.debug("------> update:" + geo.toString());
		if (!isIniting && sdc.isInDataSource(geo)) {
			//Application.debug("geo is in data source: " + geo.toString());
			//removeStatGeos();
			//dataSource = null;
			sdc.updateDialog(false);

		}
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


	public void updateDialog(boolean doSetDataSource){
		sdc.updateDialog(doSetDataSource);
	}
	
	public String[] getDataTitles(){
		return sdc.getDataTitles();
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
