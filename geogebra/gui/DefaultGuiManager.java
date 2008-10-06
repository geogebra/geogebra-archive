package geogebra.gui;

import geogebra.Application;
import geogebra.GeoGebra;
import geogebra.GeoGebraPreferences;
import geogebra.GuiManager;
import geogebra.MyError;
import geogebra.MyFileFilter;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.menubar.GeoGebraMenuBar;
import geogebra.gui.menubar.Menubar;
import geogebra.gui.toolbar.MyToolbar;
import geogebra.gui.toolbar.ToolbarConfigDialog;
import geogebra.gui.util.BrowserLauncher;
import geogebra.gui.view.algebra.AlgebraController;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.consprotocol.ConstructionProtocol;
import geogebra.gui.view.consprotocol.ConstructionProtocolNavigation;
import geogebra.gui.view.spreadsheet.SpreadsheetView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;

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
import javax.swing.plaf.basic.BasicFileChooserUI;


/**
 * Handles all geogebra.gui package related objects and methods for Application.
 * This is done to be able to put class files of geogebra.gui.* packages into a
 * separate gui jar file.
 */
public class DefaultGuiManager implements GuiManager {
	
	private static final int SPREADSHEET_INI_COLS = 26;
	private static final int SPREADSHEET_INI_ROWS = 100;

	private Application app;
	private Kernel kernel;

	protected PropertiesDialogGeoElement propDialog;
	private ConstructionProtocol constProtocol;
	protected ConstructionProtocolNavigation constProtocolNavigation;

	private AlgebraInput algebraInput;
	private AlgebraController algebraController;
	private AlgebraView algebraView;
    private SpreadsheetView spreadsheetView;   

	private JFileChooser fileChooser;
	private Menubar menuBar;

	private MyToolbar appToolbarPanel;	  
    private String strCustomToolbarDefinition;

	// Actions
	private AbstractAction showAxesAction, showGridAction, undoAction,
			redoAction;	

	public DefaultGuiManager(Application app) {
		this.app = app;
		this.kernel = app.getKernel();
		
		// removed: we need the arrow keys to work in applets 
		//if (!app.isApplet())
		
		initAlgebraController(); // needed for keyboard input in EuclidianView
	}
	
