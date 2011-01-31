package geogebra.gui;

import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

public class DynamicTextInputPanel extends JPanel implements FocusListener {

	private Application app;
	private ArrayList<JTextArea> textList;
	private DynamicTextInputPanel thisPanel;
	private int currentField = 0;

	public DynamicTextInputPanel(Application app) {
		super();
		this.app = app;
		thisPanel = this;
		setLayout(new FlowLayout(FlowLayout.LEFT));
		((FlowLayout)getLayout()).setHgap(0);
		this.setBackground(Color.white);
		textList = new ArrayList<JTextArea>();

		addStaticTextArea("text sample: ");
		addDynamicTextArea("A");
		addStaticTextArea(" = ");
		addDynamicTextArea("B + 3");
		addStaticTextArea(" some more text ");
	}

	public void addStaticTextArea(String s){
		OneLineTextArea ta =  new OneLineTextArea();
		ta.setText(s);
		add(ta);
		textList.add(ta);
	}

	public void addDynamicTextArea(String s){		
		DynamicTextArea ta =  new DynamicTextArea();
		ta.setText(s);
		add(ta);
		textList.add(ta);
	//	add(new CloseButton(ta));
	}

	public String getText(){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i < textList.size(); i++){
			if(i>0)
				sb.append(" + ");
			if(textList.get(i) instanceof DynamicTextArea){
				sb.append(textList.get(i).getText());
			}else{
				sb.append(" \"");
				sb.append(textList.get(i).getText());
				sb.append("\" ");
			}
			
		}
		return sb.toString();
	}



	private int findFocusedField(){
		int f = -1;
		for(int i=0; i < textList.size(); i++){
			if(textList.get(i).hasFocus()){
				f = i;
				return f;
			}
		}
		return f;
	}


	public void handleDocumentEvent(DocumentEvent e){
		System.out.println(getText());
	}



	private class OneLineTextArea extends JTextArea implements DocumentListener{
		OneLineTextArea thisTextArea;
		public OneLineTextArea(){
			super();
			thisTextArea = this;
			this.setMinimumSize(new Dimension(50,10));
		//	this.setMargin(new Insets(4,4,4,4));
			this.setBackground(Color.white);
			this.getDocument().addDocumentListener(this);
			this.addKeyListener(new KeyAdapter(){

				public void keyPressed(KeyEvent e) {
					int key = e.getKeyCode();
					switch (key){
					case KeyEvent.VK_LEFT:
					case KeyEvent.VK_DELETE:
					case KeyEvent.VK_BACK_SPACE:
						if(thisTextArea.getCaretPosition()==0){
							for(int f = findFocusedField()-1; f>=0; --f){
								if(!(textList.get(f) instanceof CloseButton)){
									textList.get(f).requestFocus();
									textList.get(f).setCaretPosition(textList.get(f).getText().length());
									return;
								}
							}
						if(thisTextArea instanceof DynamicTextArea){
							System.out.println("found dynamic text");
							textList.remove(thisTextArea);
							thisPanel.remove(thisTextArea);
							thisPanel.revalidate();		
						}
						}
						break;
					case KeyEvent.VK_RIGHT:
						if(thisTextArea.getCaretPosition()==thisTextArea.getText().length()){
							for(int f = findFocusedField()+1; f<textList.size(); ++f){
								if(!(textList.get(f) instanceof CloseButton)){
									textList.get(f).requestFocus();
									textList.get(f).setCaretPosition(0);
									return;
								}
							}
						}
						break;
					}	
				}				
			});

		}
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			
		}
		public void insertUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
			
		}
		public void removeUpdate(DocumentEvent e) {
			handleDocumentEvent(e);
			
		}
	}

	private class DynamicTextArea extends OneLineTextArea{
		public DynamicTextArea(){
			super();
			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createEtchedBorder(), 
					BorderFactory.createEmptyBorder(0, 0,0,0)));

			//	this.setBackground(Color.LIGHT_GRAY);
			this.setForeground(Color.red);		

		}
	}

	private class CloseButton extends DynamicTextArea{
		private JTextComponent comp;
		private CloseButton thisButton;
		public CloseButton(final JTextComponent comp){
			super();
			thisButton = this;
			this.comp = comp;
			Dimension d = this.getPreferredSize();
			d.width = 10;
			d.height=8;
			setPreferredSize(d);
			setMaximumSize(d);
			this.setBackground(Color.LIGHT_GRAY.brighter());
			this.setEditable(false);
			this.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					textList.remove(comp);
					thisPanel.remove(comp);
					thisPanel.remove(thisButton);
					thisPanel.revalidate();					
				}

				public void mouseEntered(MouseEvent arg0) {
					thisButton.setBackground(Color.LIGHT_GRAY);
				}

				public void mouseExited(MouseEvent arg0) {
					thisButton.setBackground(Color.LIGHT_GRAY.brighter());
				}

			});
		}
	}

	public void focusGained(FocusEvent arg0) {
		textList.get(0).requestFocus();

	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub

	}


}

