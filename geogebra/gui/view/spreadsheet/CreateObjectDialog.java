package geogebra.gui.view.spreadsheet;

import geogebra.gui.InputDialog;
import geogebra.gui.TextPreviewPanel;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
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
	public static final int TYPE_MATRIX = 1;
	public static final int TYPE_LISTOFPOINTS = 2;
	public static final int TYPE_TABLETEXT = 3;
	public static final int TYPE_POLYLINE = 4;
	private int objectType = TYPE_LIST;

	private JList typeList;
	private DefaultListModel model;
	private JLabel lblType, lblName, lblTake, lblOrder, lblXYOrder;

	private JCheckBox ckValue, ckObject, ckSort, ckTranspose;
	private JRadioButton rbOrderNone, rbOrderRow,rbOrderCol, rbOrderSortAZ, rbOrderSortZA;
	private JComboBox cbScanOrder, cbTake;

	private boolean isIniting = true;
	private JPanel optionsPanel;
	private JPanel typePanel;

	private MyTextField fldName;

	private GeoElement newGeo;

	private TextPreviewPanel previewPanel;
	private JTextField fldType;

	private String title;

	private boolean keepNewGeo = false;
	private JComboBox cbLeftRightOrder;
	private JPanel cards;



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

		updateGUI();

		isIniting = false;
		setLabels(null);
		setTitle((String) model.getElementAt(objectType));

		//optionPane.add(inputPanel, BorderLayout.CENTER);	
		typeList.setSelectedIndex(objectType);
		//setResizable(true);
		centerOnScreen();	
		btCancel.requestFocus();
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
		cbScanOrder = new JComboBox();
		cbScanOrder.addActionListener(this);
		lblOrder = new JLabel();
		cbTake = new JComboBox();

		cbLeftRightOrder = new JComboBox();
		cbLeftRightOrder.addActionListener(this);


		ckObject = new JCheckBox();
		ckValue = new JCheckBox();
		ckObject.setSelected(true);

		lblXYOrder = new JLabel();

		ckSort = new JCheckBox();	
		ckSort.setSelected(false);

		ckTranspose = new JCheckBox();
		ckTranspose.setSelected(false);
		ckTranspose.addActionListener(this);

		// show the object list only if an object type is not given
		if(objectType <0){
			objectType = TYPE_LIST;
			typePanel = new JPanel(new BorderLayout());
			typePanel.add(typeList, BorderLayout.WEST);	
			typeList.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
			typePanel.setBorder(BorderFactory.createEtchedBorder());		
			optionPane.add(typePanel, BorderLayout.WEST);
		}

		JPanel op = new JPanel(new BorderLayout());
		op.add(buildOptionsPanel(), BorderLayout.NORTH);
		optionPane.add(op, BorderLayout.CENTER);

	}


	private JPanel buildOptionsPanel() {

		JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		namePanel.add(lblName);		
		namePanel.add(fldName);	
		//p.add(lblTake);

		JPanel copyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		copyPanel.add(ckObject);
		copyPanel.add(ckValue);
		//copyPanel.add(cbTake);

		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(namePanel,BorderLayout.NORTH);
		northPanel.add(Box.createRigidArea(new Dimension(50,10)), BorderLayout.WEST);
		northPanel.add(copyPanel,BorderLayout.CENTER);

		JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		orderPanel.add(cbScanOrder);

		JPanel transposePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		transposePanel.add(ckTranspose);

		JPanel xySwitchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		xySwitchPanel.add(cbLeftRightOrder);

		JPanel pointListPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pointListPanel.add(Box.createRigidArea(lblName.getSize()));


		cards = new JPanel(new CardLayout());
		cards.add("c0", orderPanel);
		cards.add("c1", transposePanel);
		cards.add("c2", xySwitchPanel);
		cards.add("c3", pointListPanel);


		JPanel optionsPanel = new JPanel(new BorderLayout());
		optionsPanel.add(northPanel, BorderLayout.NORTH);
		optionsPanel.add(Box.createRigidArea(new Dimension(50,10)), BorderLayout.WEST);
		optionsPanel.add(cards, BorderLayout.CENTER);

		return optionsPanel;
	}



	public void setLabels(String title) {


		if (isIniting) return;

		// TODO: using buttons incorrectly for now
		// btnOK = cancel, cancel = create
		btOK.setText(app.getPlain("Cancel"));
		btApply.setText(app.getPlain("Apply"));
		btCancel.setText(app.getPlain("Create"));

		// object/value checkboxes
		ckObject.setText(app.getMenu("Objects"));
		ckObject.addActionListener(this);
		ckValue.setText(app.getMenu("Values"));
		ckValue.addActionListener(this);

		// transpose checkbox
		ckTranspose.setText(app.getMenu("Transpose"));
		ckSort.setText(app.getMenu("Sort"));
		ckSort.addActionListener(this);

		lblName.setText(app.getMenu("Name") + ": ");

		/*
		lblTake.setText(app.getMenu("Take") + ": ");
		lblOrder.setText(app.getMenu("Order") + ":");
		lblXYOrder.setText(app.getMenu("Order") + ": ");
		 */

		cbScanOrder.removeAllItems();
		cbScanOrder.addItem(app.getMenu("RowOrder"));
		cbScanOrder.addItem(app.getMenu("ColumnOrder"));

		cbLeftRightOrder.removeAllItems();
		cbLeftRightOrder.addItem(app.getMenu("X->Y"));
		cbLeftRightOrder.addItem(app.getMenu("Y<-X"));

		model.clear();
		model.addElement(app.getMenu("List"));
		model.addElement(app.getMenu("Matrix"));
		model.addElement(app.getMenu("ListOfPoints"));
		model.addElement(app.getMenu("Table"));
		model.addElement(app.getMenu("PolyLine"));

		//	optionsPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Options")));
		if(typePanel!=null)
			typePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Object")));

		setTitle((String) model.getElementAt(objectType));


	}



	private void updateGUI(){

		if (newGeo == null) 
			fldName.setText("");
		else 
			fldName.setText(newGeo.getLabel());

		CardLayout cl = (CardLayout)(cards.getLayout());
		cl.show(cards, "c" + typeList.getSelectedIndex());


		/*
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
		 */
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

			// btCancel acts as create for now
			if (source == btCancel ) {
				keepNewGeo = true;
				setVisible(false);

			} else if (source == btApply) {
				//processInput();


				// btOK acts as cancel for now
			} else if (source == btOK) {
				newGeo.remove();
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

			else if (source == cbScanOrder || source == cbLeftRightOrder || source == ckTranspose) {
				createNewGeo();
			} 

			ckValue.addActionListener(this);
			ckObject.addActionListener(this);

		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			setVisible(false);
		}
	}


	public void setVisible(boolean isVisible) {	
		if (!isModal()) {
			if (isVisible) { // set old mode again			
				addWindowFocusListener(this);			
			} else {		
				removeWindowFocusListener(this);
				app.setSelectionListenerMode(null);
			}
		}

		if(!isVisible){
			if(!keepNewGeo)
				newGeo.remove();
		}
		super.setVisible(isVisible);
	}

	/*
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

	 */



	public void createNewGeo(){

		boolean nullGeo = newGeo == null;

		if(!nullGeo)
			newGeo.remove();


		int column1 = table.selectedCellRanges.get(0).getMinColumn();
		int column2 = table.selectedCellRanges.get(0).getMaxColumn();
		int row1 = table.selectedCellRanges.get(0).getMinRow();
		int row2 = table.selectedCellRanges.get(0).getMaxRow();	

		boolean copyByValue = ckValue.isSelected();
		boolean scanByColumn = cbScanOrder.getSelectedIndex() == 1;
		boolean leftToRight = cbLeftRightOrder.getSelectedIndex() == 0;
		boolean transpose = ckTranspose.isSelected();

		try {
			switch (objectType){

			case TYPE_LIST:	
				newGeo = cp.createList(selectedCellRanges, scanByColumn, copyByValue);
				break;

			case TYPE_LISTOFPOINTS:	
				newGeo = cp.createPointList(selectedCellRanges, copyByValue, leftToRight);
				break;

			case TYPE_MATRIX:	
				newGeo = cp.createMatrix(column1, column2, row1, row2, copyByValue,transpose);
				break;

			case TYPE_TABLETEXT:	
				newGeo = cp.createTableText(column1, column2, row1, row2, copyByValue);		
				break;

			case TYPE_POLYLINE:	
				newGeo = cp.createPolyLine(selectedCellRanges, copyByValue, leftToRight);
				break;

			}

			if(!nullGeo)
				newGeo.setLabel(fldName.getText());
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
