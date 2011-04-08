package geogebra.gui.view.algebra;

import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
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
			
	protected Application app;
	private Kernel kernel;
	private ImageIcon iconShown, iconHidden;
		
	public MyRenderer(Application app) {
		setOpaque(true);		
		this.app = app;
		this.kernel = app.getKernel();
		
		iconShown = app.getImageIcon("shown.gif");
		iconHidden = app.getImageIcon("hidden.gif");
		
		setOpenIcon(app.getImageIcon("tree-close.png"));
		setClosedIcon(app.getImageIcon("tree-open.png"));
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
				text = getAlgebraDescriptionTextOrHTML(geo);
			} else {
				switch (kernel.getAlgebraStyle()) {
					case Kernel.ALGEBRA_STYLE_VALUE:
						text = getAlgebraDescriptionTextOrHTML(geo);
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
			
			// sometimes objects do not identify themselves as GeoElement for a second,
			// causing the else-part to give them a border (because they have no children)
			// we have to remove this border to prevent an unnecessary indent
			setBorder(null);
			
			// TODO: LaTeX in AlgebraView
		}								
		// no GeoElement
		else {			
			// has children, display icon to expand / collapse the node
			if(!node.isLeaf()) {
				if (expanded) {
					setIcon(getOpenIcon());
				} else {
					setIcon(getClosedIcon());
				}
				
				setBorder(null);
			}
			
			// no children, display no icon
			else {
				// align all elements, therefore add the space the icon would normally take as a padding 
				setBorder(BorderFactory.createEmptyBorder(0, getOpenIcon().getIconWidth() + getIconTextGap(), 0, 0));
				setIcon(null);
			}
			
			setForeground(Color.black);
			setBackground(getBackgroundNonSelectionColor());
			String str = value.toString();
			setText(str);
			
			// make sure we use a font that can display the text
			setFont(app.getFontCanDisplay(str));
		}		
		
		return this;
	}
	
	/**
	 * 
	 * @param geo
	 * @return algebra description of the geo
	 */
	protected String getAlgebraDescriptionTextOrHTML(GeoElement geo){
		return geo.getAlgebraDescriptionTextOrHTML();
	}
	
	
} // MyRenderer