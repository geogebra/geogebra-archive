/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoElement;
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
import javax.swing.Box;
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
 */

public class TraceDialog extends javax.swing.JDialog
implements 
	GeoElementSelectionListener, ActionListener, FocusListener, 
	ListSelectionListener, WindowListener 
	
	{
	
	// external components
	private Application app;
	private SpreadsheetView view;
	private SpreadsheetTraceManager traceManager;
	
	// JList to display trace geos
	private JList traceGeoList;
	private DefaultListModel traceGeoListModel;
	
	// other GUI objects
	private JSplitPane splitPane;
	private JTabbedPane tabbedPane;	
	private JPanel optionsPanel;
	private JPanel listPanel;
	private JPanel promptPanel;
	private JPanel buttonPanel;
	private JPanel locationPanel;
	private JPanel leftButtonPanel;
	private JPanel statPanel;
	
	private JTextField firstRowField, numRowsField;
	private JCheckBox cbResetColumns, cbRowLimit, 
		cbShowLabel, cbTraceList, cbShowCount, cbShowMin, cbShowMax, cbShowMean;
	private JButton btRemove;
	private JButton btAdd;
	private JButton btClose;
	private JButton btCancel;
	private JButton btChangeLocation;
	private JButton btErase;
	
	
	// modes
	private static final int MODE_NORMAL = 0;
	private static final int MODE_ADD = 1;
	private static final int MODE_LOCATE = 2;
	private int mode = MODE_NORMAL;
	
	//misc
	private CellRange newTraceLocation;
	private boolean isIniting = false;
	
	
	
	/** Constructor */
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
			
			setMode(MODE_NORMAL);
			// if selectedGeo is not a trace geo then add it to the trace collection
			if(!traceManager.isTraceGeo(selectedGeo)){
				// create default trace settings
				//TraceSettings t = new TraceSettings();
				TraceSettings t = selectedGeo.getTraceSettings(); 
				if (traceCell != null) {
					t.traceColumn1 = traceCell.getMinColumn();
					t.traceRow1 = traceCell.getMinRow();
				}			
				traceManager.addSpreadsheetTraceGeo(selectedGeo);
			}
			// update the trace geo list and select our geo 
			updateTraceGeoList();
			traceGeoList.removeListSelectionListener(this);
			traceGeoList.setSelectedValue(selectedGeo, true);
			traceGeoList.addListSelectionListener(this);
		
			
		//selectedGeo does not exist, user must select a geo	 	
		}else{	
			
			//switch to Add mode
			newTraceLocation = traceCell;
			isIniting = true;
			setMode(MODE_ADD);
		}				
	}
	
	

	@Override
	public void setVisible(boolean isVisible) {		
		super.setVisible(isVisible);

		if (isVisible) {
			view.setTraceDialogMode(true);
			updateGUI();
			app.setSelectionListenerMode(this);	
			
		} else {
			//clear the selection rectangle and switch back to normal mode
			traceGeoList.clearSelection();
			setMode(MODE_NORMAL);
			view.getTable().selectionChanged();
			view.setTraceDialogMode(false);	
			app.setSelectionListenerMode(null);	
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
			tabbedPane.addTab(app.getMenu("Options"), null, buildOptionsPanel());															
			tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			
			// split pane: trace list on left, tabbed options on left
			splitPane = new JSplitPane();
			splitPane.setLeftComponent(buildListPanel());
			splitPane.setRightComponent(tabbedPane);
						

			
			// put it all together
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
		
		// start row panel
		JLabel lblStartRow = new JLabel("Start Row: ");
		firstRowField = new MyTextField(app.getGuiManager());
		firstRowField.setColumns(3);
		firstRowField.addActionListener(this);
		firstRowField.addFocusListener(this);
		
		JPanel startRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		startRowPanel.setAlignmentX(0.0f);
		startRowPanel.add(lblStartRow);
		startRowPanel.add(firstRowField);	
		
		
        // row limit panel
		cbRowLimit = new JCheckBox(app.getMenu("Row Limit:"));  
		cbRowLimit.addActionListener(this);

		numRowsField = new MyTextField(app.getGuiManager());
		numRowsField.setAlignmentX(0.0f);
        numRowsField.setColumns(3);
        numRowsField.addActionListener(this);
        numRowsField.addFocusListener(this);    

        JPanel rowLimitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowLimitPanel.setAlignmentX(0.0f); 
        rowLimitPanel.add(cbRowLimit); 
        rowLimitPanel.add(numRowsField); 


        // locationPanel 
		locationPanel = new JPanel();
		locationPanel.setLayout(new BoxLayout(locationPanel, BoxLayout.Y_AXIS));
		locationPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));			
		locationPanel.setMinimumSize(new Dimension(200, 30));
		
        locationPanel.add(Box.createVerticalGlue());
        locationPanel.add(startRowPanel);
        locationPanel.add(rowLimitPanel);
        locationPanel.add(Box.createVerticalGlue());
        
        return locationPanel;
	}
	
	
	private JPanel buildOptionsPanel() {
		
		  // options panel
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
		optionsPanel.setMinimumSize(new Dimension(100, 30));
					
        cbShowLabel = new JCheckBox(app.getMenu("ShowLabel"));  
        cbShowLabel.addActionListener(this);        
        optionsPanel.add(cbShowLabel);
             
        cbTraceList = new JCheckBox(app.getMenu("Trace To List"));  
        cbTraceList.addActionListener(this);        
        optionsPanel.add(cbTraceList);
        
		cbResetColumns = new JCheckBox(app.getMenu("Column Reset"));  
		cbResetColumns.addActionListener(this);   
		optionsPanel.add(cbResetColumns);
    
        return optionsPanel;
	}
	
	
	private JPanel buildButtonPanel(){	
		
		// init button panel
		buttonPanel = new JPanel(new BorderLayout());
		
		btRemove = new JButton("\u2718");
		btRemove.addActionListener(this);
		
		
		btAdd = new JButton("\u271A");
		//btAdd = new JButton(app.getPlain("Add"));
		btAdd.addActionListener(this);
		
		btErase = new JButton(app.getImageIcon("delete_small.gif"));
		btErase.addActionListener(this);
		btErase.setPreferredSize(btRemove.getPreferredSize());
		
		leftButtonPanel = new JPanel();
		leftButtonPanel.add(btRemove);
		leftButtonPanel.add(btAdd);
		leftButtonPanel.add(Box.createRigidArea(new Dimension(10,0)));
		leftButtonPanel.add(btErase);
		
		
		btClose = new JButton(app.getPlain("Close"));
		btClose.addActionListener(this);
		
		btCancel = new JButton(app.getPlain("Cancel"));
		btCancel.addActionListener(this);
		JPanel closeCancelPanel = new JPanel();
		closeCancelPanel.add(btCancel);
		closeCancelPanel.add(btClose);
		
		
		promptPanel = new JPanel(new BorderLayout());			
		JLabel prompt = new JLabel("Select an Object to Trace");
		prompt.setHorizontalAlignment(JLabel.CENTER);
		prompt.setVerticalAlignment(JLabel.CENTER);
		promptPanel.add(prompt, BorderLayout.CENTER);
		promptPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		//promptPanel.setVisible(false);
		
		buttonPanel.add(closeCancelPanel, BorderLayout.EAST);
		//buttonPanel.add(promptPanel, BorderLayout.CENTER);	
		buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
		//buttonPanel.setPreferredSize(new Dimension(400,50));
		
		return buttonPanel;
	}
	

	
	


	//======================================================
	//          Update GUI 
	//======================================================
	
	
	
	private void updateGUI() {
		
		updateTraceGeoList();
		switch (mode){
		
		case MODE_ADD:
			
			//promptPanel.setVisible(true);		
			btCancel.setVisible(true);
			btClose.setVisible(false);		
			leftButtonPanel.setVisible(false);
			//splitPane.setVisible(false);
			
			//traceGeoList.clearSelection();
			//traceGeoList.setEnabled(false);
			
			//tabbedPane.setEnabled(false);
			view.getTable().selectionChanged();
			
			getContentPane().remove(splitPane);
			getContentPane().add(promptPanel,BorderLayout.CENTER);
			
			Dimension size = splitPane.getPreferredSize();
			size.height = promptPanel.getPreferredSize().height;
			promptPanel.setPreferredSize(size);
			
			pack();
			repaint();
			
		break;
		
		case MODE_NORMAL:

			//splitPane.setVisible(true);
			//promptPanel.setVisible(false);
			leftButtonPanel.setVisible(true);
			btCancel.setVisible(false);
			btClose.setVisible(true);

			//traceGeoList.setEnabled(true);
			//tabbedPane.setEnabled(true);
			
			view.getTable().selectionChanged();
			
			getContentPane().remove(promptPanel);
			getContentPane().add(splitPane,BorderLayout.CENTER);
			pack();
			repaint();
			
			
			if (!traceGeoList.isSelectionEmpty()) {

				// update checkboxes
				cbResetColumns.removeActionListener(this);
				cbResetColumns.setSelected(getSettings().doColumnReset);
				cbResetColumns.addActionListener(this);
				
				cbRowLimit.removeActionListener(this);
				cbRowLimit.setSelected(getSettings().doRowLimit);
				cbRowLimit.addActionListener(this);

				cbShowLabel.removeActionListener(this);
				cbShowLabel.setSelected(getSettings().showLabel);
				cbShowLabel.addActionListener(this);
				
				cbTraceList.removeActionListener(this);
				cbTraceList.setSelected(getSettings().showTraceList);
				cbTraceList.addActionListener(this);
								
				// update row limit textfield
				numRowsField.setEnabled(getSettings().doRowLimit);
				numRowsField.removeActionListener(this);
				numRowsField.setText("" + getSettings().numRows);
				numRowsField.setCaretPosition(0);
				numRowsField.addActionListener(this);

				// update first row textfield
				firstRowField.removeActionListener(this);
				firstRowField.setText("" + (getSettings().traceRow1 + 1));
				firstRowField.setCaretPosition(0);
				firstRowField.addActionListener(this);
				
			}

			view.repaint();

			break;
		}
		
		
		
		
	}
	
	/** Update the trace geo list with current trace geos */
	private void updateTraceGeoList(){
		
		GeoElement selectedGeo = (GeoElement) traceGeoList.getSelectedValue();
		
		traceGeoList.removeListSelectionListener(this);
		traceGeoListModel.clear();		
		for(GeoElement geo: traceManager.getTraceGeoList()){
			traceGeoListModel.addElement(geo);
		}
		if(selectedGeo != null && traceGeoListModel.contains(selectedGeo))
			traceGeoList.setSelectedValue(selectedGeo, true);
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
		
		else if (source == cbShowLabel) {
			getSettings().showLabel = cbShowLabel.isSelected();
			updateSelectedTraceGeo();
		}
		
		else if (source == cbTraceList) {
			getSettings().showTraceList = cbTraceList.isSelected();
			updateSelectedTraceGeo();
		}
		
		else if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}	
		
		
		else if (source == btAdd) {
			setMode(MODE_ADD);
		}	
		
		else if (source == btErase) {
			updateSelectedTraceGeo();
			//traceManager.clearGeoTraceColumns(getSelectedGeo());
		}	
		
		else if (source == btRemove) {
			removeTrace();
		}	
		
		else if (source == btCancel) {
			setMode(MODE_NORMAL);
			if (isIniting) {
				closeDialog();
				return;
			}			
			
		} else if (source == btClose) {
			closeDialog();
			return;
			
		} else if (source == btChangeLocation) {
			setMode(MODE_LOCATE);
		}
				
		updateGUI();	
	}
	
	
	private void doTextFieldActionPerformed(JTextField source) {
		
		try {
			String inputText = source.getText().trim();
			Integer value = Integer.parseInt(source.getText());
			
			if (value !=null && value > 0 && value < SpreadsheetView.MAX_ROWS) {

				if (source == firstRowField) {
					traceManager.clearGeoTraceColumns(getSelectedGeo());
					getSettings().traceRow1 =  value - 1;
					updateSelectedTraceGeo();
				}	

				else if (source == numRowsField) {
					getSettings().numRows = value;
					updateSelectedTraceGeo();
				}	
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
	}
	
	

	/**  Listener for selection changes in the traceGeoList */
	public void valueChanged(ListSelectionEvent e) {
		//if(getSettings() != null) getSettings().debug(getSelectedGeo());
		if (e.getValueIsAdjusting() == false) {
			updateGUI();
		}	
	}

	
	/** Listener for changes in geo selection */
	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		
		if (traceManager.isTraceGeo(geo) == true) {
			traceGeoList.setSelectedValue(geo, true);
			updateGUI();
		}else{
		if (mode == MODE_ADD 
				&& geo.isSpreadsheetTraceable()
				&& !GeoElement.isSpreadsheetLabel(geo.getLabel())) {
			
				addTrace(geo);
			}
		}

	}
	
			
	
	/** Add a geo to the traceGeoCollection and update the dialog.  */
	private void addTrace(GeoElement geo){
		
		// add geo to the trace collection 
		if (traceManager.isTraceGeo(geo) == false) {		
			TraceSettings t = geo.getTraceSettings();
			if (newTraceLocation != null) {
				t.traceColumn1 = newTraceLocation.getMinColumn();
				t.traceRow1 = newTraceLocation.getMinRow();
			}
			
			traceManager.addSpreadsheetTraceGeo(geo);				
			updateTraceGeoList();
		}
		
		//update	
		setMode(MODE_NORMAL);
		traceGeoList.setSelectedValue(geo, true);
		newTraceLocation = null;
		updateGUI();	
	}
	
	
	
	/** Remove a geo from the traceGeoCollection and update the dialog.  */
	private void removeTrace(){
		GeoElement geo = (GeoElement) traceGeoList.getSelectedValue();
		traceManager.removeSpreadsheetTraceGeo(geo);
		geo.setSpreadsheetTrace(false);
		geo.setTraceSettings(null);
		
		updateTraceGeoList();
		if (!traceGeoListModel.isEmpty()){
			traceGeoList.setSelectedIndex(0);
		}
		updateGUI();
	}
	
	

	private GeoElement getSelectedGeo(){	
		return (GeoElement)traceGeoList.getSelectedValue();
	}

	
	private TraceSettings getSettings(){
		if(traceGeoList.isSelectionEmpty())
			return null;
		else
			return ((GeoElement)traceGeoList.getSelectedValue()).getTraceSettings();
	}
	
	
	private void updateSelectedTraceGeo(){	
		traceManager.updateTraceSettings(getSelectedGeo());
	}
	
	
	/** Determine the cell range to be selected on spreadsheet mouse click. */
	public CellRange getTraceSelectionRange(int anchorColumn, int anchorRow){

		CellRange cr = new CellRange(view.getTable());			

		switch (mode) {
		case MODE_NORMAL:
			if (getSettings() == null) {
				cr.setCellRange(-1, -1, -1, -1);
			} else {
				cr.setCellRange(
					getSettings().traceColumn1,
					getSettings().traceRow1, 
					getSettings().traceColumn2,
					(getSettings().doRowLimit) ? getSettings().traceRow2: view.MAX_ROWS);
			}
			break;

		case MODE_ADD:
			if (newTraceLocation != null)
				cr = newTraceLocation;
			else
				cr = traceManager.getNextTraceCell();
			break;

		case MODE_LOCATE:

			int w = getSettings().traceColumn2 - getSettings().traceColumn1;
			int h = ((getSettings().doRowLimit) ? getSettings().traceRow2: view.MAX_ROWS)
					- getSettings().traceRow1;

			cr.setCellRange(anchorColumn, anchorRow, anchorColumn + w,anchorRow + h);
			break;
		}

		return cr;
	}
	
	
	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		//doActionPerformed(e.getSource());
		doTextFieldActionPerformed((JTextField)(e.getSource()));
		updateGUI();
	}


	
	private void setMode(int mode){
		
		this.mode = mode;
		
		switch (mode){		
		case MODE_NORMAL:
			isIniting = false;
			//app.setSelectionListenerMode(null);		
			break;	
			
		case MODE_ADD:			
			app.setMoveMode(); 
			app.setSelectionListenerMode(this);		
			view.getTable().selectionChanged();
			break;
			
		case MODE_LOCATE:
			
			break;
		}
		updateGUI();
	}
	
	
	
	public void toolbarModeChanged(int euclidianMode){
		//System.out.println(euclidianMode);
		if(euclidianMode != EuclidianView.MODE_MOVE 
				&& euclidianMode != EuclidianView.MODE_SELECTION_LISTENER 
				&&  (mode == MODE_ADD || mode == MODE_LOCATE)){
			setMode(MODE_NORMAL);
			if(isIniting)
				closeDialog();		
		}	
	}
	
	
	/** Handle notification of deleted or renamed geo */
	public void updateTraceDialog(){
		updateGUI();
	}
		
	
	public void closeDialog() {
		
		//System.out.println("closeDialog");
		setMode(MODE_NORMAL);
		
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






