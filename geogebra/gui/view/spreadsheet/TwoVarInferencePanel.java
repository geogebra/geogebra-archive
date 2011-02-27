package geogebra.gui.view.spreadsheet;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoList;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

public class TwoVarInferencePanel extends JPanel{

	private Application app;
	private GeoList dataList;
	private JList dataSourceList;
	private DefaultListModel model;
	private DefaultComboBoxModel modelTitle1, modelTitle2, modelInference;
	private JComboBox cbTitle1, cbTitle2, cbInferenceType;
	private StatDialog statDialog;
	private JLabel lblTitle1, lblTitle2 ;
	private JLabel lblTestStat;
	private JLabel lblPValue;
	private JLabel lblNullHyp;

	public TwoVarInferencePanel(Application app, GeoList dataList, StatDialog statDialog){

		this.app = app;
		this.dataList = dataList;
		this.statDialog = statDialog;

		this.setOpaque(true);
		this.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		
		JPanel selectVarsPanel = new JPanel(new BorderLayout());	
		JPanel InferencePanel = new JPanel(new BorderLayout());

		modelTitle1 = new DefaultComboBoxModel();
		modelTitle2 = new DefaultComboBoxModel();
		modelInference = new DefaultComboBoxModel();
		cbTitle1 = new JComboBox(modelTitle1);
		cbTitle2 = new JComboBox(modelTitle2);
		cbInferenceType = new JComboBox(modelInference);
		lblTitle1 = new JLabel();
		lblTitle2 = new JLabel();
			
		//createDataSourceList();
		//updateDataSourceList();
				
		
		//titlePanel = flowPanel(lblTitle1,cbTitle1,lblTitle2,cbTitle2 );
		
		
		JPanel northPanel = boxYPanel(flowPanel(cbInferenceType),
				flowPanel(lblTitle1,cbTitle1),
				flowPanel(lblTitle2,cbTitle2));
		//northPanel.add(flowPanel(cbInferenceType), BorderLayout.NORTH);
		//northPanel.add(flowPanel(lblTitle1,cbTitle1,lblTitle2,cbTitle2 ), BorderLayout.SOUTH);
		//northPanel.setBackground(Color.white);
		northPanel.setBorder(BorderFactory.createEtchedBorder());
		
		this.add(northPanel, BorderLayout.NORTH);
		this.add(createDifferenceOfMeansPanel(), BorderLayout.SOUTH);

	}

	
	private JPanel flowPanel(JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		p.setBackground(Color.white);
		return p;
	}
	
	private JPanel boxYPanel(JComponent... comp){
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		p.setBackground(Color.white);
		return p;
	}
	
	private JPanel blPanel(){
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.white);
		return p;
	}
	
	
	
	public void updateTwoVarPanel(){
		
		lblTitle1.setText(app.getMenu("sample1") + ": ");
		lblTitle2.setText(app.getMenu("sample2") + ": ");
		lblTestStat.setText(app.getMenu("TestStatistic"));
		lblPValue.setText(app.getMenu("PValue"));
		
		modelInference.removeAllElements();
		modelInference.addElement(app.getMenu("Difference of Means T Test"));	
		modelInference.addElement(app.getMenu("Difference of Means T Estimate"));
		modelInference.addElement(app.getMenu("Paired T Test"));
		
		modelTitle1.removeAllElements();
		modelTitle2.removeAllElements();
		String[] dataTitles = statDialog.getDataTitles();
		if(dataTitles!= null){
			for(int i=0; i < dataTitles.length; i++){
				modelTitle1.addElement(dataTitles[i]);
				modelTitle2.addElement(dataTitles[i]);
			}
		}
			
	}


	private JPanel createDifferenceOfMeansPanel(){
		
		lblNullHyp = new JLabel(app.getMenu("nullHyp")+": ");
		JRadioButton btnLeft = new JRadioButton("<");
		JRadioButton btnRight = new JRadioButton(">");
		JRadioButton btnTwo = new JRadioButton(ExpressionNode.strNOT_EQUAL);
		ButtonGroup group = new ButtonGroup();
		group.add(btnLeft);
		group.add(btnRight);
		group.add(btnTwo);
		
		
		String[] hypOptions = { "<", ">", ExpressionNode.strNOT_EQUAL};
		JComboBox cbHypOptions = new JComboBox(hypOptions);
		
	
		
		MyTextField fldNullHyp = new MyTextField(app.getGuiManager());
		fldNullHyp.setColumns(4);
		
		JPanel panelNull =  flowPanel(lblNullHyp, cbHypOptions,fldNullHyp );
		
		
		
		lblTestStat = new JLabel();
		lblPValue = new JLabel();
		JTextField fldPValue = new JTextField("" + 0.234);
		fldPValue.setEditable(false);
		JTextField fldTestStat = new JTextField("" + 2.234);
		fldTestStat.setEditable(false);
		
		JPanel p = boxYPanel(panelNull, 
				flowPanel(lblTestStat,fldTestStat), 
				flowPanel(lblPValue,fldPValue));
	//	p.add(lblPValue);
	//	p.setBackground(Color.white);
		
		JPanel diffPanel = blPanel();
		diffPanel.add(p, BorderLayout.CENTER);
		return p;
		
		
	}
	
	
	
	
	
	
	
	
	
	
	private void createDataSourceList(){	

		model = new DefaultListModel(); 
		dataSourceList = new JList(model);

		dataSourceList.setCellRenderer(new CheckListRenderer());
		dataSourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add a mouse listener to handle changing selection
		dataSourceList.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				JList list = (JList) event.getSource();

				// Get index of item clicked
				int index = list.locationToIndex(event.getPoint());
				CheckListItem item = (CheckListItem)
				list.getModel().getElementAt(index);

				// Toggle selected state
				item.setSelected(! item.isSelected());

				// Repaint cell
				list.repaint(list.getCellBounds(index, index));
			}
		});  

	}



	private void updateDataSourceList(){

		model.removeAllElements();

		model.addElement(new CheckListItem("apple"));
		model.addElement(new CheckListItem("apple"));
		model.addElement(new CheckListItem("apple"));
		model.addElement(new CheckListItem("apple"));

	}


	// Represents items in the list that can be selected
	class CheckListItem
	{
		private String  label;
		private boolean isSelected = false;

		public CheckListItem(String label)
		{
			this.label = label;
		}

		public boolean isSelected()
		{
			return isSelected;
		}

		public void setSelected(boolean isSelected)
		{
			this.isSelected = isSelected;
		}

		public String toString()
		{
			return label;
		}
	}

	// Handles rendering cells in the list using a check box

	class CheckListRenderer extends JCheckBox
	implements ListCellRenderer
	{
		public Component getListCellRendererComponent(
				JList list, Object value, int index,
				boolean isSelected, boolean hasFocus)
		{
			setEnabled(list.isEnabled());
			setSelected(((CheckListItem)value).isSelected());
			setFont(list.getFont());
			setBackground(list.getBackground());
			setForeground(list.getForeground());
			setText(value.toString());
			return this;
		}
	}





}
