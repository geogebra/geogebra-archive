package geogebra.gui;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.plaf.LabelUI;

public class TitleLabel extends JLabel {
	/**	 */
	private static final long serialVersionUID = 1L;
	
	public TitleLabel() {
		super();
		
		// TODO Use UI as updating font size doesn't work with this.. (F.S.)
		Font font = getFont();
		setFont(font.deriveFont(Font.BOLD));
	}
}
