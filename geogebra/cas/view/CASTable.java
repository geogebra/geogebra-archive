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
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		// Set the width of the index column;
		this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
		this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);
		
		//this.sizeColumnsToFit(0);
		this.setSurrendersFocusOnKeystroke(true);
	}

	/*
	 * Function: Insert a row after the "selectedRow row", and set the focus at
	 * the new row
	 */
	public void insertRow(int selectedRow, int selectedCol) {

		CASTableCellValue newValue = new CASTableCellValue();
		tableModel.insertRow(selectedRow + 1, new Object[] { newValue, "New" });
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
		tableModel.insertRow(selectedRow + 1, new Object[] { newValue, "New" });
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
		// TODO: Test this part
		// CASTableCellEditor dce = (CASTableCellEditor )this.getCellEditor();
		// if (dce != null) {
		// Application.debug("dce stop editting");
		// dce.stopCellEditing();
		// }

		// tableModel.removeRow(row);
		// int delRow = getSelectedRow();
		// if (delRow > -1) {
		// CASTableCellEditor dce = (CASTableCellEditor)getCellEditor();
		// if (dce != null) dce.stopCellEditing();
		// }
		tableModel.removeRow(row);
		this.repaint(); // Update the table
		if (tableModel.getRowCount() == 0)
			insertRow(-1, CASPara.contCol);
		else
			setFocusAtRow(row, CASPara.contCol);
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
