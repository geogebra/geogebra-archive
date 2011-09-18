
package geogebra.gui.view.spreadsheet;

import geogebra.gui.inputfield.MyTextField;
import geogebra.gui.view.Gridable;
import geogebra.gui.view.spreadsheet.statdialog.StatDialog;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.main.Application;
import geogebra.main.settings.AbstractSettings;
import geogebra.main.settings.SettingListener;
import geogebra.main.settings.SpreadsheetSettings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

public class SpreadsheetView extends JPanel implements 
View, ComponentListener, FocusListener, Gridable, SettingListener
{

	private static final long serialVersionUID = 1L;

	// ggb fields
	protected Application app;
	private Kernel kernel;

	// spreadsheet table and row header
	protected MyTable table;
	protected DefaultTableModel tableModel;
	private SpreadsheetRowHeader rowHeader;
	private JTableHeader tableHeader;

	// if these are increased above 32000, you need to change traceRow to an int[]
	public static int MAX_COLUMNS = 9999; // TODO make sure this is actually used
	public static int MAX_ROWS = 9999; // TODO make sure this is actually used

	private static int DEFAULT_COLUMN_WIDTH = 70;
	public static final int ROW_HEADER_WIDTH = 35; // wide enough for "9999"

	public int highestUsedColumn = -1; // for trace & print
	public int highestUsedRow = -1; //for print


	//TODO move this out
	private SpreadsheetTraceManager traceManager;
	private TraceDialog traceDialog;


	//fields for split panel, fileBrowser and stylebar
	private JScrollPane spreadsheet;
	private FileBrowserPanel fileBrowser;
	private int defaultDividerLocation = 150;
	private SpreadsheetStyleBar styleBar;
	private JPanel restorePanel;


	// toolbar manager
	SpreadsheetToolbarManager toolbarManager;

	private StatDialog oneVarStatDialog;
	private StatDialog twoVarStatDialog;
	private StatDialog multiVarStatDialog;


	// file browser defaults
	public static final String DEFAULT_URL = "http://www.geogebra.org/static/data/data.xml";
	public static final int DEFAULT_MODE = FileBrowserPanel.MODE_FILE;
	//private int initialBrowserMode = DEFAULT_MODE;
	// file browser settings
	//private String initialURL = DEFAULT_URL;



	// current toolbar mode
	private int mode = -1;


	private JSplitPane splitPane;
	private FormulaBar formulaBar;
	private JPanel spreadsheetPanel;

	private SpreadsheetViewDnD dndHandler;




	/******************************************************
	 * Construct spreadsheet view as a split panel. 
	 * Left panel holds file tree browser, right panel holds spreadsheet. 
	 */
	public SpreadsheetView(Application app, int columns, int rows) {

		this.app = app;
		kernel = app.getKernel();

		// Initialize settings and register listener
		app.getSettings().getSpreadsheet().addListener(this);


		// Build the spreadsheet table and enclosing scrollpane
		buildSpreadsheet(rows, columns);

		// Build the spreadsheet panel: formulaBar above, spreadsheet in Center  
		spreadsheetPanel = new JPanel(new BorderLayout());
		spreadsheetPanel.add(spreadsheet,BorderLayout.CENTER);

		// Set the spreadsheet panel as the right component of this JSplitPane
		splitPane = new JSplitPane();
		splitPane.setRightComponent(spreadsheetPanel);	
		splitPane.setBorder(BorderFactory.createEmptyBorder());

		// Set the browser as the left component or to null if showBrowserPanel == false
		setShowFileBrowser(settings().showBrowserPanel());  


		setLayout(new BorderLayout());
		add(splitPane,BorderLayout.CENTER);

		setBorder(BorderFactory.createEmptyBorder());
		addFocusListener(this);
		updateFonts();
		attachView();


		// Create tool bar manager to handle tool bar mode changes
		toolbarManager = new SpreadsheetToolbarManager(app, this);


		// Create spreadsheet trace manager
		// TODO move this out of the spreadsheet
		traceManager = new SpreadsheetTraceManager(this);

		dndHandler = new SpreadsheetViewDnD(app, this);

		settingsChanged(settings());

	}


	private void buildSpreadsheet(int rows, int columns){

		// Create the spreadsheet table model and the table
		tableModel = new DefaultTableModel(rows, columns);
		table = new MyTable(this, tableModel);

		// Create row header
		rowHeader = new SpreadsheetRowHeader(app,table);

		// Set column width
		table.headerRenderer.setPreferredSize(new Dimension((int)(table.preferredColumnWidth)
				, (int)(MyTable.TABLE_CELL_HEIGHT)));


		// Put the table and the row header into a scroll plane
		// The scrollPane is named as spreadsheet
		spreadsheet = new JScrollPane();
		spreadsheet.setBorder(BorderFactory.createEmptyBorder());
		spreadsheet.setRowHeaderView(rowHeader);
		spreadsheet.setViewportView(table);

		// save the table header 
		tableHeader = table.getTableHeader();

		// Create and set the scrollpane corners
		Corner upperLeftCorner = new Corner(); //use FlowLayout
		upperLeftCorner.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MyTable.HEADER_GRID_COLOR));		
		upperLeftCorner.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				table.selectAll();
			}
		});
		spreadsheet.setCorner(JScrollPane.UPPER_LEFT_CORNER, upperLeftCorner);
		spreadsheet.setCorner(JScrollPane.LOWER_LEFT_CORNER, new Corner());
		spreadsheet.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new Corner());


		// Add a resize listener to the table so it can auto-enlarge if needed
		table.addComponentListener(this); 

	}


	//===============================================================
	//             Corners
	//===============================================================



	private static class Corner extends JComponent {
		private static final long serialVersionUID = -4426785169061557674L;

		protected void paintComponent(Graphics g) {
			g.setColor(MyTable.BACKGROUND_COLOR_HEADER);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}



	//===============================================================
	//             Defaults
	//===============================================================


	public void setDefaultLayout() {
		setShowGrid(true);
		setShowRowHeader(true);
		setShowColumnHeader(true);
		setShowVScrollBar(true);
		setShowHScrollBar(true);
		setShowFileBrowser(false);
		setAllowSpecialEditor(false);
	}

	public void setDefaultSelection() {
		setSpreadsheetScrollPosition(0,0);
		table.setInitialCellSelection(0,0);
	}




	//===============================================================
	//              getters/setters
	//===============================================================


	public Application getApplication() {
		return app;
	}

	public MyTable getTable() {
		return table;
	}


	public JViewport getRowHeader(){
		return spreadsheet.getRowHeader();
	}

	public JViewport getColumnHeader(){
		return spreadsheet.getColumnHeader();
	}

	public JTableHeader getTableHeader(){
		return tableHeader;
	}
	
	public int getMode() {
		return mode;
	}



	/** 
	 * get spreadsheet styleBar 
	 */
	public SpreadsheetStyleBar getSpreadsheetStyleBar(){
		if(styleBar==null){
			styleBar = new SpreadsheetStyleBar(this);
		}
		return styleBar;
	}


	//===============================================================
	//              VIEW Implementation
	//===============================================================


	public void attachView() {
		//clearView();
		kernel.notifyAddAll(this);
		kernel.attach(this);		
	}

	public void detachView() {
		kernel.detach(this);
		//clearView();
		//kernel.notifyRemoveAll(this);		
	}


	public void add(GeoElement geo) {	
		//Application.debug(new Date() + " ADD: " + geo);				

		update(geo);	
		Point location = geo.getSpreadsheetCoords();

		// autoscroll to new cell's location
		if (scrollToShow && location != null )
			table.scrollRectToVisible(table.getCellRect(location.y, location.x, true));

		//Application.debug("highestUsedColumn="+highestUsedColumn);

	}


	public void remove(GeoElement geo) {
		//Application.debug(new Date() + " REMOVE: " + geo);

		if(traceManager.isTraceGeo(geo)){
			traceManager.removeSpreadsheetTraceGeo(geo);
			if(isTraceDialogVisible())
				traceDialog.updateTraceDialog();
		}

		Point location = geo.getSpreadsheetCoords();
		if (location != null) {
			doRemove(geo, location.y, location.x);
		}


		if(geo.isGeoBoolean()){
			table.oneClickEditMap.remove(location);
		}
	}

	private void doRemove(GeoElement geo, int row, int col) {

		tableModel.setValueAt(null, row, col);
		updateHighestUsedColAndRow(col,row);

		//Application.debug("highestUsedColumn="+highestUsedColumn);
	}



	public void rename(GeoElement geo) {
		//Application.debug(new Date() + " RENAME");
		Point location = geo.getOldSpreadsheetCoords();
		if (location != null) {
			doRemove(geo, location.y, location.x);
		}

		add(geo);

		if(traceManager.isTraceGeo(geo))
			traceManager.updateTraceSettings(geo);
		if(isTraceDialogVisible()){
			traceDialog.updateTraceDialog();
		}

	}


	public void updateAuxiliaryObject(GeoElement geo) {		
	}

	public void repaintView() {	
		repaint();		
	}

	public void clearView() {

		//Application.debug(new Date() + " CLEAR VIEW");

		//clear the table model
		int rows = tableModel.getRowCount();
		int columns = tableModel.getColumnCount();
		for (int c = 0; c < columns; ++c) {
			for (int r = 0; r < rows; ++r) {
				tableModel.setValueAt(null, r, c);
			}
		}

		setDefaultLayout();
		setDefaultSelection();
		table.oneClickEditMap.clear();

	}



	/** Respond to changes in Euclidean mode sent by GUI manager */
	public void setMode(int mode){

		this.mode = mode;

		if(isTraceDialogVisible()){
			traceDialog.toolbarModeChanged(mode);
		}

		// String command = kernel.getModeText(mode); // e.g. "Derivative"

		toolbarManager.handleModeChange(mode);

	}



	/**
	 * Clear table and set to default layout. 
	 * This method is called on startup or when new window is called
	 */
	public void restart() {

		clearView();
		//setDefaultLayout();
		//setDefaultSelection();
		highestUsedColumn = -1;
		highestUsedRow = -1;
		updateColumnWidths();
		updateFonts(); //G.Sturr 2010-6-4
		//table.changeSelection(0,0,false,false);
		traceManager.loadTraceGeoCollection();

		table.oneClickEditMap.clear();


		if(oneVarStatDialog != null)
			oneVarStatDialog.setVisible(false);
		if(twoVarStatDialog != null)
			twoVarStatDialog.setVisible(false);
		if(multiVarStatDialog != null)
			multiVarStatDialog.setVisible(false);

		// clear the formats and call settingsChanged 
		settings().setCellFormat(null);
		
	}	

	/** Resets spreadsheet after undo/redo call. */
	public void reset() {
		if(traceManager != null)
			traceManager.loadTraceGeoCollection();
	}	


	public void update(GeoElement geo) {
		Point location = geo.getSpreadsheetCoords();
		if (location != null && location.x < MAX_COLUMNS && location.y < MAX_ROWS) {

			if (location.x > highestUsedColumn) highestUsedColumn = location.x;
			highestUsedRow=Math.max(highestUsedRow, location.y);

			if (location.y >= tableModel.getRowCount()) {
				tableModel.setRowCount(location.y + 1);		
				spreadsheet.getRowHeader().revalidate();
			}
			if (location.x >= tableModel.getColumnCount()) {
				table.setMyColumnCount(location.x + 1);		
				JViewport cH = spreadsheet.getColumnHeader();

				// bugfix: double-click to load ggb file gives cH = null
				if (cH != null) cH.revalidate();
			}
			tableModel.setValueAt(geo, location.y, location.x);

			//G.Sturr 2010-4-2
			//Mark this cell to be resized by height
			table.cellResizeHeightSet.add(new Point(location.x, location.y));

			// add tracing geos to the trace collection
			if(geo.getSpreadsheetTrace()){
				traceManager.addSpreadsheetTraceGeo(geo);
			}

			// put geos with special editors in the oneClickEditMap 
			if(geo.isGeoBoolean() || geo.isGeoButton() || geo.isGeoList()){
				table.oneClickEditMap.put(location, geo);
			}
		}
	}	

	

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	/**
	 * Updates highestUsedColumn
	 */
	private void updateHighestUsedColAndRow(int col, int row) {

		if (col==highestUsedColumn){
			boolean updatedHighestUsedColumn=false;
			for (int c=highestUsedColumn;c>=0;c--){
				boolean columnEmpty = true;
				for (int r = 0;r<=highestUsedRow; r++) {
					if (tableModel.getValueAt(r, c) != null) {
						// column not empty
						columnEmpty = false;
						break;
					}
				}
				if (!columnEmpty){
					highestUsedColumn=c;
					updatedHighestUsedColumn=true;
					break;
				}
			}
			if (!updatedHighestUsedColumn){
				highestUsedColumn=-1;
			}
		}
		if (row==highestUsedRow){
			boolean updatedHighestUsedRow=false;
			for (int r=highestUsedRow;r>=0;r--){
				boolean rowEmpty = true;
				for (int c = 0;c<=highestUsedColumn; c++) {
					if (tableModel.getValueAt(r, c) != null) {
						//row not empty
						rowEmpty = false;
						break;
					}
				}
				if (!rowEmpty){
					highestUsedRow=r;
					updatedHighestUsedRow=true;
					break;
				}
			}
			if (!updatedHighestUsedRow){
				highestUsedRow=-1;
			}
		}
	}



	private boolean scrollToShow = false;


	public void setScrollToShow(boolean scrollToShow) {
		this.scrollToShow = scrollToShow;
	}


	//=====================================================
	//               Formula Bar
	//=====================================================


	public FormulaBar getFormulaBar() {
		if(formulaBar == null){
			// Build the formula bar
			formulaBar = new FormulaBar(app, this); 
			formulaBar.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0,0,1,0,SystemColor.controlShadow), 
					BorderFactory.createEmptyBorder(4, 4, 4, 4)));
		}
		return formulaBar;
	}

	public void updateFormulaBar(){
		if(formulaBar != null && settings().showFormulaBar())
			formulaBar.update();
	}




	//=====================================================
	//               Stat Dialogs
	//=====================================================



	public void showStatDialog(int mode){

		if(app.getSelectedGeos().size() == 0) return;

		switch(mode){
		case StatDialog.MODE_ONEVAR:
			if(oneVarStatDialog == null){
				oneVarStatDialog = new StatDialog(this, app, mode);
			}else{
				oneVarStatDialog.setLeftToRight(true);
				oneVarStatDialog.updateDialog(true);
			}
			oneVarStatDialog.setVisible(true);	
			break;

		case StatDialog.MODE_REGRESSION:
			if(twoVarStatDialog == null){
				twoVarStatDialog = new StatDialog(this, app, mode);
			}else{
				twoVarStatDialog.updateDialog(true);
			}
			twoVarStatDialog.setVisible(true);	
			break;

		case StatDialog.MODE_MULTIVAR:
			if(multiVarStatDialog == null){
				multiVarStatDialog = new StatDialog(this, app, mode);
			}else{
				multiVarStatDialog.updateDialog(true);
			}
			multiVarStatDialog.setVisible(true);	
			break;


		}

	}


	public boolean isStatDialogVisible(){
		boolean oneVarVisible = oneVarStatDialog != null && oneVarStatDialog.isVisible();
		boolean twoVarVisible = oneVarStatDialog != null && oneVarStatDialog.isVisible();
		return oneVarVisible || twoVarVisible;
	}


	public void notifySpreadsheetSelectionChange(){
		if(isStatDialogVisible()){
			//oneVarStatDialog.handleSpreadsheetSelectionChange();
		}

	}





	//=====================================================
	//               Tracing
	//=====================================================



	public SpreadsheetTraceManager getTraceManager() {
		if (traceManager == null)
			traceManager = new SpreadsheetTraceManager(this);
		return traceManager;
	}


	public void showTraceDialog(GeoElement geo, CellRange traceCell){
		if (traceDialog == null){
			traceDialog = new TraceDialog(app, geo, traceCell);
		}else{
			traceDialog.setTraceDialogSelection(geo, traceCell);
		}
		traceDialog.setVisible(true);		
	}

	public boolean isTraceDialogVisible(){
		return (traceDialog != null && traceDialog.isVisible());
	}

	public CellRange getTraceSelectionRange(int anchorColumn, int anchorRow){
		if (traceDialog == null){
			return null;
		}else{
			return traceDialog.getTraceSelectionRange(anchorColumn, anchorRow);
		}
	}

	public void setTraceDialogMode(boolean enableMode){
		if(enableMode){
			table.setSelectionRectangleColor(Color.GRAY);
			//table.setFocusable(false);
		}
		else{
			table.setSelectionRectangleColor(table.SELECTED_RECTANGLE_COLOR);
			//table.setFocusable(true);
		}
	}


	public int getHighestUsedColumn() {
		//traceHandler.resetTraceRow(highestUsedColumn+1);
		//traceHandler.resetTraceRow(highestUsedColumn+2);
		return highestUsedColumn;
	}
	
	public int getHighestUsedRow() {
		return highestUsedRow;
	}




	//===============================================================
	//             XML 
	//===============================================================



	/**
	 * returns settings in XML format
	 */
	public void getXML(StringBuilder sb, boolean asPreference) {
		sb.append("<spreadsheetView>\n");

		int width = getWidth();//getPreferredSize().width;
		int height = getHeight();//getPreferredSize().height;

		sb.append("\t<size ");
		sb.append(" width=\"");
		sb.append(width);
		sb.append("\"");
		sb.append(" height=\"");
		sb.append(height);
		sb.append("\"");
		sb.append("/>\n");


		sb.append("\t<prefCellSize ");
		sb.append(" width=\"");
		sb.append(table.preferredColumnWidth);
		sb.append("\"");
		sb.append(" height=\"");
		sb.append(table.getRowHeight());
		sb.append("\"");
		sb.append("/>\n");

		
		if(!asPreference){

			// column widths 
			for (int col = 0 ; col < table.getColumnCount() ; col++) {
				TableColumn column = table.getColumnModel().getColumn(col); 
				int colWidth = column.getWidth();
				//if (colWidth != DEFAULT_COLUMN_WIDTH)
				if (colWidth !=	table.preferredColumnWidth)
					sb.append("\t<spreadsheetColumn id=\""+col+"\" width=\""+colWidth+"\"/>\n");
			}

			// row heights 
			for (int row = 0 ; row < table.getRowCount() ; row++) {
				int rowHeight = table.getRowHeight(row);
				if (rowHeight != table.getRowHeight())
					sb.append("\t<spreadsheetRow id=\""+row+"\" height=\""+rowHeight+"\"/>\n");
			}

			// initial selection
			sb.append("\t<selection ");

			sb.append(" hScroll=\"");
			sb.append(spreadsheet.getHorizontalScrollBar().getValue());
			sb.append("\"");

			sb.append(" vScroll=\"");
			sb.append(spreadsheet.getVerticalScrollBar().getValue());
			sb.append("\"");

			sb.append(" column=\"");
			sb.append(table.getColumnModel().getSelectionModel().getAnchorSelectionIndex());
			sb.append("\"");

			sb.append(" row=\"");
			sb.append(table.getSelectionModel().getAnchorSelectionIndex());
			sb.append("\"");

			sb.append("/>\n");
		}



		// layout
		sb.append("\t<layout ");

		sb.append(" showFormulaBar=\"");
		sb.append(settings().showFormulaBar()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" showGrid=\"");
		sb.append(settings().showGrid()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" showHScrollBar=\"");
		sb.append(settings().showHScrollBar()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" showVScrollBar=\"");
		sb.append(settings().showVScrollBar()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" showBrowserPanel=\"");
		sb.append(settings().showBrowserPanel()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" showColumnHeader=\"");
		sb.append(settings().showColumnHeader()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" showRowHeader =\"");
		sb.append(settings().showRowHeader()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" allowSpecialEditor=\"");
		sb.append(settings().allowSpecialEditor()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" allowToolTips=\"");
		sb.append(settings().allowToolTips()  ? "true" : "false" );
		sb.append("\"");

		sb.append(" equalsRequired=\"");
		sb.append(settings().equalsRequired()  ? "true" : "false" );
		sb.append("\"");

		sb.append("/>\n");

		//---- end layout



		// file browser
		if(fileBrowser != null){
			sb.append("\t<spreadsheetBrowser ");

			if(!settings().initialFilePath().equals(settings().defaultFile()) 
					|| settings().initialURL() != DEFAULT_URL 
					|| settings().initialBrowserMode() != DEFAULT_MODE)
			{	
				sb.append(" default=\"");
				sb.append("false");
				sb.append("\"");	

				sb.append(" dir=\"");
				sb.append(settings().initialFilePath());
				sb.append("\"");

				sb.append(" URL=\"");
				sb.append(settings().initialURL());
				sb.append("\"");

				sb.append(" mode=\"");
				sb.append(settings().initialBrowserMode());
				sb.append("\"");	

			}else{

				sb.append(" default=\"");
				sb.append("true");
				sb.append("\"");	
			}

			sb.append("/>\n");
		}


		//cell formats
		if(!asPreference)
			table.getCellFormatHandler().getXML(sb);



		sb.append("</spreadsheetView>\n");

		//	Application.debug(sb);

	}






	//===============================================================
	//             Update 
	//===============================================================


	public void setLabels(){
		if(traceDialog !=null)
			traceDialog.setLabels();
		if(oneVarStatDialog !=null)
			oneVarStatDialog.setLabels();
		if(twoVarStatDialog !=null)
			twoVarStatDialog.setLabels();
		if(multiVarStatDialog !=null)
			multiVarStatDialog.setLabels();

		if (table !=null)
			table.setLabels();
		if(formulaBar != null){
			formulaBar.setLabels();
		}
	}


	public void updateFonts() {

		Font font = app.getPlainFont();

		MyTextField dummy = new MyTextField(app);
		dummy.setFont(font);
		dummy.setText("9999");  // for row header width
		int h = dummy.getPreferredSize().height;
		int w = dummy.getPreferredSize().width;	
		rowHeader.setFixedCellWidth(w);	

		//TODO: column widths are not set from here
		// need to revise updateColumnWidths() to do this correctly
		dummy.setText("MMMMMMMMMM");  // for column width
		h = dummy.getPreferredSize().height;
		w = dummy.getPreferredSize().width;
		table.setRowHeight(h);
		table.setPreferredColumnWidth(w);
		table.headerRenderer.setPreferredSize(new Dimension(w, h));

		table.setFont(app.getPlainFont());

		table.headerRenderer.setFont(font);

		// Adjust row heights for tall LaTeX images
		table.fitAll(true, false); 

		if(fileBrowser != null)
			fileBrowser.updateFonts();
		if(this.oneVarStatDialog != null)
			oneVarStatDialog.updateFonts();
		if(this.twoVarStatDialog != null)
			twoVarStatDialog.updateFonts();
		if(this.multiVarStatDialog != null)
			multiVarStatDialog.updateFonts();
		if(formulaBar != null)
			formulaBar.updateFonts(font);
	}



	public void setColumnWidth(int col, int width) {
		//Application.debug("col = "+col+" width = "+width);
		TableColumn column = table.getColumnModel().getColumn(col); 
		column.setPreferredWidth(width);
		//column.
	}

	public void setRowHeight(int row, int height) {
		table.setRowHeight(row, height);
	}


	public void updateColumnWidths() {
		Font font = app.getPlainFont();

		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (double)(size)/12.0;
		table.setPreferredColumnWidth((int) (MyTable.TABLE_CELL_WIDTH * multiplier));
		for (int i = 0; i < table.getColumnCount(); ++ i) {
			table.getColumnModel().getColumn(i).setPreferredWidth(table.preferredColumnWidth());
		}

	}

	public void setColumnWidthsFromSettings(){

		table.setPreferredColumnWidth(settings().preferredColumnWidth());
		HashMap<Integer,Integer> widthMap = settings().getWidthMap();
		for (int i = 0; i < table.getColumnCount(); ++ i) {
			if(widthMap.containsKey(i))
				table.getColumnModel().getColumn(i).setPreferredWidth(widthMap.get(i));
			else
				table.getColumnModel().getColumn(i).setPreferredWidth(table.preferredColumnWidth());
		}
	}

	public void setRowHeightsFromSettings(){
		HashMap<Integer,Integer> heightMap = app.getSettings().getSpreadsheet().getHeightMap();
		table.setRowHeight(app.getSettings().getSpreadsheet().preferredRowHeight());
		if(!heightMap.isEmpty()){
			for (Integer r: heightMap.keySet()) {
				table.setRowHeight(r, heightMap.get(r));
			}
		}
	}


	public void updateRowHeader() {
		if(rowHeader != null)
			rowHeader.updateRowHeader();
	}


	public void setSpreadsheetScrollPosition(int hScroll, int vScroll){
		spreadsheet.getHorizontalScrollBar().setValue(hScroll);
		spreadsheet.getVerticalScrollBar().setValue(vScroll);
	}





	// ==========================================================
	// Handle spreadsheet resize.
	//
	// Adds extra rows and columns to fill the enclosing scrollpane. 
	// This is sometimes needed when rows or columns are resized
	// or the application window is enlarged.


	/**
	 * Tests if the spreadsheet fits the enclosing scrollpane viewport.
	 * Adds rows or columns if needed to fill the viewport.
	 */
	public void expandSpreadsheetToViewport() {

		if (table.getWidth() < spreadsheet.getWidth()) {

			int newColumns = (spreadsheet.getWidth() - table.getWidth())
			/ table.preferredColumnWidth();
			table.removeComponentListener(this);
			table.setMyColumnCount(table.getColumnCount() + newColumns);
			table.addComponentListener(this);

		}
		if (table.getHeight() < spreadsheet.getHeight()) {
			int newRows = (spreadsheet.getHeight() - table.getHeight())
			/ table.getRowHeight();
			table.removeComponentListener(this);
			tableModel.setRowCount(table.getRowCount() + newRows);
			table.addComponentListener(this);

		}

		// if table has grown after resizing all rows or columns, then select
		// all again
		// TODO --- why doesn't this work:
		/*
		 * if(table.isSelectAll()){ table.selectAll(); }
		 */

	}

	// Listener for a resized column or row

	public void componentResized(ComponentEvent e) {
		expandSpreadsheetToViewport();
	}

	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}






	//===============================================================
	//             Data Import & File Browser 
	//===============================================================



	public boolean loadSpreadsheetFromURL(File f) {

		boolean succ = false;

		URL url = null;
		try {
			url = f.toURI().toURL();
			succ = loadSpreadsheetFromURL(url); 
		} 

		catch (IOException ex) {
			ex.printStackTrace();
		}

		return succ;
	}

	public boolean loadSpreadsheetFromURL(URL url) {

		boolean succ = table.copyPasteCut.pasteFromURL(url);
		if (succ) {
			app.storeUndoInfo();
		}
		return succ;
	}


	public FileBrowserPanel getFileBrowser() {		
		if (fileBrowser == null && app.hasFullPermissions()) {
			fileBrowser = new FileBrowserPanel(this);
			fileBrowser.setMinimumSize(new Dimension(50, 0));
			//initFileBrowser();
			//fileBrowser.setRoot(settings.initialPath(), settings.initialBrowserMode());
		}	
		return fileBrowser;
	}


	public void setShowFileBrowser(boolean showFileBrowser) {

		if (showFileBrowser) {
			splitPane.setLeftComponent(getFileBrowser());
			splitPane.setDividerLocation(defaultDividerLocation);
			splitPane.setDividerSize(4);
			initFileBrowser();

		} else {
			splitPane.setLeftComponent(null);
			splitPane.setLastDividerLocation(splitPane.getDividerLocation());
			splitPane.setDividerLocation(0);
			splitPane.setDividerSize(0);
		}

	}

	public boolean getShowBrowserPanel(){
		return settings().showBrowserPanel();

	}

	public void minimizeBrowserPanel(){
		splitPane.setDividerLocation(10);
		splitPane.setDividerSize(0);
		splitPane.setLeftComponent(getRestorePanel());
	}


	public void restoreBrowserPanel(){
		splitPane.setDividerLocation(splitPane.getLastDividerLocation());
		splitPane.setDividerSize(4);
		splitPane.setLeftComponent(getFileBrowser());

	}


	/**
	 * Returns restorePanel, if none exists a new one is built.
	 * RestorePanel is a slim vertical bar that holds the place 
	 * of the minimized fileBrowser. When clicked it restores the
	 * file browser to full size. 
	 * 
	 */
	public JPanel getRestorePanel() {
		if (restorePanel == null) {
			restorePanel = new JPanel();
			restorePanel.setMinimumSize(new Dimension(10,0));
			restorePanel.setBorder(BorderFactory.createEtchedBorder(1));
			restorePanel.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					restoreBrowserPanel();
				}
				public void mouseEntered(MouseEvent e) {
					restorePanel.setBackground(Color.LIGHT_GRAY);
				}
				public void mouseExited(MouseEvent e) {
					restorePanel.setBackground(null);
				}
			});
		}
		restorePanel.setBackground(null);
		return restorePanel;
	}



	public int getInitialBrowserMode() {
		return settings().initialBrowserMode();
	}


	public String getInitialURLString() {
		return settings().initialURL();
	}


	public String getInitialFileString() {
		return settings().initialFilePath();
	}


	public void setBrowserDefaults(boolean doRestore){

		if(doRestore){
			settings().setInitialFilePath(settings().defaultFile());
			settings().setInitialURL(DEFAULT_URL);
			settings().setInitialBrowserMode(FileBrowserPanel.MODE_FILE);
			//initFileBrowser();

		}else{
			settings().setInitialFilePath(fileBrowser.getRootString());
			settings().setInitialBrowserMode(fileBrowser.getMode());
		}
	}

	public void initFileBrowser(){
		// don't init file browser without full permissions (e.g. unsigned applets)
		if(!app.hasFullPermissions() || !settings().showBrowserPanel()) return;

		if(settings().initialBrowserMode() == FileBrowserPanel.MODE_FILE)
			setFileBrowserDirectory(settings().initialFilePath(), settings().initialBrowserMode());
		else
			setFileBrowserDirectory(settings().initialURL(), settings().initialBrowserMode());
	}

	public boolean setFileBrowserDirectory(String rootString, int mode) {
		return getFileBrowser().setRoot(rootString, mode);
	}

	/*
	public void setDefaultFileBrowserDirectory() {
		if(this.DEFAULT_MODE == FileBrowserPanel.MODE_FILE)
			setFileBrowserDirectory(String rootString, int mode)
		else
			setFileBrowserDirectory(DEFAULT_URL, FileBrowserPanel.MODE_URL);
	}
	 */


	//================================================
	//	         Spreadsheet Settings
	//================================================


	public void setShowRowHeader(boolean showRowHeader) {
		if (showRowHeader) {
			spreadsheet.setRowHeaderView(rowHeader);
		} else {
			spreadsheet.setRowHeaderView(null);
		}
	}


	public void setShowColumnHeader(boolean showColumnHeader) {
		if (showColumnHeader) {
			table.setTableHeader(tableHeader);
			spreadsheet.setColumnHeaderView(tableHeader);
		} else {
			table.setTableHeader(null);
			spreadsheet.setColumnHeaderView(null);
		}
	}


	public void setShowVScrollBar(boolean showVScrollBar) {
		if (showVScrollBar) {
			spreadsheet
			.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		} else {
			spreadsheet
			.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		}
	}


	public void setShowHScrollBar(boolean showHScrollBar) {
		if (showHScrollBar) {
			spreadsheet
			.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		} else {
			spreadsheet
			.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
	}


	public void setShowGrid(boolean showGrid) {
		table.setShowGrid(showGrid);
		if(showGrid)
			table.setIntercellSpacing(new Dimension(1,1));
		else
			table.setIntercellSpacing(new Dimension(0,0));
		getSpreadsheetStyleBar().updateStyleBar();
	}


	public boolean getAllowToolTips() {
		return settings().allowToolTips();
	}

	public void setAllowToolTips(boolean allowToolTips) {
		// do nothing yet
	}


	public void setShowFormulaBar(boolean showFormulaBar) {
		if(showFormulaBar)
			spreadsheetPanel.add(getFormulaBar(),BorderLayout.NORTH);
		else
			if(formulaBar != null)
				spreadsheetPanel.remove(formulaBar);

		if(formulaBar != null)
			formulaBar.update();
		this.revalidate();
		this.repaint();
		getSpreadsheetStyleBar().updateStyleBar();
	}

	public boolean getShowFormulaBar(){
		return settings().showFormulaBar();
	}


	public boolean isVisibleStyleBar(){
		return styleBar == null || styleBar.isVisible();
	}


	public void setColumnSelect(boolean isColumnSelect){
		//do nothing yet
	}

	public boolean isColumnSelect(){
		return settings().isColumnSelect();
	}

	public void setAllowSpecialEditor(boolean allowSpecialEditor){
		repaint();
	}

	public boolean allowSpecialEditor(){
		return settings().allowSpecialEditor();
	}

	/**
	 * sets requirement that commands entered into cells must start with "="
	 */	
	public void setEqualsRequired(boolean isEqualsRequired){
		table.setEqualsRequired(isEqualsRequired);
	}

	/**
	 * gets requirement that commands entered into cells must start with "="
	 */
	public boolean isEqualsRequired(){
		return settings().equalsRequired();
	}


	boolean allowSettingUpate = true;

	protected void updateCellFormat(String cellFormat){
		if(!allowSettingUpate) return;

		settings().removeListener(this);
		settings().setCellFormat(cellFormat);
		settings().addListener(this);
	}

	protected void updateAllRowSettings(){
		if(!allowSettingUpate) return;

		settings().removeListener(this);
		settings().setPreferredRowHeight(table.getRowHeight());
		settings().getHeightMap().clear();
		for (int row = 0 ; row < table.getRowCount() ; row++) {
			int rowHeight = table.getRowHeight(row);
			if (rowHeight != table.getRowHeight())
				settings().getHeightMap().put(row,rowHeight);
		}
		settings().addListener(this);
	}



	protected void updateRowHeightSetting(int row, int height){
		if(!allowSettingUpate) return;

		settings().removeListener(this);
		settings().getHeightMap().put(row, height);
		settings().addListener(this);
	}

	protected void updatePreferredRowHeight(int preferredRowHeight){
		if(!allowSettingUpate) return;

		settings().removeListener(this);
		settings().getHeightMap().clear();
		settings().setPreferredRowHeight(preferredRowHeight);
		settings().addListener(this);
	}


	protected void updateColumnWidth(int col, int colWidth){
		if(!allowSettingUpate) return;

		settings().removeListener(this);
		settings().getWidthMap().put(col, colWidth);
		settings().addListener(this);
	}

	protected void updatePreferredColumnWidth(int colWidth){
		if(!allowSettingUpate) return;

		settings().removeListener(this);
		settings().getWidthMap().clear();
		settings().setPreferredColumnWidth(table.preferredColumnWidth);
		settings().addListener(this);
	}


	protected void updateAllColumnWidthSettings(){
		if(!allowSettingUpate) return;

		settings().removeListener(this);
		settings().setPreferredColumnWidth(table.preferredColumnWidth);
		settings().getWidthMap().clear();
		for (int col = 0 ; col < table.getColumnCount() ; col++) {
			TableColumn column = table.getColumnModel().getColumn(col); 
			int colWidth = column.getWidth();
			if (colWidth !=	table.preferredColumnWidth)
				settings().getWidthMap().put(col, colWidth);
		}
		settings().addListener(this);
	}


	protected SpreadsheetSettings settings(){
		return app.getSettings().getSpreadsheet();
	}

	
	public void settingsChanged(AbstractSettings settings0) {

		allowSettingUpate = false;

		// layout
		setShowColumnHeader(settings().showColumnHeader());
		setShowRowHeader(settings().showRowHeader()) ;
		setShowVScrollBar(settings().showVScrollBar());
		setShowHScrollBar(settings().showHScrollBar());
		setShowGrid(settings().showGrid());
		setAllowToolTips(settings().allowToolTips());
		setShowFormulaBar(settings().showFormulaBar());
		setColumnSelect(settings().isColumnSelect());
		setAllowSpecialEditor(settings().allowSpecialEditor());
		setEqualsRequired(settings().equalsRequired());

		// browser panel
		if(app.hasFullPermissions()){
			settings().removeListener(this);
			if(settings().initialBrowserMode() < 0)
				settings().setInitialBrowserMode(FileBrowserPanel.MODE_FILE);
			if(settings().defaultFile() == null)
				settings().setDefaultFile(System.getProperty("user.dir"));
			if(settings().initialFilePath() == null)
				settings().setInitialFilePath(System.getProperty("user.dir"));
			if(settings().initialURL() == null)
				settings().setInitialURL(DEFAULT_URL);
			settings().addListener(this);
		}

		setShowFileBrowser(settings().showBrowserPanel());

		// row height and column widths		
		setColumnWidthsFromSettings();
		setRowHeightsFromSettings();

		// cell format
		getTable().getCellFormatHandler().processXMLString(settings().cellFormat());	

		// preferredSize
		this.setPreferredSize(settings().preferredSize());


		// initial position
		// TODO not working yet ... 
		//setSpreadsheetScrollPosition(settings.scrollPosition().x, settings.scrollPosition().y);
		//getTable().setInitialCellSelection(settings.selectedCell().x, settings.selectedCell().y);

		allowSettingUpate = true;

	}




	//================================================
	//	         Focus
	//================================================

	protected boolean hasViewFocus(){
		boolean hasFocus = false;
		try {
			if(app.getGuiManager().getLayout().getDockManager().getFocusedPanel() != null)
				hasFocus = app.getGuiManager().getLayout().getDockManager().getFocusedPanel().isAncestorOf(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hasFocus;
	}



	// transfer focus to the table
	public void requestFocus() {
		if (table != null)
			table.requestFocus();
	}


	// test all components of SpreadsheetView for hasFocus 
	public boolean hasFocus() {
		if (table == null)
			return false;
		return table.hasFocus()
		|| rowHeader.hasFocus()
		|| (table.getTableHeader() != null && table.getTableHeader().hasFocus())
		|| spreadsheet.getCorner(JScrollPane.UPPER_LEFT_CORNER).hasFocus()
		|| (formulaBar != null && formulaBar.hasFocus());
	}

	public void focusGained(FocusEvent arg0) {

	}


	public void focusLost(FocusEvent arg0) {
		getTable().repaint();

	}


	public int getViewID() {
		return Application.VIEW_SPREADSHEET;
	}


	public int[] getGridColwidths() {
		int[] colWidths=new int[2+highestUsedColumn];
		colWidths[0]=rowHeader.getWidth();
		for (int c=0;c<=highestUsedColumn;c++){
			colWidths[c+1]=table.getColumnModel().getColumn(c).getWidth();
		}
		return colWidths;
	}


	public int[] getGridRowHeights() {
		int[] rowHeights=new int[2+highestUsedRow];
		
		if(table.getTableHeader() == null)
			rowHeights[0] = 0;
		else
			rowHeights[0]=table.getTableHeader().getHeight();
		
		for (int r=0;r<=highestUsedRow;r++){
			rowHeights[r+1]=table.getRowHeight(r);
		}
		return rowHeights;
	}


	public Component[][] getPrintComponents() {
		return new Component[][]{
				{spreadsheet.getCorner(JScrollPane.UPPER_LEFT_CORNER), spreadsheet.getColumnHeader()},
				{spreadsheet.getRowHeader(),table}};
	}


}