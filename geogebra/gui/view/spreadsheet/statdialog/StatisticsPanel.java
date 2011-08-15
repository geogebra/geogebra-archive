package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StatisticsPanel extends JPanel implements StatPanelInterface {


	private JLabel statisticsHeader;
	private StatDialog statDialog;
	private Application app;
	protected BasicStatTable statTable;

	public StatisticsPanel(Application app, StatDialog statDialog)  {

		this.app = app;
		this.statDialog = statDialog;

		statisticsHeader = new JLabel();
		statisticsHeader.setHorizontalAlignment(JLabel.LEFT);		
		statisticsHeader.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),	
				BorderFactory.createEmptyBorder(2,5,2,2)));



		// Create a StatPanel to display basic statistics for the current data set
		//================================================
		if(statDialog.getMode() == StatDialog.MODE_ONEVAR){
			statTable = new BasicStatTable(app, statDialog, statDialog.getMode());
		}

		else if(statDialog.getMode() == StatDialog.MODE_REGRESSION){
			statTable = new BasicStatTable(app, statDialog, statDialog.getMode());
		}
		statTable.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		

		// put it all into the stat panel
		setLayout(new BorderLayout());
		add(statisticsHeader, BorderLayout.NORTH);
		add(statTable, BorderLayout.CENTER);

		setLabels();
		

	}

	public void updateFonts(Font font) {
		// TODO Auto-generated method stub

	}

	public void setLabels() {

		statisticsHeader.setText(app.getMenu("Statistics")); 	
	}

	public void updatePanel() {
		statTable.updatePanel();

	}




}
