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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.HashMap;

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

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.stat.inference.TTestImpl;

public class TwoVarInferencePanel extends JPanel implements ActionListener, FocusListener, StatPanelInterface{

	private Application app;
	private StatDialog statDialog;

	private JList dataSourceList;
	private DefaultListModel model;

	private JComboBox cbTitle1, cbTitle2;
	private JLabel lblTitle1, lblTitle2, lblHypParameter, lblTailType,
	lblNull, lblCI, lblConfLevel, lblResultHeader;
	private JButton btnCalc;
	private MyTextField fldNullHyp;
	private JTextArea taResult;
	private JPanel cardProcedure, resultPanel;
	private JCheckBox ckEqualVariances;
	private JRadioButton btnLeft, btnRight, btnTwo;
	
	
	private HashMap<Integer,String> nullHypName;
	private int selectedPlot;

	
	// test type (tail)
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNode.strNOT_EQUAL;
	private String tail = tail_two;
	private boolean isIniting;
	private MyTextField fldConfLevel;
	
	
	// input fields
	private double confLevel = .95, hypMean = 0;

	
	// statistics
	double t, P, df, lower, upper, mean, se, me, n1, n2;
	private TTestImpl tTestImpl;
	private TDistributionImpl tDist;
	private double mean1;
	private boolean pooled = false;
	
	
	
	
	/**
	 * Construct a TwoVarInference panel
	 */
	public TwoVarInferencePanel(Application app, StatDialog statDialog){
		isIniting = true;
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
		this.setMinimumSize(new Dimension(50,50));
		this.setLabels();
		
		isIniting = false;
		
		//updateGUI();
	}

	/***********************************/



	//============================================================
	//           Create GUI 
	//============================================================



	private JPanel createMainPanel(){

		// components
		cbTitle1 = new JComboBox();
		cbTitle2 = new JComboBox();
		

		lblTitle1 = new JLabel();
		lblTitle2 = new JLabel();

		ckEqualVariances = new JCheckBox();
		
		
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

		//??????????
		lblCI = new JLabel();
		
		btnCalc = new JButton();

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
				flowPanel(lblConfLevel, fldConfLevel) 
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
				
		resultPanel = blPanel(new JScrollPane(taResult));	
			
		Box procedurePanel =  boxYPanel(
				samplePanel,
				cardProcedure,
				flowPanel(ckEqualVariances));
		
		// main panel
		JPanel mainPanel = blPanel(resultPanel, null, null, procedurePanel,null);
		//	mainPanel.setBorder(BorderFactory.createEtchedBorder());
		return mainPanel;

	}

	

