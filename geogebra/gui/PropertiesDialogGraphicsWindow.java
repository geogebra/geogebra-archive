/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

// TODO deprecated due to OptionsDefaults (F.S.)

import geogebra.euclidian.EuclidianView;
import geogebra.gui.util.TableSymbols;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

public class PropertiesDialogGraphicsWindow extends JDialog 
implements ActionListener, FocusListener, 
ItemListener, WindowListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String PI_STR = "\u03c0";
	private static final String DEGREE_STR = "\u00b0";
	
	//private static final int TEXT_FIELD_COLS = 6;
	//private static int PREF_FIELD_WIDTH = 100;	
	
	private Application app;
	private Kernel kernel;
	private EuclidianView view;
	private JButton closeButton, 
	        btBackgroundColor, btAxesColor, btGridColor;
	private JCheckBox cbShowAxes, cbShowGrid, cbBoldGrid, cbIsometric, cbGridManualTick;
	private JComboBox cbAxesStyle, cbGridStyle;
	private JTextField tfAxesRatioX, tfAxesRatioY;
	private NumberFormat nfAxesRatio;
	private NumberComboBox ncbGridTickX, ncbGridTickY;
	private AxisPanel xAxisPanel, yAxisPanel; 
	
	/**
	 * Creates a new dialog for the properties of the euclidian view.
	 * @param app: parent frame
	 */
	public PropertiesDialogGraphicsWindow(Application app, EuclidianView view) {
		super(app.getFrame(), false);
		this.app = app;		
		this.view = view;
		kernel = app.getKernel();
		
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setResizable(false);	
		
		nfAxesRatio = NumberFormat.getInstance(Locale.ENGLISH);
		nfAxesRatio.setMaximumFractionDigits(5);
		nfAxesRatio.setGroupingUsed(false);
		
		// build GUI
		initGUI();		
	}
	
	public void setVisible(boolean flag) {
		if (flag) {
			updateDialog();
		}
		super.setVisible(flag);
	}

	/**
	 * inits GUI with labels of current language	 
	 */
	private void initGUI() {
		setTitle(app.getPlain("DrawingPad"));		
		
		// CREATE OBJECTS		
		// colors
		btBackgroundColor = new JButton("\u2588");		
		btAxesColor = new JButton("\u2588");		
		btGridColor = new JButton("\u2588");
		btBackgroundColor.addActionListener(this);
		btAxesColor.addActionListener(this);
		btGridColor.addActionListener(this);
		
		// Cancel and Apply Button
		closeButton = new JButton(app.getMenu("Close"));	
		closeButton.addActionListener(this);				

		// BUILD PANELS		
		
		// put it all together				 		 		 
		Container contentPane = getContentPane();
		contentPane.removeAll();	
		JPanel dialogPanel = new JPanel(new BorderLayout());
		dialogPanel.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
		contentPane.add(dialogPanel);					
		JPanel northPanel = new JPanel(new BorderLayout());	
		JPanel centerPanel = new JPanel(new BorderLayout());	
		dialogPanel.add(northPanel, BorderLayout.NORTH);
		dialogPanel.add(centerPanel, BorderLayout.CENTER);
		// tabbed pane for axes, grid
		JTabbedPane tabbedPane = new JTabbedPane();
		centerPanel.add(tabbedPane, BorderLayout.CENTER);	
		
		// background color
		JPanel bgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JLabel label = new JLabel(app.getPlain("BackgroundColor") + ":");
		bgPanel.add(label);
		bgPanel.add(btBackgroundColor);
		label.setLabelFor(btBackgroundColor);
		northPanel.add(bgPanel, BorderLayout.NORTH);
			
		// axes panel
		JPanel axesPanel = new JPanel(new BorderLayout());
		axesPanel.setBorder(BorderFactory.createEmptyBorder(5,5,2,5));		
		tabbedPane.addTab(app.getMenu("Axes"), axesPanel);		
		JPanel axesLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		cbShowAxes = new JCheckBox(app.getMenu("Axes")); 						
		axesLine.add(cbShowAxes);  
		axesLine.add(Box.createRigidArea(new Dimension(10,0)));
        
        // axes color
        label = new JLabel(app.getPlain("Color") + ":");
        label.setLabelFor(btAxesColor);
        axesLine.add(label);
        axesLine.add(btAxesColor);
        axesLine.add(Box.createRigidArea(new Dimension(10,0)));
        
        // axes style panel: line or arrow
        cbAxesStyle = new JComboBox();
        label = new JLabel(app.getPlain("LineStyle") + ":");    
        label.setLabelFor(cbAxesStyle);
        cbAxesStyle.addItem("\u2014"); // line       
        cbAxesStyle.addItem("\u2192"); // arrow
        cbAxesStyle.addItem("\u2014" + " " + app.getPlain("Bold")); // bold line 
        cbAxesStyle.addItem("\u2192" + " " + app.getPlain("Bold")); // bold arrow
        cbAxesStyle.setEditable(false);        
        axesLine.add(label);   
        axesLine.add(cbAxesStyle);   
        axesPanel.add(axesLine, BorderLayout.NORTH);
        
        
        JPanel axesRatioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
    	tfAxesRatioX = new MyTextField(app.getGuiManager(), 6);
		tfAxesRatioY = new MyTextField(app.getGuiManager(), 6);
		tfAxesRatioX.addActionListener(this);
		tfAxesRatioY.addActionListener(this);
		tfAxesRatioX.addFocusListener(this);
		tfAxesRatioY.addFocusListener(this);
		axesRatioPanel.add(new JLabel(app.getPlain("xAxis") + " : " + app.getPlain("yAxis") + " = " ));
		axesRatioPanel.add(tfAxesRatioX);
		axesRatioPanel.add(new JLabel(" : "));
		axesRatioPanel.add(tfAxesRatioY);
		axesPanel.add(axesRatioPanel, BorderLayout.SOUTH);		
		
        // add axes panels
        xAxisPanel = new AxisPanel(0);
        yAxisPanel = new AxisPanel(1);
        //JPanel xyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        JTabbedPane xyPanel = new JTabbedPane();
        xyPanel.addTab(app.getPlain("xAxis"), xAxisPanel);
        xyPanel.addTab(app.getPlain("yAxis"), yAxisPanel);
        //xyPanel.add(Box.createRigidArea(new Dimension(10,0)));
        //xyPanel.add(yAxisPanel);        
        axesPanel.add(xyPanel, BorderLayout.CENTER);
        
        // add grid panel
        tabbedPane.addTab(app.getMenu("Grid"), buildGridPanel());	
        //centerPanel.add(buildGridPanel(), BorderLayout.SOUTH);	
		
		// apply, cancel buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPanel.add(closeButton);		
		dialogPanel.add(buttonPanel, BorderLayout.SOUTH);										
				
		packDialog();
	}
		
	private JPanel buildGridPanel() {
        //grid panel
		JPanel gridPanel = new JPanel(new BorderLayout());
		gridPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
		
		// first line: show grid             
		JPanel firstPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,5));		 
		gridPanel.add(firstPanel, BorderLayout.NORTH);
        cbShowGrid = new JCheckBox(app.getMenu("Grid"));  
        cbShowGrid.addActionListener(this);        
        firstPanel.add(cbShowGrid, BorderLayout.NORTH); 
        
        cbBoldGrid = new JCheckBox(app.getMenu("Bold"));  
        cbBoldGrid.addActionListener(this);
        firstPanel.add(cbBoldGrid, BorderLayout.NORTH); 
        
        cbIsometric = new JCheckBox(app.getMenu("Isometric"));  
        cbIsometric.addActionListener(this);
        firstPanel.add(cbIsometric, BorderLayout.NORTH); 
        
        firstPanel.add(Box.createRigidArea(new Dimension(10,0))); 
               
        // second line: color, line style combo
        JLabel label = new JLabel(app.getPlain("Color") + ":");
        label.setLabelFor(btGridColor);
        firstPanel.add(label);
        firstPanel.add(btGridColor);
        firstPanel.add(Box.createRigidArea(new Dimension(10,0)));      
		
        // second line: tick distances
        JPanel secondPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5,5));  
        
        cbGridManualTick = new JCheckBox(app.getPlain("TickDistance") +  ":");        
        ncbGridTickX = new NumberComboBox(app); 
		ncbGridTickY  = new NumberComboBox(app); 
		cbGridManualTick.addActionListener(this);
		ncbGridTickX.addItemListener(this); 
		ncbGridTickY.addItemListener(this);
		
		secondPanel.add(cbGridManualTick);
		label = new JLabel("x:");
		label.setLabelFor(ncbGridTickX);
		secondPanel.add(label);
		secondPanel.add(ncbGridTickX);
		label = new JLabel("y:");
		label.setLabelFor(ncbGridTickY);
		secondPanel.add(label);
		secondPanel.add(ncbGridTickY);		 
		
		
		
		// line style combobox (dashing)		
		DashListRenderer renderer = new DashListRenderer();
		renderer.setPreferredSize(
			new Dimension(130, app.getFontSize() + 6));
		cbGridStyle = new JComboBox(EuclidianView.getLineTypes());
		cbGridStyle.setRenderer(renderer);
		cbGridStyle.addActionListener(this);
		label = new JLabel(app.getPlain("LineStyle") + ":");
		label.setLabelFor(cbGridStyle);	
		
		JPanel thirdPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1,1));  	     
		thirdPanel.add(label);
		thirdPanel.add(cbGridStyle);

		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.add(secondPanel, BorderLayout.NORTH);
		centerPanel.add(thirdPanel, BorderLayout.CENTER);
		//centerPanel.add(fourthPanel, BorderLayout.AFTER_LAST_LINE);
		gridPanel.add(centerPanel, BorderLayout.CENTER);		
        
        return gridPanel;
	}
	
	private void updateDialog() {				
		btBackgroundColor.setForeground(view.getBackground());
		btAxesColor.setForeground(view.getAxesColor());
		btGridColor.setForeground(view.getGridColor());
		
		cbShowAxes.removeActionListener(this);
        cbShowAxes.setSelected(view.getShowXaxis() && view.getShowYaxis());
        cbShowAxes.addActionListener(this);          
        
        cbShowGrid.removeActionListener(this);
        cbShowGrid.setSelected(view.getShowGrid()); 
        cbShowGrid.addActionListener(this);
        
//      Michael Borcherds 2008-04-11
        cbBoldGrid.removeActionListener(this);
        cbBoldGrid.setSelected(view.getGridIsBold()); 
        cbBoldGrid.addActionListener(this);
        
//      Michael Borcherds 2008-04-28
        cbIsometric.removeActionListener(this);
        cbIsometric.setSelected(view.getGridType()==EuclidianView.GRID_ISOMETRIC); 
        cbIsometric.addActionListener(this);
        
        cbAxesStyle.removeActionListener(this);
        cbAxesStyle.setSelectedIndex(view.getAxesLineStyle());
        cbAxesStyle.addActionListener(this);
        
        cbGridStyle.removeActionListener(this);
        int type = view.getGridLineStyle();
        for (int i = 0; i < cbGridStyle.getItemCount(); i++) {
			if (type == ((Integer) cbGridStyle.getItemAt(i)).intValue()) {
				cbGridStyle.setSelectedIndex(i);
				break;
			}
		}
        cbGridStyle.addActionListener(this);
        
        cbGridManualTick.removeActionListener(this);
        boolean autoGrid = view.isAutomaticGridDistance();
        cbGridManualTick.setSelected(!autoGrid);
        cbGridManualTick.addActionListener(this);
                
        ncbGridTickX.removeItemListener(this);
        ncbGridTickY.removeItemListener(this);
        double [] gridTicks = view.getGridDistances();
        ncbGridTickX.setValue(gridTicks[0]);
        ncbGridTickY.setValue(gridTicks[1]);
        ncbGridTickX.setEnabled(!autoGrid);
        ncbGridTickY.setEnabled(!autoGrid);        
        ncbGridTickX.addItemListener(this);
        ncbGridTickY.addItemListener(this);
        
        tfAxesRatioX.removeActionListener(this);
        tfAxesRatioY.removeActionListener(this);
        double xscale = view.getXscale();
        double yscale = view.getYscale();
        if (xscale >= yscale) {
        	tfAxesRatioX.setText("1");
        	tfAxesRatioY.setText(nfAxesRatio.format(xscale/yscale));
		} else {			        			
			tfAxesRatioX.setText(nfAxesRatio.format(yscale/xscale));
			tfAxesRatioY.setText("1");    			
		}
        tfAxesRatioX.addActionListener(this);
        tfAxesRatioY.addActionListener(this);        
        
        xAxisPanel.updatePanel();
        yAxisPanel.updatePanel();
	}
	
	public void actionPerformed(ActionEvent e) {	
		doActionPerformed(e.getSource());		
	}
	
	private void doActionPerformed(Object source) {				
		if (source == closeButton) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			app.storeUndoInfo();
			setCursor(Cursor.getDefaultCursor());
			setVisible(false);
		}
		else if (source == btBackgroundColor) {
			view.setBackground(
					app.getGuiManager().showColorChooser(view.getBackground()));			
		}
		else if (source == btAxesColor) {
			view.setAxesColor(
					app.getGuiManager().showColorChooser(view.getAxesColor()));			
		}
		else if (source == btGridColor) {
			view.setGridColor(
					app.getGuiManager().showColorChooser(view.getGridColor()));			
		}
		else if (source == cbShowAxes) {
			//view.showAxes(cbShowAxes.isSelected(), cbShowAxes.isSelected());	
			view.setShowAxes(cbShowAxes.isSelected(), true);
		}
		else if (source == cbShowGrid) {
			view.showGrid(cbShowGrid.isSelected());			
		}
		else if (source == cbBoldGrid) {
			view.setGridIsBold(cbBoldGrid.isSelected());	// Michael Borcherds 2008-04-11		
		}
		else if (source == cbIsometric) {
			view.setGridType(cbIsometric.isSelected() ? EuclidianView.GRID_ISOMETRIC : EuclidianView.GRID_CARTESIAN);	// Michael Borcherds 2008-04-28		
		}
		else if (source == cbAxesStyle) {
			view.setAxesLineStyle(cbAxesStyle.getSelectedIndex());
		}
		else if (source == cbGridStyle) {
			int type = ((Integer) cbGridStyle.getSelectedItem()).intValue();
			view.setGridLineStyle(type);
		}		
		else if (source == cbGridManualTick) {
			view.setAutomaticGridDistance(!cbGridManualTick.isSelected());
		}
		else if (source == tfAxesRatioX || source == tfAxesRatioY) {			
			double xval = parseDouble(tfAxesRatioX.getText());
			double yval = parseDouble(tfAxesRatioY.getText());
			if (!(Double.isInfinite(xval) || Double.isNaN(xval) ||
				  Double.isInfinite(yval) || Double.isNaN(yval))) {
				// ratio = xval / yval
				// xscale / yscale = ratio
				// => yscale = xscale * xval/yval
				 view.setCoordSystem(view.getXZero(), view.getYZero(), 
				 		view.getXscale(), view.getXscale() * xval/yval);			 
			}
		}		
		
		view.updateBackground();		
		updateDialog();		
	}
	
	private double parseDouble(String text) {	
		if (text == null || text.equals("")) 
			return Double.NaN;
		else
			return kernel.getAlgebraProcessor().evaluateToDouble(text);	
	}
	
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		if (e.getStateChange() != ItemEvent.SELECTED) return;
		
		if (source == ncbGridTickX) {
			double val = ncbGridTickX.getValue(); 
			if (val > 0) { 
				double [] ticks = view.getGridDistances();
				ticks[0] = val;
				view.setGridDistances(ticks);
			}
		}
		else if (source == ncbGridTickY) {
			double val = ncbGridTickY.getValue(); 
			if (val > 0) { 
				double [] ticks = view.getGridDistances();
				ticks[1] = val;
				view.setGridDistances(ticks);
			}
		}
		
		view.updateBackground();
		updateDialog();		
	}	
	
	private void packDialog() {
		pack();
				
		Dimension d1 = getSize();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// center
		int w = Math.min(d1.width, dim.width);
		int h = Math.min(d1.height, (int) (dim.height * 0.8));
		setLocation((dim.width - w) / 2, (dim.height - h) / 2);
		setSize(w, h);		
	}
	
	/*
	 * Window Listener
	 */
	public void windowActivated(WindowEvent e) {
	}
	
	public void windowDeactivated(WindowEvent e) {
	}

	public void windowClosing(WindowEvent e) {
		closeButton.doClick();
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}
	public void windowIconified(WindowEvent e) {
	}
	public void windowOpened(WindowEvent e) {
	}
	
	private class AxisPanel extends JPanel implements ActionListener, ItemListener {		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private int axis;
		
		private JCheckBox cbShowAxis, cbAxisNumber, cbManualTicks;
		private NumberComboBox ncbTickDist, ncbMin, ncbMax;		
		private JComboBox cbTickStyle, cbAxisLabel, cbUnitLabel;				
		
		// axis: 0 = x, 1 = y
		public AxisPanel(int axis) {
			this.axis = axis;			
						
			setLayout(new BorderLayout());
			String strAxis = (axis == 0) ? app.getPlain("xAxis") : app.getPlain("yAxis");
			//cbShowAxis = new JCheckBox(app.getPlain("Showu"));		
			cbAxisNumber = new JCheckBox(app.getPlain("AxisNumbers"));			
			ncbMin = new NumberComboBox(app);
			ncbMax = new NumberComboBox(app);			
			ncbTickDist = new NumberComboBox(app);
			cbManualTicks = new JCheckBox(app.getPlain("TickDistance") + ":");
			
			cbShowAxis.addActionListener(this);			
			cbAxisNumber.addActionListener(this);					
			ncbMin.addItemListener(this);
			ncbMax.addItemListener(this);			
			ncbTickDist.addItemListener(this);
			cbManualTicks.addActionListener(this);
			
			cbAxisLabel = new JComboBox();
			cbUnitLabel = new JComboBox();
			cbTickStyle = new JComboBox();
			cbAxisLabel.setEditable(true);
			cbUnitLabel.setEditable(true);
			cbTickStyle.setEditable(false);
		
			cbUnitLabel.addItem(null);
			cbUnitLabel.addItem(DEGREE_STR); // degrees			
			cbUnitLabel.addItem(PI_STR); // pi				
			cbUnitLabel.addItem("mm");
			cbUnitLabel.addItem("cm");
			cbUnitLabel.addItem("m");
			cbUnitLabel.addItem("km");
			
			cbAxisLabel.addItem(null);
			cbAxisLabel.addItem(axis == 0 ? "x" : "y");
			String [] greeks = TableSymbols.greekLowerCase;
			for (int i = 0; i < greeks.length; i++) {
				cbAxisLabel.addItem(greeks[i]);		
			}					
			
			cbTickStyle = new JComboBox();			
			char big = '|';
			char small = '\'';
			cbTickStyle.addItem(" " + big + "  " + small + "  " + big + "  " + small + "  " + big); // major and minor ticks
			cbTickStyle.addItem( " " + big + "     " + big + "     " + big); // major ticks only
			cbTickStyle.addItem(""); // no ticks
						
			cbAxisLabel.addActionListener(this);
			cbUnitLabel.addActionListener(this);
			cbTickStyle.addActionListener(this);
			
			JPanel showAxisPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));			
			showAxisPanel.add(cbShowAxis);
			showAxisPanel.add(Box.createRigidArea(new Dimension(10,0)));	
			showAxisPanel.add(new JLabel(app.getPlain("AxisTicks") + ":"));			
			showAxisPanel.add(cbTickStyle);	
			
			JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));			
			numberPanel.add(cbAxisNumber);
			numberPanel.add(Box.createRigidArea(new Dimension(5,0)));	
			numberPanel.add(cbManualTicks);			
			numberPanel.add(ncbTickDist);		
			
			JPanel firstLine = new JPanel(new BorderLayout(5,0));
			firstLine.add(showAxisPanel, BorderLayout.NORTH);
			firstLine.add(numberPanel, BorderLayout.CENTER);
					
			JPanel secondLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));	
			secondLine.add(new JLabel(app.getPlain("AxisUnitLabel") + ":"));
			secondLine.add(cbUnitLabel);
			//Dimension dim = cbUnitLabel.getPreferredSize();
			//dim.width = 50;
			//cbUnitLabel.setPreferredSize(dim);
			
			JPanel secondLine2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));				
			secondLine2.add(new JLabel(app.getPlain("AxisLabel") + ":"));
			secondLine2.add(cbAxisLabel);
			JPanel northPanel = new JPanel(new BorderLayout());
			northPanel.add(secondLine, BorderLayout.NORTH);
			northPanel.add(secondLine2, BorderLayout.CENTER);
			
			JPanel thirdLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));				
			thirdLine.add(new JLabel(app.getPlain("min") + ":"));
			thirdLine.add(ncbMin);
			thirdLine.add(new JLabel(app.getPlain("max") + ":"));
			thirdLine.add(ncbMax);
						
			add(firstLine, BorderLayout.NORTH);
			add(northPanel, BorderLayout.CENTER);	
			add(thirdLine, BorderLayout.SOUTH);
			updatePanel();
		}
		
		public void actionPerformed(ActionEvent e) {				
			Object source = e.getSource();
			
			if (source == cbShowAxis) {
				/*
				boolean showXaxis, showYaxis; 
				if (axis == 0) {
					showXaxis = cbShowAxis.isSelected();
					showYaxis = view.getShowYaxis();
				} else {					
					showXaxis = view.getShowXaxis();
					showYaxis = cbShowAxis.isSelected();
				}				
				view.showAxes(showXaxis, showYaxis);
				*/	
				view.setShowAxis(axis, cbShowAxis.isSelected(), true);
			} 
			else if (source == cbAxisNumber) {
				boolean [] show = view.getShowAxesNumbers();
				show[axis] = cbAxisNumber.isSelected();
				view.setShowAxesNumbers(show); 
			}
			else if (source == cbManualTicks) {
				view.setAutomaticAxesNumberingDistance(!cbManualTicks.isSelected(), axis);				
			}			
			else if (source == cbUnitLabel) {
				Object ob = cbUnitLabel.getSelectedItem();
				String text =  (ob == null) ? null : ob.toString().trim();
				String [] labels = view.getAxesUnitLabels();
				labels[axis] = text;
				view.setAxesUnitLabels(labels);
			}
			else if (source == cbAxisLabel) {
				Object ob = cbAxisLabel.getSelectedItem();
				String text =  (ob == null) ? null : ob.toString().trim();
				String [] labels = view.getAxesLabels();
				labels[axis] = text;
				view.setAxesLabels(labels);
			}
			else if (source == cbTickStyle) {
				int type = cbTickStyle.getSelectedIndex();
				int [] styles = view.getAxesTickStyles();
				styles[axis] = type;
				view.setAxesTickStyles(styles);
			}	
			
			view.updateBackground();			
			updateDialog();
		}
		
		public void itemStateChanged(ItemEvent e) {
		
			if (e.getStateChange() != ItemEvent.SELECTED)
				return;
			Object source = e.getSource();					
			if (source == ncbTickDist) {
				double val = ncbTickDist.getValue();
				if (val > 0) 
					view.setAxesNumberingDistance(val, axis);			
			}			
					
			else if (source == ncbMin) {
				double min = ncbMin.getValue();
				double max = axis == 0 ? view.getXmax() : view.getYmax();
				if (min  + Kernel.MIN_PRECISION < max) {				
					if (axis == 0)
						view.setRealWorldCoordSystem(min, max, view.getYmin(), view.getYmax());
					else
						view.setRealWorldCoordSystem(view.getXmin(), view.getXmax(), min, max);
				}
			}
			else if (source == ncbMax) {
				double min = axis == 0 ? view.getXmin() : view.getYmin();
				double max = ncbMax.getValue();
				if (min + Kernel.MIN_PRECISION < max) {	
					if (axis == 0)
						view.setRealWorldCoordSystem(min, max, view.getYmin(), view.getYmax());
					else
						view.setRealWorldCoordSystem(view.getXmin(), view.getXmax(), min, max);
				}
			}
			
			view.updateBackground();			
			updateDialog();
		}
				
		void updatePanel() {		
			cbAxisNumber.removeActionListener(this);
		 	cbAxisNumber.setSelected(view.getShowAxesNumbers()[axis]);
		 	cbAxisNumber.addActionListener(this);
		 	
		 	cbManualTicks.removeActionListener(this);
		 	ncbTickDist.removeItemListener(this);
		 	
		 	cbManualTicks.setSelected(!view.isAutomaticAxesNumberingDistance()[axis]);		 			 			 
		 	ncbTickDist.setValue(view.getAxesNumberingDistances()[axis]);
		 	ncbTickDist.setEnabled(cbManualTicks.isSelected());
		 	
		 	cbManualTicks.addActionListener(this);		 	
		 	ncbTickDist.addItemListener(this);
		 	
		 	ncbMin.removeItemListener(this);
		 	ncbMax.removeItemListener(this);
		 	if (axis == 0) {		 		
		 		ncbMin.setValue(view.getXmin());
				ncbMax.setValue(view.getXmax());
		 	} else {
		 		ncbMin.setValue(view.getYmin());
				ncbMax.setValue(view.getYmax());
		 	}
		 	ncbMin.addItemListener(this);
		 	ncbMax.addItemListener(this);
		 	
		 	cbAxisLabel.removeActionListener(this);
		 	cbAxisLabel.setSelectedItem(view.getAxesLabels()[axis]);
		 	cbAxisLabel.addActionListener(this);
		 	
		 	cbUnitLabel.removeActionListener(this);
		 	cbUnitLabel.setSelectedItem(view.getAxesUnitLabels()[axis]);
		 	cbUnitLabel.addActionListener(this);
		 				
		    cbShowAxis.removeActionListener(this);
	        cbShowAxis.setSelected(view.getShowXaxis());
	        cbShowAxis.addActionListener(this);	        
	        
	        cbTickStyle.removeActionListener(this);
	        int type = view.getAxesTickStyles()[axis];
	        cbTickStyle.setSelectedIndex(type);	        
	        cbTickStyle.addActionListener(this);
	        
	        cbShowAxis.removeActionListener(this);
	        cbShowAxis.setSelected(axis == 0 ? view.getShowXaxis() : view.getShowYaxis());
	        cbShowAxis.addActionListener(this);
		}

		
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed(e.getSource());
	}


}