package geogebra3D.euclidian3D;

import java.awt.Dimension;
import java.util.ArrayList;

import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianStyleBar;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;



/**
 * StyleBar for 3D euclidian view
 * 
 * @author matthieu
 *
 */
public class EuclidianStyleBar3D extends EuclidianStyleBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private PopupMenuButton btnRotateView;

	/**
	 * Common constructor.
	 * @param ev
	 */
	public EuclidianStyleBar3D(EuclidianView3D ev) {
		super(ev);
	}
	
	
	protected void addBtnPointCapture(){}
	
	protected void addBtnRotateView(){
		add(btnRotateView);
	}

	protected boolean isVisibleInThisView(GeoElement geo){
		return geo.isVisibleInView3D() ;
	}
	
	
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos){
		if (source.equals(btnRotateView)) {

			
			if (btnRotateView.getMySlider().hasFocus()){//if slider has focus, start rotation
				((EuclidianView3D) ev).setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
			}else{//if button has been clicked, toggle rotation
				if (((EuclidianView3D) ev).isRotAnimatedContinue()){
					((EuclidianView3D) ev).stopRotAnimation();
				}else{
					((EuclidianView3D) ev).setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
				}
			}
			
		}else
			super.processSource(source, targetGeos);
	}

	protected void createButtons() {

		super.createButtons();
		
		//========================================
		// show grid button
		btnRotateView = new PopupMenuButton(app, null, -1, -1, null, -1,  false,  true){
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0 && mode != EuclidianConstants.MODE_PEN);	  
			}
		};		
		btnRotateView.setIcon(app.getImageIcon("stylebar_rotateview.gif"));
		btnRotateView.getMySlider().setMinimum(-10);
		btnRotateView.getMySlider().setMaximum(10);
		btnRotateView.getMySlider().setMajorTickSpacing(10);
		btnRotateView.getMySlider().setMinorTickSpacing(5);
		btnRotateView.getMySlider().setPaintTicks(true);
		//btnRotateView.getMySlider().setSnapToTicks(true);
		btnRotateView.setSliderValue(5);
		btnRotateView.addActionListener(this);
	}	
	
	public void setLabels(){
		super.setLabels();
		btnRotateView.setToolTipText(app.getPlainTooltip("stylebar.RotateView"));
	}
	
	protected void updateGUI(){
		super.updateGUI();
		
		btnRotateView.removeActionListener(this);
		btnRotateView.setSelected(false);
		btnRotateView.addActionListener(this);

		
	}

}
