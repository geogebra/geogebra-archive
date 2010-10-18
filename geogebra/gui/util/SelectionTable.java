package geogebra.gui.util;



import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.main.Application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
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
	public static final int MODE_COLOR_SWATCH_TEXT = 5;
	public static final int MODE_POINTSTYLE = 6;
	public static final int MODE_LINESTYLE = 7;
	public static final int MODE_SLIDER = 8;
	public static final int MODE_ICON = 9;
	public static final int MODE_ICON_FILE = 10;
	
	

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
		this.setTableHeader(null);
		this.setBorder(null);
		
		// set cell selection properties
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
	
		// set cell dimensions
		int padding = 4;
		if(mode == MODE_COLOR_SWATCH || mode == MODE_COLOR_SWATCH_TEXT)
			padding = 1;
		
		rowHeight = iconSize.height + padding;
		columnWidth = iconSize.width + padding;
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
	
	

	public ImageIcon getDataIcon(Object value){
		
		ImageIcon icon = null;
		
		switch (mode){

		case MODE_IMAGE:
			icon = GeoGebraIcon.createFileImageIcon( app, (String)value, alpha, iconSize);
			break;

		case MODE_ICON:
			icon = (ImageIcon) value;
			break;
						
		case MODE_ICON_FILE:
			icon = app.getImageIcon((String)value);
			break;
			
		case MODE_LINESTYLE:
			icon = GeoGebraIcon.createLineStyleIcon( (Integer)value,  2,  iconSize,  Color.BLACK,  null);
			break;

		case MODE_COLOR_SWATCH:
		case MODE_COLOR_SWATCH_TEXT:
			alpha = getSliderValue()/100.0f;
			fgColor = (Color)value;
			icon = GeoGebraIcon.createColorSwatchIcon( alpha,  iconSize, fgColor , null);
			break;

		case MODE_POINTSTYLE:
			icon = GeoGebraIcon.createPointStyleIcon( (Integer)value,  4,  iconSize,  Color.BLACK,  null);
			break;
			
		}
			
		return icon;
	}
	
	
	 //==============================================
	 //    Cell Renderer
	 //==============================================

	
	class MyCellRenderer extends JLabel implements TableCellRenderer {

		private Border normalBorder, selectedBorder, rollOverBorder;
		private Color selectionColor, rollOverColor;
		
		public MyCellRenderer() {
			
			//TODO --- selection color should be centralized, not from spreadsheet
			
			selectionColor =  MyTable.SELECTED_BACKGROUND_COLOR ;
			rollOverColor =  Color.LIGHT_GRAY;
			if(mode == SelectionTable.MODE_COLOR_SWATCH || mode == SelectionTable.MODE_COLOR_SWATCH_TEXT){
				selectionColor = this.getBackground();
				rollOverColor = this.getBackground();
			}
			
			
			normalBorder = BorderFactory.createEmptyBorder();
			selectedBorder = BorderFactory.createEmptyBorder();
			selectedBorder = BorderFactory.createEmptyBorder();
			if(mode == SelectionTable.MODE_COLOR_SWATCH || mode == SelectionTable.MODE_COLOR_SWATCH_TEXT || mode == SelectionTable.MODE_POINTSTYLE){
				normalBorder = BorderFactory.createLineBorder(Color.GRAY);
				selectedBorder = BorderFactory.createLineBorder(Color.BLACK, 3);			
				selectedBorder = BorderFactory.createLineBorder(Color.GRAY, 3);
			}
			
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
			
			setFont(app.getPlainFont());
		}
		
		
		public Component getTableCellRendererComponent(JTable table, Object value,
														boolean isSelected,boolean hasFocus, int row,int column) 
	    {
			
			
			setAlignmentX(CENTER_ALIGNMENT);
			setAlignmentY(CENTER_ALIGNMENT);

			if(mode == MODE_TEXT){
				this.setHorizontalAlignment(SwingConstants.LEFT);
				this.setVerticalAlignment(SwingConstants.CENTER);
				setText((String)value);
				if(isSelected){
					setIcon(GeoGebraIcon.createPointStyleIcon( 0,  2,  new Dimension(8,8),  Color.BLACK,  null));
				}else{
					setIcon(GeoGebraIcon.createEmptyIcon(8,8));
				}
							
			}else{		
				setText("");
				if(value == null){				
					setIcon(null);
				}else{
					setIcon(getDataIcon(value));
				}
			}
			
			
			if (isSelected) {
				setBackground(selectionColor);
				setBorder(selectedBorder);
			} 
			else if(row == rollOverRow && column == rollOverColumn) {
				setBackground(rollOverColor);
				setBorder(rollOverBorder);
			}
			else{
				setBackground(table.getBackground());
				setBorder(normalBorder);
			}
						
	        return this;
	    }
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);	
		Graphics2D g2 = (Graphics2D)graphics;

		g2.setStroke(new BasicStroke(1));
		
		Rectangle r = this.getCellRect(this.getSelectedRow(), this.getSelectedColumn(), true);
		//r. grow(1, 1);
		g2.setPaint(Color.BLACK);
		//g2.draw(r);
		
		r = this.getCellRect(this.rollOverRow, this.rollOverColumn, true);
		//r.grow(1, 1);
		g2.setPaint(Color.GRAY);
		//g2.draw(r);
		
	}
	
	
}
