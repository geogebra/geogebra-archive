package geogebra.gui.view.spreadsheet;

import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JLabel;

/**
 * Helper class that handles cell formats for the spreadsheet table cell
 * renderer.
 * 
 * Format values are stored in an array of hash tables. Each hash table holds
 * values for a given format (e.g text alignment, background color). Table keys
 * are Point objects that locate cells, rows or columns as follows:
 * 
 * cell = (column index, row index) row = (-1, row index) column = (column
 * index, -1).
 * 
 * @author George Sturr, 2010-4-4
 * 
 */
public class CellFormat {

	MyTable table;

	// Array of format tables
	private HashMap<Point, Object>[]  formatTableArray;

	// Format types. 
	// These are also array indices, so they must be sequential: 0..n
	public static final int FORMAT_ALIGN = 0;
	public static final int FORMAT_BORDER = 1;
	public static final int FORMAT_BGCOLOR = 2;
	public static final int FORMAT_TRACING = 3;
	public static final int FORMAT_FONTSTYLE = 4;

	private int formatCount = 5;


	// Alignment constants	
	public static final int ALIGN_LEFT = JLabel.LEFT;
	public static final int ALIGN_CENTER = JLabel.CENTER;
	public static final int ALIGN_RIGHT = JLabel.RIGHT;


	// Font syle constants	
	public static final int STYLE_PLAIN = Font.PLAIN;
	public static final int STYLE_BOLD = Font.BOLD;
	public static final int STYLE_ITALIC = Font.ITALIC;
	public static final int STYLE_BOLD_ITALIC = Font.BOLD + Font.ITALIC;


	// Border style constants used by stylebar.	
	// Keep this order, they are indices to the border popup button menu
	public static final int BORDER_STYLE_NONE = 0;
	public static final int BORDER_STYLE_FRAME = 1;
	public static final int BORDER_STYLE_INSIDE = 2;
	public static final int BORDER_STYLE_ALL = 3;
	public static final int BORDER_STYLE_TOP = 4;
	public static final int BORDER_STYLE_BOTTOM = 5;
	public static final int BORDER_STYLE_LEFT = 6;
	public static final int BORDER_STYLE_RIGHT = 7;

	// Border constants for painting
	// These are stored in a format map and are bit-decoded when painting borders
	public static final byte BORDER_LEFT = 1;
	public static final byte BORDER_TOP = 2;
	public static final byte BORDER_RIGHT = 4;
	public static final byte BORDER_BOTTOM = 8;
	public static final byte BORDER_ALL = 15; // sum 


	/***************************************************
	 * Constructor
	 * @param table
	 */
	public CellFormat(MyTable table){

		this.table = table;
		// Create instances of the format hash maps 
		formatTableArray = new HashMap[formatCount];
		for(int i = 0; i < formatCount; i++){
			formatTableArray[i] = new HashMap<Point, Object>();
		}
	}



	public HashMap<Point,Object> getFormatMap(int formatType){
		return formatTableArray[formatType];
	}



	/**
	 * Add a format value to a single cell.
	 */
	public void setFormat(Point cell, int formatType, Object formatValue){
		ArrayList<CellRange> crList = new ArrayList<CellRange>();
		crList.add(new CellRange(table,cell.x, cell.y));
		setFormat(crList, formatType, formatValue);
	}


	/**
	 * Add a format value to a cell range.
	 */
	public void setFormat(CellRange cr, int formatType, Object formatValue){
		ArrayList<CellRange> crList = new ArrayList<CellRange>();
		crList.add(cr);
		setFormat(crList, formatType, formatValue);
	}


