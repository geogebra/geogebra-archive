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
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DrawImplicitPoly extends Drawable {
	
	private GeoImplicitPoly implicitPoly;
	private boolean isVisible;
	private boolean labelVisible;
	
	public DrawImplicitPoly(EuclidianView view,GeoImplicitPoly implicitPoly) {
		this.view=view;
		this.implicitPoly = implicitPoly;
		this.geo=implicitPoly;
		update();
	}

	@Override
	public void draw(Graphics2D g2) {
	//	Application.debug("draw implicitPoly");
		if (!isVisible) return;
		if (!geo.isDefined()) return;
		if (geo.doHighlighting()) {
            g2.setStroke(selStroke);
            g2.setColor(implicitPoly.getSelColor());
            int c=0;
            for (GeneralPath g:gps){
            	g2.setColor(colors[c=(c+1)%colors.length]);
            	Drawable.drawWithValueStrokePure(g, g2);
            }
        }                  
        g2.setStroke(objStroke);
        g2.setColor(implicitPoly.getObjectColor());
        int c=0;
        for (GeneralPath g:gps){
        	g2.setColor(colors[c=(c+1)%colors.length]);
        	Drawable.drawWithValueStrokePure(g, g2);
//        	g2.draw(g);	
        }
        if (labelVisible) {
			g2.setFont(view.fontConic); 
			g2.setColor(implicitPoly.getLabelColor());                   
			drawLabel(g2);                                                               
        } 
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public boolean hit(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInside(Rectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		if (geo instanceof GeoImplicitPoly){
			implicitPoly=(GeoImplicitPoly) geo;
			this.geo=geo;
		}
	}

	@Override
	public void update() {
		//Application.debug("Start Update");
		isVisible = geo.isEuclidianVisible();
        if (!isVisible) return;
        labelVisible = geo.isLabelVisible();
        
        updateStrokes(implicitPoly);          

        updateGP();
        
        if (labelVisible) {
        	labelDesc = geo.getLabelDescription();
			addLabelOffset();
        }
	}
	
	public double evalPolyAt(double x,double y){
		double sum=0;
		double zs=0;
		//Evaluating Poly via the Horner-scheme
		double[][] coeff=implicitPoly.getCoeff();
		if (implicitPoly!=null&&coeff!=null)
			for (int i=coeff.length-1;i>=0;i--){
				zs=0;
				for (int j=coeff[i].length-1;j>=0;j--){
					zs=y*zs+coeff[i][j];
				}
				sum=sum*x+zs;
			}
		return sum;
	}
	
	List<GridRectangle> actList;
	List<GridRectangle> nextList;
	List<GridRectangle> finestList;
	List<GeneralPath> gps;
	
	//double eps;
	double epsX;
	double epsY;

	public enum Dir {NORTH,EAST,WEST,SOUTH;
		public Dir reverse(){
			return values()[3-this.ordinal()];
		};
	}
	
	private class GridRectangle{
		
		double x,y,width,height; //upperLeft Corner
		GridRectangle[] nb;
		GridRectangle[] childs;
		GridRectangle parent;
		double eval; //result of evaluation on this corner
		
		Dir e1; //For the path
		Dir e2;
		
		public GridRectangle(double x, double y, double width, double height,
				GridRectangle parent) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.parent = parent;
			nb=new GridRectangle[4];
			childs=null;
			eval=Double.POSITIVE_INFINITY;
		}
		
		public void followed(Dir d){
			if (e1==d)
				e1=null;
			else if (e2==d)
				e2=null;
			else
				System.out.println("no dir");
		}
		
		public Dir nextDir(){
			if (e1!=null)
				return e1;
			else
				return e2;
		}
		
		public void setNb(GridRectangle rect,Dir dir){
			if (rect==null)
				System.out.println("error nb");
			nb[dir.ordinal()]=rect;
			rect.nb[dir.reverse().ordinal()]=this;
		}
		
		
		public void process(){
			
			double[] ev=new double[4];
			ev[0]=getEval();
			GridRectangle southEast=null;
			if (nb[Dir.EAST.ordinal()]!=null){
				ev[1]=nb[Dir.EAST.ordinal()].getEval();
				southEast=nb[Dir.EAST.ordinal()].nb[Dir.SOUTH.ordinal()];
			}else
				ev[1]=evalPolyAt(x+width,y);
			if (nb[Dir.SOUTH.ordinal()]!=null){
				ev[2]=nb[Dir.SOUTH.ordinal()].getEval();
				if (southEast==null)
					southEast=nb[Dir.SOUTH.ordinal()].nb[Dir.EAST.ordinal()];
			}else
				ev[2]=evalPolyAt(x,y-height);
			if (southEast!=null)
				ev[3]=southEast.getEval();
			else
				ev[3]=evalPolyAt(x+width,y-height);
			if (width<epsX&&height<epsY){
				int pc=0;
				int pe=-1;
				int me=-1;
				e1=null;
				e2=null;
				for (int i=0;i<4;i++){
					if (ev[i]>0){ //==0?
						pc++;
						pe=i;
					}else{
						me=i;
					}
				}

				switch(pc){
				case 0:
				case 4:
					return;
				case 1:
					break;
				case 2: //TODO rework for +--+ or similar
					if (Math.abs(pe-me)==1){
						e1=Dir.NORTH;
					}else{
						e1=Dir.WEST;
					}
					e2=e1.reverse();
					break;
				case 3:
					pe=me;
					break;
				}
				if (e1==null)
					switch(pe){
					case 0:
						e1=Dir.NORTH;
						e2=Dir.WEST;
						break;
					case 1:
						e1=Dir.NORTH;
						e2=Dir.EAST;
						break;
					case 2:
						e1=Dir.SOUTH;
						e2=Dir.WEST;
						break;
					case 3:
						e1=Dir.SOUTH;
						e2=Dir.EAST;
					}
				finestList.add(this);
				return;
			}
			
			boolean sigCh=false;
			for (int i=0;i<3;i++){
				sigCh=sigCh||(Math.signum(ev[i])!=Math.signum(ev[i+1]));
			}
					
			if (sigCh||isCrit()){
				refine();
			}
		}
		
		private void setNbDir(Dir dir,int c1,int nbc1,int c2,int nbc2){
			GridRectangle nbPar=nb[dir.ordinal()];
			if (nbPar!=null&&nbPar.childs!=null){ //Already refined
				childs[c1].setNb(nbPar.childs[nbc1],dir);
				childs[c2].setNb(nbPar.childs[nbc2],dir);
			}
		}
		
		private void refine() {
			childs=new GridRectangle[4];
			childs[0]=new GridRectangle(x,y,width/2,height/2,this);
			childs[1]=new GridRectangle(x+width/2,y,width/2,height/2,this);
			childs[2]=new GridRectangle(x,y-height/2,width/2,height/2,this);
			childs[3]=new GridRectangle(x+width/2,y-height/2,width/2,height/2,this);
			setNbDir(Dir.NORTH,0,2,1,3);
			setNbDir(Dir.EAST,1,0,3,2);
			setNbDir(Dir.WEST,0,1,2,3);
			setNbDir(Dir.SOUTH,2,0,3,1);
			childs[0].setNb(childs[1], Dir.EAST);
			childs[1].setNb(childs[3], Dir.SOUTH);
			childs[3].setNb(childs[2], Dir.WEST);
			childs[2].setNb(childs[0], Dir.NORTH);
	
			for (GridRectangle r:childs){
				nextList.add(r);
			}
		}

		public double getEval(){
			if (eval==Double.POSITIVE_INFINITY)
				eval=evalPolyAt(x,y);
			return eval;
		}
		
		public boolean isCrit2(){
			return false;
		}
		
		public boolean isCrit(){
			double maxSum=0;
			double minSum=0;
			if (implicitPoly==null||implicitPoly.getCoeff()==null)
				return false;
			
			double[][] coeff=implicitPoly.getCoeff();
			
			double cx=1;
			double cxw=1;
			double cy=1;
			double cyh=1;
			
			double max;
			double min;
			
//			StringBuilder sb=new StringBuilder();
//			sb.append("x in ["+x+","+(x+width)+"]\n");
//			sb.append("y in ["+(y-height)+","+y+"]\n");
//			
			for (int i=0;i<coeff.length/2;i++){
				int degY=coeff[2*i].length;
				if (2*i+1<coeff.length){
					degY=Math.max(degY,coeff[2*i+1].length);
				}
				cy=1;
				cyh=1;
				cx*=x*x;
				cxw*=(x+width)*(x+width);
				for (int j=0;j<degY/2;j++){
					cy*=y*y;
					cyh*=(y-height)*(y-height);
					for (int k=0;k<4;k++){
						double c;
						if (2*i+(k&1)>=coeff.length){
							c=0;
						}
						else{
							if (2*j+(k&2)/2>=degY){
								c=0;
							}else{
								c=coeff[2*i+(k&1)][2*j+(k&2)/2];
							}
						}
						if (c==0)
							continue;
						boolean pos=c>=0;
						double dx=1;
						double dxw=1;
						double dy=1;
						double dyh=1;
						if ((k&1)==1){
							pos=(pos||(x<0))&&(!pos||(x>=0));
							dx=x;
							dxw=x+width;
						}
						if ((k&2)==2){
							pos=(pos||(y<=0))&&(!pos||(y>0));
							dy=y;
							dyh=y-height;
						}
						max=c;
						min=c;
						if (x>=0){
							max*=cxw*dxw;
							min*=cx*dx;
						}else{
							max*=cx*dx;
							min*=cxw*dxw;
						}
						if (y>0){
							max*=cy*dy;
							min*=cyh*dyh;
						}else{
							max*=cyh*dyh;
							min*=cy*dy;
						}
						if (!pos){
							max=max+min;
							min=max-min;
							max=max-min;
						}
//						sb.append("pos="+pos+";c="+c+";x="+x+";y="+y+";k="+k+";max="+max+";min="+min+"\n");
						maxSum+=max;
						minSum+=min;
					}
				}
			}
			
//			int[] wmax={1,0,1,1,0,1,0,1,0};
//			int[] hmax={0,0,1,1,1,0,0,0,1};
//			int s;
//			int t;
//			for (int i=0;i<implicitPoly.getCoeff().length;i++){
//				s=((i&1+1)*(x>0?0:1));
//				for (int j=0;j<implicitPoly.getCoeff()[i].length;j++){
//					t=s+3*((j&1+1)*(y>0?0:1));
//					
//					double dmax=implicitPoly.getCoeff()[i][j];
//					double dmin=dmax;
//					if (dmax!=0){
//						int w=wmax[t];
//						int h=hmax[t];
//						if (dmax<0){
//							w=1-w;
//							h=1-h;
//						}
//						for (int p=0;p<i;p++){
//							dmax*=x+w*width;
//							dmin*=x+(1-w)*width;
//						}
//						for (int p=0;p<j;p++){
//							dmax*=y-h*height;
//							dmin*=y-(1-h)*height;
//						}
//						maxSum+=dmax;
//						minSum+=dmin;
//					}
//				}
//			}
//			sb.append("max="+maxSum+" min="+minSum);
			if (maxSum<0&&minSum>0){
				Application.debug("max="+maxSum+" min="+minSum);
			}
			return (maxSum>=0&&minSum<=0);
//			int[] xCoeff={3,2,1,0,3,2,1,0,3,2,1,0,3,2,1,0};
//			int[] yCoeff={3,3,3,3,2,2,2,2,1,1,1,1,0,0,0,0};
//		//	int[] xTimesCoeff={0,0,0,1,1,2,4,4,4,5,5,6,8,8,8,9,9,10,12,12,12,13,13,14};
//		//	int[] yTimesCoeff={0,0,0,1,1,1,2,2,2,3,3,3,4,4,5,5,6,6,7,7,8,9,10,11};
//			double maxSum=0;
//			double minSum=0;
//			double c=0;
//			for (int i=0;i<16;i++){
//				double max=Double.NEGATIVE_INFINITY;
//				double min=Double.POSITIVE_INFINITY;
//				for (int h=0;h<=1;h++)
//				for (int w=0;w<=1;w++){
//					c=cubic.getCoeffs()[i];
//					for (int p=0;p<xCoeff[i];p++)
//						c*=x+w*width;
//					for (int p=0;p<yCoeff[i];p++)
//						c*=y-h*height;
//					if (c>max)
//						max=c;
//					if (c<min)
//						min=c;
//				}
//				maxSum+=max;
//				minSum+=min;
//			}
//			return (maxSum>0&&minSum<0);
	//		return false;
		}
		
	}
	
	final public void updateGP() {
		try{

        
		if (!isVisible) return; 
		
    	double minx = view.getXmin();
    	double maxx = view.getXmax();
    	double miny = view.getYmin();
    	double maxy = view.getYmax();
	
    	epsX=(maxx-minx)/view.getWidth()/2;
    	epsY=(maxy-miny)/view.getHeight()/2;
		//eps=Math.min(, )/2;///2;
//    	eps=0.5;
    	
		GridRectangle R1,R2,R3,R4;
		R1=new GridRectangle(minx,maxy,-minx,maxy,null);
		R2=new GridRectangle(0,maxy,maxx,maxy,null);
		R3=new GridRectangle(minx,0,-minx,-miny,null);
		R4=new GridRectangle(0,0,maxx,-miny,null);
		R1.setNb(R2, Dir.EAST);
		R2.setNb(R4, Dir.SOUTH);
		R4.setNb(R3, Dir.WEST);
		R3.setNb(R1, Dir.NORTH);
		actList=new ArrayList<GridRectangle>();
		finestList=new ArrayList<GridRectangle>();
		actList.add(R1);
		actList.add(R2);
		actList.add(R3);
		actList.add(R4);
		while(actList.size()!=0){
			nextList=new ArrayList<GridRectangle>();
			for (GridRectangle R: actList){
				R.process();
			}
			actList=nextList;
		}
		
		gps=new ArrayList<GeneralPath>();
		
		if (finestList.size()>0){
			Iterator<GridRectangle> iter=finestList.iterator();
			GridRectangle r=iter.next();
			while(true){
				Dir d=r.nextDir();
				if (d!=null){
					gps.add(followPath(r,d));
				}else{
					if (iter.hasNext())
						r=iter.next();
					else
						break;
				}
			}
		}
		
		Application.debug("gps-size: "+gps.size());
		Application.debug("max-length: "+maxgpl);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	int maxgpl=0;
	
	public GeneralPath followPath(GridRectangle r,Dir e){
		GeneralPath gp=new GeneralPath();
		double[] coords={r.x+r.width/2,r.y-r.height/2};
		view.toScreenCoords(coords);
		gp.moveTo((float)coords[0],(float)coords[1]);
	//	gp.moveTo(view.toScreenCoordX(r.x+r.width/2), view.toScreenCoordY(r.y-r.height/2));
		r.followed(e);
		int i=1;
		Dir lastDir;
		double[] lastCoords=null;
		boolean drawNext=false;
//		int lc=0;
		while (e!=null&&r.nb[e.ordinal()]!=null){
			r.followed(e);
			r=r.nb[e.ordinal()];
			r.followed(e.reverse());
			lastDir=e;
			e=r.nextDir();
			coords[0]=r.x+r.width/2;
			coords[1]=r.y-r.height/2;
			view.toScreenCoords(coords);
			if (drawNext){
				gp.lineTo((float)coords[0],(float)coords[1]);
				lastCoords=null;
				drawNext=false;
				lastDir=e;
			}
			if (lastDir!=e){
				drawNext=true;
				if (lastCoords!=null)
					gp.lineTo((float)lastCoords[0],(float)lastCoords[1]);
				lastCoords=null;
			}else{
				lastCoords=new double[2];
				for (int j=0;j<2;j++)
					lastCoords[j]=coords[j];
			}
			//gp.lineTo(view.toScreenCoordX(r.x+r.width/2), view.toScreenCoordY(r.y-r.height/2));
			i++;
		}
		if (maxgpl<i)
			maxgpl=i;
		if (lastCoords!=null)
			gp.lineTo((float)lastCoords[0],(float)lastCoords[1]);
		return gp;
	}
	
	Color[] colors={Color.black};//{Color.red,Color.blue,Color.GREEN,Color.GRAY,Color.black,Color.MAGENTA,Color.orange};



}
