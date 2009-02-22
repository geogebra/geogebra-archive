package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

//public class CASTableCellEditor extends DefaultCellEditor implements
//		TableCellEditor {
public class CASTableCellEditor extends CASTableCell implements TableCellEditor {

	private int selRow;
	private CASTableCellValue cellValue;
	private CASView view;
	private boolean editing = false;
	
	private ArrayList listeners = new ArrayList();

	public CASTableCellEditor(CASView view) {
		super(view);
		this.view = view;				
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value instanceof CASTableCellValue) {			
			setFont(app.getBoldFont());
			
			editing = true;
			cellValue = (CASTableCellValue) value;
			setInput(cellValue.getInput());					
			setOutput(cellValue.getOutput(), cellValue.isOutputError());	
	
			// update row height
			updateTableRowHeight(table, row);
						
			//System.out.println("EDITOR row: " + row + ", input: " + cellValue.getInput() + ", output: " + cellValue.getOutput());	
		}
		return this;
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
		
	public boolean stopCellEditing() {	
		// update cellValue's input using editor content
		if (editing) {
			cellValue.setInput(getInput());			
		}
		
		fireEditingStopped();		
		return true;
	}
	
	public void cancelCellEditing() {
		// update cellValue's input using editor content
		if (editing) {
			cellValue.setInput(getInput());			
		}
		
		fireEditingCanceled();
	}
	
	public Object getCellEditorValue() {		
		return cellValue;
	}


	protected void fireEditingCanceled() {
		editing = false;
		
		ChangeEvent ce = new ChangeEvent(this);
		for (int i=0; i < listeners.size(); i++) {
			CellEditorListener l = (CellEditorListener) listeners.get(i);
			l.editingCanceled(ce);
		}
	}
	
	protected void fireEditingStopped() {
		editing = false;
		
		ChangeEvent ce = new ChangeEvent(this);
		for (int i=0; i < listeners.size(); i++) {
			CellEditorListener l = (CellEditorListener) listeners.get(i);
			l.editingStopped(ce);
		}
	}

	public boolean isCellEditable(EventObject anEvent) {	
		return true;
	}
	

	public void removeCellEditorListener(CellEditorListener l) {
		listeners.remove(l);
	}
	
	public void addCellEditorListener(CellEditorListener l) {
		if (!listeners.contains(l))
			listeners.add(l);
	}

	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	
	
}
