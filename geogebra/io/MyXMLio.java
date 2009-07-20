/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * XMLFileReader.java
 *
 * Created on 09. Mai 2003, 16:05
 */

package geogebra.io;

import geogebra.GeoGebra;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.Kernel;
import geogebra.kernel.Macro;
import geogebra.main.Application;
import geogebra.util.Util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

/**
 * 
 * @author Markus Hohenwarter
 */
public class MyXMLio {

	// All xml output is zipped. The created zip archive contains
	// an entry named XML_FILE for the construction
	final private static String XML_FILE = "geogebra.xml";
	// Added for Intergeo File Format (Yves Kreis) -->
	final private static String I2G_FILE = "construction/intergeo.xml";
	// <-- Added for Intergeo File Format (Yves Kreis)

	// All xml output is zipped. The created zip archive contains
	// an entry named XML_FILE_MACRO for the macros
	final private static String XML_FILE_MACRO = "geogebra_macro.xml";

	// library javascript available to GeoJavaScriptButton objects
	final private static String JAVASCRIPT_FILE = "geogebra_javascript.js";

	// All xml output is zipped. The created zip archive *may* contain
	// an entry named XML_FILE_THUMBNAIL for the construction
	final private static String XML_FILE_THUMBNAIL = "geogebra_thumbnail.png";
	// Added for Intergeo File Format (Yves Kreis) -->
	final private static String I2G_FILE_THUMBNAIL = "construction/preview.png";
	// <-- Added for Intergeo File Format (Yves Kreis)
	final private static double THUMBNAIL_PIXELS_X = 200.0; // max no of
															// horizontal pixels
	final private static double THUMBNAIL_PIXELS_Y = 200.0; // max no of
															// vertical pixels

	// Added for Intergeo File Format (Yves Kreis) -->
	final private static String I2G_IMAGES = "resources/images/";
	final private static String I2G_PRIVATE = "private/org.geogebra/";
	final private static String I2G_PRIVATE_IMAGES = "private/org.geogebra/images/";
	// <-- Added for Intergeo File Format (Yves Kreis)

	// Use the default (non-validating) parser
	// private static XMLReaderFactory factory;

	private Application app;
	private Kernel kernel;
	// Modified for Intergeo File Format (Yves Kreis) -->
	// private MyXMLHandler handler;
	private DocHandler handler, ggbDocHandler, i2gDocHandler;
	// <-- Modified for Intergeo File Format (Yves Kreis)
	private QDParser xmlParser;
	// Added for Intergeo File Format (Yves Kreis) -->
	private Construction cons;

	// <-- Added for Intergeo File Format (Yves Kreis)

	public MyXMLio(Kernel kernel, Construction cons) {
		this.kernel = kernel;
		this.cons = cons;	
		app = kernel.getApplication();

		xmlParser = new QDParser();
		handler = getGGBHandler();
	}
	
	private DocHandler getGGBHandler() {
		if (ggbDocHandler == null)
		    //ggb3D : to create also a MyXMLHandler3D
			//ggbDocHandler = new MyXMLHandler(kernel, cons);		
			ggbDocHandler = kernel.newMyXMLHandler(cons);
		return ggbDocHandler;
	}
	
	private DocHandler getI2GHandler() {
		if (i2gDocHandler == null)
			i2gDocHandler = new MyI2GHandler(kernel, cons);		
		return i2gDocHandler;
	}


