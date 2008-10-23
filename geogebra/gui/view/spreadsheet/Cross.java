
package geogebra.gui.view.spreadsheet;

import geogebra.gui.view.spreadsheet.SpreadsheetView.MouseMotionListener1;
import geogebra.main.Application;

import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Cross extends JLabel
{
	private static final long serialVersionUID = 1L;
	public static int LENGTH = 7;
	
	public boolean isMouseOver = false;
	protected Application app;
	
	public Cross(Application app0) {
		addMouseListener(new MouseListener1());
		app = app0;
	}
	
	public void paint(Graphics g) {
		if (isMouseOver) {
			g.setColor(Color.RED);
		}
		else {
			g.setColor(Color.GRAY);
		}
		g.drawLine(0, 0, LENGTH, LENGTH);		
		g.drawLine(1, 0, LENGTH, LENGTH - 1);		
		g.drawLine(0, 1, LENGTH - 1, LENGTH);		
		g.drawLine(0, LENGTH - 1, LENGTH - 1, 0);		
		g.drawLine(0, LENGTH, LENGTH, 0);		
		g.drawLine(1, LENGTH, LENGTH, 1);		
	}
	
	protected class MouseListener1 implements MouseListener
	{
		
		public void mouseClicked(MouseEvent e) {
			//Application.debug("mouseClicked");
			app.setShowSpreadsheetView(false);
			app.updateCenterPanel(true);
			isMouseOver = false;
		}
		
		public void mouseEntered(MouseEvent e) {
			//Application.debug("mouseEntered");
			isMouseOver = true;
			repaint();
		}
		
		public void mouseExited(MouseEvent e) {
			//Application.debug("mouseExited");
			isMouseOver = false;
			repaint();
		}
		
		public void mousePressed(MouseEvent e) {
		}
		
		public void mouseReleased(MouseEvent e)	{
		}

	}
	

}