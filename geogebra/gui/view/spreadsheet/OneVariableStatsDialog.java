package geogebra.gui.view.spreadsheet;


import geogebra.kernel.Construction;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OneVariableStatsDialog extends JDialog implements ActionListener  {
	 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	
	private GeoList dataListAll, dataListSelected;
	
	private JButton btnClose;
	private JCheckBox cbShowData;
	private ComboStatPanel comboStatPanel, comboStatPanel2;;
	private DataPanel dataPanel;
	private JSplitPane dataSplitPane;
	private boolean showDataPanel = true;

	
	/*************************************************
	 * Construct the dialog
	 */
	public OneVariableStatsDialog(MyTable table, Application app){
		super(app.getFrame(),true);
		
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
					
		// create data and stat geoLists
		dataListAll = (GeoList) table.getCellRangeProcessor().createListNumeric(table.selectedCellRanges, true, true, true);	
		//dataListAll.setAlgebraVisible(false);
		dataListAll.setLabel(null);
		dataListAll.update();
		
		dataListSelected = (GeoList) table.getCellRangeProcessor().createListNumeric(table.selectedCellRanges, true, true, true);	
		//dataListSelected.setAlgebraVisible(false);
		dataListSelected.setLabel("data");
		dataListSelected.update();
		
		comboStatPanel = new ComboStatPanel(ComboStatPanel.PLOT_HISTOGRAM);
		comboStatPanel2 = new ComboStatPanel(ComboStatPanel.PLOT_STATISTICS);
		
		dataPanel = new DataPanel(app, this, dataListAll, dataListSelected);
		
		initGUI();
		updateFonts();
		setShowDataPanel(false);
		btnClose.requestFocus();
		// dataPanel.ensureTableFill();
	}
	
	
	
	
	
	
	
	//=================================================
	//       Create GUI
	//=================================================
	
	
	private void initGUI() {

		try {
			setTitle(app.getPlain("One Variable Statistics"));	
			
			final JSplitPane comboPanelSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, comboStatPanel, comboStatPanel2);
				
			//this.setPreferredSize(new Dimension(400,300));
						
			dataSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, dataPanel, comboPanelSplit);
			
			
			// button panel
			JPanel buttonPanel = new JPanel(new BorderLayout());
			btnClose = new JButton(app.getPlain("Close"));
			btnClose.addActionListener(this);
			buttonPanel.add(btnClose, BorderLayout.EAST);
			cbShowData = new JCheckBox(app.getPlain("Show Data"));
			cbShowData.addActionListener(this);
			buttonPanel.add(cbShowData,BorderLayout.WEST);
			
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(dataSplitPane, BorderLayout.CENTER);
			mainPanel.add(buttonPanel, BorderLayout.SOUTH);		
			this.getContentPane().add(mainPanel);
			this.getContentPane().setPreferredSize(new Dimension(500,500));
			//setResizable(false);
			pack();
			
			comboPanelSplit.setDividerLocation(0.5);
			
			setLocationRelativeTo(app.getFrame());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	@Override
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		if(!isVisible){
			if(dataListSelected != null) dataListSelected.remove();
			if(dataListAll != null) dataListAll.remove();
			comboStatPanel.plotPanel.detachView();
			comboStatPanel2.plotPanel.detachView();
			
		}
	}
		
	
	
	
	
	
	
	//=====================================================
	// ComboStatPanel class
	//=====================================================
	
	
	private class ComboStatPanel extends JPanel{
		
		// plot types
		private static final int PLOT_HISTOGRAM = 0;
		private static final int PLOT_BOXPLOT = 1;
		private static final int PLOT_DOTPLOT = 2;
		//private static final int PLOT_NORMALQUANTILE = 3;
		private static final int PLOT_STATISTICS = 3;
		private int numPlots = 4;
		//private GeoElement[] plotGeos;
		
		private int plotIndex = PLOT_HISTOGRAM;
		
		private int numClasses = 6;
		private JPanel numClassesPanel;
		private JSlider sliderNumClasses; 
		
		private JComboBox cbPlotTypes;
		private JPanel statDisplayPanel;
		
		private PlotPanel plotPanel;
		private StatPanel statPanel;
		
		
		
		/***************************************** 
		 * Construct a ComboStatPanel
		 */
		private  ComboStatPanel(int selectedPlotIndex){
			
			this.plotIndex = selectedPlotIndex;
			final JTextField lblNumClasses = new JTextField(""+numClasses);
			lblNumClasses.setEditable(false);
			lblNumClasses.setOpaque(true);
			lblNumClasses.setColumns(2);
			lblNumClasses.setHorizontalAlignment(JTextField.CENTER);
			lblNumClasses.setBackground(Color.WHITE);
		
			sliderNumClasses = new JSlider(JSlider.HORIZONTAL, 2, 20, numClasses);
			Dimension d = sliderNumClasses.getPreferredSize();
	        d.width = 80;
	        sliderNumClasses.setPreferredSize(d);
	        sliderNumClasses.setMinimumSize(new Dimension(50,d.height));

		
			sliderNumClasses.setMajorTickSpacing(1);
			sliderNumClasses.setSnapToTicks(true);
			sliderNumClasses.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent evt) {
					JSlider slider = (JSlider) evt.getSource();
					numClasses = slider.getValue();
					lblNumClasses.setText(("" + numClasses));
					updatePlot();
				}
			});


			numClassesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			numClassesPanel.add(sliderNumClasses);
			numClassesPanel.add(new JLabel(app.getPlain("classes" + ": ")));
			numClassesPanel.add(lblNumClasses);
			


			String[] plotNames = new String[numPlots]; 
			plotNames[PLOT_HISTOGRAM] = app.getCommand("Histogram");
			plotNames[PLOT_BOXPLOT] = app.getCommand("Boxplot");
			plotNames[PLOT_DOTPLOT] = app.getCommand("DotPlot");
			plotNames[PLOT_STATISTICS] = app.getPlain("Statistics");
			//plotNames[PLOT_NORMALQUANTILE] = app.getCommand("NormalQuantile");

			cbPlotTypes = new JComboBox(plotNames);
			cbPlotTypes.setSelectedIndex(plotIndex);
			cbPlotTypes.addActionListener(new ActionListener() {       
				public void actionPerformed(ActionEvent e)
				{
					plotIndex = cbPlotTypes.getSelectedIndex();
					updatePlot();
					btnClose.requestFocus();
					
				}
			});      


			JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			plotTypePanel.add(cbPlotTypes);
			plotTypePanel.add(numClassesPanel);

			JPanel controlPanel = new JPanel(new BorderLayout());
			controlPanel.add(plotTypePanel,BorderLayout.WEST);
			//controlPanel.add(numClassesPanel,BorderLayout.CENTER);
			
			plotPanel = new PlotPanel(app);
			statPanel = new StatPanel(app, dataListSelected);

			statDisplayPanel = new JPanel(new CardLayout());
			statDisplayPanel.add("plotPanel", plotPanel);
			statDisplayPanel.add("statPanel", statPanel);
			statDisplayPanel.setBackground(plotPanel.getBackground());
			
			//plotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

			this.setLayout(new BorderLayout());
			this.add(controlPanel,BorderLayout.NORTH);
			this.add(statDisplayPanel,BorderLayout.CENTER);

			updatePlot();
			
		}
		
		
		private void updatePlot(){
			numClassesPanel.setVisible(false);
			switch(plotIndex){
			case PLOT_HISTOGRAM:
				plotPanel.updateHistogram( dataListSelected, numClasses);
				numClassesPanel.setVisible(true);
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
				break;	
			case PLOT_BOXPLOT:
				plotPanel.updateBoxPlot( dataListSelected);
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
				break;
			case PLOT_DOTPLOT:
				plotPanel.updateDotPlot( dataListSelected);
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
				break;
			case PLOT_STATISTICS:
				((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "statPanel");
				break;
			}
			//statDisplayPanel.revalidate();
			//statDisplayPanel.repaint();
			//btnClose.requestFocus();
			
		}
			
		public void updateStatTable(){
			statPanel.updateStatTable();
		}
		
		public void updateStatTableFonts(Font font){
			statPanel.updateFonts(font);
		}
			
	}
	
	// END ComboStatPanel
	//===================================================================
	


	
	//=================================================
	//      Update
	//=================================================
	
	public void updateDataList(){
		
		dataListSelected.updateCascade();
		comboStatPanel.updateStatTable();
		comboStatPanel.updatePlot();
		
		comboStatPanel2.updateStatTable();
		comboStatPanel2.updatePlot();
		
	}
	
	
	public void updateFonts() {
		
		Font font = app.getPlainFont();
		
		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;
		
		setFont(font);
		
		comboStatPanel.updateStatTableFonts(font);
		comboStatPanel2.updateStatTableFonts(font);
		
		dataPanel.updateFonts(font);
		
	}


	public void actionPerformed(ActionEvent e) {
		
		Object source = e.getSource();
		if(source == cbShowData){
			setShowDataPanel(cbShowData.isSelected());
		}
		if(source == btnClose){
			
		}
		btnClose.requestFocus();
	}
	
	private void setShowDataPanel(boolean showDataPanel){
		
		this.showDataPanel = showDataPanel;
		
		if (showDataPanel) {
			if(dataSplitPane == null){
				Application.debug("splitpane null");
			}
			dataSplitPane.setLeftComponent(dataPanel);
			dataSplitPane.setDividerLocation(100);
			dataSplitPane.setDividerSize(4);
		} else {
			dataSplitPane.setLeftComponent(null);
			dataSplitPane.setLastDividerLocation(dataSplitPane.getDividerLocation());
			dataSplitPane.setDividerLocation(0);
			dataSplitPane.setDividerSize(0);
		}
	
	}
	

}
