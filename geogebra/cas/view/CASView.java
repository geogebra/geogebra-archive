package geogebra.cas.view;

import geogebra.Application;
import geogebra.cas.GeoGebraCAS;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.Kernel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

/**
 * A class which will give the view of the CAS
 */

/**
 * @author Quan Yuan
 * 
 */
public class CASView extends JComponent {

	private Kernel kernel;

	private CASTable consoleTable;

	public JList rowHeader;

	public JTable rowHeaderTable;

	public static final int ROW_HEADER_WIDTH = 30;

	private Application app;

	private GeoGebraCAS cas;

	private CASSession session;

	private final int numOfRows = 1;

	public CASView(Application app) {
		kernel = app.getKernel();
		this.app = app;
		cas = new GeoGebraCAS();
		cas.evaluateYACAS("4");
		session = new CASSession();

		createCASTable();
		createRowHeader();

		// init the scroll panel
		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		// put the table and the row header list into a scroll plane

		// scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setRowHeaderView(rowHeaderTable);
		scrollPane.setViewportView(consoleTable);

		// put the scrollpanel in the component
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);

		this.setBackground(Color.WHITE);
	}

	private void createRowHeader() {

		// Option 1 ---- Implement rowheader with JTable

		rowHeaderTable = new JTable((CASTableModel) consoleTable.getModel());

		rowHeaderTable.setEnabled(false);

		rowHeaderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		rowHeaderTable.addMouseListener(new RowHeaderMouseListener());

		rowHeaderTable.getColumnModel().getColumn(0).setPreferredWidth(
				ROW_HEADER_WIDTH);

		rowHeaderTable.setRowHeight(CASPara.originalHeight);

		rowHeaderTable.setDefaultRenderer(rowHeaderTable.getColumnClass(0),
				new RowHeaderTableRenderer(consoleTable));

		rowHeaderTable.setPreferredScrollableViewportSize(new Dimension(
				rowHeaderTable.getColumnModel().getColumn(0)
						.getPreferredWidth(), 0));

		// Option 2 ---- Implement rowheader with JList
		/*
		 * ------------- CASListModel listModel = new
		 * CASListModel((CASTableModel) consoleTable .getModel()); rowHeader =
		 * new JList(listModel); rowHeader.setFocusable(true);
		 * rowHeader.setAutoscrolls(false); rowHeader.addMouseListener(new
		 * RowHeaderMouseListener()); // rowHeader.addMouseMotionListener(new
		 * MouseMotionListener1()); // rowHeader.addKeyListener(new
		 * KeyListener1()); rowHeader.setFixedCellWidth(ROW_HEADER_WIDTH);
		 * rowHeader.setFixedCellHeight(consoleTable.getRowHeight()); // + //
		 * rowHeader.setFixedCellHeight(-1); // table.getRowMargin(); rowHeader
		 * .setCellRenderer(new RowHeaderRenderer(consoleTable, rowHeader)); //
		 * table.setView(this); -------------
		 */
	}

	private void createCASTable() {
		consoleTable = new CASTable(app);
		consoleTable.initializeTable(numOfRows, session, app);

		// Set the property of the value column;
		consoleTable.getColumnModel().getColumn(CASPara.contCol)
				.setCellRenderer(new CASTableCellRender(this, consoleTable));
		consoleTable.getColumnModel().getColumn(CASPara.contCol).setCellEditor(
				new CASTableCellEditor(this, consoleTable));
		consoleTable.getColumnModel().getColumn(CASPara.contCol)
				.setHeaderValue("");

		// CAScontroller
		CASKeyController casKeyCtrl = new CASKeyController(this, session,
				consoleTable);
		CASMouseController casMouseCtrl = new CASMouseController(this, session,
				consoleTable);
		consoleTable.addKeyListener(casKeyCtrl);
		consoleTable.addMouseListener(casMouseCtrl);
	}

	public static class CASListModel extends AbstractListModel {

		private static final long serialVersionUID = 1L;

		protected CASTableModel model;

		public CASListModel(CASTableModel model0) {
			model = model0;
		}

		public int getSize() {
			return model.getRowCount();
		}

		public Object getElementAt(int index) {
			return "" + (index + 1);
		}

	}

	protected int minSelectionRow = -1;
	protected int maxSelectionRow = -1;

