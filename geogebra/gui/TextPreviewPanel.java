package geogebra.gui;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.AlgoDependentText;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;

import java.awt.Color;
import java.awt.Font;

/**
 * 
 * Extension of EuclidianView that can be used to preview GeoText while editing.
 * 
 * The class maintains two hidden geos (previewGeoIndependent and
 * previewGeoDependent) that are used to preview the two possible types of
 * GeoText, independent and dependent.
 * 
 * TODO: adjust the size of the panel when the bounding box of the preview geo
 * grows larger than the enclosing JScrollpane viewport. Currently the preview
 * geo uses absolute screen coords, so we can't easily get the bounding box
 * dimensions.
 * 
 * @author gsturr 2010-6-30
 * 
 */
public class TextPreviewPanel extends EuclidianView {

	private EuclidianController ec;
	private static boolean[] showAxes = { false, false };
	private static boolean showGrid = false;
	private GeoText previewGeoIndependent, previewGeoDependent;
	private boolean enableUpdateSize = true; // needed ?

	private AlgoDependentText textAlgo;
	private Construction cons ;
	private boolean isIndependent;


	public TextPreviewPanel(Kernel kernel) {

		super(new EuclidianController(kernel), showAxes, showGrid);
		this.ec = this.getEuclidianController();

		this.cons = kernel.getConstruction();

		// set EV display properties 
		setAntialiasing(true);
		setAllowShowMouseCoords(false);
		setAxesCornerCoordsVisible(false);
		updateFonts();
		updateSize();

		// remove EV mouse listeners
		removeMouseListener(ec);
		removeMouseMotionListener(ec);
		removeMouseWheelListener(ec);

	}




	/**
	 * Removes the preview geos
	 */
	public void removePreviewGeoText() {
		if(previewGeoIndependent != null)
			previewGeoIndependent.remove();
		if(previewGeoDependent != null){
			previewGeoDependent.remove();
			textAlgo.remove();
		}
	}



	/**
	 * Updates the preview geos and creates new geos if needed.
	 * Changes are determined by the inputValue string and the visual style of the targetGeo. 
	 *  
	 * @param targetGeo
	 * @param inputValue
	 * @param isLaTeX
	 */
	public void updatePreviewText(GeoText targetGeo, String inputValue, boolean isLaTeX) {

		// initialize variables
		ExpressionNode exp = null;
		String formattedInput = formatInputValue(inputValue);		
		boolean hasParseError = false;
		boolean showErrorMessage = false;
		isIndependent = false;

		// create previewGeoIndependent 
		if (previewGeoIndependent == null){
			previewGeoIndependent = new GeoText(kernel.getConstruction());
			previewGeoIndependent.addView(this);
			add(previewGeoIndependent);
		}


		// parse the input text 
		try{
			exp = (ExpressionNode) kernel.getParser().parseGeoGebraExpression(formattedInput);
		}

		catch (Exception e) {
			isIndependent = true;
			hasParseError = true;  	
			if(formattedInput.length() > 0) // no error message if we have an empty string
				showErrorMessage = true;
			//Application.debug("parse exception");
		} 
		catch (Error e) {
			isIndependent = true;
			hasParseError = true;  // odd numbers of quotes give parse errors 
			showErrorMessage = true;
			//Application.debug("parse error");
		}



		// resolve variables
		if(!(hasParseError)){
			try
			{
				exp.resolveVariables();
				isIndependent = exp.isConstant();
			} 
			catch (Error e) {
				// if an error is thrown here then we must have independent text
				// this is not considered an error, so don't show an error message
				isIndependent = true;
				//Application.debug("resolve error");
			} 
			catch (Exception e) {
				showErrorMessage = true;
				isIndependent = true;
				//Application.debug("resolve exception");
			}
		}


		// create the previewGeoText

		// case1: independent text based on string only, including error messages
		if (isIndependent) 
		{
			//System.out.println("is independent");

			// remove quote characters
			formattedInput = formattedInput. replaceAll("\"", "");

			// set the text
			if(showErrorMessage)
				previewGeoIndependent.setTextString(app.getError("InvalidInput"));
			else
				previewGeoIndependent.setTextString(formattedInput);

			// update the display style
			updateVisualProperties(previewGeoIndependent, targetGeo, isLaTeX, showErrorMessage);
		} 


		// case 2: dependent GeoText, needs AlgoDependentText	
		else
		{
			//System.out.println("is not independent");

			if(previewGeoDependent != null){
				previewGeoDependent.remove();
				textAlgo.remove();
			}

			// create new  previewGeoDependent 
			textAlgo = new AlgoDependentText(cons, exp);
			cons.removeFromConstructionList(textAlgo);
			previewGeoDependent = textAlgo.getGeoText();
			previewGeoDependent.addView(this);
			add(previewGeoDependent);

			// set the display style
			updateVisualProperties(previewGeoDependent, targetGeo, isLaTeX, showErrorMessage);
		}

		// hide/show the preview geos  
		previewGeoIndependent.setEuclidianVisible(isIndependent);
		previewGeoIndependent.updateRepaint();
		if(previewGeoDependent !=null){
			previewGeoDependent.setEuclidianVisible(!isIndependent);
			previewGeoDependent.updateRepaint();
		}

	}

