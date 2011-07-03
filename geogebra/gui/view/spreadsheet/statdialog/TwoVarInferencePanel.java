package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoList;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;

public class TwoVarInferencePanel extends JPanel implements ActionListener, StatPanelInterface{

	private Application app;
	private StatDialog statDialog;

	private static final int MODE_TTEST_2MEANS = 0;
	private static final int MODE_TTEST_PAIRED = 1;
	private static final int MODE_TINT_2MEANS = 2;
	private static final int MODE_TINT_PAIRED = 3;

	private int mode = MODE_TTEST_2MEANS;


	private JList dataSourceList;
	private DefaultListModel model;

	private JComboBox cbTitle1, cbTitle2, cbProcedure, cbIntOptions;
	private JLabel lblTitle1, lblTitle2, lblHypParameter, lblTailType,
	lblNull, lblCI, lblIntLevel;
	private JButton btnCalc;
	private MyTextField fldNullHyp;
	private JTextArea taResult;
	private JTabbedPane tabbedPane;
	private JPanel resultPanel;
	private String[] nullHypName;
	private JPanel cardProcedure;
	private JCheckBox ckEqualVariances;


	public TwoVarInferencePanel(Application app, StatDialog statDialog){

		this.app = app;
		this.statDialog = statDialog;


		//titlePanel = flowPanel(lblTitle1,cbTitle1,lblTitle2,cbTitle2 );
		//	JPanel northPanel = flowPanel(
		//			flowPanel(lblTitle1,cbTitle1),
		//			flowPanel(lblTitle2,cbTitle2));
		//northPanel.add(flowPanel(cbInferenceType), BorderLayout.NORTH);
		//northPanel.add(flowPanel(lblTitle1,cbTitle1,lblTitle2,cbTitle2 ), BorderLayout.SOUTH);
		//northPanel.setBackground(Color.white);
		//northPanel.setBorder(BorderFactory.createEtchedBorder());
		//this.add(northPanel, BorderLayout.NORTH);


		this.setLayout(new BorderLayout());
		this.add(createMainPanel(), BorderLayout.NORTH);
		this.setMinimumSize(new Dimension(10,10));
		//updateGUI();
	}




