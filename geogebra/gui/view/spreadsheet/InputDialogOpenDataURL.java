package geogebra.gui.view.spreadsheet;

import geogebra.gui.InputDialog;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * modified version of gui.InputDialogOpenURL
 * 
 *  G.Sturr 2010-2-12
 *
 */
public class InputDialogOpenDataURL extends InputDialog{
	
	private SpreadsheetView view;
	
	public InputDialogOpenDataURL(Application app, SpreadsheetView view) {
		super(app.getFrame(), false);
		this.app = app;	
		this.view = view;
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
		boolean succ = false;
		
		URL url;
		try {
			url = new URL(inputPanel.getText());
			succ = view.loadSpreadsheetFromURL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return succ;
		
	}


}
