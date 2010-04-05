package geogebra.gui.view.spreadsheet;

import java.awt.Point;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JLabel;

/**
 * Helper class that handles cell formats for the spreadsheet table 
 * cell renderer. 
 * 
 * Format values are stored in an array of hash tables. Each hash table holds 
 * values for a given format (e.g text alignment, background color). Table 
 * keys are Point objects that locate cells, rows or columns as follows:
 *  
 * cell = (column index, row index) 
 * row = (-1, row index) 
 * column = (column index, -1).
 * 
 * @author George Sturr,  2010-4-4
 *
 */
public class CellFormat {

	MyTable table;
	
	// Array of format tables
	private Hashtable[]  formatTableArray;
	
	// Format table types
	public static final int FORMAT_ALIGN = 0;
	public static final int FORMAT_BORDER = 1;
	public static final int FORMAT_BGCOLOR = 2;
	
	
	// Alignment format values	
	public static final int ALIGN_LEFT = JLabel.LEFT;
	public static final int ALIGN_CENTER = JLabel.CENTER;
	public static final int ALIGN_RIGHT = JLabel.RIGHT;
	
	
	
	
	public CellFormat(MyTable table){
		
		this.table = table;

		formatTableArray = new Hashtable[3];		
		formatTableArray[FORMAT_ALIGN] = new Hashtable();
		formatTableArray[FORMAT_BORDER] = new Hashtable();
		formatTableArray[FORMAT_BGCOLOR] = new Hashtable();
		
	}
	
	
	public void addFormat(ArrayList<CellRange> crList, int formatKind, int formatValue){
		
		Hashtable formatTable = formatTableArray[formatKind];
		
		Integer value = new Integer(formatValue);
		
		Point testCell = new Point();
		Point testRow = new Point();
		Point testColumn = new Point();		
	
		for (CellRange cr : crList) {
			//cr.debug();
			if (cr.isRow()) {
				//System.out.println("row");
				formatTable.put(new Point(-1,cr.getMinRow()), value);
				
				for (int col = 0; col < table.getColumnCount(); col++) {
					testCell.setLocation(col, cr.getMinRow());
					testColumn.setLocation(col,-1);
					formatTable.remove(testCell);
					if (formatTable.contains(testColumn)) {
						formatTable.put(testCell, value);
					}
				}
			}

			else if (cr.isColumn()) {
				//System.out.println("column");
				formatTable.put(new Point(cr.getMinColumn(),-1), value);
				
				for (int row = 0; row < table.getRowCount(); row++) {
					testCell.setLocation(cr.getMinColumn(), row);
					testRow.setLocation(-1,row);
					formatTable.remove(testCell);
					if (formatTable.contains(testRow)) {
						formatTable.put(testCell, value);
					}
				}
				
				
			}

			else {
				//System.out.println("other");
				for (Point cellPoint : cr.toCellList(true))
					formatTable.put(cellPoint, value);
			}
		}
				
		
		//System.out.println(formatTable.toString());
			
		
	}
	
	
	public Object getCellFormat(Point cellKey, int formatKind){
		
		Object value = null;
		
		Point rowKey = new Point(-1,cellKey.y);
		Point columnKey = new Point(cellKey.x,-1);
		
		Hashtable formatTable = formatTableArray[formatKind];
		
		if(formatTable.containsKey(cellKey)){
			//System.out.println("found" + cell.toString());
			value = formatTable.get(cellKey);
		}
		
		else if (formatTable.containsKey(rowKey)) {
			value = formatTable.get(rowKey);
		}
		else if (formatTable.containsKey(columnKey)) {
			value = formatTable.get(columnKey);
		}
		
		if(value != null){
			//System.out.println(value.toString());
			return setFormatValue((Integer) value, formatKind);
		}
		
		return null;
	
	}
	
	
	private Object setFormatValue(Integer value, int formatKind){
		
		switch (formatKind){
		case FORMAT_ALIGN:
			return value;
		
		case FORMAT_BORDER:
			return null;

			
		case FORMAT_BGCOLOR:
			return null;

		}
		
		return null;
	}
	
}
