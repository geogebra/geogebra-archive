/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.view.spreadsheet;

import geogebra.gui.InputDialog;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;


/**
 * View for inspecting selected GeoElements
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public class InspectorView extends InputDialog implements View, MouseListener, ListSelectionListener, KeyListener, ActionListener{

	// column types
	private static final int COL_DERIVATIVE = 0;
	private static final int COL_DERIVATIVE2 = 1;
	private static final int COL_DIFFERENCE = 2;
	private static final int COL_CURVATURE = 3;


	// ggb fields
	private Kernel kernel;
	private Construction cons;

	// table fields
	private JTable table;
	private DefaultTableModel model;
	private ArrayList<Integer> extraColumnList;

	// GUI 
	private JLabel lblGeoName, lblStart, lblStep;
	private MyTextField fldStart, fldStep;
	private JPanel controlPanel;
	private JCheckBox ckShowTangent, ckShowOscCircle, ckShowX, ckShowY, ckShowAutoTable;
	private JComboBox cbShow, cbAdd;
	private JButton btnAdd, btnRemove;
	private String[] columnNames;


	// Geos
	private GeoElement selectedGeo, tangentLine, oscCircle, xSegment, ySegment;
	private GeoElement derivative, derivative2;
	private GeoPoint testPoint;
	private GeoList pts;
	private double start =-1;
	private double step = 0.1;
	private JLabel lblShow;
	private ArrayList<GeoElement> geoList;



	/** Constructor */
	public InspectorView(Application app) {

		super(app.getFrame(), false);
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		boolean showApply = false;

		// setup InputDialog GUI
		String title = app.getPlain("Inspector");
		createGUI(title, "", false, 16, 1, false, false, false, false, false, showApply, false);
		this.btOK.setVisible(false);
		this.btCancel.setVisible(false);


		// create Table and additional GUI elements
		geoList = new ArrayList<GeoElement>();
		createTable();
		createGUIElements();
		

		// put additional GUI together
		JPanel cp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cp1.add(lblStart);
		cp1.add(fldStart);
		cp1.add(lblStep);
		cp1.add(fldStep);

		JPanel cp2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cp2.add(lblShow);
		cp2.add(ckShowTangent);
		cp2.add(ckShowX);
		cp2.add(ckShowY);
		cp2.add(ckShowOscCircle);
		
		
		
		//cp2.add(cbShow);

		JPanel cp3 = new JPanel(new FlowLayout(FlowLayout.LEFT));

		cp3.add(btnAdd);
		cp3.add(cbAdd);
		cp3.add(btnRemove);


		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
		controlPanel.add(cp1);
		controlPanel.add(cp2);
		controlPanel.add(cp3);

		
		controlPanel.setVisible(false);
		
		JScrollPane scroller = new JScrollPane(table);
		scroller.setPreferredSize(new Dimension(350,400));

		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(lblGeoName, BorderLayout.CENTER);


		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(headerPanel,BorderLayout.NORTH);
		centerPanel.add(scroller,BorderLayout.CENTER);
		centerPanel.add(controlPanel,BorderLayout.SOUTH);

		getContentPane().add(centerPanel,BorderLayout.CENTER);

		centerOnScreen();
		setResizable(true);
		app.getKernel().attach(this);
	}



	//  Create GUI elements 
	// =====================================

	private void createGUIElements(){

		columnNames = new String[4];
		columnNames[COL_DERIVATIVE] =	app.getPlain("f'");
		columnNames[COL_DERIVATIVE2] =	app.getPlain("f''");
		columnNames[COL_CURVATURE] =	app.getPlain("curvature");
		columnNames[COL_DIFFERENCE] =	app.getPlain("difference");

		lblGeoName = new JLabel();
		lblStep = new JLabel(app.getMenu("Step") + ":");
		lblStart = new JLabel(app.getMenu("Start") + ":");
		lblShow = new JLabel(app.getMenu("Show") + ":");
		fldStep = new MyTextField(app.getGuiManager());
		fldStep.addActionListener(this);
		fldStep.setColumns(6);

		fldStart = new MyTextField(app.getGuiManager());
		fldStart.addActionListener(this);
		fldStart.setColumns(6);

		ckShowTangent = new JCheckBox(app.getMenu("Tangent"));
		ckShowOscCircle = new JCheckBox(app.getMenu("OsculatingCircle"));
		ckShowX = new JCheckBox(app.getMenu("Xseg"));
		ckShowY = new JCheckBox(app.getMenu("Yseg"));
		ckShowTangent.addActionListener(this);
		ckShowOscCircle.addActionListener(this);
		ckShowX.addActionListener(this);
		ckShowY.addActionListener(this);
				

		cbShow = new JComboBox();
		cbShow.addItem(app.getMenu("Tangent"));
		cbShow.addItem(app.getMenu("XLine"));
		cbShow.addItem(app.getMenu("YLine"));
		cbShow.addItem(app.getMenu("OsculatingCircle"));

		cbAdd = new JComboBox(columnNames);

		btnAdd = new JButton("\u271A");
		btnAdd.addActionListener(this);

		btnRemove = new JButton("\u2718");
		btnRemove.addActionListener(this);

	}

	
	
	
	//  Create/Setup Table 
	// =====================================

	private void createTable(){

		table = new JTable(1,2){
			// disable cell editing
			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return false;   
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
		};

		table.setShowGrid(true);
		table.setGridColor(MyTable.TABLE_GRID_COLOR);
		//table.setAutoCreateColumnsFromModel(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setBorder(null);
		table.getSelectionModel().addListSelectionListener(this);
		table.addKeyListener(this);

		extraColumnList = new ArrayList<Integer>();

	}


	//     Table Update
	// =====================================

	private void populateTableModel(){

		String lbl = selectedGeo.getLabel();
		ArrayList<String> property = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();


		switch (selectedGeo.getGeoClassType()){

		case GeoElement.GEO_CLASS_POINT:

			property.add(app.getCommand("Angle"));
			value.add("Angle[" + lbl + "]");
			break;

		case GeoElement.GEO_CLASS_POLYGON:

			GeoPoint[] pts =  ((GeoPolygon)selectedGeo).getPoints();
			for(int i=0; i<pts.length; i++){
				property.add(pts[i].getLabel());
				value.add(pts[i].toDefinedValueString());
			}

			property.add(app.getCommand("Area"));
			value.add("Area[" + lbl + "]");

			property.add(app.getCommand("Perimeter"));
			value.add("Perimeter[" + lbl + "]");

			break;

		case GeoElement.GEO_CLASS_LIST:

			GeoList g = (GeoList) selectedGeo;
			if(((GeoElement)g).getGeoElementForPropertiesDialog().isNumberValue()){

				property.add(app.getCommand("Mean"));
				value.add("Mean[" + lbl + "]");

				property.add(app.getCommand("SD"));
				value.add("SD[" + lbl + "]");

				property.add(app.getCommand("Min"));
				value.add("Min[" + lbl + "]");

				property.add(app.getCommand("Q1"));
				value.add("Q1[" + lbl + "]");

				property.add(app.getCommand("Median"));
				value.add("Median[" + lbl + "]");

				property.add(app.getCommand("Q3"));
				value.add("Q3[" + lbl + "]");

				property.add(app.getCommand("Max"));
				value.add("Max[" + lbl + "]");

			}
			break;


		case GeoElement.GEO_CLASS_CONIC:

			property.add(app.getCommand("Center"));
			value.add("Center[" + lbl + "]");

			property.add(app.getCommand("Radius"));
			value.add("Radius[" + lbl + "]");

			property.add(app.getCommand("Area"));
			value.add("Area[" + lbl + "]");

			property.add(app.getCommand("Circumference"));
			value.add("Circumference[" + lbl + "]");

			property.add(app.getCommand("Eccentricity"));
			value.add("Eccentricity[" + lbl + "]");

			break;

		case GeoElement.GEO_CLASS_FUNCTION:

			double x = start;
			for(int i = 0; i < 10; i++){
				property.add("" + x);
				value.add( lbl + "(" + x + ")");
				x = x + step;
			}

			break;

		}

		boolean isFunction = selectedGeo.getGeoClassType() == GeoElement.GEO_CLASS_FUNCTION;

		int columnCount = isFunction ?  2 + extraColumnList.size() : 2;

		model = new DefaultTableModel(property.size(), columnCount);
		for(int i=0; i < property.size(); i++){
			model.setValueAt(property.get(i),i,0);
			model.setValueAt(evaluateToText("\"\"" + value.get(i)),i,1);
		}

		if(isFunction){
			updateExtraColumns();
		}

		table.setModel(model);

		setColumnWidths();


		if(isFunction){
			if(table.getColumnCount() > 0){
				table.getColumnModel().getColumn(0).setHeaderValue("x");
				table.getColumnModel().getColumn(1).setHeaderValue("y");
				for(int i = 0; i < extraColumnList.size() ; i++){				
					table.getColumnModel().getColumn(i+2).setHeaderValue(columnNames[extraColumnList.get(i)]);
				}

			}
			controlPanel.setVisible(true);

		}else{
			if(table.getColumnCount() > 0){
				table.getColumnModel().getColumn(0).setHeaderValue(app.getPlain("Property"));
				table.getColumnModel().getColumn(1).setHeaderValue(app.getPlain("Value"));
			}
			controlPanel.setVisible(false);
		}

	}




	private void addColumn(int columnType){
		extraColumnList.add(columnType);
		populateTableModel();
	}

	private void removeColumn(){
		int count = table.getColumnCount();
		if(count <= 2) return;
		extraColumnList.remove(extraColumnList.size()-1);
		populateTableModel();

	}


	private void updateExtraColumns(){

		if(extraColumnList.size()==0) return;

		String expr;

		for(int column = 2; column < extraColumnList.size() + 2; column ++ ){

			int columnType = extraColumnList.get(column-2);
			switch (columnType){

			case COL_DERIVATIVE:

				for(int row=0; row < table.getRowCount(); row++){
					double x = Double.parseDouble((String) model.getValueAt(row, 0));
					expr = derivative.getLabel() + "(" + x + ")";
					model.setValueAt(evaluateToText("\"\"" + expr),row,column);
				}	
				break;

			case COL_DERIVATIVE2:

				for(int row=0; row < table.getRowCount(); row++){
					double x = Double.parseDouble((String) model.getValueAt(row, 0));
					expr = derivative2.getLabel() + "(" + x + ")";
					model.setValueAt(evaluateToText("\"\"" + expr),row,column);
				}	
				break;

			case COL_CURVATURE:

				for(int row=0; row < table.getRowCount(); row++){
					double x = Double.parseDouble((String) model.getValueAt(row, 0));
					double y = Double.parseDouble((String) model.getValueAt(row, 1));
					double c = this.evaluateExpression(
							"Curvature[ (" + x + "," + y  + ")," + selectedGeo.getLabel() + "]");
					model.setValueAt("" + c,row,column);
				}	
				break;

			case COL_DIFFERENCE:

				for(int row=1; row < table.getRowCount(); row++){
					double prev = Double.parseDouble((String) model.getValueAt(row-1, column -1));
					double x = Double.parseDouble((String) model.getValueAt(row, column-1));
					model.setValueAt("" + (x - prev),row,column);
				}	
				break;

			}

		}

	}





	private String getTitleString(){

		String title = selectedGeo.getLongDescriptionHTML(false, true);
		if (title.length() > 80)
			title = selectedGeo.getNameDescriptionHTML(false, true);          

		return title;
	}



	private void setColumnWidths(){

		int w;
		for (int i = 0; i < table.getColumnCount(); ++ i) {	
			w = getMaxColumnWidth(table,i); 
			table.getColumnModel().getColumn(i).setPreferredWidth(w);
		}
	}


	/**
	 * Finds the maximum preferred width of a column.
	 */
	public int getMaxColumnWidth(JTable table, int column){

		TableColumn tableColumn = table.getColumnModel().getColumn(column); 

		// iterate through the rows and find the preferred width
		int maxPrefWidth = 0;
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





	//  Action and Other Event Handlers
	// =====================================

	public void actionPerformed(ActionEvent e) {	
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}

		else if (source == btnAdd) {
			addColumn(cbAdd.getSelectedIndex());
		}	

		else if (source == btnRemove) {
			removeColumn();
		}	
		
		else if (source == ckShowTangent) {
			tangentLine.setEuclidianVisible(ckShowTangent.isSelected());
			tangentLine.updateRepaint();
		}	
		
		else if (source == ckShowOscCircle) {
			oscCircle.setEuclidianVisible(ckShowOscCircle.isSelected());
			oscCircle.updateRepaint();
		}	
		
		else if (source == ckShowX) {
			xSegment.setEuclidianVisible(ckShowX.isSelected());
			xSegment.updateRepaint();
		}	
		
		else if (source == ckShowY) {
			ySegment.setEuclidianVisible(ckShowY.isSelected());
			ySegment.updateRepaint();
		}	
		
		
		
		

	}	

	private void doTextFieldActionPerformed(JTextField source) {
		try {
			String inputText = source.getText().trim();
			Double value = Double.parseDouble(source.getText());

			if (value != null) {
				if (source == fldStep) 
					step = value;	
				if (source == fldStart)
					start = value;
				this.populateTableModel();			
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

	}



	public void setVisible(boolean isVisible) {		
		super.setVisible(isVisible);

		if (isVisible) {
			app.getKernel().attach(this);
		} else {
			app.getKernel().detach(this);
			clearGeoList();
		}		
	}



	//     View Implementation
	// =====================================

	public void update(GeoElement geo) {
		if(selectedGeo == null) return;
		if(selectedGeo.equals(geo)){
			lblGeoName.setText(selectedGeo.toString());
			populateTableModel();
			table.repaint();
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

		if (e.getValueIsAdjusting()) return;

		table.getSelectionModel().removeListSelectionListener(this);
		if (e.getSource() == table.getSelectionModel() && table.getRowSelectionAllowed()) {
			// row selection changed
			updateTestPoint();
		}		
		table.getSelectionModel().addListSelectionListener(this);
	}



	//    Geo Selection Listener
	// =====================================

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		// TODO: not working directly yet, currently the listener
		// is in InputDialog, so an overridden insertGeoElement() is used instead
	}
	public void insertGeoElement(GeoElement geo) {
		selectedGeo = geo;
		lblGeoName.setText(getTitleString());

		if(selectedGeo.getGeoClassType() == GeoElement.GEO_CLASS_FUNCTION){
			start = -1;
			step = 0.25 * kernel.getApplication().getEuclidianView().getGridDistances()[0];
			fldStart.removeActionListener(this);
			fldStep.removeActionListener(this);
			fldStart.setText("" + start);
			fldStep.setText("" + step);
			fldStart.addActionListener(this);
			fldStep.addActionListener(this);

			defineDisplayGeos();
		}

		populateTableModel();
		//table.changeSelection(0,0, false, false);
		
		
	}




	//      Key Listeners
	//=========================================

	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();

		table.getSelectionModel().removeListSelectionListener(this);
		switch (key){
		case KeyEvent.VK_UP:
			if(table.getSelectedRow()==0){
				start = start-step;
				populateTableModel();
				updateTestPoint();
			}
			break;

		case KeyEvent.VK_DOWN:
			if(table.getSelectedRow()==table.getRowCount()-1){
				start = start+step;
				populateTableModel();
				table.changeSelection(table.getRowCount()-1, 0, false, false);
				updateTestPoint();
			}
			break;
		}

		table.getSelectionModel().addListSelectionListener(this);

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

		// test point
		if(testPoint != null) 
			testPoint.remove();
		String expr = "Point[" + selectedGeo.getLabel() + "]";
		testPoint = (GeoPoint) createGeoFromString(expr, null, true);
		testPoint.setLabel("testPoint");

		// X segment
		if(xSegment != null) 
			xSegment.remove();

		expr = "Segment[" + testPoint.getLabel() + ", (x(" + testPoint.getLabel() + "),0) ]";
		//Application.debug(expr);
		xSegment = createGeoFromString(expr, null, true);
		xSegment.setEuclidianVisible(false);
		xSegment.setObjColor(Color.red);
		xSegment.setLabel("xSegment");

		// Y segment
		if(ySegment != null) 
			ySegment.remove();

		expr = "Segment[" + testPoint.getLabel() + ", (0, y(" + testPoint.getLabel() + ")) ]";
		//Application.debug(expr);
		ySegment = createGeoFromString(expr, null, true);
		ySegment.setObjColor(Color.red);
		ySegment.setEuclidianVisible(false);
		ySegment.setLabel("ySegment");


		// tangent line
		if(tangentLine != null) 
			tangentLine.remove();

		expr = "Tangent[" + selectedGeo.getLabel() + "," + testPoint.getLabel() + "]";
		//Application.debug(expr);
		tangentLine = createGeoFromString(expr, null, true);
		tangentLine.setObjColor(Color.red);
		tangentLine.setEuclidianVisible(false);
		tangentLine.setLabel("tangentLine");


		// osculating circle
		if( oscCircle != null) 
			oscCircle.remove();

		expr = "OsculatingCircle[" + testPoint.getLabel() + "," + selectedGeo.getLabel() + "]";
		//Application.debug(expr);
		oscCircle = createGeoFromString(expr, null, true);
		oscCircle.setObjColor(Color.red);
		oscCircle.setEuclidianVisible(false);
		oscCircle.setLabel("oscCircle");


		// derivative
		if( derivative != null) 
			derivative.remove();

		expr = "Derivative[" + selectedGeo.getLabel() + "]";
		//Application.debug(expr);
		derivative = createGeoFromString(expr, null, true);
		derivative.setEuclidianVisible(false);
		derivative.setLabel("derivative");

		// 2nd derivative
		if( derivative2 != null) 
			derivative2.remove();

		expr = "Derivative[" + selectedGeo.getLabel() + " , 2 ]";
		//Application.debug(expr);
		derivative2 = createGeoFromString(expr, null, true);
		derivative2.setEuclidianVisible(false);
		derivative2.setLabel("derivative2");

		updateTestPoint();

	}



	private void updateTestPoint(){

		if(testPoint != null){
			int row = table.getSelectedRow();
			if (row >=0){
				double x = Double.parseDouble((String) model.getValueAt(row, 0));
				double y = Double.parseDouble((String) model.getValueAt(row, 1));
				testPoint.setCoords(x, y, 1);
				testPoint.updateRepaint();	
			}
		}
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
		for(GeoElement geo : geoList){
			if(geo != null)
				geo.remove();
		}
		geoList.clear();
	}



}






