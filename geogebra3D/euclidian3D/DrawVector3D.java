package geogebra3D.euclidian3D;

import geogebra.kernel.linalg.GgbMatrix;
import geogebra3D.kernel3D.GeoVector3D;

public class DrawVector3D extends Drawable3D {

	private GeoVector3D m_vector;
	
	public DrawVector3D(EuclidianView3D a_view3D, GeoVector3D a_vector)
	{
		this.m_vector=a_vector;
		setView3D(a_view3D);
		setGeoElement(a_vector);
		update();
	}
	
	
	public void draw(EuclidianRenderer3D renderer) {
		if(!getGeoElement().isEuclidianVisible())
			return;
		
		renderer.setMaterial(getGeoElement().getObjectColor(),1.0f);//TODO geo.getAlphaValue());
		renderer.setMatrix(getMatrixGL());
		renderer.drawCylinder(LINE3D_THICKNESS); 
		renderer.resetMatrix();

	}

	
	public void drawForPicking(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	
	public void drawHidden(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	
	public void drawHiding(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	
	public void drawPicked(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	
	public void drawTransp(EuclidianRenderer3D renderer) {
		// TODO Auto-generated method stub

	}

	
	public int getPickOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void update() {
        setVisible(getGeoElement().isEuclidianVisible());       				 
        if (!isVisible()) return;
        setLabelVisible(getGeoElement().isLabelVisible());    	

		GgbMatrix mc = m_vector.getMatrixCompleted();  //TODO use frustrum
		getView3D().toScreenCoords3D(mc);
		
		setMatrix(mc.copy());

	}

}
