package geogebra.gui.view.spreadsheet;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


/**
 * Dialog for selecting copy to spreadsheet options.
 * 
 * @author G. Sturr
 * 
 */
public class DialogCopyToSpreadsheet extends JDialog implements ActionListener {

	private SpreadsheetViewDnD dndHandler;
	private Application app;

	private JButton btnCancel, btnOK;
	private JRadioButton rbFree, rbDependent;
	private JRadioButton rbOrderRow, rbOrderCol;

	private JPanel optionsPanel;

	private String title;
	


	public DialogCopyToSpreadsheet(Application app, SpreadsheetViewDnD dndHandler) {

		super(app.getFrame(), app.getMenu("CopyToSpreadsheet"), true);  // modal dialog
		this.app = app;	
		this.dndHandler = dndHandler;

		createGUI();

		this.setResizable(false);
		pack();
		setLocationRelativeTo(app.getMainComponent());
		btnOK.requestFocus();

		dndHandler.setAllowDrop(false);
	}


	private void createGUI() {

		createGUIElements();

		JPanel copyTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		copyTypePanel.add(rbFree);
		copyTypePanel.add(rbDependent);

		JPanel orderTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		orderTypePanel.add(rbOrderRow);
		orderTypePanel.add(rbOrderCol);

		JPanel cancelOKPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		cancelOKPanel.add(btnCancel);
		cancelOKPanel.add(btnOK);

		Box vBox = Box.createVerticalBox();
		vBox.add(copyTypePanel);
		vBox.add(orderTypePanel);
		vBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2), 
				BorderFactory.createTitledBorder(app.getMenu("Options"))));

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(vBox, BorderLayout.CENTER);
		getContentPane().add(cancelOKPanel, BorderLayout.SOUTH);

		setLabels();
	}



	private void createGUIElements(){

		btnOK = new JButton();
		btnOK.addActionListener(this);
		btnCancel = new JButton();
		btnCancel.addActionListener(this);

		rbDependent = new JRadioButton();
		rbFree = new JRadioButton();
		rbFree.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbDependent);
		bg.add(rbFree);

		rbOrderRow = new JRadioButton();
		rbOrderCol = new JRadioButton();
		rbOrderCol.setSelected(true);
		ButtonGroup bg2 = new ButtonGroup();
		bg2.add(rbOrderRow);
		bg2.add(rbOrderCol);


	}


	public void setLabels() {

		btnOK.setText(app.getPlain("OK"));
		btnCancel.setText(app.getMenu("Cancel"));

		rbDependent.setText(app.getPlain("DependentObjects"));
		rbFree.setText(app.getPlain("FreeObjects"));

		rbOrderRow.setText(app.getMenu("RowOrder"));
		rbOrderCol.setText(app.getMenu("ColumnOrder"));
	}




	/**
	 * Handles button clicks for dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == btnCancel) 
			setVisible(false);

		else if (source == btnOK) {
			dndHandler.setCopyByValue(rbFree.isSelected());
			dndHandler.setRowOrdered(rbOrderRow.isSelected());
			dndHandler.setAllowDrop(true);
			setVisible(false);
		} 
	}


	public void setVisible(boolean isVisible) {	
		if(!isVisible){

		}
		super.setVisible(isVisible);
	}















}
