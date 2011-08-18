package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.main.GeoGebraColorConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BorderFactory;
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

	private JTable myTable;
	private MyRowHeader rowHeader;
	private String[] rowNames;

	// layout
	private static final Color TABLE_GRID_COLOR = StatDialog.TABLE_GRID_COLOR ;
	private static final Color TABLE_HEADER_COLOR = StatDialog.TABLE_HEADER_COLOR;  
	private static final Color SELECTED_BACKGROUND_COLOR = GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR;  

	protected DefaultTableModel tableModel;


	public StatTable(){
		
		// create and initialize the table
		initTable();

		// enclose the table in this scrollPane	
		setViewportView(myTable);
		myTable.setBorder(BorderFactory.createEmptyBorder());
		//setBorder(BorderFactory.createEmptyBorder());

		// set the  corners
		setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new Corner());
		setCorner(ScrollPaneConstants.LOWER_RIGHT_CORNER, new Corner());
		setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, new Corner());
		this.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, new Corner());
		((JPanel)this.getCorner(ScrollPaneConstants.UPPER_LEFT_CORNER)).
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,TABLE_GRID_COLOR));
		
		
        myTable.setPreferredScrollableViewportSize(myTable.getPreferredSize());

		
		
	} 

	public JTable getTable(){
		return myTable;
	}

	private void initTable(){

		// construct the stat table	
		myTable = new JTable(){
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
		myTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		myTable.setColumnSelectionAllowed(true); 
		myTable.setRowSelectionAllowed(true);
		myTable.setShowGrid(true); 	 
		myTable.setGridColor(TABLE_GRID_COLOR); 	 	
		myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		//((JLabel) statTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

		myTable.setBackground(Color.white);

	}

	private class Corner extends JPanel {
		protected void paintComponent(Graphics g) {
			g.setColor(TABLE_HEADER_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}


	
	public void setStatTable(int rows, String[] rowNames, int columns, String[] columnNames){

		//TODO: cannot remove columns ... call this again with fewer columns
		// and the older columns persist ????
		
		tableModel = new DefaultTableModel(rows,columns);
		myTable.setModel(tableModel);

		// set column names
		if(columnNames == null){
			myTable.setTableHeader(null);
			this.setColumnHeaderView(null);	
		}else{
			tableModel.setColumnCount(0);
			for(int i=0; i< columnNames.length; i++)
				tableModel.addColumn(columnNames[i]);	
		}

		// create row header
		if(rowNames != null){
			this.rowNames = rowNames;
			rowHeader = new MyRowHeader(myTable);	
			//rowHeaderModel = new DefaultListModel();
			//.setModel(rowHeaderModel);
			setRowHeaderView(rowHeader);
		}else{
			setRowHeaderView(null);
		}


		myTable.setPreferredScrollableViewportSize(myTable.getPreferredSize());		
		//statTable.setMinimumSize(statTable.getPreferredSize());


		this.revalidate();

		repaint();

	}

	public void setLabels(String[] rowNames, String[] columnNames){

		// set column names
		if(columnNames != null){
			for(int i=0; i< columnNames.length; i++)
				myTable.getColumnModel().getColumn(i).setHeaderValue(columnNames[i]);	
		}

		if(rowNames != null){
			this.rowNames = rowNames; 
			rowHeader = new MyRowHeader(myTable);		
			setRowHeaderView(rowHeader);
		}

		repaint();
	}


	public DefaultTableModel getModel(){
		return tableModel;
	}



	public void updateFonts(Font font) {
		setFont(font);
		//Application.debug("");
		if(myTable != null && myTable.getRowCount()>0){
			myTable.setFont(font); 

			int h = myTable.getCellRenderer(0,0).getTableCellRendererComponent(myTable, "X",
					false, false, 0, 0).getPreferredSize().height; 
			myTable.setRowHeight(h);

			if(rowHeader != null){
				rowHeader.setFont(font);
				rowHeader.setFixedCellHeight(myTable.getRowHeight());
			}
			
			if(myTable.getTableHeader() != null)	
				myTable.getTableHeader().setFont(font);
		}
	}



	/**
	 * Adjust the width of a column to fit the maximum preferred width of 
	 * its cell contents.
	 */
	public void autoFitColumnWidth(int column, int defaultColumnWidth){

		JTable table = myTable;
		if(table.getRowCount() <= 0)
			return;

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
			prefWidth = defaultColumnWidth- table.getIntercellSpacing().width;
		} else {
			
			prefWidth = Math.max(prefWidth, tableColumn.getMinWidth());
			//System.out.println("pref width: " + prefWidth);
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
			setFont(table.getFont());
			setText((String) value);

			if (isSelected) 
				setBackground(SELECTED_BACKGROUND_COLOR);
			else
				setBackground(Color.white);

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
