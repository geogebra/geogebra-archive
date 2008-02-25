package org.freehep.swing.treetable;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/**
 * This example shows how to create a simple JTreeTable component,
 * by using a JTree as a renderer (and editor) for the cells in a
 * particular column in the JTable.
 *
 * @author Philip Milne
 * @author Scott Violet
 * @version $Id: JTreeTable.java,v 1.1 2008-02-25 21:18:33 murkle Exp $
 */
public class JTreeTable extends JTable
{
   /**
    * A subclass of JTree.
    */
   protected TreeTableCellRenderer tree;

   public JTreeTable()
   {
      this(null);
   }

   public JTreeTable(TreeTableModel treeTableModel)
   {
      super();
      if (treeTableModel != null)
      {
         setModel(treeTableModel);
      }
   }

   public void setModel(TreeTableModel treeTableModel)
   {
      // Create the tree. It will be used as a renderer and editor. 
      tree = new TreeTableCellRenderer(treeTableModel);


      // Install a tableModel representing the visible rows in the tree. 
      super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

      // Force the JTable and JTree to share their row selection models. 
      ListToTreeSelectionModelWrapper selectionWrapper = new ListToTreeSelectionModelWrapper();
      tree.setSelectionModel(selectionWrapper);
      setSelectionModel(selectionWrapper.getListSelectionModel());


      // Install the tree editor renderer and editor. 
      setDefaultRenderer(TreeTableModel.class, tree);


      // No grid.
      setShowGrid(false);


      // No intercell spacing
      setIntercellSpacing(new Dimension(0, 0));

      // And update the height of the trees row to match that of
      // the table.
      if (tree.getRowHeight() < 1)
      {
         // Metal looks better like this.
         setRowHeight(18);
      }
   }

   public Object getNodeForRow(int row)
   {
      TreeTableModelAdapter a = (TreeTableModelAdapter) getModel();
      return a.nodeForRow(row);
   }

   public int getRowForLocation(int x, int y)
   {
      TreeTableModelAdapter a = (TreeTableModelAdapter) getModel();
      return a.getRowForLocation(x, y);
   }

   /**
    * Overridden to pass the new rowHeight to the tree.
    */
   public void setRowHeight(int rowHeight)
   {
      super.setRowHeight(rowHeight);
      if ((tree != null) && (tree.getRowHeight() != rowHeight))
      {
         tree.setRowHeight(getRowHeight());
      }
   }

   /**
    * Returns the tree that is being shared between the model.
    */
   public JTree getTree()
   {
      return tree;
   }

   public TreePath getTreePathForRow(int row)
   {
      TreeTableModelAdapter a = (TreeTableModelAdapter) getModel();
      return a.pathForRow(row);
   }

