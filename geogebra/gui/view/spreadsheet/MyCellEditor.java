package geogebra.gui.view.spreadsheet;

import geogebra.gui.inputbar.AlgebraInput;
import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;

public class MyCellEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;

	protected Kernel kernel;
	protected Application app;
	protected GeoElement value;
	protected MyTable table;
	protected int column;
	protected int row;
	public boolean editing = false;

	public MyCellEditor(Kernel kernel0) {
		//super(new JTextField());
		super(new AutoCompleteTextField(0, kernel0.getApplication(), false));
		kernel = kernel0;
		app = kernel.getApplication();
		
		editorComponent.addKeyListener(new KeyListener4());		
	}
	
	private boolean clickedToType = false;

	public Component getTableCellEditorComponent(JTable table0, Object value0,
			boolean isSelected, int row0, int column0) {
		table = (MyTable) table0;
		
		Font font1 = kernel.getApplication().getPlainFont();
		if (font1 == null || font1.getSize() == 0) {
			font1 = new Font("dialog", 0, 12);
		}
		getComponent().setFont(font1);
		
		if (value0 instanceof String) { // clicked to type
			value = null;
			clickedToType = true;
		}
		else
		{
			value = (GeoElement) value0;
			clickedToType = false;
		}
		
		column = column0;
		row = row0;
		String text = "";
		
		if (value != null) {
			if (value.isChangeable()) {
				text = value.toValueString();
			} else {
				text = value.getCommandDescription();
			}
			int index = text.indexOf("=");
			if ((!value.isGeoText())) {
				if (index == -1) {
					text = "=" + text;
				}
				// if (index > 0) {
				// text = text.substring(index - 1).trim();
				// }
			}
		}
		delegate.setValue(text);
		editing = true;
		table.repaint();
		Component component = getComponent();
		return component;
	}

	public class KeyListener4 implements KeyListener {

		public void keyTyped(KeyEvent e) {

			//checkCursorKeys(e); // needed for MAC_OS???

			char keyChar = e.getKeyChar();
			//Application.debug(e.toString());
			switch (keyChar) {
				case 0x1b:	// case KeyEvent.VK_ESCAPE:
					cancelCellEditing();
					editing = false;
					
					// restore old text in spreadsheet
					table.getModel().setValueAt(kernel.getGeoAt(column, row), row, column);
					
					break;													
			}	
		}

		public void keyPressed(KeyEvent e) {
			
			checkCursorKeys(e);
			
				char keyChar = e.getKeyChar();
				switch (keyChar) {
			case 0x1b:	// case KeyEvent.VK_ESCAPE:
				cancelCellEditing();
				editing = false;
				
				// restore old text in spreadsheet
				table.getModel().setValueAt(kernel.getGeoAt(column, row), row, column);
				
				break;													
			case 0x0a:	// case KeyEvent.VK_ENTER:
					if (e.isConsumed())
						break;
					
					// go to cell below
					table.changeSelection(row + 1, column, false, false);	
					break;							
			}	
		}

		public void keyReleased(KeyEvent e) {
		}
		
		public void checkCursorKeys(KeyEvent e) {
			int keyCode = e.getKeyCode();
			//Application.debug(e+"");
			switch (keyCode) {
			case KeyEvent.VK_UP:
				//Application.debug("UP");
				stopCellEditing();		
				editing = false;
				table.setRowSelectionInterval(table.minSelectionRow - 1,table.minSelectionRow - 1);
				break;
			case KeyEvent.VK_RIGHT:
				//Application.debug("RIGHT");
				stopCellEditing();		
				editing = false;
				table.setColumnSelectionInterval(table.minSelectionColumn + 1,table.minSelectionColumn + 1);
				break;
			case KeyEvent.VK_DOWN:
				//Application.debug("DOWN");
				stopCellEditing();		
				editing = false;
				table.setRowSelectionInterval(table.minSelectionRow + 1,table.minSelectionRow + 1);
				break;
			case KeyEvent.VK_LEFT:
				//Application.debug("LEFT");
				stopCellEditing();		
				editing = false;
				table.setColumnSelectionInterval(table.minSelectionColumn - 1,table.minSelectionColumn - 1);
				break;
			}
			
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
		if (!editing)
			return;
		String text = (String) delegate.getCellEditorValue();
		delegate.setValue(text + label);
	}

	public void setLabel(String text) {
		if (!editing)
			return;
		delegate.setValue(text);
	}

	public String getEditingValue() {
		return (String) delegate.getCellEditorValue();
	}

	public Object getCellEditorValue() {
		return value;
	}

	public boolean stopCellEditing() {
		
		String text = (String) delegate.getCellEditorValue();

		try {
			

			// get GeoElement of current cell
			value = kernel.lookupLabel(  GeoElement.getSpreadsheetCellName(column, row), false);

			
			value = prepareAddingValueToTableNoStoringUndoInfo(kernel, table,
					text, value, column, row);
			app.storeUndoInfo();

			AlgebraInput ai = (AlgebraInput) (app.getGuiManager()
					.getAlgebraInput());

			// copy description into input bar
			if (value != null) {
				app.geoElementSelected(value, true); // copy definiton to input bar
				//ai.setStxxring(value);
			} else {
				ai.clear();
			}

		} catch (Exception ex) {
			// show GeoGebra error dialog
			// kernel.getApplication().showError(ex.getMessage());
			ex.printStackTrace();
			// Util.handleException(table, ex);
			return false;
		}
		editing = false;
		
		boolean ret = super.stopCellEditing();
		table.selectionChanged();
		return ret;
	}

	public void undoEdit() {
		String text = "";
		
		if (value != null) {
			if (value.isChangeable()) {
				text = value.toValueString();
			} else {
				text = value.getCommandDescription();
			}
			if ((!value.isGeoText()) && text.indexOf("=") == -1) {
				text = "=" + text;
			}
		}
		delegate.setValue(text);
		super.stopCellEditing();
	}

	private static GeoElement prepareNewValue(Kernel kernel, String name,
			String text) throws Exception {
		if (text == null)
			return null;

		// remove leading equal sign, e.g. "= A1 + A2"
		if (text.startsWith("=")) {
			text = text.substring(1);
		}

		// no equal sign in input
		GeoElement[] newValues = null;
		try {
			// check if input is the name of an existing variable
			if (kernel.lookupLabel(text) != null) {
				// make sure we copy this existing geo by providing the new cell
				// name in the beginning
				text = name + " = " + text;
			}

			// evaluate input text
			newValues = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(text, false);
			// newValues[0].setLabel(name);
			GeoElement.setLabels(name, newValues); // set names to be D1, E1,
													// F1, etc for multiple
													// objects
			newValues[0].setAuxiliaryObject(true);
		} catch (Exception e) {
			// create text if something went wrong
			text = "\"" + text + "\"";
			newValues = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(text, false);
			newValues[0].setLabel(name);
			newValues[0].setEuclidianVisible(false);
			newValues[0].update();
		}
		return newValues[0];
	}

	private static GeoElement updateOldValue(Kernel kernel,
			GeoElement oldValue, String name, String text) throws Exception {
		String text0 = text;
		if (text.startsWith("=")) {
			text = text.substring(1);
		}
		GeoElement newValue = null;
		try {
			// always redefine objects in spreadsheet, don't store undo info
			// here
			newValue = kernel.getAlgebraProcessor()
					.changeGeoElementNoExceptionHandling(oldValue, text, true,
							false);
			// newValue.setConstructionDefaults();
			newValue.setAllVisualProperties(oldValue);
			if (oldValue.isAuxiliaryObject())
				newValue.setAuxiliaryObject(true);

			// Application.debug("GeoClassType = " +
			// newValue.getGeoClassType()+" " + newValue.getGeoClassType());
			if (newValue.getGeoClassType() == oldValue.getGeoClassType()) {
				// newValue.setVisualStyle(oldValue);
			} else {
				kernel.getApplication().refreshViews();
			}
		} catch (Throwable e) {
			 //Application.debug("SPREADSHEET: input error: " + e.getMessage());
			 //Application.debug("text0 = " + text0);
			
			
			//if (text0.startsWith("=") || text0.startsWith("\"")) {
				//throw new Exception(e);				
			//} else
			{
				if (!oldValue.hasChildren()) {
					oldValue.remove();
					
					// add input as text
					try {
						newValue = prepareNewValue(kernel, name, "\"" + text0 + "\"");
					}
					catch (Throwable t) {
						newValue = prepareNewValue(kernel, name, "");
					}
					newValue.setEuclidianVisible(false);
					newValue.update();
				} else {
					throw new Exception(e);
				}
			}
		}
		return newValue;
	}

	// also used in RelativeCopy.java
	public static GeoElement prepareAddingValueToTableNoStoringUndoInfo(
			Kernel kernel, MyTable table, String text, GeoElement oldValue,
			int column, int row) throws Exception {
		// column = table.convertColumnIndexToModel(column);
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
		} else if (oldValue == null) {
			try {
				return prepareNewValue(kernel, name, text);
			} catch (Throwable t) {
				return prepareNewValue(kernel, name, "");
			}
		} else { // value != null;
			return updateOldValue(kernel, oldValue, name, text);
		}
	}

}
