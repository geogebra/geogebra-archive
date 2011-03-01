/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.gui.util.TableSymbols;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

/**
 * Panel with options for the euclidian view.
 * TODO: optimization: updateGUI() called too often (F.S.)
 * 
 * revised by G.Sturr 2010-8-15
 * 
 */
class OptionsEuclidian extends JPanel  implements ActionListener, FocusListener, ItemListener {	
	private static final long serialVersionUID = 1L;

	private static final String PI_STR = "\u03c0";
	private static final String DEGREE_STR = "\u00b0";
	
	private Application app;
	private Kernel kernel;
	private EuclidianViewInterface view;
	
	// GUI
	private JTabbedPane tabbedPane;
	private JLabel[] dimLabel;
	private JLabel axesRatioLabel;
	private JPanel dimPanel; 
	
	private JButton btBackgroundColor, btAxesColor, btGridColor;
	private JCheckBox cbShowAxes, cbShowGrid, cbBoldGrid, cbGridManualTick, cbShowMouseCoords;
	private JComboBox cbAxesStyle, cbGridType, cbGridStyle, cbGridTickAngle, cbView, cbTooltips;
	private JTextField tfAxesRatioX, tfAxesRatioY;
	private NumberFormat nfAxesRatio;
	private NumberComboBox ncbGridTickX, ncbGridTickY; 
	private JTextField tfMinX, tfMaxX, tfMinY, tfMaxY;	
	private AxisPanel xAxisPanel, yAxisPanel;
	private JLabel gridLabel1, gridLabel2, gridLabel3;
	
	private boolean isIniting;
	
	
	/**
	 * Creates a new dialog for the properties of the Euclidian view.
	 * @param app: parent frame
	 */
	public OptionsEuclidian(Application app, EuclidianView view) {
		
		isIniting = true;
		this.app = app;	
		kernel = app.getKernel();
		this.view = view;	
		
		// build GUI
		initGUI();
		isIniting = false;
	}
	
	
	public void setView(EuclidianViewInterface view){
		this.view = view;
		if(!isIniting)
			updateGUI();
	}

	
	/**
	 * inits GUI with labels of current language	 
	 */
	private void initGUI() {
				
		// create color buttons
		btBackgroundColor = new JButton("\u2588");		
		btAxesColor = new JButton("\u2588");		
		btGridColor = new JButton("\u2588");
		btBackgroundColor.addActionListener(this);
		btAxesColor.addActionListener(this);
		btGridColor.addActionListener(this);			

		// setup axes ratio field
		nfAxesRatio = NumberFormat.getInstance(Locale.ENGLISH);
		nfAxesRatio.setMaximumFractionDigits(5);
		nfAxesRatio.setGroupingUsed(false);
		
		
		
		// create panels for the axes
        xAxisPanel = new AxisPanel(0);
        yAxisPanel = new AxisPanel(1);

        // create panel with comboBox to switch between Euclidian views
        cbView = new JComboBox();
        cbView.addItem(""); //ev
        cbView.addItem(""); //ev2
        cbView = new JComboBox();
        
        cbView.addActionListener(this);       
        
        JPanel selectViewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectViewPanel.add(cbView);
     
        
        // create tabbed pane for basic, axes, and grid options
		 tabbedPane = new JTabbedPane();
				
        /* single panel for both axes --- too wide?
        JPanel axesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        axesPanel.add(xAxisPanel);
        axesPanel.add(Box.createRigidArea(new Dimension(16,0)));
      	*/
       
        tabbedPane.addTab("", buildBasicPanel());
        tabbedPane.addTab("" , xAxisPanel);
        tabbedPane.addTab("", yAxisPanel);   
        tabbedPane.addTab("", buildGridPanel());	
        
        
        // put it all together
		removeAll();	
		setLayout(new BorderLayout());
		add(selectViewPanel, BorderLayout.NORTH);	
		add(tabbedPane, BorderLayout.CENTER);			
         
	}
	
	
	