	/**
	 * Reads zipped file from input stream that includes the construction saved
	 * in xml format and maybe image files.
	 */
	public final void readZipFromInputStream(InputStream is, boolean isGGTfile)
			throws Exception {
		ZipInputStream zip = new ZipInputStream(is);

		// we have to read everything (i.e. all images)
		// before we process the XML file, that's why we
		// read the XML file into a buffer first
		byte[] xmlFileBuffer = null;
		byte[] macroXmlFileBuffer = null;
		boolean xmlFound = false;
		boolean macroXMLfound = false;

		// get all entries from the zip archive
		while (true) {
			ZipEntry entry = zip.getNextEntry();
			if (entry == null)
				break;

			String name = entry.getName();
			if (name.equals(XML_FILE)) {
				// load xml file into memory first
				xmlFileBuffer = Util.loadIntoMemory(zip);
				xmlFound = true;
				// Added for Intergeo File Format (Yves Kreis) -->
				handler = getGGBHandler();
			} else if (name.equals(I2G_FILE)) {
				// load i2g file into memory first
				xmlFileBuffer = Util.loadIntoMemory(zip);
				xmlFound = true;
				handler = getI2GHandler();
				// <-- Added for Intergeo File Format (Yves Kreis)
			} else if (name.equals(XML_FILE_MACRO)) {
				// load macro xml file into memory first
				macroXmlFileBuffer = Util.loadIntoMemory(zip);
				macroXMLfound = true;
			} else if (name.equals(JAVASCRIPT_FILE)) {
				// load JavaScript
				kernel.setLibraryJavaScript(Util.loadIntoString(zip));
			} else {
				// try to load image
				try {
					BufferedImage img = ImageIO.read(zip);
					app.addExternalImage(name, img);
				} catch (IOException e) {
					Application
							.debug("readZipFromURL: image could not be loaded: "
									+ name);
					e.printStackTrace();
				}
			}

			// get next entry
			zip.closeEntry();
		}
		zip.close();

		if (!isGGTfile) {
			// ggb file: remove all macros from kernel before processing
			kernel.removeAllMacros();
		}

		// process macros
		if (macroXmlFileBuffer != null) {
			// don't clear kernel for macro files
			processXMLBuffer(macroXmlFileBuffer, !isGGTfile, isGGTfile);
		}

		// process construction
		if (!isGGTfile && xmlFileBuffer != null) {
			processXMLBuffer(xmlFileBuffer, !macroXMLfound, isGGTfile);
		}

		if (!(macroXMLfound || xmlFound))
			throw new Exception("No XML data found in file.");
	}

	/**
	 * Get the preview image of a ggb file.
	 * 
	 * @param file
	 * @throws IOException
	 * @return
	 */
	public final static BufferedImage getPreviewImage(File file) throws IOException
	{
		// just allow preview images for ggb files
		if(!file.getName().endsWith(".ggb")) {
			throw new IllegalArgumentException("Preview image source file has to be of the type .ggb");
		}
		
		FileInputStream fis = new FileInputStream(file);
		ZipInputStream zip = new ZipInputStream(fis);
		BufferedImage result = null;
		
		// get all entries from the zip archive
		while (true) {
			ZipEntry entry = zip.getNextEntry();
			if (entry == null)
				break;
			
			if (entry.getName().equals(XML_FILE_THUMBNAIL))
			{
				result = ImageIO.read(zip);
				break;
			}

			// get next entry
			zip.closeEntry();
		}
		
		zip.close();
		fis.close();
		
		return result;
	}

	/**
	 * Handles the XML file stored in buffer.
	 * 
	 * @param buffer
	 */
	private void processXMLBuffer(byte[] buffer, boolean clearConstruction,
			boolean isGGTFile) throws Exception {
		// handle the data in the memory buffer
		ByteArrayInputStream bs = new ByteArrayInputStream(buffer);
		InputStreamReader ir = new InputStreamReader(bs, "UTF8");

		// process xml file
		doParseXML(ir, clearConstruction, isGGTFile);

		ir.close();
		bs.close();
	}

	private void doParseXML(Reader ir, boolean clearConstruction,
			boolean isGGTFile) throws Exception {
		boolean oldVal = kernel.isNotifyViewsActive();
		if (!isGGTFile) {
			kernel.setNotifyViewsActive(false);
		}

		if (clearConstruction) {
			// clear toolbar definition
			if (app.hasGuiManager()) {
				app.getGuiManager().setToolBarDefinition(null);
			}

			// clear construction
			kernel.clearConstruction();
		}

		try {
			xmlParser.parse(handler, ir);
			xmlParser.reset();
		} catch (Error e) {
			// e.printStackTrace();
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (!isGGTFile) {
				kernel.updateConstruction();
				kernel.setNotifyViewsActive(oldVal);				
			}
		}

		// handle construction step stored in XMLhandler
		// do this only if the construction protocol navigation is showing	
		if (!isGGTFile && oldVal &&
				app.showConsProtNavigation()) 
		{
				app.getGuiManager().setConstructionStep(handler.getConsStep());
		}
		
	}

	/**
	 * Reads from a zipped input stream that includes only the construction
	 * saved in xml format.
	 */
	public final void readZipFromMemory(InputStream is) throws Exception {
		ZipInputStream zip = new ZipInputStream(is);

		// get all entries from the zip archive
		ZipEntry entry = zip.getNextEntry();
		if (entry != null && entry.getName().equals(XML_FILE)) {
			// process xml file
			doParseXML(new InputStreamReader(zip, "UTF8"), true, false);
			zip.close();
		} else {
			zip.close();
			throw new Exception(XML_FILE + " not found");
		}
		
		System.gc();
	}

