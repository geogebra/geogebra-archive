package geogebra.kernel;

import geogebra.kernel.arithmetic.MyStringBuffer;
import geogebra.kernel.arithmetic.TextValue;
import geogebra.main.Application;
import geogebra.util.Util;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Comparator;

public class GeoText extends GeoElement
implements Locateable, AbsoluteScreenLocateable, TextValue {

	private static final long serialVersionUID = 1L;
	private String str; 	
	private GeoPoint startPoint; // location of Text on screen
	private boolean isLaTeX; // text is a LaTeX formula
	// corners of the text Michael Borcherds 2007-11-26, see AlgoTextCorner
	private Rectangle2D boundingBox; 
	private boolean needsUpdatedBoundingBox = false;
	
	// font options
	private boolean serifFont = false;
	private int fontStyle = Font.PLAIN;
	private int fontSize = 0; // size relative to default font size
	private int printDecimals = -1;
	private int printFigures = -1;
	public boolean useSignificantFigures = false;
	
	// for absolute screen location
	boolean hasAbsoluteScreenLocation = false;
	
	public GeoText(Construction c) {
		super(c);
		// don't show in algebra view	
		setAlgebraVisible(false); 	
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
		str = gt.str;

		// macro output: don't set start point
		if (cons != geo.cons && isAlgoMacroOutput()) 
			return;
	
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
		
		//setLaTeX(gt.isLaTeX, true);
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
			str = text;
		}		
		
	}
	
	final public String getTextString() {
		return str;
	}
	
	/**
	 * Sets the startpoint without performing any checks.
	 * This is needed for macros.	 
	 */
	public void initStartPoint(GeoPoint p, int number) {
		startPoint = p;
	}
	
	public void setStartPoint(GeoPoint p, int number)  throws CircularDefinitionException {
		setStartPoint(p);
	}
	
	public void removeStartPoint(GeoPoint p) {    
		if (startPoint == p) {
			try {
				setStartPoint(null);
			} catch(Exception e) {}
		}
	}
			
	public void setStartPoint(GeoPoint p)  throws CircularDefinitionException { 
		// macro output uses initStartPoint() only
		if (isAlgoMacroOutput()) return; 
		
		// check for circular definition
		if (isParentOf(p))
			throw new CircularDefinitionException();		
		
		// remove old dependencies
		if (startPoint != null) startPoint.unregisterLocateable(this);	
		
		// set new location	
		if (p == null) {
			if (startPoint != null) // copy old startPoint			
				startPoint = new GeoPoint(startPoint);
			else 
				startPoint = null; 
			labelOffsetX = 0;
			labelOffsetY = 0;					
		} else {
			startPoint = p;
			//	add new dependencies
			startPoint.registerLocateable(this);
			
			// absolute screen position should be deactivated
			setAbsoluteScreenLocActive(false);
		}											
	}
	
	
	
	protected void doRemove() {
		super.doRemove();
		// tell startPoint	
		if (startPoint != null) startPoint.unregisterLocateable(this);
	}
	
	public GeoPoint getStartPoint() {
		return startPoint;
	}
	
	
	public GeoPoint [] getStartPoints() {
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
	
	public void setMode(int mode) {
	}

	public int getMode() {
		return 0;
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
	private StringBuffer sbToString = new StringBuffer(80);

	protected boolean showInAlgebraView() {
		return false;
	}

	protected boolean showInEuclidianView() {		
		return isDefined();
	}

	protected String getClassName() {
		return "GeoText";
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
	
	// used for eg Text["text",(1,2)]
	// to stop it being editable
	boolean isTextCommand = false;
	
	public void setIsCommand(boolean isCommand) {
		this.isTextCommand = isCommand;
	}
	
	public boolean isTextCommand() {
		return isTextCommand;
	}
	
	// used for eg Text["text",(1,2)]
	// to stop it being draggable
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
		return new MyStringBuffer(str);
	}	
		
	/**
	   * save object in XML format
	   */ 
	  public final String getXML() {
		 StringBuffer sb = new StringBuffer();
	 
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
		  sb.append(getXMLtags());
		  sb.append("</element>\n");
	  	  
		  return sb.toString();
	  }

	/**
	* returns all class-specific xml tags for getXML
	*/
   	protected String getXMLtags() {   	
	   	StringBuffer sb = new StringBuffer();
	   	sb.append(getXMLvisualTags(false));			
		
		if (isFixed()) {
			sb.append("\t<fixed val=\"true\"/>\n");	
		}
		
		if (isLaTeX) {
			sb.append("\t<isLaTeX val=\"true\"/>\n");	
		}
		
		// font settings
		if (serifFont || fontSize != 0 || fontStyle != 0) {
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
						
		sb.append(getBreakpointXML());

		// store location of text (and possible labelOffset)
		sb.append(getXMLlocation());			
			
	   return sb.toString();   
   	}
   	
   	/**
   	 * Returns startPoint of this text in XML notation.  	 
   	 */
   	private String getXMLlocation() {   		
   		StringBuffer sb = new StringBuffer();   				
   		
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

	public void setAllVisualProperties(GeoElement geo) {
		super.setAllVisualProperties(geo);
		
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
		
		if (updateParentAlgo) {
			updateCascadeParentAlgo();
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
		GeoPoint loc = getStartPoint();
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
				startPoint.unregisterLocateable(this);				
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
		fontSize = size;
	}
	public int getFontStyle() {
		return fontStyle;
	}
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
	final public int getPrintDecimals() {
		return printDecimals;
	}
	final public int getPrintFigures() {
		return printFigures;
	}
	public void setPrintDecimals(int printDecimals) {		
		AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			this.printDecimals = printDecimals;
			printFigures = -1;
			useSignificantFigures = false;
			algo.update();
		}			
	}
	public void setPrintFigures(int printFigures) {		
		AlgoElement algo = getParentAlgorithm();
		if (algo != null) {
			this.printFigures = printFigures;
			printDecimals = -1;
			useSignificantFigures = true;
			algo.update();
		}			
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
	
	public static Comparator compare = new Comparator() {
	      public int compare(Object a, Object b) {
	        GeoText itemA = (GeoText) a;
	        GeoText itemB = (GeoText) b;
	        int comp = itemA.getTextString().compareTo(itemB.getTextString());
	        
	        
	        if (comp == 0) 
	        	// if we return 0 for equal strings, the TreeSet deletes the equal one
	        	return itemA.getConstructionIndex() > itemB.getConstructionIndex() ? -1 : 1;
	        else
	        	return comp;
	      }
	};
	
	/**
	 * Returns a comparator for GeoText objects.
	 * If equal, doesn't return zero (otherwise TreeSet deletes duplicates)
	 */
	public static Comparator getComparator() {
		if (comparator == null) {
			comparator = new Comparator() {
			      public int compare(Object a, Object b) {
				        GeoText itemA = (GeoText) a;
				        GeoText itemB = (GeoText) b;
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
	private static Comparator comparator;


}
