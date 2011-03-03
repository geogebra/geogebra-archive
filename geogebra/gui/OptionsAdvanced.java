package geogebra.gui;

import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.gui.layout.Layout;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Advanced options for the options dialog.
 */
public class OptionsAdvanced  extends JPanel implements ActionListener, ChangeListener, FocusListener {
	/** */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Application object.
	 */
	private Application app;

	/** */
	private JPanel virtualKeyboardPanel, tooltipPanel, languagePanel, anglePanel,  perspectivesPanel, scriptingPanel;
	
	/**	*/
	private JLabel keyboardLanguageLabel, widthLabel, heightLabel, opacityLabel, tooltipLanguageLabel, tooltipTimeoutLabel;
	
	/** */
	private JComboBox cbKeyboardLanguage, cbTooltipLanguage, cbTooltipTimeout;
	
	/**	 */
	private JCheckBox cbKeyboardShowAutomatic, cbUseLocalDigits, cbUseLocalLabels, cbReturnAngleInverseTrig, cbIgnoreDocumentLayout, cbShowTitleBar, cbEnableScripting;
	
	/** */
	private JTextField tfKeyboardWidth, tfKeyboardHeight;
	
	/** */
	private JSlider slOpacity;
	
	/** */
	private JButton managePerspectivesButton;
	
	/**
	 * Timeout values of tooltips (last entry reserved for "Off", but that has to be translated)
	 */
	private String[] tooltipTimeouts = new String[] {
		"1",
		"3",
		"5",
		"10",
		"20",
		"30",
		"60",
		"-"
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
		initAnglePanel();
		initPerspectivesPanel();
		initScriptingPanel();

		JPanel panel = new JPanel();
		panel.setLayout(new FullWidthLayout());
		panel.add(virtualKeyboardPanel);
		panel.add(tooltipPanel);
		panel.add(languagePanel);
		panel.add(anglePanel);
		panel.add(perspectivesPanel);
		panel.add(scriptingPanel);
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(20);
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
		// listener to this combo box is added in setLabels()
		panel.add(cbKeyboardLanguage);
		
		virtualKeyboardPanel.add(panel, BorderLayout.NORTH);
		
		panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		widthLabel = new JLabel();
		panel.add(widthLabel);
		
		tfKeyboardWidth = new JTextField(3);
		tfKeyboardWidth.addFocusListener(this);
		panel.add(tfKeyboardWidth);
		
		panel.add(new JLabel("px"));
		
		panel.add(Box.createHorizontalStrut(10));
		
		heightLabel = new JLabel();
		panel.add(heightLabel);
		
		tfKeyboardHeight = new JTextField(3);
		tfKeyboardHeight.addFocusListener(this);
		panel.add(tfKeyboardHeight);
		
		panel.add(new JLabel("px"));
		
		panel.add(Box.createHorizontalStrut(10));
		
		cbKeyboardShowAutomatic = new JCheckBox();
		panel.add(cbKeyboardShowAutomatic);
		
		opacityLabel = new JLabel();
		panel.add(opacityLabel);
		
		slOpacity = new JSlider(25, 100);
		slOpacity.setPreferredSize(new Dimension(100, (int)slOpacity.getPreferredSize().getHeight()));
		// listener added in updateGUI()
		panel.add(slOpacity);
		
		opacityLabel.setLabelFor(slOpacity);
		
		virtualKeyboardPanel.add(panel, BorderLayout.CENTER);
	}
	
	/**
	 * Initialize the language panel.
	 */
	private void initLanguagePanel() {
		languagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		cbUseLocalDigits = new JCheckBox();
		cbUseLocalDigits.addActionListener(this);
		languagePanel.add(cbUseLocalDigits);
		
		cbUseLocalLabels = new JCheckBox();
		cbUseLocalLabels.addActionListener(this);
		languagePanel.add(cbUseLocalLabels);
	}
	
