package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.Drawable;
import geogebra.kernel.GeoBoolean;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import geogebra.util.Util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

public class MyCellRenderer extends DefaultTableCellRenderer
{
	private static final long serialVersionUID = 1L;
	private Application app;
	private Kernel kernel;
	
	private ImageIcon latexIcon, emptyIcon; //G.Sturr 2010-1-15
	private String latexStr = new String();
	
	
	//G.Sturr 2010-4-4
	// vars for cell format
	private CellFormat formatHandler;
	private Point cellPoint;
	private Integer alignment = -1;
	private Integer traceBorder = -1;
	
	private Border cellPadding = BorderFactory.createEmptyBorder(2, 5, 2, 5);
	
	private Border bTop = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.RED);
	private Border bLeft = BorderFactory.createMatteBorder(0, 1, 0, 0, Color.RED);
	private Border bBottom = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.RED);
	private Border bRight = BorderFactory.createMatteBorder(0, 0, 0, 1, Color.RED);
	private Border bAll = BorderFactory.createMatteBorder(1, 1, 1, 1, Color.RED);

	
	private JCheckBox checkBox;
	private JButton button;
	private JComboBox comboBox;
	private DefaultComboBoxModel cbModel;
	
	//END G.Sturr
	
	
	public MyCellRenderer(Application app, CellFormat formatHandler) {
		
		this.app = app;		
		this.kernel = app.getKernel();
		this.formatHandler = formatHandler;
		
		//G.Sturr 2009-10-3:  add horizontal padding
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		
		//G.Sturr 2010-1-15
		// The cell renderer is an extension of JLabel and thus supports an icon.
		// Here the icon is used to display LaTeX.
		latexIcon = new ImageIcon();
		emptyIcon = new ImageIcon();
		cellPoint = new Point();
		checkBox = new JCheckBox();
		button = new JButton();
		comboBox = new JComboBox();
		comboBox.setRenderer(new MyListCellRenderer());
		cbModel = new DefaultComboBoxModel();
		comboBox.setModel(cbModel);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{	
		//this.setVerticalAlignment(JLabel.CENTER);
		cellPoint.setLocation(column, row);
		setIcon(emptyIcon);
		setBackground(table.getBackground());
		
		if (value == null) {		
			setText("");
			return this;
		}
				
		// set cell content
		GeoElement geo = (GeoElement)value;
		
		
		// use special rendering for buttons, booleans and lists
		if(((MyTable)table).testFlag && kernel.getAlgebraStyle()==Kernel.ALGEBRA_STYLE_VALUE){

			if(geo.isGeoBoolean()){
				checkBox.setBackground(table.getBackground());
				checkBox.setHorizontalAlignment(CENTER);
				if(geo.isLabelVisible())
					checkBox.setText(geo.getCaption());
				checkBox.setSelected(((GeoBoolean)geo).getBoolean());

				return checkBox;
			}


			if(geo.isGeoButton()){
				//button.setBackground(table.getBackground());
				button.setHorizontalAlignment(CENTER);
				button.setText(geo.getCaption());
				button.setForeground(geo.getObjectColor());
				return button;
			}

			if(geo.isGeoList()){
				GeoList list = (GeoList)geo;
				comboBox.setBackground(table.getBackground());
				cbModel.removeAllElements();
				if(list.size()>0)
					cbModel.addElement(list.get(list.getSelectedIndex()));
				//comboBox.setSelected(((GeoBoolean)geo).getBoolean());

				return comboBox;
			}
		}


		
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
		//setFont(app.getFontCanDisplay(text, Font.BOLD));
		setFont(app.getFontCanDisplay(text, Font.PLAIN));
		
		
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
		alignment = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_ALIGN);
		if (alignment != null) {
			setHorizontalAlignment(alignment);
		} else if (geo.isGeoText()) {
			setHorizontalAlignment(JLabel.LEFT);
		} else {
			setHorizontalAlignment(JLabel.RIGHT);
		}	
		
	
		// trace border
		traceBorder = (Integer) formatHandler.getCellFormat(cellPoint,
				CellFormat.FORMAT_TRACING);
		
		if (traceBorder != null){
			
			switch (traceBorder){
			case CellFormat.BORDER_ALL:
				setBorder(BorderFactory.createCompoundBorder(bAll, cellPadding));
			break;
			case CellFormat.BORDER_TOP:
				setBorder(BorderFactory.createCompoundBorder(bTop, cellPadding));
			break;
			case CellFormat.BORDER_LEFT:
				setBorder(BorderFactory.createCompoundBorder(bLeft, cellPadding));
			break;
			case CellFormat.BORDER_BOTTOM:
				setBorder(BorderFactory.createCompoundBorder(bBottom, cellPadding));
			break;
			case CellFormat.BORDER_RIGHT:
				setBorder(BorderFactory.createCompoundBorder(bRight, cellPadding));
			break;
			
			}
			
		}else{
			setBorder(cellPadding);	
		}
		
		
		
		// G.STURR 2010-1-17
		// set LaTeX icons
		// use LaTeX for any geo other than geoNumeric or non-latex geoText
		
		if (geo.isDefined() && kernel.getAlgebraStyle() == Kernel.ALGEBRA_STYLE_VALUE) {

			if ( !(geo.isGeoText() && !((GeoText) geo).isLaTeX())
					&& !geo.isGeoNumeric()) {
				try {
					latexStr = Util.toLaTeXString(geo.getFormulaString(
							ExpressionNode.STRING_TYPE_LATEX, true), false);

					drawLatexImageIcon(latexIcon, latexStr, getFont(), geo
							.getAlgebraColor(), bgColor);
					setIcon(latexIcon);
					setText("");
					
				} catch (Exception e) {
				}
			}
		}
		//END GSTURR
		
		return this;
	}

	
	
	/**
	 * Draw a LaTeX image in the cell icon. Drawing is done twice. First draw gives 
	 * the needed size of the image. Second draw renders the image with the correct
	 * dimensions.
	 */
	private void drawLatexImageIcon(ImageIcon latexIcon, String latex, Font font, Color fgColor, Color bgColor) {
		
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
		d = Drawable.drawEquation(app, g2image, 0, 0, latex, font, fgColor,
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
		d = Drawable.drawEquation(app, g2image, 0, 0, latex, font, fgColor,
				bgColor);

		latexIcon.setImage(image);
		
	}
	

	//======================================================
	//         ComboBox Cell Renderer 
	//======================================================
	
	/**
	 * Custom cell renderer that displays GeoElement descriptions.
	 */
	private class MyListCellRenderer extends DefaultListCellRenderer {
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean hasFocus) {

			//super.getListCellRendererComponent(list, value, index, isSelected,hasFocus);
			JLabel lbl = (JLabel)super.getListCellRendererComponent(
	                list, value, index, isSelected, hasFocus);
	        lbl.setHorizontalAlignment(LEFT);

			if (value != null) {
				GeoElement geo = (GeoElement) value;
				setText(geo.getLabel());
			} else
				setText(" ");
			
			return lbl;
		}

	}


}