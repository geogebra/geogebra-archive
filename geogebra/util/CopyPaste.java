/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.util;

import geogebra.main.Application;
import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.ConstructionElement;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.ExpressionNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Arrays;

/**
 * This class saves the given geos
 * (which are usually the selected ones) into an XML string, and
 * makes it possible to insert a copy of them into the construction
 * As a nature of the clipboard,
 * this class contains only static data and methods
 * 
 * @author Arpad Fekete
 */
public class CopyPaste {

	public static final String labelPrefix = "CLIPBOARDMAGICSTRING";
	protected static StringBuilder copiedXML;
	protected static ArrayList<String> copiedXMLlabels;

	public static boolean isEmpty() {
		if (copiedXML == null)
			return true;
		
		return (copiedXML.length() == 0);
	}

	/**
	 * copyToXML - Step 1
	 * Drop the GeoElements in ArrayList which are depending from outside
	 *  
	 * @param geos input and output
	 */
	public static void dropGeosDependentFromOutside(ArrayList<ConstructionElement> geos) {
		
		ConstructionElement geo;
		GeoElement geo2;
		for (int i = geos.size() - 1; i >= 0; i--)
		{
			geo = (ConstructionElement) geos.get(i);
			if (geo.isIndependent()) {
				continue;
			} else {
				TreeSet ts = geo.getAllIndependentPredecessors();

				// exclude from ts the numeric input of AlgoPolygonRegular or AlgoCirclePointRadius
		    	Iterator it = ts.iterator();
		    	while (it.hasNext()) {
		    		geo2 = (GeoElement)it.next();
		    		if (geo2.isGeoNumeric()) {
		    			// check the case of input of AlgoPolygonRegular
		    			ArrayList<ConstructionElement> geoal = geo2.getAlgorithmList();
		    			if ((geoal.size() == 1) && ((AlgoElement)geoal.get(0)).getClassName().equals("AlgoPolygonRegular")) {
		    				it.remove();
		    			// or AlgoCirclePointRadius
		    			} else if ((geoal.size() == 1) && ((AlgoElement)geoal.get(0)).getClassName().equals("AlgoCirclePointRadius")) {
		    				it.remove();
		    			}
		    		}
		    	}

				if (geos.containsAll(ts)) {
					continue;
				} else {
					geos.remove(i);
				}
			}
		}
	}

	/**
	 * copyToXML - Step 2
	 * Add geos which might be intermediates between our existent geos
	 *  
	 * @param geos input and output
	 * @return just the intermediate geos for future handling
	 */
	public static ArrayList<ConstructionElement> addIntermediateGeos(ArrayList<ConstructionElement> geos) {

		ArrayList<ConstructionElement> ret = new ArrayList<ConstructionElement>();

		GeoElement geo, geo2;
		TreeSet<GeoElement> ts;
		Iterator<GeoElement> it;
		for (int i = 0; i < geos.size(); i++)
		{
			geo = (GeoElement) geos.get(i);
			ts = geo.getAllPredecessors();
	    	it = ts.iterator();
	    	while (it.hasNext()) {
	    		geo2 = it.next();
	    		if (!ret.contains(geo2) && !geos.contains(geo2)) {
	    			// note: may contain independent GeoNumeric input of AlgoPolygonRegular or AlgoCirclePointRadius, too
	    			ret.add(geo2);
	    		}
	    	}  
		}
		geos.addAll(ret);
		return ret;
	}

	/**
	 * copyToXML - Step 3
	 * Add the algos which belong to our selected geos
	 * Also add the geos which might be side-effects of these algos
	 *  
	 * @param conels input and output
	 * @return the possible side-effect geos
	 */
	public static ArrayList<ConstructionElement> addAlgosDependentFromInside(ArrayList<ConstructionElement> conels) {

		ArrayList<ConstructionElement> ret = new ArrayList<ConstructionElement>();
		
		GeoElement geo;
		ArrayList<ConstructionElement> geoal;
		AlgoElement ale;
		ArrayList<ConstructionElement> ac;
		GeoElement [] geos;
		for (int i = conels.size() - 1; i >= 0; i--)
		{
			geo = (GeoElement) conels.get(i);
			geoal = geo.getAlgorithmList();

			for (int j = 0; j < geoal.size(); j++) {
				ale = (AlgoElement) geoal.get(j);
				ac = new ArrayList<ConstructionElement>();
				ac.addAll(ale.getAllIndependentPredecessors());
				if (conels.containsAll(ac) && !conels.contains((ConstructionElement) ale)) {
					conels.add((ConstructionElement) ale);
					geos = ale.getOutput();
					for (int k = 0; k < geos.length; k++) {
						if (!ret.contains(geos[k]) && !conels.contains(geos[k])) {
					    	ret.add(geos[k]);
					    }
					}
				}
			}
		}
		conels.addAll(ret);
		return ret;
	}

