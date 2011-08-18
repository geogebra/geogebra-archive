package geogebra.gui.inputfield;


import geogebra.gui.util.GeoGebraIcon;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 * Extended Border class that adds simulated buttons to the right border of a JTextField.
 * See MyTextField and AutoCompleteTextField for usage.
 * 
 * @author G. Sturr
 *
 */
public class BorderButton extends AbstractBorder
implements MouseListener, MouseMotionListener{


	private Component borderOwner;
	

	public static final String cmdSuffix = "BorderButtonAction";
	private ImageIcon[] icon;
	private static final int hGap = 7;

	private boolean[] isVisibleIcon;
	private boolean[] isMouseOverIcon;
	private Rectangle[] iconRect;
	private ActionListener[] al;
	private Cursor otherCursor = Cursor.getDefaultCursor();
	private boolean isMouseOverIconRegion = false;
	
	private int maxIconCount = 4;
	

	public BorderButton(Component borderOwner) {

		this.borderOwner = borderOwner;

		MouseListener[] ml = borderOwner.getMouseListeners();
		for(int i=0; i < ml.length; i++)
			borderOwner.removeMouseListener(ml[i]);
		borderOwner.addMouseListener(this);
		for(int i=0; i < ml.length; i++)
			borderOwner.addMouseListener(ml[i]);
		
		borderOwner.addMouseMotionListener(this);
		
		icon = new ImageIcon[maxIconCount];
		isVisibleIcon = new boolean[maxIconCount];
		isMouseOverIcon = new boolean[maxIconCount];
		iconRect = new Rectangle[maxIconCount];
		al = new ActionListener[maxIconCount];

		for(int i = 0; i < maxIconCount; i++){
			icon[i] = new ImageIcon();
			iconRect[i] = new Rectangle();
			isMouseOverIcon[i] = false;
			// need default visibility = false so that focus lost/gained visibility works
			isVisibleIcon[i] = false;
		}
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {

		int offset = hGap;
		int xStart = x + w - getTotalInsetWidth();
		int yMid = y + h/2;


		for(int i = 0; i < icon.length; i++){
			if(icon[i] != null && isVisibleIcon[i]){
				g.drawImage(icon[i].getImage(), xStart + offset, yMid - icon[i].getIconHeight()/2 , null);

				iconRect[i].x = xStart + offset;
				iconRect[i].y = yMid - icon[i].getIconHeight()/2 ;
				iconRect[i].width = icon[i].getIconWidth();
				iconRect[i].height = icon[i].getIconHeight();

			//	g.setColor(Color.GRAY);
			//	if(isMouseOverIcon[i])
			//		g.drawRect(iconRect[i].x, iconRect[i].y, iconRect[i].width-1, iconRect[i].height-1);

				offset += icon[i].getIconWidth() + hGap;
			}
		}
	}

	public void setBorderButton(int index, ImageIcon icon, ActionListener listener){
		if(index < 0 || index > maxIconCount) return;
		this.icon[index] = icon;
		al[index] = listener;
	}

	public void setIconVisible(int index, boolean isVisible){
		if(index < 0 || index > maxIconCount) return;
		isVisibleIcon[index] = isVisible;
	}

	public boolean isIconVisible(int index){
		return isVisibleIcon[index];
	}
	
	
	private int getTotalInsetWidth(){
		int insetWidth = 0;
		for(int i = 0; i < icon.length; i++){	
			if(isVisibleIcon[i])
				insetWidth += icon[i].getIconWidth() + hGap;
		}
		return insetWidth;
	}


	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, getTotalInsetWidth());
	}

	public boolean isBorderOpaque() {
		return false;
	}

	private void swapCursor(){ 
		Cursor tmp = borderOwner.getCursor(); 
		borderOwner.setCursor(otherCursor); 
		otherCursor = tmp; 
	} 
	

	//=============================================
	//  Mouse Listeners
	//=============================================

	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		
		boolean isOver = e.getPoint().x > iconRect[0].x;
		if(isMouseOverIconRegion != isOver){
			isMouseOverIconRegion = isOver;
			swapCursor();
		}
		
		for(int i=0; i < iconRect.length; i++){
			isOver = iconRect[i].contains(e.getPoint());
			if(isMouseOverIcon[i] != isOver){
				isMouseOverIcon[i] = isOver;
				borderOwner.repaint();
			}
		}
	}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e) {	
		for(int i=0; i < iconRect.length; i++){
			if(isMouseOverIcon[i]){
				e.consume();
				ActionEvent ae = new ActionEvent(this,ActionEvent.ACTION_PERFORMED, i + cmdSuffix);
				al[i].actionPerformed(ae);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		for(int i=0; i < iconRect.length; i++){
			if(isMouseOverIcon[i]){
				e.consume();
			}
		}
	}




	/**************************************************************
	 *   Main --- for testing, remove later
	 *   
	 **************************************************************/

	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(150, 150));


		JTextField tf = new JTextField("Some text here");
		tf.setColumns(3);

		

		ActionListener al = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if(cmd.endsWith(cmdSuffix)){
					int i = Integer.parseInt(cmd.substring(0, 1));
				}			
			}	
		};

		BorderButton btnBorder = new BorderButton(tf);
		btnBorder.setBorderButton(0, GeoGebraIcon.createTreeIcon(), al);
		btnBorder.setBorderButton(1, GeoGebraIcon.createTreeIcon(), al);
		
		Border tfBorder = tf.getBorder();
		tf.setBorder(BorderFactory.createCompoundBorder(tfBorder, btnBorder));

		//tf.setBorder(BorderFactory.createEtchedBorder());


		JPanel p = new JPanel(new BorderLayout());
		p.add(tf, BorderLayout.NORTH);

		frame.getContentPane().add(p, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}






}



