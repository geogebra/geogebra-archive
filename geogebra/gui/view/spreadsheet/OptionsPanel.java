package geogebra.gui.view.spreadsheet;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoText;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class OptionsPanel extends JPanel implements PropertyChangeListener, ActionListener, FocusListener{


	private Application app;
	private StatPanelSettings settings;

	// histogram  panel components
	private JCheckBox ckCumulative, ckManual, ckOverlayNormal,ckOverlayPolygon;
	private JRadioButton ckRelative, ckNormalized,  ckFreq ;
	private JLabel lblFreqType;

	// graph  panel components
	private JCheckBox ckAutoWindow, ckShowGrid;
	private JLabel lblXMin, lblXMax, lblYMin, lblYMax, lblXInterval, lblYInterval;
	private MyTextField fldXMin, fldXMax, fldYMin, fldYMax, fldXInterval, fldYInterval;

	private static final int tab1 = 1;
	private static final int tab2 = 15;

	// option panels
	private JPanel histogramPanel, graphPanel, classesPanel, scatterplotPanel;

	private JPanel mainPanel;
	private boolean showYSettings = true;
	private JTabbedPane tabbedPane;
	private JCheckBox ckShowLines;
	private JLabel lblOverlay;

	private boolean isUpdating = false;


	public OptionsPanel(Application app, StatPanelSettings settings){

		this.app = app;
		this.settings = settings;

		// create option panels
		createHistogramPanel();
		createGraphPanel();
		createClassesPanel();
		createScatterplotPanel();
		/*
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setLayout(new BorderLayout());
		add(new JScrollPane(mainPanel),BorderLayout.CENTER);
		 */

		tabbedPane = new JTabbedPane();
		tabbedPane.addTab(app.getMenu("Classes"), new JScrollPane(classesPanel));
		tabbedPane.addTab(app.getMenu("Histogram"), new JScrollPane(histogramPanel));
		tabbedPane.addTab(app.getMenu("Scatterplot"), new JScrollPane(scatterplotPanel));
		tabbedPane.addTab(app.getMenu("Graph"), new JScrollPane(graphPanel));
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);

		tabbedPane.addFocusListener(this);
		// update
		setLabels();
		updateGUI();

	}


	public void setMode(int mode){

		showYSettings = false;
		tabbedPane.removeAll();
		switch(mode){
		case StatComboPanel.PLOT_HISTOGRAM:
			tabbedPane.addTab(app.getMenu("Classes"), new JScrollPane(classesPanel));
			tabbedPane.addTab(app.getMenu("Histogram"), new JScrollPane(histogramPanel));			
			tabbedPane.addTab(app.getMenu("Graph"), new JScrollPane(graphPanel));
			break;
		case StatComboPanel.PLOT_DOTPLOT:
		case StatComboPanel.PLOT_BOXPLOT:
			tabbedPane.addTab(app.getMenu("Graph"), new JScrollPane(graphPanel));
			break;
		case StatComboPanel.PLOT_SCATTERPLOT:	
			showYSettings = true;
			tabbedPane.addTab(app.getMenu("Scatterplot"), new JScrollPane(scatterplotPanel));
			tabbedPane.addTab(app.getMenu("Graph"), new JScrollPane(graphPanel));
			break;
		case StatComboPanel.PLOT_RESIDUAL:	
			showYSettings = true;
			tabbedPane.addTab(app.getMenu("Graph"), new JScrollPane(graphPanel));
			break;
		}
		updateGUI();
	}

	private void createHistogramPanel(){


		ckCumulative = new JCheckBox();
		ckCumulative.addActionListener(this);

		lblFreqType = new JLabel();
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

		lblOverlay = new JLabel();
		ckOverlayNormal = new JCheckBox();
		ckOverlayNormal.addActionListener(this);

		ckOverlayPolygon = new JCheckBox();
		ckOverlayPolygon.addActionListener(this);


		// layout
		JPanel p = new JPanel(new GridBagLayout()); 
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.anchor=GridBagConstraints.WEST;

		p.add(insetPanel(tab1, lblFreqType),c);
		p.add(insetPanel(tab2,ckCumulative),c);
		p.add(insetPanel(tab2, ckFreq),c);
		p.add(insetPanel(tab2, ckRelative),c);
		p.add(insetPanel(tab2, ckNormalized),c);

		p.add(Box.createRigidArea(new Dimension(0,10)));
		p.add(insetPanel(tab1, lblOverlay),c);
		p.add(insetPanel(tab2, ckOverlayNormal),c);
		p.add(insetPanel(tab2, ckOverlayPolygon),c);

		histogramPanel = new JPanel(new BorderLayout());
		histogramPanel.add(p, BorderLayout.NORTH);
	}

	private void createClassesPanel(){

		// create components
		ckManual = new JCheckBox();		
		ckManual.addActionListener(this);
		// layout
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		p.add(insetPanel(tab1,ckManual));	

		classesPanel = new JPanel(new BorderLayout());
		classesPanel.add(p, BorderLayout.NORTH);

	}


	private void createScatterplotPanel(){

		// create components
		ckShowLines = new JCheckBox();		
		ckShowLines.addActionListener(this);

		// layout
		Box p = Box.createVerticalBox();
		p.add(insetPanel(tab1,ckShowLines));	

		scatterplotPanel = new JPanel(new BorderLayout());
		scatterplotPanel.add(p, BorderLayout.NORTH);	
	}




	private void createGraphPanel(){

		int fieldWidth = 5;

		// create components
		ckAutoWindow = new JCheckBox();		
		ckAutoWindow.addActionListener(this);

		ckShowGrid = new JCheckBox();		
		ckShowGrid.addActionListener(this);

		lblXMin = new JLabel();
		fldXMin = new MyTextField(app.getGuiManager(),fieldWidth);
		fldXMin.setEditable(true);
		fldXMin.addActionListener(this);
		
		lblXMax = new JLabel();
		fldXMax = new MyTextField(app.getGuiManager(),fieldWidth);
		fldXMax.addActionListener(this);
		
		lblYMin = new JLabel();
		fldYMin = new MyTextField(app.getGuiManager(),fieldWidth);
		fldYMin.addActionListener(this);
		
		lblYMax = new JLabel();
		fldYMax = new MyTextField(app.getGuiManager(),fieldWidth);
		fldYMax.addActionListener(this);
		
		lblXInterval = new JLabel();
		fldXInterval = new MyTextField(app.getGuiManager(),fieldWidth);
		fldXInterval.addActionListener(this);
		
		lblYInterval = new JLabel();
		fldYInterval = new MyTextField(app.getGuiManager(),fieldWidth);
		fldYInterval.addActionListener(this);
		
		//layout
		JPanel p = new JPanel(new GridBagLayout()); 
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.anchor=GridBagConstraints.WEST;
		p.add(ckShowGrid,c);
		p.add(ckAutoWindow,c);
		c.anchor=GridBagConstraints.EAST;
		p.add(insetPanelRight(0, lblXMin,fldXMin),c);
		p.add(insetPanelRight(0, lblXMin,fldXMin),c);
		p.add(insetPanelRight(0, lblXMax,fldXMax),c);
		p.add(insetPanelRight(0, lblXInterval,fldXInterval),c);

		p.add(insetPanelRight(tab2, lblYMin,fldYMin),c);
		p.add(insetPanelRight(tab2, lblYMax,fldYMax),c);
		p.add(insetPanelRight(tab2, lblYInterval,fldYInterval),c);

		graphPanel = new JPanel(new BorderLayout());
		graphPanel.add(p, BorderLayout.NORTH);

	}



	private JComponent insetPanelRight(int inset, JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		p.add(Box.createRigidArea(new Dimension(10,0)));
		p.setBorder(BorderFactory.createEmptyBorder(2, inset, 0, 0));
		return p;
	}


	private JComponent insetPanel(int inset, JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		p.setBorder(BorderFactory.createEmptyBorder(2, inset, 0, 0));
		return p;
	}





	private void setLabels(){

		// histogram options
		ckManual.setText(app.getMenu("SetClasssesManually"));		
		lblFreqType.setText(app.getMenu("FrequencyType") + ":");

		ckFreq.setText(app.getMenu("Count"));
		ckNormalized.setText(app.getMenu("Normalized"));
		ckRelative.setText(app.getMenu("Relative"));

		ckCumulative.setText(app.getMenu("Cumulative"));
		lblOverlay.setText(app.getMenu("Overlay"));
		ckOverlayNormal.setText(app.getMenu("NormalCurve"));
		ckOverlayPolygon.setText(app.getMenu("FrequencyPolygon"));

		// graph options
		ckAutoWindow.setText(app.getMenu("AutoDimension"));
		ckShowGrid.setText(app.getMenu("ShowGridlines"));
		lblXMin.setText("X " + app.getPlain("min") + ":");
		lblXMax.setText("X " + app.getPlain("max") + ":");
		lblYMin.setText("Y " + app.getPlain("min") + ":");
		lblYMax.setText("Y " + app.getPlain("max") + ":");

		lblXInterval.setText("X " + app.getMenu("Interval") + ":");
		lblYInterval.setText("Y " + app.getMenu("Interval") + ":");

		ckShowLines.setText(app.getMenu("LineGraph"));
	}


	private void updateGUI(){

		isUpdating  = true;

		ckManual.setSelected(settings.useManualClasses);	
		//cbType.setSelectedIndex(prefs.type);

		if(ckFreq.isSelected())
			settings.type = StatPanelSettings.TYPE_COUNT;
		if(ckNormalized.isSelected())
			settings.type = StatPanelSettings.TYPE_NORMALIZED;
		if(ckRelative.isSelected())
			settings.type = StatPanelSettings.TYPE_RELATIVE;

		ckCumulative.setSelected(settings.isCumulative);	
		ckOverlayNormal.setSelected(settings.hasOverlayNormal);	
		ckOverlayPolygon.setSelected(settings.hasOverlayPolygon);	
		ckShowGrid.setSelected(settings.showGrid);	
		ckAutoWindow.setSelected(settings.isAutomaticWindow);


		lblYMin.setVisible(showYSettings);
		fldYMin.setVisible(showYSettings);
		lblYMax.setVisible(showYSettings);
		fldYMax.setVisible(showYSettings);
		lblYInterval.setVisible(showYSettings);
		fldYInterval.setVisible(showYSettings);

		// enable/disable window dimension components
		fldXMin.setEnabled(!ckAutoWindow.isSelected());
		fldXMax.setEnabled(!ckAutoWindow.isSelected());
		fldXInterval.setEnabled(!ckAutoWindow.isSelected());
		fldYMin.setEnabled(!ckAutoWindow.isSelected());
		fldYMax.setEnabled(!ckAutoWindow.isSelected());
		fldYInterval.setEnabled(!ckAutoWindow.isSelected());

		lblXMin.setEnabled(!ckAutoWindow.isSelected());
		lblXMax.setEnabled(!ckAutoWindow.isSelected());
		lblXInterval.setEnabled(!ckAutoWindow.isSelected());
		lblYMin.setEnabled(!ckAutoWindow.isSelected());
		lblYMax.setEnabled(!ckAutoWindow.isSelected());
		lblYInterval.setEnabled(!ckAutoWindow.isSelected());


		if(ckAutoWindow.isSelected()){
			//PlotSettings ps = settings.plotPanel.getPlotSettings();
			fldXMin.setText("" + settings.xMin);
			fldXMax.setText("" + settings.xMax);
			fldYMin.setText("" + settings.yMin);
			fldYMax.setText("" + settings.yMax);

		}

		isUpdating  = false;
	}


	private void doTextFieldActionPerformed(JTextField source) {
		if(isUpdating) return;
		try {
			String inputText = source.getText().trim();
			NumberValue nv;
			nv = app.getKernel().getAlgebraProcessor().evaluateToNumeric(inputText, false);		
			double value = nv.getDouble();

			if(source == fldXMin){
				settings.xMin = value;  
				firePropertyChange("settings", true, false);
			}
			else if(source == fldXMax){
				settings.xMax = value;
				firePropertyChange("settings", true, false);
			}
			else if(source == fldYMax){
				settings.yMax = value;
				firePropertyChange("settings", true, false);
			}
			else if(source == fldYMin){
				settings.yMin = value;
				firePropertyChange("settings", true, false);
			}
			else if(source == fldXInterval){
				settings.xInterval = value;
				firePropertyChange("settings", true, false);
			}
			else if(source == fldYInterval){
				settings.yInterval = value;
				firePropertyChange("settings", true, false);
			}
						
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {

		if(isUpdating) return;

		Object source  = e.getSource();
		if(source instanceof JTextField){
			doTextFieldActionPerformed((JTextField) source);
		}

		else if(source == ckManual){
			settings.useManualClasses = ckManual.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckCumulative){
			settings.isCumulative = ckCumulative.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckFreq){
			settings.type = StatPanelSettings.TYPE_COUNT;
			firePropertyChange("settings", true, false);
		}
		else if(source == ckRelative){
			settings.type = StatPanelSettings.TYPE_RELATIVE;
			firePropertyChange("settings", true, false);
		}
		else if(source == ckNormalized){
			settings.type = StatPanelSettings.TYPE_NORMALIZED;
			firePropertyChange("settings", true, false);
		}
		else if(source == ckOverlayNormal){
			settings.hasOverlayNormal = ckOverlayNormal.isSelected();
			firePropertyChange("settings", true, false);
		}	
		else if(source == ckOverlayPolygon){
			settings.hasOverlayPolygon = ckOverlayPolygon.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckShowGrid){
			settings.showGrid = ckShowGrid.isSelected();
			firePropertyChange("settings", true, false);
		}
		else if(source == ckAutoWindow){
			settings.isAutomaticWindow = ckAutoWindow.isSelected();
			firePropertyChange("settings", true, false);
		}

		else{
			firePropertyChange("settings", true, false);
		}

		updateGUI();
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub

	}


	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}


	public void focusLost(FocusEvent arg0) {
		System.out.println("================== focus lost =======");

	}


}
