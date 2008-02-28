package geogebra.gui;

import geogebra.Application;
import geogebra.kernel.Kernel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GeoGebraMenuBar extends MenubarImpl implements Menubar, ActionListener {

	public GeoGebraMenuBar(Application app) {
		super();
		this.app = app;
		kernel = app.getKernel();
	}

	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		
		// change angle unit
		if (cmd.equals("Degree")) {
			kernel.setAngleUnit(Kernel.ANGLE_DEGREE);
			kernel.updateConstruction();
			app.setUnsaved();
		} else if (cmd.equals("Radiant")) {
			kernel.setAngleUnit(Kernel.ANGLE_RADIANT);
			kernel.updateConstruction();
			app.setUnsaved();
		}

		// change graphics quality
		else if (cmd.equals("LowQuality")) {
			app.getEuclidianView().setAntialiasing(false);
		} else if (cmd.equals("HighQuality")) {
			app.getEuclidianView().setAntialiasing(true);
		}

		// font size
		else if (cmd.endsWith("pt")) {
			try {
				app.setFontSize(Integer.parseInt(cmd.substring(0, 2)));
				app.setUnsaved();
				System.gc();
			} catch (Exception e) {
				app.showError(e.toString());
			}
			;
		}

		// decimal places
		else if (cmd.endsWith("decimals")) {
			try {
				kernel.setPrintDecimals(Integer.parseInt(cmd.substring(0, 1)));
				kernel.updateConstruction();
				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}			
		}

		// Point capturing
		else if (cmd.endsWith("PointCapturing")) {
			int mode = Integer.parseInt(cmd.substring(0, 1));
			app.getEuclidianView().setPointCapturing(mode);
			app.setUnsaved();
		}

		// Continuity
		else if (cmd.endsWith("Continuity")) {
			boolean state = cmd.startsWith("true");
			kernel.setContinuous(state);
			kernel.updateConstruction();
			app.setUnsaved();
		}
		
		
		// Labeling
		else if (cmd.endsWith("labeling")) {
			int style = Integer.parseInt(cmd.substring(0, 1));
			app.setLabelingStyle(style);
			app.setUnsaved();
		}
	}
}
