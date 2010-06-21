package geogebra.gui.view.spreadsheet;

import geogebra.GeoGebraPanel;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.main.Application;
import geogebra.main.DefaultApplication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

public class OneVariableStatsDialog extends JDialog   {
	
	private Application app;
	private Kernel kernel;
	private MyTable table; 
	private JPanel statPanel;
	private JPanel euclidianPanel;
	private EuclidianView ev;
	private EuclidianController ec;
	private boolean[] showAxes = { true, true };
	private boolean showGrid = false;
	private boolean antialiasing = true;
	
	private GeoElement dataList;
	private GeoList statList;
	private GeoElement boxPlot;
	private GeoElement histogram;
	
	
	
	public OneVariableStatsDialog(MyTable table, Application app){
		super(app.getFrame(),true);
		this.app = app;	
		kernel = app.getKernel();
		this.table = table;
		
		// create an instance of EuclideanView
		ec = new EuclidianController(kernel);
		ev = new EuclidianView(ec, showAxes, showGrid);
		ev.setAntialiasing(antialiasing);
		ev.updateFonts();
		ev.setPreferredSize(new Dimension(300,200));
		ev.setSize(new Dimension(300,200));
		ev.updateSize();
			
		initGUI();
	}
	
	private void initGUI() {

		try {
			setTitle(app.getPlain("One Variable Statistics"));	
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			
			buildStatPanel();
			
			euclidianPanel = new JPanel(new BorderLayout());
			euclidianPanel.add(ev,BorderLayout.CENTER);
			euclidianPanel.setBorder(BorderFactory.createEtchedBorder());
			
			mainPanel.add(euclidianPanel, BorderLayout.CENTER);	
			mainPanel.add(statPanel, BorderLayout.SOUTH);		
			//this.setPreferredSize(new Dimension(400,300));
			
			
			this.getContentPane().add(mainPanel);
			//setResizable(false);
			pack();
			setLocationRelativeTo(app.getFrame());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	private JPanel buildStatPanel(){
	
		// create temporary geoLists
		dataList = table.getCellRangeProcessor().CreateList(table.selectedCellRanges, true, true);	
		createStatList((GeoList)dataList);
		createChart( (GeoList)dataList);
		
		// build the stat table
		String[] columnNames = {" ", " "};	
		Object[][] data = {
			    {"Count",  statList.get(0).toDefinedValueString()},
			    {"Mean",  statList.get(1).toDefinedValueString()},
			    {"Standard Deviation",  statList.get(2).toDefinedValueString()},
			    {"Variance",  statList.get(3).toDefinedValueString()},
			    {" ",  " "},
			    {"Min",  statList.get(4).toDefinedValueString()},
			    {"Q1",  statList.get(5).toDefinedValueString()},
			    {"Median",  statList.get(6).toDefinedValueString()},
			    {"Q3",  statList.get(7).toDefinedValueString()},
			    {"Max",  statList.get(8).toDefinedValueString()}
			};	
		JTable statTable = new JTable(data,columnNames);
		//statTable.setPreferredSize(new Dimension(250,250));
		
		// add table to panel
		statPanel = new JPanel(new BorderLayout());
		statPanel.add(new JScrollPane().add(statTable));
		statPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		return statPanel;
		
	}

	private void  createStatList(GeoList dataList){
		
		String label = dataList.getLabel();	
		String geoText = "";
		ArrayList<String> list = new ArrayList<String>();
		
		try {		
			geoText += "{Length[" + label + "],";
			geoText += "Mean[" + label + "],";
			geoText += "SD[" + label + "],";
			geoText += "Variance[" + label + "],";	
			
			geoText += "Min[" + label + "],";		
			geoText += "Q1[" + label + "],";	
			geoText += "Median[" + label + "],";	
			geoText += "Q3[" + label + "],";
			geoText += "Max[" + label + "]}";	

			Application.debug(geoText);
			// convert list string to geo
			GeoElement[] geos = table.kernel
					.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(geoText, false);
	
			statList = (GeoList) geos[0];
					
		} catch (Exception ex) {
			Application.debug("Creating list failed with exception " + ex);
		}	
		
	}
	
	
	
	private void createChart(GeoList dataList){
		
		String label = dataList.getLabel();	
		String geoText = "";
		
		double xMin = Double.parseDouble(statList.get(4).toValueString());
		double xMax = Double.parseDouble(statList.get(8).toValueString());
		double buffer = .1*(xMax - xMin);
		double barWidth = (xMax - xMin)/5;
	
		
		/*
		// Create boxplot
		listString = "BoxPlot[-1,0.5," + label + "]";
		try {
			GeoElement[] geos = table.kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptionHandling(listString, false);
			boxPlot = geos[0];
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 */	
		
		
		// Create histogram	
		geoText = "BarChart[" + label + "," + Double.toString(barWidth) + "]";
		try {
			GeoElement[] geos = table.kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptionHandling(geoText, false);
			histogram = geos[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		// Set view parameters		
		ev.setRealWorldCoordSystem(xMin - barWidth, xMax + barWidth, -1.0, 6.0);
		ev.setShowAxis(EuclidianView.AXIS_Y, false, true);
		
	}
	
	
	
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		if(!isVisible){
			if(statList != null) 
				statList.remove();
			if(dataList != null) 
				dataList.remove();
			if(boxPlot != null)
				boxPlot.remove();
			if(histogram != null)
				histogram.remove();
			
		}
	}

	
	
}