	public void processXMLString(String str, boolean clearAll, boolean isGGTfile)
			throws Exception {
		StringReader rs = new StringReader(str);
		doParseXML(rs, clearAll, isGGTfile);
		rs.close();
	}

	/**
	 * Creates a zipped file containing the construction and all settings saved
	 * in xml format plus all external images.
	 */
	public void writeGeoGebraFile(File file) throws IOException {
		// create file
		FileOutputStream f = new FileOutputStream(file);
		BufferedOutputStream b = new BufferedOutputStream(f);
		// Modified for Intergeo File Format (Yves Kreis) -->
		// writeGeoGebraFile(b);
		if (Application.getExtension(file)
				.equals(Application.FILE_EXT_INTERGEO)) {
			// File Extension for Intergeo: I2G
			writeIntergeoFile(b);
		} else {
			// File Extension for GeoGebra: GGB or GGT
			writeGeoGebraFile(b);
		}
		// <-- Modified for Intergeo File Format (Yves Kreis)
		b.close();
		f.close();
	}

	/**
	 * Creates a zipped file containing the construction and all settings saved
	 * in xml format plus all external images. GeoGebra File Format.
	 */
	public void writeGeoGebraFile(OutputStream os) throws IOException {
		// zip stream
		ZipOutputStream zip = new ZipOutputStream(os);
		OutputStreamWriter osw = new OutputStreamWriter(zip, "UTF8");

		// write construction images
		writeConstructionImages(kernel.getConstruction(), zip);

		// Modified for Intergeo File Format (Yves Kreis) -->
		// write construction thumbnails
		// writeThumbnail(kernel.getConstruction(), zip);
		writeThumbnail(kernel.getConstruction(), zip, XML_FILE_THUMBNAIL);
		// <-- Modified for Intergeo File Format (Yves Kreis)

		// save macros
		if (kernel.hasMacros()) {
			// get all registered macros from kernel
			ArrayList macros = kernel.getAllMacros();

			// write all images used by macros
			writeMacroImages(macros, zip);

			// write all macros to one special XML file in zip
			zip.putNextEntry(new ZipEntry(XML_FILE_MACRO));
			osw.write(getFullMacroXML(macros));
			osw.flush();
			zip.closeEntry();
		}

		// write library JavaScript to one special file in zip
		zip.putNextEntry(new ZipEntry(JAVASCRIPT_FILE));
		osw.write(kernel.getLibraryJavaScript());
		osw.flush();
		zip.closeEntry();

		// write XML file for construction
		zip.putNextEntry(new ZipEntry(XML_FILE));
		osw.write(getFullXML());
		osw.flush();
		zip.closeEntry();

		osw.close();
		zip.close();
	}

	/**
	 * Creates a zipped file containing the construction saved in xml and i2g
	 * format plus all external images. Intergeo File Format (Yves Kreis)
	 */
	public void writeIntergeoFile(OutputStream os) throws IOException {
		// zip stream
		ZipOutputStream zip = new ZipOutputStream(os);
		OutputStreamWriter osw = new OutputStreamWriter(zip, "UTF8");

		// write I2G file for construction
		zip.putNextEntry(new ZipEntry(I2G_FILE));
		osw.write(getFullI2G());
		osw.flush();
		zip.closeEntry();

		// write construction thumbnails
		writeThumbnail(kernel.getConstruction(), zip, I2G_FILE_THUMBNAIL);

		// write construction images
		writeConstructionImages(kernel.getConstruction(), zip,
				I2G_IMAGES);

		// save macros
		if (kernel.hasMacros()) {
			// get all registered macros from kernel
			ArrayList macros = kernel.getAllMacros();

			// write all images used by macros
			writeMacroImages(macros, zip, I2G_PRIVATE_IMAGES);

			// write all macros to one special XML file in zip
			zip.putNextEntry(new ZipEntry(I2G_PRIVATE + XML_FILE_MACRO));
			osw.write(getFullMacroXML(macros));
			osw.flush();
			zip.closeEntry();
		}

		// write XML file for construction
		zip.putNextEntry(new ZipEntry(I2G_PRIVATE + XML_FILE));
		osw.write(getFullXML());
		osw.flush();
		zip.closeEntry();

		osw.close();
		zip.close();
	}