	/**
	 * Add a format value to a list of cell ranges.
	 */
	public void setFormat(ArrayList<CellRange> crList, int formatType, 
			Object value){

		HashMap<Point,Object> formatTable = formatTableArray[formatType];

		//Integer value = new Integer(formatValue);

		Point testCell = new Point();
		Point testRow = new Point();
		Point testColumn = new Point();		

		for (CellRange cr : crList) {
			//cr.debug();
			if (cr.isRow()) {

				// iterate through each row in the selection
				for(int r = cr.getMinRow(); r <= cr.getMaxRow(); ++r){

					// format the row
					formatTable.put(new Point(-1,r), value);

					// handle cells in the row with prior formatting 
					for (int col = 0; col < table.getColumnCount(); col++) {
						testCell.setLocation(col, r);
						testColumn.setLocation(col,-1);
						formatTable.remove(testCell);
						if (formatTable.containsKey(testColumn)) {
							formatTable.put(testCell, value);
						}
					}
				}
			}

			else if (cr.isColumn()) {
				// iterate through each column in the selection
				for(int c = cr.getMinColumn(); c <= cr.getMaxColumn(); ++c){

					// format the column
					formatTable.put(new Point(c,-1), value);

					// handle cells in the column with prior formatting 
					for (int row = 0; row < table.getRowCount(); row++) {

						testCell.setLocation(c, row);
						testRow.setLocation(-1,row);
						formatTable.remove(testCell);
						if (formatTable.containsKey(testRow)) {
							//	System.out.println(row);
							formatTable.put(testCell, value);
						}
					}
				}

			}

			else {
				//System.out.println("other");
				for (Point cellPoint : cr.toCellList(true))
					formatTable.put(cellPoint, value);
			}
		}
		table.repaint();		

		//System.out.println(formatTable.toString());


	}



	/**
	 * Returns the format object for a given cell and a given format. 
	 * If format does not exist, returns null.
	 */
	public Object getCellFormat(Point cellKey, int formatType){

		Object formatObject = null;

		Point rowKey = new Point(-1,cellKey.y);
		Point columnKey = new Point(cellKey.x,-1);

		// get the format table
		HashMap formatTable = formatTableArray[formatType];


		if(formatTable.containsKey(cellKey)){
			//System.out.println("found" + cellKey.toString());
			formatObject = formatTable.get(cellKey);
		}

		else if (formatTable.containsKey(rowKey)) {
			formatObject = formatTable.get(rowKey);
		}

		else if (formatTable.containsKey(columnKey)) {
			formatObject = formatTable.get(columnKey);
		}

		return formatObject;

	}

	public Object getCellFormat(CellRange cr, int formatType){

		// get the format in the upper left cell
		Point cell = new Point(cr.getMinColumn(), cr.getMinRow());
		Object format = (Integer) getCellFormat(cell, formatType);

		if(format == null) return null;

		// iterate through the range and test if they cells have the same format
		for (int r = 0; r > cr.getMaxRow(); r++){
			for(int c = 0; c > cr.getMaxColumn(); c++){
				cell.x = c;
				cell.y = r;
				if(!format.equals(getCellFormat(cell, formatType))){
					format = null;
					break;
				}
			}
		}
		return format;
	}


