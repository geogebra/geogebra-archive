package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.Drawable;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
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
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MyCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	private Application app;
	private Kernel kernel;
	
	private ImageIcon latexIcon, emptyIcon; //G.Sturr 2010-1-15

	public MyCellRenderer(Application app) {
		this.app = app;		
		kernel = app.getKernel();
		
		//G.Sturr 2009-10-3:  add horizontal padding
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		
		//G.Sturr 2010-1-15: icons for displaying LaTeX 
		latexIcon = new ImageIcon();
		emptyIcon = new ImageIcon();
		
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{	
		setIcon(emptyIcon);
		setBackground(table.getBackground());
		
		if (value == null) {		
			setText("");
			return this;
		}
				
		// set cell content
		GeoElement geo = (GeoElement)value;
		String text = null;
		if (geo.isIndependent()) {
			text = geo.toValueString();
		} else {
			switch (kernel.getAlgebraStyle()) {
				case Kernel.ALGEBRA_STYLE_VALUE:
					text = geo.toValueString();
					break;
					
				case Kernel.ALGEBRA_STYLE_DEFINITION:
					text = GeoElement.convertIndicesToHTML(geo.getDefinitionDescription());
					break;
					
				case Kernel.ALGEBRA_STYLE_COMMAND:
					text = GeoElement.convertIndicesToHTML(geo.getCommandDescription());
					break;
			}	
		}

		// make sure that we use a font that can display the cell content
		setText(text);
		setFont(app.getFontCanDisplay(text, Font.BOLD));
		
		// foreground and background color
		
		/*
		setForeground(geo.getAlgebraColor());
		if (isSelected || geo.doHighlighting()) {
			setBackground(MyTable.SELECTED_BACKGROUND_COLOR);
		}		
		*/
		
		Color bgColor = table.getBackground();
		if (geo.doHighlighting()) {
			bgColor = MyTable.SELECTED_BACKGROUND_COLOR;
		}
		setBackground(bgColor);
		setForeground(geo.getAlgebraColor());
		
		
		// horizontal alignment
		if (geo.isGeoText()) {
			setHorizontalAlignment(JLabel.LEFT);
		} else {
			setHorizontalAlignment(JLabel.RIGHT);
		}		
		
		//G.STURR 2010-1-15
		// The cell renderer is an extension of JLabel and thus supports an icon.
		// This icon is used to display LaTeX. 
		if(geo.isGeoText()){
			if(((GeoText) geo).isLaTeX()) {
				setText("");
				drawLatexImageIcon( latexIcon,  geo, bgColor, getFont());
				setIcon(latexIcon);
			}
		}
		//END GSTURR
		
		
		return this;
	}

	public void drawLatexImageIcon(ImageIcon latexIcon, GeoElement geo,  Color bgColor, Font font) {
		
		BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2image = image.createGraphics();
		
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
							RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
							RenderingHints.VALUE_ANTIALIAS_ON);
		
		// draw the LaTeX image just to get its size
		Dimension d = new Dimension();
		d = Drawable.drawEquation(app, g2image, 0, 0, geo.toValueString(), font, geo.getAlgebraColor(), bgColor);
		
		// now use this size and draw again to get the final image
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		g2image = image.createGraphics();
		g2image.setBackground(bgColor);
		g2image.clearRect(0, 0, image.getWidth(), image.getHeight());
		g2image.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		d = Drawable.drawEquation(app, g2image, 0, 0, geo.toValueString(), getFont(), geo.getAlgebraColor(), bgColor);
		
		latexIcon.setImage(image);
		
	}
	
	
	

}