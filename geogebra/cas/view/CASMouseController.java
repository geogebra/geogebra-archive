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
		final MouseEvent me = e;// �¼�Դ

		this.flag = false;// ÿ�ε������ʼ��˫���¼�ִ�б�־Ϊfalse

		if (this.clickNum == 1) {// ��clickNum==1ʱִ��˫���¼�
			this.mouseDoubleClicked(me);// ִ��˫���¼�
			this.clickNum = 0;// ��ʼ��˫���¼�ִ�б�־Ϊ0
			this.flag = true;// ˫���¼���ִ��,�¼���־Ϊtrue
			return;
		}

		// ���嶨ʱ��
		java.util.Timer timer = new java.util.Timer();

		// ��ʱ����ʼִ��,��ʱ0.2���ȷ���Ƿ�ִ�е����¼�
		timer.schedule(new java.util.TimerTask() {
			private int n = 0;// ��¼��ʱ��ִ�д���

			public void run() {
				if (flag) {// ���˫���¼��Ѿ�ִ��,��ôֱ��ȡ������ִ��
					n = 0;
					clickNum = 0;
					this.cancel();
					return;
				}
				if (n == 1) {// ��ʱ���ȴ�0.2���,˫���¼���δ����,ִ�е����¼�
					mouseSingleClicked(me);// ִ�е����¼�
					flag = true;
					clickNum = 0;
					n = 0;
					this.cancel();
					return;
				}
				clickNum++;
				n++;
			}
		}, new java.util.Date(), 200); // �����ӳ�ʱ��

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
