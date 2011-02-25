package geogebra.gui;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

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
	private JPanel virtualKeyboardPanel, tooltipPanel, languagePanel, scriptingPanel;
	
	/**	*/
	private JLabel keyboardLanguageLabel, widthLabel, heightLabel, tooltipLanguageLabel, tooltipTimeoutLabel;
	
	/** */
	private JComboBox cbKeyboardLanguage, cbTooltipLanguage, cbTooltipTimeout;
	
	/**	 */
	private JCheckBox cbKeyboardShowAutomatic, cbUseLocalDigits, cbUseLocalPointNames, cbEnableScripting;
	
	/** */
	private JTextField tfKeyboardWidth, tfKeyboardHeight;

	/**
	 * Construct advanced option panel.
	 * 
	 * @param app
	 */
	public OptionsAdvanced(Application app) {
		super(new BorderLayout());
		
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
		initVirtualKeyboardPanel();
		initTooltipPanel();
		initLanguagePanel();
		initScriptingPanel();

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(virtualKeyboardPanel);
		panel.add(tooltipPanel);
		panel.add(languagePanel);
		panel.add(scriptingPanel);
		panel.add(Box.createVerticalGlue());
		
		add(panel, BorderLayout.CENTER);
	}
	
	/**
	 * Initialize the virtual keyboard panel
	 */
	private void initVirtualKeyboardPanel() {
		virtualKeyboardPanel = new JPanel();
		virtualKeyboardPanel.setLayout(new BoxLayout(virtualKeyboardPanel, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		keyboardLanguageLabel = new JLabel();
		panel.add(keyboardLanguageLabel);
		
		// TODO gather keyboard languages
		cbKeyboardLanguage = new JComboBox();
		panel.add(cbKeyboardLanguage);
		
		virtualKeyboardPanel.add(panel, BorderLayout.NORTH);
		
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		widthLabel = new JLabel();
		panel.add(widthLabel);
		
		tfKeyboardWidth = new JTextField(3);
		panel.add(tfKeyboardWidth);
		
		panel.add(new JLabel("px"));
		
		panel.add(Box.createHorizontalStrut(10));
		
		heightLabel = new JLabel();
		panel.add(heightLabel);
		
		tfKeyboardHeight = new JTextField(3);
		panel.add(tfKeyboardHeight);
		
		panel.add(new JLabel("px"));
		
		panel.add(Box.createHorizontalStrut(10));
		
		cbKeyboardShowAutomatic = new JCheckBox();
		panel.add(cbKeyboardShowAutomatic);
		
		virtualKeyboardPanel.add(panel, BorderLayout.CENTER);
	}
	
	/**
	 * Initialize the language panel.
	 */
	private void initLanguagePanel() {
		languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		cbUseLocalDigits = new JCheckBox();
		languagePanel.add(cbUseLocalDigits);
		
		cbUseLocalPointNames = new JCheckBox();
		languagePanel.add(cbUseLocalPointNames);
	}
	
	/**
	 * Initialize the tooltip panel.
	 */
	private void initTooltipPanel() {
		tooltipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));	
		
		tooltipLanguageLabel = new JLabel();
		tooltipPanel.add(tooltipLanguageLabel);
		
		// tooltip language		
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
		
		cbTooltipLanguage = new JComboBox(languages);
		tooltipPanel.add(cbTooltipLanguage);
		
		tooltipTimeoutLabel = new JLabel();
		tooltipPanel.add(tooltipTimeoutLabel);
		
		// TODO construct timeouts
		cbTooltipTimeout = new JComboBox();
		tooltipPanel.add(cbTooltipTimeout);
	}
	
	/**
	 * Initialize the scripting panel.
	 */
	private void initScriptingPanel() {
		scriptingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		cbEnableScripting = new JCheckBox();
		scriptingPanel.add(cbEnableScripting);
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
		virtualKeyboardPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("VirtualKeyboard")));
		tooltipPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Tooltips")));
		languagePanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Language")));
		scriptingPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Scripting")));
		
		keyboardLanguageLabel.setText(app.getPlain("VirtualKeyboardLanguage")+":");
		widthLabel.setText(app.getPlain("Width")+":");
		heightLabel.setText(app.getPlain("Height")+":");
		cbKeyboardShowAutomatic.setText(app.getPlain("ShowAutomatically"));

		tooltipLanguageLabel.setText(app.getPlain("TooltipLanguage")+":");
		tooltipTimeoutLabel.setText(app.getPlain("TooltipTimeout")+":");
		
		cbUseLocalDigits.setText(app.getPlain("LocalizedDigits"));
		cbUseLocalPointNames.setText(app.getPlain("LocalizedPointNames"));
		
		cbEnableScripting.setText(app.getPlain("EnableScripting"));
		
		// TODO translate special combo box fields (languages: default, tooltip timeout: off)
	}

	/**
	 * Apply changes
	 */
	public void apply() {
		// TODO Auto-generated method stub
	}
}
