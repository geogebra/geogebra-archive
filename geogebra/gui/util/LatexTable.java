package geogebra.gui.util;

import geogebra.gui.TextInputDialog;
import geogebra.gui.view.algebra.InputPanel;
import geogebra.gui.view.spreadsheet.MyTable;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;

public class LatexTable extends SelectionTable implements MenuElement{

	public final static String [] miscSymbols = {

		"\\#",
		"\\&",
		"\\prime",
		"\\backprime",

		"\\angle",
		"\\measuredangle",
		"\\sphericalangle",

		"\\nabla",
		"\\neg",
		"\\nexists",

		"\\varnothing",
		"\\emptyset",
		"\\exists",
		"\\forall",
		"\\infty",
		"\\surd",
		"\\top",
		"\\bot",
		"\\diagdown",
		"\\diagup",

		"\\bigstar",
		"\\lozenge",
		"\\blacklozenge",
		"\\square",
		"\\blacksquare",
		"\\triangle",
		"\\triangledown",
		"\\blacktriangle",
		"\\blacktriangledown",


		"\\spadesuit",
		"\\clubsuit",
		"\\diamondsuit",
		"\\heartsuit",
		"\\flat",
		"\\natural",
		"\\sharp",

	};



	public final static String [] roots_fractions = {

		"\\frac{a}{b}",
		"x^{a}",
		"x_{a}",
		"\\sqrt{x}",
		"\\sqrt[n]{x}",
		"\\binom{a}{b}",

	};



	public final static String [] sums = {
		
		"\\sum{ }",
		"\\sum_{a}^{b}{ }",
		"\\int{ }",
		"\\int_{a}^{b}{ }",
		"\\oint{ }" ,
		"\\oint_{a}^{b}{ }"

	};



	public final static String [] accents = {

		"\\acute{x}",
		"\\grave{x}",
		"\\tilde{x}",
		"\\bar{x}",
		"\\breve{x}",
		"\\check{x}",
		"\\hat{x}",
		"\\vec{x}",
		"\\dot{x}",
		"\\ddot{x}",
		"\\dddot{x}",
		"\\mathring{x}",

	};


	public final static String [] accentsExtended = {

		"\\overline{xx}",
		"\\underline{xx}",
		"\\overbrace{xx}",
		"\\underbrace{xx}",
		"\\overleftarrow{xx}",
		"\\underleftarrow{xx}",
		"\\overrightarrow{xx}",
		"\\underrightarrow{xx}",
		"\\overleftrightarrow{xx}",
		"\\underleftrightarrow{xx}",

		"\\widetilde{xx} ",
		"\\widehat{xx}"

	};

	public final static String [] brackets = {

		"\\left(   \\right) ",
		"\\left [  \\right ] ",
		"\\left\\lbrace  \\right\\rbrace ",
		"\\left\\vert  \\right\\vert ",
		"\\left\\Vert  \\right\\Vert ",
		"\\left\\langle  \\right\\rangle ",
		"\\left\\lceil   \\right\\rceil  ",
		"\\left\\lfloor  \\right\\rfloor ",
		"\\left\\lgroup  \\right\\rgroup ",
		"\\left\\lmoustache  \\right\\rmoustache "

	};



	public final static String [] matrices = {

		"\\begin{array}{} a & b & c \\end{array} ",

		"\\begin{array}{} a \\\\ b \\\\ c \\end{array} ",

		"\\begin{array}{} a & b \\\\ c & d \\\\ \\end{array} ",					

		"\\begin{array}{} a & b & c \\\\ d & e & f \\\\ g & h & i \\\\ \\end{array}",						


	};




	public final static String [] operators = {

		"\\pm",
		"\\mp",
		"\\times",
		"\\div",
		"\\cdot",
		"\\ast",
		"\\star",
		"\\dagger",
		"\\ddagger",
		"\\amalg",
		"\\cap",
		"\\cup",
		"\\uplus",
		"\\sqcap",
		"\\sqcup",
		"\\vee",
		"\\wedge",
		"\\oplus",
		"\\ominus",
		"\\otimes",
		"\\circ",
		"\\bullet",
		"\\diamond",
		"\\lhd",
		"\\rhd",
		"\\unlhd",
		"\\unrhd",
		"\\oslash",
		"\\odot",
		"\\bigcirc",
		"\\triangleleft",
		"\\Diamond",
		"\\bigtriangleup",
		"\\bigtriangledown",
		"\\Box",
		"\\triangleright",
		"\\setminus",
		"\\wr"

	};

