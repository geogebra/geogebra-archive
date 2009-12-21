package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.PropertiesPanelMiniListener;
import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PropertiesPanelMini extends JFrame implements ActionListener {
	
	JComboBox dashCB;
	JLabel dashLabel;
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
		JPanel dashPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		dashLabel = new JLabel();
		dashPanel.add(dashLabel);
		dashPanel.add(dashCB);
		
		add(dashPanel);

		pack();

	}



	public void actionPerformed(ActionEvent e) {
		//Application.debug(e.getSource().getClass()+"");
		
		if (e.getSource().equals(dashCB)) {
			//Application.debug(dashCB.getSelectedIndex()+"");
			listener.setLineStyle(EuclidianView.getLineTypes()[dashCB.getSelectedIndex()]);
		}
		
	}

}
