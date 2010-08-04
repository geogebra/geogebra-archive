package geogebra.kernel;

public class AlgoCasSimplify extends AlgoCasBase {

	public AlgoCasSimplify(Construction cons,  String label, CasEvaluableFunction f) {
		super(cons, label, f);
	}

	@Override
	public String getClassName() {
		return "AlgoCasSimplify";
	}

	@Override
	protected void applyCasCommand() {
		g.setUsingCasCommand("SimplifyFull(%)", f, false);		
	}
	
}
