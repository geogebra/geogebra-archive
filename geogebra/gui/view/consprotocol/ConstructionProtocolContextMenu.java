package geogebra.gui.view.consprotocol;

import geogebra.gui.TitlePanel;
import geogebra.gui.view.consprotocol.ConstructionProtocolView.ColumnKeeper;
import geogebra.kernel.Construction;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

public class ConstructionProtocolContextMenu extends JPopupMenu {
	//private static final long serialVersionUID = 1L;
	
	private Application app;
	private Kernel kernel;
	private ConstructionProtocolView constprotView;
	private AbstractAction exportHtmlAction, printPreviewAction;
	
	
	public ConstructionProtocolContextMenu(Application app){
		this.app = app;
		this.kernel = app.getKernel();
		constprotView = app.getGuiManager().getConstructionProtocolView();
		initActions();
		initItems();
	}

	private void initActions() {

		exportHtmlAction = new AbstractAction(app.getPlain("ExportAsWebpage")
				+ " (" + Application.FILE_EXT_HTML + ") ...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();

				Thread runner = new Thread() {
					public void run() {
						JDialog d = new geogebra.export.ConstructionProtocolExportDialog(
								constprotView);
						d.setVisible(true);
					}
				};
				runner.start();

				app.setDefaultCursor();
			}
		};
		
		printPreviewAction = new AbstractAction(app.getMenu("Print")
				+ "...", app.getImageIcon("document-print-preview.png")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setWaitCursor();

				Thread runner = new Thread() {
					public void run() {
						
						try {
							Construction cons = app.getKernel().getConstruction();
							constprotView.getTable().print(JTable.PrintMode.FIT_WIDTH, 
									new MessageFormat(tableHeader(cons)), 
									new MessageFormat("{0}"), // page numbering 
									/*showPrintDialog*/ true, 
									/*attr*/ null, 
									/*interactive*/ true /*,*/ 
									/*service*/ /*null*/);
							// service must be omitted for Java version 1.5.0
						} catch (HeadlessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (PrinterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					// This may be too long. FIXME
					private String tableHeader(Construction cons) {
					
						TitlePanel tp = new TitlePanel(app);
						String author = tp.loadAuthor();
						String title = cons.getTitle();
						String date = tp.configureDate(cons.getDate());
						
						if (title.equals(""))
							title = app.getPlain("UntitledConstruction");
						if (author.equals(""))
							return title + " (" + date + ")";
						else
							return author + ": " + title + " (" + date + ")";

					}
				};
				runner.start();

				app.setDefaultCursor();
			}
		};

	}	
	
	/**
	 * Initialize the menu items.
	 */
	private void initItems() {		
		// title for menu
		JLabel title = new JLabel(app.getPlain("ConstructionProtocol"));
		
		JMenu colMenu = new JMenu(app.getPlain("Columns"));
		JMenu optionsMenu = new JMenu(app.getPlain("Options"));
		
        title.setFont(app.getBoldFont());                      
        title.setBackground(Color.white);
        title.setForeground(Color.black);
                
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 5));      
        add(title);
        addSeparator();
        
        title.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		setVisible(false);
        	}
        });
		
        // menu items	
		
        
        //"Columns" menu
		for (int k = 1; k < constprotView.getTableColumns().length; k++) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(
					constprotView.getData().columns[k].getTranslatedTitle());
			TableColumn column = constprotView.getTableColumns()[k];
			ColumnKeeper colKeeper = constprotView.new ColumnKeeper(column, constprotView.getData().columns[k]);
			item.setSelected(constprotView.isColumnInModel(column));
			item.addActionListener(colKeeper);
			colMenu.add(item);
			
		}
		add(colMenu);
		
		
		//"Options" menu
		JCheckBoxMenuItem cbShowOnlyBreakpoints = new JCheckBoxMenuItem(
				app.getPlain("ShowOnlyBreakpoints"));
		cbShowOnlyBreakpoints.setSelected(kernel.showOnlyBreakpoints());
		
		cbShowOnlyBreakpoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				kernel.setShowOnlyBreakpoints(!kernel.showOnlyBreakpoints());
				constprotView.getData().initView();
				constprotView.repaint();
			}
		});
		optionsMenu.add(cbShowOnlyBreakpoints);

		JCheckBoxMenuItem cbUseColors = new JCheckBoxMenuItem(
				app.getPlain("ColorfulConstructionProtocol"));
		cbUseColors.setSelected(constprotView.getUseColors());
		cbUseColors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				constprotView.setUseColors(!constprotView.getUseColors());
				//constprotView.getData().updateAll();
			}
		});
		optionsMenu.add(cbUseColors);
		add(optionsMenu);
		
		//Export and Print menu		
		add(exportHtmlAction);
		add(printPreviewAction);
		
		//Help menu
		JMenuItem mi = new JMenuItem(app.getMenu("FastHelp"),
				app.getImageIcon("help.png"));
		ActionListener lstHelp = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.showHelp("ConstructionProtocolHelp");
				requestFocus();
			}
		};
		mi.addActionListener(lstHelp);
		add(mi);
		
	}
	
	
}
