package geogebra.gui;

import geogebra.kernel.GeoElement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import java.awt.geom.Line2D;
import java.awt.geom.Arc2D;
/**
 * @author Loïc Le Coq
 * date 31/10/2006
 * This class defines the renderer for the ComboBox where
 * the user chooses the decoration for GeoAngle
 * 
 * 
 * 
 * */

public class DecorationAngleListRenderer extends JPanel implements ListCellRenderer {
	private Line2D.Double tick=new Line2D.Double();
	private Arc2D.Double arc=new Arc2D.Double();
	private int id=0;
	public DecorationAngleListRenderer() {
		setOpaque(true);
//		setPreferredSize(new Dimension(50,20));
	}

	public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
//Get the selected index. (The index param isn't
//always valid, so just use the value.)
		int selectedIndex = ((Integer)value).intValue();
		this.id=selectedIndex;
			if (isSelected) {
				setBackground(list.getSelectionBackground());
//				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
//  				setForeground(list.getForeground());
			}
			setBorder(BorderFactory.createEmptyBorder(12,2,12,2));	       
			return this;
	}
	private void drawTick(double angle){
		tick.setLine(13+37*Math.cos(angle),
				27-37*Math.sin(angle),
				13+43*Math.cos(angle),
				27-43*Math.sin(angle));

	}

	public void paint(Graphics g){
		super.paint(g);
		// added by Markus Hohenwarter, BEGIN
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
		// added by Markus Hohenwarter, END
		g2.setColor(getBackground());
		g2.fillRect(0,0,getWidth(),getHeight());
		g2.setColor(Color.BLACK);
		g2.drawLine(13,27, 67, 27);
		g2.drawLine(13,27,67,3);
		arc.setArcByCenter(13,27,40,0,24,Arc2D.OPEN);
		g2.draw(arc);
		switch(id){
			case GeoElement.DECORATION_ANGLE_TWO_ARCS:
				arc.setArcByCenter(13,27,35,0,24,Arc2D.OPEN);
				g2.draw(arc);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_ARCS:
				arc.setArcByCenter(13,27,35,0,24,Arc2D.OPEN);
				g2.draw(arc);
				arc.setArcByCenter(13,27,45,0,24,Arc2D.OPEN);
				g2.draw(arc);
			break;
			case GeoElement.DECORATION_ANGLE_ONE_TICK:
				drawTick(Math.toRadians(12));
				g2.draw(tick);
			break;
			case GeoElement.DECORATION_ANGLE_TWO_TICKS:
				drawTick(Math.toRadians(9.6));
				g2.draw(tick);
				drawTick(Math.toRadians(14.4));
				g2.draw(tick);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_TICKS:
				drawTick(Math.toRadians(12));
				g2.draw(tick);
				drawTick(Math.toRadians(7));
				g2.draw(tick);
				drawTick(Math.toRadians(16));
				g2.draw(tick);
			break;			
		}
	}

}
