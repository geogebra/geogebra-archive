package geogebra3D.euclidian3D;

import geogebra.Matrix.Coords;
import geogebra.kernel.GeoConic;
import geogebra.kernel.arithmetic.Functional2Var;
import geogebra.kernel.kernelND.GeoConicND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.euclidian3D.opengl.PlotterBrush;
import geogebra3D.euclidian3D.opengl.PlotterSurface;
import geogebra3D.euclidian3D.opengl.Renderer;
import geogebra3D.kernel3D.GeoConic3D;

/**
 * @author ggb3D
 * 
 * Drawable for GeoConic3D
 *
 */
public class DrawConic3D extends Drawable3DCurves implements Functional2Var {
	
	
	
	
	/** alpha value for rendering transparency */
	protected float alpha;
	
	
	
	
	/**
	 * @param view3d the 3D view where the conic is drawn
	 * @param conic the conic to draw
	 */
	public DrawConic3D(EuclidianView3D view3d, GeoConicND conic) {
		super(view3d,conic);
	}

	
	
	
	

	public void drawGeometry(Renderer renderer) {
		
		GeoConicND conic = (GeoConicND) getGeoElement();
		
		switch(conic.getType()){
		case GeoConic.CONIC_CIRCLE:
		case GeoConic.CONIC_ELLIPSE:
		case GeoConic.CONIC_HYPERBOLA:
		case GeoConic.CONIC_PARABOLA:
		case GeoConic.CONIC_DOUBLE_LINE:
		case GeoConic.CONIC_INTERSECTING_LINES:
		case GeoConic.CONIC_PARALLEL_LINES:
		case GeoConic.CONIC_SINGLE_POINT:
			renderer.getGeometryManager().draw(getGeometryIndex());
			break;
		default:
			break;
		
		}

	}


	// method used only if surface is not transparent
	public void drawNotTransparentSurface(Renderer renderer){
		
		if(!isVisible()){
			return;
		}
		

		if (alpha<1)
			return;

		GeoConicND conic = (GeoConicND) getGeoElement();

		switch(conic.getType()){
		case GeoConic.CONIC_CIRCLE:
		case GeoConic.CONIC_ELLIPSE:
			setLight(renderer);
			setSurfaceHighlightingColor(alpha);
			renderer.getGeometryManager().draw(getSurfaceIndex());
			break;
		default:
			break;

		}
		

		
	}


	
	

	private double acosh(double x){
		if (x<=1)
			return 0;
		else
			return Math.log(x+Math.sqrt(x*x-1));
	}

