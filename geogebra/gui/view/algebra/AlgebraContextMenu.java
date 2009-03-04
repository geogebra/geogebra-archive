package geogebra.gui.view.algebra;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import geogebra.main.Application;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

/**
 * Context menu for the algebra view
 * 
 * This menu is displayed if the user right-clicked on an empty region
 * of the algebra view.
 * 
 * @author Florian Sonner
 */
public class AlgebraContextMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	private Application app;
	
	public AlgebraContextMenu(Application app){
		this.app = app;
		initItems();
	}
	
	/**
	 * Initialize the menu items.
	 */
	private void initItems() {
		// actions
		AbstractAction showAuxiliaryAction = new AbstractAction(app.getPlain("AuxiliaryObjects")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
			}
		};
		
		// title for menu
		JLabel title = new JLabel(app.getPlain("AlgebraWindow"));
        title.setFont(app.getBoldFont());                      
        title.setBackground(Color.white);
        title.setForeground(Color.black);
                
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 5));      
        add(title);
        addSeparator();
        
        title.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		setVisible(false);
        	}
        });
		
        // menu items
		JCheckBoxMenuItem cbShowAuxiliary = new JCheckBoxMenuItem(showAuxiliaryAction);
		cbShowAuxiliary.setSelected(app.showAuxiliaryObjects());
		
		add(cbShowAuxiliary);
	}
}
