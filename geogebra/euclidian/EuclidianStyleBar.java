package geogebra.euclidian;

import geogebra.gui.util.GeoGebraIcon;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.AlgoTableText;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionDefaults;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImage;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.GeoText;
import geogebra.kernel.PointProperties;
import geogebra.kernel.TextProperties;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.main.Application;
import geogebra.main.MyError;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
/**
 * Stylebar for the Euclidian Views
 * 
 * @author G. Sturr 
 *
 */
public class EuclidianStyleBar extends JToolBar implements ActionListener {
		
	/***/
	private static final long serialVersionUID = 1L;

	
	
	private PopupMenuButton btnColor, btnTextColor, btnLineStyle, btnPointStyle, btnTextSize, btnMode, 
		btnTableTextJustify, btnTableTextBracket;
	private PopupMenuButton[] popupBtnList;
			
	
	private MyToggleButton btnCopyVisualStyle, btnPen, btnShowGrid, btnShowAxes,
    		btnBold, btnItalic, btnDelete, btnLabel, btnPenEraser, btnHideShowLabel, btnTableTextLinesV, btnTableTextLinesH;
	private MyToggleButton[] toggleBtnList;

	private JButton btnPenDelete, btnDeleteGeo;
	
	private EuclidianController ec;
	private EuclidianView ev;
	private Application app;
	private Integer[] lineStyleArray, pointStyleArray;
	private HashMap<Integer, Integer> lineStyleMap, pointStyleMap;
	
	private HashMap<Integer,Integer> defaultGeoMap;
	private Construction cons; 
	
	private ArrayList<GeoElement> defaultGeos;
	
	
	private int maxIconHeight = 18;	
	
	private int mode;
	
	private Color[] colors, textColors;
	private HashMap<Color,Integer> colorMap, textColorMap;
	
	private boolean isIniting;
	private boolean needUndo = false;
	
	private AlgoTableText tableText;
	private final String[] bracketArray = { "\u00D8" , "{ }" , "( )", "[ ]", "| |", "|| ||"};
	private final String[] bracketArray2 = { "\u00D8" , "{ }" , "( )", "[ ]", "||", "||||"};
	
	
	
	/*************************************************
	 * Constructs a styleBar
	 */
	public EuclidianStyleBar(EuclidianView ev) {
		
		isIniting = true;
		this.ev = ev;
		ec = ev.getEuclidianController(); 
		app = ev.getApplication();
		cons = app.getKernel().getConstruction();
		createDefaultMap();
		
		
		setFloatable(false);

		Dimension d = getPreferredSize();
		d.height = maxIconHeight+8;
		setPreferredSize(d);

		
		colors = getStyleBarColors(true);
		colorMap = new HashMap<Color,Integer>();
		for(int i = 0; i < colors.length; i++)
			colorMap.put(colors[i], i);
		
		textColors = getStyleBarColors(true);
		textColorMap = new HashMap<Color,Integer>();
		for(int i = 0; i < textColors.length; i++)
			textColorMap.put(textColors[i], i);
		
		pointStyleArray = EuclidianView.getPointStyles();
		pointStyleMap = new HashMap<Integer,Integer>();
		for(int i = 0; i < pointStyleArray.length; i++)
			pointStyleMap.put(pointStyleArray[i], i);

		lineStyleArray = EuclidianView.getLineTypes();
		lineStyleMap = new HashMap<Integer,Integer>();
		for(int i = 0; i < lineStyleArray.length; i++)
			lineStyleMap.put(lineStyleArray[i], i);
		
		defaultGeos = new ArrayList<GeoElement>();		
		
		initGUI();
		isIniting = false;
		
		setMode(ev.getMode());
		updateStyleBar();
	}
	
	
	public int getMode() {
		return mode;
	}


	public void setMode(int mode) {
		
		this.mode = mode;
		
		// MODE_TEXT temporarily switches to  MODE_SELECTION_LISTENER 
		// so we need to ignore this.
		if(mode == EuclidianConstants.MODE_SELECTION_LISTENER) return;
		
		updateStyleBar();
		updateGUI();
	}
	
	
	
	
	public void updateStyleBar(){
	
		if(mode == EuclidianConstants.MODE_VISUAL_STYLE)
			return;
		
		ArrayList<GeoElement> geos = new ArrayList<GeoElement>();
		
		if(mode == EuclidianConstants.MODE_MOVE){
			geos = ev.getApplication().getSelectedGeos();
		}
		else if (defaultGeoMap.containsKey(mode)){
			geos.add(cons.getConstructionDefaults().getDefaultGeo(defaultGeoMap.get(mode)));
			defaultGeos = geos;
		}
		
		updateTableText(geos.toArray());
		for(int i = 0; i < popupBtnList.length; i++){
			popupBtnList[i].update(geos.toArray());
		}
		for(int i = 0; i < toggleBtnList.length; i++){
			toggleBtnList[i].update(geos.toArray());
		}
		
		btnPenDelete.setVisible((mode == EuclidianConstants.MODE_PEN));
		
	}
	