	protected boolean updateForItSelf(){
		


		//update alpha value
		//use 1-Math.sqrt(1-alpha) because transparent parts are drawn twice
		alpha = (float) (1-Math.pow(1-getGeoElement().getAlphaValue(),1./3.));
		
		setColorsOutlined(alpha);
    	
		Renderer renderer = getView3D().getRenderer();
		
		
		
		GeoConicND conic = (GeoConicND) getGeoElement();
		
		
		// outline
		
		if (conic.getType()==GeoConic.CONIC_SINGLE_POINT){
			
			PlotterSurface surface;

			surface = renderer.getGeometryManager().getSurface();
			surface.start(this);
			//number of vertices depends on point size
			int nb = 2+conic.getLineThickness();
			surface.setU((float) getMinParameter(0), (float) getMaxParameter(0));surface.setNbU(2*nb); 
			surface.setV((float) getMinParameter(1), (float) getMaxParameter(1));surface.setNbV(nb);
			surface.draw();
			setGeometryIndex(surface.end());
			
		}else{

			PlotterBrush brush = renderer.getGeometryManager().getBrush();	
			brush.start(8);

			brush.setThickness(getGeoElement().getLineThickness(),(float) getView3D().getScale());		


			brush.setAffineTexture(0f,0f);
			double tMin, tMax;
			

			Coords m;
			Coords d;
			Coords ev1, ev2;
			double e1, e2;
			double[] minmax;
			
			switch(conic.getType()){
			case GeoConic.CONIC_CIRCLE:
				m = conic.getMidpoint3D();
				//Application.debug(m.toString()+"\n2D:\n"+conic.getMidpoint2D().toString());
				brush.circle(m, conic.getEigenvec3D(0), conic.getEigenvec3D(1), conic.getHalfAxis(0));
				break;
			case GeoConic.CONIC_ELLIPSE:
				m = conic.getMidpoint3D();
				brush.ellipse(m, conic.getEigenvec3D(0), conic.getEigenvec3D(1), conic.getHalfAxis(0), conic.getHalfAxis(1));
				break;
			case GeoConic.CONIC_HYPERBOLA:
				m = conic.getMidpoint3D();
				ev1 = conic.getEigenvec3D(0);
				ev2 = conic.getEigenvec3D(1);
				e1 = conic.getHalfAxis(0);
				e2 = conic.getHalfAxis(1);
				minmax = getView3D().getRenderer().getIntervalInFrustum(
						new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
						getView3D().getToScreenMatrix().mul(m), getView3D().getToScreenMatrix().mul(ev1.mul(e1).add(ev2.mul(e2))), true);				
				double[] minmax2 = getView3D().getRenderer().getIntervalInFrustum(
						new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
						getView3D().getToScreenMatrix().mul(m), getView3D().getToScreenMatrix().mul(ev1.mul(e1).add(ev2.mul(-e2))), true);

				tMax=acosh(minmax2[1])*1.1; //extends a little
				brush.quarterHyperbola(m, ev1, ev2.mul(-1), e1, e2,tMax);
				tMax=acosh(minmax[1])*1.1;
				brush.quarterHyperbola(m, ev1, ev2, e1, e2,tMax);
				tMax=acosh(-minmax[0])*1.1;
				brush.quarterHyperbola(m, ev1.mul(-1), ev2.mul(-1), e1, e2,tMax);
				tMax=acosh(-minmax2[0])*1.1;
				brush.quarterHyperbola(m, ev1.mul(-1), ev2, e1, e2,tMax);
				break;
			case GeoConic.CONIC_PARABOLA:
				m = conic.getMidpoint3D();
				ev1 = conic.getEigenvec3D(0);
				ev2 = conic.getEigenvec3D(1);
				double p = conic.p;
				minmax = getView3D().getRenderer().getIntervalInFrustum(
						new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
						getView3D().getToScreenMatrix().mul(m), getView3D().getToScreenMatrix().mul(ev1), true);	
				tMax=Math.sqrt(2*minmax[1]/p);
				//Application.debug("max="+minmax[1]+", tMax="+tMax);
				//tMax=4;
				brush.parabola(m, ev1, ev2, p,-2*tMax,2*tMax);
				break;
			case GeoConic.CONIC_DOUBLE_LINE:
				d = conic.getDirection3D(0);
				m = conic.getMidpoint3D();
				minmax = getView3D().getRenderer().getIntervalInFrustum(
						new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
						m, d, true);
				brush.segment(m.add(d.mul(minmax[0])), m.add(d.mul(minmax[1])));
				break;
			case GeoConic.CONIC_INTERSECTING_LINES:
				m = conic.getMidpoint3D();
				
				d = conic.getDirection3D(0);
				minmax = getView3D().getRenderer().getIntervalInFrustum(
						new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
						getView3D().getToScreenMatrix().mul(m), getView3D().getToScreenMatrix().mul(d), true);
				brush.segment(m.add(d.mul(minmax[0])), m.add(d.mul(minmax[1])));

				d = conic.getDirection3D(1);
				minmax = getView3D().getRenderer().getIntervalInFrustum(
						new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
						getView3D().getToScreenMatrix().mul(m), getView3D().getToScreenMatrix().mul(d), true);
				brush.segment(m.add(d.mul(minmax[0])), m.add(d.mul(minmax[1])));
				break;
			case GeoConic.CONIC_PARALLEL_LINES:
				m = conic.getOrigin3D(0);
				d = conic.getDirection3D(0);
				minmax = getView3D().getRenderer().getIntervalInFrustum(
						new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
						getView3D().getToScreenMatrix().mul(m), getView3D().getToScreenMatrix().mul(d), true);
				brush.segment(m.add(d.mul(minmax[0])), m.add(d.mul(minmax[1])));

				m = conic.getOrigin3D(1);
				d = conic.getDirection3D(1);
				minmax = getView3D().getRenderer().getIntervalInFrustum(
						new double[] {Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY},
						getView3D().getToScreenMatrix().mul(m), getView3D().getToScreenMatrix().mul(d), true);
				brush.segment(m.add(d.mul(minmax[0])), m.add(d.mul(minmax[1])));
				break;
			default:
				break;

			}

			setGeometryIndex(brush.end());
			
			// surface
			PlotterSurface surface = renderer.getGeometryManager().getSurface();
			surface.start();
			
			switch(conic.getType()){
			case GeoConic.CONIC_CIRCLE:
				surface.disc(conic.getMidpoint3D(), conic.getEigenvec3D(0), conic.getEigenvec3D(1), conic.getHalfAxis(0));
				break;
			case GeoConic.CONIC_ELLIPSE:
				surface.ellipse(conic.getMidpoint3D(), conic.getEigenvec3D(0), conic.getEigenvec3D(1), conic.getHalfAxis(0), conic.getHalfAxis(1));
				break;
			default:
				break;
			
			}
			
			setSurfaceIndex(surface.end());
		}
		
		
		
		return true;
	}
	

