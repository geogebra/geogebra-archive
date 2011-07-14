package geogebra.gui.view.spreadsheet;

import geogebra.gui.inputbar.AutoCompleteTextField;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * Default cell editor for the spreadsheet, extends DefaultCellEditor(JTextField)
 *
 */
public class MyCellEditor extends DefaultCellEditor implements FocusListener {

	private static final long serialVersionUID = 1L;

	protected Kernel kernel;
	protected Application app;
	protected GeoElement value;
	protected MyTable table;
	private AutoCompleteTextField textField;

	protected int column;
	protected int row;
	private boolean editing = false;
	private boolean errorOnStopEditing = false;



	private boolean allowProcessGeo = false;

	public boolean allowProcessGeo() {
		return allowProcessGeo;
	}

	public void setAllowProcessGeo(boolean allowProcessGeo) {
		this.allowProcessGeo = allowProcessGeo;
	}



	public MyCellEditor(Kernel kernel) {

		super(new AutoCompleteTextField(0, kernel.getApplication(), false));
		this.kernel = kernel;
		app = kernel.getApplication();
		textField = (AutoCompleteTextField) editorComponent;

		editorComponent.addKeyListener(new SpreadsheetCellEditorKeyListener(false));		
		editorComponent.addFocusListener(this);




		DocumentListener documentListener = new DocumentListener() {
			public void changedUpdate(DocumentEvent documentEvent) {
				// do nothing
			}
			public void insertUpdate(DocumentEvent documentEvent) {
				updateFormulaBar(documentEvent);
			}
			public void removeUpdate(DocumentEvent documentEvent) {
				updateFormulaBar(documentEvent);
			}
			private void updateFormulaBar(DocumentEvent documentEvent) {
				if(table.view.getShowFormulaBar() && (textField.hasFocus() || table.isDragging2))
					table.view.getFormulaBar().setEditorText(textField.getText());
			}
		};
		textField.getDocument().addDocumentListener(documentListener);


	}



	public void setText(String text){
		if(!textField.hasFocus() && !table.isDragging2)
			textField.setText(text);

	}

	@Override
	public Component getTableCellEditorComponent(JTable table0, Object value0,
			boolean isSelected, int row0, int column0) {

		table = (MyTable) table0;

		if (value0 instanceof String) { // clicked to type
			value = null;
		} else {
			value = (GeoElement) value0;
		}

		column = column0;
		row = row0;
		String text = "";

		if (value != null) {		
			text = getEditorInitString(value);
			int index = text.indexOf("=");
			if ((!value.isGeoText())) {
				if (index == -1) {
					text = "=" + text;
				}
			}
		}
		delegate.setValue(text);

		Component component = getComponent();
		component.setFont(app.getFontCanDisplay(text));

		editing = true;	

		return component;
	}

	/**
	 * set flag to require text start with "="
	 * to activate autocomplete
	 */
	public void setEqualsRequired(boolean equalsRequired){
		textField.setEqualsRequired(equalsRequired);
	}

	/**
	 * returns flag that requires text start with "="
	 * to activate autocomplete
	 */
	public boolean isEqualsRequired(){
		return textField.isEqualsRequired();
	}

	public void setLabels() {
		textField.setDictionary(app.getCommandDictionary());	
	}


	//=======================================================
	//             In-cell Editing Methods
	//=======================================================


	/**
	 * Returns the definition of geo used to init the editor
	 * when editing is started.
	 * 
	 * @param geo
	 */
	public String getEditorInitString(GeoElement geo) {
		return geo.getRedefineString(true, false);
	}


	public boolean isEditing() {
		return editing;
	}



	public int getCaretPosition() {
		return textField.getCaretPosition();
	}


	/** Insert a geo label into current editor string. */
	public void addLabel(String label) {
		if (!editing)
			return;
		//String text = (String) delegate.getCellEditorValue();			
		//delegate.setValue(text + label);
		textField.replaceSelection(" " + label + " ");
	}


	public void setLabel(String text) {
		if (!editing)
			return;
		delegate.setValue(text);
	}

	public String getEditingValue() {
		return (String) delegate.getCellEditorValue();
	}

	@Override
	public Object getCellEditorValue() {
		return value;
	}






	//=======================================================
	//             Stop/Cancel Editing
	//=======================================================


	@Override
	public void cancelCellEditing() {
		editing = false;
		errorOnStopEditing = false;

		super.cancelCellEditing();				
	}


	@Override
	public boolean stopCellEditing() {

		errorOnStopEditing = true; //flag to handle column resizing during editing (see focusLost method)


		// try to redefine or create the cell geo  with the current editing string 
		if(!processGeo())
			return false;

		errorOnStopEditing = false;
		editing = false;
		boolean success = super.stopCellEditing();

		return success;
	}