	/**
	 * Creates a zipped file containing the given macros in xml format plus all
	 * their external images (e.g. icons).
	 */
	public void writeMacroFile(File file, ArrayList macros) throws IOException {
		if (macros == null)
			return;

		// create file
		FileOutputStream f = new FileOutputStream(file);
		BufferedOutputStream b = new BufferedOutputStream(f);
		writeMacroStream(b, macros);
		b.close();
		f.close();
	}

	/**
	 * Writes a zipped file containing the given macros in xml format plus all
	 * their external images (e.g. icons) to the specified output stream.
	 */
	public void writeMacroStream(OutputStream os, ArrayList macros)
			throws IOException {
		// zip stream
		ZipOutputStream zip = new ZipOutputStream(os);
		OutputStreamWriter osw = new OutputStreamWriter(zip, "UTF8");

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
	// Modified for Intergeo File Format (Yves Kreis) -->
	private void writeConstructionImages(Construction cons, ZipOutputStream zip)
			throws IOException {
		writeConstructionImages(cons, zip, "");
	}

	private void writeConstructionImages(Construction cons,
			ZipOutputStream zip, String filePath) throws IOException {
		// <-- Modified for Intergeo File Format (Yves Kreis)
		// save all GeoImage images
		TreeSet images = cons.getGeoSetLabelOrder(GeoElement.GEO_CLASS_IMAGE);
		if (images == null)
			return;

		Iterator it = images.iterator();
		while (it.hasNext()) {
			GeoImage geoImage = (GeoImage) it.next();
			// Michael Borcherds 2007-12-10 this line put back (not needed now
			// MD5 code put in the correct place!)
			String fileName = geoImage.getFileName();
			BufferedImage img = geoImage.getImage();
			if (img != null)
				// Modified for Intergeo File Format (Yves Kreis) -->
				// writeImageToZip(zip, fileName, img);
				writeImageToZip(zip, filePath + fileName, img);
			// <-- Modified for Intergeo File Format (Yves Kreis)
		}
	}

	/**
	 * Writes thumbnail to zip. Michael Borcherds 2008-04-18
	 */
	// Modified for Intergeo File Format (Yves Kreis) -->
	// private void writeThumbnail(Construction cons, ZipOutputStream zip)
	// throws IOException {
	private void writeThumbnail(Construction cons, ZipOutputStream zip,
			String fileName) throws IOException {
		// <-- Modified for Intergeo File Format (Yves Kreis)

		EuclidianView ev = app.getEuclidianView();

		// max 128 pixels either way
		double exportScale = Math.min(THUMBNAIL_PIXELS_X
				/ ev.getSelectedWidth(), THUMBNAIL_PIXELS_Y
				/ ev.getSelectedHeight());
		try {
			BufferedImage img = app.getEuclidianView().getExportImage(exportScale);
			if (img != null)
				// Modified for Intergeo File Format (Yves Kreis) -->
				// writeImageToZip(zip, XML_FILE_THUMBNAIL, img);
				writeImageToZip(zip, fileName, img);
			// <-- Modified for Intergeo File Format (Yves Kreis)
		} catch (Exception e) { } // catch error if size is zero

	}

	/**
	 * Writes all images used in the given macros to zip.
	 */
	// Modified for Intergeo File Format (Yves Kreis) -->
	private void writeMacroImages(ArrayList macros, ZipOutputStream zip)
			throws IOException {
		writeMacroImages(macros, zip, "");
	}

	private void writeMacroImages(ArrayList macros, ZipOutputStream zip,
			String filePath) throws IOException {
		// <-- Modified for Intergeo File Format (Yves Kreis)
		if (macros == null)
			return;

		for (int i = 0; i < macros.size(); i++) {
			// save all images in macro construction
			Macro macro = (Macro) macros.get(i);
			// Modified for Intergeo File Format (Yves Kreis) -->
			// writeConstructionImages(macro.getMacroConstruction(), zip);
			writeConstructionImages(macro.getMacroConstruction(), zip, filePath);
			// <-- Modified for Intergeo File Format (Yves Kreis)

			// save macro icon
			String fileName = macro.getIconFileName();
			BufferedImage img = app.getExternalImage(fileName);
			if (img != null)
				// Modified for Intergeo File Format (Yves Kreis) -->
				// writeImageToZip(zip, fileName, img);
				writeImageToZip(zip, filePath + fileName, img);
			// <-- Modified for Intergeo File Format (Yves Kreis)
		}
	}

	private void writeImageToZip(ZipOutputStream zip, String fileName,
			BufferedImage img) {
		// create new entry in zip archive
		try {
			zip.putNextEntry(new ZipEntry(fileName));
		} catch (Exception e) {
			// if the same image file is used more than once in the construction
			// we get a duplicate entry exception: ignore this
			return;
		}

		writeImageToStream(zip, fileName, img);
	}

	public void writeImageToStream(OutputStream os, String fileName,
			BufferedImage img) {
		// if we get here we need to save the image from the memory
		try {
			// try to write image using the format of the filename extension
			int pos = fileName.lastIndexOf('.');
			String ext = fileName.substring(pos + 1).toLowerCase(Locale.US);
			if (ext.equals("jpg") || ext.equals("jpeg"))
				ext = "JPG";
			else
				ext = "PNG";
			ImageIO.write(img, ext, os);
		} catch (Exception e) {
			Application.debug(e.getMessage());
			try {
				// if this did not work save image as png
				ImageIO.write(img, "png", os);
			} catch (Exception ex) {
				Application.debug(ex.getMessage());
				return;
			}
		}
	}

	/**
	 * Compresses xml String and writes result to os.
	 */
	public static void writeZipped(OutputStream os, StringBuffer xmlString) throws IOException {
		ZipOutputStream z = new ZipOutputStream(os);
		z.putNextEntry(new ZipEntry(XML_FILE));
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(z, "UTF8"));
		for (int i=0; i < xmlString.length(); i++) {
			w.write(xmlString.charAt(i));
		}		
		w.close();
		z.close();
	}

