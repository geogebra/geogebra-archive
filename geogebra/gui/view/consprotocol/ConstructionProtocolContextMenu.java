package geogebra.gui.view.consprotocol;

import geogebra.gui.view.consprotocol.ConstructionProtocolView.ColumnKeeper;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.table.TableColumn;

public class ConstructionProtocolContextMenu extends JPopupMenu {
	//private static final long serialVersionUID = 1L;
	
	private Application app;
	private Kernel kernel;
	
	public ConstructionProtocolContextMenu(Application app){
		this.app = app;
		this.kernel = app.getKernel();
		initItems();
	}
	
	/**
	 * Initialize the menu items.
	 */
	private void initItems() {		
		// title for menu
		JLabel title = new JLabel(app.getPlain("ConstructionProtocol"));
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
		final ConstructionProtocolView constprotView = app.getGuiManager().getConstructionProtocolView();		
		for (int k = 1; k < constprotView.getTableColumns().length; k++) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(
					constprotView.getData().columns[k].getTranslatedTitle());
			TableColumn column = constprotView.getTableColumns()[k];
			ColumnKeeper colKeeper = constprotView.new ColumnKeeper(column, constprotView.getData().columns[k]);
			item.setSelected(constprotView.isColumnInModel(column));
			item.addActionListener(colKeeper);
			add(item);
			
		}

		addSeparator();

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
		add(cbShowOnlyBreakpoints);

		JCheckBoxMenuItem cbUseColors = new JCheckBoxMenuItem(
				app.getPlain("ColorfulConstructionProtocol"));
		cbUseColors.setSelected(constprotView.getUseColors());
		cbUseColors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				constprotView.setUseColors(!constprotView.getUseColors());
				//constprotView.getData().updateAll();
			}
		});
		add(cbUseColors);
		
		addSeparator();
		
		AbstractAction exportHtmlAction = new AbstractAction(app.getPlain("ExportAsWebpage")
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
		
		add(exportHtmlAction);
		
	}
}