   /**
    * Overriden to pass events on to the tree if the editor
    * does not want to start editing.
    */
   public boolean editCellAt(int row, int column, EventObject e)
   {
      if (!super.editCellAt(row, column, e))
      {
         if (e instanceof MouseEvent)
         {
            for (int counter = getColumnCount() - 1; counter >= 0; counter--)
            {
               if (getColumnClass(counter) == TreeTableModel.class)
               {
                  MouseEvent me = (MouseEvent) e;
                  MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - getCellRect(0, counter, true).x, me.getY(), me.getClickCount(), me.isPopupTrigger());
                  tree.dispatchEvent(newME);
                  break;
               }
            }
         }
         return false;
      }
      return true;
   }

   /**
    * Overridden to message super and forward the method to the tree.
    * Since the tree is not actually in the component hieachy it will
    * never receive this unless we forward it in this manner.
    */
   public void updateUI()
   {
      super.updateUI();
      if (tree != null)
      {
         tree.updateUI();
      }


      // Use the tree's default foreground and background colors in the
      // table. 
      LookAndFeel.installColorsAndFont(this, "Tree.background", "Tree.foreground", "Tree.font");
   }

   /**
    * A TreeCellRenderer that displays a JTree.
    */
   public class TreeTableCellRenderer extends JTree implements TableCellRenderer
   {
      /**
       * Last table/tree row asked to renderer.
       */
      protected int visibleRow;

      public TreeTableCellRenderer(TreeModel model)
      {
         super(model);
      }

      /**
       * This is overridden to set the height to match that of the JTable.
       */
      public void setBounds(int x, int y, int w, int h)
      {
         super.setBounds(x, 0, w, JTreeTable.this.getHeight());
      }

      /**
       * Sets the row height of the tree, and forwards the row height to
       * the table.
       */
      public void setRowHeight(int rowHeight)
      {
         if (rowHeight > 0)
         {
            super.setRowHeight(rowHeight);
            if ((JTreeTable.this != null) && (JTreeTable.this.getRowHeight() != rowHeight))
            {
               JTreeTable.this.setRowHeight(getRowHeight());
            }
         }
      }

      /**
       * TreeCellRenderer method. Overridden to update the visible row.
       */
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
      {
         if (isSelected)
         {
            setBackground(table.getSelectionBackground());
         }
         else
         {
            setBackground(table.getBackground());
         }

         visibleRow = row;
         return this;
      }

      /**
       * Sublcassed to translate the graphics such that the last visible
       * row will be drawn at 0,0.
       */
      public void paint(Graphics g)
      {
         g.translate(0, -visibleRow * getRowHeight());
         super.paint(g);
      }

      /**
       * updateUI is overridden to set the colors of the Tree's renderer
       * to match that of the table.
       */
      public void updateUI()
      {
         super.updateUI();

         // Make the tree's cell renderer use the table's cell selection
         // colors. 
         TreeCellRenderer tcr = getCellRenderer();
         if (tcr instanceof DefaultTreeCellRenderer)
         {
            DefaultTreeCellRenderer dtcr = (DefaultTreeCellRenderer) tcr;


            // For 1.1 uncomment this, 1.2 has a bug that will cause an
            // exception to be thrown if the border selection color is
            // null.
            // dtcr.setBorderSelectionColor(null);
            dtcr.setTextSelectionColor(UIManager.getColor("Table.selectionForeground"));
            dtcr.setBackgroundSelectionColor(UIManager.getColor("Table.selectionBackground"));
         }
      }
   }

   /**
    * ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
    * to listen for changes in the ListSelectionModel it maintains. Once
    * a change in the ListSelectionModel happens, the paths are updated
    * in the DefaultTreeSelectionModel.
    */
   class ListToTreeSelectionModelWrapper extends DefaultTreeSelectionModel
   {
      /**
       * Set to true when we are updating the ListSelectionModel.
       */
      protected boolean updatingListSelectionModel;

      public ListToTreeSelectionModelWrapper()
      {
         super();
         getListSelectionModel().addListSelectionListener(createListSelectionListener());
      }

      /**
       * This is overridden to set <code>updatingListSelectionModel</code>
       * and message super. This is the only place DefaultTreeSelectionModel
       * alters the ListSelectionModel.
       */
      public void resetRowSelection()
      {
         if (!updatingListSelectionModel)
         {
            updatingListSelectionModel = true;
            try
            {
               super.resetRowSelection();
            }
            finally
            {
               updatingListSelectionModel = false;
            }
         }

         // Notice how we don't message super if
         // updatingListSelectionModel is true. If
         // updatingListSelectionModel is true, it implies the
         // ListSelectionModel has already been updated and the
         // paths are the only thing that needs to be updated.
      }

      /**
       * Creates and returns an instance of ListSelectionHandler.
       */
      protected ListSelectionListener createListSelectionListener()
      {
         return new ListSelectionHandler();
      }

      /**
       * If <code>updatingListSelectionModel</code> is false, this will
       * reset the selected paths from the selected rows in the list
       * selection model.
       */
      protected void updateSelectedPathsFromSelectedRows()
      {
         if (!updatingListSelectionModel)
         {
            updatingListSelectionModel = true;
            try
            {
               // This is way expensive, ListSelectionModel needs an
               // enumerator for iterating.
               int min = listSelectionModel.getMinSelectionIndex();
               int max = listSelectionModel.getMaxSelectionIndex();

               clearSelection();
               if ((min != -1) && (max != -1))
               {
                  for (int counter = min; counter <= max; counter++)
                  {
                     if (listSelectionModel.isSelectedIndex(counter))
                     {
                        TreePath selPath = tree.getPathForRow(counter);

                        if (selPath != null)
                        {
                           addSelectionPath(selPath);
                        }
                     }
                  }
               }
            }
            finally
            {
               updatingListSelectionModel = false;
            }
         }
      }

      /**
       * Returns the list selection model. ListToTreeSelectionModelWrapper
       * listens for changes to this model and updates the selected paths
       * accordingly.
       */
      ListSelectionModel getListSelectionModel()
      {
         return listSelectionModel;
      }

      /**
       * Class responsible for calling updateSelectedPathsFromSelectedRows
       * when the selection of the list changse.
       */
      class ListSelectionHandler implements ListSelectionListener
      {
         public void valueChanged(ListSelectionEvent e)
         {
            updateSelectedPathsFromSelectedRows();
         }
      }
   }
}