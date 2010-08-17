package geogebra.plugin;

import geogebra.kernel.GeoElement;
import geogebra.main.Application;
import geogebra.main.MyError;

import javax.swing.JOptionPane;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionCall;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

/*
 * @author Joel Duffin
 */

public class GeoGebraGlobal implements IdFunctionCall {

	Application app;

	GeoGebraGlobal(Application app) {
		this.app = app;
	}

	public static void init(Application app, Scriptable scope, boolean sealed) {
		GeoGebraGlobal obj = new GeoGebraGlobal(app);

		for (int id = 1; id <= LAST_SCOPE_FUNCTION_ID; ++id) {
			String name;
			int arity = 1;
			switch (id) {
			case Id_alert:
				name = "alert";
				break;
			case Id_prompt:
				name = "prompt";
				break;
			default:
				throw Kit.codeBug();
			}
			IdFunctionObject f = new IdFunctionObject(obj, FTAG, id, name, arity, scope);
			if (sealed) {
				f.sealObject();
			}
			f.exportAsScopeProperty();
		}
	}
	
	public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		if (f.hasTag(FTAG)) {
			int methodId = f.methodId();
			switch (methodId) {
			case Id_alert: {
				
				if (args.length != 1) {
			        String error = argNumError(args.length, "alert( <String> )");
			        app.showError(error);
					throw new Error(error);
				}
				
				Object value = (args.length != 0) ? args[0] : Undefined.instance;
				
				if (!(value instanceof String)) {
			        //app.showError(app.getPlain("ErrorInJavaScript")+"\n"+app.getPlain("SynataxAatLineB", "alert(String)", linep[0]+""));
			        String error = argError(value.toString(), "alert( <String> )");
			        app.showError(error);
					throw new Error(error);
				}
				
				Object[] options = {app.getPlain("StopScript"), app.getPlain("OK")};
				int n = JOptionPane.showOptionDialog(app.getFrame(),
						(String) value,
					    "GeoGebra",
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,     //do not use a custom Icon
					    options,  //the titles of buttons
					    options[0]); //default button title
				
				if (n == 0) throw new Error("Script stopped by user");
				
				return "";
			}
			case Id_prompt: {
				Object value0 = (args.length != 0) ? args[0] : "";
				Object value1 = (args.length > 1) ? args[1] : "";
				String s = (String)JOptionPane.showInputDialog(
	                    app.getFrame(),
	                    value0,
	                    "GeoGebra",
	                    JOptionPane.PLAIN_MESSAGE,
	                    null,
	                    null,
	                    value1);
				return s;
			}
			}
		}
		throw f.unknown();
	}
	
	private StringBuilder sb;
	int[] linep = new int[1];
	
	private String argError(Object arg, String syntax) {
		if (sb == null) sb = new StringBuilder();
		else sb.setLength(0);
        Context.getSourcePositionFromStack(linep); // line number of error
		sb.append(app.getPlain("ErrorInJavaScriptAtLineA", linep[0]+""));
		sb.append("\n");
		sb.append(app.getError("IllegalArgument"));
		sb.append(": ");
		sb.append(arg.toString());
		sb.append("\n\n");
		sb.append(app.getPlain("Syntax"));
		sb.append(":\n");
		sb.append(syntax);
		return sb.toString();
	}

	private String argNumError(int argNumber, String syntax) {
		if (sb == null) sb = new StringBuilder();
		else sb.setLength(0);
        Context.getSourcePositionFromStack(linep); // line number of error
		sb.append(app.getPlain("ErrorInJavaScriptAtLineA", linep[0]+""));
		sb.append("\n");
		sb.append(app.getError("IllegalArgumentNumber"));
		sb.append(": ");
		sb.append(argNumber);
		sb.append("\n\n");
		sb.append(app.getPlain("Syntax"));
		sb.append(":\n");
		sb.append(syntax);
		return sb.toString();
	}

	public static void initStandardObjects(Application app, Scriptable scope, String arg, boolean sealed) {
		geogebra.plugin.GgbAPI ggbApi = app.getGgbApi();
		Object wrappedOut = Context.javaToJS(ggbApi, scope);
		ScriptableObject.putProperty(scope, "ggbApplet", wrappedOut);

		if (arg != null) {
			Object wrappedArg = Context.javaToJS(arg, scope);
			ScriptableObject.putProperty(scope, "arg", wrappedArg);
		}

		// add geogebra methods as top level js methods
		init(app, scope, sealed);
	}

	private static final Object FTAG = "Global";

	private static final int 
	Id_alert = 1, 
	Id_prompt = 2, 
		LAST_SCOPE_FUNCTION_ID = 2; 
}
