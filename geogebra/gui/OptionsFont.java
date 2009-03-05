package geogebra.gui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import geogebra.gui.util.SpringUtilities;
import geogebra.main.Application;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 * Options for font sizes & language.
 * 
 * @author Florian Sonner
 */
class OptionsFont extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private Application app;
	
	private JPanel fontSizePanel;
	
	private JLabel guiSizeLabel, axesSizeLabel, euclidianSizeLabel;
	private JComboBox guiSizeCb, axesSizeCb, euclidianSizeCb;
	private boolean updateFonts = false;
	
	private JPanel languagePanel;
	private JComboBox languageCb;
	private boolean updateLanguage;
	
	public OptionsFont(Application app) {
		this.app = app;
		
		initGUI();
		updateGUI();
	}
	
	/**
	 * Initialize the GUI.
	 */
	private void initGUI() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		String[] fontSizeStr = new String[] { "10", "12", "14", "16", "18", "20", "24", "28", "32" };
		
		// font size of GUI
		guiSizeCb = new JComboBox(fontSizeStr);
		guiSizeCb.setSelectedItem(Integer.toString(app.getFontSize()));
		guiSizeCb.addActionListener(this);
		guiSizeLabel = new JLabel();
		
		// font size of drawing pad
		euclidianSizeCb = new JComboBox(fontSizeStr);
		euclidianSizeCb.setSelectedItem(Integer.toString(app.getEuclidianFontSize()));
		euclidianSizeCb.addActionListener(this);
		euclidianSizeLabel = new JLabel();
		
		// font size of coordinate system 
		axesSizeCb = new JComboBox(fontSizeStr);
		axesSizeCb.setSelectedItem(Integer.toString(app.getAxesFontSize()));
		axesSizeCb.addActionListener(this);
		axesSizeLabel = new JLabel();
		
		// construct the font size panel
		fontSizePanel = new JPanel(new SpringLayout());
		fontSizePanel.add(guiSizeLabel);
		fontSizePanel.add(guiSizeCb);
		fontSizePanel.add(euclidianSizeLabel);
		fontSizePanel.add(euclidianSizeCb);
		fontSizePanel.add(axesSizeLabel);
		fontSizePanel.add(axesSizeCb);
		
		SpringUtilities.makeCompactGrid(fontSizePanel,
				3, 2,
				3, 3, 
				15, 5);
		
		add(fontSizePanel);
		
		// language panel
		String[] languages = new String[Application.supportedLocales.size()];
		String ggbLangCode;
		
		int selectedLanguage = 0;
		
		for (int i = 0; i < Application.supportedLocales.size(); i++) {
			Locale loc = (Locale) Application.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();
	
			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			languages[i] = (String) Application.specialLanguageNames.get(ggbLangCode);
			if (languages[i] == null)
				languages[i] = loc.getDisplayLanguage(Locale.ENGLISH);
			
			if(loc == app.getLocale())
				selectedLanguage = i;
		}
		
		languageCb = new JComboBox(languages);
		languageCb.setSelectedIndex(selectedLanguage);
		languageCb.addActionListener(this);
		
		languagePanel = new JPanel(new FlowLayout());
		languagePanel.add(languageCb);
		
		add(languagePanel);
		
		setLabels();
	}
	
	/**
	 * Update all labels.
	 */
	public void setLabels() {
		fontSizePanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("FontSize")));
		
		guiSizeLabel.setText(app.getPlain("FontSizeGUI"));
		euclidianSizeLabel.setText(app.getPlain("FontSizeEuclidian"));
		axesSizeLabel.setText(app.getPlain("FontSizeAxes"));
		
		languagePanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Language")));
	}
	
	/**
	 * Update the GUI.
	 */
	public void updateGUI() {		
		guiSizeCb.removeActionListener(this);
		euclidianSizeCb.removeActionListener(this);
		axesSizeCb.removeActionListener(this);
		
		guiSizeCb.setSelectedItem(Integer.toString(app.getFontSize()));
		euclidianSizeCb.setSelectedItem(Integer.toString(app.getEuclidianFontSize()));
		axesSizeCb.setSelectedItem(Integer.toString(app.getAxesFontSize()));
		
		guiSizeCb.addActionListener(this);
		euclidianSizeCb.addActionListener(this);
		axesSizeCb.addActionListener(this);
		
		for (int i = 0; i < Application.supportedLocales.size(); i++) {
			if(app.getLocale() == (Locale) Application.supportedLocales.get(i)) {
				languageCb.setSelectedIndex(i);
				break;
			}
		}
	}
	
	/**
	 * Apply the options.
	 */
	public void apply() {
		if(updateFonts) {
			app.resetFonts();
			updateFonts = false;
		}
		
		if(updateLanguage) {
			Locale loc = (Locale) Application.supportedLocales.get(languageCb.getSelectedIndex());
			app.setLanguage(loc);
			updateLanguage = false;
		}
	}
	
	/**
	 * A list item from the font list was selected.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == guiSizeCb) {
			int fontSize = Integer.parseInt((String)guiSizeCb.getSelectedItem());
			app.setGUIFontSize(fontSize, false);
			updateFonts = true;
		} else if(e.getSource() == axesSizeCb) {
			int fontSize = Integer.parseInt((String)axesSizeCb.getSelectedItem());
			app.setAxesFontSize(fontSize, false);
			updateFonts = true;
		} else if(e.getSource() == euclidianSizeCb) {
			int fontSize = Integer.parseInt((String)euclidianSizeCb.getSelectedItem());
			app.setEuclidianFontSize(fontSize, false);
			updateFonts = true;
		} else if(e.getSource() == languageCb) {
			updateLanguage = true;
		}
	}
}
