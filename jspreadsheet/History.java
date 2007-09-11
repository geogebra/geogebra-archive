package jspreadsheet;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEditSupport;


/** This is the class to support nearly-arbitrary undo/redo operations.
 * <p>
 * It's based on clipboard.  Each time a range of cells being changed,
 * they are saved in a clipboard and the clipboard is added to a linked
 * list.  A <b>current</b> pointer is maintained.
 * <ol>
 * <li> When a new clipboard is added, the objects after <b>current</b> are deleted
 *    and the new clipboard is added to the list and <b>current</b> is updated to
 *    point to the this new clipboard.</li>
 * <li> To undo, take out the object that <b>current</b> points to, and paste to the
 *    table.  But before the paste the table data is saved to replace the
 *    <b>current</b> clipboard.  Then move <b>current</b> one step backward</li>
 * <li> To redo, move current a step forward, take out the object <b>current</b>
 *    points to, and paste to the table.  But before the paste the table data
 *    is saved to replace the <b>current</b> clipboard.</li>
 * <li> Row/Column insertion/deletion are specially treated.<li>
 *</ol>
 *
 * @author Hua Zhong <huaz@cs.columbia.edu>
 * @version $Revision: 1.1 $
 */
class History extends UndoableEditSupport
{
   final public static int UNCHANGED = 0;
   final public static int INSERTROW = 1;
   final public static int INSERTCOLUMN = 2;
   final public static int REMOVEROW = 3;
   final public static int REMOVECOLUMN = 4;
   private SpreadsheetTableModel model;

   /**
    * Constructor:
    */
   History()
   {
   }

   /**
    * This adds the range of cells to the history.
    *
    * @param model the SharpTableModel we operate on
    * @param range the cell range the operation will affect
    */
   public void add(CellRange range)
   {
      // construct the clipboard to be saved
      SpreadsheetClipboard clip = new SpreadsheetClipboard(model, range, false);
      add(clip);
   }

   /**
    * This adds a clipboard object to the history list.
    *
    * @param model the SharpTableModel we operate on
    * @param range the cell range the operation will affect
    */
   public void add(SpreadsheetClipboard clip)
   {
      UndoableUpdate node = new UndoableUpdate(clip);
      if (Debug.isDebug())
      {
         Debug.println("Add history for range " + clip.getSource());
      }

      postEdit(node);

      // modified!
      // all operations should call add instead of setModified themselves
      model.setModified(true);
   }

   /*
    * This method should only be called with type != UNCHANGED,
    * i.e., for table insertion/deletion operations.
    *
    * @param model the SharpTableModel we operate on
    * @param range the cell range the operation will affect
    * @param type the operation type
    */
   public void add(CellRange range, int type)
   {
      SpreadsheetClipboard clip;
      UndoableUpdate node;

      if (type == UNCHANGED)
      {
         add(range);

         return;
      }

      if ((type == REMOVEROW) || (type == REMOVECOLUMN))
      {
         // save the current range
         clip = new SpreadsheetClipboard(model, range, false);
         node = new UndoableUpdate(clip);
      }
      else
      {
         // for insertion, no data need to be saved
         // just save the range value
         node = new UndoableUpdate(range);
      }

      node.setType(type);
      postEdit(node);
      model.setModified(true);

      //sharp.checkUndoRedoState();
   }

   void setTableModel(SpreadsheetTableModel model)
   {
      this.model = model;
   }

   /**
    * This is the redo method.
    *
    * @param model the SharpTableModel we operate on
    */
   private void redo(UndoableUpdate current)
   {
      int type = current.getType();
      CellRange range;

      if (type == UNCHANGED)
      {
         // get the saved clipboard
         SpreadsheetClipboard oldClip = (SpreadsheetClipboard) current.getObject();
         range = oldClip.getSource();

         // replace the current object with the current model data
         SpreadsheetClipboard newClip = new SpreadsheetClipboard(model, range, false);
         current.setObject(newClip);

         // restore data and selection
         oldClip.paste(model, range.getminCorner());
         model.setSelection(range);
      }
      else if ((type == REMOVEROW) || (type == REMOVECOLUMN))
      {
         // redo a removal
         SpreadsheetClipboard clip = (SpreadsheetClipboard) current.getObject();
         range = clip.getSource();

         // insert lines first
         if (type == REMOVEROW)
         {
            model.removeRow(range);
         }
         else
         {
            model.removeColumn(range);
         }
      }
      else
      {
         // redo an insertion
         range = (CellRange) current.getObject();

         if (type == INSERTROW)
         {
            model.insertRow(range);
         }
         else
         {
            model.insertColumn(range);
         }
      }

      model.setModified(true);
   }

   /**
    * This is the undo method.
    *
    * @param model the SharpTableModel we operate on
    */
   private void undo(UndoableUpdate current)
   {
      int type = current.getType();
      CellRange range;

      if (type == UNCHANGED)
      {
         // get the saved clipboard and its range
         SpreadsheetClipboard oldClip = (SpreadsheetClipboard) current.getObject();
         range = oldClip.getSource();

         // replace the current object with the current table data
         SpreadsheetClipboard newClip = new SpreadsheetClipboard(model, range, false);
         current.setObject(newClip);

         // recover the data (undo)
         oldClip.paste(model, range.getminCorner());
      }
      else if ((type == REMOVEROW) || (type == REMOVECOLUMN))
      {
         // undo a removal is just do an insertion and paste
         // saved data to it
         SpreadsheetClipboard clip = (SpreadsheetClipboard) current.getObject();
         range = clip.getSource();

         // insert lines first
         if (type == REMOVEROW)
         {
            model.insertRow(range);
         }
         else
         {
            model.insertColumn(range);
         }

         // then paste stuff
         clip.paste(model, range.getminCorner());
      }
      else
      {
         // undo an insertion is just a removal
         range = (CellRange) current.getObject();

         //	    System.out.println("Undo: remove "+range);
         if (type == INSERTROW)
         {
            model.removeRow(range);
         }
         else
         {
            model.removeColumn(range);
         }
      }

      // recover the selection
      model.setSelection(range);
      model.setModified(true);
   }

   class UndoableUpdate extends AbstractUndoableEdit
   {
      private Object obj; // could be a SharpClipboard or a CellRange
      private int type; // one of the 5 History static values defined below

      /** Creates a new instance of UndoableUpdate */
      public UndoableUpdate(Object obj)
      {
         setObject(obj);
      }

      public void redo() throws CannotRedoException
      {
         super.redo();
         History.this.redo(this);
      }

      public void undo() throws CannotUndoException
      {
         super.undo();
         History.this.undo(this);
      }

      void setObject(Object obj)
      {
         this.obj = obj;
      }

      // simple get/set functions
      Object getObject()
      {
         return obj;
      }

      void setType(int type)
      {
         this.type = type;
      }

      int getType()
      {
         return type;
      }
   }
}
