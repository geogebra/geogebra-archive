package geogebra.gui.util;

import geogebra.gui.TextInputDialog;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;

public class LatexTable extends SelectionTable implements MenuElement{


	private Application app;
	private TextInputDialog inputDialog;
	private String[] latexArray;
	private PopupMenuButton popupButton;
	private int caretPosition = 0;

	public LatexTable(Application app, TextInputDialog textInputDialog, PopupMenuButton popupButton, 
			String[] latexArray, int rows, int columns, int mode ){

		super(app, latexArray, rows,columns, new Dimension(24,24), mode);
		this.app = app;
		this.inputDialog = textInputDialog;
		this.latexArray = latexArray;
		this.popupButton = popupButton;
		//setShowGrid(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setSelectedIndex(0);
		//	this.setUseColorSwatchBorder(true);
		this.setShowGrid(true);
		this.setGridColor(MyTable.TABLE_GRID_COLOR);
		this.setBorder(BorderFactory.createLineBorder(MyTable.TABLE_GRID_COLOR));
		//this.setBorder(BorderFactory.createEmptyBorder());
		this.setShowSelection(false);
	}

	public void setCaretPosition(int caretPosition){
		this.caretPosition = caretPosition;
	}
	 
	
	
	// support for MenuElement interface

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

		if(this.getSelectedIndex() >= latexArray.length) return;

		if (arg0.getID()==MouseEvent.MOUSE_RELEASED){
			StringBuffer sb = new StringBuffer(latexArray[this.getSelectedIndex()]);
			String selText = ((InputPanel)inputDialog.getInputPanel()).getSelectedText();		
			if (selText != null) {
				sb.deleteCharAt(sb.indexOf("{")+1);
				sb.insert(sb.indexOf("{")+1, selText);
			}

			inputDialog.insertString(sb.toString());
			//inputDialog.setRelativeCaretPosition(caretPosition);
			popupButton.handlePopupActionEvent();
		}	
	}

}
