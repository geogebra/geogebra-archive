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
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math.stat.inference.TTestImpl;
import org.apache.commons.math.util.FastMath;

public class TwoVarInferencePanel extends JPanel implements ActionListener, FocusListener, StatPanelInterface{

	private Application app;
	private StatDialog statDialog;
	private StatTable resultTable;


	private JList dataSourceList;
	private DefaultListModel model;

	private JComboBox cbTitle1, cbTitle2, cbAltHyp;
	private JLabel lblTitle1, lblTitle2, lblHypParameter, lblTailType,
	lblNull, lblCI, lblConfLevel, lblResultHeader;
	private JButton btnCalc;
	private MyTextField fldNullHyp;
	private JPanel cardProcedure, resultPanel;
	private JCheckBox ckEqualVariances;
	private MyTextField fldConfLevel;


	private int selectedPlot = StatComboPanel.PLOT_TINT_2MEANS;

	// test type (tail)
	private static final String tail_left = "<";
	private static final String tail_right = ">";
	private static final String tail_two = ExpressionNode.strNOT_EQUAL;
	private String tail = tail_two;


	// input fields
	private double confLevel = .95, hypMean = 0;


	// statistics
	double t, P, df, lower, upper, mean, se, me, n1, n2, diffMeans,  mean1, mean2;
	private TTestImpl tTestImpl;
	private TDistributionImpl tDist;
	private boolean pooled = false;

	private boolean isIniting;


	/**
	 * Construct a TwoVarInference panel
	 */
	public TwoVarInferencePanel(Application app, StatDialog statDialog){
		isIniting = true;
		this.app = app;
		this.statDialog = statDialog;
		this.setLayout(new BorderLayout());
		this.add(createMainPanel(), BorderLayout.NORTH);
		this.setMinimumSize(new Dimension(50,50));
		this.setLabels();
		isIniting = false;
	}





	//============================================================
	//           Create GUI 
	//============================================================



	private JPanel createMainPanel(){

		// components
		cbTitle1 = new JComboBox();
		cbTitle2 = new JComboBox();
		cbTitle1.addActionListener(this);
		cbTitle2.addActionListener(this);

		lblTitle1 = new JLabel();
		lblTitle2 = new JLabel();

		ckEqualVariances = new JCheckBox();

		cbAltHyp = new JComboBox();
		cbAltHyp.addActionListener(this);


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


		lblResultHeader = new JLabel();

		// Result panel
		resultTable = new StatTable();
		setResultTable();
		resultTable.setBorder(BorderFactory.createEtchedBorder());
		lblResultHeader = new JLabel();
		resultPanel = new JPanel(new BorderLayout());
		resultPanel.add(resultTable, BorderLayout.WEST);
		resultPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


		// Test panel	
		Box testPanel =  boxYPanel(
				flowPanel(lblNull, lblHypParameter, fldNullHyp),
				flowPanel(lblTailType, cbAltHyp)
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
				flowPanel(lblTitle1,cbTitle1, lblTitle2,cbTitle2)
		);


		Box procedurePanel =  boxYPanel(
				samplePanel,
				cardProcedure,
				flowPanel(ckEqualVariances),
				resultPanel);

		// main panel
		JPanel mainPanel = blPanel(null,procedurePanel,null,null,null);
		//JPanel mainPanel = blPanel(resultPanel, null, null, procedurePanel,null);
		//	mainPanel.setBorder(BorderFactory.createEtchedBorder());
		return mainPanel;

	}



	//============================================================
	//           Updates and Event Handlers
	//============================================================


