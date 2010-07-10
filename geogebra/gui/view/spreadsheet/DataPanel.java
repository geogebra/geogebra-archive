package geogebra.gui.view.spreadsheet;


import geogebra.kernel.Construction;
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
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class DataPanel extends JPanel implements ActionListener  {
	

	private Application app;
	private Kernel kernel; 
	
	private JTable dataTable;
	private JButton btnEnableAll;
	private MyRowHeader rowHeader;
	private MyColumnHeaderRenderer columnHeader;
	
	private GeoList dataListAll, dataListSelected; 
	OneVariableStatsDialog statDialog;
	public int preferredColumnWidth = MyTable.TABLE_CELL_WIDTH; 
	
	
	
	/*************************************************
	 * Construct a DataPanel
	 */
	public DataPanel(Application app, OneVariableStatsDialog statDialog, GeoList dataAll, GeoList dataSelected){

		this.app = app;	
		kernel = app.getKernel();
		this.dataListAll = dataAll;
		this.dataListSelected = dataSelected;
		this.statDialog = statDialog;
		
		// build the data table	
		dataTable = new JTable(){
			// disable cell edits (for now)
			@Override
			public boolean isCellEditable(int rowIndex, int vColIndex) { 
				return false; 
				}  
			
		      @Override
		  	protected void configureEnclosingScrollPane() {
		  		super.configureEnclosingScrollPane();
		  		Container p = getParent();
		  		if (p instanceof JViewport) {
		  			((JViewport) p).setBackground(getBackground());
		  		}
		      }
		};
		populateDataTable(dataAll);
	
		
				
		// set table and column renderers
		dataTable.setDefaultRenderer(Object.class, new MyCellRenderer());
		columnHeader = new MyColumnHeaderRenderer();
		columnHeader.setPreferredSize(new Dimension(preferredColumnWidth, MyTable.TABLE_CELL_HEIGHT));
		for (int i = 0; i < dataTable.getColumnCount(); ++ i) {
			dataTable.getColumnModel().getColumn(i).setHeaderRenderer(columnHeader);
			dataTable.getColumnModel().getColumn(i).setPreferredWidth(preferredColumnWidth);
		}
		
		
		// enable row selection 
		dataTable.setColumnSelectionAllowed(false); 
		dataTable.setRowSelectionAllowed(true);
		
	
		//dataTable.setAutoResizeMode(JTable.);
		dataTable.setPreferredScrollableViewportSize(dataTable.getPreferredSize());
		dataTable.setMinimumSize(new Dimension(100,50));
		//dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		dataTable.setAutoCreateColumnsFromModel(false);
		dataTable.setGridColor(MyTable.TABLE_GRID_COLOR);
		
		
		// create a scrollPane for the table
		JScrollPane dataScroller = new JScrollPane(dataTable);
		dataScroller.setBorder(BorderFactory.createEmptyBorder());
		
		
		// create row header
		rowHeader = new MyRowHeader(this,dataTable);	
		
		dataScroller.setRowHeaderView(rowHeader);
		
		
		// create enableAll button and put it in the upper left corner
		btnEnableAll = new JButton(app.getImageIcon("shown.gif"));
		btnEnableAll.addActionListener(this);
		btnEnableAll.setBackground(MyTable.BACKGROUND_COLOR_HEADER);	
		Corner upperLeftCorner = new Corner(); 
		upperLeftCorner.setLayout(new BorderLayout());
		upperLeftCorner.add(btnEnableAll, BorderLayout.CENTER);
		upperLeftCorner.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MyTable.TABLE_GRID_COLOR));		
	
		
		// set the other corners
		dataScroller.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, upperLeftCorner);
		dataScroller.setCorner(ScrollPaneConstants.LOWER_LEFT_CORNER, new Corner());
		dataScroller.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, new Corner());
			
			
		
		// finally, load up our JPanel
		this.setLayout(new BorderLayout());
		this.add(dataScroller, BorderLayout.CENTER);
		
	
	}  // END constructor 

	
	private void populateDataTable(GeoList dataList){
		TableModel dataModel = new DefaultTableModel(dataList.size(),1);
		for (int row = 0; row < dataList.size(); ++row){
			//dataModel.setValueAt(new Boolean(true),row,0);
			dataModel.setValueAt(dataList.get(row).toDefinedValueString(),row,0);
		}
		dataTable.setModel(dataModel);
		//dataModel.addTableModelListener(this);

		dataTable.getColumnModel().getColumn(0).setHeaderValue("data");
		
	}


	public void ensureTableFill(){
		Container p = getParent();
		DefaultTableModel dataModel = (DefaultTableModel) dataTable.getModel();
		if (dataTable.getHeight() < p.getHeight()) {
			int newRows = (p.getHeight() - dataTable.getHeight()) / dataTable.getRowHeight();			
			dataModel.setRowCount(dataTable.getRowCount() + newRows);
			for(int i = 0; i <= dataTable.getRowCount(); ++i){
				if(rowHeader.getModel().getElementAt(i) != null)
				((DefaultListModel)rowHeader.getModel()).add(i, true);
			}
		}		
		
	}


	public void updateDataSelection() {
		
		Construction cons = kernel.getConstruction();
	
		dataListSelected.clear();
		DefaultListModel model = (DefaultListModel) rowHeader.getModel();
		for(int i=0; i < dataListAll.size(); ++i){
			if(((Boolean)model.getElementAt(i))== true){
				dataListSelected.add(dataListAll.get(i).copyInternal(cons));
			}
		}
		
		statDialog.updateDataList();	
	}
	
	
	
	private class Corner extends JPanel {

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(MyTable.BACKGROUND_COLOR_HEADER);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}



	public void updateFonts(Font font) {

		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;

		setFont(font);
		dataTable.setFont(font);  
		
		dataTable.setRowHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		rowHeader.setFixedCellHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		
		preferredColumnWidth = (int) (MyTable.TABLE_CELL_WIDTH * multiplier);
		columnHeader.setPreferredSize(new Dimension(preferredColumnWidth, (int)(MyTable.TABLE_CELL_HEIGHT * multiplier)));
		
	}
	
	public MyRowHeader getRowHeader(){
		return rowHeader;
	}
	
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnEnableAll){
			rowHeader.enableAll(); 
			updateDataSelection();
		}
	}

	
	
	
	
	//=================================================
	//      Column Header Renderer
	//=================================================
	
	
	protected class MyColumnHeaderRenderer extends JLabel implements TableCellRenderer  
	{
		private Color defaultBackground;

		public MyColumnHeaderRenderer() {    		
			super("", SwingConstants.CENTER);
			setOpaque(true);
			defaultBackground = MyTable.BACKGROUND_COLOR_HEADER;
			setBackground(defaultBackground);
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MyTable.TABLE_GRID_COLOR));
			Font font1 = getFont(); 
			if (font1 == null || font1.getSize() == 0) {
				kernel.getApplication().getPlainFont();
				if (font1 == null || font1.getSize() == 0) {
					font1 = new Font("dialog", 0, 12);
				}
			}
			setFont(font1);		
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int colIndex) {
			
			setText(value.toString());

			return this;        			
		}

	}
	
	

	//======================================================
	//         Table Cell Renderer 
	//======================================================
	
	class MyCellRenderer extends DefaultTableCellRenderer {

		public MyCellRenderer(){
			setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));	
		}


		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) 
		{	
			if (value == null) {		
				setText("");
				return this;
			}
			
			String text = value.toString();
			
			if (isSelected) 
				setBackground(MyTable.SELECTED_BACKGROUND_COLOR_HEADER);
			else 								
				setBackground(table.getBackground());

			setText(text);


			//setFont(app.getFontCanDisplay(text, Font.PLAIN));

			return this;
		}

	}

	//======================================================
	//         Row Header 
	//======================================================
	

	public class MyRowHeader extends JList implements MouseListener{
		
		DefaultListModel model;
		JTable table;
		DataPanel dataPanel;

		public MyRowHeader(DataPanel dataPanel, JTable table){
			super();
			this.table = table;
			this.dataPanel = dataPanel;
			model = new DefaultListModel();
			for(int i=0; i< table.getRowCount(); ++i){
				model.addElement(new Boolean(true));
			}
			setModel(model);

			setCellRenderer(new RowHeaderRenderer(table));
			addListSelectionListener(new MySelectionListener());
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			setSelectionModel( table.getSelectionModel());
			this.addMouseListener(this);
			
		}


		class RowHeaderRenderer extends JLabel implements ListCellRenderer {

			private ImageIcon iconShown, iconHidden;

			RowHeaderRenderer(JTable table) {    
				setOpaque(true);
				setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MyTable.TABLE_GRID_COLOR));
				setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, MyTable.TABLE_GRID_COLOR), 
						BorderFactory.createEmptyBorder(0, 0, 0, 5)));

				setHorizontalAlignment(LEFT);
				setFont(table.getTableHeader().getFont());
				
				iconShown = app.getImageIcon("shown.gif");
				iconHidden = app.getImageIcon("hidden.gif");
			}

			public Component getListCellRendererComponent( JList list, 
					Object value, int index, boolean isSelected, boolean cellHasFocus) {

				setText("" + (index+1));
				
				// add/remove icons
				if((Boolean) value){
					setIcon(iconShown);
				} else {
					setIcon(iconHidden);
				}

				// selection 
				if (isSelected){ 
					setBackground(MyTable.SELECTED_BACKGROUND_COLOR_HEADER);
				}else {								
					setBackground(MyTable.BACKGROUND_COLOR_HEADER);
				}
				
				return this;
			}

		}

		class MySelectionListener implements ListSelectionListener{
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					if (rowHeader.getSelectedIndex() != -1) {
						;
					}
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			// check if we clicked on the 16x16 show/hide icon		
			int index = this.locationToIndex(e.getPoint());
			Rectangle rect = getCellBounds(index, index);		
			boolean iconClicked = rect != null && e.getX() - rect.x < 16; // distance from left border				
				if (iconClicked) {
					// icon clicked: toggle show/hide
					model.setElementAt(!((Boolean)getSelectedValue()), getSelectedIndex());
					dataPanel.updateDataSelection();
					return;
				}		
		}

		public void mouseEntered(MouseEvent arg0) {	
		}

		public void mouseExited(MouseEvent arg0) {		
		}

		public void mousePressed(MouseEvent arg0) {	
		}

		public void mouseReleased(MouseEvent arg0) {	
		}


		public void enableAll(){
			model.clear();
			for(int i=0; i< table.getRowCount(); ++i){
				model.addElement(new Boolean(true));
			}
		}

	}



	


}
