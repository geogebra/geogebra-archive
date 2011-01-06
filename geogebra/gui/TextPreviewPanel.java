package geogebra.gui;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.AlgoDependentText;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoText;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.parser.TokenMgrError;

import java.awt.Color;
import java.awt.Dimension;

/**
 * 
 * Extension of EuclidianView that can be used to preview GeoText while editing. 
 * 
 * @author gsturr 2010-6-30
 *
 */
public class TextPreviewPanel extends EuclidianView {

	private EuclidianController ec;
	private static boolean[] showAxes = { false, false };
	private static boolean showGrid = false;
	private GeoText previewGeoText;
	private boolean enableUpdateSize = true; // needed ?
	
	private AlgoDependentText textAlgo;
	Construction cons ;
	//private boolean isIndependent;

	
	public TextPreviewPanel(Kernel kernel) {
		
		super(new EuclidianController(kernel), showAxes, showGrid);
		this.ec = this.getEuclidianController();

		this.cons = kernel.getConstruction();

		// set display properties 
		setAntialiasing(true);
		setAllowShowMouseCoords(false);
		setAxesCornerCoordsVisible(false);
		updateFonts();
		updateSize();
		
		// remove mouse listeners
		removeMouseListener(ec);
		removeMouseMotionListener(ec);
		removeMouseWheelListener(ec);
		
	}

	
	/**
	 * creates the preview GeoText, hides it from EV and puts it into
	 * this view
	 */
	private void createPreviewGeo(){

		// create and label the preview geo
		previewGeoText = new GeoText(kernel.getConstruction());
		previewGeoText.setLabel(null);

		// add the geo to this view and remove it from EV	
		previewGeoText.addView(this);
		this.add(previewGeoText);
		previewGeoText.removeView(app.getEuclidianView());
		app.getEuclidianView().remove(previewGeoText);
		previewGeoText.setEuclidianVisible(true);

		// position the text using absolute screen coordinates
		previewGeoText.setAbsoluteScreenLocActive(true);
		locateTextGeo();

	}

	
	/**
	 * positions the preview geo in the upper left corner of the panel
	 * two settings are needed to account for differences in the way
	 * LaTeX and standard text is drawn
	 */
	private void locateTextGeo() {
		int xInset = 4;
		int yInset = previewGeoText.isLaTeX() ? 4 : 4 + app.getFontSize();
		previewGeoText.setAbsoluteScreenLocActive(true);
		previewGeoText.setAbsoluteScreenLoc( xInset, yInset);
	}
	
	
	/**
	 * removes the preview geo
	 */
	public void removePreviewGeoText() {
		if(previewGeoText != null)
			previewGeoText.remove();
	}

	
	
	
	/**
	 * updates the preview geo by redefining it from the string inputValue;
	 * also copies visual style from targetGeo 
	 *  
	 * @param targetGeo
	 * @param inputValue
	 * @param isLaTeX
	 */
	public void updatePreviewText(GeoText targetGeo, String inputValue, boolean isLaTeX) {

		if(textAlgo != null)
			textAlgo.remove();
		
		if(previewGeoText != null)
			previewGeoText.remove();

		String formattedInput = formatInputValue(inputValue);
		

		try {
			
			boolean independent = false;
			
			try
			{
				ExpressionNode exp = (ExpressionNode) kernel.getParser().parseGeoGebraExpression(formattedInput);
				exp.resolveVariables();
				
				if (!exp.isConstant()) {
					System.out.println("is not independent");
					textAlgo = new AlgoDependentText(cons, exp);
					cons.removeFromConstructionList(textAlgo);
				} else {
					independent = true;
				}
				
				previewGeoText = textAlgo.getGeoText();
			} catch (Exception e) {
				independent = true;
			} catch (Error e) {
				independent = true;
			}
			
			if (independent) {
				// independent text
				System.out.println("is independent");

				
				// reuse previewGeoText if we can
				if (previewGeoText == null || !previewGeoText.isIndependent())
					previewGeoText = new GeoText(kernel.getConstruction());
				
				previewGeoText.setTextString(formattedInput);
			}
			

			// update the display style
			if(targetGeo != null){
				previewGeoText.setVisualStyle(targetGeo);
			} else {
				if (isLaTeX) previewGeoText.setSerifFont(true);
				previewGeoText.setObjColor(Color.black);
			}
			previewGeoText.setLaTeX(isLaTeX, true);

			locateTextGeo();
			previewGeoText.setEuclidianVisible(true);
			previewGeoText.addView(this);
			add(previewGeoText);

			
		} catch (Throwable t) {
			t.printStackTrace();
		}


	}



