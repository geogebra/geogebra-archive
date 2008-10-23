package geogebra.cas.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class CASLinePanel extends JPanel {
	Graphics2D g2;

	public CASLinePanel() {
		this.setBackground(Color.white);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		g2.drawLine(0, 0, this.getWidth(), 0);
	}
}