	//============================================================
	//           Updates and Event Handlers
	//============================================================

	
	private void updateGUI(){
	
		
		cbTitle1.removeAllItems();
		cbTitle2.removeAllItems();
		String[] dataTitles = statDialog.getDataTitles();
		if(dataTitles!= null){
			for(int i=0; i < dataTitles.length; i++){
				cbTitle1.addItem(dataTitles[i]);
				cbTitle2.addItem(dataTitles[i]);
			}
		}
		
		
		// swap card panels
		switch (selectedPlot){
		case StatComboPanel.PLOT_TTEST_2MEANS:
		case StatComboPanel.PLOT_TTEST_PAIRED:
			((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "testPanel");
			lblHypParameter.setText(nullHypName.get(selectedPlot) + " = " );
			break;

		case StatComboPanel.PLOT_TINT_2MEANS:
		case StatComboPanel.PLOT_TINT_PAIRED:	
			((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "intPanel");
			break;
		}

		updateNumberField(fldNullHyp, hypMean);
		updateNumberField(fldConfLevel, confLevel);
		updateResultPanel();
		
	}

	
	/** Helper method for updateGUI() */
	private void updateNumberField(JTextField fld,  double n){
		NumberFormat nf = statDialog.getNumberFormat();
		fld.removeActionListener(this);
		fld.setText(nf.format(n));
		//fld.setCaretPosition(0);
		fld.addActionListener(this);
	}
	
	
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
	}


	public void setSelectedPlot(int selectedPlot){
		this.selectedPlot = selectedPlot;
		updateGUI();
	}

	public void updateFonts(Font font) {
		// TODO Auto-generated method stub
		
	}

	public void setLabels() {
		
		lblTitle1.setText(app.getMenu("Sample1") + ": ");
		lblTitle2.setText(app.getMenu("Sample2") + ": ");		

		nullHypName = new HashMap<Integer,String>();
		nullHypName.put(StatComboPanel.PLOT_TTEST_2MEANS, app.getMenu("DifferenceOfMeans.short"));
		nullHypName.put(StatComboPanel.PLOT_TTEST_PAIRED, app.getMenu("MeanDifference"));	
		
		lblNull.setText(app.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(app.getMenu("AlternativeHypothesis") + ": ");

		lblCI.setText("Interval Estimate");
		lblConfLevel.setText(app.getMenu("ConfidenceLevel") + ": ");

		btnCalc.setText(app.getMenu("Calculate"));

		ckEqualVariances.setText(app.getMenu("EqualVariance"));
		
	}


	public void updatePanel(){
		evaluate();
		updateGUI();
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

		String sample1 = app.getMenu("Sample") + " " + 1;
		String sample2 = app.getMenu("Sample") + " " + 2;
		String meanDiff = app.getMenu("MeanDifference");
		String diffMean = app.getMenu("Difference of means");


		StringBuilder sb = new StringBuilder();
		String sep = " = ";
		
		switch (selectedPlot){
		case StatComboPanel.PLOT_TTEST_2MEANS:
		case StatComboPanel.PLOT_TTEST_PAIRED:

			sb.append(strP + sep + nf.format(P));
			sb.append("\n");
			sb.append("\n");

			sb.append(strTestStat + sep + nf.format(t));
			sb.append("\n");
			sb.append(strDF + sep + nf.format(df));
			sb.append("\n");
			sb.append(strSE + sep + nf.format(se));

			break;

		case StatComboPanel.PLOT_TINT_2MEANS:
		case StatComboPanel.PLOT_TINT_PAIRED:

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

		GeoList dataCollection = statDialog.getStatDialogController().getDataSelected();
		
		GeoList dataList1 = (GeoList) dataCollection.get(cbTitle1.getSelectedIndex());
		double[] val1 = statDialog.getStatDialogController().getValueArray(dataList1);
		SummaryStatistics stats1 = new SummaryStatistics();
		for (int i = 0; i < val1.length; i++) {
			stats1.addValue(val1[i]);
		}
		
		GeoList dataList2 = (GeoList) dataCollection.get(cbTitle2.getSelectedIndex());
		double[] val2 = statDialog.getStatDialogController().getValueArray(dataList2);
		SummaryStatistics stats2 = new SummaryStatistics();
		for (int i = 0; i < val2.length; i++) {
			stats2.addValue(val2[i]);
		}
		
	
		if(tTestImpl == null)
			tTestImpl = new TTestImpl();

		
		mean1 = StatUtils.mean(val1);
		n1 = val1.length;
		n2 = val2.length;
		double v1 = stats1.getVariance();
		double v2 = stats2.getVariance();
		double se;
		
		try {

			df =  getDegreeOfFreedom( v1, v2, n1, n2, pooled);
			
			if(pooled){			
				double pooledVariance = ((n1  - 1) * v1 + (n2 -1) * v2 ) / (n1 + n2 - 2);
				se = Math.sqrt(pooledVariance * (1d / n1 + 1d / n2));
			}
			else
				se = Math.sqrt((v1 / n1) + (v2 / n2));
		
			tDist = new TDistributionImpl(df);
			
			double tCritical = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
			
			me = tCritical*se;
			upper = mean + me;
			lower = mean - me;
			
			
			if(pooled){
				t = tTestImpl.homoscedasticT(val1, val2);
				P = tTestImpl.homoscedasticTTest(val1, val2);
				P = adjustedPValue(P, t, tail);
			}else{
				t = tTestImpl.t(val1, val2);
				P = tTestImpl.tTest(val1, val2);
				P = adjustedPValue(P, t, tail);
			}


		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MathException e) {
			e.printStackTrace();
		}

	}
	
	
	
// TODO: Validate !!!!!!!!!!!
	
	
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


	/**
	 * Computes approximate degrees of freedom for 2-sample t-estimate.
	 * (code from Apache commons, TTestImpl class)
	 *
	 * @param v1 first sample variance
	 * @param v2 second sample variance
	 * @param n1 first sample n
	 * @param n2 second sample n
	 * @return approximate degrees of freedom
	 */
	private double getDegreeOfFreedom(double v1, double v2, double n1, double n2, boolean pooled) {

		if(pooled)
			return n1 + n2 - 2;

		else
			return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2))) /
			((v1 * v1) / (n1 * n1 * (n1 - 1d)) + (v2 * v2) /
					(n2 * n2 * (n2 - 1d)));
	}


	/**
	 * Computes margin of error for 2-sample t-estimate; 
	 * this is the half-width of the confidence interval
	 * 
	 * @param v1 first sample variance
	 * @param v2 second sample variance
	 * @param n1 first sample n
	 * @param n2 second sample n
	 * @param confLevel confidence level
	 * @return margin of error for 2 mean interval estimate
	 * @throws MathException
	 */
	private double getMarginOfError(double v1, double n1, double v2, double n2, double confLevel, boolean pooled) throws MathException {

		if(pooled){
			
			double pooledVariance = ((n1  - 1) * v1 + (n2 -1) * v2 ) / (n1 + n2 - 2);
			double se = Math.sqrt(pooledVariance * (1d / n1 + 1d / n2));
			tDist = new TDistributionImpl(getDegreeOfFreedom(v1, v2, n1, n2, pooled));
			double a = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
			return a * se;
			
		
		}else{

			double se = Math.sqrt((v1 / n1) + (v2 / n2));
			tDist = new TDistributionImpl(getDegreeOfFreedom(v1, v2, n1, n2, pooled));
			double a = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
			return a * se;
		}

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
