/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.export;

import geogebra.GeoGebraApplicationBase;
import geogebra.euclidian.EuclidianView;
import geogebra.export.epsgraphics.EpsGraphics2D;
import geogebra.gui.GeoGebraPreferences;
import geogebra.io.MyImageIO;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.emf.EMFGraphics2D;
import org.freehep.graphicsio.pdf.PDFGraphics2D;
import org.freehep.graphicsio.svg.SVGGraphics2D;


/**
 * @author Markus Hohenwarter
 */
public class GraphicExportDialog extends JDialog implements KeyListener {
	
	private static final long serialVersionUID = 1L;

	private GeoGebraApplicationBase app;
	private JComboBox cbFormat, cbDPI;
	private JLabel sizeLabel;
	private JButton cancelButton;
		
	private double exportScale;
	private int pixelWidth, pixelHeight;
	private NumberFormat sizeLabelFormat;
	
	private final int FORMAT_PNG = 0;
	private final int FORMAT_PDF = 1;
	private final int FORMAT_EPS = 2;		
	private final int FORMAT_SVG = 3;
	private final int FORMAT_EMF = 4;		

	public GraphicExportDialog(GeoGebraApplicationBase app) {
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
					{ app.getPlain("png") + " (" + GeoGebraApplicationBase.FILE_EXT_PNG + ")",
					  app.getPlain("pdf") + " (" + GeoGebraApplicationBase.FILE_EXT_PDF + ")",
					  app.getPlain("eps") + " (" + GeoGebraApplicationBase.FILE_EXT_EPS + ")", 			
					  app.getPlain("svg") + " (" + GeoGebraApplicationBase.FILE_EXT_SVG + ")",
					  app.getPlain("emf") + " (" + GeoGebraApplicationBase.FILE_EXT_EMF + ")"};
		
		cbFormat = new JComboBox(formats);
		formatPanel.add(new JLabel(app.getPlain("Format") + ":"));
		formatPanel.add(cbFormat);
		cp.add(formatPanel, BorderLayout.NORTH);
		
		
		// panel with fields to enter
		// scale of image, dpi and
		// width and height of picture	
		JPanel p = new JPanel();
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
		JPanel dpiPanel = new JPanel(new FlowLayout(5));
	
		String [] dpiStr =  {"72", "96", "150", "300", "600"};
		cbDPI = new JComboBox(dpiStr);
		cbDPI.setSelectedItem("300");			
		dpiPanel.add(new JLabel(app.getPlain("ResolutionInDPI") + ":"));
		dpiPanel.add(cbDPI);
		p.add(dpiPanel);
		cbDPI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {							
				updateSizeLabel();
			}		
		});	
		
		cbFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switch (cbFormat.getSelectedIndex()) {
					case FORMAT_EPS:
					case FORMAT_SVG:
					case FORMAT_EMF:
					case FORMAT_PDF:
						cbDPI.setSelectedItem("72");
						cbDPI.setEnabled(false);						
						break;											
					
					default:
						cbDPI.setSelectedItem("300");
						cbDPI.setEnabled(true);						
				}
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
		JButton exportButton = new JButton(app.getMenu("Export"));
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {	
				Thread runner = new Thread() {
					public void run() {				
						setVisible(false);
						
						int index = cbFormat.getSelectedIndex();
						switch (index) {
							case FORMAT_PNG : // PNG
								exportPNG();
								break;
							
							case FORMAT_EPS: // EPS
								exportEPS();
								break;		
															
							case FORMAT_EMF: // EMF
								exportEMF();
								break;
								
							case FORMAT_PDF: // PDF
								exportPDF();
								break;
								
							case FORMAT_SVG: // SVG
								exportSVG();
								break;	
							
						}				
					}
				};
				runner.start();
			}
		});

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(exportButton);
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
			String format = GeoGebraPreferences.loadPreference(GeoGebraPreferences.EXPORT_PIC_FORMAT, "png");		
	    	if (format.equals("eps")) formatID = FORMAT_EPS; 
	    	else if (format.equals("svg")) formatID = FORMAT_SVG;	    	
			cbFormat.setSelectedIndex(formatID);					
			
			// dpi
	    	if (cbDPI.isEnabled()) {
				String strDPI = GeoGebraPreferences.loadPreference(
		    							GeoGebraPreferences.EXPORT_PIC_DPI, "300");
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
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_PIC_DPI, cbDPI.getSelectedItem().toString());
    	
    	// format
    	String format;
    	switch (cbFormat.getSelectedIndex()) {
    		case FORMAT_EPS: format = "eps"; break;
    		case FORMAT_SVG: format = "svg"; break;    		
    		default: format = "png";
    	}    	
    	GeoGebraPreferences.savePreference(GeoGebraPreferences.EXPORT_PIC_FORMAT, format);
    	
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
		double cmWidth = printingScale * (ev.getSelectedWidth() / ev.getXscale());
		double cmHeight = printingScale * (ev.getSelectedHeight() / ev.getYscale());	
		sb.append(sizeLabelFormat.format(cmWidth));
		sb.append(" x ");
		sb.append(sizeLabelFormat.format(cmHeight));
		sb.append(" cm");			
		
		// pixel size
		pixelWidth = (int) Math.floor(ev.getSelectedWidth() * exportScale);
		pixelHeight = (int) Math.floor(ev.getSelectedHeight() * exportScale);	
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
	final private boolean exportEPS() {
		File file =
			app.showSaveDialog(
				GeoGebraApplicationBase.FILE_EXT_EPS, null,
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
			ev.exportPaint(g, exportScale, false); // Michael Borcherds 2008-02-27 added false
			g.close();			
			return true;						
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
			return false;
		}
	}		
	
	/**
	  *  Exports drawing as emf
	  */
	final private boolean exportEMF() {
		File file =
			app.showSaveDialog(
				GeoGebraApplicationBase.FILE_EXT_EMF, null,
				app.getPlain("emf") + " " + app.getMenu("Files"));
		
		if (file == null)
			return false;
		try {					   
			VectorGraphics g = new EMFGraphics2D(file, new Dimension(pixelWidth, pixelHeight));
		    g.startExport();	
			app.getEuclidianView().exportPaint(g, exportScale, false); // Michael Borcherds 2008-02-26 added false
			g.endExport();								    		     		   		    		    			
			
			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
			return false;
		} 
	}
	
	/**
	  *  Exports drawing as pdf
	  */
	final private boolean exportPDF() {
		File file =
			app.showSaveDialog(
				GeoGebraApplicationBase.FILE_EXT_PDF, null,
				app.getPlain("pdf") + " " + app.getMenu("Files"));
		
		if (file == null)
			return false;
		try {					   
			VectorGraphics g = new PDFGraphics2D(file, new Dimension(pixelWidth, pixelHeight));
		    g.startExport();			
			app.getEuclidianView().exportPaint(g, exportScale, false); // Michael Borcherds 2008-02-27 added false
			g.endExport();								    		     		   		    		    			
			
			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
			return false;
		} 
	}
	
	/**
	  *  Exports drawing as SVG
	  */
	final private boolean exportSVG() {
		File file =
			app.showSaveDialog(
				GeoGebraApplicationBase.FILE_EXT_SVG, null,
				app.getPlain("svg") + " " + app.getMenu("Files"));
		
		if (file == null)
			return false;
		try {						
		    // Michael Borcherds 2008-02-27
			// added SVGExtensions to support grouped objects in layers
			SVGExtensions g = new SVGExtensions(file, new Dimension(pixelWidth, pixelHeight));
		    //VectorGraphics g = new SVGGraphics2D(file, new Dimension(pixelWidth, pixelHeight));
		    g.startExport();
			app.getEuclidianView().exportPaint(g, exportScale, true);
			g.endExport();	
			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
			return false;
		} 
	}		

	/**
	  *  Exports drawing as png with given resolution in dpi
	  */
	final private boolean exportPNG() {
		File file =
			app.showSaveDialog(
				GeoGebraApplicationBase.FILE_EXT_PNG, null,
				app.getPlain("png") + " " + app.getMenu("Files"));
		if (file == null)
			return false;
		try {
			BufferedImage img =
			app.getEuclidianView().getExportImage(exportScale);			
			MyImageIO.write(img, "png", getDPI(),  file);						
			return true;
		} catch (Exception ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
			return false;
		} catch (Error ex) {
			app.showError("SaveFileFailed");
			System.err.println(ex.toString());
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
			System.err.println(ex.toString());
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
	
	
}
