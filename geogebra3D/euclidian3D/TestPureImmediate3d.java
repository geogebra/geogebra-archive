package geogebra3D.euclidian3D;


/*
ecrit par:Roswell
email:philgauthier_@hotmail.com

Tout d'abord, nous allons tudier la structure minimale d'un programme 
en java 3D, ce qui permettra de mettre en application le modle gnral 
vu plus haut (dans le tutorial) 
La premiere classe utilis est Canvas3D qui est drive de Canvas 
( donc mme utilisation ) qui nous sert juste  afficher du java3D. 

Les classes utiliss sont SimpleUniverse qui reprsente vore 
univers 3d qui va contenir tout votre scene. C'est en fait un univers qui 
a ses parametre prconfigur por simplifier le code. Nous n'avons 
jamais vue d'autre exemple d'univers dans le tutorial de sun aussi nous n'utiliserons 
que celui-ci. Nous ne configurons que la camera. Notre univers contiendra 
un seul regroupement d'objet qui correspond  la classe BranchGroup 
(BG), et ce BG ne contiendra qu'un seul objet ( classe Shape3D) qui est la 
classe ColorCube qui a une gomtrie et une apparence prdfinie. 
Pour associer notre objet ( Shape3D ), il faut crer un lien d'hritage 
entre le BG et la Shape3D, ce lien se fait grace  la mthode 
addChild()
*/

//Java standart API
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;
//Java 3d API
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Geometry;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.GraphicsContext3D;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.utils.geometry.ColorCube; 
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;


public class TestPureImmediate3d extends Frame implements WindowListener, Runnable
{	    
	
	
	Canvas3D canvas3D;
	private SimpleUniverse u = null;
	private GraphicsContext3D gc;
	


	private Transform3D cmt = new Transform3D();

	private Vector3f leftTrans, rightTrans;
	
	
	Appearance greenApp;
	Appearance redApp;
	
	
	
