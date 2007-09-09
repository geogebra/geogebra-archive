/* 
GeoGebra - Dynamic Geometry and Algebra
Copyright Markus Hohenwarter, http://www.geogebra.at

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation; either version 2 of the License, or 
(at your option) any later version.
*/
package geogebra.gui;

import geogebra.Application;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Navigation buttons for the construction protocol
 */
public class ConstructionProtocolNavigation extends JPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton btFirst, btPrev, btPlay, btNext, btLast, btOpenWindow;
	private JLabel lbSteps;
	private JSpinner spDelay;
	private double playDelay = 2; // in seconds	 
	private JPanel playPanel;
	
	private Application app;
	private ConstructionProtocol prot;
	private boolean showPlayButton = true, 
					showConsProtButton = true;
	
	private AutomaticPlayer player;
	private boolean isPlaying;
	
	/**
	 * Creates a new navigation bar to step through the construction protocol.
	 * @param internalNavigation: true if navigation bar is part of the protocol window
	 */
	public ConstructionProtocolNavigation(ConstructionProtocol prot) {		
		this.prot = prot;			
		app = prot.getApplication();	
				
		SpinnerModel model =
	        new SpinnerNumberModel(2, //initial value
	                               1, //min
	                               10, //max
	                               1); //step
		spDelay = new JSpinner(model);	
		lbSteps = new JLabel();
		
		initGUI();		
	}
		
	public boolean isPlayButtonVisible() {
		return showPlayButton;
	}
	
	public void setPlayButtonVisible(boolean flag) {
		showPlayButton = flag;	
		playPanel.setVisible(flag);
	}
	
	public boolean isConsProtButtonVisible() {
		return showConsProtButton;
	}	
	
	public void setConsProtButtonVisible(boolean flag) {		
		showConsProtButton = flag;	
		btOpenWindow.setVisible(flag);
	}
	
	/**
	 * Returns delay between frames of automatic construction protocol
	 * playing in seconds.
	 * @return
	 */
	public double getPlayDelay() {
		return playDelay;
	}
	
	public void setPlayDelay(double delay) {
		playDelay = delay;
		spDelay.setValue(new Double(playDelay));
	}	
	
	public void initGUI() {
		removeAll();
		
		btFirst = new JButton(app.getImageIcon("nav_skipback.png"));
		btLast = new JButton(app.getImageIcon("nav_skipforward.png"));		
		btPrev = new JButton(app.getImageIcon("nav_rewind.png"));		
		btNext = new JButton(app.getImageIcon("nav_fastforward.png"));				
		
		
		btFirst.addActionListener(this);
		btLast.addActionListener(this);		
		btPrev.addActionListener(this); 
		btNext.addActionListener(this); 			
		
		add(btFirst);
		add(btPrev);
		add(lbSteps);			
		add(btNext);
		add(btLast);
		
		playPanel = new JPanel();
		playPanel.setVisible(showPlayButton);
		playPanel.add(Box.createRigidArea(new Dimension(20,10)));
		btPlay = new JButton();
		btPlay.setIcon(app.getImageIcon("nav_play.png"));
		btPlay.addActionListener(this); 	
											
		spDelay.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				playDelay = Double.parseDouble(spDelay.getValue().toString());					
			}			
		});
					
		playPanel.add(btPlay);
		playPanel.add(spDelay);	
		playPanel.add(new JLabel("s"));		
		add(playPanel);
		
		
		if (showConsProtButton) {
			btOpenWindow = new JButton();
			btOpenWindow.setIcon(app.getImageIcon("table.gif"));			
			btOpenWindow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.showConstructionProtocol();					
				}				
			});
					
			add(Box.createRigidArea(new Dimension(20,10)));
			add(btOpenWindow);
		}
		
		setLabels();
		setPlayDelay(playDelay);
		update();
	}
	
	public void setLabels() {
		if (btPlay != null)
			btPlay.setText(app.getPlain("Play"));
		if (btOpenWindow != null)
			btOpenWindow.setToolTipText(app.getPlain("ConstructionProtocol"));
	}
	
	/**
	 * Updates the texts that show the current construction step and
	 * the number of construction steps.	
	 */
	public void update() {	
		int currentStep = prot.getCurrentStepNumber();
		int stepNumber  = prot.getLastStepNumber();
		lbSteps.setText(currentStep + " / " + stepNumber);	
	}
	
	/**
	 * Registers this navigation bar at its protocol
	 * to be informed about updates.
	 */
	public void register() {
		prot.registerNavigationBar(this);
		update();
	}
	
	/**
	 * Unregisters this navigation bar from its protocol.
	 */
	public void unregister() {
		prot.unregisterNavigationBar(this);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
		
		if (source == btFirst) {
			prot.firstStep();		
		} 
		else if (source == btLast) {			
			prot.lastStep();
		}
		else if (source == btPrev) {
			prot.previousStep();
		}
		else if (source == btNext) {
			prot.nextStep();
		}
		else if (source == btPlay) {						
			if (isPlaying) {				
				player.stopAnimation();
			} else {									
				player = new AutomaticPlayer(playDelay);
				player.startAnimation();
			}									
		}	
			
		if (prot.isVisible()) 
			prot.scrollToConstructionStep();
				
		setCursor(Cursor.getDefaultCursor());		
	}
	
	private void setComponentsEnabled(boolean flag) {
		Component comps[] = getComponents();
		for (int i=0; i < comps.length; i++) {
			comps[i].setEnabled(flag);
		}
		btPlay.setEnabled(true);	
		lbSteps.setEnabled(true);
	}	
	
	/**
	 * Steps through the construction automatically.
	 */
	private class AutomaticPlayer implements ActionListener {             
        private Timer timer; // for animation                     
        
        /**
         * Creates a new player to step through the construction
         * automatically.
         * @param delay in seconds between steps
         */
        public AutomaticPlayer(double delay) {
        	 timer = new Timer((int) (delay * 1000), this);        	         	        	
        }      

        public synchronized void startAnimation() {    
        	// dispatch events to play button
			app.startDispatchingEventsTo(btPlay);
			isPlaying = true;
			btPlay.setIcon(app.getImageIcon("nav_pause.png"));
			btPlay.setText(app.getPlain("Pause"));
			setComponentsEnabled(false);
			app.setWaitCursor();
			
			if (prot.getCurrentStepNumber() == prot.getLastStepNumber()) {
        		prot.firstStep();
        	}
			
            timer.start();
        }

        public synchronized void stopAnimation() {
            timer.stop();                   
            
            // unblock application events
			app.stopDispatchingEvents();
			isPlaying = false;
			btPlay.setIcon(app.getImageIcon("nav_play.png"));
			btPlay.setText(app.getPlain("Play"));
			setComponentsEnabled(true);
			app.setDefaultCursor();
        }

        public synchronized void actionPerformed(ActionEvent e) {        	        	
        	prot.nextStep();        	
        	if (prot.getCurrentStepNumber() == prot.getLastStepNumber()) {
        		stopAnimation();
        	}
        }       
    }
}
