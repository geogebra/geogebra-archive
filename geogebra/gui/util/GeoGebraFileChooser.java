package geogebra.gui.util;

import geogebra.io.MyXMLio;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 * An enhanced file chooser for GeoGebra which can be used
 * to load images or ggb files with a preview image.
 * 
 * @author Florian Sonner
 * @version 1.0
 */
public class GeoGebraFileChooser extends JFileChooser implements ComponentListener {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The with at which the accessory is displayed.
	 */
	private static final int ACCESSORY_WIDTH = 600;
	
	/**
	 * The file chooser is used to load images at the moment.
	 */
	public static final int MODE_IMAGES = 0;
	
	/**
	 * The file chooser is used to load / save ggb files at the moment.
	 */
	public static final int MODE_GEOGEBRA = 1;

	/**
	 * An instance of the GeoGebra application.
	 */
	private Application app;
	
	/**
	 * The current mode of the file chooser.
	 */
	private int currentMode = -1;
	
	/**
	 * The accessory panel which displays the preview of the selected file.
	 */
	private PreviewPanel previewPanel;
	
	/**
	 * Whether to show the accessory or not. The accessory is not displayed in case
	 * the dialog is too small.
	 */
	private boolean showAccessory = true;

	/**
	 * Construct a file chooser without a restricted file system view.
	 * 
	 * May throw IOException: Could not get shell folder ID list
	 * 		(Java bug http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6544857)
	 * 
	 * If an exception is catched, the constructor with a restricted file system view
	 * should be used.
	 * 
	 * @param currentDirectory
	 */
	public GeoGebraFileChooser(Application app, File currentDirectory)
	{
		this(app, currentDirectory, false);
	}
	
	/**
	 * Construct a file chooser which may have a restricted file system view if the second
	 * parameter is set to true.
	 * 
	 * @param currentDirectory
	 * @param restricted
	 */
	public GeoGebraFileChooser(Application app,File currentDirectory, boolean restricted)
	{
		super(currentDirectory, (restricted ? new RestrictedFileSystemView() : null));
		
		this.app = app;
		
		previewPanel = new PreviewPanel(this);
		setAccessory(previewPanel);
		addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, previewPanel);
		
		addComponentListener(this);
		
