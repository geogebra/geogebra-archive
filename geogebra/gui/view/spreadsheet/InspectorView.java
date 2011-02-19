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
import geogebra.gui.InputDialog;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


/**
 * View for inspecting selected GeoElements
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public class InspectorView extends InputDialog 
implements View, MouseListener, ListSelectionListener, 
KeyListener, ActionListener{

	private static final Color DISPLAY_GEO_COLOR = Color.BLUE;
	private static final Color DISPLAY_GEO2_COLOR = Color.RED;

	private static final Color EVEN_ROW_COLOR = new Color(241, 245, 250);
	//	private static final Color TABLE_GRID_COLOR = new Color(0xd9d9d9);
	private static final Color TABLE_GRID_COLOR = MyTable.TABLE_GRID_COLOR;

	private static final int minRows = 12;

	// column types
	private static final int COL_DERIVATIVE = 0;
	private static final int COL_DERIVATIVE2 = 1;
	private static final int COL_DIFFERENCE = 2;
	private static final int COL_CURVATURE = 3;


	// ggb fields
	private Kernel kernel;
	private Construction cons;

	// table fields
	private InspectorTable tableXY, tableInterval;
	private DefaultTableModel modelXY, modelInterval;
	private ArrayList<Integer> extraColumnList;
	private String[] columnNames;

	private double xMin, xMax, start =-1, step = 0.1;
	private boolean isChangingValue;
	private int pointCount = 9;

	// GUI 
	private JLabel lblGeoName, lblStart, lblStep, lblShow, lblLow, lblInterval;
	private MyTextField fldStart, fldStep, fldLow, fldHigh;
	private JPanel controlPanel;
	private JCheckBox ckFullTable;
	private JComboBox cbAdd;
	private JButton btnAdd, btnRemoveColumn;
	private JToggleButton btnOscCircle, btnTangent, btnXYSegments, btnTable;
	private PopupMenuButton btnAddColumn;

	// Geos
	private GeoElement selectedGeo, tangentLine, oscCircle, xSegment, ySegment;
	private GeoElement functionInterval, derivative, derivative2;
	private GeoPoint testPoint, lowPoint, highPoint;
	private GeoList pts;
	private ArrayList<GeoElement> geoList;
	private JTabbedPane tabPanel;
	private JPanel intervalTabPanel;
	private JPanel pointTabPanel;
	private boolean isIniting;
	private double initialX;

	private NumberFormat nf;



	/** Constructor */
	public InspectorView(Application app, GeoElement selectedGeo) {

		super(app.getFrame(), false);
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		boolean showApply = false;
		this.selectedGeo = selectedGeo;

		// setup InputDialog GUI
		isIniting = true;
		String title = app.getPlain("Inspector");
		createGUI(title, "", false, 16, 1, false, false, false, false, false, showApply, false);
		this.btOK.setVisible(false);
		this.btCancel.setVisible(false);


		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(4);
		nf.setGroupingUsed(false);


		// list of all geos we create
		geoList = new ArrayList<GeoElement>();

		// create the GUI components
		createGUIElements();


		// Header panel 
		//================================================

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(lblGeoName, BorderLayout.CENTER);		
		headerPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,2));



		// Point tab panel 
		//================================================

		JToolBar tb1 = new JToolBar();   
		tb1.setFloatable(false);
		//	tb1.add(ckFullTable);
		//cp1.add(fldStart);
		tb1.add(lblStep);
		tb1.add(fldStep);

		JToolBar tb2 = new JToolBar();
		tb2.setFloatable(false);
		tb2.add(btnAddColumn);
		tb2.add(btnRemoveColumn);

		//JPanel hideShowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel hideShowPanel = new JPanel(new BorderLayout());
		FlowLayout flow = new FlowLayout(FlowLayout.CENTER); 
		flow.setHgap(10);              
		JPanel tb3 = new JPanel(flow);
		//tb3.setFloatable(false);
		tb3.add(btnTable);
		tb3.add(btnXYSegments);
		tb3.add(btnTangent);
		tb3.add(btnOscCircle);
		hideShowPanel.add(tb3, BorderLayout.CENTER);

		JPanel tableControlPanel = new JPanel(new BorderLayout());
		tableControlPanel.add(tb1,BorderLayout.WEST);
		tableControlPanel.add(tb2,BorderLayout.EAST);

		JPanel southPanel = new JPanel(new BorderLayout());
		//southPanel.add(tableControlPanel,BorderLayout.NORTH);
		southPanel.add(hideShowPanel,BorderLayout.CENTER);

		JScrollPane scroller = new JScrollPane(tableXY);
		//scroller.setPreferredSize(tableXY.getPreferredSize());

		pointTabPanel = new JPanel(new BorderLayout(2,2));
		pointTabPanel.add(tableControlPanel,BorderLayout.NORTH);
		pointTabPanel.add(scroller,BorderLayout.CENTER);
		pointTabPanel.add(southPanel,BorderLayout.SOUTH);


		// Interval tab panel 
		//================================================

		JToolBar intervalTB = new JToolBar();   //JPanel(new FlowLayout(FlowLayout.LEFT));
		intervalTB.setFloatable(false);
		intervalTB.add(fldLow);
		intervalTB.add(lblInterval);
		intervalTB.add(fldHigh);

		intervalTabPanel = new JPanel(new BorderLayout(5,5));
		intervalTabPanel.add(new JScrollPane(tableInterval), BorderLayout.CENTER);
		intervalTabPanel.add(intervalTB, BorderLayout.SOUTH);


		// build tab panel
		//================================================

		tabPanel = new JTabbedPane();		
		tabPanel.addTab("Point", pointTabPanel);
		tabPanel.addTab("Interval", intervalTabPanel);
		tabPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				updateGUI();
			}
		});



		// build dialog content pane
		//================================================
		getContentPane().add(headerPanel,BorderLayout.NORTH);
		getContentPane().add(tabPanel,BorderLayout.CENTER);

		setLabels();
		centerOnScreen();
		setResizable(true);
		app.getKernel().attach(this);


		insertGeoElement(selectedGeo);
		updateGUI();
		updateFonts();
		isIniting = false;
		pack();

	}



	//  Create GUI elements 
	// =====================================

	private void createGUIElements(){

		// create XY table
		tableXY = new InspectorTable();
		modelXY = new DefaultTableModel();
		modelXY.addColumn("x");
		modelXY.addColumn("y(x)");
		modelXY.setRowCount(pointCount);
		tableXY.setModel(modelXY);
		setMyCellRenderer(tableXY);

		tableXY.getSelectionModel().addListSelectionListener(this);
		//tableXY.addKeyListener(this);
		tableXY.getColumnModel().getColumn(0).setCellEditor(new MyEditor());


		// create interval table
		tableInterval = new InspectorTable();
		modelInterval = new DefaultTableModel();
		modelInterval.setColumnCount(2);
		modelInterval.setRowCount(pointCount);
		tableInterval.setModel(modelInterval);


		lblGeoName = new JLabel(getTitleString());
		lblGeoName.setFont(app.getBoldFont());

		lblStep = new JLabel();
		fldStep = new MyTextField(app.getGuiManager());
		fldStep.addActionListener(this);
		fldStep.setColumns(6);

		lblInterval = new JLabel();
		fldLow = new MyTextField(app.getGuiManager());
		fldLow.addActionListener(this);
		fldLow.setColumns(6);
		fldHigh = new MyTextField(app.getGuiManager());
		fldHigh.addActionListener(this);
		fldHigh.setColumns(6);

		btnOscCircle = new JToggleButton(app.getImageIcon("osculating_circle.png"));
		btnTangent = new JToggleButton(app.getImageIcon("tangent_line.png"));
		btnXYSegments = new JToggleButton(app.getImageIcon("xy_segments.png"));
		btnTable = new JToggleButton(app.getImageIcon("xy_table.png"));
		btnOscCircle.addActionListener(this);
		btnTangent.addActionListener(this);
		btnXYSegments.addActionListener(this);
		btnTable.addActionListener(this);

		btnOscCircle.setPreferredSize(new Dimension(24,24));
		btnTangent.setPreferredSize(new Dimension(24,24));
		btnXYSegments.setPreferredSize(new Dimension(24,24));
		btnTable.setPreferredSize(new Dimension(24,24));

		btnXYSegments.setSelected(true);

		btnRemoveColumn = new JButton();
		btnRemoveColumn.addActionListener(this);

		columnNames = new String[4];
		columnNames[COL_DERIVATIVE] =	app.getPlain("derivative");
		columnNames[COL_DERIVATIVE2] =	app.getPlain("derivative2");
		columnNames[COL_CURVATURE] =	app.getPlain("curvature");
		columnNames[COL_DIFFERENCE] =	app.getPlain("difference");

		btnAddColumn = new PopupMenuButton(app, columnNames, -1, 1, 
				new Dimension(0, 18), SelectionTable.MODE_TEXT);
		btnAddColumn.setKeepVisible(false);
		btnAddColumn.setStandardButton(true);
		btnAddColumn.setFixedIcon(GeoGebraIcon.createEmptyIcon(1, 1));
		btnAddColumn.setText("\u271A");
		btnAddColumn.addActionListener(this);
	}


	public void setLabels() {

		String[] intervalColumnNames = {app.getPlain("Property"), app.getPlain("Value")};
		modelInterval.setColumnIdentifiers(intervalColumnNames);

		lblStep.setText(app.getMenu("Step") + ":");		
		lblInterval.setText(app.getMenu(" < " + app.getPlain("x")  + " < " ) );	

		btnRemoveColumn.setText("\u2718");
		//btnAddColumn.setText("\u271A");

		tabPanel.setTitleAt(0, app.getMenu("Point"));
		tabPanel.setTitleAt(1, app.getMenu("Interval"));

		btnOscCircle.setToolTipText(app.getPlain("fncInspector.showOscCircle"));
		btnXYSegments.setToolTipText(app.getPlain("fncInspector.showXYLines"));
		btnTable.setToolTipText(app.getPlain("fncInspector.showTable"));
		btnTangent.setToolTipText(app.getPlain("fncInspector.showTangent"));

	}



	//  GUI update
	// =====================================
	private void updateGUI(){


		// set visibility of all geos to false
		for(int i=0; i<geoList.size(); i++){
			geoList.get(i).setEuclidianVisible(false);
		}	

		if(tabPanel.getSelectedComponent()==intervalTabPanel){

			double[] coords = new double[3];
			lowPoint.getCoords(coords);
			fldLow.setText("" + coords[0]);
			highPoint.getCoords(coords);
			fldHigh.setText("" + coords[0]);


			updateIntervalTable();
			lowPoint.setEuclidianVisible(true);
			highPoint.setEuclidianVisible(true);
			functionInterval.setEuclidianVisible(true);

		}else{

			tableXY.getSelectionModel().removeListSelectionListener(this);
			// reset table model and update the XYtable


			tableXY.setCellEditable(-1, -1);
			if(btnTable.isSelected()){
				modelXY.setRowCount(pointCount);
				tableXY.setCellEditable((pointCount -1)/2,0);
				//	tableXY.setRowSelectionAllowed(true);
				tableXY.changeSelection((pointCount - 1)/2, 0, false, false);

			}else{

				modelXY.setRowCount(1);
				tableXY.setCellEditable(0,0);
				tableXY.changeSelection(0, 0, false, false);
				//	tableXY.setRowSelectionAllowed(false);
			}

			updateXYTable();
			updateTestPoint();
			tableXY.getSelectionModel().addListSelectionListener(this);

			//set visibility of the display geos	
			testPoint.setEuclidianVisible(true);
			tangentLine.setEuclidianVisible(btnTangent.isSelected());
			oscCircle.setEuclidianVisible(btnOscCircle.isSelected());
			xSegment.setEuclidianVisible(btnXYSegments.isSelected());
			ySegment.setEuclidianVisible(btnXYSegments.isSelected());
			lblStep.setVisible(btnTable.isSelected());
			fldStep.setVisible(btnTable.isSelected());
			pts.setEuclidianVisible(btnTable.isSelected());
		} 

		// update the geos
		for(int i=0; i<geoList.size(); i++){
			isChangingValue = true;
			geoList.get(i).updateRepaint();
			isChangingValue = false;
		}	

	}



	//     Table Update
	// =====================================

	private void updateIntervalTable(){


		boolean isFunction = selectedGeo.getGeoClassType() == GeoElement.GEO_CLASS_FUNCTION;
		String lbl = selectedGeo.getLabel();
		ArrayList<String> property = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();

		double[] coords = new double[3];
		lowPoint.getCoords(coords);
		xMin = coords[0];
		highPoint.getCoords(coords);
		xMax = coords[0];

		double integral = evaluateExpression("Integral[" + lbl + "," + xMin + "," + xMax + "]");
		double mean = integral/(xMax - xMin);
		double length = evaluateExpression("Length[" + lbl + "," + xMin + "," + xMax + "]");


		property.add(app.getCommand("Maximum"));
		value.add("  ");

		property.add(app.getCommand("Minimum"));
		value.add("  ");

		property.add(app.getCommand("Area"));
		value.add(nf.format(integral));

		property.add(app.getCommand("Mean"));
		value.add(nf.format(mean));

		property.add(app.getCommand("Length"));
		value.add(nf.format(length));

		property.add(app.getCommand("Root"));

		value.add("Root[" + lbl + "," + xMin + "," + xMax + "]");


		int rowCount = Math.max(minRows, property.size());
		modelInterval.setRowCount(property.size());

		for(int i=0; i < property.size(); i++){
			modelInterval.setValueAt(property.get(i),i,0);
			modelInterval.setValueAt(evaluateToText("\"\"" + value.get(i)),i,1);
		}

		//tableInterval.setColumnWidths();
	}



	private void updateXYTable(){

		isChangingValue = true;

		String lbl = selectedGeo.getLabel();

		ArrayList<String> property2 = new ArrayList<String>();
		ArrayList<String> value2 = new ArrayList<String>();

		boolean isFunction = selectedGeo.getGeoClassType() == GeoElement.GEO_CLASS_FUNCTION;

		//columnCount = isFunction ?  2 + extraColumnList.size() : 2;

		
		if(btnTable.isSelected())
		{
			double x = start - step*(pointCount-1)/2;
			double y;
			for(int i=0; i < modelXY.getRowCount(); i++){
				y = evaluateExpression(lbl + "(" + x + ")");
				modelXY.setValueAt(nf.format(x),i,0);
				modelXY.setValueAt(nf.format(y),i,1);
				((GeoPoint) pts.get(i)).setCoords(x, y, 1);
				x = x + step;
			}

			pts.updateRepaint();	
		}
		else{
			double x = start;
			double y;
			y = evaluateExpression(lbl + "(" + x + ")");
			modelXY.setValueAt(nf.format(x),0,0);
			modelXY.setValueAt(nf.format(y),0,1);

		}

		updateExtraColumns();
		//	updatePointList();

		//tableXY.setColumnWidths();

		isChangingValue = false;
	}




	private void addColumn(int columnType){
		extraColumnList.add(columnType);
		modelXY.addColumn(columnNames[columnType]);
		setMyCellRenderer(tableXY);
		tableXY.getColumnModel().getColumn(0).setCellEditor(new MyEditor());

		updateXYTable();
	}

	private void removeColumn(){
		int count = tableXY.getColumnCount();
		if(count <= 2) return;

		extraColumnList.remove(extraColumnList.size()-1);
		//	int lastColumn = tableXY.getColumnCount()-1;
		//	tableXY.removeColumn(tableXY.getColumnModel().getColumn(lastColumn));
		modelXY.setColumnCount(modelXY.getColumnCount()-1);
		setMyCellRenderer(tableXY);
		tableXY.getColumnModel().getColumn(0).setCellEditor(new MyEditor());

		updateXYTable();

	}


	private void updateExtraColumns(){

		if(extraColumnList.size()==0) return;

		String expr;

		for(int column = 2; column < extraColumnList.size() + 2; column ++ ){

			int columnType = extraColumnList.get(column-2);
			switch (columnType){

			case COL_DERIVATIVE:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double d = evaluateExpression(derivative.getLabel() + "(" + x + ")");
					modelXY.setValueAt(nf.format(d),row,column);
				}	
				break;

			case COL_DERIVATIVE2:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double d2 = evaluateExpression(derivative2.getLabel() + "(" + x + ")");
					modelXY.setValueAt(nf.format(d2),row,column);
				}	
				break;

			case COL_CURVATURE:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double y = Double.parseDouble((String) modelXY.getValueAt(row, 1));
					double c = evaluateExpression(
							"Curvature[ (" + x + "," + y  + ")," + selectedGeo.getLabel() + "]");
					modelXY.setValueAt(nf.format(c),row,column);
				}	
				break;

			case COL_DIFFERENCE:

				for(int row=1; row < modelXY.getRowCount(); row++){
					if(modelXY.getValueAt(row-1, column -1) != null){
						double prev = Double.parseDouble((String) modelXY.getValueAt(row-1, column -1));
						double x = Double.parseDouble((String) modelXY.getValueAt(row, column-1));
						modelXY.setValueAt(nf.format(x - prev),row,column);
					}else{
						modelXY.setValueAt(null,row,column);
					}
				}	
				break;

			}

		}

	}



	private String getTitleString(){

		String title;

		if(selectedGeo == null){
			title = app.getMenu("SelectObject");

		}else{
			//	title = selectedGeo.getLongDescriptionHTML(false, true);
			//	if (title.length() > 80)
			title = selectedGeo.getNameDescriptionHTML(false, true);          
		}
		return title;
	}







	//  Action and Other Event Handlers
	// =====================================

	public void actionPerformed(ActionEvent e) {	
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}
		else if (source == btnAddColumn) {
			addColumn(btnAddColumn.getSelectedIndex());
			System.out.println("======================> add colun");
		}	

		else if (source == btnRemoveColumn) {
			removeColumn();
		}	

		updateGUI();


	}	

	private void doTextFieldActionPerformed(JTextField source) {
		try {

			Double value = Double.parseDouble( source.getText().trim());

			if (value != null) {
				if (source == fldStep){ 
					step = value;	
					updateXYTable();		
				}	
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}



	public void setVisible(boolean isVisible) {	

		if (isVisible) {
			app.getKernel().attach(this);
		} else {
			app.getKernel().detach(this);
			clearGeoList();
		}
		super.setVisible(isVisible);
	}



	//     View Implementation
	// =====================================

	public void update(GeoElement geo) {

		if(selectedGeo == null 
				|| testPoint == null 
				|| lowPoint == null 
				|| highPoint == null 
				|| isChangingValue 
				|| isIniting ) return;


		if(selectedGeo.equals(geo)){
			//lblGeoName.setText(selectedGeo.toString());
		}

		else if(tabPanel.getSelectedComponent() == pointTabPanel && testPoint.equals(geo)){
			double[] coords = new double[3];
			testPoint.getCoords(coords);
			this.start = coords[0];
			updateXYTable();
			return;
		}

		else if(tabPanel.getSelectedComponent() == intervalTabPanel 
				&& (lowPoint.equals(geo) || highPoint.equals(geo)) ){
			updateGUI();
			return;
		}



	}

	public void add(GeoElement geo) {}
	public void remove(GeoElement geo) {}
	public void rename(GeoElement geo) {}
	public void updateAuxiliaryObject(GeoElement geo) {}
	public void repaintView() {}
	public void reset() {}
	public void clearView() {}
	public void setMode(int mode) {}



	//  Table Selection Listener
	// =====================================

	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting() || isChangingValue) return;

		tableXY.getSelectionModel().removeListSelectionListener(this);
		if (e.getSource() == tableXY.getSelectionModel()) {
			// row selection changed
			updateTestPoint();
		}		
		tableXY.getSelectionModel().addListSelectionListener(this);
	}



	//    Geo Selection Listener
	// =====================================

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		// TODO: not working directly yet, currently the listener
		// is in InputDialog, so an overridden insertGeoElement() is used instead
	}
	public void insertGeoElement(GeoElement geo) {
		if(geo == null) return;
		selectedGeo = geo;
		lblGeoName.setText(getTitleString());

		initialX = 0.5* (kernel.getApplication().getEuclidianView().getXmin()-
				kernel.getApplication().getEuclidianView().getXmin());

		if(selectedGeo.getGeoClassType() == GeoElement.GEO_CLASS_FUNCTION){

			start = initialX;
			step = 0.25 * kernel.getApplication().getEuclidianView().getGridDistances()[0];
			//	fldStart.removeActionListener(this);
			fldStep.removeActionListener(this);
			//	fldStart.setText("" + start);
			fldStep.setText("" + step);
			//	fldStart.addActionListener(this);
			fldStep.addActionListener(this);

			defineDisplayGeos();

			double x = initialX - 4*step; 
			double y = evaluateExpression(selectedGeo.getLabel() + "(" + x + ")");
			lowPoint.setCoords(x, y, 1);

			x = initialX + 4*step; 
			y = evaluateExpression(selectedGeo.getLabel() + "(" + x + ")");
			highPoint.setCoords(x, y, 1);


			updateGUI();
		}
		//this.pack();
		//table.changeSelection(0,0, false, false);


	}




	//      Key Listeners
	//=========================================

	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();

		tableXY.getSelectionModel().removeListSelectionListener(this);
		switch (key){
		case KeyEvent.VK_UP:
			if(tableXY.getSelectedRow()==0){
				start = start-step;
				updateXYTable();
				updateTestPoint();
			}
			break;

		case KeyEvent.VK_DOWN:
			if(tableXY.getSelectedRow()==tableXY.getRowCount()-1){
				start = start+step;
				updateXYTable();
				tableXY.changeSelection(tableXY.getRowCount()-1, 0, false, false);
				updateTestPoint();
			}
			break;
		}

		tableXY.getSelectionModel().addListSelectionListener(this);

	}

	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}




	//      Mouse Listeners
	//=========================================

	public void mouseClicked(MouseEvent arg0) { }
	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
	public void mousePressed(MouseEvent arg0) { }
	public void mouseReleased(MouseEvent arg0) { }





	//  Update/Create Display Geos
	//=========================================

	private void defineDisplayGeos(){

		String fcn = selectedGeo.getLabel();

		// test point
		if(testPoint != null) 
			testPoint.remove();
		String expr = "Point[" + fcn + "]";
		testPoint = (GeoPoint) createGeoFromString(expr, null, true);
		testPoint.setObjColor(DISPLAY_GEO_COLOR);
		testPoint.setPointSize(4);
		testPoint.setLabel("fiTestPoint");

		// X segment
		if(xSegment != null) 
			xSegment.remove();

		expr = "Segment[" + testPoint.getLabel() + ", (x(" + testPoint.getLabel() + "),0) ]";
		//Application.debug(expr);
		xSegment = createGeoFromString(expr, null, true);
		xSegment.setEuclidianVisible(true);
		xSegment.setObjColor(DISPLAY_GEO_COLOR);
		xSegment.setLineThickness(3);
		xSegment.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		xSegment.setLabel("fiXSegment");

		// Y segment
		if(ySegment != null) 
			ySegment.remove();

		expr = "Segment[" + testPoint.getLabel() + ", (0, y(" + testPoint.getLabel() + ")) ]";
		//Application.debug(expr);
		ySegment = createGeoFromString(expr, null, true);
		ySegment.setObjColor(DISPLAY_GEO_COLOR);
		ySegment.setLineThickness(3);
		ySegment.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		ySegment.setEuclidianVisible(true);
		ySegment.setLabel("fiYSegment");


		// tangent line
		if(tangentLine != null) 
			tangentLine.remove();

		expr = "Tangent[" + fcn + "," + testPoint.getLabel() + "]";
		//Application.debug(expr);
		tangentLine = createGeoFromString(expr, null, true);
		tangentLine.setObjColor(DISPLAY_GEO_COLOR);
		tangentLine.setEuclidianVisible(false);
		tangentLine.setLabel("fiTangentLine");


		// osculating circle
		if( oscCircle != null) 
			oscCircle.remove();

		expr = "OsculatingCircle[" + testPoint.getLabel() + "," + fcn + "]";
		//Application.debug(expr);
		oscCircle = createGeoFromString(expr, null, true);
		oscCircle.setObjColor(DISPLAY_GEO_COLOR);
		oscCircle.setEuclidianVisible(false);
		oscCircle.setLabel("fiOscCircle");


		// derivative
		if( derivative != null) 
			derivative.remove();

		expr = "Derivative[" + fcn + "]";
		//Application.debug(expr);
		derivative = createGeoFromString(expr, null, true);
		derivative.setEuclidianVisible(false);
		derivative.setLabel("fiDerivative");

		// 2nd derivative
		if( derivative2 != null) 
			derivative2.remove();

		expr = "Derivative[" + fcn + " , 2 ]";
		//Application.debug(expr);
		derivative2 = createGeoFromString(expr, null, true);
		derivative2.setEuclidianVisible(false);
		derivative2.setLabel("fiDerivative2");


		// point list
		if( pts != null) 
			pts.remove();

		//Application.debug(expr);
		pts = (GeoList) createGeoFromString("{}", null, true);
		pts.setEuclidianVisible(true);
		pts.setObjColor(new Color(125,125,255));
		pts.setLabel("fiPointList");
		for(int i = 0; i < pointCount; i++){
			pts.add(new GeoPoint(cons));
		}


		// interval points
		if( lowPoint != null) 
			lowPoint.remove();

		expr = "Point[" + fcn + "]";
		//Application.debug(expr);
		lowPoint = (GeoPoint) createGeoFromString(expr, null, true);
		lowPoint.setEuclidianVisible(false);
		lowPoint.setPointSize(4);
		lowPoint.setObjColor(DISPLAY_GEO_COLOR);
		lowPoint.setLabel("fiLowPoint");

		if( highPoint != null) 
			highPoint.remove();

		expr = "Point[" + fcn + "]";
		//Application.debug(expr);
		highPoint = (GeoPoint) createGeoFromString(expr, null, true);
		highPoint.setEuclidianVisible(false);
		highPoint.setPointSize(4);
		highPoint.setObjColor(DISPLAY_GEO_COLOR);
		highPoint.setLabel("fiHighPoint");

		expr = "Function[" + fcn + ", x(" + lowPoint.getLabel() + ") , x(" + highPoint.getLabel() + ") ]";
		//Application.debug(expr);
		functionInterval = createGeoFromString(expr, null, true);
		functionInterval.setEuclidianVisible(false);
		functionInterval.setLineThickness(selectedGeo.getLineThickness()+3);
		functionInterval.setObjColor(DISPLAY_GEO_COLOR);
		functionInterval.setLabel("fiFunctionInterval");

		updateTestPoint();

	}



	private void updateTestPoint(){

		if(testPoint == null || isIniting ) return;

		isChangingValue = true;
		int row = tableXY.getSelectedRow();
		if (row >= 0){
			double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
			double y = evaluateExpression(selectedGeo.getLabel() + "(" + x + ")");
			//double y = Double.parseDouble((String) modelXY.getValueAt(row, 1));
			testPoint.setCoords(x, y, 1);
			testPoint.updateRepaint();	
		}
		isChangingValue = false;

	}





	//  Geo Creation and Evaluation Methods
	//=========================================

	private double evaluateExpression(String expr){
		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	
		return nv.getDouble();
	}


	private String evaluateToText(String expr){
		GeoText text = kernel.getAlgebraProcessor().evaluateToText(expr, false);	
		return text.getTextString();
	}


	public GeoElement createGeoFromString(String text, String label, boolean suppressLabelCreation ){

		try {

			boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();

			if(suppressLabelCreation)
				cons.setSuppressLabelCreation(true);
			//Application.debug(text);
			GeoElement[] geos = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptions(text, false);	

			if(label != null)
				geos[0].setLabel(label);

			// set visibility
			geos[0].setEuclidianVisible(true);	
			geos[0].setAuxiliaryObject(true);
			geos[0].setLabelVisible(false);

			if(suppressLabelCreation)
				cons.setSuppressLabelCreation(oldSuppressLabelMode);

			geoList.add(geos[0]);
			return geos[0];

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



	private void clearGeoList(){
		Application.debug("");
		for(GeoElement geo : geoList){
			if(geo != null){
				geo.remove();
			}
		}
		geoList.clear();
	}

	public void updateFonts(){
		this.setFont(app.getPlainFont());
		tableXY.setFont(app.getPlainFont());
		tableInterval.setFont(app.getPlainFont());
		MyTextField dummyField = new MyTextField(app.getGuiManager());
		tableXY.setRowHeight(dummyField.getPreferredSize().height);
		tableInterval.setRowHeight(dummyField.getPreferredSize().height);
	}


	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			//if(app.getMode() == EuclidianConstants.MODE_FUNCTION_INSPECTOR)
			//app.setSelectionListenerMode(sl);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}





	public void changeStart(double x) {
		tableXY.getSelectionModel().removeListSelectionListener(this);
		try {
			start = x;
			//Application.debug("" + start);
			//modelXY.removeTableModelListener(this);
			updateXYTable();
			updateTestPoint();
			//modelXY.addTableModelListener(this);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		tableXY.getSelectionModel().addListSelectionListener(this);
	}

	public void updateSelectionRectangle(EuclidianView view){
		//System.out.println(view.getSelectionRectangle().toString());

	}





	//  InspectorTable 
	// =====================================

	private class InspectorTable extends JTable{

		boolean doRedNegative = false;	
		HashSet<Point> editableCell;

		public InspectorTable(){
			super(minRows,2);

			this.setShowGrid(true);
			this.setGridColor(TABLE_GRID_COLOR);
			this.setSelectionBackground(MyTable.SELECTED_BACKGROUND_COLOR);


			//table.setAutoCreateColumnsFromModel(false);
			this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			this.setPreferredScrollableViewportSize(this.getPreferredSize());
			this.setBorder(null);
			//this.addKeyListener(this);

			// list to store column types of dynamically appended columns 
			extraColumnList = new ArrayList<Integer>();
			editableCell = new HashSet<Point>();
		}


		public boolean isDoRedNegative() {
			return doRedNegative;
		}

		public void setDoRedNegative(boolean doRedNegative) {
			this.doRedNegative = doRedNegative;
		}

		public void setCellEditable(int rowIndex, int colIndex) {
			if(rowIndex == -1 && colIndex == -1)
				editableCell.clear();
			else
				editableCell.add(new Point(rowIndex, colIndex));

		}

		// control cell editing
		@Override
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return editableCell.contains(new Point(rowIndex, colIndex));   
		}

		// fill empty scroll pane space with table background color
		@Override
		protected void configureEnclosingScrollPane() {
			super.configureEnclosingScrollPane();
			Container p = getParent();
			if (p instanceof JViewport) {
				((JViewport) p).setBackground(getBackground());
			}
		}



		public void setColumnWidths(){
			setColumnWidths(this);
		}
		private void setColumnWidths(JTable table){

			int w;
			for (int i = 0; i < getColumnCount(); ++ i) {	
				w = getMaxColumnWidth(table,i) + 5; 
				table.getColumnModel().getColumn(i).setPreferredWidth(w);
			}

			int gap = table.getParent().getPreferredSize().width - table.getPreferredSize().width;
			//System.out.println(table.getParent().getPreferredSize().width);
			if(gap > 0){
				w = table.getColumnCount() - 1;
				int newWidth = gap + table.getColumnModel().getColumn(table.getColumnCount() - 1).getWidth() ;
				table.getColumnModel().getColumn(w).setPreferredWidth(newWidth);
			}
		}


		/**
		 * Finds the maximum preferred width of a column.
		 */
		public int getMaxColumnWidth(JTable table, int column){

			TableColumn tableColumn = table.getColumnModel().getColumn(column); 

			// iterate through the rows and find the preferred width
			int maxPrefWidth = tableColumn.getPreferredWidth();
			int colPrefWidth = 0;
			for (int row = 0; row < table.getRowCount(); row++) {
				if(table.getValueAt(row, column)!=null){
					colPrefWidth = (int) table.getCellRenderer(row, column)
					.getTableCellRendererComponent(table,
							table.getValueAt(row, column), false, false,
							row, column).getPreferredSize().getWidth();
					maxPrefWidth = Math.max(maxPrefWidth, colPrefWidth);
				}
			}

			return maxPrefWidth + table.getIntercellSpacing().width;
		}





	}

	private void setMyCellRenderer(InspectorTable table){
		for (int i = 0; i < table.getColumnCount(); i++){ 
			TableColumn col = table.getColumnModel().getColumn(i);
			col.setCellRenderer(new MyCellRenderer(table));
		}
	}



	public class MyCellRenderer extends DefaultTableCellRenderer  {

		private JTextField tf;
		private Border editCellBorder;
		private JTable table;
		private Border paddingBorder;
		private boolean doRedNegative;


		private MyCellRenderer(InspectorTable table){
			this.table = table;
			tf = new JTextField();
			this.doRedNegative = table.isDoRedNegative();
			paddingBorder = BorderFactory.createEmptyBorder(2,2,2,2);
			editCellBorder = BorderFactory.createCompoundBorder(tf.getBorder(), paddingBorder);

		}
		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, final int row, int column) {

			setFont(app.getPlainFont());

			if(table.isCellEditable(row, column))
				setBorder(editCellBorder);
			else
				setBorder(paddingBorder);


			if (isSelected && !table.isCellEditable(row, column)) {
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			} else {
				setBackground(rowColor(row));
				setForeground(getForeground());
			}

			setForeground(Color.black);

			if(value != null){
				try {
					double val = Double.parseDouble((String) value);
					if(val < 0 && doRedNegative)
						setForeground(Color.red);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}

			setText((String) value);
			return this;

		}

		// shade alternate rows
		private Color rowColor(int row){
			Color c;
			//if (row % 2 == 0) 
			//	c = EVEN_ROW_COLOR;
			// else 
			c = table.getBackground();
			return c;
		}

	}


	class MyEditor extends DefaultCellEditor {
		public MyEditor() {
			super(new MyTextField(app.getGuiManager()));
			this.setClickCountToStart(1);

		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected,
				int row, int column) {
			JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected,
					row, column);
			editor.setFont(app.getPlainFont());
			return editor;
		}


		public boolean stopCellEditing() {
			boolean isStopped = super.stopCellEditing();
			//("-----------> STOPPED    !!!!!!!!!!!!");
			//System.out.println("-----------> " + (String) this.getCellEditorValue());
			try {
				if(isStopped){

					double val = Double.parseDouble((String) this.getCellEditorValue());
					changeStart(val);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}

			return isStopped; 
		}

	}



}