	protected void updateForView(){
		if (getView3D().viewChanged())
			switch(((GeoConicND) getGeoElement()).getType()){
			case GeoConic.CONIC_DOUBLE_LINE:
			case GeoConic.CONIC_HYPERBOLA:
			case GeoConic.CONIC_INTERSECTING_LINES:
			case GeoConic.CONIC_LINE:
			case GeoConic.CONIC_PARABOLA:
			case GeoConic.CONIC_PARALLEL_LINES:
				updateForItSelf();
				break;
			case GeoConic.CONIC_CIRCLE:
			case GeoConic.CONIC_ELLIPSE:
			case GeoConic.CONIC_SINGLE_POINT:
				if (getView3D().viewChangedByZoom()) //update only if zoom occurred
					updateForItSelf();
				break;
			}
		
	}
	
	
	
	public int getPickOrder() {
		return DRAW_PICK_ORDER_1D;
	}
	
	
	
	public void addToDrawable3DLists(Drawable3DLists lists){
		super.addToDrawable3DLists(lists);
		if (((GeoConicND) getGeoElement()).isEndOfQuadric())
			addToDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
		else
			addToDrawable3DLists(lists,DRAW_TYPE_SURFACES);
	}
    
    public void removeFromDrawable3DLists(Drawable3DLists lists){
    	super.removeFromDrawable3DLists(lists);
		if (((GeoConicND) getGeoElement()).isEndOfQuadric())
	    	removeFromDrawable3DLists(lists,DRAW_TYPE_CLOSED_SURFACES);
		else
	    	removeFromDrawable3DLists(lists,DRAW_TYPE_SURFACES);
   	
    }
    
    
    private void drawSurfaceGeometry(Renderer renderer){

    	switch(((GeoConicND) getGeoElement()).getType()){
    	case GeoConic.CONIC_CIRCLE:
		case GeoConic.CONIC_ELLIPSE:
    		renderer.getGeometryManager().draw(getSurfaceIndex());
    		break;
    	}

    }
    

    public void drawTransp(Renderer renderer){
    	if(!isVisible()){
    		return;
    	}


    	if (alpha<=0 || alpha == 1)
    		return;

    	setLight(renderer);

    	setSurfaceHighlightingColor(alpha);

    	drawSurfaceGeometry(renderer);

    }
    
    


	public void drawHiding(Renderer renderer){
		if(!isVisible())
			return;

		if (alpha<=0 || alpha == 1)
			return;
		
		
		drawSurfaceGeometry(renderer);
		
	}
	
	
	
	
	
	
	///////////////////////////////////
	// FUNCTION2VAR INTERFACE
	///////////////////////////////////
	
	








	public Coords evaluatePoint(double u, double v) {
		
		GeoConicND conic = (GeoConicND) getGeoElement();
		
		double r = conic.getLineThickness()/getView3D().getScale()*1.5;
		Coords n = new Coords(new double[] {
				Math.cos(u)*Math.cos(v)*r,
				Math.sin(u)*Math.cos(v)*r,
				Math.sin(v)*r});
		
		return (Coords) n.add(conic.getMidpoint3D());
	}


	

	public Coords evaluateNormal(double u, double v) {
		return new Coords(new double[] {
				Math.cos(u)*Math.cos(v),
				Math.sin(u)*Math.cos(v),
				Math.sin(v)});
	}




	public double getMinParameter(int index) {
		switch(index){
		case 0: //u
		default:
			return 0;
		case 1: //v
			return -Math.PI/2;
		}
	}


	public double getMaxParameter(int index) {
		switch(index){
		case 0: //u
		default:
			return 2*Math.PI; 
		case 1: //v
			return Math.PI/2;
		}
		
	}
	
	


}
