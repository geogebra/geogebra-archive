
package geogebra.spreadsheet;

import javax.swing.JTable;

import geogebra.kernel.Kernel;
import geogebra.kernel.GeoElement;

public class RelativeCopy {

	protected Kernel kernel;
	protected JTable table;
	
	public RelativeCopy(JTable table0, Kernel kernel0) {
		table = table0;
		kernel = kernel0;
	}
	
	public boolean doCopy(int sx1, int sy1, int sx2, int sy2, int dx1, int dy1, int dx2, int dy2) {
		// -|1|-
		// 2|-|3
		// -|4|-
		if (sx1 == dx1 && sx2 == dx2) {
			if (dy2 < sy1) { // 1
				return doCopyHorizontal(sx1, sx2, sy1, dy1, dy2);
			}
			else if (dy1 > sy2) { // 4
				return doCopyHorizontal(sx1, sx2, sy2, dy1, dy2);
			}
		}
		else if (sy1 == dy1 && sy1 == dy2) {
			if (dx2 < sx1) { // 2
				return doCopyVertical(sy1, sy1, sx1, dx1, dx2);
			}
			else if (dx1 > sx2) { // 4
				return doCopyVertical(sy1, sy1, sx2, dx1, dx2);
			}			
		}
		throw new RuntimeException("Error state.");
	}
	
	public boolean doCopyHorizontal(int x1, int x2, int sy, int dy1, int dy2) {
		// TODO
		return true;
	}
	
	public boolean doCopyVertical(int y1, int y2, int sx, int dx1, int dx2) {
		// TODO
		return true;
	}
	
	protected GeoElement getValue(int column, int row) {
		MyTableModel tableModel = (MyTableModel)table.getModel();
		return (GeoElement)tableModel.getValueAt(row, column);
	}
	
	protected void setValue(int column, int row, GeoElement value) {
		column = table.convertColumnIndexToModel(column);
		String name = table.getModel().getColumnName(column) + (row + 1);
		MyTableModel tableModel = (MyTableModel)table.getModel();
		GeoElement value0 = getValue(row, column);
		if (value0 != null) {
			value0.remove();
		}
		tableModel.setValueAt(value, row, column);
	}
	
	
}
