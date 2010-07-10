
package geogebra.gui.view.spreadsheet;

import geogebra.gui.OptionsDialog;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class SpreadsheetContextMenu extends JPopupMenu 
{	
	private static final long serialVersionUID = -7749575525048631798L;

	final static Color bgColor = Color.white;

	protected MyTable table = null;
	protected int row1 = -1;
	protected int row2 = -1;
	protected int column1 = -1;
	protected int column2 = -1;


	private ArrayList<CellRange> selectedCellRanges;
	private int selectionType;

	protected Application app;
	private SpreadsheetView view;
	private ArrayList<GeoElement> geos; 
	private CellRangeProcessor cp;

	OneVariableStatsDialog oneVarStatDialog;
	
	
	// for testing
	private boolean isShiftDown = false;


	public SpreadsheetContextMenu(MyTable table, boolean isShiftDown) {

		this.table = table;
		app = table.kernel.getApplication();
		cp = table.getCellRangeProcessor();
		view = table.getView();

		column1 = table.selectedCellRanges.get(0).getMinColumn();
		column2 = table.selectedCellRanges.get(0).getMaxColumn();
		row1 = table.selectedCellRanges.get(0).getMinRow();
		row2 = table.selectedCellRanges.get(0).getMaxRow();						
		selectionType = table.getSelectionType();  
		selectedCellRanges = table.selectedCellRanges;		
		geos = app.getSelectedGeos();

		
		this.isShiftDown = isShiftDown;
		initMenu();
	}



	protected void initMenu() {

		JMenuItem item = new JMenuItem();
		JCheckBoxMenuItem cbItem = new JCheckBoxMenuItem();
		JMenu subMenu = new JMenu();

		setTitle(getTitleString()); 


		// ===============================================
		//    Cut-Copy-Paste-Delete 
		// ===============================================

		if(!isEmptySelection()){
			addSeparator();   
			item = new JMenuItem(app.getMenu("Copy"), app.getImageIcon("edit-copy.png"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					table.copyPasteCut.copy(column1, row1, column2, row2, false);
				}        	
			});
			add(item);

			item = new JMenuItem(app.getMenu("Paste"),app.getImageIcon("edit-paste.png"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean succ = table.copyPasteCut.paste(column1, row1, column2, row2);
					if (succ) app.storeUndoInfo();
					table.getView().getRowHeader().revalidate(); 		
				}        	
			});	 	
			add(item);

			item = new JMenuItem(app.getMenu("Cut"),app.getImageIcon("edit-cut.png"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean succ = table.copyPasteCut.cut(column1, row1, column2, row2);
					if (succ) app.storeUndoInfo();
				}
			});	 	
			add(item);


			item = new JMenuItem(app.getMenu("Delete"), app.getImageIcon("delete_small.gif"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					boolean succ = table.copyPasteCut.delete(column1, row1, column2, row2);
					if (succ) app.storeUndoInfo();
				}
			});	 	
			add(item);
			item.setEnabled(!allFixed());
		}

		addSeparator();   


		// ===============================================
		//        Insert (new row or new column)
		// ===============================================
		if(selectionType == MyTable.COLUMN_SELECT || selectionType == MyTable.ROW_SELECT){
			subMenu = new JMenu(app.getMenu("Insert") + " ...");
			subMenu.setIcon(app.getEmptyIcon()); 	 	
			add(subMenu);   	 	

			if(selectionType == MyTable.COLUMN_SELECT){ 
				item = new JMenuItem(app.getMenu("InsertLeft"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.InsertLeft(column1, column2);
					}
				});	 	 	
				subMenu.add(item);	 		

				item = new JMenuItem(app.getMenu("InsertRight"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.InsertRight(column1, column2);
					}
				});	 	  	 	
				subMenu.add(item);

			}

			if(selectionType == MyTable.ROW_SELECT){
				item = new JMenuItem(app.getMenu("InsertAbove"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.InsertAbove(row1, row2);
					}
				});	 	 	 	
				subMenu.add(item);


				item = new JMenuItem(app.getMenu("InsertBelow"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.InsertBelow(row1, row2);
					}
				});	 	 	 	
				subMenu.add(item);
			}
		}


		// ===============================================
		//        Trace to spreadsheet	
		// ===============================================

		if(selectionType != MyTable.ROW_SELECT){
			//addSeparator();
			cbItem = new JCheckBoxMenuItem(app.getPlain("TraceToSpreadsheet")+" ...");
			cbItem.setIcon(app.getImageIcon("spreadsheettrace.gif"));
			cbItem.setSelected(view.getTraceManager().isTraceColumn(table.minSelectionColumn));
			cbItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					view.showTraceDialog(null, selectedCellRanges.get(0));
				}
			});	  		 	
			add(cbItem);
		}


		// ===============================================
		//       Create (Lists, Matrix, etc.) 	
		// ===============================================

		if (!isEmptySelection()) {
			subMenu = new JMenu(app.getMenu("Create") + " ...");
			subMenu.setIcon(app.getEmptyIcon()); 	 	
			add(subMenu);   	 	

			item = new JMenuItem(app.getMenu("List"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cp.createList(selectedCellRanges, true, false);
				}
			});	 
			subMenu.add(item);

			if (cp.isCreatePointListPossible(selectedCellRanges))  {
				item = new JMenuItem(app.getMenu("ListOfPoints"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.CreatePointList(selectedCellRanges, true, true);
					}
				});	 
				subMenu.add(item);
			}   	 


			if (cp.isCreateMatrixPossible(selectedCellRanges)) {	
				item = new JMenuItem(app.getMenu("Matrix"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.CreateMatrix(column1, column2, row1, row2);
					}
				});	 
				subMenu.add(item);

				item = new JMenuItem(app.getMenu("Table"));
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cp.CreateTableText(column1, column2, row1, row2);
					}
				});	 
				subMenu.add(item);

			}

		}


		// ===============================================
		//             Import Data	
		// ===============================================

		item = new JMenuItem(app.getPlain("Import Data File") + " ...");
		item.setIcon(app.getImageIcon("document-open.png"));
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File dataFile = app.getGuiManager().getDataFile();				
				table.getView().loadSpreadsheetFromURL(dataFile);
			}
		});
		add(item);
		
		/*
		if (app.selectedGeosSize() >= 0) {
			addSeparator();

			item = new JMenu(app.getPlain("Import Data File") + " ...");
			item.setIcon(app.getImageIcon("document-open.png"));
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File dataFile = app.getGuiManager().getDataFile();				
					table.getView().loadSpreadsheetFromURL(dataFile);
				}
			});
			add(item);
			
			
			
			subMenu = new JMenu(app.getPlain("Import Data") + "...");
			subMenu.setIcon(app.getEmptyIcon());
			add(subMenu);

			item = new JMenuItem(app.getMenu(app.getPlain("File"))+"...", app.getEmptyIcon());
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					File dataFile = app.getGuiManager().getDataFile();				
					table.getView().loadSpreadsheetFromURL(dataFile);
				}
			});	 
			subMenu.add(item);



			item = new JMenuItem(app.getMenu(app.getPlain("URL"))+"...", app.getEmptyIcon());
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					InputDialog id = new InputDialogOpenDataURL(app,view);
					id.setVisible(true);
				}
			});	 
			subMenu.add(item);


			subMenu.addSeparator();
			item = new JMenuItem(app.getMenu(app.getPlain("Browser")),app.getEmptyIcon());
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					table.getView().setShowBrowserPanel(true);
				}
			});	 
			subMenu.add(item);
		

			
			subMenu.addSeparator();
			item = new JMenuItem(app.getMenu(app.getPlain("ProbCalc")),app.getEmptyIcon());
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					ProbabilityCalculator pc = new ProbabilityCalculator(app);
					pc.setVisible(true);
				}
			});	 
			subMenu.add(item);
			 

		}
		*/

		
		
		// ===============================================
		//     Data analysis
		// ===============================================
		
		if(!isEmptySelection()){ // && isShiftDown){
			item = new JMenuItem(app.getMenu(app.getPlain("Data Analysis")),app.getEmptyIcon());
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(oneVarStatDialog == null)
						oneVarStatDialog = new OneVariableStatsDialog(table,app);
					oneVarStatDialog.setVisible(true);
				}
			});	 
			add(item);
		}

		

		// ===============================================
		//     Show Toolbars / Spreadsheet Options
		// ===============================================

		addSeparator();
		
		subMenu = new JMenu(app.getPlain("Show") + " ...");
		subMenu.setIcon(app.getEmptyIcon());
		add(subMenu);
		
		cbItem = new JCheckBoxMenuItem(app.getPlain("ToolBar"));
		//cbItem.setIcon(app.getEmptyIcon());
		cbItem.setSelected(view.getShowToolBar());
		cbItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setShowToolBar(!view.getShowToolBar());
			}
		});	 
		subMenu.add(cbItem);

		cbItem = new JCheckBoxMenuItem(app.getPlain("Browser"));
		//cbItem.setIcon(app.getEmptyIcon());
		cbItem.setSelected(view.getShowBrowserPanel());
		cbItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.setShowBrowserPanel(!view.getShowBrowserPanel());
			}
		});	 
		subMenu.add(cbItem);
		
		
		
		item = new JMenuItem(app.getMenu("Spreadsheet Options") + "...",app.getEmptyIcon());
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.getGuiManager().showOptionsDialog(OptionsDialog.TAB_SPREADSHEET);
			}
		});	 
		add(item);





		// ===============================================
		// Object properties 
		// ===============================================

		if (app.selectedGeosSize() > 0) {
			addSeparator();
			item = new JMenuItem(app.getMenu(app.getPlain("Properties"))+"...", app.getImageIcon("document-properties.png"));	 	
			item.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					app.getGuiManager().showPropertiesDialog();	
				}
			});	 
			add(item);
		}

	}



	private String getTitleString(){

		//title = cell range if empty or multiple cell selection
		String title = GeoElement.getSpreadsheetCellName(column1, row1);
		if(column1 != column2 || row1 != row2){
			title += ":" + GeoElement.getSpreadsheetCellName(column2, row2);
		} 
		// title = geo description if single geo in cell  
		else if (geos!=null && geos.size() == 1){	 
			GeoElement geo0 = geos.get(0);
			title = geo0.getLongDescriptionHTML(false, true);
			if (title.length() > 80)
				title = geo0.getNameDescriptionHTML(false, true);          
		}

		return title;
	}


	//setTitle (copied from gui.ContextMenuGeoElement)
	private void setTitle(String str) {
		JLabel title = new JLabel(str);
		title.setFont(app.getBoldFont());                      
		title.setBackground(bgColor);        

		title.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 5));      
		add(title);

		title.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});

	}


	private boolean allFixed(){
		boolean allFixed = true; 	
		if (geos != null && geos.size() >0) {
			for (int i = 0 ; (i < geos.size() && allFixed) ; i++) {
				GeoElement geo = geos.get(i);
				if (!geo.isFixed()) allFixed = false;
			}
		}
		return allFixed;
	}


	private boolean isEmptySelection(){
		return (app.getSelectedGeos().isEmpty()) ;
	}
	
	

}
