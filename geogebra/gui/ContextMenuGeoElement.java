/* 
GeoGebra - Dynamic Mathematics for Schools
Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/


package geogebra.gui;

import geogebra.Application;
import geogebra.kernel.GeoConic;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoLine;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.GeoSegment;
import geogebra.kernel.GeoText;
import geogebra.kernel.GeoVector;
import geogebra.kernel.Kernel;
import geogebra.kernel.Traceable;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Context menu for GeoElement objects.
 * @author  Markus Hohenwarter
 * @version 
 */
public class ContextMenuGeoElement extends JPopupMenu {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static Color bgColor = Color.white;
    final static Color fgColor = Color.black;

    private GeoElement geo;
    private GeoPoint point;
    private GeoLine line;
    private GeoVector vector;
    private GeoConic conic;
    //private GeoNumeric numeric;
    //private Point location;
    Application app;

    ContextMenuGeoElement(Application app) {
        this.app = app;     
        setBackground(bgColor);
    }

    /** Creates new MyPopupMenu for GeoElement*/
    public ContextMenuGeoElement(Application app, GeoElement geo, Point location) {
        this(app);
        this.geo = geo;
        //this.location = location;                               
                    
        String title = geo.getLongDescriptionHTML(false, true);
        if (title.length() > 80)
        	title = geo.getNameDescriptionHTML(false, true);
        setTitle(title);
        
        if (app.showAlgebraView()) {
	        addPointItems();
	        addLineItems();
	        addVectorItems();
	        addConicItems();
	        addNumberItems();	       
        }

        if (getComponentCount() > 2)
            addSeparator();
        addForAllItems();
    }

    private void addPointItems() {
        if (!(geo instanceof GeoPoint))
            return;
        point = (GeoPoint) geo;
        int mode = point.getMode();
        AbstractAction action;

        if (mode != Kernel.COORD_CARTESIAN) {
            action = new AbstractAction(app.getPlain("CartesianCoords")) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    point.setMode(Kernel.COORD_CARTESIAN);
                    point.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }

        if (mode != Kernel.COORD_POLAR) {
            action = new AbstractAction(app.getPlain("PolarCoords")) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    point.setMode(Kernel.COORD_POLAR);
                    point.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }
        
        if (mode != Kernel.COORD_COMPLEX) {
            action = new AbstractAction(app.getPlain("ComplexNumber")) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    point.setMode(Kernel.COORD_COMPLEX);
                    point.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }
    }

    private void addLineItems() {
        if (!(geo instanceof GeoLine))
            return;
        if (geo instanceof GeoSegment)
            return;        
        
        line = (GeoLine) geo;
        int mode = line.getMode();
        AbstractAction action;
        StringBuffer sb = new StringBuffer();

        if (mode != GeoLine.EQUATION_IMPLICIT) {
			sb.setLength(0);
            sb.append(app.getPlain("Equation"));
            sb.append(' ');
            sb.append(app.getPlain("ImplicitLineEquation"));
            action = new AbstractAction(sb.toString()) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    line.setMode(GeoLine.EQUATION_IMPLICIT);
                    line.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }

        if (mode != GeoLine.EQUATION_EXPLICIT) {
			sb.setLength(0);
            sb.append(app.getPlain("Equation"));
            sb.append(' ');
            sb.append(app.getPlain("ExplicitLineEquation"));
            action = new AbstractAction(sb.toString()) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    line.setMode(GeoLine.EQUATION_EXPLICIT);
                    line.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }

        if (mode != GeoLine.PARAMETRIC) {
            action = new AbstractAction(app.getPlain("ParametricForm")) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    line.setMode(GeoLine.PARAMETRIC);
                    line.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }

    }

    private void addVectorItems() {
        if (!(geo instanceof GeoVector))
            return;
        vector = (GeoVector) geo;
        int mode = vector.getMode();
        AbstractAction action;

        if (mode != Kernel.COORD_CARTESIAN) {
            action = new AbstractAction(app.getPlain("CartesianCoords")) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                	vector.setMode(Kernel.COORD_CARTESIAN);
                	vector.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }

        if (mode != Kernel.COORD_POLAR) {
            action = new AbstractAction(app.getPlain("PolarCoords")) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                	vector.setMode(Kernel.COORD_POLAR);
                	vector.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }
    }

