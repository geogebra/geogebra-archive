package geogebra.gui;

import geogebra.CommandLineArguments;
import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.app.MyFileFilter;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.layout.Layout;
import geogebra.gui.layout.panels.AlgebraDockPanel;
import geogebra.gui.layout.panels.CasDockPanel;
import geogebra.gui.layout.panels.Euclidian2DockPanel;
import geogebra.gui.layout.panels.EuclidianDockPanel;
import geogebra.gui.layout.panels.EuclidianDockPanelAbstract;
import geogebra.gui.layout.panels.SpreadsheetDockPanel;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.gui.toolbar.Toolbar;
import geogebra.gui.toolbar.ToolbarConfigDialog;
import geogebra.gui.toolbar.ToolbarContainer;
import geogebra.gui.util.BrowserLauncher;
import geogebra.gui.util.GeoGebraFileChooser;
import geogebra.gui.view.algebra.AlgebraController;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.consprotocol.ConstructionProtocol;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.gui.view.spreadsheet.FunctionInspector;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.gui.virtualkeyboard.WindowsUnicodeKeyboard;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;
import geogebra.main.MyError;
import geogebra.main.MyResourceBundle;
import geogebra.util.Unicode;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JApplet;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;


/**
 * Handles all geogebra.gui package related objects and methods for Application.
 * This is done to be able to put class files of geogebra.gui.* packages into a
 * separate gui jar file.
 */
public class GuiManager {	
	private static final int SPREADSHEET_INI_COLS = 26;
	private static final int SPREADSHEET_INI_ROWS = 100;
	
	// Java user interface properties, for translation of JFileChooser
	private ResourceBundle rbJavaUI;

	public Application app;
	private Kernel kernel;
	
	private OptionsDialog optionsDialog;

	protected PropertiesDialog propDialog;
	private ConstructionProtocol constProtocol;
	protected ConstructionProtocolNavigation constProtocolNavigation;

	private AlgebraInput algebraInput;
	private AlgebraController algebraController;
	private AlgebraView algebraView;
	private CasManager casView;
    private SpreadsheetView spreadsheetView; 
    private EuclidianView euclidianView2;

	private GeoGebraFileChooser fileChooser;
	private GeoGebraMenuBar menuBar;

	private ToolbarContainer toolbarPanel;	  
    private String strCustomToolbarDefinition;
    private Locale currentLocale;
    private boolean htmlLoaded;//added by Zbynek Konecny, 2010-05-28 (see #126)    

	private Layout layout;

	private FunctionInspector functionInspector;
	private TextInputDialog textInputDialog;
	
	// Actions
	private AbstractAction showAxesAction, showGridAction, undoAction,
			redoAction;	

	public GuiManager(Application app) {
		this.app = app;
		this.kernel = app.getKernel();
		
		// the layout component
		createLayout();
		
		initAlgebraController(); // needed for keyboard input in EuclidianView
		
		//Zbynek Konecny, 2010-05-28 (see #126)
		htmlLoaded = false;
	}
	
	protected void createLayout(){
		setLayout(new Layout());
	}
	
	protected void setLayout(Layout layout){
		this.layout = layout;
	}
	
	public void initialize() {
		layout.initialize(app);
		initLayoutPanels();
	}
	
	/**
	 * Performs a couple of actions required if the user is switching between frame and applet:
	 *  - Make the title bar visible if the user is using an applet.
	 *  - Active the glass pane if the application is changing from applet to
	 *    frame mode.
	 */
	public void updateLayout() {
		// show / hide title bar if necessary
		// TODO we need to check the user preferences as well for frame mode
		// 		as the application may have no title bars at all
		layout.setTitlebarVisible(!app.isApplet());
		
		// update the glass pane (add it for frame, remove it for applet)
		layout.getDockManager().updateGlassPane();
		
		// we now need to make sure that the relative dimensions of views
		// are kept, therefore we update the dividers
		Dimension oldCenterSize = app.getCenterPanel().getSize();
		Dimension newCenterSize;
		
		// frame -> applet
		if(app.isApplet()) {
			newCenterSize = app.getApplet().getJApplet().getSize();
		}
		
		// applet -> frame
		else {
			// TODO redo this, guessing dimensions is bad
			if(app.getFrame().getPreferredSize().width <= 0) {
				newCenterSize = new Dimension(700, 500);
			} else {
				newCenterSize = app.getFrame().getPreferredSize();
				newCenterSize.width -= 10;
				newCenterSize.height -= 100;
			}
		}
		
		layout.getDockManager().scale(newCenterSize.width / (float)oldCenterSize.width, newCenterSize.height / (float)oldCenterSize.height);
	}
	
	/**
	 * Register panels for the layout manager.
	 */
	protected void initLayoutPanels() {
		// register euclidian view
		layout.registerPanel(newEuclidianDockPanel());
		
		// register spreadsheet view 
		layout.registerPanel(new SpreadsheetDockPanel(app));
		
		// register algebra view
		layout.registerPanel(new AlgebraDockPanel(app));
		
		// register CAS view 
		layout.registerPanel(new CasDockPanel(app));
		
		// register EuclidianView2  
		layout.registerPanel(newEuclidian2DockPanel());	
	}
	
	/**
	 * @return new euclidian view
	 */
	protected EuclidianDockPanel newEuclidianDockPanel(){
		return new EuclidianDockPanel(app,null);
	}
	
	protected Euclidian2DockPanel newEuclidian2DockPanel(){
		return new Euclidian2DockPanel(app,null);
	}
	
	
	public boolean isPropertiesDialogSelectionListener() {
		return app.getCurrentSelectionListener() == propDialog;
	}
	
	public boolean isInputFieldSelectionListener() {
		return app.getCurrentSelectionListener() == algebraInput.getTextField();
	}
	
	  public void clearPreferences() {
	    	if (app.isSaved() || app.saveCurrentFile()) {
	    		app.setWaitCursor();
	    		GeoGebraPreferences.getPref().clearPreferences();
				
				// clear custom toolbar definition
				strCustomToolbarDefinition = null;			
				
				GeoGebraPreferences.getPref().loadXMLPreferences(app); // this will load the default settings
				app.setLanguage(app.getMainComponent().getLocale());
				app.updateContentPaneAndSize();
				app.setDefaultCursor();
				app.setUndoActive(true);
			}
	    }
	  
	public synchronized CasManager getCasView() {
		if (casView == null) {
			casView = new geogebra.cas.view.CASView(app);
		}

		return casView;
	}
	
	public boolean hasCasView() {
		return casView != null;
	}

