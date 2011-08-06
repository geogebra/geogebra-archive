/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.gui.view.functioninspector;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.InputDialog;
import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.AlgoCasDerivative;
import geogebra.kernel.AlgoCurvature;
import geogebra.kernel.AlgoDependentNumber;
import geogebra.kernel.AlgoDependentPoint;
import geogebra.kernel.AlgoFunctionInterval;
import geogebra.kernel.AlgoIntegralDefinite;
import geogebra.kernel.AlgoJoinPointsSegment;
import geogebra.kernel.AlgoLengthFunction;
import geogebra.kernel.AlgoOsculatingCircle;
import geogebra.kernel.AlgoPointOnPath;
import geogebra.kernel.AlgoRoots;
import geogebra.kernel.AlgoTangentFunctionPoint;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.Kernel;
import geogebra.kernel.Path;
import geogebra.kernel.View;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.MyDouble;
import geogebra.kernel.arithmetic.MyVecNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.kernel.optimization.ExtremumFinder;
import geogebra.kernel.roots.RealRootFunction;
import geogebra.main.Application;
import geogebra.main.GeoGebraColorConstants;

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
import java.awt.event.WindowEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;


/**
 * View for inspecting selected GeoFunctions
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public class FunctionInspector extends InputDialog 
implements View, MouseListener, ListSelectionListener, 
KeyListener, ActionListener{

	private static final Color DISPLAY_GEO_COLOR = Color.BLUE;
	private static final Color DISPLAY_GEO2_COLOR = Color.BLUE;

	private static final Color EVEN_ROW_COLOR = new Color(241, 245, 250);
	private static final Color TABLE_GRID_COLOR = GeoGebraColorConstants.TABLE_GRID_COLOR;

	private static final int minRows = 12;

	// column types
	private static final int COL_DERIVATIVE = 0;
	private static final int COL_DERIVATIVE2 = 1;
	private static final int COL_DIFFERENCE = 2;
	private static final int COL_CURVATURE = 3;


	// ggb fields
	private Kernel kernel;
	private Construction cons;
	private EuclidianView activeEV;
	
	// table fields
	private InspectorTable tableXY, tableInterval;
	private DefaultTableModel modelXY, modelInterval;
	private String[] columnNames;

	// list to store column types of dynamically appended columns 
	private ArrayList<Integer> extraColumnList;


	// GUI 
	private JLabel lblGeoName, lblStep, lblInterval;
	private MyTextField fldStep, fldLow, fldHigh;
	private JButton btnRemoveColumn;
	private JToggleButton btnOscCircle, btnTangent, btnXYSegments, btnTable;
	private PopupMenuButton btnAddColumn;
	private JTabbedPane tabPanel;
	private JPanel intervalTabPanel, pointTabPanel, headerPanel, helpPanel;

	
	// Geos
	private GeoElement tangentLine, oscCircle, xSegment, ySegment;
	private GeoElement functionInterval, integralGeo, lengthGeo;
	private GeoFunction derivative, derivative2, selectedGeo;
	private GeoPoint testPoint, lowPoint, highPoint, minPoint, maxPoint;
	private GeoList pts;
	
	private ArrayList<GeoElement> intervalTabGeoList, pointTabGeoList, hiddenGeoList;
	private GeoElement[] rootGeos;

	
	
	private boolean isIniting;
	private double xMin, xMax, start =-1, step = 0.1;
	private double initialX;
	
	private boolean isChangingValue;
	private int pointCount = 9;

	private NumberFormat nf;



	/** Constructor */
	public FunctionInspector(Application app, GeoFunction selectedGeo) {

		super(app.getFrame(), false);
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		boolean showApply = false;
		this.selectedGeo = selectedGeo;
		activeEV = (EuclidianView) app.getActiveEuclidianView();	

		extraColumnList = new ArrayList<Integer>();


		// setup InputDialog GUI
		isIniting = true;
		String title = app.getMenu("FunctionInspector");
		createGUI(title, "", false, 16, 1, false, false, false, false, false, showApply, false);
		this.btOK.setVisible(false);
		this.btCancel.setVisible(false);


		nf = NumberFormat.getInstance(Locale.ENGLISH);
		nf.setMaximumFractionDigits(4);
		nf.setGroupingUsed(false);


		// lists of all geos we create
		intervalTabGeoList = new ArrayList<GeoElement>();
		pointTabGeoList = new ArrayList<GeoElement>();
		hiddenGeoList = new ArrayList<GeoElement>();

		// create the GUI components
		createGUIElements();


		// build dialog content pane
		createHeaderPanel();
		createTabPanel();
		
		getContentPane().add(headerPanel,BorderLayout.NORTH);
		getContentPane().add(tabPanel,BorderLayout.CENTER);
		
		centerOnScreen();
		setResizable(true);


		// attach this view to the kernel
		app.getKernel().attach(this);


		// update and load selected function 
		updateFonts();
		setLabels();
		insertGeoElement(selectedGeo);
		handleTabChange();
		
		//addHelpButton(Application.WIKI_MANUAL);
		
		pack();

		isIniting = false;

	}


	private void createTabPanel(){

		createTabPointPanel();
		createTabIntervalPanel();

		// build tab panel
		tabPanel = new JTabbedPane();		
		tabPanel.addTab("Interval", intervalTabPanel);
		tabPanel.addTab("Point", pointTabPanel);

		tabPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent evt) {
				handleTabChange();
			}

		});
	}

	private void createHeaderPanel(){

		createHelpPanel();
		
		headerPanel = new JPanel(new BorderLayout());
		headerPanel.add(lblGeoName, BorderLayout.CENTER);	
	//	headerPanel.add(helpPanel,BorderLayout.EAST);
		headerPanel.setBorder(BorderFactory.createEmptyBorder(2,5,2,2));
	}


	private void createHelpPanel(){

		helpPanel = new JPanel(new FlowLayout());
		JButton helpButton = new JButton(app.getImageIcon("help.png"));
		helpPanel.add(helpButton);
	}


	private void createTabIntervalPanel(){
		JToolBar intervalTB = new JToolBar();   //JPanel(new FlowLayout(FlowLayout.LEFT));
		intervalTB.setFloatable(false);
		intervalTB.add(fldLow);
		intervalTB.add(lblInterval);
		intervalTB.add(fldHigh);

		intervalTabPanel = new JPanel(new BorderLayout(5,5));
		intervalTabPanel.add(new JScrollPane(tableInterval), BorderLayout.CENTER);
		intervalTabPanel.add(intervalTB, BorderLayout.SOUTH);

	}

	private void createTabPointPanel(){


		// create step toolbar
		JToolBar tb1 = new JToolBar();   
		tb1.setFloatable(false);
		tb1.add(lblStep);
		tb1.add(fldStep);

		// create add/remove column toolbar
		JToolBar tb2 = new JToolBar();
		tb2.setFloatable(false);
		tb2.add(btnAddColumn);
		tb2.add(btnRemoveColumn);


		// create toggle graphics panel

		FlowLayout flow = new FlowLayout(FlowLayout.CENTER); 
		flow.setHgap(10);              
		JPanel tb3 = new JPanel(flow);
		//JToolBar tb3 = new JToolBar();
		//tb3.setFloatable(false);
		tb3.add(btnTable);
		tb3.add(btnXYSegments);
		tb3.add(btnTangent);
		tb3.add(btnOscCircle);
		JPanel toggleGraphicsPanel = new JPanel(new BorderLayout());
		toggleGraphicsPanel.add(tb3, BorderLayout.CENTER);



		// create the panel
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(tb1,BorderLayout.WEST);
		northPanel.add(tb2,BorderLayout.EAST);

		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(toggleGraphicsPanel,BorderLayout.CENTER);

		JScrollPane scroller = new JScrollPane(tableXY);

		pointTabPanel = new JPanel(new BorderLayout(2,2));
		pointTabPanel.add(northPanel,BorderLayout.NORTH);
		pointTabPanel.add(scroller,BorderLayout.CENTER);
		pointTabPanel.add(southPanel,BorderLayout.SOUTH);

	}





	//  Create GUI elements 
	// =====================================

	private void createGUIElements(){

		// create XY table
		tableXY = new InspectorTable(app, this, minRows, InspectorTable.TYPE_XY);
		modelXY = new DefaultTableModel();
		modelXY.addColumn("x");
		modelXY.addColumn("y(x)");
		modelXY.setRowCount(pointCount);
		tableXY.setModel(modelXY);


		tableXY.getSelectionModel().addListSelectionListener(this);
		//tableXY.addKeyListener(this);
		tableXY.setMyCellEditor(0);


		// create interval table
		tableInterval = new InspectorTable(app, this, minRows, InspectorTable.TYPE_INTERVAL);
		modelInterval = new DefaultTableModel();
		modelInterval.setColumnCount(2);
		modelInterval.setRowCount(pointCount);
		tableInterval.setModel(modelInterval);
		tableInterval.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e) {  
				updateIntervalGeoVisiblity();
			}
		});


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
		//btnTable.setSelectedIcon(app.getImageIcon("xy_table.png", Color.black));

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

		makeBtnAddColumn();

	}


	private void makeBtnAddColumn() {
		columnNames = new String[4];
		columnNames[COL_DERIVATIVE] =	app.getPlain("fncInspector.Derivative");
		columnNames[COL_DERIVATIVE2] =	app.getPlain("fncInspector.Derivative2");
		columnNames[COL_CURVATURE] =	app.getPlain("fncInspector.Curvature");
		columnNames[COL_DIFFERENCE] =	app.getPlain("fncInspector.Difference");
		btnAddColumn = new PopupMenuButton(app, columnNames, -1, 1, 
				new Dimension(0, 18), SelectionTable.MODE_TEXT);
		btnAddColumn.setKeepVisible(false);
		btnAddColumn.setStandardButton(true);
		btnAddColumn.setFixedIcon(GeoGebraIcon.createEmptyIcon(1, 1));
		btnAddColumn.setText("\u271A");
		btnAddColumn.addActionListener(this);
	}



	public void setLabels() {

		String[] intervalColumnNames = {app.getPlain("fncInspector.Property"), app.getPlain("fncInspector.Value")};
		modelInterval.setColumnIdentifiers(intervalColumnNames);

		lblStep.setText(app.getMenu("Step") + ":");		
		lblInterval.setText(" \u2264 x \u2264 " );	// <= x <=

		btnRemoveColumn.setText("\u2718");
		//btnAddColumn.setText("\u271A");

		tabPanel.setTitleAt(1, app.getPlain("fncInspector.Points"));
		tabPanel.setTitleAt(0, app.getPlain("fncInspector.Interval"));

		//tool tips
		btnOscCircle.setToolTipText(app.getPlainTooltip("fncInspector.showOscCircle"));
		btnXYSegments.setToolTipText(app.getPlainTooltip("fncInspector.showXYLines"));
		btnTable.setToolTipText(app.getPlainTooltip("fncInspector.showTable"));
		btnTangent.setToolTipText(app.getPlainTooltip("fncInspector.showTangent"));
		btnAddColumn.setToolTipText(app.getPlainTooltip("fncInspector.addColumn"));
		btnRemoveColumn.setToolTipText(app.getPlainTooltip("fncInspector.removeColumn"));
		fldStep.setToolTipText(app.getPlainTooltip("fncInspector.step"));
		lblStep.setToolTipText(app.getPlainTooltip("fncInspector.step"));		
		lblGeoName.setText(getTitleString());
		Container c = btnAddColumn.getParent();
		c.removeAll();
		makeBtnAddColumn();		
		c.add(btnAddColumn);
		c.add(btnRemoveColumn);
	}


	private String getTitleString(){

		if(selectedGeo == null)
			return app.getMenu("SelectObject");
		else
			return selectedGeo.getNameDescriptionHTML(false, true);          
	}



	//  GUI update
	// =====================================
	private void updateGUI(){

		if(tabPanel.getSelectedComponent()==intervalTabPanel){

			updateIntervalTable();
			updateIntervalGeoVisiblity();

		}else{

			tangentLine.setEuclidianVisible(btnTangent.isSelected());
			tangentLine.update();
			oscCircle.setEuclidianVisible(btnOscCircle.isSelected());
			oscCircle.update();
			xSegment.setEuclidianVisible(btnXYSegments.isSelected());
			xSegment.update();
			ySegment.setEuclidianVisible(btnXYSegments.isSelected());
			ySegment.update();
			lblStep.setVisible(btnTable.isSelected());
			fldStep.setVisible(btnTable.isSelected());
			pts.setEuclidianVisible(btnTable.isSelected());
			pts.updateRepaint();

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

		} 

	}

	private void handleTabChange(){

		boolean isInterval = tabPanel.getSelectedComponent()==intervalTabPanel;

		updateIntervalFields();

		for(GeoElement geo: intervalTabGeoList){
			geo.setEuclidianVisible(isInterval);
			geo.update();
		}	
		for(GeoElement geo: pointTabGeoList){
			geo.setEuclidianVisible(!isInterval);
			geo.update();
		}	

		activeEV.repaint();
		updateGUI();

	}



	private void updateIntervalFields(){

		if(tabPanel.getSelectedComponent()==intervalTabPanel){

			double[] coords = new double[3];
			lowPoint.getCoords(coords);
			fldLow.setText(nf.format(coords[0]));
			highPoint.getCoords(coords);
			fldHigh.setText(nf.format(coords[0]));

			updateIntervalTable();
		}
	}




	/**
	 * Updates the interval table. The max, min, roots, area etc. for
	 * the current interval are calculated and put into the IntervalTable model.
	 */
	private void updateIntervalTable(){

		isChangingValue = true;

		ArrayList<String> property = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();


		// prepare algos and other objects needed for the calcs
		//=======================================================

		double[] coords = new double[3];
		lowPoint.getCoords(coords);
		xMin = coords[0];
		highPoint.getCoords(coords);
		xMax = coords[0];


		ExtremumFinder ef = new ExtremumFinder();
		RealRootFunction fun = selectedGeo.getRealRootFunctionY();    

		// get the table
		double integral = ((GeoNumeric) integralGeo).getDouble();
		double mean = integral/(xMax - xMin);
		double length = ((GeoNumeric) lengthGeo).getDouble();

		double yMin = selectedGeo.evaluate(xMin);
		double yMax = selectedGeo.evaluate(xMax);
		double xMinInt = ef.findMinimum(xMin,xMax,fun,5.0E-8);
		double xMaxInt = ef.findMaximum(xMin,xMax,fun,5.0E-8);
		double yMinInt = selectedGeo.evaluate(xMinInt);
		double yMaxInt = selectedGeo.evaluate(xMaxInt);

		if(yMin < yMinInt){
			yMinInt = yMin;
			xMinInt = xMin;
		}

		if(yMax > yMaxInt){
			yMaxInt = yMax;
			xMaxInt = xMax;
		}

		minPoint.setCoords(xMinInt, yMinInt, 1.0);
		//minPoint.setEuclidianVisible(!(minPoint.isEqual(lowPoint) || minPoint.isEqual(highPoint)));
		minPoint.update();
		maxPoint.setCoords(xMaxInt, yMaxInt, 1.0);
		//maxPoint.setEuclidianVisible(!(maxPoint.isEqual(lowPoint) || maxPoint.isEqual(highPoint)));
		maxPoint.update();




		// set the property/value pairs 
		//=================================================

		property.add(app.getCommand("Min"));
		value.add("(" + nf.format(xMinInt) + " , " + nf.format(yMinInt) + ")" );

		property.add(app.getCommand("Max"));
		value.add("(" + nf.format(xMaxInt) + " , " + nf.format(yMaxInt) + ")" );

		property.add(null);
		value.add(null );


		// calculate roots
		ExpressionNode low = new ExpressionNode(kernel, lowPoint, ExpressionNode.XCOORD, null);
		ExpressionNode high = new ExpressionNode(kernel, highPoint, ExpressionNode.XCOORD, null);				
		AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low, false);
		cons.removeFromConstructionList(xLow);
		AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high, false);
		cons.removeFromConstructionList(xHigh);

		AlgoRoots root = new AlgoRoots(cons, selectedGeo, (GeoNumeric)xLow.getGeoElements()[0], (GeoNumeric)xHigh.getGeoElements()[0]);
		cons.removeFromConstructionList(root);		
		rootGeos = root.getGeoElements();


		switch (rootGeos.length) {
		case 0: value.add(app.getPlain("fncInspector.NoRoots"));
		break;
		case 1: 
			if (rootGeos[0].isDefined())
				value.add(kernel.format(((GeoPoint)rootGeos[0]).inhomX));
			else
				value.add(app.getPlain("fncInspector.NoRoots"));
			break;
		default: value.add(app.getPlain("fncInspector.MultipleRoots"));
		}

		property.add(app.getCommand("Root"));
		property.add(null);
		value.add(null );


		property.add(app.getCommand("Area"));
		value.add(nf.format(integral));

		property.add(app.getCommand("Mean"));
		value.add(nf.format(mean));

		property.add(app.getCommand("Length"));
		value.add(nf.format(length));



		// load the model with these pairs
		//=================================================
		int rowCount = Math.max(minRows, property.size());
		modelInterval.setRowCount(property.size());

		for(int i=0; i < property.size(); i++){
			modelInterval.setValueAt(property.get(i),i,0);
			modelInterval.setValueAt(value.get(i),i,1);
		}


		//tableInterval.setColumnWidths();
		isChangingValue = false;

	}



	/**
	 * Updates the XYTable with the coordinates of the current sample points and
	 * any related values (e.g. derivative, difference)
	 */
	private void updateXYTable(){

		isChangingValue = true;

		//String lbl = selectedGeo.getLabel();
		GeoFunction f = (GeoFunction) selectedGeo;

		if(btnTable.isSelected()){
			double x = start - step*(pointCount-1)/2;
			double y;
			for(int i=0; i < modelXY.getRowCount(); i++){
				y = f.evaluate(x); 
				modelXY.setValueAt(nf.format(x),i,0);
				modelXY.setValueAt(nf.format(y),i,1);
				((GeoPoint) pts.get(i)).setCoords(x, y, 1);
				x = x + step;
			}

			pts.updateRepaint();	
		}
		else{
			double x = start;
			double y = f.evaluate(x); 
			modelXY.setValueAt(nf.format(x),0,0);
			modelXY.setValueAt(nf.format(y),0,1);
		}

		// update any extra columns added by the user (these will show derivatives, differences etc.) 
		updateExtraColumns();


		isChangingValue = false;
	}

	/**
	 * Updates any extra columns added by the user to the XYTable.
	 */
	private void updateExtraColumns(){

		if(extraColumnList.size()==0) return;

		for(int column = 2; column < extraColumnList.size() + 2; column ++ ){

			int columnType = extraColumnList.get(column-2);
			switch (columnType){

			case COL_DERIVATIVE:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double d = derivative.evaluate(x);// evaluateExpression(derivative.getLabel() + "(" + x + ")");
					modelXY.setValueAt(nf.format(d),row,column);
				}	
				break;

			case COL_DERIVATIVE2:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double d2 = derivative2.evaluate(x);//evaluateExpression(derivative2.getLabel() + "(" + x + ")");
					modelXY.setValueAt(nf.format(d2),row,column);
				}	
				break;

			case COL_CURVATURE:

				for(int row=0; row < modelXY.getRowCount(); row++){
					double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
					double y = Double.parseDouble((String) modelXY.getValueAt(row, 1));

					MyVecNode vec = new MyVecNode( kernel, new MyDouble(kernel, x), new MyDouble(kernel, y));

					ExpressionNode point = new ExpressionNode(kernel, vec, ExpressionNode.NO_OPERATION, null);
					point.setForcePoint();

					AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons, point, false);
					cons.removeFromConstructionList(pointAlgo);

					AlgoCurvature curvature = new AlgoCurvature(cons, (GeoPoint) pointAlgo.getGeoElements()[0], selectedGeo);
					cons.removeFromConstructionList(curvature);

					double c = ((GeoNumeric)curvature.getGeoElements()[0]).getDouble();

					//double c = evaluateExpression(
					//		"Curvature[ (" + x + "," + y  + ")," + selectedGeo.getLabel() + "]");
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



	private void addColumn(int columnType){
		extraColumnList.add(columnType);
		modelXY.addColumn(columnNames[columnType]);
		tableXY.setMyCellEditor(0);
		updateXYTable();
	}

	private void removeColumn(){
		int count = tableXY.getColumnCount();
		if(count <= 2) return;

		extraColumnList.remove(extraColumnList.size()-1);
		modelXY.setColumnCount(modelXY.getColumnCount()-1);
		tableXY.setMyCellEditor(0);
		updateXYTable();

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
		}	

		else if (source == btnRemoveColumn) {
			removeColumn();
		}	

		else if (source == btnOscCircle 
				|| source == btnTangent 
				|| source == btnTable
				|| source == btnXYSegments) {
			updateGUI();
		}


	}	

	private void doTextFieldActionPerformed(JTextField source) {
		try {

			Double value = Double.parseDouble( source.getText().trim());
			if (value == null) return;


			if (source == fldStep){ 
				step = value;	
				updateXYTable();		
			}	
			else if (source == fldLow){ 
				isChangingValue = true;
				double y = selectedGeo.evaluate(value);
				lowPoint.setCoords(value, y, 1);
				lowPoint.updateCascade();
				lowPoint.updateRepaint();
				isChangingValue = false;
				updateIntervalTable();	
			}	
			else if (source == fldHigh){ 
				isChangingValue = true;
				double y = selectedGeo.evaluate(value);
				highPoint.setCoords(value, y, 1);
				highPoint.updateCascade();
				highPoint.updateRepaint();
				isChangingValue = false;
				updateIntervalTable();	
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





	// ====================================================
	//          View Implementation
	// ====================================================

	public void update(GeoElement geo) {

		if(selectedGeo == null 
				|| testPoint == null 
				|| lowPoint == null 
				|| highPoint == null 
				|| isChangingValue 
				|| isIniting ) 
			return;


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


			if(lowPoint.x > highPoint.x){
				if(lowPoint.equals(geo))
					doTextFieldActionPerformed(fldLow);
				else
					doTextFieldActionPerformed(fldHigh);

			}


			updateIntervalFields();
			return;
		}

	}

	public void add(GeoElement geo) {}
	public void remove(GeoElement geo) {}
	public void rename(GeoElement geo) {}
	public void updateAuxiliaryObject(GeoElement geo) {}
	public void repaintView() {}
	public void reset() {
		setVisible(false);
	}
	public void clearView() {}
	public void setMode(int mode) {}




	// ====================================================
	//         Table Selection Listener
	// ====================================================

	public void valueChanged(ListSelectionEvent e) {

		if (e.getValueIsAdjusting() || isChangingValue) return;

		tableXY.getSelectionModel().removeListSelectionListener(this);
		if (e.getSource() == tableXY.getSelectionModel()) {
			// row selection changed
			updateTestPoint();
		}		
		tableXY.getSelectionModel().addListSelectionListener(this);
	}




	// ====================================================
	//    Geo Selection Listener
	// ====================================================

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		// TODO: not working directly yet, currently the listener
		// is in InputDialog, so an overridden insertGeoElement() is used instead
	}
	public void insertGeoElement(GeoElement geo) {
		if(geo == null 
				|| geo.getGeoClassType() != GeoElement.GEO_CLASS_FUNCTION)
			return;

		activeEV = (EuclidianView) app.getActiveEuclidianView();	
		selectedGeo = (GeoFunction)geo;

		lblGeoName.setText(getTitleString());

		initialX = 0.5* (activeEV.getXmin()- activeEV.getXmin());
		start = initialX;

		// initial step = EV grid step 
		step = 0.25 * kernel.getApplication().getEuclidianView().getGridDistances()[0];
		fldStep.removeActionListener(this);
		fldStep.setText("" + step);
		fldStep.addActionListener(this);

		defineDisplayGeos();

		double x = initialX - 4*step; 
		double y = ((GeoFunction)selectedGeo).evaluate(x); 
		lowPoint.setCoords(x, y, 1);

		x = initialX + 4*step; 
		y = ((GeoFunction)selectedGeo).evaluate(x); 
		highPoint.setCoords(x, y, 1);

		lowPoint.updateCascade();
		highPoint.updateCascade();

		updateGUI();
	}





	// ====================================================
	//      Key Listeners
	// ====================================================

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




	// ====================================================
	//  Update/Create Display Geos
	// ====================================================

	private void defineDisplayGeos(){

		// remove all geos
		clearGeoList();

		GeoFunction f = (GeoFunction)selectedGeo;

		// create XY table geos
		//========================================
		// test point
		AlgoPointOnPath pAlgo = new AlgoPointOnPath(cons, (Path)f, (activeEV.getXmin() + activeEV.getXmax()) / 2, 0);
		cons.removeFromConstructionList(pAlgo);
		testPoint = (GeoPoint) pAlgo.getGeoElements()[0];
		testPoint.setObjColor(DISPLAY_GEO_COLOR);
		testPoint.setPointSize(4);
		pointTabGeoList.add(testPoint);


		// X segment
		ExpressionNode xcoord = new ExpressionNode(kernel, testPoint, ExpressionNode.XCOORD, null);
		MyVecNode vec = new MyVecNode( kernel, xcoord, new MyDouble(kernel, 0.0));
		ExpressionNode point = new ExpressionNode(kernel, vec, ExpressionNode.NO_OPERATION, null);
		point.setForcePoint();
		AlgoDependentPoint pointAlgo = new AlgoDependentPoint(cons, point, false);
		cons.removeFromConstructionList(pointAlgo);

		AlgoJoinPointsSegment seg1 = new AlgoJoinPointsSegment(cons, testPoint, (GeoPoint)pointAlgo.getGeoElements()[0], null);
		cons.removeFromConstructionList(seg1);	
		xSegment = (GeoSegment)seg1.getGeoElements()[0];
		xSegment.setObjColor(DISPLAY_GEO_COLOR);
		xSegment.setLineThickness(3);
		xSegment.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		xSegment.setEuclidianVisible(true);
		xSegment.setFixed(true);
		pointTabGeoList.add(xSegment);


		// Y segment
		ExpressionNode ycoord = new ExpressionNode(kernel, testPoint, ExpressionNode.YCOORD, null);
		MyVecNode vecy = new MyVecNode( kernel, new MyDouble(kernel, 0.0), ycoord);
		ExpressionNode pointy = new ExpressionNode(kernel, vecy, ExpressionNode.NO_OPERATION, null);
		pointy.setForcePoint();
		AlgoDependentPoint pointAlgoy = new AlgoDependentPoint(cons, pointy, false);
		cons.removeFromConstructionList(pointAlgoy);	

		AlgoJoinPointsSegment seg2 = new AlgoJoinPointsSegment(cons, testPoint, (GeoPoint)pointAlgoy.getGeoElements()[0], null);
		cons.removeFromConstructionList(seg2);

		ySegment = (GeoSegment)seg2.getGeoElements()[0];
		ySegment.setObjColor(DISPLAY_GEO_COLOR);
		ySegment.setLineThickness(3);
		ySegment.setLineType(EuclidianView.LINE_TYPE_DASHED_SHORT);
		ySegment.setEuclidianVisible(true);
		ySegment.setFixed(true);
		pointTabGeoList.add(ySegment);


		// tangent line		
		AlgoTangentFunctionPoint tangent = new AlgoTangentFunctionPoint(cons, testPoint, f);
		cons.removeFromConstructionList(tangent);
		tangentLine = tangent.getGeoElements()[0];
		tangentLine.setObjColor(DISPLAY_GEO_COLOR);
		tangentLine.setEuclidianVisible(false);
		pointTabGeoList.add(tangentLine);


		// osculating circle
		AlgoOsculatingCircle oc = new AlgoOsculatingCircle(cons, testPoint, f);
		cons.removeFromConstructionList(oc);
		oscCircle = oc.getGeoElements()[0];
		oscCircle.setObjColor(DISPLAY_GEO_COLOR);
		oscCircle.setEuclidianVisible(false);
		pointTabGeoList.add(oscCircle);


		// derivative
		AlgoCasDerivative deriv = new AlgoCasDerivative(cons, f);
		cons.removeFromConstructionList(deriv);
		derivative = (GeoFunction)deriv.getGeoElements()[0];
		derivative.setEuclidianVisible(false);
		hiddenGeoList.add(derivative);

		// 2nd derivative
		AlgoCasDerivative deriv2 = new AlgoCasDerivative(cons, f, null, new MyDouble(kernel, 2.0));
		cons.removeFromConstructionList(deriv2);
		derivative2 = (GeoFunction)deriv2.getGeoElements()[0];
		derivative2.setEuclidianVisible(false);
		hiddenGeoList.add(derivative2);


		// point list
		pts = new GeoList(cons);
		pts.setEuclidianVisible(true);
		pts.setObjColor(new Color(125,125,255));
		for(int i = 0; i < pointCount; i++){
			pts.add(new GeoPoint(cons));
		}
		pointTabGeoList.add(pts);



		// create interval table geos
		//================================================

		// interval points
		AlgoPointOnPath pxAlgo = new AlgoPointOnPath(cons, (Path)f, (2 * activeEV.getXmin() + activeEV.getXmax()) / 3, 0);
		cons.removeFromConstructionList(pxAlgo);
		lowPoint = (GeoPoint) pxAlgo.getGeoElements()[0];
		lowPoint.setEuclidianVisible(false);
		lowPoint.setPointSize(4);
		lowPoint.setObjColor(DISPLAY_GEO_COLOR);
		lowPoint.setLayer(f.getLayer()+1);
		intervalTabGeoList.add(lowPoint);


		AlgoPointOnPath pyAlgo = new AlgoPointOnPath(cons, (Path)f, (activeEV.getXmin() + 2 * activeEV.getXmax()) / 3, 0);
		cons.removeFromConstructionList(pyAlgo);
		highPoint = (GeoPoint) pyAlgo.getGeoElements()[0];
		highPoint.setEuclidianVisible(false);
		highPoint.setPointSize(4);
		highPoint.setObjColor(DISPLAY_GEO_COLOR);
		highPoint.setLayer(f.getLayer()+1);
		intervalTabGeoList.add(highPoint);


		ExpressionNode low = new ExpressionNode(kernel, lowPoint, ExpressionNode.XCOORD, null);
		ExpressionNode high = new ExpressionNode(kernel, highPoint, ExpressionNode.XCOORD, null);				
		AlgoDependentNumber xLow = new AlgoDependentNumber(cons, low, false);
		cons.removeFromConstructionList(xLow);
		AlgoDependentNumber xHigh = new AlgoDependentNumber(cons, high, false);
		cons.removeFromConstructionList(xHigh);


		AlgoFunctionInterval interval = new AlgoFunctionInterval(cons, f, (NumberValue)xLow.getGeoElements()[0], (NumberValue)xHigh.getGeoElements()[0]);
		cons.removeFromConstructionList(interval);	

		functionInterval = interval.getGeoElements()[0];
		functionInterval.setEuclidianVisible(false);
		functionInterval.setLineThickness(selectedGeo.getLineThickness()+3);
		functionInterval.setObjColor(DISPLAY_GEO_COLOR);
		functionInterval.setLayer(f.getLayer()+1);
		intervalTabGeoList.add(functionInterval);


		AlgoIntegralDefinite inte = new AlgoIntegralDefinite(cons, selectedGeo, (NumberValue)xLow.getGeoElements()[0], (NumberValue)xHigh.getGeoElements()[0], null);
		cons.removeFromConstructionList(inte);
		integralGeo = inte.getGeoElements()[0];
		integralGeo.setEuclidianVisible(false);
		integralGeo.setObjColor(DISPLAY_GEO_COLOR);
		intervalTabGeoList.add(integralGeo);

		AlgoLengthFunction len = new AlgoLengthFunction(cons, selectedGeo, (GeoNumeric)xLow.getGeoElements()[0], (GeoNumeric)xHigh.getGeoElements()[0]);
		cons.removeFromConstructionList(len);
		lengthGeo = len.getGeoElements()[0];
		hiddenGeoList.add(lengthGeo);

		minPoint = new GeoPoint(cons);
		minPoint.setEuclidianVisible(false);
		minPoint.setPointSize(4);
		minPoint.setPointStyle(EuclidianView.POINT_STYLE_FILLED_DIAMOND);
		minPoint.setObjColor(DISPLAY_GEO_COLOR.darker());
		minPoint.setLayer(f.getLayer()+1);
		minPoint.setFixed(true);
		intervalTabGeoList.add(minPoint);

		maxPoint = new GeoPoint(cons);
		maxPoint.setEuclidianVisible(false);
		maxPoint.setPointSize(4);
		maxPoint.setPointStyle(EuclidianView.POINT_STYLE_FILLED_DIAMOND);
		maxPoint.setObjColor(DISPLAY_GEO_COLOR.darker());
		maxPoint.setLayer(f.getLayer()+1);
		maxPoint.setFixed(true);
		intervalTabGeoList.add(maxPoint);




		// process the geos
		// ==================================================

		// add the display geos to the active EV and hide the tooltips 
		for(GeoElement geo:intervalTabGeoList){
			activeEV.add(geo);
			geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
			geo.update();

		}	
		for(GeoElement geo:pointTabGeoList){
			activeEV.add(geo);
			geo.setTooltipMode(GeoElement.TOOLTIP_OFF);
			geo.update();
		}	

		updateTestPoint();
		activeEV.repaint();


	}



	private void updateTestPoint(){

		if(testPoint == null || isIniting ) return;

		isChangingValue = true;
		int row = tableXY.getSelectedRow();
		if (row >= 0){
			double x = Double.parseDouble((String) modelXY.getValueAt(row, 0));
			double y = ((GeoFunction)selectedGeo).evaluate(x); //evaluateExpression(selectedGeo.getLabel() + "(" + x + ")");
			//double y = Double.parseDouble((String) modelXY.getValueAt(row, 1));
			testPoint.setCoords(x, y, 1);
			testPoint.updateRepaint();	
		}
		isChangingValue = false;

	}

	private void clearGeoList(){
		for(GeoElement geo : intervalTabGeoList){
			if(geo != null){
				geo.remove();
			}
		}
		intervalTabGeoList.clear();

		for(GeoElement geo : pointTabGeoList){
			if(geo != null){
				geo.remove();
			}
		}
		pointTabGeoList.clear();

		for(GeoElement geo : hiddenGeoList){
			if(geo != null){
				geo.remove();
			}
		}
		hiddenGeoList.clear();

		rootGeos = null;
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
			updateXYTable();
			updateTestPoint();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		tableXY.getSelectionModel().addListSelectionListener(this);
	}



	private void updateIntervalGeoVisiblity(){

	//	minPoint.setEuclidianVisible(tableInterval.isRowSelected(0));
		minPoint.setEuclidianVisible(false);
		minPoint.update();
	//	maxPoint.setEuclidianVisible(tableInterval.isRowSelected(1));
		maxPoint.setEuclidianVisible(false);
		maxPoint.update();
		
		
		
		
	//	integralGeo.setEuclidianVisible(tableInterval.isRowSelected(5));
		integralGeo.setEuclidianVisible(true);
		integralGeo.update();

		activeEV.repaint();
	}





}






