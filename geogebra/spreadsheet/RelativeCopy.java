
package geogebra.spreadsheet;

import javax.swing.JTable;
import java.util.TreeSet;

import geogebra.kernel.Kernel;
import geogebra.kernel.GeoElement;

public class RelativeCopy {

	protected Kernel kernel;
	protected MyTable table;
	
	public RelativeCopy(JTable table0, Kernel kernel0) {
		table = (MyTable)table0;
		kernel = kernel0;
	}
	
	public boolean doCopy(int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) {
		// -|1|-
		// 2|-|3
		// -|4|-
		try {
			if (sx1 == dx1 && sx2 == dx2) {
				if (dy2 < sy1) { // 1
					doCopyHorizontal(sx1, sx2, sy1, dy1, dy2);
					return true;
				}
				else if (dy1 > sy2) { // 4
					doCopyHorizontal(sx1, sx2, sy2, dy1, dy2);
					return true;
				}
			}
			else if (sy1 == dy1 && sy2 == dy2) {
				if (dx2 < sx1) { // 2
					doCopyVertical(sy1, sy2, sx1, dx1, dx2);
					return true;
				}
				else if (dx1 > sx2) { // 4
					doCopyVertical(sy1, sy2, sx2, dx1, dx2);
					return true;
				}			
			}
			String msg = 
				"sx1 = " + sx1 + "\r\n" +
				"sy1 = " + sy1 + "\r\n" +
				"sx2 = " + sx2 + "\r\n" +
				"sy2 = " + sy2 + "\r\n" +
				"dx1 = " + dx1 + "\r\n" +
				"dy1 = " + dy1 + "\r\n" +
				"dx2 = " + dx2 + "\r\n" +
				"dy2 = " + dy2 + "\r\n";
			throw new RuntimeException("Error state:\r\n" + msg);
		} catch (Exception ex) {
			Util.handleException(table, ex);
			return false;
		}
	}
	
	public void doCopyHorizontal(int x1, int x2, int sy, int dy1, int dy2) {
		GeoElement[][] values1 = getValues(table, x1, sy, x2, sy);
		GeoElement[][] values2 = getValues(table, x1, dy1, x2, dy2);
		for (int x = x1; x <= x2; ++ x) {
			int ix = x - x1;
			if (values1[ix][0] == null) {
				throw new NullPointerException("Relative copy: Source cannot be empty.");
			}
		}
		if (checkDependency(values1, values2)) {
			throw new RuntimeException("Relative copy: Source is dependent on destination.");			
		}
		for (int x = x1; x <= x2; ++ x) {
			int ix = x - x1;
			for (int y = dy1; y <= dy2; ++ y) {
				int iy = y - dy1;
				doCopy0(values1[ix][0], values2[ix][iy], 0, y - sy);
			}
		}
	}
	
	public void doCopyVertical(int y1, int y2, int sx, int dx1, int dx2) {
		GeoElement[][] values1 = getValues(table, sx, y1, sx, y2);
		GeoElement[][] values2 = getValues(table, dx1, y1, dx2, y2);
		for (int y = y1; y <= y2; ++ y) {
			int iy = y - y1;
			if (values1[0][iy] == null) {
				throw new NullPointerException("Relative copy: Source cannot be empty.");
			}
		}
		if (checkDependency(values1, values2)) {
			throw new RuntimeException("Relative copy: Source is dependent on destination.");			
		}
		for (int y = y1; y <= y2; ++ y) {
			int iy = y - y1;
			for (int x = dx1; x <= dx2; ++ x) {
				int ix = x - dx1;
				doCopy0(values1[0][iy], values2[ix][iy], x - sx, 0);
			}
		}
	}
	
