package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

public abstract class CASTableCell extends JPanel{

	protected CASInputPanel inputPanel;
	protected CASOutputPanel outputPanel;
	private CASTable consoleTable;
	protected Application app;
	protected CASView view;

	public CASTableCell(CASView view) {
		this.view = view;
		this.app = view.getApp();
		this.consoleTable = view.getConsoleTable();
			
		setLayout(new BorderLayout(5, 5));
		setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));		
		setBackground(Color.white);
		
		inputPanel = new CASInputPanel(app);
		outputPanel = new CASOutputPanel(view.getApp());		
		add(inputPanel, BorderLayout.NORTH);
		add(outputPanel, BorderLayout.CENTER);
		return;
	}

	public int getInputPanelHeight() {
		return inputPanel.getHeight();
	}
	
	public int getOutputPanelHeight() {
		return outputPanel.getHeight();
	}
	
	public void setValue(CASTableCellValue cellValue) {
		// set input panel
		String input = cellValue.getTranslatedInput();
		inputPanel.setInput(input);
		
		// set output panel
		boolean showOutput = cellValue.showOutput();
		outputPanel.setVisible(showOutput);
		if (showOutput) {
			// show eval command (e.g. "Substitute") in output cell
			String outputComment = app.getCommand(cellValue.getEvalCommand());
			if (input.startsWith(outputComment)) {
				// don't show command if it is already at beginning of input
				outputComment = "";
			}
			
			// eval comment (e.g. "x=5, y=8")
			String evalComment = cellValue.getEvalComment();
			if ("".equals(outputComment)) {
				outputComment = evalComment;
			} else {
				outputComment = outputComment + " " + evalComment;
			}
			
			outputPanel.setOutput(
					cellValue.getOutput(), 
					cellValue.getLaTeXOutput(), 
					outputComment, 
					cellValue.isError()
				);
		}	
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
	
	public JTextComponent getInputArea() {
		return inputPanel.getInputArea();
	}	
	
	public void setFont(Font ft) {
		super.setFont(ft);
		if (inputPanel != null)
			inputPanel.setFont(ft);
		if (outputPanel != null)
			outputPanel.setFont(ft);
	}


}
