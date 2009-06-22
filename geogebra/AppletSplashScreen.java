package geogebra;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.ImageObserver;

/**
 * Loading screen in applet that shows the GeoGebra logo and a percentage
 * progress while downloading jar files.
 */
public class AppletSplashScreen implements ImageObserver {
	
	// splash screen settings
	private static final int SPLASH_IMAGE_WIDTH = 320;
	private static final int SPLASH_IMAGE_HEIGHT = 106;
	private static final int PROGRESS_IMAGE_WIDTH = 16;
	private static final int PROGRESS_IMAGE_HEIGHT = 16;
	private static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 11);

	
	// splash screen stuff
	private Image splashImage, progressImage;
	private Image splashScreenImage;
	private Graphics splashScreenImageGraphics;
	
	private int width, height;
	private GeoGebraApplet parentApplet;
	private boolean dispose = false;
	
	public AppletSplashScreen(GeoGebraApplet parentApplet) {		
		this.parentApplet = parentApplet;			

		// update splash screen image and paint it
		update();
		parentApplet.repaint();
	}
	
	public void dispose() {
		dispose = true;
		splashScreenImage = null;
		splashScreenImageGraphics = null;
		splashImage = null;
		progressImage = null;
	}
	
	/**
	 * Paints a loading screen to show progress with downloading jar files.
	 */
	private synchronized void update() {
		if (splashScreenImageGraphics == null) {
			// create splash screen image for fast drawing
			width = parentApplet.getWidth();
			height = parentApplet.getHeight();
			if (width <= 0 || height <= 0) return;
			
			splashScreenImage = parentApplet.createImage(width, height);
			if (splashScreenImage != null) {
				splashScreenImageGraphics = splashScreenImage.getGraphics();
	
				// load splash image and animated progress image
				splashImage = parentApplet.getImage(AppletSplashScreen.class.getResource("splash.gif"));
				progressImage = parentApplet.getImage(AppletSplashScreen.class.getResource("progress.gif"));
			} else {
				// we couldn't get splashScreenImageGraphics
				return;				
			}
		}
	
		Graphics2D g = (Graphics2D) splashScreenImageGraphics;
	
		// white background
		g.setColor(Color.white);
		g.clearRect(0, 0, width, height);
	
		// splash image position
		int splashX = -1;
		int splashY = -1;
		if (splashImage != null) {
			splashX = (width - SPLASH_IMAGE_WIDTH) / 2;
			splashY = (height - SPLASH_IMAGE_HEIGHT) / 2
					- (int) (1.5 * PROGRESS_IMAGE_HEIGHT);
		}
	
		// progress image position
		int progressX = (width - PROGRESS_IMAGE_WIDTH) / 2;
		int progressY = (height - PROGRESS_IMAGE_HEIGHT) / 2;
	
		// Splash image fits into content pane: draw splash image
		if (splashX >= 0 && splashY >= 0) {
			g.drawImage(splashImage, splashX, splashY, this);
	
			// put progress image below splash image
			progressY = splashY + SPLASH_IMAGE_HEIGHT;
		}
	
		// draw progress image
		g.drawImage(progressImage, progressX, progressY, this);
	
		// draw status message of JarManager below progress image
		String statusMessage = parentApplet.getDownloadStatusMessage();

		if (statusMessage != null) {
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setColor(Color.darkGray);
			g.setFont(DEFAULT_FONT);
			g.drawString(statusMessage, width / 2
					- (int) (2.5 * statusMessage.length()), progressY
					+ PROGRESS_IMAGE_HEIGHT + 2 * DEFAULT_FONT.getSize());
		}	
	}
	
	public Image getImage() {
		return splashScreenImage;
	}
	
	public boolean isReady() {
		return splashScreenImageGraphics != null;
	}

	
	/**
	 * Updates the progress image (animated gif) in the SplashScreen
	 * loading. Implements ImageObserver.
	 */
	public boolean imageUpdate(Image img, int flags, int x, int y, int w, int h) {
		// stop after dispose() was called
		if (dispose) return false;
		
		// repaint applet to update progress image
		update();
		parentApplet.repaint();		
		return true;
	}

}
