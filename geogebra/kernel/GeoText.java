package geogebra.kernel;

import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.arithmetic.MyStringBuffer;
import geogebra.kernel.arithmetic.TextValue;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra.util.Util;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

public class GeoText extends GeoElement
implements Locateable, AbsoluteScreenLocateable, TextValue, TextProperties {

	private static final long serialVersionUID = 1L;
	private String str; 	
	private GeoPoint startPoint; // location of Text on screen
	private boolean isLaTeX; // text is a LaTeX formula
	// corners of the text Michael Borcherds 2007-11-26, see AlgoTextCorner
	private Rectangle2D boundingBox; 
	private boolean needsUpdatedBoundingBox = false;
	
	// font options
	private boolean serifFont;
	private int fontStyle;
	private int fontSize; // size relative to default font size
	private int printDecimals = -1;
	private int printFigures = -1;
	private boolean useSignificantFigures = false;
	
	// for absolute screen location
	private boolean hasAbsoluteScreenLocation = false;
	
	public GeoText(Construction c) {
		super(c);
		// don't show in algebra view	
		//setAlgebraVisible(false); 
		setAuxiliaryObject(true);
	}  
	
	public GeoText(Construction c, String label, String value) {
		this(c);
		setTextString(value);
		setLabel(label);
	}  
	
	/**
	 * Copy constructor
	 */
	public GeoText(GeoText text) {
		this(text.cons);
		set(text);
	}

	public GeoElement copy() {
		return new GeoText(this);
	}

	public void set(GeoElement geo) {
		GeoText gt = (GeoText) geo;	
		// macro output: don't set start point
		// but update to desired number format
		if (cons != geo.cons && isAlgoMacroOutput()){
			if(!useSignificantFigures)
				gt.setPrintDecimals(printDecimals > -1 ? printDecimals :  kernel.getPrintDecimals(), true);	
			else
				gt.setPrintFigures(printFigures > -1 ? printFigures :  kernel.getPrintFigures(),true);				
			str = gt.str;
			isLaTeX = gt.isLaTeX;		
			return;
		}
		
		str = gt.str;
		isLaTeX = gt.isLaTeX;
			
		
		// needed for Corner[Element[text
		setBoundingBox(gt.getBoundingBox());
	
		try {
			if (gt.startPoint != null) {
				if (gt.hasAbsoluteLocation()) {
					//	create new location point	
					setStartPoint(new GeoPoint(gt.startPoint));
				} else {
					//	take existing location point	
					setStartPoint(gt.startPoint);
				}
			}
		}
		catch (CircularDefinitionException e) {
			Application.debug("set GeoText: CircularDefinitionException");
		}		
	}
	
	public void setVisualStyle(GeoElement geo) {
		super.setVisualStyle(geo);		
		if (!geo.isGeoText()) return;
		
		GeoText text = (GeoText) geo;		
		serifFont = text.serifFont;
		fontStyle = text.fontStyle;
		fontSize = text.fontSize;
		printDecimals = text.printDecimals;	
		printFigures = text.printFigures;	
		useSignificantFigures = text.useSignificantFigures;
	}
	
	final public void setTextString(String text) {
		
		// Michael Borcherds 2008-05-11
		// remove trailing linefeeds (FreeHEP EMF export doesn't like them)
		while (text.length() > 1 && text.charAt(text.length()-1) == '\n') {
			text = text.substring(0, text.length()-1);
		}
		
		if (isLaTeX) {
			//TODO: check greek letters of latex string
			str = Util.toLaTeXString(text, false);
		} else {
			// replace "\\n" with a proper newline
			// for eg Text["Hello\\nWorld",(1,1)]
			str = text.replaceAll("\\\\\\\\n", "\n");
		}		
		
	}
	
	final public String getTextString() {
		return str;
	}
	
	/**
	 * Sets the startpoint without performing any checks.
	 * This is needed for macros.	 
	 */
	public void initStartPoint(GeoPointND p, int number) {
		startPoint = (GeoPoint) p;
	}
	
	public void setStartPoint(GeoPointND p, int number)  throws CircularDefinitionException {
		setStartPoint(p);
	}
	
	public void removeStartPoint(GeoPointND p) {    
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch(Exception e) {}
		}
	}
			
	public void setStartPoint(GeoPointND p) throws CircularDefinitionException { 
		// don't allow this if it's eg Text["hello",(2,3)]
		if (alwaysFixed) return;				
		// macro output uses initStartPoint() only
		//if (isAlgoMacroOutput()) return; 
		
		// check for circular definition
		if (isParentOf((GeoElement) p))
			throw new CircularDefinitionException();		
		
		// remove old dependencies
		if (startPoint != null) startPoint.getLocateableList().unregisterLocateable(this);	
		
		// set new location	
		if (p == null) {
			if (startPoint != null) // copy old startPoint			
				startPoint = new GeoPoint(startPoint);
			else 
				startPoint = null; 
			labelOffsetX = 0;
			labelOffsetY = 0;					
		} else {
			startPoint = (GeoPoint) p;
			//	add new dependencies
			startPoint.getLocateableList().registerLocateable(this);
			
			// absolute screen position should be deactivated
			setAbsoluteScreenLocActive(false);
		}											
	}
	
	
	
	public void doRemove() {
		super.doRemove();
		// tell startPoint	
		if (startPoint != null) startPoint.getLocateableList().unregisterLocateable(this);
	}
	
	public GeoPointND getStartPoint() {
		return startPoint;
	}
	
	
	public GeoPointND [] getStartPoints() {
		if (startPoint == null)
			return null;
	
		GeoPoint [] ret = new GeoPoint[1];
		ret[0] = startPoint;
		return ret;			
	}
	
	public boolean hasAbsoluteLocation() {
		return startPoint == null || startPoint.isAbsoluteStartPoint();
	}
	
	public void setWaitForStartPoint() {
		// this can be ignored for a text 
		// as the position of its startpoint
		// is irrelevant for the rest of the construction
	}
		
	
	public void update() {

		super.update();
		
		
//		if (needsUpdatedBoundingBox) {
//			kernel.notifyUpdate(this);
//		}
				
	}

	/**
	 * always returns true
	*/
	public boolean isDefined() {
		return str != null && (startPoint == null || startPoint.isDefined());
	}

	/**
	 * doesn't do anything
 	*/
	public void setUndefined() {
		str = null;
	}

	public String toValueString() {		
		return str;		
	}
	
	/**
	 * Returns quoted text value string.
	 */
	public String toOutputValueString() {	
		sbToString.setLength(0);
		sbToString.append('\"');
		if (str != null)
			sbToString.append(str);
		sbToString.append('\"');	
		return sbToString.toString();	
	}
	
	public String toString() {		
		sbToString.setLength(0);
		sbToString.append(label);
		sbToString.append(" = ");
		sbToString.append('\"');
		if (str != null)
			sbToString.append(str);
		sbToString.append('\"');	
		return sbToString.toString();
	}
	private StringBuilder sbToString = new StringBuilder(80);

	public boolean showInAlgebraView() {
		return true;
	}

	protected boolean showInEuclidianView() {		
		return isDefined();
	}

	public String getClassName() {
		return "GeoText";
	}

    public int getRelatedModeID() {
    	return EuclidianConstants.MODE_TEXT;
    }
	
    protected String getTypeString() {
		return "Text";
	}

	public int getGeoClassType() {
		return GEO_CLASS_TEXT;
	}    
	
	public boolean isMoveable() {
		
		if (alwaysFixed) return false;
		
		return !isFixed();
	}
	
	/** used for eg Text["text",(1,2)]
	* to stop it being editable */
	boolean isTextCommand = false;
	
	public void setIsTextCommand(boolean isCommand) {
		this.isTextCommand = isCommand;
	}
	
	public boolean isTextCommand() {

		// check for eg If[ a==1 , "hello", "bye"] first
		if (!(getParentAlgorithm() == null) && !(getParentAlgorithm() instanceof AlgoDependentText)) return true;

		return isTextCommand;
	}
	
	void setAlgoMacroOutput(boolean isAlgoMacroOutput) {
		super.setAlgoMacroOutput(true);
		setIsTextCommand(true);
	}
	
	/** used for eg Text["text",(1,2)]
	 * to stop it being draggable */
	boolean alwaysFixed = false;
	
	public void setAlwaysFixed(boolean alwaysFixed) {
		this.alwaysFixed = alwaysFixed;
	}

	public boolean isFixable() {
		
		// workaround for Text["text",(1,2)]
		if (alwaysFixed) return false;

		return true;
	}

	public boolean isNumberValue() {
		return false;
	}

	public boolean isVectorValue() {
		return false;
	}

	public boolean isPolynomialInstance() {
		return false;
	}
	
	public boolean isTextValue() {
		return true;
	}
	
	public boolean isGeoText() {
		return true;
	}
	
	public MyStringBuffer getText() {
		if (str != null)
			return new MyStringBuffer(str);
		else
			return new MyStringBuffer("");
	}	
		
	/**
	   * save object in XML format
	   */ 
	  public final void getXML(StringBuilder sb) {
	 
		 // an independent text needs to add
		 // its expression itself
		 // e.g. text0 = "Circle"
		 if (isIndependent()) {
			sb.append("<expression");
				sb.append(" label=\"");
				sb.append(Util.encodeXML(label));
				sb.append("\" exp=\"");
				sb.append(Util.encodeXML(toOutputValueString()));
				// expression   
			sb.append("\"/>\n");
		 }
  		  
		  sb.append("<element"); 
			  sb.append(" type=\"text\"");
			  sb.append(" label=\"");
			  sb.append(Util.encodeXML(label));
		  sb.append("\">\n");
		  getXMLtags(sb);
		  sb.append("</element>\n");

	  }

	/**
	* returns all class-specific xml tags for getXML
	*/
		protected void getXMLtags(StringBuilder sb) {
	   	getXMLvisualTags(sb, false);			
		
	   	getXMLfixedTag(sb);
		
		if (isLaTeX) {
			sb.append("\t<isLaTeX val=\"true\"/>\n");	
		}
		
		// font settings
		if (serifFont || fontSize != 0 || fontStyle != 0 || isLaTeX) {
			sb.append("\t<font serif=\"");
			sb.append(serifFont);
			sb.append("\" size=\"");
			sb.append(fontSize);
			sb.append("\" style=\"");
			sb.append(fontStyle);
			sb.append("\"/>\n");
		}
		
		// print decimals
		if (printDecimals >= 0 && !useSignificantFigures) {
			sb.append("\t<decimals val=\"");
			sb.append(printDecimals);
			sb.append("\"/>\n");
		}
						
		// print significant figures
		if (printFigures >= 0 && useSignificantFigures) {
			sb.append("\t<significantfigures val=\"");
			sb.append(printFigures);
			sb.append("\"/>\n");
		}
						
		getBreakpointXML(sb);
		
		getAuxiliaryXML(sb);

		// store location of text (and possible labelOffset)
		sb.append(getXMLlocation());			
		getScriptTags(sb);

   	}
   	
   	/**
   	 * Returns startPoint of this text in XML notation.  	 
   	 */
   	private String getXMLlocation() {   		
   		StringBuilder sb = new StringBuilder();   				
   		
   		if (hasAbsoluteScreenLocation) {
   			sb.append("\t<absoluteScreenLocation ");			
   				sb.append(" x=\""); 	sb.append( labelOffsetX ); 	sb.append("\"");
   				sb.append(" y=\""); 	sb.append( labelOffsetY ); 	sb.append("\"");
			sb.append("/>\n");	
   		} 
   		else {   			
			// location of text
			if (startPoint != null) {
				sb.append(startPoint.getStartPointXML());
	
				if (labelOffsetX != 0 || labelOffsetY != 0) {
					sb.append("\t<labelOffset");			
						sb.append(" x=\""); 	sb.append( labelOffsetX ); 	sb.append("\"");
						sb.append(" y=\""); 	sb.append( labelOffsetY ); 	sb.append("\"");
					sb.append("/>\n");	  	
				}	
			}
   		}
   		return sb.toString();
   	}

	public void setAllVisualProperties(GeoElement geo, boolean keepAdvanced) {
		super.setAllVisualProperties(geo, keepAdvanced);
		
		// start point of text
		if (geo instanceof GeoText) {
			GeoText text = (GeoText) geo;									
			setSameLocation(text);
			setLaTeX(text.isLaTeX, true);
		}		
	}	
	
	private void setSameLocation(GeoText text) {
		if (text.hasAbsoluteScreenLocation) {
			setAbsoluteScreenLocActive(true);
			setAbsoluteScreenLoc(text.getAbsoluteScreenLocX(), 
								 text.getAbsoluteScreenLocY());
		} 
		else {
			if (text.startPoint != null) {
				try {
					setStartPoint(text.startPoint);
				} catch (Exception e) {				
				}
			}
		}						
	}


	public boolean isLaTeX() {
		return isLaTeX;
	}

	public void setLaTeX(boolean b, boolean updateParentAlgo) {
		if (b == isLaTeX) return;
		
		isLaTeX = b;
	
		// update parent algorithm if it's not a sequence
		if (updateParentAlgo) {
			AlgoElement parent = getParentAlgorithm();
			if (parent != null && !(parent instanceof AlgoSequence)) {
				parent.update();
			}
		}			
	}		

	public void setAbsoluteScreenLoc(int x, int y) {
		labelOffsetX = x;
		labelOffsetY = y;		
	}

	public int getAbsoluteScreenLocX() {	
		return labelOffsetX;
	}

	public int getAbsoluteScreenLocY() {		
		return labelOffsetY;
	}
	
	public double getRealWorldLocX() {
		if (startPoint == null)
			return 0;
		else
			return startPoint.inhomX;
	}
	
	public double getRealWorldLocY() {
		if (startPoint == null)
			return 0;
		else
			return startPoint.inhomY;
	}
	
	public void setRealWorldLoc(double x, double y) {
		GeoPoint loc = (GeoPoint) getStartPoint();
		if (loc == null) {
			loc = new GeoPoint(cons);	
			try {setStartPoint(loc); }
			catch(Exception e){}
		}
		loc.setCoords(x, y, 1.0);
		labelOffsetX = 0;
		labelOffsetY = 0;	
	}

	public void setAbsoluteScreenLocActive(boolean flag) {
		if (flag == hasAbsoluteScreenLocation) return;
		
		hasAbsoluteScreenLocation = flag;			
		if (flag) {
			// remove startpoint
			if (startPoint != null) {
				startPoint.getLocateableList().unregisterLocateable(this);				
				startPoint = null;
			}			
		} else {
			labelOffsetX = 0;
			labelOffsetY = 0;
		}
	}

	public boolean isAbsoluteScreenLocActive() {
		return hasAbsoluteScreenLocation;
	}
	
	public boolean isAbsoluteScreenLocateable() {
		return true;
	}

	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int size) {
		if (size < -MAX_FONTSIZE) size = -MAX_FONTSIZE;
		else if (size > MAX_FONTSIZE) size = MAX_FONTSIZE;
		fontSize = size;
	}
	public int getFontStyle() {
		return fontStyle;
	}
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
		
		// needed for eg \sqrt in latex
		if ((fontStyle & Font.BOLD) != 0)
			lineThickness = EuclidianView.DEFAULT_LINE_THICKNESS * 2;
		else
			lineThickness = EuclidianView.DEFAULT_LINE_THICKNESS;
			
	}
	final public int getPrintDecimals() {
		return printDecimals;
	}
	final public int getPrintFigures() {
		return printFigures;
	}
	public void setPrintDecimals(int printDecimals, boolean update) {		
		AlgoElement algo = getParentAlgorithm();
		if (algo != null && update) {
			this.printDecimals = printDecimals;
			printFigures = -1;
			useSignificantFigures = false;
			algo.update();
		}			
	}
	public void setPrintFigures(int printFigures, boolean update) {		
		AlgoElement algo = getParentAlgorithm();
		if (algo != null && update) {
			this.printFigures = printFigures;
			printDecimals = -1;
			useSignificantFigures = true;
			algo.update();
		}			
	}
	public boolean useSignificantFigures() {
		return useSignificantFigures;

	}
	public boolean isSerifFont() {
		return serifFont;
	}
	public void setSerifFont(boolean serifFont) {
		this.serifFont = serifFont;
	}
	public void calculateCornerPoint(GeoPoint result, int n) {	
		// adapted from GeoImage by Michael Borcherds 2007-11-26
		if (hasAbsoluteScreenLocation || boundingBox == null) {
			result.setUndefined();
			return;
		}					
	
		switch (n) {
			case 4: // top left
				result.setCoords(boundingBox.getX(),boundingBox.getY(),1.0);
				break;
			
			case 3: // top right
				result.setCoords(boundingBox.getX()+boundingBox.getWidth(),boundingBox.getY(),1.0);
				break;
				
			case 2: // bottom right
				result.setCoords(boundingBox.getX()+boundingBox.getWidth(),boundingBox.getY()+boundingBox.getHeight(),1.0);
				break;
				
			case 1: // bottom left
				result.setCoords(boundingBox.getX(),boundingBox.getY()+boundingBox.getHeight(),1.0);
				break;
				
			default:
				result.setUndefined();
		}	
	}
	
	public Rectangle2D getBoundingBox() 
	{ 
		return boundingBox;
	}

	public void setBoundingBox(Rectangle2D rect) 
	{ 
		boundingBox = rect;
	}

	public void setBoundingBox(double x, double y, double w, double h) 
	{ 
		
		boolean firstTime = boundingBox == null;
		if (firstTime) {
			boundingBox = new Rectangle2D.Double();
		}
		
		boundingBox.setRect(x, y, w, h);
	}

	public final boolean isNeedsUpdatedBoundingBox() {
		return needsUpdatedBoundingBox;
	}

	public final void setNeedsUpdatedBoundingBox(boolean needsUpdatedBoundingBox) {
		this.needsUpdatedBoundingBox = needsUpdatedBoundingBox;
	}

	// Michael Borcherds 2008-04-30
	final public boolean isEqual(GeoElement geo) {
		// return false if it's a different type
		if (geo.isGeoText()) return str.equals(((GeoText)geo).str); else return false;
	}
	public void setZero() {
		str="";
	}
	
	
	/**
	 * Returns a comparator for GeoText objects.
	 * If equal, doesn't return zero (otherwise TreeSet deletes duplicates)
	 */
	public static Comparator<GeoText> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<GeoText>() {
			      public int compare(GeoText itemA, GeoText itemB) {

				        int comp = itemA.getTextString().compareTo(itemB.getTextString());
				        
				        
				        if (comp == 0) 
				        	// if we return 0 for equal strings, the TreeSet deletes the equal one
				        	return itemA.getConstructionIndex() > itemB.getConstructionIndex() ? -1 : 1;
				        else
				        	return comp;
				      }
			};
		}
		
		return comparator;
	}
	
	private static Comparator<GeoText> comparator;
	
		public void setTemporaryPrintAccuracy() {
		if (useSignificantFigures()) {
			kernel.setTemporaryPrintFigures(printFigures);
		}
		else
		{
			kernel.setTemporaryPrintDecimals(printDecimals);
		}
	}
	
	public void restorePrintAccuracy() {
		kernel.restorePrintAccuracy();
	}

	public boolean isAlwaysFixed() {
		return alwaysFixed;
	}

	public boolean isVector3DValue() {
		// TODO Auto-generated method stub
		return false;
	}
	
	final public boolean isAuxiliaryObjectByDefault() {
		return true;
	}

	public boolean justFontSize() {
		return false;
	}
	
	public boolean isRedefineable() {
		return true;
	}
	
	



}
