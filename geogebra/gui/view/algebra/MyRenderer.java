package geogebra.gui.view.algebra;

import geogebra.euclidian.Drawable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

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
	

	
	private boolean useLaTeX = true;  //<============== flag for testing 
	
	private ImageIcon latexIcon;
	private String latexStr = null;
	
	
	
	public MyRenderer(Application app) {
		setOpaque(true);		
		this.app = app;
		this.kernel = app.getKernel();
		
		iconShown = app.getImageIcon("shown.gif");
		iconHidden = app.getImageIcon("hidden.gif");
		
		setOpenIcon(app.getImageIcon("tree-close.png"));
		setClosedIcon(app.getImageIcon("tree-open.png"));
		
		latexIcon = new ImageIcon();
		String laTextStr;
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

			if(useLaTeX  && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE){
				latexStr  = "\\," +  geo.getLabel() + "\\,=\\," +
					geo.getFormulaString(ExpressionNode.STRING_TYPE_LATEX, true);				
				drawLatexImageIcon(latexIcon, latexStr, app.getPlainFont(), false, Color.black, this.getBackground() );
				setIcon(joinIcons((ImageIcon) getIcon(),latexIcon));
				setText("");
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
	
	
	/**
	 * Draw a LaTeX image in the cell icon. Drawing is done twice. First draw gives 
	 * the needed size of the image. Second draw renders the image with the correct
	 * dimensions.
	 */
	private void drawLatexImageIcon(ImageIcon latexIcon, String latex, Font font, boolean serif, Color fgColor, Color bgColor) {
		
		// Create image with dummy size, then draw into it to get the correct size
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		Dimension d = new Dimension();
		d = Drawable.drawEquation(app, null, g2image, 0, 0, latex, font, serif, fgColor,
				bgColor);

		// Now use this size and draw again to get the final image
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		d = Drawable.drawEquation(app, null, g2image, 0, 0, latex, font, serif, fgColor,
				bgColor);

		latexIcon.setImage(image);
		
	}
	
	public ImageIcon joinIcons(ImageIcon leftIcon, ImageIcon rightIcon){
		
		int w1 = leftIcon.getIconWidth();
		int w2 = rightIcon.getIconWidth();
		int h1 = leftIcon.getIconHeight();
		int h2 = rightIcon.getIconHeight();
		int h = Math.max(h1, h2);
		int mid = h/2;
		BufferedImage image = new BufferedImage(w1+w2, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.drawImage(leftIcon.getImage(), 0, mid - h1/2, null);
		g2.drawImage(rightIcon.getImage(), w1,  mid - h2/2, null);
		g2.dispose(); 
		
		ImageIcon ic = new ImageIcon(image);
		return ic;
	}
	
	
} // MyRenderer