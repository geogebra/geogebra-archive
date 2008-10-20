package geogebra.cas.view;

import geogebra.Application;

import java.awt.Component;
import java.util.EventObject;

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

	public CASTableCellEditor(CASView view, JTable consoleTable, Application app) {
		super(view, consoleTable, app);

		CASTableCellController inputListener = new CASTableCellController(this,
				view);
		this.getInputArea().addKeyListener(inputListener);
		this.getLinePanel().addKeyListener(inputListener);
		
		EditorFocusListener l = new EditorFocusListener(this);
		this.addFocusListener(l);

		table = consoleTable;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value instanceof CASTableCellValue) {
			setFont(app.getPlainFont());
			this.setLineInvisiable(); // Initialize the editor display without
										// line panle

			cellValue = (CASTableCellValue) value;
			// Application.debug("Editor - Row: " + row);
			// Application.debug(cellValue.getCommand());
			// Application.debug(cellValue.getOutput());
			String tempIn = cellValue.getCommand();
			String tempOut = cellValue.getOutput();

			Component[] temp = this.getComponents();
//			Application.debug("We have componets: " + temp.length);
//			Application.debug("Output: " + tempOut.length());
//			for (int i = 0; i < temp.length; i++) {
//				Application.debug("componets: " + temp[i]);
//			}

			this.setInput(tempIn);
			this.setOutput(tempOut);

			if (tempOut.length() == 0) {
				this.setOutputFieldVisiable(true);
				this.removeOutputPanel();
			}

			SwingUtilities.updateComponentTreeUI(this);
		}
		return this;
	}

	public Object getCellEditorValue() {
		return cellValue;
	}
	
	public String getSelectedText() {
		return getInputArea().getSelectedText();
	}

	public void setCellEditorValue(CASTableCellValue value) {
		this.cellValue = value;
	}

	public boolean stopCellEditing() {
		cellValue.setCommand(this.getInput());
		cellValue.setOutput(this.getOutput());
		
		//Application.debug("Cell Editor stops editting at selected " + this.getInputArea().getText());
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
