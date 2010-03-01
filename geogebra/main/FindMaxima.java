/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.
 */

package geogebra.main;



import java.io.File;
import java.io.FileFilter;

/**
 * Class FindMaxima
 * Used by setDefaultCas(String optionValue) in Application.java
 * Made to get a better oversight over the search process and
 * for easier modifications later.
 *
 * This class is just a suggestion, nothing more.
 * 
 * Interface:
 * 		search()
 * 		isMaximaFound():boolean
 * 		getPath():String 
 *
 * @author Hans-Petter Ulven
 * @version 2010-02-27
 */


public  class FindMaxima {
	
	private static  boolean		foundmaxima =	false;
	private static  String		path		=	null;
	private static  String[]	searchfolders;
	

	//Search folders (additional folders can be added if neccessary, hopefully not...)
	private final static String[]	winfolders={
			"",										//space for getenv(ProgramFiles)
			""										//space for getenv(ProgramFiles(x86)
			//"C:\Program Files"					//more choices possible...
	};//winfolders
	
	private final static String[]	macfolders={
			"/Applications/Maxima.app/Contents/Resources/bin/maxima"
	};//macfolders
	
	private final static String[]	linuxfolders={
			"/usr/bin/maxima",
			"/usr/local/bin/maxima",
			"/opt/bin/maxima",
			"/opt/local/bin/maxima"
	};//linuxfolders
	
	/** Constructor, todo: should be singleton... */
	public  FindMaxima(){
		String pf=null;
		foundmaxima=false;
		if(Application.WINDOWS){
			pf=System.getenv("ProgramFiles");
			if(pf==null){
				winfolders[0]="C:\\Program Files";
			}else{
				winfolders[0]=pf;
			}//if
			pf=System.getenv("ProgramFile(x86)");
			if(pf==null){
				winfolders[1]="C:\\Program Files (x86)";
			}else{
				winfolders[1]=pf;
			}//if
			searchfolders=winfolders;	
		}else if(Application.MAC_OS){
			searchfolders=macfolders;
		}else{										//Has to be Linux then, hopefully...
			searchfolders=linuxfolders;
		}//if os

	}//getInstance(os)
	
	public boolean isMaximaFound(){ return foundmaxima ? true : false;}
	
	public void search(){
		File	programfolder;		//searchfolder
		File[] 	maximafolders;		//possible maximafolders
		File	file;
		int		sizemaxima;
		String	pathstr;
		int		size			=	searchfolders.length;
		foundmaxima=false;			//to be sure...
		try{
			int i = -1;
			while (++i < size && foundmaxima == false) {
				programfolder=new File(searchfolders[i]);
				if(Application.WINDOWS){			//WIN
					maximafolders=programfolder.listFiles(MaximaFileFilter.getInstance());	//get possible Maxima-x.xx folders
					sizemaxima= (maximafolders == null) ? 0 : maximafolders.length;
					if(sizemaxima>0){
						//ToDo?: if(sizemaxima>1){ sort and decide highest version number...
						pathstr=maximafolders[0].getCanonicalPath();	//use the first one for the time being
						if(Application.WINDOWS){pathstr=fixSlashes(pathstr);}		//The wretched \\ business...
						file=new File(pathstr);
						if(file.exists()){
							Application.debug("Maxima found at: "+pathstr);
							foundmaxima=true;
							path=pathstr;
						}else{
							Application.debug("Maxima not found at: "+pathstr);
						}//if maximafolder existed
					}else{
						Application.debug("Maxima not found under: "+programfolder);
					}//if maximafolders
				}else if(Application.MAC_OS){
					if(programfolder.exists()){		//MAC: programfolder is full path to executable!
						Application.debug("Maxima found at: "+searchfolders[i]);
						foundmaxima=true;
						path=searchfolders[i];
					}else{
						Application.debug("Maxima not found at: "+searchfolders[i]);
					}//if maxima executable
				}else{								//has to be linux, hopefully. Could group with Mac, but keeps options open...
					if(programfolder.exists()){		//Linux: programfolder is full path to executable!
						Application.debug("Maxima found at: "+searchfolders[i]);
						foundmaxima=true;
						path=searchfolders[i];
					}else{
						Application.debug("Maxima not found at: "+searchfolders[i]);
					}//if maxima executable
				}//if os
			}//for all searchfolders
		}catch(Exception e){
			Application.debug(e.toString());
		}//try-catch
	}//search()
	
	public String getPath(){ return path;}
	
    // I found no other way of doing this :-(
	// replaceAll() doesn't handle \ to well...
    private static String fixSlashes(String s){
        StringBuilder result = new StringBuilder();
        char c;
        for(int i=0;i<s.length();i++){
            c=s.charAt(i);
            if(c=='\\'){
                result.append("\\\\");
            }else{
                result.append(c);
            }
        }//for
        return result.toString();
    }//replace(String)

	// If more sophisticated search is needed
	static class MaximaFileFilter implements java.io.FileFilter{
		private static MaximaFileFilter	singleton=null;
		
		public static FileFilter getInstance(){
			if(singleton==null){singleton=new MaximaFileFilter();}
			return singleton;
		}//getInstance()
		
		public final boolean accept(File file){
			if(file.getName().startsWith("Maxima-")){	// - avoids problems when someone makes a program with name MaxiMath or something like that...
				return true;
			}else{
				return false;
			}//if acceptable
		}//accept(File)
	}//class MaximaFileFilter
		
 /// --- SNIP --- ///	
	public static final void main(String[] args){
		FindMaxima fm=new FindMaxima();
		fm.search();
		Application.debug("found:"+fm.isMaximaFound());
		Application.debug("path: "+fm.getPath());
		
		
	}//main() for testing
 /// --- SNIP --- ///	
	
}//class FindMaxima
