package geogebra.cas.view;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class EditorFocusListener implements FocusListener{

	private CASTableCellEditor editor;
	
	public EditorFocusListener(CASTableCellEditor inEditor){
		this.editor = inEditor;
	}
	
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("Focus Gained ");
		editor.setInputAreaFocused();
	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		//System.out.println("Focus Lost " + editor.getRow());
//		if ()
		editor.stopCellEditing();		
	}

}
