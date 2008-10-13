
package geogebra.gui.view.spreadsheet;

import geogebra.Application;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class CopyPasteCut {
	
	protected Kernel kernel;
	protected Application app;
	protected MyTable table;
	
	protected String externalBuf;
	protected GeoElement[][] internalBuf;
	protected int bufColumn;
	protected int bufRow;
	
	public CopyPasteCut(JTable table0, Kernel kernel0) {
		table = (MyTable)table0;
		kernel = kernel0;	
		app = kernel.getApplication();
	}
	
	public void copy(int column1, int row1, int column2, int row2, boolean skipInternalCopy) {
		// external
		externalBuf = "";
		for (int row = row1; row <= row2; ++ row) {
			for (int column = column1; column <= column2; ++ column) {
				GeoElement value = RelativeCopy.getValue(table, column, row);
				if (value != null) {
					externalBuf += value.toValueString();
					//if (value.isChangeable()) {
					//	externalBuf += value.toValueString();
					//}
					//else {
					//	String def = value.getDefinitionDescription();
					//	def = def.replaceAll("\\s+", "");
					//	externalBuf += def;
					//}
				}
				if (column != column2) {
					externalBuf += "\t";
				}
			}
			if (row != row2) {
				externalBuf += "\n";
			}
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = new StringSelection(externalBuf);
		clipboard.setContents(stringSelection, null);
		
		// internal
		if (skipInternalCopy) {
			internalBuf = null;
		}
		else
		{
			bufColumn = column1;
			bufRow = row1;
			internalBuf = RelativeCopy.getValues(table, column1, row1, column2, row2);
		}
	}
	
	public boolean cut(int column1, int row1, int column2, int row2) {
		copy(column1, row1, column2, row2, false);
		//externalBuf = null;
		return delete(column1, row1, column2, row2);	
	}
	
	public boolean paste(int column1, int row1, int column2, int row2) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		String buf = null;
		boolean succ = false;
		
		//Application.debug("paste: "+row1+" "+row2+" "+column1+" "+column2);
		
		if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				buf = (String)contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
				ex.printStackTrace();
				//app.showError(ex.getMessage());
				// Util.handleException(table, ex);
			}
		}
		
		if (buf != null && externalBuf != null && buf.equals(externalBuf) && internalBuf != null) {
			try {
				succ = true;
				// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
				for (int c = column1 ; c <= column2 ; c+= internalBuf.length)
				for (int r = row1 ; r <= row2 ; r+= internalBuf[0].length)
					succ = succ && pasteInternal(c, r);
			} catch (Exception ex) {
				//ex.printStackTrace(System.out);
				//app.showError(ex.getMessage());
				
				//for (int c = column1 ; c <= column2 ; c++)
				//for (int r = row1 ; r <= row2 ; r++)
				//	pasteExternal(buf, c, r);
				
				// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
				succ = pasteExternalMultiple(buf, column1, row1, column2, row2);
				
				// Util.handleException(table, ex);
			}
		}
		else if (buf != null) {
			//Application.debug("newline index "+buf.indexOf("\n"));
			//Application.debug("length "+buf.length());

			// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
			succ = pasteExternalMultiple(buf, column1, row1, column2, row2);
		}
		
		return succ;
	}

	public boolean pasteInternal(int column1, int row1) throws Exception {		
		int width = internalBuf.length;
		if (width == 0) return false;
		int height = internalBuf[0].length;
		if (height == 0) return false;
		
		app.setWaitCursor();
		boolean succ = false; 
		
		//Application.debug("height=" + height);
		int x1 = bufColumn;
		int y1 = bufRow;
		int x2 = bufColumn + width - 1;
		int y2 = bufRow + height - 1;
		int x3 = column1;
		int y3 = row1;
		int x4 = column1 + width - 1;
		int y4 = row1 + height - 1;
		GeoElement[][] values2 = RelativeCopy.getValues(table, x3, y3, x4, y4);
		/*
		for (int i = 0; i < values2.length; ++ i) {
			for (int j = 0; j < values2[i].length; ++ j) {
				if (values2[i][j] != null) {
					values2[i][j].remove();
					values2[i][j] = null;
				}
			}
		}
		/**/
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		if (model.getRowCount() < y4 + 1) {
			model.setRowCount(y4 + 1);
		}
		if (model.getRowCount() < x4 + 1) {
			table.setMyColumnCount(x4 + 1);
		}
		GeoElement[][] values1 = RelativeCopy.getValues(table, x1, y1, x2, y2);
		try {
			for (int x = x1; x <= x2; ++ x) {
				int ix = x - x1;
				for (int y = y1; y <= y2; ++ y) {
					int iy = y - y1;
					values2[ix][iy] = RelativeCopy.doCopyNoStoringUndoInfo0(kernel, table, values1[ix][iy], values2[ix][iy], x3 - x1, y3 - y1);
					values2[ix][iy].setVisualStyle(values1[ix][iy]);
				}
			}
			
			/*
			if (values2.length == 1 || (values2.length > 0 && values2[0].length == 1)) {
				createPointsAndAList1(values2);
			}
			if (values2.length == 2 || (values2.length > 0 && values2[0].length == 2)) {
				createPointsAndAList2(values2);
			}*/
			
			succ = true;
		}
		catch (Exception e)
		{			
			e.printStackTrace();	
		}
		 finally {
			 app.setDefaultCursor();
		}
		 
		 return succ;
	}
	
	//protected static Pattern pattern = Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^,\\t\\\"]+)");
	//protected static Pattern pattern = Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^\\t\\\"]+)");
	protected static Pattern pattern1 = Pattern.compile("((\\\"([^\\\"]+)\\\")|([^\\t\\\"\\(]+)|(\\([^)]+\\)))?(\\t|$)");
	protected static Pattern pattern2 = Pattern.compile("((\\\"([^\\\"]+)\\\")|([^,\\\"\\(]+)|(\\([^)]+\\)))?(,|$)");
	
	public static String[][] parseData(String input) {
		String[] lines = input.split("\\r*\\n", -1);
		String[][] data = new String[lines.length][];
		for (int i = 0; i < lines.length; ++ i) {
			lines[i] = lines[i].trim();
			Matcher matcher = null;
			if (lines[i].indexOf('\t') != -1) {
				matcher = pattern1.matcher(lines[i]);
			}
			else {
				matcher = pattern2.matcher(lines[i]);
			}
			LinkedList list = new LinkedList();
			while (matcher.find()) {
				String data1 = matcher.group(3);
				String data2 = matcher.group(4);
				String data3 = matcher.group(5);
				if (data1 != null) {
					list.addLast(data1);
				}
				else if (data2 != null) {
					data2 = data2.trim();
					data2 = data2.replaceAll(",", ".");
					list.addLast(data2);
				}
				else if (data3 != null) {
					data3 = data3.trim();
					list.addLast(data3);
				}
				else {
					list.addLast("");
				}
			}
			if (list.size() > 0 && list.getLast().equals("")) {
				list.removeLast();
			}
			data[i] = (String[])list.toArray(new String[0]);
		}
		return data;		
	}
	
	private boolean pasteExternalMultiple(String buf,int column1, int row1, int column2, int row2) {
		int newlineIndex = buf.indexOf("\n");
		int rowStep = 1;
		if ( newlineIndex == -1 || newlineIndex == buf.length()-1) { 
			rowStep = 1; // no linefeeds in string
		}
		else
		{
		    for (int i = 0; i < buf.length()-1 ; i++) { // -1 : don't want to count a newline if it's the last char
		        char c = buf.charAt(i);
		        if (c == '\n') rowStep++; // count no of linefeeds in string
		    }
		}
		boolean succ = true;
		// paste data multiple times to fill in the selection rectangle (and maybe overflow a bit)
		for (int c = column1 ; c <= column2 ; c++)
		for (int r = row1 ; r <= row2 ; r+= rowStep)
			succ = succ && pasteExternal(buf, c, r);
		
		return succ;
		
	}
	
	public boolean pasteExternal(String buf, int column1, int row1) {
		app.setWaitCursor();
		boolean succ = false;			
		
		try {
			String[][] data = parseData(buf);
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			if (model.getRowCount() < row1 + data.length) {
				model.setRowCount(row1 + data.length);
			}
			GeoElement[][] values2 = new GeoElement[data.length][];
			int maxLen = -1;
			for (int row = row1; row < row1 + data.length; ++ row) {
				if (row < 0) continue;
				int iy = row - row1;
				values2[iy] = new GeoElement[data[iy].length];
				if (maxLen < data[iy].length) maxLen = data[iy].length;
				if (model.getColumnCount() < column1 + data[iy].length) {
					table.setMyColumnCount(column1 + data[iy].length);						
				}
				for (int column = column1; column < column1 + data[iy].length; ++ column) {
					if (column < 0) continue;
					int ix = column - column1;
					//Application.debug(iy + " " + ix + " [" + data[iy][ix] + "]");
					data[iy][ix] = data[iy][ix].trim();
					if (data[iy][ix].length() == 0) {
						GeoElement value0 = RelativeCopy.getValue(table, column, row);
						if (value0 != null) {
							//Application.debug(value0.toValueString());
							//MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column, row);
							value0.remove();
						}	
					}
					else {
						GeoElement value0 = RelativeCopy.getValue(table, column, row);
						values2[iy][ix] = MyCellEditor.prepareAddingValueToTableNoStoringUndoInfo(kernel, table, data[iy][ix], value0, column, row);
						values2[iy][ix].setAuxiliaryObject(values2[iy][ix].isGeoNumeric()); 
						table.setValueAt(values2[iy][ix], row, column);
					}
				}
			}
			//Application.debug("maxLen=" + maxLen);
			table.getView().repaintView();
			
			/*
			if (values2.length == 1 || maxLen == 1) {
				createPointsAndAList1(values2);
			}
			if (values2.length == 2 || maxLen == 2) {
				createPointsAndAList2(values2);
			}*/
			
			succ = true;
		} catch (Exception ex) {
			//app.showError(ex.getMessage());
			//Util.handleException(table, ex);
			ex.printStackTrace();
		} finally {
			app.setDefaultCursor();
		}
		
		return succ;
	}
	
	

	public boolean delete(int column1, int row1, int column2, int row2)  {
		boolean succ = false;
		for (int column = column1; column <= column2; ++ column) {
			//int column3 = table.convertColumnIndexToModel(column);
			for (int row = row1; row <= row2; ++ row) {
				GeoElement value0 = RelativeCopy.getValue(table, column, row);
				if (value0 != null) {
					value0.remove();
					succ = true;
				}
				//try {
				//	MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column3, row);
				//} catch (Exception e) {
				//	Application.debug("spreadsheet.delete: " + e.getMessage());
				//}
			}
		}
		return succ;
	}
	
	public void createPointsAndAList2(GeoElement[][] values) throws Exception {
		LinkedList list = new LinkedList();
		
		/* 
		 * Markus Hohenwarter, 2008-08-24, I think this is not needed...
		 * 
		if (values.length == 2) {
	   	 	for (int i = 0; i < values[0].length && i < values[1].length; ++ i) {
	   	 		GeoElement v1 = values[0][i];
	   	 		GeoElement v2 = values[1][i];
	   	 		if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {
	   	 			String text = "(" + v1.getLabel() + "," + v2.getLabel() + ")";
	   	 			GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	   	 			
	   	 			// set label P_1, P_2, etc.
	   	 		    String pointName = geos[0].getIndexLabel("P");
	   	 		    geos[0].setLabel(pointName);
	   	 		    
	   	 			list.addLast(pointName);
	   	 		}
	   	 	}
	   	 }
	   	 */
		
		// create points
	   	 if (values.length > 0) {
	   	 	for (int i = 0; i < values.length; ++ i) {
	   	 		if (values[i].length != 2) continue;
	   	 		GeoElement v1 = values[i][0];
	   	 		GeoElement v2 = values[i][1];
	   	 		if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {	   	 			
	   	 			String text = "(" + v1.getLabel() + "," + v2.getLabel() + ")";
	   	 			GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	   	 			
	   	 			// set label P_1, P_2, etc.
	   	 		    String pointName = geos[0].getIndexLabel("P");
	   	 		    geos[0].setLabel(pointName);
	   	 			
	   	 			list.addLast(geos[0].getLabel());
	   	 		}
	   	 	}
	   	 }
	   	 
	   	 // create list of points
	   	 if (list.size() > 0) {
	   		 String[] points = (String[])list.toArray(new String[0]);	   		
	   		 String text = "{";
	   		 for (int i = 0; i < points.length; ++ i) {
	   			text += points[i];
	   			 if (i != points.length - 1) text += ",";
	   		 }
	   		text += "}";
	   		
	   		GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	   		
	   		// set list name
 		    String listName = geos[0].getIndexLabel("L");
 		    geos[0].setLabel(listName);
	   	 }
	}

	public void createPointsAndAList1(GeoElement[][] values) throws Exception {
		LinkedList list = new LinkedList();
		if (values.length == 1 && values[0].length > 0) {
	   	 	for (int i = 0; i < values[0].length; ++ i) {
	   	 		GeoElement v1 = values[0][i];
	   	 		if (v1 != null && v1.isGeoPoint()) {
	   	 			list.addLast(v1.getLabel());
	   	 		}
	   	 	}
	   	 }
	   	 if (values.length > 0 && values[0].length == 1) {
	   	 	for (int i = 0; i < values.length; ++ i) {
	   	 		GeoElement v1 = values[i][0];
	   	 		if (v1 != null && v1.isGeoPoint()) {
	   	 			list.addLast(v1.getLabel());
	   	 		}
	   	 	}
	   	 }
	   	 
	   	 if (list.size() > 0) {
	   		 String[] points = (String[])list.toArray(new String[0]);	   		 
	   		 String text = "={";
	   		 for (int i = 0; i < points.length; ++ i) {
	   			text += points[i];
	   			 if (i != points.length - 1) text += ",";
	   		 }
	   		text += "}";
	   		GeoElement [] geos = table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, false);
	   		
	   		// set list name
 		    String listName = geos[0].getIndexLabel("L");
 		    geos[0].setLabel(listName);
	   	 }
	}
}
