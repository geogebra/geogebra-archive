
package geogebra.spreadsheet;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import javax.swing.JTable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.LinkedList;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class CopyPasteCut {
	
	protected Kernel kernel;
	protected MyTable table;
	
	protected String externalBuf;
	protected GeoElement[][] internalBuf;
	protected int bufColumn;
	protected int bufRow;
	
	public CopyPasteCut(JTable table0, Kernel kernel0) {
		table = (MyTable)table0;
		kernel = kernel0;	
	}
	
	public void copy(int column1, int row1, int column2, int row2) {
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
		bufColumn = column1;
		bufRow = row1;
		internalBuf = RelativeCopy.getValues(table, column1, row1, column2, row2);
	}
	
	public void cut(int column1, int row1, int column2, int row2) {
		copy(column1, row1, column2, row2);
		externalBuf = null;
		delete(column1, row1, column2, row2);	
	}
	
	public void paste(int column1, int row1, int column2, int row2) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		String buf = null;
		if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				buf = (String)contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
				kernel.getApplication().showError(ex.getMessage());
				// Util.handleException(table, ex);
			}
		}
		if (buf != null && externalBuf != null && buf.equals(externalBuf)) {
			try {
				pasteInternal(column1, row1);
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
				kernel.getApplication().showError(ex.getMessage());
				// Util.handleException(table, ex);
			}
		}
		else if (buf != null) {
			pasteExternal(buf, column1, row1);
		}
	}

	public void pasteInternal(int column1, int row1) throws Exception {
		kernel.getApplication().setWaitCursor();
		int width = internalBuf.length;
		if (width == 0) return;
		int height = internalBuf[0].length;
		if (height == 0) return;
		//System.out.println("height=" + height);
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
		MyTableModel model = (MyTableModel)table.getModel();
		if (model.rowCount < y4 + 1) {
			model.setRowCount(y4 + 1);
		}
		if (model.columnCount < x4 + 1) {
			model.setColumnCount(x4 + 1);
		}
		GeoElement[][] values1 = RelativeCopy.getValues(table, x1, y1, x2, y2);
		for (int x = x1; x <= x2; ++ x) {
			int ix = x - x1;
			for (int y = y1; y <= y2; ++ y) {
				int iy = y - y1;
				values2[ix][iy] = RelativeCopy.doCopy0(kernel, table, values1[ix][iy], values2[ix][iy], x3 - x1, y3 - y1);
			}
		}
		if (values2.length == 2 || (values2.length > 0 && values2[0].length == 2)) {
			createPointsAndAList(values2);
		}
		kernel.getApplication().setDefaultCursor();
	}
	
	protected static Pattern pattern = Pattern.compile("\\s*(\\\"([^\\\"]+)\\\")|([^,\\t\\\"]+)");
	
	public void pasteExternal(String buf, int column1, int row1) {
		kernel.getApplication().setWaitCursor();
		String[] lines = buf.split("\\r*\\n", -1);
		String[][] data = new String[lines.length][];
		for (int i = 0; i < lines.length; ++ i) {
			LinkedList list = new LinkedList();
			if (lines[i].indexOf('\t') != -1) {
				lines[i] = lines[i].replaceAll(",", ".");
			}
			Matcher matcher = pattern.matcher(lines[i]);
			int index = 0;
			while (index != -1 && matcher.find(index)) {
				index = matcher.end();
				String data1 = matcher.group(2);
				String data2 = matcher.group(3);
				if (data1 != null) {
					list.addLast(data1);
				}
				if (data2 != null) {
					data2 = data2.trim();
					list.addLast(data2);
				}
			}
			data[i] = (String[])list.toArray(new String[0]);
		}
		try {
			MyTableModel model = (MyTableModel)table.getModel();
			if (model.rowCount < row1 + data.length) {
				model.setRowCount(row1 + data.length);
			}
			GeoElement[][] values2 = new GeoElement[data.length][data.length > 0 ? data[0].length : 0];
			for (int row = row1; row < row1 + data.length; ++ row) {
				if (row < 0) continue;
				int iy = row - row1;
				if (model.getColumnCount() < column1 + data[iy].length) {
					model.setColumnCount(column1 + data[iy].length);						
				}
				for (int column = column1; column < column1 + data[iy].length; ++ column) {
					if (column < 0) continue;
					int ix = column - column1;
					data[iy][ix] = data[iy][ix].trim();
					if (data[iy][ix].length() == 0) {
						GeoElement value0 = RelativeCopy.getValue(table, column, row);
						if (value0 != null) {
							//System.out.println(value0.toValueString());
							//MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column, row);
							value0.remove();
						}	
					}
					else {
						GeoElement value0 = RelativeCopy.getValue(table, column, row);
						values2[iy][ix] = MyCellEditor.prepareAddingValueToTable(kernel, table, data[iy][ix], value0, column, row);
						table.setValueAt(values2[iy][ix], row, column);
					}
				}
			}
			table.getView().repaintView();
			if (values2.length == 2 || (values2.length > 0 && values2[0].length == 2)) {
				createPointsAndAList(values2);
			}
		} catch (Exception ex) {
			kernel.getApplication().showError(ex.getMessage());
			// Util.handleException(table, ex);
		} finally {
			kernel.getApplication().setDefaultCursor();
		}
	}

	public void delete(int column1, int row1, int column2, int row2)  {
		for (int column = column1; column <= column2; ++ column) {
			//int column3 = table.convertColumnIndexToModel(column);
			for (int row = row1; row <= row2; ++ row) {
				GeoElement value0 = RelativeCopy.getValue(table, column, row);
				if (value0 != null) {
					value0.remove();
				}
				//try {
				//	MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column3, row);
				//} catch (Exception e) {
				//	System.err.println("spreadsheet.delete: " + e.getMessage());
				//}
			}
		}
	}
	
	public static int listNameCount = 0;
	
	public static String getNextListName() {
		++ listNameCount;
		if (listNameCount < 10) {
			return "L_" + listNameCount;		
		}
		return "L_{" + listNameCount + "}";
	}
	
	public void createPointsAndAList(GeoElement[][] values) throws Exception {
		System.out.println("createPointsAndAList");
		LinkedList list = new LinkedList();
		if (values.length == 2 && values[0].length > 0) {
	   	 	for (int i = 0; i < values[0].length; ++ i) {
	   	 		GeoElement v1 = values[0][i];
	   	 		GeoElement v2 = values[1][i];
	   	 		if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {
	   	 			String pointName = ContextMenu.getNextPointName();
	   	 			String text = pointName + "=(" + v1.getLabel() + "," + v2.getLabel() + ")";
	   	 			table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, true);
	   	 			list.addLast(pointName);
	   	 		}
	   	 	}
	   	 }
	   	 if (values.length > 0 && values[0].length == 2) {
	   	 	for (int i = 0; i < values.length; ++ i) {
	   	 		GeoElement v1 = values[i][0];
	   	 		GeoElement v2 = values[i][1];
	   	 		if (v1 != null && v2 != null && v1.isGeoNumeric() && v2.isGeoNumeric()) {
	   	 			String pointName = ContextMenu.getNextPointName();
	   	 			String text = pointName + "=(" + v1.getLabel() + "," + v2.getLabel() + ")";
	   	 			table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, true);
	   	 			list.addLast(pointName);
	   	 		}
	   	 	}
	   	 }
	   	 if (list.size() > 0) {
	   		 String[] points = (String[])list.toArray(new String[0]);
	   		 String listName = getNextListName();
	   		 String text = listName + "={";
	   		 for (int i = 0; i < points.length; ++ i) {
	   			text += points[i];
	   			 if (i != points.length - 1) text += ",";
	   		 }
	   		text += "}";
	   		table.kernel.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(text, true);
	   	 }
	}

}
