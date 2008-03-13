package geogebra.cas.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

public class CASTableCell extends JPanel {
	// implements FocusListener {

	// Components in a cell
	private CASInputPanel inputPanel;

	private CASOutputPanel outputPanel;

	private CASLinePanel linePanel;

	private JTable consoleTable;

	private boolean lineVisiable;

	private boolean outputFieldVisiable;

	public CASTableCell(CASView view, JTable consoleTable) {
		// TODO: remove
		// System.out.println("created new CASTableCell");

		inputPanel = new CASInputPanel();
		outputPanel = new CASOutputPanel();
		linePanel = new CASLinePanel();

		this.consoleTable = consoleTable;
		lineVisiable = false;
		outputFieldVisiable = false;

		this.setInput("");
		this.setOutput("");
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.add(inputPanel);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBackground(Color.white);
		// addFocusListener(this);
		return;
	}

	
	
	public void removeOutputPanel() {
		if (outputFieldVisiable) {
			this.remove(outputPanel);
			outputFieldVisiable = false;
			this.validate();
		}
	}

	public void addOutputPanel() {
		if (!outputFieldVisiable) {
			//System.out.println("Add Output panel");
			this.add(outputPanel);
			outputFieldVisiable = true;
			this.validate();
		}
	}

	public int removeLinePanel() {
		this.remove(linePanel);
		
		// TODO: check this
		//this.validate();
		SwingUtilities.updateComponentTreeUI(this);	

		int cellHeight = 0;
		Component[] temp = this.getComponents();
		switch (temp.length) {
		case 1:
			cellHeight = CASPara.originalHeight;
			break;
		case 2:
			cellHeight = CASPara.inputOutputHeight;
			break;
		}

		lineVisiable = false;
		return cellHeight;
	}

	public int addLinePanel() {
		int cellHeight = 0;
		Component[] temp = this.getComponents();
		switch (temp.length) {
		case 1:
			cellHeight = CASPara.inputLineHeight;
			break;
		case 2:
			cellHeight = CASPara.threeCompHeight;
			break;

		}
		this.add(linePanel);
		this.validate();
		lineVisiable = true;
		return cellHeight;
	}

	public void setInput(String inValue) {
		this.inputPanel.setInput(inValue);
	}

	public void setOutput(String inValue) {
		this.outputPanel.setOutput(inValue);
		if (inValue == "")
			this.removeOutputPanel();
		else
			this.addOutputPanel();
	}

	/*
	 * Function: set the line unhighlighted, and return a proper cell height
	 */
	public int setLineInvisiable() {
		lineVisiable = false;
		return this.removeLinePanel();
	}

	public boolean isLineVisiable() {
		return lineVisiable;
	}

	public String getInput() {
		return inputPanel.getInput();
	}

	public String getOutput() {
		return outputPanel.getOutput();
	}

	public JTable getConsoleTable() {
		return consoleTable;
	}

	public boolean isOutputPanelAdded() {
		return outputFieldVisiable;
	}

	public void setInputAreaFocused() {
		inputPanel.setInputAreaFocused();
		(inputPanel.getInputArea()).setCaretPosition(inputPanel.getInput()
				.length());
	}

	public void setLineBorderFocus() {
		linePanel.requestFocus();
	}

	// public void focusGained(FocusEvent arg0) {
	// // TODO: remove
	// System.out.println("focus gained " + this);
	//		
	// setInputAreaFocus();
	// }
	//
	// public void focusLost(FocusEvent arg0) {
	// // TODO Auto-generated method stub
	// // TODO: remove
	// System.out.println("focus lost " + this);
	// }

	public CASLinePanel getLinePanel() {
		return linePanel;
	}

	public JTextField getInputArea() {
		return inputPanel.getInputArea();
	}

	public CASTableModel getTableModel() {
		return (CASTableModel) consoleTable.getModel();
	}



	public void setOutputFieldVisiable(boolean outputFieldVisiable) {
		this.outputFieldVisiable = outputFieldVisiable;
	}
}