	/**
	 * Returns XML representation of all settings and construction. GeoGebra
	 * File Format.
	 */
	public String getFullXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\">\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(false));		

		// save construction
		sb.append(kernel.getConstructionXML());

		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns I2G representation of construction. Intergeo File Format. (Yves
	 * Kreis)
	 */
	public String getFullI2G() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\"?>\n");

		sb.append("<!--\n\tIntergeo File Format Version "
				+ GeoGebra.I2G_FILE_FORMAT + "\n\twritten by "
				+ app.getPlain("ApplicationName") + " "
				+ GeoGebra.VERSION_STRING + " (" + GeoGebra.BUILD_DATE
				+ ")\n-->\n");

		sb.append("<construction>\n");

		// save construction
		sb.append(kernel.getConstructionI2G());

		StringBuffer display = new StringBuffer();
		display.append(kernel.getDisplayI2G());
		if (!display.toString().equals("")) {
			sb.append("\t<display>\n");
			sb.append(display.toString());
			sb.append("\t</display>\n");
		}

		sb.append("</construction>\n");

		return sb.toString();
	}

	/**
	 * Returns XML representation of all settings WITHOUT construction.
	 */
	public String getPreferencesXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb
				.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT
						+ "\">\n");

		// save gui settings
		sb.append(app.getCompleteUserInterfaceXML(true));		

		sb.append("</geogebra>");
		return sb.toString();
	}

	/**
	 * Returns XML representation of given macros in the kernel.
	 */
	public String getFullMacroXML(ArrayList macros) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT
						+ "\">\n");

		// save construction
		sb.append(kernel.getMacroXML(macros));

		sb.append("</geogebra>");
		return sb.toString();
	}

	

	
	
	/**
	 * Returns XML representation of all settings and construction needed for
	 * undo.
	 */
	public static synchronized StringBuffer getUndoXML(Construction c) {
		Application app = c.getApplication();

		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		sb.append("<geogebra format=\"" + GeoGebra.XML_FILE_FORMAT + "\">\n");

		// save euclidianView settings
		//String addStr = app.getEuclidianView().getXML();
		String addStr = app.getEuclidianViewsXML();
		sb.ensureCapacity(sb.length() + addStr.length());
		sb.append(addStr);
		

		// save construction
		addStr = c.getXML();
		sb.ensureCapacity(sb.length() + addStr.length());
		sb.append(addStr);

		// save cas session
		if (app.hasCasView()) {
			addStr = app.getCasView().getSessionXML();
			sb.ensureCapacity(sb.length() + addStr.length());
			sb.append(addStr);
		}
			
		sb.ensureCapacity(sb.length() + 20);
		sb.append("</geogebra>");

		/*
		 * Application.debug("*******************");
		 * Application.debug(sb.toString());
		 * Application.debug("*******************");
		 */

		return sb;
	}

	/*
	 * 
	 * public static void main(String [] args) { String [] formats =
	 * ImageIO.getWriterFormatNames(); for (int i=0; i < formats.length; i++) {
	 * Application.debug(formats[i]); } }
	 */
}
