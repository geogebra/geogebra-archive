package geogebra.cas.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class CASLinePanel extends JPanel {
	Graphics2D g2;
	boolean drawLine;

	public CASLinePanel() {
		this.setBackground(Color.white);
		drawLine = false;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		
		if(drawLine)
			g2.drawLine(0, 0, this.getWidth(), 0);
		
	}

	public boolean showLine() {
		return drawLine;
	}

	public void setShowLine(boolean lineShown) {
		this.drawLine = lineShown;
	}
}
