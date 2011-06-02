package geogebra.gui;


import geogebra.gui.GuiManager.NumberInputHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.Dilateable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoPolygon;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class InputDialogDilate extends AngleInputDialog {
	
	GeoPolygon[] polys;
	GeoPoint[] points;
	GeoElement[] selGeos;

	private Kernel kernel;
		
	public InputDialogDilate(Application app, String title, InputHandler handler, GeoPolygon[] polys, GeoPoint[] points, GeoElement[] selGeos, Kernel kernel) {
		super(app, app.getPlain("Numeric"), title, "", false, handler, false);
		
		this.polys = polys;
		this.points = points;
		this.selGeos = selGeos;
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

		boolean success = inputHandler.processInput(inputPanel.getText());

		cons.setSuppressLabelCreation(oldVal);
		
		
		
		if (success) {
			NumberValue num = ((NumberInputHandler)inputHandler).getNum();

			if (selGeos.length > 0) {					
				// mirror all selected geos
				//GeoElement [] selGeos = getSelectedGeos();
				GeoPoint point = points[0];
				ArrayList<GeoElement> ret = new ArrayList<GeoElement>();
				for (int i=0; i < selGeos.length; i++) {				
					if (selGeos[i] != point) {
						if (selGeos[i] instanceof Dilateable || selGeos[i].isGeoPolygon())
							ret.addAll(Arrays.asList(kernel.Dilate(null,  selGeos[i], num, point)));
					}
				}
				kernel.getApplication().getActiveEuclidianView().getEuclidianController().memorizeJustCreatedGeos(ret);
				return true;
			}
			
		}

		
		return false;
		
	}

	public void windowGainedFocus(WindowEvent arg0) {
		if (!isModal()) {
			app.setCurrentSelectionListener(null);
		}
		app.getGuiManager().setCurrentTextfield(this, true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
