/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.export;

import geogebra.euclidian.EuclidianView;
import geogebra.export.epsgraphics.EpsGraphics2D;
import geogebra.gui.util.FileTransferable;
import geogebra.gui.util.ImageSelection;
import geogebra.io.MyImageIO;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.freehep.util.UserProperties;
//import org.freehep.graphicsio.svg.SVGGraphics2D;


/**
 * @author Markus Hohenwarter
 */
public class GraphicExportDialog extends JDialog implements KeyListener {
	
	private static final long serialVersionUID = 1L;

	private Application app;
	private JComboBox cbFormat, cbDPI;
	private JLabel sizeLabel;
	private JButton cancelButton;
		
	private double exportScale;
	private int pixelWidth, pixelHeight;
	private NumberFormat sizeLabelFormat;
	
	private boolean textAsShapes=true;
	
	private final int FORMAT_PNG = 0;
	private final int FORMAT_PDF = 1;
	private final int FORMAT_EPS = 2;		
	private final int FORMAT_SVG = 3;
	private final int FORMAT_EMF = 4;		

	public GraphicExportDialog(Application app) {
		super(app.getFrame(), true);
		this.app = app;

		sizeLabelFormat = NumberFormat.getInstance(Locale.ENGLISH);		
		sizeLabelFormat.setGroupingUsed(false);
		sizeLabelFormat.setMaximumFractionDigits(2);
		
		initGUI();				
	}
	
	public void setVisible(boolean flag) {		
		if (flag) {
			loadPreferences();
			super.setVisible(true);
		} else {
			savePreferences();
			super.setVisible(false);
		}		
	}