	public void updateTwoVarPanel(){

		String P = app.getMenu("Pvalue");
		String t = app.getMenu("TStatistic");
		String up = app.getMenu("UpperLimit");
		String low = app.getMenu("LowerLimit");
		String me = app.getMenu("MarginOfError");
		String SE = app.getMenu("StandardError.short");
		String level = app.getMenu("ConfidenceLevel");
		String df = app.getMenu("DegreeofFreedom.short");
		String stats = app.getMenu("Statistics") ;
		String se = app.getMenu("StandardError");
		
		
		String sample1 = app.getMenu("Sample") + " " + 1;
		String sample2 = app.getMenu("Sample") + " " + 2;
		String meanDiff = app.getMenu("MeanDifference");
		String diffMean = app.getMenu("Difference of means");


		StringBuilder testSB = new StringBuilder();
		testSB.append(sample1);
		testSB.append("\n");
		testSB.append(sample2);
		testSB.append("\n");
		

		//taResult.setText(testSB.toString());

		lblTitle1.setText(app.getMenu("Sample1") + ": ");
		lblTitle2.setText(app.getMenu("Sample2") + ": ");		

		nullHypName = new String[2];
		nullHypName[MODE_TTEST_2MEANS] = app.getMenu("DifferenceOfMeans.short");
		nullHypName[MODE_TTEST_PAIRED] = app.getMenu("MeanDifference");


		String[] procedureName = new String[4];
		procedureName[MODE_TTEST_2MEANS] = app.getMenu("TTestDifferenceOfMeans");
		procedureName[MODE_TTEST_PAIRED] = app.getMenu("TTestPairedDifferences");
		procedureName[MODE_TINT_2MEANS] = app.getMenu("TEstimateDifferenceOfMeans");
		procedureName[MODE_TINT_PAIRED] = app.getMenu("TEstimatePairedDifferences");

		DefaultComboBoxModel model = new DefaultComboBoxModel(procedureName);
		cbProcedure.setModel(model);

		cbTitle1.removeAllItems();
		cbTitle2.removeAllItems();
		String[] dataTitles = statDialog.getDataTitles();
		if(dataTitles!= null){
			for(int i=0; i < dataTitles.length; i++){
				cbTitle1.addItem(dataTitles[i]);
				cbTitle2.addItem(dataTitles[i]);
			}
		}

		lblNull.setText(app.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(app.getMenu("AlternativeHypothesis") + ": ");

		lblCI.setText("Interval Estimate");
		lblIntLevel.setText(app.getMenu("ConfidenceLevel") + ": ");

		cbIntOptions.removeAllItems();
		cbIntOptions.addItem("90%");
		cbIntOptions.addItem("95%");
		cbIntOptions.addItem("98%");
		cbIntOptions.addItem("99%");
		cbIntOptions.addItem("99.9%");

		btnCalc.setText(app.getMenu("Calculate"));

		//tabbedPane.setTitleAt(0, app.getMenu("Sample"));
		//tabbedPane.setTitleAt(1, app.getMenu("Test"));
		//tabbedPane.setTitleAt(2, app.getMenu("Estimation"));
		resultPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Result")));

		ckEqualVariances.setText(app.getMenu("EqualVariance"));
		
		updateGUI();
	}


	private JPanel createMainPanel(){

		// components
		cbTitle1 = new JComboBox();
		cbTitle2 = new JComboBox();
		cbProcedure = new JComboBox();
		cbProcedure.addActionListener(this);

		lblTitle1 = new JLabel();
		lblTitle2 = new JLabel();

		ckEqualVariances = new JCheckBox();
		
		JRadioButton btnLeft = new JRadioButton("<");
		JRadioButton btnRight = new JRadioButton(">");
		JRadioButton btnTwo = new JRadioButton(ExpressionNode.strNOT_EQUAL);
		ButtonGroup group = new ButtonGroup();
		group.add(btnLeft);
		group.add(btnRight);
		group.add(btnTwo);

		lblNull = new JLabel();
		lblHypParameter = new JLabel();
		lblTailType = new JLabel();

		fldNullHyp = new MyTextField(app.getGuiManager());
		fldNullHyp.setColumns(4);

		lblCI = new JLabel();
		lblIntLevel = new JLabel();
		cbIntOptions = new JComboBox();

		btnCalc = new JButton();

		taResult = new JTextArea();
		taResult.setFont(app.getPlainFont());

		// sample panel
		//	JPanel samplePanel = boxYPanel(flowPanel(lblTitle1,cbTitle1), flowPanel(lblTitle2,cbTitle2));
		//	JPanel tabPanelSample = blPanel(null,null,null,samplePanel,null);


		// test panel		
		Box testPanel =  boxYPanel(
				flowPanel(lblNull, lblHypParameter, fldNullHyp ),
				flowPanel(lblTailType, btnLeft, btnRight, btnTwo)
		);

		//CI panel		
		Box intPanel = boxYPanel(
				flowPanel(lblIntLevel, cbIntOptions) 
		);
		
		cardProcedure = new JPanel(new CardLayout());
		cardProcedure.add("testPanel", testPanel);
		cardProcedure.add("intPanel", intPanel);

		JPanel calcPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		calcPanel.add(btnCalc);
		
		Box samplePanel =  boxYPanel(
				flowPanel(lblTitle1,cbTitle1), 
				flowPanel(lblTitle2,cbTitle2)
				);
			
	//	tabbedPane = new JTabbedPane();
	//	tabbedPane.addTab(" ", samplePanel);
	//	tabbedPane.addTab(" ", testPanel);
	//	tabbedPane.addTab(" ", intPanel);
		
		
		resultPanel = blPanel(new JScrollPane(taResult));	
			
		Box procedurePanel =  boxYPanel(
				samplePanel,
				flowPanel(this.cbProcedure),
				cardProcedure,
				flowPanel(ckEqualVariances));
		
	

		// main panel
		JPanel mainPanel = blPanel(resultPanel, null, null, procedurePanel,null);
		//	mainPanel.setBorder(BorderFactory.createEtchedBorder());
		return mainPanel;

	}


	private void updateGUI(){

		if( mode == MODE_TTEST_2MEANS 
				|| mode ==  MODE_TTEST_PAIRED)
		{
			((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "testPanel");
			lblHypParameter.setText(nullHypName[mode] + " = " );
		} else{
			((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "intPanel");
		}

	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == cbProcedure){
			mode = cbProcedure.getSelectedIndex();
			updateGUI();
		}
	}






	//============================================================
	//            Utilities
	//============================================================


	private JPanel flowPanel(JComponent... comp){
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for(int i = 0; i<comp.length; i++){
			p.add(comp[i]);
		}
		//	p.setBackground(Color.white);
		return p;
	}

	private Box boxYPanel(JComponent... comp){
		Box p = Box.createVerticalBox();
		for(int i = 0; i<comp.length; i++){
			p.add(Box.createVerticalGlue());
			p.add(comp[i]);
		}
		return p;
	}


	private JPanel blPanel(Component center){
		return blPanel( center, null, null, null, null);
	}
	private JPanel blPanel(Component center, Component north, Component south, Component west, Component east){
		JPanel p = new JPanel(new BorderLayout());
		if(center != null)
			p.add(center, BorderLayout.CENTER);
		if(north != null)
			p.add(north, BorderLayout.NORTH);
		if(south != null)
			p.add(south, BorderLayout.SOUTH);
		if(west != null)
			p.add(west, BorderLayout.WEST);
		if(east != null)
			p.add(east, BorderLayout.EAST);

		//	p.setBackground(Color.white);
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

	public void updateFonts(Font font) {
		// TODO Auto-generated method stub
		
	}




	public void setLabels() {
		// TODO Auto-generated method stub
		
	}




	public void updatePanel() {
		// TODO Auto-generated method stub
		
	}





}
