package geogebra.kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class AnimationUpdater implements ActionListener {
	
	private ArrayList geos;
	private Timer timer;
	
	public AnimationUpdater() {
		
		geos = new ArrayList();
		
		timer = new Timer(100, this);
		timer.start();
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
	
	public synchronized void updateCascadeRepaintDelayed(GeoElement geo) {
		geos.add(geo);
	}
	
	public synchronized void actionPerformed(ActionEvent e) {
		
		int size = geos.size();
		
		if (size == 0) return; // no objects need redrawing
		
		GeoElement.updateCascade(geos);
		
		for (int i=0 ; i < size ; i++) {
			GeoElement geo = (GeoElement)geos.get(i);
			geo.updateRepaint();
		}
		
		geos.clear();

	}

}
