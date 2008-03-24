/**
 * 
 */
package geogebra.cas.view;

import geogebra.Application;

import java.awt.Color;
import java.awt.Component;

import javax.swing.*;

/**
 * @author Quan
 * 
 */
public class CASTable extends JTable {

	private CASTableModel tableModel;

	public CASTable() {
		super();
	}

	/*
	 * Default Cinfiguration for Table
	 */
	public void initializeTable(int rows, CASSession session, Application app) {

		this.setShowGrid(false);
		// Dynamically change the height of the table
		this.setRowHeight(CASPara.originalHeight);
		this.setBackground(Color.white);

		tableModel = new CASTableModel(this, rows, session, app);
		this.setModel(tableModel);

		// Set the width of the index column;
		this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
		this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);
		this.sizeColumnsToFit(0);
		this.setSurrendersFocusOnKeystroke(true);
	}

	/*
	 * Function: Insert a row after the "selectedRow row", and set the focus at
	 * the new row
	 */
	public void insertRow(int selectedRow, int selectedCol) {

		CASTableCellValue newValue = new CASTableCellValue();
		tableModel.insertRow(selectedRow + 1, new Object[] { "New", newValue });
		changeSelection(selectedRow + 1, selectedCol, false, false);
		editCellAt(selectedRow + 1, selectedCol);
		((Component) ((CASTableCellEditor) getCellEditor(selectedRow + 1,
				selectedCol)).getTableCellEditorComponent(this, newValue, true,
				selectedRow + 1, selectedCol)).requestFocus();
	}
	
	public void insertRow(int selectedRow, int selectedCol, char c) {
		char[] in = new char[1];
		in[0] = c;
		CASTableCellValue newValue = new CASTableCellValue(new String(in));
		tableModel.insertRow(selectedRow + 1, new Object[] { "New", newValue });
		changeSelection(selectedRow + 1, selectedCol, false, false);
		editCellAt(selectedRow + 1, selectedCol);
		((Component) ((CASTableCellEditor) getCellEditor(selectedRow + 1,
				selectedCol)).getTableCellEditorComponent(this, newValue, true,
				selectedRow + 1, selectedCol)).requestFocus();
		
		
	}

	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteRow(int row) {
		tableModel.removeRow(row);
		if(tableModel.getRowCount()==0)
			insertRow(-1, CASPara.contCol);
	}

	/*
	 * Function: Set the focus on the specified row
	 */
	public void setFocusAtRow(int editRow, int editCol) {

		CASTableCellValue value = (CASTableCellValue) tableModel.getValueAt(
				editRow, editCol);

		changeSelection(editRow, editCol, false, false);
		editCellAt(editRow, editCol);
		((Component) ((CASTableCellEditor) getCellEditor(editRow, editCol))
				.getTableCellEditorComponent(this, value, true, editRow,
						editCol)).requestFocus();
	}

	/*
	 * Function: Set the focus on the linepanle of a specified row
	 */
	public void setFocusAtRowLinePanel(int editRow, int editCol) {
		CASTableCellValue value = (CASTableCellValue) tableModel.getValueAt(
				editRow, editCol);

		changeSelection(editRow, editCol, false, false);
		editCellAt(editRow, editCol);
		CASTableCell temp = ((CASTableCell) ((CASTableCellEditor) getCellEditor(
				editRow, editCol)).getTableCellEditorComponent(this, value,
				true, editRow, editCol));
		setRowHeight(editRow, temp.addLinePanel());
		temp.setLineBorderFocus();
	}
}
