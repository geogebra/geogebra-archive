package geogebra.export;

import geogebra.GeoGebra;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public class GeoGebraTubeExport {
	/**
	 * URL of the webpage to call if a file should be uploaded.
	 */
	private static final String uploadURL = "http://geogebra-tube/upload";
	
	/**
	 * Application instance.
	 */
	private Application app;
	
	/**
	 * Progress bar dialog.
	 */
	private JDialog progressDialog;
	
	/**
	 * Constructs a new instance of the GeoGebraTube exporter.
	 * 
	 * @param app
	 */
	public GeoGebraTubeExport(Application app) {
		this.app = app;
	}
	
	/**
	 * Upload the current worksheet to GeoGebraTube.
	 * 
	 * @throws IOException If uploading failed.
	 */
	public void uploadWorksheet() throws IOException {
		try {
			URL url;
		    URLConnection urlConn;
		    DataOutputStream printout;
		    BufferedReader input;
		    
			url = new URL(uploadURL);
			urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);

			// content type
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			// send output
			try {
				printout = new DataOutputStream(urlConn.getOutputStream());
				
				// build post query
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("data=");
				stringBuffer.append(URLEncoder.encode(getBase64String(), "UTF-8"));
				stringBuffer.append("&version=");
				stringBuffer.append(URLEncoder.encode(GeoGebra.VERSION_STRING, "UTF-8"));
				printout.writeBytes(stringBuffer.toString());
				
				// send data
				printout.flush();
				printout.close();
				
				stringBuffer = null;
				
				// get response and read it into a string buffer 
				input = new BufferedReader(new InputStreamReader(urlConn
						.getInputStream()));
				
				StringBuffer output = new StringBuffer();
				
				String line;
				while (null != ((line = input.readLine()))) {
					output.append(line);
				}
				
				// TODO parse output
				
				input.close();
			} catch (IOException e) {
				throw e;
			}
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Append a base64 encoded .ggb file to the passed string buffer. 
	 * 
	 * @throws IOException
	 */
	private String getBase64String() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		app.getXMLio().writeGeoGebraFile(baos, false);
		return geogebra.util.Base64.encode(baos.toByteArray(), 0);
	}
	
	/**
	 * Shows a small dialog with a progress bar. 
	 */
	public void showProgressBar() {
		progressDialog = new JDialog();
		progressDialog.setTitle(app.getMenu("UploadGeoGebraTube"));
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(progressBar);
		progressDialog.add(panel);
		
		progressDialog.pack();
		progressDialog.setVisible(true);
		
		progressDialog.setLocationRelativeTo(null);
	}
	
	/**
	 * Hides progress dialog.
	 */
	public void hideProgressBar() {
		if(progressDialog != null) {
			progressDialog.setVisible(false);
		}
	}
}
