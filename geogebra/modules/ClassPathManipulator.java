package geogebra.modules;

/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.

 */
import geogebra.Application;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * <h3>Class to manipulate Classpath</h3> Hack: Use reflection to overcome
 * protected modifiers, not nice, but it works...
 * <ul>
 * Interface:
 * <li>addFile(String)
 * <li>addFile(File)
 * <li>addURL(URL)
 * <li>getClassPath():String
 * <li>listClassPath()
 * </ul>
 * 
 * @author H-P Ulven
 * @version 04.06.08
 */
public final class ClassPathManipulator {

	private static String nl = System.getProperty("line.separator");

	/** Adds a file give as String to the Classpath */
	public synchronized static boolean addFile(String s) {
		File f = new File(s);
		return addFile(f);
	}// end method

	/** Adds a file given as File to the Classpath */
	public synchronized static boolean addFile(File f) {
		try {
			addURL(f.toURL(), null); // Application.debug(f.toURL());
			return true;
		} catch (MalformedURLException e) {
			Application.debug("MalformedURLException for " + f.getName());
			return false;
		}// try-catch
	}// addFile(File)

	/** Adds a URL to the Classpath */
	public synchronized static boolean addURL(URL u, ClassLoader loader) {
		Class[] parameter = new Class[] { URL.class };
		// URLClassLoader sysloader =
		// (URLClassLoader)ClassLoader.getSystemClassLoader();
		// URLClassLoader sysloader =
		// (URLClassLoader)geogebra.gui.menubar.MenubarImpl
		// .class.getClassLoader();
		if (loader == null)
			loader = ClassLoader.getSystemClassLoader();
		URLClassLoader sysloader = (URLClassLoader) loader;
		Class sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameter);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { u });
			return true;
		} catch (NoSuchMethodException t) {
			Application
					.debug("ClassPathManipulator: addURL gives NoSuchMethodExcepton.");
			return false;
		} catch (IllegalAccessException e) {
			Application
					.debug("ClassPathManipulator: addURL gives IllegalAccesException.");
			return false;
		} catch (InvocationTargetException e) {
			Application
					.debug("ClassPathManipulator: addURL gives InvocationTargetException");
			return false;
		} catch (Throwable t) {
			Application.debug("ClassPathManipulator: addURL gives "
					+ t.getMessage());
			return false;
		}// end try catch
	}// addURL(URL)

	/** Lists the URLs int the Classpath */
	public static void listClassPath() {
		Application.debug(getClassPath());
	}// listClassPath()

	public static String getClassPath() {
		String urlsstr = "Classpath:" + nl;
		Class[] emptyparameter = new Class[] {};
		URLClassLoader sysloader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
		URL[] urls = null;
		try {
			Method method = sysclass.getDeclaredMethod("getURLs",
					emptyparameter);
			method.setAccessible(true);
			Object obs = method.invoke(sysloader, new Object[] {});
			urls = (URL[]) obs;
			for (int i = 0; i < urls.length; i++) {
				urlsstr += urls[i].toString() + nl;
			}// for
		} catch (NoSuchMethodException t) {
			Application
					.debug("ClassPathManipulator: getURL gives NoSuchMethodExcepton.");
		} catch (IllegalAccessException e) {
			Application
					.debug("ClassPathManipulator: getURL gives IllegalAccesException.");
		} catch (InvocationTargetException e) {
			Application
					.debug("ClassPathManipulator: getURL gives InvocationTargetException");
		} catch (Throwable t) {
			Application.debug("ClassPathManipulator: getURL gives "
					+ t.getMessage());
		}// end try catch
		return urlsstr;
	}// getClassPath()

}// class ClassPathManipulator