	private void initGUI() {
		setResizable(false);		
		setTitle(app.getMenu("Export") + ": " + app.getPlain("DrawingPad"));

		JPanel cp = new JPanel(new BorderLayout(5, 5));
		cp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		getContentPane().add(cp);

		// format list 
		JPanel formatPanel = new JPanel(new FlowLayout(5));
		String[] formats =
					{ app.getPlain("png") + " (" + Application.FILE_EXT_PNG + ")",
					  app.getPlain("pdf") + " (" + Application.FILE_EXT_PDF + ")",
					  app.getPlain("eps") + " (" + Application.FILE_EXT_EPS + ")", 			
					  app.getPlain("svg") + " (" + Application.FILE_EXT_SVG + ")",
					  app.getPlain("emf") + " (" + Application.FILE_EXT_EMF + ")"};
		
		cbFormat = new JComboBox(formats);
		formatPanel.add(new JLabel(app.getPlain("Format") + ":"));
		formatPanel.add(cbFormat);
		cp.add(formatPanel, BorderLayout.NORTH);
		
		
		// panel with fields to enter
		// scale of image, dpi and
		// width and height of picture	
		final JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));		
				
		// scale 
		EuclidianView ev = app.getEuclidianView();
		final PrintScalePanel psp = new PrintScalePanel(app, ev);		
		psp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateSizeLabel();
			}			
		});			
		p.add(psp);
		
		// dpi combo box
		final JPanel dpiPanel = new JPanel(new FlowLayout(5));
	
		String [] dpiStr =  {"72", "96", "150", "300", "600"};
		cbDPI = new JComboBox(dpiStr);
		cbDPI.setSelectedItem("300");			
		final JLabel resolutionInDPILabel=new JLabel(app.getPlain("ResolutionInDPI") + ":");
		if  (cbFormat.getSelectedIndex()==FORMAT_PNG)
		{
			dpiPanel.add(resolutionInDPILabel);
			dpiPanel.add(cbDPI);
		}
		p.add(dpiPanel);
		cbDPI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {							
				updateSizeLabel();
			}		
		});	
		
		
		final JCheckBox textAsShapesCB = new JCheckBox(app.getPlain("ExportTextAsShapes"),textAsShapes);
		dpiPanel.add(textAsShapesCB);
		
		if  (cbFormat.getSelectedIndex()==FORMAT_SVG || cbFormat.getSelectedIndex()==FORMAT_PDF)
			dpiPanel.add(textAsShapesCB);

				
		textAsShapesCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textAsShapes=textAsShapesCB.isSelected();
			}					
		});
		
		cbFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				textAsShapesCB.setEnabled(true);
				switch (cbFormat.getSelectedIndex()) {
					case FORMAT_SVG:
					case FORMAT_PDF:
						dpiPanel.remove(resolutionInDPILabel);
						dpiPanel.remove(cbDPI);
						dpiPanel.add(textAsShapesCB);
						break;
					case FORMAT_EPS:
						dpiPanel.remove(resolutionInDPILabel);
						dpiPanel.remove(cbDPI);
						dpiPanel.add(textAsShapesCB);
						textAsShapesCB.setEnabled(false);
						textAsShapesCB.setSelected(true);
						break;
					case FORMAT_EMF:
						dpiPanel.remove(resolutionInDPILabel);
						dpiPanel.remove(cbDPI);
						dpiPanel.remove(textAsShapesCB);
						break;
					default: // PNG
						dpiPanel.add(resolutionInDPILabel);
						dpiPanel.add(cbDPI);
						dpiPanel.remove(textAsShapesCB);
						cbDPI.setSelectedItem("300");
						cbDPI.setEnabled(true);	
						break;
				}
				SwingUtilities.updateComponentTreeUI(p);
			}			
		});
							
		// width and height of picture	
		JPanel sizePanel = new JPanel(new FlowLayout(5));
		sizePanel.add(new JLabel(app.getPlain("Size") + ":"));
		sizeLabel = new JLabel();
		sizePanel.add(sizeLabel);		
		p.add(sizePanel);			
		cp.add(p, BorderLayout.CENTER);				

		//	Cancel and Export Button
		cancelButton = new JButton(app.getPlain("Cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				setVisible(false);
			}
		});
		JButton exportButton = new JButton(app.getMenu("Save"));
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				Thread runner = new Thread() {
					public void run() {				
						setVisible(false);
						
						int index = cbFormat.getSelectedIndex();
						switch (index) {
							case FORMAT_PNG : // PNG
								exportPNG(false);
								break;
							
							case FORMAT_EPS: // EPS
								exportEPS(false);
								break;		
															
							case FORMAT_EMF: // EMF
								exportEMF(false);
								break;
								
							case FORMAT_PDF: // PDF
								exportPDF(false);
								break;
								
							case FORMAT_SVG: // SVG
								exportSVG(false);
								break;	
							
						}				
					}
				};
				runner.start();
			}
		});
		JButton exportClipboardButton = new JButton(app.getMenu("Clipboard"));
		exportClipboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				Thread runner = new Thread() {
					public void run() {				
						setVisible(false);
						
						int index = cbFormat.getSelectedIndex();
						switch (index) {
							case FORMAT_PNG : // PNG
								exportPNG(true);
								break;
							
							case FORMAT_EPS: // EPS
								exportEPS(true);
								break;		
															
							case FORMAT_EMF: // EMF
								exportEMF(true);
								break;
								
							case FORMAT_PDF: // PDF
								exportPDF(true);
								break;
								
							case FORMAT_SVG: // SVG
								exportSVG(true);
								break;	
							
						}				
					}
				};
				runner.start();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(exportButton);
		buttonPanel.add(exportClipboardButton);
		buttonPanel.add(cancelButton);		
		cp.add(buttonPanel, BorderLayout.SOUTH);
		
		Util.addKeyListenerToAll(this, this);

		updateSizeLabel();
		centerOnScreen();
	}
	
	private int getDPI() {
		return Integer.parseInt((String)cbDPI.getSelectedItem());
	}
	
	private void loadPreferences() {
		try {
			// format
			int formatID = FORMAT_PNG;
			String format = GeoGebraPreferences.getPref().
				loadPreference(GeoGebraPreferences.EXPORT_PIC_FORMAT, "png");		
	    	if (format.equals("eps")) formatID = FORMAT_EPS; 
	    	else if (format.equals("svg")) formatID = FORMAT_SVG;	    	
			cbFormat.setSelectedIndex(formatID);					
			
			// dpi
	    	if (cbDPI.isEnabled()) {
				String strDPI = GeoGebraPreferences.getPref().
					loadPreference(GeoGebraPreferences.EXPORT_PIC_DPI, "300");
				for (int i=0; i < cbDPI.getItemCount(); i++) {
					String dpi = cbDPI.getItemAt(i).toString();
					if (dpi.equals(strDPI))
						cbDPI.setSelectedIndex(i);
				}		
	    	}			
			
			/*
	    	// scale in cm
			double scale = Double.parseDouble(GeoGebraPreferences.loadPreference(
	    						GeoGebraPreferences.EXPORT_PIC_SCALE, "1"));
			app.getEuclidianView().setPrintingScale(scale);
			*/
			
			updateSizeLabel();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void savePreferences() {    		
    	// dpi
    	GeoGebraPreferences.getPref().savePreference(GeoGebraPreferences.EXPORT_PIC_DPI, cbDPI.getSelectedItem().toString());
    	
    	// format
    	String format;
    	switch (cbFormat.getSelectedIndex()) {
    		case FORMAT_EPS: format = "eps"; break;
    		case FORMAT_SVG: format = "svg"; break;    		
    		default: format = "png";
    	}    	
    	GeoGebraPreferences.getPref().savePreference(GeoGebraPreferences.EXPORT_PIC_FORMAT, format);
    	
    	/*
    	// scale in cm
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_PIC_SCALE, Double.toString(app.getEuclidianView().getPrintingScale()));
    	*/   
    }
	
	private void updateSizeLabel() {
		EuclidianView ev = app.getEuclidianView(); 
		double printingScale = ev.getPrintingScale();
		// takes dpi into account (note: eps has 72dpi)
		exportScale = printingScale * getDPI() / 2.54 / ev.getXscale();
						
		StringBuffer sb = new StringBuffer();
		// cm size
		double cmWidth = printingScale * (ev.getExportWidth() / ev.getXscale());
		double cmHeight = printingScale * (ev.getExportHeight() / ev.getYscale());	
		sb.append(sizeLabelFormat.format(cmWidth));
		sb.append(" x ");
		sb.append(sizeLabelFormat.format(cmHeight));
		sb.append(" cm");			
		
		// pixel size
		pixelWidth = (int) Math.floor(ev.getExportWidth() * exportScale);
		pixelHeight = (int) Math.floor(ev.getExportHeight() * exportScale);	
		sb.append(", ");
		sb.append(pixelWidth);
		sb.append(" x ");
		sb.append(pixelHeight);
		sb.append(" pixel");							
		sizeLabel.setText(sb.toString());	
	}		

	private void centerOnScreen() {		
		//	center on screen
		pack();				
		setLocationRelativeTo(app.getFrame());	
	}

	/**
	 *  Shows save dialog and exports drawing as eps. 
	 */
	final private boolean exportEPS(boolean exportToClipboard) {
		//  Michael Borcherds 2008-03-02 BEGIN
		File file;
		String tempDir = System.getProperty("java.io.tmpdir");
		if (exportToClipboard)
		{
			file= new File(tempDir+"geogebra.eps");
		}
		else
			//  Michael Borcherds 2008-03-02 END
		file =
			app.getGuiManager().showSaveDialog(
				Application.FILE_EXT_EPS, null,
				app.getPlain("eps") + " " + app.getMenu("Files"));
		if (file == null)
			return false;
		try {		
			EuclidianView ev = app.getEuclidianView();						
			EpsGraphics2D g =
				new EpsGraphics2D(
					app.getPlain("ApplicationName") + ", " + app.getPlain("ApplicationURL"),
					file, 0,0, pixelWidth, pixelHeight);		
										
	    	// draw to epsGraphics2D
			ev.exportPaint(g, exportScale); 
			g.close();		
			
			if (exportToClipboard) sendToClipboard(file); //  Michael Borcherds 2008-03-02 END			
			
			return true;						
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
			return false;
		}
	}		
	
	/**
	  *  Exports drawing as emf
	  */
	final private boolean exportEMF(boolean exportToClipboard) {

		//  Michael Borcherds 2008-03-02 BEGIN
		File file;
		String tempDir = System.getProperty("java.io.tmpdir");
		if (exportToClipboard)
		{
			file= new File(tempDir+"geogebra.emf");
		}
		else
		file =
			app.getGuiManager().showSaveDialog(
				Application.FILE_EXT_EMF, null,
				app.getPlain("emf") + " " + app.getMenu("Files"));
		//  Michael Borcherds 2008-03-02 END

		if (file == null)
			return false;
		try {					   
			VectorGraphics g = new EMFGraphics2D(file, new Dimension(pixelWidth, pixelHeight));
		    g.startExport();	
			app.getEuclidianView().exportPaint(g, exportScale); 
			g.endExport();		
			
			if (exportToClipboard) sendToClipboard(file); //  Michael Borcherds 2008-03-02 END


			
			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
			return false;
		} 
	}
	
	/**
	  *  Exports drawing as pdf
	  */
	final private boolean exportPDF(boolean exportToClipboard) {
		//  Michael Borcherds 2008-03-02 BEGIN
		File file;
		String tempDir = System.getProperty("java.io.tmpdir");
		if (exportToClipboard)
		{
			file= new File(tempDir+"geogebra.pdf");
		}
		else
			//  Michael Borcherds 2008-03-02 END
		file =
			app.getGuiManager().showSaveDialog(
				Application.FILE_EXT_PDF, null,
				app.getPlain("pdf") + " " + app.getMenu("Files"));
		
		if (file == null)
			return false;
		try {					   
			// export text as shapes or plaintext
			// shapes: better representation
			// text: smaller file size, but some unicode symbols don't export eg Upsilon 
			UserProperties props=(UserProperties)PDFGraphics2D.getDefaultProperties();			
			props.setProperty(PDFGraphics2D.EMBED_FONTS, !textAsShapes);
			//props.setProperty(PDFGraphics2D.EMBED_FONTS_AS, FontConstants.EMBED_FONTS_TYPE1);
			props.setProperty(PDFGraphics2D.TEXT_AS_SHAPES, textAsShapes);			
			PDFGraphics2D.setDefaultProperties(props);

			VectorGraphics g = new PDFGraphics2D(file, new Dimension(pixelWidth, pixelHeight));
		    g.startExport();			
			app.getEuclidianView().exportPaint(g, exportScale); 
			g.endExport();	
			
			if (exportToClipboard) sendToClipboard(file); //  Michael Borcherds 2008-03-02 END
			
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			app.showError("SaveFileFailed");
			
			return false;
		} catch (Error ex) {
			ex.printStackTrace();
			app.showError("SaveFileFailed");
			
			return false;
		} 
	}
	
	/**
	  *  Exports drawing as SVG
	  */
	final private boolean exportSVG(boolean exportToClipboard) {
		//  Michael Borcherds 2008-03-02 BEGIN
		File file;
		String tempDir = System.getProperty("java.io.tmpdir");
		if (exportToClipboard)
		{
			file= new File(tempDir+"geogebra.svg");
		}
		else
			//  Michael Borcherds 2008-03-02 END
		file =
			app.getGuiManager().showSaveDialog(
				Application.FILE_EXT_SVG, null,
				app.getPlain("svg") + " " + app.getMenu("Files"));
		
		if (file == null)
			return false;
		try {	
			
			// export text as shapes or plaintext
			// shapes: better representation
			// text: smaller file size, but some unicode symbols don't export eg Upsilon 
			UserProperties props=(UserProperties)SVGGraphics2D.getDefaultProperties();			
			props.setProperty(SVGGraphics2D.EMBED_FONTS, !textAsShapes);
			props.setProperty(SVGGraphics2D.TEXT_AS_SHAPES, textAsShapes);			
			SVGGraphics2D.setDefaultProperties(props);

			// Michael Borcherds 2008-03-01
			// added SVGExtensions to support grouped objects in layers
			SVGExtensions g = new SVGExtensions(file, new Dimension(pixelWidth, pixelHeight));
		    //VectorGraphics g = new SVGGraphics2D(file, new Dimension(pixelWidth, pixelHeight));
			

			
			EuclidianView ev=app.getEuclidianView();
			
		    g.startExport();
			ev.exportPaintPre(g, exportScale);
			
			g.startGroup("misc");
			ev.drawObjectsPre(g);
			g.endGroup("misc");
			
			for (int layer=0 ; layer<=ev.getMaxLayerUsed() ; layer++) //  draw only layers we need
			{
				g.startGroup("layer "+layer);
				ev.drawLayers[layer].drawAll(g);
				g.endGroup("layer "+layer);
			}
			
			g.endExport();	
			
			if (exportToClipboard) sendToClipboard(file); //  Michael Borcherds 2008-03-02 END
			
			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
			return false;
		} 
	}		

	/**
	  *  Exports drawing as png with given resolution in dpi
	  */
	final private boolean exportPNG(boolean exportToClipboard) {
		//  Michael Borcherds 2008-03-02 BEGIN
		File file;
		if (exportToClipboard)
		{
			file = null;
		}
		else
		file =
			app.getGuiManager().showSaveDialog(
				Application.FILE_EXT_PNG, null,
				app.getPlain("png") + " " + app.getMenu("Files"));
		try {
			
			if (exportToClipboard)
			{
				sendToClipboard(app.getEuclidianView().getExportImage(exportScale));
			}
			else
			{
				if (file == null) return false;
				BufferedImage img =
				app.getEuclidianView().getExportImage(exportScale);			
				MyImageIO.write(img, "png", getDPI(),  file);	
			}
			//  Michael Borcherds 2008-03-02 END
			
			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
			return false;
		} 
	}
	



	/**
	  *  Exports drawing as jpg. 
	  *
	final private boolean exportJPG() {
		File file =
			app.showSaveDialog(
				Application.FILE_EXT_JPG,
				app.getPlain("jpg") + " " + app.getMenu("Files"));
		if (file == null)
			return false;
		try {
			BufferedImage img =
				app.getEuclidianView().getExportImage(
					sizePanel.getSelectedWidth());
			ImageIO.write(img, "jpg", file);
			return true;
		} catch (IOException ex) {
			app.showError("SaveFileFailed");
			Application.debug(ex.toString());
			return false;
		}
	}*/
	
	/*
	 * Keylistener implementation of PropertiesDialog
	 */
 
	public void keyPressed(KeyEvent e) {		
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {				
			setVisible(false);
		}	
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {

	} 
	
	//  Michael Borcherds 2008-03-02 BEGIN
	private void sendToClipboard(File file)
	{
        FileTransferable ft = new FileTransferable(file);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ft, null);
		}
	
	private void sendToClipboard(Image img)
	{
		ImageSelection imgSel = new ImageSelection(img);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);	
		}
	//  Michael Borcherds 2008-03-02 END
	
	
}
