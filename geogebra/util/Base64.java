package geogebra.util;
import java.util.prefs.*;
 
/** Utility class that encodes and decodes byte arrays in Base64 representation.
* Uses underlying functionality in the java.util.prefs package of the Java API.  
* Note that this is slightly different from RFC 2045; ie, there are no CRLFs in the encoded string.
* Should be thread safe.
* Requires Java 1.4 or better.
*/ 
public class Base64 extends AbstractPreferences 
{
	private String store;
	private static Base64 instance=new Base64(); 
	
	/**Hide the constructor; this is a singleton. */
	private Base64() 
	{   
		super(null,"");
	}
 
	/**Given a byte array, return its Base64 representation as a String. */ 
	public static synchronized String encode(byte[] b)
	{   
		instance.putByteArray(null, b);   
		return instance.get(null,null);
	}
 
	/**Given a String containing a Base64 representation, return the corresponding byte array. */ 
	public static synchronized byte[] decode(String base64String)
	{   
		instance.put(null,base64String);   
		return instance.getByteArray(null, null);   
	}
 
	public String get(String key, String def) 
	{   
		return store;
	}
 
	public void put(String key, String value)
	{   
		store=value;
	}
 
	//Other methods required to implement the abstract class;  these methods are not used.
	protected AbstractPreferences childSpi(String name){return null;}
	protected void putSpi(String key,String value){}
	protected String getSpi(String key){return null;}
	protected void removeSpi(String key){}
	protected String[] keysSpi()throws BackingStoreException {return null;}
	protected String[] childrenNamesSpi()throws BackingStoreException{return null;}
	protected void syncSpi()throws BackingStoreException{}
	protected void removeNodeSpi()throws BackingStoreException{}
	protected void flushSpi()throws BackingStoreException{}
 

}