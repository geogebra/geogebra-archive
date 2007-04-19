package tutor;

import geogebra.Application;
import geogebra.ConstructionProtocol;
import geogebra.MyError;
import geogebra.View;
import geogebra.io.MyXMLio;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TutorView implements View {
	
	
	private Application app;
	private MyXMLio xmlio;
	private LinkedList strategies;

	public TutorView (String[] strategiesXML,Application app)
	{
		this.app = app;
		strategies = new LinkedList();
			// PROCEED STRATEGIES FILES
			for (int i =0; i<strategiesXML.length; i++){
				// Try to load Strategies files;
				try {
				 URL url = handleFileArg(strategiesXML[i]); 
				 Construction c = getConstruction(url);
				 //System.out.println("Construction"+i+c.getXML());
				 strategies.add(c);
				} catch (Exception e) {					
					app.showError(app.getError("Strategies Loading Process Failed. ") 
										+ "\n" + e.getMessage());
				}
			}
	
	}
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
	                else new Exception("File doesn't exists");
	            }
	            return url;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	
	
	public void add(GeoElement geo) {
		// TODO Auto-generated method stub
		//Every GeoElement gets here.
		Construction c = geo.getConstruction();
		
		//TreeSet t = geo.getAllPredecessors();
		System.out.println("STR1"+ ((Construction)strategies.getFirst()).getXML());
		System.out.println("STR2"+ ((Construction)strategies.getLast()).getXML());
		System.out.println("CMD"+geo.getCommandDescription());
		System.out.println(geo+"/"+geo.getObjectType());
		//System.out.println(t);
		System.out.println(c.getXML());
		
		
		//ConstructionProtocol cp = c.getApplication().getConstructionProtocol();
		
	}

	public void clearView() {
		// TODO Auto-generated method stub

	}

	public void remove(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void rename(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void repaintView() {
		// TODO Auto-generated method stub

	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void update(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void updateAuxiliaryObject(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	private void compareConstruction(GeoElement geo)
	{
		
		
	}

	 private Construction getConstruction(URL url) throws Exception
	 {		 
	    	// build macro construction
		 
		    Kernel k = new Kernel(app); 
		   
	        k.setContinuous(true);
	    	
	    	xmlio = new MyXMLio(k,k.getConstruction());
	    	
	    	
	    	try {        	
	    			xmlio.readZipFromURL(url, false);
	    			k.initUndoInfo();
	    	} 
	    	catch (MyError e) {  
	    		String msg = e.getLocalizedMessage();
	    		System.err.println(msg);
	    		e.printStackTrace(); 
	    		throw new Exception(msg);
	    	}    	
	    	catch (Exception e) {
	    		e.printStackTrace();       		   
	        	throw new Exception(e.getMessage());
	    	}    	
	    	Construction c = k.getConstruction();
	    	int i=0;
	    	while (c.getConstructionElement(i)!= null)
	    	{
	    		System.out.println(c.getConstructionElement(i));
	    		i++;
	    	}
	    	
	    	//System.out.println(k.getConstruction().getXML());
	    	return k.getConstruction();
	 }
}

