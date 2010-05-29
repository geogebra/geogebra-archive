/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.view.spreadsheet;

import geogebra.gui.view.spreadsheet.SpreadsheetTraceManager.TraceSettings;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * Dialog to add/remove/edit spreadsheet traces
 * 
 * @author G. Sturr, 2010-5-14
 * 
 * 
 * TODO: handle undo 
 * 
 */

public class TraceDialog extends javax.swing.JDialog
implements 
	GeoElementSelectionListener, 
	ActionListener, 
	FocusListener, 
	ListSelectionListener, 
	WindowListener 
	
	{
	
	// external components
	private Application app;
	private SpreadsheetView view;
	private SpreadsheetTraceManager traceManager;
	
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;	
	private JPanel optionsPanel;
	private JPanel listPanel;
	private JPanel promptPanel;
	private JPanel buttonPanel;
	private JPanel locationPanel;
	private JList traceGeoList;
	private DefaultListModel traceGeoListModel;
	
	private JTextField cellRangeField;
	private JTextField numRowsField;
	private JCheckBox cbResetColumns, cbRowLimit, cbShowHeader;
	private JButton btRemove;
	private JButton btAdd;
	private JButton btClose;
	private JButton btCancel;
	private JButton btChangeLocation;
	
	private boolean isAddingTrace = false;
	private CellRange newTraceLocation;
	private TraceSettings newTraceSettings;
	
	private static final int MODE_NORMAL = 0;
	private static final int MODE_ADDTRACE = 1;
	private static final int MODE_SETLOCATION = 2;
	private int mode = MODE_NORMAL;
	
	private boolean isStartingUp = false;
	
	public TraceDialog(Application app, GeoElement selectedGeo, CellRange traceCell) {
		super(app.getFrame());
		
		this.app = app;
		this.view = (SpreadsheetView) app.getGuiManager().getSpreadsheetView();		
		traceManager = view.getTraceManager();
		traceGeoList = new JList();
				
		initGUI();
		setTraceDialogSelection(selectedGeo, traceCell);				
		updateGUI();
		
	}
	
	

	
	//======================================================
	//           Initialize
	//======================================================
	
	
	/**
	 * Sets the intial selection of a trace geo and handles these different calling
	 * contexts:
	 * 
	 * 1) Spreadsheet context menu. This passes either a currently tracing
	 * geo, or just a cell location. In this case the user must be prompted for
	 * a geo to trace.
	 * 
	 * 2) Euclidian or algebra view context menu. This passes either a
	 * currently tracing geo, or just a geo. In this case the geo is
	 * automatically assigned a trace location.
	 * 
	 * 3) Toolbar button. A button click loads the dialog without any selection.
	 * 
	 */
	public void setTraceDialogSelection(GeoElement selectedGeo, CellRange traceCell){
			
		// if the traceCell column is tracing a geo then set selectedGeo to this geo 
		if(traceCell != null && traceManager.isTraceColumn(traceCell.getMinColumn())){
			selectedGeo = traceManager.getTraceGeo(traceCell.getMinColumn());
		}
		
		//selectedGeo exists
		if(selectedGeo != null){
		
			// if selectedGeo is not a trace geo then add it to the trace collection
			if(!traceManager.isTraceGeo(selectedGeo)){
				// create default trace settings
				TraceSettings t = traceManager.new TraceSettings();
				if (traceCell != null) {
					t.traceColumn1 = traceCell.getMinColumn();
					t.traceRow1 = traceCell.getMinRow();
				}			
				traceManager.addSpreadsheetTraceGeo(selectedGeo, t);
			}
			// update the trace geo list and select our geo 
			updateTraceGeoList();
			traceGeoList.removeListSelectionListener(this);
			traceGeoList.setSelectedValue(selectedGeo, true);
			traceGeoList.addListSelectionListener(this);
		
			
		//selectedGeo does not exist, user must select a geo	 	
		}else{	
			
			//set adding trace flag, this will open the add dialog when 
			//updateGUI is called
			newTraceLocation = traceCell;
			isStartingUp = true;
			prepareToAddTrace();
		}		
		
	}
	
	

	@Override
	public void setVisible(boolean isVisible) {		
		super.setVisible(isVisible);

		if (isVisible) {
			view.setTraceDialogMode(true);
			updateGUI();
			
		} else {
			//clear the selection rectangle and switch back to normal mode
			traceGeoList.setSelectedIndex(-1);
			view.getTable().selectionChanged();
			view.setTraceDialogMode(false);
			
			
		}		
	}
	
	
	
	
	

	//======================================================
	//          Create GUI 
	//======================================================
	
	
	
	private void initGUI() {
		
		//TODO use a set labels method for language support
		
		try {
			setTitle(app.getPlain("TraceToSpreadsheet"));			
			BorderLayout thisLayout = new BorderLayout();			
			getContentPane().setLayout(thisLayout);
						
			// tabbed panel
			tabbedPane = new JTabbedPane();	
			tabbedPane.addTab(app.getMenu("Location"), null, buildLocationPanel());
			tabbedPane.addTab(app.getMenu("Options"), null, buildTraceOptionsPanel());															
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			
			// split pane: trace list on left, tabbed options on left
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(buildListPanel());
			splitPane.setRightComponent(tabbedPane);
						
			// put components together
			getContentPane().add(splitPane,BorderLayout.CENTER);
			getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
					
			// finish setup
			setResizable(false);			
			pack();
			setLocationRelativeTo(app.getFrame());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	private JPanel buildListPanel() {
		
		// init the trace options panel
		listPanel = new JPanel();
		listPanel.setLayout(new BorderLayout());		
		
		traceGeoListModel = new DefaultListModel();
		traceGeoList = new JList(traceGeoListModel);
		
		traceGeoList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		traceGeoList.addListSelectionListener(this);
		traceGeoList.setLayoutOrientation(JList.VERTICAL);
		traceGeoList.setVisibleRowCount(-1);
		MyCellRenderer rend = new MyCellRenderer();
		traceGeoList.setCellRenderer(rend);
		
		JScrollPane listScroller = new JScrollPane(traceGeoList);
		listScroller.setPreferredSize(new Dimension(180, 30));	
		listScroller.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
					
		listPanel.add(listScroller, BorderLayout.CENTER);	
	    
        return listPanel;
	}
	
	
	private JPanel buildLocationPanel() {
		
		// init 
		locationPanel = new JPanel();
		locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
		locationPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));			
		locationPanel.setMinimumSize(new Dimension(200, 30));
	
		
		// cellRange panel
		JPanel cellRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cellRangePanel.setAlignmentX(0.0f);
		JLabel cellLabel = new JLabel("First row: ");
		cellRangeField = new MyTextField(app.getGuiManager());
		cellRangeField.setColumns(4);
		cellRangeField.addActionListener(this);
		cellRangeField.addFocusListener(this);	
		
	    btChangeLocation = new JButton("change...");
	    btChangeLocation.addActionListener(this);
		
		cellRangePanel.add(cellLabel); 
		cellRangePanel.add(cellRangeField);	
		cellRangePanel.add(btChangeLocation);	
		
		
        // row limit panel
        JPanel traceRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        traceRowPanel.setAlignmentX(0.0f);    
        cbRowLimit = new JCheckBox(app.getMenu("Row Limit:"));  
        cbRowLimit.addActionListener(this);
        traceRowPanel.add(cbRowLimit); 
               
        numRowsField = new MyTextField(app.getGuiManager());
        numRowsField.setAlignmentX(0.0f);
        numRowsField.setColumns(3);
        numRowsField.addActionListener(this);
        numRowsField.addFocusListener(this);         
        //traceRowPanel.add(Box.createHorizontalStrut(tab));
        traceRowPanel.add(numRowsField); 
        
      
        // put it together
      //  locationPanel.add(cellRangePanel); 
        locationPanel.add(traceRowPanel);
      
        
        return locationPanel;
	}
	
	
	private JPanel buildTraceOptionsPanel() {
		
		// init the trace options panel
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
		optionsPanel.setMinimumSize(new Dimension(100, 30));
		
		
		// reset columns panel
		JPanel resetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		resetPanel.setAlignmentX(0.0f);
		cbResetColumns = new JCheckBox(app.getMenu("Column Reset"));  
		cbResetColumns.addActionListener(this);   
		resetPanel.add(cbResetColumns);
		
		// show header panel
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		headerPanel.setAlignmentX(0.0f);
        cbShowHeader = new JCheckBox(app.getMenu("Header"));  
        cbShowHeader.addActionListener(this);        
        headerPanel.add(cbShowHeader);
        
      
        // put it together
        optionsPanel.add(headerPanel);
        optionsPanel.add(resetPanel); 
        
        return optionsPanel;
	}
	
	
	private JPanel buildButtonPanel(){	
		
		// init button panel
		buttonPanel = new JPanel(new BorderLayout());
		//buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlDkShadow));	
		
		btRemove = new JButton("\u2718");
		//btRemove = new JButton(app.getPlain("Remove"));
		//btRemove = new JButton(app.getImageIcon("delete_small.gif"));
		btRemove.setToolTipText(app.getPlain("Remove"));
		btRemove.addActionListener(this);
		
		
		btAdd = new JButton("\u271A");
		//btAdd = new JButton(app.getPlain("Add"));
		btAdd.setToolTipText(app.getPlain("Add"));
		btAdd.addActionListener(this);
		
		JPanel addRemovePanel = new JPanel();
		addRemovePanel.add(btRemove);
		addRemovePanel.add(btAdd);
		
		btClose = new JButton(app.getPlain("Close"));
		btClose.addActionListener(this);
		
		btCancel = new JButton(app.getPlain("Cancel"));
		btCancel.addActionListener(this);
		JPanel closeCancelPanel = new JPanel();
		closeCancelPanel.add(btCancel);
		closeCancelPanel.add(btClose);
		
		
		promptPanel = new JPanel(new BorderLayout());			
		JLabel prompt = new JLabel("Select an Object to Trace");
		promptPanel.add(prompt, BorderLayout.CENTER);
		promptPanel.setVisible(false);
		
		buttonPanel.add(closeCancelPanel, BorderLayout.EAST);
		buttonPanel.add(promptPanel, BorderLayout.CENTER);	
		buttonPanel.add(addRemovePanel, BorderLayout.WEST);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
		//buttonPanel.setPreferredSize(new Dimension(400,50));
		
		return buttonPanel;
	}
	

	
	


	//======================================================
	//          Update GUI 
	//======================================================
	
	
	
	public void updateGUI() {				
	
		if(isAddingTrace){
			
		//	getContentPane().remove(splitPane);		
		//	getContentPane().add(promptPanel,BorderLayout.CENTER);
			
			/*
			getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			
			Dimension size = splitPane.getPreferredSize();
			size.height = promptPanel.getPreferredSize().height;
			promptPanel.setPreferredSize(size);
			*/
			
		//	tabbedPane.setVisible(false);
			promptPanel.setVisible(true);
			
			btCancel.setVisible(true);
			btClose.setVisible(false);
			
			btAdd.setVisible(false);
			btRemove.setVisible(false);
	
		//	pack();
			this.repaint();
			app.setMoveMode(); app.setSelectionListenerMode(this);			
			//view.getTable().setSelection(null);	
			traceGeoList.setSelectedIndex(-1);
			view.getTable().selectionChanged();
			
			return;
			
		}else{
			
			
		//	getContentPane().remove(promptPanel);
		//	getContentPane().add(splitPane,BorderLayout.CENTER);
			//getContentPane().remove(buttonPanel);
			
		//	tabbedPane.setVisible(true);
			promptPanel.setVisible(false);
			btAdd.setVisible(true);
			btRemove.setVisible(true);
			btCancel.setVisible(false);
			btClose.setVisible(true);
			
		//	pack();
			app.setSelectionListenerMode(null); 
			//view.getTable().setSelection(this.getTraceSelectionRange(-1, -1));
			view.getTable().selectionChanged();
			
		}		
			
		
		if (traceGeoListModel.isEmpty()){
			//view.table.setTraceSelectionRectangle(null);
			//optionsPanel.setVisible(false);
		}else{
			//view.table.setTraceSelectionRectangle(getSettings());
			//optionsPanel.setVisible(true);
	
			// update column reset checkbox
			cbResetColumns.removeActionListener(this);
			cbResetColumns.setSelected(getSettings().doColumnReset);
			cbResetColumns.addActionListener(this);

			// update row limit checkbox
			cbRowLimit.removeActionListener(this);
			cbRowLimit.setSelected(getSettings().doRowLimit);
			cbRowLimit.addActionListener(this);

			// update show header checkbox
			cbShowHeader.removeActionListener(this);
			cbShowHeader.setSelected(getSettings().showName);
			cbShowHeader.addActionListener(this);

			// update max row textfield
			numRowsField.setEnabled(getSettings().doRowLimit);
			numRowsField.removeActionListener(this);
			numRowsField.setText("" + getSettings().numRows);
			numRowsField.setCaretPosition(0);
			numRowsField.addActionListener(this);


			// update target cell textfield
			CellRange traceCell = new CellRange(view.table, getSettings().traceColumn1, getSettings().traceRow1);
			cellRangeField.removeActionListener(this);
			if (traceCell != null) {
				cellRangeField.setText(traceCell.getName());
			} else {
				cellRangeField.setText("");
			}
			cellRangeField.setCaretPosition(0);
			cellRangeField.addActionListener(this);
		}
		
		view.repaint();
		
	}
	
	/** Update the trace geo list with current trace geos */
	private void updateTraceGeoList(){
		//update traceGeo list
		traceGeoList.removeListSelectionListener(this);
		traceGeoListModel.clear();		
		for(GeoElement geo: traceManager.getTraceGeoList()){
			traceGeoListModel.addElement(geo);
		}
		traceGeoList.addListSelectionListener(this);	
	}
	
	
	
	
	//======================================================
	//           Event Listeners and Handlers
	//======================================================
	
	
	public void actionPerformed(ActionEvent e) {	
		doActionPerformed(e.getSource());
	}	
	
	public void doActionPerformed(Object source) {		
		
		if (source == cbResetColumns) {
			getSettings().doColumnReset = cbResetColumns.isSelected();
			updateSelectedTraceGeo(); 
		}
		
		else if (source == cbRowLimit) {
			getSettings().doRowLimit = cbRowLimit.isSelected();
			updateSelectedTraceGeo();
		}
		
		else if (source == cbShowHeader) {
			getSettings().showName = cbShowHeader.isSelected();
			updateSelectedTraceGeo();
		}
		
		else if (source == numRowsField) {
			getSettings().numRows =  Integer.parseInt(numRowsField.getText());
			updateSelectedTraceGeo();
		}	
		
		else if (source == btAdd) {
			//set flag to open add dialog
			prepareToAddTrace();
		}	
		
		else if (source == btRemove) {
			removeTrace();
		}	
		
		else if (source == btCancel) {
			if (isAddingTrace & !isStartingUp) {
				isAddingTrace = false;
			}else{
				closeDialog();
				return;
			}
			isAddingTrace = false;
			isStartingUp = false;
			
		} else if (source == btClose) {
			closeDialog();
			return;
			
		} else if (source == btChangeLocation) {
			if (isAddingTrace) {
				isAddingTrace = false;
			}
		}
				
		updateGUI();	
	}
	
	

	/**  Listener for selection changes in the traceGeoList */
	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting() == false) {
			updateGUI();
		}	
	}

	
	/** Listener for changes in geo selection. Only used when adding traces */
	public void geoElementSelected(GeoElement selectedGeo, boolean addToSelection) {
				
		if (selectedGeo.isSpreadsheetTraceable()
				&& !GeoElement.isSpreadsheetLabel(selectedGeo.getLabel())) {
			addTrace(selectedGeo);			
		}
	}	
	
	
	private void prepareToAddTrace(){
		newTraceSettings = traceManager.new TraceSettings();
		if (newTraceLocation != null) {
			newTraceSettings.traceColumn1 = newTraceLocation.getMinColumn();
			newTraceSettings.traceRow1 = newTraceLocation.getMinRow();
		}
		isAddingTrace = true;
	}
	
	
	
	/** Add a geo to the traceGeoCollection and update the dialog.  */
	private void addTrace(GeoElement geo){
		
		// add geo to the trace collection 
		if (traceManager.isTraceGeo(geo) == false) {
			/*
			TraceSettings t = traceManager.new TraceSettings();
			if (newTraceLocation != null) {
				t.traceColumn1 = newTraceLocation.getMinColumn();
				t.traceRow1 = newTraceLocation.getMinRow();
			}
			traceManager.addSpreadsheetTraceGeo(geo, t);
			*/
			
			traceManager.addSpreadsheetTraceGeo(geo, newTraceSettings);
			updateTraceGeoList();
		}

		// select this geo 
		traceGeoList.setSelectedValue(geo, true);
	
		
		//update
		isAddingTrace = false;
		newTraceLocation = null;
		updateGUI();	
	}
	
	
	
	/** Remove a geo from the traceGeoCollection and update the dialog.  */
	private void removeTrace(){
		GeoElement traceGeo = (GeoElement) traceGeoList.getSelectedValue();
		traceManager.removeSpreadsheetTraceGeo(traceGeo);		
		updateTraceGeoList();
		if (!traceGeoListModel.isEmpty()){
			traceGeoList.setSelectedIndex(0);
		}
	}
	
	

	private GeoElement getSelectedGeo(){
		
		return (GeoElement)traceGeoList.getSelectedValue();
	}

	private TraceSettings getSettings(){
		if(isAddingTrace)
			return newTraceSettings;
		else
			return traceManager.getTraceSettings((GeoElement)traceGeoList.getSelectedValue());
	}
	
	private void updateSelectedTraceGeo(){	
		traceManager.updateTraceSettings(getSelectedGeo(), getSettings());
	}
	

	

	public CellRange getTraceSelectionRange(int anchorColumn, int anchorRow){
		CellRange cr = new CellRange(view.getTable());	
		
		
		if(getSettings() == null){
			cr.setCellRange(-1,-1,-1,-1);		
		}else if(isAddingTrace){
			if(newTraceLocation != null)
				cr = newTraceLocation;
			else
				cr = traceManager.getNextTraceCell();
		}else{
			cr.setCellRange(
					getSettings().traceColumn1,
					getSettings().traceRow1,
					getSettings().traceColumn2,
					(getSettings().doRowLimit) ? getSettings().traceRow2 : view.MAX_ROWS );
		}
				
		return cr;
	}
	
	
	
	
	
	
	
	
	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		//doActionPerformed(e.getSource());
	}


	
	public void closeDialog() {
		System.out.println("closeDialog");
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));				
		app.storeUndoInfo();
		setCursor(Cursor.getDefaultCursor());
		setVisible(false);
		//view.table.setTraceSelectionRectangle(null);
		//view.getTable().setSelection(null,null,true);
		view.repaint();
	}
	

	public void windowActivated(WindowEvent arg0) {		
	}

	public void windowClosed(WindowEvent arg0) {	
	}

	public void windowClosing(WindowEvent arg0) {
		closeDialog();	
	}

	public void windowDeactivated(WindowEvent arg0) {	
	}

	public void windowDeiconified(WindowEvent arg0) {	
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {	
	}
	

	
	
	
	


	//======================================================
	//         Cell Renderer 
	//======================================================
	
	/**
	 * Custom cell renderer that displays GeoElement descriptions.
	 */
	class MyCellRenderer extends DefaultListCellRenderer {
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus) {

			super.getListCellRendererComponent(list, value, index, isSelected,
					hasFocus);

			if (value != null) {
				GeoElement geo = (GeoElement) value;
				String text = geo.getLongDescriptionHTML(true, true);
				if (text.length() < 100)
					setText(text);
				else
					setText(geo.getNameDescriptionHTML(true, true));
			} else
				setText(" ");
			return this;
		}

	}




}






