package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.euclidian.EuclidianViewInterface;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class StatPanel extends JPanel {
	
	// ggb 
	private Application app;
	private Kernel kernel; 
	

	private JTable statTable;
	//private DefaultTableModel statModel;
	private JScrollPane statScroller;
	
	// data and stat lists
	private GeoList statList;
	
	// layout
	private static final Color TABLE_GRID_COLOR = Color.gray;
	
	
	
	/*************************************************
	 * Construct the panel
	 */
	public StatPanel(Application app, GeoList dataList){
			
		this.app = app;	
		kernel = app.getKernel();				
		
		createStatList(dataList);
		
		// build the stat table	
		statTable = new JTable(){
			// disable cell editing
		      @Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
		        return false;   
		      }
		    };
		 updateStatTable(); 
		 
		statTable.setDefaultRenderer(Object.class, new MyCellRenderer());    
		statTable.setColumnSelectionAllowed(true); 
		statTable.setRowSelectionAllowed(true);
		statTable.setShowGrid(true); 	 
		statTable.setGridColor(TABLE_GRID_COLOR); 	 	
		statTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		statTable.setPreferredScrollableViewportSize(statTable.getPreferredSize());
		
		statTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		statTable.setMinimumSize(new Dimension(50,50));
		
	
		// enclose the table in a scroller
		statScroller = new JScrollPane(statTable);
		statScroller.setBorder(BorderFactory.createEmptyBorder());
		
		// hide the table header
		statTable.setTableHeader(null);
		statScroller.setColumnHeaderView(null);
		
		// put it all into the stat panel
		this.setLayout(new BorderLayout());
		this.add(statScroller, BorderLayout.CENTER);
		this.setBorder(BorderFactory.createEmptyBorder());

	}

	
	private void  createStatList(GeoList dataList){
		
		String label = dataList.getLabel();	
		System.out.println(dataList.toDefinedValueString());
		System.out.println(dataList.toString());
		
		String text = "";
		ArrayList<String> list = new ArrayList<String>();
		
		try {		
			text += "{";
			text += statListCmdString("Length", label);
			text += ",";
			text += statListCmdString("Mean", label);
			text += ",";
			text += statListCmdString("SD", label);
			text += ",";
			text += statListCmdString("SampleSD", label);
			text += ",";
			text += statListCmdString("Min", label);
			text += ",";
			text += statListCmdString("Q1", label);
			text += ",";
			text += statListCmdString("Median", label);
			text += ",";
			text += statListCmdString("Q3", label);
			text += ",";
			text += statListCmdString("Max", label);
			
			text += "}";
			System.out.println(text);	
			
			// convert list string to geo
			GeoElement[] geos = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionHandling(text, false);
	
			statList = (GeoList) geos[0];
			statList.setLabel("statlist");
			//statList.setAlgebraVisible(false);
			statList.update();
			
		} catch (Exception ex) {
			Application.debug("Creating list failed with exception " + ex);
			setVisible(false);
		}	
		
	}
	
	private String statListCmdString(String cmdStr, String geoLabel){
		
		String text = "{";
		text += "\"" + app.getCommand(cmdStr)+ "\",";
		text += cmdStr + "[" + geoLabel + "]";
		text += "}";
		
		return text; 
	}
	
	
	public void updateStatTable(){
		TableModel statModel = new DefaultTableModel(statList.size(), 2);
		GeoList list;
		for (int elem = 0; elem < statList.size(); ++elem){
			list = (GeoList)statList.get(elem);
			statModel.setValueAt(list.get(0).toDefinedValueString(), elem, 0);
			statModel.setValueAt(list.get(1).toDefinedValueString(), elem, 1);
		}
		statTable.setModel(statModel);
	}

	
	
	public void updateFonts(Font font) {

		int size = font.getSize();
		if (size < 12) size = 12; // minimum size
		double multiplier = (size)/12.0;

		setFont(font);
		statTable.setRowHeight((int)(MyTable.TABLE_CELL_HEIGHT * multiplier));
		statTable.setFont(font);  

	}


	//======================================================
	//         Cell Renderer 
	//======================================================
	
	class MyCellRenderer extends DefaultTableCellRenderer {
		
		public MyCellRenderer(){
			
		setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		
		}
		
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) 
		{	
			String text = value.toString();
			
			if(value instanceof Boolean){
				setText(text);
			}else{
				setText(text);
			}
			
			//setFont(app.getFontCanDisplay(text, Font.PLAIN));
			
			return this;
		}

	}

	


	
	
}
