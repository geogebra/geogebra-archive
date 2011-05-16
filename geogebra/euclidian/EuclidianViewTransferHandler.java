package geogebra.euclidian;


import geogebra.gui.GuiManager;
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

import javax.swing.JComponent;
import javax.swing.TransferHandler;


public class EuclidianViewTransferHandler extends TransferHandler implements Transferable {

	private EuclidianView ev;

	private static final DataFlavor supportedFlavors[] = { 
		DataFlavor.imageFlavor,
		DataFlavor.stringFlavor,
		DataFlavor.javaFileListFlavor,
		AlgebraViewTransferHandler.algebraViewFlavor };

	private boolean debug  = false;
	private Image image;
	

	public EuclidianViewTransferHandler(EuclidianView ev){
		this.ev = ev;
	}


	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

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



	public Transferable createTransferable(JComponent comp) {
		return null;
	}

	public boolean importData(JComponent comp, Transferable t) {


		
		// handle image file
		if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || 
				t.isDataFlavorSupported(GuiManager.getUriListFlavor())){

			if (debug) System.out.println("dropped image: " + t.toString());
			 
			Construction cons = ev.getApplication().getKernel().getConstruction();
			Point p = ev.getMousePosition();
			GeoPoint gp = new GeoPoint(cons);
			gp.setCoords(ev.toRealWorldCoordX(p.x), ev.toRealWorldCoordY(p.y), 1.0);
			
			if(ev.getApplication().getGuiManager().loadImage(gp, t, false))
				return true;	
		}



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
		else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
				|| t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {
			try {
				
				// get text for algebra processor
				String text;
				if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)){
					text = (String) t.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);
					text = "FormulaText[" + text + "]";
				}else{
					text = (String) t.getTransferData(DataFlavor.stringFlavor);
					text = "\"" + text + "\"";
				}
				if (debug) System.out.println("dropped geo: " + text);
				
				// convert text to geo
				GeoElement[] ret = ev.getApplication().getKernel().getAlgebraProcessor()
				.processAlgebraCommand(text, true);

				if (ret != null && ret[0].isTextValue()) {
					GeoText geo = (GeoText) ret[0];
					Point p = ev.getMousePosition();
					geo.setRealWorldLoc(ev.toRealWorldCoordX(p.x), ev.toRealWorldCoordY(p.y));
					geo.addView(this);
					geo.updateRepaint();
				}

				return true;

			} catch (UnsupportedFlavorException ignored) {
			} catch (IOException ignored) {
			}
		}

		return false;
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

