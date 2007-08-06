package geogebra.export.pstricks.gui;

import geogebra.Application;
import geogebra.export.pstricks.GeoGebraToPstricks;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
;
/**
 * @author Le Coq Loïc
 */
public class PstricksPanel extends JFrame{
	private static final long serialVersionUID = 1L;
	//addition of the textValues and Jlabels for xmin,xmax,ymin and ymax
	private TextValue textXUnit,textYUnit,textwidth,textheight,
		textXmin,textXmax, textYmin,textYmax;
	private JLabel labelXUnit,labelYUnit,labelwidth,labelheight,labelXmin,
		labelXmax,labelYmin,labelYmax,labelFontSize;
	//end changes
	final String[] msg={"10 pt","11 pt","12 pt"};
	private JComboBox comboFontSize;
	private JPanel panel;
	private JButton button,button_copy;
	private JCheckBox jcb;
	private JScrollPane js;
	private JTextArea textarea;
	private Application app;
	//all the necessary parameters are in gg2ps and are read
	//and modified directly there
	private double width,height;
	//end changes
	//changes of the constructor to accomodate the use of the new parameters
	public PstricksPanel(final GeoGebraToPstricks ggb2ps){
		this.app=ggb2ps.getApp();
		//changes start here
		width=ggb2ps.getxmax()-ggb2ps.getxmin();
		height=ggb2ps.getymax()-ggb2ps.getymin();
		//they stop here
		setTitle(app.getPlain("TitleExportPstricks"));
		textXUnit=new TextValue(this,String.valueOf(ggb2ps.getxunit()),false){
			private static final long serialVersionUID = 1L;

			public void keyReleased(KeyEvent e){
				try{
					double value = getValue();
					ggb2ps.setxunit(value);
					textwidth.setValue(value*width);
				}
				catch(NumberFormatException e1){
				}
			}
		
		};
		textYUnit=new TextValue(this,String.valueOf(ggb2ps.getyunit()),false){
			private static final long serialVersionUID = 1L;

			public void keyReleased(KeyEvent e){
				try{
					double value=getValue();
					ggb2ps.setyunit(value);
					textheight.setValue(value*height);
				}
				catch(NumberFormatException e1){
				}
			}
		
		};
		textwidth=new TextValue(this,String.valueOf(width),false){
			private static final long serialVersionUID = 1L;

			public void keyReleased(KeyEvent e){
				try{
					double value = getValue()/width;
					ggb2ps.setxunit(value);
					textXUnit.setValue(value);
				}
				catch(NumberFormatException e1){}
			}
		
		};
		textheight=new TextValue(this,String.valueOf(height),false){
			private static final long serialVersionUID = 1L;

			public void keyReleased(KeyEvent e){
				try{
					double value = getValue()/height;
					ggb2ps.setyunit(value);
					textYUnit.setValue(value);
				}
				catch(NumberFormatException e1){}
			}
		
		};
		//definition of the behaviour of the textValues corresponding
		//to xmin, xmax, ymin and ymax.
		//Explaination for xs:
		//if xmin is changed, then both xmin and xmax are changed
		//to be sure that everything is allright even though xmin is set
		//to a higher value than xmax
		//then the width is changed.
		textXmin=new TextValue(this,String.valueOf(ggb2ps.getxmin()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double xmax = ggb2ps.getxmax();
					double m=getValue();
					if(m>xmax){
						ggb2ps.setxmax(m);
						ggb2ps.setxmin(xmax);
						width=m-xmax;
						int pos=getCaretPosition();
						textXmin.setValue(xmax);
						textXmax.setValue(m);
						textXmax.setCaretPosition(pos);
						textXmax.requestFocus();
					}
					else{
						ggb2ps.setxmin(m);
						width=xmax-m;
					}
					textwidth.setValue(width*ggb2ps.getxunit());
					ggb2ps.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
			
			
		};
		textXmax=new TextValue(this,String.valueOf(ggb2ps.getxmax()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double xmin = ggb2ps.getxmin();
					double m=getValue();
					if(m<xmin){
						ggb2ps.setxmin(m);
						ggb2ps.setxmax(xmin);
						width=xmin-m;
						int pos=getCaretPosition();
						textXmin.setValue(m);
						textXmax.setValue(xmin);
						textXmin.setCaretPosition(pos);
						textXmin.requestFocus();
					}
					else{
						ggb2ps.setxmax(m);
						width=m-xmin;
					}
					textwidth.setValue(width*ggb2ps.getxunit());
					ggb2ps.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		textYmin=new TextValue(this,String.valueOf(ggb2ps.getymin()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double ymax = ggb2ps.getymax();
					double m=getValue();
					if(m>ymax){
						ggb2ps.setymax(m);
						ggb2ps.setymin(ymax);
						height=m-ymax;
						int pos=getCaretPosition();
						textYmin.setValue(ymax);
						textYmax.setValue(m);
						textYmax.setCaretPosition(pos);
						textYmax.requestFocus();

					}
					else{
						ggb2ps.setymin(m);
						height=ymax-m;
					}
					textheight.setValue(height*ggb2ps.getyunit());
					ggb2ps.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		textYmax=new TextValue(this,String.valueOf(ggb2ps.getymax()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double ymin = ggb2ps.getymin();
					double m=getValue();
					if(m<ymin){
						ggb2ps.setymin(m);
						ggb2ps.setymax(ymin);
						height=ymin-m;
						int pos=getCaretPosition();
						textYmin.setValue(m);
						textYmax.setValue(ymin);
						textYmin.setCaretPosition(pos);
						textYmin.requestFocus();
					}
					else{
						ggb2ps.setymax(m);
						height=m-ymin;
					}
					textheight.setValue(height*ggb2ps.getyunit());
					ggb2ps.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		//,textXmax, textYmin,textYmax;
		panel=new JPanel();
		button=new JButton(app.getPlain("GeneratePstricks"));
		button_copy=new JButton(app.getPlain("CopyToClipboard"));
		labelXUnit=new JLabel(app.getPlain("XUnits"));
		labelYUnit=new JLabel(app.getPlain("YUnits"));
		labelwidth=new JLabel(app.getPlain("PictureWidth"));
		labelheight=new JLabel(app.getPlain("PictureHeight"));
		labelXmin=new JLabel(app.getPlain("xmin"));
		labelXmax=new JLabel(app.getPlain("xmax"));
		labelYmin=new JLabel(app.getPlain("ymin"));
		labelYmax=new JLabel(app.getPlain("ymax"));
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
	//I changed this method so as to diplay the field correctly.
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
		panel.add(labelXmin, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textXmin, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelXmax, new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0,				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textXmax, new GridBagConstraints(3, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelYmin, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textYmin, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelYmax, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textYmax, new GridBagConstraints(3, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelFontSize, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(comboFontSize, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(jcb, new GridBagConstraints(2, 4, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(button, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(button_copy, new GridBagConstraints(3, 5, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(js, new GridBagConstraints(0, 6, 4, 5, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
				5, 5, 5, 5), 0, 0));
		textXUnit.setPreferredSize(new Dimension(110,textXUnit.getFont().getSize()+6));
		textYUnit.setPreferredSize(new Dimension(110,textYUnit.getFont().getSize()+6));
		js.setPreferredSize(new Dimension(400,400));
		getContentPane().add(panel);
		centerOnScreen();
		setVisible(true);
	}
	//end changes.
	
	private void centerOnScreen() {
		//	center on screen
		pack();				
		setLocationRelativeTo(app.getFrame());
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
