package geogebra.gui;


import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class InputDialogOpenURL extends InputDialog{
	
	
	public InputDialogOpenURL(Application app) {
		super(app.getFrame(), false);
		this.app = app;	
		
		initString = "http://";
		
		
		// check if there's a string starting http:// already on the clipboard
		// (quite likely!!)
		String clipboardString = app.getStringFromClipboard();
		if (clipboardString != null && clipboardString.startsWith("http://"))
			initString = clipboardString;

		createGUI(app.getMenu("LoadURL"), app.getMenu("EnterAppletAddress"), false, DEFAULT_COLUMNS, 1, false, false, true, false, false, false);
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
