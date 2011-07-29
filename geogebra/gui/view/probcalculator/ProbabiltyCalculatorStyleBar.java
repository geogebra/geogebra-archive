package geogebra.gui.view.probcalculator;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.view.spreadsheet.statdialog.PlotSettings;
import geogebra.main.Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;

/**
 * StyleBar for the ProbabilityCalculator view
 * @author G. Sturr
 *
 */
public class ProbabiltyCalculatorStyleBar extends JToolBar implements ActionListener{

	private Application app;
	private ProbabilityCalculator probCalc;
	protected int iconHeight = 18;
	private PopupMenuButton btnOptions;
	private JMenu menuDecimalPlaces;

	public ProbabiltyCalculatorStyleBar(Application app, ProbabilityCalculator probCalc){

		this.probCalc = probCalc;
		this.app = app;

		this.setFloatable(false);
		buildOptionsButton();
		add(btnOptions); 

	}



	/** 
	 * Builds popup button with options menu items 
	 */
	private void buildOptionsButton(){

		btnOptions = new PopupMenuButton();
		btnOptions.setKeepVisible(true);
		btnOptions.setStandardButton(true);
		btnOptions.setFixedIcon(GeoGebraIcon.createDownTriangleIcon(iconHeight));
		btnOptions.setDownwardPopup(true);
		btnOptions.setText(app.getMenu("Options"));


		JMenuItem menuItem;

		btnOptions.addPopupMenuItem(this.createMenuDecimalPlaces());


		menuItem = new JCheckBoxMenuItem(app.getMenu("Cumulative"));
		menuItem.setSelected(probCalc.isCumulative());
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				probCalc.setCumulative(!probCalc.isCumulative());
				probCalc.updateDistribution();
				probCalc.updatePlotSettings();
			}
		});
		btnOptions.addPopupMenuItem(menuItem);


		menuItem = new JCheckBoxMenuItem(app.getPlain("ShowGrid"));
		menuItem.setSelected(probCalc.getPlotSettings().showGrid);
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				PlotSettings ps = probCalc.getPlotSettings();
				ps.showGrid = !ps.showGrid;
				probCalc.setPlotSettings(ps);
				probCalc.updatePlotSettings();
			}
		});
		//btnOptions.addPopupMenuItem(menuItem);

		menuItem = new JMenuItem(app.getPlain("Export") + "..." );
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

			}
		});
		menuItem.setEnabled(false);
		btnOptions.addPopupMenuItem(menuItem);
		updateMenuDecimalPlaces();

		
	}



	/**
	 * Update the menu with the current number format.
	 */
	private void updateMenuDecimalPlaces() {
		int printFigures = probCalc.getPrintFigures();
		int printDecimals = probCalc.getPrintDecimals();

		if (menuDecimalPlaces == null)
			return;
		int pos = -1;

		if (printFigures >= 0) {
			if (printFigures > 0 && printFigures < Application.figuresLookup.length)
				pos = Application.figuresLookup[printFigures];
		} else {
			if (printDecimals > 0 && printDecimals < Application.decimalsLookup.length)
				pos = Application.decimalsLookup[printDecimals];
		}

		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
			.setSelected(true);
		} catch (Exception e) {
		}

	}

	private JMenu createMenuDecimalPlaces(){
		menuDecimalPlaces = new JMenu(app.getMenu("Rounding"));
		String[] strDecimalSpaces = app.getRoundingMenu();

		addRadioButtonMenuItems(menuDecimalPlaces, (ActionListener) this,
				strDecimalSpaces, Application.strDecimalSpacesAC, 0);

		return menuDecimalPlaces;
	}


	/**
	 * Create a set of radio buttons automatically.
	 * 
	 * @param menu
	 * @param al
	 * @param items
	 * @param actionCommands
	 * @param selectedPos
	 */
	private void addRadioButtonMenuItems(JMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				menu.addSeparator();
			} else {
				String text = app.getMenu(items[i]);
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(app.getFontCanDisplay(text));
				if (i == selectedPos)
					mi.setSelected(true);
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}


	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);
				probCalc.updatePrintFormat(decimals, -1);

			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int figures = Integer.parseInt(decStr);
				//	 Application.debug("figures " + figures);
				probCalc.updatePrintFormat(-1, figures);

			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}

	}





}