	private void updateGUI(){


		// swap card panels
		switch (selectedPlot){
		case StatComboPanel.PLOT_TTEST_2MEANS:
		case StatComboPanel.PLOT_TTEST_PAIRED:
			((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "testPanel");
			lblHypParameter.setText(getNullHypName() + " = " );
			break;

		case StatComboPanel.PLOT_TINT_2MEANS:
		case StatComboPanel.PLOT_TINT_PAIRED:	
			((CardLayout)cardProcedure.getLayout()).show(cardProcedure, "intPanel");
			break;
		}

		ckEqualVariances.removeActionListener(this);
		ckEqualVariances.setVisible(
				selectedPlot == StatComboPanel.PLOT_TINT_2MEANS
				|| selectedPlot == StatComboPanel.PLOT_TTEST_2MEANS);
		ckEqualVariances.setSelected(pooled);
		ckEqualVariances.addActionListener(this);

		updateNumberField(fldNullHyp, hypMean);
		updateNumberField(fldConfLevel, confLevel);
		updateCBAlternativeHyp();
		updateResultTable();

	}


	/** Helper method for updateGUI() */
	private void updateNumberField(JTextField fld,  double n){
		NumberFormat nf = statDialog.getNumberFormat();
		fld.removeActionListener(this);
		fld.setText(nf.format(n));
		//fld.setCaretPosition(0);
		fld.addActionListener(this);
	}


	private void setTitleComboBoxes(){

		cbTitle1.removeActionListener(this);
		cbTitle2.removeActionListener(this);

		cbTitle1.removeAllItems();
		cbTitle2.removeAllItems();
		String[] dataTitles = statDialog.getDataTitles();
		if(dataTitles!= null){
			for(int i=0; i < dataTitles.length; i++){
				cbTitle1.addItem(dataTitles[i]);
				cbTitle2.addItem(dataTitles[i]);
			}
		}
		cbTitle1.setSelectedIndex(0);
		cbTitle2.setSelectedIndex(1);

		cbTitle1.addActionListener(this);
		cbTitle2.addActionListener(this);

	}



	private void updateCBAlternativeHyp(){

		int selectedIndex = cbAltHyp.getSelectedIndex();
		NumberFormat nf = statDialog.getNumberFormat();
		cbAltHyp.removeActionListener(this);
		cbAltHyp.removeAllItems();
		cbAltHyp.addItem(getNullHypName() + " " + tail_right + " " + nf.format(hypMean));
		cbAltHyp.addItem(getNullHypName() + " " + tail_left + " " + nf.format(hypMean));
		cbAltHyp.addItem(getNullHypName() + " " + tail_two + " " + nf.format(hypMean));
		if(tail == tail_right)
			cbAltHyp.setSelectedIndex(0);
		else if(tail == tail_left)
			cbAltHyp.setSelectedIndex(1);
		else
			cbAltHyp.setSelectedIndex(2);
		cbAltHyp.addActionListener(this);
	}




	public void setSelectedPlot(int selectedPlot){
		this.selectedPlot = selectedPlot;
		if(!isIniting){
			this.setResultTable();
		}
		updateGUI();
	}

	public void updateFonts(Font font) {
		// TODO Auto-generated method stub

	}


	private String getNullHypName(){

		if(selectedPlot == StatComboPanel.PLOT_TTEST_2MEANS)
			return app.getMenu("DifferenceOfMeans.short");
		else if(selectedPlot == StatComboPanel.PLOT_TTEST_PAIRED)
			return app.getMenu("MeanDifference");
		else
			return "";


	}


	public void setLabels() {

		lblTitle1.setText(app.getMenu("Sample1") + ": ");
		lblTitle2.setText(app.getMenu("Sample2") + ": ");		

		lblNull.setText(app.getMenu("NullHypothesis") + ": ");
		lblTailType.setText(app.getMenu("AlternativeHypothesis") + ": ");

		lblCI.setText("Interval Estimate");
		lblConfLevel.setText(app.getMenu("ConfidenceLevel") + ": ");

		btnCalc.setText(app.getMenu("Calculate"));

		ckEqualVariances.setText(app.getMenu("EqualVariance"));

	}


	public void updatePanel(){

		updateGUI();
		//updateResultTable();
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}

		else if(source == cbAltHyp){
			if(cbAltHyp.getSelectedIndex() == 0)
				tail = tail_right;
			else if(cbAltHyp.getSelectedIndex() == 1)
				tail = tail_left;
			else
				tail = tail_two;
			updateResultTable();
		}

		else if(source == cbTitle1 || source == cbTitle2){
			updateResultTable();
		}

		else if(source == ckEqualVariances){
			pooled = ckEqualVariances.isSelected();
			updateResultTable();
		}

	}

	private void doTextFieldActionPerformed(JTextField source) {
		if(isIniting) return;

		Double value = Double.parseDouble(source.getText().trim());

		if(source == fldConfLevel){
			confLevel = value;
			updateGUI();
		}

		if(source == fldNullHyp){
			hypMean = value;
			updateGUI();
		}

	}

	public void focusGained(FocusEvent e) {}

	public void focusLost(FocusEvent e) {
		doTextFieldActionPerformed((JTextField)(e.getSource()));
	}


	private void  setResultTable(){

		setTitleComboBoxes();


		switch (selectedPlot){
		case StatComboPanel.PLOT_TTEST_2MEANS:
		case StatComboPanel.PLOT_TTEST_PAIRED:
		{
			String[] columnNames = 
			{
					app.getMenu("PValue"),
					app.getMenu("TStatistic"),
					"",
					app.getMenu("StandardError.short"),
					app.getMenu("DegreesOfFreedom.short")
			};

			resultTable.setStatTable(1, null, columnNames.length, columnNames);
		}
		break;

		case StatComboPanel.PLOT_TINT_2MEANS:
		case StatComboPanel.PLOT_TINT_PAIRED:
		{
			String[] columnNames2 = 
			{
					app.getMenu("ConfidenceInterval"),
					app.getMenu("LowerLimit"),
					app.getMenu("UpperLimit"),

					"",
					app.getMenu("StandardError.short"),
					app.getMenu("DegreesOfFreedom.short")

			};
			resultTable.setStatTable(1, null, columnNames2.length, columnNames2);
			break;
		}
		};

	}


