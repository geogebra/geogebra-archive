package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.*;

public class CASTableCell extends JPanel {
	// implements FocusListener {

	// Components in a cell
	private CASInputPanel inputPanel;

	private CASOutputPanel outputPanel;

	private CASLinePanel linePanel;

	private CASTable consoleTable;

	private boolean lineVisiable;

	private boolean outputFieldVisiable;

	protected Application app;

	public CASTableCell(CASView view, CASTable consoleTable, Application app) {

		this.app = app;

		inputPanel = new CASInputPanel();
		outputPanel = new CASOutputPanel();
		linePanel = new CASLinePanel();

		this.consoleTable = consoleTable;
		lineVisiable = false;
		outputFieldVisiable = true;

		this.setInput("");
		this.setOutput("");
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.add(inputPanel);
		this.add(linePanel);
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setBackground(Color.white);

		setFont(app.getPlainFont());
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
			// Application.debug("Add Output panel");
			this.remove(linePanel);

			this.add(outputPanel);
			this.add(linePanel);
			outputFieldVisiable = true;
			this.validate();
		}
	}

	public void removeLine() {

		// this.validate();
		SwingUtilities.updateComponentTreeUI(this);

		linePanel.setLineVisiable(false);
		lineVisiable = false;
	}

	public void addLine() {

		linePanel.setLineVisiable(true);
		lineVisiable = true;
		this.validate();
	}

	public void setInput(String inValue) {
		this.inputPanel.setInput(inValue);
	}

	public void setOutput(String inValue) {
		this.outputPanel.setOutput(inValue);
		String empty = new String("");
		if (empty.compareTo(inValue) == 0)
			this.removeOutputPanel();
		else
			this.addOutputPanel();
	}

	/*
	 * Function: set the line unhighlighted, and return a proper cell height
	 */
	public void setLineInvisiable() {
		lineVisiable = false;
		this.removeLine();
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

	public CASTable getConsoleTable() {
		return consoleTable;
	}

	public boolean isOutputPanelAdded() {
		return outputFieldVisiable;
	}

	public void setInputAreaFocused() {
		inputPanel.setInputAreaFocused();
		inputPanel.setInputCaretPosition(inputPanel.getInput().length());
	}

	public void setLineBorderFocus() {
		linePanel.requestFocus();
	}

	public CASLinePanel getLinePanel() {
		return linePanel;
	}

	public CASInputPanel getInputPanel() {
		return inputPanel;
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

	final public void setFont(Font ft) {
		super.setFont(ft);
		if (inputPanel != null)
			inputPanel.setFont(ft);
		if (outputPanel != null)
			outputPanel.setFont(ft);
	}

}
