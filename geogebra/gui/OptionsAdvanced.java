package geogebra.gui;

import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;

/**
 * Advanced options for the options dialog.
 */
public class OptionsAdvanced  extends JPanel implements ActionListener {
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
	
	private String[] tooltipTimeouts = new String[] {
		"1",
		"3",
		"5",
		"10",
		"20",
		"30",
		"60",
		""
	};

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
		panel.setLayout(new FullWidthLayout());
		panel.add(virtualKeyboardPanel);
		panel.add(tooltipPanel);
		panel.add(languagePanel);
		panel.add(scriptingPanel);
		panel.add(Box.createVerticalGlue());
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		
		add(scrollPane, BorderLayout.CENTER);
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
		
		cbTooltipLanguage = new JComboBox();
		tooltipPanel.add(cbTooltipLanguage);
		
		tooltipTimeoutLabel = new JLabel();
		tooltipPanel.add(tooltipTimeoutLabel);
		
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
		
		updateKeyboardLanguages();
		updateTooltipLanguages();
		updateTooltipTimeouts();
	}
	
	/**
	 * Updates the keyboard languages, this is just necessary if the language 
	 * changed (or at startup). As we use an immutable list model we have to
	 * recreate the list all the time, even if we just change the label of the
	 * first item in the list.
	 */
	private void updateKeyboardLanguages() {
		String[] languages = new String[VirtualKeyboard.supportedLocales.size()+1];
		languages[0] = app.getPlain("Default");
		String ggbLangCode;

		for (int i = 0; i < VirtualKeyboard.supportedLocales.size(); i++) {
			Locale loc = (Locale) VirtualKeyboard.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();

			languages[i+1] = (String) Application.specialLanguageNames.get(ggbLangCode);
			if (languages[i+1] == null)
				languages[i+1] = loc.getDisplayLanguage(Locale.ENGLISH);
		}
		
		// take care that this doesn't fire events by accident 
		cbKeyboardLanguage.removeActionListener(this);
		cbKeyboardLanguage.setModel(new DefaultComboBoxModel(languages));
		cbKeyboardLanguage.addActionListener(this);
	}
	
	/**
	 * @see #updateKeyboardLanguages()
	 */
	private void updateTooltipLanguages() {
		String[] languages = new String[Application.supportedLocales.size()+1];
		languages[0] = app.getPlain("Default");
		String ggbLangCode;

		for (int i = 0; i < Application.supportedLocales.size(); i++) {
			Locale loc = (Locale) Application.supportedLocales.get(i);
			ggbLangCode = loc.getLanguage() + loc.getCountry()
					+ loc.getVariant();
			
			languages[i+1] = (String) Application.specialLanguageNames.get(ggbLangCode);
			if (languages[i+1] == null)
				languages[i+1] = loc.getDisplayLanguage(Locale.ENGLISH);
		}
		
		// take care that this doesn't fire events by accident 
		cbTooltipLanguage.removeActionListener(this);
		cbTooltipLanguage.setModel(new DefaultComboBoxModel(languages));
		cbTooltipLanguage.addActionListener(this);
	}
	
	/**
	 * @see #updateKeyboardLanguages() 
	 */
	private void updateTooltipTimeouts() {
		tooltipTimeouts[tooltipTimeouts.length-1] = app.getPlain("off");

		// take care that this doesn't fire events by accident 
		cbTooltipTimeout.removeActionListener(this);
		cbTooltipTimeout.setModel(new DefaultComboBoxModel(tooltipTimeouts));
		cbTooltipTimeout.addActionListener(this);
	}

	/**
	 * Values changed.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cbTooltipTimeout) {
			int index = cbTooltipTimeout.getSelectedIndex();
			int delay = Integer.MAX_VALUE;
			if (index < tooltipTimeouts.length - 1) {
				delay = 1000 * Integer.parseInt(tooltipTimeouts[index]);
			}
			ToolTipManager.sharedInstance().setDismissDelay(delay);
			Application.debug(delay);

		} else if (e.getSource() == cbTooltipLanguage) {
			int index = cbTooltipLanguage.getSelectedIndex() - 1;
			if (index == -1) app.setTooltipLanguage(null);
			else app.setTooltipLanguage(Application.supportedLocales.get(index));
		}
	}
}
