/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

/*
 * XMLFileReader.java
 *
 * Created on 09. Mai 2003, 16:05
 */

package geogebra.io;

import geogebra.Application;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.util.Util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

/**
 *
 * @author  Markus Hohenwarter
 */
public class MyXMLio {
    
    // All xml output is zipped. The created zip archive contains
    // an entry named XML_FILE for the construction
    final private static String XML_FILE = "geogebra.xml";
    
    // All xml output is zipped. The created zip archive contains
    // an entry named XML_FILE_MACRO for the macros 
    final private static String XML_FILE_MACRO = "geogebra_macro.xml";  
    
    // Use the default (non-validating) parser
    //private static XMLReaderFactory factory;
    
    private Application app;
    private Kernel kernel;
    private MyXMLHandler handler;
    private QDParser xmlParser;
    
    public MyXMLio(Kernel kernel, Construction cons) {
        this.kernel = kernel;
        app = kernel.getApplication();  
	
        // similar to SAX event handler
        handler = new MyXMLHandler(kernel, cons);
        xmlParser = new QDParser();   
    }
    
    /**
     * Reads zipped file from url that includes the construction saved 
     * in xml format and maybe image files.
     */    
    public final void readZipFromURL(URL url, boolean isMacroFile) throws Exception {      	
    	BufferedInputStream bis = new BufferedInputStream(url.openStream());	
        readZipFromInputStream(bis, isMacroFile);
        bis.close();
    }

    /**
     * Reads zipped file from input stream that includes the construction saved 
     * in xml format and maybe image files.
     */    
    public final void readZipFromInputStream(InputStream is, boolean isMacroFile) throws Exception {      	    	
        ZipInputStream zip = new ZipInputStream(is);
                
        // we have to read everything (i.e. all images)
        // before we process the XML file, that's why we
        // read the XML file into a buffer first
        byte [] xmlFileBuffer = null;        
        byte [] macroXmlFileBuffer = null;
        boolean xmlFound = false;
        boolean macroXMLfound = false;
                
        // get all entries from the zip archive        
        while (true) {        	
        	ZipEntry entry = zip.getNextEntry();
        	if (entry == null) break;
        	
        	String name = entry.getName();                	        	
        	if (name.equals(XML_FILE)) {
        	    // load xml file into memory first
        		xmlFileBuffer = Util.loadIntoMemory(zip);  
        		xmlFound = true;
        	} 
        	else if (name.equals(XML_FILE_MACRO)) {
        	    // load macro xml file into memory first
        		macroXmlFileBuffer = Util.loadIntoMemory(zip);     
        		macroXMLfound = true;
        	} 
        	else {
        		// try to load image        		
        		try {        			
        			BufferedImage img = ImageIO.read(zip);        			
        			app.addExternalImage(name, img);
        		} catch (IOException e) {
        			System.err.println("readZipFromURL: image could not be loaded: " + name);
        			e.printStackTrace();
        		}
        	}
        	
        	// get next entry
        	zip.closeEntry();        	
        }
        zip.close();          
                
        if (!isMacroFile) {
        	// ggb file: remove all macros from kernel before processing
        	kernel.removeAllMacros();
        }
                               
        // process macros
        if (macroXmlFileBuffer != null) {        	
        	// don't clear kernel for macro files
        	processXMLBuffer(macroXmlFileBuffer, !isMacroFile);        	
        }  
        
        // process construction
        if (!isMacroFile && xmlFileBuffer != null) {
        	processXMLBuffer(xmlFileBuffer, !macroXMLfound);
        } 
        
        if (!(macroXMLfound || xmlFound)) 
			throw new Exception("No XML data found in file.");   
    }
    
    /**
     * Handles the XML file stored in buffer.
     * @param buffer
     */
    private void processXMLBuffer(byte [] buffer, boolean clearConstruction) throws Exception {
    	// handle the data in the memory buffer 
		ByteArrayInputStream bs = new ByteArrayInputStream(buffer); 		
		InputStreamReader ir = new InputStreamReader(bs, "UTF8");
		
        // process xml file  		  
		doParseXML(ir, clearConstruction);            
        
        ir.close();
        bs.close();        
    }
    
