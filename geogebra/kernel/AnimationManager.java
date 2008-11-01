package geogebra.kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class AnimationManager implements ActionListener {
	
	private Kernel kernel;
	private ArrayList animatedGeos;
	private Timer timer;
	
	public final static int STANDARD_ANIMATION_TIME = 5; // secs
	public final static int MAX_ANIMATION_FRAME_RATE = 30; // frames per second
	public final static int MAX_ANIMATION_STEPS = STANDARD_ANIMATION_TIME *	MAX_ANIMATION_FRAME_RATE;	
	
	public AnimationManager(Kernel kernel) {	
		this.kernel = kernel;
		animatedGeos = new ArrayList();
		
		timer = new Timer(1000 / MAX_ANIMATION_FRAME_RATE, this);		
	}
		
	public synchronized void startAnimation() {
		if (!timer.isRunning() && animatedGeos.size() > 0) 
			timer.start();		
	}
	
	public synchronized void stopAnimation() {
		if (timer.isRunning()) 
			timer.stop();
	}
	
	public boolean isRunning() {
		return timer.isRunning();
	}
	
	/**
	 * Adds geo to the list of animated GeoElements.
	 */
	final public void addAnimatedGeo(GeoElement geo) {
		if (geo.isAnimating()) {
			animatedGeos.add(geo);								
		}
	}
	
	/**
	 * Removes geo from the list of animated GeoElements.
	 */
	final public void removeAnimatedGeo(GeoElement geo) {
		animatedGeos.remove(geo);
		
		if (animatedGeos.size() == 0) 
			stopAnimation();
	}
	
	/**
	 * Updates all geos in the updateCascadeQueue and their dependent algorithms 
	 * and repaints all views.
	 */
	final public synchronized void actionPerformed(ActionEvent e) {		
		// perform animation step for all animatedGeos
		doAnimationStep();
		
		// efficiently update all animated GeoElements
		GeoElement.updateCascade(animatedGeos);
		
		// repaint views		
		kernel.notifyRepaint();		
	}
	
	/**
	 * Performs the next animation step for all animated geos.
	 */
	private void doAnimationStep() {
		
	}

}
