package geogebra.cas.view;

import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class CASTableCell extends JPanel{

	private JTextField	inputArea;
	private JTextField	outputArea;
	private CASTableCellController inputListener;
	private String		input;
	private String		output;
	
	public CASTableCell(CASView view) {
		inputArea = new JTextField();
		outputArea = new JTextField();
		
		inputListener =  new CASTableCellController(this, view);
		inputArea.addKeyListener(inputListener);
		inputArea.setText(">>");
		//outputArea.setText("<<");
		
		//this.setLayout(new GridLayout(2, 1));
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(inputArea);	
		this.add(outputArea);
		return;
	}			

	public void setInput(String inValue){
		this.inputArea.setText(inValue);
		this.input = inValue;
	}
	
	public void setOutput(String inValue){
		//add outputarea
		this.outputArea.setText(inValue);
		this.output = inValue;
	}
	
	public String getInput(){
		return inputArea.getText();
	}
	
	public String getOutput(){
		return outputArea.getText();
	}
	
}
