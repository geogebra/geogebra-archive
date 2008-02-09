
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
	protected MyTable table;
	protected int column; 
	protected int row; 
	protected boolean editing = false; 

    public MyCellEditor(Kernel kernel0) {
		super(new JTextField());
		kernel = kernel0;
    }

	public Component getTableCellEditorComponent(JTable table0, Object value0, boolean isSelected, int row0, int column0) {
		table = (MyTable)table0;
		value = (GeoElement)value0;
		column = column0;
		row = row0;
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
		editing = true;
		return getComponent();
    }
	
	public boolean isEditing() {
		return editing;
	}
	
	public void addLabel(int column, int row) {
		if (! editing) return;
		column = table.convertColumnIndexToModel(column);
		String name = table.getModel().getColumnName(column) + (row + 1);
		String text = (String)delegate.getCellEditorValue();
		delegate.setValue(text + name);
	}

	public String getEditingValue() {
		return (String)delegate.getCellEditorValue();
	}

    public Object getCellEditorValue() {
    	return value;
    }

	public boolean stopCellEditing() {
		String text = (String)delegate.getCellEditorValue();
		try {
			value = prepareAddingValueToTable(kernel, table, text, value, column, row);
		} catch (Exception ex) {
			kernel.getApplication().showError(ex.getMessage());
			// show GeoGebra error dialog
			kernel.getApplication().showError(ex.getMessage());
			
			//Util.handleException(table, ex);
			return false;
		}
		editing = false;
		return super.stopCellEditing();
	}
	
	// also used in RelativeCopy.java
	public static GeoElement prepareAddingValueToTable(Kernel kernel, MyTable table, String text, GeoElement oldValue, int column, int row) throws Exception {
		column = table.convertColumnIndexToModel(column);
		String name = table.getModel().getColumnName(column) + (row + 1);
    	if (text != null) {
    		text = text.trim();
    		if (text.length() == 0) {
    			text = null;
    		}
    	}
    	if (text == null) {
    		if (oldValue != null) {
    			oldValue.remove();
    			oldValue = null;
    		}
    		return null;
    	}
    	else if (oldValue == null) {
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
    		
    		GeoElement[] newValues = null;
    		try {
    			newValues = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, true);
    		} catch (Exception e) {
    			// TODO: handle exception
        		System.err.println("SPREADSHEET: input error: " + e.getMessage());        		
        		throw e;
    		}
    		
    		if (newValues != null) {    
    			return newValues[0];
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
    		
    	}
        else { // value != null;
        	if (text.startsWith("=")) {
        		text = text.substring(1);
        	} 
        	GeoElement newValue = null;
        	try {
	        	if (oldValue.isIndependent()) {
	        		newValue = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(oldValue, text, false);
	        	}
	        	else {
	        		newValue = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(oldValue, text, true);
	        	}
        	} catch (Exception e) {
        		// TODO: handle exception
        		System.err.println("SPREADSHEET: input error: " + e.getMessage());
        		throw e;
        	}
    		if (newValue != null) {
    			return newValue;    		
    		}   
    		// TODO: make text changeable too
//    		else if (value.isGeoText()) {
//        		if
////        		text = "\"" + text + "\"";
////        	}
        	
        }
    	throw new RuntimeException("Error state.");
    }

}
