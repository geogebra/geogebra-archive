package geogebra.gui.toolbar;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FontMetrics;
import java.awt.SystemColor;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.BreakIterator;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import geogebra.gui.MySmallJButton;
import geogebra.main.Application;
import geogebra.util.Util;

/**
 * Container for one or multiple toolbars. Takes care of fundamental things such
 * as the help text.
 * 
 * @author Florian Sonner
 */
public class ToolbarContainer extends JPanel implements ComponentListener {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Show help text at the right.
	 */
	private static boolean showHelp = true;
	
	/**
	 * Application instance.
	 */
	private Application app;
	
	/**
	 * True if this is the main toolbar which also contains the undo buttons.
	 */
	private boolean isMain; 
	
	/**
	 * Help panel.
	 */
	private JPanel toolbarHelpPanel;
	
   	/**
   	 * Lable in the help panel showing the current mode name.
   	 */
	private JLabel modeNameLabel;
	
	/**
	 * Panel which contains all toolbars.
	 */
	private JPanel toolbarPanel;
	
	/**
	 * Toolbars added to this container.
	 */
	private ArrayList<Toolbar> toolbars;
	
	/**
	 * The active toolbar.
	 */
	private int activeToolbar = -1;
	
	/**
	 * Create a new toolbar container.
	 * 
	 * @param app
	 * @param isMain If this container is used in the main panel, where additional
	 * 		functions are added to the toolbar (undo buttons)
	 */
	public ToolbarContainer(Application app, boolean isMain) {
		super(new BorderLayout(10, 0));
		
		this.app = app;
		this.isMain = isMain;
		
		// add general toolbar
		toolbars = new ArrayList<Toolbar>(1);
		
		if(isMain) {
			addToolbar(new Toolbar(app));
		}
		
        // if the container is resized we have to check if the 
		// help text still has enough space.
		addComponentListener(this);
	}
	
	/**
	 * Build the toolbar container GUI.
	 */
	public void buildGui() {
		removeAll();

		// add visible top border in main toolbar container
		if(isMain) {
	        setBorder(BorderFactory.createCompoundBorder(
	        	BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow),
	        	BorderFactory.createEmptyBorder(2, 2, 1, 2)));
		} else {
			setBorder(BorderFactory.createEmptyBorder(2, 2, 1, 2));
		}
		
		toolbarPanel = new JPanel(new CardLayout());
		updateToolbarPanel();
		
        // wrap toolbar to be vertically centered
        JPanel gluePanel = new JPanel();
        gluePanel.setLayout(new BoxLayout(gluePanel, BoxLayout.Y_AXIS));
        gluePanel.add(Box.createVerticalGlue());
        gluePanel.add(toolbarPanel);
        gluePanel.add(Box.createVerticalGlue());
        add(gluePanel, BorderLayout.WEST);
		
        // UNDO Toolbar     
        if (isMain && app.isUndoActive()) {
           	JPanel undoPanel = new JPanel();
           	
	        if (app.getMaxIconSize() >= 32) {
	        	undoPanel.setLayout(new BoxLayout(undoPanel, BoxLayout.Y_AXIS));
	        } else {
	        	undoPanel.setLayout(new BoxLayout(undoPanel, BoxLayout.X_AXIS));
	        }
	        undoPanel.add(Box.createVerticalGlue());
	        
	        // undo button
	        MySmallJButton button = new MySmallJButton(app.getGuiManager().getUndoAction(), 7); 	
	        String text = app.getMenu("Undo");
	        button.setText(null);
	        button.setToolTipText(text);  
	        button.setAlignmentX(RIGHT_ALIGNMENT);
	        undoPanel.add(button);

	        // redo button
	        button = new MySmallJButton(app.getGuiManager().getRedoAction(), 7);         	        
	        text = app.getMenu("Redo");
	        button.setText(null);
	        button.setToolTipText(text);        
	        button.setAlignmentX(RIGHT_ALIGNMENT);
	        undoPanel.add(button); 
	       
	        undoPanel.add(Box.createVerticalGlue());

	        add(undoPanel, BorderLayout.EAST);
        }
		