	public final static String [] relations = {

		"\\le", 
		"\\ge", 
		"\\neq", 
		"\\sim", 
		"\\ll", 
		"\\gg", 
		"\\doteq", 
		"\\simeq", 
		"\\subset", 
		"\\supset", 
		"\\approx", 
		"\\asymp", 
		"\\subseteq", 
		"\\supseteq", 
		"\\cong", 
		"\\smile", 
		"\\sqsubset", 
		"\\sqsupset", 
		"\\equiv", 
		"\\frown", 
		"\\sqsubseteq", 
		"\\sqsupseteq", 
		"\\propto", 
		"\\bowtie", 
		"\\in", 
		"\\ni", 
		"\\prec", 
		"\\succ", 
		"\\vdash", 
		"\\dashv", 
		"\\preceq", 
		"\\succeq", 
		"\\models", 
		"\\perp", 
		"\\parallel", 
		"\\|", 
		"\\mid"

	};

	public final static String [] negations = {
		"\\nmid", 
		"\\nleq", 
		"\\ngeq",
		"\\nsim", 
		"\\ncong", 
		"\\nparallel",
		"\\not<", 
		"\\not>", 
		"\\not=",
		"\\not\\le", 
		"\\not\\ge", 
		"\\not\\sim",
		"\\not\\approx", 
		"\\not\\cong", 
		"\\not\\equiv",
		"\\not\\parallel", 
		"\\nless", 
		"\\ngtr",
		"\\lneq", 
		"\\gneq", 
		"\\lnsim",
		"\\lneqq", 
		"\\gneqq", 

	};


	public final static String [] arrows = {

		"\\xleftarrow{xx}",
		"\\xrightarrow{xx}",

		"\\leftarrow",
		"\\rightarrow",
		"\\leftrightarrow",

		"\\Leftarrow",
		"\\Rightarrow",
		"\\Leftrightarrow",

		"\\longleftarrow",
		"\\longrightarrow",
		"\\longleftrightarrow",

		"\\Longleftarrow",
		"\\Longrightarrow",
		"\\Longleftrightarrow",


		"\\mapsto",
		"\\longmapsto",

		"\\hookleftarrow",
		"\\hookrightarrow",

		"\\leftharpoonup",
		"\\leftharpoondown",
		"\\rightharpoonup",
		"\\rightharpoondown",
		"\\rightleftharpoons",

		"\\leadsto",
		"\\uparrow",
		"\\downarrow",
		"\\updownarrow",

		"\\Uparrow",		
		"\\Downarrow",
		"\\Updownarrow",

		"\\nearrow",
		"\\searrow",
		"\\swarrow",
		"\\nwarrow",


	};



	private Application app;
	private TextInputDialog inputDialog;
	private String[] latexArray;
	private PopupMenuButton popupButton;
	private int caretPosition = 0;

	public LatexTable(Application app, TextInputDialog textInputDialog, PopupMenuButton popupButton, 
			String[] latexArray, int rows, int columns, int mode ){

		super(app, latexArray, rows,columns, new Dimension(24,24), mode);
		this.app = app;
		this.inputDialog = textInputDialog;
		this.latexArray = latexArray;
		this.popupButton = popupButton;
		//setShowGrid(true);
		setHorizontalAlignment(SwingConstants.CENTER);
		setSelectedIndex(0);
		//	this.setUseColorSwatchBorder(true);
		this.setShowGrid(true);
		this.setGridColor(MyTable.TABLE_GRID_COLOR);
		this.setBorder(BorderFactory.createLineBorder(MyTable.TABLE_GRID_COLOR));
		//this.setBorder(BorderFactory.createEmptyBorder());
	}

	public void setCaretPosition(int caretPosition){
		this.caretPosition = caretPosition;
	}
	 
	
	
	// support for MenuElement interface

	public Component getComponent() {
		return this;
	}

	public MenuElement[] getSubElements() {
		return new MenuElement[0];
	}

	public void menuSelectionChanged(boolean arg0) {
	}

	public void processKeyEvent(KeyEvent arg0, MenuElement[] arg1, MenuSelectionManager arg2) {
	}

	public void processMouseEvent(MouseEvent arg0, MenuElement[] arg1, MenuSelectionManager arg2) {

		if(this.getSelectedIndex() >= latexArray.length) return;

		if (arg0.getID()==MouseEvent.MOUSE_RELEASED){
			StringBuffer sb = new StringBuffer(latexArray[this.getSelectedIndex()]);
			String selText = ((InputPanel)inputDialog.getInputPanel()).getSelectedText();		
			if (selText != null) {
				sb.deleteCharAt(sb.indexOf("{")+1);
				sb.insert(sb.indexOf("{")+1, selText);
			}

			inputDialog.insertString(sb.toString());
			//inputDialog.setRelativeCaretPosition(caretPosition);
			popupButton.handlePopupActionEvent();
		}	
	}

}
