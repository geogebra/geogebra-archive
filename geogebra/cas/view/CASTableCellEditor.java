package geogebra.cas.view;

import java.awt.Component;
import java.awt.event.ItemEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JTable;

public class CASTableCellEditor extends DefaultCellEditor {

	CASTableCell panel;

	public CASTableCellEditor(JTextField textField, CASTableCell panel) {
		super(textField);
		this.panel = panel;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value instanceof CASTableCellValue){
			//panel.setInput(((String) value).toString());
			System.out.println("Row: " + row);
    		System.out.println(((CASTableCellValue)value).getCommand());
    		System.out.println(((CASTableCellValue)value).getOutput());
			panel.setInput(((CASTableCellValue)value).getCommand());
			panel.setOutput(((CASTableCellValue)value).getOutput());
			//System.out.println("The render is " + value.getClass().getName());
		}
		return panel;
	}

	public Object getCellEditorValue() {
		// return new Integer(panel.getSelectedIndex());
		return this;
	}

	public void itemStateChanged(ItemEvent e) {
		super.fireEditingStopped();
	}

}
