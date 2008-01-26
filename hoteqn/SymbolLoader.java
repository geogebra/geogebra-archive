/*****************************************************************************
*                                                                            *
*                   HotEqn Equation Viewer Component                         *
*                                                                            *
******************************************************************************
* Java-Coponent to view mathematical Equations provided in the LaTeX language*
******************************************************************************

Copyright 2006 Stefan Müller and Christian Schmid

This file is part of the HotEqn package.

    HotEqn is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; 
    HotEqn is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

******************************************************************************/


package hoteqn;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.StringTokenizer;

//SymbolLoader for packed font files (fast speed)
class SymbolLoader {
private ImageProducer [] imageSources = {null,null,null,null,null};
private String [] fontsizes = {"8","10","12","14","18"};	
private Hashtable fontdesH = new Hashtable (189);
//Fonts are included in HotEqn zip/jar file
private static boolean kLocalFonts=true;

public SymbolLoader() { }
// dummy constructor

public Image getImage( boolean  appletB, boolean beanB, String filenameS,
                Graphics g,      Applet   app) {
	StringTokenizer st = new StringTokenizer(filenameS, "/");
	String fontsize = st.nextToken();
	fontsize = (st.nextToken()).substring(5);
	String fn = st.nextToken();
	int	k =	-1;
	for	(boolean loop =	true; loop;) {
		if (fontsizes[++k].equals(fontsize)) loop=false;
		if (k==4) loop=false;
	}
	//System.out.println(fontsizes[k]);
	if (imageSources[k] == null) { 			
		imageSources[k]=getBigImage(appletB, beanB,  "Fonts"+fontsize+".gif",  app);
		String desname = "Des"+fontsize+".gif";
		BufferedInputStream istream = null;
		// load font descriptors
		try {
			if (kLocalFonts) {
				InputStream ip = getClass().getResourceAsStream(desname);
				//System.out.println("ip");
				istream = new BufferedInputStream(getClass().getResourceAsStream(desname));
				//System.out.println("nlocal");
			} else {
				//Try loading external Font files in component/applet/bean specific manner
				if (!appletB & !beanB) {
					// component code
					istream = new BufferedInputStream((new URL(desname)).openStream());
				} else if (appletB) { 
					// applet code
					istream = new BufferedInputStream((new URL(app.getCodeBase(), desname)).openStream());
					//System.out.println("file");
				} else {
					// bean code
					// beanB==true
					try {
						istream = new BufferedInputStream(getClass().getResource(desname).openStream());
					} catch (Exception ex) { }
				}
			}
			ObjectInputStream p = new ObjectInputStream(istream);
			int len = (int)p.readInt();
			for (int i=0;i<len;i++) {
				String ft = (String)p.readObject();
				fontdesH.put(fontsize+ft,new Rectangle((Rectangle)p.readObject()));
			}
			istream.close();
		}	
		catch(Exception	exf)
		{
			System.out.println(exf.toString());
			imageSources[k] = null;
		}
	}
	// crop and filter images
	Image image = null;
	if (imageSources[k]!= null) {
		Rectangle r = (Rectangle)(fontdesH.get(fontsize+fn));
		image = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(
            new FilteredImageSource(imageSources[k],
			new CropImageFilter(r.x,r.y,r.width,r.height )), new ColorMaskFilter(g.getColor())));
  }
  return image;
} // end getImage


public ImageProducer getBigImage( boolean  appletB, boolean beanB, String filenameS, Applet   app) {
  ImageProducer imageSource=null;

  if(kLocalFonts) {
    imageSource = getLocalImageSource(filenameS);
  }
  if(imageSource==null) { //Fonts are not local
    kLocalFonts=false;  //don't attempt to load local fonts anymore

    //Try loading external Font files in component/applet/bean specific manner
    if (!appletB & !beanB) {
      // component code
      imageSource=Toolkit.getDefaultToolkit().getImage( filenameS ).getSource();
    } else if (appletB) { 
      // applet code
      imageSource= app.getImage(app.getCodeBase(), filenameS ).getSource();
    } else {
      // bean code
      // beanB==true
      try {
        URL url = getClass().getResource(  filenameS );
        imageSource = (ImageProducer) url.getContent();
      } catch (Exception ex) { }
    }
  }
  return imageSource;
} // end getImage

