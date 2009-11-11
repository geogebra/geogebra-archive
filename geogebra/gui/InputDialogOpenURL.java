package geogebra.gui;


import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class InputDialogOpenURL extends InputDialog{
	
	
	public InputDialogOpenURL(Application app) {
		super(app.getFrame(), false);
		this.app = app;	
		
		initString = "http://";

		createGUI(app.getMenu("LoadURL"), app.getPlain("EnterURL"), false, DEFAULT_COLUMNS, 1, false, false, true, false, false, false);
		optionPane.add(inputPanel, BorderLayout.CENTER);		
		centerOnScreen();
		
		inputPanel.selectText();
		
	}

	public void setLabels(String title) {
		setTitle(title);
		
		btOK.setText(app.getPlain("Open"));
		btCancel.setText(app.getPlain("Cancel"));

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
