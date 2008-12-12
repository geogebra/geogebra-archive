package geogebra3D.euclidian3D;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra.main.Application;
import geogebra3D.kernel3D.GeoRay3D;

public class DrawRay3D extends Drawable3D {

	private GeoRay3D m_ray;
	
	public DrawRay3D(EuclidianView3D a_view, GeoRay3D a_ray)
	{
        Application.debug("DrawRay3D : constructor");

        m_ray=a_ray;
		setView3D(a_view);
		setGeoElement(a_ray);
		
		update();
	}
	
	public void draw(EuclidianRenderer3D renderer) {
		if(!getGeoElement3D().isEuclidianVisible())
			return;
		
		renderer.setMaterial(getGeoElement().getObjectColor(),1.0f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawCylinder(LINE3D_THICKNESS); 
		renderer.resetMatrix();
	}

	@Override
	public void drawForPicking(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawHidden(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawHiding(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawPicked(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawTransp(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getPickOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update() {
	        setVisible(getGeoElement().isEuclidianVisible());       				 
	        if (!isVisible()) return;
	        setLabelVisible(getGeoElement().isLabelVisible());    	

			GgbMatrix mc = m_ray.getSegmentMatrix(0,21);  //TODO use frustrum
			getView3D().toScreenCoords3D(mc);
			
			setMatrix(mc.copy());
			
			
			//dashLength = 0.12f/((float) L.getUnit()); //TODO use object property

	}

}
