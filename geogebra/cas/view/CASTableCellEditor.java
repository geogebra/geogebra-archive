package geogebra.cas.view;

import geogebra.main.Application;

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
	private CASTable table;

	private CASTableCellValue cellValue;

	public CASTableCellEditor(CASView view, CASTable consoleTable, Application app) {
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
			this.setInput(cellValue.getInput());
			String output = cellValue.getOutput();			
			this.setOutput(output, cellValue.isOutputError());
			if (output == null || output.length() == 0) {
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
	
	public String getInputSelectedText() {	
		return getInputArea().getSelectedText();
	}
	
	public int getInputSelectionStart() {	
		return getInputArea().getSelectionStart();
	}
	
	public int getInputSelectionEnd() {	
		return getInputArea().getSelectionEnd();
	}

	public void setCellEditorValue(CASTableCellValue value) {
		this.cellValue = value;
	}
	
	public void cancelCellEditing() {
		stopCellEditing();
	}

	public boolean stopCellEditing() {
		cellValue.setInput(this.getInput());
		cellValue.setOutput(this.getOutput());

		Application.debug("Cell Editor stops editting at selected " + this.getInputArea().getText());
		return true;
	}

	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
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
