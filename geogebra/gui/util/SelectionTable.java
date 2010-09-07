package geogebra.gui.util;



import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 * Creates a table to display and select images and other custom icons. The
 * table takes a one dimensional array of data objects as input and then, using
 * row and column size parameters, displays the data as GeoGebra icons in a 2D
 * table. User selection is returned as an index to the data array.
 * 
 * The table is intended for use in a popup menu.
 * 
 * @author G.Sturr
 * 
 */
public class SelectionTable extends JTable{

	private Application app;
	private MyCellRenderer renderer;
	private DefaultTableModel model;
	private SelectionTable myTable;
	
	private int rollOverRow = -1;
	private int rollOverColumn = -1;
	
	
	private int sliderValue;	

	private Object[] data;
	private int numRows, numColumns, rowHeight, columnWidth;
	private Dimension iconSize;
	
	private int mode;
	private Object iconArgs[];


	public SelectionTable(Application app, Object[] data, int rows, int columns, Dimension iconSize, int mode){
		
		this.app = app;	
		this.data = data;
		this.myTable = this;
		this.mode = mode;
		this.iconSize = iconSize;
		//selectedIcon = new GeoGebraIcon();
		
		iconArgs = new Object[2];
		
		//=======================================
		// determine the dimensions of the table
		
		// rows = -1, cols = -1  ==> square table to fit data
		if(rows == -1 && columns == -1){
			rows = (int) Math.floor(Math.sqrt(data.length));
			columns = (int) Math.ceil(data.length / rows);
		}
		
		// rows = -1  ==> fixed cols, rows added to fit data
		else if(rows == -1){
			rows = (int) (Math.ceil(data.length / columns));
		}
		
		// cols = -1 ==> fixed rows, cols added to fit data
		else if(columns == -1){
			columns = (int) (Math.ceil(data.length / rows));
		}
		
		
		//=======================================
		// create the table model and load the data
		numRows = rows;
		numColumns = columns;
		model = new DefaultTableModel(rows, columns);
		
		int r=0;
		int c=0;
		for(int i=0; i < Math.min(data.length, this.numRows * this.numColumns); i++){
			model.setValueAt(data[i], r, c);
			++c;
			if(c == this.numColumns){
				c = 0;
				++r;
			}
			
		}
		
		// add the model to the table
		this.setModel(model);
		
		
		//=======================================	
		// set cell renderer
		renderer = new MyCellRenderer();
		this.setDefaultRenderer(Object.class, renderer);
		
		
		//=======================================
		// set various display properties
		this.setAutoResizeMode(AUTO_RESIZE_OFF);
		this.setAutoCreateColumnsFromModel(false);
		setShowGrid(false);
		//this.setIntercellSpacing(new Dimension(1,1));
		setGridColor(MyTable.TABLE_GRID_COLOR);
		//setBackground(Color.white);
		//setOpaque(true);
		this.setTableHeader(null);
		//this.setBorder(BorderFactory.createLineBorder(getGridColor()));
		this.setBorder(null);
		
		// set cell selection properties
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
	
		// set cell dimensions
		rowHeight = iconSize.height + 3;
		columnWidth = iconSize.width + 3;
		setRowHeight(rowHeight);	
		for (int i = 0; i < getColumnCount(); ++ i) {
			getColumnModel().getColumn(i).setPreferredWidth(columnWidth);
		}
		
		
		
		//=======================================
		// add listener for mouse roll over
		RollOverListener rollOverListener = new RollOverListener();
		addMouseMotionListener(rollOverListener);
		addMouseListener(rollOverListener);
		
	}
	


	/** Disable cell editing */
	@Override
	public boolean isCellEditable(int rowIndex, int vColIndex) { 
		return false; 
	}  


	
	 //==============================================
	 //    Listeners
	 //==============================================

	
	 private class RollOverListener extends MouseInputAdapter {
		 
	        public void mouseExited(MouseEvent e) {
	            rollOverRow = -1;
	            rollOverColumn = -1;
	            repaint();
	        }
	 
	        public void mouseMoved(MouseEvent e) {
	            int row = rowAtPoint(e.getPoint());
	            int column = columnAtPoint(e.getPoint());
	            if( row != rollOverRow || column != rollOverColumn ) {
	                rollOverRow = row;
	                rollOverColumn = column;
	                repaint();
	            }
	        }
	    }
	
	
	 //==============================================
	 //    Getters/Setters
	 //==============================================


	 public int getSelectedIndex(){
		 return this.getColumnCount() * this.getSelectedRow()  + this.getSelectedColumn();
	 }

	 public void setSelectedIndex(int index){

		 int row = (int) Math.floor(index/data.length);
		 int column = index % data.length;
		 this.changeSelection(row, column, false, false);

	 }

	 public int getSliderValue() {
		 return sliderValue;
	 }

	 public void setSliderValue(int sliderValue) {
		 this.sliderValue = sliderValue;
	 }

	
	public Object[] getData(){
		return data;
	}
	
	

	public void updateIcon(GeoGebraIcon icon, Object value){

		iconArgs[0] = value;

		if(mode == GeoGebraIcon.MODE_COLOR_SWATCH){
			iconArgs[1] = getSliderValue()/100.0f;	
		}else{
			iconArgs[1] = getSliderValue();		
		}

		icon.setImage(app, iconArgs, iconSize, mode);

	}
	
	
	 //==============================================
	 //    Cell Renderer
	 //==============================================

	
	class MyCellRenderer extends JLabel implements TableCellRenderer {

		private Border normalBorder, selectedBorder, rollOverBorder;
		private GeoGebraIcon icon;
		private Object[] iconArgs;
		
		public MyCellRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			
			normalBorder = BorderFactory.createEmptyBorder();
			//normalBorder = BorderFactory.createEtchedBorder();
			//normalBorder = BorderFactory.createLineBorder(Color.BLACK);
			selectedBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
			rollOverBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
			icon = new GeoGebraIcon();
			iconArgs = new Object[3];
		}
		
		
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected,boolean hasFocus, int row,int column) 
	    {
			
			this.setAlignmentX(CENTER_ALIGNMENT);
			this.setAlignmentY(CENTER_ALIGNMENT);
			
	    	// hide file name and draw icon from this image file name
			setText("");
			
			if(value == null){
				
				setIcon(null);
				
			}else{
				
				updateIcon(icon,value);
				setIcon(icon);

			}

	    	// set border --- should this be in prepareRenderer??
	    	setBackground(table.getBackground());
	    	if (isSelected) {
	    		setBorder(selectedBorder);
	    	} 
	    	else if(row == rollOverRow && column == rollOverColumn) {
	    		setBorder(rollOverBorder);
	    	}
	    	else{
	    		setBorder(normalBorder);
	    	}
	        
	        return this;
	    }
	}

	
}
