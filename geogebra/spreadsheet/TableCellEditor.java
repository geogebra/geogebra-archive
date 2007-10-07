package geogebra.spreadsheet;
/**
 * @author Amy Mathew Varkey
 *Oct 4 2007
 */
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TableCellEditor extends DefaultCellEditor{
	
	 public TableCellEditor(final JTextField textField) {
		 
	        super(textField); 
	 }
		 
	 public Component getTableCellEditorComponent(JTable table, Object value, boolean selected, int row, int column)
    {
        String str = null;
        if (value instanceof GeoNumeric)
        {
            GeoElement geo = (GeoElement) value;
            if (geo.isChangeable())
            {
                str = geo.toValueString();
            }
            else
            {
                str = geo.getCommandDescription();
            }
        }
        String stringValue;
        if (str == null)
        {
            stringValue = (value == null) ? "" : value.toString();
        }
        else
        {
            stringValue = str;
        }
        Component  c = this.getComponent();
        if( c instanceof JTextField)
        {
            ((JTextField)c).setText(stringValue);
        }
        delegate.setValue(stringValue);

        //return editorComponent;
        return c;
    }
//	 public Object getCellEditorValue() 
//	 {
//		 
//	 }
	 /** stores currently selected GeoElement and node.
		 *  selectedNode, selectedGeoElement are private members of AlgebraView
		 */
//		private void storeSelection(TreePath tp) {
//			if (tp == null)
//				return;
//
//			Object ob;
//			textField = (DefaultMutableTreeNode) tp.getLastPathComponent();
//			if (selectedNode != null
//				&& (ob = selectedNode.getUserObject()) instanceof GeoElement) {
//				selectedGeoElement = (GeoElement) ob;
//			} else {
//				selectedGeoElement = null;
//			}
//		}
}
