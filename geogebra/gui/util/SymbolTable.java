package geogebra.gui.util;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import geogebra.util.Unicode;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;

/**
 * Symbols for quick pasting into text fields. See MyTextField.
 * 
 * @author G Sturr
 *
 */
public class SymbolTable extends SelectionTable implements MenuElement{


	private MyTextField inputField;


	public SymbolTable(Application app, MyTextField inputField) {
		super(app, TableSymbols.basicSymbols(app), -1,10, new Dimension(24,24), SelectionTable.MODE_TEXT);
		setShowGrid(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setSelectedIndex(1);
		setFocusable(false);
		setToolTipArray(TableSymbols.basicSymbolsToolTips(app));
		this.inputField = inputField;

	}


	public Component getComponent() {
		return this;
	}

	public MenuElement[] getSubElements() {
		return new MenuElement[0];
	}

	public void menuSelectionChanged(boolean arg0) {
	}

	public void processKeyEvent(KeyEvent arg0, MenuElement[] arg1, MenuSelectionManager arg2) {
	}

	public void processMouseEvent(MouseEvent arg0, MenuElement[] arg1, MenuSelectionManager arg2) {

		if(this.getSelectedIndex() >= TableSymbols.basicSymbols.length) return;

		if (arg0.getID()==MouseEvent.MOUSE_RELEASED){
			inputField.handlePopupSelection();
		}	
	}


}
