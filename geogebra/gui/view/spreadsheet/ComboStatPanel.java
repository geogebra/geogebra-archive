package geogebra.gui.view.spreadsheet;

import geogebra.kernel.GeoList;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



//=====================================================
// ComboStatPanel class
//=====================================================


public class ComboStatPanel extends JPanel{

	// plot types
	public static final int PLOT_HISTOGRAM = 0;
	public static final int PLOT_BOXPLOT = 1;
	public static final int PLOT_DOTPLOT = 2;
	//private static final int PLOT_NORMALQUANTILE = 3;
	public static final int PLOT_STATISTICS = 3;
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

	private Application app;
	private GeoList dataListSelected;

	/***************************************** 
	 * Construct a ComboStatPanel
	 */
	public  ComboStatPanel(Application app, int selectedPlotIndex, GeoList dataListSelected){
		
		this.dataListSelected = dataListSelected;
		this.app = app;

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
				updatePlot(true);
				//btnClose.requestFocus();
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
				updatePlot(true);
				//btnClose.requestFocus();

			}
		});      


		JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		plotTypePanel.add(cbPlotTypes);
		plotTypePanel.add(numClassesPanel);

		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(plotTypePanel,BorderLayout.WEST);
		//controlPanel.add(numClassesPanel,BorderLayout.CENTER);

		plotPanel = new PlotPanel(app);
		plotPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));


		statPanel = new StatPanel(app, dataListSelected);
		statPanel.setBorder(plotPanel.getBorder());

		statDisplayPanel = new JPanel(new CardLayout());
		statDisplayPanel.add("plotPanel", plotPanel);
		statDisplayPanel.add("statPanel", statPanel);
		statDisplayPanel.setBackground(plotPanel.getBackground());

		//plotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

		this.setLayout(new BorderLayout());
		this.add(controlPanel,BorderLayout.NORTH);
		this.add(statDisplayPanel,BorderLayout.CENTER);

		/*
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(2, 2, 2, 2), 
				BorderFactory.createLineBorder(Color.BLACK)));
		 */

		this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));


		updatePlot(true);

	}

	public void removeGeos(){
		statPanel.removeGeos();
		plotPanel.removeGeos();
	}
	
	public void detachView(){
		plotPanel.detachView();
		
	}

	public void updateData(GeoList dataList){
		dataListSelected = dataList;
	}
	
	
	public void updatePlot(boolean doCreate){
		numClassesPanel.setVisible(false);
		switch(plotIndex){
		case PLOT_HISTOGRAM:
			plotPanel.updateHistogram( dataListSelected, numClasses, doCreate);
			numClassesPanel.setVisible(true);
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;	
		case PLOT_BOXPLOT:
			plotPanel.updateBoxPlot( dataListSelected, doCreate);
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;
		case PLOT_DOTPLOT:
			plotPanel.updateDotPlot( dataListSelected, doCreate);
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "plotPanel");
			break;
		case PLOT_STATISTICS:
			((CardLayout)statDisplayPanel.getLayout()).show(statDisplayPanel, "statPanel");
			//statPanel.updateTable();
			statPanel.updateData(dataListSelected);
			break;
		}

	}


	public void updateStatTableFonts(Font font){
		statPanel.updateFonts(font);
	}

	public void attachView() {
		plotPanel.attachView();
		
	}

	
}
	// END class ComboStatPanel 
	//========================================================


