/**
 * 
 */
package geogebra.spreadsheet;
import java.util.TreeSet;
import geogebra.Application;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.Iterator;
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
   // ArrayList copyGeo;
    GeoElement copyGeo= null;
    int flag=0;
    

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
    
   /*Used to set value of a geoelement in a cell in the spreadsheet*/
    public void setValueAt( Object obj, int row, int col)
    {
        GeoElement geo = null;
        // input string
        if(obj instanceof String)
        {
        	String inputString = ((String) obj).trim();           	
            geo = (GeoElement)getValueAt( row, col);
                        
            // delete old cell object if empty input string
            if (inputString.length() == 0) {
            	if (geo != null) geo.remove();
            	return;
            }
            
            // cell is empty at the moment:
            if( geo == null )
            {
            	String str;
                if( inputString.startsWith("=") )
                {
                    //TODO: do the equation here
                    str = getLabel(row, col) + inputString;                   
                }
                else 
                {
                     str = getLabel(row, col) + "=" + inputString;                    
                }
                                
                app.getKernel().getAlgebraProcessor().processAlgebraCommand( str, true );
                fireTableDataChanged();
            }
            
            // we have a GeoElement in this cell already:
            else
            {
              
               String newValue = inputString;                  
               if( inputString.startsWith("=") )
               {
                   // case of formula
                   newValue = newValue.substring(1);
               }
              
               if ( geo.isIndependent()) {
          	     // change geo, but don't redefine       
          	     	app.getKernel().getAlgebraProcessor().changeGeoElement(geo, newValue, false);  
              	} else {
              	    // redefine geo, note that redefining changes the entire construction and produces new GeoElement objects
              	    app.getKernel().getAlgebraProcessor().changeGeoElement(geo, newValue, true);
              	}               
            
            }
        }
        else
        {
            super.setValueAt(obj, row, col);
        }
    }
      
    /**
     * Copies cell (fromRow, fromCol) to cell (toRow, toCol).
     */
    public void paste(int fromRow, int fromCol, int toRow, int toCol)
    {
    	GeoElement geo= (GeoElement)getValueAt(fromRow,fromCol);
    	//Check if the geoElement is dependent on other GeElements(Relative Copy)
    	if(geo != null && !geo.isIndependent())
    	{
    
    		String geoAll = geo.getDefinitionDescription();
            processGeoElement(toRow - fromRow,toCol - fromCol, geo, toRow, toCol);
    		
    	}
    	else
    	{//Independent case
    	//Get the range of rows and columns and loop around them
    	
	    	if(geo != null)
	    	{
	    	copyGeo = geo.copyInternal(geo.getConstruction());
			String lbl = getLabel(toRow,toCol);
			copyGeo.setLabel(lbl);
	    	}
    	}
    }
    /*Takes the dependent elements in a formula and replaces it with the new elements
      and in the respective cells*/
    private void processGeoElement(int rowdiff,int coldiff, GeoElement geo, int toRow, int toCol) 
    {
    	boolean createNew = true;
    	TreeSet geoTree = geo.getAllPredecessors();
		String geoAll = geo.getDefinitionDescription();
		System.out.println("The elements are\n"+geoAll);
		int size = geoTree.size();
		System.out.println("Size of GeoTree is\n"+size);
		Iterator it = geoTree.iterator();
		
		// change geoAll String to protect variable names like A1, A2, ...
		// add @ sign in front of all cell variable names 
		// e.g. "A1 + A2 + b" is changed to "@A1 + @A2 + b"
		 while(it.hasNext())
		{
		 	// for all in geoTree 
		 	//Get the GeoElement
			GeoElement newgeo =(GeoElement)it.next();
            //Iterate through ist of all predecessors and replace with a new string
			 String l = newgeo.getLabel();
			 int col = GeoElement.getSpreadsheetColumn( l );
	         int row = GeoElement.getSpreadsheetRow( l );
			 if( row == -1 || col == -1)
	                continue;
			 geoAll = geoAll.replaceAll(l, "@" + l);
		}
   
	    // change geoAll String to result value
		// now transform "@A1 + @A2 + b" for example into "A2 + A3 + b" 
		it = geoTree.iterator();
        while(it.hasNext())
		{
			//Get the GeoElement
			GeoElement newgeo =(GeoElement)it.next();
            //Iterate through ist of all predecessors and replace with a new string
			String l = newgeo.getLabel();
            System.out.println("geo label ="+l);
            int col = GeoElement.getSpreadsheetColumn( l );
            int row = GeoElement.getSpreadsheetRow( l );
            if( row == -1 || col == -1)
                continue;
            System.out.println("col ="+col + " row = " + row);
            String newLabel = getLabel(row + rowdiff, col + coldiff);
            System.out.println("new label ="+newLabel);
           
            Kernel kernel = app.getKernel();
            geo = kernel.lookupLabel(newLabel);
            if( geo == null)
            {
                System.out.println(newLabel + " is not defined.");
                createNew = false;                
                break;              
            }
            
            geoAll = geoAll.replaceAll("@" + l, newLabel);
          
		}
        System.out.println("new formula = " + geoAll);
			
	
		// only paste if there is a GeoElement with this name
		if (createNew ) {			
			GeoElement[] geoArray =app.getKernel().getAlgebraProcessor().processAlgebraCommand( geoAll, true );    					
	    	String newLabel = getLabel(toRow,toCol);
	    	geoArray[0].setLabel(newLabel);
        }
        else
        {
            System.out.println("create new is false");
        }
	}
  
	public Object getValueAt( int row, int col)
    {
        Object obj = super.getValueAt(row,col );
        if( !(obj instanceof GeoElement ))
        {
       //     System.out.println("Getting a non-geo element");
          
        }
        return obj;
    }
    
    public boolean isCellEditable(int row, int col) {
        //GeoElement geo = (GeoElement) getValueAt(row, col);
        GeoElement geo = null;
        Object obj = getValueAt(row, col);
        if( !(obj instanceof GeoElement))
        {
            return true;
        }
        else
        {
            geo = (GeoElement )obj;
            return geo.isChangeable() || geo.isRedefineable();
        }
    }
}
