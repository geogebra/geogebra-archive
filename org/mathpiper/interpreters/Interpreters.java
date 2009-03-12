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
 */

//}}}
// :indentSize=4:lineSeparator=\n:noTabs=false:tabSize=4:folding=explicit:collapseFolds=0:
package org.mathpiper.interpreters;

/**
 * This class consists exclusively of static factory methods which return MathPiper interpreter instances.
 * These static methods are the only way to obtain instances of MathPiper interpeters.
 */
public class Interpreters {

    private Interpreters() {
    }

    /**
     * Instantiates a new synchronous {@link Interpreter} and returns it.  The interpreter contains
     * its own namespace and it runs in the client's thread.
     * 
     * @return a new synchronous interpreter
     */
    public static Interpreter newSynchronousInterpreter() {
        return SynchronousInterpreter.newInstance();
    }

    /**
     * Instantiates a new synchronous {@link Interpreter} and returns it.  The interpreter contains
     * its own namespace and it runs in the client's thread.  The docBase argument is used
     * to specify a path which contains the core MathPiper scripts.  A typical case where a
     * docBase path needs to be  used is with Applets.  The following code shows the document
     * base being obtained inside of an applet and then being used to obtain a new interpreter
     * instance which uses the docBase path to locate the core MathPiper scripts:
     * <p>
     * {@code String docBase = getDocumentBase().toString();}<br />
     *  {@code mathPiperInterpreter = org.mathpiper.interpreters.Interpreters.newSynchronousInterpreter(docBase);}
     *
     * @param  docBase path which contains core MathPiper scripts
     * @return a new synchronous interpreter
     */
    public static Interpreter newSynchronousInterpreter(String docBase) {
        return SynchronousInterpreter.newInstance(docBase);
    }

     /**
     * Returns a synchronous {@link Interpreter} singleton.  All users of the interpreter singleton share
      * the same namespace and it runs in the client's thread.
     *
     * @return a synchronous interpreter singleton
     */
    public static Interpreter getSynchronousInterpreter() {
        return SynchronousInterpreter.getInstance();
    }

     /**
     * Returns a synchronous {@link Interpreter} singleton.  All users of the interpreter singleton share
      * the same namespace and it runs in the client's thread.  The docBase argument is used
     * to specify a path which contains the core MathPiper scripts.  A typical case where a
     * docBase path needs to be  used is with Applets.  The following code shows the document
     * base being obtained inside of an applet and then being used to obtain a new interpreter
     * instance which uses the docBase path to locate the core MathPiper scripts:
     * <p>
     * {@code String docBase = getDocumentBase().toString();}<br />
     * {@code mathPiperInterpreter = org.mathpiper.interpreters.Interpreters.newSynchronousInterpreter(docBase);}
     *
     * @param docBase path which contains core MathPiper scripts
     * @return a synchronous interpreter singleton
     */
    public static Interpreter getSynchronousInterpreter(String docBase) {
        return SynchronousInterpreter.getInstance(docBase);
    }



}//end class.
