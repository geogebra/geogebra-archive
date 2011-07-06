package geogebra.gui.view.spreadsheet.statdialog;

import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoList;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.inference.TTestImpl;

public class OneVarInferencePanel extends JPanel implements ActionListener,  FocusListener, StatPanelInterface {

	// ggb fields
	private Application app;
	private Kernel kernel;
	private StatDialog statDialog;

	// GUI
	private JLabel lblHypParameter, lblTailType, lblNull, lblConfLevel;
	private JButton btnCalculate;
	private MyTextField fldNullHyp;
	private JPanel cardProcedure;
	private JPanel resultPanel;
	private MyTextField fldConfLevel;
	private JLabel lblResultHeader;
	private JRadioButton btnLeft, btnRight, btnTwo;
	private JComboBox cbAlternativeHyp;

	// test type (tail)
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNode.strNOT_EQUAL;
	private String tail = tail_two;

	// input fields
	private double confLevel = .95, hypMean = 0;

	// statistics
	double t, P, df, lower, upper, mean, se, me, N;
	private TTestImpl tTestImpl;
	private TDistributionImpl tDist;


	private int selectedPlot = StatComboPanel.PLOT_TINT;
	private boolean isIniting;
	private int altHyp;
	private StatTable resultTable;

	/**
	 * Construct a OneVarInference panel
	 */
	public OneVarInferencePanel(Application app, StatDialog statDialog){

		isIniting = true;
		this.app = app;
		this.kernel = app.getKernel();
		this.statDialog = statDialog;

		this.setLayout(new BorderLayout());
		this.add(createMainPanel(), BorderLayout.CENTER);

		this.setMinimumSize(new Dimension(50,50));
		this.setLabels();
		isIniting = false;

	}
	/***********************************/



	//============================================================
	//           Create GUI 
	//============================================================


