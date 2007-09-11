package jspreadsheet;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;


/** This is the only editor for the SharpTableModel because it is the only
 * cell object editor. The only special feature is that it displays the
 * formula string of a formula cell when cell is being edited. It inherits
 * all methods of the DefaultCellEditor.
 *
 * @author Ricky Chin
 * @version $Revision: 1.1 $
 */
class SpreadsheetCellEditor extends DefaultCellEditor
{
   /** the JTextField object this editor uses
    */
   private JTextField textField;

   /**
    * Creates new SharpCellEditor.
    *
    * @param textField a JtextField object
    */
   public SpreadsheetCellEditor(final JTextField textField)
   {
      super(textField);
      this.textField = textField;

      /* To account for formula feature only need
       * to override the setValue method in EditorDelegate inner
       * class.
       */
      delegate = new EditorDelegate()
            {
               public void setValue(Object value)
               {
                  if (value instanceof Cell)
                  {
                     Cell temp = (Cell) value;

                     /* when editing formula cell
                      * a string representation is displayed
                      */
                     if (temp.isFormula())
                     {
                        textField.setText("=" + temp.getFormula().toString());
                     }
                     else
                     {
                        //otherwise it is just the normal string conversion
                        textField.setText(temp.getValue().toString());
                     }
                  }
                  else
                  {
                     //empty cells display nothing
                     textField.setText((value == null) ? "" : value.toString());
                  }
               }

               public Object getCellEditorValue()
               {
                  return textField.getText();
               }
            };
      textField.addActionListener(delegate);
   }

   /** get the component used by this editor
    * @return the JTextField used by this editor
    */
   public JTextField getTextField()
   {
      return textField;
   }
}
