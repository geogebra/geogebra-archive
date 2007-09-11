package jspreadsheet;


/** This clipboard class holds the objects that will be cut and pasted to
 * a spreadsheet. Actually, it creates a string representation of the range
 * of cell objects that is compatible with Microsoft Excel and the Windows
 * clipboard. It also remembers the range of the cells in the table from
 * which it was created. This class is used for many table manipulation
 * methods.
 * @author Ricky Chin
 * @version $Revision: 1.1 $
 */
class SpreadsheetClipboard
{
   /** the range of Cells from which the clipboard was created
    */
   private CellRange source;

   /** holds the objects that are cut or copied
    */
   private String text;

   /** Creates new SharpClipboard. If it is a cut, then it clears the range it
    * was created from.
    * @param model the SharpTableModel you are operating on
    * @param range an array of CellPoint objects where the first is the
    *       upper left hand corner and the second entry is the
    *       lower right hand corner coordinates
    * @param isCut true only if this is a cut
    */
   public SpreadsheetClipboard(SpreadsheetTableModel model, CellRange range, boolean isCut)
   {
      text = model.toString(range, false, '\t');

      source = range;

      //if it is a cut, set the old cells to null
      if (isCut)
      {
         model.clearRange(range);
      }
   }

   /** This gets the actual range of a paste from a corner point. This is
    * actually a helper method for paste
    * @param corner the upper left corner coordinate
    * @return the actual cell range; null if it's beyond the table range
    * @param model the SharpTableModel you are using
    */
   public CellRange getRange(SpreadsheetTableModel model, CellPoint corner)
   {
      //limit to paste region
      int rowLimit = model.getRowCount() - 1;
      int colLimit = model.getColumnCount() - 1;

      //calculate dimensions of clipboard
      int rowMax = (corner.getRow() + source.getHeight()) - 1;
      int colMax = (corner.getCol() + source.getWidth()) - 1;

      //cannot paste to nonexistent cells
      if ((corner.getRow() < 0) || (corner.getCol() < 0))
      {
         return null;
      }
      else
      {
         //paste as much as you can
         return new CellRange(corner, new CellPoint(Math.min(rowMax, rowLimit), Math.min(colMax, colLimit)));
      }
   }

   /** Set the source of the clipboard
    * @param x CellRange to set as the source of the clipboard
    */
   public void setSource(CellRange x)
   {
      source = x;
   }

   /** This method returns the range the clipboard originally came from
    * @return the range the clipboard originally can from
    */
   public CellRange getSource()
   {
      return source;
   }

   /** This pastes the current contents of the spreadsheet object
    * on to the region defined by the coordinates of the upper
    * right hand corner. If the contents cannot be entirely pasted
    * on this region, it pastes as much as it can
    * @param table SharpTable model you are pasting to
    * @param corner coordinate of upper left hand corner
    */
   public void paste(SpreadsheetTableModel table, CellPoint corner)
   {
      //if region to paste to is out of bounds
      CellRange range = getRange(table, corner);
      paste(table, range);
   }

   /** This is similar to the other paste, but take range as a parameter.
    * This is used together with getRange.
    * @param table SharpTableModel you are pasting to
    * @param range range you are pasting to
    */
   public void paste(SpreadsheetTableModel table, CellRange range)
   {
      //if region to paste to is out of bounds
      if (range != null)
      {
         int rowOff = range.getStartRow() - source.getStartRow();
         int colOff = range.getStartCol() - source.getStartCol();
         table.fromString(text, '\t', rowOff, colOff, range);
      }
   }

   /** Return the string representation of contents of the clipboard
    * @return the string representation of contents of the clipboard
    */
   public String toString()
   {
      return text;
   }
}
