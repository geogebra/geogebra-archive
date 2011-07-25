package geogebra.gui.view.consprotocol;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.PopupMenuButtonMultiple;
import geogebra.gui.util.SelectionTable;
import geogebra.gui.view.consprotocol.ConstructionProtocolView.ColumnKeeper;
import geogebra.main.Application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


public class ConstructionProtocolStyleBar extends JToolBar implements ActionListener {
	
	private static final long serialVersionUID = 1L;

	/**
	 * The construction protocol view which uses this tool bar.
	 */
	protected ConstructionProtocolView cpView;
	
	/**
	 * Instance of the application.
	 */
	protected Application app;
	
	PopupMenuButtonMultiple btnColumns, btnOptions;
	JButton btnExport, btnPrint, btnHelp;
	
	
	/**
	 * Helper bar.
	 * @param cpView
	 * @param app
	 */
	public ConstructionProtocolStyleBar(ConstructionProtocolView cpView, Application app) {
		this.cpView = cpView;
		this.app = app;
		
		setFloatable(false);
		
		addButtons();
	}
	
	/**
	 * add the buttons
	 */
	protected void addButtons(){
		
		int iconHeight = 16;
		
		// "columns" button
		
		int countCol= cpView.getData().columns.length;
		String[] strColumns = new String[countCol];
		for (int k=0; k<countCol; k++){
			strColumns[k] = app.getMenu(app.getPlain(cpView.getData().columns[k].getTitle()));
		}
		
		btnColumns = new PopupMenuButtonMultiple(app, strColumns, -1, 1, 
				new Dimension(0, iconHeight), SelectionTable.MODE_TEXT){
			@Override
			public void update(Object[] geos) {
			}

			public boolean prepareToShowPopup(){
				setVisibleColumnIndeces(this);		
				return true;
			}
			
			public ImageIcon getButtonIcon(){
				return (ImageIcon) this.getIcon();
			}

		};
		
		ImageIcon ptShowColumnsIcon = app.getImageIcon("show_columns.png");
		btnColumns.setIconSize(new Dimension(ptShowColumnsIcon.getIconWidth(),iconHeight));
		btnColumns.setIcon(ptShowColumnsIcon);
		btnColumns.addActionListener(this);
		btnColumns.setKeepVisible(false);
		//btnColumns.setToolTipText(app.getPlainTooltip("Columns"));
		add(btnColumns);
	
		addSeparator();
		
		//options button
		
			
		// Test code: 
		// PopupMenuButton without selection table, add JMenuItems directly.
		// ==============================================================
		 
		PopupMenuButton btnOptions2 = new PopupMenuButton();
		btnOptions2.setKeepVisible(true);
		btnOptions2.setStandardButton(true);  // mouse clicks over total button region
		btnOptions2.setIcon(app.getImageIcon("options.png"));
		
		JCheckBoxMenuItem menuItem;	
		menuItem = new JCheckBoxMenuItem(app.getPlain("ShowOnlyBreakpoints"));
		menuItem.setSelected(app.getKernel().showOnlyBreakpoints());
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				app.getKernel().setShowOnlyBreakpoints(!app.getKernel().showOnlyBreakpoints());
			}
		});
		btnOptions2.addPopupMenuItem(menuItem);
		
		menuItem = new JCheckBoxMenuItem(app.getPlain("ColorfulConstructionProtocol"));
		menuItem.setSelected(cpView.getUseColors());
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				cpView.setUseColors(!cpView.getUseColors());
			}
		});
		btnOptions2.addPopupMenuItem(menuItem);
		add(btnOptions2);
		
		// ==============================================================
		 
		
		
		String[] optionsArray = new String[] {
				app.getPlain("ShowOnlyBreakpoints"),
				app.getPlain("ColorfulConstructionProtocol")
		};
		
		btnOptions = new PopupMenuButtonMultiple(app, optionsArray, -1, 1, 
				new Dimension(0, iconHeight), SelectionTable.MODE_TEXT){
			@Override
			public void update(Object[] geos) {
			}

			public boolean prepareToShowPopup(){
				//TODO: set the state of buttons
				return true;
			}
		
			public ImageIcon getButtonIcon(){
				return (ImageIcon) this.getIcon();
			}

		};	
		ImageIcon ptOptionsIcon = app.getImageIcon("options.png");
		btnOptions.setIconSize(new Dimension(ptOptionsIcon.getIconWidth(),iconHeight));
		btnOptions.setIcon(ptOptionsIcon);
		btnOptions.addActionListener(this);
		btnOptions.setKeepVisible(false);	
		//btnOptions.setToolTipText(app.getPlainTooltip("Options"));
		add(btnOptions);
		
		
		
	
		addSeparator();
		
		//export button
		
		btnExport = new JButton(app.getImageIcon("text-html.png"));
		btnExport.setToolTipText(app.getPlainTooltip("ExportAsWebpage"));
		btnExport.addActionListener(this);
		add(btnExport);
		
		addSeparator();

		//print button
		btnPrint = new JButton(app.getImageIcon("document-print-preview.png"));
		btnPrint.setToolTipText(app.getPlainTooltip("Print"));
		btnPrint.addActionListener(this);
		add(btnPrint);
		
		addSeparator();
		
		//Help button
		btnHelp = new JButton(app.getImageIcon("help.png"));
		//btnHelp.setToolTipText(app.getPlainTooltip("FastHelp"));
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.showHelp("ConstructionProtocolHelp");
				//requestFocus();
			}
		});
		add(btnHelp);
		
		setLabels();
	}
	
	
	
	
	/**
	 * Sets the button's selected value accordingly the visible columns
	 * @param button
	 */
	public void setVisibleColumnIndeces(PopupMenuButtonMultiple button){
		
		TableColumnModel model = cpView.getTable().getColumnModel();
		//button.removeRowSelectionInterval(0,model.getColumnCount());
		//button.addSelectedIndex(-1);  //clear the selection
		//button.clearSelection();
		
		for(int i=1;i<model.getColumnCount();i++){
			//JTable table = cpView.getTable();
			//Application.debug(model.getColumn(i).getModelIndex());
			button.addSelectedIndex(model.getColumn(i).getModelIndex());
		}
	}

	
	/**
	 * Set the tool tip texts (used for language change, and at initialization labels).
	 */
	public void setLabels() {
		btnColumns.setToolTipText(app.getPlainTooltip("Columns"));
		btnOptions.setToolTipText(app.getPlainTooltip("Options"));
		btnExport.setToolTipText(app.getPlainTooltip("ExportAsWebpage"));
		btnPrint.setToolTipText(app.getPlainTooltip("Print"));
		btnHelp.setToolTipText(app.getPlainTooltip("FastHelp"));
	}

	/**
	 * React to button presses.
	 */
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource()==btnColumns){
			int selIndex = btnColumns.getSelectedIndex();
			TableColumn column = cpView.getTableColumns()[selIndex];
			ColumnKeeper colKeeper = cpView.new ColumnKeeper(column, cpView.getData().columns[selIndex]);
			colKeeper.actionPerformed(e);
			
		}
		
		if(e.getSource()==btnOptions){
			if(btnOptions.getSelectedIndex()==0){
				app.getKernel().setShowOnlyBreakpoints(!app.getKernel().showOnlyBreakpoints());
				cpView.getData().initView();
				cpView.repaint();
			}
			else if(btnOptions.getSelectedIndex()==1)
				cpView.setUseColors(!cpView.getUseColors());
		}
		
		if(e.getSource()==btnExport){
			cpView.getExportHtmlAction().actionPerformed(e);
		}
		if(e.getSource()==btnPrint){
			cpView.getPrintPreviewAction().actionPerformed(e);
		}
	}
}
