package tutor.gui;

import geogebra.Application;
import geogebra.kernel.Kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import tutor.io.StringOutputStream;
import tutor.net.util.HttpMultiPartFileUpload;
import tutor.net.util.HttpParam;

public class TeacherController implements MouseListener, ActionListener {

	private Application app;
	private Kernel kernel;
	private TeacherView view;

	public void actionPerformed(ActionEvent e) {
		
		if (e.getSource().equals(view.getJustificationCombo())) {
			
			JComboBox cb = (JComboBox) e.getSource();
	        String just = (String)cb.getSelectedItem();    			
	        view.printTextArea(just, TeacherView.ARGUMENT); // changed to static reference Michael Borcherds 2008-04-06
		}
		else if (e.getSource().equals(view.getCommentField())) {
			
			view.processCommentField();
		}/*
		else if (e.getSource().equals(view.getBotoNou())) {
			
			System.out.println("Nou");
			app.deleteAllGeoElements();			
		}
		
		else if (e.getSource().equals(view.getBotoGuardar())) {
			
			System.out.println("Guardar");
			System.out.println(app.getXML());
			
			//String url = "http://localhost/agentgeom/continguts/problemes/upload_file.php";
			String url = "http://158.109.2.26/edumat/agentgeom/continguts/problemes/upload_file.php";
			
			System.out.println("1111111111111");
			
			StringOutputStream sos = new StringOutputStream();
			File fileOut = null;
			
			System.out.println("222222222222222");
			
			try {
				fileOut = File.createTempFile("tempfile",".tmp");
				System.out.println(fileOut.getAbsolutePath());
				
				FileOutputStream fos = new FileOutputStream(fileOut);
				app.getXMLio().writeGeoGebraFile(fos);
				
				HttpParam param = new HttpParam();
				param.setName("fitxer");
				param.setValue(fileOut);
				
				HttpParam pIdProblema = new HttpParam();
				pIdProblema.setName("id_problem");
				pIdProblema.setValue("1");
				
				HttpParam pIdStudent = new HttpParam();
				pIdStudent.setName("id_student");
				pIdStudent.setValue("2");
				
				List params = new ArrayList();
				params.add(param);
				params.add(pIdStudent);
				params.add(pIdProblema);

				HttpMultiPartFileUpload mpfu = new HttpMultiPartFileUpload();
				
				mpfu.send(url, params);
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
			catch (Throwable t) {
				t.printStackTrace();
			}

		}
		*/
	}

	public TeacherController(Kernel kernel) {
		this.kernel = kernel;
		this.app = kernel.getApplication();
	}
	
	public Application getApp() {
		return app;
	}

	public Kernel getKernel() {
		return kernel;
	}

	public TeacherView getView() {
		return view;
	}
	
	public void setApp(Application app) {
		this.app = app;
	}

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	public void setView(TeacherView view) {
		this.view = view;
	}
	
	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		System.out.println("Mouse Clicked.");
		this.view.setCommentFieldFocus();
		view.incrementLineCounter();
	}

	public void mouseReleased(MouseEvent e) {
	}

}
