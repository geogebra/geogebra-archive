package geogebra.gui;


import geogebra.gui.DefaultGuiManager.NumberInputHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class InputDialogOpenURL extends InputDialog{
	
	
	public InputDialogOpenURL(Application app) {
		super(app, app.getPlain("EnterURL"), app.getMenu("LoadURL"), "", false, null);
		
	}

	/**
	 * Handles button clicks for dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
					setVisible(!processInput());
				} else if (source == btApply) {
					processInput();
				} else if (source == btCancel) {
					setVisible(false);
			} 
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			setVisible(false);
		}
	}
	
	private boolean processInput() {
		

			return app.getGuiManager().loadURL(inputPanel.getText());
		
	}


}
