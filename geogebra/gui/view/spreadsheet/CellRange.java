package geogebra.gui.view.spreadsheet;

import java.util.ArrayList;

import geogebra.kernel.GeoElement;
import geogebra.kernel.arithmetic.Term;

/**
 * Utility class for spreadsheet cell ranges.
 *
 *
 * A cell range is any rectangular block of cells defined by two
 * diagonal corner cells. One corner is designated as the anchor 
 * cell. This is the cell first clicked when dragging out a cell range. 
 * Once the corner index values are set the max and min corner
 * index values are calculated as follows:
 *  
 *        minColumn   
 *    minRow *---------*
 *           |         |
 *           *-------- * maxRow
 *                 maxColumn      
 *  
 * Rows and columns have index values of -1.
 * 
 *      row:     minRow >=0, minColumn = -1, maxColumn = -1  
 *   column:  minColumn >=0, minRow = -1, maxRow = -1  
 * 
 * 
 * @author George Sturr, 2010-1-23
 */


public class CellRange {
	
	private int minColumn = -1;
	private int minRow = -1;
	private int maxColumn = -1;
	private int maxRow = -1;
	private int anchorColumn = -1;
	private int anchorRow = -1;
	
	
	private MyTable table;

	
	
	/** Create new CellRange */
	CellRange(MyTable table) {
		this.table = table;
	}

	CellRange(MyTable table, int anchorColumn, int anchorRow, int col2, int row2) {
		this.table = table;
		setCellRange( anchorColumn, anchorRow, col2, row2);

	}

	// TODO Constructor with string parameter, e.g. CellRange(A1:B10)

	
	

	/**
	 * Set a cell range with diagonal corner cells colIndex0, rowIndex0 and
	 * colIndex1, rowIndex1 Can be any two diagonal corners in either order.
	 */
	public void setCellRange(int anchorColumn, int anchorRow, int col2, int row2) {
		
		minColumn = Math.min(anchorColumn, col2);
		maxColumn = Math.max(anchorColumn, col2);
		minRow = Math.min(anchorRow, row2);
		maxRow = Math.max(anchorRow, row2);
		
		this.anchorColumn = anchorColumn;
		this.anchorRow = anchorRow;
		
	}

	public int getMinColumn() {
		return minColumn;
	}

	public int getMinRow() {
		return minRow;
	}

	public int getMaxColumn() {
		return maxColumn;
	}

	public int getMaxRow() {
		return maxRow;
	}
	
	
	
	
	boolean isColumn() {
		return (minColumn > -1 && minRow == -1 && maxRow == -1);
	}

	boolean isRow() {
		return (minRow > -1 && minColumn == -1 && maxColumn == -1);
	}
	
	
	boolean is2D() {
		return (maxColumn - minColumn == 1) || (maxRow - minRow == 1);
	}

	boolean isEmpty() {
		return toGeoList().size() == 0;
	}


	/**
	 * Returns a cell range that holds the actual cell range 
	 * of an input row or column.
	 * e.g. (-1,1,-1,4) ---> (0,1,100,4)
	 */
	public CellRange getActualRange(CellRange cr) {

		CellRange adjustedCellRange = cr.clone();

		if (cr.minRow == -1 && cr.maxRow == -1 && cr.minColumn != -1) {
			adjustedCellRange.minRow = 0;
			adjustedCellRange.maxRow = table.getRowCount() - 1;
		}

		if (cr.minColumn == -1 && cr.maxColumn == -1 && cr.minRow != -1) {
			adjustedCellRange.minColumn = 0;
			adjustedCellRange.maxColumn = table.getColumnCount() - 1;
		}

		return adjustedCellRange;
	}
	
	
	/**
	 * Sets the corners of a row or column to the actual cell range 
	 * e.g. (-1,1,-1,4) ---> (0,1,100,4)
	 */
	public void setActualRange() {
		
		if (minRow == -1 && maxRow == -1 && minColumn != -1) {
			minRow = 0;
			maxRow = table.getRowCount() - 1;
		}

		if (minColumn == -1 && maxColumn == -1 && minRow != -1) {
			minColumn = 0;
			maxColumn = table.getColumnCount() - 1;
		}

	}
	
		
	
	/**
	 * ArrayList of all geos found in the cell range 
	 */
	public ArrayList toGeoList() {

		ArrayList list = new ArrayList();
		
		for (int col = minColumn; col <= maxColumn; ++col) {
			for (int row = minRow; row <= maxRow; ++row) {
				GeoElement geo = RelativeCopy.getValue(table, col, row);
				if (geo != null){
					list.add(geo);
				}
			}
		}
		return list;
	}
		
	/**
	 * ArrayList of labels for each geo found in the cell range
	 */
	public ArrayList toGeoLabelList(boolean scanByColumn, boolean copyByValue) {

		ArrayList list = new ArrayList();

		if (scanByColumn) { 
			for (int col = minColumn; col <= maxColumn; ++col) {
				for (int row = minRow; row <= maxRow; ++row) {
					GeoElement geo = RelativeCopy.getValue(table, col, row);
					if (geo != null){
						if (copyByValue)
							list.add(geo.getValueForInputBar());
						else
							list.add(geo.getLabel());
					}
				}
			}
		} else {
			for (int row = minRow; row <= maxRow; ++row) {
				for (int col = minColumn; col <= maxColumn; ++col) {
					GeoElement geo = RelativeCopy.getValue(table, col, row);
					if (geo != null)
						if (copyByValue)
							list.add(geo.getValueForInputBar());
						else
							list.add(geo.getLabel());
				}
			}
		}

		return list;
	}
	
	boolean hasSameAnchor(CellRange cr){
		return (cr.anchorRow == anchorRow) && (cr.anchorColumn == anchorColumn);
	}
	
	
	
	public CellRange clone(){
		return new CellRange(table, minColumn,minRow,maxColumn,maxRow);
	}
	
	public boolean equals(Object obj) {
		CellRange cr;
		if (obj instanceof CellRange) {
			cr = (CellRange) obj;
			return (cr.minColumn == minColumn && cr.minRow == minRow
					&& cr.maxColumn == maxColumn && cr.maxRow == maxRow);
		} else
			return false;
	}
	
	public void debug(){
		System.out.println("(" + anchorColumn + "," + anchorRow + ")" );
		System.out.println("(" + minColumn + "," + minRow + ")  (" + maxColumn + "," + maxRow + ")"  );
	}
	
	
	
}