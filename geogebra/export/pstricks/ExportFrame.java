package geogebra.export.pstricks;
import geogebra.Application;


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
	final String[] msg={"10 pt","11 pt","12 pt"};
	protected JComboBox comboFontSize;
	protected JPanel panel;
	protected JButton button,button_copy;
	protected JCheckBox jcb;
	protected JScrollPane js;
	protected JTextArea textarea;
	protected Application app;
	protected double width,height;
	protected JButton buttonSave;
	protected File currentFile=null;
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
		panel=new JPanel();
		button=new JButton(app.getPlain(action));
		button_copy=new JButton(app.getPlain("CopyToClipboard"));
		labelXUnit=new JLabel(app.getPlain("XUnits"));
		labelYUnit=new JLabel(app.getPlain("YUnits"));
		labelwidth=new JLabel(app.getPlain("PictureWidth"));
		labelheight=new JLabel(app.getPlain("PictureHeight"));
		labelFontSize=new JLabel(app.getPlain("LatexFontSize"));
		jcb=new JCheckBox(app.getPlain("DisplayPointSymbol"));
		comboFontSize=new JComboBox(msg);
		jcb.setSelected(true);
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
		            app.showSaveDialog(
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
