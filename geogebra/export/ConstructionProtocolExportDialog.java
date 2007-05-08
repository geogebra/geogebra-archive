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
import geogebra.ConstructionProtocol;
import geogebra.euclidian.EuclidianView;
import geogebra.gui.TitlePanel;
import geogebra.util.Util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class ConstructionProtocolExportDialog extends JDialog implements KeyListener {
	        
	       
			private static final long serialVersionUID = -2626950140196416416L;
			
			//private static final int DEFAULT_GRAPHICS_WIDTH = 600;  
	        private static final int DIALOG_WIDTH = 500;
	                
	        private JCheckBox cbDrawingPadPicture, cbScreenshotPicture;
	        private JCheckBox cbColor;
	        private GraphicSizePanel sizePanel;
	        private boolean kernelChanged = false;
	        private ConstructionProtocol prot;
	        private Application app;
	        
	        public ConstructionProtocolExportDialog(ConstructionProtocol prot) {
	            super(prot.getApplication().getFrame(), true);
	            this.prot = prot;
	            app = prot.getApplication();

	            initGUI();
	        }
	        
	        private void initGUI() {
	            setResizable(false);
	            setTitle(app.getMenu("Export") + ": " +
	                                    app.getPlain("ConstructionProtocol") +
	                                    " (" +  Application.FILE_EXT_HTML + ")");
	            
	            JPanel cp = new JPanel(new BorderLayout(5,5));
	            cp.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	            getContentPane().add(cp);
	            
	            TitlePanel tp = new TitlePanel(app);
	            cp.add(tp, BorderLayout.NORTH);         
	            tp.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {                  
	                    kernelChanged = true;                               
	                }
	            });         
	            
	            // checkbox: insert picture of drawing pad      
	            JPanel picPanel = new JPanel(new BorderLayout(20, 5));              
	            cbDrawingPadPicture = new JCheckBox(
	                app.getPlain("InsertPictureOfConstruction"));
	            cbDrawingPadPicture.setSelected(true);
	            cbScreenshotPicture = new JCheckBox(
	                app.getPlain("InsertPictureOfAlgebraAndConstruction"));
	            cbScreenshotPicture.setSelected(false);

	            picPanel.add(cbDrawingPadPicture, BorderLayout.WEST);
	            if (app.showAlgebraView()) {
	                picPanel.add(cbScreenshotPicture, BorderLayout.SOUTH);
	            }
	            
	            // panel with fields to enter width and height of picture
	            EuclidianView ev = app.getEuclidianView();
	            //int height = (int) Math.ceil(DEFAULT_GRAPHICS_WIDTH *
	            //                  (double) ev.getHeight() / ev.getWidth());           
	            //sizePanel = new GraphicSizePanel(app, DEFAULT_GRAPHICS_WIDTH, height);
	            sizePanel = new GraphicSizePanel(app, ev.getWidth(), ev.getHeight());
	            picPanel.add(sizePanel, BorderLayout.CENTER);   
	            picPanel.setBorder(BorderFactory.createEtchedBorder());     
	            cp.add(picPanel, BorderLayout.CENTER);
	            
	            cbColor =  new JCheckBox(
	                app.getPlain("ColorfulConstructionProtocol")); 
	            cbColor.setSelected(false);
	            
	            // disable width and height field when checkbox is deselected
	            cbDrawingPadPicture.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {        
	                    boolean flag = cbDrawingPadPicture.isSelected();  
	                    sizePanel.setEnabled(flag); 
	                    if (flag) { 
	                        cbScreenshotPicture.setSelected(false);
	                    } 
	                }
	            });
	            cbScreenshotPicture.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {        
	                    boolean flag = cbScreenshotPicture.isSelected();   
	                    sizePanel.setEnabled(false);  
	                    if (flag) {                 
	                        cbDrawingPadPicture.setSelected(false); 
	                    }                                   
	                }
	            });
	            cbColor.addActionListener(new ActionListener() {
	                public void actionPerformed(ActionEvent e) {          
	                	prot.setUseColors(cbColor.isSelected());                     
	                }
	            });
	                    
	            //  Cancel and Export Button
	             JButton cancelButton = new JButton(app.getPlain("Cancel"));
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
	                                if (kernelChanged) app.storeUndoInfo();              
	                                exportHTML(cbDrawingPadPicture.isSelected(), sizePanel.getSelectedWidth(),
	                                                    cbScreenshotPicture.isSelected(), cbColor.isSelected());            
	                           }
	                        };
	                        runner.start();                  
	                     }
	                 });
	             JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	             buttonPanel.add(exportButton);
	             buttonPanel.add(cancelButton);     
	             
	            JPanel southPanel = new JPanel(new BorderLayout()); 
	            southPanel.add(cbColor, BorderLayout.NORTH);    
	            southPanel.add(buttonPanel, BorderLayout.SOUTH);     
	            cp.add(southPanel, BorderLayout.SOUTH);
	             
	             Util.addKeyListenerToAll(this, this);
	             centerOnScreen();
	        }
	        
	    	private void centerOnScreen() {
	    		//	center on screen
	    		pack();				
	    		setLocationRelativeTo(app.getFrame());
	    	}
	        
	        /*
	         * Keylistener implementation of ConstructionProtocol
	         */
	 
	        public void keyPressed(KeyEvent e) {
	            int code = e.getKeyCode();
	            if (code == KeyEvent.VK_ESCAPE) {       
	                dispose();
	            }   
	        }

	        public void keyReleased(KeyEvent e) {
	        }

	        public void keyTyped(KeyEvent e) {

	        } 
	        
	        /* *******************
	         * HTML export
	         * *******************/
	         
	        
	       
	        
	        /**
	         *  Exports construction protocol as html
	         * @param includePicture: states whether a picture of the drawing pad
	         * should be exportet with the html output file
	         * @param includeAlgebraPicture: states whether a picture of the algebraWindow
	         * should be exportet with the html output file
	         */
	        private void exportHTML(boolean includePicture, int width,
	        						boolean includeAlgebraPicture,
	                                                   boolean useColors) {    
	            File file, pngFile = null;
	            prot.setUseColors(useColors);
	            file = app.showSaveDialog(Application.FILE_EXT_HTML, null,
	                   app.getPlain("html") + " " + app.getMenu("Files"));
	            if (file == null) return;                       
	            try {          
	               BufferedImage img = null;
	               
	               if (includePicture) {
	                   // picture of drawing pad
	                   img = app.getEuclidianView().getExportImage(1d);             
	               }  
	               else if (includeAlgebraPicture) {
	            	   // picture of drawing pad
	                   img = getMainCompScreenshot();
	               }
	              
	               //  save image to PNG file
	               if (img != null) {
	                   pngFile = Application.addExtension(file, "png");
	                   ImageIO.write(img, "png", pngFile);
	               } 
	                               
	                 // write html string to file
	                 FileWriter fw = new FileWriter(file);
	                 fw.write(prot.getHTML(pngFile));
	                 fw.close();                
	            } catch (IOException ex) {
	                app.showError("SaveFileFailed");
	                System.err.println(ex.toString());                      
	            }
	        }
	        
	        private BufferedImage getMainCompScreenshot() {
	        	Component mainComp = app.getMainComponent();
	        	BufferedImage img = new BufferedImage(mainComp.getWidth(), mainComp.getHeight(), BufferedImage.TYPE_INT_RGB);       
                Graphics2D g = img.createGraphics();
                mainComp.paint(g);
                g.dispose();
                img.flush();
                return img;
             }
	        
	    }
