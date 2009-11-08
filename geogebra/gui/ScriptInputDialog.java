/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/
package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoJavaScriptButton;
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
	
	/**
	 * Input Dialog for a GeoJavaScriptButton object
	 */
	public ScriptInputDialog(Application app,  String title, GeoJavaScriptButton button,
								int cols, int rows) {	
		super(app.getFrame(), false);
		this.app = app;
		inputHandler = new TextInputHandler();
				
				
		createGUI(title, "", false, cols, rows, true, true, false, true, false, false);		
		
		// init dialog using text
		setGeo(button);
		
		JPanel centerPanel = new JPanel(new BorderLayout());		
							
			
		centerPanel.add(inputPanel, BorderLayout.CENTER);		

		getContentPane().add(centerPanel, BorderLayout.CENTER);
		centerOnScreen();		
	}
	
	public void setGeo(GeoElement button) {
		
		if (global) {
			setGlobal();
			return;
		}
		this.button = button;

        inputPanel.setText(button == null ? "TODO" : button.getScript());
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
		Application.debug("unimplemented");
	}
	
	/**
	 * Returns how often the given char is in the given String
	 * @param str
	 * @param ch
	 * @return
	 */
	private int countChar(char ch, String str) {
		if (str == null) return 0;
		
		int count = 0;
		for (int i=0; i < str.length(); i++) {
			if (str.charAt(i) == ch) {
				
				count ++;
			}
		}
		return count;		
	}
	
	private class TextInputHandler implements InputHandler {
		
		private Kernel kernel;
		private EuclidianView euclidianView;
       
        private TextInputHandler() { 
        	kernel = app.getKernel();
        	euclidianView = app.getEuclidianView();
        }        
        
        public boolean processInput(String inputValue) {
            if (inputValue == null) return false;                        
          
        
            if (global) {
            	app.getKernel().setLibraryJavaScript(inputValue);
            	return true;
            }

            if (button == null) {
            	button = new GeoJavaScriptButton(kernel.getConstruction());
            
            }
                    
            // change existing text
            	
            	button.setScript(inputValue);
            	
            	return true;
    }

}
}
