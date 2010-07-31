package geogebra.gui;


import geogebra.gui.GuiManager.NumberInputHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.event.ActionEvent;

public class InputDialogRegularPolygon extends InputDialog{
	
	private GeoPoint geoPoint1, geoPoint2;

	private Kernel kernel;
	
	public InputDialogRegularPolygon(Application app, String title, InputHandler handler, GeoPoint point1, GeoPoint point2, Kernel kernel) {
		super(app, app.getPlain("Points"), title, "4", false, handler);
		
		geoPoint1 = point1;
		geoPoint2 = point2;
		this.kernel = kernel;

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
		
		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		boolean ret = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);
		
		if (ret) 
			kernel.RegularPolygon(null, geoPoint1, geoPoint2, ((NumberInputHandler)inputHandler).num);
		

		return ret;
		
	}


}
