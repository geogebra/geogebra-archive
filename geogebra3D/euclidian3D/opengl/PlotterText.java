package geogebra3D.euclidian3D.opengl;

import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.ParseException;

/*
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
*/

/**
 * Class that manages text rendering
 * 
 * @author matthieu
 *
 */
public class PlotterText {
	
	/** geometry manager */
	private Manager manager;
	

	
	
	/**
	 * common constructor
	 * @param manager
	 */
	public PlotterText(Manager manager){
		
		this.manager = manager;
		
	}
	
	
	/**
	 * draws a rectangle
	 * @param x
	 * @param y
	 * @param z
	 * @param width
	 * @param height
	 */
	public void rectangle(int x, int y, int z, int width, int height){
		
		GL2 gl = manager.getGL();
		
		gl.glBegin(GLlocal.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3i(x,y,z); 
		gl.glTexCoord2f(1, 0);
		gl.glVertex3i(x+width,y,z); 
		gl.glTexCoord2f(1, 1);
		gl.glVertex3i(x+width,y+height,z); 
		gl.glTexCoord2f(0, 1);
		gl.glVertex3i(x,y+height,z); 	
		gl.glEnd();
		
	}
	
	
	
	
}