ImageProducer getLocalImageSource(String resourceName) {
  //Try loading images from jar
  ImageProducer source = null;
  try {
    // Next line assumes that Fonts are in the same jar file as SymbolLoader
    // Since resourceName doesn't start with a "/", resourceName is treated
    // as the relative path to the image file from the directory where
    // SymbolLoader.class is.
    InputStream imageStream = getClass().getResourceAsStream(resourceName);
    int numBytes = imageStream.available();//System.out.println(numBytes);
    byte[] imageBytes = new byte[numBytes];
	//System.out.println(numBytes);
    // Note: If all bytes are immediately available, the while loop just
    // executes once and could be replaced by the line:
    // imageStream.read(imageBytes,0,numBytes);
    // This may always be the case for the small Font images

    int alreadyRead = 0;
    int justRead = 0;
    while (justRead != -1) {
      justRead = imageStream.read(imageBytes,alreadyRead,numBytes);
      if(justRead != -1) { //didn't get all the bytes
        alreadyRead += justRead; //Total Read so far
        numBytes = imageStream.available(); //Amount left to read
        int totalBytes = alreadyRead + numBytes; //total bytes needed to
                                                 //store everything we know about
		//System.out.println("+"+numBytes);
        if((totalBytes) > imageBytes.length) {  //haven't yet allocated enough space
          byte[] tempImageBytes= (byte[]) imageBytes.clone();
          imageBytes = new byte[totalBytes];
          System.arraycopy(tempImageBytes, 0, imageBytes, 0, alreadyRead);
        }
      }
      if (numBytes == 0) break;
    }
    //Create an ImageProducer from the image bytes
    source = Toolkit.getDefaultToolkit().createImage(imageBytes).getSource();
  }
  catch (Exception io) {}
  return source;
} // end getLocalImageSource

} // end class SymbolLoader

/* 
// SymbolLoader for unpacked font files (slow speed)
class SymbolLoader {

public SymbolLoader() { }
// dummy constructor

//Fonts are included in HotEqn zip/jar file
private static boolean kLocalFonts=true;

public Image getImage( boolean  appletB, boolean beanB, String filenameS,
                Graphics g,      Applet   app) {
  ImageProducer imageSource=null;
  Image image=null;

  if(kLocalFonts) {
    imageSource = getLocalImageSource(filenameS);
  }
  if(imageSource==null) { //Fonts are not local
    kLocalFonts=false;  //don't attempt to load local fonts anymore

    //Try loading external Font files in component/applet/bean specific manner
    if (!appletB & !beanB) {
      // component code
      imageSource=Toolkit.getDefaultToolkit().getImage( filenameS ).getSource();
    } else if (appletB) { 
      // applet code
      imageSource= app.getImage(app.getCodeBase(), filenameS ).getSource();
    } else {
      // bean code
      // beanB==true
      try {
        URL url = getClass().getResource(  filenameS );
        imageSource = (ImageProducer) url.getContent();
      } catch (Exception ex) {
      }
    }
  }
  if(imageSource!=null) {
    image = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(
               imageSource, new ColorMaskFilter(g.getColor())));
  }
  return image;
} // end getImage

ImageProducer getLocalImageSource(String resourceName) {
  //Try loading images from jar
  ImageProducer source = null;
  try {
    // Next line assumes that Fonts are in the same jar file as SymbolLoader
    // Since resourceName doesn't start with a "/", resourceName is treated
    // as the relative path to the image file from the directory where
    // SymbolLoader.class is.
    InputStream imageStream = getClass().getResourceAsStream(resourceName);
    int numBytes = imageStream.available();//System.out.println(numBytes);
    byte[] imageBytes = new byte[numBytes];
//System.out.println(numBytes);
    // Note: If all bytes are immediately available, the while loop just
    // executes once and could be replaced by the line:
    // imageStream.read(imageBytes,0,numBytes);
    // This may always be the case for the small Font images

    int alreadyRead = 0;
    int justRead = 0;
    while (justRead != -1) {
      justRead = imageStream.read(imageBytes,alreadyRead,numBytes);
      if(justRead != -1) { //didn't get all the bytes
        alreadyRead += justRead; //Total Read so far
        numBytes = imageStream.available(); //Amount left to read
        int totalBytes = alreadyRead + numBytes; //total bytes needed to
                                                 //store everything we know about
//System.out.println("+"+numBytes);
        if((totalBytes) > imageBytes.length) {  //haven't yet allocated enough space
          byte[] tempImageBytes= (byte[]) imageBytes.clone();
          imageBytes = new byte[totalBytes];
          System.arraycopy(tempImageBytes, 0, imageBytes, 0, alreadyRead);
        }
      }
    }
    //Create an ImageProducer from the image bytes
    source = Toolkit.getDefaultToolkit().createImage(imageBytes).getSource();
  }
  catch (Exception io) {}
  return source;
} // end getLocalImageSource

} // end class SymbolLoader
*/

