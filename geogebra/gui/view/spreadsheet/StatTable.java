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
	private GeoList statList;
	
	// layout
	private static final Color TABLE_GRID_COLOR = StatDialog.TABLE_GRID_COLOR ;
	private static final Color TABLE_HEADER_COLOR = StatDialog.TABLE_HEADER_COLOR;  
	
	
	
	/*************************************************
	 * Construct the panel
	 */
	public StatTable(Application app, GeoList statList, int mode){
		
		this.app = app;	
		this.kernel = app.getKernel();				
		this.statList = statList;
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
		
	}
	
	
	private class Corner extends JPanel {

		protected void paintComponent(Graphics g) {
			//g.setColor(bgColor);
			g.setColor(TABLE_HEADER_COLOR);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	
	
	
	
	private void populateStatTable(){
		
		if(statList == null){
			statTable.setModel(new DefaultTableModel(1, 1));
			return;
		}
		
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
	
	public void updateData(GeoList statList){
		
		
		//this.dataList = dataList;
		//statDialog.getStatGeo().createStatList(dataList,mode);
		
		this.statList = statList;
		populateStatTable();
		rowHeader.populateRowModel();
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
			populateRowModel();
			setModel(model);
			setCellRenderer(new RowHeaderRenderer(table));

			//	setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			//	setSelectionModel( table.getSelectionModel());
		}

		public void populateRowModel(){
			
			model.removeAllElements();
			if(statList != null){
				GeoList list;
				for (int elem = 0; elem < statList.size(); ++elem){
					list = (GeoList)statList.get(elem);	
					model.addElement(list.get(0));
				}
			}
			//setModel(model);
			
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
