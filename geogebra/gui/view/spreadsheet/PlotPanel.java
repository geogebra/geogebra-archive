package geogebra.gui.view.spreadsheet;

import geogebra.euclidian.EuclidianController;
import geogebra.euclidian.EuclidianView;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoList;
import geogebra.kernel.GeoNumeric;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.NumberValue;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

public class PlotPanel extends JPanel implements ComponentListener {
	
	// ggb 
	private Application app;
	private Kernel kernel; 
	private Construction cons;
	
	private GeoElement plotGeo;
	//private GeoList boundList, freqList;
	
	
	private double xMinData, xMaxData;
	private double xMinEV, xMaxEV, yMinEV, yMaxEV;
	private boolean showYAxis = false;
	private boolean showArrows = false;
	private boolean forceXAxisBuffer = false;
	
	// new EuclidianView instance 
	private myEV ev;
	private EuclidianController ec;
	
	
	
	/*************************************************
	 * Construct the panel
	 */
	public PlotPanel(Application app){
		
		this.app = app;	
		kernel = app.getKernel();
		cons = kernel.getConstruction();
		
		// create an instance of EuclideanView
		ec = new EuclidianController(kernel);
		boolean[] showAxes = { true, true };
		boolean showGrid = false;
		ev = new myEV(ec, showAxes, showGrid);
		ev.setAntialiasing(true);
		ev.updateFonts();
		ev.setPreferredSize(new Dimension(300,200));
		ev.setSize(new Dimension(300,200));
		ev.updateSize();
		
		this.setLayout(new BorderLayout());
		this.add(ev, BorderLayout.CENTER);
		
		this.addComponentListener(this);
		
		
	}

	
	public void removeGeos(){
		if(plotGeo != null){
			plotGeo.remove();
			plotGeo = null;
		}
	}
	
	
	public void setEVParams(){
		
		ev.setShowAxis(EuclidianView.AXIS_Y, showYAxis, false);
		
		if(showArrows){
			ev.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_ARROW);
		}else{
			ev.setAxesLineStyle(EuclidianView.AXES_LINE_TYPE_FULL);
		}
		
		
		// ensure that the axis labels are shown
		// by forcing a fixed pixel height below the x-axis
		
		if(forceXAxisBuffer){
			double pixelOffset = 30 * app.getSmallFont().getSize()/12.0;
			double pixelHeight = this.getHeight(); 
			yMinEV = - pixelOffset * yMaxEV / (pixelHeight + pixelOffset);
		}