//	This part is to implement rowheader with JList
//	public class RowHeaderRenderer extends JLabel implements ListCellRenderer,
//			ListSelectionListener {
//
//		private static final long serialVersionUID = 1L;
//
//		protected JTableHeader header;
//		protected JList rowHeader;
//		protected ListSelectionModel selectionModel;
//		private Color defaultBackground;
//
//		public RowHeaderRenderer(CASTable table, JList rowHeader) {
//			super("", JLabel.CENTER);
//			setOpaque(true);
//			defaultBackground = getBackground();
//
//			this.rowHeader = rowHeader;
//			header = table.getTableHeader();
//			// setOpaque(true);
//			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
//			// setHorizontalAlignment(CENTER) ;
//			// setForeground(header.getForeground()) ;
//			// setBackground(header.getBackground());
//			if (getFont().getSize() == 0) {
//				Font font1 = app.getPlainFont();
//				if (font1 == null || font1.getSize() == 0) {
//					font1 = new Font("dialog", 0, 12);
//				}
//				setFont(font1);
//			}
//			// TODO: add a Listener
//			table.getSelectionModel().addListSelectionListener(this);
//		}
//
//		public Component getListCellRendererComponent(JList list, Object value,
//				int index, boolean isSelected, boolean cellHasFocus) {
//			setText((value == null) ? "" : value.toString());
//			if (minSelectionRow != -1 && maxSelectionRow != -1) {
//				if (index >= minSelectionRow && index <= maxSelectionRow
//						&& selectionModel.isSelectedIndex(index)) {
//					setBackground(CASTable.SELECTED_BACKGROUND_COLOR_HEADER);
//				} else {
//					setBackground(defaultBackground);
//				}
//			} else {
//				setBackground(defaultBackground);
//			}
//			return this;
//		}
//
//		public void valueChanged(ListSelectionEvent e) {
//			selectionModel = (ListSelectionModel) e.getSource();
//			minSelectionRow = selectionModel.getMinSelectionIndex();
//			maxSelectionRow = selectionModel.getMaxSelectionIndex();
//			rowHeader.repaint();
//		}
//
//	}

	protected int row0 = -1;

	// Listener for RowHeader
	protected class RowHeaderMouseListener implements MouseListener {

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			boolean shiftPressed = e.isShiftDown();
			boolean metaDown = Application.isControlDown(e);
			boolean rightClick = Application.isRightClick(e);

			int x = e.getX();
			int y = e.getY();

			// left click
			if (!rightClick) {
				Point point = consoleTable.getIndexFromPixel(x, y);
				if (point != null) {
					if (consoleTable.getSelectionModel().getSelectionMode() != ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
							|| consoleTable.getColumnSelectionAllowed() == true) {
						consoleTable
								.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
						consoleTable.setColumnSelectionAllowed(false);
						consoleTable.setRowSelectionAllowed(true);
					}
					if (shiftPressed) {
						if (row0 != -1) {
							int row = (int) point.getY();
							consoleTable.setRowSelectionInterval(row0, row);
						}
					} else if (metaDown) {
						row0 = (int) point.getY();
						consoleTable.addRowSelectionInterval(row0, row0);
					} else {
						row0 = (int) point.getY();
						consoleTable.setRowSelectionInterval(row0, row0);
					}
					// consoleTable.repaint();
				}

				System.out.println("Selected number of rows is: "
						+ consoleTable.getSelectedRowCount());
				for (int i = 0; i < consoleTable.getSelectedRowCount(); i++) {
					System.out.println(consoleTable.getSelectedRows()[i]);
				}

			}
			// RIGHT CLICK
			else {
				if (!app.letShowPopupMenu())
					return;

				if (minSelectionRow != -1 && maxSelectionRow != -1)
				{
					CASContextMenuRow popupMenu = new CASContextMenuRow(
							consoleTable, 0, minSelectionRow, consoleTable
									.getModel().getColumnCount() - 1,
							maxSelectionRow, new boolean[0]);
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}

			}
		}

		public void mouseReleased(MouseEvent e) {

		}

	}

	public class RowHeaderTableRenderer extends JLabel implements
			TableCellRenderer, ListSelectionListener

	{

		JTable reftable;
		protected JTableHeader header;
		protected ListSelectionModel selectionModel;
		
		public RowHeaderTableRenderer(JTable reftable)

		{

			this.reftable = reftable;
			header = reftable.getTableHeader() ;
			
			reftable.getSelectionModel().addListSelectionListener(this);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int col)

		{

			CASTableModel casTableModel = (CASTableModel) table.getModel();

			CASTableCellValue cellValue = (CASTableCellValue) casTableModel
					.getValueAt(row, col);

			if (cellValue.isOutputVisible())
				table.setRowHeight(row, CASPara.inputOutputHeight);
			else
				table.setRowHeight(row, CASPara.originalHeight);

			JTableHeader header = reftable.getTableHeader();

			this.setOpaque(true);

			/* Set the border of TableHeader */

			setBorder(UIManager.getBorder("TableHeader.cellBorder"));

			setHorizontalAlignment(CENTER);

			setBackground(header.getBackground());

			Font font = header.getFont();

			if (isSelect(row)) /* if seleted, set the color of row header */

			{

				setForeground(Color.white);

				setBackground(Color.LIGHT_GRAY);

				setFont(font);

				setText(String.valueOf(row + 1));

			}

			else

			{

				setForeground(header.getForeground());

				setFont(font);

				setText(String.valueOf(row + 1));

			}

			return this;

		}

		private boolean isSelect(int row)

		{

			int[] sel = reftable.getSelectedRows();

			for (int i = 0; i < sel.length; i++)

			{

				if (sel[i] == row)
					return true;

			}

			return false;

		}

		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
			selectionModel = (ListSelectionModel) e.getSource();
			minSelectionRow = selectionModel.getMinSelectionIndex();
			maxSelectionRow = selectionModel.getMaxSelectionIndex();
		}

	}

	public GeoGebraCAS getCAS() {
		return cas;
	}

	public CASTable getConsoleTable() {
		return consoleTable;
	}

	/**
	 * returns settings in XML format
	 */
	public String getGUIXML() {
		StringBuffer sb = new StringBuffer();
		sb.append("<casView>\n");

		int width = getWidth(); // getPreferredSize().width;
		int height = getHeight(); // getPreferredSize().height;

		// if (width > MIN_WIDTH && height > MIN_HEIGHT)
		{
			sb.append("\t<size ");
			sb.append(" width=\"");
			sb.append(width);
			sb.append("\"");
			sb.append(" height=\"");
			sb.append(height);
			sb.append("\"");
			sb.append("/>\n");
		}

		sb.append("</casView>\n");
		return sb.toString();
	}

	public String getSessionXML() {

		CASTableModel tableModel = (CASTableModel) consoleTable.getModel();

		StringBuffer sb = new StringBuffer();
		sb.append("<casSession>\n");

		// get the number of pairs in the view
		int numOfRows = tableModel.getRowCount();

		// get the content of each pair in the table with a loop
		// append the content to the string sb
		for (int i = 0; i < numOfRows; ++i) {
			CASTableCellValue temp = (CASTableCellValue) tableModel.getValueAt(
					i, CASPara.contCol);
			sb.append(temp.getXML());
		}

		sb.append("</casSession>\n");
		return sb.toString();
	}
}