    private void doParseXML(Reader ir, boolean clearConstruction) throws Exception {    
    	boolean oldVal = kernel.isNotifyViewsActive();    	 
    	kernel.setNotifyViewsActive(false);
		
		if (clearConstruction)
			kernel.clearConstruction();	
		
		try {		    
			xmlParser.parse(handler, ir);				
		} catch (Error e) {
			//e.printStackTrace();						 
			 throw e;
		} catch (Exception e) {			 
			 throw e;
		} finally {
			kernel.updateConstruction();	
	     	kernel.setNotifyViewsActive(oldVal);	
		}
			    			     	     
     	// handle construction step stored in XMLhandler     	
     	if (app.showConsProtNavigation() && oldVal) // do this only if the views are active
     		app.getConstructionProtocol().setConstructionStep(handler.getConsStep());     		
    }
    
    /**
     * Reads from a zipped input stream that includes only the construction saved 
     * in xml format.
     */    
    public final void readZipFromMemory(InputStream is) throws Exception {             	
        ZipInputStream zip = new ZipInputStream(is);
                        
        // get all entries from the zip archive
        ZipEntry entry = zip.getNextEntry();
        if (entry != null && entry.getName().equals(XML_FILE)) {
        	// process xml file      		   		
    		doParseXML(new InputStreamReader(zip, "UTF8"), true);                      
        	zip.close();       
        } else {
        	zip.close();   
        	throw new Exception(XML_FILE + " not found");
        }   
    }
    
    public void processXMLString(String str, boolean clearAll) throws Exception {         
        StringReader rs = new StringReader(str);          
        doParseXML(rs, clearAll);                                    
        rs.close();   
    }
    
    /**
     * Creates a zipped file containing the construction and all settings
     * saved in xml format plus all external images.
     */
    public void writeGeoGebraFile(File file) throws IOException {   
    	// create file
        FileOutputStream f = new FileOutputStream(file);
        BufferedOutputStream b = new BufferedOutputStream(f);

        // zip stream
        ZipOutputStream zip = new ZipOutputStream(b);  
        OutputStreamWriter osw = new OutputStreamWriter(zip,  "UTF8");       
        
        // write construction images
        writeConstructionImages(kernel.getConstruction(), zip);
        
        // save macros
        if (kernel.hasMacros()) {
        	// get all registered macros from kernel
        	Macro [] macros = kernel.getAllMacros();
        	
        	// write all images used by macros
            writeMacroImages(macros, zip);
 
            // write all macros to one special XML file in zip
        	zip.putNextEntry(new ZipEntry(XML_FILE_MACRO));                       
            osw.write(getFullMacroXML(macros)); 
            osw.flush();
            zip.closeEntry();
        }        
        
        // write XML file for construction
        zip.putNextEntry(new ZipEntry(XML_FILE));           
        osw.write(getFullXML());        
        osw.flush();
        zip.closeEntry();
                    
        osw.close();
        zip.close();           
        b.close();      
        f.close();
    } 
    
    /**
     * Creates a zipped file containing the given macros 
     * in xml format plus all their external images (e.g. icons).
     */
    public void writeMacroFile(File file, Macro [] macros) throws IOException {   
    	if (macros == null) return;
    	
    	// create file
        FileOutputStream f = new FileOutputStream(file);
        BufferedOutputStream b = new BufferedOutputStream(f);
        writeMacroStream(b, macros);           
        b.close(); 
        f.close();
    } 
    
    /**
     * Writes a zipped file containing the given macros 
     * in xml format plus all their external images (e.g. icons)
     * to the specified output stream.
     */
    public void writeMacroStream(OutputStream os, Macro [] macros) throws IOException {
    	  // zip stream
        ZipOutputStream zip = new ZipOutputStream(os);  
        OutputStreamWriter osw = new OutputStreamWriter(zip,  "UTF8");       
        
        // write images
        writeMacroImages(macros, zip);
        
        // write macro XML file        
    	zip.putNextEntry(new ZipEntry(XML_FILE_MACRO));                       
        osw.write(getFullMacroXML(macros)); 
        osw.flush();
        zip.closeEntry();                
                    
        osw.close();
        zip.close();             
    }
    
    /** 
     * Writes all images used in construction to zip.
     */
    private void writeConstructionImages(Construction cons, ZipOutputStream zip) throws IOException {    	
    	// save all GeoImage images    	    
    	TreeSet images = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
    	if (images == null) return;
    	
    	Iterator it = images.iterator();
    	while (it.hasNext()) {    	    		
    		GeoImage geoImage = (GeoImage) it.next();    		    		    			
    		String fileName = geoImage.getFileName();    
			BufferedImage img = geoImage.getImage();
    		if (img != null) 	       
    			writeImageToZip(zip, fileName, img);
	    }    	    	
    }
    