    private void addConicItems() {
        if (geo.getClass() != GeoConic.class)
            return;
        conic = (GeoConic) geo;

        // there's no need to show implicit equation
        // if you can't select the specific equation
        boolean specificPossible = conic.isSpecificPossible();
        boolean explicitPossible = conic.isExplicitPossible();
        if (!(specificPossible || explicitPossible))
            return;

        int mode = conic.getToStringMode();
        AbstractAction action;
        StringBuffer sb = new StringBuffer();

        if (mode != GeoConic.EQUATION_IMPLICIT) {
            sb.append(app.getPlain("Equation"));
            sb.append(' ');
            sb.append(app.getPlain("ImplicitConicEquation"));
            action = new AbstractAction(sb.toString()) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    conic.setToImplicit();
                    conic.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }

        if (specificPossible && mode != GeoConic.EQUATION_SPECIFIC) {
            // specific conic string
            String conicEqn = conic.getSpecificEquation();
            if (conicEqn != null) {
                sb.setLength(0);
                sb.append(app.getPlain("Equation"));
                sb.append(' ');
                sb.append(conicEqn);
                action = new AbstractAction(sb.toString()) {
                    /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
                        conic.setToSpecific();
                        conic.updateRepaint();
                        app.storeUndoInfo();
                    }
                };
                addAction(action);
            }
        }