	public boolean isPropertiesDialogSelectionListener() {
		return app.getCurrentSelectionListener() == propDialog;
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
			}
	    }
	    
	   

	public JComponent getAlgebraView() {
		if (algebraView == null) {
			initAlgebraController();
			algebraView = new AlgebraView(algebraController);
			if (!app.isApplet()) {
				// allow drag & drop of files on algebraView
				algebraView.setDropTarget(new DropTarget(algebraView,
						new FileDropTargetListener(app)));
			}
		}

		return algebraView;
	}
	
	public JComponent getSpreadsheetView() {
		// init spreadsheet view
    	if (spreadsheetView == null) { 
    		spreadsheetView = new SpreadsheetView(app, SPREADSHEET_INI_COLS, SPREADSHEET_INI_ROWS);
    	}
    	
    	return spreadsheetView; 
	}	
	
	public int getHighestUsedSpreadsheetColumn() {
		if (spreadsheetView != null) { 
			return spreadsheetView.getHighestUsedColumn();
		}
		return -1;
	}
	
	public int getSpreadsheetTraceRow(int column) {
		if (spreadsheetView != null) { 
			return spreadsheetView.getTraceRow(column);
		}
		return -1;
	}
	
	public void attachSpreadsheetView() {	
		getSpreadsheetView();
		spreadsheetView.attachView();		
	}
	
	public void detachSpreadsheetView(){
		if (spreadsheetView != null)
			spreadsheetView.detachView();		
	}	
	
	public String getSpreadsheetViewXML() {
		if (spreadsheetView != null)
			return spreadsheetView.getXML();
		else
			return "";
	}
	
	public String getConsProtocolXML() {
		StringBuffer sb = new StringBuffer();
	
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
		
		return sb.toString();
	}
	
	public void attachAlgebraView(){	
		getAlgebraView();
		algebraView.attachView();		
	}	
	
	public void detachAlgebraView(){	
		if (algebraView != null)
			algebraView.detachView();		
	}	
	
	public void setShowAuxiliaryObjects(boolean flag) {
		getAlgebraView();
		algebraView.setShowAuxiliaryObjects(flag);
	}

	
	private void initAlgebraController() {
		if (algebraController == null) {
			algebraController = new AlgebraController(app.getKernel());
			
			// add controller as keylistener to EuclidianView
			app.getEuclidianView().addKeyListener(algebraController);
		}
	}

	public JComponent getAlgebraInput() {
		if (algebraInput == null)
			algebraInput = new AlgebraInput(app);

		return algebraInput;
	}
	
	public void updateAlgebraInput() {
		if (algebraInput != null)
			algebraInput.initGUI();
	}

	public synchronized void initPropertiesDialog() {
		if (propDialog == null) {
			propDialog = new PropertiesDialogGeoElement(app);
		}
	}

	public void doAfterRedefine(GeoElement geo) {
		// select geoElement with label again
		if (propDialog != null && propDialog.isShowing()) {
			// propDialog.setViewActive(true);
			propDialog.geoElementSelected(geo, false);
		}
	}

	public JComponent getToolbarPanel() {
		if (appToolbarPanel == null) {
			appToolbarPanel = new MyToolbar(app);
		}

		return appToolbarPanel;
	}
	
	public void updateToolbar() {
		if (appToolbarPanel != null) {
			appToolbarPanel.initToolbar();
		}
	}
	
	public void setShowToolBarHelp(boolean flag) {
		if (appToolbarPanel != null)
			appToolbarPanel.setShowToolBarHelp(flag);
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

	public JDialog getConstructionProtocol() {
		if (constProtocol == null) {
			try {
			constProtocol = new ConstructionProtocol(app);
			}
			catch (java.lang.NoClassDefFoundError ee) {
				app.showErrorDialog(app.getError("ExportJarMissing"));
				ee.printStackTrace();
			}

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
		if (app.showToolBar())
			return appToolbarPanel.getHeight();
		else
			return 0;
	}

	public String getDefaultToolbarString() {
		if (appToolbarPanel == null)
			return "";

		return appToolbarPanel.getDefaultToolbarString();
	}

	public void updateFonts() {

		if (algebraView != null)
			algebraView.updateFonts();
		if (algebraInput != null)
			algebraInput.updateFonts();	

		if (fileChooser != null) {
			fileChooser.setFont(app.getPlainFont());
			SwingUtilities.updateComponentTreeUI(fileChooser);
		}

		if (appToolbarPanel != null)
			appToolbarPanel.initToolbar();

		if (propDialog != null)
			propDialog.initGUI();
		if (constProtocol != null)
			constProtocol.initGUI();
		if (constProtocolNavigation != null)
			constProtocolNavigation.initGUI();
	}

	public void setLabels() {
		// reinit actions to update labels
		showAxesAction = null;
		initActions();

		if (app.showMenuBar()) {
			initMenubar();
			if (app.isApplet())
				((JApplet) app.getMainComponent())
						.setJMenuBar((JMenuBar) menuBar);
			else
				((JFrame) app.getMainComponent())
						.setJMenuBar((JMenuBar) menuBar);
		}

		// update views
		if (algebraView != null)
			algebraView.setLabels();
		if (algebraInput != null)
			algebraInput.setLabels();

		if (appToolbarPanel != null)
			appToolbarPanel.initToolbar();
		if (propDialog != null)
			propDialog.initGUI();
		if (constProtocol != null)
			constProtocol.initGUI();
		if (constProtocolNavigation != null)
			constProtocolNavigation.setLabels();
	}

	public void initMenubar() {
		if (menuBar == null) {
			menuBar = new GeoGebraMenuBar(app);
		}
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
		menuBar = (Menubar) newMenuBar;
	}

	public void showAboutDialog() {
		GeoGebraMenuBar.showAboutDialog(app);
	}

	public void showPrintPreview() {
		GeoGebraMenuBar.showPrintPreview(app);
	}

	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 */
	public void showDrawingPadPopup(Component invoker, Point p) {
		// clear highlighting and selections in views
		app.getEuclidianView().resetMode();

		// menu for drawing pane context menu
		ContextMenuGraphicsWindow popupMenu = new ContextMenuGraphicsWindow(
				app, p.x, p.y);
		popupMenu.show(invoker, p.x, p.y);
	}

	/**
	 * Displays the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 */
	public void showPopupMenu(GeoElement geo, Component invoker, Point p) {
		if (geo == null || !app.letShowPopupMenu())
			return;

		if (app.getKernel().isAxis(geo)) {
			showDrawingPadPopup(invoker, p);
			return;
		}

		// clear highlighting and selections in views
		app.getEuclidianView().resetMode();
		Point screenPos = invoker.getLocationOnScreen();
		screenPos.translate(p.x, p.y);

		ContextMenuGeoElement popupMenu = new ContextMenuGeoElement(app, geo,
				screenPos);
		popupMenu.show(invoker, p.x, p.y);
	}

	/**
	 * Displays the porperties dialog for geos
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

		// open properties dialog
		initPropertiesDialog();
		propDialog.setVisibleWithGeos(selGeos);
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
		app.getEuclidianView().resetMode();
		PropertiesDialogGraphicsWindow euclidianViewDialog = new PropertiesDialogGraphicsWindow(
				app, app.getEuclidianView());
		euclidianViewDialog.setVisible(true);
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
				true, selectInitText, null);

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
		JDialog dialog = createTextDialog(text, startPoint);
		dialog.setVisible(true);
	}

	public JDialog createTextDialog(GeoText text, GeoPoint startPoint) {
		TextInputDialog id = new TextInputDialog(app, app.getPlain("Text"),
				text, startPoint, 30, 6);
		return id;
	}

	/**
	 * Displays the redefine dialog for geo
	 */
	public void showRedefineDialog(GeoElement geo) {
		// doBeforeRedefine();

		if (geo.isGeoText()) {
			showTextDialog((GeoText) geo);
			return;
		}

		// Michael Borcherds 2007-12-31 BEGIN
		// InputHandler handler = new RedefineInputHandler(this, geo);
		String str = geo.isIndependent() ? geo.toValueString() : geo
				.getCommandDescription();
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
		id.showSpecialCharacters(true);
		id.setVisible(true);
		id.selectText();
	}

	/**
	 * Creates a new slider at given location (screen coords).
	 * 
	 * @return whether a new slider (number) was create or not
	 */
	public boolean showSliderCreationDialog(int x, int y) {
		SliderDialog dialog = new SliderDialog(app, x, y);
		dialog.setVisible(true);
		GeoNumeric num = (GeoNumeric) dialog.getResult();
		if (num != null) {
			// make sure that we show name and value of slider
			num.setLabelMode(GeoElement.LABEL_NAME_VALUE);
			num.setLabelVisible(true);
			num.update();
		}
		return num != null;
	}

	/**
	 * Creates a new image at the given location (real world coords).
	 * 
	 * @return whether a new image was create or not
	 */
	public boolean loadImage(GeoPoint loc, boolean fromClipboard) {
		String fileName;
		if (fromClipboard)
			fileName = getImageFromClipboard();
		else
			fileName = getImageFromFile();

		if (fileName == null)
			return false;

		// create GeoImage object for this fileName
		GeoImage geoImage = new GeoImage(app.getKernel().getConstruction());
		geoImage.setFileName(fileName);
		geoImage.setCorner(loc, 0);
		geoImage.setLabel(null);

		GeoImage.updateInstances();
		return true;
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
				StringBuffer sbuf = new StringBuffer();
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
			fileChooser = new JFileChooser(app.getCurrentImagePath());
			// Added for Intergeo File Format (Yves Kreis) -->
			fileChooser.addPropertyChangeListener(
					JFileChooser.FILE_FILTER_CHANGED_PROPERTY,
					new FileFilterChangedListener());
			// <-- Added for Intergeo File Format (Yves Kreis)
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
				fileChooser.setCurrentDirectory(app.getCurrentImagePath());
				MyFileFilter fileFilter = new MyFileFilter();
				fileFilter.addExtension("jpg");
				fileFilter.addExtension("png");
				fileFilter.addExtension("gif");
				fileFilter.addExtension("tif");
				if (Util.getJavaVersion() >= 1.5)
					fileFilter.addExtension("bmp");
				fileFilter.setDescription(app.getPlain("Image"));
				fileChooser.resetChoosableFileFilters();
				fileChooser.setFileFilter(fileFilter);

				// add image previewto fileChooser, Philipp Weissenbacher
				ImagePreview preview = new ImagePreview(fileChooser);
				fileChooser.setAccessory(preview);
				fileChooser.addPropertyChangeListener(preview);

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

				// remove image preview in order to reset fileChooser
				fileChooser.removePropertyChangeListener(preview);
				fileChooser.setAccessory(null);

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

	  // returns true for YES or NO and false for CANCEL
    public boolean saveCurrentFile() {    	
    	if (propDialog != null && propDialog.isShowing()) 
    		propDialog.cancel();
    	app.getEuclidianView().reset();
    	
    	// use null component for iconified frame
    	GeoGebra frame = app.getFrame();
    	Component comp = frame != null && !frame.isIconified() ? frame : null;


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
    	// close properties dialog if open
    	closeOpenDialogs();
    		    	
        if (app.getCurrentFile() != null)
			return app.saveGeoGebraFile(app.getCurrentFile());
		else
			return saveAs();
    }
	
	public boolean saveAs() {
		// Added for Intergeo File Format (Yves Kreis) -->
		String[] fileExtensions;
		String[] fileDescriptions;
		if (Application.DISABLE_I2G) {
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
							+ Application.I2G_FILE_FORMAT + "]" };
		}
		// <-- Added for Intergeo File Format (Yves Kreis)
		File file = showSaveDialog(
		// Modified for Intergeo File Format (Yves Kreis) -->
				// Application.FILE_EXT_GEOGEBRA, currentFile,
				// app.getPlain("ApplicationName") + " " + app.getMenu("Files"));
				fileExtensions, app.getCurrentFile(), fileDescriptions);
		// <-- Modified for Intergeo File Format (Yves Kreis)
		if (file == null)
			return false;

		boolean success = app.saveGeoGebraFile(file);
		if (success)
			app.setCurrentFile(file);
		return success;
	}
	
	   

	public File showSaveDialog(String fileExtension, File selectedFile,
			String fileDescription) {
		// Added for Intergeo File Format (Yves Kreis) -->
		String[] fileExtensions = { fileExtension };
		String[] fileDescriptions = { fileDescription };
		return showSaveDialog(fileExtensions, selectedFile, fileDescriptions);
	}

	public File showSaveDialog(String[] fileExtensions, File selectedFile,
			String[] fileDescriptions) {
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
		fileChooser.setCurrentDirectory(app.getCurrentPath());

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
				if (fileChooser.getFileFilter() instanceof geogebra.MyFileFilter) {
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

				if (file.exists()) {
					// ask overwrite question

					// Michael Borcherds 2008-05-04
					Object[] options = {
							app.getMenu("Overwrite") + " " + file.getName(),
							app.getMenu("DontOverwrite") };
					int n = JOptionPane.showOptionDialog(app.getMainComponent(),
							app.getPlain("OverwriteFile"), app.getPlain("Question"),
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

	

	public void openFile() {
		if (propDialog != null && propDialog.isShowing())
			propDialog.cancel();

		if (app.isSaved() || saveCurrentFile()) {
			File oldCurrentFile = app.getCurrentFile();
			app.setCurrentFile(null);

			initFileChooser();
			fileChooser.setCurrentDirectory(app.getCurrentPath());
			fileChooser.setMultiSelectionEnabled(true);

			// GeoGebra File Filter
			MyFileFilter fileFilter = new MyFileFilter();
			fileFilter.addExtension(Application.FILE_EXT_GEOGEBRA);
			fileFilter.addExtension(Application.FILE_EXT_GEOGEBRA_TOOL);
			fileFilter.setDescription(app.getPlain("ApplicationName") + " "
					+ app.getMenu("Files"));
			fileChooser.resetChoosableFileFilters();
			// Modified for Intergeo File Format (Yves Kreis & Ingo Schandeler)
			// -->
			fileChooser.addChoosableFileFilter(fileFilter);

			// Intergeo File Filter
			if (!Application.DISABLE_I2G) {
				MyFileFilter i2gFileFilter = new MyFileFilter();
				i2gFileFilter.addExtension(Application.FILE_EXT_INTERGEO);
				i2gFileFilter.setDescription("Intergeo " + app.getMenu("Files")
						+ " [Version " + Application.I2G_FILE_FORMAT + "]");
				fileChooser.addChoosableFileFilter(i2gFileFilter);
			}
			// fileChooser.setFileFilter(fileFilter);
			if (Application.DISABLE_I2G
					|| oldCurrentFile == null
					|| Application.getExtension(oldCurrentFile).equals(
							Application.FILE_EXT_GEOGEBRA)
					|| Application.getExtension(oldCurrentFile).equals(
							Application.FILE_EXT_GEOGEBRA_TOOL)) {
				fileChooser.setFileFilter(fileFilter);
			}
			// <-- Modified for Intergeo File Format (Yves Kreis & Ingo
			// Schandeler)

			int returnVal = fileChooser.showOpenDialog(app.getMainComponent());

			File[] files = null;
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				files = fileChooser.getSelectedFiles();
			}
			// Modified for Intergeo File Format (Yves Kreis) -->
			if (fileChooser.getFileFilter() instanceof geogebra.MyFileFilter) {
				fileFilter = (MyFileFilter) fileChooser.getFileFilter();
				doOpenFiles(files, true, fileFilter.getExtension());
			} else {
				// doOpenFiles(files, true);
				doOpenFiles(files, true);
			}
			// <-- Modified for Intergeo File Format (Yves Kreis)

			if (app.getCurrentFile() == null) {
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
		// <-- Added for Intergeo File Format (Yves Kreis)
		// there are selected files
		app.setWaitCursor();
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
					// <-- Modified for Intergeo File Format (Yves Kreis)
				}

				if (file.exists()) {
					if (Application.FILE_EXT_GEOGEBRA_TOOL.equals(
							Application.getExtension(file).toLowerCase(Locale.US))) {
						// load macro file
						loadFile(file, true);
					} else {
						// standard GeoGebra file
						GeoGebra inst = GeoGebra.getInstanceWithFile(file);
						if (inst == null) {
							counter++;
							if (counter == 1 && allowOpeningInThisInstance) {
								// open first file in current window
								loadFile(file, false);
							} else {
								// create new window for file
								try {
									String[] args = { file.getCanonicalPath() };
									GeoGebra wnd = GeoGebra
											.createNewWindow(args);
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
		app.setDefaultCursor();
	}

	public boolean loadFile(final File file, boolean isMacroFile) {
		if (!file.exists()) {
			// show file not found message
			JOptionPane.showConfirmDialog(app.getMainComponent(),
					app.getError("FileNotFound") + ":\n" + file.getAbsolutePath(),
					app.getError("Error"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE);
			return false;
		}

		if (!isMacroFile) {
			// hide navigation bar for construction steps if visible
			app.setShowConstructionProtocolNavigation(false);
		}

		boolean success = app.loadXML(file, isMacroFile);

		if (isMacroFile) {
			app.updateToolBar();
			app.updateContentPane();
		} else {
			// update GUI
			if (app.getEuclidianView().hasPreferredSize()) {

				// Michael Borcherds 2008-04-27 BEGIN
				// Scale drawing pad down if it doesn't fit on the screen

				// calculate total width of everything in window except
				// EuclidianView
				int algebraWidth = app.showAlgebraView() ? algebraView
						.getPreferredSize().width : 0;
				int spreadsheetWidth = 0;
					spreadsheetWidth = app.showSpreadsheetView() ? 
							spreadsheetView.getPreferredSize().width : 0;
				int furnitureWidth = algebraWidth + spreadsheetWidth;

				// calculate total height of everything in window except
				// EuclidianView
				int toolbarHeight = app.showToolBar() ? getToolBarHeight() : 0;
				int inputbarHeight = app.showAlgebraInput() ? getAlgebraInputHeight()
						: 0;
				int menubarHeight = app.showMenuBar() ? getMenuBarHeight() : 0;
				// int titlebarHeight = 30; // TODO how do we calculate this?

				// calculate titlebar height
				// TODO is there a better way?
				// getFrame().getHeight() -
				// getFrame().getContentPane().getHeight(); doesn't seem to give
				// the right answer
				JFrame testFrame = new JFrame();
				JFrame testFrame2 = new JFrame();

				testFrame.setUndecorated(false);
				testFrame.setVisible(true);
				int height1 = testFrame.getHeight();
				testFrame.setVisible(false);
				testFrame2.setUndecorated(true);
				testFrame2.setVisible(true);
				int height2 = testFrame2.getHeight();
				testFrame2.setVisible(false);

				int titlebarHeight = height1 - height2 - 5;
				int furnitureHeight = toolbarHeight + inputbarHeight
						+ menubarHeight + titlebarHeight;

				double height = app.getEuclidianView().getPreferredSize().height;
				double width = app.getEuclidianView().getPreferredSize().width;

				GraphicsEnvironment env = GraphicsEnvironment
						.getLocalGraphicsEnvironment();

				Rectangle screenSize = env.getMaximumWindowBounds(); // takes
				// Windows
				// toolbar
				// (etc)
				// into
				// account

				// fake smaller screen for testing
				// screenSize.width=1024; screenSize.height=768;

				// Application.debug(width);
				// Application.debug(screenSize.width - furnitureWidth);
				// Application.debug(screenSize.width );
				// Application.debug(height);
				// Application.debug(screenSize.height-furnitureHeight);
				// Application.debug(screenSize.height);

				if (width > screenSize.width - furnitureWidth
						|| height > screenSize.height - furnitureHeight) {

					// close algebra and spreadsheet views
					app.setShowAlgebraView(false);
					app.setShowSpreadsheetView(false);

					double xscale = app.getEuclidianView().getXscale();
					double yscale = app.getEuclidianView().getYscale();
					double xZero = app.getEuclidianView().getXZero();
					double yZero = app.getEuclidianView().getYZero();
					double scale_down = Math.max(width
							/ (screenSize.width - furnitureWidth), height
							/ (screenSize.height - furnitureHeight));
					app.getEuclidianView().setCoordSystem(xZero / scale_down, yZero
							/ scale_down, xscale / scale_down, yscale
							/ scale_down, false);
				}

				// now check all absolute objects are still on screen
				Construction cons = kernel.getConstruction();
				TreeSet geoSet = cons.getGeoSetConstructionOrder();

				int i = 0;
				Iterator it = geoSet.iterator();
				while (it.hasNext()) { // iterate through all objects
					GeoElement geo = (GeoElement) it.next();

					if (geo.isGeoText())
						if (((GeoText) geo).isAbsoluteScreenLocActive()) {
							GeoText geoText = (GeoText) geo;
							boolean fixed = geoText.isFixed();

							int x = geoText.getAbsoluteScreenLocX();
							int y = geoText.getAbsoluteScreenLocY();

							geoText.setFixed(false);
							if (x > screenSize.width)
								geoText.setAbsoluteScreenLoc(
										x = screenSize.width - furnitureWidth
												- 100, y);
							if (y > screenSize.height)
								geoText
										.setAbsoluteScreenLoc(x,
												y = screenSize.height
														- furnitureHeight);
							geoText.setFixed(fixed);
						}
					if (geo.isGeoNumeric())
						if (((GeoNumeric) geo).isAbsoluteScreenLocActive()) {
							GeoNumeric geoNum = (GeoNumeric) geo;
							boolean fixed = geoNum.isSliderFixed();

							int x = geoNum.getAbsoluteScreenLocX();
							int y = geoNum.getAbsoluteScreenLocY();

							int sliderWidth = 20, sliderHeight = 20;
							if (geoNum.isSliderHorizontal())
								sliderWidth = (int) geoNum.getSliderWidth(); // else
							// sliderHeight
							// =
							// (
							// int
							// )
							// geoNum
							// .
							// getSliderWidth
							// (
							// )
							// ;
							geoNum.setSliderFixed(false);

							if (x + sliderWidth > screenSize.width)
								geoNum.setAbsoluteScreenLoc(
										x = screenSize.width - sliderWidth
												- furnitureWidth, y);
							if (y + sliderHeight > screenSize.height)
								geoNum.setAbsoluteScreenLoc(x,
										y = screenSize.height - sliderHeight
												- furnitureHeight);
							geoNum.setSliderFixed(fixed);
						}

					i++;
				}

				// Michael Borcherds 2007-04-27 END

				// update GUI: size of euclidian view was set
				app.updateContentPaneAndSize();
			} else {
				app.updateContentPane();
			}
		}

		return success;
	}

	// Added for Intergeo File Format (Yves Kreis) -->
	/*
	 * PropertyChangeListener implementation to handle file filter changes
	 */
	private class FileFilterChangedListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (fileChooser.getFileFilter() instanceof geogebra.MyFileFilter) {
				String fileName = null;
				if (fileChooser.getSelectedFile() != null) {
					fileName = fileChooser.getSelectedFile().getName();
				}
				if (Application.MAC_OS) {
					fileName = getFileNameMAC(fileName);
				} else {
					fileName = getFileName(fileName);
				}
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

		private String getFileNameMAC(String fileName) {
			if (fileChooser.getUI() instanceof apple.laf.AquaFileChooserUI) {
				apple.laf.AquaFileChooserUI ui = (apple.laf.AquaFileChooserUI) fileChooser.getUI();
				return ui.getFileName();
			} else if (fileChooser.getUI() instanceof apple.laf.CUIAquaFileChooser) {
				apple.laf.CUIAquaFileChooser ui = (apple.laf.CUIAquaFileChooser) fileChooser
						.getUI();
				return ui.getFileName();
			} else if (fileName == null) {
				Application.debug("Unknown UI in JFileChooser: "
						+ fileChooser.getUI().getClass());
			}
			return fileName;
		}

		private String getFileName(String fileName) {
			if (fileChooser.getUI() instanceof BasicFileChooserUI) {
				BasicFileChooserUI ui = (BasicFileChooserUI) fileChooser
						.getUI();
				return ui.getFileName();
			} else if (fileName == null) {
				Application.debug("Unknown UI in JFileChooser: "
						+ fileChooser.getUI().getClass());
			}
			return fileName;
		}
	}

	// <-- Added for Intergeo File Format (Yves Kreis)

	

	private void initActions() {	
		if (showAxesAction != null) return;
		
		showAxesAction = new AbstractAction(app.getMenu("Axes"),
				app.getImageIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle axes
				boolean bothAxesShown = app.getEuclidianView().getShowXaxis()
						&& app.getEuclidianView().getShowYaxis();
				app.getEuclidianView().showAxes(!bothAxesShown, !bothAxesShown);
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
				app.getEuclidianView().showGrid(!app.getEuclidianView().getShowGrid());
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
				app.setWaitCursor();
				kernel.undo();
				updateActions();
				app.setDefaultCursor();
				System.gc();
			}
		};

		redoAction = new AbstractAction(app.getMenu("Redo"),
				app.getImageIcon("edit-redo.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				if (propDialog != null && propDialog.isShowing())
					propDialog.cancel();

				app.setWaitCursor();
				kernel.redo();
				updateActions();
				app.setDefaultCursor();
				System.gc();
			}
		};
		
		updateActions();
	}

	public void updateActions() {
		if (app.isUndoActive() && undoAction != null) {
			undoAction.setEnabled(kernel.undoPossible());
			redoAction.setEnabled(kernel.redoPossible());
		}				
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
	 * Creates a new text at given startPoint
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
		return handler.num;
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
		Object[] ret = { handler.num, id };
		return ret;
	}

	private class NumberInputHandler implements InputHandler {
		NumberValue num = null;

		public boolean processInput(String inputString) {
			GeoElement[] result = kernel.getAlgebraProcessor()
					.processAlgebraCommand(inputString, false);
			boolean success = result != null && result[0].isNumberValue();
			if (success) {
				num = (NumberValue) result[0];
			}
			return success;
		}
	}
	
	 public void setToolBarDefinition(String toolBarDefinition) {    	  
	    	strCustomToolbarDefinition = toolBarDefinition;    	
	    }

	    public String getToolBarDefinition() {
	    	if (strCustomToolbarDefinition == null && appToolbarPanel != null)
	    		return appToolbarPanel.getDefaultToolbarString();
	    	else
	    		return strCustomToolbarDefinition;
	    }        
	  

	    public void removeFromToolbarDefinition(int mode) {    	
	    	if (strCustomToolbarDefinition != null) {    		
	    		//Application.debug("before: " + strCustomToolbarDefinition + ",  delete " + mode);
	    		
	    		strCustomToolbarDefinition = 
	    			strCustomToolbarDefinition.replaceAll(Integer.toString(mode), "");
	    		
	    		if (mode >= EuclidianView.MACRO_MODE_ID_OFFSET) {
	    			// if a macro mode is removed all higher macros get a new id (i.e. id-1)
	    			int lastID = kernel.getMacroNumber() + EuclidianView.MACRO_MODE_ID_OFFSET-1;
	    			for (int id=mode+1; id <= lastID; id++) {
	    				strCustomToolbarDefinition = 
	    					strCustomToolbarDefinition.replaceAll(Integer.toString(id), Integer.toString(id-1));
	    			}
	    		}

	    		//Application.debug("after: " + strCustomToolbarDefinition);
	    	}    	
	    }
	    
	    public void addToToolbarDefinition(int mode) {    	
	    	if (strCustomToolbarDefinition != null) {
	    		strCustomToolbarDefinition = 
	    			strCustomToolbarDefinition + " | " + mode;
	    	}  
	    }
	    
	    public void showURLinBrowser(URL url) {
	        //	if (applet != null) {
	         //   	applet.getAppletContext().showDocument(url, "_blank");
	         //   } else {
	            	BrowserLauncher.openURL(url.toExternalForm());
	          //  }
	        }
	    

	    public void openHelp(String command) {
	    	String internalCmd = null;
	    	
	    	if (command != null)
	        try { // convert eg uppersum to UpperSum
	         	internalCmd = app.translateCommand(command);
	            String command2 = app.getCommand(internalCmd);
	            if (command2 != null && command2 != "")
	            	command = command2;
	        }
	        catch (Exception e) {}
	        
	        try{   	
	        	URL helpURL = getHelpURL(app.getLocale(), command, internalCmd);
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
	    
	   

	    private URL getHelpURL(Locale locale, String command, String intCommand) throws Exception {
	    	 // try to get help for current locale (language + country + variant)
	        URL helpURL = getHelpURL(locale.toString(), command);

	        if (helpURL != null) {        	
	        	return helpURL;
	        }
	    	       
	        /* Michael Borcherds 2008-03-26
	         * removed and replaced with dummy help files which redirect
	        // try to get help for current language
	        String  language = locale.getLanguage();     
	        helpURL = getHelpURL(language);
	        if (helpURL != null) {        	
	        	return helpURL;
	        }
	                
	        // for Catalan and Basque we take the 
	        // Spanish help instead of the English one
	        if (language.equals("eu") || language.equals("ca")) {        	
	        	helpURL = getHelpURL("es"); // Spanish
	        	if (helpURL != null) {            	
	            	return helpURL;
	            }
	        }
	        */
	                
	        // last attempt: try to get English help 
	        helpURL = getHelpURL("en", intCommand);
	        if (helpURL != null) {        	
	        	return helpURL;
	        }
	        
	        // sorry, no help available
	        throw new Exception("HelpNotFound");
	    }
	    
	    private URL getHelpURL(String languageISOcode, String command)  {
	    	// try to get help for given language
	    	String strFile;
	    	if (command == null)
	    	{ // ORIGINAL CODE
	    		strFile = "docu" + languageISOcode + "/index.html";
	    	}
	    	else
	    	{ // TEST CODE
	    		// URL like http://www.geogebra.org/help/docuen/topics/UpperSum.html
	    		strFile = "docu" + languageISOcode + "/topics/" + command + ".html";
	    	}
			String strURL = Application.GEOGEBRA_WEBSITE + "help/" + strFile;  
			
			if (Application.MAC_OS) {
				String path = app.getCodeBase().getPath();
	        	int i = path.lastIndexOf("/Java/");
	        	if (i > -1) strFile = path.substring(0, i) + "/Help/" + strFile;
	        }
	        
	        try {
	            File f = new File(strFile);
	            if (f.exists())
					return f.toURL();
				else { // web url 
	                URL url =   new URL(strURL);
	                if (Util.existsHttpURL(url)) return url;
	            }
	        } catch (Exception e) {        	
	        }
	        return null;
	    }



	    /**
	     * Returns text "Created with <ApplicationName>" and link
	     * to application homepage in html.
	     */
	    public String getCreatedWithHTML() {
	        StringBuffer sb = new StringBuffer();
	        sb.append(Util.toHTMLString(app.getPlain("CreatedWith"))); // MRB 2008-06-14 added Util.toHTMLString
	        sb.append(" ");
	        sb.append("<a href=\"");
	        sb.append(Application.GEOGEBRA_WEBSITE);
	        sb.append("\" target=\"_blank\" >");
	        sb.append("GeoGebra");
	        sb.append("</a>");
	        return sb.toString();
	    }
	    
	    public void setMode(int mode) {  
	    	
	    	// if the properties dialog is showing, close it
	     	if (propDialog != null && propDialog.isShowing()) {    		
	     		propDialog.setVisible(false);	
	     	}
	     	
	        if (algebraView != null)
	        	algebraView.reset();
	        
	        app.getEuclidianView().setMode(mode);
	        
	        if (algebraInput != null && app.showAlgebraInput()) {
	        	if (mode == EuclidianView.MODE_ALGEBRA_INPUT) {
	            	app.setCurrentSelectionListener(algebraInput.getTextField());            	                       	
	            	algebraInput.getInputButton().setSelected(true);
	            }
	        	else {
	        		algebraInput.getInputButton().setSelected(false);
	        	}
	        }                
	        
	        // select toolbar button
	        setToolbarMode(mode);	         
	    }
	    
	    public void setToolbarMode(int mode) {
	    	 if (appToolbarPanel == null) return;
	    	 
        	appToolbarPanel.setSelectedMode(mode);
        			        	
        	// check if toolbar shows selected mode
    		// if not we set the first mode of toolbar	    		
			try {				
				if (appToolbarPanel.getSelectedMode() != mode) {
					int firstMode = appToolbarPanel.getFirstMode();
					if (firstMode > 0)
						setMode(firstMode);
				}
			} catch (Exception e) {
				// ignore nullpoint exception
			}		    				     
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
	    	 
		/*
		 * KeyEventDispatcher implementation to handle key events globally for the
		 * application
		 */
		public boolean dispatchKeyEvent(KeyEvent e) {		

			boolean consumed = false;
			Object source = e.getSource();		

			// catch all key events from algebra view and give
			// them to the algebra controller
			if (source == algebraView) {
				switch (e.getID()) {
				case KeyEvent.KEY_PRESSED:
					consumed = algebraController.keyPressedConsumed(e);
					break;
				}
			}
			if (consumed)
				return true;

			switch (e.getKeyCode()) {
			case KeyEvent.VK_F3:
				// F3 key: set focus to input field				
				if (algebraInput != null) {
					algebraInput.requestFocus();
					consumed = true;
				}
				break;

			// ESC changes to move mode
			case KeyEvent.VK_ESCAPE:
				// ESC is also handeled by algebra input field
				if (algebraInput != null && algebraInput.hasFocus()) {
					consumed = false;
				} else {						
					setMode(EuclidianView.MODE_MOVE);
					consumed = true;
				}
				break;
			}
			if (consumed)
				return true;		

			return consumed;
		}
		

}