       	if (showHelp) {
       		// mode label       		
           	modeNameLabel = new JLabel();
           	modeNameLabel.setAlignmentX(LEFT_ALIGNMENT);

           	// put into panel to 
           	toolbarHelpPanel = new JPanel();
           	toolbarHelpPanel.setLayout(new BoxLayout(toolbarHelpPanel, BoxLayout.Y_AXIS));
           	toolbarHelpPanel.add(Box.createVerticalGlue());
           	toolbarHelpPanel.add(modeNameLabel);
           	toolbarHelpPanel.add(Box.createVerticalGlue());
           	
        	add(toolbarHelpPanel, BorderLayout.CENTER);
       	}
        
        invalidate();
	}
	
	/**
	 * Select a mode.
	 * 
	 * @param mode
	 */
	public void setMode(int mode) {
		for(Toolbar toolbar : toolbars) {
			toolbar.setMode(mode);
		}
		
		updateHelpText();
	}     
    
    /**
     * Marks the passed toolbar as active and makes it visible.
     * 
     * @param toolbar
     */
    public void setActiveToolbar(Toolbar toolbar) {
    	setActiveToolbar(getViewId(toolbar));
    }
    
    /**
     * Marks the toolbar with the passed id as active and makes it visible.
     * 
     * @param id The view ID 
     */
    public void setActiveToolbar(int id) {
    	activeToolbar = id;
    	((CardLayout)toolbarPanel.getLayout()).show(toolbarPanel, Integer.toString(id));
    }
	
	/**
	 * Update toolbars.
	 */
	public void updateToolbarPanel() {
		toolbarPanel.removeAll();
		
		for(Toolbar toolbar : toolbars) {
			toolbar.buildGui();
			toolbarPanel.add(toolbar, Integer.toString(getViewId(toolbar)));
		}
	}
	
	/**
	 * Adds a toolbar to this container. Use updateToolbarPanel() to update the GUI after 
	 * all toolbar changes were made. 
	 * 
	 * @param toolbar
	 */
	public void addToolbar(Toolbar toolbar) {
		toolbars.add(toolbar);
	}
	
	/**
	 * Removes a toolbar from this container. Use updateToolbarPanel() to update the GUI
	 * after all toolbar changes were made.
	 * 
	 * @param toolbar
	 */
	public void removeToolbar(Toolbar toolbar) {
		toolbars.remove(toolbar);
		
		if(getViewId(toolbar) == activeToolbar) {
			setActiveToolbar(-1);
		}
	}
	
	/**
	 * @param toolbar
	 * @return The ID of the dock panel associated with the passed toolbar or -1
	 */
	private int getViewId(Toolbar toolbar) {
		return (toolbar.getDockPanel() != null ? toolbar.getDockPanel().getViewId() : -1);
	}
	
	/**
	 * Old width of this container.
	 */
	private int oldWidth;
	
	/**
	 * Check if we still can display a help text.
	 */
	public void componentResized(ComponentEvent e) {
		if (getWidth() != oldWidth) {
			oldWidth = getWidth();
			
			// update help text if we show one
			if(ToolbarContainer.showHelp) {
				updateHelpText();
			}
		}
	}

	/**
	 * Update the help text.
	 */
    private void updateHelpText() {
    	if (modeNameLabel == null) return;
    	
    	int mode = app.getMode();
    	
    	String toolName = app.getToolName(mode);
    	String helpText = app.getToolHelp(mode);
    	
    	// get wrapped toolbar help text
        String wrappedText = wrappedModeText(toolName, helpText, toolbarHelpPanel);    	
    	modeNameLabel.setText(wrappedText);
    	
    	// tooltip
    	modeNameLabel.setToolTipText(app.getToolTooltipHTML(mode));
    	toolbarHelpPanel.validate();
    }
    
    /** 
     * Returns mode text and toolbar help as html text with line breaks
     * to fit in the given panel.     
     */
    private String wrappedModeText(String modeName, String helpText, JPanel panel) {
    	FontMetrics fm = getFontMetrics(app.getBoldFont());    	
   
    	// check width of panel
    	int panelWidth = panel.getWidth();
    	int charWidth = fm.stringWidth("W");    	
    	panelWidth = panelWidth - charWidth; // needed for correct line breaks
    	
    	if (panelWidth <= 0) {    	
    		return "";
    	} 
    	
    	// show no more than 2 lines
     	int maxLines = 2*fm.getHeight() < panel.getHeight() ? 2 : 1; 
     	//Math.min(2, Math.round(panel.getHeight() / (float) fm.getHeight()));    	
    	StringBuilder sbToolName = new StringBuilder();    
    	sbToolName.append("<html><b>");
    	
    	// check if mode name itself fits
    	if (fm.stringWidth(modeName) >  panelWidth)
    		return "";
    	
    	// mode name
    	BreakIterator iterator = BreakIterator.getWordInstance(app.getLocale());
		iterator.setText(modeName);
		int start = iterator.first();
		int end = iterator.next();
		int line = 1;
		
		int len = 0;
		while (end != BreakIterator.DONE)
		{
			String word = modeName.substring(start,end);
			if( len + fm.stringWidth(word) > panelWidth )
			{
				if (++line > maxLines) {
					// if the tool name doesn't fit: return an empty string
					return "";
					
					//sbToolName.append("...");
					//sbToolName.append("</b></html>");
					//return sbToolName.toString();
				}
				sbToolName.append("<br>");
				len = fm.stringWidth(word);	
			}
			else
			{
				len += fm.stringWidth(word);
			}
 
			sbToolName.append(Util.toHTMLString(word));
			start = end;
			end = iterator.next();
		}		
		sbToolName.append("</b>");
    	
		
		// mode help text
		StringBuilder sbToolHelp = new StringBuilder();   
		fm = getFontMetrics(app.getPlainFont());
		
		// try to put help text into single line
		if (line < maxLines && fm.stringWidth(helpText) < panelWidth) {
			++line;
			sbToolHelp.append("<br>");
			sbToolHelp.append(Util.toHTMLString(helpText));
		}
		else {			
			sbToolHelp.append(": ");
			iterator.setText(helpText);
			start = iterator.first();
			end = iterator.next();
			while (end != BreakIterator.DONE)
			{
				String word = helpText.substring(start,end);
				if( len + fm.stringWidth(word) >  panelWidth)
				{
					if (++line > maxLines) {						
						// show tool help only when it can be completely shown
						sbToolHelp.setLength(0);
						//sbToolHelp.append("...");
						break;
					}
					sbToolHelp.append("<br>");
					len = fm.stringWidth(word);								
				}
				else
				{
					len += fm.stringWidth(word);
				}
	 
				sbToolHelp.append(Util.toHTMLString(word));
				start = end;
				end = iterator.next();
			}
		}
		
		// show tool help only when it can be completely shown		
		sbToolName.append(sbToolHelp);
		sbToolName.append("</html>");
		return sbToolName.toString();
	}
    
    /**
     * @return The first toolbar in our list, used for the general toolbar in the main
     * toolbar container.
     */
    public Toolbar getFirstToolbar() {
    	if(toolbars.size() > 0) {
    		return toolbars.get(0);
    	} else {
    		return null;
    	}
    }
	
	/**
	 * @return If the help text is displayed.
	 */
	public static boolean showHelp() {
		return showHelp;
	}
	
	/**
	 * @param showHelp
	 */
	public static void setShowHelp(boolean showHelp) {
		ToolbarContainer.showHelp = showHelp; 
	}
    
    // Component listener methods
	public void componentShown(ComponentEvent e) { }

	public void componentHidden(ComponentEvent e) { }

	public void componentMoved(ComponentEvent e) { }
}