	/**
	 * Sets the visual properties of a preview geo
	 */
	private void updateVisualProperties(GeoText geo, GeoText targetGeo, boolean isLaTeX, boolean isErrorMessage ){

		// set error message style
		if(isErrorMessage)
		{
			geo.setVisualStyle(cons.getConstructionDefaults().getDefaultGeo(GeoElement.GEO_CLASS_TEXT));
			geo.setObjColor(Color.red);
			geo.setBackgroundColor(Color.white);
			//geo.setFontSize(app.getFontSize());
			geo.setFontStyle(Font.ITALIC);
			geo.setLaTeX(false, true);
		}

		// set text style
		else
		{	
			if(targetGeo != null){
				geo.setVisualStyle(targetGeo);
			} else {
				if (isLaTeX) geo.setSerifFont(true);
				geo.setObjColor(Color.black);
			}
			geo.setLaTeX(isLaTeX, true);
		}

		// set geo position in upper left corner (it might need changing after isLaTeX change)
		locateTextGeo(geo);

	}



	/**
	 * Positions the preview geo in the upper left corner of the panel
	 * two settings are needed to account for differences in the way
	 * LaTeX and standard text is drawn
	 */
	private void locateTextGeo(GeoText geo) {
		int xInset = 4;
		int yInset = geo.isLaTeX() ? 4 : 4 + app.getFontSize();
		geo.setAbsoluteScreenLocActive(true);
		geo.setAbsoluteScreenLoc( xInset, yInset);
	}


	/**
	 * Prepares the inputValue string for the parser
	 * 
	 *  TODO: not sure this is really needed, review the code
	 */
	private String formatInputValue(String inputValue){

		// if inputValue is null then use the current definition
		if(inputValue == null){
			System.out.println("=== null input === ");
			if(previewGeoIndependent.isIndependent()){ 
				inputValue = previewGeoIndependent.getTextString();
				if(previewGeoIndependent.getKernel().lookupLabel(inputValue) != null)
					inputValue = "\"" + inputValue + "\"";            		 		
			}
			else{
				inputValue = previewGeoIndependent.getCommandDescription(); 
			}


			// inputValue is not null (code copied from TextInputHandler ... inner class in TextInputDialog)		
		} else {
			//System.out.println("process input: " + inputValue);
			// no quotes?
			if (inputValue.indexOf('"') < 0) {
				// this should become either
				// (1) a + "" where a is an object label or
				// (2) "text", a plain text 

				// ad (1) OBJECT LABEL 
				// add empty string to end to make sure
				// that this will become a text object
				if (kernel.lookupLabel(inputValue.trim()) != null) {
					inputValue = "(" + inputValue + ") + \"\"";
					//isIndependent = false;
					System.out.println("lookpup label found" + inputValue);
				} 

				// ad (2) PLAIN TEXT
				// add quotes to string
				else {
					//inputValue = "\"" + inputValue + "\"";
				}        			
			} 
			else {
				// replace \n\" by \"\n, this is useful for e.g.:
				//    "a = " + a + 
				//	"b = " + b 
				inputValue = inputValue.replaceAll("\n\"", "\"\n");
			}
		}

		return inputValue;
	}


}
