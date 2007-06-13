package tutor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseInterface {

	
	
	private String driver;
	private String url;
	private String user;
	private String pwd;



	
	public ResultSet execQuery( Connection con, String query )
	  {
	    try
	    {
	     // A partir de la connexió amb la base de dades creem un objecte Statment 
	     // (declaració)
	      Statement stmt = con.createStatement();
	      System.out.println( query );
	      
	    // Un cop creada la declaració podem dir quina comanda SQL es la que volem 
	    // executar: 	 
	    return( stmt.executeQuery( query ));
	     
	     // el mètode executeQuery ens retorna un Objecte del tipus ResultSet que conté 
	     // el resultat de la consulta feta
	      
	    }
	    catch( SQLException e )
	    {
	      System.err.println( "Query failed - " + e.getMessage());
	      return( null );
	    }
	  } // Fi Funcio execQuery
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
	public Connection connectURL ()
	{
		if (this.url!= null)
			return connectURL(this.url,this.user,this.pwd);
		else return null;
	}
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
	public DataBaseInterface(String driver, String url, String user, String pwd) {
		super();
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.pwd = pwd;
		this.loadDriver(driver);
		
	}
}
