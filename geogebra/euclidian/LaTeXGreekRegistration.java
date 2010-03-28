package geogebra.euclidian;

import geogebra.main.Application;

import java.lang.Character.UnicodeBlock;
import java.net.URL;
import java.net.URLClassLoader;

import org.scilab.forge.jlatexmath.AlphabetRegistration;
import org.scilab.forge.jlatexmath.greek.GreekRegistration;

/*
load dynamically  greek fonts in JLaTeXMath
 */




public class LaTeXGreekRegistration implements AlphabetRegistration {

	Object pack = null;
	Application app;

	public LaTeXGreekRegistration(Application app) {
		this.app = app;
	}

	public UnicodeBlock getUnicodeBlock() {
		return Character.UnicodeBlock.GREEK;
	}

	public Object getPackage() {
		
		if (pack != null) return pack;
		
		try {
			URL url[] = {new URL(app.getCodeBase()+"jlm_greek.jar")};
			Application.debug("loading Greek fonts from"+app.getCodeBase()+"/"+"jlm_greek.jar");
			ClassLoader loader = new URLClassLoader(url);
			Object obj;
			obj = Class.forName("org.scilab.forge.jlatexmath.greek.GreekRegistration", true, loader).newInstance();
			this.pack = obj;
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getTeXFontFileName() {
		return ((GreekRegistration) pack).getTeXFontFileName();
	}
}

