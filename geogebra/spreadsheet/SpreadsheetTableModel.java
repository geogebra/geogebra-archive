/**
 * 
 */
package geogebra.spreadsheet;
import java.awt.Point;

import geogebra.Application;
import geogebra.algebra.parser.Parser;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 * @author brisk1
 *
 */
public class SpreadsheetTableModel extends DefaultTableModel
{
    /** Stores file name of current document */
    private JTable table;
    /**
     * Stores modified state of document
     */
    private boolean modified;

    private Application app = null;

    public SpreadsheetTableModel(JTable table)
    {
        super();
        this.table=table;
        modified=false;
    }
    
    public SpreadsheetTableModel(JTable table, int numRows, int numColumns, Application app)
    {
        super(numRows, numColumns);
        this.app = app;
//        for (int row = 0; row < numRows; row++)
//           for (int col = 0; col < numColumns; col++)
//
//              // we initialize it here
//              super.setValueAt(new String(""), row, col);

        // initialize state to unmodified and file to untitled
        modified = false;
        this.table = table;
    }
    
    /**
     * This constructor is convenience for loading objects that are already in
     * an array. It fills the SharpTableModel with the objects in the array
     * making Object[0][0] be in cell A1, etc.
     * <P>
     * <B>Note: </B> This constructor does not assume that objects are of the
     * desired form. It will parse a string to see if it is a number or formula.
     *
     * @param sharp
     *            gui object to associate with this SharpTableModel
     * @param data
     *            the array of objects to place into the SharpTableModel
     */
    public SpreadsheetTableModel(JTable table, Object[][] data, Application app)
    {
       this(table, data.length, data[0].length,app);

       /* load the data */
       for (int i = 0; i < data.length; i++)
       {
          for (int j = 0; j < data[i].length; j++)
          {
             setValueAt(data[i][j], i, j);
          }
       }

       // initialize state to unmodified and file to untitled
       modified = false;
    }
    
    public String getLabel (int row, int col)
    {
        String lbl = "";
        lbl = getColName(col) + (row + 1);
        return lbl;
    }
    
    public String getColName( int col)
    {
        String colLbl = "";
        if( col >=0 && col <26)
        {
            char c = (char)( 'A' + col);
            colLbl = c + "";
        }
        else
        {
            System.out.println(col +" is greater than 25");
        }
        return colLbl;
    }
    
    public void setValueAt( Object obj, int row, int col)
    {
        GeoElement geo = null;
        if( !(obj instanceof GeoElement))
        {
            geo = (GeoElement)getValueAt( row, col);
            if( geo == null)
            {
                if( obj.toString().startsWith("="))
                {
                    //TODO: do the equation here
                    String str = getLabel(row, col) + obj.toString();
                    app.getKernel().getAlgebraProcessor().processAlgebraCommand( str, true );
                    fireTableDataChanged();
                }
                else
                {
                    String str = getLabel(row, col) + "=" + obj.toString();
                    
                    app.getKernel().getAlgebraProcessor().processAlgebraCommand( str, true );
                    fireTableDataChanged();
                }
            }
            else
            {
               if( obj != null)
               {
                   String newValue = obj.toString();
                   int index = newValue.indexOf("=");
                   if( index != -1)
                   {
                       // case of formula
                       newValue = newValue.substring(index + 1);
                       if ( geo.isIndependent()) {
                    	     // change geo, but don't redefine       
                    	     app.getKernel().getAlgebraProcessor().changeGeoElement(geo, newValue, false);  
                    	} else {
                    	    // redefine geo, note that redefining changes the entire construction and produces new GeoElement objects
                    	    GeoElement changedGeo = app.getKernel().getAlgebraProcessor().changeGeoElement(geo, newValue, true);
                    	//    updateGeoElement(geo, newValue);
                    	}         
                       
                   }
                   else
                   {
                	   if ( geo.isIndependent()) 
                	   {
                  	     // change geo, but don't redefine       
                  	     app.getKernel().getAlgebraProcessor().changeGeoElement(geo, newValue, false);  
                	   	} 
                	   else 
                	   {
                  	    // redefine geo, note that redefining changes the entire construction and produces new GeoElement objects
                  	    GeoElement changedGeo = app.getKernel().getAlgebraProcessor().changeGeoElement(geo, newValue, true);
                  	 //   updateGeoElement(geo, newValue);
                  	}      
                    //   updateGeoElement(geo, newValue);
                   }
               }
               else
               {
                   super.setValueAt(obj, row, col);
               }
                
            }
        }
        else
        {
            super.setValueAt(obj, row, col);
        }
    }

    /**
     * @param geo
     * @param newValue
     */
    private void updateGeoElement(GeoElement geo, String newValue)
    {
        newValue = newValue.trim();
        if (geo instanceof GeoNumeric)
        {
            GeoNumeric geoNum = (GeoNumeric) geo;
            try
            {
                geoNum.setValue(Double.parseDouble(newValue));
                geoNum.update();
            }
            catch (NumberFormatException nfe)
            {
                nfe.printStackTrace();
            }
        }
        else
        {
            //
        }
    }
    
    public Object getValueAt( int row, int col)
    {
        Object obj = super.getValueAt(row,col );
        if( !(obj instanceof GeoElement ))
        {
            //System.out.println("Getting a non-geo element");
          
        }
        return obj;
    }
    
    public boolean isCellEditable(int row, int col) {
        //GeoElement geo = (GeoElement) getValueAt(row, col);
        GeoElement geo = null;
        Object obj = getValueAt(row, col);
        if( !(obj instanceof GeoElement))
        {
         //   System.out.println("null");
            return true;
        }
        else
        {
            geo = (GeoElement )obj;
            return geo.isChangeable() || geo.isRedefineable();
        }
    }

}