        if (explicitPossible && mode != GeoConic.EQUATION_EXPLICIT) {
            sb.setLength(0);
            sb.append(app.getPlain("Equation"));
            sb.append(' ');
            sb.append(app.getPlain("ExplicitConicEquation"));
            action = new AbstractAction(sb.toString()) {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    conic.setToExplicit();
                    conic.updateRepaint();
                    app.storeUndoInfo();
                }
            };
            addAction(action);
        }
    }

    private void addNumberItems() {
    }
    
    private void addTextItems() {
    	if (geo.isGeoText()) {
    		final GeoText geoText = (GeoText) geo;
    		 // show object
        	JCheckBoxMenuItem cbItem = new JCheckBoxMenuItem(app.getPlain("AbsoluteScreenLocation"));
            cbItem.setSelected(geoText.isAbsoluteScreenLocActive());
            cbItem.addActionListener(new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
        			boolean flag = !geoText.isAbsoluteScreenLocActive();
        			if (flag) {
						// convert real world to screen coords
						int x = app.getEuclidianView().toScreenCoordX(geoText.getRealWorldLocX());
						int y = app.getEuclidianView().toScreenCoordY(geoText.getRealWorldLocY());
						geoText.setAbsoluteScreenLoc(x, y);							
					} else {
						// convert screen coords to real world 
						double x = app.getEuclidianView().toRealWorldCoordX(geoText.getAbsoluteScreenLocX());
						double y = app.getEuclidianView().toRealWorldCoordY(geoText.getAbsoluteScreenLocY());
						geoText.setRealWorldLoc(x, y);
					}
        			geoText.setAbsoluteScreenLocActive(flag);            		
        			geoText.updateRepaint();
                    app.storeUndoInfo();
                }        	
        	});
            addItem(cbItem);     
    	}
    }

    private void addForAllItems() {
        // SHOW, HIDE
        if (geo.isDrawable()) { 
        	 JCheckBoxMenuItem cbItem;
            
            // show object
        	if (geo.getShowObjectCondition() == null) {
	            cbItem = new JCheckBoxMenuItem( app.getPlain("ShowObject"));
	            cbItem.setIcon(app.getImageIcon("mode_showhideobject_16.gif"));
	            cbItem.setSelected(geo.isSetEuclidianVisible());
	            cbItem.addActionListener(new ActionListener() {
	        		public void actionPerformed(ActionEvent e) {
	                    geo.setEuclidianVisible(!geo.isSetEuclidianVisible());
	                    geo.updateRepaint();
	                    app.storeUndoInfo();
	                }        	
	        	});
	            addItem(cbItem);
        	}
            
            if (geo.isLabelShowable()) {           
	            // show label
	            cbItem = new JCheckBoxMenuItem( app.getPlain("ShowLabel"));	           
	            cbItem.setSelected(geo.isLabelVisible());
	            cbItem.setIcon(app.getImageIcon("mode_showhidelabel_16.gif"));
	            cbItem.addActionListener(new ActionListener() {
	        		public void actionPerformed(ActionEvent e) {
	                    geo.setLabelVisible(!geo.isLabelVisible());
	                    geo.updateRepaint();
	                    app.storeUndoInfo();
	                }        	
	        	});
	            addItem(cbItem);	        		            	            	            
            }     
            
            //  trace
            if (geo.isTraceable()) {            	
                cbItem = new JCheckBoxMenuItem( app.getPlain("TraceOn"));
                cbItem.setIcon(app.getImageIcon("trace_on.gif"));
                cbItem.setSelected(((Traceable) geo).getTrace());
                cbItem.addActionListener(new ActionListener() {
                	public void actionPerformed(ActionEvent e) {
                        ((Traceable) geo).setTrace(!((Traceable) geo).getTrace());
                        geo.updateRepaint();
                        app.storeUndoInfo();
                    }       	
            	});
                addItem(cbItem);            	
            }  
            
            // AUXILIARY OBJECT
            
            if (app.showAlgebraView() &&
            		geo.isAlgebraShowable()) {
                
                // show object
            	cbItem = new JCheckBoxMenuItem( app.getPlain("AuxiliaryObject"));
                cbItem.setIcon(app.getImageIcon("aux_folder.gif"));
            	cbItem.setSelected(geo.isAuxiliaryObject());
                cbItem.addActionListener(new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
                        geo.setAuxiliaryObject(!geo.isAuxiliaryObject());
                        geo.updateRepaint();
                        app.storeUndoInfo();
                    }        	
            	});
                addItem(cbItem);                      
            }                                                           
            
            //  fix object
            if (geo.isFixable() && (geo.isGeoText() || geo.isGeoImage())) {            	
                cbItem = new JCheckBoxMenuItem( app.getPlain("FixObject"));
                cbItem.setIcon(app.getEmptyIcon());
                cbItem.setSelected(geo.isFixed());
                cbItem.addActionListener(new ActionListener() {
                	public void actionPerformed(ActionEvent e) {
                        geo.setFixed(!geo.isFixed());
                        geo.updateRepaint();
                        app.storeUndoInfo();
                    }       	
            	});
                addItem(cbItem);            	
            }  
            
            // text position
            if (geo.isGeoText()) {
            	addTextItems();
            }
            
        	addSeparator();
        }
        
      
        
          
      
        /*
        // EDIT in AlgebraView
        else if (app.showAlgebraView() && geo.isChangeable() && !geo.isGeoImage()) { 
            addAction(new AbstractAction(
                app.getPlain("Edit"),
                app.getImageIcon("edit.png")) {
					private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    app.startEditing(geo);
                }
            });
        }             
        
        
        
        // EDIT: copy to input field       
        if (app.showAlgebraInput() && !geo.isGeoImage()) {
            addAction(new AbstractAction(
                app.getMenu("InputField"),
                app.getImageIcon("edit.png")) {

					private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {                    
                    AlgebraInput ai = app.getAlgebraInput();
                    if (ai != null) {
                    	ai.clear();                  
                    	ai.insertString(geo.toString());
                    	ai.setFocus();
                    }
                }
            });
        }
        */
        
        // Rename      
        if (app.letRename() && geo.isRenameable())  {    
            addAction(new AbstractAction(
                    app.getPlain("Rename"),
                    app.getImageIcon("rename.png")) {
						private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
                        app.showRenameDialog(geo, true, geo.getLabel(), true);
                    }
                });
        }
    
       
        // EDITING      
        // EDIT Text in special dialog
        if (geo.isTextValue() && !geo.isFixed()) {
            addAction(new AbstractAction(
                app.getPlain("Edit"),
                app.getImageIcon("edit.png")) {
					private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    app.showTextDialog((GeoText) geo); 
                }
            });
        }      
        
        // REDEFINE    
        else if (app.letRedefine() && geo.isRedefineable()) {     
                addAction(new AbstractAction(
                            app.getPlain("Redefine"),
                            app.getImageIcon("edit.png")) {

								private static final long serialVersionUID = 1L;

							public void actionPerformed(ActionEvent e) {
                                app.showRedefineDialog(geo);
                            }
                        });         
        }
        
                 
        
        
        // DELETE    
        if (app.letDelete()) {  
            addAction(new AbstractAction(
                app.getPlain("Delete"),
                app.getImageIcon("delete_small.gif")) {
                /**
					 * 
					 */
					private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                    geo.remove();
                    app.storeUndoInfo();
                }
            });       
        }
        
     

        if (app.letShowPropertiesDialog() && geo.hasProperties()) {
            addSeparator();

            // open properties dialog      
            addAction(new AbstractAction(
                app.getPlain("Properties") + " ...",
                app.getImageIcon("document-properties.png")) {
					private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
                	tempArrayList.clear();
                	tempArrayList.add(geo);
                    app.showPropertiesDialog(tempArrayList);
                }
            });
        }
    }
    private ArrayList tempArrayList = new ArrayList();

    void addAction(Action ac) {
        JMenuItem mi = this.add(ac);
        mi.setBackground(bgColor);              
    }
    
    void addItem(JMenuItem mi) {        
        mi.setBackground(bgColor);
        add(mi);
    }

    void setTitle(String str) {
    	JLabel title = new JLabel(str);
        title.setFont(app.getBoldFont());                      
        title.setBackground(bgColor);
        title.setForeground(fgColor);          
                
        title.setBorder(BorderFactory.createEmptyBorder(5, 10, 2, 5));      
        add(title);
        addSeparator();   
        
        title.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		setVisible(false);
        	}
        });
        
    }
        
}
