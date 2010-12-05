package geogebra.cas.view;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

public abstract class CASTableCell extends JPanel{

	protected CASInputPanel inputPanel;
	protected CASOutputPanel outputPanel;
	protected JLabel inputLabel; // dummy label used to get preferred size;
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
		inputLabel = new JLabel();
		
		// create BorderLayout JPanel with:
		// 1) inputPanel is in WEST so that its width can be controlled.
		// The cell editor will set the width to fit the viewport, the 
		// cell renderer will set it to fill the table column.
		// 2) inputLabel is in CENTER, but invisible since it is a dummy
		// that is used only to get a preferred width.
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.setBackground(this.getBackground());
		northPanel.add(inputLabel, BorderLayout.CENTER);
		northPanel.add(inputPanel, BorderLayout.WEST);	
		inputLabel.setVisible(false);
		
		outputPanel = new CASOutputPanel(view.getApp());
		
		add(northPanel, BorderLayout.NORTH);
		add(outputPanel, BorderLayout.CENTER);
		return;
	}

	/**
	 * Overrides getPreferredSize so that it reports the preferred size
	 * when the input label is completely drawn, not clipped for the editor.
	 */
	@Override
	public Dimension getPreferredSize(){
		Dimension d = super.getPreferredSize();
		d.width = Math.max(d.width, inputLabel.getPreferredSize().width);
		return d;
	}
	
	
	public int getInputPanelHeight() {
		return inputPanel.getHeight();
	}
	
	public int getOutputPanelHeight() {
		return outputPanel.getHeight();
	}
	
	/**
	 * Sets the width of the input panel. 
	 * If width = -1 it sets the width to the full input string.
	 * This width is given by the preferred size of the dummy label.
	 */
	public void setInputPanelWidth(int width){
		
		Dimension d = inputPanel.getPreferredSize();
		if(width == -1)
			d.width = inputLabel.getPreferredSize().width;
		else
			d.width = width - 15;  // adjust 15 pixels for the empty border
		
		inputPanel.setPreferredSize(d);
		//inputPanel.validate();
	}

	
	
	public void setValue(CASTableCellValue cellValue) {
		// set input panel
		String input = cellValue.getTranslatedInput();
		inputPanel.setInput(input);
		inputLabel.setText(inputPanel.getInputArea().getText());
		
		
		// set output panel
		boolean showOutput = cellValue.showOutput();
		outputPanel.setVisible(showOutput);
		if (showOutput) {
			// show eval command (e.g. "Substitute") in output cell
			String evalCmd = cellValue.getEvalCommand();
			String evalCmdLocal = app.getCommand(evalCmd);
			
			if (input.startsWith(evalCmdLocal)) {
				// don't show command if it is already at beginning of input
				evalCmdLocal = "";
			}
			
			// eval comment (e.g. "x=5, y=8")
			String evalComment = cellValue.getEvalComment();
			if (evalComment.length() > 0) {
				if (evalCmdLocal.length() == 0) {
					evalCmdLocal = evalComment;
				} else {
					evalCmdLocal = evalCmdLocal + ", " + evalComment;
				}
			}
			
			outputPanel.setOutput(
					cellValue.getOutput(), 
					cellValue.getLaTeXOutput(), 
					evalCmdLocal, 
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
		if (inputLabel != null){
			inputLabel.setFont(ft);
		}
		if (outputPanel != null)
			outputPanel.setFont(ft);
	}


}
