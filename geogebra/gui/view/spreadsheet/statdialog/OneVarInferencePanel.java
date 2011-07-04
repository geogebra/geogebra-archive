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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
	private JTextArea taResult;
	private JPanel cardProcedure;
	private JPanel resultPanel;
	private MyTextField fldConfLevel;
	private JLabel lblResultHeader;
	private JRadioButton btnLeft, btnRight, btnTwo;
	

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

	
	private int selectedPlot;
	private boolean isIniting;

	/**
	 * Construct a OneVarInference panel
	 */
	public OneVarInferencePanel(Application app, StatDialog statDialog){

		isIniting = true;
		this.app = app;
		this.kernel = app.getKernel();
		this.statDialog = statDialog;

		this.setLayout(new BorderLayout());
		this.add(createMainPanel(), BorderLayout.NORTH);

		this.setMinimumSize(new Dimension(50,50));
		//this.setBorder(BorderFactory.createEtchedBorder());
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
		taResult = new JTextArea("");
		taResult.setFont(app.getPlainFont());
		taResult.setLineWrap(true);
		taResult.setEditable(false);
		taResult.setRows(6);
		taResult.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(5,5,5,5)));

		//taResult.setPreferredSize(new Dimension(-1,60));
		//	JScrollPane resultScroller = new JScrollPane(taResult); 
		//	resultScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		lblResultHeader = new JLabel();
	//	resultPanel = blPanel(taResult,null, flowPanel(btnCalculate),null,null);	
		resultPanel = blPanel(taResult);	
		resultPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

		
		// Test panel		
		Box testPanel =  boxYPanel(
				flowPanel(lblNull),
				flowPanel(lblHypParameter, fldNullHyp ),
				flowPanel(lblTailType),
				flowPanel(btnLeft, btnRight, btnTwo)
		);


		// CI panel		
		Box intPanel = boxYPanel(
				flowPanel(lblConfLevel),
				flowPanel(fldConfLevel) 
		);

		cardProcedure = new JPanel(new CardLayout());
		cardProcedure.add("testPanel", testPanel);
		cardProcedure.add("intervalPanel", intPanel);

		((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "testPanel");

		JPanel procedurePanel = blPanel(null, cardProcedure,null, null, null);
		
		JPanel subMainPanel = new JPanel(new BorderLayout());
		subMainPanel.add(procedurePanel, BorderLayout.WEST);
		subMainPanel.add(resultPanel, BorderLayout.CENTER);

		JPanel mainPanel = blPanel(null,subMainPanel,null,null,null);


		return subMainPanel;

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
		updateResultPanel();

	}

	public void actionPerformed(ActionEvent e) {
		if(isIniting) return;
		Object source = e.getSource();	

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}
		else if(source == btnLeft || source == btnRight || source == btnTwo){
			tail = ((JRadioButton)source).getText();
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

		if(source == fldNullHyp){
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
		evaluate();
		updateGUI();
	}


	public void updateResultPanel(){

		NumberFormat nf = statDialog.getNumberFormat();
		evaluate();

		String strP = app.getMenu("PValue");
		String strTestStat = app.getMenu("TStatistic");
		String strUpper = app.getMenu("UpperLimit");
		String strLower = app.getMenu("LowerLimit");
		String strME = app.getMenu("MarginOfError");
		String strSE = app.getMenu("StandardError.short");
		String strConfLevel = app.getMenu("ConfidenceLevel");
		String strDF = app.getMenu("DegreesOfFreedom.short");
		String statLabel = app.getMenu("Statistics") ;
		//String se = app.getMenu("StandardError");


		StringBuilder sb = new StringBuilder();
		String sep = " = ";

		switch (selectedPlot){
		case StatComboPanel.PLOT_ZTEST:
		case StatComboPanel.PLOT_TTEST:

			sb.append(strP + sep + nf.format(P));
			sb.append("\n");
			sb.append("\n");

			sb.append(strTestStat + sep + nf.format(t));
			sb.append("\n");
			sb.append(strDF + sep + nf.format(df));
			sb.append("\n");
			sb.append(strSE + sep + nf.format(se));

			break;

		case StatComboPanel.PLOT_ZINT:
		case StatComboPanel.PLOT_TINT:

			sb.append(nf.format(mean) + " \u00B1 " + nf.format(me));
			sb.append("\n");
			sb.append(strLower + sep + nf.format(lower));
			sb.append("\n");
			sb.append(strUpper + sep + nf.format(upper));
			sb.append("\n");

			sb.append("\n");
			sb.append(strDF + sep + nf.format(df));
			sb.append("\n");
			sb.append(strSE + sep + nf.format(se));

			break;

		}

		taResult.setText(sb.toString());
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


}
