package geogebra.cas.view;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;

//public class CASTableCellEditor extends DefaultCellEditor implements
//		TableCellEditor {
public class CASTableCellEditor extends CASTableCell implements TableCellEditor {
	
	private JTable table;
	
	private CASTableCellValue cellValue;
	private boolean editing = false;
	private int editingRow;
	
	private ArrayList listeners = new ArrayList();

	public CASTableCellEditor(CASView view) {
		super(view);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof CASTableCellValue) {			
			setFont(app.getBoldFont());
			
			editing = true;
			editingRow = row;
			cellValue = (CASTableCellValue) value;
			this.table = table;
			
			setInput(cellValue.getInput());					
			setOutput(cellValue.getOutput(), cellValue.isOutputError());	
	
			// update row height
			updateTableRowHeight(table, row);
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
			fireEditingStopped();	
		}
					
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
		if (editing && editingRow < table.getRowCount()) {	
			ChangeEvent ce = new ChangeEvent(this);
			for (int i=0; i < listeners.size(); i++) {
				CellEditorListener l = (CellEditorListener) listeners.get(i);
				l.editingCanceled(ce);
			}
		}
		
		editing = false;
	}
	
	protected void fireEditingStopped() {		
		if (editing && editingRow < table.getRowCount()) {	
			ChangeEvent ce = new ChangeEvent(this);
			for (int i=0; i < listeners.size(); i++) {
				CellEditorListener l = (CellEditorListener) listeners.get(i);
				l.editingStopped(ce);
			}
		}
		
		editing = false;		
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
