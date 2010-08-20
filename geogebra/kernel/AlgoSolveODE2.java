package geogebra.kernel;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;

public class AlgoSolveODE2 extends AlgoElement {

		private static final long serialVersionUID = 1L;
		private GeoFunction b, c, f; // input
		private GeoNumeric x, y, yDot, start, end, step; // input
	    private GeoList g; // output        
	    
	    public AlgoSolveODE2(Construction cons, String label, GeoFunctionable b, GeoFunctionable c, GeoFunctionable f, GeoNumeric x, GeoNumeric y, GeoNumeric yDot, GeoNumeric end, GeoNumeric step) {
	    	super(cons);
	        this.b = b.getGeoFunction();            	
	        this.c = c.getGeoFunction();            	
	        this.f = f.getGeoFunction();            	
	        this.x = x;            	
	        this.y = y;            	   	
	        this.yDot = yDot;            	   	
	        this.end = end;            	
	        this.step = step;            	
	    	
	        g = new GeoList(cons);                
	        setInputOutput(); // for AlgoElement        
	        compute();
	        g.setLabel(label);
	    }
	    
	    public String getClassName() {
	        return "AlgoSolveODE2";
	    }
	    
	    // for AlgoElement
	    protected void setInputOutput() {
	        input = new GeoElement[8];
	    	
	        input[0] = b;
	        input[1] = c;
	        input[2] = f;
	        input[3] = x;
	        input[4] = y;
	        input[5] = yDot;
	        input[6] = end;
	        input[7] = step;

	        output = new GeoElement[1];
	        output[0] = g;
	        setDependencies(); // done by AlgoElement
	    }

	    public GeoList getResult() {
	        return g;
	    }

	    protected final void compute() {       
	        if (!b.isDefined() || !c.isDefined() || !f.isDefined() || !x.isDefined() || !y.isDefined() || !yDot.isDefined() || !step.isDefined() || !end.isDefined() || kernel.isZero(step.getDouble())) {
	        	g.setUndefined();
	        	return;
	        }    
	        
	        g.clear();
	        
	        FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(step.getDouble());
	        FirstOrderDifferentialEquations ode;
	        
	        ode = new ODE2(b, c, f);
	        integrator.addStepHandler(stepHandler);
	        
            boolean oldState = cons.isSuppressLabelsActive();
            cons.setSuppressLabelCreation(true);
            g.add(new GeoPoint(cons, null, x.getDouble(), y.getDouble(), 1.0));
            cons.setSuppressLabelCreation(oldState);

	        double[] yy2 = new double[] { y.getDouble(), yDot.getDouble() }; // initial state
	        try {
	        	integrator.integrate(ode, x.getDouble(), yy2, end.getDouble(), yy2);
			} catch (DerivativeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IntegratorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			g.setDefined(true);	
			System.gc();
			
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
	            //System.out.println(t + " " + y[0]+ " "+y[1]);
	            
	            boolean oldState = cons.isSuppressLabelsActive();
	            cons.setSuppressLabelCreation(true);
	            
	            g.add(new GeoPoint(cons, null, t, y[0], 1.0));
	            	
	            cons.setSuppressLabelCreation(oldState);
	        }
	    };
	    //integrator.addStepHandler(stepHandler);
	    
	    
	    private static class ODE2 implements FirstOrderDifferentialEquations {

	        GeoFunction b, c, f;

	        public ODE2(GeoFunction b, GeoFunction c, GeoFunction f) {
	            this.b = b;
	            this.c = c;
	            this.f = f;
	        }

	        public int getDimension() {
	            return 2;
	        }
	        /* Transform 2nd order into 2 linked first order
	         * y0'' + b y0' + c y0 = f(x)
	         * substitute y0' = y1 (1)
	         * y1' + b y1 + c y = f(x)
	         * y1' = f(x) - b y1 - c y0 (2)
	         */

	        public void computeDerivatives(double t, double[] y, double[] yDot) {
	        	
	        	yDot[0] = y[1]; // (1)
	        	yDot[1] = f.evaluate(t) - b.evaluate(t) * y[1] - c.evaluate(t) * y[0]; // (2)

	        }

	    }
	}