	private void updateTableText(Object[] geos){
		
		tableText = null;
		if (geos.length == 0) return;
		
		for (int i = 0; i < geos.length; i++) {
			if ((((GeoElement)geos[i]).getGeoElementForPropertiesDialog().isGeoText())) {
				AlgoElement algo = ((GeoElement)geos[i]).getParentAlgorithm();
				if(algo != null  && (algo instanceof AlgoTableText)){
					tableText = (AlgoTableText) algo;
				}		
			}			
		}
		
	}
	
	
	
	private void createDefaultMap(){
		defaultGeoMap = new HashMap<Integer,Integer>();
		defaultGeoMap.put(EuclidianConstants.MODE_POINT, ConstructionDefaults.DEFAULT_POINT_FREE);
		defaultGeoMap.put(EuclidianConstants.MODE_POLYGON, ConstructionDefaults.DEFAULT_POLYGON);
		defaultGeoMap.put(EuclidianConstants.MODE_TEXT, ConstructionDefaults.DEFAULT_TEXT);
		defaultGeoMap.put(EuclidianConstants.MODE_JOIN, ConstructionDefaults.DEFAULT_LINE);
		
	}
	
	
	
	//=====================================================
	//                  Init  GUI
	//=====================================================
	
	private void initGUI() {
		
		createButtons();
		createTextButtons();
		
		add(btnShowAxes);
		add(btnShowGrid);
		add(btnColor);
		add(btnTextColor);
		add(btnLineStyle);
		add(btnPointStyle);
		add(btnBold);
		add(btnItalic);
		add(btnTextSize);
	
		createTableTextButtons();
		add(btnTableTextJustify);
		add(btnTableTextLinesV);
		add(btnTableTextLinesH);
		add(btnTableTextBracket);
		
		add(btnPenEraser);
		add(btnHideShowLabel);
		add(btnPenDelete);
			
		popupBtnList = new PopupMenuButton[]{
				btnColor, btnTextColor, btnLineStyle, btnPointStyle, btnTextSize, btnTableTextJustify, btnTableTextBracket};
		
		toggleBtnList = new MyToggleButton[]{
				btnCopyVisualStyle, btnPen, btnShowGrid, btnShowAxes,
	            btnBold, btnItalic, btnDelete, btnLabel, btnPenEraser, btnHideShowLabel, btnTableTextLinesV, btnTableTextLinesH};	
		
	}
	
	

	
	//=====================================================
	//                 Create Buttons
	//=====================================================
	