	private JPanel createMainPanel(){

		// components

		btnLeft = new JRadioButton(tail_left);
		btnRight = new JRadioButton(tail_right);
		btnTwo = new JRadioButton(tail_two);
		ButtonGroup group = new ButtonGroup();
		group.add(btnLeft);
		group.add(btnRight);
		group.add(btnTwo);
		btnLeft.addActionListener(this);
		btnRight.addActionListener(this);
		btnTwo.addActionListener(this);
		btnTwo.setSelected(true);

		cbAlternativeHyp = new JComboBox();
		cbAlternativeHyp.addActionListener(this);


		lblNull = new JLabel();
		lblHypParameter = new JLabel();
		lblTailType = new JLabel();

		fldNullHyp = new MyTextField(app.getGuiManager());
		fldNullHyp.setColumns(4);
		fldNullHyp.setText("" + 0);
		fldNullHyp.addActionListener(this);
		fldNullHyp.addFocusListener(this);

		lblConfLevel = new JLabel();
		fldConfLevel = new MyTextField(app.getGuiManager());
		fldConfLevel.setColumns(4);
		fldConfLevel.addActionListener(this);
		fldConfLevel.addFocusListener(this);

		btnCalculate = new JButton();

		

		// Result panel
		resultTable = new StatTable();
		setResultTable();
		resultTable.setBorder(BorderFactory.createEtchedBorder());
		lblResultHeader = new JLabel();
		resultPanel = new JPanel(new BorderLayout());
		resultPanel.add(resultTable, BorderLayout.NORTH);
		resultPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


		// Test panel	
		Box testPanel =  boxYPanel(
				flowPanel(lblNull), 
				flowPanel(Box.createRigidArea(new Dimension(10,0)), lblHypParameter, fldNullHyp),
				flowPanel(lblTailType),
				flowPanel(Box.createRigidArea(new Dimension(10,0)), cbAlternativeHyp)
		);


		// CI panel		
		
		Box intPanel = boxYPanel(
				flowPanel(lblConfLevel, fldConfLevel)
		);

		cardProcedure = new JPanel(new CardLayout());
		cardProcedure.add("testPanel", testPanel);
		cardProcedure.add("intervalPanel", flowPanel(lblConfLevel, fldConfLevel));

		((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "testPanel");

		JPanel procedurePanel = blPanel(null, cardProcedure, null, null, null);
		procedurePanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		
	//	Box subMainPanel = boxYPanel(procedurePanel,resultPanel);
		
	//	subMainPanel.add(procedurePanel, BorderLayout.NORTH);
	//	subMainPanel.add(resultPanel, BorderLayout.CENTER);

	//	JPanel mainPanel = new JPanel(new BorderLayout());
	//	mainPanel.add(procedurePanel, BorderLayout.WEST);
	//	mainPanel.add(resultPanel, BorderLayout.EAST);

		JPanel mainPanel = flowPanel(procedurePanel, resultPanel);

		return mainPanel;

	}


	private void  setResultTable(){

		switch (selectedPlot){
		case StatComboPanel.PLOT_ZTEST:
		case StatComboPanel.PLOT_TTEST:
		{
			String[] rowNames = 
			{
					app.getMenu("PValue"),
					app.getMenu("TStatistic"),
					"",
					app.getMenu("StandardError.short")
			};

			resultTable.setStatTable(rowNames.length, rowNames, 1, null);
		}
			break;

		case StatComboPanel.PLOT_ZINT:
		case StatComboPanel.PLOT_TINT:
		{
			String[] rowNames2 = 
			{
					app.getMenu("LowerLimit"),
					app.getMenu("LowerLimit"),
					app.getMenu("Mean"),
					app.getMenu("MarginOfError"),
					"",
					app.getMenu("StandardError.short")
			};
			resultTable.setStatTable(rowNames2.length, rowNames2, 1, null);
			break;
		}
		};

	}


	private void updateResultTable(){
		
		NumberFormat nf = statDialog.getNumberFormat();
		DefaultTableModel model = resultTable.getModel();

		evaluate();
		
		switch (selectedPlot){
		case StatComboPanel.PLOT_ZTEST:
		case StatComboPanel.PLOT_TTEST:
			
			model.setValueAt(nf.format(P),0,0);
			model.setValueAt(nf.format(t), 1, 0);
			model.setValueAt("", 2, 0);
			model.setValueAt(nf.format(se), 3, 0);
			break;

		case StatComboPanel.PLOT_ZINT:
		case StatComboPanel.PLOT_TINT:

			model.setValueAt(nf.format(lower),0,0);
			model.setValueAt(nf.format(upper), 1, 0);
			model.setValueAt(nf.format(mean), 2, 0);
			model.setValueAt(nf.format(me), 3, 0);
			model.setValueAt("", 4, 0);
			model.setValueAt(nf.format(se), 5, 0);

			break;
		};


	}










	//============================================================
	//           Updates and Event Handlers
	//============================================================

	public void updateFonts(Font font) {
		// not needed 
		// ... font updates handled by recursive call in StatDialog
	}

	public void setLabels() {

		lblHypParameter.setText(app.getMenu("HypothesizedMean.short") + " = " );
		lblNull.setText(app.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(app.getMenu("AlternativeHypothesis") + ": ");
		lblConfLevel.setText(app.getMenu("ConfidenceLevel") + ": ");
		lblResultHeader.setText(app.getMenu("Result"));

		btnCalculate.setText(app.getMenu("Calculate"));
		//resultPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("Result")));
		repaint();
	}


	/** Helper method for updateGUI() */
	private void updateNumberField(JTextField fld,  double n){
		NumberFormat nf = statDialog.getNumberFormat();
		fld.removeActionListener(this);
		fld.setText(nf.format(n));
		//fld.setCaretPosition(0);
		fld.addActionListener(this);
	}

	private void updateGUI(){

		// swap card panels
		switch (selectedPlot){
		case StatComboPanel.PLOT_ZTEST:
		case StatComboPanel.PLOT_TTEST:
			((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "testPanel");
			break;

		case StatComboPanel.PLOT_ZINT:
		case StatComboPanel.PLOT_TINT:	
			((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "intervalPanel");
			break;
		}

		updateNumberField(fldNullHyp, hypMean);
		updateNumberField(fldConfLevel, confLevel);
		updateCBAlternativeHyp();
		setResultTable();
		updateResultTable();		
	}


	private void updateCBAlternativeHyp(){

		NumberFormat nf = statDialog.getNumberFormat();

		cbAlternativeHyp.removeAllItems();
		cbAlternativeHyp.addItem(app.getMenu("HypothesizedMean.short") + " " + tail_right + " " + nf.format(hypMean));
		cbAlternativeHyp.addItem(app.getMenu("HypothesizedMean.short") + " " + tail_left + " " + nf.format(hypMean));
		cbAlternativeHyp.addItem(app.getMenu("HypothesizedMean.short") + " " + tail_two + " " + nf.format(hypMean));
		cbAlternativeHyp.setSelectedIndex(altHyp);
	}


	public void actionPerformed(ActionEvent e) {
		if(isIniting) return;
		Object source = e.getSource();	

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}
		
		else if(source == cbAlternativeHyp){
			
			if(cbAlternativeHyp.getSelectedIndex() == 0)
				tail = tail_right;
			else if(cbAlternativeHyp.getSelectedIndex() == 1)
				tail = tail_left;
			else
				tail = tail_two;

			evaluate();
			updateGUI();
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
		if(isIniting) return;

		Double value = Double.parseDouble(source.getText().trim());

		if(source == fldConfLevel){
			confLevel = value;
			evaluate();
			updateGUI();
		}

		else if(source == fldNullHyp){
			hypMean = value;
			evaluate();
			updateGUI();
		}
	}


	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField)(e.getSource()));
	}


	public void setSelectedPlot(int selectedPlot){
		this.selectedPlot = selectedPlot;
		updateGUI();
	}

	public void updatePanel(){
		//evaluate();
		updateGUI();
		updateResultTable();
	}


	


	//============================================================
	//          Computation
	//============================================================


	private void evaluate(){

		GeoList dataList = statDialog.getStatDialogController().getDataSelected();
		double[] val = statDialog.getStatDialogController().getValueArray(dataList);

		mean = StatUtils.mean(val);
		N = val.length;
		se = Math.sqrt(StatUtils.variance(val)/N);
		df = N-1;

		if(tTestImpl == null)
			tTestImpl = new TTestImpl();

		try {
			t = tTestImpl.t(hypMean, val);
			P = tTestImpl.tTest(hypMean, val);
			P = adjustedPValue(P, t, tail);

			tDist = new TDistributionImpl(N - 1);
			double tCritical = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
			me  =  tCritical * se;
			upper = mean + me;
			lower = mean - me;


		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}

	}

	private double getCmdResult(String cmd, String label){
		return evaluateExpression(cmd + "[" + label + "]");
	}


	private double adjustedPValue(double p, double testStatistic, String tail){

		// two sided test
		if(tail.equals(tail_two)) 
			return p;

		// one sided test
		else if((tail.equals(tail_right) && testStatistic > 0)
				|| (tail.equals(tail_left) && testStatistic < 0))
			return p/2;
		else
			return 1 - p/2;
	}


	protected double evaluateExpression(String expr){

		NumberValue nv;

		try {
			nv = kernel.getAlgebraProcessor().evaluateToNumeric(expr, false);
		} catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}	
		return nv.getDouble();
	}



	//============================================================
	//           GUI  Utilities
	//============================================================


	private JPanel flowPanel(Component... comp){
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


}
