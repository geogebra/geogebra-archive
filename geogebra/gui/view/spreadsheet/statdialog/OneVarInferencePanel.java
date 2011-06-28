package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
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

public class OneVarInferencePanel extends JPanel implements ActionListener,  StatPanelInterface {

	private Application app;
	private Kernel kernel;
	
	private GeoList dataList;

	private static final int MODE_ZTEST = 0;
	private static final int MODE_TTEST = 1;
	//private static final int MODE_SIGNTEST = 2;
	//private static final int MODE_VARTEST = 2;
	private static final int MODE_ZINT = 2;
	private static final int MODE_TINT = 3;
	//private static final int MODE_VARINT = 5;

	private int mode = MODE_TTEST;

	private JComboBox cbProcedure, cbIntOptions;
	private StatDialog statDialog;
	private JLabel lblHypParameter, lblTailType, lblNull, lblIntLevel;
	private JButton btnCalcTest;
	private MyTextField fldNullHyp;
	private JTextArea taResult;
	private JPanel cardProcedure;
	private JPanel resultPanel;
	private String[] nullHypName;
	


	public OneVarInferencePanel(Application app, GeoList dataList, StatDialog statDialog){

		this.app = app;
		this.kernel = app.getKernel();
		this.dataList = dataList;
		this.statDialog = statDialog;

		this.setLayout(new BorderLayout());
		this.add(createMainPanel(), BorderLayout.NORTH);
		this.setMinimumSize(new Dimension(10,10));
		this.setBorder(BorderFactory.createEtchedBorder());

	}


	public void updateOneVarPanel(){

		String P = app.getMenu("PValue");
		String t = app.getMenu("TStatistic");
		String up = app.getMenu("UpperLimit");
		String low = app.getMenu("LowerLimit");
		String me = app.getMenu("MarginOfError");
		String SE = app.getMenu("StandardError.short");
		String level = app.getMenu("ConfidenceLevel");
		
		String df = app.getMenu("DegreeofFreedom.short");
		String stats = app.getMenu("Statistics") ;
		
		String se = app.getMenu("StandardError");

		StringBuilder testSB = new StringBuilder();
		testSB.append(t + " = 123");
		testSB.append("\n");
		testSB.append(P + " = 123");
		testSB.append("\n");
		testSB.append(df + " = 123");
		testSB.append("\n");
		testSB.append("\n");
		testSB.append(low + " = 123");
		testSB.append("\n");
		testSB.append(up + " = 123");
		testSB.append("\n");
		testSB.append(123 + "\u00B1 " +0.5);
		testSB.append("\n");
		testSB.append(stats + ":");
		testSB.append("\n");
		testSB.append(SE + " = 123");
		

		taResult.setText(testSB.toString());

		nullHypName = new String[3];
		nullHypName[MODE_ZTEST] = app.getMenu("HypothesizedMean.short");
		nullHypName[MODE_TTEST] = app.getMenu("HypothesizedMean.short");
		
		
		String[] procedureName = new String[7];

		procedureName[MODE_ZTEST] = app.getMenu("ZMeanTest");
		procedureName[MODE_TTEST] = app.getMenu("TMeanTest");
		procedureName[MODE_ZINT] = app.getMenu("ZMeanInterval");
		procedureName[MODE_TINT] = app.getMenu("TMeanInterval");
	

		DefaultComboBoxModel model = new DefaultComboBoxModel(procedureName);
		cbProcedure.setModel(model);

		lblNull.setText(app.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(app.getMenu("AlternativeHypothesis") + ": ");

		
		lblIntLevel.setText(app.getMenu("ConfidenceLevel") + ": ");

		cbIntOptions.removeAllItems();
		cbIntOptions.addItem("90%");
		cbIntOptions.addItem("95%");
		cbIntOptions.addItem("98%");
		cbIntOptions.addItem("99%");
		cbIntOptions.addItem("99.9%");

		btnCalcTest.setText(app.getMenu("Calculate"));
		resultPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Result")));

	}



	private JPanel createMainPanel(){

		// components
		cbProcedure = new JComboBox();
		cbProcedure.addActionListener(this);

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

		lblIntLevel = new JLabel();
		cbIntOptions = new JComboBox();
		cbIntOptions.addActionListener(this);

		btnCalcTest = new JButton();

		taResult = new JTextArea();
		taResult.setFont(app.getPlainFont());
		taResult.setPreferredSize(new Dimension(60,60));

		// panels		
		Box testPanel =  boxYPanel(
				flowPanel(lblNull, lblHypParameter, fldNullHyp ),
				flowPanel(lblTailType, btnLeft, btnRight, btnTwo)
		);


		// CI panel		
		Box intPanel = boxYPanel(
				flowPanel(lblIntLevel, cbIntOptions) 
		);

		cardProcedure = new JPanel(new CardLayout());
		cardProcedure.add("testPanel", testPanel);
		cardProcedure.add("intPanel", intPanel);

		((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "testPanel");

		JPanel northPanel = blPanel(null,null, null, this.cbProcedure,null);	
		resultPanel = blPanel(new JScrollPane(taResult));	
		
		JPanel mainPanel = blPanel(resultPanel, northPanel, null, cardProcedure, null);

		return mainPanel;

	}


	private void updateGUI(){

		if( mode == MODE_ZTEST || mode ==  MODE_TTEST){
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

	private double evaluateExpression(String expr){

		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);	

		return nv.getDouble();
	}
	
	
	
	

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


	public void updateFonts(Font font) {
		// TODO Auto-generated method stub
		
	}


	public void setLabels() {
		// TODO Auto-generated method stub
		
	}


	public void updatePanel(GeoList selectedData) {
		// TODO Auto-generated method stub
		
	}



}
