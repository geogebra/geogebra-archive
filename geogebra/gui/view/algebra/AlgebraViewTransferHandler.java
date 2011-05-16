package geogebra.gui.view.algebra;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.JComponent;
import javax.swing.TransferHandler;


public class AlgebraViewTransferHandler extends TransferHandler implements Transferable {

	public static DataFlavor algebraViewFlavor = new DataFlavor(AlgebraView.class, "geoLabel");
	private static final DataFlavor supportedFlavors[] = { algebraViewFlavor };

	private String geoLabel;
	
	
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	public boolean canImport(JComponent comp, DataFlavor flavor[]) {	
		return false;
	}

	public Transferable createTransferable(JComponent comp) {
		
		geoLabel = null;
		if (comp instanceof AlgebraView) {
			geoLabel = ((AlgebraView)comp).getSelectedGeoElement().getLabel();
				return this;
		}
		return null;
	}

	public boolean importData(JComponent comp, Transferable t) {
		return false;
	}

	
	public Object getTransferData(DataFlavor flavor) {
		if (isDataFlavorSupported(flavor)) {
			return geoLabel;
		}
		return null;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return supportedFlavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for(int i = 0; i < supportedFlavors.length; i++){
			System.out.println(flavor.getMimeType());
			System.out.println(supportedFlavors[i].getMimeType());
			System.out.println("------------");
			if (supportedFlavors[i].equals(flavor))
				return true;
		}
		return false;
	}
}

