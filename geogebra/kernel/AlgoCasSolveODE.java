package geogebra.kernel;

public class AlgoCasSolveODE extends AlgoCasBase {

	public AlgoCasSolveODE(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public String getClassName() {
		return "AlgoCasSolveODE";
	}

	@Override
	protected void applyCasCommand() {
		g.setUsingCasCommand("SolveODE(%)", f, false);		
	}
	
}
