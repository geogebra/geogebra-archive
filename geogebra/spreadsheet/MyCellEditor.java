
package geogebra.spreadsheet;

import geogebra.Application;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Function;
import geogebra.kernel.arithmetic.ValidExpression;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

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
		Component component = getComponent();
		component.addKeyListener(new KeyListener4());
		if (component.getFont().getSize() == 0) {
			Font font1 = kernel.getApplication().getPlainFont();
			if (font1 == null || font1.getSize() == 0) {
				font1 = new Font("dialog", 0, 12);
			}
			component.setFont(font1);

		}
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
			int index = text.indexOf("=");
			if ((! value.isGeoText())) {
				if (index == -1) {
					text = "=" + text;			
				}
				if (index > 0) {
					text = text.substring(index - 1).trim();
				}
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

	public void setLabel(String text) {
		if (! editing) return;
		delegate.setValue(text);
	}

	public String getEditingValue() {
		return (String)delegate.getCellEditorValue();
	}

    public Object getCellEditorValue() {
    	return value;
    }

	public boolean stopCellEditing() {
		//Application.debug("stopCellEditing()");
		String text = (String)delegate.getCellEditorValue();
		try {
			value = prepareAddingValueToTableNoStoringUndoInfo(kernel, table, text, value, column, row);
			kernel.storeUndoInfo();
		} catch (Exception ex) {
			// show GeoGebra error dialog
			//kernel.getApplication().showError(ex.getMessage());
			ex.printStackTrace();
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
	
	private static GeoElement prepareNewValue(Kernel kernel, String name, String text) throws Exception {
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
			// check if input is a function in x
			ValidExpression ve = kernel.getParser().parse(text);	
			GeoElement [] temp = kernel.getAlgebraProcessor().processValidExpression(ve);
			
			if (temp[0].isGeoFunction())
				text = name + "(x)=" + text;
			else
				text = name + "=" + text;
			
			temp[0].remove();
		}
		
		GeoElement[] newValues = null;
		try {
			newValues = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
			for (int i = 0; i < newValues.length; ++ i) {
				newValues[i].setAuxiliaryObject(newValues[i].isGeoNumeric());
			}
		} catch (Exception e) {
			// TODO: handle exception
    		//Application.debug("SPREADSHEET: input error: " + e.getMessage());
			if (! text0.startsWith("=")) {
    			text = name + "=\"" + text0 + "\"";
   				newValues = kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
   				if (newValues[0].isGeoText()) {
   					newValues[0].setEuclidianVisible(false);
       				newValues[0].update();
   				}
			}
			else {
				throw e;
			}
		}
		return newValues[0];
	}
	
	private static GeoElement updateOldValue(Kernel kernel, GeoElement oldValue, String name, String text) throws Exception {
		String text0 = text;
    	if (text.startsWith("=")) {
    		text = text.substring(1);
    	} 
    	GeoElement newValue = null;
    	try {
//        	if (oldValue.isIndependent()) {
//        		newValue = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(oldValue, text, false);
//        		// !!!problem here to be solved.
//        		//Application.debug(">> " + newValue.toValueString());
//        	}
//        	else {
    			// always redefine objects in spreadsheet
        		newValue = kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(oldValue, text, true);
    //    	}
        	newValue.setConstructionDefaults();
        	//Application.debug("GeoClassType = " + newValue.getGeoClassType());
        	if (newValue.getGeoClassType() == oldValue.getGeoClassType()) {
        		newValue.setVisualStyle(oldValue);	        		
        	}
        	else {
        		kernel.getApplication().refreshViews();
        	}
    	} catch (Throwable e) {
    		// TODO: handle exception
    		//Application.debug("SPREADSHEET: input error: " + e.getMessage());
    		//Application.debug("text0 = " + text0);
    		if (text0.startsWith("=") || text0.startsWith("\"")){
    			throw new Exception(e);
    		} else {
    			if (!oldValue.hasChildren()) {
	    			oldValue.remove();
	    			prepareNewValue(kernel, name, text0);
    			}
    			else {
        			throw new Exception(e);
    			}
    		}
    	}
		return newValue;    		
	}
	
	// also used in RelativeCopy.java
	public static GeoElement prepareAddingValueToTableNoStoringUndoInfo(Kernel kernel, MyTable table, String text, GeoElement oldValue, int column, int row) throws Exception {
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
    		}
    		return null;
    	}
    	else if (oldValue == null) {
    		return prepareNewValue(kernel, name, text);
     	}
        else { // value != null;
        	return updateOldValue(kernel, oldValue, name, text);        	
        }
    }

}
