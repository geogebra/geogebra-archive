package jspreadsheet;

import java.awt.Component;

import java.util.EventObject;


/** An event fired when the range of cells selected in a spreadsheet changes */
public class SpreadsheetSelectionEvent extends EventObject
{
   private CellRange range;

   SpreadsheetSelectionEvent(Component source, CellRange range)
   {
      super(source);
      this.range = range;
   }

   /** The selected range of cells
    * @return The selected range, or null if no cells are selected
    */
   public CellRange getSelectionRange()
   {
      return range;
   }
}
