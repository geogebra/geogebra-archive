package geogebra.plugin;

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
				Object value = (args.length != 0) ? args[0] : Undefined.instance;
				app.showMessage((String) value);
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
