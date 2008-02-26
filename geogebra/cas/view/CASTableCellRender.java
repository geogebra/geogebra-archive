package geogebra.cas.view;

import java.awt.Component;
import java.awt.Window;

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

		if (value instanceof CASTableCellValue){
			//this.setInput(((String) value).toString());
			CASTableCellValue tempV = (CASTableCellValue)value;
//			System.out.println("Rendering Row: " + row);
//    		System.out.println("Input: " + tempV.getCommand());
//    		System.out.println("Output: " + tempV.getOutput());
//    		System.out.println(tempV.getOutputAreaInclude());
			String tempIn = tempV.getCommand();
			String tempOut = tempV.getOutput();
			
			if(tempIn.compareTo("")>0)
				setInput(tempIn);
			else{
				//System.out.println("Input Set Blank");
				setInputBlank();
			}
			
			if(tempOut.compareTo("")>0)
				setOutput(tempOut);
			else{
				//System.out.println("Output Blank");
				setOutputBlank();
			}
			
			//Bug: I set the value to true, but why it is still false?
			if(tempV.getOutputAreaInclude()){
				System.out.println("Render: Output Area is added " + row);
				this.addOutputArea();
			}
		}
		return this;
	}	
	
}
