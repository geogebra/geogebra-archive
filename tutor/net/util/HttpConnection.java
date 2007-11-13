package tutor.net.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * @author albert
 *
 */
public class HttpConnection {

	private BufferedReader in = null;
	private HttpURLConnection connection = null;
	
	/**
	 * 
	 */
	public HttpConnection() {
	}
	
	/**
	 * 
	 * @param url
	 * @throws IOException
	 */
	public void connect(URL url) throws IOException
	{
		connection = (HttpURLConnection)url.openConnection();
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			in = new BufferedReader
				(new InputStreamReader(connection.getInputStream()));
		} 
		else {
			connection.disconnect();
			throw new IOException(connection.getResponseMessage());
		}
	}
	
	/**
	 * 
	 * @param location
	 * @throws IOException
	 */
	public void connect(String location) throws IOException
	{
		connect(new URL(location));
	}
	
	/**
	 * 
	 */
	public void close()
	{
		connection.disconnect();
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException
	{
		return connection.getInputStream();
	}
	
	/**
	 * 
	 * @return
	 */
	public String nextLine() {
		if (in == null) {
		    return null;
		}
	
		String line = null;
		try {
			line = in.readLine();
		} 
		catch(IOException e) {
		}
	
		if (line == null) {
			connection.disconnect();
		}
	
		return line;
	}
	
	public static void save(InputStream in, String filename) throws IOException
	{
		byte[] b = new byte[500];
		
		OutputStream out = new FileOutputStream(filename, false);
		
		int c = 0;
		while (c != -1) {
			out.write(b, 0, c);
			c = in.read(b);
		}
		
		out.close();
	}

}