package geogebra.export.pstricks.gui;

import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Toolkit;
import geogebra.Application;
import geogebra.export.pstricks.GeoGebraToPstricks;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
;
/**
 * @author Le Coq Loïc
 */
public class PstricksPanel extends JFrame{
	private TextValue textXUnit,textYUnit,textwidth,textheight;
	private JLabel labelXUnit,labelYUnit,labelwidth,labelheight,labelFontSize;
	final String[] msg={"10 pt","11 pt","12 pt"};
	private JComboBox comboFontSize;
	private JPanel panel;
	private JButton button,button_copy;
	private JCheckBox jcb;
	private JScrollPane js;
	private JTextArea textarea;
	private Application app;
	private final double width,height;
	public PstricksPanel(GeoGebraToPstricks ggb2ps,double w,double h){
		this.app=ggb2ps.getApp();
		width=w;
		height=h;
		setTitle(app.getPlain("TitleExportPstricks"));
		textXUnit=new TextValue(this,"1"){
			public void keyReleased(KeyEvent e){
				try{
					double d=getValue()*width;
					textwidth.setValue(d);
				}
				catch(NumberFormatException e1){
				}
			}
		
		};
		textYUnit=new TextValue(this,"1"){
			public void keyReleased(KeyEvent e){
				try{
					textheight.setValue(getValue()*height);
				}
				catch(NumberFormatException e1){
				}
			}
		
		};
		textwidth=new TextValue(this,String.valueOf(width)){
			public void keyReleased(KeyEvent e){
				try{
					textXUnit.setValue(getValue()/width);
				}
				catch(NumberFormatException e1){}
			}
		
		};
		textheight=new TextValue(this,String.valueOf(height)){
			public void keyReleased(KeyEvent e){
				try{
					textYUnit.setValue(getValue()/height);
				}
				catch(NumberFormatException e1){}
			}
		
		};
		panel=new JPanel();
		button=new JButton(app.getPlain("GeneratePstricks"));
		button_copy=new JButton(app.getPlain("CopyToClipboard"));
		labelXUnit=new JLabel(app.getPlain("XUnits"));
		labelYUnit=new JLabel(app.getPlain("YUnits"));
		labelwidth=new JLabel(app.getPlain("PictureWidth"));
		labelheight=new JLabel(app.getPlain("PictureHeight"));
		labelFontSize=new JLabel(app.getPlain("LatexFontSize"));
		jcb=new JCheckBox(app.getPlain("DisplayPointSymbol"));
		comboFontSize=new JComboBox(msg);
		jcb.setSelected(true);
		button.addActionListener(ggb2ps);
		button_copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					textarea.copy();
				}
		});
		js=new JScrollPane();
		textarea=new JTextArea();
		initGui();
		
	}
	private void initGui(){ 
		js.getViewport().add(textarea);
		
		panel.setLayout(new GridBagLayout());
		panel.add(labelXUnit, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textXUnit, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelwidth, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textwidth, new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelYUnit, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textYUnit, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelheight, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textheight, new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelFontSize, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(comboFontSize, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(jcb, new GridBagConstraints(2, 2, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(button, new GridBagConstraints(0, 3, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(button_copy, new GridBagConstraints(3, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(js, new GridBagConstraints(0, 4, 4, 5, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
				5, 5, 5, 5), 0, 0));
		textXUnit.setPreferredSize(new Dimension(110,textXUnit.getFont().getSize()+6));
		textYUnit.setPreferredSize(new Dimension(110,textYUnit.getFont().getSize()+6));
		js.setPreferredSize(new Dimension(400,400));
		getContentPane().add(panel);
		centerOnScreen();
		setVisible(true);
	}
	
	private void centerOnScreen() {
		//	center on screen
		pack();
		Dimension size = getPreferredSize();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = Math.min(size.width, dim.width);
		int h = Math.min(size.height, (int) (dim.height * 0.8));
		setLocation((dim.width - w) / 2, (dim.height - h) / 2);
		setSize(w, h);
	}
	
	public boolean getExportPointSymbol(){
		return jcb.isSelected();
	}
	public double getXUnit(){
		double d;
		try{
			d=textXUnit.getValue();	
		}
		catch(NumberFormatException e){d=1;}
		return d;
	}
	public double getYUnit()throws NumberFormatException{
		double d;
		try{
			d=textYUnit.getValue();	
		}
		catch(NumberFormatException e){d=1;}
		return d;
	}
	public double getLatexHeight(){
		return textheight.getValue();
	}
	public void write(StringBuffer sb){
		textarea.setText(new String(sb));
		textarea.selectAll();
	}
	public int getFontSize(){
		switch(comboFontSize.getSelectedIndex()){
			case 0:
				return 10;
			case 1:
				return 11;
			case 2:
				return 12;
		}
		return 10;
	}
}
