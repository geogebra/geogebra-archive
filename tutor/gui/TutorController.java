package tutor.gui;

import geogebra.GeoGebraApplicationBase;
import geogebra.kernel.Kernel;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TutorController implements MouseListener {

	private GeoGebraApplicationBase app;
	private Kernel kernel;
	private TutorView view;
	
	
	public TutorController(Kernel kernel) {
		this.kernel = kernel;
		this.app = kernel.getApplication();
	}
	
	public GeoGebraApplicationBase getApp() {
		return app;
	}

	public Kernel getKernel() {
		return kernel;
	}

	public TutorView getView() {
		return view;
	}
	
	public void setApp(GeoGebraApplicationBase app) {
		this.app = app;
	}

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	public void setView(TutorView view) {
		this.view = view;
	}
	
	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse Clicked.");
		this.view.setCommentFieldFocus();
		view.incrementLineCounter();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
