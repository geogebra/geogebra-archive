package geogebra3D.kernel3D;

import geogebra.kernel.AlgoElement;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;



/**
 * 
 * Super-class of algorithms creating {@link GeoElement3D}.
 * 
 * <h3> How to create a new algo </h3>
 
  We'll call here our new element "AlgoNew3D" and create an new class AlgoNew3D.
  <ul>
    <li> Create a member referring to the ouput(s)
    <p>
    <code>
      private GeoNew3D geoNew3D;
    </code>
    </li>
    <li> Create a constructor :
    <p>
    <code>
    public AlgoNew3D(Construction c, ... inputs) { <br> &nbsp;&nbsp;
      super(c); <br> &nbsp;&nbsp;
      geoNew3D = new GeoNew3D(....); <br> &nbsp;&nbsp;
      //other outputs <br> &nbsp;&nbsp;
      //eventually remember the inputs in special members <br> &nbsp;&nbsp;
      setInputOutput(new GeoElement[] {... inputs}, new GeoElement[] {... outputs}); <br> 
	}
    </code>
    </li>
    <li> Create a constructor with a label :
    <p>
    <code>
    public AlgoNew3D(Construction c, String label, ... inputs) { <br> &nbsp;&nbsp;
      this(c, ... inputs); <br> &nbsp;&nbsp;
      geoNew3D.setLabel(label); <br> 
    }
    </code>
    </li>
    <li> Explain how outputs are computed with the inputs :
    <p>
    <code>
    protected void compute() { <br> &nbsp;&nbsp;
      // stuff <br> 
    }
    </code>
    </li>
    <li> Set the classname :
    <p>
    <code>
    protected String getClassName() { <br> &nbsp;&nbsp;
      return "AlgoNew3D"; <br> 
    }
    </code>
    </li>
    <li> Create a <code>getGeo()</code> method for each output you want the kernel be able to catch
    </li>
   </ul>
   
    <h3> See </h3> 
	<ul>
	<li> algo2command.properties file in geogebra.kernel to create a key for AlgoNew3D :
	     <p>
	     <code> 
	     AlgoNew3D=New3D
	     </code>
	</li>
	</ul>

 
 * 
 * @author ggb3D
 *
 */
abstract public class AlgoElement3D extends AlgoElement{

	/**
	 * Default constructor.
	 * 
	 * @param c construction
	 */
	public AlgoElement3D(Construction c) {
		this(c,true);
	}
	
	/**
	 * constructor.
	 * 
	 * @param c construction
	 * @param addToConstructionList says if it has to be added to the construction list
	 */
	public AlgoElement3D(Construction c, boolean addToConstructionList) {
		super(c,addToConstructionList);
	}

	
	/**
	 * set the {@link GeoElement} in input and in output.
	 * call finally {@link #setInputOutput()}
	 * @param a_input elements in input
	 * @param a_output elements in output
	 */
	protected void setInputOutput(GeoElement[] a_input, GeoElement[] a_output) {
		setInputOutput(a_input,a_output,true);
	}
	
	/**
	 * set the {@link GeoElement} in input and in output.
	 * call finally {@link #setInputOutput()}
	 * @param a_input elements in input
	 * @param a_output elements in output
	 * @param setDependencies says if the dependencies have to be set
	 */
	protected void setInputOutput(GeoElement[] a_input, GeoElement[] a_output, boolean setDependencies) {
		
		input = a_input;
		output = a_output;
		setInputOutput(setDependencies);
	
	}
	/**
	 * calls {@link AlgoElement#setDependencies()} and {@link AlgoElement#compute()}
	 */
	protected void setInputOutput() {
		
		setInputOutput(true);
       
	}	
	/**
	 * calls {@link AlgoElement#setDependencies()} and {@link AlgoElement#compute()}
	 * @param setDependencies says if the dependencies have to be set
	 */
	protected void setInputOutput(boolean setDependencies) {
		    
		if (setDependencies)
			setDependencies();
        compute();
       
	}

}
