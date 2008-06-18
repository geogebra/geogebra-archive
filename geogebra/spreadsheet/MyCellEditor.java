
package geogebra.spreadsheet;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent ;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class MyCellEditor extends DefaultCellEditor {
	
	private static final long serialVersionUID = 1L;

	protected Kernel kernel;
	protected GeoElement value;
	protected MyTable table;
	protected int column; 
	protected int row; 
	public boolean editing = false; 

    public MyCellEditor(Kernel kernel0) {
		super(new JTextField());
		kernel = kernel0;
		getComponent().addKeyListener(new KeyListener4());
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
			if ((! value.isGeoText()) && text.indexOf("=") == -1) {
				text = "=" + text;			
			}
		}
		delegate.setValue(text);
		editing = true;
		table.repaint();
		Component component = getComponent();
		return component;
    }
	
	public class KeyListener4 implements KeyListener 
	{
		
		public void keyTyped(KeyEvent e) {
			int keyCode = e.getKeyChar();
			if (keyCode == 27) {
				fireEditingCanceled();
				editing = false;
			}
		}
		
		public void keyPressed(KeyEvent e) {
		}
		
		public void keyReleased(KeyEvent e) {
		}
		
	}
	
	public boolean isEditing() {
		return editing;
	}
	
	public void addLabel(int column, int row) {
		column = table.convertColumnIndexToModel(column);
		String name = table.getModel().getColumnName(column) + (row + 1);
		addLabel(name);
	}

	public void addLabel(String label) {
		if (! editing) return;
		String text = (String)delegate.getCellEditorValue();
		delegate.setValue(text + label);
	}

	public String getEditingValue() {
		return (String)delegate.getCellEditorValue();
	}

    public Object getCellEditorValue() {
    	return value;
    }

	public boolean stopCellEditing() {
		//System.out.println("stopCellEditing()");
		String text = (String)delegate.getCellEditorValue();
		try {
			value = prepareAddingValueToTable(kernel, table, text, value, column, row);
		} catch (Exception ex) {
			// show GeoGebra error dialog
			kernel.getApplication().showError(ex.getMessage());
			
			//Util.handleException(table, ex);
			return false;
		}
		editing = false;
		// TODO: add undo point here
		return super.stopCellEditing();
	}
	
	public void undoEdit() {
		String text = "";
		if (value != null) {
			if (value.isChangeable()) {
				text = value.toValueString();
			}
			else {
				text = value.getCommandDescription();
			}
			if ((! value.isGeoText()) && text.indexOf("=") == -1) {
				text = "=" + text;			
			}
		}
		delegate.setValue(text);
	}
	
	// also used in RelativeCopy.java
	public static GeoElement prepareAddingValueToTable(Kernel kernel, MyTable table, String text, GeoElement oldValue, int column, int row) throws Exception {
		//column = table.convertColumnIndexToModel(column);
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
    		String text0 = text;
    		int posEqual = text.indexOf('=');
    		// text like "= A1 + A2"
    		if (posEqual == 0) {
    			text = name + text;
    		}
    		// text like "x^2 + y^2 = 25"
    		else if (posEqual > 0) {
    			if (! text.startsWith(name)) {
    				text = name + ":" + text;
    			}
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
        		//System.err.println("SPREADSHEET: input error: " + e.getMessage());
    			if (! text0.startsWith("=")) {
        			text = name + "=\"" + text0 + "\"";
       				newValues = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, true);
       				if (newValues[0].isGeoText()) {
       					newValues[0].setEuclidianVisible(false);
           				newValues[0].update();
       				}
    			}
    			else {
    				throw e;
    			}
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
    		String text0 = text;
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
        		//System.err.println("SPREADSHEET: input error: " + e.getMessage());
        		if (text0.startsWith("=") || text0.startsWith("\"")){
        			throw e;
        		} else {
        			text = name + "=\"" + text0 + "\"";
    	        	if (oldValue.isIndependent()) {
    	        		newValue = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(oldValue, text, false);
    	        	}
    	        	else {
    	        		newValue = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(oldValue, text, true);
    	        	}
        		}
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
