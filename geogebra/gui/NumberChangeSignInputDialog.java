package geogebra.gui;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import geogebra.gui.GuiManager.NumberChangeSignInputHandler;
import geogebra.main.Application;

/**
 * InputDialog with checkbox to change sign
 * @author mathieu
 *
 */
public class NumberChangeSignInputDialog extends InputDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private boolean changingSign;
	

	/**
	 * 
	 * @param app
	 * @param message
	 * @param title
	 * @param initString
	 * @param handler
	 * @param changingSign says if the sign has to be changed
	 */
	public NumberChangeSignInputDialog(Application app, String message,
			String title, String initString, 
			NumberChangeSignInputHandler handler, 
			boolean changingSign, String checkBoxText) {
		super(app, message, title, initString, false, 
				handler, true,
				false, null,
				new JCheckBox(checkBoxText,true));
		
		this.changingSign=changingSign;
		
	}
	
	
	protected boolean processInputHandler(){
		return ((NumberChangeSignInputHandler) inputHandler).processInput(inputText,changingSign && checkBox.isSelected());
	}
	

	
	protected void createBtPanel(boolean showApply){
		btPanel.add(checkBox);
		super.createBtPanel(showApply);
	}
	

}
