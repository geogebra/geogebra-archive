package geogebra.kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class AnimationManager implements ActionListener {
		
	public final static int STANDARD_ANIMATION_TIME = 10; // secs
	public final static int MAX_ANIMATION_FRAME_RATE = 25; // frames per second
	public final static int MIN_ANIMATION_FRAME_RATE = 2; // frames per second

	private Kernel kernel;
	private ArrayList animatedGeos, changedGeos;
	private Timer timer;
	private double frameRate = MAX_ANIMATION_FRAME_RATE;
	
	public AnimationManager(Kernel kernel) {	
		this.kernel = kernel;
		animatedGeos = new ArrayList();
		changedGeos = new ArrayList();
		
		timer = new Timer(1000 / MAX_ANIMATION_FRAME_RATE, this);		
	}
		
	public synchronized void startAnimation() {
		if (!timer.isRunning() && animatedGeos.size() > 0) 
			timer.start();		
	}
	
	public synchronized void stopAnimation() {
		if (timer.isRunning()) {
			timer.stop();			
			clearAnimatedGeos();
		}
	}
	
	private void clearAnimatedGeos() {
		for (int i=0; i < animatedGeos.size(); i++) {
			GeoElement geo = (GeoElement) animatedGeos.get(i);
			geo.setAnimating(false);
		}
	}
	
	public boolean isRunning() {
		return timer.isRunning();
	}
	
	/**
	 * Adds geo to the list of animated GeoElements.
	 */
	final public synchronized void addAnimatedGeo(GeoElement geo) {
		if (geo.isAnimating() && !animatedGeos.contains(geo)) {
			animatedGeos.add(geo);								
		}
	}
	
	/**
	 * Removes geo from the list of animated GeoElements.
	 */
	final public synchronized void removeAnimatedGeo(GeoElement geo) {
		animatedGeos.remove(geo);
		
		if (animatedGeos.size() == 0) 
			stopAnimation();
	}
	
	/**
	 * Updates all geos in the updateCascadeQueue and their dependent algorithms 
	 * and repaints all views.
	 */
	final public synchronized void actionPerformed(ActionEvent e) {		
		long startTime = System.currentTimeMillis();
		
		// clear list of geos that need to be updated
		changedGeos.clear();
		
		// perform animation step for all animatedGeos
		int size = animatedGeos.size();
		for (int i=0; i < size; i++) {
			Animatable anim = (Animatable) animatedGeos.get(i);
			boolean changed = anim.doAnimationStep(frameRate);
			if (changed)
				changedGeos.add(anim);
		}
		
		// efficiently update all changed GeoElements
		GeoElement.updateCascade(changedGeos);
		
		// repaint views		
		kernel.notifyRepaint();	
		
		// check frame rate
		long compTime = System.currentTimeMillis() - startTime;
		adaptFrameRate(compTime);
	}
	
	/**
	 * Adapts the frame rate depending on how long it took to compute the last frame.
	 * @param frameTime
	 */
	private void adaptFrameRate(long compTime) {		
		double framesPossible = 1000.0 / compTime;
		
		// the frameRate is too high: decrease it
		if (framesPossible < frameRate) {			
			frameRate = Math.max(framesPossible, MIN_ANIMATION_FRAME_RATE);
			timer.setDelay((int) Math.round(1000.0 / frameRate));
			
			// TODO: remove
			System.out.println("DECREASED frame rate: " + frameRate + ", framesPossible: " + framesPossible);
	
		}
				
		// the frameRate is too low: try to increase it
		else if (frameRate < MAX_ANIMATION_FRAME_RATE) {			
			frameRate = Math.min(framesPossible, MAX_ANIMATION_FRAME_RATE);
			timer.setDelay((int) Math.round(1000.0 / frameRate));
			
			// TODO: remove
			System.out.println("INCREASED frame rate: " + frameRate + ", framesPossible: " + framesPossible);
		}
		

	}
	
	private long maxTime = 0;
		
}
