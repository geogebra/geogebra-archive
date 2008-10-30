package geogebra.kernel;

import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class SliderAnimator implements ActionListener {
	
	private GeoNumeric num;
	private Timer timer;
	private int direction = 1; // either 1 or -1
	private AnimationUpdater animationUpdater;
	
	public SliderAnimator(AnimationUpdater animationUpdater, GeoNumeric num) {
		this.num = num;
		this.animationUpdater = animationUpdater;
		
		timer = new Timer(1, this);
		
	}
	
	public void startAnimation(boolean start) {
		if (start && !timer.isRunning()) timer.start();
		else if (!start && timer.isRunning()) timer.stop();
	}
	
	public void stopAnimation() {
		timer.stop();
	}
	
	public boolean isAnimating() {
		return timer.isRunning();
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		
		//Application.debug("speed "+num.getAnimationSpeed()+" type = "+num.getAnimationType());
		
		double val = num.getDouble();
		double min = num.getIntervalMin();
		double max = num.getIntervalMax();
		double step = num.getAnimationStep();
		double speed = num.getAnimationSpeed();
		int type = num.getAnimationType();
		
		if (Kernel.isEqual(max, min, Kernel.MIN_PRECISION)) {
			timer.setDelay(1000); // 1 sec
			return;
		}
		
		int increasing = type == GeoElement.ANIMATION_DECREASING ? -1 : 1;
		
		val += num.getAnimationStep() * direction * increasing * (speed < 0 ? -1 : +1);
		
		
		switch (type) {
		
		case GeoElement.ANIMATION_DECREASING:
		case GeoElement.ANIMATION_INCREASING:
			
			direction = 1; // reset in case slider was cyclic
			
			if (val > max) val = min;
			else if (val < min) val = max;
		
			break;
			
		case GeoElement.ANIMATION_OSCILLATING:
		default: //shouldn't happen
			
			if (val > max) {
				val = max;
				direction *= -1;
			} else if (val < min) {
				val = min;
				direction *= -1;				
			}
			
			break;
		
		
		}
		
		
		num.setValue(val);
		animationUpdater.updateCascadeRepaintDelayed(num);
		
		//num.updateCascade();
		//num.updateRepaint();
		
		// getAnimationSpeed 1 -> 5secs to get from one end of the slider to the other
		/*
		int stepsNumber = (int)((max - min) / step);
		
		if (stepsNumber > AnimationUpdater.MAX_ANIMATION_STEPS) {
	            // use this new increment that will make sure that we
		   		// don't exceed the max number of frames per second
	         step = (max - min) / AnimationUpdater.MAX_ANIMATION_STEPS;
		} 
		*/
		int delay = (int)(AnimationUpdater.STANDARD_ANIMATION_TIME * 1000.0 * step / (max - min) / Math.abs(speed));
		
		if (delay < AnimationUpdater.STANDARD_ANIMATION_TIME) delay = AnimationUpdater.STANDARD_ANIMATION_TIME;
		
		timer.setDelay(delay);

	}

}
