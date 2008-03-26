
package geogebra.spreadsheet;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import javax.swing.JTable;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

public class CopyPasteCut {
	
	protected Kernel kernel;
	protected MyTable table;
	
	public CopyPasteCut(JTable table0, Kernel kernel0) {
		table = (MyTable)table0;
		kernel = kernel0;	
	}
	
	public void copy(int column1, int row1, int column2, int row2) {
		String buf = "";
		for (int row = row1; row <= row2; ++ row) {
			for (int column = column1; column <= column2; ++ column) {
				GeoElement value = RelativeCopy.getValue(table, column, row);
				if (value != null) {
					buf += value.toValueString();
				}
				if (column != column2) {
					buf += "\t";
				}
			}
			buf += "\r\n";
		}
		System.out.println(column1);
		System.out.println(row1);
		System.out.println(column2);
		System.out.println(row2);
		System.out.println(buf);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSelection = new StringSelection(buf);
		clipboard.setContents(stringSelection, null);
	}

	public void paste(int column1, int row1, int column2, int row2) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		String buf = null;
		if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				buf = (String)contents.getTransferData(DataFlavor.stringFlavor);
			} catch (Exception ex) {
				//kernel.getApplication().showError(ex.getMessage());
				kernel.getApplication().showError(ex.getMessage());
				//Util.handleException(table, ex);
			}
		}
		if (buf != null) {
			String[] lines = buf.split("\\r*\\n", -1);
			String[][] data = new String[lines.length][];
			for (int i = 0; i < lines.length; ++ i) {
				data[i] = lines[i].split("\\s", -1);
			}
			try {
				for (int row = row1; row < row1 + data.length; ++ row) {
					int iy = row - row1;
					for (int column = column1; column < column1 + data[iy].length; ++ column) {
						int ix = column - column1;
						data[iy][ix] = data[iy][ix].trim();
						if (data[iy][ix].length() == 0) {
							GeoElement value0 = RelativeCopy.getValue(table, column, row);
							if (value0 != null) {
								//System.out.println(value0.toValueString());
								MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column, row);
							}	
						}
						else {
							GeoElement value0 = RelativeCopy.getValue(table, column, row);
							GeoElement value = MyCellEditor.prepareAddingValueToTable(kernel, table, data[iy][ix], value0, column, row);
							table.setValueAt(value, row, column);
						}
					}
				}
			} catch (Exception ex) {
				kernel.getApplication().showError(ex.getMessage());
				Util.handleException(table, ex);
			}
		}
	}

	public void delete(int column1, int row1, int column2, int row2)  {
		for (int column = column1; column <= column2; ++ column) {
			for (int row = row1; row <= row2; ++ row) {
				GeoElement value0 = RelativeCopy.getValue(table, column, row);
				try {
					MyCellEditor.prepareAddingValueToTable(kernel, table, null, value0, column, row);
				} catch (Exception e) {
					System.err.println("spreadsheet.delete: " + e.getMessage());
				}
			}
		}
	}

}