	private String formatInputValue(String inputValue){

		//isIndependent = true;

		// Fit quote characters to the inputValue string so that the algebra processor 
		// can use it to redefine the geo.

		// if inputValue is null then use the current definition
		if(inputValue == null){
			if(previewGeoText.isIndependent()){ 
				inputValue = previewGeoText.getTextString();
				if(previewGeoText.getKernel().lookupLabel(inputValue) != null)
					inputValue = "\"" + inputValue + "\"";            		 		
			}
			else{
				inputValue = previewGeoText.getCommandDescription(); 
				//isIndependent = false;
			}

		// inputValue is not null (code copied from TextInputHandler ... inner class in TextInputDialog)		
		} else {
			System.out.println("process input: " + inputValue);
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






	//=======================================================
	// old code

 
	public void updatePreviewTextOLD(GeoText targetGeo, String inputValue, boolean isLaTeX) {

		// create a new preview geo if needed
		if(previewGeoText == null)
			createPreviewGeo();

		// update the display style
		if(targetGeo != null){
			previewGeoText.setVisualStyle(targetGeo);
		} else {
			if (isLaTeX) previewGeoText.setSerifFont(true);
		}

		// Fit quote characters to the inputValue string so that the algebra processor 
		// can use it to redefine the geo.
		
		// if inputValue is null then use the current definition
		if(inputValue == null){
			if(previewGeoText.isIndependent()){ 
				inputValue = previewGeoText.getTextString();
				if(previewGeoText.getKernel().lookupLabel(inputValue) != null)
					inputValue = "\"" + inputValue + "\"";            		 		
			}
			else
				inputValue = previewGeoText.getCommandDescription(); 

		// inputValue is not null (code copied from TextInputHandler ... inner class in TextInputDialog)		
		} else {

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
				} 
				// ad (2) PLAIN TEXT
				// add quotes to string
				else {
					inputValue = "\"" + inputValue + "\"";
				}        			
			} 
			else {
				// replace \n\" by \"\n, this is useful for e.g.:
				//    "a = " + a + 
				//	"b = " + b 
				inputValue = inputValue.replaceAll("\n\"", "\"\n");
			}
		}

		
		// redefine the preview geo
		GeoText newText;
		try {
			newText = (GeoText) kernel.getAlgebraProcessor()
			.changeGeoElementNoExceptionHandling(previewGeoText,
					inputValue, true, false);

			if(newText != null)
				previewGeoText = newText;
			
			// make sure the correct LaTeX setting is used
			previewGeoText.setLaTeX(isLaTeX, true);
			
			// put the geo in the correct location (depends on LaTeX setting) 
			locateTextGeo();
			previewGeoText.setObjColor(Color.black);
			previewGeoText.updateRepaint();

			// make sure the geo is displayed in this view and not EV
			// this needs to be done here in case the geo was redefined as indepenedent
			// TODO: why does this kind of redefinition change the view settings?
			previewGeoText.addView(this);
			this.add(previewGeoText);
			previewGeoText.removeView(app.getEuclidianView());
			app.getEuclidianView().remove(previewGeoText);

		} catch (Exception e) {
			// reset the preview geo to show an error message
			previewGeoText.setTextString(app.getError("InvalidInput"));
			previewGeoText.setObjColor(Color.red);
			previewGeoText.updateRepaint();
			//e.printStackTrace();
		}                


		
		//TODO: adjust the size of the panel when the bounding box of the preview geo
		// grows larger than the enclosing scrollpane viewport.
		// Currently the preview geo uses absolute screen coords, so we can't get the 
		// bounding box dimensions.


		//this.setRealWorldCoordSystem(0, 1, -1, 0);
		//updateSize();
		//this.repaint();

		enableUpdateSize = true;

		Dimension d = this.getMinimumSize();

		if(d.height < getBounds().height)
			d.height = getBounds().height;
		if(d.width < getBounds().width)
			d.width = getBounds().width;

		//setSize(d);
		//updateSize();
		//enableUpdateSize = false;

		//System.out.println("bounding box: " + getBounds().toString());
		//this.setPreferredSize(new Dimension((int)r.getWidth(), (int)r.getHeight()));

	}

	
	public void updateSize() {

		// override updateSize so we can control the size of the panel?
		
		//	Application.printStacktrace("update size");
		//if(enableUpdateSize)
		super.updateSize();
	}

	
	


}
