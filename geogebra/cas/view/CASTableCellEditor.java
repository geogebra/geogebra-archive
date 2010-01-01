package geogebra.cas.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

//public class CASTableCellEditor extends DefaultCellEditor implements
//		TableCellEditor {
public class CASTableCellEditor extends CASTableCell implements TableCellEditor, KeyListener {
	
	private JTable table;
	
	private CASTableCellValue cellValue;
	private boolean editing = false;
	private int editingRow;
		
	private ArrayList listeners = new ArrayList();

	public CASTableCellEditor(CASView view) {
		super(view);
		getInputArea().addKeyListener(this);
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof CASTableCellValue) {						
			editing = true;
			editingRow = row;
			cellValue = (CASTableCellValue) value;
			this.table = table;
			
			// fill input and output panel
			setValue(cellValue);					
		
			// update row height
			updateTableRowHeight(table, row);
		}
		return this;
	}	
	
	public void setFont(Font ft) {
		super.setFont(ft);
		if (inputPanel != null)
			inputPanel.setFont(ft.deriveFont(Font.BOLD));		
	}
	
	public String getInputText() {	
		return getInputArea().getText();
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
	
	public void insertText(String text) {
		getInputArea().replaceSelection(text);
		getInputArea().requestFocusInWindow();
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
	
	public final int getEditingRow() {
		return editingRow;
	}
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
			case KeyEvent.VK_ESCAPE:
				getInputArea().setText("");
				e.consume();
				break;
				
		}
	}

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent e) {
		char ch = e.getKeyChar();
		JTextComponent inputArea = getInputArea();
		String text = inputArea.getText();
		
		// if closing paranthesis is typed and there is no opening parenthesis for it
		// add one in the beginning
		switch (ch){				
			
			case ' ':
			case '|':
				// insert output of previous row (not in parentheses)
				if (editingRow > 0 && text.length() == 0) {
					CASTableCellValue selCellValue = view.getConsoleTable().getCASTableCellValue(editingRow - 1);				
					inputArea.setText(selCellValue.getOutput() + " ");
				}
				break;
				
			case ')':
				// insert output of previous row in parentheses		
				if (editingRow > 0 && text.length() == 0) {
					CASTableCellValue selCellValue = view.getConsoleTable().getCASTableCellValue(editingRow - 1);				
					String prevOutput = selCellValue.getOutput();
					inputArea.setText("(" +  prevOutput);
				}
				break;		
				
			case '=':
				// insert input of previous row
				if (editingRow > 0 && text.length() == 0) {
					CASTableCellValue selCellValue = view.getConsoleTable().getCASTableCellValue(editingRow - 1);				
					inputArea.setText(selCellValue.getInput());
					e.consume();
				}
				break;
		}
	}
	
}
