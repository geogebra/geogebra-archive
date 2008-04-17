package geogebra.plugin;
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
 *  <li> getClassPath():String
 *  <li> listClassPath()
 *  </ul>
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
        addURL(f.toURL());   //System.out.println(f.toURL());
    }catch(MalformedURLException e){
        System.out.println("MalformedURLException for "+f.getName());
    }//try-catch
}//addFile(File)
 
/** Adds a URL to the Classpath */
public static void addURL(URL u) {
    Class[] parameter=new Class[] {URL.class};
    URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
    Class sysclass = URLClassLoader.class;
    try {
        Method method = sysclass.getDeclaredMethod("addURL",parameter);
        method.setAccessible(true);
        method.invoke(sysloader,new Object[]{ u });
    } catch (Throwable t) {
        //throw new IOException("Error, could not add URL to system classloader");
        System.out.println("Could not add URL "+u.toString()+" to ClassPath!");
        t.printStackTrace();
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
    } catch (Throwable t) {
        //throw new IOException("Error, could not add URL to system classloader");
        System.out.println("Could not get (some?/all?) URLs in Classpath!");
        t.printStackTrace();
    }//end try catch
    return urlsstr;
}//getClassPath()

/** main() - Only for testing of this class */
public final static void main(String[] args){
/*
    Class c=null,c2=null;
    Object o=null,o2=null;
    ClassPathManipulator cph=new ClassPathManipulator();
    cph.listClassPath();
    cph.addFile("plugins.zip");
    cph.listClassPath();
    try{
        c=Class.forName("plugins.Plugin_One");
        c2=Class.forName("plugins.Plugin_Two");
    }catch(ClassNotFoundException cnfe){
        System.out.println("Class not found!");
    }//try-catch
    try{
        o = c.newInstance();
        o2=c2.newInstance();
    }catch(Exception e) {
        System.out.println("Could not instantiate "+o.toString());
    }//try-catch
    plugins.Plugin_One p=(plugins.Plugin_One)o;
    plugins.Plugin_Two p2=(plugins.Plugin_Two)o2;
    System.out.println(p.getMenuText());
    System.out.println(p2.getMenuText());
    */
}//main()
 
}//class ClassPathManipulator