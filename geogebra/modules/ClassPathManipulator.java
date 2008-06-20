package geogebra.modules;
/* 
 GeoGebra - Dynamic Mathematics for Schools
 Copyright Markus Hohenwarter and GeoGebra Inc.,  http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */
import java.lang.reflect.*;
import java.io.*;
import java.net.*;
/** 
 *  <h3>Class to manipulate Classpath</h3>
 *  Hack: Use reflection to overcome protected modifiers,
 *  not nice, but it works...
 *  <ul>Interface:
 *  <li> addFile(String)
 *  <li> addFile(File)
 *  <li> addURL(URL)
 *  <li> getClassPath():String
 *  <li> listClassPath()
 *  </ul>
    @author 	H-P Ulven
    @version  	04.06.08
 */
public final class ClassPathManipulator {

private static String   nl=System.getProperty("line.separator");

/** Adds a file give as String to the Classpath */
public static void addFile(String s) {
        File f = new File(s);
        addFile(f);
}//end method
 
/** Adds a file given as File to the Classpath */
public static void addFile(File f) {
    try{
        addURL(f.toURL(),null);   //System.out.println(f.toURL());
    }catch(MalformedURLException e){
        System.out.println("MalformedURLException for "+f.getName());
    }//try-catch
}//addFile(File)
 
/** Adds a URL to the Classpath */
public static void addURL(URL u, ClassLoader loader) {
    Class[] parameter=new Class[] {URL.class};
    //URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
    //URLClassLoader sysloader = (URLClassLoader)geogebra.gui.menubar.MenubarImpl.class.getClassLoader();
    if (loader == null) loader = ClassLoader.getSystemClassLoader();
    URLClassLoader sysloader = (URLClassLoader)loader;
    Class sysclass = URLClassLoader.class;
    try {
        Method method = sysclass.getDeclaredMethod("addURL",parameter);
        method.setAccessible(true);
        method.invoke(sysloader,new Object[]{ u });
    } catch (NoSuchMethodException t){
        System.out.println("ClassPathManipulator: addURL gives NoSuchMethodExcepton.");
    }catch(IllegalAccessException e){
        System.out.println("ClassPathManipulator: addURL gives IllegalAccesException.");
    }catch(InvocationTargetException e){
        System.out.println("ClassPathManipulator: addURL gives InvocationTargetException");
    }catch(Throwable t){
        System.out.println("ClassPathManipulator: addURL gives "+t.getMessage());
    }//end try catch
}//addURL(URL)

/** Lists the URLs int the Classpath */
public static void listClassPath() {
    System.out.println(getClassPath());
}//listClassPath()

public static String getClassPath() {
    String urlsstr="Classpath:"+nl;
    Class[] emptyparameter=new Class[] {};
    URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
    Class sysclass = URLClassLoader.class;
    URL[]   urls=null;
    try {
        Method method = sysclass.getDeclaredMethod("getURLs",emptyparameter);
        method.setAccessible(true);
        Object obs=method.invoke(sysloader,new Object[]{});
        urls=(URL[])obs;
        for(int i=0;i<urls.length;i++){
            urlsstr+=urls[i].toString()+nl;
        }//for
    } catch (NoSuchMethodException t){
        System.out.println("ClassPathManipulator: getURL gives NoSuchMethodExcepton.");
    }catch(IllegalAccessException e){
        System.out.println("ClassPathManipulator: getURL gives IllegalAccesException.");
    }catch(InvocationTargetException e){
        System.out.println("ClassPathManipulator: getURL gives InvocationTargetException");
    }catch(Throwable t){
        System.out.println("ClassPathManipulator: getURL gives "+t.getMessage());
    }//end try catch
    return urlsstr;
}//getClassPath()


 
}//class ClassPathManipulator