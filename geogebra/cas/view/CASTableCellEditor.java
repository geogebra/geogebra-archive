package geogebra.cas.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

//public class CASTableCellEditor extends DefaultCellEditor implements
//		TableCellEditor {
public class CASTableCellEditor extends CASTableCell implements TableCellEditor {

	// CASTableCell panel;
	private JTable table;

	private CASTableCellValue cellValue;

	private int row;

	private int col;

	public CASTableCellEditor(CASView view, JTable consoleTable) {
		super(view, consoleTable);

		CASTableCellController inputListener = new CASTableCellController(this,
				view);
		this.getInputArea().addKeyListener(inputListener);
		this.getLinePanel().addKeyListener(inputListener);

		table = consoleTable;
	}

	// public CASTableCellEditor(JTextField textField, CASTableCell panel) {
	// //super(textField);
	// this.panel = panel;
	// }

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value instanceof CASTableCellValue) {
			this.setLineInvisiable(); // Initialize the editor display without
										// line panle

			this.row = row;
			this.col = column;
			// panel.setInput(((String) value).toString());
			cellValue = (CASTableCellValue) value;
			System.out.println("Editor - Row: " + row
					+ cellValue.isOutputVisible());
			System.out.println(cellValue.getCommand());
			System.out.println(cellValue.getOutput());
			String tempIn = cellValue.getCommand();
			String tempOut = cellValue.getOutput();

			Component[] temp = this.getComponents();
			System.out.println("We have componets: " + temp.length);
			System.out.println("Output: " + tempOut.length());
			for (int i = 0; i < temp.length; i++) {
				System.out.println("componets: " + temp[i]);
			}

			this.setInput(tempIn);
			this.setOutput(tempOut);

			if (tempOut.length() == 0) {
				this.setOutputFieldVisiable(true);
				this.removeOutputPanel();
			}

			// if(cellValue.isLineBorderVisible()){
			// this.addLinePanel();
			// System.out.println("Line is added at " + row);
			// }

			SwingUtilities.updateComponentTreeUI(this);
		}
		return this;
	}

	public Object getCellEditorValue() {
		return cellValue;
	}

	public void setCellEditorValue(CASTableCellValue value) {
		this.cellValue = value;
	}

	public boolean stopCellEditing() {
		CASTableCellValue temp = new CASTableCellValue(cellValue.getCommand(),
				cellValue.getOutput());
		temp.setOutputAreaInclude(cellValue.isOutputVisible());
		// temp.setLineBorderVisible(cellValue.isLineBorderVisible());
		table.setValueAt(temp, row, col);
		setCellEditorValue(temp);
		return true;
	}

	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub

	}

	public void cancelCellEditing() {
		// TODO Auto-generated method stub
		return;
	}

	public boolean isCellEditable(EventObject anEvent) {
		// TODO Auto-generated method stub
		return true;
	}

	public void removeCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub

	}

	public boolean shouldSelectCell(EventObject anEvent) {
		// TODO Auto-generated method stub
		return true;
	}
}
