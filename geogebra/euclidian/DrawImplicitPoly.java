/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * DrawImplicitPoly.java
 *
 * Created on 03. June 2010, 12:21
 */
package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoImplicitPoly;
import geogebra.kernel.Kernel;
import geogebra.main.Application;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

/**
 * Draw GeoImplicitPoly on euclidian view
 */
public class DrawImplicitPoly extends Drawable {
	
	private GeoImplicitPoly implicitPoly;
	private boolean isVisible;
	private boolean labelVisible;
	private int fillSign; //0=>no filling, only curve -1=>fill the negativ part, 1=>fill positiv part
	
	public DrawImplicitPoly(EuclidianView view,GeoImplicitPoly implicitPoly) {
//		Application.debug("DrawImplicitPoly, new object");
		this.view=view;
    	hitThreshold = view.getCapturingThreshold();
		this.implicitPoly = implicitPoly;
		this.geo=implicitPoly;
		update();
	}
	public Area getShape(){		
		return new Area();
	}
	@Override
	public void draw(Graphics2D g2) {
		if (!isVisible) return;
		if (!geo.isDefined()) return;
		if (geo.doHighlighting()) {
            g2.setStroke(selStroke);
            g2.setColor(geo.getSelColor());
            for (GeneralPath g:gps){
            	Drawable.drawWithValueStrokePure(g, g2);
            }
        }                  
        g2.setStroke(objStroke);
        g2.setColor(geo.getObjectColor());
        for (GeneralPath g:gps){
        	Drawable.drawWithValueStrokePure(g, g2);
        	
        }

        if (labelVisible) {
			g2.setFont(view.fontConic); 
			g2.setColor(geo.getLabelColor());                   
			drawLabel(g2);                                                               
        } 
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		if (isVisible) {
			for (GeneralPath gp:gps){
				if (objStroke.createStrokedShape(gp).intersects(x-hitThreshold,y-hitThreshold,2*hitThreshold,2*hitThreshold))
					return true;
			}
    	}
    	return false;
	}

	@Override
	public boolean isInside(Rectangle rect) {
		if (gps.size()>0)
		for (GeneralPath gp:gps)
			if (!rect.contains(gp.getBounds()))
				return false;
		return true;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo=geo;		
	}

	@Override
	public void update() {
		isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
        labelVisible = geo.isLabelVisible();
        
//        firstX=Double.NaN;
//        firstY=Double.NaN;
        
        updateStrokes(implicitPoly);    
        
        pointNearRDCornerX=0;
        pointNearRDCornerY=0;
        
        if (implicitPoly!=null&&implicitPoly.getCoeff()!=null&&!implicitPoly.isConstant()){ 
        	updateGP();
        }
        
        
        
     // draw trace
		if (implicitPoly.getTrace()) {
			isTracing = true;
			Graphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null){
				g2.setPaint(geo.getObjectColor());	   
				g2.setStroke(objStroke);
				for (GeneralPath g:gps){
	            	Drawable.drawWithValueStrokePure(g, g2);
	            }	
			}
		} else {
			if (isTracing) {
				isTracing = false;
				view.updateBackground();
			}
		}
        
