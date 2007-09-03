package tutor;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class DataBaseInterface {

	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://158.109.2.26:3306/intermates";
	private static final String USER ="jmfortuny";
	private static final String PWD = "jmfortuny";
	
	private String driver = DRIVER;
	private String url= URL;
	private String user = USER;
	private String pwd = PWD;
    static String blanks = "                                   ";
    static String dashes = "-----------------------------------";

	/**
	 * Creates Url from a String
	 * @param fileArgument File name 
	 * @return URL for the file.
	 */
	 private URL handleFileArg(String fileArgument) {	     	      
	        try {             		        	
	        	String lowerCase = fileArgument.toLowerCase();
	            URL url=null;
	        	if (lowerCase.startsWith("http") || 
	            	lowerCase.startsWith("file")) {         
	                 url = new URL(fileArgument);                                        
	            } else {                       	
	                File f = new File(fileArgument);
	                f = f.getCanonicalFile();
	                if (f.exists())
	                	url = f.toURL();
	                else throw new Exception("File:"+ fileArgument +" doesn't exists");
	            }
	            return url;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	 /**
	  * Retrieves an array of Strategies for problem "problema"
	  * @param problema Id of the problem
	  * @return Array of stratgegies
	  */
	public Strategy[] retrieveStrategies(String problema)
	{
		try {
			LinkedList list = new LinkedList();
			Strategy[] result = new Strategy[0];
			Connection con = connectURL();
			ResultSet rs = execQuery(con, "select * from est_estrategies where id_problema= "+problema);
			while (rs.next())
			{
				Strategy str = new Strategy();
				str.setIdstrategy(rs.getLong("id"));
				str.setIdproblema(rs.getLong("id_problema"));
				str.setTitle(rs.getString("titol"));
				str.setUrl(handleFileArg(rs.getString("fitxer_estrategia")));
				list.add(str);
			}
			con.close();
			result  = (Strategy[]) list.toArray(result);
			return result;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
			return null;
		
	}
	/**
	 * Executes an SQL Query
	 * @param con An open Connection
	 * @param query SQL Query to the DataBase
	 * @return ResultSet with the results from DataBase.
	 */
	public ResultSet execQuery( Connection con, String query )
	  {
	    try
	    {
	     // A partir de la connexi— amb la base de dades creem un objecte Statment 
	     // (declaraci—)
	      Statement stmt = con.createStatement();
	      System.out.println( query );
	      
	    // Un cop creada la declaraci— podem dir quina comanda SQL es la que volem 
	    // executar: 	 
	    return( stmt.executeQuery( query ));
	     
	     // el mtode executeQuery ens retorna un Objecte del tipus ResultSet que contŽ 
	     // el resultat de la consulta feta
	      
	    }
	    catch( SQLException e )
	    {
	      System.err.println( "Query failed - " + e.getMessage());
	      return( null );
	    }
	  } // Fi Funcio execQuery
	
	/**
	 * To Get a connection from Database
	 * @param URL  URL from Database
	 * @param usr  user who connects with Database
	 * @param pswd  password of the user who connects whith Database
	 * @return An open connection to Database.
	 */
	public Connection connectURL( String URL,String usr, String pswd )
	  {
	    try
	    {
	      return( DriverManager.getConnection( URL,usr,pswd ));
	    }
	    catch( SQLException e )
	    {
	      System.err.println( "Can't connect - " + e.getMessage());
	      return( null );
	    }
	  }
	/**
	 * To get a connection from the deffault Database
	 * @return An open connection to Database
	 */
	public Connection connectURL ()
	{
		if (this.url!= null)
			return connectURL(this.url,this.user,this.pwd);
		else return null;
	}
	/**
	 * Load the Database driver
	 * @param driverName String containing the name for the database driver
	 * @return Return a static class
	 */
	 private Class loadDriver( String driverName )
	  {
	    try 
	    { 
	      return( Class.forName( driverName ));
	    }
	    catch( ClassNotFoundException e )
	    {
	      System.err.println( "Can't load driver - " + e.getMessage());
	      return( null );
	    }
	  }
	 
	
	 /**
	  * Constructor for Database Interface
	  * @param driver Database Connector driver
	  * @param url Database URL
	  * @param user Database User
	  * @param pwd  Database Password
	  */
	public DataBaseInterface(String driver, String url, String user, String pwd) {
		this.driver = driver;
		this.loadDriver(driver);
		this.url = url;
		this.user = user;
		this.pwd = pwd;
		
		
	}
	
	/**
	 * Default Constructor for Database Interface
	 *
	 */
	public DataBaseInterface(){
		this.loadDriver(driver);
		
		}
	 static String pad( String in, int len, String fill )
	  {
	      String result = in;

	      len -= in.length();

	      while( len > 0  )
	      {
		  int l;

		  if( len > fill.length())
		      l = fill.length();
		  else
		      l = len;

		result = result + fill.substring( 0, l );

		len -= l;
	      }

	      return( result );
	  }

	
	  static void printResultSet( ResultSet rs )
	    throws SQLException
	  {
	    int[] 	      sizes;
	    ResultSetMetaData rsmd     = rs.getMetaData();
	    int		      colCount = rsmd.getColumnCount();
	    int		      rowCount = 0;
	    
	    sizes = new int[colCount+1];

	    //
	    // Compute column widths
	    //
	    while( rs.next())
	    {
	      rowCount++;

	      for( int i = 1; i <= colCount; i++ )
	      {
		String val = rs.getString(i);
			  
		if(( rs.wasNull() == false ) && ( val.length() > sizes[i] ))
		  sizes[i] = val.length();
	      }
	    }

	    //
	    // Print column headers
	    //
	    for( int i = 1; i <= colCount; i++ )
	    {
	      if( rsmd.getColumnLabel(i).length() > sizes[i] )
		sizes[i] = rsmd.getColumnLabel(i).length();

	      System.out.print( pad( rsmd.getColumnLabel( i ), 
				     sizes[i], 
				     blanks ));

	      if( i < colCount )
		System.out.print( " | " );
	      else
		System.out.println();
	    }	

	    for( int i = 1; i <= colCount; i++ )
	    {
	      if( i < colCount )
		System.out.print( pad( "", sizes[i], dashes ) + "-+-" );
	      else
		System.out.println( pad( "", sizes[i], dashes ));		  
	    }	

	    //
	    //  Rewind the result set and print the contents
	    //
	    rs.beforeFirst();
	      
	    while( rs.next())
	    {
	      for( int i = 1; i <= colCount; i++ )
	      {
	        String val = rs.getString(i);
			  
		if( rs.wasNull())
		  val = "";

		if( i < colCount )
		  System.out.print( pad( val, sizes[i], blanks ) + " | " );
		else
		  System.out.println( pad( val, sizes[i], blanks ));
	      }
	    }
	  }





}