	protected void doCopy0(GeoElement value, GeoElement oldValue, int dx, int dy) {
		String text = null;
		if (value.isChangeable()) {
			text = value.toValueString();
		}
		else {
			text = value.getDefinitionDescription();
		}
		GeoElement[] dependents = getDependentObjects(value);
		for (int i = 0; i < dependents.length; ++ i) {
			int column = GeoElement.getSpreadsheetColumn(dependents[i].getLabel());
			int row = GeoElement.getSpreadsheetRow(dependents[i].getLabel());
			if (column == -1 || row == -1) continue;
			String name1 = "" + (char)('A' + column) + (row + 1);
			String name2 = "" + (char)('A' + column) + "::" + (row + 1);
			text = text.replaceAll(name1, name2);
		}
		for (int i = 0; i < dependents.length; ++ i) {
			int column = GeoElement.getSpreadsheetColumn(dependents[i].getLabel());
			int row = GeoElement.getSpreadsheetRow(dependents[i].getLabel());
			if (column == -1 || row == -1) continue;
			String name1 = "" + (char)('A' + column) + "::" + (row + 1);
			String name2 = "" + (char)('A' + column + dx) + (row + dy + 1);
			text = text.replaceAll(name1, name2);
		}
		int column = GeoElement.getSpreadsheetColumn(value.getLabel());
		int row = GeoElement.getSpreadsheetRow(value.getLabel());
		//System.out.println("add text = " + text + ", name = " + (char)('A' + column + dx) + (row + dy + 1));
		GeoElement value2 = MyCellEditor.prepareAddingValueToTable(kernel, table, text, oldValue, column + dx, row + dy);
		table.setValueAt(value2, row + dy, column + dx);
	}
	
	// return true if any of elems1 is dependent on any of elems
	// preposition: every elems1 is not null.
	public static boolean checkDependency(GeoElement[][] elems1, GeoElement[][] elems2) {
		for (int i = 0; i < elems1.length; ++ i) {
			for (int j = 0; j < elems1[i].length; ++ j) {
				if (checkDependency(elems1[i][j], elems2)) return true;
			}			
		}
		return false;
	}
	
	// return true if elem is dependent on any of elems
	// preposition: elem is not null
	public static boolean checkDependency(GeoElement elem, GeoElement[][] elems) {
		for (int i = 0; i < elems.length; ++ i) {
			for (int j = 0; j < elems[i].length; ++ j) {
				if (elems[i] == null) continue;
				if (checkDependency(elem, elems[i][j])) return true;
			}			
		}
		return false;
	}
	
	// return true if elem1 is dependent on elem2
	// preposition: elem is not null
	public static boolean checkDependency(GeoElement elem1, GeoElement elem2) {
		if (elem2 == null) return false;
		GeoElement[] elems = getDependentObjects(elem1);
		if (elems.length == 0) return false;
        int column = GeoElement.getSpreadsheetColumn(elem2.getLabel());
        int row = GeoElement.getSpreadsheetRow(elem2.getLabel());
        if (column == -1 || row == -1) return false;
		for (int i = 0; i < elems.length; ++ i) {
            int column2 = GeoElement.getSpreadsheetColumn(elems[i].getLabel());
            int row2 = GeoElement.getSpreadsheetRow(elems[i].getLabel());
            if (column == column2 && row == row2) return true;
		}
		return false;
	}
	
	public static GeoElement[] getDependentObjects(GeoElement geo) {
		if (geo.isIndependent()) return new GeoElement[0];
    	TreeSet geoTree = geo.getAllPredecessors();
    	return (GeoElement[])geoTree.toArray(new GeoElement[0]);
	}
	
	public static GeoElement[][] getValues(MyTable table, int x1, int y1, int x2, int y2) {
		GeoElement[][] values = new GeoElement[x2 - x1 + 1][y2 - y1 + 1];
		for (int y = y1; y <= y2; ++ y) {
			for (int x = x1; x <= x2; ++ x) {
				int x0 = table.convertColumnIndexToModel(x);
				values[x - x1][y - y1] = getValue(table, x0, y);
			}			
		}
		return values;
	}
	
	public static GeoElement getValue(MyTable table, int column, int row) {
		MyTableModel tableModel = (MyTableModel)table.getModel();
		return (GeoElement)tableModel.getValueAt(row, column);
	}	
	
}
