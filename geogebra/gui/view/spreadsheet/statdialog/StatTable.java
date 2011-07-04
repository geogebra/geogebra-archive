package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;

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
	protected Application app;
	private int mode = -1;

	private JTable statTable;
	private MyRowHeader rowHeader;
	private String[] rowNames;
	
	// layout
	private static final Color TABLE_GRID_COLOR = StatDialog.TABLE_GRID_COLOR ;
	private static final Color TABLE_HEADER_COLOR = StatDialog.TABLE_HEADER_COLOR;  

	protected DefaultTableModel tableModel;
	

	/*************************************************
	 * Construct the panel
	 */
	public StatTable(Application app){

		this.app = app;

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



		// table settings
		statTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		statTable.setColumnSelectionAllowed(true); 
		statTable.setRowSelectionAllowed(true);
		statTable.setShowGrid(true); 	 
		statTable.setGridColor(TABLE_GRID_COLOR); 	 	
		statTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//statTable.setAutoCreateColumnsFromModel(false);
		//statTable.setPreferredScrollableViewportSize(statTable.getPreferredSize());

		statTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		//statTable.setPreferredSize(new Dimension(400,200));


		// set the width of the stat label column
		//autoFitColumnWidth(statTable, 0, 50);


		// enclose the table in this scrollPane	

		setViewportView(statTable);
		setBorder(BorderFactory.createEmptyBorder());


		// set the  corners
		this.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new Corner());
		this.setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, new Corner());
		this.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, new Corner());

		this.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, new Corner());
		((JPanel)this.getCorner(ScrollPaneConstants.UPPER_LEFT_CORNER)).
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,TABLE_GRID_COLOR));

		
		
		//setColumnHeaders();

	} // END constructor


	public void setStatTable(int rows, String[] rowNames, int columns, String[] columnNames){

		tableModel = new DefaultTableModel(rows,columns);
		statTable.setModel(tableModel);

		// set column names
		if(columnNames == null){
			statTable.setTableHeader(null);
			this.setColumnHeaderView(null);	
		}else{
			tableModel.setColumnCount(0);
			for(int i=0; i< columnNames.length; i++)
				tableModel.addColumn(columnNames[i]);	
		}

		// create row header
		if(rowNames != null){
			this.rowNames = rowNames;
			rowHeader = new MyRowHeader(statTable);	
			//rowHeaderModel = new DefaultListModel();
			//.setModel(rowHeaderModel);
			setRowHeaderView(rowHeader);
		}else{
			setRowHeaderView(null);
		}

		repaint();
		
	}

	public void setLabels(String[] rowNames, String[] columnNames){

		// set column names
		if(columnNames != null){
			for(int i=0; i< columnNames.length; i++)
				statTable.getColumnModel().getColumn(i).setHeaderValue(columnNames[i]);	
		}

		if(rowNames != null){
			this.rowNames = rowNames; 
			rowHeader = new MyRowHeader(statTable);		
			setRowHeaderView(rowHeader);
		}

		repaint();

	}



	public DefaultTableModel getModel(){
		return tableModel;
	}


	private class Corner extends JPanel {

		protected void paintComponent(Graphics g) {
			//g.setColor(bgColor);
			g.setColor(TABLE_HEADER_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}


	public void updateFonts(Font font) {
		setFont(font);

		if(statTable != null){
			statTable.setFont(font);  
			rowHeader.setFont(font);
			
			int h = statTable.getCellRenderer(0,0).getTableCellRendererComponent(statTable, "X",
					false, false, 0, 0).getPreferredSize().height; 
			statTable.setRowHeight(h);
			rowHeader.setFixedCellHeight(statTable.getRowHeight());
			//preferredColumnWidth = (int) (MyTable.TABLE_CELL_WIDTH * multiplier);
			//columnHeader.setPreferredSize(new Dimension(preferredColumnWidth, (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
		}
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
			setText((String) value);
			return this;
		}

	}


	//======================================================
	//         Row Header 
	//======================================================


	public class MyRowHeader extends JList  {

		JTable table;

		public MyRowHeader(JTable table){
			super(rowNames);
			this.table = table;
			setCellRenderer(new RowHeaderRenderer(table));
			setFixedCellHeight(table.getRowHeight());
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
				setFont(table.getFont());

			}

			public Component getListCellRendererComponent( JList list, 
					Object value, int index, boolean isSelected, boolean cellHasFocus) {

				setFont(table.getFont());
				setText((String) value);
				
				return this;
			}

		}


	}



}