	private double angle;
	private Alpha rotAlpha = new Alpha(-1, 6000);
	 
	
	public TestPureImmediate3d(){
		super("TestPureImmediate3d");
	}

	
	public void init() {

		
        this.addWindowListener(this);
        setLayout(new BorderLayout());
        // 1ere tape cration du Canvas3d qui vas afficher votre univers virtuel avec une config prdfinie
        //Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        
        /*
        GraphicsConfigTemplate3D template = new GraphicsConfigTemplate3D();
        template.setStencilSize(8);        
        System.out.println("stencil size = "+template.getStencilSize());
        System.out.println("depth size = "+template.getDepthSize());
        
        GraphicsConfiguration gcfg =
            GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getBestConfiguration(template);
        canvas3D = new Canvas3D(gcfg);
 		*/
        
        canvas3D = new Canvas3D(GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getBestConfiguration(new GraphicsConfigTemplate3D()));
        
        
        add("Center", canvas3D);
        canvas3D.stopRenderer();
        
        
        // Create the universe and viewing branch
        u = new SimpleUniverse(canvas3D);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        u.getViewingPlatform().setNominalViewingTransform();
        
    	Background bg = new Background(1f,1f,1f);
    	bg.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));    
    	canvas3D.getGraphicsContext3D().setBackground(bg);
        
        
        
        
        
        


        
        
    }
	
	
	
	
	
	
	public void run() {

		

		gc = canvas3D.getGraphicsContext3D();


		
	    //gc.setBufferOverride(true);
	    
	    /*
	    Color3f lightColor = new Color3f(1, 1, 1);
	    Vector3f lightDir = new Vector3f(0, 0, -1);	    
	    DirectionalLight light = new DirectionalLight(new Color3f(1, 1, 1),  new Vector3f(0, 0, -1));
	    */
	    gc.addLight(new DirectionalLight(new Color3f(1, 1, 1),  new Vector3f(0, 0, -1)));
	    
	    AmbientLight ambientLightNode = new AmbientLight (new Color3f(1, 1, 1));
    	ambientLightNode.setInfluencingBounds (new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.POSITIVE_INFINITY));
    	//gc.addLight(ambientLightNode);
	    
	    
	    Color3f ambientColor = new Color3f(0,0,0);
	    Color3f emissiveColor = new Color3f(0, 0, 0);
	    Color3f diffuseColor = new Color3f(1, 0, 0);
	    Color3f specularColor = new Color3f(1, 1, 1);
	    
	    
	    Appearance redAppB = new Appearance();
	    Appearance greenAppB = new Appearance();
	    
	    redApp = new Appearance();
	    redApp.setMaterial(new Material(ambientColor, emissiveColor, diffuseColor, specularColor, 5));
	    redAppB.setMaterial(new Material(ambientColor, emissiveColor, diffuseColor, specularColor, 5));
	    
	    diffuseColor = new Color3f(0, 1, 0);
	    greenApp = new Appearance();
	    greenApp.setMaterial(new Material(ambientColor, emissiveColor, diffuseColor, specularColor, 5));
		greenAppB.setMaterial(new Material(ambientColor, emissiveColor, diffuseColor, specularColor, 5));	    
	    
		float transp = 0.5f;
	    redApp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST,transp));
	    redAppB.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST,transp));
	    greenApp.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST,transp));
	    greenAppB.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.FASTEST,transp));

	    
	    //culling for back faces
	    PolygonAttributes paB = new PolygonAttributes();
		paB.setCullFace(PolygonAttributes.CULL_FRONT );
		paB.setBackFaceNormalFlip(true);
		
	    redAppB.setPolygonAttributes(paB);
	    greenAppB.setPolygonAttributes(paB);
    		    	

	    
	    
	    //parties transparentes
	    RenderingAttributes raRG = new RenderingAttributes();	    	   
	    raRG.setDepthBufferWriteEnable(false); //don't write zbuffer	    
	    redApp.setRenderingAttributes(raRG);
	    greenApp.setRenderingAttributes(raRG);
	    
	    
	    
	    //parties cachantes
	    RenderingAttributes raZB = new RenderingAttributes();
	    raZB.setRasterOpEnable(true);
	    raZB.setRasterOp(RenderingAttributes.ROP_NOOP); //don't draw it
	    Appearance zApp = new Appearance();
	    zApp.setRenderingAttributes(raZB);
	    

	    //parties cachées
	    diffuseColor = new Color3f(0, 0, 1);
	    Appearance hiddenApp = new Appearance();
	    hiddenApp.setMaterial(new Material(ambientColor, emissiveColor, diffuseColor, specularColor, 5));
	    
	    
	    
	    //parties non cachées
	    diffuseColor = new Color3f(0, 0, 1);
	    Appearance notHiddenApp = new Appearance();
	    notHiddenApp.setMaterial(new Material(ambientColor, emissiveColor, diffuseColor, specularColor, 5));

	    
    	

	    
	    

	    // Set up geometry
	    
	    //parties transparentes
	    Cone leftCone = new Cone(0.4f, 0.6f);	    
	    Cone rightCone = new Cone(0.4f, 0.6f);
	    
	    
	    Geometry transparentGeom1 = leftCone.getShape(Cone.BODY).getGeometry();
	    Geometry transparentGeom2 = rightCone.getShape(Cone.BODY).getGeometry();
	    
	    
	    
	    //parties cachées
	    float rayon = 0.02f;
	    float longueur = 2f;
	    //Cylinder hiddenCyl = new Cylinder(rayon,longueur);

	    //Geometry hiddenGeom = createPointilles(rayon*0.99f,longueur,8,0.07f);
	    Geometry hiddenGeom = createCylinder(rayon,longueur,8,0.08f);
	    
	    //parties non cachées
	    /*
	    Cylinder notHiddenCyl = new Cylinder(rayon,longueur);
	    Geometry notHiddenGeom1 = notHiddenCyl.getShape(Cylinder.BODY).getGeometry();	    
	    Geometry notHiddenGeom2 = notHiddenCyl.getShape(Cylinder.BOTTOM).getGeometry();
	    Geometry notHiddenGeom3 = notHiddenCyl.getShape(Cylinder.TOP).getGeometry();
	    */
	    Geometry notHiddenGeom = createCylinder(rayon,longueur,8,longueur);
	    
	    
	    //transformations
	    leftTrans = new Vector3f(-0.2f, 0, 0);
	    rightTrans = new Vector3f(0.2f, 0, 0);
	    Vector3f tubeTrans = new Vector3f(0, -0.15f, 0.f);
	    
	    
	    
	    
	    while (true) {
	    	angle = rotAlpha.value() * 2.0 * Math.PI;
	        gc.setBufferOverride(true);
	    	
	    	
	    	gc.clear();

	        
	    	
	    	//parties cachées
	    	//Transform3D t2;
	    	//cmt.rotZ(Math.PI/2); t2 = new Transform3D(); t2.rotX(angle); cmt.mul(t2);
	    	cmt.rotY(angle);	    	
	    	cmt.setTranslation(tubeTrans);
	    	gc.setModelTransform(cmt);
	    	gc.setAppearance(hiddenApp);
	    	gc.draw(hiddenGeom);
	    	
	    	
	    	
	    	//parties transparentes
	    	cmt.setIdentity();
	    	
	    	//back faces
	        cmt.setTranslation(leftTrans);
	        gc.setModelTransform(cmt);
	        gc.setAppearance(redAppB);gc.draw(transparentGeom1);	        

	        cmt.setTranslation(rightTrans);
	        gc.setModelTransform(cmt);
	        gc.setAppearance(greenAppB);gc.draw(transparentGeom2);
	        
	    	//front faces
	        cmt.setTranslation(leftTrans);
	        gc.setModelTransform(cmt);
	        gc.setAppearance(redApp);gc.draw(transparentGeom1);	        

	        cmt.setTranslation(rightTrans);
	        gc.setModelTransform(cmt);
	        gc.setAppearance(greenApp);gc.draw(transparentGeom2);
	    	
	        
	    	
	        //parties cachantes
	        //Shape3D hiddingShape;
	        
	        cmt.setTranslation(leftTrans);
	        gc.setModelTransform(cmt);
	        gc.setAppearance(zApp);gc.draw(transparentGeom1);

	        cmt.setTranslation(rightTrans);
	        gc.setModelTransform(cmt);
	        gc.setAppearance(zApp);gc.draw(transparentGeom2);

	        
	        
	        
	        
	        
	        
	        
	    	
	    	
	    	//parties non cachées
	    	//cmt.rotZ(Math.PI/2); t2 = new Transform3D(); t2.rotX(angle); cmt.mul(t2);
	    	cmt.rotY(angle);		    	
	    	cmt.setTranslation(tubeTrans);
	    	//cmt.setIdentity();cmt.setTranslation(new Vector3f(0,-0.5f,0.5f));
	    	gc.setModelTransform(cmt);
	    	gc.setAppearance(notHiddenApp);
	    	//gc.draw(notHiddenGeom1);
	    	//gc.draw(notHiddenGeom2);gc.draw(notHiddenGeom3);
	    	gc.draw(notHiddenGeom);

	        
	        canvas3D.swap();
	    	Thread.yield();
	    }


	}
	
	
	
	public Geometry createCylinder(float radius, float length, int nbFaces, float pointLength){
		
		int nbPointilles = (int) (length/pointLength)/2 + ((int) (length/pointLength) %2);
		QuadArray ret = new QuadArray(nbFaces*nbPointilles*4,QuadArray.COORDINATES|QuadArray.NORMALS);
		
		int index = 0;
		
		float[] c = new float[nbFaces+1]; 
		float[] s = new float[nbFaces+1];
		Vector3f[] v = new Vector3f[nbFaces]; //normal vectors
		
		int i;
		for (i=0; i<=nbFaces; i++){
			c[i] = (float) (Math.cos(i*2*Math.PI/nbFaces));
			s[i] = (float) (Math.sin(i*2*Math.PI/nbFaces));
		}
		
		for (i=0; i<nbFaces; i++){
			v[i] = new Vector3f(0f,c[i],s[i]);
		}
		
		for (i=0; i<=nbFaces; i++){
			c[i] *= radius;
			s[i] *= radius;
		}
		
		
		
		index = 0;
		float x = -length/2;
		float x2;
		for (int j=0; j<nbPointilles; j++){
			//System.out.println("y= "+y);
			for (i=0; i<nbFaces; i++){
				x2=x+pointLength;
				if (x2>length) x2=length;
				ret.setCoordinate(index,new Point3f(x2,c[i],s[i])); ret.setNormal(index,v[i]); index++;
				ret.setCoordinate(index,new Point3f(x,c[i],s[i])); ret.setNormal(index,v[i]); index++;
				ret.setCoordinate(index,new Point3f(x,c[i+1],s[i+1])); ret.setNormal(index,v[(i+1)%nbFaces]); index++;
				ret.setCoordinate(index,new Point3f(x2,c[i+1],s[i+1])); ret.setNormal(index,v[(i+1)%nbFaces]); index++;
			}
			x+=2*pointLength;
		}
		
		

		
		
		
		
		return ret;
		
		
	}
	
	
    
    public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
	
	public void windowClosing(WindowEvent e)
	{
		System.exit(1);
	}
    	
	public static void main(String args[])
	{
		TestPureImmediate3d myApp=new TestPureImmediate3d();
		myApp.init();
		(new Thread(myApp)).start();
		myApp.setSize(600,600);
		myApp.setVisible(true);
		
	}
	
}