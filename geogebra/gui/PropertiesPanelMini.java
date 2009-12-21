package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.PropertiesPanelMiniListener;
import geogebra.main.Application;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PropertiesPanelMini extends JFrame implements ActionListener, ChangeListener {
	
	JComboBox dashCB;
	//JLabel dashLabel;
	JPanel panel, sizePanel, lineStylePanel;
	JSlider slider;
	Application app;
	float transparency = 0.75f;
	PropertiesPanelMiniListener listener;
	
	public PropertiesPanelMini(Application app, PropertiesPanelMiniListener listener) {
		
		super();
		
		this.app = app;
		this.listener = listener;
		
		
		this.setFocusableWindowState(false);
		this.setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		try { // Java 6u10+ only
			Class<?> awtUtilitiesClass = Class.forName("com.sun.awt.AWTUtilities");
			Method mSetWindowOpacity = awtUtilitiesClass.getMethod("setWindowOpacity", Window.class, float.class);
			mSetWindowOpacity.invoke(null, this, Float.valueOf(transparency));
		} catch (Exception ex) {

			// fallback for OSX Leopard pre-6u10
			this.getRootPane().putClientProperty("Window.alpha", Float.valueOf(transparency));

		} 


		initialize();

	}
	
	private void initialize() {
		//setSize(windowX, windowY);
		//setPreferredSize(new Dimension(windowX, windowY));
		populateContentPane();
	}
	
	private void populateContentPane() {

		//setLayout(null);

		// line style combobox (dashing)		
		DashListRenderer renderer = new DashListRenderer();
		renderer.setPreferredSize(
			new Dimension(130, app.getFontSize() + 6));
		dashCB = new JComboBox(EuclidianView.getLineTypes());
		dashCB.setRenderer(renderer);
		dashCB.addActionListener(this);

		// line style panel
		
		
		lineStylePanel = new JPanel();
		//panel.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//dashLabel = new JLabel();
		//panel.add(dashLabel);
		lineStylePanel.add(dashCB);
		
		Container pane = getContentPane();
		
		
		slider = new JSlider(1, 13);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.addChangeListener(this);
		sizePanel = new JPanel();
		sizePanel.add(slider);		
		

		//setLayout(new FlowLayout(FlowLayout.CENTER));
		//setLayout(new FlowLayout());
		//add(sizePanel);
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(2,0));
		panel.add(sizePanel);
		panel.add(lineStylePanel);
		
		pane.add(panel);




		pack();

	}



	public void actionPerformed(ActionEvent e) {
		//Application.debug(e.getSource().getClass()+"");
		
		if (e.getSource().equals(dashCB)) {
			//Application.debug(dashCB.getSelectedIndex()+"");
			listener.setLineStyle(EuclidianView.getLineTypes()[dashCB.getSelectedIndex()]);
		}
		
	}

	public void stateChanged(ChangeEvent e) {
		//Application.debug(e.getSource().getClass()+"");
		
		if (e.getSource().equals(slider)) {
			//Application.debug(dashCB.getSelectedIndex()+"");
			listener.setSize(slider.getValue());
		}
		
	}

}
