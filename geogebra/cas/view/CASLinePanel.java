package geogebra.cas.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class CASLinePanel extends JPanel {
	Graphics2D g2;

	private double leftX = 1.0;

	private double topY = 5.0;

	private double W = 800.0;

	private double H = 5.0;

	private Line2D line = new Line2D.Double(leftX, topY, W, H);

	public CASLinePanel() {
		this.setBackground(Color.white);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		g2.draw(line);
	}
}
