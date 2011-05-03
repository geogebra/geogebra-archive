package geogebra.gui;

import geogebra.euclidian.Drawable;
import geogebra.gui.layout.Layout;
import geogebra.gui.virtualkeyboard.VirtualKeyboard;
import geogebra.kernel.Kernel;
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
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
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
	private JPanel virtualKeyboardPanel, tooltipPanel, languagePanel,  perspectivesPanel, miscPanel, angleUnitPanel, continuityPanel, pointStylePanel, checkboxSizePanel;
	
	/**	*/
	private JLabel keyboardLanguageLabel, widthLabel, heightLabel, opacityLabel, tooltipLanguageLabel, tooltipTimeoutLabel;
	
	/** */
	private JComboBox cbKeyboardLanguage, cbTooltipLanguage, cbTooltipTimeout;
	
	/**	 */
	private JCheckBox cbKeyboardShowAutomatic, cbUseLocalDigits, cbUseLocalLabels, cbReturnAngleInverseTrig, cbIgnoreDocumentLayout, cbShowTitleBar, cbEnableScripting, cbUseJavaFonts, cbReverseMouseWheel;
	
	/** */
	private JRadioButton angleUnitRadioDegree, angleUnitRadioRadian, continuityRadioOn, continuityRadioOff, pointStyleRadio0, pointStyleRadio1, pointStyleRadio2, pointStyleRadio3, pointStyleRadio4, pointStyleRadio6, pointStyleRadio7, checkboxSizeRadioRegular, checkboxSizeRadioLarge;
	
	/** */
	private ButtonGroup angleUnitButtonGroup, continuityButtonGroup, pointStyleButtonGroup, checkboxSizeButtonGroup;
	
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
		initPerspectivesPanel();
		initScriptingPanel();
		initAngleUnitPanel();
		initContinuityPanel();
		initPointStylePanel();
		initCheckboxSizePanel();

		JPanel panel = new JPanel();
		panel.setLayout(new FullWidthLayout());
		panel.add(virtualKeyboardPanel);
		panel.add(tooltipPanel);
		panel.add(languagePanel);
		panel.add(perspectivesPanel);
		panel.add(angleUnitPanel);
		panel.add(continuityPanel);
		panel.add(pointStylePanel);
		panel.add(checkboxSizePanel);
		panel.add(miscPanel);
		
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
		

		
		
		miscPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		// two columns
		JPanel guiPanelWest = new JPanel();
		guiPanelWest.setLayout(new BoxLayout(guiPanelWest, BoxLayout.Y_AXIS));
		JPanel guiPanelEast = new JPanel();
		guiPanelEast.setLayout(new BoxLayout(guiPanelEast, BoxLayout.Y_AXIS));
		JPanel twoColumns = new JPanel();
		twoColumns.setLayout(new BorderLayout());
		twoColumns.add(guiPanelEast, BorderLayout.EAST);
		twoColumns.add(guiPanelWest, BorderLayout.WEST);
		twoColumns.setAlignmentX(LEFT_ALIGNMENT);
		miscPanel.add(twoColumns);

		cbEnableScripting = new JCheckBox();
		cbEnableScripting.addActionListener(this);
		guiPanelWest.add(cbEnableScripting);
		
		cbReturnAngleInverseTrig = new JCheckBox();
		cbReturnAngleInverseTrig.addActionListener(this);
		guiPanelWest.add(cbReturnAngleInverseTrig);
		
		cbUseJavaFonts = new JCheckBox();
		cbUseJavaFonts.addActionListener(this);
		guiPanelEast.add(cbUseJavaFonts);
		
		cbReverseMouseWheel = new JCheckBox();
		cbReverseMouseWheel.addActionListener(this);
		guiPanelEast.add(cbReverseMouseWheel);
		
	
	}	

	/**
	 * Initialize the angle unit panel
	 */
	private void initAngleUnitPanel() {
		angleUnitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		angleUnitButtonGroup = new ButtonGroup();
		
		angleUnitRadioDegree = new JRadioButton();
		angleUnitRadioDegree.addActionListener(this);
		angleUnitPanel.add(angleUnitRadioDegree);
		angleUnitButtonGroup.add(angleUnitRadioDegree);

		angleUnitRadioRadian = new JRadioButton();
		angleUnitRadioRadian.addActionListener(this);
		angleUnitPanel.add(angleUnitRadioRadian);
		angleUnitButtonGroup.add(angleUnitRadioRadian);
	}
	
	/**
	 * Initialize the continuity panel
	 */
	private void initContinuityPanel() {
		continuityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		continuityButtonGroup = new ButtonGroup();
		
		continuityRadioOn = new JRadioButton();
		continuityRadioOn.addActionListener(this);
		continuityPanel.add(continuityRadioOn);
		continuityButtonGroup.add(continuityRadioOn);

		continuityRadioOff = new JRadioButton();
		continuityRadioOff.addActionListener(this);
		continuityPanel.add(continuityRadioOff);
		continuityButtonGroup.add(continuityRadioOff);
	}

	/**
	 * Initialize the point style panel
	 */
	private void initPointStylePanel() {
		pointStylePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pointStyleButtonGroup = new ButtonGroup();

		pointStyleRadio0 = new JRadioButton();
		pointStyleRadio0.addActionListener(this);
		pointStylePanel.add(pointStyleRadio0);
		pointStyleButtonGroup.add(pointStyleRadio0);

		pointStyleRadio2 = new JRadioButton();
		pointStyleRadio2.addActionListener(this);
		pointStylePanel.add(pointStyleRadio2);
		pointStyleButtonGroup.add(pointStyleRadio2);

		pointStyleRadio1 = new JRadioButton();
		pointStyleRadio1.addActionListener(this);
		pointStylePanel.add(pointStyleRadio1);
		pointStyleButtonGroup.add(pointStyleRadio1);

		pointStyleRadio3 = new JRadioButton();
		pointStyleRadio3.addActionListener(this);
		pointStylePanel.add(pointStyleRadio3);
		pointStyleButtonGroup.add(pointStyleRadio3);

		pointStyleRadio4 = new JRadioButton();
		pointStyleRadio4.addActionListener(this);
		pointStylePanel.add(pointStyleRadio4);
		pointStyleButtonGroup.add(pointStyleRadio4);

		pointStyleRadio6 = new JRadioButton();
		pointStyleRadio6.addActionListener(this);
		pointStylePanel.add(pointStyleRadio6);
		pointStyleButtonGroup.add(pointStyleRadio6);

		pointStyleRadio7 = new JRadioButton();
		pointStyleRadio7.addActionListener(this);
		pointStylePanel.add(pointStyleRadio7);
		pointStyleButtonGroup.add(pointStyleRadio7);
	}
	
	/**
	 * Initialize the checkbox size panel
	 */
	private void initCheckboxSizePanel() {
		checkboxSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		checkboxSizeButtonGroup = new ButtonGroup();
		
		checkboxSizeRadioRegular = new JRadioButton();
		checkboxSizeRadioRegular.addActionListener(this);
		checkboxSizePanel.add(checkboxSizeRadioRegular);
		checkboxSizeButtonGroup.add(checkboxSizeRadioRegular);

		checkboxSizeRadioLarge = new JRadioButton();
		checkboxSizeRadioLarge.addActionListener(this);
		checkboxSizePanel.add(checkboxSizeRadioLarge);
		checkboxSizeButtonGroup.add(checkboxSizeRadioLarge);
	}
	
	/**
	 * Update the user interface, ie change selected values.
	 * 
	 * @remark Do not call setLabels() here
	 */
	public void updateGUI() {
		cbUseLocalDigits.setSelected(app.isUsingLocalizedDigits());
		cbUseLocalLabels.setSelected(app.isUsingLocalizedLabels());

		angleUnitRadioDegree.setSelected(app.getKernel().getAngleUnit() == Kernel.ANGLE_DEGREE);
		angleUnitRadioRadian.setSelected(app.getKernel().getAngleUnit() != Kernel.ANGLE_DEGREE);

		continuityRadioOn.setSelected(app.getKernel().isContinuous());
		continuityRadioOff.setSelected(!app.getKernel().isContinuous());

		checkboxSizeRadioRegular.setSelected(app.getEuclidianView().getBooleanSize() == 13);
		checkboxSizeRadioLarge.setSelected(app.getEuclidianView().getBooleanSize() == 26);

		switch (app.getEuclidianView().getPointStyle()) {
		case 1:
			pointStyleRadio1.setSelected(true);
			break;
		case 2:
			pointStyleRadio2.setSelected(true);
			break;
		case 3:
			pointStyleRadio3.setSelected(true);
			break;
		case 4:
			pointStyleRadio4.setSelected(true);
			break;
		case 6:
			pointStyleRadio6.setSelected(true);
			break;
		case 7:
			pointStyleRadio7.setSelected(true);
			break;
		case 0:
		default:
			pointStyleRadio0.setSelected(true);
			break;
		}

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
		} else if(e.getSource() == cbUseJavaFonts) {
			Drawable.setUseJavaFontsForLaTeX(app, cbUseJavaFonts.isSelected());
		} else if(e.getSource() == cbReverseMouseWheel) {
			app.reverseMouseWheel(cbReverseMouseWheel.isSelected());
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
		} else if (e.getSource() == angleUnitRadioDegree) {
			app.getKernel().setAngleUnit(Kernel.ANGLE_DEGREE);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (e.getSource() == angleUnitRadioRadian) {
			app.getKernel().setAngleUnit(Kernel.ANGLE_RADIANT);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (e.getSource() == continuityRadioOn) {
			app.getKernel().setContinuous(true);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (e.getSource() == continuityRadioOff) {
			app.getKernel().setContinuous(false);
			app.getKernel().updateConstruction();
			app.setUnsaved();
		} else if (e.getSource() == pointStyleRadio0) {
			app.getEuclidianView().setPointStyle(0);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setPointStyle(0);
		} else if (e.getSource() == pointStyleRadio1) {
			app.getEuclidianView().setPointStyle(1);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setPointStyle(1);
		} else if (e.getSource() == pointStyleRadio2) {
			app.getEuclidianView().setPointStyle(2);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setPointStyle(2);
		} else if (e.getSource() == pointStyleRadio3) {
			app.getEuclidianView().setPointStyle(3);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setPointStyle(3);
		} else if (e.getSource() == pointStyleRadio4) {
			app.getEuclidianView().setPointStyle(4);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setPointStyle(4);
		} else if (e.getSource() == pointStyleRadio6) {
			app.getEuclidianView().setPointStyle(6);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setPointStyle(6);
		} else if (e.getSource() == pointStyleRadio7) {
			app.getEuclidianView().setPointStyle(7);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setPointStyle(7);
		} else if (e.getSource() == checkboxSizeRadioRegular) {
			app.getEuclidianView().setBooleanSize(13);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setBooleanSize(13);
		} else if (e.getSource() == checkboxSizeRadioLarge) {
			app.getEuclidianView().setBooleanSize(26);
			if (app.getGuiManager().hasEuclidianView2())
				app.getGuiManager().getEuclidianView2().setBooleanSize(26);
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
				app.showError("InvalidInput", tfKeyboardHeight.getText());
				tfKeyboardHeight.setText(Integer.toString(virtualKeyboard.getHeight()));
			}
		} else if(e.getSource() == tfKeyboardWidth) {
			try {
				int windowWidth = Integer.parseInt(tfKeyboardWidth.getText());
				virtualKeyboard.setWindowWidth(windowWidth);
			} catch(NumberFormatException ex) {
				app.showError("InvalidInput", tfKeyboardWidth.getText());
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
		
		languagePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Language")));
		cbUseLocalDigits.setText(app.getPlain("LocalizedDigits"));
		cbUseLocalLabels.setText(app.getPlain("LocalizedLabels"));

		angleUnitPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("AngleUnit")));
		angleUnitRadioDegree.setText(app.getMenu("Degree"));
		angleUnitRadioRadian.setText(app.getMenu("Radiant"));

		continuityPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Continuity")));
		continuityRadioOn.setText(app.getMenu("on"));
		continuityRadioOff.setText(app.getMenu("off"));

		checkboxSizePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("CheckboxSize")));
		checkboxSizeRadioRegular.setText(app.getMenu("CheckboxSize.Regular"));
		checkboxSizeRadioLarge.setText(app.getMenu("CheckboxSize.Large"));

		pointStylePanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("PointStyle")));
		pointStyleRadio0.setText(app.getMenu("\u25cf"));
		pointStyleRadio1.setText(app.getMenu("\u2716"));
		pointStyleRadio2.setText(app.getMenu("\u25cb"));
		pointStyleRadio3.setText(app.getMenu("\u271a"));
		pointStyleRadio4.setText(app.getMenu("\u25c6"));
		pointStyleRadio6.setText(app.getMenu("\u25b2"));
		pointStyleRadio7.setText(app.getMenu("\u25bc"));
	
		perspectivesPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Perspectives")));
		cbIgnoreDocumentLayout.setText(app.getPlain("IgnoreDocumentLayout"));
		cbShowTitleBar.setText(app.getPlain("ShowTitleBar"));
		managePerspectivesButton.setText(app.getMenu("ManagePerspectives"));
		
		miscPanel.setBorder(BorderFactory.createTitledBorder(app.getPlain("Miscellaneous")));
		cbEnableScripting.setText(app.getPlain("EnableScripting"));
		//cbEnableScripting.setSelected(b)
		cbUseJavaFonts.setText(app.getPlain("UseJavaFontsForLaTeX"));	
		cbUseJavaFonts.setSelected(Drawable.useJavaFontsForLaTeX());
		cbReverseMouseWheel.setText(app.getPlain("ReverseMouseWheel"));	
		cbReverseMouseWheel.setSelected(app.isMouseWheelReversed());
		cbReturnAngleInverseTrig.setText(app.getMenu("ReturnAngleInverseTrig"));
		cbReturnAngleInverseTrig.setSelected(app.getKernel().getInverseTrigReturnsAngle());
		
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
