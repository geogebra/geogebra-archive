package geogebra.gui.view.spreadsheet;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.gui.view.algebra.AlgebraViewTransferHandler;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
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

	private boolean isRowOrdered = false;
	private boolean isCopyByValue = true;
	boolean allowDrop = true;
	

	


	public SpreadsheetViewDnD(Application app, SpreadsheetView view){
		this.app = app;
		this.view = view;
		this.table = view.getTable();

		ds = new DragSource();
		//DragGestureRecognizer dgr = ds.createDefaultDragGestureRecognizer(table, DnDConstants.ACTION_COPY, this);

		dt = new DropTarget(table,this);

	}

	
	
	
	//=============================================
	// Getters/setters
	//=============================================
	
	public void setAllowDrop(boolean allowDrop) {
		this.allowDrop = allowDrop;
	}

	public boolean isRowOrdered() {
		return isRowOrdered;
	}

	public boolean isCopyByValue() {
		return isCopyByValue;
	}

	public void setRowOrdered(boolean isRowOrdered) {
		this.isRowOrdered = isRowOrdered;
	}

	public void setCopyByValue(boolean isCopyByValue) {
		this.isCopyByValue = isCopyByValue;
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
	public void dragOver(DragSourceDragEvent e) {
	}
	public void dropActionChanged(DragSourceDragEvent e) {
	}




	// Drop Target

	public void dragEnter(DropTargetDragEvent dte) {
		//System.out.println("drag enter");
		table.setTableMode(table.TABLE_MODE_DROP);

	}

	public void dragExit(DropTargetEvent dte) {
		//System.out.println("drag exit");
		table.setTableMode(table.TABLE_MODE_STANDARD);

	}

	Point currentCell = new Point(0,0);

	public void dragOver(DropTargetDragEvent dte) {

		Point overCell = table.getIndexFromPixel(dte.getLocation().x, dte.getLocation().y) ;

		if(!overCell.equals(currentCell)){

			//System.out.println(overCell.toString());
			currentCell = overCell;
			//table.setSelection(currentCell.x, currentCell.y);

			table.setTargetcellFrame(table.getCellBlockRect(currentCell.x, currentCell.y, 
					currentCell.x, currentCell.y, true));		
			table.repaint();
		}

	}

	public void drop(DropTargetDropEvent dte) {
		//System.out.println("=========== dropped ==============");
		//System.out.println(Arrays.toString(dte.getCurrentDataFlavors()));

		String textImport = null;

		//dte.acceptDrop(DnDConstants.ACTION_LINK);
		

		Transferable t = dte.getTransferable();

		DataFlavor HTMLflavor = null;
		try {
			HTMLflavor = new	DataFlavor("text/html;class=java.lang.String");
		} catch (ClassNotFoundException e1) {
			//e1.printStackTrace();
		}

		// handle String or HTML flavor
		if (t.isDataFlavorSupported(DataFlavor.stringFlavor)
				||t.isDataFlavorSupported(HTMLflavor)){

			boolean success = table.copyPasteCut.paste(currentCell.x, currentCell.y, currentCell.x, currentCell.y, t);
			dte.dropComplete(success);
			return;
		}

		// handle algebraView flavor
		else if (t.isDataFlavorSupported(AlgebraViewTransferHandler.algebraViewFlavor)){

			StringBuilder sb = new StringBuilder();

			try {
				// get list of selected geo labels 
				ArrayList<String> list;
				list = (ArrayList<String>) t.getTransferData(AlgebraViewTransferHandler.algebraViewFlavor);

				// exit if empty list
				if(list.size()==0) {
					dte.dropComplete(false);
					return;
				}

				int rowCount = list.size();

				int columnCount = 1;

				GeoElement[] geoArray = new GeoElement[rowCount];
				for(int i=0; i < geoArray.length; i++){
					GeoElement geo = app.getKernel().lookupLabel(list.get(i));
					if(geo != null){
						geoArray[i] = geo;
						if(geo.isGeoList())
							columnCount = Math.max(columnCount, ((GeoList)geo).size());
					}
				}

				
				
				
				
			//	if(dte.getSourceActions() != DnDConstants.ACTION_COPY_OR_MOVE){
					if(Application.getShiftDown() || Application.getControlDown()){
					DialogCopyToSpreadsheet id = new DialogCopyToSpreadsheet(app,this);
					id.setVisible(true);	
				}

				if(!allowDrop){
					dte.dropComplete(false);
					table.setTableMode(table.TABLE_MODE_STANDARD);
					allowDrop = true;
					return;
				}
				
				
				
				dte.acceptDrop(dte.getDropAction());

				String[][] data = new String[rowCount][columnCount];
				String[][] dataTranspose = new String[columnCount][rowCount];

				if(this.isCopyByValue){	  // create array of geo.toValueStrings
					for(int r = 0; r < rowCount; r ++)
						if(geoArray[r].isGeoList())
							for(int c = 0; c < ((GeoList)geoArray[r]).size(); c ++){
								data[r][c] = ((GeoList)geoArray[r]).get(c).toValueString();
							}
						else
							data[r][0] = geoArray[r].toValueString();
				}	

				else {      // create array of "=geo.label" strings
					for(int r = 0; r < rowCount; r ++)
						if(geoArray[r].isGeoList())
							for(int c = 0; c < ((GeoList)geoArray[r]).size(); c ++){						 
								data[r][c] = "=Element[" + geoArray[r].getLabel() + "," + (c+1) + "]";
							}
						else
							data[r][0] = "=" + geoArray[r].label;
				}


				if( !isRowOrdered){  // create a transposed array
					for(int r = 0; r < rowCount; r ++)
						for(int c = 0; c < columnCount; c ++){
							dataTranspose[c][r] = data[r][c];
						}	
				}

				if(isRowOrdered)
					table.copyPasteCut.pasteExternal(data, 
							currentCell.x, currentCell.y, 
							currentCell.x + columnCount-1, currentCell.y + rowCount-1);

				else
					table.copyPasteCut.pasteExternal(dataTranspose, 
							currentCell.x, currentCell.y, 
							currentCell.x + rowCount-1, currentCell.y + columnCount-1);


				dte.dropComplete(true);

			} catch (UnsupportedFlavorException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				//e.printStackTrace();
			}

		}

		table.setTableMode(table.TABLE_MODE_STANDARD);
	}





	public void dropActionChanged(DropTargetDragEvent dte) {
		// TODO Auto-generated method stub

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




}
