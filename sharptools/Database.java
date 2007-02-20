package sharptools;
/*
 * @ (#)Database.java
 *
 * $Id: Database.java,v 1.1 2007-02-20 13:58:20 hohenwarter Exp $
 *
 * Created on May 19, 2001, 09:10:28 PM
 *
 * Tested with Oracle and Hsql
 *
 *  TODO
 *
 *  1) Add error handling - DONE
 *  2) Add a Save function
 *  3) Add to recent tables on menu
 *  4) Save connection params in props - DONE (saving fails, Why? Gotta study Config.java)
 *  5) Select from a list of tables - DONE
 *  6) Save from file to db and from db to file.
 *  7) Draw proper icons - DONE (could be better).
 *
 * This is not the most efficient implementation - just the easy way out.
 *
 * Currently the SharpTableModel knows how to create itself from a tab
 * delimited string so we simply construct one of these from the database
 * ResultSet and give it to the SharpTableModel. A better way would be to
 * teach SharpTableModel how to create itself from a ResultSet too.
 *
 */

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.sql.*;
import javax.swing.*;

/**
 * This contains database operations on the spreadsheet table
 *
 * @author  Shiraz Kanga
 * @version $Revision: 1.1 $
 */
public class Database
{
    // these variables correspond to the variables in SharpTools
    private SharpTools sharp;
    private JTable table;
    private SharpTableModel tableModel;
    final private static ImageIcon connectedIcon = SharpTools.getImageIcon ("database32.gif");
    //    final private static ImageIcon selectTableIcon = SharpTools.getImageIcon ("table32.gif");

    final private static ImageIcon dbErrorIcon = null;

    /**
     * constructor
     *
     * @param sharp the GUI object
     */
    Database (SharpTools sharp)
    {
	this.sharp = sharp;
	table = sharp.getTable ();
	tableModel = sharp.getTableModel ();
    }
    
    public void connectDb ()
    {
	String dbUsername = null;
	String dbPassword = null;
	String dbUrl = null;
	String dbDriver = null;
	String dbTable = null;
	String connectName = null;
	
	boolean lockTable;
	boolean verifySave;
	boolean newConnection = false;
	
	StringBuffer textBuf = new StringBuffer ();
	
	ConnectDialog connectDialog = new ConnectDialog (sharp);
	connectDialog.setVisible (true);
	if (connectDialog.isCancelled())
	    return;
	
	Connection dbConnection = connectDialog.getConnection();
	/*
	  connectName = connectDialog.getConnectName ();
	  
	  dbUsername = connectDialog.getDbUsername ();
	  dbPassword = connectDialog.getDbPassword ();
	  dbUrl = connectDialog.getDbUrl ();
	  dbDriver = connectDialog.getDbDriver ();  
	*/
	
	try {
	    DatabaseMetaData dma = dbConnection.getMetaData ();
		
	    SharpOptionPane.showMessageDialog (sharp, "Connected to database " + dma.getDatabaseProductName () + " v" + dma.getDatabaseProductVersion () + "\nusing driver " + dma.getDriverName () + " v" + dma.getDriverVersion (),
					       "Connected", JOptionPane.INFORMATION_MESSAGE, connectedIcon);
	    
	    if ((dbTable == null) || dbTable.equals (""))
		dbTable = selectDbTable (dma);

	    // need to check cancel - huaz
	    if (dbTable == null)
		return;
	    
	    Statement stmt = dbConnection.createStatement ();
	    ResultSet dbResults = stmt.executeQuery ("SELECT * FROM " + dbTable);
	    
	    String warnings = checkForWarning (dbConnection.getWarnings ());
	    if ((warnings!=null) && !warnings.equals (""))
		SharpOptionPane.showMessageDialog (sharp, warnings,
						   "Warning", JOptionPane.INFORMATION_MESSAGE, connectedIcon);
	    
	    // metadata can supply information about the schema
	    ResultSetMetaData rsmd = dbResults.getMetaData ();
	    int numCols = rsmd.getColumnCount ();
	    
	    // first print header labels from meta-data
	    for (int i=1; i<=numCols; i++) {
		if (i != 1) textBuf.append ("\t");
		textBuf.append (rsmd.getColumnLabel (i));
	    }
	    textBuf.append ("\n");
	    
	    while (dbResults.next ()) {
		// for one row
		for (int j=1; j<=numCols; j++) {
		    if (j != 1) textBuf.append ("\t");
		    textBuf.append (dbResults.getString (j));
		}
		textBuf.append ("\n");
	    }
	    
	    String text = textBuf.toString ();
	    // create new table model
	    CellPoint size = SharpTableModel.getSize (text);
	    //      System.out.println (size);
	    sharp.newTableModel (size.getRow (), size.getCol ());
	    tableModel = sharp.getTableModel ();
	    tableModel.fromString (text, 0, 0, new CellRange (1, size.getRow (), 1, size.getCol ()));
	    
	    // tableModel.setModified (false);
	    // set new title for spreadsheet
	    sharp.setTitle (dbTable + " - Sharp Tools Spreadsheet");
	    
	    // update recent files
	    //addRecentFile (dbTable);	    
	    
	    dbResults.close ();
	    stmt.close ();
	}
	catch (SQLException e) {
	    SharpOptionPane.showMessageDialog (sharp, "Unable to get data from the database.\n" + e.toString (),
					       "ERROR", JOptionPane.INFORMATION_MESSAGE, dbErrorIcon);
	    return;
	}
    }

    /*
     * check if the database server has anything to say
     */
    private String checkForWarning (SQLWarning warn)
	throws SQLException {
	StringBuffer textBuf = new StringBuffer ();
	
	if (warn != null) {
	    textBuf.append ("Warning:\n\n");
	    while (warn != null) {
		textBuf.append ("Message:  " + warn.getMessage () + "\n");
		textBuf.append ("SQLState: " + warn.getSQLState () + "\n");
		textBuf.append ("Vendor:   " + warn.getErrorCode () + "\n");
		warn = warn.getNextWarning ();
	    }
	}
	return textBuf.toString ();
    }
    
    private String selectDbTable (DatabaseMetaData dma)
	throws SQLException {
	// JDBC exposes must meta data as ResultSets
	// Change the second parameter below to retrieve information
	// about a particular schema in the database
	ResultSet dbResults = dma.getTables (null, null, "%", null);
	Vector vec = new Vector ();
	
	/*
	  Each table description row has the following columns:
	  TABLE_CAT String => table catalog (may be null)
	  TABLE_SCHEM String => table schema (may be null)
	  TABLE_NAME String => table name
	  TABLE_TYPE String => table type
	  Common types are "TABLE", "VIEW", "SYSTEM TABLE"
	  REMARKS String => explanatory comment on the table
	  We are only want column 3 (table name)
	*/
	
	while (dbResults.next ())
	    vec.addElement (dbResults.getString (3));

	if (vec.size() == 0) {
	    SharpOptionPane.showMessageDialog(sharp,
					      "This database has no tables defined.",
					      "Empty Database",
					      JOptionPane.WARNING_MESSAGE);
	    return null;
	}   
	
	Object[] possibleValues = new Object[vec.size ()];
	vec.copyInto (possibleValues);
	Object selectedValue = SharpOptionPane.showInputDialog(sharp,
							       "Please select the table you wish to load: ", "Select Table",
							       0,
							       /*selectTableIcon*/
							       connectedIcon,
							       possibleValues,
							       possibleValues[0]);
	if (selectedValue == null)
	    return null;
	else
	    return selectedValue.toString ();
    }
}




