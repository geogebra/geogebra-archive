package geogebra.gui;


import geogebra.gui.GuiManager.NumberInputHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

/**
 * abstract class for input radius for any circle
 * @author mathieu
 *
 */
public abstract class InputDialogRadius extends InputDialog{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** current kernel */
	protected Kernel kernel;
	
	/**
	 * 
	 * @param app
	 * @param title
	 * @param handler
	 * @param kernel
	 */
	public InputDialogRadius(Application app, String title, InputHandler handler, Kernel kernel) {
		super(app, app.getPlain("Radius"), title, "", false, handler);
		
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
			GeoElement circle = createOutput(((NumberInputHandler)inputHandler).getNum());
			GeoElement[] geos = { circle };
			kernel.getApplication().getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(geos);
		}

		return ret;
		
	}
	
	/**
	 * 
	 * @param num
	 * @return the circle
	 */
	abstract protected GeoElement createOutput(NumberValue num);
	

	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}
}
