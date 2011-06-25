package geogebra3D.euclidian3D.opengl;

import geogebra.main.Application;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellatorCallback;

public class RendererTesselCallBack implements GLUtessellatorCallback
  {
    private GL2 gl;
    private GLU glu;

    public RendererTesselCallBack(GL2 gl, GLU glu)
    {
      this.gl = gl;
      this.glu = glu;
    }

    public void begin(int type)
    {
      gl.glBegin(type);
    }

    public void end()
    {
      gl.glEnd();
    }

    public void vertex(Object vertexData)
    {

      double[] pointer;
      if (vertexData instanceof double[])
      {
        pointer = (double[]) vertexData;
        //if (pointer.length == 6) gl.glColor3dv(pointer, 3);
        gl.glVertex3dv(pointer, 0);
      }
      
    	
    	

    }

    public void vertexData(Object vertexData, Object polygonData)
    {
    }

    /*
     * combineCallback is used to create a new vertex when edges intersect.
     * coordinate location is trivial to calculate, but weight[4] may be used to
     * average color, normal, or texture coordinate data. In this program, color
     * is weighted.
     */
    public void combine(double[] coords, Object[] data, //
        float[] weight, Object[] outData)
    {
      //double[] vertex = new double[6];
      //int i;
      
      double[] vertex = new double[3];

      vertex[0] = coords[0];
      vertex[1] = coords[1];
      vertex[2] = coords[2];
      /*
      for (i = 3; i < 6; i++)
        vertex[i] = weight[0] //
                    * ((double[]) data[0])[i] + weight[1]
                    * ((double[]) data[1])[i] + weight[2]
                    * ((double[]) data[2])[i] + weight[3]
                    * ((double[]) data[3])[i];
      */
      outData[0] = vertex;
    }

    public void combineData(double[] coords, Object[] data, //
        float[] weight, Object[] outData, Object polygonData)
    {
    }

    public void error(int errnum)
    {
      String estring;

      estring = glu.gluErrorString(errnum);
      Application.debug("Tessellation Error: " + estring);
      //System.exit(0);
    }

    public void beginData(int type, Object polygonData)
    {
    }

    public void endData(Object polygonData)
    {
    }

    public void edgeFlag(boolean boundaryEdge)
    {
    }

    public void edgeFlagData(boolean boundaryEdge, Object polygonData)
    {
    }

    public void errorData(int errnum, Object polygonData)
    {
    }
  }

