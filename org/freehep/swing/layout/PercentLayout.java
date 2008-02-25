package org.freehep.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * Lays out components within a Container such that each component takes a fixed percentage of the size.
 * 
 * Each Component added to the Container must have a Constraint object that specifies what proportion 
 * of the container it will fill. The Component will be stretched to fill exactly that percentage.
 * 
 * @see Constraint
 */
public class PercentLayout implements LayoutManager2
{
   private Hashtable hash = new Hashtable();

   public float getLayoutAlignmentX(Container p1)
   {
      return 0.5f;
   }

   public float getLayoutAlignmentY(Container p1)
   {
      return 0.5f;
   }

   public void addLayoutComponent(Component component, Object constraint)
   {
      if (constraint instanceof Constraint)
      {
         hash.put(component, constraint);
      }
      else
      {
         throw new IllegalArgumentException("Invalid constraint");
      }
   }

   public void addLayoutComponent(String constraint, Component comp)
   {
      throw new IllegalArgumentException("Invalid constraint");
   }

   public void invalidateLayout(Container p1)
   {
   }

   public void layoutContainer(Container p1)
   {
      Dimension size = p1.getSize();
      Enumeration keys = hash.keys();
      while (keys.hasMoreElements())
      {
         Component comp = (Component) keys.nextElement();
         Constraint constraint = (Constraint) hash.get(comp);
         int x = (int) (size.width * constraint.x / 100);
         int y = (int) (size.height * constraint.y / 100);
         int width = (int) (size.width * constraint.width / 100);
         int height = (int) (size.height * constraint.height / 100);
         comp.setBounds(x, y, width, height);
      }
   }

   public Dimension maximumLayoutSize(Container p1)
   {
      int maxx = Integer.MAX_VALUE;
      int maxy = Integer.MAX_VALUE;

      Enumeration keys = hash.keys();
      while (keys.hasMoreElements())
      {
         Component comp = (Component) keys.nextElement();
         Constraint constraint = (Constraint) hash.get(comp);
         Dimension max = comp.getMaximumSize();
         int mx = (max.width == Integer.MAX_VALUE)          ? max.width : (int) (max.width * 100 / constraint.width);
         int my = (max.height == Integer.MAX_VALUE)          ? max.height : (int) (max.height * 100 / constraint.height);
         if (mx < maxx)
         {
            maxx = mx;
         }
         if (my < maxy)
         {
            maxy = my;
         }
      }
      return new Dimension(maxx, maxy);
   }

   public Dimension minimumLayoutSize(Container p1)
   {
      int minx = 0;
      int miny = 0;

      Enumeration keys = hash.keys();
      while (keys.hasMoreElements())
      {
         Component comp = (Component) keys.nextElement();
         Constraint constraint = (Constraint) hash.get(comp);
         Dimension min = comp.getMinimumSize();
         int mx = (int) (min.width * 100 / constraint.width);
         int my = (int) (min.height * 100 / constraint.height);
         if (mx > minx)
         {
            minx = mx;
         }
         if (my > miny)
         {
            miny = my;
         }
      }
      return new Dimension(minx, miny);
   }

   public Dimension preferredLayoutSize(Container p1)
   {
      int prefx = 0;
      int prefy = 0;

      Enumeration keys = hash.keys();
      while (keys.hasMoreElements())
      {
         Component comp = (Component) keys.nextElement();
         Constraint constraint = (Constraint) hash.get(comp);
         Dimension pref = comp.getPreferredSize();
         prefx += ((pref.width * 100) / constraint.width);
         prefy += ((pref.height * 100) / constraint.height);
      }

      int n = hash.size();
      return new Dimension(prefx / n, prefy / n);
   }

   public void removeLayoutComponent(Component component)
   {
      hash.remove(component);
   }

   public Constraint getConstraintFor(Component component)
   {
      return (Constraint) hash.get(component);
   }
   
   public static class Constraint
   {
      double height;
      double width;
      double x;
      double y;

      /**
       * Creates a Constraint Object. 
       * @param x The X position of the top left corner of the component (0-100)
       * @param y The Y position of the top left corner of the component (0-100)
       * @param width The percentage width of the component (0-100)
       * @param height The percentage height of the component (0-100)
       */
      public Constraint(double x, double y, double width, double height)
      {
         setConstraints(x,y,width,height);
      }
      public void setConstraints(double x, double y, double width, double height)
      {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
      }
   }
}