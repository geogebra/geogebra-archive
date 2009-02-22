package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

public abstract class CASTableCell extends JPanel {

	private CASInputPanel inputPanel;
	private CASOutputPanel outputPanel;
	private CASLinePanel linePanel;
	private CASTable consoleTable;
	protected Application app;

	public CASTableCell(CASView view) {
		this.app = view.getApp();
		this.consoleTable = view.getConsoleTable();
		
		inputPanel = new CASInputPanel();
		outputPanel = new CASOutputPanel();
		linePanel = new CASLinePanel();
		setInput("");
		setOutput("", false);			

		setLayout(new BorderLayout(5, 5));
		setBackground(Color.white);
		add(inputPanel, BorderLayout.NORTH);
		add(outputPanel, BorderLayout.CENTER);
		add(linePanel, BorderLayout.SOUTH);	
		return;
	}

	public void setInput(String inValue) {
		inputPanel.setInput(inValue);
	}

	public void setOutput(String inValue, boolean isError) {
		outputPanel.setOutput(inValue, isError);
		boolean showOutputPanel = (inValue == null || inValue.length() == 0);
		// TODO: check
		//outputPanel.setVisible(showOutputPanel);
		
		//		if (showOutputPanel)
		//			this.removeOutputPanel();
		//		else
		//			this.addOutputPanel();
	}
	
	void updateTableRowHeight(JTable table, int row) {
		if (isVisible()) {
			Dimension prefSize = getPreferredSize();

			if (prefSize != null) {
				setSize(prefSize);
				if (table.getRowHeight(row) != prefSize.height)
					table.setRowHeight(row, prefSize.height);
			}
		}
	}

	public String getInput() {
		return inputPanel.getInput();
	}

	public String getOutput() {
		return outputPanel.getOutput();
	}

	public void setInputAreaFocused() {
		inputPanel.setInputAreaFocused();
	}	

	public CASLinePanel getLinePanel() {
		return linePanel;
	}
	
	public JTextComponent getInputArea() {
		return inputPanel.getInputArea();
	}

	public CASTableModel getTableModel() {
		return (CASTableModel) consoleTable.getModel();
	}

	final public void setFont(Font ft) {
		if (ft == null) return;
		
		if (inputPanel != null)
			inputPanel.setFont(ft);
		if (outputPanel != null)
			outputPanel.setFont(ft);
	}

}
