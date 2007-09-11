package jspreadsheet;

import java.util.LinkedList;


/**
 * Node is basic unit for Formula processing.
 * <p>
 * It can be one of the following:
 * <ol>
 * <li>Relative Address (LETTERS+numbers: A1)</li>
 * <li>Absolute Address ($LETTERS$numbers: $A$1)</li>
 * <li>Function (LETTERS)</li>
 * <li>Left Parenthese</li>
 * <li>Right Parenthese</li>
 * <li>Number (a float number)</li>
 * <li>Operator (+ - * / % ^)</li>
 * <li>Comma (separating parameters)</li>
 * <li>Colon (used in range addresses: A1:C6)</li>
 * </ol>
 *
 * @see Formula
 *
 * @author Hua Zhong <huaz@cs.columbia.edu>
 * @version $Revision: 1.1 $
 */
class Node
{
   public static final int DEFAULT = 0; // reserved
   public static final int REL_ADDR = 1; // LETTERS+numbers: A1
   public static final int ABS_ADDR = 2; // $LETTERS$numbers: $A$1
   public static final int FUNCTION = 3; // LETTERS: SUM
   public static final int LPAREN = 4; // (
   public static final int RPAREN = 5; // )
   public static final int NUMBER = 6; // all numbers and has .: 0.5
   public static final int OPERATOR = 7; // + - * / ^
   public static final int COMMA = 8; // ,
   public static final int COLON = 9; // :
   public static final int EXP = 10; // an expression.  the exp field counts.

   // for each function param, its type is EXP
   private static final String[] desc = 
   {
      "Default", "Rel_Addr", "Abs_Addr", "Function", "LBracket", "RBracket",
      "Number", "Operator", "Comma", "Colon", "Param"
   };
   private LinkedList exp; // a LinkedList for a function's params

   /*
    * Used for Function address range parameter (ADDR1:ADDR2)
    * An address range is ultimately represented as follows:
    *
    * node type: COLON
    * node.nextRange points to the start address (a node of REL_ADDR or
    * ABS_ADDR), the start address' nextRange points to the end address.
    */
   private Node nextRange;
   private String data; // the raw data
   private boolean pending; // used for processing functions, see Formula
   private float number; // the numeric value
   private int col;
   private int row;
   private int type; // which type the node is (see above 10 types)

   /**
    * This is an empty node constructor
    */
   Node()
   {
   }

   /**
    * Node constructor
    *
    * @param node
    */
   Node(Node node)
   {
      type = node.type;
      if (data != null)
      {
         data = new String(node.data);
      }
      number = node.number;
      row = node.row;
      col = node.col;
   }

   public void setCol(int col)
   {
      this.col = col;
   }

   public int getCol()
   {
      return col;
   }

   public void setData(String data)
   {
      this.data = data;
   }

   public String getData()
   {
      return data;
   }

   public void setExp(LinkedList exp)
   {
      this.exp = exp;
   }

   public LinkedList getExp()
   {
      return exp;
   }

   public void setNextRange(Node node)
   {
      nextRange = node;
   }

   public Node getNextRange()
   {
      return nextRange;
   }

   public void setNumber(float number)
   {
      this.number = number;
   }

   public float getNumber()
   {
      return number;
   }

   public void setParams(LinkedList list)
   {
      exp = list;
   }

   public LinkedList getParams()
   {
      return exp;
   }

   public void setPending(boolean pending)
   {
      this.pending = pending;
   }

   public boolean isPending()
   {
      return pending;
   }

   public void setRow(int row)
   {
      this.row = row;
   }

   public int getRow()
   {
      return row;
   }

   public void setType(int type)
   {
      this.type = type;
   }

   /** get/set functions */
   public int getType()
   {
      return type;
   }

   public boolean isType(int type)
   {
      return this.type == type;
   }

   public void addParam(Node node)
   {
      if (node.getExp().size() > 0)
      {
         exp.add(node);
      }
   }

   public void appendData(char data)
   {
      this.data += data;
   }

   public void appendData(String data)
   {
      this.data += data;
   }

   public boolean hasValue()
   {
      return ((type == Node.NUMBER) || (type == Node.REL_ADDR) || (type == Node.ABS_ADDR) || (type == Node.FUNCTION) || (type == Node.RPAREN));
   }

