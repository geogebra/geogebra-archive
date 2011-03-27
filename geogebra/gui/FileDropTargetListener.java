/*
 * GeoGebra - Dynamic Mathematics for Everyone 
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

import geogebra.main.Application;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class FileDropTargetListener implements DropTargetListener {

	private Application app;

	public FileDropTargetListener(Application app) {
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

		ArrayList<File> al = getGGBfiles(event);	
		
		if (al.size() == 0) {
			event.dropComplete(false);
		} else if (app.isSaved() || app.saveCurrentFile()) {				
			File [] files = new File[al.size()];
			for (int i = 0 ; i < al.size() ; i++)
				files[i] = al.get(i);
			app.getGuiManager().doOpenFiles(files, true);			
			event.dropComplete(true);			
		}			
	}
	
	private ArrayList<File> getGGBfiles(DropTargetDropEvent event) {
		Transferable transferable = event.getTransferable();
		DataFlavor[] flavors = event.getCurrentDataFlavors();
		
		ArrayList<File> al = new ArrayList<File>();
		

		for (int i = 0; i < flavors.length; i++) {
			DataFlavor dataFlavor = flavors[i];
			try {
				if (dataFlavor.equals(DataFlavor.javaFileListFlavor)) {
					java.util.List fileList = (java.util.List) transferable
							.getTransferData(dataFlavor);

					for (int j = 0 ; j < fileList.size() ; j++) {
						File droppedFile = (File) fileList.get(j);
						String lowerCase = droppedFile.getName().toLowerCase(Locale.US);
						if (lowerCase.endsWith(Application.FILE_EXT_GEOGEBRA)) {						
							//return droppedFile;
							al.add(droppedFile);
						} 
						else if (lowerCase.endsWith(Application.FILE_EXT_GEOGEBRA_TOOL)) {						
							//return droppedFile;
							al.add(droppedFile);
	
						}									
					}
				}			
			} catch (Exception e) {	
				e.printStackTrace();
			}
		}
		return al;
	}
}