	/**
	 * copyToXML - Step 4
	 * Before saving the conels to xml, we have to rename its labels
	 * with labelPrefix and memorize those renamed labels
	 * and also hide the GeoElements in geostohide, and keep in
	 * geostohide only those which were actually hidden...
	 * 
	 * @param conels
	 * @param geostohide
	 */
	public static void beforeSavingToXML(ArrayList<ConstructionElement> conels, ArrayList<ConstructionElement> geostohide) {

		copiedXMLlabels = new ArrayList<String>();

		ConstructionElement geo;
		String label;
		String reallabel;
		for (int i = 0; i < conels.size(); i++)
		{
			geo = (ConstructionElement) conels.get(i);
			if (geo.isGeoElement())
			{
				label = ((GeoElement)geo).getLabelSimple();
				if (label != null) {
					((GeoElement)geo).setLabel(labelPrefix + label);
					copiedXMLlabels.add(((GeoElement)geo).getLabelSimple());

					// TODO: check possible realLabel issues
					//reallabel = ((GeoElement)geo).getRealLabel();
					//if (!reallabel.equals( ((GeoElement)geo).getLabelSimple() )) {
					//	((GeoElement)geo).setRealLabel(labelPrefix + reallabel);
					//}
				}
			}
		}

		for (int j = geostohide.size() - 1; j >= 0; j--)
		{
			geo = geostohide.get(j);
			if (geo.isGeoElement() && ((GeoElement)geo).isEuclidianVisible()) {
				((GeoElement)geo).setEuclidianVisible(false);
			} else {
				geostohide.remove(geo);
			}
		}
	}

	/* This method is buggy, so kernel.restoreCurrentUndoInfo is used instead of it
	 * copyToXML - Step 6
	 * After saving the conels to xml, we have to rename its labels
	 * and also show the GeoElements in geostoshow
	 * 
	 * @param conels
	 * @param geostoshow
	 *
	/*public static void afterSavingToXML(ArrayList<ConstructionElement> conels, ArrayList<ConstructionElement> geostoshow) {

		ConstructionElement geo;
		String label;
		for (int i = 0; i < conels.size(); i++)
		{
			geo = (ConstructionElement) conels.get(i);
			if (geo.isGeoElement())
			{
				((GeoElement)geo).setRealLabel(null);
				label = ((GeoElement)geo).getRealLabel();
				try {
					((GeoElement)geo).setLabel(label.substring(labelPrefix.length()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		for (int j = geostoshow.size() - 1; j >= 0; j--)
		{
			geo = geostoshow.get(j);
			if (geo.isGeoElement()) {
				((GeoElement)geo).setEuclidianVisible(true);
			}
		}
	}*/

	/**
	 * This method saves independent geos - and those which
	 * depend only on these - as XML
	 */
	public static void copyToXML(Application app, ArrayList<GeoElement> geos) {

		copiedXML = new StringBuilder();
		copiedXMLlabels = new ArrayList<String>();

		if (geos.isEmpty())
			return;
		
		// create geoslocal and geostohide
		ArrayList<ConstructionElement> geoslocal = new ArrayList<ConstructionElement>();
		geoslocal.addAll(geos);
		dropGeosDependentFromOutside(geoslocal);
		
		if (geoslocal.isEmpty())
			return;

		ArrayList<ConstructionElement> geostohide = addIntermediateGeos(geoslocal);
		geostohide.addAll(addAlgosDependentFromInside(geoslocal));

		// store undo info
		Kernel kernel = app.getKernel();
		kernel.setUndoActive(true);
        kernel.getConstruction().storeUndoInfo();

		// change kernel settings temporarily
		int oldCoordStlye = kernel.getCoordStyle();
		int oldPrintForm = kernel.getCASPrintForm();
        boolean oldValue = kernel.isTranslateCommandName();
		kernel.setCoordStyle(Kernel.COORD_STYLE_DEFAULT);
		kernel.setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
        kernel.setTranslateCommandName(false);

		beforeSavingToXML(geoslocal, geostohide);
		try {
			// step 5
			copiedXML = new StringBuilder();
			ConstructionElement ce;

			// loop through Construction to keep the good order of ConstructionElements
			Construction cons = app.getKernel().getConstruction();
			for (int i = 0; i < cons.steps(); ++i) {
				ce = cons.getConstructionElement(i);
				if (geoslocal.contains(ce))
					ce.getXML(copiedXML);
			}
		} catch (Exception e) {
			e.printStackTrace();
			copiedXML = new StringBuilder();
		}
		kernel.restoreCurrentUndoInfo();
		//afterSavingToXML(geoslocal, geostohide);

		// restore kernel settings
		kernel.setCoordStyle(oldCoordStlye);
		kernel.setCASPrintForm(oldPrintForm);
		kernel.setTranslateCommandName(oldValue);
	}

	/**
	 * In some situations, we may need to clear the clipboard
	 */
	public static void clearClipboard() {
		copiedXML = null;
		copiedXMLlabels = new ArrayList<String>();
	}

	/**
	 * This method pastes the content of the clipboard from XML
	 * into the construction
	 * 
	 * @param app
	 * @param cons
	 */
	public static void pasteFromXML(Application app) {

		if (copiedXML == null)
			return;

		if (copiedXML.length() == 0)
			return;

		app.getActiveEuclidianView().getEuclidianController().clearSelections();
		app.getActiveEuclidianView().getEuclidianController().setPastePreviewSelected();
		app.getGgbApi().evalXML(copiedXML.toString());

		Kernel kernel = app.getKernel();

		GeoElement geo;
		for (int i = 0; i < copiedXMLlabels.size(); i++) {
			String ll = copiedXMLlabels.get(i);
			geo = kernel.lookupLabel(ll);
			if (geo != null) {
				// TODO: handle EuclidianView2
				//geo.removeView(app.getEuclidianView());
				//geo.removeView(app.getEuclidianView2());
				//geo.addView(app.getActiveEuclidianView());

				geo.setLabel(geo.getDefaultLabel(false));

				app.addSelectedGeo(geo);
			}
		}
		app.getActiveEuclidianView().getEuclidianController().setPastePreviewSelected();
	}
}
