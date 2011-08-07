package geogebra.gui.view.spreadsheet;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.algebra.AlgebraViewTransferHandler;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.ImageIcon;

public class SpreadsheetViewDnD implements DragGestureListener, DragSourceListener, DropTargetListener{

	private Application app;
	private SpreadsheetView view;
	private MyTable table;
	private ArrayList<String> geoLabelList;

	private DragSource ds;
	private DropTarget dt;

	public SpreadsheetViewDnD(Application app, SpreadsheetView view){
		this.app = app;
		this.view = view;
		this.table = view.getTable();

		ds = new DragSource();
		//DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(table, DnDConstants.ACTION_COPY, this);

		dt = new DropTarget(table,this);

	}



	/**
	 * 	Extension of Transferable for exporting AlgegraView selections as a list of Geo labels
	 */
	class TransferableAlgebraView implements Transferable {

		public final DataFlavor algebraViewFlavor = new DataFlavor(AlgebraView.class, "geoLabel list");
		private final DataFlavor supportedFlavors[] = { algebraViewFlavor };

		private ArrayList<String> geoLabelList;

		public TransferableAlgebraView(ArrayList<String> geoLabelList) {
			this.geoLabelList = geoLabelList;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return supportedFlavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			if (flavor.equals(algebraViewFlavor))
				return true;
			return false;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if (flavor.equals(algebraViewFlavor))
				return geoLabelList;
			throw new UnsupportedFlavorException(flavor);
		}
	}



	public void dragGestureRecognized(DragGestureEvent dge) {


		if(!table.isOverDnDRegion) 
			return;


		if(geoLabelList == null)
			geoLabelList = new ArrayList<String>();
		else
			geoLabelList.clear();

		for(GeoElement geo : app.getSelectedGeos()){
			geoLabelList.add(geo.getLabel());
		}

		// if we have something ... do the drag! 
		if(geoLabelList.size() > 0){

			// create drag image 
			StringBuilder sb = new StringBuilder();
			sb.append("\\fbox{\\begin{array}{l}"); 
			for(GeoElement geo:app.getSelectedGeos()){
				sb.append(geo.getLaTeXAlgebraDescription(true));
				sb.append("\\\\");
			}
			sb.append("\\end{array}}");
			ImageIcon ic  = GeoGebraIcon.createLatexIcon(app, sb.toString(), app.getPlainFont(), false, Color.DARK_GRAY, null);

			// start drag
			ds.startDrag(dge, DragSource.DefaultCopyDrop, ic.getImage(), 
					new Point(-5,-ic.getIconHeight()+5), new TransferableAlgebraView(geoLabelList),  this);
		}

	}





	//Drag Source 
	public void dragDropEnd(DragSourceDropEvent e) {}
	public void dragEnter(DragSourceDragEvent e) {}
	public void dragExit(DragSourceEvent e) {}
	public void dragOver(DragSourceDragEvent e) {}
	public void dropActionChanged(DragSourceDragEvent e) {}




	// Drop Target

	public void dragEnter(DropTargetDragEvent dte) {
		System.out.println("drag enter");
		table.setTableMode(table.TABLE_MODE_DROP);

	}

	public void dragExit(DropTargetEvent dte) {
		System.out.println("drag exit");
		table.setTableMode(table.TABLE_MODE_STANDARD);

	}

	Point currentCell = new Point(0,0);

	public void dragOver(DropTargetDragEvent dte) {

		Point overCell = table.getIndexFromPixel(dte.getLocation().x, dte.getLocation().y) ;

		if(!overCell.equals(currentCell)){

			System.out.println(overCell.toString());
			currentCell = overCell;
			//table.setSelection(currentCell.x, currentCell.y);

			table.setTargetcellFrame(table.getCellBlockRect(currentCell.x, currentCell.y, 
					currentCell.x, currentCell.y, true));		
			table.repaint();
		}

	}

	public void drop(DropTargetDropEvent dte) {
		System.out.println("=========== dropped ==============");


		System.out.println(Arrays.toString(dte.getCurrentDataFlavors()));

		String textImport = null;

		dte.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		Transferable t = dte.getTransferable();

		boolean success = table.copyPasteCut.paste(currentCell.x, currentCell.y, currentCell.x, currentCell.y, t);
		dte.dropComplete(success);

		/*
			// handle text
			if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {

				textImport = (String) t.getTransferData(DataFlavor.stringFlavor);
				// ??????
				// dte.dropComplete(textImport != null);			
			}

			// handle algebraView flavor
			else if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)){

				// get list of selected geo labels 
				ArrayList<String> list = (ArrayList<String>) t
				.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);

				// exit if empty list
				if(list.size()==0) {
					//dropEvent.dropComplete(false);
					return;
				}

				// if only one geo, get definition string 
				if(list.size()==1){
					GeoElement geo = app.getKernel().lookupLabel(list.get(0));
					if(geo != null)
						textImport = geo.getDefinitionForInputBar();
					else{
						//dte.dropComplete(false);
						return;
					}
				}

			}
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		System.out.println(textImport);

		 */



		table.setTableMode(table.TABLE_MODE_STANDARD);
	}

	public void dropActionChanged(DropTargetDragEvent dte) {
		// TODO Auto-generated method stub

	}




}
