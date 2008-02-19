package geogebra.cas.view;

import java.awt.event.MouseEvent;

import javax.swing.JTable;

public class CASMouseController extends java.awt.event.MouseAdapter {

	private CASSession session;

	private CASView view;

	private JTable consoleTable;

	private boolean flag = false;// Judge whether there is a double-click

	private int clickNum = 0;// Record the number of clicks

	public CASMouseController(CASView view, CASSession session, JTable table) {
		this.session = session;
		this.view = view;
		this.consoleTable = table;
	}

	public void mouseClicked(MouseEvent e) {
		final MouseEvent me = e;// 事件源

		this.flag = false;// 每次点击鼠标初始化双击事件执行标志为false

		if (this.clickNum == 1) {// 当clickNum==1时执行双击事件
			this.mouseDoubleClicked(me);// 执行双击事件
			this.clickNum = 0;// 初始化双击事件执行标志为0
			this.flag = true;// 双击事件已执行,事件标志为true
			return;
		}

		// 定义定时器
		java.util.Timer timer = new java.util.Timer();

		// 定时器开始执行,延时0.2秒后确定是否执行单击事件
		timer.schedule(new java.util.TimerTask() {
			private int n = 0;// 记录定时器执行次数

			public void run() {
				if (flag) {// 如果双击事件已经执行,那么直接取消单击执行
					n = 0;
					clickNum = 0;
					this.cancel();
					return;
				}
				if (n == 1) {// 定时器等待0.2秒后,双击事件仍未发生,执行单击事件
					mouseSingleClicked(me);// 执行单击事件
					flag = true;
					clickNum = 0;
					n = 0;
					this.cancel();
					return;
				}
				clickNum++;
				n++;
			}
		}, new java.util.Date(), 200); // 设置延迟时间

		Object src = e.getSource();

		int selectedRow = consoleTable.getSelectedRow();
		int selectedCol = consoleTable.getSelectedColumn();

		System.out.println("Mouse Clicked at " + selectedRow + " "
				+ selectedCol);
	}
	

	/*
	 * Function for the singe-click
	 */
	public void mouseSingleClicked(MouseEvent e) {
		// System.out.println("Single Clicked!");
		int rowI, colI;
		rowI = consoleTable.rowAtPoint(e.getPoint());// Get the row number
		colI = consoleTable.columnAtPoint(e.getPoint());
		if (rowI <0 )
			return;
		System.out.println("single click at"
					+ rowI + "" + colI);
		if (colI == CASPara.contCol){ //Set the focus to the input textfiled
			//CASTableCellValue src = (CASTableCellValue)((CASTableModel) consoleTable.getModel()).getValueAt(rowI);
			//src
		}
	}

	/*
	 * Function for the double-click
	 */
	public void mouseDoubleClicked(MouseEvent e) {
		// System.out.println("Doublc Clicked!");
		int rowI, colI;
		rowI = consoleTable.rowAtPoint(e.getPoint());// Get the row number
		colI = consoleTable.columnAtPoint(e.getPoint());
		if (rowI > -1)
			System.out.println("double click at"
					+ rowI + "" + colI);

		if (colI == CASPara.indexCol){
			//Insert a new row
			CASTableCellValue value = new CASTableCellValue();
			((CASTableModel) consoleTable.getModel()).insertRow(
					(rowI >= 0 ? rowI : 0), new Object[]{ "New", value});
		}
	}
}
