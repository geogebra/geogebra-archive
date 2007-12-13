/**
 * 
 */
package geogebra.spreadsheet;
import java.awt.Point;
import java.util.ArrayList;
import java.util.TreeSet;

import geogebra.Application;
import geogebra.algebra.parser.Parser;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoNumeric;

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
              	//    updateGeoElement(geo, newValue);
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
    		TreeSet geoTree = geo.getAllPredecessors();
    		String geoAll = geo.getDefinitionDescription();
    		System.out.println("The elements are\n"+geoAll);
    		int size = geoTree.size();
    		System.out.println("Size of GeoTree is\n"+size);
    		Iterator it = geoTree.iterator();
    		while(it.hasNext())
    		{
    			//Do the processing
    			GeoElement geoIt = (GeoElement) it.next();
    			if(geoIt != null)
    			{
    			//	Point loc =getCellLocation(geoAll.substring(0, 2));
    				Point loc =getCellLocation(geoAll.substring(0, 2));
    				if(toRow>fromRow)
    				{
    					int diff = toRow - fromRow;
    					Point newLoc = new Point();
    					newLoc.setLocation((loc.x+diff), loc.y);
    					System.out.println("Old Location are \n"+loc.x +loc.y);
    					System.out.println("New Label is\n"+newLoc.x +newLoc.y);
    					String newPos1 = getLabel(newLoc.x, newLoc.y);
    		
    					geoAll = geoAll.replaceAll(geoIt.getLabel(), newPos1);
    					System.out.println("Old label is\n"+geoIt.getLabel());
    					System.out.println("New Label is \n"+newPos1);
    					System.out.println("New geoAll is\n"+geoAll);
    					GeoElement[] geoArray =app.getKernel().getAlgebraProcessor().processAlgebraCommand( geoAll, true );
    					String newLabel = getLabel(toRow,toCol);
    					geoArray[0].setLabel(newLabel);
    					toRow++;
    					fromRow++;
    				}
    				else if(toRow < fromRow)
    				{
    					int diff = fromRow - toRow;
    					Point newLoc = new Point();
    					newLoc.setLocation((loc.x-diff), loc.y);
    					System.out.println("Old Location are \n"+loc.x +loc.y);
    					System.out.println("New Label is\n"+newLoc.x +newLoc.y);
    					String newPos1 = getLabel(newLoc.x, newLoc.y);
    		
    					geoAll = geoAll.replaceAll(geoIt.getLabel(), newPos1);
    					System.out.println("Old label is\n"+geoIt.getLabel());
    					System.out.println("New Label is \n"+newPos1);
    					System.out.println("New geoAll is\n"+geoAll);
    					GeoElement[] geoArray =app.getKernel().getAlgebraProcessor().processAlgebraCommand( geoAll, true );
    					String newLabel = getLabel(toRow,toCol);
    					geoArray[0].setLabel(newLabel);
    					toRow--;
    					fromRow--;
    					
    				}
    				else if(toCol > fromCol)
    				{
    					int diff = toCol - fromCol;
    					Point newLoc = new Point();
    					newLoc.setLocation(loc.x, (loc.y+diff));
    					System.out.println("Old Location are \n"+loc.x +loc.y);
    					System.out.println("New Label is\n"+newLoc.x +newLoc.y);
    					String newPos1 = getLabel(newLoc.x, newLoc.y);
    		
    					geoAll = geoAll.replaceAll(geoIt.getLabel(), newPos1);
    					System.out.println("Old label is\n"+geoIt.getLabel());
    					System.out.println("New Label is \n"+newPos1);
    					System.out.println("New geoAll is\n"+geoAll);
    					GeoElement[] geoArray =app.getKernel().getAlgebraProcessor().processAlgebraCommand( geoAll, true );
    					String newLabel = getLabel(toRow,toCol);
    			//		geoArray[0].setLabel(newLabel);
    					geoArray[0].setLabel(newLabel);
    					toCol++;
    					fromCol++;
    				}
    				else if(toCol < fromCol)
    				{
    					
    				}
    				else if(toRow>fromRow && toCol > fromCol)
    				{
    					int diffx = toRow - fromRow;
    					int diffy = toCol - fromCol;
    					Point newLoc = new Point();
    					newLoc.setLocation((loc.x+diffx), (loc.y+diffy));
    					System.out.println("Old Location are \n"+loc.x +loc.y);
    					System.out.println("New Label is\n"+newLoc.x +newLoc.y);
    					String newPos1 = getLabel(newLoc.x, newLoc.y);
    		
    					geoAll = geoAll.replaceAll(geoIt.getLabel(), newPos1);
    					System.out.println("Old label is\n"+geoIt.getLabel());
    					System.out.println("New Label is \n"+newPos1);
    					System.out.println("New geoAll is\n"+geoAll);
    					GeoElement[] geoArray =app.getKernel().getAlgebraProcessor().processAlgebraCommand( geoAll, true );
    					String newLabel = getLabel(toRow,toCol);
    					geoArray[0].setLabel(newLabel);
    					toRow++;
    					fromRow++;
    					toCol++;
    					fromCol++;
    				}
    				
    			}
    		}
    		
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
    
    /*Function which will return the position of a geoElement in the spreadsheet*/
    
    public static Point getCellLocation(String label)
    {
    	Point coords = new Point();
    	if(label.length()> 1 && Character.isLetter(label.charAt(0))
    			&& Character.isDigit(label.charAt(1)))
    	{
    		int column = Character.getNumericValue(label.charAt(0)) - Character.getNumericValue('A');
			int row = Integer.parseInt(label.charAt(1) + "") - 1;
			coords.setLocation(row, column);
    	}
    	return coords;
    }

}
