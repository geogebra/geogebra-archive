/**
 * 
 */
package geogebra.spreadsheet;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import geogebra.Application;
import geogebra.kernel.GeoElement;
import javax.swing.JPanel;
import javax.swing.JTable;
/**
 * @author Amy Mathew Varkey
 *
 */
public class SpreadsheetController extends JPanel implements KeyListener
{
    private Application app;
    private SpreadsheetView view;
    private GeoElement geo;
    private SpreadsheetTableModel model;
    private GeoElement selectedGeoElement;
    int x1,y1,x2,y2;
   
     /**
     * 
     */
    public SpreadsheetController(Application app, SpreadsheetView view, SpreadsheetTableModel model){
        this.app=app;
        this.view=view;
        this.model=model;
        view.addKeyListener(this);
        this.y1= view.selectedColStart;
    	this.y2 = view.selectedColEnd;
    	this.x1 = view.selectedRowStart;
    	this.x2 = view.selectedRowEnd;
       
    }
  
    public GeoElement getSelectedGeoElement() {
		return selectedGeoElement;
	}

    /*To handle delete function*/
	public void keyPressed(KeyEvent event) {
		if (keyPressedConsumed(event))
			event.consume();	
	}
	
	public boolean keyPressedConsumed(KeyEvent event){
		
   		boolean consumed = false;
		int keyCode =event.getKeyCode();
		
		switch(keyCode){
			//Case to delete GeoElements from the table
			case KeyEvent.VK_DELETE:
				System.out.println("Coming to delete function");
				//Get the elements which are selected and delete them
				
				for(int i=x1; i<= x2; i++ )
				{
					for(int j=y1; j<= y2; j++)
					{
						
						GeoElement geo= (GeoElement)model.getValueAt(x1, y1);
						geo.remove();
					}
				}
				break;
		}
		return true;
	}

	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
    

}