        if (labelVisible) {
        	labelDesc = geo.getLabelDescription();
        	setLabelPosition();
			addLabelOffset();
        }
        if(gps==null)
        	gps=new ArrayList<GeneralPath>();
	}
	
	// set label position (xLabel, yLabel) fixed #255
	//TODO find good label position
    protected final void setLabelPosition() {
    	xLabel=pointNearRDCornerX-10;
    	yLabel=pointNearRDCornerY-10;
    }
	
	private List<GeneralPath> gps;
	/**
	 * X and Y of the point on the curve next to the Right Down Corner of the View, for the Label
	 */
	private int pointNearRDCornerX=0, pointNearRDCornerY=0;
	private ArrayList<Double[]> singularitiesCollection;
	private ArrayList<Double[]> boundaryIntersectCollection;
	
	//Second Algorithm
	final public static double EPS=Kernel.EPSILON;
	
	/**
	 * @param x
	 * @return 0 if |x|<EPS, sgn(x) otherwise
	 */
	public int epsSignum(double x){
		if (x>EPS)
			return 1;
		if (x<-EPS)
			return -1;
		return 0;
	}
	
	private class GridRect{
		double x,y,width,height;
		int[] eval;

		public GridRect(double x, double y, double width, double height) {
			super();
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			eval=new int[4];
		}
	}
	
	private boolean[][] remember;
	private GridRect[][] grid;
	private final static double GRIDSIZE=10; //in Pixel
	
	private double scaleX;
	private double scaleY;
	private void updateGP(){
		try{
			singularitiesCollection=new ArrayList<Double[]>();
			if (fillSign!=0)
				boundaryIntersectCollection=new ArrayList<Double[]>();
		int gridWidth=(int)Math.ceil(view.getWidth()/GRIDSIZE);
		int gridHeight=(int)Math.ceil(view.getHeight()/GRIDSIZE);
		//ticket #249
		if (view.getWidth()==0||view.getHeight()==0){ 
			Application.debug("View not visible (width or height equal to zero)");
			return;
		}
		grid=new GridRect[gridWidth][gridHeight];
		remember=new boolean[gridWidth][gridHeight];
		scaleX=(view.xmax-view.xmin)/view.getWidth();
		scaleY=(view.ymax-view.ymin)/view.getHeight();
		double grw=GRIDSIZE*scaleX;
		double grh=GRIDSIZE*scaleY;
		double x=view.xmin;
		int e;
		for (int w=0;w<gridWidth;w++){
			double y=view.ymax;
			for (int h=0;h<gridHeight;h++){
				e=epsSignum(implicitPoly.evalPolyAt(x,y,true));
				grid[w][h]=new GridRect(x,y,grw,grh);
				grid[w][h].eval[0]=e;
				if (w>0){
					grid[w-1][h].eval[1]=e;
					if (h>0){
						grid[w][h-1].eval[2]=e;
						grid[w-1][h-1].eval[3]=e;
					}
				}
				else if (h>0)
					grid[w][h-1].eval[2]=e;
				y-=grh;
			}
			e=epsSignum(implicitPoly.evalPolyAt(x,y,true));
			grid[w][gridHeight-1].eval[2]=e;
			if (w>0)
				grid[w-1][gridHeight-1].eval[3]=e;
			x+=grw;
		}
		double y=view.ymax;
		for (int h=0;h<gridHeight;h++){
			e=epsSignum(implicitPoly.evalPolyAt(x,y,true));
			grid[gridWidth-1][h].eval[1]=e;
			if (h>0)
				grid[gridWidth-1][h-1].eval[3]=e;
			y-=grh;
		}
		grid[gridWidth-1][gridHeight-1].eval[3]=epsSignum(implicitPoly.evalPolyAt(x,y,true));
		for (int w=0;w<gridWidth;w++){
			for (int h=0;h<gridHeight;h++){
				remember[w][h]=false;
				if (grid[w][h].eval[0]==0){
					remember[w][h]=true;
					continue;
				}
				for (int i=1;i<4;i++){
					if (grid[w][h].eval[0]!=grid[w][h].eval[i]){
						remember[w][h]=true;
						break;
					}
				}
			}
		}
		gps=new ArrayList<GeneralPath>();
		for (int w=0;w<gridWidth;w++){
			for (int h=0;h<gridHeight;h++){
				if (remember[w][h]){
					if (grid[w][h].eval[0]==0){
						gps.add(startPath(w,h,grid[w][h].x,grid[w][h].y));
					}else{
						double xs,ys;
						if (grid[w][h].eval[0]!=grid[w][h].eval[3]){
							double a=bisec(grid[w][h].x,grid[w][h].y,grid[w][h].x+grid[w][h].width,grid[w][h].y-grid[w][h].height);
							xs=grid[w][h].x+a*grid[w][h].width;
							ys=grid[w][h].y-a*grid[w][h].height;
						}else if (grid[w][h].eval[1]!=grid[w][h].eval[2]){
							double a=bisec(grid[w][h].x+grid[w][h].width,grid[w][h].y,grid[w][h].x,grid[w][h].y-grid[w][h].height);
							xs=grid[w][h].x+(1-a)*grid[w][h].width;
							ys=grid[w][h].y-a*grid[w][h].height;
						}else{
							double a=bisec(grid[w][h].x,grid[w][h].y,grid[w][h].x+grid[w][h].width,grid[w][h].y);
							xs=grid[w][h].x+a*grid[w][h].width;
							ys=grid[w][h].y;
						}
						gps.add(startPath(w,h,xs,ys));
					}
				}
			}
		}
//		Application.debug("gps-size="+gps.size());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	private final static double MIN_GRAD=Kernel.STANDARD_PRECISION; 
	private final static double MIN_STEP_SIZE=0.1; //Pixel on Screen
	private final static double START_STEP_SIZE=0.5;
	private final static double MAX_STEP_SIZE=1;
	private final static double MIN_PATH_GAP=1;  
	private final static double SING_RADIUS=1; 
	private final static double NEAR_SING=1E-3;
	
	private double scaledNormSquared(double x,double y){
		return x*x/scaleX/scaleX+y*y/scaleY/scaleY;
	}
	
	private GeneralPath startPath(int w, int h, double x, double y) {
//		Application.debug("startPath at "+x+"/"+y+" in GridRect["+w+","+h+"]");
//		if (Double.isNaN(firstX)||Double.isNaN(firstY)){ //Save the first point on the curve for the label
//			firstX=view.toScreenCoordXd(x);
//			firstY=view.toScreenCoordYd(y);
//		}
		GeneralPath gp=new GeneralPath();
		double sx=x;
		double sy=y;
		double lx=Double.NaN; //no previous point
		double ly=Double.NaN;
		boolean first=true;

		double stepSize=START_STEP_SIZE*Math.max(scaleX, scaleY);
		double startX=x;
		double startY=y;
		float pathX=(float)view.toScreenCoordXd(x);
		float pathY=(float)view.toScreenCoordYd(y);
		gp.moveTo(pathX,pathY);
		int s=0;
		int lastW=w;
		int lastH=h;
		int startW=w;
		int startH=h;
		int stepCount=0;
		boolean nearSing=false;
		double lastGradX=Double.POSITIVE_INFINITY;
		double lastGradY=Double.POSITIVE_INFINITY;
		while(true){
//			if (s>10000){
//				Application.debug("Too much steps");
//				return gp;
//			}
			s++;
			boolean reachedSingularity=false;
			boolean reachedEnd=false;
			if (!Double.isNaN(lx)&&!Double.isNaN(ly)){
				if ((scaledNormSquared(startX-sx, startY-sy)<MAX_STEP_SIZE*MAX_STEP_SIZE)
					&& (scaledNormSquared(startX-sx,startY-sy)<scaledNormSquared(startX-lx,startY-ly))){
					
					pathX=(float)view.toScreenCoordXd(x);
					pathY=(float)view.toScreenCoordYd(y);
					gp.lineTo(pathX,pathY);
//					singularitiesCollection.add(new Double[]{x,y});
//					Application.debug("reached start; s="+s+"; stepcount="+stepCount);
					return gp;
				}
			}
			while (sx<grid[w][h].x){
				if (w>0)
					w--;
				else{
					reachedEnd=true;
					break;
				}
			}
			while (sx>grid[w][h].x+grid[w][h].width){
				if (w<grid.length-1)
					w++;
				else{
					reachedEnd=true;
					break;
				}
			}
			while (sy>grid[w][h].y){
				if (h>0)
					h--;
				else{
					reachedEnd=true;
					break;
				}
			}
			while (sy<grid[w][h].y-grid[w][h].height){
				if (h<grid[w].length-1)
					h++;
				else{
					reachedEnd=true;
					break;
				}
			}
			if (reachedEnd&&fillSign!=0){ //we reached the boundary
				
			}
			if (lastW!=w||lastH!=h){
				int dw=(int)Math.signum(lastW-w);
				int dh=(int)Math.signum(lastH-h);
				for (int i=0;i<=Math.abs(lastW-w);i++){
					for (int j=0;j<=Math.abs(lastH-h);j++){
						remember[lastW-dw*i][lastH-dh*j]=false;
					}
				}
			}
			lastW=w;
			lastH=h;
			
			double gradX=0;
			double gradY=0;
			if (!reachedEnd){
				gradX=implicitPoly.evalDiffXPolyAt(sx, sy,true);
				gradY=implicitPoly.evalDiffYPolyAt(sx, sy,true);
				
				/*
				 * Dealing with singularities: tries to reach the singularity but stops there.
				 * Assuming that the singularity is on or at least near the curve. (Since first
				 * derivative is zero this can be assumed for 'nice' 2nd derivative)
				 */
				
				if (nearSing||(Math.abs(gradX)<NEAR_SING&&Math.abs(gradY)<NEAR_SING)){
					for (Double[] pair:singularitiesCollection){ //check if this singularity is already known
						if ((scaledNormSquared(pair[0]-sx,pair[1]-sy)<SING_RADIUS*SING_RADIUS)){
							sx=pair[0];
							sy=pair[1];
//							Application.debug("jump to Sing @["+sx+","+sy+"]");
							reachedSingularity=true;
							reachedEnd=true;
							break;
						}
					}
					if (!reachedEnd){
						if (gradX*gradX+gradY*gradY>lastGradX*lastGradX+lastGradY*lastGradY){ //going away from the singularity, stop here
//							Application.debug("Sing-c @["+sx+","+sy+"]");
							singularitiesCollection.add(new Double[]{sx,sy});
							reachedEnd=true;
							reachedSingularity=true;
						}else if (Math.abs(gradX)<MIN_GRAD&&Math.abs(gradY)<MIN_GRAD){ //singularity
//							Application.debug("Sing-a @["+sx+","+sy+"]");
							singularitiesCollection.add(new Double[]{sx,sy});
							reachedEnd=true;
							reachedSingularity=true;
						}
//						Application.debug("Near Sing dp = ["+gradX+","+gradY+"]; p = ["+sx+","+sy+"]");
						lastGradX=gradX;
						lastGradY=gradY;
						nearSing=true;
					}
				}
			}
			double a=0,nX=0,nY=0;
			if (!reachedEnd){
				a=1/(Math.abs(gradX)+Math.abs(gradY)); //trying to increase numerical stability
				gradX=a*gradX;
				gradY=a*gradY;
				a=Math.sqrt(gradX*gradX+gradY*gradY);
				gradX=gradX/a; //scale vector
				gradY=gradY/a;
				nX=-gradY;
				nY=gradX;
				if (!Double.isNaN(lx)&&!Double.isNaN(ly)){
					double c=(lx-sx)*nX+(ly-sy)*nY;
					if (c>0){
						nX=-nX;
						nY=-nY;
					}
				}else{
					if (!first){ //other dir now
						nX=-nX;
						nY-=nY;
//						Application.debug("Go in other dir now");
					}
				}
				lx=sx;
				ly=sy;
			}
			while(!reachedEnd){
				sx=lx+nX*stepSize; //go in "best" direction
				sy=ly+nY*stepSize;
				int e=epsSignum(implicitPoly.evalPolyAt(sx,sy,true));
//				Application.debug("s: "+sx+"/"+sy+";l: "+lx+"/"+ly+"; e="+e);
				if (e==0){
					if (stepSize*2<=MAX_STEP_SIZE*Math.max(scaleX, scaleY))
						stepSize*=2;
					break;
				}else{
					gradX=implicitPoly.evalDiffXPolyAt(sx, sy,true);
					gradY=implicitPoly.evalDiffYPolyAt(sx, sy,true);
//					Application.debug("gradient in "+sx+"/"+sy+"="+gradX+"/"+gradY);
					if (Math.abs(gradX)<MIN_GRAD&&Math.abs(gradY)<MIN_GRAD){ //singularity
						stepSize/=2;
						if (stepSize>MIN_STEP_SIZE*Math.max(scaleX, scaleY))
							continue;
						else{
//							Application.debug("Sing-b @["+sx+","+sy+"]");
							singularitiesCollection.add(new Double[]{sx,sy});
							reachedEnd=true;
							break;
						}
					}
					a=Math.sqrt(gradX*gradX+gradY*gradY);
					gradX*=stepSize/a;
					gradY*=stepSize/a;
					if (e>0){
						gradX=-gradX;
						gradY=-gradY;
					}
					int e1=epsSignum(implicitPoly.evalPolyAt(sx+gradX,sy+gradY,true));
					if (e1==0){
						sx=sx+gradX;
						sy=sy+gradY;
						break;
					}
					if (e1!=e){
						a=bisec(sx,sy,sx+gradX,sy+gradY);
						sx+=a*gradX;
						sy+=a*gradY;
						break;
					}else{
						stepSize/=2;
						if (stepSize>MIN_STEP_SIZE*Math.max(scaleX, scaleY))
							continue;
						else{
//							Application.debug("no matching point found");
							reachedEnd=true;
							break;
						}
					}
				}
			}
			if (!reachedEnd||reachedSingularity){
				float newPathX=(float)view.toScreenCoordXd(sx);
				float newPathY=(float)view.toScreenCoordYd(sy);
				if (reachedSingularity||(newPathX-pathX)*(newPathX-pathX)+(newPathY-pathY)*(newPathY-pathY)>MIN_PATH_GAP*MIN_PATH_GAP){
					gp.lineTo(newPathX, newPathY);
					if (pointNearRDCornerY+pointNearRDCornerX<(int)pathY+(int)pathX){
						pointNearRDCornerX=(int)pathX;
						pointNearRDCornerY=(int)pathY;
					}
					stepCount++;
					pathX=newPathX;
					pathY=newPathY;
				}
			}
			if (reachedEnd){
				if (!first){
//					Application.debug("reached end in both dir; s="+s+"; stepcount="+stepCount);
					return gp; //reached the end two times
				}
				lastGradX=Double.POSITIVE_INFINITY;
				lastGradY=Double.POSITIVE_INFINITY;
				pathX=(float)view.toScreenCoordXd(startX);
				pathY=(float)view.toScreenCoordYd(startY);
				gp.moveTo(pathX,pathY);
				sx=startX;
				sy=startY;
				lx=Double.NaN;
				ly=Double.NaN; 
				w=startW;
				h=startH;
				lastW=w;
				lastH=h;
				first=false;//start again with other direction
				reachedEnd=false;
				reachedSingularity=false;
				nearSing=false;
			}
		}
	}
	
	/**
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return a such that |f(x1+(x2-x1)*a,y1+(y2-y1)*a)|<eps
	 */
	public double bisec(double x1,double y1,double x2,double y2){
		int e1=epsSignum(implicitPoly.evalPolyAt(x1,y1,true));
		int e2=epsSignum(implicitPoly.evalPolyAt(x2,y2,true));
		if (e1==0)
			return 0.;
		if (e2==0)
			return 1.;
		double a1=0;
		double a2=1;
		int e;
		if (e1!=e2){
//			Application.debug("enter bisec");
			//solved #278 PRECISION to small (was Double.MIN_VALUE)
			while(a2-a1>Kernel.MAX_PRECISION){
				e=epsSignum(implicitPoly.evalPolyAt(x1+(x2-x1)*(a2+a1)/2,y1+(y2-y1)*(a2+a1)/2,true));
				if (e==0){
//					Application.debug("leave bisec");
					return (a2+a1)/2;
				}
				if (e==e1){
					a1=(a2+a1)/2;
				}else{
					a2=(a1+a2)/2;
				}
			}
//			Application.debug("leave bisec");
			return a1;
		}
		return Double.NaN;
	}

	/**
	 * Returns the poly to be draw
	 * (might not be equal to geo, if this is part of bigger geo)
	 * @return poly
	 */
	public GeoImplicitPoly getPoly() {
		return implicitPoly;
	}

}