		ev.setRealWorldCoordSystem(xMinEV, xMaxEV, yMinEV, yMaxEV);
	
		
	}
	
	
	private class myEV extends EuclidianView {

		public myEV(EuclidianController ec, boolean[] showAxes, boolean showGrid) {
			super(ec, showAxes, showGrid);
			this.removeMouseListener(ec);
			this.removeMouseMotionListener(ec);
			this.setAxesCornerCoordsVisible(false);
		}
		
		// restore the old coord system after a resize
		// this will keep our plots centered and scaled to the new window 
		
		public void updateSize(){
			
			double xminTemp = getXmin();
			double xmaxTemp = getXmax();
			double yminTemp = getYmin();
			double ymaxTemp = getYmax();				
			super.updateSize();		
			setRealWorldCoordSystem(xminTemp, xmaxTemp, yminTemp, ymaxTemp);
		}	
	}
	
	
	public void detachView(){
		kernel.detach(ev);
	}
	
	

	
	//=================================================
	//       Plots
	//=================================================
	
	
	private void setXMinMax(GeoList dataList){
		String label = dataList.getLabel();	
		NumberValue nv;
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Min[" + label + "]", false);		
		xMinData = nv.getDouble();
		
		nv = kernel.getAlgebraProcessor().evaluateToNumeric("Max[" + label + "]", false);		
		xMaxData = nv.getDouble();
	}
	
	
	public void updateHistogram(GeoList dataList, int numClasses){
		
		setXMinMax(dataList);
		String label = dataList.getLabel();	
		String text = "";
		//Application.debug(dataList.toDefinedValueString());	
		double barWidth = (xMaxData - xMinData)/(numClasses - 1);  
		double freqMax = getFrequencyTableMax(dataList, barWidth);
		
		// Set view parameters
		
		//xMinEV = xMinData - barWidth;
		//xMaxEV = xMaxData + barWidth;
		
		double buffer = .25*(xMaxData - xMinData);
		xMinEV = xMinData - buffer;  
		xMaxEV = xMaxData + buffer;
		
		yMinEV = -1.0;
		yMaxEV = 1.1 * freqMax;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();
		//System.out.println(yMaxEV + "," + freqMax + ", "  + barWidth + "," + xMinData);
		
		// Create histogram	
		if(plotGeo != null)
			plotGeo.remove();

		text = "BarChart[" + label + "," + Double.toString(barWidth) + "]";
		plotGeo = createGeoFromString(text);
		Color col = new Color(0, 153, 153);		
		plotGeo.setObjColor(col);
		
		
		
		
	/*
		EmpiricalDistributionImpl dist  = new EmpiricalDistributionImpl(numClasses);
		
		double[] dataArray = new double[dataList.size()];
		for (int i=0; i<dataList.size();++i){
			dataArray[i] = ((GeoNumeric)dataList.get(i)).getDouble();
		}
		
		dist.load(dataArray);
		List<SummaryStatistics> s = dist.getBinStats();
		double[] bounds = dist.getUpperBounds();

		if(boundList == null){
			boundList = new GeoList(cons);
			boundList.setLabel("bounds");
		}

		if(freqList == null){
			freqList = new GeoList(cons);
			freqList.setLabel("freq");
		}
		
		//freqList.clear();
		//boundList.clear();
		

		String boundStr = "{" +  dist.getSampleStats().getN() + ",";
		String freqStr = "{";
		for(int i=0; i < s.size(); ++i){
			freqStr += s.get(i).getN();
			boundStr += bounds[i];
			if(i < s.size()-1){
				freqStr += ",";
				boundStr += ",";
			}
		}
		freqStr += "}";
		boundStr += "}";

		
		System.out.println(boundStr);
		//System.out.println(freqStr);
		
		
		text = "Histogram[" + boundStr + "," + freqStr + "]";
		
		if(plotGeo != null)
			plotGeo.remove();
		
		plotGeo = createGeoFromString(text);
		Color col = new Color(0, 153, 153);		
		plotGeo.setObjColor(col);
		
		*/
			
	}
	

	public void updateBoxPlot(GeoList dataList){

		setXMinMax(dataList);
		String label = dataList.getLabel();	
		String text = "";
		
		// Set view parameters	
		double buffer = .25*(xMaxData - xMinData);
		xMinEV = xMinData - buffer;
		xMaxEV = xMaxData + buffer;
		yMinEV = -1.0;
		yMaxEV = 2;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();
		
		// create boxplot
		if(plotGeo != null)
			plotGeo.remove();
		
		text = "BoxPlot[1,0.5," + label + "]";
		plotGeo = createGeoFromString(text);
		
				
	}
	
	
	public void updateDotPlot(GeoList dataList){

		setXMinMax(dataList);
		String label = dataList.getLabel();	
		String text = "";
	
		double buffer = .25*(xMaxData - xMinData);	
		
		
		// create dotplot
		int maxCount = 1;
		text = "{";
		if(dataList.size()>0){
			text += "(" + ((GeoNumeric)dataList.get(0)).getDouble() + ", 1)";
			int k = 1;
			for (int i = 1; i < dataList.size(); ++i){
				if(((GeoNumeric)dataList.get(i-1)).getDouble() == ((GeoNumeric)dataList.get(i)).getDouble() ) 
					++k;
				else
					k = 1;

				text += ",(" + ((GeoNumeric)dataList.get(i)).getDouble() + "," + k + ")";
				maxCount = maxCount < k ? k : maxCount;
			}
		}
		text += "}";	
		
		if(plotGeo != null)  
			plotGeo.remove();		
		plotGeo = createGeoFromString(text);
		
		
		// Set view parameters		
		xMinEV = xMinData - buffer;
		xMaxEV = xMaxData + buffer;
		yMinEV = -1.0;
		yMaxEV = maxCount + 1;
		showYAxis = false;
		forceXAxisBuffer = true;
		setEVParams();	
		
	}
	
	
	
	private GeoElement createGeoFromString(String text){
		try {
			boolean oldMacroMode = cons.isSuppressLabelsActive();
			cons.setSuppressLabelCreation(true);
			
			GeoElement[] geos = kernel.getAlgebraProcessor()
			.processAlgebraCommandNoExceptionHandling(text, false);
			geos[0].addView(ev);
			//geos[0].setAlgebraVisible(false);
			cons.setSuppressLabelCreation(oldMacroMode);
			geos[0].setLabel(null);
			geos[0].setEuclidianVisible(true);
			geos[0].setAuxiliaryObject(true);
			geos[0].update();
			return geos[0];
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	
	
	//=================================================
	//       Frequency Table 
	//=================================================
	
	// create frequency table
	private double getFrequencyTableMax(GeoList list1, double n){

		//Application.debug(list1.toDefinedValueString());
		
		
		double [] yval; // y value (= min) in interval 0 <= i < N
		double [] leftBorder; // leftBorder (x val) of interval 0 <= i < N
		GeoElement geo;	

		double mini = Double.MAX_VALUE;
		double maxi = Double.MIN_VALUE;
		int minIndex = -1;
		int maxIndex = -1;

		double step = n ;   //n.getDouble();
		int rawDataSize = list1.size();

		if (step < 0 || Kernel.isZero(step) || rawDataSize < 2)
		{
			return 0;
		}


		// find max and min
		for (int i = 0; i < rawDataSize; i++) {
			geo = list1.get(i);
			if (!geo.isGeoNumeric()) {
				return 0;
			}
			double val = ((GeoNumeric)geo).getDouble();

			if (val > maxi) {
				maxi = val;
				maxIndex = i;
			}
			if (val < mini) {
				mini = val;
				minIndex = i;
			}
		}

		if (maxi == mini || maxIndex == -1 || minIndex == -1) {
			return 0;
		}

		double totalWidth = maxi - mini;
		double noOfBars = totalWidth / n;    //n.getDouble();
		double gap = 0;

		int N = (int)noOfBars + 2;
		gap = ((N-1) * step - totalWidth) / 2.0;
		
		NumberValue a = (new GeoNumeric(cons,mini - gap));
		NumberValue b = (new GeoNumeric(cons,maxi + gap));

		yval = new double[N];
		leftBorder = new double[N];


		// fill in class boundaries
		//double width = (maxi-mini)/(double)(N-2);
		for (int i=0; i < N; i++) {
			leftBorder[i] = mini - gap + step * i;
		}


		// zero frequencies
		for (int i=0; i < N; i++) yval[i] = 0; 	

		// work out frequencies in each class
		double datum;

		for (int i=0; i < list1.size() ; i++) {
			geo = list1.get(i);
			if (geo.isGeoNumeric())	datum = ((GeoNumeric)geo).getDouble(); 
			else {  return 0; }

			// fudge to make the last boundary eg 10 <= x <= 20
			// all others are 10 <= x < 20
			double oldMaxBorder = leftBorder[N-1];
			leftBorder[N-1] += Math.abs(leftBorder[N-1] / 100000000);

			// check which class this datum is in
			for (int j=1; j < N; j++) {
				//System.out.println("checking "+leftBorder[j]);
				if (datum < leftBorder[j]) 
				{
					//System.out.println(datum+" "+j);
					yval[j-1]++;
					break;
				}
			}

			leftBorder[N-1] = oldMaxBorder;
		}

		double freqMax = 0.0;
		for(int k = 0; k < yval.length; ++k){
			if(yval[k] > freqMax)
				freqMax = yval[k];
		}
		return freqMax;
		
	}


	
	
	//==================================================
	//       Component Listener  (for resizing our EV)
	//=================================================
	
	public void componentHidden(ComponentEvent arg0) {	
	}
	public void componentMoved(ComponentEvent arg0) {
	}
	public void componentResized(ComponentEvent arg0) {
		// make sure that we force a pixel buffer under the x-axis 
		setEVParams();
	}
	public void componentShown(ComponentEvent arg0) {
	}



}
