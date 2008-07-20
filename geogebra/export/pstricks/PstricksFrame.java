package geogebra.export.pstricks;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
;
/**
 * @author Le Coq Loïc
 */
public class PstricksFrame extends ExportFrame{
	private static final long serialVersionUID = 1L;
	private TextValue textXmin,textXmax, textYmin,textYmax;
	private JLabel labelXmin,labelXmax,labelYmin,labelYmax;
	public PstricksFrame(final GeoGebraToPstricks ggb2pst){
		super(ggb2pst,"GeneratePstricks");
		//definition of the behaviour of the textValues corresponding
		//to xmin, xmax, ymin and ymax.
		//Explaination for xs:
		//if xmin is changed, then both xmin and xmax are changed
		//to be sure that everything is allright even though xmin is set
		//to a higher value than xmax
		//then the width is changed.
		textXmin=new TextValue(this,String.valueOf(ggb2pst.getXmin()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double xmax = ggb2pst.getXmax();
					double m=getValue();
					if(m>xmax){
						ggb2pst.setXmax(m);
						ggb2pst.setXmin(xmax);
						width=m-xmax;
						int pos=getCaretPosition();
						textXmin.setValue(xmax);
						textXmax.setValue(m);
						textXmax.setCaretPosition(pos);
						textXmax.requestFocus();
					}
					else{
						ggb2pst.setXmin(m);
						width=xmax-m;
					}
					textwidth.setValue(width*ggb2pst.getXunit());
					ggb2pst.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
			
			
		};
		textXmax=new TextValue(this,String.valueOf(ggb2pst.getxmax()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double xmin = ggb2pst.getxmin();
					double m=getValue();
					if(m<xmin){
						ggb2pst.setxmin(m);
						ggb2pst.setxmax(xmin);
						width=xmin-m;
						int pos=getCaretPosition();
						textXmin.setValue(m);
						textXmax.setValue(xmin);
						textXmin.setCaretPosition(pos);
						textXmin.requestFocus();
					}
					else{
						ggb2pst.setxmax(m);
						width=m-xmin;
					}
					textwidth.setValue(width*ggb2pst.getxunit());
					ggb2pst.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		textYmin=new TextValue(this,String.valueOf(ggb2pst.getymin()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double ymax = ggb2pst.getymax();
					double m=getValue();
					if(m>ymax){
						ggb2pst.setymax(m);
						ggb2pst.setymin(ymax);
						height=m-ymax;
						int pos=getCaretPosition();
						textYmin.setValue(ymax);
						textYmax.setValue(m);
						textYmax.setCaretPosition(pos);
						textYmax.requestFocus();

					}
					else{
						ggb2pst.setymin(m);
						height=ymax-m;
					}
					textheight.setValue(height*ggb2pst.getyunit());
					ggb2pst.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		textYmax=new TextValue(this,String.valueOf(ggb2pst.getymax()),true){
			private static final long serialVersionUID = 1L;
			public void keyReleased(KeyEvent e){
				try{
					double ymin = ggb2pst.getymin();
					double m=getValue();
					if(m<ymin){
						ggb2pst.setymin(m);
						ggb2pst.setymax(ymin);
						height=ymin-m;
						int pos=getCaretPosition();
						textYmin.setValue(m);
						textYmax.setValue(ymin);
						textYmin.setCaretPosition(pos);
						textYmin.requestFocus();
					}
					else{
						ggb2pst.setymax(m);
						height=m-ymin;
					}
					textheight.setValue(height*ggb2pst.getyunit());
					ggb2pst.refreshSelectionRectangle();
				}
				catch(NumberFormatException e1){}
			}
		};
		initGui();
	}
	protected void initGui(){ 
		setTitle(app.getPlain("TitleExportPstricks"));
		labelXmin=new JLabel(app.getPlain("xmin"));
 		labelXmax=new JLabel(app.getPlain("xmax"));
 		labelYmin=new JLabel(app.getPlain("ymin"));
 		labelYmax=new JLabel(app.getPlain("ymax"));

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
		panel.add(labelheight, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
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
		panel.add(labelXmax, new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
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
		panel.add(labelYmax, new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textYmax, new GridBagConstraints(3, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelFontSize, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
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
		panel.add(buttonSave, new GridBagConstraints(2, 5, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(button_copy, new GridBagConstraints(3, 5, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(js, new GridBagConstraints(0, 6, 4, 5, 1.0, 20.0,
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
	
}
