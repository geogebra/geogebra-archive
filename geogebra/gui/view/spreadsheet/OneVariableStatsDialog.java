package geogebra.gui.view.spreadsheet;

import geogebra.GeoGebraPanel;
import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import geogebra.main.DefaultApplication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class OneVariableStatsDialog extends JDialog implements ActionListener, TableModelListener  {
	
	// ggb 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	
	private static final int PLOT_HISTOGRAM = 0;
	private static final int PLOT_BOXPLOT = 1;
	private static final int PLOT_DOTPLOT = 2;
	private static final int PLOT_NORMALQUANTILE = 3;
	private int numPlots = 4;
	private int selectedPlot = PLOT_HISTOGRAM;
	
	// gui
	private JPanel statPanel, plotPanel, dataPanel;
	private JTable statTable, dataTable;
	private DefaultTableModel statModel;
	private JScrollPane statScroller;
	
	// new EuclidianView for the dialog plots
	private EuclidianView ev;
	private EuclidianController ec;
	private boolean[] showAxes = { true, true };
	private boolean showGrid = false;
	private boolean antialiasing = true;
	
	
	private GeoElement boxPlot, histogram;
	private GeoList rawDataList, dataList, statList;
	private int numClasses = 6;
	
	private JComboBox cbNumClasses, cbPlotTypes;
	
	private JToggleButton btnSort;
	private JCheckBox enableAllData;
	
	private static final Color TABLE_GRID_COLOR = Color.gray;
	
	
	/*************************************************
	 * Construct the dialog
	 */
	public OneVariableStatsDialog(MyTable table, Application app){
		super(app.getFrame(),true);
		this.app = app;	
		kernel = app.getKernel();
	
		cons = kernel.getConstruction();
				
		// create an instance of EuclideanView
		ec = new EuclidianController(kernel);
		ev = new EuclidianView(ec, showAxes, showGrid);
		ev.setAntialiasing(antialiasing);
		ev.updateFonts();
		ev.setPreferredSize(new Dimension(300,200));
		ev.setSize(new Dimension(300,200));
		ev.updateSize();
		
		// create temporary geoLists
		rawDataList = (GeoList) table.getCellRangeProcessor().createListNumeric(table.selectedCellRanges, true, true, true);
		
		dataList = (GeoList)rawDataList.copyInternal(cons);
		dataList.setLabel(null);
		createStatList(dataList);
		
		
		initGUI();
		updateFonts();
	}
	
	private void initGUI() {

		try {
			setTitle(app.getPlain("One Variable Statistics"));	
			
			JPanel mainPanel = new JPanel(new BorderLayout());
			mainPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			
			mainPanel.add(buildPlotPanel(), BorderLayout.NORTH);	
			mainPanel.add(buildStatPanel(), BorderLayout.CENTER);
			//mainPanel.add(buildDataPanel(), BorderLayout.WEST);
			//this.setPreferredSize(new Dimension(400,300));
						
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildDataPanel(), mainPanel);
			
			this.getContentPane().add(splitPane);
			this.getContentPane().setPreferredSize(new Dimension(500,500));
			//setResizable(false);
			pack();
			setLocationRelativeTo(app.getFrame());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void setVisible(boolean isVisible){
		super.setVisible(isVisible);

		if(!isVisible){
			if(statList != null) statList.remove();
			if(dataList != null) dataList.remove();
			if(rawDataList != null) rawDataList.remove();
			if(boxPlot != null) boxPlot.remove();
			if(histogram != null) histogram.remove();
		}
	}

	
	
	
	
	private JPanel buildPlotPanel(){
		
		JLabel lblNumClasses = new JLabel(app.getPlain("classes")); 
		
		Integer[] classArray = {2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};		
		cbNumClasses = new JComboBox(classArray);
		cbNumClasses.setSelectedItem(numClasses);
		cbNumClasses.addActionListener(this);
		
		JPanel numClassesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		numClassesPanel.add(lblNumClasses);
		numClassesPanel.add(cbNumClasses);
		
		String[] plotNames = new String[numPlots]; 
		plotNames[PLOT_HISTOGRAM] = app.getCommand("Histogram");
		plotNames[PLOT_BOXPLOT] = app.getCommand("Boxplot");
		plotNames[PLOT_DOTPLOT] = app.getCommand("DotPlot");
		plotNames[PLOT_NORMALQUANTILE] = app.getCommand("NormalQuantile");
		
		cbPlotTypes = new JComboBox(plotNames);
		cbPlotTypes.setSelectedIndex(selectedPlot);
		cbPlotTypes.addActionListener(this);
		JPanel plotTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		plotTypePanel.add(cbPlotTypes);
		
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(plotTypePanel,BorderLayout.WEST);
		controlPanel.add(numClassesPanel,BorderLayout.EAST);
		
		plotPanel = new JPanel(new BorderLayout());
		plotPanel.add(ev,BorderLayout.CENTER);
		plotPanel.add(controlPanel,BorderLayout.SOUTH);
		plotPanel.setBorder(BorderFactory.createEtchedBorder());
			
		updatePlot();
		
		return plotPanel;
	}
	
	
	
	// update GUI 
	private void updatePlot(){
		switch(selectedPlot){
		case PLOT_HISTOGRAM:
			createHistogram( dataList, numClasses);
			break;
			
		default:
			createHistogram( dataList, numClasses);
			break;
		}
	}
	
	private JPanel buildStatPanel(){
		
		// build the stat table
		
		/*
		String[] columnNames = {app.getPlain(" "), "Data"};	
		
		Object[][] data = new Object[statList.size()][2];
		GeoList list;
		for (int elem = 0; elem < statList.size(); ++elem){
			list = (GeoList)statList.get(elem);
			data[elem][0] = list.get(0).toDefinedValueString();
			data[elem][1] = list.get(1).toDefinedValueString();
		}
		statModel = new DefaultTableModel(data,columnNames);
		*/
		
		statModel = new DefaultTableModel(statList.size(), 2);
		updateStatModel();
		statTable = new JTable(statModel){
		      public boolean isCellEditable(int rowIndex, int colIndex) {
		        return false;   
		      }
		    };
		    
		 // Enable cell selection 
		statTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		statTable.setColumnSelectionAllowed(true); 
		statTable.setRowSelectionAllowed(true);
		
		statTable.setShowGrid(true); 	 
		statTable.setGridColor(TABLE_GRID_COLOR); 	 
		
		statTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		statTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		statTable.setPreferredScrollableViewportSize(statTable.getPreferredSize());
		statTable.setMinimumSize(new Dimension(50,50));
		
		
		
		statScroller = new JScrollPane(statTable);
		statScroller.setBorder(BorderFactory.createEmptyBorder());
		
		// hide the table header
		statTable.setTableHeader(null);
		statScroller.setColumnHeaderView(null);
		
	
		statPanel = new JPanel(new BorderLayout());
		statPanel.add(statScroller, BorderLayout.CENTER);
		
		// statPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		statPanel.setBorder(BorderFactory.createEmptyBorder());
		
		return statPanel;
		
	}

	
private void updateStatModel(){
		GeoList list;
		for (int elem = 0; elem < statList.size(); ++elem){
			list = (GeoList)statList.get(elem);
			statModel.setValueAt(list.get(0).toDefinedValueString(), elem, 0);
			statModel.setValueAt(list.get(1).toDefinedValueString(), elem, 1);
		}
}
	
	private JPanel buildDataPanel(){
		
				
		// build the data table
		String[] columnNames = {" ", " "};	
		
		Object[][] data = new Object[dataList.size()][2];
		for (int i = 0; i < dataList.size(); ++i){
			data[i][0] = new Boolean(true);
			data[i][1] = dataList.get(i).toDefinedValueString();
		}
		
		TableModel dataModel = new DefaultTableModel(data,columnNames);
		dataModel.addTableModelListener(this);
		dataTable = new JTable(dataModel){
		      
		      @Override
		  	protected void configureEnclosingScrollPane() {
		  		super.configureEnclosingScrollPane();
		  		Container p = getParent();
		  		if (p instanceof JViewport) {
		  			((JViewport) p).setBackground(getBackground());
		  		}
		  	}
		    };
		
		
		
		 // Enable cell selection 
		dataTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		dataTable.setColumnSelectionAllowed(true); 
		dataTable.setRowSelectionAllowed(true);
		
		dataTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		
		//dataTable.setAutoResizeMode(JTable.);
		dataTable.setPreferredScrollableViewportSize(dataTable.getPreferredSize());
		//dataTable.setMinimumSize(new Dimension(50,50));

		TableColumn tc = dataTable.getColumnModel().getColumn(0);  
		tc.setCellEditor(dataTable.getDefaultEditor(Boolean.class));  
		tc.setCellRenderer(dataTable.getDefaultRenderer(Boolean.class));  
		tc.setHeaderRenderer(new CheckBoxHeader(new MyItemListener()));
		tc.setPreferredWidth(40);

		JScrollPane dataScroller = new JScrollPane(dataTable);
		dataScroller.setBorder(BorderFactory.createEmptyBorder());
		
		dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		dataTable.doLayout();
		dataTable.setAutoCreateColumnsFromModel(false);
		

		// hide the table header
		//dataTable.setTableHeader(null);
		//dataScroller.setColumnHeaderView(null);
		
		
		btnSort = new JToggleButton(app.getCommand("sort"));
		enableAllData = new JCheckBox(" ");
		
		
		JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		//headerPanel.add(enableAllData);
		//headerPanel.add(btnSort);
		
		
		
		dataPanel = new JPanel(new BorderLayout());
		dataPanel.add(headerPanel, BorderLayout.NORTH);
		dataPanel.add(dataScroller, BorderLayout.CENTER);
		
			
		return dataPanel;
	}
	
	
	
	public void tableChanged(TableModelEvent e) {

		if(e.getColumn()==0){
			int row = e.getFirstRow();
			int column = e.getColumn();
			TableModel model = (TableModel)e.getSource();
			System.out.println("row="+row+", Column="+column+" "+model.getValueAt(row,column));
			this.updateDataList();
		}
	}
	
	
	class MyItemListener implements ItemListener  
	{  
		public void itemStateChanged(ItemEvent e) {  
			Object source = e.getSource();  
			if (source instanceof AbstractButton == false) return;  
			boolean checked = e.getStateChange() == ItemEvent.SELECTED;  
			for(int x = 0, y = dataTable.getRowCount(); x < y; x++)  
			{  
				dataTable.setValueAt(new Boolean(checked),x,0);  
			}  
		}  
	}  
	
	

	public void actionPerformed(ActionEvent e) {	
		doActionPerformed(e.getSource());
	}	
	
	public void doActionPerformed(Object source) {		
				
		if (source == cbNumClasses) {
			numClasses = (Integer) cbNumClasses.getSelectedItem();
			createHistogram( dataList, numClasses);
		}
		
		if (source == cbPlotTypes) {
			selectedPlot = cbNumClasses.getSelectedIndex();
			updatePlot();
		}
		statPanel.requestFocus(); 
	}



	
	
	private void  createStatList(GeoList dataList){
		
		String label = dataList.getLabel();	
		String text = "";
		ArrayList<String> list = new ArrayList<String>();
		
		try {		
			text += "{";
			text += statListCmdString("Length", label);
			text += ",";
			text += statListCmdString("Mean", label);
			text += ",";
			text += statListCmdString("SD", label);
			text += ",";
			text += statListCmdString("SampleSD", label);
			text += ",";
			text += statListCmdString("Min", label);
			text += ",";
			text += statListCmdString("Q1", label);
			text += ",";
			text += statListCmdString("Median", label);
			text += ",";
			text += statListCmdString("Q3", label);
			text += ",";
			text += statListCmdString("Max", label);
			
			text += "}";
			
			System.out.println(label);	
			System.out.println(text);	
			// convert list string to geo
			GeoElement[] geos = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(text, false);
	
			statList = (GeoList) geos[0];
			System.out.println(text);		
		} catch (Exception ex) {
			Application.debug("Creating list failed with exception " + ex);
			setVisible(false);
		}	
		
	}
	
	private String statListCmdString(String cmdStr, String geoLabel){
		
		String text = "{";
		text += "\"" + app.getCommand(cmdStr)+ "\",";
		text += cmdStr + "[" + geoLabel + "]";
		text += "}";
		
		return text; 
	}
	
	
	private void updateDataList(){
		dataList.clear();
		for(int i=0; i < rawDataList.size(); ++i){
			if(((Boolean)dataTable.getValueAt(i, 0))== true){
				dataList.add(rawDataList.get(i).copyInternal(cons));
			}
		}
		dataList.updateCascade();
		statList.updateCascade();
		updateStatModel();
		ev.repaint();
	}
	
	
	
	
	
	
	
	
	
	private void createHistogram(GeoList dataList, int numClasses){
		
		String label = dataList.getLabel();	
		String geoText = "";
		
		
		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Min[" + label + "]", false);		
		double xMin = nv.getDouble();
		
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Max[" + label + "]", false);		
		double xMax = nv.getDouble();
		
		
		double buffer = .1*(xMax - xMin);
		double barWidth = (xMax - xMin)/(numClasses - 1);  
	
		
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
		if(histogram != null){
			histogram.remove();
		}
		geoText = "BarChart[" + label + "," + Double.toString(barWidth) + "]";
		try {
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			
			GeoElement[] geos = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptionHandling(geoText, false);
			histogram = geos[0];
			histogram.addView(ev);
			histogram.setAlgebraVisible(false);
			cons.setSuppressLabelCreation(oldMacroMode);
			histogram.setLabel(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		double freqMax = getFrequencyTableMax(dataList, barWidth);
		
		// Set view parameters		
		ev.setRealWorldCoordSystem(xMin - barWidth, xMax + barWidth, -1.0, 1.1 * freqMax);
		ev.setShowAxis(EuclidianView.AXIS_Y, false, true);
		
		
		
	}
	
	
	
	
	
	
	// create frequency table
	private double getFrequencyTableMax(GeoList list1, double n){

		
		double [] yval; // y value (= min) in interval 0 <= i < N
		double [] leftBorder; // leftBorder (x val) of interval 0 <= i < N
		GeoElement geo;	

		double mini = Double.MAX_VALUE;
		double maxi = Double.MIN_VALUE;
		int minIndex = -1;
		int maxIndex = -1;

		double step = n ;   //n.getDouble();
		int rawDataSize = list1.size();

		if (step < 0 || kernel.isZero(step) || rawDataSize < 2)
		{
			return 0;
		}


		// find max and min
		for (int i = 0; i < rawDataSize; i++) {
			geo = list1.get(i);
			if (!geo.isGeoNumeric()) {
				return 0;
			}
			double val = ((GeoNumeric)geo).getDouble();

			if (val > maxi) {
				maxi = val;
				maxIndex = i;
			}
			if (val < mini) {
				mini = val;
				minIndex = i;
			}
		}

		if (maxi == mini || maxIndex == -1 || minIndex == -1) {
			return 0;
		}

		double totalWidth = maxi - mini;
		double noOfBars = totalWidth / n;    //n.getDouble();
		double gap = 0;

		int N = (int)noOfBars + 2;
		gap = ((N-1) * step - totalWidth) / 2.0;
		
		NumberValue a = (NumberValue)(new GeoNumeric(cons,mini - gap));
		NumberValue b = (NumberValue)(new GeoNumeric(cons,maxi + gap));

		yval = new double[N];
		leftBorder = new double[N];


		// fill in class boundaries
		//double width = (maxi-mini)/(double)(N-2);
		for (int i=0; i < N; i++) {
			leftBorder[i] = mini - gap + step * i;
		}


		// zero frequencies
		for (int i=0; i < N; i++) yval[i] = 0; 	

		// work out frequencies in each class
		double datum;

		for (int i=0; i < list1.size() ; i++) {
			geo = list1.get(i);
			if (geo.isGeoNumeric())	datum = ((GeoNumeric)geo).getDouble(); 
			else {  return 0; }

			// fudge to make the last boundary eg 10 <= x <= 20
			// all others are 10 <= x < 20
			double oldMaxBorder = leftBorder[N-1];
			leftBorder[N-1] += Math.abs(leftBorder[N-1] / 100000000);

			// check which class this datum is in
			for (int j=1; j < N; j++) {
				//System.out.println("checking "+leftBorder[j]);
				if (datum < leftBorder[j]) 
				{
					//System.out.println(datum+" "+j);
					yval[j-1]++;
					break;
				}
			}

			leftBorder[N-1] = oldMaxBorder;
		}

		double freqMax = 0.0;
		for(int k = 0; k < yval.length; ++k){
			if(yval[k] > freqMax)
				freqMax = yval[k];
		}
		return freqMax;
		
	}

	
	public void updateFonts() {
		
		Font font = app.getPlainFont();
		
		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (double)(size)/12.0;
		
		setFont(font);
		statTable.setRowHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		statTable.setFont(font);  
		dataTable.setRowHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		dataTable.setFont(font);  
		
		
		//statTable.columnHeader.setFont(font);
		//table.preferredColumnWidth = (int) (MyTable.TABLE_CELL_WIDTH * multiplier);
		//table.columnHeader.setPreferredSize(new Dimension(table.preferredColumnWidth, (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
		
	}
	
	
	
	
	
	
	
	
	
	

	//======================================================
	//         Cell Renderer 
	//======================================================
	
	class MyCellRenderer extends DefaultTableCellRenderer {
		
		public MyCellRenderer(){
			
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		
		}
		
		
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) 
		{	
			String text = value.toString();
			
			if(value instanceof Boolean){
				setText(text);
			}else{
				setText(text);
			}
			
			//setFont(app.getFontCanDisplay(text, Font.PLAIN));
			
			return this;
		}

	}

	
	//======================================================
	//         CheckBoxHeader  
	//======================================================
	

	class CheckBoxHeader extends JCheckBox  implements TableCellRenderer, MouseListener { 

		protected CheckBoxHeader rendererComponent;  
		protected int column;  
		protected boolean mousePressed = false;  

		public CheckBoxHeader(ItemListener itemListener) {  
			rendererComponent = this;  
			rendererComponent.addItemListener(itemListener);  
		}  

		public Component getTableCellRendererComponent( JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			
			if (table != null) {  
				JTableHeader header = table.getTableHeader();  
				if (header != null) {  
					rendererComponent.setForeground(header.getForeground());  
					rendererComponent.setBackground(header.getBackground());  
					rendererComponent.setFont(header.getFont());  
					header.addMouseListener(rendererComponent);  
				}  
			}  
			setColumn(column);  
			rendererComponent.setText(null);  
			setBorder(UIManager.getBorder("TableHeader.cellBorder")); 
			setHorizontalAlignment(CENTER);
			
			return rendererComponent;  
		}  
		protected void setColumn(int column) {  
			this.column = column;  
		}  
		public int getColumn() {  
			return column;  
		}  
		protected void handleClickEvent(MouseEvent e) {  
			if (mousePressed) {  
				mousePressed=false;  
				JTableHeader header = (JTableHeader)(e.getSource());  
				JTable tableView = header.getTable();  
				TableColumnModel columnModel = tableView.getColumnModel();  
				int viewColumn = columnModel.getColumnIndexAtX(e.getX());  
				int column = tableView.convertColumnIndexToModel(viewColumn);  

				if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {  
					doClick();  
				}  
			}  
		}  
		public void mouseClicked(MouseEvent e) {  
			handleClickEvent(e);  
			((JTableHeader)e.getSource()).repaint();  
		}  
		public void mousePressed(MouseEvent e) {  
			mousePressed = true;  
		}  
		public void mouseReleased(MouseEvent e) {  
		}  
		public void mouseEntered(MouseEvent e) {  
		}  
		public void mouseExited(MouseEvent e) {  
		}  
	}  



	
	
}
