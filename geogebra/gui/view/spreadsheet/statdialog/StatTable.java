package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.text.NumberFormat;

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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class StatTable extends JScrollPane {

	// ggb 
	private Application app;
	private Kernel kernel; 
	private StatDialog statDialog;
	private int mode;

	private JTable statTable;
	private MyRowHeader rowHeader;
	private JScrollPane statScroller;

	// layout
	private static final Color TABLE_GRID_COLOR = StatDialog.TABLE_GRID_COLOR ;
	private static final Color TABLE_HEADER_COLOR = StatDialog.TABLE_HEADER_COLOR;  


	String[][] statMap;
	private DefaultTableModel model;


	/*************************************************
	 * Construct the panel
	 */
	public StatTable(Application app, StatDialog statDialog, int mode){

		this.app = app;	
		this.kernel = app.getKernel();				
		this.mode = mode;
		this.statDialog = statDialog;

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



		setStatMap();
		model = new DefaultTableModel(statMap.length, 1);
		statTable.setModel(model);


		// table settings
		statTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		statTable.setColumnSelectionAllowed(true); 
		statTable.setRowSelectionAllowed(true);
		statTable.setShowGrid(true); 	 
		statTable.setGridColor(TABLE_GRID_COLOR); 	 	
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


	} // END constructor





	private class Corner extends JPanel {

		protected void paintComponent(Graphics g) {
			//g.setColor(bgColor);
			g.setColor(TABLE_HEADER_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}


	private void setStatMap(){
		if(mode == StatDialog.MODE_ONEVAR)
			statMap = createOneVarStatMap();
		else
			statMap = createTwoVarStatMap();
	}

	private String[][]  createOneVarStatMap(){



		String[][]statMap1 = { 
				{app.getMenu("Length.short") ,"Length"},
				{app.getMenu("Mean") ,"Mean"},
				{app.getMenu("StandardDeviation.short") ,"SD"},
				{app.getMenu("SampleStandardDeviation.short") ,"SampleSD"},
				{app.getMenu("Sum") ,"Sum"},
				{app.getMenu("Sum2") ,"SigmaXX"},
				{null , null},
				{app.getMenu("Minimum.short") ,"Min"},
				{app.getMenu("LowerQuartile.short") ,"Q1"},
				{app.getMenu("Median") ,"Median"},
				{app.getMenu("UpperQuartile.short") ,"Q3"},
				{app.getMenu("Maximum.short") ,"Max"},
		};

		return statMap1;
	}


	private String[][]  createTwoVarStatMap(){

		String[][]statMap2 = {
				{app.getMenu("Length.short") ,"Length"},
				{app.getMenu("MeanX") ,"MeanX"},
				{app.getMenu("MeanY") ,"MeanY"},
				{app.getMenu("Sx") ,"SampleSDX"},
				{app.getMenu("Sy") ,"SampleSDY"},
				{app.getMenu("CorrelationCoefficient.short") ,"PMCC"},
				{app.getMenu("Spearman.short") ,"Spearman"},
				{app.getMenu("Sxx") ,"Sxx"},
				{app.getMenu("Syy") ,"Syy"},
				{app.getMenu("Sxy") ,"Sxy"},
				{null , null},
				//TODO --- these cmds won't work, why?
				//{app.getMenu("RSquare") ,"RSquare"}
				//{app.getMenu("SSE") ,"SumSquaredErrors"}
		};

		return statMap2;
	}


	/**
	 * Evaluates all statistics for the given GeoList of data. If the list is
	 * null the cells are set to empty.
	 * 
	 * @param dataList
	 */
	public void evaluateStatTable(GeoList dataList){

		NumberFormat nf = statDialog.getNumberFormat();

		String geoLabel = dataList.getLabel();
		String expr;
		double value;
		for(int row=0; row < statMap.length; row++){
			for(int column=0; column < 1; column++){
				if(statMap[row][1] != null){
					expr = statMap[row][1] + "[" + geoLabel + "]";
					value = evaluateExpression(expr);
					model.setValueAt(nf.format(value), row, 0);
				}
			}
		}
	}


	private double evaluateExpression(String expr){

		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	

		return nv.getDouble();
	}




	public void updateFonts(Font font) {

		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;

		statTable.setFont(font);  
		rowHeader.setFont(font);
		int h = statTable.getCellRenderer(0,0).getTableCellRendererComponent(statTable, "X",
				false, false, 0, 0).getPreferredSize().height; 
		statTable.setRowHeight(h);
		rowHeader.setFixedCellHeight(h);

		//preferredColumnWidth = (int) (MyTable.TABLE_CELL_WIDTH * multiplier);
		//columnHeader.setPreferredSize(new Dimension(preferredColumnWidth, (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));

	}


	public void setLabels(){
		setStatMap();
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
			setFont(app.getPlainFont());
			setText((String) value);
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
			for(int i=0; i<statMap.length; i++){
				model.addElement(statMap[i][0]);
			}
			setModel(model);
			setCellRenderer(new RowHeaderRenderer(table));
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

				setFont(app.getPlainFont());
				setText((String) value);

				return this;
			}

		}


	}

}
