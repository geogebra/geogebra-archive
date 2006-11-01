package geogebra.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import geogebra.kernel.GeoSegment;


/**
 * @author Le Coq Loïc
 * 30/10/2006
 * This class defines the renderer for the ComboBox where
 * the user chooses the decoration for GeoSegment
 * 
 */
public class DecorationListRenderer extends JPanel implements ListCellRenderer {
    	int id=0;
    	public DecorationListRenderer() {
    		setOpaque(true);
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
//    				setForeground(list.getSelectionForeground());
    			} else {
    				setBackground(list.getBackground());
  //  				setForeground(list.getForeground());
    			}
    			
    			setBorder(BorderFactory.createEmptyBorder(2,2,2,2));	       
    			return this;
    	}
    	public void paint(Graphics g){
    		super.paint(g);
    		
    		g.setColor(getBackground());
    		g.fillRect(0,0,getWidth(),getHeight());
    		g.setColor(Color.BLACK);
    		int mid = getHeight() / 2;
    		g.drawLine(0, mid, getWidth(), mid);

    		switch(id){
    			case GeoSegment.DECORATION_NONE:
    			break;
    			case GeoSegment.DECORATION_SEGMENT_ONE_TICK:
    	    		int quart=mid/2;
    				int mid_width=getWidth()/2;
    				g.drawLine(mid_width,quart,mid_width,mid+quart);
    			break;
    			case GeoSegment.DECORATION_SEGMENT_TWO_TICKS:
    				quart=mid/2;
					mid_width=getWidth()/2;
					g.drawLine(mid_width-1,quart,mid_width-1,mid+quart);    				
					g.drawLine(mid_width+2,quart,mid_width+2,mid+quart);
    			break;
    			case GeoSegment.DECORATION_SEGMENT_THREE_TICKS:
    	    		quart=mid/2;
					mid_width=getWidth()/2;
					g.drawLine(mid_width,quart,mid_width,mid+quart);
					g.drawLine(mid_width+3,quart,mid_width+3,mid+quart);
					g.drawLine(mid_width-3,quart,mid_width-3,mid+quart);
   				break;
    		}		
    	}
    }