	private void updateResultTable(){

		NumberFormat nf = statDialog.getNumberFormat();
		DefaultTableModel model = resultTable.getModel();

		evaluate();

		switch (selectedPlot){
		case StatComboPanel.PLOT_TTEST_2MEANS:
		case StatComboPanel.PLOT_TTEST_PAIRED:

			model.setValueAt(nf.format(P),0,0);
			model.setValueAt(nf.format(t), 0,1);
			model.setValueAt("", 0,2);
			model.setValueAt(nf.format(se), 0,3);
			model.setValueAt(nf.format(df), 0,4);
			break;

		case StatComboPanel.PLOT_TINT_2MEANS:
		case StatComboPanel.PLOT_TINT_PAIRED:

			String cInt = nf.format(mean) + " \u00B1 "  + nf.format(me);
			model.setValueAt(cInt,0,0);
			model.setValueAt(nf.format(lower), 0, 1);
			model.setValueAt(nf.format(upper), 0, 2);
			model.setValueAt("", 0, 3);
			model.setValueAt(nf.format(se), 0, 4);
			model.setValueAt(nf.format(df), 0, 5);

			break;
		};


	}




	//============================================================
	//          Evaluate 
	//============================================================

	private void evaluate(){

		// get the sample data

		GeoList dataCollection = statDialog.getStatDialogController().getDataSelected();

		GeoList dataList1 = (GeoList) dataCollection.get(cbTitle1.getSelectedIndex());
		double[] sample1 = statDialog.getStatDialogController().getValueArray(dataList1);
		SummaryStatistics stats1 = new SummaryStatistics();
		for (int i = 0; i < sample1.length; i++) {
			stats1.addValue(sample1[i]);
		}

		GeoList dataList2 = (GeoList) dataCollection.get(cbTitle2.getSelectedIndex());
		double[] sample2 = statDialog.getStatDialogController().getValueArray(dataList2);
		SummaryStatistics stats2 = new SummaryStatistics();
		for (int i = 0; i < sample2.length; i++) {
			stats2.addValue(sample2[i]);
		}


		if(tTestImpl == null)
			tTestImpl = new TTestImpl();
		double tCritical;

		try {

			switch (selectedPlot){
			case StatComboPanel.PLOT_TTEST_2MEANS:
			case StatComboPanel.PLOT_TINT_2MEANS:

				// get statistics
				mean1 = StatUtils.mean(sample1);
				mean2 = StatUtils.mean(sample2);
				diffMeans = mean1 - mean2;
				n1 = sample1.length;
				n2 = sample2.length;
				double v1 = stats1.getVariance();
				double v2 = stats2.getVariance();
				df =  getDegreeOfFreedom( v1, v2, n1, n2, pooled);

				if(pooled){			
					double pooledVariance = ((n1  - 1) * v1 + (n2 -1) * v2 ) / (n1 + n2 - 2);
					se = Math.sqrt(pooledVariance * (1d / n1 + 1d / n2));
				}
				else
					se = Math.sqrt((v1 / n1) + (v2 / n2));


				// get confidence interval
				tDist = new TDistributionImpl(df);
				tCritical = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
				me = tCritical*se;
				upper = diffMeans + me;
				lower = diffMeans - me;

				// get test results
				if(pooled){
					t = tTestImpl.homoscedasticT(sample1, sample2);
					P = tTestImpl.homoscedasticTTest(sample1, sample2);
				}else{
					t = tTestImpl.t(sample1, sample2);
					P = tTestImpl.tTest(sample1, sample2);
				}
				P = adjustedPValue(P, t, tail);

				break;


			case StatComboPanel.PLOT_TTEST_PAIRED:
			case StatComboPanel.PLOT_TINT_PAIRED:

				// get statistics
				n1 = sample1.length;
				double meanDifference = StatUtils.meanDifference(sample1, sample2);
				se = Math.sqrt(StatUtils.varianceDifference(sample1, sample2, meanDifference)/n1);
				df = n1 - 1;

				tDist = new TDistributionImpl(df);
				tCritical = tDist.inverseCumulativeProbability((confLevel + 1d)/2);
				me = tCritical*se;
				upper = diffMeans + me;
				lower = diffMeans - me;

				// get test results
				t = meanDifference/se;    
				P = 2.0 * tDist.cumulativeProbability(-Math.abs(t));
				P = adjustedPValue(P, t, tail);

				break;
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
