package geogebra.gui.view.algebra;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

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
	private Kernel kernel;
	private ImageIcon iconShown, iconHidden;
		
	public MyRenderer(Application app) {
		setOpaque(true);		
		this.app = app;
		this.kernel = app.getKernel();
		
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
			setForeground(geo.getAlgebraColor());
			
			String text = null;
			if (geo.isIndependent()) {
				text = geo.getAlgebraDescriptionTextOrHTML();
			} else {
				switch (kernel.getAlgebraStyle()) {
					case Kernel.ALGEBRA_STYLE_VALUE:
						text = geo.getAlgebraDescriptionTextOrHTML();
						break;
						
					case Kernel.ALGEBRA_STYLE_DEFINITION:
						text = geo.addLabelTextOrHTML(geo.getDefinitionDescription());
						break;
						
					case Kernel.ALGEBRA_STYLE_COMMAND:
						text = geo.addLabelTextOrHTML(geo.getCommandDescription());
						break;
				}	
			}

			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplay(text, Font.BOLD));
			setText(text);
			
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
			selected = false;				
			setBorder(null);
			String str = value.toString();
			setText(str);
			
			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplay(str));
		}		
		
		return this;
	}							
	
//	
//	final public void paint(Graphics g) {
//		Graphics2D g2 = (Graphics2D) g;
//		 g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);                                        
//		 g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
//                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		 
//		 //Font f = app.getFontCanDisplay(getText(), Font.BOLD);
//		 Font f = new Font("SansSerif", Font.BOLD, 12);
//		 //UIManager.put("Tree.font", f);
//		 //g2.setFont(f);
//		 setFont(f);
//		 super.paint(g);
//		// UIManager.put("Tree.font", app.getBoldFont());
//			
//		 
//		 //TODO: remove
//		 System.out.println("paint renderer with font: " + getText() + ", "+ f);
//	}
	
	
} // MyRenderer