	public void setBorderStyle(CellRange cr, int borderStyle){

		int r1 = cr.getMinRow();
		int r2 = cr.getMaxRow();	
		int c1 = cr.getMinColumn();
		int c2 = cr.getMaxColumn();

		Point cell = new Point();
		Point cell2 = new Point();

		switch(borderStyle){
		case BORDER_STYLE_NONE:
			for(int r = r1; r<=r2; r++)
				for(int c = c1; c <=c2; c++)
					setFormat(cr,FORMAT_BORDER, null);
			break;
			
		case BORDER_STYLE_ALL:

			for(int r = r1; r<=r2; r++)
				for(int c = c1; c <=c2; c++){
					cell.x = c;
					cell.y = r;
					setFormat(cell,FORMAT_BORDER, BORDER_ALL);
				}
			break;
			
		case BORDER_STYLE_FRAME:
			
			// single cell
			if(r1 == r2 && c1 == c2) {
				cell.x = c1; cell.y = r1;
				setFormat(cell,FORMAT_BORDER, BORDER_ALL);
				return;
			}
			
			// top & bottom
			cell.y = r1; cell2.y = r2;
			for(int c = c1+1; c <=c2-1; c++){
				cell.x = c;
				cell2.x = c;
				if(r1 == r2){
					byte b = (int)BORDER_TOP + (int)BORDER_BOTTOM;
					setFormat(cell,FORMAT_BORDER, b);
				}else{
					setFormat(cell,FORMAT_BORDER, BORDER_TOP);
					setFormat(cell2,FORMAT_BORDER, BORDER_BOTTOM);
				}
			}
			// left & right
			cell.x = c1; cell2.x = c2;
			for(int r = r1+1; r <=r2-1; r++){
				cell.y = r;
				cell2.y = r;
				if(c1 == c2){
					byte b = (int)BORDER_LEFT + (int)BORDER_RIGHT;
					setFormat(cell,FORMAT_BORDER, b);
				}else{
					setFormat(cell,FORMAT_BORDER, BORDER_LEFT);
					setFormat(cell2,FORMAT_BORDER, BORDER_RIGHT);
				}
			}

			
			// CORNERS
			
			// case 1: column corners
			if(c1 == c2){
				cell.x = c1; cell.y = r1;
				byte b = (int)BORDER_LEFT + (int)BORDER_RIGHT + (int)BORDER_TOP;
				setFormat(cell,FORMAT_BORDER, b);
				
				cell.x = c1; cell.y = r2;
				b = (int)BORDER_LEFT + (int)BORDER_RIGHT + (int)BORDER_BOTTOM;
				setFormat(cell,FORMAT_BORDER, b);
			}
			// case 2: row corners
			else if(r1 == r2){
				cell.x = c1; cell.y = r1;
				byte b = (int)BORDER_LEFT + (int)BORDER_TOP + (int)BORDER_BOTTOM;
				setFormat(cell,FORMAT_BORDER, b);
				
				cell.x = c2; cell.y = r1;
				b = (int)BORDER_RIGHT + (int)BORDER_TOP + (int)BORDER_BOTTOM;
				setFormat(cell,FORMAT_BORDER, b);
				
			}
			
			// case 3: block corners
			else {
				cell.y = r1; cell.x = c1;
				byte b = (int)BORDER_LEFT + (int)BORDER_TOP;
				setFormat(cell,FORMAT_BORDER, b);

				cell.y = r1; cell.x = c2;
				b = (int)BORDER_RIGHT + (int)BORDER_TOP;
				setFormat(cell,FORMAT_BORDER, b);

				cell.y = r2; cell.x = c2;
				b = (int)BORDER_RIGHT + (int)BORDER_BOTTOM;
				setFormat(cell,FORMAT_BORDER, b);

				cell.y = r2; cell.x = c1;
				b = (int)BORDER_LEFT + (int)BORDER_BOTTOM;
				setFormat(cell,FORMAT_BORDER, b);
			}


			break;
			
		case BORDER_STYLE_INSIDE:
			// TODO --- inside style
			for(int r = r1; r<=r2; r++)
				for(int c = c1; c <=c2; c++)
				{
					cell.x = c;
					cell.y = r;
					setFormat(cell,FORMAT_BORDER, BORDER_ALL);
				}
			break;
			
		case BORDER_STYLE_TOP:
			cell.y = r1;
			for(int c = c1; c <=c2; c++){
				cell.x = c;
				setFormat(cell,FORMAT_BORDER, BORDER_TOP);
			}
			break;
			
		case BORDER_STYLE_BOTTOM:
			cell.y = r2;
			for(int c = c1; c <=c2; c++){
				cell.x = c;
				setFormat(cell,FORMAT_BORDER, BORDER_BOTTOM);
			}
			break;
			
		case BORDER_STYLE_LEFT:
			cell.x = c1;
			for(int r = r1; r <=r2; r++){
				cell.y = r;
				setFormat(cell,FORMAT_BORDER, BORDER_LEFT);
			}
			break;
			
		case BORDER_STYLE_RIGHT:
			cell.x = c2;
			for(int r = r1; r <=r2; r++){
				cell.y = r;
				setFormat(cell,FORMAT_BORDER, BORDER_RIGHT);
			}
			break;

		}


	}




}