	private JPanel buildBasicPanel() {
		
		JLabel label;		
			
		//===================================
		// create sub panels
		
		//-------------------------------------
		// window dimensions panel 
		
		dimLabel = new JLabel[4]; // "Xmin", "Xmax" etc.
		for(int i=0;i<4;i++)
			dimLabel[i] = new JLabel("");
		
        JPanel xDimPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        tfMinX = new MyTextField(app.getGuiManager(),8);
		tfMaxX = new MyTextField(app.getGuiManager(),8);
		tfMinX.addActionListener(this);
		tfMaxX.addActionListener(this);
		tfMinX.addFocusListener(this);
		tfMaxX.addFocusListener(this);
		
        xDimPanel.add(dimLabel[0]);
        xDimPanel.add(tfMinX);
        xDimPanel.add(dimLabel[1]);
        xDimPanel.add(tfMaxX);
              
        JPanel yDimPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
		tfMinY = new MyTextField(app.getGuiManager(),8);
		tfMaxY = new MyTextField(app.getGuiManager(),8);		
		tfMinY.addActionListener(this);
		tfMaxY.addActionListener(this);
		tfMinY.addFocusListener(this);
		tfMaxY.addFocusListener(this);
		
        yDimPanel.add(dimLabel[2]);
        yDimPanel.add(tfMinY);
        yDimPanel.add(dimLabel[3]);
        yDimPanel.add(tfMaxY);
   
            
        JPanel axesRatioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        tfAxesRatioX = new MyTextField(app.getGuiManager(),6);
        tfAxesRatioY = new MyTextField(app.getGuiManager(),6);
        tfAxesRatioX.setEnabled(view.isZoomable());
        tfAxesRatioY.setEnabled(view.isZoomable());
        tfAxesRatioX.addActionListener(this);
        tfAxesRatioY.addActionListener(this);
        tfAxesRatioX.addFocusListener(this);
        tfAxesRatioY.addFocusListener(this);
        axesRatioLabel = new JLabel("");
        axesRatioPanel.add(axesRatioLabel);
        axesRatioPanel.add(tfAxesRatioX);
        axesRatioPanel.add(new JLabel(" : "));
        axesRatioPanel.add(tfAxesRatioY);
      
        dimPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        dimPanel.setLayout(new BoxLayout(dimPanel, BoxLayout.Y_AXIS));
        dimPanel.add(xDimPanel);
        dimPanel.add(yDimPanel);
        dimPanel.add(axesRatioPanel);
        dimPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Dimensions")));

		
        //-------------------------------------
		// axes options panel
		JPanel axesOptionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		axesOptionsPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Axes")));
		
		// show axes
		cbShowAxes = new JCheckBox(app.getMenu(app.getPlain("Show") + " " + "Axes")); 						
		axesOptionsPanel.add(cbShowAxes);  
		axesOptionsPanel.add(Box.createRigidArea(new Dimension(10,0)));
		
        // color
        label = new JLabel(app.getPlain("Color") + ":");
        label.setLabelFor(btAxesColor);
        axesOptionsPanel.add(label);
        axesOptionsPanel.add(btAxesColor);
        axesOptionsPanel.add(Box.createRigidArea(new Dimension(10,0)));
        
        // axes style
        cbAxesStyle = new JComboBox();
        label = new JLabel(app.getPlain("LineStyle") + ":");    
        label.setLabelFor(cbAxesStyle);
        cbAxesStyle.addItem("\u2014"); // line       
        cbAxesStyle.addItem("\u2192"); // arrow
        cbAxesStyle.addItem("\u2014" + " " + app.getPlain("Bold")); // bold line 
        cbAxesStyle.addItem("\u2192" + " " + app.getPlain("Bold")); // bold arrow
        cbAxesStyle.setEditable(false); 
        axesOptionsPanel.add(label);   
        axesOptionsPanel.add(cbAxesStyle);   
       
        
        
       //-------------------------------------
		// background color panel
		JPanel bgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		label = new JLabel(app.getPlain("BackgroundColor") + ":");
		bgPanel.add(label);
		bgPanel.add(btBackgroundColor);
		label.setLabelFor(btBackgroundColor);
		
		bgPanel.add(Box.createHorizontalStrut(5));
		
		cbShowMouseCoords = new JCheckBox();
		cbShowMouseCoords.addActionListener(this);
		bgPanel.add(cbShowMouseCoords);
		
		bgPanel.add(Box.createHorizontalStrut(5));
		
		label = new JLabel(app.getPlain("Tooltips")+":");
		bgPanel.add(label);
		
		// TODO implement
		cbTooltips = new JComboBox(new String[] { app.getPlain("On"), app.getPlain("Automatic"), app.getPlain("Off") });
		bgPanel.add(cbTooltips);
		
		
		
		//==========================================
		// create basic panel and add all sub panels
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
		northPanel.setBorder(BorderFactory.createEmptyBorder(5,5,2,5));
        
        northPanel.add(dimPanel);
        northPanel.add(Box.createRigidArea(new Dimension(0,16)));
        northPanel.add(axesOptionsPanel);
        northPanel.add(Box.createRigidArea(new Dimension(0,16)));
        northPanel.add(bgPanel);

        // use a BorderLayout to keep sub panels together
        JPanel basicPanel = new JPanel(new BorderLayout());
        basicPanel.add(northPanel, BorderLayout.NORTH);
  	
       return basicPanel;
		
	}
	
	
	
