package geogebra.cas.view;

import java.awt.Component;

import javax.swing.*;

public class CASTableCell extends JPanel {

	private JTextField inputArea;

	private JTextField outputArea;

	private CASBPanel BBorder;

	private CASTableCellController inputListener;

	private String input;

	private String output;

	private JTable consoleTable;

	private boolean lineHighlighted;

	public CASTableCell(CASView view, JTable consoleTable) {
		inputArea = new JTextField();
		outputArea = new JTextField();
		BBorder = new CASBPanel();
		inputArea.setBorder(BorderFactory.createEmptyBorder());
		outputArea.setBorder(BorderFactory.createEmptyBorder());
		this.consoleTable = consoleTable;
		lineHighlighted = false;

		inputListener = new CASTableCellController(this, view);
		inputArea.addKeyListener(inputListener);
		setInputBlank();
		setOutputBlank();

		//Initially, there is only a input area in the cell panel
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(inputArea);
		// this.add(outputArea);
		// this.add(BBorder);
		this.setBorder(BorderFactory.createEmptyBorder());
		return;
	}

	public void removeOutputArea() {
		this.remove(outputArea);
		this.repaint();
	}

	public void addOutputArea() {
		System.out.println("Add Output Area");
		this.add(outputArea);
		this.validate();
	}

	public void removeBBorder() {
		this.remove(BBorder);
		this.validate();
	}

	public int addBBorder() {
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
		this.add(BBorder);
		this.validate();
		return cellHeight;
	}

	public void setInput(String inValue) {
		this.inputArea.setText(">>" + inValue);
		this.input = inValue;
	}

	public void setInputBlank() {
		this.inputArea.setText("");
		this.input = "";
	}

	public void setOutput(String inValue) {
		this.outputArea.setText("<<" + inValue);
		this.output = inValue;
	}

	public void setOutputBlank() {
		this.outputArea.setText("");
		this.output = "";
	}

	public void setLineUnHighlighted() {
		lineHighlighted = false;
		this.removeBBorder();
	}

	public void setLineHighlighted() {
		lineHighlighted = true;
	}

	public boolean isLineHighlighted() {
		return this.lineHighlighted;
	}

	public String getInputCAS() {
		return inputArea.getText();
	}

	public String getInput() {
		return input;
	}

	public String getOutputCAS() {
		return outputArea.getText();
	}

	public String getOutput() {
		return output;
	}

	public JTable getConsoleTable() {
		return consoleTable;
	}
}
