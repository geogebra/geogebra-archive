/* {{{ License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */ //}}}
// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.builtin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.mathpiper.lisp.LispError;
import org.mathpiper.lisp.Utility;
import org.mathpiper.lisp.cons.Cons;
import org.mathpiper.lisp.cons.ConsPointer;

public class JavaObject extends BuiltinContainer {

    private Object javaObject;

    public JavaObject(Object javaObject) {
        this.javaObject = javaObject;
    }

    public String send(ArgumentList aArgList) {
        return null;
    }

    // Narrow a type from String to the
    // narrowest possible type
    public static Object narrow(Object argument) {
        //System.out.println("XXXXXXX argstring: " + argstring);
        if (argument instanceof String) {

            String argstring = (String) argument;
            // Try integer
            try {
                return Integer.valueOf(argstring);
            } catch (NumberFormatException nfe) {
            }

            // Try double
            try {
                return Double.valueOf(argstring);
            } catch (NumberFormatException nfe) {
            }

            // Try boolean
            if (argstring.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if (argstring.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }

            // Try null
            if (argstring.equals("null")) {
                return null;
            }

            //Try  class
            try {
                Object clas = Class.forName(argstring);
                return clas;
            } catch (ClassNotFoundException cnfe) {
            }
        }//end if

        // Give up -- it's a string
        return argument;
    }

    // Narrow the the arguments
    public static Object[] narrow(Object argstrings[]) {
        Object narrowed[] = new Object[argstrings.length];

        for (int i = 0; i < narrowed.length; ++i) {
            narrowed[i] = narrow(argstrings[i]);
        }

        return narrowed;
    }

    // Get an array of the types of the give
    // array of objects
    public static Class[] getTypes(Object objs[]) {
        Class types[] = new Class[objs.length];

        for (int i = 0; i < objs.length; ++i) {

            if (objs[i] == null) {
                //types[i] = Class.forName("java.awt.Component");
                try {
                    types[i] = Class.forName("java.awt.Component");

                } catch (ClassNotFoundException cnfe) {
                }
            } else {
                types[i] = objs[i].getClass();

                // Convert wrapper types (like Double)
                // to primitive types (like double)

                if (types[i] == Double.class) {
                    types[i] = double.class;
                }
                if (types[i] == Integer.class) {
                    types[i] = int.class;
                }

                if (types[i] == Boolean.class) {
                    types[i] = boolean.class;
                }

            }//end if.
        }//end for.

        return types;
    }

    public static JavaObject instantiate(String className, Object[] parameters) throws Exception {

        // Narrow the arguments
        Object args[] = narrow(parameters);
        Class types[] = getTypes(args);

        try {
            // Find the specified class
            Class clas = Class.forName(className);

            Constructor constructor = clas.getConstructor(types);

            Object newObject = constructor.newInstance(args);

            JavaObject newObjectWrapper = new JavaObject(newObject);

            return newObjectWrapper;

        } catch (ClassNotFoundException cnfe) {
            throw new Exception(
                    "Can't find class " + className);
        } catch (InstantiationException nsme) {
            throw new Exception(
                    "Can't instantiate " + className);
        } catch (IllegalAccessException iae) {
            throw new Exception(
                    "Not allowed to instantiate " + className);
        } catch (InvocationTargetException ite) {
            // If the method itself throws an exception, we want to save it
            throw (Exception) new Exception(
                    "Exception while executing command").initCause(ite);
        }//end catch.


    }

    public JavaObject execute(String methodName, Object parameters[]) throws Exception {


        String className = javaObject.getClass().getName();

        try {
            Class clas;
            if (className.equals("java.lang.Class")) {
                clas = (Class) this.javaObject;
                className = clas.getName();
            } else {
                clas = Class.forName(className);
            }


            // Narrow the arguments
            Object args[] = narrow(parameters);
            Class types[] = getTypes(args);





            /*
            System.out.println("XXXXX " + methodName);
            for(Object ob:types)
            {
            System.out.println("XXXXX " + ob.toString());
            }
             */

            // Find the specified method

            Method method = clas.getMethod(methodName, types);

            // Invoke the method on the narrowed arguments
            Object retval = method.invoke(javaObject, args);

            return new JavaObject(retval);

        } catch (ClassNotFoundException cnfe) {
            throw new Exception(
                    "Can't find class " + className);
        } catch (NoSuchMethodException nsme) {
            throw new Exception(
                    "Can't find method " + methodName + " in " + className);
        } catch (IllegalAccessException iae) {
            throw new Exception(
                    "Not allowed to call method " + methodName + " in " + className);
        } catch (InvocationTargetException ite) {
            // If the method itself throws an exception, we want to save it
            throw (Exception) new Exception(
                    "Exception while executing command").initCause(ite);
        }//end catch.

    }//end class

    public String typeName() {
        return javaObject.getClass().getName();
    }//end method.


    public Object getObject() {
        return javaObject;
    }//end method.

    public static List LispListToJavaList(ConsPointer lispList) throws Exception {
        LispError.check(Utility.isList(lispList), LispError.NOT_A_LIST);
        
        lispList.goNext();

        ArrayList javaList = new ArrayList();

        while (lispList.getCons() != null) {

            Object item = lispList.car();
            item = narrow(item);
            javaList.add(item);

            lispList.goNext();

        }//end while.

        return javaList;
    }//end method.



    public static double[] LispListToJavaDoubleArray(ConsPointer lispListPointer) throws Exception {
        LispError.check(Utility.isList(lispListPointer), LispError.NOT_A_LIST);

        lispListPointer.goNext(); //Remove List designator.

        double[] values = new double[Utility.listLength(lispListPointer)];

        int index = 0;
        while (lispListPointer.getCons() != null) {

            Object item = lispListPointer.car();

            LispError.check(item instanceof String, LispError.INVALID_ARGUMENT);
            String itemString = (String) item;

            try {
                values[index++] = Double.parseDouble(itemString);
            } catch (NumberFormatException nfe) {
                LispError.raiseError("Can not convert into a double." );
            }//end try/catch.

            lispListPointer.goNext();

        }//end while.

        return values;
        
    }//end method.

}//end class.

