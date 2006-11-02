package geogebra.gui;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoSegment;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

/**
 * @author loic
 * date 31/10/2006
 * This class defines the renderer for the ComboBox where
 * the user chooses the decoration for GeoAngle
 * 
 * 
 * 
 * */
public class DecorationAngleListRenderer extends JPanel implements ListCellRenderer {
 	int id=0;
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
	public void paint(Graphics g){
		super.paint(g);
		
		// added by Markus Hohenwarter, BEGIN
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
		// added by Markus Hohenwarter, END
		
		g.setColor(getBackground());
		g.fillRect(0,0,getWidth(),getHeight());
		g.setColor(Color.BLACK);
		g.drawLine(13,27, 67, 27);
		g.drawLine(13,27,67,3);
		g.drawArc(-27,-13,80,80,0,24);
		switch(id){
			case GeoElement.DECORATION_NONE:
				
			break;
			case GeoElement.DECORATION_ANGLE_TWO_ARCS:
				g.drawArc(-22,-8,70,70,0,24);
			break;
			case GeoElement.DECORATION_ANGLE_THREE_ARCS:
				g.drawArc(-22,-8,70,70,0,24);
				g.drawArc(-32,-18,90,90,0,24);
			break;
			case GeoElement.DECORATION_ANGLE_ONE_TICK:
				g.drawLine(49,20,57,18);
				break;
			case GeoElement.DECORATION_ANGLE_TWO_TICKS:
				g.drawLine(49,21,58,20);
				g.drawLine(49,17,58,15);

			break;
			case GeoElement.DECORATION_ANGLE_THREE_TICKS:
				g.drawLine(49,20,59,17);
				g.drawLine(50,23,59,21);
				g.drawLine(49,17,57,14);
			break;			
		}
	}

}
