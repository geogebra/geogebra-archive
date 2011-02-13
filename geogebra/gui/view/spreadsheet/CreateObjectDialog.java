package geogebra.gui.view.spreadsheet;

import geogebra.gui.InputDialog;
import geogebra.gui.TextPreviewPanel;
import geogebra.gui.util.SpringUtilities;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * Dialog to create GeoElements (lists, matrices, tabletext, etc.) from
 * spreadsheet cell selections
 * 
 * @author G. Sturr
 * 
 */
public class CreateObjectDialog extends InputDialog implements ListSelectionListener{

	private SpreadsheetView view;
	private CellRangeProcessor cp;
	private ArrayList<CellRange> selectedCellRanges;
	private int selectionType;
	private MyTable table;

	public static final int TYPE_LIST = 0;
	public static final int TYPE_LISTOFPOINTS = 1;
	public static final int TYPE_MATRIX = 2;
	public static final int TYPE_TABLETEXT = 3;
	public static final int TYPE_POLYLINE = 4;
	private int objectType = TYPE_LIST;

	private JList typeList;
	private DefaultListModel model;
	private JLabel lblType, lblName, lblTake, lblOrder, lblXYOrder;

	private JCheckBox ckValue, ckObject, ckSort, ckTranspose;
	private JRadioButton rbOrderNone, rbOrderRow,rbOrderCol, rbOrderSortAZ, rbOrderSortZA;
	private JComboBox cbOrder, cbTake;

	private boolean isIniting = true;
	private JPanel optionsPanel;
	private JPanel typePanel;

	private MyTextField fldName;

	private GeoElement newGeo;

	private TextPreviewPanel previewPanel;
	private JTextField fldType;

	private String title;



	public CreateObjectDialog(Application app, SpreadsheetView view, int objectType) {

		super(app.getFrame(), true);
		this.app = app;	
		this.view = view;
		this.objectType = objectType;
		this.table = view.getTable();
		cp = table.getCellRangeProcessor();
		selectionType = table.getSelectionType();  
		selectedCellRanges = table.selectedCellRanges;	

		boolean showApply = false;

		createGUI(title, "", false, 16, 1, false, false, false, false, false, showApply, false);

		//	this.btCancel.setVisible(false);

		createAdditionalGUI();

		isIniting = false;
		updateGUI();
		setLabels(null);
		setTitle((String) model.getElementAt(objectType));

		//optionPane.add(inputPanel, BorderLayout.CENTER);	
		typeList.setSelectedIndex(objectType);
		//setResizable(true);
		centerOnScreen();


	}

