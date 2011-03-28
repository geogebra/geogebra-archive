package geogebra3D.euclidian3D;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JTextField;

import geogebra.euclidian.EuclidianConstants;
import geogebra.euclidian.EuclidianStyleBar;
import geogebra.euclidian.EuclidianStyleBar.MyToggleButton;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.util.SelectionTable;
import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra.main.MyError;



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
	
	//private JTextField textRotateX;
	
	private MyToggleButton btnViewPerspective, btnViewXY, btnViewXZ, btnViewYZ;

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
		//add(textRotateX);
		add(btnViewPerspective);
		add(btnViewXY);
		add(btnViewXZ);
		add(btnViewYZ);
	}

	protected boolean isVisibleInThisView(GeoElement geo){
		return geo.isVisibleInView3D() ;
	}
	
	
	protected void processSource(Object source, ArrayList<GeoElement> targetGeos){
		if (source.equals(btnRotateView)) {

			if (btnRotateView.getMySlider().isShowing()){//if slider is showing, start rotation
				((EuclidianView3D) ev).setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
			}else{//if button has been clicked, toggle rotation
				if (((EuclidianView3D) ev).isRotAnimatedContinue()){
					((EuclidianView3D) ev).stopRotAnimation();
					btnRotateView.setSelected(false);
				}else{
					((EuclidianView3D) ev).setRotContinueAnimation(0, (btnRotateView.getSliderValue())*0.01);
					btnRotateView.setSelected(true);
				}
			}
			
			/*
		}else if (source.equals(textRotateX)) {
			EuclidianView3D ev3D = ((EuclidianView3D) ev);
			try{
				int angle = Integer.parseInt(textRotateX.getText());
				Application.debug(angle);
				ev3D.setRotAnimation(angle,ev3D.getZRot(),false);
			} catch (Exception e) {
				Application.debug("erreur: "+textRotateX.getText());
				textRotateX.setText(""+((int) ev3D.getXRot()));
			}
			*/
		}else if (source.equals(btnViewPerspective)) {
			((EuclidianView3D) ev).setRotAnimation(-60,20,false);
		}else if (source.equals(btnViewXY)) {
			((EuclidianView3D) ev).setRotAnimation(-90,90,true);
		}else if (source.equals(btnViewXZ)) {
			((EuclidianView3D) ev).setRotAnimation(-90,0,true);
		}else if (source.equals(btnViewYZ)) {
			((EuclidianView3D) ev).setRotAnimation(0,0,true);
		}else
			super.processSource(source, targetGeos);
	}

	protected void createButtons() {

		super.createButtons();
		
		//========================================
		// rotate view button
		btnRotateView = new PopupMenuButton(app, null, -1, -1, null, -1,  false,  true){
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0 && mode != EuclidianConstants.MODE_PEN);	  
			}
		};		
		btnRotateView.setIcon(app.getImageIcon("stylebar_rotateview.gif"));
		btnRotateView.getMySlider().setMinimum(-10);
		btnRotateView.getMySlider().setMaximum(10);
		btnRotateView.getMySlider().setMajorTickSpacing(10);
		btnRotateView.getMySlider().setMinorTickSpacing(1);
		btnRotateView.getMySlider().setPaintTicks(true);
		//btnRotateView.getMySlider().setPaintLabels(true);
		btnRotateView.getMySlider().setPaintTrack(false);
		btnRotateView.getMySlider().setSnapToTicks(true);
		btnRotateView.setSliderValue(5);
		btnRotateView.addActionListener(this);
		
		
		//========================================
		/* rotate x text field
		textRotateX = new JTextField(3);
		textRotateX.addActionListener(this);
		*/
		
		//========================================
		// view perspective button	
		btnViewPerspective = new MyToggleButton(app.getImageIcon("view_perspective.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		btnViewPerspective.addActionListener(this);
		
		
		//========================================
		// view xy button	
		btnViewXY = new MyToggleButton(app.getImageIcon("view_xy.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		btnViewXY.addActionListener(this);
		
		//========================================
		// view xz button	
		btnViewXZ = new MyToggleButton(app.getImageIcon("view_xz.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		btnViewXZ.addActionListener(this);		
		
		//========================================
		// view yz button	
		btnViewYZ = new MyToggleButton(app.getImageIcon("view_yz.gif")){
		      @Override
			public void update(Object[] geos) {
				this.setVisible(geos.length == 0  && mode != EuclidianConstants.MODE_PEN);	  
		      }
		};
		
		btnViewYZ.addActionListener(this);	
	}	
	
	public void setLabels(){
		super.setLabels();
		btnRotateView.setToolTipText(app.getPlainTooltip("stylebar.RotateView"));
		btnViewPerspective.setToolTipText(app.getPlainTooltip("stylebar.ViewPerspective"));
		btnViewXY.setToolTipText(app.getPlainTooltip("stylebar.ViewXY"));
		btnViewXZ.setToolTipText(app.getPlainTooltip("stylebar.ViewXZ"));
		btnViewYZ.setToolTipText(app.getPlainTooltip("stylebar.ViewYZ"));
	}
	
	protected void updateGUI(){
		super.updateGUI();
		
		btnRotateView.removeActionListener(this);
		btnRotateView.setSelected(false);
		btnRotateView.addActionListener(this);
		
		btnViewPerspective.removeActionListener(this);
		btnViewPerspective.setSelected(false);
		btnViewPerspective.addActionListener(this);


		btnViewXY.removeActionListener(this);
		btnViewXY.setSelected(false);
		btnViewXY.addActionListener(this);

		btnViewXZ.removeActionListener(this);
		btnViewXZ.setSelected(false);
		btnViewXZ.addActionListener(this);

		btnViewYZ.removeActionListener(this);
		btnViewYZ.setSelected(false);
		btnViewYZ.addActionListener(this);

		
	}
	
	
	protected PopupMenuButton[] newPopupBtnList(){
		PopupMenuButton[] superList = super.newPopupBtnList();
		PopupMenuButton[] ret = new PopupMenuButton[superList.length+1];
		for (int i=0; i<superList.length; i++)
			ret[i]=superList[i];
		ret[superList.length]=btnRotateView;
		return ret;
	}
	
	protected MyToggleButton[] newToggleBtnList(){
		MyToggleButton[] superList = super.newToggleBtnList();
		MyToggleButton[] ret = new MyToggleButton[superList.length+4];
		for (int i=0; i<superList.length; i++)
			ret[i]=superList[i];
		
		int index = superList.length;
		ret[index]=btnViewPerspective;index++;
		ret[index]=btnViewXY;index++;
		ret[index]=btnViewXZ;index++;
		ret[index]=btnViewYZ;
		return ret;
	}

}