		setMode(MODE_GEOGEBRA); // default mode is the mode to load geogebra files
	}
	
	/**
	 * Get the current mode of the file chooser. 
	 * 
	 * @return
	 */
	public int getMode()
	{
		return currentMode;
	}
	
	/**
	 * Set a new mode for the file chooser. Use the constants defined in this class
	 * for the different modes.
	 * 
	 * @param mode
	 */
	public void setMode(int mode)
	{
		// invalid mode?
		if(mode != MODE_IMAGES && mode != MODE_GEOGEBRA) {
			Application.debug("Invalid file chooser mode, MODE_GEOGEBRA used as default.");
			mode = MODE_GEOGEBRA;
		}
		
		// do not perform any unncessary actions
		if(this.currentMode == mode)
			return;
	
		if(mode == MODE_GEOGEBRA) {			// load/save ggb, ggt etc. files
			setMultiSelectionEnabled(true);
		} else {							// load images
			setMultiSelectionEnabled(false);
		}
		
		// TODO apply mode specific settings..
		
		this.currentMode = mode;
	}

	/**
	 * Check if we have to show / hide the accessory
	 */
	public void componentResized(ComponentEvent e) {
		if(getSize().width < ACCESSORY_WIDTH && showAccessory) {
			setAccessory(null);
			removePropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, previewPanel);
			
			showAccessory = false;
			validate();
		} else if(getSize().width > ACCESSORY_WIDTH && !showAccessory) {
			setAccessory(previewPanel);
			addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, previewPanel);
			
			// fire an event that the selected file has changed to update the preview image
			previewPanel.propertyChange(null);
			
			showAccessory = true;
			validate();
		}
	}

	public void componentShown(ComponentEvent e) {
	}
	
	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	
	/**
	 * Component to preview image files in a file chooser.
	 * 
	 * This file is based on Hack #31 in
	 * "Swing Hacks - Tips & Tools for Building Killer GUIs" by Joshua Marinacci
	 * and Chris Adamson.
	 * 
	 * Modified & commented by Florian Sonner for GeoGebraFileChooser
	 * 
	 * @author Joshua Marinacci
	 * @author Chris Adamson
	 * @author Philipp Weissenbacher (materthron@users.sourceforge.net)
	 * @author Florian Sonner
	 */

	private class PreviewPanel extends JPanel implements PropertyChangeListener {
		private static final long serialVersionUID = 1L;
		
		/**
		 * The maximum file size of images in kb.
		 */
		private static final int maxImageSize = 300;

		private GeoGebraFileChooser fileChooser;

		/**
		 * The image to draw in the preview area.
		 */
		private BufferedImage img = null;
		
		/**
		 * The panel on which the image is drawn.
		 */
		private ImagePanel imagePanel = null;
		
		/**
		 * A label to describe the properties of the selected file.
		 */
		private Label fileLabel;

		public PreviewPanel(GeoGebraFileChooser fileChooser) {
			this.fileChooser = fileChooser;
			
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5)); // border at the left & right

			imagePanel = new ImagePanel();
			add(BorderLayout.CENTER, imagePanel);
			
			fileLabel = new Label();
			add(BorderLayout.SOUTH, fileLabel);
		}

		/**
		 * A new file was selected -> update the panel if necessary.
		 */
		public void propertyChange(PropertyChangeEvent evt) {
			try {
				File file = fileChooser.getSelectedFile();
				
				if (file != null && file.exists()) // don't update on directory change
					updateImage(file);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		/**
		 * Update the preview image if it's possible to load one.
		 * 
		 * @param file
		 * @throws IOException
		 */
		private void updateImage(File file) throws IOException {
			fileLabel.setText("");
			
			try {
				BufferedImage tmpImage = null;
				
				// Update preview for ggb files
				if (fileChooser.getMode() == GeoGebraFileChooser.MODE_GEOGEBRA) {
					String fileName = file.getName();
					
					if(fileName.endsWith(".ggb")) {
						tmpImage = MyXMLio.getPreviewImage(file); // load preview from zip
						
						StringBuffer fileInfo = new StringBuffer();
						
						if(fileName.length() > 20) {
							fileInfo.append(fileName.substring(0, 20));
							fileInfo.append("..");
						} else {
							fileInfo.append(fileName);
						}
						
						fileInfo.append(" : ");
						fileInfo.append(file.length() / 1024);
						fileInfo.append(" kB");
						fileLabel.setText(fileInfo.toString());
					}	
				}
				
				// Update preview for images
				else {
					// fails for a few JPEGs so turn off preview for large files
					if (file.length() < 1024 * maxImageSize) {
						tmpImage = ImageIO.read(file); // returns null if file isn't an image
						
						StringBuffer imgInfo = new StringBuffer();
						
						String fileName = file.getName();
						
						if(fileName.length() > 20) {
							imgInfo.append(fileName.substring(0, 20));
							imgInfo.append("..");
						} else {
							imgInfo.append(fileName);
						}

						imgInfo.append(" : ");
						imgInfo.append(tmpImage.getWidth());
						imgInfo.append(" x ");
						imgInfo.append(tmpImage.getHeight());
						fileLabel.setText(imgInfo.toString());
					}
				}
				
				// resize tmp image if necessary
				if(tmpImage != null) {
					int oldWidth = tmpImage.getWidth();
					int oldHeight = tmpImage.getHeight();
					
					int newWidth;
					int newHeight;
					
					if(oldWidth > ImagePanel.SIZE || oldHeight > ImagePanel.SIZE) {
				        if (oldWidth > oldHeight) {
				            newWidth = ImagePanel.SIZE;
				            newHeight = (ImagePanel.SIZE * oldHeight) / oldWidth;
				        } else {
				            newWidth = (ImagePanel.SIZE * oldWidth) / oldHeight;
				            newHeight = ImagePanel.SIZE;
				        }
				        
				        img = null;
				        
					    // Create a new image for the scaled preview image 
						img = new BufferedImage(newWidth, newHeight,
								BufferedImage.TYPE_INT_RGB);
						
						Graphics2D graphics2D = img.createGraphics();
						
						graphics2D.drawImage(tmpImage, 0, 0, newWidth, newHeight, null);			          
				        
						graphics2D.dispose(); 
						
					} else {
						newWidth = oldWidth;
						newHeight = oldHeight;
						
						img = tmpImage;
					}
					
					tmpImage = null;
				} else {
					img = null;
				}
				
				repaint();
			} catch (IllegalArgumentException iae) {
				// This is thrown if you select .ico files
				img = null;
			} catch (Throwable t) {
				Application.debug(t.getClass() + "");
				img = null;
			}
		}
		
		/**
		 * The panel at which the real preview image is drawn.
		 * 
		 * @author Florian Sonner
		 */
		private class ImagePanel extends JPanel
		{
			private static final long serialVersionUID = 1L;

			/**
			 * The size of the image panel.
			 */
			public final static int SIZE = 200;
			
			public ImagePanel()
			{
				setPreferredSize(new Dimension(SIZE, SIZE));
				setBorder(BorderFactory.createEtchedBorder());
			}

			/**
			 * Paint the preview area.
			 */
			public void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g;
				
				// fill background
				g2.setColor(Color.white);
				g2.fillRect(0, 0, getWidth(), getHeight());
				
				g2.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);
				
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);

				// draw preview image if possible
				if (img != null) {
					// calculate the scaling factor
					int width = img.getWidth();
					int height = img.getHeight();
					
					// center image
					int x = (int) ((getWidth() - width) / 2);
					int y = (int) ((getHeight() - height) / 2);

					// draw the image
					g2.drawImage(img, x, y, width, height, null);
				}
				
				// draw "no preview" message
				else {
					String message = app.getPlain("PreviewUnavailable");
					
					FontMetrics fm = g2.getFontMetrics();
					Rectangle2D bounds = fm.getStringBounds(message, g2);
					
					g2.setColor(Color.darkGray);
					g2.drawString(message, (float)(getWidth() - bounds.getWidth()) / 2, (float)(getHeight() - bounds.getHeight()) / 2);
				}
			}
		}
	}
	
	/*
	 * TODO: override to make File Chooser non-modal for VirtualKeyboard
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        dialog.setModal(false);
        return dialog;
    }*/

}