	/**
	 * Initialize the tooltip panel.
	 */
	private void initTooltipPanel() {
		tooltipPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));	
		
		tooltipLanguageLabel = new JLabel();
		tooltipPanel.add(tooltipLanguageLabel);
		
		cbTooltipLanguage = new JComboBox();
		// listener to this combo box is added in setLabels()
		tooltipPanel.add(cbTooltipLanguage);
		
		tooltipTimeoutLabel = new JLabel();
		tooltipPanel.add(tooltipTimeoutLabel);
		
		cbTooltipTimeout = new JComboBox(tooltipTimeouts);
		tooltipPanel.add(cbTooltipTimeout);
	}
	
	/**
	 * Initialize the angle panel.
	 */
	private void initAnglePanel() {
		anglePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		cbReturnAngleInverseTrig = new JCheckBox();
		cbReturnAngleInverseTrig.addActionListener(this);
		anglePanel.add(cbReturnAngleInverseTrig);
	}
	
	/**
	 * Initialize the perspectives panel.
	 */
	private void initPerspectivesPanel() {
		perspectivesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		cbShowTitleBar = new JCheckBox();
		cbShowTitleBar.addActionListener(this);
		perspectivesPanel.add(cbShowTitleBar);

		cbIgnoreDocumentLayout = new JCheckBox();
		cbIgnoreDocumentLayout.addActionListener(this);
		perspectivesPanel.add(cbIgnoreDocumentLayout);
		
		managePerspectivesButton = new JButton();
		managePerspectivesButton.addActionListener(this);
		perspectivesPanel.add(managePerspectivesButton);
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
		cbUseLocalDigits.setSelected(app.isUsingLocalizedDigits());
		cbUseLocalLabels.setSelected(app.isUsingLocalizedLabels());
		
		Layout layout = app.getGuiManager().getLayout();
		cbIgnoreDocumentLayout.setSelected(layout.isIgnoringDocument());
		cbShowTitleBar.setSelected(layout.isTitleBarVisible());
		
		VirtualKeyboard virtualKeyboard = app.getGuiManager().getVirtualKeyboard();
		tfKeyboardWidth.setText(Integer.toString(virtualKeyboard.getWidth()));
		tfKeyboardHeight.setText(Integer.toString(virtualKeyboard.getHeight()));
		
		slOpacity.removeChangeListener(this);
		slOpacity.setValue((int)(virtualKeyboard.getOpacity() * 100));
		slOpacity.addChangeListener(this);
		
		// tooltip timeout
		int timeoutIndex = -1;
		int currentTimeout = ToolTipManager.sharedInstance().getDismissDelay();
		
		// search for combobox index
		for(int i = 0; i < tooltipTimeouts.length-1; ++i) {
			if(Integer.parseInt(tooltipTimeouts[i]) == currentTimeout) {
				timeoutIndex = i;
			}
		}
		
		// no index found, must be "off"
		if(timeoutIndex == -1) {
			timeoutIndex = tooltipTimeouts.length-1;
		}
		
		cbTooltipTimeout.removeActionListener(this);
		cbTooltipTimeout.setSelectedIndex(timeoutIndex);
		cbTooltipTimeout.addActionListener(this);
		
		// TODO update tooltip language 
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
		} else if(e.getSource() == cbUseLocalDigits) {
			app.setUseLocalizedDigits(cbUseLocalDigits.isSelected());
		} else if(e.getSource() == cbReturnAngleInverseTrig) {
			app.getKernel().setInverseTrigReturnsAngle(cbReturnAngleInverseTrig.isSelected());
			
			// make sure all calculations fully updated
			//app.getKernel().updateConstruction(); doesn't do what we want
			app.getKernel().storeUndoInfo();
			app.getKernel().undo();
		
		} else if(e.getSource() == cbUseLocalLabels) {
			app.setUseLocalizedLabels(cbUseLocalLabels.isSelected());
		} else if(e.getSource() == cbShowTitleBar) {
			app.getGuiManager().getLayout().setTitlebarVisible(cbShowTitleBar.isSelected());
		} else if(e.getSource() == cbIgnoreDocumentLayout) {
			app.getGuiManager().getLayout().setIgnoreDocument(cbIgnoreDocumentLayout.isSelected());
		} else if(e.getSource() == managePerspectivesButton) {
			app.getGuiManager().getLayout().showManageDialog();
		}
	}

	/**
	 * Slider changed.
	 */
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == slOpacity) {
			app.getGuiManager().getVirtualKeyboard().setOpacity(slOpacity.getValue() / 100.0f);
		}
	}

	/**
	 * Not implemented.
	 */
	public void focusGained(FocusEvent e) {}

	/**
	 * Apply textfield changes.
	 */
	public void focusLost(FocusEvent e) {
		VirtualKeyboard virtualKeyboard = app.getGuiManager().getVirtualKeyboard();
		
		if(e.getSource() == tfKeyboardHeight) {
			try {
				int windowHeight = Integer.parseInt(tfKeyboardHeight.getText());
				virtualKeyboard.setWindowHeight(windowHeight);
			} catch(NumberFormatException ex) {
				app.showError("InvalidInput");
				tfKeyboardHeight.setText(Integer.toString(virtualKeyboard.getHeight()));
			}
		} else if(e.getSource() == tfKeyboardWidth) {
			try {
				int windowWidth = Integer.parseInt(tfKeyboardWidth.getText());
				virtualKeyboard.setWindowWidth(windowWidth);
			} catch(NumberFormatException ex) {
				app.showError("InvalidInput");
				tfKeyboardWidth.setText(Integer.toString(virtualKeyboard.getWidth()));
			}
		}
	}

	/**
	 * Update the language of the user interface.
	 */
	public void setLabels() {
		virtualKeyboardPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("VirtualKeyboard")));		
		keyboardLanguageLabel.setText(app.getPlain("VirtualKeyboardLanguage")+":");
		widthLabel.setText(app.getPlain("Width")+":");
		heightLabel.setText(app.getPlain("Height")+":");
		cbKeyboardShowAutomatic.setText(app.getPlain("ShowAutomatically"));
		opacityLabel.setText(app.getMenu("Opacity")+":");

		tooltipPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Tooltips")));
		tooltipLanguageLabel.setText(app.getPlain("TooltipLanguage")+":");
		tooltipTimeoutLabel.setText(app.getPlain("TooltipTimeout")+":");
		
		languagePanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Language")));
		cbUseLocalDigits.setText(app.getPlain("LocalizedDigits"));
		cbUseLocalLabels.setText(app.getPlain("LocalizedLabels"));
		
		anglePanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Angle")));
		cbReturnAngleInverseTrig.setText(app.getMenu("ReturnAngleInverseTrig"));
		cbReturnAngleInverseTrig.setSelected(app.getKernel().getInverseTrigReturnsAngle());
		
		perspectivesPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Perspectives")));
		cbIgnoreDocumentLayout.setText(app.getPlain("IgnoreDocumentLayout"));
		cbShowTitleBar.setText(app.getPlain("ShowTitleBar"));
		managePerspectivesButton.setText(app.getMenu("ManagePerspectives"));
		
		scriptingPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Scripting")));
		cbEnableScripting.setText(app.getPlain("EnableScripting"));
		
		setLabelsKeyboardLanguage();
		setLabelsTooltipLanguages();
		setLabelsTooltipTimeouts();
	}
	
	/**
	 * Updates the keyboard languages, this is just necessary if the language 
	 * changed (or at startup). As we use an immutable list model we have to
	 * recreate the list all the time, even if we just change the label of the
	 * first item in the list.
	 */
	private void setLabelsKeyboardLanguage() {
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
		
		int selectedIndex = cbKeyboardLanguage.getSelectedIndex();
		
		// take care that this doesn't fire events by accident 
		cbKeyboardLanguage.removeActionListener(this);
		cbKeyboardLanguage.setModel(new DefaultComboBoxModel(languages));
		cbKeyboardLanguage.setSelectedIndex(selectedIndex);
		cbKeyboardLanguage.addActionListener(this);
	}
	
	/**
	 * @see #setLabelsKeyboardLanguage()
	 */
	private void setLabelsTooltipLanguages() {	
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
		
		int selectedIndex = cbTooltipLanguage.getSelectedIndex();
		
		// take care that this doesn't fire events by accident 
		cbTooltipLanguage.removeActionListener(this);
		cbTooltipLanguage.setModel(new DefaultComboBoxModel(languages));
		cbTooltipLanguage.setSelectedIndex(selectedIndex);
		cbTooltipLanguage.addActionListener(this);
	}
	
	/**
	 * @see #setLabelsKeyboardLanguage() 
	 */
	private void setLabelsTooltipTimeouts() {
		tooltipTimeouts[tooltipTimeouts.length-1] = app.getPlain("off");
		
		int selectedIndex = cbTooltipTimeout.getSelectedIndex();

		// take care that this doesn't fire events by accident 
		cbTooltipTimeout.removeActionListener(this);
		cbTooltipTimeout.setModel(new DefaultComboBoxModel(tooltipTimeouts));
		cbTooltipTimeout.setSelectedIndex(selectedIndex);
		cbTooltipTimeout.addActionListener(this);
	}
}
