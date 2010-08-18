package geogebra.kernel;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.EulerIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;

public class AlgoSolveODE extends AlgoElement {

		private static final long serialVersionUID = 1L;
		private GeoFunctionNVar f; // input
		private GeoNumeric x, y, start, end, step; // input
	    private GeoList g; // output        
	    
	    public AlgoSolveODE(Construction cons, String label, GeoFunctionNVar f, GeoNumeric x, GeoNumeric y, GeoNumeric end, GeoNumeric step) {
	    	super(cons);
	        this.f = f;            	
	        this.x = x;            	
	        this.y = y;            	   	
	        this.end = end;            	
	        this.step = step;            	
	    	
	        g = new GeoList(cons);                
	        setInputOutput(); // for AlgoElement        
	        compute();
	        g.setLabel(label);
	    }
	    
	    public String getClassName() {
	        return "AlgoSolveODE";
	    }
	    
	    // for AlgoElement
	    protected void setInputOutput() {
	        input = new GeoElement[5];
	        input[0] = f;
	        input[1] = x;
	        input[2] = y;
	        input[3] = end;
	        input[4] = step;

	        output = new GeoElement[1];
	        output[0] = g;
	        setDependencies(); // done by AlgoElement
	    }

	    public GeoList getResult() {
	        return g;
	    }

	    protected final void compute() {       
	        if (!f.isDefined() || kernel.isZero(step.getDouble())) {
	        	g.setUndefined();
	        	return;
	        }    
	        
	        g.clear();
	        
	        //FirstOrderIntegrator dp853 = new DormandPrince853Integrator(1.0e-8, 100.0, 1.0e-10, 1.0e-10);
	        FirstOrderIntegrator dp853 = new EulerIntegrator(step.getDouble());
	        FirstOrderDifferentialEquations ode = new ODE(f);
	        dp853.addStepHandler(stepHandler);
	        double[] yy = new double[] { y.getDouble() }; // initial state
	        try {
				dp853.integrate(ode, x.getDouble(), yy, end.getDouble(), yy);
			} catch (DerivativeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IntegratorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // now y contains final state at time t=16.0
			
			g.setDefined(true);	
			
	    }
	    
	    final public String toString() {
	    	return getCommandDescription();
	    }

	    StepHandler stepHandler = new StepHandler() {
	        public void reset() {}
	        
	        Construction cons = kernel.getConstruction();
	                
	        public boolean requiresDenseOutput() { return false; }
	                
	        public void handleStep(StepInterpolator interpolator, boolean isLast) throws DerivativeException {
	            double   t = interpolator.getCurrentTime();
	            double[] y = interpolator.getInterpolatedState();
	            //System.out.println(t + " " + y[0]);
	            
	            boolean oldState = cons.isSuppressLabelsActive();
	            cons.setSuppressLabelCreation(true);
	            g.add(new GeoPoint(cons, null, t, y[0], 1.0));
	            cons.setSuppressLabelCreation(oldState);
	        }
	    };
	    //integrator.addStepHandler(stepHandler);
	    
	    private static class ODE implements FirstOrderDifferentialEquations {

	        GeoFunctionNVar f;

	        public ODE(GeoFunctionNVar f) {
	            this.f = f;
	        }

	        public int getDimension() {
	            return 1;
	        }

	        public void computeDerivatives(double t, double[] y, double[] yDot) {
	            
	        	double input[] = {t, y[0]};
	        	
	        	yDot[0] = f.evaluate(input);

	        }

	    }
	}