	private JPanel buildGridPanel() {
		
		int hgap = 5;
		int vgap = 5;	
		
		//==================================================
		// create sub panels		
		
		//-------------------------------------
		// show grid panel            
		JPanel showGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap,vgap));
        cbShowGrid = new JCheckBox(app.getPlain("Show") + " " + app.getMenu("Grid"));  
        cbShowGrid.addActionListener(this);        
        showGridPanel.add(cbShowGrid, BorderLayout.NORTH); 
       
        
        //-------------------------------------
        // grid type panel
        
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap,vgap));    
        typePanel.setBorder(BorderFactory.createTitledBorder((app.getPlain("GridType"))));
        
        // type
        String[] gridTypeLabel = new String[3];
        gridTypeLabel[EuclidianView.GRID_CARTESIAN] = app.getMenu("Cartesian");
        gridTypeLabel[EuclidianView.GRID_ISOMETRIC] = app.getMenu("Isometric");
        gridTypeLabel[EuclidianView.GRID_POLAR] = app.getMenu("Polar");
        cbGridType = new JComboBox(gridTypeLabel);
        cbGridType.addActionListener(this);        
		typePanel.add(cbGridType);
   
		
        // tick intervals
        JPanel tickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap,vgap));        
        cbGridManualTick = new JCheckBox(app.getPlain("TickDistance") +  ":");        
        ncbGridTickX = new NumberComboBox(app); 
		ncbGridTickY  = new NumberComboBox(app);
		
		cbGridManualTick.addActionListener(this);
		ncbGridTickX.addItemListener(this); 
		ncbGridTickY.addItemListener(this);
		
		// angleStep intervals for polar grid lines
		String PI_STRING = "\u03c0";
		String [] angleOptions =  {
				PI_STRING + "/12",
				PI_STRING + "/6",
				PI_STRING + "/4",
				PI_STRING + "/3",
				PI_STRING + "/2",	
		};
		
		cbGridTickAngle = new JComboBox(angleOptions);
		cbGridTickAngle.addItemListener(this);		
		tickPanel.add(cbGridManualTick);
		
		gridLabel1 = new JLabel("x:");
		gridLabel1.setLabelFor(ncbGridTickX);
		tickPanel.add(gridLabel1);
		tickPanel.add(ncbGridTickX);
		
		gridLabel2 = new JLabel("y:");
		gridLabel2.setLabelFor(ncbGridTickY);
		tickPanel.add(gridLabel2);
		tickPanel.add(ncbGridTickY);
		
		gridLabel3 = new JLabel("\u0398" + ":");  // Theta
		gridLabel3.setLabelFor(cbGridTickAngle);
		tickPanel.add(gridLabel3);
		tickPanel.add(cbGridTickAngle);	
		
		typePanel.add(tickPanel);
			
		
		//-------------------------------------
		// style panel
		JPanel stylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, hgap,vgap));
		stylePanel.setBorder(BorderFactory.createTitledBorder((app.getPlain("LineStyle"))));
		
		//line style
		DashListRenderer renderer = new DashListRenderer();
		renderer.setPreferredSize(
			new Dimension(130, app.getFontSize() + 6));
		cbGridStyle = new JComboBox(EuclidianView.getLineTypes());
		cbGridStyle.setRenderer(renderer);
		cbGridStyle.addActionListener(this);
		stylePanel.add(cbGridStyle);
        
        // color   
        JLabel lblColor = new JLabel(app.getPlain("Color") + ":");
        lblColor.setLabelFor(btGridColor);
        // bold
        cbBoldGrid = new JCheckBox(app.getMenu("Bold"));  
        cbBoldGrid.addActionListener(this);
        
        
        stylePanel.add(lblColor);     
        stylePanel.add(btGridColor);
        stylePanel.add(cbBoldGrid); 
			
		
		//==================================================
		// create grid panel and add all the sub panels
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));		
		northPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));	
			
		northPanel.add(showGridPanel);
		northPanel.add(Box.createRigidArea(new Dimension(0,16)));
		northPanel.add(typePanel);	
		northPanel.add(Box.createRigidArea(new Dimension(0,16)));
		northPanel.add(stylePanel);

		JPanel gridPanel = new JPanel(new BorderLayout());
		gridPanel.add(northPanel, BorderLayout.NORTH);
        
        return gridPanel;
	}
	
	
	
	
	public void updateGUI() {
		
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
        
        cbShowMouseCoords.removeActionListener(this);
        cbShowMouseCoords.setSelected(view.getAllowShowMouseCoords());
        cbShowMouseCoords.addActionListener(this);      
        
        
        cbView.removeActionListener(this);
    	if(view == app.getEuclidianView())
    		cbView.setSelectedIndex(0);
		else
			cbView.setSelectedIndex(1);
    	cbView.addActionListener(this);
    	tfAxesRatioX.setEnabled(view.isZoomable());
		tfAxesRatioY.setEnabled(view.isZoomable());
        
        tfMinX.removeActionListener(this);
	 	tfMaxX.removeActionListener(this);
        tfMinY.removeActionListener(this);
	 	tfMaxY.removeActionListener(this);	
	 		((EuclidianView)view).updateBoundObjects();
	 		tfMinX.setText(view.getXminObject().getLabel());
	 		tfMaxX.setText(view.getXmaxObject().getLabel());
	 		tfMinY.setText(view.getYminObject().getLabel());
	 		tfMaxY.setText(view.getYmaxObject().getLabel());
	 	tfMinX.addActionListener(this);
	 	tfMaxX.addActionListener(this);
	 	tfMinY.addActionListener(this);
	 	tfMaxY.addActionListener(this);
        
        
        cbGridType.removeActionListener(this);
        cbGridType.setSelectedIndex(view.getGridType());
        cbGridType.addActionListener(this);
      
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
        cbGridTickAngle.removeItemListener(this);
        double [] gridTicks = view.getGridDistances();
            
        if(view.getGridType() != EuclidianView.GRID_POLAR){
        	
        	ncbGridTickY.setVisible(true);
        	gridLabel2.setVisible(true);
        	cbGridTickAngle.setVisible(false);
        	gridLabel3.setVisible(false);
        	
        	ncbGridTickX.setValue(gridTicks[0]);
        	ncbGridTickY.setValue(gridTicks[1]);
        	gridLabel1.setText("x:");
      
        }else{	
        	ncbGridTickY.setVisible(false);
        	gridLabel2.setVisible(false);
        	cbGridTickAngle.setVisible(true);
        	gridLabel3.setVisible(true);

        	ncbGridTickX.setValue(gridTicks[0]);
        	int val = (int) (view.getGridDistances(2)*12/Math.PI) - 1;
        	if(val == 5) val = 4; //handle Pi/2 problem
        	cbGridTickAngle.setSelectedIndex(val);
        	gridLabel1.setText("r:");
        }
        
        ncbGridTickX.setEnabled(!autoGrid);
        ncbGridTickY.setEnabled(!autoGrid);
        cbGridTickAngle.setEnabled(!autoGrid);
        ncbGridTickX.addItemListener(this);
        ncbGridTickY.addItemListener(this);
        cbGridTickAngle.addItemListener(this);
        
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
	
	
	public void setLabels() {
	
		//TODO --- finish set labels
		
		
		// tab titles
		tabbedPane.setTitleAt(0,app.getMenu("Properties.Basic"));
        tabbedPane.setTitleAt(1, app.getPlain("xAxis"));
        tabbedPane.setTitleAt(2, app.getPlain("yAxis"));   
        tabbedPane.setTitleAt(3, app.getMenu("Grid"));	
		

        // window dimension panel
		dimLabel[0].setText("X " + app.getPlain("min") + ":");
		dimLabel[1].setText("X " + app.getPlain("max") + ":");
		dimLabel[2].setText("Y " + app.getPlain("min") + ":");
		dimLabel[3].setText("Y " + app.getPlain("max") + ":");
		axesRatioLabel.setText(app.getPlain("xAxis") + " : " + app.getPlain("yAxis") + " = " );
	//	dimPanelTitle = "ttt";
			
		
		

		cbView.removeActionListener(this);
		cbView.removeAllItems();
		cbView.addItem(app.getPlain("DrawingPad")); 
		cbView.addItem(app.getPlain("DrawingPad2")); 
		cbView.removeActionListener(this);
		
		cbShowMouseCoords.setText(app.getMenu("ShowMouseCoordinates"));

		
		
	}
	
	
	
	
	public void actionPerformed(ActionEvent e) {	
		doActionPerformed(e.getSource());		
	}
	
	private void doActionPerformed(Object source) {				
		if (source == btBackgroundColor) {
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

		else if (source == cbShowMouseCoords) {
			view.setAllowShowMouseCoords(cbShowMouseCoords.isSelected());	
		}


		else if (source == cbGridType) {
			view.setGridType(cbGridType.getSelectedIndex());
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

		else if (source == cbView) {
			
			if(cbView.getSelectedIndex() == 0)
				setView(app.getEuclidianView());
			else
				setView(app.getGuiManager().getEuclidianView2());
		}

		
		else if (source == tfMinX || source == tfMaxX || source == tfMaxY || source == tfMinY) {
			
			NumberValue minMax = kernel.getAlgebraProcessor().evaluateToNumeric(((JTextField)source).getText(), false);
			//not parsed to number => return all
			if(minMax == null){
				tfMinX.setText(view.getXminObject().getLabel());
		 		tfMaxX.setText(view.getXmaxObject().getLabel());
		 		tfMinY.setText(view.getYminObject().getLabel());
		 		tfMaxY.setText(view.getYmaxObject().getLabel());
			}
			else {
				if(source == tfMinX){
					((EuclidianView)view).setXminObject(minMax);				
				}else if(source== tfMaxX){
					((EuclidianView)view).setXmaxObject(minMax);						
				}else if(source == tfMinY){
					((EuclidianView)view).setYminObject(minMax);				
				}else if(source== tfMaxY){
					((EuclidianView)view).setYmaxObject(minMax);					
				}	
				((EuclidianView)view).setXminObject(view.getXminObject());
				tfAxesRatioX.setEnabled(view.isZoomable());
				tfAxesRatioY.setEnabled(view.isZoomable());
				((EuclidianView)view).updateBounds();
			}
		}

		

		view.updateBackground();		
		updateGUI();		
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
		
		else if (source == cbGridTickAngle) {
			double val = cbGridTickAngle.getSelectedIndex(); 
			if (val >= 0) { 
				double [] ticks = view.getGridDistances();
				//val = 4 gives  5*PI/12, skip this and go to 6*Pi/2 = Pi/2
				if(val == 4) val = 5;
				ticks[2] = (val + 1)*Math.PI/12;
				view.setGridDistances(ticks);
			}
		}

		
		view.updateBackground();
		updateGUI();		
	}
	
	
	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		// handle focus changes in text fields
		doActionPerformed(e.getSource());
		
	}
	
	
	
	//=======================================================
	//              AxisPanel Class
	//=======================================================
	
	
	private class AxisPanel extends JPanel implements ActionListener, ItemListener, FocusListener {		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private int axis;		
		private JCheckBox cbShowAxis, cbAxisNumber, cbManualTicks, cbPositiveAxis, cbDrawAtBorder;
		private NumberComboBox ncbTickDist;	
		private JComboBox cbTickStyle, cbAxisLabel, cbUnitLabel;
		private JTextField tfCross;
		
		
		// axis: 0 = x, 1 = y
		public AxisPanel(int axis) {
			
			this.axis = axis;			
				
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			String strAxis = (axis == 0) ? app.getPlain("xAxis") : app.getPlain("yAxis");
			this.setBorder(BorderFactory.createTitledBorder(strAxis));		
			
			
			cbShowAxis = new JCheckBox(app.getPlain("Show") + " " + strAxis);		
			cbAxisNumber = new JCheckBox(app.getPlain("Show") + " " + app.getPlain("AxisNumbers"));					
			ncbTickDist = new NumberComboBox(app);
			cbManualTicks = new JCheckBox(app.getPlain("TickDistance") + ":");
			
			cbShowAxis.addActionListener(this);			
			cbAxisNumber.addActionListener(this);						
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
			
			
			JPanel showTicksPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));	
			showTicksPanel.add(new JLabel(app.getPlain("AxisTicks") + ":"));			
			showTicksPanel.add(cbTickStyle);	
			
			
			// check box for positive axis
			JPanel showPosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));	
			cbPositiveAxis = new JCheckBox(app.getPlain("PositiveDirectionOnly"));
			cbPositiveAxis.addActionListener(this);
			showPosPanel.add(cbPositiveAxis);	
			
			
			JPanel numberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));			
			numberPanel.add(cbAxisNumber);
			
			JPanel distancePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 5));		
			distancePanel.add(cbManualTicks);			
			distancePanel.add(ncbTickDist);		
					
			
			JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));				
			labelPanel.add(new JLabel(app.getPlain("AxisLabel") + ":"));
			labelPanel.add(cbAxisLabel);
			labelPanel.add(Box.createRigidArea(new Dimension(10,0)));
			labelPanel.add(new JLabel(app.getPlain("AxisUnitLabel") + ":"));
			labelPanel.add(cbUnitLabel);
			
			
			JPanel crossPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));	
			tfCross = new MyTextField(app.getGuiManager(),6);
			tfCross.addActionListener(this);
			crossPanel.add(new JLabel(app.getPlain("CrossAt") + ":"));
			crossPanel.add(tfCross);
			
			cbDrawAtBorder = new JCheckBox();
			cbDrawAtBorder.addActionListener(this);
			crossPanel.add(cbDrawAtBorder);
			crossPanel.add(new JLabel(app.getPlain("StickToEdge")));
			
			
						
			// add all panels
			add(showAxisPanel);
			add(numberPanel);
			add(showPosPanel);
			add(distancePanel);
			add(showTicksPanel);
			add(labelPanel);
			add(crossPanel);
				
			updatePanel();
		}
		
		public void actionPerformed(ActionEvent e) {	
			doActionPerformed(e.getSource());		
		}
		
		private void doActionPerformed(Object source) {	
						
			if (source == cbShowAxis) {
				boolean showXaxis, showYaxis; 
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

			else if (source == tfCross) {			
				double cross = parseDouble(tfCross.getText());			
					double[] ac = view.getAxesCross();
					ac[axis] = cross;
					view.setAxesCross(ac);
			}		

			else if (source == cbPositiveAxis) {
				boolean[] posAxis = view.getPositiveAxes();				
				posAxis[axis] = cbPositiveAxis.isSelected();		
				view.setPositiveAxes(posAxis);
			}		
			
			else if (source == cbDrawAtBorder) {
				boolean[] border = view.getDrawBorderAxes();				
				border[axis] = cbDrawAtBorder.isSelected();		
				view.setDrawBorderAxes(border);
				if(!cbDrawAtBorder.isSelected())
					view.setAxisCross(axis, 0.0);
			}		
			
			
			view.updateBackground();			
			updateGUI();
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
						
			view.updateBackground();			
			updateGUI();
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
	        
	       
	        tfCross.removeActionListener(this);     
	        if(view.getDrawBorderAxes()[axis])
	        	tfCross.setText("");
	        else
	        	tfCross.setText(""+ view.getAxesCross()[axis]);
	        tfCross.setEnabled(!view.getDrawBorderAxes()[axis]);
	        tfCross.addActionListener(this);
	        tfCross.addFocusListener(this);


	        cbPositiveAxis.removeActionListener(this);
	        cbPositiveAxis.setSelected(view.getPositiveAxes()[axis]);
	        cbPositiveAxis.addActionListener(this);


	        cbDrawAtBorder.removeActionListener(this);
	        cbDrawAtBorder.setSelected(view.getDrawBorderAxes()[axis]);
	        cbDrawAtBorder.addActionListener(this);

	        
	        
		}

		public void focusGained(FocusEvent e) {	
		}

		public void focusLost(FocusEvent e) {
			// (needed for textfields)
			doActionPerformed(e.getSource());
		}

		
		
	} // end AxisPanel class
	



}
