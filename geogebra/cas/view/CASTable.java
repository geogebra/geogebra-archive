/**
 * 
 */
package geogebra.cas.view;

import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;

import javax.swing.*;

/**
 * @author Quan
 * 
 */
public class CASTable extends JTable {

	private CASTableModel tableModel;
	protected Kernel kernel;
	protected Application app;

	public static final Color SELECTED_BACKGROUND_COLOR_HEADER = new Color(185,
			185, 210);

	public CASTable(Application app) {
		super();

		this.app = app;
		this.kernel = this.app.getKernel();

	}

	/*
	 * Default Cinfiguration for Table
	 */
	public void initializeTable(int rows, CASSession session, Application app) {

		this.setShowGrid(false);
		// Dynamically change the height of the table
		this.setRowHeight(CASPara.inputLineHeight);
		this.setBackground(Color.white);

		tableModel = new CASTableModel(this, rows, session, app);
		this.setModel(tableModel);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		// Set the width of the index column;
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMinWidth(30);
		// this.getColumn(this.getColumnName(CASPara.indexCol)).setMaxWidth(30);

		// this.sizeColumnsToFit(0);
		this.setSurrendersFocusOnKeystroke(true);
	}

	/*
	 * Function: Insert a row after the "selectedRow row", and set the focus at
	 * the new row
	 */
	public void insertRow(int selectedRow, int selectedCol,
			CASTableCellValue inValue) {
		CASTableCellValue newValue;

		if (this.getSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_SELECTION) {
			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}

		if (inValue == null)
			newValue = new CASTableCellValue();
		else
			newValue = inValue;

		tableModel.insertRow(selectedRow + 1, new Object[] { newValue, "New" });

		changeSelection(selectedRow + 1, selectedCol, false, false);
		editCellAt(selectedRow + 1, selectedCol);
		// ((Component) ((CASTableCellEditor) getCellEditor(selectedRow + 1,
		// selectedCol)).getTableCellEditorComponent(this, newValue, true,
		// selectedRow + 1, selectedCol)).requestFocus();
		CASTableCellEditor tableCell = (CASTableCellEditor) getCellEditor(
				selectedRow + 1, selectedCol);
		tableCell.setInputAreaFocused();
	}

	public void insertRow(CASTableCellValue newValue) {

		// CASTableCellValue newValue = new CASTableCellValue();
		int rowNum = tableModel.getRowCount();
		// insert the row before
		tableModel.insertRow(rowNum, new Object[] { newValue, "load" });
		
		// Enlarge the cell hight
		if (newValue.isOutputVisible())
			this.setRowHeight(rowNum, CASPara.inputOutputHeight);
	}

	public void insertRow(int selectedRow, int selectedCol, char c) {
		char[] in = new char[1];
		in[0] = c;
		CASTableCellValue newValue = new CASTableCellValue(new String(in));
		tableModel.insertRow(selectedRow + 1, new Object[] { newValue, "New" });

		// ((CASTableCellRender) getCellRenderer(selectedRow + 1, selectedCol))
		// .getTableCellRendererComponent(this, newValue, false, false,
		// selectedRow + 1, selectedCol);

		changeSelection(selectedRow + 1, selectedCol, false, false);
		editCellAt(selectedRow + 1, selectedCol);
		// Component editor = (Component) ((CASTableCellEditor) getCellEditor(
		// selectedRow + 1, selectedCol)).getTableCellEditorComponent(
		// this, newValue, true, selectedRow + 1, selectedCol);
		// editor.requestFocus();

		CASTableCellEditor tableCell = (CASTableCellEditor) getCellEditor(
				selectedRow + 1, selectedCol);
		tableCell.setInputAreaFocused();

	}

	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteAllRow() {
		int row = tableModel.getRowCount();

		for (int i = row - 1; i >= 0; i--)
			tableModel.removeRow(i);
		this.repaint();

		this.getRowCount();
		// if (tableModel.getRowCount() == 0)
		// insertRow(-1, CASPara.contCol);
	}

	/*
	 * Function: Delete a rolw, and set the focus at the right position
	 */
	public void deleteRow(int row) {
		tableModel.removeRow(row);
		this.repaint(); // Update the table
		if (tableModel.getRowCount() == 0)
			insertRow(-1, CASPara.contCol, null);
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
		// ((Component) ((CASTableCellEditor) getCellEditor(editRow, editCol))
		// .getTableCellEditorComponent(this, value, true, editRow,
		// editCol)).requestFocus();
		CASTableCellEditor tableCell = (CASTableCellEditor) getCellEditor(
				editRow, editCol);
		tableCell.setInputAreaFocused();
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
		// setRowHeight(editRow, temp.addLinePanel());
		temp.addLine();
		temp.setLineBorderFocus();
	}

	public Point getIndexFromPixel(int x, int y) {
		if (x < 0 || y < 0)
			return null;
		int indexX = -1;
		int indexY = -1;
		for (int i = 0; i < getColumnCount(); ++i) {
			Point point = getPixel(i, 0, false);
			if (x < point.getX()) {
				indexX = i;
				break;
			}
		}
		if (indexX == -1) {
			return null;
		}
		for (int i = 0; i < getRowCount(); ++i) {
			Point point = getPixel(0, i, false);
			if (y < point.getY()) {
				indexY = i;
				break;
			}
		}
		if (indexY == -1) {
			return null;
		}
		return new Point(indexX, indexY);
	}

	protected Point getPixel(int column, int row, boolean min) {
		if (column < 0 || row < 0) {
			return null;
		}
		if (min && column == 0 && row == 0) {
			return new Point(0, 0);
		}
		int x = 0;
		int y = 0;
		if (!min) {
			++column;
			++row;
		}
		for (int i = 0; i < column; ++i) {
			x += getColumnModel().getColumn(i).getWidth();
		}
		int rowHeight;
		for (int i = 0; i < row; ++i) {
			rowHeight = getRowHeight(i);
			y += rowHeight;
		}
		return new Point(x, y);
	}

	public void save() {
		app.getGuiManager().save();
	}
}
