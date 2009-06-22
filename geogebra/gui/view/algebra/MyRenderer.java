package geogebra.gui.view.algebra;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Algebra view cell renderer 
 * @author Markus
 */
public class MyRenderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = 1L;				
			
	private Application app;
	private ImageIcon iconShown, iconHidden;
		
	public MyRenderer(Application app) {
		setOpaque(true);		
		this.app = app;
		
		iconShown = app.getImageIcon("shown.gif");
		iconHidden = app.getImageIcon("hidden.gif");
	}
	
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean selected,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus) {	
					
		//Application.debug("getTreeCellRendererComponent: " + value);
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;			
		Object ob = node.getUserObject();
					
		if (ob instanceof GeoElement) {	
			GeoElement geo = (GeoElement) ob;										
			
			setFont(app.boldFont);
			setForeground(geo.getAlgebraColor());
			String str = geo.getAlgebraDescriptionTextOrHTML();
			//String str = geo.getAlgebraDescription();
			setText(str);								
			
			if (geo.doHighlighting())				   
				setBackground(Application.COLOR_SELECTION);
			else 
				setBackground(getBackgroundNonSelectionColor());
							
			// ICONS               
			if (geo.isEuclidianVisible()) {
				setIcon(iconShown);
			} else {
				setIcon(iconHidden);
			}
			
//				 TODO: LaTeX in AlgebraView
			//if (geo.isGeoFunction()) {
				
		//	}
			
			
			/*// HIGHLIGHTING
			if (geo.highlight) {
				//setBorder(BorderFactory.createLineBorder(geo.selColor));
				setBackground(geo.selColor);
			} else {
				//setBorder(null);
			}*/
		}								
		//	no leaf (no GeoElement)
		else { 
			if (expanded) {
				setIcon(getOpenIcon());
			} else {
				setIcon(getClosedIcon());
			}
			setForeground(Color.black);
			setBackground(getBackgroundNonSelectionColor());
			setFont(app.plainFont);
			selected = false;				
			setBorder(null);
			setText(value.toString());
		}		
		
		return this;
	}							
	
	/*
	final public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);                                        
		 g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);    
		 super.paint(g);
	}
	*/	
	
} // MyRenderer