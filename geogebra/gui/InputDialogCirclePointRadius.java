package geogebra.gui;


import geogebra.gui.GuiManager.NumberInputHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class InputDialogCirclePointRadius extends InputDialog{
	
	private GeoPoint geoPoint1;

	private Kernel kernel;
	
	public InputDialogCirclePointRadius(Application app, String title, InputHandler handler, GeoPoint point1, Kernel kernel) {
		super(app, app.getPlain("Radius"), title, "", false, handler);
		
		geoPoint1 = point1;
		this.kernel = kernel;

	}

	/**
	 * Handles button clicks for dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		try {
			if (source == btOK || source == inputPanel.getTextComponent()) {
					setVisibleForTools(!processInput());
				} else if (source == btApply) {
					processInput();
				} else if (source == btCancel) {
					setVisibleForTools(false);
			} 
		} catch (Exception ex) {
			// do nothing on uninitializedValue		
			setVisibleForTools(false);
		}
	}
	
	private boolean processInput() {
		
		// avoid labeling of num
		Construction cons = kernel.getConstruction();
		boolean oldVal = cons.isSuppressLabelsActive();
		cons.setSuppressLabelCreation(true);

		boolean ret = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);
		
		if (ret) {
			GeoElement circle = kernel.Circle(null, geoPoint1, ((NumberInputHandler)inputHandler).getNum());
			GeoElement[] geos = { circle };
			kernel.getApplication().getActiveEuclidianView().getEuclidianController().selectGeos(geos);
		}

		return ret;
		
	}

	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}
}
