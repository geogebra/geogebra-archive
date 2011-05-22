package geogebra.gui.view.algebra;


import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.main.Application;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.text.JTextComponent;

/**
 * Transfer handler for InputBar
 * @author gsturr
 *
 */
public class AlgebraInputTransferHandler extends TransferHandler implements Transferable {

	private Application app;
	private JTextComponent ta;

	// supported data flavors
	private static final DataFlavor supportedFlavors[] = { 
		DataFlavor.javaFileListFlavor,
		DataFlavor.stringFlavor,
		AlgebraViewTransferHandler.algebraViewFlavor };

	private boolean debug  = false;

	private String text;


	/****************************************
	 * Constructor
	 * @param ev
	 */
	public AlgebraInputTransferHandler(Application app, JTextComponent ta){
		this.ta = ta;
		this.app = app;
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

		// handle text
		if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
				|| t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)) {
			try {

				// get text for algebra processor
				if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)){
					String label = (String) t.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);
					GeoElement geo = app.getKernel().lookupLabel(label);
					if(geo != null)
						text = geo.getDefinitionForInputBar();
				}else{
					text = (String) t.getTransferData(DataFlavor.stringFlavor);
				}
				ta.setText(text);
				return true;

			} catch (UnsupportedFlavorException ignored) {
			} catch (IOException ignored) {
			}
		}

		// handle potential ggb file drop
		app.getGuiManager().handleGGBFileDrop(t);
		
		return false;
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




