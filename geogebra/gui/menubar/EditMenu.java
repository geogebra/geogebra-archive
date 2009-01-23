package geogebra.gui.menubar;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import geogebra.gui.util.ImageSelection;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

/**
 * The "Edit" menu.
 */
public class EditMenu extends BaseMenu {
	private static final long serialVersionUID = -2649808771324470803L;

	private AbstractAction
		deleteAction,
		drawingPadToClipboardAction,
		propertiesAction,
		selectAllAction,
		selectAllAncestorsAction,
		selectAllDescendantsAction,
		selectCurrentLayerAction
	;
	
	public EditMenu(Application app) {
		super(app, app.getMenu("Edit"));

		initActions();
		initItems();
		
		update();
	}
	
	/**
	 * Initialize the items.
	 */
	private void initItems()
	{
		JMenuItem mi;
		
		if (app.isUndoActive()) {
			mi = add(app.getGuiManager().getUndoAction());
			setMenuShortCutAccelerator(mi, 'Z');
			mi = add(app.getGuiManager().getRedoAction());
			if (Application.MAC_OS)
				// Command-Shift-Z
				setMenuShortCutShiftAccelerator(mi, 'Z');
			else
				// Ctrl-Y
				setMenuShortCutAccelerator(mi, 'Y');
			addSeparator();
		}

		// Michael Borcherds 2008-03-03 added to Edit menu
		mi = add(drawingPadToClipboardAction);
		setMenuShortCutShiftAccelerator(mi, 'C');

		// mi = add(DataFromClipboardAction);
		// setMenuShortCutAccelerator(mi, 'V');

		if (app.letDelete()) {
			mi = add(deleteAction);

			if (Application.MAC_OS) {
				mi.setAccelerator(KeyStroke.getKeyStroke(
						KeyEvent.VK_BACK_SPACE, 0));
			} else {
				mi
						.setAccelerator(KeyStroke.getKeyStroke(
								KeyEvent.VK_DELETE, 0));
			}
		}
		addSeparator();

		mi = add(selectAllAction);
		setMenuShortCutAccelerator(mi, 'A');

		mi = add(selectCurrentLayerAction);
		setMenuShortCutAccelerator(mi, 'L');

		mi = add(selectAllDescendantsAction);
		setMenuShortCutShiftAccelerator(mi, 'Q');

		mi = add(selectAllAncestorsAction);
		setMenuShortCutAccelerator(mi, 'Q');

		addSeparator();

		mi = add(propertiesAction);
		setMenuShortCutAccelerator(mi, 'E');
	}
	
	/**
	 * Initialize the actions.
	 */
	private void initActions()
	{
		propertiesAction = new AbstractAction(app.getPlain("Properties")
				+ " ...", app.getImageIcon("document-properties.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showPropertiesDialog();
			}
		};

		drawingPadToClipboardAction = new AbstractAction(app
				.getMenu("DrawingPadToClipboard"), app
				.getImageIcon("edit-copy.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.clearSelectedGeos();

				Thread runner = new Thread() {
					public void run() {
						app.setWaitCursor();
						// copy drawing pad to the system clipboard
						Image img = app.getEuclidianView().getExportImage(1d);
						ImageSelection imgSel = new ImageSelection(img);
						Toolkit.getDefaultToolkit().getSystemClipboard()
								.setContents(imgSel, null);
						app.setDefaultCursor();
					}
				};
				runner.start();
			}
		};

		selectAllAction = new AbstractAction(app.getMenu("SelectAll"), app
				.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.selectAll(-1); // Michael Borcherds 2008-03-03 pass "-1" to
									// select all
			}
		};

		selectCurrentLayerAction = new AbstractAction(app
				.getMenu("SelectCurrentLayer"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				int layer = getSelectedLayer();
				if (layer != -1)
					app.selectAll(layer); // select all objects in layer

			}
		};

		selectAllAncestorsAction = new AbstractAction(app
				.getMenu("SelectAncestors"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				app.selectAllPredecessors();
			}
		};

		selectAllDescendantsAction = new AbstractAction(app
				.getMenu("SelectDescendants"), app.getEmptyIcon()) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {

				app.selectAllDescendants();
			}
		};

		deleteAction = new AbstractAction(app.getPlain("Delete"), app
				.getImageIcon("delete_small.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.deleteSelectedObjects();
			}
		};
	}

	/*
	 * Michael Borcherds 2008-03-03 return -1 if nothing selected return -2 if
	 * objects from more than one layer selected return layer number if objects
	 * from exactly one layer are selected
	 */
	private int getSelectedLayer() {
		Object[] geos = app.getSelectedGeos().toArray();
		if (geos.length == 0)
			return -1; // return -1 if nothing selected

		int layer = ((GeoElement) geos[0]).getLayer();

		for (int i = 1; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (geo.getLayer() != layer)
				return -2; // return -2 if more than one layer selected
		}
		return layer;
	}
	
	@Override
	public void update() {
		updateSelection();
		
		// TODO update labels
	}
	
	/**
	 * Called if the user changes the selected items.
	 */
	public void updateSelection() {
		// Michael Borcherds 2008-03-03 BEGIN
		// boolean haveSelection = !app.getSelectedGeos().isEmpty();
		// deleteAction.setEnabled(haveSelection);
		int layer = getSelectedLayer();
		deleteAction.setEnabled(layer != -1); // -1 means nothing selected, -2
												// means different layers
												// selected
		selectCurrentLayerAction.setEnabled(getSelectedLayer() >= 0); // exactly
																		// one
																		// layer
																		// selected
		// Michael Borcherds 2008-03-03 END
		boolean haveSelection = !app.getSelectedGeos().isEmpty();
		selectAllDescendantsAction.setEnabled(haveSelection);
		selectAllAncestorsAction.setEnabled(haveSelection);

		Kernel kernel = app.getKernel();
		propertiesAction.setEnabled(!kernel.isEmpty());
		selectAllAction.setEnabled(!kernel.isEmpty());
	}
}
