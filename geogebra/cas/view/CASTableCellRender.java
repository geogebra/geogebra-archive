package geogebra.cas.view;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class CASTableCellRender extends CASTableCell implements 
	TableCellRenderer{

	CASTableCellRender(CASView view, JTable consoleTable) {
		super(view, consoleTable);
	}

	public Component getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
//		if (value instanceof Integer) {
//			setSelectedIndex(((Integer) value).intValue());
//		}
		
		Object t= ((CASTableModel)table.getModel()).getValueAt(row); 
		System.out.println("Row " + row + " Out-- " + ((CASTableCellValue)t).getOutput());
		if (value instanceof CASTableCellValue){
			//this.setInput(((String) value).toString());
			System.out.println("Row: " + row);
    		System.out.println(((CASTableCellValue)value).getCommand());
    		System.out.println(((CASTableCellValue)value).getOutput());
			setInput(((CASTableCellValue)value).getCommand());
			setOutput(((CASTableCellValue)value).getOutput());	
		}
		return this;
	}	
	
}
