package geogebra.gui.view.spreadsheet;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatDialogOptionsPanel extends JPanel implements  ActionListener{

	private Application app;
	private StatDialog statDialog;
	
	private JCheckBox cbShowData, cbShowCombo2;
	private AbstractButton cbShowStats;

	public void setShowData(boolean flag) {
		cbShowData.removeActionListener(this);
		cbShowData.setSelected(flag);
		cbShowData.addActionListener(this);
	}

	public void setShowCombo2(boolean flag) {
		cbShowCombo2.removeActionListener(this);
		cbShowCombo2.setSelected(flag);
		cbShowCombo2.addActionListener(this);
	}
	
	public void setShowStats(boolean flag) {
		cbShowStats.removeActionListener(this);
		cbShowStats.setSelected(flag);
		cbShowStats.addActionListener(this);
	}

	public StatDialogOptionsPanel(Application app, StatDialog statDialog){

		this.app = app;
		this.statDialog = statDialog;
		setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));

		cbShowData = new JCheckBox();
		cbShowData.addActionListener(this);

		cbShowCombo2 = new JCheckBox();
		cbShowCombo2.addActionListener(this);

		cbShowStats = new JCheckBox();
		cbShowStats.addActionListener(this);

		int tab1 = 1;
		int tab2 = 15;
		
		add(insetPanel(cbShowStats,tab1));
		add(insetPanel(cbShowData,tab1));
		add(insetPanel(cbShowCombo2,tab1));

		add(Box.createRigidArea(new Dimension(0,10)));
		setLabels();

	}

	private JComponent insetPanel(JComponent comp, int inset){
		comp.setBorder(BorderFactory.createEmptyBorder(2, inset, 0, 0));
		return comp;
	}

	public void actionPerformed(ActionEvent e) {
		Object source  = e.getSource();
		if(source == cbShowData){
			firePropertyChange("cbShowData", !cbShowData.isSelected(), cbShowData.isSelected());
		}
		else if(source == cbShowCombo2){
			firePropertyChange("cbShowCombo2", !cbShowCombo2.isSelected(), cbShowCombo2.isSelected());
		}
		else if(source == cbShowStats){
			firePropertyChange("cbShowStats", !cbShowStats.isSelected(), cbShowStats.isSelected());
		}
	}


	
	private void setLabels(){

		cbShowData.setText(app.getMenu("ShowData"));
		cbShowCombo2.setText(app.getMenu("ShowPlot2"));
		cbShowStats.setText(app.getMenu("ShowStatistics"));
	}

}