	private void createButtons() {
		
		//========================================
		// mode button
		
		String[] modeArray = new String[]{
				"cursor_arrow.png",
				"applications-graphics.png",
				"delete_small.gif",
				"mode_point_16.gif",
				"mode_copyvisualstyle_16.png"
		};
		btnMode = new PopupMenuButton(ev.getApplication(), modeArray, -1,1,
				new Dimension(20,maxIconHeight), SelectionTable.MODE_ICON_FILE);
		btnMode.addActionListener(this);
		btnMode.setKeepVisible(false);
		//add(btnMode);
		
		
		//========================================
		// pen button
		btnPen = new MyToggleButton(ev.getApplication().getImageIcon("applications-graphics.png")){
		      @Override
			public void update(Object[] geos) {
					this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE) || mode == EuclidianConstants.MODE_PEN);	  
			      }
			};
		btnPen.addActionListener(this);
		//add(btnPen);

		
		//========================================
		// delete button
		btnDelete = new MyToggleButton(ev.getApplication().getImageIcon("delete_small.gif")){
		      @Override
			public void update(Object[] geos) {
					this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE)  || mode == EuclidianConstants.MODE_DELETE);	  
			      }
			};
		btnDelete.addActionListener(this);
		//add(btnDelete);
		
		
		
		//========================================
		// hide/show labels button
		btnLabel = new MyToggleButton(ev.getApplication().getImageIcon("mode_copyvisualstyle_16.png")){
		      @Override
			public void update(Object[] geos) {
					this.setVisible((geos.length == 0 && mode == EuclidianConstants.MODE_MOVE) || mode == EuclidianConstants.MODE_SHOW_HIDE_LABEL);	  
			      }
			};
		btnLabel.addActionListener(this);
		//add(btnLabel);
		
		
		//========================================
		// visual style button
		
		btnCopyVisualStyle = new MyToggleButton(ev.getApplication().getImageIcon("mode_copyvisualstyle_16.png")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible( (geos.length > 0 && mode == EuclidianConstants.MODE_MOVE) || mode == EuclidianConstants.MODE_VISUAL_STYLE);	  
		      }
		};
		btnCopyVisualStyle.addActionListener(this);
		//add(this.btnCopyVisualStyle);

		
		//========================================
		// show axes button	
		btnShowAxes = new MyToggleButton(app.getImageIcon("axes.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		//btnShowAxes.setPreferredSize(new Dimension(16,16));
		btnShowAxes.addActionListener(this);
		
		
		
		//========================================
		// show grid button
		btnShowGrid = new MyToggleButton(app.getImageIcon("grid.gif")){
		      @Override
			public void update(Object[] geos) {
					this.setVisible(geos.length == 0 && mode != EuclidianConstants.MODE_PEN);	  
			      }
			};			
		//btnShowGrid.setPreferredSize(new Dimension(16,16));
		btnShowGrid.addActionListener(this);
		
		
		//========================================
		// object color button  (color for everything except text)
		
		btnColor = new PopupMenuButton(ev.getApplication(), colors, -1,8,
				new Dimension(20,maxIconHeight), SelectionTable.MODE_COLOR_SWATCH) {

			@Override
			public void update(Object[] geos) {

				if( mode == EuclidianConstants.MODE_PEN){
					this.setVisible(true);
					setSelectedIndex(colorMap.get(ec.getPen().getPenColor()));
					setSliderValue(100);
					getMySlider().setVisible(false);

				}else{
					boolean geosOK = (geos.length > 0 || mode == EuclidianConstants.MODE_PEN);
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = (GeoElement)geos[i];
						if (geo instanceof GeoImage)
							geosOK = false;
						break;
					}

					setVisible(geosOK);

					if(geosOK){
						Color geoColor;
						if(geos[0] instanceof TextProperties)
							geoColor = ((GeoElement) geos[0]).getBackgroundColor();
						else
							geoColor = ((GeoElement) geos[0]).getObjectColor();
						
						float alpha = 1.0f;
						boolean hasFillable = false;
						
						// check if selection contains a fillable geo
						// if true, then set slider to first fillable's alpha value
						for (int i = 0; i < geos.length; i++) {
							if (((GeoElement) geos[i]).isFillable()) {
								hasFillable = true;
								alpha = ((GeoElement) geos[i]).getAlphaValue();
								break;
							}
						}
						
						setButton(geoColor, alpha, hasFillable);
						
						this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
					}
				}
			}
			
			
			private void setButton(Color color, float alpha, boolean showSlider){
				
				//TODO: handle null color properly
				if (color == null) color = Color.WHITE;
				
				int index;
				// if color is in our table then select it and color the non-standard swatch white
				if(colorMap.containsKey(color)){
					colors[colors.length-1] = Color.WHITE;
					index = colorMap.get(color);
				}else{		
					// otherwise show the color in the non-standard swatch
					colors[colors.length-1] = color;
					index = colors.length-1;
				}

				getMyTable().populateModel(colors);					
				setSelectedIndex(index);
				
				setSliderValue(Math.round(alpha * 100));
				getMySlider().setVisible(showSlider);	
			}
		};

		btnColor.getMySlider().setMinimum(0);
		btnColor.getMySlider().setMaximum(100);
		btnColor.getMySlider().setMajorTickSpacing(25);
		btnColor.getMySlider().setMinorTickSpacing(5);
		btnColor.setSliderValue(50);
		btnColor.addActionListener(this);
		
		
	
		//========================================
		// line style button
		
		btnLineStyle = new PopupMenuButton(ev.getApplication(), lineStyleArray, -1,1,
				new Dimension(80,maxIconHeight), SelectionTable.MODE_LINESTYLE){

			@Override
			public void update(Object[] geos) {

				if( mode == EuclidianConstants.MODE_PEN){
					this.setVisible(true);
					setFgColor(ec.getPen().getPenColor());
					setSliderValue(ec.getPen().getPenSize());
					setSelectedIndex(lineStyleMap.get(ec.getPen().getPenLineStyle()));
				}else{

					boolean geosOK = (geos.length > 0 );
					for (int i = 0; i < geos.length; i++) {
						GeoElement geo = ((GeoElement) geos[i]).getGeoElementForPropertiesDialog();
						if (!(geo.isPath()
								|| (geo.isGeoList() && ((GeoList)geo).showLineProperties() )
								|| (geo.isGeoNumeric() && ((GeoNumeric) geo).isDrawable()))) {
							geosOK = false;
							break;
						}
					}

					this.setVisible(geosOK);

					if(geosOK){	
						//setFgColor(((GeoElement)geos[0]).getObjectColor());

						setFgColor(Color.black);
						setSliderValue( ((GeoElement)geos[0]).getLineThickness());

						setSelectedIndex(lineStyleMap.get(((GeoElement)geos[0]).getLineType()));

						this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
					}							
				}
			}

		};
		
		btnLineStyle.getMySlider().setMinimum(1);
		btnLineStyle.getMySlider().setMaximum(13);
		btnLineStyle.getMySlider().setMajorTickSpacing(2);
		btnLineStyle.getMySlider().setMinorTickSpacing(1);
		btnLineStyle.getMySlider().setPaintTicks(true);	
		btnLineStyle.addActionListener(this);
				
		
		//========================================
		// point style button

		btnPointStyle = new PopupMenuButton(ev.getApplication(), pointStyleArray, 2, -1, 
				new Dimension(20, maxIconHeight), SelectionTable.MODE_POINTSTYLE){

			@Override
			public void update(Object[] geos) {

				boolean geosOK = (geos.length > 0 );
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = (GeoElement)geos[i];
					if (!(geo.getGeoElementForPropertiesDialog().isGeoPoint())
							&& (!(geo.isGeoList() && ((GeoList)geo).showPointProperties()))) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);

				if(geosOK){					
					//setFgColor(((GeoElement)geos[0]).getObjectColor());
					setFgColor(Color.black);
					
					setSliderValue( ((PointProperties)geos[0]).getPointSize());
					setSelectedIndex(pointStyleMap.get(((PointProperties)geos[0]).getPointStyle()));
					this.setKeepVisible(mode == EuclidianConstants.MODE_MOVE);
				}
			}		  
		};
		btnPointStyle.getMySlider().setMinimum(1);
		btnPointStyle.getMySlider().setMaximum(9);
		btnPointStyle.getMySlider().setMajorTickSpacing(2);
		btnPointStyle.getMySlider().setMinorTickSpacing(1);
		btnPointStyle.getMySlider().setPaintTicks(true);		
		btnPointStyle.addActionListener(this);
		
	
		//========================================
		// eraser button
		btnPenEraser = new MyToggleButton(app.getImageIcon("delete_small.gif")){
			@Override
			public void update(Object[] geos) {
				this.setVisible(mode == EuclidianConstants.MODE_PEN);
			}	
		};
		btnPenEraser.addActionListener(this);
		
		
		//========================================
		// delete geo button
		btnDeleteGeo = new JButton(app.getImageIcon("delete_small.gif"));
		btnDeleteGeo.addActionListener(this);
		//add(btnDeleteGeo);
		
		
		
		//========================================
		// hide/show label button
		btnHideShowLabel = new MyToggleButton(app.getImageIcon("mode_showhidelabel_16.gif")){
			@Override
			public void update(Object[] geos) {
				// only show this button when handling selection, do not use it for defaults
				if(mode != EuclidianConstants.MODE_MOVE) return;
				boolean geosOK = (geos.length > 0 );
				for (int i = 0; i < geos.length; i++) {
					if ((((GeoElement)geos[i]).getGeoElementForPropertiesDialog().isGeoText())) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);
				if(geosOK){	
					btnHideShowLabel.setSelected(((GeoElement)geos[0]).isLabelVisible());
				}
			}	
			
		};
		btnHideShowLabel.addActionListener(this);
		
		//========================================
		// pen delete button
		btnPenDelete = new JButton("\u2718");
		Dimension d = new Dimension(maxIconHeight,maxIconHeight);
		btnPenDelete.setPreferredSize(d);
		btnPenDelete.setMaximumSize(d);
		btnPenDelete.addActionListener(this);

	}

	

	
	

	//=====================================================
	//           Text Format Buttons
	//=====================================================
	
	private void createTextButtons() {	

		Dimension iconDimension = new Dimension(16, maxIconHeight);
		//========================
		// text color  button
		
		btnTextColor = new PopupMenuButton(ev.getApplication(), textColors, -1,8,
				new Dimension(20,maxIconHeight), SelectionTable.MODE_COLOR_SWATCH_TEXT) {

			@Override
			public void update(Object[] geos) {

				boolean geosOK = (geos.length > 0);
				for (int i = 0; i < geos.length; i++) {
					if (!(((GeoElement)geos[i]).getGeoElementForPropertiesDialog().isGeoText()) 
							|| ((GeoElement)geos[i]) instanceof GeoList ) {
						geosOK = false;
						break;
					}
				}
				setVisible(geosOK);
				
				if(geosOK){			
					Color geoColor = ((GeoElement) geos[0]).getObjectColor();
					int index;
					if(textColorMap.containsKey(geoColor)){
						textColors[textColors.length-1] = Color.WHITE;
						index = textColorMap.get(geoColor);
					}else{		
						textColors[textColors.length-1] = geoColor;
						index = textColors.length-1;
					}
					btnTextColor.getMyTable().populateModel(textColors);					
					setSelectedIndex(index);
					setFgColor(((GeoElement)geos[0]).getObjectColor());
					setFontStyle(((GeoText)geos[0]).getFontStyle());
				}
			}

		};
		btnTextColor.getMySlider().setVisible(false);
		btnTextColor.setSliderValue(100);
		btnTextColor.addActionListener(this);
		
			
		//========================================
		// bold text button
		ImageIcon boldIcon = GeoGebraIcon.createStringIcon(app.getPlain("Bold").substring(0,1),
				app.getPlainFont(), true, false, true, iconDimension, Color.black, null);
		btnBold = new MyToggleButton(boldIcon){
			@Override
			public void update(Object[] geos) {
				boolean geosOK = (geos.length > 0 );
				for (int i = 0; i < geos.length; i++) {
					if (!(((GeoElement)geos[i]).getGeoElementForPropertiesDialog().isGeoText())) {
						geosOK = false;
						break;
					} 
				}
				this.setVisible(geosOK);
				if(geosOK){	
					int style = ((TextProperties)geos[0]).getFontStyle();
					//int style = ((GeoText)geos[0]).getFontStyle();
					btnBold.setSelected(style == Font.BOLD || style == (Font.BOLD + Font.ITALIC));		
				}
			}		  
		};
		btnBold.addActionListener(this);



		//========================================
		// italic text button
		ImageIcon italicIcon = GeoGebraIcon.createStringIcon(app.getPlain("Italic").substring(0,1),
				app.getPlainFont(), false, true, true, iconDimension, Color.black, null);
		btnItalic = new MyToggleButton(italicIcon){
			@Override
			public void update(Object[] geos) {
				boolean geosOK = (geos.length > 0 );
				for (int i = 0; i < geos.length; i++) {
					if (!(((GeoElement)geos[i]).getGeoElementForPropertiesDialog().isGeoText())
							|| ((GeoElement)geos[i]) instanceof GeoList ) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);
				if(geosOK){	
					int style = ((GeoText)geos[0]).getFontStyle();
					btnItalic.setSelected(style == Font.ITALIC || style == (Font.BOLD + Font.ITALIC));
				}
			}	

		};
		btnItalic.addActionListener(this);


		//========================================
		// text size button

		String[] textSizeArray = new String[] { 
				app.getPlain("ExtraSmall"), 
				app.getPlain("Small"), 
				app.getPlain("Medium"), 
				app.getPlain("Large"), 
				app.getPlain("ExtraLarge") };


		btnTextSize = new PopupMenuButton(app, textSizeArray, -1, 1, 
				new Dimension(80, maxIconHeight), SelectionTable.MODE_TEXT){

			@Override
			public void update(Object[] geos) {

				boolean geosOK = (geos.length > 0 );
				for (int i = 0; i < geos.length; i++) {
					GeoElement geo = (GeoElement)geos[i];
					if (!(((GeoElement)geos[i]).getGeoElementForPropertiesDialog().isGeoText())
							|| ((GeoElement)geos[i]) instanceof GeoList ) {
						geosOK = false;
						break;
					}
				}
				this.setVisible(geosOK);

				if(geosOK){								
					setSelectedIndex(((GeoText)geos[0]).getFontSize() / 2 + 2); // font size ranges from -4 to 4, transform this to 0,1,..,4
				}
			}		  
		};	
		btnTextSize.addActionListener(this);

	}

	
	
	
	//================================================
	//      Create TableText buttons
	//================================================
	
	
	private void createTableTextButtons(){
		Dimension iconDimension = new Dimension(16, maxIconHeight);
		
		//==============================
		// justification popup
		String[] justifyIcons = new String[]{
				"format-justify-left.png",
				"format-justify-center.png",
				"format-justify-right.png"
		};
		btnTableTextJustify = new PopupMenuButton(ev.getApplication(), justifyIcons, 1,-1,
				new Dimension(20,maxIconHeight), SelectionTable.MODE_ICON_FILE){
			@Override
			public void update(Object[] geos) {
				if(tableText != null){					
					this.setVisible(true);
					String justification = tableText.getJustification(); 
					if(justification.equals("c")) btnTableTextJustify.setSelectedIndex(1);
					else if (justification.equals("r")) btnTableTextJustify.setSelectedIndex(2);
					else btnTableTextJustify.setSelectedIndex(0); //left align
					
				}else{
					this.setVisible(false);
				}
			}
			public String getJustification() {
				String s = "l";
				if(this.getSelectedIndex() == 1) s = "c";
				if(this.getSelectedIndex() == 2) s = "r";
				return s;
			}
		};
		
		btnTableTextJustify.addActionListener(this);
		btnTableTextJustify.setKeepVisible(false);	
	
		
		//==============================
		// bracket style popup

		
	
		ImageIcon[] bracketIcons = new ImageIcon[bracketArray.length];
		for(int i = 0; i<bracketIcons.length; i++){
				bracketIcons[i] = GeoGebraIcon.createStringIcon(bracketArray[i], app.getPlainFont(), true, false, true, new Dimension(30,maxIconHeight) , Color.BLACK, null);
		}
		
		btnTableTextBracket = new PopupMenuButton(ev.getApplication(), bracketIcons, 2,-1,
				new Dimension(30,maxIconHeight), SelectionTable.MODE_ICON){
			@Override
			public void update(Object[] geos) {
				if(tableText != null){					
					this.setVisible(true);
					String s = tableText.getOpenSymbol() + " " + tableText.getCloseSymbol(); 
					int index = 0;
					for(int i = 0; i < bracketArray.length; i++){
						if(s.equals(bracketArray[i])) {
							index = i;
							break;
						}
					}
					//System.out.println("index" + index);
					btnTableTextBracket.setSelectedIndex(index); 
					
				}else{
					this.setVisible(false);
				}
			}
		};
		
		btnTableTextBracket.addActionListener(this);
		btnTableTextBracket.setKeepVisible(false);	
		
		
		
		
		//====================================
		// vertical grid lines toggle button
		btnTableTextLinesV = new MyToggleButton(GeoGebraIcon.createVGridIcon(iconDimension)){
			@Override
			public void update(Object[] geos) {
				if(tableText != null){
					setVisible(true);
					setSelected(tableText.isVerticalLines());
				}else{
					setVisible(false);
				}
			}	
		};
		btnTableTextLinesV.addActionListener(this);
		
		//====================================
		// horizontal grid lines toggle button
		btnTableTextLinesH = new MyToggleButton(GeoGebraIcon.createHGridIcon(iconDimension)){
			@Override
			public void update(Object[] geos) {
				if(tableText != null){
					setVisible(true);
					setSelected(tableText.isHorizontalLines());
				}else{
					setVisible(false);
				}
			}
		};
		btnTableTextLinesH.addActionListener(this);
		
		
		
		
		
		
	}
		
		

	
	//=====================================================
	//                 Event Handlers
	//=====================================================
	
	
	private void updateGUI(){

		if(isIniting) return;

		btnMode.removeActionListener(this);
		switch (mode){
		case EuclidianConstants.MODE_MOVE:
			btnMode.setSelectedIndex(0);
			break;
		case EuclidianConstants.MODE_PEN:
			btnMode.setSelectedIndex(1);
			break;
		case EuclidianConstants.MODE_DELETE:
			btnMode.setSelectedIndex(2);
			break;
		case EuclidianConstants.MODE_SHOW_HIDE_LABEL:
			btnMode.setSelectedIndex(3);
			break;
		case EuclidianConstants.MODE_VISUAL_STYLE:
			btnMode.setSelectedIndex(4);
			break;
		}
		btnMode.addActionListener(this);	
		
		btnPen.removeActionListener(this);
		btnPen.setSelected(mode == EuclidianConstants.MODE_PEN);
		btnPen.addActionListener(this);
		
		btnDelete.removeActionListener(this);
		btnDelete.setSelected(mode == EuclidianConstants.MODE_DELETE);
		btnDelete.addActionListener(this);
		
		btnLabel.removeActionListener(this);
		btnLabel.setSelected(mode == EuclidianConstants.MODE_SHOW_HIDE_LABEL);
		btnLabel.addActionListener(this);
		
		btnShowAxes.removeActionListener(this);
		btnShowAxes.setSelected(ev.getShowXaxis());
		btnShowAxes.addActionListener(this);

		btnShowGrid.removeActionListener(this);
		btnShowGrid.setSelected(ev.getShowGrid());
		btnShowGrid.addActionListener(this);

	}

	
	
	

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
	
		// mode changing buttons, removed for now?
		/* 
		if (source.equals(btnMode)) {
			switch (btnMode.getSelectedIndex()){
			case 0:
				ev.getApplication().setMoveMode();
				break;
			case 1:
				ev.getApplication().setMode(EuclidianConstants.MODE_PEN);
				break;
			case 2:
				ev.getApplication().setMode(EuclidianConstants.MODE_DELETE);	
				break;
			case 3:
				ev.getApplication().setMode(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
				break;
			case 4:
				ev.getApplication().setMode(EuclidianConstants.MODE_VISUAL_STYLE);
			}
		}

		if (source.equals(btnCopyVisualStyle)) {		
			if(btnCopyVisualStyle.isSelected())
				ev.getApplication().setMode(EuclidianConstants.MODE_VISUAL_STYLE);
			else
				ev.getApplication().setMoveMode();
		}


		if (source.equals(btnPen)) {		
			if(btnPen.isSelected())
				ev.getApplication().setMode(EuclidianConstants.MODE_PEN);
			else
				ev.getApplication().setMoveMode();
		}

		if (source.equals(btnDelete)) {		
			if(btnDelete.isSelected())
				ev.getApplication().setMode(EuclidianConstants.MODE_DELETE);
			else
				ev.getApplication().setMoveMode();
		}

		if (source.equals(btnLabel)) {		
			if(btnLabel.isSelected())
				ev.getApplication().setMode(EuclidianConstants.MODE_SHOW_HIDE_LABEL);
			else
				ev.getApplication().setMoveMode();
		}

	*/
		
		needUndo = false;
		
		ArrayList<GeoElement> targetGeos = defaultGeos;
		if(mode == EuclidianConstants.MODE_MOVE)
			targetGeos = app.getSelectedGeos();
		
		
		if (source.equals(btnShowAxes)) {		
			ev.setShowAxes(!ev.getShowXaxis(), true);
			ev.repaint();
		}

		else if (source.equals(btnShowGrid)) {
			ev.showGrid(!ev.getShowGrid());
			ev.repaint();
		}
		
		else if (source == btnColor) {
			if(btnColor.getSelectedValue() != null){
				if(mode == EuclidianConstants.MODE_PEN){
					ec.getPen().setPenColor((Color) btnColor.getSelectedValue());
					//btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());
				} else {
					applyColor(targetGeos);
					//btnLineStyle.setFgColor((Color)btnColor.getSelectedValue());
					//btnPointStyle.setFgColor((Color)btnColor.getSelectedValue());
				}
			}
		}
		else if (source == btnTextColor) {
			if(btnTextColor.getSelectedValue() != null){
				applyTextColor(targetGeos);
				btnTextColor.setFgColor((Color)btnTextColor.getSelectedValue());
				//btnItalic.setForeground((Color)btnTextColor.getSelectedValue());
				//btnBold.setForeground((Color)btnTextColor.getSelectedValue());
			}
		}
		else if (source == btnLineStyle) {
			if(btnLineStyle.getSelectedValue() != null){
				if(mode == EuclidianConstants.MODE_PEN){
					ec.getPen().setPenLineStyle((Integer) btnLineStyle.getSelectedValue());
					ec.getPen().setPenSize(btnLineStyle.getSliderValue());
				} else {
					applyLineStyle(targetGeos);
				}

			}
		}
		else if (source == btnPointStyle) {
			if(btnPointStyle.getSelectedValue() != null){
				applyPointStyle(targetGeos);				
			}
		}
		else if (source == btnBold) {
			btnBold.toggle();
			applyTextBold(targetGeos);			
		}
		else if (source == btnItalic) {
			btnItalic.toggle();
			applyTextItalic(targetGeos);			
		}
		else if (source == btnTextSize) {
			applyTextSize(targetGeos);			
		}
		else if (source == btnHideShowLabel) {
			btnHideShowLabel.toggle();
			applyHideShowLabel(targetGeos);			
		}
		else if (source == btnTableTextJustify ) {
			applyTableTextFormat(targetGeos);			
		}
		else if (source == btnTableTextLinesH ) {
			btnTableTextLinesH.toggle();
			applyTableTextFormat(targetGeos);			
		}
		else if (source ==  btnTableTextLinesV) {
			btnTableTextLinesV.toggle();
			applyTableTextFormat(targetGeos);			
		}
		else if (source == btnTableTextBracket ) {
			applyTableTextFormat(targetGeos);			
		}
		
		else if (source == btnPenDelete) {
			
			//add code here to delete pen image
			
		}
		else if (source == btnPenEraser) {
			btnPenEraser.toggle();
			
			// add code here to toggle between pen and eraser mode;			
			
		}

		if(needUndo){
			app.storeUndoInfo();
			needUndo = false;
		}
		
		updateGUI();
		
	}




	//==============================================
	//           Apply Styles
	//==============================================

	private void applyLineStyle(ArrayList<GeoElement> geos) {
		int lineStyle = (Integer) btnLineStyle.getSelectedValue();
		int lineSize = btnLineStyle.getSliderValue();

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo.getLineType() != lineStyle || geo.getLineThickness() != lineSize){
				geo.setLineType(lineStyle);
				geo.setLineThickness(lineSize);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	private void applyPointStyle(ArrayList<GeoElement> geos) {
		int pointStyle = (Integer) btnPointStyle.getSelectedValue();
		int pointSize = btnPointStyle.getSliderValue();
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if (geo instanceof PointProperties) {
				if(((PointProperties)geo).getPointSize()  != pointSize || (((PointProperties)geo).getPointStyle()  != pointStyle)){
					((PointProperties)geo).setPointSize(pointSize);
					((PointProperties)geo).setPointStyle(pointStyle);
					geo.updateRepaint();
					needUndo = true;
				}
			}
		}
	}


	private void applyColor(ArrayList<GeoElement> geos) {

		Color color = (Color) btnColor.getSelectedValue();
		float alpha = btnColor.getSliderValue() / 100.0f;

		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if (!(geo instanceof GeoImage)){
				if(geo instanceof TextProperties && (geo.getBackgroundColor() != color || geo.getAlphaValue() != alpha )){
					geo.setBackgroundColor(color);
					geo.setAlphaValue(alpha);
					geo.updateRepaint();
					needUndo = true;
				}else if(geo.getObjectColor() != color || geo.getAlphaValue() != alpha ){
					geo.setObjColor(color);
					geo.setAlphaValue(alpha);
					geo.updateRepaint();
					needUndo = true;
				}
			}
		}
	}

	private void applyTextColor(ArrayList<GeoElement> geos) {

		Color color = (Color) btnTextColor.getSelectedValue();
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo.isGeoText() && geo.getObjectColor() != color){
				geo.setObjColor(color);
				geo.updateRepaint();
				needUndo = true;
			}
		}
	}

	private void applyTextItalic(ArrayList<GeoElement> geos) {
		int fontStyle = 0;
		if (btnItalic.isSelected()) fontStyle += 2;
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo.isGeoText() && ((GeoText)geo).getFontStyle() != fontStyle){
			((GeoText)geo).setFontStyle(fontStyle);
			geo.updateRepaint();
			needUndo = true;
			}
		}
	}
	
	
	private void applyTextBold(ArrayList<GeoElement> geos) {

		int fontStyle = 0;
		if (btnBold.isSelected()) fontStyle += 1;
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo.isGeoText() && ((GeoText)geo).getFontStyle() != fontStyle){
			((GeoText)geo).setFontStyle(fontStyle);
			geo.updateRepaint();
			needUndo = true;
			}
		}
	}
	
	

	private void applyTextSize(ArrayList<GeoElement> geos) {

		int fontSize = btnTextSize.getSelectedIndex() * 2 - 4; // transform indices to the range -4, .. , 4
		
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo.isGeoText()   && ((GeoText)geo).getFontSize() != fontSize){
			((GeoText)geo).setFontSize(fontSize); 
			geo.updateRepaint();
			needUndo = true;
			}		
		}
	}
	
	private void applyHideShowLabel(ArrayList<GeoElement> geos) {
		boolean visible = btnHideShowLabel.isSelected();
		for (int i = 0 ; i < geos.size() ; i++) {
			GeoElement geo = geos.get(i);
			if(geo.isLabelVisible() != visible){
				geo.setLabelVisible(visible);
				geo.updateRepaint();
				needUndo = true;
			}
		}		
	}
	
	
	private void applyTableTextFormat(ArrayList<GeoElement> geos) {

		AlgoElement algo = null;
		GeoElement[] input;
		GeoElement geo;
		String arg = null;

		String[] justifyArray = { "l", "c", "r"};
		arg = justifyArray[btnTableTextJustify.getSelectedIndex()];
		if(this.btnTableTextLinesH.isSelected()) arg += "_";
		if(this.btnTableTextLinesV.isSelected()) arg += "|";
		if(btnTableTextBracket.getSelectedIndex() >0)
			arg += this.bracketArray2[btnTableTextBracket.getSelectedIndex()];
		ArrayList<GeoElement> newGeos = new ArrayList<GeoElement>();
		
		StringBuilder cmdText = new StringBuilder();

		for (int i = 0; i < geos.size(); i++) {

			// get the  TableText algo for this geo and its input
			geo = geos.get(i);
			algo = geo.getParentAlgorithm();
			input = algo.getInput();

			// create a new TableText cmd
			cmdText.setLength(0);
			cmdText.append("TableText[");
			cmdText.append(((GeoList) input[0]).getFormulaString(ExpressionNode.STRING_TYPE_GEOGEBRA, false));
			cmdText.append(",\"");
			cmdText.append(arg);
			cmdText.append("\"]");

			// use the new cmd to redefine the geo and save it to a list.
			// (the list is needed to reselect the geo)
			newGeos.add(redefineGeo(geo, cmdText.toString()));
		}

		// reset the selection 
		app.setSelectedGeos(newGeos);
	}

	
	public void applyVisualStyle(ArrayList<GeoElement> geos) {
		
		if(geos == null || geos.size() < 1) return;
		needUndo = false;
		
		if(btnColor.isVisible()) applyColor(geos);
		if(btnLineStyle.isVisible()) applyLineStyle(geos);
		if(btnPointStyle.isVisible()) applyPointStyle(geos);
		if(btnBold.isVisible()) applyTextBold(geos);
		if(btnItalic.isVisible()) applyTextItalic(geos);
		if(btnTextColor.isVisible()) applyTextColor(geos);
		if(btnTextSize.isVisible()) applyTextSize(geos);
		if(btnHideShowLabel.isVisible()) applyHideShowLabel(geos);
		
		if(needUndo){
			app.storeUndoInfo();
			needUndo = false;
		}
		
		
		// TODO update prop panel
		// see code in PropertiesDialog.applyDefaults
		//propPanel.updateSelection(selectionList.toArray());
		
	}

	
	
	

	public Color[] getStyleBarColors(boolean doTextColor) {
		
		Color[] colors = new Color[24];
		
		Color[]	primaryColors = new Color[] {		
				new Color(255, 0, 0), // Red
				new Color(255, 153, 0), // Orange
				new Color(255, 255, 0), // Yellow
				new Color(0, 255, 0), // Green 
				new Color(0, 255, 255), // Cyan 
				new Color(0, 0, 255), // Blue
				new Color(153, 0, 255), // Purple
				new Color(255, 0, 255) // Magenta 
		};
		
		
		for(int i = 0; i< 8; i++){
			
			// first row: primary colors
			colors[i] = primaryColors[i];
			
			// second row: modified primary colors
			float[] hsb = Color.RGBtoHSB(colors[i].getRed(), colors[i].getGreen(), colors[i].getBlue(), null); 
			int rgb;
			if(doTextColor){
				colors[i+8] = colors[i].darker();
			}else{
				rgb = Color.HSBtoRGB((float) (.9*hsb[0]), (float) (.5*hsb[1]), (1*hsb[2]));
				colors[i+8] = new Color(rgb);
			}
			// third row: gray scales (white ==> black)
			float p = 1.0f - i/7f;
			colors[i+16] = new Color(p,p,p);
		}

		// third row: gray scales (white ==> black)
		for(int i = 0; i< 6; i++){		
			float p = 1.0f - i/5f;
			colors[i+16] = new Color(p,p,p);
		}
		colors[22] = colors[23] = Color.white;
		return colors;
		
	}
	
	
	
	public GeoElement redefineGeo(GeoElement geo, String cmdtext) {		
		GeoElement newGeo = null;
		
		if (cmdtext == null)
			return newGeo;
		
		try {
			newGeo = app.getKernel().getAlgebraProcessor().changeGeoElement(
					geo, cmdtext, true);
			app.doAfterRedefine(newGeo); 
			newGeo.updateRepaint();
			return newGeo;
			
		} catch (Exception e) {
			app.showError("ReplaceFailed");			
		} catch (MyError err) {
			app.showError(err);			
		} 
		return newGeo;
	}
	
	
	private class MyToggleButton extends JButton {
		public MyToggleButton(ImageIcon icon){
			super(icon);
			Dimension d = new Dimension(icon.getIconWidth(), maxIconHeight);
			setIcon(GeoGebraIcon.ensureIconSize(icon, d));
			
			//this.setPreferredSize(d);
			//this.setMinimumSize(d);
			this.setRolloverEnabled(true);
			//this.setBorderPainted(true);
		}
		
		public void update(Object[] geos) {	 
		}
		public void toggle(){
			this.setSelected(!this.isSelected());
		}
		
	}

	
	
	

}
