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
import javax.swing.SwingConstants;
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

	private Color fgColor, bgColor;
	private float alpha;
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}


	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
		repaint();
	}



	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}


	public static final int MODE_IMAGE = 0;
	public static final int MODE_IMAGE_FILE = 1;
	public static final int MODE_LATEX = 2;
	public static final int MODE_TEXT = 3;
	public static final int MODE_COLOR_SWATCH = 4;
	public static final int MODE_POINTSTYLE = 5;
	public static final int MODE_LINESTYLE = 6;
	public static final int MODE_SLIDER_LINE = 7;
	public static final int MODE_SLIDER_POINT = 8;
	public static final int MODE_COLOR_SWATCH_TEXT = 9;
	public static final int MODE_ICON = 10;
	
	

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
		
		populateModel(data);
		
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
		//setGridColor(MyTable.TABLE_GRID_COLOR);
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




	/** loads a one dimensional array of data into the table model*/
	public void populateModel( Object [] data){

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

		 int row = (int) Math.floor(index / getColumnCount()) ;
		 int column = index - (row * getColumnCount());
		 this.changeSelection(row, column, false, false);
		// Application.debug("=======SET SELECTED INDEX: " + index + "," + row + ", " + column );

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
		
		switch (mode){

		case MODE_IMAGE:
			icon.createFileImageIcon( app, (String)value, alpha, iconSize,  fgColor,  bgColor);
			break;

		case MODE_ICON:
			icon.setImage(app.getImageIcon((String)value).getImage());
			break;
			
			
		case MODE_LINESTYLE:
			icon.createLineStyleIcon( (Integer)value,  2,  iconSize,  Color.BLACK,  null);
			break;

		case MODE_COLOR_SWATCH:
		case MODE_COLOR_SWATCH_TEXT:
			alpha = getSliderValue()/100.0f;
			fgColor = (Color)value;
			icon.createColorSwatchIcon( alpha,  iconSize, fgColor , null);
			break;

		case MODE_POINTSTYLE:
			icon.createPointStyleIcon( (Integer)value,  4,  iconSize,  Color.BLACK,  null);
			break;
			
		}
			
	}
	
	
	 //==============================================
	 //    Cell Renderer
	 //==============================================

	
	class MyCellRenderer extends JLabel implements TableCellRenderer {

		private Border normalBorder, selectedBorder, rollOverBorder;
		private GeoGebraIcon icon;
		//TODO --- selection color should be centralized, not from spreadsheet
		// also maybe try to simulate combobox gui with checkmark for selection?
		private Color selectionColor =  MyTable.SELECTED_BACKGROUND_COLOR ;
		
		public MyCellRenderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			
			normalBorder = BorderFactory.createEmptyBorder();
			//normalBorder = BorderFactory.createEtchedBorder();
			//normalBorder = BorderFactory.createLineBorder(Color.BLACK);
			selectedBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);		
			rollOverBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 2);
			
			icon = new GeoGebraIcon();
			setFont(app.getPlainFont());
		}
		
		
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected,boolean hasFocus, int row,int column) 
	    {
			
			
			setAlignmentX(CENTER_ALIGNMENT);
			setAlignmentY(CENTER_ALIGNMENT);

			// hide file name and draw icon from this image file name
			if(mode == MODE_TEXT){
				this.setHorizontalAlignment(SwingConstants.LEFT);
				this.setVerticalAlignment(SwingConstants.CENTER);
				setIcon(null);
				setText((String)value);
				
				
				if (isSelected) {
					setBackground(selectionColor);
				} 
				else if(row == rollOverRow && column == rollOverColumn) {
					setBackground(selectionColor);
				}
				else{
					setBackground(table.getBackground());
				}
				
				
				
			}else{		
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
			}
			
	        return this;
	    }
	}

	
}
