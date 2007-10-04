/**
 * 
 */
package geogebra.spreadsheet;
import geogebra.Application;
import geogebra.kernel.GeoElement;


import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
/**
 * @author Amy Mathew Varkey
 *
 */
public class SpreadsheetController extends JPanel implements CellEditorListener 
{
    private Application app;
    private JTable table;
    private GeoElement geo;
    private SpreadsheetTableModel model;
    private GeoElement selectedGeoElement;
     /**
     * 
     */
    public SpreadsheetController(Application app, JTable table, SpreadsheetTableModel model){
        this.app=app;
        this.table = table;
        this.model=model;
    }
   
    public GeoElement getSelectedGeoElement() {
		return selectedGeoElement;
	}
    /*
     * CellEditorListener implementation 
    */
    public void editingCanceled(ChangeEvent event) {
    }

    public void editingStopped(ChangeEvent event) {
           
    	GeoElement geo=null;
        // get the entered String
       // String inputStr = table.getCellEditor().getCellEditorValue().toString();
    	 String inputStr = (String)(table.getDefaultEditor(this.getClass()).getCellEditorValue());
    	 Object obj = event.getSource();
        
        // TODO: remove
        System.out.println("editingStopped: inputStr = " + inputStr);
        
    //    geo=;
        // TODO: make sure to set GeoElement into this cell again, otherwise we have a String here
        // compare to MyDefaultTreeCellEditor in AlgebraView
        //      the userObject was changed to this String
        // reset it to the old userObject, which we stored
        // in selectedGeoElement (see valueChanged())        
        // only nodes with a GeoElement as userObject can be edited!        
        //selectedNode.setUserObject(selectedGeoElement);
        //model=(SpreadsheetTableModel)table.getModel();
        
        Object comp=table.getDefaultEditor(this.getClass()).getTableCellEditorComponent(table, (Object)inputStr, true, 1, 1);
        if (inputStr == null || inputStr.length() == 0) return;
        
        // TODO: if String starts with = we need to add the name of the resulting
        // GeoElement
        if (inputStr.charAt(0) == '=') {
            inputStr = inputStr.substring(1);   // remove leading = sign, CHANGE THIS
        }
       
     //   model.setValueAt(obj, model.get, col);
        // change this GeoElement in the Kernel                  
    //     geo = app.getKernel().getAlgebraProcessor().changeGeoElement(geo, inputStr, false);
          
        
        //assign it to the cell in the selected location
        
           
    }
    
	

  
}
