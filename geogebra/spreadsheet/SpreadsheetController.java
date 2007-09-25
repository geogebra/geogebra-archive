/**
 * 
 */
package geogebra.spreadsheet;
import geogebra.Application;
import geogebra.kernel.GeoElement;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import geogebra.kernel.*;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
/**
 * @author Amy Mathew Varkey
 *
 */
public class SpreadsheetController extends JPanel implements CellEditorListener 
{
    private Application app;
    private JTable table;
    
     /**
     * 
     */
    public SpreadsheetController(Application app, JTable table){
        this.app=app;
        this.table = table;
    }
   
    /*
     * CellEditorListener implementation 
    */
    public void editingCanceled(ChangeEvent event) {
    }

    public void editingStopped(ChangeEvent event) {
        //TODO: why is string returned here null?
       
        // get the entered String
        String inputStr = table.getCellEditor().getCellEditorValue().toString();
        
        // TODO: remove
        System.out.println("editingStopped: inputStr = " + inputStr);
        
        
        // TODO: make sure to set GeoElement into this cell again, otherwise we have a String here
        // compare to MyDefaultTreeCellEditor in AlgebraView
        //      the userObject was changed to this String
        // reset it to the old userObject, which we stored
        // in selectedGeoElement (see valueChanged())        
        // only nodes with a GeoElement as userObject can be edited!        
        //selectedNode.setUserObject(selectedGeoElement);
        
        if (inputStr == null || inputStr.length() == 0) return;

        // TODO: if String starts with = we need to add the name of the resulting
        // GeoElement
        if (inputStr.charAt(0) == '=') {
            inputStr = inputStr.substring(1);   // remove leading = sign, CHANGE THIS
        }
       
        
        // change this GeoElement in the Kernel                  
        GeoElement [] geo = app.getKernel().getAlgebraProcessor().
            processAlgebraCommand(inputStr, true);  
           
    }

  
}
