package geogebra.cas.view;

import java.awt.Component;
import java.awt.event.ItemEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class CASTableCellEditor extends DefaultCellEditor implements TableCellEditor{

	CASTableCell panel;
	protected Object value;

	public CASTableCellEditor(JTextField textField, CASTableCell panel) {
		super(textField);
		this.panel = panel;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		if (value instanceof CASTableCellValue){
			//panel.setInput(((String) value).toString());
			System.out.println("Editor - Row: " + row);
			CASTableCellValue tempV = (CASTableCellValue)value;
    		//System.out.println(((CASTableCellValue)value).getCommand());
    		//System.out.println(((CASTableCellValue)value).getOutput());
			String tempIn = tempV.getCommand();
			String tempOut = tempV.getOutput();
			
			if(isSelected)
				panel.setInput(tempIn);
			
			if(tempOut.compareTo("")>0)
				panel.setOutput(tempOut);
			else
				panel.setOutputBlank();
			
			if(tempV.getOutputAreaInclude()){
				panel.addOutputArea();
			}
			
			setCellEditorValue(value);
			//System.out.println("The render is " + value.getClass().getName());
		}
		
		return panel;
	}
	
	public Object getCellEditorValue() {
		return value;
	}

	public void itemStateChanged(ItemEvent e) {
		super.fireEditingStopped();
	}

	public void setCellEditorValue(Object value) {
		this.value = value;
	}
	
	public boolean stopCellEditing() {
		CASTableCellValue temp = new CASTableCellValue(panel.getInput(), panel.getOutput());
		temp.setOutputAreaInclude(panel.isOutputAreaAdded());
		temp.setBBorderInclude(panel.isLineHighlighted());
		setCellEditorValue(temp);
		return super.stopCellEditing();
	}
}
