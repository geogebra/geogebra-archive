/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/

package geogebra.export;

import geogebra.Application;
import geogebra.euclidian.EuclidianView;
import geogebra.export.pstricks.GeoGebraToPstricks;
import geogebra.gui.PrintScalePanel;
import geogebra.util.MyImageIO;
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
import org.freehep.graphicsio.svg.SVGGraphics2D;
import org.jibble.epsgraphics.EpsGraphics2D;


/**
 * @author Markus Hohenwarter
 */
public class GraphicExportDialog extends JDialog implements KeyListener {
	
	private static final long serialVersionUID = 1L;

	private Application app;
	private JComboBox cbFormat;
	private JLabel sizeLabel;
	private JButton cancelButton;
	
	private int DPI = 300;
	private double exportScale;
	private int pixelWidth, pixelHeight;
	private NumberFormat sizeLabelFormat;
	
	private final int FORMAT_PNG = 0;
	private final int FORMAT_EPS = 1;		
	private final int FORMAT_SVG = 2;
	private final int FORMAT_PSTRICKS = 3;
	//private final int FORMAT_EMF = 3;

	public GraphicExportDialog(Application app) {
		super(app.getFrame(), true);
		this.app = app;

		sizeLabelFormat = NumberFormat.getInstance(Locale.ENGLISH);		
		sizeLabelFormat.setGroupingUsed(false);
		sizeLabelFormat.setMaximumFractionDigits(2);
		
		initGUI();		
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
					  app.getPlain("eps") + " (" + Application.FILE_EXT_EPS + ")", 			
					  //app.getPlain("emf") + " (" + Application.FILE_EXT_EMF + ")",
					  app.getPlain("svg") + " (" + Application.FILE_EXT_SVG + ")",
					  "PSTricks"};
		
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
		final JComboBox cb = new JComboBox(dpiStr);
		cb.setSelectedItem("300");			
		dpiPanel.add(new JLabel(app.getPlain("ResolutionInDPI") + ":"));
		dpiPanel.add(cb);
		p.add(dpiPanel);
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				DPI = Integer.parseInt((String)cb.getSelectedItem());				
				updateSizeLabel();
			}		
		});	
		
		cbFormat.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switch (cbFormat.getSelectedIndex()) {
					case FORMAT_EPS:
					//case FORMAT_EMF:
					case FORMAT_SVG:					
						cb.setSelectedItem("72");
						cb.setEnabled(false);						
						break;											
						
					case FORMAT_PSTRICKS:
						dispose();
						exportPStricks();									
						break;
						
					default:
						cb.setSelectedItem("300");
						cb.setEnabled(true);						
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
				dispose();
			}
		});
		JButton exportButton = new JButton(app.getMenu("Export"));
		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread runner = new Thread() {
					public void run() {
						dispose();
						
						int index = cbFormat.getSelectedIndex();
						switch (index) {
							case FORMAT_PNG : // PNG
								exportPNG();
								break;
							
							case FORMAT_EPS: // EPS
								exportEPS();
								break;		
															
							//case FORMAT_EMF: // EMF
							//	exportEMF();
							//	break;
								
							case FORMAT_SVG: // SVG
								exportSVG();
								break;	

							case FORMAT_PSTRICKS: // PSTRICKS
								exportPStricks();
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
	
	private void updateSizeLabel() {
		EuclidianView ev = app.getEuclidianView(); 
		double printingScale = ev.getPrintingScale();
		// takes dpi into account (note: eps has 72dpi)
		exportScale = printingScale * DPI / 2.54 / ev.getXscale();
						
		StringBuffer sb = new StringBuffer();
		// cm size
		double cmWidth = printingScale * (ev.getXmax() - ev.getXmin());
		double cmHeight = printingScale * (ev.getYmax() - ev.getYmin());	
		sb.append(sizeLabelFormat.format(cmWidth));
		sb.append(" x ");
		sb.append(sizeLabelFormat.format(cmHeight));
		sb.append(" cm");
				
		// pixel size
		pixelWidth = (int) Math.floor(ev.getWidth() * exportScale);
		pixelHeight = (int) Math.floor(ev.getHeight() * exportScale);	
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
				Application.FILE_EXT_EPS, null,
				app.getPlain("eps") + " " + app.getMenu("Files"));
		if (file == null)
			return false;
		try {		
			
			  EpsGraphics2D g =
				new EpsGraphics2D(
					app.getPlain("ApplicationName") + ", " + app.getPlain("ApplicationURL"),
					file, 0,0, pixelWidth, pixelHeight);		
										
	    	// draw to epsGraphics2D
			app.getEuclidianView().exportPaint(g, exportScale);
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
	  *
	final private boolean exportEMF() {
		File file =
			app.showSaveDialog(
				Application.FILE_EXT_EMF, null,
				app.getPlain("emf") + " " + app.getMenu("Files"));
		
		if (file == null)
			return false;
		try {					   
			VectorGraphics g = new EMFGraphics2D(file, new Dimension(pixelWidth, pixelHeight));
		    g.startExport();			
			app.getEuclidianView().exportPaint(g, exportScale);
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
	}*/
	
	/**
	  *  Exports drawing as SVG
	  */
	final private boolean exportSVG() {
		File file =
			app.showSaveDialog(
				Application.FILE_EXT_SVG, null,
				app.getPlain("svg") + " " + app.getMenu("Files"));
		
		if (file == null)
			return false;
		try {						
		    VectorGraphics g = new SVGGraphics2D(file, new Dimension(pixelWidth, pixelHeight));
		    g.startExport();
			app.getEuclidianView().exportPaint(g, exportScale);
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
	 *  Shows save dialog and exports drawing as eps. 
	 */
	final private boolean exportPStricks() {
		try {		
			new GeoGebraToPstricks(app);		
			return true;
		} catch (Exception ex) {
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
				Application.FILE_EXT_PNG, null,
				app.getPlain("png") + " " + app.getMenu("Files"));
		if (file == null)
			return false;
		try {
			BufferedImage img =
			app.getEuclidianView().getExportImage(exportScale);			
			MyImageIO.write(img, "png", DPI,  file);						
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
			cancelButton.doClick();
		}	
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {

	} 
}
