package geogebra.gui.util;

import geogebra.io.MyXMLio;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.RenderingHints;
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
public class GeoGebraFileChooser extends JFileChooser {
	private static final long serialVersionUID = 1L;
	
	/**
	 * The file chooser is used to load images at the moment.
	 */
	public static final int MODE_IMAGES = 0;
	
	/**
	 * The file chooser is used to load / save ggb files at the moment.
	 */
	public static final int MODE_GEOGEBRA = 1;
	
	/**
	 * The current mode of the file chooser.
	 */
	private int currentMode = -1;
	
	/**
	 * The accessory panel which displays the preview of the selected file.
	 */
	private PreviewPanel previewPanel;

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
	public GeoGebraFileChooser(File currentDirectory)
	{
		this(currentDirectory, false);
	}
	
	/**
	 * Construct a file chooser which may have a restricted file system view if the second
	 * parameter is set to true.
	 * 
	 * @param currentDirectory
	 * @param restricted
	 */
	public GeoGebraFileChooser(File currentDirectory, boolean restricted)
	{
		super(currentDirectory, (restricted ? new RestrictedFileSystemView() : null));
		
		previewPanel = new PreviewPanel(this);
		setAccessory(previewPanel);
		addPropertyChangeListener(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, previewPanel);
		
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
				// Update preview for ggb files
				if (fileChooser.getMode() == GeoGebraFileChooser.MODE_GEOGEBRA) {
					String fileName = file.getName();
					
					if(fileName.endsWith(".ggb")) {
						img = MyXMLio.getPreviewImage(file); // load preview from zip
						
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
					else
						img = null;		
				}
				
				// Update preview for images
				else {
					// fails for a few JPEGs (Java bug? -> OutOfMemory)
					// so turn off preview for large files
					if (file.length() < 512 * 1024) {
						img = ImageIO.read(file); // returns null if file isn't an image
						
						StringBuffer imgInfo = new StringBuffer();
						
						String fileName = file.getName();
						
						if(fileName.length() > 20) {
							imgInfo.append(fileName.substring(0, 20));
							imgInfo.append("..");
						} else {
							imgInfo.append(fileName);
						}

						imgInfo.append(" : ");
						imgInfo.append(img.getWidth());
						imgInfo.append(" x ");
						imgInfo.append(img.getHeight());
						fileLabel.setText(imgInfo.toString());
					}
					else
						img = null;
				}
				repaint();
			} catch (IllegalArgumentException iae) {
				// This is thrown if you select .ico files
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
			private final static int SIZE = 200;
			
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

				// if the selected file is an image go on
				if (img != null) {
					// calculate the scaling factor
					int width = img.getWidth();
					int height = img.getHeight();

					// set drawing location to upper left corner
					int x = 0, y = 0;

					int largerSide = Math.max(width, height);
					if (largerSide > SIZE) { // only resize large images
						double scale = (double) SIZE / (double) largerSide;

						width = (int) (scale * (double) width);
						height = (int) (scale * (double) height);
					}
					
					// centre images
					x = (int) ((getWidth() - width) / 2);
					y = (int) ((getHeight() - height) / 2);

					// draw the image
					g2.drawImage(img, x, y, width, height, null);
				}
			}
		}
	}
}