	private void createAdditionalGUI(){

		model = new DefaultListModel();
		typeList = new JList(model);
		typeList.addListSelectionListener(this);

		lblName = new JLabel();
		fldName = new MyTextField(app.getGuiManager());
		fldName.setColumns(14);
		fldName.setShowSymbolTableIcon(true);
		fldName.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					createNewGeo();
					//updateGUI();
				}
			}
		});


		lblTake = new JLabel();
		cbOrder = new JComboBox();
		lblOrder = new JLabel();
		cbTake = new JComboBox();


		ckObject = new JCheckBox();
		ckValue = new JCheckBox();
		ckObject.setSelected(true);

		lblXYOrder = new JLabel();
		
		ckSort = new JCheckBox();	
		ckSort.setSelected(false);

		ckTranspose = new JCheckBox();
		ckTranspose.setSelected(false);

		typePanel = new JPanel(new BorderLayout());
		//typePanel.add(typeList, BorderLayout.WEST);	
		typeList.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		typePanel.setBorder(BorderFactory.createEtchedBorder());

		//optionPane.add(typePanel, BorderLayout.WEST);
		
		JPanel op = new JPanel(new BorderLayout());
		op.add(buildOptionsPanel(), BorderLayout.NORTH);
		optionPane.add(op, BorderLayout.CENTER);
		
		
		
		
	}


	private JPanel buildOptionsPanel() {
		JPanel p = new JPanel();
		int rows = 1;
		p.removeAll();

		p.add(lblName);		
		p.add(fldName);

		// showCopyType
		rows++;
		p.add(lblTake);
		JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		copyPanel.add(ckObject);
		copyPanel.add(ckValue);
		//copyPanel.add(cbTake);
		p.add(copyPanel);

		if(objectType == TYPE_LIST){
			rows++;
			p.add(lblOrder);
			JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			orderPanel.add(cbOrder);
			orderPanel.add(ckSort);
			orderPanel.add(ckTranspose);
			p.add(orderPanel);
		}

		//Lay out the panel
		p.setLayout(new SpringLayout());
		SpringUtilities.makeCompactGrid(p,
				rows, 2, 	// rows, cols
				5, 5,   //initX, initY
				2, 5);  //xPad, yPad	

		p.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
		return p;
	}



	public void setLabels(String title) {

		setTitle(title);

		if (isIniting) return;

		btOK.setText(app.getPlain("Cancel"));
		btApply.setText(app.getPlain("Apply"));
		btCancel.setText(app.getPlain("Create"));

		cbTake.removeAllItems();
		cbTake.addItem(app.getMenu("Objects"));
		cbTake.addItem(app.getMenu("Values"));
		ckObject.setText(app.getMenu("Objects"));
		ckObject.addActionListener(this);
		ckValue.setText(app.getMenu("Values"));
		ckValue.addActionListener(this);
		ckTranspose.setText(app.getMenu("Transpose"));
		ckSort.setText(app.getMenu("Sort"));
		ckSort.addActionListener(this);

		lblName.setText(app.getMenu("Name") + ": ");
		lblTake.setText(app.getMenu("Take") + ": ");
		lblOrder.setText(app.getMenu("Order") + ":");
		lblXYOrder.setText(app.getMenu("Order") + ": ");


		cbOrder.removeAllItems();
		if(objectType == TYPE_LIST){
			cbOrder.addItem(app.getMenu("Row"));
			cbOrder.addItem(app.getMenu("Column"));
		}else if(objectType == TYPE_LISTOFPOINTS){
			cbOrder.addItem(app.getMenu("X->Y"));
			cbOrder.addItem(app.getMenu("Y->X"));
		}

		model.clear();
		model.addElement(app.getMenu("List"));
		model.addElement(app.getMenu("ListOfPoints"));
		model.addElement(app.getMenu("Matrix"));
		model.addElement(app.getMenu("Table"));


		//	optionsPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Options")));
		typePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Object")));

	}



	private void updateGUI(){

		if (newGeo == null) 
			fldName.setText("");
		else 
			fldName.setText(newGeo.getLabel());

		cbOrder.removeAllItems();
		if(objectType == TYPE_LIST){
			cbOrder.addItem(app.getMenu("Row"));
			cbOrder.addItem(app.getMenu("Column"));
		}else{
			cbOrder.addItem(app.getMenu("X to Y"));
			cbOrder.addItem(app.getMenu("Y to X"));
		}	

		if(objectType == TYPE_MATRIX){
			cbOrder.setVisible(false);
			ckTranspose.setVisible(true);
		}else{
			cbOrder.setVisible(true);
			ckTranspose.setVisible(false);
		}
		
		ckSort.setVisible(objectType == TYPE_POLYLINE);

	}




	/**
	 * Handles button clicks for dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			ckValue.removeActionListener(this);
			ckObject.removeActionListener(this);

			if (source == btOK ) {
				setVisible(!processInput());
			} else if (source == btApply) {
				processInput();
			} else if (source == btCancel) {
				setVisible(false);
			} 
			else if (source == ckObject) {
				ckValue.setSelected(!ckObject.isSelected());
				createNewGeo();
			} 
			else if (source == ckValue) {
				ckObject.setSelected(!ckValue.isSelected());
				createNewGeo();
			} 

			ckValue.addActionListener(this);
			ckObject.addActionListener(this);

		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			setVisible(false);
		}
	}



	private boolean processInput() {
		boolean succ = true;

		int column1 = table.selectedCellRanges.get(0).getMinColumn();
		int column2 = table.selectedCellRanges.get(0).getMaxColumn();
		int row1 = table.selectedCellRanges.get(0).getMinRow();
		int row2 = table.selectedCellRanges.get(0).getMaxRow();		

		switch (objectType){

		case TYPE_LIST:	
			cp.createList(selectedCellRanges, true, ckValue.isSelected());
			break;

		case TYPE_LISTOFPOINTS:	
			cp.createPointList(selectedCellRanges, true, ckValue.isSelected());
			break;

		case TYPE_MATRIX:	
			cp.createMatrix(column1, column2, row1, row2, ckValue.isSelected());
			break;

		case TYPE_TABLETEXT:	
			cp.createTableText(column1, column2, row1, row2, ckValue.isSelected());		
			break;

		}

		return succ;

	}


	public void createNewGeo(){

		if(newGeo != null)
			newGeo.remove();

		int column1 = table.selectedCellRanges.get(0).getMinColumn();
		int column2 = table.selectedCellRanges.get(0).getMaxColumn();
		int row1 = table.selectedCellRanges.get(0).getMinRow();
		int row2 = table.selectedCellRanges.get(0).getMaxRow();	

		boolean copyByValue = ckValue.isSelected();


		try {
			switch (objectType){

			case TYPE_LIST:	
				newGeo = cp.createList(selectedCellRanges, true, copyByValue);
				break;

			case TYPE_LISTOFPOINTS:	
				newGeo = cp.createPointList(selectedCellRanges, true, copyByValue);
				break;

			case TYPE_MATRIX:	
				newGeo = cp.createMatrix(column1, column2, row1, row2, copyByValue);
				break;

			case TYPE_TABLETEXT:	
				newGeo = cp.createTableText(column1, column2, row1, row2, copyByValue);		
				break;
			}

			updateGUI();

		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	public void valueChanged(ListSelectionEvent e) {

		if(e.getSource() == typeList){
			typeList.removeListSelectionListener(this);
			objectType = typeList.getSelectedIndex();
			// fldName.setText("");
			createNewGeo();
			typeList.addListSelectionListener(this);
		}
	}



}
