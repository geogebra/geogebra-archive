package geogebra.cas.view;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

public class RowHeader extends JList {
	
	public static final int ROW_HEADER_WIDTH = 30;
	
	public RowHeader(CASTable table) {
		setModel(new RowHeaderListModel(table));
		
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setFixedCellWidth(ROW_HEADER_WIDTH);
		setFocusable(true);
		
		// renderer
		setCellRenderer(new RowHeaderRenderer(table));
			
		// listener 
		RowHeaderListener rhl = new RowHeaderListener(table, this);						
		addMouseListener(rhl);
		addMouseMotionListener(rhl);		
		
//		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//		table.getSelectionModel().addListSelectionListener(this);
//		table.setRowSelectionAllowed(true);	
	}

}
