package geogebra.gui.view.spreadsheet;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.virtualkeyboard.MyTextField;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoText;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class RegressionPanel extends JPanel implements  ActionListener{


	private Application app;
	private StatDialog statDialog;

	// regression panel objects
	private JLabel lblRegEquation, lblEqn;
	private JLabel lblTitleX, lblTitleY;
	private MyTextField fldTitleX, fldTitleY;
	private JComboBox cbRegression, cbPolyOrder;
	private JButton btnSwapXY;
	private JLabel lblEvaluate;
	private MyTextField fldInputX;
	private JLabel lblOutputY;

	private String[] regressionLabels;
	private MyTextField fldOutputY;
	private boolean isIniting = true;
	private JPanel predictionPanel;


	public RegressionPanel(Application app, StatDialog statDialog){

		this.app = app;
		this.statDialog = statDialog;
		this.setLayout(new BorderLayout());
		this.add(createRegressionPanel(), BorderLayout.CENTER);
		setLabels();
		updateRegressionPanel();
		updateGUI();
		isIniting = false;
	}

	private JPanel regressionPanel;
	private JPanel createRegressionPanel(){

		// components
		String[] orders = {"2","3","4","5","6","7","8","9"};
		cbPolyOrder = new JComboBox(orders);
		cbPolyOrder.setSelectedIndex(0);
		cbPolyOrder.addActionListener(this);
		cbPolyOrder.setFocusable(false);
		
		regressionLabels = new String[StatDialog.regressionTypes];
		setRegressionLabels();
		cbRegression = new JComboBox(regressionLabels);
		cbRegression.addActionListener(this);
		cbRegression.setFocusable(false);
		
		lblTitleX = new JLabel();
		lblTitleY = new JLabel();

		fldTitleX = new MyTextField(app.getGuiManager());
		fldTitleY = new MyTextField(app.getGuiManager());
	//	fldTitleX.setColumns(15);
	//	fldTitleY.setColumns(15);

		lblRegEquation = new JLabel();
		lblEqn = new JLabel();

		btnSwapXY = new JButton();
		btnSwapXY.setSelected(false);
		btnSwapXY.setMaximumSize(btnSwapXY.getPreferredSize());
		btnSwapXY.addActionListener(this);
		btnSwapXY.setFocusable(false);
		
		// panels
		JPanel titlePanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0,5,0,5);
		GridBagConstraints cHFill = new GridBagConstraints();
		cHFill.fill = GridBagConstraints.HORIZONTAL;
		cHFill.insets = c.insets;
		cHFill.weightx=1;
		
		titlePanel.add(lblTitleX,c);
		titlePanel.add(fldTitleX,cHFill);
		titlePanel.add(lblTitleY,c);
		titlePanel.add(fldTitleY,cHFill);
	//	titlePanel.add(btnSwapXY,c);
		titlePanel.setBorder(BorderFactory.createEtchedBorder());
		//JPanel titlePanel = new JPanel(new BorderLayout());
		//titlePanel.add(titleXPanel, BorderLayout.CENTER);

		JPanel cbPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		cbPanel.add(cbRegression);
		cbPanel.add(cbPolyOrder);

		JPanel eqnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		eqnPanel.add(lblRegEquation);

		JPanel swapPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		swapPanel.add(btnSwapXY);
		
		
		
		
		JPanel modelPanel = new JPanel(new BorderLayout());
		modelPanel.add(cbPanel, BorderLayout.WEST);
		modelPanel.add(eqnPanel, BorderLayout.CENTER);
		JScrollPane scroller = new JScrollPane(modelPanel);
		scroller.setBorder(BorderFactory.createEmptyBorder());

		createPredictionPanel();
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(predictionPanel, BorderLayout.CENTER);
		southPanel.add(swapPanel, BorderLayout.WEST);
		
		regressionPanel = new JPanel(new BorderLayout());
		regressionPanel.add(scroller,BorderLayout.CENTER);
		regressionPanel.add(southPanel, BorderLayout.SOUTH);
		regressionPanel.setBorder(BorderFactory.createTitledBorder(app.getMenu("RegressionModel")));


		JPanel mainPanel = new JPanel(new BorderLayout());

		//mainPanel.add(titlePanel, BorderLayout.SOUTH);
		mainPanel.add(regressionPanel, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createEtchedBorder());

		return mainPanel;
	}


	/** Creates a panel to evaluate the regression model for a given x value
	 */
	private void createPredictionPanel(){
		
		predictionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		lblEvaluate = new JLabel();	
		fldInputX = new MyTextField(app.getGuiManager());
		fldInputX.addActionListener(this);

		fldInputX.setColumns(6);
		lblOutputY = new JLabel();
		fldOutputY = new MyTextField(app.getGuiManager());

		fldOutputY.setColumns(6);
		fldOutputY.setEditable(false);

		predictionPanel.add(lblEvaluate);
		predictionPanel.add(new JLabel("x = "));
		predictionPanel.add(fldInputX);
		predictionPanel.add(new JLabel("y = "));
		predictionPanel.add(lblOutputY);
		predictionPanel.add(fldOutputY);

	}



	public void updateRegressionPanel(){
		fldTitleX.setText(statDialog.getDataTitles()[0]);
		fldTitleY.setText(statDialog.getDataTitles()[1]);
		setRegressionEquationLabel();
		doTextFieldActionPerformed(fldInputX);
		updateGUI();
		
	}

	private void setRegressionLabels(){	
		regressionLabels[StatDialog.REG_NONE] = app.getMenu("None");
		regressionLabels[StatDialog.REG_LINEAR] = app.getMenu("Linear");
		regressionLabels[StatDialog.REG_LOG] = app.getMenu("Log");
		regressionLabels[StatDialog.REG_POLY] = app.getMenu("Polynomial");
		regressionLabels[StatDialog.REG_POW] = app.getMenu("Power");
		regressionLabels[StatDialog.REG_EXP] = app.getMenu("Exponential");
		regressionLabels[StatDialog.REG_SIN] = app.getMenu("Sin");
		regressionLabels[StatDialog.REG_LOGISTIC] = app.getMenu("Logistic");	
	}


	/**
	 * Sets the labels according to current locale 
	 */
	public void setLabels(){		
		regressionLabels = new String[StatDialog.regressionTypes];
		setRegressionLabels();
		
		//we need to remove old labels from combobox and we don't want the listener to
		//be operational since it will call unnecessary Construction updates
		int j = cbRegression.getSelectedIndex(); 
		ActionListener al = cbRegression.getActionListeners()[0];
		cbRegression.removeActionListener(al);
		cbRegression.removeAllItems();
		for(int i=0;i<regressionLabels.length;i++)
			cbRegression.addItem(regressionLabels[i]);
		cbRegression.setSelectedIndex(j);
		cbRegression.addActionListener(al);		
		((TitledBorder)regressionPanel.getBorder()).setTitle(app.getMenu("RegressionModel"));
		lblEqn.setText(app.getMenu("Equation")+ ":");
		lblTitleX.setText(app.getMenu("Column.X") + ": ");
		lblTitleY.setText(app.getMenu("Column.Y") + ": ");
		
		String swapString = app.getMenu("Column.X") + " \u21C6 " + app.getMenu("Column.Y");
		//btnSwapXY.setIcon(GeoGebraIcon.createLatexIcon(app, swapString, app.getPlainFont(), false, Color.BLACK, null)); 
		btnSwapXY.setFont(app.getPlainFont());
		btnSwapXY.setText(swapString);
		lblEvaluate.setText(app.getMenu("Evaluate")+ ": ");
	
	}



	/**
	 * Draws the regression equation into the regression equation JLabel icon  
	 */
	public void setRegressionEquationLabel(){

		// get the LaTeX string for the regression equation 

		String eqn;
		//GeoElement geoRegression = statDialog.getRegressionModel();

		try {

			// no regression 
			if(statDialog.getRegressionMode() == StatDialog.REG_NONE || statDialog.getRegressionModel() == null){
				eqn = app.getPlain("");
			}

			// linear 
			else if(statDialog.getRegressionMode() == StatDialog.REG_LINEAR){
				((GeoLine)statDialog.getRegressionModel()).setToExplicit();	
				eqn = statDialog.getRegressionModel().getFormulaString(ExpressionNode.STRING_TYPE_LATEX, true);
			}

			// nonlinear
			else{
				eqn = "y = " + statDialog.getRegressionModel().getFormulaString(ExpressionNode.STRING_TYPE_LATEX, true);		
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			eqn = "\\text{" + app.getPlain("NotAvailable") + "}";

		}

		//System.out.println("============>" + eqn);
		// create an icon with the LaTeX string	
		ImageIcon icon = (ImageIcon) lblRegEquation.getIcon();
		if(icon == null)
			icon = new ImageIcon();

		icon = GeoGebraIcon.createLatexIcon(app, eqn, this.getFont(), false, Color.black, null);


		// set the label icon with our equation string
		lblRegEquation.setIcon(icon);
		lblRegEquation.revalidate();

		updateGUI();
	}



	private void updateGUI(){

		cbPolyOrder.setVisible(statDialog.getRegressionMode() == StatDialog.REG_POLY);	
		predictionPanel.setVisible(!(statDialog.getRegressionMode() == StatDialog.REG_NONE));	
		repaint();		

	}



	public void actionPerformed(ActionEvent e) {

		Object source  = e.getSource();

		if (source instanceof JTextField) {
			doTextFieldActionPerformed((JTextField)source);
		}	

		else if(source == cbRegression){			
			statDialog.setRegressionMode(cbRegression.getSelectedIndex());
			statDialog.setRegressionGeo();
			updateRegressionPanel();
		}

		else if(source == cbPolyOrder){
			statDialog.setRegressionOrder(cbPolyOrder.getSelectedIndex() + 2);
			statDialog.setRegressionGeo();
			setRegressionEquationLabel();
		}
		
		else if(source == btnSwapXY){
			statDialog.swapXY();
			// clear the prediction panel
			fldInputX.setText("");
			fldOutputY.setText("");
		}


	}

	 
	private void doTextFieldActionPerformed(JTextField source) {
		if(isIniting ) return;

		if(source == fldInputX){
			try {
				String inputText = source.getText().trim();
				if(inputText == null || inputText.length() == 0) return;
				
				NumberValue nv;
				nv = app.getKernel().getAlgebraProcessor().evaluateToNumeric(inputText, true);		
				double value = nv.getDouble();

				String str = "\"\" + " + statDialog.getRegressionModel().getLabel() + "(" + value + ")";
				GeoText text = app.getKernel().getAlgebraProcessor().evaluateToText(str, false);
				fldOutputY.setText(text.getTextString());

			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
