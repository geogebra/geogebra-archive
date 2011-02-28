package geogebra.gui.view.spreadsheet;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;

public class HistogramOptionsPanel extends JPanel implements PropertyChangeListener, ActionListener{


	private Application app;
	private StatPanelSettings prefs;
	private JCheckBox ckCumulative, ckManual, ckOverlayNormal;
	private JRadioButton ckRelative, ckNormalized,  ckFreq ;
	private JLabel lblFreqType;
	private JCheckBox ckOverlayPolygon;
	private JComboBox cbType;


	public HistogramOptionsPanel(Application app, StatPanelSettings prefs){

		this.app = app;
		this.prefs = prefs;
		
		// create components
		ckManual = new JCheckBox();		
		ckManual.addActionListener(this);
		
		ckCumulative = new JCheckBox();
		ckCumulative.addActionListener(this);
		
		lblFreqType = new JLabel();
		cbType = new JComboBox();
		cbType.addActionListener(this);
				
		ckFreq = new JRadioButton();
		ckFreq.addActionListener(this);
		
		ckNormalized = new JRadioButton();
		ckNormalized.addActionListener(this);
		
		ckRelative = new JRadioButton();	
		ckRelative.addActionListener(this);
		
		ButtonGroup g = new ButtonGroup();
		g.add(ckFreq);
		g.add(ckNormalized);
		g.add(ckRelative);
				
		ckOverlayNormal = new JCheckBox();
		ckOverlayNormal.addActionListener(this);
		
		ckOverlayPolygon = new JCheckBox();
		ckOverlayPolygon.addActionListener(this);

		
		// layout
		
		JPanel basicPanel = new JPanel();
		basicPanel.setLayout(new BoxLayout(basicPanel,BoxLayout.Y_AXIS));
		int tab1 = 1;
		int tab2 = 15;
		
		basicPanel.add(insetPanel(ckManual,tab1));	
		basicPanel.add(insetPanel(ckCumulative,tab2));
		basicPanel.add(Box.createRigidArea(new Dimension(0,10)));

		basicPanel.add(insetPanel(lblFreqType,tab1));
		//add(cbType);
		basicPanel.add(insetPanel(ckFreq,tab2));
		basicPanel.add(insetPanel(ckRelative,tab2));
		basicPanel.add(insetPanel(ckNormalized,tab2));

		basicPanel.add(Box.createRigidArea(new Dimension(0,10)));
		basicPanel.add(insetPanel(ckOverlayNormal,tab1));
		basicPanel.add(insetPanel(ckOverlayPolygon,tab1));
		
		JPanel plotPanel = new JPanel();
		plotPanel.setLayout(new BoxLayout(plotPanel,BoxLayout.Y_AXIS));
		plotPanel.add(new JLabel("dsd"));
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(app.getMenu("Properties.Basic"), basicPanel);
		tabbedPane.addTab(app.getMenu("Graph"), plotPanel);
		
		
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
		// update
		setLabels();
		updateGUI();
		

	}

	private JComponent insetPanel(JComponent comp, int inset){
		comp.setBorder(BorderFactory.createEmptyBorder(2, inset, 0, 0));
		return comp;
	}

	private void setLabels(){

		ckManual.setText(app.getMenu("SetClasssesManually"));		
		lblFreqType.setText(app.getMenu("FrequencyType") + ":");
		
		ckFreq.setText(app.getMenu("Count"));
		ckNormalized.setText(app.getMenu("Normalized"));
		ckRelative.setText(app.getMenu("Relative"));
		
		ckCumulative.setText(app.getMenu("Cumulative"));
		ckOverlayNormal.setText(app.getMenu("OverlayNormalCurve"));
		ckOverlayPolygon.setText(app.getMenu("OverlayFrequencyPolygon"));

	}

	private void updateGUI(){

		ckManual.setSelected(prefs.useManualClasses);	
		//cbType.setSelectedIndex(prefs.type);
		
		if(ckFreq.isSelected())
				prefs.type = StatPanelSettings.TYPE_COUNT;
		if(ckNormalized.isSelected())
			prefs.type = StatPanelSettings.TYPE_NORMALIZED;
		if(ckRelative.isSelected())
			prefs.type = StatPanelSettings.TYPE_RELATIVE;
		
		ckCumulative.setSelected(prefs.isCumulative);	
		ckOverlayNormal.setSelected(prefs.hasOverlayNormal);	
		ckOverlayPolygon.setSelected(prefs.hasOverlayPolygon);	

	}



	public void actionPerformed(ActionEvent e) {

		Object source  = e.getSource();

		if(source == ckManual){
			prefs.useManualClasses = ckManual.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckCumulative){
			prefs.isCumulative = ckCumulative.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckFreq){
			prefs.type = StatPanelSettings.TYPE_COUNT;
			firePropertyChange("settings", true, false);
		}
		else if(source == ckRelative){
			prefs.type = StatPanelSettings.TYPE_RELATIVE;
			firePropertyChange("settings", true, false);
		}
		else if(source == ckNormalized){
			prefs.type = StatPanelSettings.TYPE_NORMALIZED;
			firePropertyChange("settings", true, false);
		}
		else if(source == ckOverlayNormal){
			prefs.hasOverlayNormal = ckOverlayNormal.isSelected();
			firePropertyChange("settings", true, false);
		}	
		else if(source == ckOverlayPolygon){
			prefs.hasOverlayPolygon = ckOverlayPolygon.isSelected();
			firePropertyChange("settings", true, false);
		}
		else{
			firePropertyChange("settings", true, false);
		}
			

	}

	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}


}