	boolean stopCellEditing(int colOff, int rowOff) {
		allowProcessGeo = true;
		boolean success = stopCellEditing();
		moveSelectedCell(colOff, rowOff);
		allowProcessGeo = false;
		return success;
	}


	private void moveSelectedCell(int colOff, int rowOff) {
		int nextRow = Math.min(row + rowOff, table.getRowCount()-1);
		int nextColumn = Math.min(column + colOff, table.getColumnCount()-1);
		table.setSelection(nextColumn, nextRow);
	}


	/**
	 * Attempts to create or redefine the cell geo using the current editing string	
	 * @return
	 */
	private boolean processGeo(){

		try {

			if(allowProcessGeo){
				String text = (String) delegate.getCellEditorValue();	
				// get GeoElement of current cell
				value = kernel.lookupLabel(  GeoElement.getSpreadsheetCellName(column, row), false);

				if (text.equals("")) {
					if (value != null){
						value.removeOrSetUndefinedIfHasFixedDescendent();
						value = null;	
					}

				} else {
					GeoElement newVal = RelativeCopy.prepareAddingValueToTableNoStoringUndoInfo(kernel, table, text, value, column, row);
					if (newVal == null) {
						return false;
					}
					value = newVal;
				}

				if (value != null)
					app.storeUndoInfo();
			}

		} catch (Exception ex) {
			// show GeoGebra error dialog
			// kernel.getApplication().showError(ex.getMessage());
			ex.printStackTrace();
			super.stopCellEditing();
			editing = false;
			return false;
		}
		return true;
	}


	//=======================================================
	//             Key and Focus Listeners
	//=======================================================

	// keep track of when <tab> was first pressed
	// so we can return to that column when <enter> pressed
	public static int tabReturnCol = -1;

	public class SpreadsheetCellEditorKeyListener implements KeyListener {


		boolean escape = false;
		boolean isFormulaBarListener;

		public SpreadsheetCellEditorKeyListener(boolean isFormulaBarListener){
			this.isFormulaBarListener = isFormulaBarListener;
		}

		public void keyTyped(KeyEvent e) {	
		}

		public void keyPressed(KeyEvent e) {
			checkCursorKeys(e);

			escape = false;
			int keyCode = e.getKeyCode();
			switch (keyCode) {
			case KeyEvent.VK_ESCAPE:
				escape = true;
				GeoElement oldGeo = kernel.getGeoAt(column, row);
				cancelCellEditing();

				// restore old text in spreadsheet
				table.getModel().setValueAt(oldGeo, row, column);

				//stopCellEditing(0,0);
				// force nice redraw
				table.setSelection(column, row);

				// update the formula bar after escape
				table.getView().updateFormulaBar();

				break;

			}	
		}

		public void keyReleased(KeyEvent e) {

		}


		public void checkCursorKeys(KeyEvent e) {

			String text = (String) delegate.getCellEditorValue();

			int keyCode = e.getKeyCode();
			//Application.debug(e+"");

			switch (keyCode) {
			case KeyEvent.VK_UP:
				if(isFormulaBarListener) return;

				//Application.debug("UP");
				stopCellEditing(0,-1);		
				editing = false;
				e.consume();
				tabReturnCol = -1;
				break;


			case KeyEvent.VK_TAB:
				if(isFormulaBarListener) return;

				//Application.debug("RIGHT");
				// shift-tab moves left
				// tab moves right
				if (tabReturnCol == -1) tabReturnCol = column;
				stopCellEditing(e.isShiftDown() ? -1 : 1,0);		
				editing = false;

				break;

			case KeyEvent.VK_ENTER:				
				// if incomplete command entered, want to move the cursor to between []
				int bracketsIndex = text.indexOf("[]");
				if (bracketsIndex == -1) {

					if (tabReturnCol != -1) {
						int colOffset = tabReturnCol - column;
						stopCellEditing(colOffset,1);		
						editing = false;
					}else{
						stopCellEditing(0,1);	
					}
				} else {
					textField.setCaretPosition(bracketsIndex + 1);
					e.consume();
				}				

				tabReturnCol = -1;
				break;

			case KeyEvent.VK_DOWN:
				if(isFormulaBarListener){
					e.consume();
					return;
				}
				//Application.debug("DOWN");
				stopCellEditing(0,1);		
				editing = false;
				tabReturnCol = -1;
				break;


			case KeyEvent.VK_LEFT:
				if(isFormulaBarListener) return;
				//Application.debug("LEFT");
				// Allow left/right keys to exit cell for easier data entry
				if(getCaretPosition()==0)
				{
					stopCellEditing(-1, 0);
					editing=false;
				}
				editing = false;
				tabReturnCol = -1;
				break;


			case KeyEvent.VK_RIGHT:
				if(isFormulaBarListener) return;
				//Application.debug("RIGHT");	
				// Allow left/right keys to exit cell for easier data entry
				if(getCaretPosition()==text.length())
				{
					stopCellEditing(1, 0);
					editing=false;
				}

				editing = false;	
				tabReturnCol = -1;
				break;

			case KeyEvent.VK_PAGE_DOWN:
			case KeyEvent.VK_PAGE_UP:
				e.consume();
				tabReturnCol = -1;
				break;

				// An F1 keypress causes the focus to be lost, so we
				// need to set 'editing' to false to prevent the focusLost() 
				// method from calling stopCellEditing()
			case KeyEvent.VK_F1:
				editing = false;	
				break;

			}

		}

	}



