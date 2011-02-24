package geogebra.gui;

import geogebra.cas.GeoGebraCAS;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Options for the CAS view.
 */
public class OptionsCAS  extends JPanel implements ActionListener {
	/** */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Application object.
	 */
	private Application app;
	
	/** */
	private JLabel timeoutLabel;
	
	/** */
	private JComboBox timeoutCb;

	/**
	 * Construct CAS option panel.
	 * 
	 * @param app
	 */
	public OptionsCAS(Application app) {
		super(new BorderLayout());
		
		this.app = app;
		
		initGUI();
		updateGUI();
	}
	
	/**
	 * Initialize the user interface.
	 * 
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of calling setLabels()
	 */
	private void initGUI() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		timeoutCb = new JComboBox(new Integer[] { 5, 10, 20, 30, 60 });
		timeoutCb.addActionListener(this);
		
		timeoutLabel = new JLabel();
		timeoutLabel.setLabelFor(timeoutCb);
		
		panel.add(timeoutLabel);
		panel.add(timeoutCb);
		
		add(panel, BorderLayout.CENTER);
	}
	
	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * @remark Do not call setLabels() here
	 */
	public void updateGUI() {
		int timeout = GeoGebraCAS.getTimeout();
		timeoutCb.setSelectedItem(timeout);
	}

	/**
	 * React to actions.
	 */
	public void actionPerformed(ActionEvent e) {
		// change timeout
		if(e.getSource() == timeoutCb) {
			GeoGebraCAS.setTimeout((Integer)timeoutCb.getSelectedItem());
		}
	}

	/**
	 * Update the language of the user interface.
	 */
	public void setLabels() {
		timeoutLabel.setText(app.getPlain("CasTimeout"));
	}

	/**
	 * Apply changes
	 */
	public void apply() {
	}
}
