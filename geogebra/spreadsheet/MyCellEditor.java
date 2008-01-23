
package geogebra.spreadsheet;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.Component;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class MyCellEditor extends DefaultCellEditor {
	
	private static final long serialVersionUID = 1L;

	protected Kernel kernel;
	protected GeoElement value;
	protected String name;
	protected JTable table;

    public MyCellEditor(Kernel kernel0) {
		super(new JTextField());
		kernel = kernel0;
    }

	public Component getTableCellEditorComponent(JTable table0, Object value0, boolean isSelected, int row, int column) {
		table = table0;
		value = (GeoElement)value0;
		column = table.convertColumnIndexToModel(column);
		name = table.getModel().getColumnName(column) + (row + 1);
		String text = "";
		if (value != null) {
			if (value.isChangeable()) {
				text = value.toValueString();
			}
			else {
				text = value.getCommandDescription();
			}
		}
		delegate.setValue(text);
		return getComponent();
    }

    public Object getCellEditorValue() {
    	return value;
    }

	public boolean stopCellEditing() {
		if (! stopCellEditing0()) {
			return false;
		}
		return super.stopCellEditing();
	}

	public boolean stopCellEditing0() {
    	String text = (String)delegate.getCellEditorValue();
    	
    	if (text != null) {
    		text = text.trim();
    		if (text.length() == 0) {
    			text = null;
    		}
    	}
    	if (text == null) {
    		if (value != null) {
    			value.remove();
    			value = null;
    		}
    		return true;
    	}
    	else if (value == null) {
    		int posEqual = text.indexOf('=');
    		// text like "= A1 + A2"
    		if (posEqual == 0) {
    			text = name + text;
    		}
    		// text like "x^2 + y^2 = 25"
    		else if (posEqual > 0) {
    			text = name + ":" + text;
    		}
    		// no equal sign in input
    		else {
    			text = name + "=" + text;
    		}
    		GeoElement[] newValues = kernel.getAlgebraProcessor().processAlgebraCommand(text, true);
    		
    		if (newValues != null) {    
    			value = newValues[0];
    			return true;    		
    		} 
    		
    		// TODO: 
    		// make input text if input is not recognized like in Excel    		
//    		else {    
//        		
//        		text = name + "\"" + text + "\"";
//        		newValues = kernel.getAlgebraProcessor().processAlgebraCommand(text, true);
//        		
//        		value = newValues[0];
//    			if (value.isGeoText()) {
//    				value.setEuclidianVisible(false);
//    				value.update();
//    			}
//        	}
    		
    		return false;
    	}
        else { // value != null;
        	if (text.startsWith("=")) {
        		text = text.substring(1);
        	} 
        	
        	GeoElement newValue = null;
        	if (value.isIndependent()) {
        		newValue = kernel.getAlgebraProcessor().changeGeoElement(value, text, false);
        	}
        	else {
        		newValue = kernel.getAlgebraProcessor().changeGeoElement(value, text, true);
        	}        
    		if (newValue != null) {
    			value = newValue;
    			return true;    		
    		}
    		return false;    
    		
    		
    		// TODO: make text changeable too
//    		else if (value.isGeoText()) {
//        		if
////        		text = "\"" + text + "\"";
////        	}
        	
        }
    }

}
