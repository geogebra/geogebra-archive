package geogebra.cas.view;

import javax.swing.table.DefaultTableCellRenderer;

public class CASTableRenderer extends DefaultTableCellRenderer {
	public CASTableRenderer()
	{
		super();
	}
	
	public void setValue(Object value)
	{
		if (value != null)
		{
			setText((String) value);
		} else {
			setText("");
		}
	}
}
