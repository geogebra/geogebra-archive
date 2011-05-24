package geogebra.euclidian;


import geogebra.gui.FileDropTargetListener;
import geogebra.gui.GuiManager;
import geogebra.gui.app.GeoGebraFrame;
import geogebra.gui.view.algebra.AlgebraViewTransferHandler;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoText;
import geogebra.main.Application;

import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Transfer handler for Euclidian Views
 * @author gsturr
 *
 */
public class EuclidianViewTransferHandler extends TransferHandler implements Transferable {

	private EuclidianView ev;
	private Application app;

	// supported data flavors
	private static final DataFlavor supportedFlavors[] = { 
		DataFlavor.imageFlavor,
		DataFlavor.stringFlavor,
		DataFlavor.javaFileListFlavor,
		AlgebraViewTransferHandler.algebraViewFlavor };

	private boolean debug  = true;


	/****************************************
	 * Constructor
	 * @param ev
	 */
	public EuclidianViewTransferHandler(EuclidianView ev){
		this.ev = ev;
		this.app = ev.getApplication();
	}


	/**
	 * Ensures that transfer are done in COPY mode
	 */
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	/**
	 * Returns true if any element of the DataFlavor parameter array is a supported flavor.
	 */
	public boolean canImport(JComponent comp, DataFlavor flavor[]) {

		for (int i = 0, n = flavor.length; i < n; i++) {
			for (int j = 0, m = supportedFlavors.length; j < m; j++) {
				if (flavor[i].equals(supportedFlavors[j])) {
					return true;
				}
			}
		}
		return false;
	}



	/**
	 * Handles data import.
	 */
	public boolean importData(JComponent comp, Transferable t) {

		// give the drop target (this EV) the view focus
		requestViewFocus();

		// handle image
		if (t.isDataFlavorSupported(DataFlavor.imageFlavor)){

			if (debug) System.out.println("dropped image: " + t.toString());

			Construction cons = ev.getApplication().getKernel().getConstruction();
			Point p = ev.getMousePosition();
			GeoPoint gp = new GeoPoint(cons);
			gp.setCoords(ev.toRealWorldCoordX(p.x), ev.toRealWorldCoordY(p.y), 1.0);
			if(ev.getApplication().getGuiManager().loadImage(gp, t, false))
				return true;	

		}


		// handle text
		if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
				|| t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {
			try {

				// string for algebra processor
				String text;
				
				// handle algebra view data flavor 
				if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)){

					// get list of selected geo labels
					ArrayList<String> list = (ArrayList<String>) t
					.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);
					
					// exit if empty list
					if(list.size()==0) return false;
					
					// if single geo, create FormulaText
					if(list.size()==1){
						text = "FormulaText[" + list.get(0) + "]";
					}
					
					// if multiple geos, create TableText
					else{
						GeoElement geo;
						text = "TableText[";
						for(int i=0; i<list.size(); i++){
							geo = app.getKernel().lookupLabel(list.get(i));
							
							text += "{\"" + geo.getLabel() + " = \"" + "FormulaText[" + list.get(i) + "]}";
							if(i<list.size()-1){
								text += ",";
							}
						}
						text += "]";
						
					}
					
				// handle text flavor	
				}else{
					text = (String) t.getTransferData(DataFlavor.stringFlavor);
					text = "\"" + text + "\"";
				}
				ev.getEuclidianViewNo();
				if (debug) System.out.println("dropped geo: " + text);

				// convert text to geo
				GeoElement[] ret = ev.getApplication().getKernel().getAlgebraProcessor()
				.processAlgebraCommand(text, true);

				if (ret != null && ret[0].isTextValue()) {
					GeoText geo = (GeoText) ret[0];
					Point p = ev.getMousePosition();
					geo.setRealWorldLoc(ev.toRealWorldCoordX(p.x), ev.toRealWorldCoordY(p.y));
					geo.updateRepaint();
				}

				return true;

			} catch (UnsupportedFlavorException ignored) {
			} catch (IOException ignored) {
			}
		}

		// handle ggb and image files
		if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || 
				t.isDataFlavorSupported(GuiManager.getUriListFlavor())){

			// pass the transferable to the FileDropListener and exit if ggb file 
			if(app.getGuiManager().handleGGBFileDrop(t))
				return false;

			// if it's not a ggb file then try to get an image file
			if (debug) System.out.println("dropped image: " + t.toString());

			Construction cons = ev.getApplication().getKernel().getConstruction();
			Point p = ev.getMousePosition();
			GeoPoint gp = new GeoPoint(cons);
			gp.setCoords(ev.toRealWorldCoordX(p.x), ev.toRealWorldCoordY(p.y), 1.0);

			return (ev.getApplication().getGuiManager().loadImage(gp, t, false));	

		}

		return false;
	}


	/**
	 * Sets the focus to this EV. 
	 * TODO: use this view's id directly to set the focus (current code assumes only 2 EVs)
	 */
	private void requestViewFocus(){
		if(ev.equals(app.getEuclidianView()))
			app.getGuiManager().getLayout().getDockManager().setFocusedPanel(Application.VIEW_EUCLIDIAN);
		else
			app.getGuiManager().getLayout().getDockManager().setFocusedPanel(Application.VIEW_EUCLIDIAN2);
	}



	public Transferable createTransferable(JComponent comp) {
		return null;
	}

	public Object getTransferData(DataFlavor flavor) {
		return null;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for(int i = 0; i < supportedFlavors.length; i++){
			if (supportedFlavors[i].equals(flavor))
				return true;
		}
		return false;
	}
}




