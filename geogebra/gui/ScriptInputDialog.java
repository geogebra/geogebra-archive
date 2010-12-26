/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.editor.GeoGebraEditorPane;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoButton;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Input dialog for GeoText objects with additional option
 * to set a "LaTeX formula" flag
 * 
 * @author hohenwarter
 */
public class ScriptInputDialog extends InputDialog {
	
	private static final long serialVersionUID = 1L;

	private GeoElement button;
	private boolean global = false;
	private boolean javaScript = false;
	
	/**
	 * Input Dialog for a GeoButton object
	 */
	public ScriptInputDialog(Application app,  String title, GeoButton button,
								int cols, int rows, boolean javaScript) {	
		super(app.getFrame(), false);
		this.app = app;
		this.javaScript = javaScript;
		inputHandler = new TextInputHandler();
				
				
		createGUI(title, "", false, cols, rows, true, true, false, true, false, false);		
		
		// init dialog using text
		setGeo(button);
		
		JPanel centerPanel = new JPanel(new BorderLayout());		
							
			
		centerPanel.add(inputPanel, BorderLayout.CENTER);		

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		if (javaScript) {
			((GeoGebraEditorPane) inputPanel.getTextComponent()).setEditorKit("javascript");
		}
		
		centerOnScreen();		
	}
	
	public void setGeo(GeoElement button) {
		
		if (global) {
			setGlobal();
			return;
		}
		this.button = button;
		
		if (button != null)
			inputPanel.setText(javaScript ? button.getJavaScript() : button.getScript());
	}
	
	/*
	 * edit global javascript
	 */
	public void setGlobal() {
		this.button = null;
		global = true;

        inputPanel.setText(app.getKernel().getLibraryJavaScript());
	}
	
	
	public JPanel getInputPanel() {
		return inputPanel;
	}
	
	public JButton getApplyButton() {
		return btApply;
	}
	
	
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
				inputText = inputPanel.getText();
				
				boolean finished = inputHandler.processInput(inputText);	
				if (isShowing()) {	
					// text dialog window is used and open
					setVisible(!finished);
				} else {		
					// text input field embedded in properties window
					setGeo(button);
				}
			} 
			else if (source == btCancel) {
				if (isShowing())
					setVisible(false);		
				else {
					setGeo(button);
				}
			}
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			ex.printStackTrace();
		}			
	}
	
	/**
	 * Inserts geo into text and creates the string for a dynamic text, e.g.
	 * "Length of a = " + a + "cm"
	 * @param geo
	 */
	public void insertGeoElement(GeoElement geo) {
		Application.debug("TODO: unimplemented");
	}
	
	
	private class TextInputHandler implements InputHandler {
		
		private Kernel kernel;
       
        private TextInputHandler() { 
        	kernel = app.getKernel();
        }        
        
        public boolean processInput(String inputValue) {
            if (inputValue == null) return false;                        
          
        
            if (global) {
            	app.getKernel().setLibraryJavaScript(inputValue);
            	return true;
            }

            if (button == null) {
            	button = new GeoButton(kernel.getConstruction());
            
            }
                    
            // change existing text
            	if (javaScript)
            		button.setJavaScript(inputValue);
            	else
            		button.setScript(inputValue);
            	
            	return true;
    }

}
}
