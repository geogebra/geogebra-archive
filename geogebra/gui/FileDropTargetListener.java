/*
 * GeoGebra - Dynamic Mathematics for Schools Copyright Markus Hohenwarter and GeoGebra Inc., 
 * http://www.geogebra.org
 * 
 * This file is part of GeoGebra.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 */

package geogebra.gui;

import geogebra.GeoGebraApplicationBase;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;

public class FileDropTargetListener implements DropTargetListener {

	private GeoGebraApplicationBase app;

	public FileDropTargetListener(GeoGebraApplicationBase app) {
		this.app = app;
	}

	public void dragEnter(DropTargetDragEvent event) {
	}

	public void dragExit(DropTargetEvent event) {
	}

	public void dragOver(DropTargetDragEvent event) {
		// provide visual feedback
		event.acceptDrag(DnDConstants.ACTION_COPY);
	}

	public void dropActionChanged(DropTargetDragEvent event) {
	}

	public void drop(DropTargetDropEvent event) {
		if ((event.getSourceActions() & DnDConstants.ACTION_COPY) != 0)
			event.acceptDrop(DnDConstants.ACTION_COPY);
		else {
			event.rejectDrop();
			return;
		}

		File droppedFile = getGGBfile(event);		
		if (droppedFile == null) {
			event.dropComplete(false);
		} else if (app.isSaved() || app.saveCurrentFile()) {				
			File [] files = { droppedFile };
			app.doOpenFiles(files, true);			
			event.dropComplete(true);			
		}			
	}
	
	private File getGGBfile(DropTargetDropEvent event) {
		Transferable transferable = event.getTransferable();
		DataFlavor[] flavors = event.getCurrentDataFlavors();

		for (int i = 0; i < flavors.length; i++) {
			DataFlavor dataFlavor = flavors[i];
			try {
				if (dataFlavor.equals(DataFlavor.javaFileListFlavor)) {
					java.util.List fileList = (java.util.List) transferable
							.getTransferData(dataFlavor);
					File droppedFile = (File) fileList.get(0);
					String lowerCase = droppedFile.getName().toLowerCase();
					if (lowerCase.endsWith(GeoGebraApplicationBase.FILE_EXT_GEOGEBRA)) {						
						return droppedFile;
					} 
					else if (lowerCase.endsWith(GeoGebraApplicationBase.FILE_EXT_GEOGEBRA_TOOL)) {						
						return droppedFile;
					}											
				}			
			} catch (Exception e) {							
			}
		}
		return null;
	}
}