/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

package geogebra.gui.util;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;

public class FileTransferable implements Transferable {
    DataFlavor[] dataFlavors = { DataFlavor.javaFileListFlavor};
    
    List files = new LinkedList();
 
    public FileTransferable(File file) {
    	files.add(file);
    }
    
    // Returns supported flavors
    public DataFlavor[] getTransferDataFlavors() {
        return dataFlavors;
    }
 
    // Returns true if flavor is supported
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return dataFlavors[0].equals(flavor);
    }
 
    // Returns file
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return files;
    }
    
}