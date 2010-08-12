package geogebra.gui.view.spreadsheet;


import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

/**
 * Button with popup component for choosing colors. A mouse click on the left
 * side of the button sets the selected color. A mouse click on the
 * right side triggers a popup with a swatch panel to choose a color. When the
 * popup is done the newly selected color is set. An actionPerformed() method
 * can retrieve the color by calling getSelectedColor().
 * 
 * @author G. Sturr 2010-7-10
 * 
 */
public class ColorChooserButton extends JButton{

	private ColorChooserButton btn;
	private ColorChooserPopup myPopup;
	private Color selectedColor; 
	
	
	/** Button constructor */
	public ColorChooserButton(){
		
		super(); 
		btn = this;
		myPopup = new ColorChooserPopup();
		this.addMouseListener( new MyMouseListener());
		if(selectedColor == null)
			selectedColor = myPopup.getSelectedColor();
		setIcon(drawColorFillIcon(selectedColor));
		
	}

	
	
	/** Listener to trigger the popup */
	class MyMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			Point locButton = btn.getLocation();
			int h = e.getX() - locButton.x;
			// trigger popup if the mouse is over the right side of the button
			if(e.getX() >= 20 &&  e.getX() <=38){ 
				myPopup.show(btn.getParent(), locButton.x,locButton.y + btn.getHeight());
			}
		}
		public void mouseReleased(MouseEvent e) {
			
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public Color getSelectedColor(){
		if(myPopup.isVisible()) 
			return null;
		else
			return selectedColor;
	}
	
	public void handlePopupEvent(){
		setIcon(drawColorFillIcon(selectedColor));
		repaint();
		this.fireActionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED,getActionCommand())); 
	}


	/** Draw an icon for the button. Left side is a grid that shows the selected color.
	 * Right side is a downward triangle for the drop down popup. */
	private ImageIcon drawColorFillIcon( Color selectedColor) {

		// Create image 
		BufferedImage image = new BufferedImage(32, 18, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();

		// right hand side: a grid filled with our selected color
		// (a click here just sends back the selected color): 
		int s = 7;
		int d = 1;
		g2.setColor(selectedColor);
		g2.fillRect(d, d, s, s);
		g2.setColor(Color.BLACK);
		g2.drawRect(d, d, s, s);

		
		g2.setColor(selectedColor);
		g2.fillRect(d+s, d, s, s);
		g2.setColor(Color.BLACK);
		g2.drawRect(d+s, d, s, s);

		//g2.setColor(selectedColor);
		g2.setColor(this.getBackground());
		g2.fillRect(d, d+s, s, s);
		g2.setColor(Color.BLACK);
		g2.drawRect(d, d+s, s, s);

		//g2.setColor(selectedColor);
		g2.setColor(this.getBackground());
		g2.fillRect(d+s, d+s, s, s);
		g2.setColor(Color.BLACK);
		g2.drawRect(d+s, d+s, s, s);

		
		// use a divider line ??
		//g2.setColor(Color.DARK_GRAY);
		//g2.drawLine(18, 0, 18, getHeight());

		
		
		// right hand side: a downward triangle
		// a click here triggers the popup
		g2.setColor(Color.BLACK);
		int x = 23;
		int y = 7;
		g2.drawLine(x, y, x+6, y);
		g2.drawLine(x+1, y+1, x+5, y+1);
		g2.drawLine(x+2, y+2, x+4, y+2);
		g2.drawLine(x+3, y+3, x+3, y+3);
		
		return new ImageIcon(image);	

	}

	
	
	
	
	
	/************************************************************* 
	 *             Swatch Panel Popup
	 ************************************************************/
	
	public class ColorChooserPopup extends JPopupMenu {

		SwatchPanel swatchPanel;

		public ColorChooserPopup() {
			super();

			swatchPanel = new SwatchPanel();
			swatchPanel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectedColor = swatchPanel.getSelectedColor(); 
					handlePopupEvent();
				}
			});

			this.add(swatchPanel);
		}

		public Color getSelectedColor(){
			return swatchPanel.getSelectedColor();
		}

		
		
		
	

		/** 
		 * Draw a swatch panel and handle mouse events in the panel. 
		*/
		class SwatchPanel extends JMenuItem implements MouseMotionListener{

			protected Color[] colors;
			protected Dimension swatchSize;
			protected Dimension numSwatches;
			protected Dimension gap;
			private int selectedRow = 0;
			private int selectedColumn = 0;
			SwatchPanel swatchPanel;

			public SwatchPanel() {
				swatchPanel = this;
				initValues();
				initColors();
				setToolTipText(""); 
				//setOpaque(true);
				setBackground(Color.white);
				setRequestFocusEnabled(false);
				//setInheritsPopupMenu(true);
				addMouseMotionListener(this);
				
			}

			//=======================================================
			//        Init
			
			protected void initValues() {
				
				//swatchSize = UIManager.getDimension("ColorChooser.swatchesSwatchSize");
				
				swatchSize = new Dimension(20,20);  // <------- set swatch pixel domensions
				numSwatches = new Dimension( 6, 4 ); // <------ set grid dimensions (row x column)
				
				gap = new Dimension(1, 1);
			}

			protected void initColors() {
				int[] rawValues = initRawValues3(); // <------ set color table 
				
				int numColors = rawValues.length / 3;
				colors = new Color[numColors];
				for (int i = 0; i < numColors ; i++) {
					colors[i] = new Color( rawValues[(i*3)], rawValues[(i*3)+1], rawValues[(i*3)+2] );
				}
			}


			public Dimension getPreferredSize() {
				int x = numSwatches.width * (swatchSize.width + gap.width) - 1;
				int y = numSwatches.height * (swatchSize.height + gap.height) - 1;
				return new Dimension( x, y );
			}


			public String getToolTipText(MouseEvent e) {
				Color color = getColorForLocation(e.getX(), e.getY());
				return color.getRed()+", "+ color.getGreen() + ", " + color.getBlue();
			}

			
			
			
			//=======================================================
			//           Get Properties
			
			public Color getColorForLocation( int x, int y ) {
				int column;
				if ((!this.getComponentOrientation().isLeftToRight())) {
					column = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
				} else {
					column = x / (swatchSize.width + gap.width);
				}
				int row = y / (swatchSize.height + gap.height);
				return getColorForCell(column, row);
			}

			public Point getCellCoordinates( int x, int y ) {
				int column;
				if ((!this.getComponentOrientation().isLeftToRight())) {
					column = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
				} else {
					column = x / (swatchSize.width + gap.width);
				}
				int row = y / (swatchSize.height + gap.height);
				return new Point(row, column);
			}

			private Color getColorForCell( int column, int row) {
				return colors[ (row * numSwatches.width) + column ]; 
			}

			public Color getSelectedColor(){
				return getColorForCell(selectedColumn,selectedRow);

			}



			//=======================================================
			//              Mouse Listeners
			
			public void mouseDragged(MouseEvent e) {	
			}

			public void mouseMoved(MouseEvent e) {
				if(e.getSource().equals(swatchPanel)){
					Point p = getCellCoordinates(e.getPoint().x, e.getPoint().y);
					selectedRow = p.x;
					selectedColumn = p.y;	
					swatchPanel.repaint();
				}
			}



			//=======================================================
			//              Paint
	
			public void paintComponent(Graphics g) {
				g.setColor(getBackground());
				g.fillRect(0,0,getWidth(), getHeight());
			
				int x,y;

				// draw swatches
				for (int row = 0; row < numSwatches.height; row++) {
					y = row * (swatchSize.height + gap.height);
					for (int column = 0; column < numSwatches.width; column++) {

						g.setColor( getColorForCell(column, row) );
						if ((!this.getComponentOrientation().isLeftToRight()) ) {
							x = (numSwatches.width - column - 1) * (swatchSize.width + gap.width);
						} else {
							x = column * (swatchSize.width + gap.width);
						}
						g.fillRect( x, y, swatchSize.width, swatchSize.height);
						g.setColor(Color.black);
						g.drawLine( x+swatchSize.width-1, y, x+swatchSize.width-1, y+swatchSize.height-1);
						g.drawLine( x, y+swatchSize.height-1, x+swatchSize.width-1, y+swatchSize.height-1);
					}
				}

				//draw border around the selected swatch
				g.setColor(Color.white);  

				y = selectedRow * (swatchSize.height + gap.height);
				if ((!this.getComponentOrientation().isLeftToRight()) ) {
					x = (numSwatches.width - selectedColumn - 1) * (swatchSize.width + gap.width);
				} else {
					x = selectedColumn * (swatchSize.width + gap.width);
				}
				g.drawLine( x+swatchSize.width-1, y, x+swatchSize.width-1, y+swatchSize.height-1);
				g.drawLine( x, y+swatchSize.height-1, x+swatchSize.width-1, y+swatchSize.height-1);
				g.drawLine( x, y, x, y+swatchSize.height-1);
				g.drawLine( x, y, x+swatchSize.width-1, y);
				
				
				// panel border
				g.setColor(Color.BLACK);
				g.drawLine(0,0, 0, getHeight());
				g.drawLine(0,0, getWidth(), 0);
				
				g.drawLine(getWidth(),getHeight(), 0, getHeight());
				g.drawLine(getWidth(),getHeight(), getWidth(), 0);
				
			}

			
			// our 4x6 color swatch
			// taken from http://www.rapidtables.com/prog/rgb_color.htm	
			
			private int[] initRawValues3() {

				int[] rawValues3 = {
						
						// first row 					
						255,255,255,	//White
						255,153,204,	//Rose
							
						255,204,153,	//Tan
						255,255,153,	//Light Yellow
						204,255,255,	//Light Turquoise
						204,255,204,	//Light Green
						
						
						// row 2
				
						192,192,192,	//Silver
						255,0,255,		//Magenta (fuchsia)	
						255,102,0,		//Orange
						
						255,255,0,		//Yellow
						153,204,255,	//Pale Blue		
						0,255,0,		//Green (lime)
						
						
						// row 3
						128,128,128,	//Gray / Grey
						204,153,255,	//Lavender
						255,0,0,		//Red
						
						255,215,0,		//Gold
						0,0,255,		//Blue
						153,204,0,		//Yellow Green
						
						
						// row 4
						0,0,0,			//Black			
						153,51,102,		//Plum
						153,51,0,		//Brown
						
						255,153,0,		//Light Orange
						0,128,128,		//Teal
						51,153,102,		//Sea Green
						
						//=================================
						
						
						64,64,64,		//Dark Gray		
						0,128,0,		//Dark Green
						0,255,255,		//Cyan (aqua)
					
						51,204,204,		//Aqua
						51,102,255,		//Light Blue
						0,204,255,		//Sky Blue	
						51,51,153,		//Indigo
						
		
						
					//	128,128,0,		//Olive
					
					//	51,51,0,		//Dark Olive
					//	128,0,0,		//Maroon
					
					//	102,102,153,	//Blue Gray
					//	0,0,128,		//Navy
					//	0,51,0,			//Dark Green
					//	0,51,102,		//Dark Teal
						128,0,128		//Purple
							
				};
				return rawValues3;
			}
			

	

			// swatch color codes used in the standard java color picker code
			private int[] initRawValues() {

				int[] rawValues = {
						255, 255, 255, // first row.
						204, 255, 255,
						204, 204, 255,
						204, 204, 255,
						204, 204, 255,
						204, 204, 255,
						204, 204, 255,
						204, 204, 255,
						204, 204, 255,
						204, 204, 255,
						204, 204, 255,
						255, 204, 255,
						255, 204, 204,
						255, 204, 204,
						255, 204, 204,
						255, 204, 204,
						255, 204, 204,
						255, 204, 204,
						255, 204, 204,
						255, 204, 204,
						255, 204, 204,
						255, 255, 204,
						204, 255, 204,
						204, 255, 204,
						204, 255, 204,
						204, 255, 204,
						204, 255, 204,
						204, 255, 204,
						204, 255, 204,
						204, 255, 204,
						204, 255, 204,
						204, 204, 204,  // second row.
						153, 255, 255,
						153, 204, 255,
						153, 153, 255,
						153, 153, 255,
						153, 153, 255,
						153, 153, 255,
						153, 153, 255,
						153, 153, 255,
						153, 153, 255,
						204, 153, 255,
						255, 153, 255,
						255, 153, 204,
						255, 153, 153,
						255, 153, 153,
						255, 153, 153,
						255, 153, 153,
						255, 153, 153,
						255, 153, 153,
						255, 153, 153,
						255, 204, 153,
						255, 255, 153,
						204, 255, 153,
						153, 255, 153,
						153, 255, 153,
						153, 255, 153,
						153, 255, 153,
						153, 255, 153,
						153, 255, 153,
						153, 255, 153,
						153, 255, 204,
						204, 204, 204,  // third row
						102, 255, 255,
						102, 204, 255,
						102, 153, 255,
						102, 102, 255,
						102, 102, 255,
						102, 102, 255,
						102, 102, 255,
						102, 102, 255,
						153, 102, 255,
						204, 102, 255,
						255, 102, 255,
						255, 102, 204,
						255, 102, 153,
						255, 102, 102,
						255, 102, 102,
						255, 102, 102,
						255, 102, 102,
						255, 102, 102,
						255, 153, 102,
						255, 204, 102,
						255, 255, 102,
						204, 255, 102,
						153, 255, 102,
						102, 255, 102,
						102, 255, 102,
						102, 255, 102,
						102, 255, 102,
						102, 255, 102,
						102, 255, 153,
						102, 255, 204,
						153, 153, 153, // fourth row
						51, 255, 255,
						51, 204, 255,
						51, 153, 255,
						51, 102, 255,
						51, 51, 255,
						51, 51, 255,
						51, 51, 255,
						102, 51, 255,
						153, 51, 255,
						204, 51, 255,
						255, 51, 255,
						255, 51, 204,
						255, 51, 153,
						255, 51, 102,
						255, 51, 51,
						255, 51, 51,
						255, 51, 51,
						255, 102, 51,
						255, 153, 51,
						255, 204, 51,
						255, 255, 51,
						204, 255, 51,
						153, 255, 51,
						102, 255, 51,
						51, 255, 51,
						51, 255, 51,
						51, 255, 51,
						51, 255, 102,
						51, 255, 153,
						51, 255, 204,
						153, 153, 153, // Fifth row
						0, 255, 255,
						0, 204, 255,
						0, 153, 255,
						0, 102, 255,
						0, 51, 255,
						0, 0, 255,
						51, 0, 255,
						102, 0, 255,
						153, 0, 255,
						204, 0, 255,
						255, 0, 255,
						255, 0, 204,
						255, 0, 153,
						255, 0, 102,
						255, 0, 51,
						255, 0 , 0,
						255, 51, 0,
						255, 102, 0,
						255, 153, 0,
						255, 204, 0,
						255, 255, 0,
						204, 255, 0,
						153, 255, 0,
						102, 255, 0,
						51, 255, 0,
						0, 255, 0,
						0, 255, 51,
						0, 255, 102,
						0, 255, 153,
						0, 255, 204,
						102, 102, 102, // sixth row
						0, 204, 204,
						0, 204, 204,
						0, 153, 204,
						0, 102, 204,
						0, 51, 204,
						0, 0, 204,
						51, 0, 204,
						102, 0, 204,
						153, 0, 204,
						204, 0, 204,
						204, 0, 204,
						204, 0, 204,
						204, 0, 153,
						204, 0, 102,
						204, 0, 51,
						204, 0, 0,
						204, 51, 0,
						204, 102, 0,
						204, 153, 0,
						204, 204, 0,
						204, 204, 0,
						204, 204, 0,
						153, 204, 0,
						102, 204, 0,
						51, 204, 0,
						0, 204, 0,
						0, 204, 51,
						0, 204, 102,
						0, 204, 153,
						0, 204, 204,
						102, 102, 102, // seventh row
						0, 153, 153,
						0, 153, 153,
						0, 153, 153,
						0, 102, 153,
						0, 51, 153,
						0, 0, 153,
						51, 0, 153,
						102, 0, 153,
						153, 0, 153,
						153, 0, 153,
						153, 0, 153,
						153, 0, 153,
						153, 0, 153,
						153, 0, 102,
						153, 0, 51,
						153, 0, 0,
						153, 51, 0,
						153, 102, 0,
						153, 153, 0,
						153, 153, 0,
						153, 153, 0,
						153, 153, 0,
						153, 153, 0,
						102, 153, 0,
						51, 153, 0,
						0, 153, 0,
						0, 153, 51,
						0, 153, 102,
						0, 153, 153,
						0, 153, 153,
						51, 51, 51, // eigth row
						0, 102, 102,
						0, 102, 102,
						0, 102, 102,
						0, 102, 102,
						0, 51, 102,
						0, 0, 102,
						51, 0, 102,
						102, 0, 102,
						102, 0, 102,
						102, 0, 102,
						102, 0, 102,
						102, 0, 102,
						102, 0, 102,
						102, 0, 102,
						102, 0, 51,
						102, 0, 0,
						102, 51, 0,
						102, 102, 0,
						102, 102, 0,
						102, 102, 0,
						102, 102, 0,
						102, 102, 0,
						102, 102, 0,
						102, 102, 0,
						51, 102, 0,
						0, 102, 0,
						0, 102, 51,
						0, 102, 102,
						0, 102, 102,
						0, 102, 102,
						0, 0, 0, // ninth row
						0, 51, 51,
						0, 51, 51,
						0, 51, 51,
						0, 51, 51,
						0, 51, 51,
						0, 0, 51,
						51, 0, 51,
						51, 0, 51,
						51, 0, 51,
						51, 0, 51,
						51, 0, 51,
						51, 0, 51,
						51, 0, 51,
						51, 0, 51,
						51, 0, 51,
						51, 0, 0,
						51, 51, 0,
						51, 51, 0,
						51, 51, 0,
						51, 51, 0,
						51, 51, 0,
						51, 51, 0,
						51, 51, 0,
						51, 51, 0,
						0, 51, 0,
						0, 51, 51,
						0, 51, 51,
						0, 51, 51,
						0, 51, 51,
						51, 51, 51 };
				return rawValues;
			}
		}
	}

}

