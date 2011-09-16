package geogebra.gui.view.spreadsheet.statdialog;


import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SpecialNumberFormat;
import geogebra.gui.util.SpecialNumberFormatInterface;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class StatDialog extends JDialog  implements ActionListener, View, Printable,
SpecialNumberFormatInterface {

	// ggb 
	private Application app;
	private Kernel kernel; 
	private SpreadsheetView spView;	
	private StatGeo statGeo;
	private StatDialogController sdc;

	// modes
	public static final int MODE_ONEVAR = 0;
	public static final int MODE_REGRESSION = 1;
	public static final int MODE_MULTIVAR = 2;
	private int mode;


	// flags
	private boolean showDataPanel = false;
	private boolean showStatPanel = false;
	private boolean showComboPanel2 = false;
	protected boolean isIniting;
	protected boolean leftToRight = true;

	// colors
	public static final Color TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;
	public static final Color TABLE_HEADER_COLOR = new Color(240,240,240);   
	public static final Color HISTOGRAM_COLOR = GeoGebraColorConstants.BLUE;    
	public static final Color BOXPLOT_COLOR = GeoGebraColorConstants.CRIMSON;
	public static final Color DOTPLOT_COLOR = GeoGebraColorConstants.GRAY5;    
	public static final Color NQPLOT_COLOR =  GeoGebraColorConstants.GRAY5;    
	public static final Color REGRESSION_COLOR = Color.RED;   
	public static final Color OVERLAY_COLOR = GeoGebraColorConstants.DARKBLUE;    
	
	public static final float opacityBarChart = 0.3f; 
	public static final int thicknessCurve = 4;
	public static final int thicknessBarChart = 3;
	
	
	

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

	// button panel objects
	private JButton btnClose, btnPrint;
	private PopupMenuButton btnOptions;
	private StatDialogOptionsPanel dialogOptionsPanel;

	// main GUI panels 
	protected DataPanel dataPanel;
	protected StatisticsPanel statisticsPanel;
	protected RegressionPanel regressionPanel;
	protected StatComboPanel comboStatPanel, comboStatPanel2;
	private JSplitPane statDataPanel, displayPanel, comboPanelSplit; 
	private JPanel buttonPanel;
	private int defaultDividerSize;

	private Dimension defaultDialogDimension;


	// number format
	private SpecialNumberFormat nf;





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

		nf = new SpecialNumberFormat(app, this);

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
		sdc.updateAllStatPanels(false);

	} 

	/*************************************************  
	 * END StatDialog constructor  */


	private void createGUI(){

		// Create two StatCombo panels with default plots.
		// StatCombo panels display various plots and tables selected by a comboBox.

		switch(mode){

		case MODE_ONEVAR:
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_HISTOGRAM, mode, true);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_BOXPLOT, mode, true);
			break;

		case MODE_REGRESSION:
			//showComboPanel2 = true;
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_SCATTERPLOT, mode, true);
			comboStatPanel2 = new StatComboPanel(this, StatComboPanel.PLOT_RESIDUAL, mode, true);
			break;

		case MODE_MULTIVAR:
			showComboPanel2 = false;
			comboStatPanel = new StatComboPanel(this, StatComboPanel.PLOT_MULTIBOXPLOT, mode, true);
			break;		

		}


		// Create StatisticPanel to displays statistics and inference results
		// from the current data set
		statisticsPanel = new StatisticsPanel(app, this);
		statisticsPanel.setBorder(BorderFactory.createEmptyBorder(4, 2, 2, 2));


		// Create DataPanel to display the current data set(s) and allow
		// temporary editing. 
		if(mode != MODE_MULTIVAR){
			dataPanel = new DataPanel(app, this, sdc.getDataAll(), mode);
			//dataPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			dataPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}



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




	//=================================================
	//       GUI
	//=================================================


	private void initGUI() {

		//===========================================
		// statData panel

		if(mode == MODE_ONEVAR)
			showStatPanel = true;

		if(mode != MODE_MULTIVAR){					
			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, statisticsPanel, null);
			statDataPanel.setResizeWeight(0.5);
			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
		}
		if(mode == MODE_MULTIVAR){			
			statDataPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, statisticsPanel, null);
			statDataPanel.setDividerSize(0);
			statDataPanel.setBorder(BorderFactory.createEmptyBorder());
		}

		// create a splitPane to hold the two plotComboPanels		
		comboPanelSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT, comboStatPanel, comboStatPanel2);

		comboPanelSplit.setDividerLocation(0.5);
		comboPanelSplit.setBorder(BorderFactory.createEmptyBorder());

		// grab the default divider size 
		defaultDividerSize = comboPanelSplit.getDividerSize();



		//===========================================
		// button panel

		btnClose = new JButton();
		btnClose.addActionListener(this);

		createOptionsButton();

		JPanel rightButtonPanel = new JPanel(new FlowLayout());
		rightButtonPanel.add(btnOptions);
		rightButtonPanel.add(btnClose);

		buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

		JPanel plotComboPanel = new JPanel(new BorderLayout());
		plotComboPanel.add(comboPanelSplit, BorderLayout.CENTER);
		if(mode == MODE_ONEVAR){
			JPanel oneVarTitlePanel = createOneVarTitlePanel();
			buttonPanel.add(oneVarTitlePanel, BorderLayout.NORTH);
		}
		if(mode == MODE_REGRESSION){
			regressionPanel = new RegressionPanel(app, this);
			buttonPanel.add(regressionPanel, BorderLayout.NORTH);
		}


		// display panel
		//============================================
		if(mode != MODE_MULTIVAR){
			displayPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
					statDataPanel, plotComboPanel);
			displayPanel.setResizeWeight(0.5);
		}else{
			displayPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
					plotComboPanel, statDataPanel);
			displayPanel.setResizeWeight(1);


		}
		displayPanel.setBorder(BorderFactory.createEmptyBorder());




		// main panel
		//============================================
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(displayPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		getContentPane().add(mainPanel);
		getContentPane().setPreferredSize(defaultDialogDimension);
		setResizable(true);
		pack();

		setShowComboPanel2(showComboPanel2);
		updateStatDataPanelVisibility();
		setLocationRelativeTo(app.getFrame());

	}


	private JPanel createOneVarTitlePanel(){

		lblOneVarTitle = new JLabel();
		fldOneVarTitle = new MyTextField(app);

		JPanel titlePanel = new JPanel(new BorderLayout(5,0));
		titlePanel.add(lblOneVarTitle, BorderLayout.WEST);
		titlePanel.add(fldOneVarTitle, BorderLayout.CENTER);

		//titlePanel.setBorder(BorderFactory.createEtchedBorder());
		titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		return titlePanel;
	}


	private void createOptionsButton(){

		if(btnOptions == null){
			btnOptions = new PopupMenuButton(app);
			btnOptions.setKeepVisible(true);
			btnOptions.setStandardButton(true);
			btnOptions.setFixedIcon(GeoGebraIcon.createUpDownTriangleIcon(false,true));
			btnOptions.setDownwardPopup(false);
		}

		btnOptions.removeAllMenuItems();
		btnOptions.setText(app.getMenu("Options"));

		JCheckBoxMenuItem menuItem;

		// rounding
		btnOptions.addPopupMenuItem(nf.createMenuDecimalPlaces());

		menuItem = new JCheckBoxMenuItem(app.getMenu("ShowData"));
		menuItem.setSelected(showDataPanel);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showDataPanel = !showDataPanel;
				updateStatDataPanelVisibility();
			}
		});
		btnOptions.addPopupMenuItem(menuItem);


		menuItem = new JCheckBoxMenuItem(app.getMenu("ShowStatistics"));
		menuItem.setSelected(showStatPanel);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showStatPanel = !showStatPanel;
				updateStatDataPanelVisibility();
			}
		});
		menuItem.setEnabled(true);
		btnOptions.addPopupMenuItem(menuItem);


		menuItem = new JCheckBoxMenuItem(app.getMenu("ShowPlot2"));
		menuItem.setSelected(showComboPanel2);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setShowComboPanel2(!showComboPanel2);
			}
		});
		btnOptions.addPopupMenuItem(menuItem);
		menuItem.setEnabled(true);

		JMenuItem item = new JMenuItem(app.getMenu("Print")+ "...");
		item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				doPrint();
			}
		});
		btnOptions.addPopupMenuItem(item);

	}





	//======================================
	//    Getters/setters
	//======================================

	public String format(double x){

		return nf.format(x);
	}

	public int getPrintDecimals(){
		return nf.getPrintDecimals();
	}
	
	public int getPrintFigures(){
		return nf.getPrintFigures();
	}

	public StatDialogController getStatDialogController() {
		return sdc;
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


	public void updateStatDataPanelVisibility(){

		if(statDataPanel == null) return;

		if(mode != MODE_MULTIVAR){

			if(showDataPanel){
				if(statDataPanel.getRightComponent() == null){
					statDataPanel.setRightComponent(dataPanel);
					statDataPanel.resetToPreferredSizes();				
				}
			}else{
				if(statDataPanel.getRightComponent() != null){
					statDataPanel.setRightComponent(null);
					statDataPanel.resetToPreferredSizes();
				}
			}

			if(showStatPanel){
				if(statDataPanel.getLeftComponent() == null){
					statDataPanel.setLeftComponent(statisticsPanel);
					statDataPanel.resetToPreferredSizes();
				}
			}else{
				if(statDataPanel.getLeftComponent() != null){
					statDataPanel.setLeftComponent(null);
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

		setLabels();
		updateFonts();

		displayPanel.resetToPreferredSizes();
	}


	private void doPrint(){
		new geogebra.export.PrintPreview(app, this, PageFormat.LANDSCAPE);
	}



	//=================================================
	//      Event Handlers and Updates
	//=================================================

	public void actionPerformed(ActionEvent e) {

		Object source = e.getSource();

		if(source == btnClose){
			setVisible(false);
		}
	
		btnClose.requestFocus();
	}


	/**
	 * Updates the dialog when the number format options have been changed
	 */
	public void changedNumberFormat() {
		updateDialog(false);
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
			if(com instanceof StatPanelInterface){
				((StatPanelInterface)com).updateFonts(font);
			}
			if(com instanceof Container) 
				setFontRecursive((Container) com, font);
		}
	}



	public void setLabels(){

		switch(mode){
		case MODE_ONEVAR:
			setTitle(app.getMenu("OneVariableStatistics"));	
			lblOneVarTitle.setText(app.getMenu("DataTitle") + ": ");
			break;

		case MODE_REGRESSION:
			setTitle(app.getMenu("RegressionAnalysis"));	
			regressionPanel.setLabels();
			break;

		case MODE_MULTIVAR:
			setTitle(app.getMenu("MultiVariableStatistics"));	
			break;
		}

		this.createOptionsButton();

		btnClose.setText(app.getMenu("Close"));
		//	btnPrint.setText(app.getMenu("Print"));	
		//	btnOptions.setText(app.getMenu("Options"));

		// call setLabels() for all child panels
		setLabelsRecursive(this.getContentPane()); 

	}


	public void setLabelsRecursive(Container c) {

		Component[] components = c.getComponents();
		for(Component com : components) {
			if(com instanceof StatPanelInterface){
				//System.out.println(c.getClass().getSimpleName());
				((StatPanelInterface)com).setLabels();
			}
			else if(com instanceof Container) 
				setLabelsRecursive((Container) com);
		}
	}





	//=================================================
	//      View Implementation
	//=================================================


	public void remove(GeoElement geo) {
		//Application.debug("removed geo: " + geo.toString());
		sdc.handleRemovedDataGeo(geo);

	}

	public void update(GeoElement geo) {
		//Application.debug("updated geo:" + geo.toString());
		if (!isIniting && sdc.isInDataSource(geo)) {
			//Application.debug("this geo is in data source: " + geo.toString());
			sdc.updateDialog(false);
		}
	}

	

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}
	
	
	public void add(GeoElement geo) {}
	public void clearView() {}
	public void rename(GeoElement geo) {}
	public void repaintView() {}
	public void updateAuxiliaryObject(GeoElement geo) {}
	public void reset() {}
	public void setMode(int mode) {}


	public void attachView() {
		//clearView();
		//kernel.notifyAddAll(this);
		kernel.attach(this);

		// attachView to plot panels
		comboStatPanel.attachView();
		if(comboStatPanel2 != null)
			comboStatPanel2.attachView();
	}

	public void detachView() {
		kernel.detach(this);
		comboStatPanel.detachView();
		if(comboStatPanel2 != null)
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

	public int getViewID() {
		return Application.VIEW_NONE;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
