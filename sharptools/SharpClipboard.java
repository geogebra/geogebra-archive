package sharptools;
/*
 * @(#)SharpClipboard.java
 *
 * $Id: SharpClipboard.java,v 1.1 2007-02-20 13:58:20 hohenwarter Exp $
 *
 * Created on October 27, 2000, 9:40 PM
 */

/** This clipboard class holds the objects that will be  cut and pasted to
 * a spreadsheet. Actually, it creates a string representation of the range
 * of cell objects that is compatible with Microsoft Excel and the Windows
 * clipboard. It also remembers the range of the cells in the table from
 * which it was created. This class is used for many table manipulation
 * methods.
 * @author Ricky Chin
 * @version $Revision: 1.1 $
 */
public class SharpClipboard {

    /** holds the objects that are cut or copied
     */
    private String text;
    /** the range of Cells from which the clipboard was created
     */
    private CellRange source;
    
    /** Creates new SharpClipboard. If it is a cut, then it clears the range it
     * was created from.
     * @param model the SharpTableModel you are operating on
     * @param range an array of CellPoint objects where the first is the
     *       upper left hand corner and the second entry is the
     *       lower right hand corner coordinates
     * @param isCut true only if this is a cut
     */
    public SharpClipboard(SharpTableModel model, CellRange range,
			  boolean isCut) {
	//        board = table.getRange(range);
	text = model.toString(range, false);

        source = range;
        
        //if it is a cut, set the old cells to null
        if (isCut) {
            model.clearRange(range);
        }
    }
    
    /** This gets the actual range of a paste from a corner point. This is
     * actually a helper method for paste
     * @param corner the upper left corner coordinate
     * @return the actual cell range; null if it's beyond the table range
     * @param model the SharpTableModel you are using
     */
    public CellRange getRange(SharpTableModel model, CellPoint corner) {
        //limit to paste region
        int rowLimit = model.getRowCount() - 1;
        int colLimit = model.getColumnCount() - 1;
        
        //calculate dimensions of clipboard
        int rowMax = corner.getRow() + source.getHeight() - 1;
        int colMax = corner.getCol() + source.getWidth() - 1;
        
        //cannot paste to nonexistent cells
        if ((corner.getRow() < SharpTools.baseRow) ||
	    (corner.getCol() < SharpTools.baseCol)) {
	    return null;
        }else {
            //paste as much as you can
	    return new CellRange(corner,
				 new CellPoint(Math.min(rowMax, rowLimit),
					       Math.min(colMax, colLimit)));
        }
    }
            
    /** This pastes the current contents of the spreadsheet object
     * on to the region defined by the coordinates of the upper
     * right hand corner. If the contents cannot be entirely pasted
     * on this region, it pastes as much as it can
     * @param table SharpTable model you are pasting to
     * @param corner coordinate of upper left hand corner
     */
    public void paste(SharpTableModel table, CellPoint corner) {
        //if region to paste to is out of bounds
	CellRange range = getRange(table, corner);
	paste(table, range);
    }

    /** This is similar to the other paste, but take range as a parameter.
     * This is used together with getRange.
     * @param table SharpTableModel you are pasting to
     * @param range range you are pasting to
     */
    public void paste(SharpTableModel table, CellRange range) {
	System.out.println(range);
		      //		      boolean byValue) {
        //if region to paste to is out of bounds
	if (range != null) {
	    int rowOff = range.getStartRow()-source.getStartRow();
	    int colOff = range.getStartCol()-source.getStartCol();
	    //	    table.setRange(range, board, byValue);
	    table.fromString(text, rowOff, colOff, range);
	}
    }            
    
    /** This method returns the range the clipboard originally can from
     * @return the range the clipboard originally can from
     */
    public CellRange getSource() {
        return source;
    }
    
    /** Set the source of the clipboard
     * @param x CellRange to set as the source of the clipboard
     */
    public void setSource(CellRange x) {
        source = x;
    }

    /** Return the string representation of contents of the clipboard
     * @return the string representation of contents of the clipboard
     */
    public String toString() { return text; }
}