	public void focusGained(FocusEvent arg0) {
		editing = true;
	}

	public void focusLost(FocusEvent arg0) {	

		// VirtualKeyboard gets the focus very briefly when opened
		// so ignore this!
		if (arg0.getOppositeComponent() instanceof VirtualKeyboard)
			return;

		// only needed if eg columns resized
		if (editing == true) {
			if (!errorOnStopEditing) {
				// this stops editing but does not process geos ... needed for formula bar sync
				stopCellEditing();				
			} else if (!app.isErrorDialogShowing()) {
				cancelCellEditing();
			}
		}
	}








	//*********************************************************************
	//     old code, has been moved to RelativeCopy   (except UndoEdit)
	//*********************************************************************


	// This method is never called, do we still need it? G.Sturr 2010-6-4
	private void undoEdit() {
		String text = "";

		if (value != null) {
			/*
			if (value.isChangeable()) {
				text = value.toValueString();
			} else {
				text = value.getCommandDescription();
			} */
			text = value.getRedefineString(true, false);
			if ((!value.isGeoText()) && text.indexOf("=") == -1) {
				text = "=" + text;
			}
		}
		delegate.setValue(text);
		super.stopCellEditing();
	}





	/*
	private static GeoElement prepareNewValue(Kernel kernel, String name,
			String text) throws Exception {
		if (text == null)
			return null;



		// remove leading equal sign, e.g. "= A1 + A2"
		if (text.startsWith("=")) {
			text = text.substring(1);
		}
		text = text.trim();

		// no equal sign in input
		GeoElement[] newValues = null;
		try {
			// check if input is same as name: circular definition
			if (text.equals(name)) {
				// circular definition
				throw new CircularDefinitionException();
			}

			// evaluate input text
			newValues = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(text, false);

			// check if text was the label of an existing geo 
			if (text.equals(newValues[0].getLabel())) {
				// make sure we create a copy of this existing or auto-created geo 
				// by providing the new cell name in the beginning
				text = name + " = " + text;		
				newValues = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(text, false);
			}

			// check if name was auto-created: if yes we could have a circular definition
			GeoElement autoCreateGeo = kernel.lookupLabel(name);		
			if (autoCreateGeo != null) {				
				// check for circular definition: if newValue depends on autoCreateGeo
				boolean circularDefinition = false;
				for (int i=0; i < newValues.length; i++) {
					if (newValues[i].isChildOf(autoCreateGeo)) {
						circularDefinition = true;
						break;
					}
				}

				if (circularDefinition) {
					// remove the auto-created object and the result
					autoCreateGeo.remove();
					newValues[0].remove();

					// circular definition
					throw new CircularDefinitionException();
				}
			}

			for (int i=0; i < newValues.length; i++) {
				newValues[i].setAuxiliaryObject(true);
				if (newValues[i].isGeoText())
					newValues[i].setEuclidianVisible(false);
			}

			GeoElement.setLabels(name, newValues); // set names to be D1, E1,
													// F1, etc for multiple
													// objects			
		} 
		catch (CircularDefinitionException ce) {
			// circular definition
			kernel.getApplication().showError("CircularDefinition");
			return null;
		}
		catch (Exception e) {
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
			newValue.setAllVisualProperties(oldValue, true);
			if (oldValue.isAuxiliaryObject())
				newValue.setAuxiliaryObject(true);

			// Application.debug("GeoClassType = " +
			// newValue.getGeoClassType()+" " + newValue.getGeoClassType());
			if (newValue.getGeoClassType() == oldValue.getGeoClassType()) {
				// newValue.setVisualStyle(oldValue);
			} else {
				kernel.getApplication().refreshViews();
			}
		} 
		catch (CircularDefinitionException cde) {			
			kernel.getApplication().showError("CircularDefinition");
			return null;
		}
		catch (Throwable e) {
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

		// make sure that text can be changed to something else
		// eg (2,3 can be overwritten as (2,3)
		if (oldValue != null && oldValue.isGeoText() && !oldValue.hasChildren()) {
			oldValue.remove();
			oldValue = null;
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

	 */






}
