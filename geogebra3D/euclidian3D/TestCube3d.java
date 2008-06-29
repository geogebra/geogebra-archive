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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.BorderLayout;
//Java 3d API
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.BranchGroup;
import com.sun.j3d.utils.geometry.ColorCube; 


public class TestCube3d extends Frame implements WindowListener
{	    
	public TestCube3d()
	{
        super("- un cube vue de face -");
        this.addWindowListener(this);
        setLayout(new BorderLayout());
        // 1ere tape cration du Canvas3d qui vas afficher votre univers virtuel avec une config prdfinie
        Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        add("Center", canvas3D);
        // 2eme tape on cre notre scene (regroupement d'objet)
        BranchGroup scene = createSceneGraph();
        // on les compile pour optimiser les calcules
        scene.compile();
        
        // 3eme tape on creer l'univer qui va contenir notre scene 3d
        // utilise simpleUniverse qui simplifie le code (il cre un environemment minimal simple)
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
        // on met le plan de projection en arriere par rapport a l'origine
        simpleU.getViewingPlatform().setNominalViewingTransform();
        // on place la scene dans l'univers simpleU
        simpleU.addBranchGraph(scene);
    }
	
	//cre un regroupement d'objet contenant un objet cube
	public BranchGroup createSceneGraph()
	{
		//on cre le Bg principal
		BranchGroup objRoot=new BranchGroup();
		
		// on creer un cube
		objRoot.addChild(new ColorCube(0.5));// de rayon 50 cm

		return objRoot;
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
		TestCube3d myApp=new TestCube3d();
		myApp.setSize(300,300);
		myApp.setVisible(true);
	}
	
}