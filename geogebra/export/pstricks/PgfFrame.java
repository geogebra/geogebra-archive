package geogebra.export.pstricks;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PgfFrame extends ExportFrame{
	private static final long serialVersionUID = 1L;
	final String[] compiler={"Latex","Context"};
	private JComboBox comboCompiler;
	public PgfFrame(final GeoGebraToPgf ggb2pgf){
		super(ggb2pgf,"GeneratePgf");
		initGui();
	}
	protected void initGui(){ 
		js.getViewport().add(textarea);
		setTitle(app.getPlain("TitleExportPgf"));
		panel.setLayout(new GridBagLayout());
		panel.add(labelXUnit, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textXUnit, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelwidth, new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textwidth, new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelYUnit, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textYUnit, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelheight, new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(textheight, new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(labelFontSize, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(comboFontSize, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(jcb, new GridBagConstraints(2, 4, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(button, new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(button_copy, new GridBagConstraints(3, 5, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(
				5, 5, 5, 5), 0, 0));
		panel.add(js, new GridBagConstraints(0, 6, 4, 5, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
				5, 5, 5, 5), 0, 0));
		textXUnit.setPreferredSize(new Dimension(110,textXUnit.getFont().getSize()+6));
		textYUnit.setPreferredSize(new Dimension(110,textYUnit.getFont().getSize()+6));
		js.setPreferredSize(new Dimension(400,400));
		getContentPane().add(panel);
		centerOnScreen();
		setVisible(true);
	}
}
