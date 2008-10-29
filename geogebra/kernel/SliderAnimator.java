package geogebra.kernel;

import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

public class SliderAnimator implements ActionListener {
	
	private GeoNumeric num;
	private Construction cons;
	private Timer timer;
	private int direction = 1; // either 1 or -1
	
	public SliderAnimator(Construction cons, GeoNumeric num) {
		this.cons = cons;
		this.num = num;
		
		timer = new Timer(100, this);
		//timer.start();
		
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
		
		Application.debug("speed "+num.getAnimationSpeed()+" type = "+num.getAnimationType());
		
		double val = num.getDouble();
		
		val += num.getAnimationStep() * num.getAnimationSpeed() * direction;
		
		switch (num.getAnimationType()) {
		
		case GeoElement.ANIMATION_CYCLIC:
			
			if (val > num.getIntervalMax()) val = num.getIntervalMin();
		
			break;
		
		default: //GeoElement.ANIMATION_TOANDFRO:
			
			if (val > num.getIntervalMax()) {
				val = num.getIntervalMax();
				direction *= -1;
			} else if (val < num.getIntervalMin()) {
				val = num.getIntervalMin();
				direction *= -1;				
			}
			
			break;
		
		
		}
		
		
		num.setValue(val);
		num.updateCascade();
		num.updateRepaint();

	}

}
