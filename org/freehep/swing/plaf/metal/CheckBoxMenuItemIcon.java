// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.swing.plaf.metal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.Serializable;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.freehep.swing.JTriStateMenuItem;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: CheckBoxMenuItemIcon.java,v 1.1 2008-02-25 21:18:32 murkle Exp $
 */
public class CheckBoxMenuItemIcon implements Icon, UIResource, Serializable {

    static private final Dimension menuCheckIconSize = new Dimension( 10, 10 );
	
	public void paintIcon( Component c, Graphics g, int x, int y ) {
	    JTriStateMenuItem b = (JTriStateMenuItem) c;
	    ButtonModel model = b.getModel();

	    int state = b.getTriState();
	    boolean isSelected = model.isSelected();
	    boolean isEnabled = model.isEnabled();
	    boolean isPressed = model.isPressed();
	    boolean isArmed = model.isArmed();

	    g.translate( x, y );

	    if ( isEnabled ) {
	        if ( isPressed || isArmed ) {
    		    g.setColor( MetalLookAndFeel.getControlInfo()  );
    		    g.drawLine( 0, 0, 8, 0 );
    		    g.drawLine( 0, 0, 0, 8 );
    		    g.drawLine( 8, 2, 8, 8 );
    		    g.drawLine( 2, 8, 8, 8 );
    
    		    g.setColor( MetalLookAndFeel.getPrimaryControl()  );
    		    g.drawLine( 1, 1, 7, 1 );
    		    g.drawLine( 1, 1, 1, 7 );
    		    g.drawLine( 9, 1, 9, 9 );
    		    g.drawLine( 1, 9, 9, 9 );
		    } else {
    		    g.setColor( MetalLookAndFeel.getControlDarkShadow()  );
    		    g.drawLine( 0, 0, 8, 0 );
    		    g.drawLine( 0, 0, 0, 8 );
    		    g.drawLine( 8, 2, 8, 8 );
    		    g.drawLine( 2, 8, 8, 8 );
    
    		    g.setColor( MetalLookAndFeel.getControlHighlight()  );
    		    g.drawLine( 1, 1, 7, 1 );
    		    g.drawLine( 1, 1, 1, 7 );
    		    g.drawLine( 9, 1, 9, 9 );
    		    g.drawLine( 1, 9, 9, 9 );
    		}
	    } else {
	        g.setColor( MetalLookAndFeel.getMenuDisabledForeground()  );
		    g.drawRect( 0, 0, 8, 8 );
	    }

	    if ( isSelected ) {
	        if ( isEnabled ) {
                if (state == -1) {
                    g.setColor( MetalLookAndFeel.getControlShadow() );
    	            g.fillRect( 1, 1, 8, 8);
                }
                
		        if ( model.isArmed() || ( c instanceof JMenu && model.isSelected() ) ) {
		            g.setColor( MetalLookAndFeel.getMenuSelectedForeground() );
		        } else {

		            g.setColor( b.getForeground() );
		        }
		    } else {
		        g.setColor( MetalLookAndFeel.getMenuDisabledForeground()  );
		    }

		    g.drawLine( 2, 2, 2, 6 );
		    g.drawLine( 3, 2, 3, 6 );
		    g.drawLine( 4, 4, 8, 0 );
		    g.drawLine( 4, 5, 9, 0 );
	    }
	    g.translate( -x, -y );
	}

	public int getIconWidth() { 
	    return menuCheckIconSize.width; 
	}

	public int getIconHeight() { 
	    return menuCheckIconSize.height; 
	}

} 