    /** 
     * Writes all images used in the given macros to zip.
     */
    private void writeMacroImages(Macro [] macros, ZipOutputStream zip) throws IOException {
    	if (macros == null) return;
    	
    	for (int i=0; i < macros.length; i++) {
	    	// save all images in macro construction 
	    	writeConstructionImages(macros[i].getMacroConstruction(), zip);
	    	
	    	// save macro icon
	    	String fileName = macros[i].getIconFileName();   
			BufferedImage img = app.getExternalImage(fileName);
			if (img != null)
				writeImageToZip(zip, fileName, img);
    	}
    }
    
    
    private void writeImageToZip(ZipOutputStream zip, String fileName, BufferedImage img) {
    	// create new entry in zip archive
		try {
			zip.putNextEntry(new ZipEntry(fileName));
		} catch (Exception e) {
			// if the same image file is used more than once in the construction
			// we get a duplicate entry exception: ignore this
			return;	
		}    		    				
		
		try {
        	// try to write image using the format of the filename extension
        	int pos = fileName.lastIndexOf('.');
        	String ext = fileName.substring(pos+1).toLowerCase();
        	if (ext.equals("jpg") || ext.equals("jpeg"))
        		ext = "JPG";
        	else 
        		ext = "PNG";	        	
        	ImageIO.write(img, ext, zip);          		
		} catch (Exception e) {    						
			try {
				//	if this did not work save image as png
				ImageIO.write(img, "png", zip);    
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
				return;	
			}   
		}		
    }
          
    
    /**
     * Compresses xml String and writes result to os.    
     */
    public static void writeZipped(OutputStream os, String xmlString) 
    throws IOException {                                                                
        ZipOutputStream z = new ZipOutputStream(os);
        z.putNextEntry(new ZipEntry(XML_FILE));
        OutputStreamWriter osw = new  OutputStreamWriter(z,  "UTF8");
        osw.write(xmlString);                       
        osw.close();                
        z.close();
    }    
    
    /**
     * Returns XML representation of all settings and construction.
     */ 
    public String getFullXML() {    	    	
        StringBuffer sb = new StringBuffer();            
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<geogebra format=\"" + Application.XML_FILE_FORMAT + "\">\n");
                
        // save gui settings
        sb.append(app.getUserInterfaceXML());       
        
        // save euclidianView settings
        sb.append(app.getEuclidianView().getXML());  
        
        // save construction
        sb.append(kernel.getConstructionXML());  
        
        sb.append("</geogebra>");
        return sb.toString();            
    }
    
    /**
     * Returns XML representation of all settings WITHOUT construction.
     */ 
    public String getPreferencesXML() {    	    	
        StringBuffer sb = new StringBuffer();            
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<geogebra format=\"" + Application.XML_FILE_FORMAT + "\">\n");
                
        // save gui settings
        sb.append(app.getUserInterfaceXML());       
        
        // save euclidianView settings
        sb.append(app.getEuclidianView().getXML());                  
        
        sb.append("</geogebra>");
        return sb.toString();            
    }
    
    /**
     * Returns XML representation of given macros in the kernel.
     */ 
    public String getFullMacroXML(Macro [] macros) {    	    	
        StringBuffer sb = new StringBuffer();            
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<geogebra format=\"" + Application.XML_FILE_FORMAT + "\">\n");
        
        // save construction
        sb.append(kernel.getMacroXML(macros));  
        
        sb.append("</geogebra>");
        return sb.toString();            
    }
    
    /**
     * Returns XML representation of all settings and construction needed for undo.
     */ 
    public String getUndoXML(Construction c) {      
        StringBuffer sb = new StringBuffer();            
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
        sb.append("<geogebra format=\"" + Application.XML_FILE_FORMAT + "\">\n");                
        
        // save euclidianView settings
        sb.append(app.getEuclidianView().getXML());
        
        // save construction
        sb.append(c.getXML());      
        
        sb.append("</geogebra>");
        

       /*
        System.out.println("*******************");
        System.out.println(sb.toString());
        System.out.println("*******************");
        */
        
        return sb.toString();            
    }
    
    /*
    
    public static void main(String [] args) {
    	String [] formats = ImageIO.getWriterFormatNames();
    	for (int i=0; i < formats.length; i++) {
    		System.out.println(formats[i]);
    	}
    }*/
}
