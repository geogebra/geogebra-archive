package jspreadsheet;

import java.util.EventListener;


/** A listener for spreadsheet selection events */
public interface SpreadsheetSelectionListener extends EventListener
{
   /** Called when the selected cells change
    * @param e The selection event
    */
   void selectionChanged(SpreadsheetSelectionEvent e);
}