	public AlgebraView getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = newAlgebraView(algebraController);
			if (!app.isApplet()) {
				// allow drag & drop of files on algebraView
				algebraView.setDropTarget(new DropTarget(algebraView,
						new FileDropTargetListener(app)));
			}
		}

		return algebraView;
	}
	
	/**
	 * 
	 * @param algc
	 * @return new algebra view
	 */
	protected AlgebraView newAlgebraView(AlgebraController algc){
		return new AlgebraView(algc);
	}
	
	public void startEditing(GeoElement geo) {
		((AlgebraView)getAlgebraView()).startEditing(geo, false);
	}
	
	public void setScrollToShow(boolean scrollToShow) {
    	if (spreadsheetView != null) 
    		spreadsheetView.setScrollToShow(scrollToShow);
	}
	
	public void resetSpreadsheet() {
    	if (spreadsheetView != null) 
    		spreadsheetView.restart();
	}
	
	public boolean hasSpreadsheetView() {
		return spreadsheetView != null;
	}
	
	public JComponent getSpreadsheetView() {
		// init spreadsheet view
    	if (spreadsheetView == null) { 
    		spreadsheetView = new SpreadsheetView(app, SPREADSHEET_INI_COLS, SPREADSHEET_INI_ROWS);
    	}
    	
    	return spreadsheetView; 
	}	
	
	public void updateSpreadsheetColumnWidths() {
		if (spreadsheetView != null) { 
			spreadsheetView.updateColumnWidths();
		}
	}
	
	//==========================================
	// G.Sturr 2010-5-12
	// revised spreadsheet tracing code to work with trace manager
	//
	
	public void addSpreadsheetTrace(GeoElement geo){
		if (spreadsheetView != null) 
			spreadsheetView.getTraceManager().addSpreadsheetTraceGeo(geo);
	}
	
	public void removeSpreadsheetTrace(GeoElement geo){
		if (spreadsheetView != null) 
			spreadsheetView.getTraceManager().removeSpreadsheetTraceGeo(geo);
		geo.setSpreadsheetTrace(false);
		geo.setTraceSettings(null);
	}
	
	/** Set a trace manager flag to auto-reset the trace column */
	public void resetTraceColumn(GeoElement geo){
		if (spreadsheetView != null) 
			spreadsheetView.getTraceManager().setNeedsColumnReset(geo, true);
	}
	
	public void startCollectingSpreadsheetTraces() {
		if (spreadsheetView != null) 
			spreadsheetView.getTraceManager().startCollectingSpreadsheetTraces();
	}

	public void stopCollectingSpreadsheetTraces() {
		if (spreadsheetView != null) 
			spreadsheetView.getTraceManager().stopCollectingSpreadsheetTraces();
	}
	
	public void traceToSpreadsheet(GeoElement geo) {
		if (spreadsheetView != null) 
			spreadsheetView.getTraceManager().traceToSpreadsheet(geo);		
	}	
	
	public void getSpreadsheetViewXML(StringBuilder sb) {
		if (spreadsheetView != null)
			spreadsheetView.getXML(sb);
	}
	
	public void getConsProtocolXML(StringBuilder sb) {
	
		if (constProtocol != null)
			sb.append(constProtocol.getConsProtocolXML());
	
		// navigation bar of construction protocol
		if (app.showConsProtNavigation() && constProtocolNavigation != null) {
			sb.append("\t<consProtNavigationBar ");
			sb.append("show=\"");
			sb.append(app.showConsProtNavigation());
			sb.append("\"");
			sb.append(" playButton=\"");
			sb.append(constProtocolNavigation.isPlayButtonVisible());
			sb.append("\"");
			sb.append(" playDelay=\"");
			sb.append(constProtocolNavigation.getPlayDelay());
			sb.append("\"");
			sb.append(" protButton=\"");
			sb.append(constProtocolNavigation.isConsProtButtonVisible());
			sb.append("\"");
			sb.append(" consStep=\"");
			sb.append(kernel.getConstructionStep());
			sb.append("\"");
			sb.append("/>\n");
		}

	}
	
	
	public EuclidianView getEuclidianView2() {
    	if (euclidianView2 == null) {
    		boolean [] showAxis = { true, true };
    		boolean showGrid = false;
    		Application.debug("XXXXX Creating 2nd Euclidian View XXXXX",1);
    		euclidianView2 = new EuclidianView(new EuclidianController(kernel), showAxis, showGrid);
    		euclidianView2.setEuclidianViewNo(2);
    		euclidianView2.setAntialiasing(true);
    		euclidianView2.updateFonts();
    	}
    	return euclidianView2;
	}
	
	public boolean hasEuclidianView2() {
		return euclidianView2 != null;
	}
	
	/**
	 * @todo Do not just use the default euclidian view if no EV has focus, but
	 * determine if maybe just one EV is visible etc. 
	 * 
	 * @return The euclidian view to which new geo elements should be added by
	 * default (if the user uses this mode). This is the focused euclidian
	 * view or the first euclidian view at the moment.
	 */
	public EuclidianViewInterface getActiveEuclidianView() {

		EuclidianDockPanelAbstract focusedEuclidianPanel = layout.getDockManager().getFocusedEuclidianPanel();

		if(focusedEuclidianPanel != null)
			return (EuclidianViewInterface)focusedEuclidianPanel.getComponent();			
		else 
			return app.getEuclidianView();
		
	}
	
	/**
	 * Attach a view which by using the view ID.
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-21
	 * 
	 * @param viewId
	 */
	public void attachView(int viewId) {
		switch(viewId) {
			case Application.VIEW_ALGEBRA:
				attachAlgebraView();
				break;
			case Application.VIEW_SPREADSHEET:
				attachSpreadsheetView();
				break;
			case Application.VIEW_CAS:
				attachCasView();
				break;
		}
	}
	
	/**
	 * Detach a view which by using the view ID.
	 * 
	 * @author Florian Sonner
	 * @version 2008-10-21
	 * 
	 * @param viewId
	 */
	public void detachView(int viewId) {
		switch(viewId) {
			case Application.VIEW_ALGEBRA:
				detachAlgebraView();
				break;
			case Application.VIEW_SPREADSHEET:
				detachSpreadsheetView();
				break;
			case Application.VIEW_CAS:
				detachCasView();
				break;
		}
	}
	
	public void attachSpreadsheetView() {	
		getSpreadsheetView();
		spreadsheetView.attachView();		
	}
	
	public void detachSpreadsheetView(){
		if (spreadsheetView != null)
			spreadsheetView.detachView();		
	}	
	
	public void attachAlgebraView(){	
		getAlgebraView();
		algebraView.attachView();		
	}	
	
	public void detachAlgebraView(){	
		if (algebraView != null)
			algebraView.detachView();		
	}	
	
	public void attachCasView(){	
		getCasView();
		casView.attachView();		
	}	
	
	public void detachCasView(){	
		if (casView != null)
			casView.detachView();		
	}	
	
	public void setShowAuxiliaryObjects(boolean flag) {
		getAlgebraView();
		algebraView.setShowAuxiliaryObjects(flag);
	}

	
	private void initAlgebraController() {
		if (algebraController == null) {
			algebraController = new AlgebraController(app.getKernel());			
		}
	}

	public JComponent getAlgebraInput() {
		if (algebraInput == null)
			algebraInput = new AlgebraInput(app);

		return algebraInput;
	}
	
	public JTextComponent getAlgebraInputTextField() {
		getAlgebraInput();
		return algebraInput.getTextField();
	}
	
	public void updateAlgebraInput() {
		if (algebraInput != null)
			algebraInput.initGUI();
	}

	public synchronized void initPropertiesDialog() {
		if (propDialog == null) {
			propDialog = new PropertiesDialog(app);
		}
	}
	
	public synchronized void reinitPropertiesDialog() {
		propDialog = null;
		System.gc();
		propDialog = new PropertiesDialog(app);
		
	}

	public void doAfterRedefine(GeoElement geo) {
		// select geoElement with label again
		if (propDialog != null && propDialog.isShowing()) {
			// propDialog.setViewActive(true);
			propDialog.geoElementSelected(geo, false);
		}
		
		// G.Sturr 2010-6-28
		// if a tracing geo has been redefined, then put it back into the traceGeoCollection
		if(geo.getSpreadsheetTrace()){
			addSpreadsheetTrace(geo);
		}
	}
	
	public Layout getLayout(){
		return layout;
	}

	public ToolbarContainer getToolbarPanel() {
		if (toolbarPanel == null) {
			toolbarPanel = new ToolbarContainer(app, true);
		}

		return toolbarPanel;
	}
	
	public void updateToolbar() {
		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
		}
		
		if(layout != null) {
			layout.getDockManager().updateToolbars();
		}
	}
	
	public void setShowView(boolean flag, int viewId) {
		if(flag) {
			layout.getDockManager().show(viewId);
			
			if(viewId == Application.VIEW_SPREADSHEET) {
				getSpreadsheetView().requestFocus();
			}
		} else {
			layout.getDockManager().hide(viewId);
			
			if(viewId == Application.VIEW_SPREADSHEET) {
				app.getEuclidianView().requestFocus();
			}
		}
	}
	
	public boolean showView(int viewId) {
		try {
			return layout.getDockManager().getPanel(viewId).isVisible();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setShowToolBarHelp(boolean flag) {
		ToolbarContainer.setShowHelp(flag);
	}

	public JComponent getConstructionProtocolNavigation() {
		if (constProtocolNavigation == null) {
			getConstructionProtocol();
			constProtocolNavigation = new ConstructionProtocolNavigation(constProtocol);
		}

		return constProtocolNavigation;
	}
	
	public void setShowConstructionProtocolNavigation(boolean show) {
		if (show) {
			if (app.getEuclidianView() != null)
				app.getEuclidianView().resetMode();
			getConstructionProtocolNavigation();
			constProtocolNavigation.register();
		} else {
			if (constProtocolNavigation != null)
				constProtocolNavigation.unregister();
		}
	}
	
	public void setShowConstructionProtocolNavigation(boolean show, 
			boolean playButton, double playDelay, boolean showProtButton) 
	{
		setShowConstructionProtocolNavigation(show);
		
		if (constProtocolNavigation != null) {
			constProtocolNavigation.setPlayButtonVisible(playButton);
			constProtocolNavigation.setPlayDelay(playDelay);
			constProtocolNavigation.setConsProtButtonVisible(showProtButton);
		}

	}

	public boolean isConsProtNavigationPlayButtonVisible() {
		if (constProtocolNavigation != null)
			return constProtocolNavigation.isPlayButtonVisible();
		else
			return true;
	}

	public boolean isConsProtNavigationProtButtonVisible() {
		if (constProtocolNavigation != null)
			return constProtocolNavigation.isConsProtButtonVisible();
		else
			return true;
	}

	/**
	 * Displays the construction protocol dialog
	 */
	public void showConstructionProtocol() {
		app.getEuclidianView().resetMode();
		getConstructionProtocol();
		constProtocol.setVisible(true);
	}

	/**
	 * Displays the construction protocol dialog
	 */
	public void hideConstructionProtocol() {
		if (constProtocol == null) return;
		app.getEuclidianView().resetMode();
		constProtocol.setVisible(false);
	}

	/**
	 * returns whether the construction protocol is visible
	 */
	public boolean isConstructionProtocolVisible() {
		if (constProtocol == null) return false;
		return constProtocol.isVisible();
	}

	public JDialog getConstructionProtocol() {
		if (constProtocol == null) {		
			constProtocol = new ConstructionProtocol(app);
		}
		return constProtocol;
	}
	
	public void setConstructionStep(int step) {
		if (constProtocol != null) 
			constProtocol.setConstructionStep(step);
	}
	
	public void updateConstructionProtocol() {
		if (constProtocol != null)
			constProtocol.update();
	}
	
	public boolean isUsingConstructionProtocol() {
		return constProtocol != null;
	}
	                                              

	public int getToolBarHeight() {
		if (app.showToolBar() && toolbarPanel != null)
			return toolbarPanel.getHeight();
		else
			return 0;
	}

	public String getDefaultToolbarString() {
		if (toolbarPanel == null)
			return "";

		return getGeneralToolbar().getDefaultToolbarString();
	}

	public void updateFonts() {
		if (algebraView != null)
			algebraView.updateFonts();
		if (spreadsheetView != null)
			spreadsheetView.updateFonts();
		if (algebraInput != null)
			algebraInput.updateFonts();	

		if (fileChooser != null) {
			fileChooser.setFont(app.getPlainFont());
			SwingUtilities.updateComponentTreeUI(fileChooser);
		}
		
		if(optionsDialog != null) {
			SwingUtilities.updateComponentTreeUI(optionsDialog);
		}

		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
		}
		
		if (menuBar != null) {
			menuBar.initMenubar();
		}

		if (propDialog != null)
			// changed to force all panels to be updated
			reinitPropertiesDialog();  //was propDialog.initGUI();
			
		if (constProtocol != null)
			constProtocol.initGUI();
		if (constProtocolNavigation != null)
			constProtocolNavigation.initGUI();
		
		if (casView != null)
			casView.updateFonts();
		
		if(layout.getDockManager() != null)
			layout.getDockManager().updateFonts();
		
		if(functionInspector != null)
			functionInspector.updateFonts();
			
		SwingUtilities.updateComponentTreeUI(app.getMainComponent());			
	}

	public void setLabels() {
		// reinit actions to update labels
		showAxesAction = null;
		initActions();

		if (app.showMenuBar()) {
			initMenubar();
			Component comp = app.getMainComponent();
			if (comp instanceof JApplet)
				((JApplet) comp).setJMenuBar((JMenuBar) menuBar);
			else if (comp instanceof JFrame)
				((JFrame) comp).setJMenuBar((JMenuBar) menuBar);
		}

		// update views
		if (algebraView != null)
			algebraView.setLabels();
		if (algebraInput != null)
			algebraInput.setLabels();

		if(app.getEuclidianView() != null)
			app.getEuclidianView().getStyleBar().setLabels();
			
		if(getEuclidianView2() != null)
			getEuclidianView2().getStyleBar().setLabels();
		
		
		if (spreadsheetView != null)
			spreadsheetView.setLabels();
		
		if (toolbarPanel != null) {
			toolbarPanel.buildGui();
			toolbarPanel.updateHelpText();
		}
		
		if (propDialog != null)
			// changed to force all language strings to be updated
			reinitPropertiesDialog();  //was propDialog.initGUI();
			
		if (constProtocol != null)
			constProtocol.initGUI();
		if (constProtocolNavigation != null)
			constProtocolNavigation.setLabels();
		if (fileChooser != null)
			updateJavaUILanguage();
		if (optionsDialog != null)
			optionsDialog.setLabels();
		
		if (virtualKeyboard != null)
			virtualKeyboard.setLabels();
			
		if(functionInspector != null)
			functionInspector.setLabels();

		if(textInputDialog != null)
			textInputDialog.setLabels();
		
		layout.getDockManager().setLabels();			
	}

	public void initMenubar() {
		if (menuBar == null) {
			menuBar = new GeoGebraMenuBar(app, layout);
		}
		//((GeoGebraMenuBar) menuBar).setFont(app.getPlainFont());
		menuBar.initMenubar();
	}
	
	public void updateMenubar() {
		if (menuBar != null) 
			menuBar.updateMenubar();
	}
	
	public void updateMenubarSelection(){
		if (menuBar != null) 
			menuBar.updateSelection();
	}
	
	public void updateMenuWindow(){
		if (menuBar != null) 
			menuBar.updateMenuWindow();
	}
	
	public void updateMenuFile(){
		if (menuBar != null) 
			menuBar.updateMenuFile();
	}
	
	public JMenuBar getMenuBar() {
		return (JMenuBar) menuBar;
	}

	public void setMenubar(JMenuBar newMenuBar) {
		menuBar = (GeoGebraMenuBar) newMenuBar;
	}

	public void showAboutDialog() {
		GeoGebraMenuBar.showAboutDialog(app);
	}

	public void showPrintPreview() {
		GeoGebraMenuBar.showPrintPreview(app);
	}
	
	ContextMenuGraphicsWindow drawingPadpopupMenu;

	/**
	 * Displays the Graphics View menu at the position p in the coordinate space of
	 * euclidianView
	 */
	public void showDrawingPadPopup(Component invoker, Point p) {
		// clear highlighting and selections in views		
		app.getEuclidianView().resetMode();
		
		
		// menu for drawing pane context menu
		drawingPadpopupMenu = new ContextMenuGraphicsWindow(
				app, p.x, p.y);
		drawingPadpopupMenu.show(invoker, p.x, p.y);
	}

	/**
	 * Toggles the Graphics View menu at the position p in the coordinate space of
	 * euclidianView
	 */
	public void toggleDrawingPadPopup(Component invoker, Point p) {
		if (drawingPadpopupMenu == null || !drawingPadpopupMenu.isVisible()) {
			showDrawingPadPopup(invoker, p);
			return;
		}
		
		drawingPadpopupMenu.setVisible(false);
	}

	
	ContextMenuGeoElement popupMenu;

	/**
	 * Displays the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 */
	public void showPopupMenu(ArrayList<GeoElement> geos, Component invoker, Point p) {
		if (geos == null || !app.letShowPopupMenu())
			return;
		
		if (app.getKernel().isAxis(geos.get(0))) {
			showDrawingPadPopup(invoker, p);
		} else {
			// clear highlighting and selections in views
			app.getEuclidianView().resetMode();
			
			Point screenPos = (invoker == null) ? new Point(0,0) : invoker.getLocationOnScreen();
			screenPos.translate(p.x, p.y);
	
			
			popupMenu = new ContextMenuGeoElement(app, geos,
					screenPos);
			popupMenu.show(invoker, p.x, p.y);
		}
	
	}
	
	/**
	 * Toggles the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 */
	public void togglePopupMenu(ArrayList<GeoElement> geos, Component invoker, Point p) {
		if (popupMenu == null || !popupMenu.isVisible()) {
			showPopupMenu(geos, invoker, p);
			return;
		}
		
		popupMenu.setVisible(false);
	
	}
	
	/**
	 * Displays the options dialog.
	 *
	 * @param tabIndex Index of the tab. Use OptionsDialog.TAB_* constants for this, or -1 for the default.
	 */
	public void showOptionsDialog(int tabIndex)	{
		if(optionsDialog == null)
			optionsDialog = new OptionsDialog(app);
		else
			optionsDialog.updateGUI();
		
		if(tabIndex != -1)
			optionsDialog.showTab(tabIndex);
		
		optionsDialog.setVisible(true);
	}

	/**
	 * Displays the properties dialog for geos
	 */
	public void showPropertiesDialog(ArrayList geos) {
		if (!app.letShowPropertiesDialog())
			return;
		
	
		// save the geos list: it will be cleared by setMoveMode()
		ArrayList selGeos = null;
		if (geos == null)
			geos = app.getSelectedGeos();

		if (geos != null) {
			tempGeos.clear();
			tempGeos.addAll(geos);
			selGeos = tempGeos;
		}

		app.setMoveMode();
		app.setWaitCursor();

		// open properties dialog
		initPropertiesDialog();
		propDialog.setVisibleWithGeos(selGeos);

		// double-click on slider -> open properties at slider tab
		if (geos != null && geos.size() == 1  && ((GeoElement)geos.get(0)).isEuclidianVisible() && geos.get(0) instanceof GeoNumeric )
		  propDialog.showSliderTab();
		
		app.setDefaultCursor();
	}

	private ArrayList tempGeos = new ArrayList();

	public void showPropertiesDialog() {
		showPropertiesDialog(null);
	}

	/**

	 * Displays the porperties dialog for the drawing pad
	 */
	public void showDrawingPadPropertiesDialog() {
		if (!app.letShowPropertiesDialog())
			return;
		app.setWaitCursor();
		app.getEuclidianView().resetMode();
		PropertiesDialogGraphicsWindow euclidianViewDialog = new PropertiesDialogGraphicsWindow(
				app, app.getEuclidianView());
		euclidianViewDialog.setVisible(true);
		app.setDefaultCursor();
	}

	/**
	 * Displays the configuration dialog for the toolbar
	 */
	public void showToolbarConfigDialog() {
		app.getEuclidianView().resetMode();
		ToolbarConfigDialog dialog = new ToolbarConfigDialog(app);
		dialog.setVisible(true);
	}

	/**
	 * Displays the rename dialog for geo
	 */
	public void showRenameDialog(GeoElement geo, boolean storeUndo,
			String initText, boolean selectInitText) {
		if (!app.isRightClickEnabled())
			return;

		geo.setLabelVisible(true);
		geo.updateRepaint();

		InputHandler handler = new RenameInputHandler(app, geo, storeUndo);

		// Michael Borcherds 2008-03-25
		// a Chinese friendly version
		InputDialog id = new InputDialog(app, "<html>"
				+ app.getPlain("NewNameForA", "<b>" + geo.getNameDescription()
						+ "</b>") + // eg New name for <b>Segment a</b>
				"</html>", app.getPlain("Rename"), initText, false, handler,
				false, selectInitText, null);

		/*
		 * InputDialog id = new InputDialog( this, "<html>" +
		 * app.getPlain("NewName") + " " + app.getPlain("for") + " <b>" +
		 * geo.getNameDescription() + "</b></html>", app.getPlain("Rename"),
		 * initText, false, handler, true, selectInitText);
		 */

		id.setVisible(true);
	}

	/**
	 * Displays the text dialog for a given text.
	 */
	public void showTextDialog(GeoText text) {
		showTextDialog(text, null);
	}

	/**
	 * Creates a new text at given startPoint
	 */
	public void showTextCreationDialog(GeoPoint startPoint) {
		showTextDialog(null, startPoint);
	}

	

	private void showTextDialog(GeoText text, GeoPoint startPoint) {
		app.setWaitCursor();

		if(textInputDialog == null)
			textInputDialog = (TextInputDialog) createTextDialog(text, startPoint);
		else
			((TextInputDialog)textInputDialog).reInitEditor(text,startPoint);

		textInputDialog.setVisible(true);
		app.setDefaultCursor();
	}

	public JDialog createTextDialog(GeoText text, GeoPoint startPoint) {
		boolean isTextMode = app.getMode() == EuclidianConstants.MODE_TEXT;
		TextInputDialog id = new TextInputDialog(app, app.getPlain("Text"),
				text, startPoint, 30, 6, isTextMode);
		return id;
	}

	/**
	 * Displays the redefine dialog for geo
	 * 
	 * @param allowTextDialog: whether text dialog should be used for texts
	 */
	public void showRedefineDialog(GeoElement geo, boolean allowTextDialog) {
		// doBeforeRedefine();

		if (allowTextDialog && geo.isGeoText() && !geo.isTextCommand()) {
			showTextDialog((GeoText) geo);
			return;
		}

		// Michael Borcherds 2007-12-31 BEGIN
		// InputHandler handler = new RedefineInputHandler(this, geo);
		//String str = geo.isIndependent() ? geo.toValueString() : geo
		//		.getCommandDescription();
		
		String str = geo.getRedefineString(false, true);
		
		InputHandler handler = new RedefineInputHandler(app, geo, str);
		// Michael Borcherds 2007-12-31 END
		/*
		 * String str = initSB.toString(); // add label to make renaming
		 * possible too if (str.indexOf('=') == -1) { // no equal sign in
		 * definition string // functions need either "f(x) =" or "f =" if
		 * (!geo.isGeoFunction()) initSB.insert(0, geo.getLabel() + " = "); else
		 * if (str.indexOf('[') == -1) // no command initSB.insert(0,
		 * geo.getLabel() + "(x) = "); } else { // make sure that initSB does
		 * not already contain the label, // e.g. like for functions: f(x) = a
		 * x^2 if (!str.startsWith(geo.getLabel())) { initSB.insert(0,
		 * geo.getLabel() + ": "); } }
		 */

		InputDialog id = new InputDialog(app, geo.getNameDescription(), app.getPlain("Redefine"), str, true, handler, geo);
		id.showSymbolTablePopup(true);
		id.setVisible(true);
		//id.selectText();
	}

	/**
	 * Creates a new slider at given location (screen coords).
	 * 
	 * @return whether a new slider (number) was create or not
	 */
	public boolean showSliderCreationDialog(int x, int y) {
		app.setWaitCursor();
		
		SliderDialog dialog = new SliderDialog(app, x, y);
		dialog.setVisible(true);
		/*
		GeoNumeric num = (GeoNumeric) dialog.getResult();
		Application.debug("finish");
		if (num != null) {
			// make sure that we show name and value of slider
			num.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			num.setLabelVisible(true);
			num.update();
		}*/
		
		app.setDefaultCursor();
		
		return true;//num != null;
	}

	/**
	 * Creates a new JavaScript button at given location (screen coords).
	 * 
	 * @return whether a new slider (number) was create or not
	 */
	public boolean showButtonCreationDialog(int x, int y, boolean textfield) {
		ButtonDialog dialog = new ButtonDialog(app, x, y, textfield);
		dialog.setVisible(true);
		//GeoJavaScriptButton button = (GeoJavaScriptButton) dialog.getResult();
		//Application.debug("finish");
		//	if (button != null) {
		//	// make sure that we show name and value of slider
		//	button.setLabelMode(GeoElement.LABEL_NAME_VALUE);
		//	button.setLabelVisible(true);
		//	button.update();
		//}
		return true;//button != null;
	}

	/**
	 * Creates a new image at the given location (real world coords).
	 * 
	 * @return whether a new image was create or not
	 */
	public boolean loadImage(GeoPoint loc, boolean fromClipboard) {
		app.setWaitCursor();
		
		String fileName;
		if (fromClipboard)
			fileName = getImageFromClipboard();
		else
			fileName = getImageFromFile();

		boolean ret;
		if (fileName == null) {
			ret = false;
		}
		else {
			// create GeoImage object for this fileName
			GeoImage geoImage = new GeoImage(app.getKernel().getConstruction());
			geoImage.setImageFileName(fileName);
			geoImage.setCorner(loc, 0);
			geoImage.setLabel(null);
	
			GeoImage.updateInstances();
			ret = true;
		}
		
		app.setDefaultCursor();
		return ret;
	}
	
	
	/**
	 * Shows the function inspector dialog. If none exists, a new inspector is
	 * created.
	 */
	public boolean showFunctionInspector(GeoElement function){
		boolean success = true;

		try {
			if(functionInspector == null){
				 functionInspector = new FunctionInspector(app,function);
			}else{
				functionInspector.insertGeoElement(function);
			}
			functionInspector.setVisible(true);
			 
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		}
		return success;

	}

	public Color showColorChooser(Color currentColor) {
		// there seems to be a bug concerning ToolTips in JColorChooser
		// so we turn off ToolTips
		// ToolTipManager.sharedInstance().setEnabled(false);
		try {
			Color newColor = JColorChooser.showDialog(null, 
					app.getPlain("ChooseColor"), currentColor);
			// ToolTipManager.sharedInstance().setEnabled(true);
			return newColor;
		} catch (Exception e) {
			// ToolTipManager.sharedInstance().setEnabled(true);
			return null;
		}
	}

	/**
	 * gets String from clipboard Michael Borcherds 2008-04-09
	 */
	public String getStringFromClipboard() {
		String selection = null;

		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transfer = clip.getContents(null);

		try {
			if (transfer.isDataFlavorSupported(DataFlavor.stringFlavor))
				selection = (String) transfer
						.getTransferData(DataFlavor.stringFlavor);
			else if (transfer.isDataFlavorSupported(DataFlavor.plainTextFlavor)) {
				StringBuilder sbuf = new StringBuilder();
				InputStreamReader reader;
				char readBuf[] = new char[1024 * 64];
				int numChars;

				reader = new InputStreamReader((InputStream) transfer
						.getTransferData(DataFlavor.plainTextFlavor), "UNICODE");

				while (true) {
					numChars = reader.read(readBuf);
					if (numChars == -1)
						break;
					sbuf.append(readBuf, 0, numChars);
				}

				selection = new String(sbuf);
			}
		} catch (Exception e) {
		}

		return selection;
	}

	/**
	 * gets an image from the clipboard Then the image file is loaded and stored
	 * in this application's imageManager. Michael Borcherds 2008-05-10
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String getImageFromClipboard() {

		BufferedImage img = null;
		String fileName = null;
		try {
			app.setWaitCursor();

			// if (fromClipboard)
			{

				Clipboard clip = Toolkit.getDefaultToolkit()
						.getSystemClipboard();
				Transferable transfer = clip.getContents(null);

				try {
					if (transfer.isDataFlavorSupported(DataFlavor.imageFlavor)) {
						img = (BufferedImage) transfer
								.getTransferData(DataFlavor.imageFlavor);
					}
				} catch (UnsupportedFlavorException ufe) {
					app.showError("PasteImageFailed");
					return null;
					// ufe.printStackTrace();
				} catch (IOException ioe) {
					app.showError("PasteImageFailed");
					return null;
					// ioe.printStackTrace();
				}

				if (img == null) {
					app.showError("PasteImageFailed");
					return null;
				}

				fileName = "clipboard.png"; // extension determines what format
				// it will be in ggb file
			}
		} catch (Exception e) {
			app.setDefaultCursor();
			e.printStackTrace();
			app.showError("PasteImageFailed");
			return null;
		}

		return app.createImage(img, fileName);

	}

	public synchronized void initFileChooser() {
		if (fileChooser == null) {
			try {
				fileChooser = new GeoGebraFileChooser(app, app.getCurrentImagePath()); // non-restricted
				// Added for Intergeo File Format (Yves Kreis) -->
				fileChooser.addPropertyChangeListener(
						JFileChooser.FILE_FILTER_CHANGED_PROPERTY,
						new FileFilterChangedListener());
				// <-- Added for Intergeo File Format (Yves Kreis)
			} catch (Exception e) { 
				// fix for  java.io.IOException: Could not get shell folder ID list
				// Java bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6544857
				Application.debug("Error creating GeoGebraFileChooser - using fallback option");
				fileChooser = new GeoGebraFileChooser(app, app.getCurrentImagePath(), true); // restricted version		
			} 
					
			updateJavaUILanguage();
		}
	}
	
	/**
	 * Loads java-ui.properties and sets all key-value pairs
	 * using UIManager.put(). This is needed to translate JFileChooser to
	 * languages not supported by Java natively.
	 */
	private void updateJavaUILanguage() {	
		// load properties jar file
		if (currentLocale == app.getLocale())
			return;		
		
		// update locale
		currentLocale = app.getLocale();			
		
		// load javaui properties file for specific locale
		//next two lines edited by Zbynek Konecny 2010-04-23 to avoid false exception message
		String underscoreLocale = "en".equals(currentLocale.getLanguage()) ? "" : "_"+currentLocale;
		rbJavaUI = MyResourceBundle.loadSingleBundleFile(Application.RB_JAVA_UI + underscoreLocale);		
		boolean foundLocaleFile = rbJavaUI != null;
		if (!foundLocaleFile) 
			// fall back on English
			rbJavaUI = MyResourceBundle.loadSingleBundleFile(Application.RB_JAVA_UI);
		
		// set or delete all keys in UIManager
		Enumeration<String> keys = rbJavaUI.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = foundLocaleFile ? rbJavaUI.getString(key) : null;
			
			// set or delete UIManager key entry (set values to null when locale file not found)
			UIManager.put(key, value);										
		}	
		
		// update file chooser
		if (fileChooser != null) {
			fileChooser.setLocale(currentLocale);
			SwingUtilities.updateComponentTreeUI(fileChooser);
		}
	}

	/**
	 * Shows a file open dialog to choose an image file, Then the image file is
	 * loaded and stored in this application's imageManager.
	 * 
	 * @return fileName of image stored in imageManager
	 */
	public String getImageFromFile() {

		BufferedImage img = null;
		String fileName = null;
		try {
			app.setWaitCursor();
			// else
			{

				initFileChooser();
				fileChooser.setMode(GeoGebraFileChooser.MODE_IMAGES);
				fileChooser.setCurrentDirectory(app.getCurrentImagePath());
				
				MyFileFilter fileFilter = new MyFileFilter();
				fileFilter.addExtension("jpg");
				fileFilter.addExtension("jpeg");
				fileFilter.addExtension("png");
				fileFilter.addExtension("gif");
				fileFilter.addExtension("tif");
				if (Util.getJavaVersion() >= 1.5)
					fileFilter.addExtension("bmp");
				fileFilter.setDescription(app.getPlain("Image"));
				fileChooser.resetChoosableFileFilters();
				fileChooser.setFileFilter(fileFilter);

				File imageFile = null;
				int returnVal = fileChooser.showOpenDialog(app.getMainComponent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					imageFile = fileChooser.getSelectedFile();
					if (imageFile != null) {
						app.setCurrentImagePath(imageFile.getParentFile());
						if (!app.isApplet()) {
							GeoGebraPreferences.getPref().
									saveDefaultImagePath(app.getCurrentImagePath());
						}
					}
				}

				if (imageFile == null) {
					app.setDefaultCursor();
					return null;
				}

				// get file name
				fileName = imageFile.getCanonicalPath();

				// load image
				img = ImageIO.read(imageFile);
			}

			return app.createImage(img, fileName);

		} catch (Exception e) {
			app.setDefaultCursor();
			e.printStackTrace();
			app.showError("LoadFileFailed");
			return null;
		}

	}

	
	/**
	 * Opens file chooser and returns a data file for the spreadsheet
	 * G.Sturr 2010-2-5
	 */
	public File getDataFile() {
		
		//TODO -- create MODE_DATA that shows preview of text file (or no preview?)

		File dataFile = null;

		try {
			app.setWaitCursor();
			initFileChooser();
			fileChooser.setMode(GeoGebraFileChooser.MODE_DATA);
			fileChooser.setCurrentDirectory(app.getCurrentImagePath());

			MyFileFilter fileFilter = new MyFileFilter();
			fileFilter.addExtension("txt");
			fileFilter.addExtension("csv");
			fileFilter.addExtension("dat");

			// fileFilter.setDescription(app.getPlain("Image"));
			fileChooser.resetChoosableFileFilters();
			fileChooser.setFileFilter(fileFilter);

			int returnVal = fileChooser.showOpenDialog(app.getMainComponent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dataFile = fileChooser.getSelectedFile();
				if (dataFile != null) {
					app.setCurrentImagePath(dataFile.getParentFile());
					if (!app.isApplet()) {
						GeoGebraPreferences.getPref().saveDefaultImagePath(
								app.getCurrentImagePath());
					}
				}
			}

		} catch (Exception e) {
			app.setDefaultCursor();
			e.printStackTrace();
			app.showError("LoadFileFailed");
			return null;
		}

		app.setDefaultCursor();
		return dataFile;

	}
	
	
	
	
	
	  // returns true for YES or NO and false for CANCEL
    public boolean saveCurrentFile() {    	
    	if (propDialog != null && propDialog.isShowing()) 
    		propDialog.cancel();
    	app.getEuclidianView().reset();
    	
    	// use null component for iconified frame
    	Component comp = app.getMainComponent();
    	if (app.getFrame() instanceof GeoGebraFrame) {
    		GeoGebraFrame frame = (GeoGebraFrame) app.getFrame();
    		comp = frame != null && !frame.isIconified() ? frame : null;
    	}
    	
    	// Michael Borcherds 2008-05-04
    	Object[] options = { app.getMenu("Save"), app.getMenu("DontSave"), app.getMenu("Cancel") };
    	int	returnVal=    
    			JOptionPane.showOptionDialog(comp, app.getMenu("DoYouWantToSaveYourChanges"), app.getMenu("CloseFile"),
             JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,

             null, options, options[0]);     

/*    	
        int returnVal =
            JOptionPane.showConfirmDialog(
            		comp,
                getMenu("SaveCurrentFileQuestion"),
                app.getPlain("ApplicationName") + " - " + app.getPlain("Question"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);*/

        switch (returnVal) {
            case 0 :
                return save();

            case 1 :
                return true;

            default : 
                return false;
        }
    }
    

    public boolean save() {
    	//app.getFrame().getJMenuBar()
    	app.setWaitCursor();
    	
    	// close properties dialog if open
    	closeOpenDialogs();
    		    	
    	boolean success = false;
        if (app.getCurrentFile() != null){
        	// Mathieu Blossier - 2008-01-04
        	// if the file is read-only, open save as        	
			if (!app.getCurrentFile().canWrite()){
				success = saveAs();
			} else {
				success = app.saveGeoGebraFile(app.getCurrentFile());
			}
        }
		else {
			success = saveAs();
		}
        
        app.setDefaultCursor();
        return success;
    }
	
	public boolean saveAs() {
		
		// Mathieu Blossier - 2008-01-04
		// if the file is hidden, set current file to null
		if (app.getCurrentFile() != null){
			if (!app.getCurrentFile().canWrite() && app.getCurrentFile().isHidden()){
				app.setCurrentFile(null);
				app.setCurrentPath(null);
			}
		}
		
		// Added for Intergeo File Format (Yves Kreis) -->
		String[] fileExtensions;
		String[] fileDescriptions;
		if (GeoGebra.DISABLE_I2G) {
			fileExtensions = new String[] { Application.FILE_EXT_GEOGEBRA };
			fileDescriptions = new String[] { app.getPlain("ApplicationName")
					+ " " + app.getMenu("Files") };
		} else {
			fileExtensions = new String[] {
					Application.FILE_EXT_GEOGEBRA,
					Application.FILE_EXT_INTERGEO };
			fileDescriptions = new String[] {
					app.getPlain("ApplicationName") + " "
							+ app.getMenu("Files"),
					"Intergeo " + app.getMenu("Files") + " [Version "
							+ GeoGebra.I2G_FILE_FORMAT + "]" };
		}
		// <-- Added for Intergeo File Format (Yves Kreis)
		File file = showSaveDialog(
		// Modified for Intergeo File Format (Yves Kreis) -->
				// Application.FILE_EXT_GEOGEBRA, currentFile,
				// app.getPlain("ApplicationName") + " " + app.getMenu("Files"));
				fileExtensions, app.getCurrentFile(), fileDescriptions, true, false);
		// <-- Modified for Intergeo File Format (Yves Kreis)
		if (file == null)
			return false;

		boolean success = app.saveGeoGebraFile(file);
		if (success)
			app.setCurrentFile(file);
		return success;
	}
	
	   

	public File showSaveDialog(String fileExtension, File selectedFile,
			String fileDescription, boolean promptOverwrite, boolean dirsOnly) {
		// Added for Intergeo File Format (Yves Kreis) -->
		String[] fileExtensions = { fileExtension };
		String[] fileDescriptions = { fileDescription };
		return showSaveDialog(fileExtensions, selectedFile, fileDescriptions, promptOverwrite,
				dirsOnly);
	}

	public File showSaveDialog(String[] fileExtensions, File selectedFile,
			String[] fileDescriptions, boolean promptOverwrite, boolean dirsOnly) {
		// <-- Added for Intergeo File Format (Yves Kreis)
		boolean done = false;
		File file = null;

		// Added for Intergeo File Format (Yves Kreis) -->
		if (fileExtensions == null || fileExtensions.length == 0
				|| fileDescriptions == null) {
			return null;
		}
		String fileExtension = fileExtensions[0];
		// <-- Added for Intergeo File Format (Yves Kreis)

		initFileChooser();
		fileChooser.setMode(GeoGebraFileChooser.MODE_GEOGEBRA_SAVE);
		fileChooser.setCurrentDirectory(app.getCurrentPath());
		
		if (dirsOnly)
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		// set selected file
		// Modified for Intergeo File Format (Yves Kreis) -->
		/*
		 * if (selectedFile == null) { selectedFile =
		 * removeExtension(fileChooser.getSelectedFile()); }
		 */
		// <-- Modified for Intergeo File Format (Yves Kreis)
		if (selectedFile != null) {
			// Added for Intergeo File Format (Yves Kreis) -->
			fileExtension = Application.getExtension(selectedFile);
			int i = 0;
			while (i < fileExtensions.length
					&& !fileExtension.equals(fileExtensions[i])) {
				i++;
			}
			if (i >= fileExtensions.length) {
				fileExtension = fileExtensions[0];
			}
			// <-- Added for Intergeo File Format (Yves Kreis)
			selectedFile = addExtension(selectedFile, fileExtension);
			fileChooser.setSelectedFile(selectedFile);
		}

		// Modified for Intergeo File Format (Yves Kreis) -->
		/*
		 * MyFileFilter fileFilter = new MyFileFilter();
		 * fileFilter.addExtension(fileExtension); if (fileDescription != null)
		 * fileFilter.setDescription(fileDescription);
		 * fileChooser.resetChoosableFileFilters();
		 * fileChooser.setFileFilter(fileFilter);
		 */
		fileChooser.resetChoosableFileFilters();
		MyFileFilter fileFilter;
		MyFileFilter mainFilter = null;
		for (int i = 0; i < fileExtensions.length; i++) {
			fileFilter = new MyFileFilter(fileExtensions[i]);
			if (fileDescriptions.length >= i && fileDescriptions[i] != null)
				fileFilter.setDescription(fileDescriptions[i]);
			fileChooser.addChoosableFileFilter(fileFilter);
			if (fileExtension.equals(fileExtensions[i])) {
				mainFilter = fileFilter;
			}
		}
		fileChooser.setFileFilter(mainFilter);		
		// <-- Modified for Intergeo File Format (Yves Kreis)

		while (!done) {
			// show save dialog
			int returnVal = fileChooser.showSaveDialog(app.getMainComponent());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = fileChooser.getSelectedFile();

				// Added for Intergeo File Format (Yves Kreis) -->
				if (fileChooser.getFileFilter() instanceof geogebra.gui.app.MyFileFilter) {
					fileFilter = (MyFileFilter) fileChooser.getFileFilter();
					fileExtension = fileFilter.getExtension();
				} else {
					fileExtension = fileExtensions[0];
				}
				// <-- Added for Intergeo File Format (Yves Kreis)

				// remove all special characters from HTML filename
				if (fileExtension == Application.FILE_EXT_HTML) {
					file = removeExtension(file);
					file = new File(file.getParent(), Util
							.keepOnlyLettersAndDigits(file.getName()));
				}

				// remove "*<>/\?|:
				file = new File(file.getParent(), Util.processFilename(file
						.getName())); // Michael Borcherds 2007-11-23

				// add file extension
				file = addExtension(file, fileExtension);
				fileChooser.setSelectedFile(file);

				if (promptOverwrite && file.exists()) {
					// ask overwrite question

					// Michael Borcherds 2008-05-04
					Object[] options = {
							app.getMenu("Overwrite"), 
							app.getMenu("DontOverwrite") };
					int n = JOptionPane.showOptionDialog(app.getMainComponent(),
							app.getPlain("OverwriteFile") + "\n" + file.getName() , app.getPlain("Question"),
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options,
							options[1]);

					done = (n == 0);

					/*
					 * int n = JOptionPane.showConfirmDialog( app.getMainComponent(),
					 * app.getPlain("OverwriteFile") + "\n" +
					 * file.getAbsolutePath(), app.getPlain("Question"),
					 * JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					 * done = (n == JOptionPane.YES_OPTION);
					 */
				} else {
					done = true;
				}
				// Modified for Intergeo File Format (Yves Kreis) -->
			} else {
				// } else
				// return null;
				file = null;
				break;
			}
			// <-- Modified for Intergeo File Format (Yves Kreis)
		}

		return file;
	}

	public static File addExtension(File file, String fileExtension) {
		if (file == null)
			return null;
		if (Application.getExtension(file).equals(fileExtension))
			return file;
		else
			return new File(file.getParentFile(), // path
					file.getName() + '.' + fileExtension); // filename
	}

	public static File removeExtension(File file) {
		if (file == null)
			return null;
		String fileName = file.getName();
		int dotPos = fileName.indexOf('.');

		if (dotPos <= 0)
			return file;
		else
			return new File(file.getParentFile(), // path
					fileName.substring(0, dotPos));
	}

	public void openURL() {
		InputDialog id = new InputDialogOpenURL(app);
		id.setVisible(true);
		
	}

	public void openFile() {
		
		if (propDialog != null && propDialog.isShowing())
			propDialog.cancel();

		if (app.isSaved() || saveCurrentFile()) {
			app.setWaitCursor();
			File oldCurrentFile = app.getCurrentFile();
			app.setCurrentFile(null);

			initFileChooser();
			fileChooser.setMode(GeoGebraFileChooser.MODE_GEOGEBRA);
			fileChooser.setCurrentDirectory(app.getCurrentPath());
			fileChooser.setMultiSelectionEnabled(true);
				
			// GeoGebra File Filter
			MyFileFilter fileFilter = new MyFileFilter();
			// This order seems to make sure that .ggb files come first
			// so that getFileExtension() returns "ggb"
			// TODO: more robust method
			fileFilter.addExtension(Application.FILE_EXT_GEOGEBRA);
			fileFilter.addExtension(Application.FILE_EXT_GEOGEBRA_TOOL);
			fileFilter.addExtension(Application.FILE_EXT_HTML);
			fileFilter.addExtension(Application.FILE_EXT_HTM);
			fileFilter.setDescription(app.getPlain("ApplicationName") + " "
					+ app.getMenu("Files"));
			fileChooser.resetChoosableFileFilters();
			// Modified for Intergeo File Format (Yves Kreis & Ingo Schandeler)
			// -->
			fileChooser.addChoosableFileFilter(fileFilter);
			
			// HTML File Filter (for ggbBase64 files)
//			MyFileFilter fileFilterHTML = new MyFileFilter();
//			fileFilterHTML.addExtension(Application.FILE_EXT_HTML);
//			fileFilterHTML.addExtension(Application.FILE_EXT_HTM);
//			fileFilterHTML.setDescription(Application.FILE_EXT_HTML + " "
//					+ app.getMenu("Files"));
//			fileChooser.addChoosableFileFilter(fileFilterHTML);

			// Intergeo File Filter
			if (!GeoGebra.DISABLE_I2G) {
				MyFileFilter i2gFileFilter = new MyFileFilter();
				i2gFileFilter.addExtension(Application.FILE_EXT_INTERGEO);
				i2gFileFilter.setDescription("Intergeo " + app.getMenu("Files")
						+ " [Version " + GeoGebra.I2G_FILE_FORMAT + "]");
				fileChooser.addChoosableFileFilter(i2gFileFilter);
			}
			// fileChooser.setFileFilter(fileFilter);
			if (GeoGebra.DISABLE_I2G
					|| oldCurrentFile == null
					|| Application.getExtension(oldCurrentFile).equals(
							Application.FILE_EXT_GEOGEBRA)
					|| Application.getExtension(oldCurrentFile).equals(
							Application.FILE_EXT_GEOGEBRA_TOOL)) {
				fileChooser.setFileFilter(fileFilter);
			}
			// <-- Modified for Intergeo File Format (Yves Kreis & Ingo
			// Schandeler)

			app.setDefaultCursor();
			int returnVal = fileChooser.showOpenDialog(app.getMainComponent());

			File[] files = null;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				files = fileChooser.getSelectedFiles();
			}
			
			// Modified for Intergeo File Format (Yves Kreis) -->
			if (fileChooser.getFileFilter() instanceof geogebra.gui.app.MyFileFilter) {
				fileFilter = (MyFileFilter) fileChooser.getFileFilter();
				doOpenFiles(files, true, fileFilter.getExtension());
			} else {
				// doOpenFiles(files, true);
				doOpenFiles(files, true);
			}
			// <-- Modified for Intergeo File Format (Yves Kreis)

			
			if (app.getCurrentFile() == null && !htmlLoaded) { //edited by Zbynek Konecny, 2010-05-28 (see #126)
				app.setCurrentFile(oldCurrentFile);
			}
			fileChooser.setMultiSelectionEnabled(false);
		}
	}

	public synchronized void doOpenFiles(File[] files,
			boolean allowOpeningInThisInstance) {
		// Added for Intergeo File Format (Yves Kreis) -->
		doOpenFiles(files, allowOpeningInThisInstance,
				Application.FILE_EXT_GEOGEBRA);
	}

	public synchronized void doOpenFiles(File[] files,
			boolean allowOpeningInThisInstance, String extension) {
		//Zbynek Konecny, 2010-05-28 (see #126)
		htmlLoaded=false;
		// <-- Added for Intergeo File Format (Yves Kreis)
		// there are selected files
		if (files != null) {
			File file;
			int counter = 0;
			for (int i = 0; i < files.length; i++) {
				file = files[i];

				if (!file.exists()) {
					// Modified for Intergeo File Format (Yves Kreis) -->
					// file = addExtension(file, Application.FILE_EXT_GEOGEBRA);
					file = addExtension(file, extension);
					if (extension.equals(Application.FILE_EXT_GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								Application.FILE_EXT_GEOGEBRA_TOOL);
					}
					if (extension.equals(Application.FILE_EXT_GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								Application.FILE_EXT_HTML);
					}
					if (extension.equals(Application.FILE_EXT_GEOGEBRA)
							&& !file.exists()) {
						file = addExtension(removeExtension(file),
								Application.FILE_EXT_HTM);
					}
					
					if (!file.exists()) {
						//Put the correct extension back on for the error message
						file = addExtension(removeExtension(file), extension);
						
						JOptionPane.showConfirmDialog(app.getMainComponent(),
								app.getError("FileNotFound") + ":\n" + file.getAbsolutePath(),
								app.getError("Error"), JOptionPane.DEFAULT_OPTION,
								JOptionPane.WARNING_MESSAGE);
					
					}
					// <-- Modified for Intergeo File Format (Yves Kreis)
				}
				
				String ext = Application.getExtension(file).toLowerCase(Locale.US);

				if (file.exists()) {
					if (Application.FILE_EXT_GEOGEBRA_TOOL.equals(ext)) {
							// load macro file
							loadFile(file, true);
						} else 	if (Application.FILE_EXT_HTML.equals(ext) 
								|| Application.FILE_EXT_HTM.equals(ext) ) {
							// load HTML file with applet param ggbBase64
							//if we loaded from GGB, we don't want to overwrite old file
							//next line Zbynek Konecny, 2010-05-28 (#126)
							htmlLoaded=loadBase64File(file); 
						} else {
						// standard GeoGebra file
						GeoGebraFrame inst = GeoGebraFrame.getInstanceWithFile(file);
						if (inst == null) {
							counter++;
							if (counter == 1 && allowOpeningInThisInstance) {
								// open first file in current window
								loadFile(file, false);
							} else {
								// create new window for file
								try {
									String[] args = { file.getCanonicalPath() };
									GeoGebraFrame wnd = GeoGebraFrame
											.createNewWindow(new CommandLineArguments(args));
									wnd.toFront();
									wnd.requestFocus();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else if (counter == 0) {
							// there is an instance with this file opened
							inst.toFront();
							inst.requestFocus();
						}
					}
				} 
			}
		}
		
	}
	
	public void allowGUIToRefresh() {
		if (!SwingUtilities.isEventDispatchThread())
			return;
		
//		 // use Foxtrot to wait a bit until screen has refreshed
//        Worker.post(new Job()
//        {
//           public Object run()
//           {  
//              try {
//            	  Thread.sleep(10);
//              } catch (InterruptedException e) {
//            	  e.printStackTrace();
//              }   
//              return null;
//           }
//        });
	}

	public boolean loadFile(final File file, final boolean isMacroFile) {
		// show file not found message
		if (!file.exists()) {
			/*
			 * First parameter can not be the main component of the
			 * application, otherwise that component would be validated
			 * too early if a missing file was loaded through 
			 * the command line, which causes some nasty rendering
			 * problems.
			 */
			JOptionPane.showConfirmDialog(null,
					app.getError("FileNotFound") + ":\n" + file.getAbsolutePath(),
					app.getError("Error"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE);
			return false;
		}										     
        
 	   app.setWaitCursor();  
	   if (!isMacroFile) {
			// hide navigation bar for construction steps if visible
			app.setShowConstructionProtocolNavigation(false);
		}

		boolean success = app.loadXML(file, isMacroFile);
        updateGUIafterLoadFile(success, isMacroFile);
        app.setDefaultCursor();
        return success;
        
    }
	
	public boolean loadURL(String urlString) {
		
		boolean success = false;
		boolean isMacroFile =  false;
		app.setWaitCursor();
		
		// check first for ggb/ggt file
		if (urlString.endsWith(".ggb") || urlString.endsWith(".ggt")) {
			
			try {
				URL url = new URL(urlString);
				isMacroFile = urlString.endsWith(".ggt");
				success = app.loadXML(url, isMacroFile);
			} catch (IOException e) {
				//success = false;
			}

			
		} else
		// special case: urlString is actually a base64 encoded ggb file
			if (urlString.startsWith("UEs")) {
				// decode Base64
				byte[] zipFile;
				try {
					zipFile = geogebra.util.Base64.decode(urlString);
					// load file
					success = app.loadXML(zipFile);   
				} catch (IOException e) {
					//success = false;
				}			
				// special case: urlString is actually a GeoGebra XML file
			} else if (urlString.startsWith("<?xml ") && urlString.endsWith("</geogebra>")) {
				success = app.loadXML(urlString);   		
			} else {	
			
			try {
				
				// try base64
				URL url = new URL(urlString);
				success = loadBase64fromHTML(url.openStream());
				
				// try ggb
				if (success == false) 
					success = loadGgbFromHTML(url.openStream(), urlString);
				
			} catch (IOException e) {
				//success = false;
			}
		}
		
		if (!success) {
			app.showError(app.getError("LoadFileFailed") + "\n" + urlString);
		}
		
	    updateGUIafterLoadFile(success, isMacroFile);
		
		app.setDefaultCursor();
		return success;
	}
	
	static String base64Str;
	
	public boolean loadBase64fromHTML(InputStream fis) throws IOException {

		BufferedReader myInput = new BufferedReader(new InputStreamReader(fis));

		HTMLEditorKit.ParserCallback callback = 
			new HTMLEditorKit.ParserCallback () {
			
			boolean base64Found = false;
			
			public void handleSimpleTag(HTML.Tag tag, 
                    MutableAttributeSet attrSet, int pos) {
				if (!base64Found && tag == HTML.Tag.PARAM) {
					if (((String)attrSet.getAttribute(HTML.Attribute.NAME)).toLowerCase(Locale.US).equals("ggbbase64")) {
						//Application.debug(""+attrSet.getAttribute(HTML.Attribute.VALUE));
						
						//Application.debug("base64 found using HTML parser");
						
						base64Found = true;
						base64Str = (String)attrSet.getAttribute(HTML.Attribute.VALUE);
					}
				} 
			}
		};
		
		 Reader reader = new InputStreamReader (fis);
		new ParserDelegator().parse(reader, callback, true);
		
		if (base64Str != null) {
			// decode Base64
			byte[] zipFile = geogebra.util.Base64.decode(base64Str);
			base64Str = null;
			
			// load file
			return app.loadXML(zipFile);   
		}
		
		return false;

	}
    
	public boolean loadGgbFromHTML(InputStream fis, String source) throws IOException {

		BufferedReader myInput = new BufferedReader
		(new InputStreamReader(fis));

		StringBuilder sb = new StringBuilder();

		String thisLine;

		boolean started = false;

		while ((thisLine = myInput.readLine()) != null)  {

			// don't start reading until filename tag
			if (!started && thisLine.indexOf("filename") > -1) started = true;

			if (started) {
				sb.append(thisLine);

				if (thisLine.indexOf("</applet>") > -1) break;
			}



		}

		String fileArgument = sb.toString();

		String matchString = "name=\"filename\" value=\"";
		// old style, no quotes
		String matchString2 = "name=filename value=\"";

		int start = fileArgument.indexOf(matchString);
		
		if (start == -1) {
			matchString = matchString2;
			start = fileArgument.indexOf(matchString);
		}
		
		// match "/> or " /> or "> etc
		int end   = fileArgument.indexOf(">");
		while (end > start && fileArgument.charAt(end) != '\"') end--;

		// check for two <param> tags on the same line
		if (start > end) {
			fileArgument = fileArgument.substring(start);
			start = 0;
			end   = fileArgument.indexOf(">");
			while (end > start && fileArgument.charAt(end) != '\"') end--;
		}
		
		if (start < 0 || end < 0 || end <= start) {
			//app.setDefaultCursor();
			//app.showError(app.getError("LoadFileFailed") + ":\n" + source);
			return false;
		}

		
		int index = source.lastIndexOf('/');
		if (index != -1)
			source = source.substring(0, index + 1); // path without file.ggb

		Application.debug("Trying to load from:" + source + fileArgument
				.substring(matchString.length() + start, end));

		URL url = new URL(source + fileArgument.substring(matchString.length() + start, end));
		
		return app.loadXML(url, false);

	}
    
    	/*
	 * loads an html file with <param name="ggbBase64" value="UEsDBBQACAAI...
	 */
	public boolean loadBase64File(final File file) {
		if (!file.exists()) {
			// show file not found message
			JOptionPane.showConfirmDialog(app.getMainComponent(),
					app.getError("FileNotFound") + ":\n" + file.getAbsolutePath(),
					app.getError("Error"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE);
			return false;
		}										     

		boolean success = false;

		app.setWaitCursor();  
		// hide navigation bar for construction steps if visible
		app.setShowConstructionProtocolNavigation(false);


		try {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			success = loadBase64fromHTML(fis);		
			
		} catch (Exception e) {
			app.setDefaultCursor();
			app.showError(app.getError("LoadFileFailed") + ":\n" + file);
			e.printStackTrace();
			return false;

		}
        updateGUIafterLoadFile(success, false);
		app.setDefaultCursor();
		return success;

	}
		
	private void updateGUIafterLoadFile(boolean success, boolean isMacroFile) {
		if(success && !isMacroFile && !layout.isIgnoringDocument()) {
			getLayout().setPerspectives(app.getTmpPerspectives());
			SwingUtilities.updateComponentTreeUI(getLayout().getRootComponent());
			
			if(!app.isIniting()) {
				updateFrameSize(); // checks internally if frame is available
			}
		}
		
		// force JavaScript ggbOnInit(); to be called
		if (!app.isApplet())
			app.getScriptManager().ggbOnInit();
	}

	// Added for Intergeo File Format (Yves Kreis) -->
	/*
	 * PropertyChangeListener implementation to handle file filter changes
	 */
	private class FileFilterChangedListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (fileChooser.getFileFilter() instanceof geogebra.gui.app.MyFileFilter) {
				String fileName = null;
				if (fileChooser.getSelectedFile() != null) {
					fileName = fileChooser.getSelectedFile().getName();
				}
				
				//fileName = getFileName(fileName);
				
				if (fileName != null && fileName.indexOf(".") > -1) {
					fileName = fileName.substring(0, fileName.lastIndexOf("."))
							+ "."
							+ ((MyFileFilter) fileChooser.getFileFilter())
									.getExtension();
					fileChooser.setSelectedFile(new File(fileChooser
							.getCurrentDirectory(), fileName));
				}
			}
		}
	}	

	protected boolean initActions() {	
		if (showAxesAction != null) return false;
		
		showAxesAction = new AbstractAction(app.getMenu("Axes"),
				app.getImageIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle axes
				app.toggleAxis();
				
				/*
				boolean bothAxesShown = app.getEuclidianView().getShowXaxis()
						&& app.getEuclidianView().getShowYaxis();
				app.getEuclidianView().showAxes(!bothAxesShown, !bothAxesShown);
				*/
				app.getEuclidianView().repaint();
				app.storeUndoInfo();
				app.updateMenubar();
				
			}
		};

		showGridAction = new AbstractAction(app.getMenu("Grid"),
				app.getImageIcon("grid.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle grid
				app.toggleGrid();
				app.getEuclidianView().repaint();
				app.storeUndoInfo();
				app.updateMenubar();
				
			}
		};

		undoAction = new AbstractAction(app.getMenu("Undo"),
				app.getImageIcon("edit-undo.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (propDialog != null && propDialog.isShowing())
					propDialog.cancel();
				
				undo();
			}
		};

		redoAction = new AbstractAction(app.getMenu("Redo"),
				app.getImageIcon("edit-redo.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (propDialog != null && propDialog.isShowing())
					propDialog.cancel();

				redo();
			}
		};
		
		updateActions();
		
		return true;
	}

	public void updateActions() {
		if (app.isUndoActive() && undoAction != null) {
			undoAction.setEnabled(kernel.undoPossible());
			redoAction.setEnabled(kernel.redoPossible());
		}
	}
	
	public void redo() {
		app.setWaitCursor();
		kernel.redo();
		updateActions();
		app.setDefaultCursor();
		System.gc();
	}
	
	public void undo() {
		app.setWaitCursor();
		kernel.undo();
		updateActions();
		app.setDefaultCursor();
		System.gc();
	}

	public int getMenuBarHeight() {
		if (menuBar == null)
			return 0;
		else
			return ((JMenuBar) menuBar).getHeight();
	}

	public int getAlgebraInputHeight() {
		if (app.showAlgebraInput() && algebraInput != null)
			return algebraInput.getHeight();
		else
			return 0;
	}

	public AbstractAction getShowAxesAction() {
		initActions();
		return showAxesAction;
	}

	public AbstractAction getShowGridAction() {
		initActions();
		return showGridAction;
	}



	/**
	 * Creates a new checkbox at given startPoint
	 */
	public void showBooleanCheckboxCreationDialog(Point loc, GeoBoolean bool) {
		CheckboxCreationDialog d = new CheckboxCreationDialog(app, loc, bool);
		d.setVisible(true);
	}

	/**
	 * Shows a modal dialog to enter a number or number variable name.
	 */
	public NumberValue showNumberInputDialog(String title, String message,
			String initText) {
		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler();
		InputDialog id = new InputDialog(app, message, title, initText, false,
				handler, true, false, null);
		id.setVisible(true);

		cons.setSuppressLabelCreation(oldVal);
		return handler.getNum();
	}
	
	
	/**
	 * Shows a modal dialog to enter a number or number variable name.
	 */
	public NumberValue showNumberInputDialog(String title, String message,
			String initText, boolean changingSign, String checkBoxText) {
		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberChangeSignInputHandler handler = new NumberChangeSignInputHandler();
		NumberChangeSignInputDialog id = new NumberChangeSignInputDialog(app, message, title, initText, 
				handler,changingSign,checkBoxText);
		id.setVisible(true);

		cons.setSuppressLabelCreation(oldVal);
		
		NumberValue num = handler.getNum();
		

		return handler.getNum();
	}
	
	
	public void showNumberInputDialogRegularPolygon(String title, GeoPoint geoPoint1, GeoPoint geoPoint2) {

		NumberInputHandler handler = new NumberInputHandler();
		InputDialog id = new InputDialogRegularPolygon(app, title, handler, geoPoint1, geoPoint2, kernel);
		id.setVisible(true);

	}

	/**
	 * Shows a modal dialog to enter an angle or angle variable name.
	 * 
	 * @return: Object[] with { NumberValue, AngleInputDialog } pair
	 */
	public Object[] showAngleInputDialog(String title, String message,
			String initText) {
		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		NumberInputHandler handler = new NumberInputHandler();
		AngleInputDialog id = new AngleInputDialog(app, message, title,
				initText, false, handler, true);
		id.setVisible(true);

		cons.setSuppressLabelCreation(oldVal);
		Object[] ret = { handler.getNum(), id };
		return ret;
	}

	public class NumberInputHandler implements InputHandler {
		private NumberValue num = null;

		public boolean processInput(String inputString) {
			GeoElement[] result = kernel.getAlgebraProcessor()
					.processAlgebraCommand(inputString, false);
			boolean success = result != null && result[0].isNumberValue();
			if (success) {
				setNum((NumberValue) result[0]);
			}
			return success;
		}

		public void setNum(NumberValue num) {
			this.num = num;
		}

		public NumberValue getNum() {
			return num;
		}
	}
	
	/**
	 * Handler of a number, with possibility of changing the sign
	 * @author mathieu
	 *
	 */
	public class NumberChangeSignInputHandler extends NumberInputHandler {
		/**
		 * If (changeSign==true), change sign of the number handled
		 * @param inputString
		 * @param changeSign
		 * @return number handled
		 */
		public boolean processInput(String inputString, boolean changeSign) {
			if (changeSign){
				StringBuilder sb = new StringBuilder();
				sb.append("-(");
				sb.append(inputString);
				sb.append(")");
				return processInput(sb.toString());
			}else
				return processInput(inputString);
			
		}
	}
	
	public Toolbar getGeneralToolbar() {
		return toolbarPanel.getFirstToolbar();
	}

	public void setToolBarDefinition(String toolBarDefinition) {
		strCustomToolbarDefinition = toolBarDefinition;
	}

	public String getToolbarDefinition() {
		if (strCustomToolbarDefinition == null && toolbarPanel != null)
			return getGeneralToolbar().getDefaultToolbarString();
		else
			return strCustomToolbarDefinition;
	}

	public void removeFromToolbarDefinition(int mode) {
		if (strCustomToolbarDefinition != null) {
			// Application.debug("before: " + strCustomToolbarDefinition +
			// ",  delete " + mode);

			strCustomToolbarDefinition = strCustomToolbarDefinition.replaceAll(
					Integer.toString(mode), "");

			if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
				// if a macro mode is removed all higher macros get a new id
				// (i.e. id-1)
				int lastID = kernel.getMacroNumber()
						+ EuclidianView.MACRO_MODE_ID_OFFSET - 1;
				for (int id = mode + 1; id <= lastID; id++) {
					strCustomToolbarDefinition = strCustomToolbarDefinition
							.replaceAll(Integer.toString(id),
									Integer.toString(id - 1));
				}
			}

			// Application.debug("after: " + strCustomToolbarDefinition);
		}
	}

	public void addToToolbarDefinition(int mode) {
		if (strCustomToolbarDefinition != null) {
			strCustomToolbarDefinition = strCustomToolbarDefinition + " | "
					+ mode;
		}
	}

	public void showURLinBrowser(URL url) {
		if (app.getJApplet() != null) {
			app.getJApplet().getAppletContext().showDocument(url, "_blank");
		} else {
			BrowserLauncher.openURL(url.toExternalForm());
		}
	}

	    public void openHelp() {
	    	showURLinBrowser(app.getGuiManager().getHelpURL(0,""));
	    	/*
	    	try {
				showURLinBrowser(new URL(GeoGebra.HELP_URL));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}*/
	    	
	    }
		
	    public void openCommandHelp(String command) {
	    	String internalCmd = null;
	    	if (command != null)
	        try { // convert eg uppersum to UpperSum
	         	internalCmd = app.translateCommand(command);	          
	        }
	        catch (Exception e) {}
	        
	        openHelp(internalCmd,HELP_COMMAND);	            
	    }    
	    
	    public void openHelp(String page) {
	    	openHelp(page,HELP_GENERIC);
	    }
	    
	    public void openToolHelp(String page) {
	    	openHelp(page,HELP_TOOL);
	    }
	    
	    private void openHelp(String page,int type) {
	    	try{   	
	        	URL helpURL = getHelpURL(type,page);
	            showURLinBrowser(helpURL);
	        } catch (MyError e) {           
	            app.showError(e);
	        } catch (Exception e) {           
	            Application.debug(
	                "openHelp error: " + e.toString() + e.getMessage());
	            app.showError(e.getMessage());
	        }
	    }
	    
	    public void showURLinBrowser(String strURL) {
	    	try {
	    		URL url = new URL(strURL);
	    		showURLinBrowser(url);
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	   

	    private static final int HELP_COMMAND = 0;
	    private static final int HELP_TOOL = 1;	  
	    private static final int HELP_GENERIC = 2;
	    
	    private URL getHelpURL(int type, String pageName)  {
	    	// try to get help for given language
	    	// eg http://www.geogebra.org/help/en/FitLogistic

	    	String localeCode = app.getLocale().toString();
	    		    	
	    	String strURL = "";
	    	String typeStr = "";
	    	switch(type){
	    	case HELP_COMMAND:
	    		strURL =  app.getEnglishCommand(pageName);
	    		typeStr = "cmd";
	    		break;
	    	case HELP_TOOL:
	    		strURL =  app.getEnglishMenu(pageName);
	    		typeStr = "tool";
	    		break;
	    	case HELP_GENERIC:
	    		strURL =  app.getWiki(pageName);
	    		typeStr = "article";
	    		break;
	    	default:
	    		Application.printStacktrace("Bad getHelpURL call");
	    	}
			try {
			strURL = GeoGebra.GEOGEBRA_WEBSITE
					+ "help/" + localeCode+"/" + typeStr + "/" +
	        			java.net.URLEncoder.encode(strURL.replace(" ", "_"),"utf-8");
	        		Application.debug(strURL);
	                URL url =   new URL(strURL);
                	return url;

	        } catch (Exception e) {     
	        	e.printStackTrace();
	        }
	        return null;
	    }



	    /**
	     * Returns text "Created with <ApplicationName>" and link
	     * to application homepage in html.
	     */
	    public String getCreatedWithHTML(boolean JSXGraph) {
	        String ret;
	        
	        if (!JSXGraph) ret = Util.toHTMLString(app.getPlain("CreatedWithGeoGebra")); // MRB 2008-06-14 added Util.toHTMLString
	        else           ret = Util.toHTMLString(app.getPlain("CreatedWithGeoGebraAndJSXGraph"));

	        if (ret.toLowerCase().indexOf("geogebra") == -1)
	        	ret="Created with GeoGebra";
	        
	        ret = ret.replaceAll("[Gg]eo[Gg]ebra", "<a href=\""+GeoGebra.GEOGEBRA_WEBSITE+"\" target=\"_blank\" >GeoGebra</a>");
	        ret = ret.replaceAll("JSXGraph", "<a href=\"http://jsxgraph.org/\" target=\"_blank\" >JSXGraph</a>");
	        
	        return ret;
	    }
	    
	    public void setMode(int mode) {  
	    	// close properties dialog 
	    	// if it is not the current selection listener
	     	if (propDialog != null && 
	     			propDialog.isShowing() &&
	     			propDialog != app.getCurrentSelectionListener()) 
	     	{    		
	     		propDialog.setVisible(false);	
	     	}
	     	
	     	kernel.notifyModeChanged(mode);  
	        
	        // select toolbar button
	        setToolbarMode(mode);	         
	    }
	    
	    public void setToolbarMode(int mode) {
	    	if (toolbarPanel == null) return;
	    	
        	toolbarPanel.setMode(mode);
        	layout.getDockManager().setToolbarMode(mode);
	    }
	    
	    /**
	        *  Exports construction protocol as html 
	        */
	    final public void exportConstructionProtocolHTML() {
	        getConstructionProtocol();
	        constProtocol.initProtocol();
	        constProtocol.showHTMLExportDialog();
	    }
	    

		public final String getCustomToolbarDefinition() {
			return strCustomToolbarDefinition;
		}
		
		public void closeOpenDialogs() {
			// close open windows
	    	if (propDialog != null && propDialog.isShowing())
	    		propDialog.cancel();    	
	    	if (constProtocol != null && constProtocol.isShowing())
	    		constProtocol.setVisible(false);
	    	
		}
		
		public AbstractAction getRedoAction() {
			initActions();
			return redoAction;
		}

		public AbstractAction getUndoAction() {		
			initActions();
			return undoAction;
		}
	    	 	
		public void updateFrameSize() {
			JFrame fr = app.getFrame();
			if (fr != null) {
				((GeoGebraFrame) fr).updateSize();
				app.validateComponent();
			}
		}
		
		public void updateFrameTitle() {
			if (!(app.getFrame() instanceof GeoGebraFrame))
				return;
			
			GeoGebraFrame frame = (GeoGebraFrame) app.getFrame();

			StringBuilder sb = new StringBuilder();
			sb.append("GeoGebra");
			if (app.getCurrentFile() != null) {
				sb.append(" - ");
				sb.append(app.getCurrentFile().getName());
			} else {
				if (GeoGebraFrame.getInstanceCount() > 1) {
					int nr = frame.getInstanceNumber();
					sb.append(" (");
					sb.append(nr + 1);
					sb.append(")");
				}
			}
			frame.setTitle(sb.toString());
		}
		
		public JFrame createFrame() {
			GeoGebraFrame wnd = new GeoGebraFrame();
			wnd.setGlassPane(layout.getDockManager().getGlassPane());
			wnd.setApplication(app);
			
			return wnd;
		}
		
		public synchronized void exitAll() {
			ArrayList insts = GeoGebraFrame.getInstances();
			GeoGebraFrame[] instsCopy = new GeoGebraFrame[insts.size()];
			for (int i = 0; i < instsCopy.length; i++) {
				instsCopy[i] = (GeoGebraFrame) insts.get(i);
			}

			for (int i = 0; i < instsCopy.length; i++) {
				instsCopy[i].getApplication().exit();
			}
		}
		
		public void setColumnWidth(int column, int width) {
			((SpreadsheetView)getSpreadsheetView()).setColumnWidth(column, width);
		}
		
		//G.Sturr 2010-6-2
		public void setRowHeight(int row, int height) {
			((SpreadsheetView)getSpreadsheetView()).setRowHeight(row, height);
		}
		//END G.Sturr
		
		VirtualKeyboardListener currentKeyboardListener = null;

		public VirtualKeyboardListener getCurrentKeyboardListener() {
			return currentKeyboardListener;
		}

		private boolean ignoreNext = false;
		
		public void setCurrentTextfield(VirtualKeyboardListener keyboardListener, boolean autoClose) {
			currentKeyboardListener = keyboardListener;
			if (virtualKeyboard != null)
				if (currentKeyboardListener == null) {
					// close virtual keyboard when focus lost
					// ... unless we've lost focus because we've just opened it!
					if (autoClose) toggleKeyboard(false);
				} else {
					// open virtual keyboard when focus gained
					if (Application.isVirtualKeyboardActive())
						toggleKeyboard(true);
				}
			
			
		}
		
		
		
		WindowsUnicodeKeyboard kb = null;

		public void insertStringIntoTextfield(String text, boolean altPressed, boolean ctrlPressed, boolean shiftPressed) {

			if (currentKeyboardListener != null && !text.equals("\n")
					&& (!text.startsWith("<") || !text.endsWith(">"))
					&& !altPressed
					&& !ctrlPressed) {
					currentKeyboardListener.insertString(text);
			} else {
				// use Robot if no TextField currently active
				// or for special keys eg Enter
				if (kb == null) {
					try{
						kb = new WindowsUnicodeKeyboard();
					} catch (Exception e) {}
				}
				
				kb.doType(altPressed, ctrlPressed, shiftPressed, text);
								
			}
		}
		
		VirtualKeyboard virtualKeyboard = null;
		
		public void toggleKeyboard(boolean show) {
			getVirtualKeyboard().setVisible(show);
		}
		
		/**
		 * @return The virtual keyboard (initializes it if necessary)
		 */
		public VirtualKeyboard getVirtualKeyboard() {
			if (virtualKeyboard == null) {
				// TODO use config values
				virtualKeyboard = new VirtualKeyboard(app, 400, 235, 0.7f);
			}
			
			return virtualKeyboard;
		}
		
		/*
		HandwritingRecognitionTool handwritingRecognition = null;
		
		public Component getHandwriting() {
			
			if (handwritingRecognition == null) {
				handwritingRecognition = new HandwritingRecognitionTool(app);
			}
			return handwritingRecognition;
			
		}
		
		public void toggleHandwriting(boolean show) {
			
			if (handwritingRecognition == null) {
				handwritingRecognition = new HandwritingRecognitionTool(app);
			}
			handwritingRecognition.setVisible(show);
			handwritingRecognition.repaint();
			
		}
		
				public boolean showHandwritingRecognition() {
			if (handwritingRecognition == null) 
				return false;
			
			return handwritingRecognition.isVisible();
		}


		*/
		
		PropertiesPanelMini ppm;

		public boolean miniPropertiesOpen() {
			if (ppm == null || !ppm.isVisible()) return false;
			return true;
		}

		
		public boolean showMiniProperties() {
			if (ppm == null) 
				return false;
			
			return ppm.isVisible();
		}
		
		public void toggleMiniProperties(final boolean show) {
			
			if (!show && ppm == null) return;

		        SwingUtilities.invokeLater( new Runnable(){ public void
		        	run() { 
		        	if (ppm == null) ppm = new PropertiesPanelMini(app, app.getEuclidianView().getEuclidianController());
		        	else ppm.setListener(app.getEuclidianView().getEuclidianController());
		        	ppm.setVisible(show);
		        	
		        } });


		}
		
		public boolean showVirtualKeyboard() {
			if (virtualKeyboard == null) 
				return false;
			
			return virtualKeyboard.isVisible();
		}

		public boolean noMenusOpen() {
			if (popupMenu != null && popupMenu.isVisible()) {
				//Application.debug("menus open");
				return false;
			}
			if (drawingPadpopupMenu != null && drawingPadpopupMenu.isVisible()) {
				//Application.debug("menus open");
				return false;
			}
			
			//Application.debug("no menus open");
			return true;
		}
		
		// TextInputDialog recent symbol list
		private ArrayList<String> recentSymbolList;
		public ArrayList<String> getRecentSymbolList(){
			if(recentSymbolList == null){
				recentSymbolList = new ArrayList<String>();
				recentSymbolList.add(Unicode.PI_STRING);
				for(int i=0; i < 9; i++){
					recentSymbolList.add("");
				}
			}
			return recentSymbolList;
		}
		
		String [] fontSizeStrings = null;

		public String[] getFontSizeStrings() {
			if (fontSizeStrings == null)
				fontSizeStrings = new String[] { app.getPlain("ExtraSmall"), app.getPlain("VerySmall"), app.getPlain("Small"), app.getPlain("Medium"), app.getPlain("Large"), app.getPlain("VeryLarge"), app.getPlain("ExtraLarge") };
			
			return fontSizeStrings;
		}
		

}
