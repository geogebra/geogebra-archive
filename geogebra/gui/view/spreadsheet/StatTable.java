package geogebra.gui.view.spreadsheet;

import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class StatTable extends JScrollPane {
	
	// ggb 
	private Application app;
	private Kernel kernel; 
	private StatDialog statDialog;
	private int mode;
	
	private JTable statTable;
	private MyColumnHeaderRenderer columnHeader;
	private MyRowHeader rowHeader;
	//private DefaultTableModel statModel;
	private JScrollPane statScroller;
	
	// data and stat lists
	private GeoList dataList, statList;
	
	// layout
	private static final Color TABLE_GRID_COLOR = StatDialog.TABLE_GRID_COLOR ;
	private static final Color TABLE_HEADER_COLOR = StatDialog.TABLE_HEADER_COLOR;  
	
	public static final int TABLE_ONE_VAR = 0;
	public static final int TABLE_TWO_VAR = 1;
	public static final int TABLE_REGRESSION = 2;
	
	
	
	/*************************************************
	 * Construct the panel
	 */
	public StatTable(Application app, GeoList dataList, int mode){
		
		this.app = app;	
		this.kernel = app.getKernel();				
		this.dataList = dataList;
		this.mode = mode;
		
		// construct the stat table	
		statTable = new JTable(){
			// disable cell editing
		      @Override
		      public boolean isCellEditable(int rowIndex, int colIndex) {
		    	  return false;   
		      }
		      
		      // fill empty scroll pane space with table background color
		      @Override
			  	protected void configureEnclosingScrollPane() {
			  		super.configureEnclosingScrollPane();
			  		Container p = getParent();
			  		if (p instanceof JViewport) {
			  			((JViewport) p).setBackground(getBackground());
			  		}
			      }
		};

		
		// create GeoList statList 
		// --- this holds our stats as a list of geos  
		createStatList();

		// load the statList results into statTable 
		populateStatTable(); 

		
		// column headers
		
		/*
		columnHeader = new MyColumnHeaderRenderer();
		
		columnHeader.setPreferredSize(new Dimension(30, MyTable.TABLE_CELL_HEIGHT));
		for (int i = 0; i < statTable.getColumnCount(); ++ i) {
			statTable.getColumnModel().getColumn(i).setHeaderRenderer(columnHeader);
			statTable.getColumnModel().getColumn(i).setPreferredWidth(30);
		}
		statTable.getTableHeader().setReorderingAllowed(false);
		
		*/
		
		
		// table settings
		statTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		statTable.setColumnSelectionAllowed(true); 
		statTable.setRowSelectionAllowed(true);
		statTable.setShowGrid(true); 	 
		statTable.setGridColor(TABLE_GRID_COLOR); 	 	
		//statTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		statTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		statTable.setAutoCreateColumnsFromModel(false);
		statTable.setPreferredScrollableViewportSize(statTable.getPreferredSize());
		
		statTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		//statTable.setPreferredSize(new Dimension(400,200));
		
	
		// set the width of the stat label column
		autoFitColumnWidth(statTable, 0, 50);
		
		
		// enclose the table in this scrollPane	
		
		this.setViewportView(statTable);
		this.setBorder(BorderFactory.createEmptyBorder());
		
		// create row header
		rowHeader = new MyRowHeader(statTable);		
		this.setRowHeaderView(rowHeader);
	
		
		// set the  corners
		this.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new Corner());
		this.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, new Corner());
		this.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, new Corner());
		
		this.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, new Corner());
		((JPanel)this.getCorner(ScrollPaneConstants.UPPER_LEFT_CORNER)).
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,TABLE_GRID_COLOR));
				
		
		// hide the table header
		statTable.setTableHeader(null);
		this.setColumnHeaderView(null);
		
		
		/*
		JLabel header = new JLabel(app.getMenu("Statistics"));
		header.setHorizontalAlignment(JLabel.LEFT);
		
		header.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),	
				BorderFactory.createEmptyBorder(2,5,2,2)));
		
		// put it all into the stat panel
		this.setLayout(new BorderLayout());
		this.add(header, BorderLayout.NORTH);
		this.add(statScroller, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder());
*/
	
		
	} // END constructor

	
	
	public void removeGeos(){
		if(statList != null){
			statList.remove();
			statList = null;
		}
		
		if(dataList != null){
			dataList.remove();
			dataList = null;
		}
	}
	
	
	private class Corner extends JPanel {

		protected void paintComponent(Graphics g) {
			//g.setColor(bgColor);
			g.setColor(TABLE_HEADER_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	
	
	
	
	
	private void  createStatList(){
		
		String label = dataList.getLabel();	
		
		String text = "";
		ArrayList<String> list = new ArrayList<String>();	

			
		switch(mode){
		case TABLE_ONE_VAR:
			
			String[][]statMap1 = { 
					{app.getMenu("Length.short") ,"Length"},
					{app.getMenu("Mean") ,"Mean"},
					{app.getMenu("StandardDeviation.short") ,"SD"},
					{app.getMenu("SampleStandardDeviation.short") ,"SampleSD"},
					{app.getMenu("Sum") ,"Sum"},
					{null , null},
					{app.getMenu("Minimum.short") ,"Min"},
					{app.getMenu("LowerQuartile.short") ,"Q1"},
					{app.getMenu("Median") ,"Median"},
					{app.getMenu("UpperQuartile.short") ,"Q3"},
					{app.getMenu("Maximum.short") ,"Max"}
			};

			text = createStatListString(statMap1, label);			
			break;


		case TABLE_TWO_VAR:
			
			String[][]statMap2 = {
					{app.getMenu("MeanX") ,"MeanX"},
					{app.getMenu("MeanY") ,"MeanY"},
					{app.getMenu("CorrelationCoefficient.short") ,"PMCC"},
					{app.getMenu("Sxx") ,"Sxx"},
					{app.getMenu("Syy") ,"Syy"},
					{app.getMenu("Sxy") ,"Sxy"},
			};
			
			text = createStatListString(statMap2, label);	
			break;
			
			
		case TABLE_REGRESSION:
			
			String[][]statMap3 = {
					
			};
			
			text = createStatListString(statMap3, label);	
			break;
			
		}

		//System.out.println(text);	
		
		Construction cons = kernel.getConstruction();
		try {
			
			/*
			if(statList != null) 
				statList.remove();
			
			boolean oldSuppressLabelMode = cons.isSuppressLabelsActive();	
			cons.setSuppressLabelCreation(true);
			
			GeoElement[] geos = kernel.getAlgebraProcessor()
				.processAlgebraCommandNoExceptionHandling(text, false);	
			


			geos[0].setLabel("statList");
			geos[0].setAuxiliaryObject(true);
			statList = (GeoList) geos[0];

			cons.setSuppressLabelCreation(oldSuppressLabelMode);

			 */


			if(statList == null){
				statList = new GeoList(kernel.getConstruction());
				statList.setLabel("statList");
				//statList.setLabel(null);
				statList.setAuxiliaryObject(true);
				statList.updateCascade();
			}

			statList = (GeoList) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling((GeoElement)statList, text, true, false);


		} catch (Exception ex) {
			Application.debug("Creating list failed with exception: " + ex);
		}	

	}
	
	private String createStatListString(String[][] statMap, String geoLabel){
		
		String text = "";
		String nameStr = "";
		String cmdStr = "";
				
		// generate a list of lists
		// e.g. { {name, cmd[]}, {name, cmd[]} }
		text += "{";
		for(int i = 0 ; i < statMap.length; ++ i){
			
			nameStr = statMap[i][0];
			cmdStr = statMap[i][1];
			
			// create an interior list 
			// e.g. { "Mean" , Mean[geoLabel] }
			if(cmdStr == null){
				text += "{ \"\", \"\"}";
				
			}else{
				text += "{";
				text += "\"" + nameStr + "\",";
				text += cmdStr + "[" + geoLabel + "]";
				text += "}";
			}
			
			
			// add comma delimiter
			if(i<statMap.length - 1)
				text += ",";
		}
		text += "}";
		
		return text; 
	}
	
	
	
	
	private void populateStatTable(){
		
		//statList.updateCascade();
		
		TableModel statModel = new DefaultTableModel(statList.size(), 1);
		GeoList list;
		for (int elem = 0; elem < statList.size(); ++elem){
			list = (GeoList)statList.get(elem);	
			statModel.setValueAt(list.get(1), elem, 0);
		}
		
		statTable.setModel(statModel);	
		
	/*	
		String[] titles = statDialog.getDataTitles();
		for(int i = 0; i < titles.length; ++i)
			statTable.getColumnModel().getColumn(i).setHeaderValue(titles[i]);
	*/
	
	//	statTable.getTableHeader().resizeAndRepaint();
		statTable.repaint();
	}

	
	
	public void updateTable(){
		//populateStatTable();
		if(statList != null){
			statList.updateCascade();
			//Application.debug("=======> con index " + statList.getConstructionIndex());
		}else{
			//Application.debug("statList is null");
		}
		
		statTable.repaint();
		
	
	}
	
	public void updateData(GeoList dataList){
		this.dataList = dataList;
		createStatList();
		populateStatTable();
		
		updateFonts(getFont());
		
		// repaint
		statTable.repaint();
		rowHeader.repaint();
		
	}
	
	
	
	public void updateFonts(Font font) {

		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;

		setFont(font);
		
		statTable.setFont(font);  
			
		statTable.setRowHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		rowHeader.setFixedCellHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		
		
		//preferredColumnWidth = (int) (MyTable.TABLE_CELL_WIDTH * multiplier);
		//columnHeader.setPreferredSize(new Dimension(preferredColumnWidth, (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
		
		
	}

	
	
	
	/**
	 * Adjust the width of a column to fit the maximum preferred width of 
	 * its cell contents.
	 */
	public void autoFitColumnWidth(JTable table, int column, int defaultColumnWidth){
		
		TableColumn tableColumn = table.getColumnModel().getColumn(column); 
		
		// iterate through the rows and find the preferred width
		int currentWidth = tableColumn.getWidth();
		int prefWidth = 0;
		int tempWidth = -1;
		for (int row = 0; row < table.getRowCount(); row++) {
			if(table.getValueAt(row, column)!=null){
			tempWidth = (int) table.getCellRenderer(row, column)
					.getTableCellRendererComponent(table,
							table.getValueAt(row, column), false, false,
							row, column).getPreferredSize().getWidth();
			prefWidth = Math.max(prefWidth, tempWidth);
			}
		}
			
		// set the new column width
		if (tempWidth == -1) {
			// column is empty
			prefWidth = defaultColumnWidth
					- table.getIntercellSpacing().width;
		} else {
			prefWidth = Math.max(prefWidth, tableColumn.getMinWidth());
		}
		table.getTableHeader().setResizingColumn(tableColumn);
		tableColumn.setWidth(prefWidth
				+ table.getIntercellSpacing().width);
	}
	
	

	//=================================================
	//      Column Header Renderer
	//=================================================
	
	
	private class MyColumnHeaderRenderer extends JLabel implements TableCellRenderer  
	{
		
		public MyColumnHeaderRenderer() {    		
			super("", SwingConstants.LEFT);
			
			setOpaque(true);
			setBackground(TABLE_HEADER_COLOR);
			
			// cell padding
			/*
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(), 
					BorderFactory.createEmptyBorder(2, 5, 2, 5)));
			*/
			
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, 1, 1, TABLE_GRID_COLOR), 
					BorderFactory.createEmptyBorder(0, 5, 0, 2)));
					
			setFont(app.getPlainFont());		
		}

		public Component getTableCellRendererComponent(JTable table, Object value, 
				boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
			
			setText(value.toString());
			return this;        			
		}

	}
	
	
	
	
	
	//======================================================
	//         Table Cell Renderer 
	//======================================================
	
	class MyCellRenderer extends DefaultTableCellRenderer {

		public MyCellRenderer(){
			// cell padding
			setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) 
		{	
			if(value==null){
				setText("");
				return this;
			}
			
			GeoElement geo = (GeoElement)value;
			setText(geo.toDefinedValueString());
			//setText(geo.toDefinedValueString() + ":" + geo.getCommandDescription());
			setFont(app.getPlainFont());
			
			if(row ==1 && column == 1){
				Application.debug(this.getText());
			}
			return this;
		}

	}


	//======================================================
	//         Row Header 
	//======================================================
	

	public class MyRowHeader extends JList  {
		
		DefaultListModel model;
		JTable table;
		

		public MyRowHeader(JTable table){
			super();
			this.table = table;
			
			model = new DefaultListModel();
			GeoList list;
			for (int elem = 0; elem < statList.size(); ++elem){
				list = (GeoList)statList.get(elem);	
				model.addElement(list.get(0));
			}
				setModel(model);
			
			setCellRenderer(new RowHeaderRenderer(table));
			
		//	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//	setSelectionModel( table.getSelectionModel());
			
			
		}


		class RowHeaderRenderer extends JLabel implements ListCellRenderer {

			RowHeaderRenderer(JTable table) {    
				setOpaque(true);
				setBackground(TABLE_HEADER_COLOR);
				
				/*
				setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createEtchedBorder(), 
						BorderFactory.createEmptyBorder(2, 5, 2, 5)));
				*/

				setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 1, 1, TABLE_GRID_COLOR), 
						BorderFactory.createEmptyBorder(2, 5, 2, 5)));
				
				setHorizontalAlignment(RIGHT);
				setFont(table.getTableHeader().getFont());
				
			}

			public Component getListCellRendererComponent( JList list, 
					Object value, int index, boolean isSelected, boolean cellHasFocus) {

				if(value==null){
					setText("");
					return this;
				}
				
				GeoElement geo = (GeoElement)value;
				setText(geo.toDefinedValueString());
		
				return this;
			}

		}


	}

}
