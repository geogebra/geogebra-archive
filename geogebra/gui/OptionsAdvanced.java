package geogebra.gui;

import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Advanced options for the options dialog.
 */
public class OptionsAdvanced  extends JPanel{
	/** */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Application object.
	 */
	private Application app;
	
	/** */
	private JLabel primLanguageLabel, secLanguageLabel;
	
	/** */
	private JComboBox primLanguageCb, secLanguageCb;
	
	/**	 */
	private JCheckBox secTooltipsCb, secKeyboardCb;

	/**
	 * Construct advanced option panel.
	 * 
	 * @param app
	 */
	public OptionsAdvanced(Application app) {
		super(new GridLayout(0, 1));
		
		this.app = app;
		
		initGUI();
		updateGUI();
	}
	
	/**
	 * Initialize the user interface.
	 * 
	 * @remark updateGUI() will be called directly after this method
	 * @remark Do not use translations here, the option dialog will take care of calling setLabels()
	 */
	private void initGUI() {
		initLanguagePanel();
	}
	
	/**
	 * Initialize the language panel to select primary and secondary language.
	 */
	private void initLanguagePanel() {
		JPanel languagePanel = new JPanel(new FlowLayout());
		languagePanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Language")));
		
		// primary language		
		String[] languages = new String[Application.supportedLocales.size()];
		String ggbLangCode;

		for (int i = 0; i < Application.supportedLocales.size(); i++) {
			Locale loc = (Locale) Application.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();

			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			languages[i] = (String) Application.specialLanguageNames.get(ggbLangCode);
			if (languages[i] == null)
				languages[i] = loc.getDisplayLanguage(Locale.ENGLISH);
		}
				
		primLanguageCb = new JComboBox(languages);
		
		primLanguageLabel = new JLabel();
		primLanguageLabel.setLabelFor(primLanguageCb);
		
		languagePanel.add(primLanguageLabel);
		languagePanel.add(primLanguageCb);
		
		languagePanel.add(Box.createHorizontalStrut(10));
		
		add(languagePanel);
		
		// secondary language		
		secLanguageCb = new JComboBox(languages);
		
		secLanguageLabel = new JLabel();
		secLanguageLabel.setLabelFor(secLanguageCb);
		
		languagePanel.add(secLanguageLabel);
		languagePanel.add(secLanguageCb);
		
		add(languagePanel);
		
		// Panel for secondary language		
		JPanel secLanguagePanel = new JPanel(new FlowLayout());
		secLanguagePanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("LanguageSecondary")));
		
		// use secondary language for keyboard?
		secKeyboardCb = new JCheckBox();
		secLanguagePanel.add(secKeyboardCb);
		
		languagePanel.add(Box.createHorizontalStrut(10));
		
		// use secondary language for tooltips?
		secTooltipsCb = new JCheckBox();
		secLanguagePanel.add(secTooltipsCb);

		add(secLanguagePanel);
	}
	
	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * @remark Do not call setLabels() here
	 */
	public void updateGUI() {
		
	}

	/**
	 * Update the language of the user interface.
	 */
	public void setLabels() {
		primLanguageLabel.setText(app.getMenu("Language"));
		secLanguageLabel.setText(app.getPlain("LanguageSecondary"));
		
		secTooltipsCb.setText(app.getPlain("TooltipsSecondary"));
		secKeyboardCb.setText(app.getPlain("KeyboardSecondary"));
	}

	/**
	 * Apply changes
	 */
	public void apply() {
		// TODO Auto-generated method stub
	}
}
