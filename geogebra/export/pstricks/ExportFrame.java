package geogebra.export.pstricks;
import geogebra.Application;


import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStreamWriter;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

abstract public class ExportFrame extends JFrame{
	protected TextValue textXUnit,textYUnit,textwidth,textheight;
	protected JLabel labelwidth,labelheight,labelXUnit,labelYUnit,labelFontSize;
	protected TextValue textXmin,textXmax, textYmin,textYmax;
	protected JLabel labelXmin,labelXmax,labelYmin,labelYmax;
	final String[] msg={"10 pt","11 pt","12 pt"};
	protected JComboBox comboFontSize;
	protected JPanel panel;
	protected JButton button,button_copy;
	protected JCheckBox jcbPointSymbol,jcbGrayscale;
	protected JScrollPane js;
	protected JTextArea textarea;
	protected Application app;
	protected double width,height;
	protected JButton buttonSave;
	
	protected File currentFile=null;
	//definition of the behaviour of the textValues corresponding
	//to xmin, xmax, ymin and ymax.
	//Explaination for xs:
	//if xmin is changed, then both xmin and xmax are changed
	//to be sure that everything is allright even though xmin is set
	//to a higher value than xmax
	//then the width is changed.
	public ExportFrame(final GeoGebraExport ggb,String action){
		this.app=ggb.getApp();
		width=ggb.getXmax()-ggb.getXmin();
		height=ggb.getYmax()-ggb.getYmin();
		textXUnit=new TextValue(this,String.valueOf(ggb.getXunit()),false){
			private static final long serialVersionUID = 1L;

			public void keyReleased(KeyEvent e){
				try{
					double value = getValue();
					ggb.setXunit(value);
					textwidth.setValue(value*width);
				}
				catch(NumberFormatException e1){
				}
			}
		
		};
		textYUnit=new TextValue(this,String.valueOf(ggb.getYunit()),false){
			private static final long serialVersionUID = 1L;

			public void keyReleased(KeyEvent e){
				try{
					double value=getValue();
					ggb.setYunit(value);
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
					ggb.setXunit(value);
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
					ggb.setYunit(value);
					textYUnit.setValue(value);
				}
				catch(NumberFormatException e1){}
			}
		
		};
		textXmin=new TextValue(this,String.valueOf(ggb.getXmin()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double xmax = ggb.getXmax();
					double m=getValue();
					if(m>xmax){
						ggb.setXmax(m);
						ggb.setXmin(xmax);
						width=m-xmax;
						int pos=getCaretPosition();
						textXmin.setValue(xmax);
						textXmax.setValue(m);
						textXmax.setCaretPosition(pos);
						textXmax.requestFocus();
					}
					else{
						ggb.setXmin(m);
						width=xmax-m;
					}
					textwidth.setValue(width*ggb.getXunit());
					ggb.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
			
			
		};
		textXmax=new TextValue(this,String.valueOf(ggb.getxmax()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double xmin = ggb.getxmin();
					double m=getValue();
					if(m<xmin){
						ggb.setxmin(m);
						ggb.setxmax(xmin);
						width=xmin-m;
						int pos=getCaretPosition();
						textXmin.setValue(m);
						textXmax.setValue(xmin);
						textXmin.setCaretPosition(pos);
						textXmin.requestFocus();
					}
					else{
						ggb.setxmax(m);
						width=m-xmin;
					}
					textwidth.setValue(width*ggb.xunit);
					ggb.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		textYmin=new TextValue(this,String.valueOf(ggb.getymin()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double ymax = ggb.getymax();
					double m=getValue();
					if(m>ymax){
						ggb.setymax(m);
						ggb.setymin(ymax);
						height=m-ymax;
						int pos=getCaretPosition();
						textYmin.setValue(ymax);
						textYmax.setValue(m);
						textYmax.setCaretPosition(pos);
						textYmax.requestFocus();

					}
					else{
						ggb.setymin(m);
						height=ymax-m;
					}
					textheight.setValue(height*ggb.yunit);
					ggb.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		textYmax=new TextValue(this,String.valueOf(ggb.getymax()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double ymin = ggb.getymin();
					double m=getValue();
					if(m<ymin){
						ggb.setymin(m);
						ggb.setymax(ymin);
						height=ymin-m;
						int pos=getCaretPosition();
						textYmin.setValue(m);
						textYmax.setValue(ymin);
						textYmin.setCaretPosition(pos);
						textYmin.requestFocus();
					}
					else{
						ggb.setymax(m);
						height=m-ymin;
					}
					textheight.setValue(height*ggb.yunit);
					ggb.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		panel=new JPanel();
		button=new JButton(app.getPlain(action));
		button_copy=new JButton(app.getPlain("CopyToClipboard"));
		labelXUnit=new JLabel(app.getPlain("XUnits"));
		labelYUnit=new JLabel(app.getPlain("YUnits"));
		labelwidth=new JLabel(app.getPlain("PictureWidth"));
		labelheight=new JLabel(app.getPlain("PictureHeight"));
		labelFontSize=new JLabel(app.getPlain("LatexFontSize"));
		labelXmin=new JLabel(app.getPlain("xmin"));
 		labelXmax=new JLabel(app.getPlain("xmax"));
 		labelYmin=new JLabel(app.getPlain("ymin"));
 		labelYmax=new JLabel(app.getPlain("ymax"));
		jcbPointSymbol=new JCheckBox(app.getPlain("DisplayPointSymbol"));
		jcbGrayscale=new JCheckBox(app.getPlain("PGFExport.Grayscale"));
		comboFontSize=new JComboBox(msg);
		jcbPointSymbol.setSelected(true);
		jcbGrayscale.setSelected(false);
		button.addActionListener(ggb);
		button_copy.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
					textarea.copy();
				}
		});
		js=new JScrollPane();
		textarea=new JTextArea();
		buttonSave=new JButton(app.getMenu("SaveAs"));
		buttonSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
		        currentFile =
		            app.getGuiManager().showSaveDialog(
		                Application.FILE_EXT_TEX, currentFile,
		                "TeX " + app.getMenu("Files"));
		        if (currentFile == null)
		            return;
		        else {
		        	try{
		        		FileOutputStream f = new FileOutputStream(currentFile);
		        		BufferedOutputStream b = new BufferedOutputStream(f);	
		        		OutputStreamWriter osw = new  OutputStreamWriter(b,  "UTF8");
		        		osw.write(textarea.getText());
		        		osw.close();
		        		b.close();
		        		f.close();
		        	}
		        	catch(FileNotFoundException e1){}
		        	catch(UnsupportedEncodingException e2){}
		        	catch(IOException e3){}
		        }
			}
		});
	}
	protected void centerOnScreen() {
		//	center on screen
		pack();				
		setLocationRelativeTo(app.getFrame());
	}
	public boolean isGrayscale(){
		return jcbGrayscale.isSelected();
	}
	public boolean getExportPointSymbol(){
		return jcbPointSymbol.isSelected();
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