   /**
    *  This string funciton is for debug purpose only.
    *  Node -> String
    */
   public String toString()
   {
      //	return String.valueOf(type);
      switch (type)
      {
      case Node.NUMBER:
         return Float.toString(number);

      case Node.REL_ADDR:

         StringBuffer buf1 = new StringBuffer();
         buf1.append('(');
         buf1.append(String.valueOf(col));
         buf1.append(',');
         buf1.append(row);
         buf1.append(')');

         return buf1.toString();

      case Node.ABS_ADDR:

         StringBuffer buf2 = new StringBuffer();
         buf2.append('$');
         buf2.append(translateColumn(col));
         buf2.append('$');
         buf2.append(translateRow(row));

         return buf2.toString();

      case Node.FUNCTION:
         return data + exp.toString();

      case Node.EXP:
         return exp.toString();

      case Node.LPAREN:
         return "(";

      case Node.RPAREN:
         return ")";

      case Node.COMMA:
         return ",";

      case Node.COLON:
         return nextRange.toString() + ":" + nextRange.nextRange.toString();

      default:
         return data;
      }
   }

   /**
    * This translates the string form of column into column number ('A' -> 1)
    *
    * @param column the string value of the column to be converted
    * @return the int value of the column
    */
   public static int translateColumn(String column)
   {
      int col = 0;

      for (int i = 0; i < column.length(); i++)
      {
         col = (col * 26) + (column.charAt(i) - 'A' + 1);
      }

      return col - 1;
   }

   /**
    * This translates the int form of column into column string (1 -> 'A')
    *
    * @param column the int value of the column to be converted
    * @return the string value of the column
    */
   public static String translateColumn(int column)
   {
      column = column + 1;
      if (column < 1)
      {
         return null;
      }

      StringBuffer buf = new StringBuffer();

      int div = 1;

      while (div > 0)
      {
         div = (column - 1) / 26;
         buf.insert(0, (char) ('A' + (column - 1 - (div * 26))));
         column = div;
      }

      return buf.toString();
   }

   /**
    * This translates the string form of row into row number ('12' -> 12).
    *
    * @param row the string value of the row to be converted
    * @return the int value of the row
    */
   public static int translateRow(String row)
   {
      int r = Integer.parseInt(row);

      return r - 1;
   }

   /**
    * This translates the int value of row into a string (12 -> '12').
    *
    * @param row the int value of the row to be converted
    * @return the string value of the row
    */
   public static String translateRow(int row)
   {
      if (row < 0)
      {
         return null;
      }
      else
      {
         return String.valueOf(row + 1);
      }
   }

   /**
    * Given one Node, this returns two CellPoints that identify the
    * upleft and downright cells.
    *
    * row and col matter because relative addresses need them.
    *
    * @param row the int value fo the row
    * @param col the int value of the column
    * @return two CellPoint value for the range
    */
   public CellPoint[] getAddressRange(int row, int col)
   {
      CellPoint[] addr = new CellPoint[2];
      if (isType(Node.COLON))
      {
         // if it's an address range
         // get the start address
         CellPoint addr1 = getNextRange().toCellPoint(row, col);

         // get the end address
         CellPoint addr2 = getNextRange().getNextRange().toCellPoint(row, col);

         addr[0] = new CellPoint(min(addr1.getRow(), addr2.getRow()), min(addr1.getCol(), addr2.getCol()));
         addr[1] = new CellPoint(max(addr1.getRow(), addr2.getRow()), max(addr1.getCol(), addr2.getCol()));
      }
      else
      {
         // otherwise it's a normal address
         CellPoint cell = toCellPoint(row, col);
         addr[0] = cell;
         addr[1] = cell;
      }

      return addr;
   }

   /**
    * Node -> CellPoint
    */
   public CellPoint toCellPoint(int row, int col)
   {
      if (isType(Node.REL_ADDR))
      {
         return new CellPoint(getRow() + row, getCol() + col);
      }
      else
      {
         return new CellPoint(getRow(), getCol());
      }
   }

   private static int max(int m, int n)
   {
      return (m > n) ? m : n;
   }

   private static int min(int m, int n)
   {
      return (m < n) ? m : n;
   }
}
