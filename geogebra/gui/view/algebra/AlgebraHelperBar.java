package geogebra.gui.view.algebra;

import geogebra.main.Application;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * Helper tool bar for the algebra view which displays some useful
 * buttons to change the functionality (e.g. show auxiliary objects).
 */
public class AlgebraHelperBar extends JToolBar implements ActionListener {
	/**
	 * The algebra view which uses this tool bar.
	 */
	private AlgebraView algebraView;
	
	/**
	 * Instance of the application.
	 */
	private Application app;
	
	/**
	 * Button to show/hide auxiliary objects in the algebra view.
	 */
	private JButton toggleAuxiliary;
	
	/**
	 * Helper bar.
	 * 
	 * @param algebraView
	 * @param app
	 */
	public AlgebraHelperBar(AlgebraView algebraView, Application app) {
		this.algebraView = algebraView;
		this.app = app;
		
		setFloatable(false);
		
		toggleAuxiliary = new JButton("..");
		toggleAuxiliary.addActionListener(this);
		add(toggleAuxiliary);
		
		addSeparator();
		
		updateStates();
		updateLabels();
	}
	
	/**
	 * Update the states of the tool bar buttons.
	 */
	public void updateStates() {
		toggleAuxiliary.setSelected(app.showAuxiliaryObjects());
	}
	
	/**
	 * Update the tool tip texts (used for language change).
	 */
	public void updateLabels() {
		toggleAuxiliary.setToolTipText(app.getPlain("AuxiliaryObjects"));
	}

	/**
	 * React to button presses.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == toggleAuxiliary) {
			app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
			toggleAuxiliary.setSelected(app.showAuxiliaryObjects());
		}
	}
}
