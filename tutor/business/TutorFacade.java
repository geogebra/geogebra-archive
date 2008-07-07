package tutor.business;

import geogebra.Application;
import geogebra.io.MyXMLio;
import geogebra.kernel.Construction;
import geogebra.kernel.GeoElement;
import geogebra.kernel.Kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import tutor.business.services.TutorConstructionService;
import tutor.business.services.TutorResultService;
import tutor.factory.GeoElementFactory;
import tutor.logs.TutorLog;
import tutor.model.TutorConstruction;
import tutor.model.TutorResult;
import tutor.model.TutorVertex;

/**
 * <p>concentrate all service methods for dealing with the view</p>
 * @author AlbertV
 *
 */
public class TutorFacade implements TutorConstants {
	/** logging*/
	private final Logger logger = TutorLog.getLogger();
	private static final Logger logger_graph = TutorLog.getLogger("graph");
	
	/** services */
	private TutorConstructionService tutorConstructionService;
	private TutorResultService tutorResultService;
	
	/** the user TutorConstruction (under construction) */
	private TutorConstruction actual_tutor_construction;
	
	/** to read ggb files */
	private MyXMLio xmlio;
	//TODO: read Strategies from DB
	private final String[] strategy_paths = getAllStrategies(); //getStrategies();

	
	/********** Constructors */
	
	public TutorFacade() {
		tutorConstructionService = new TutorConstructionService();
		tutorResultService = new TutorResultService();
		actual_tutor_construction = new TutorConstruction();
	}
	
	/********** Public Methods */
	
	/**
	 * given different paths where different strategies are, load them
	 * @param application
	 * @param strategy_paths
	 * @return
	 * @throws Exception 
	 * @throws FileNotFoundException
	 */
	public List loadStrategies(final Application application) throws FileNotFoundException, Exception {
		List strategies = new ArrayList();
		
		// a new Kernel to play
		Kernel kernel = new Kernel(application);
		kernel.setUndoActive(false);

		// load every strategy we have
		for(int i=0; i<strategy_paths.length; i++) {
			// path to the strategy file
			String ggbPath = strategy_paths[i];
			
			logger.entering(this.getClass().getName(), "*******************loadStrategies*******************", ggbPath);
			logger_graph.entering(this.getClass().getName(), "*******************loadStrategies*******************", ggbPath);
			
			TutorConstruction tutorConstruction = loadStrategyFromFile(kernel, ggbPath);
			tutorConstruction.setGgbPath(ggbPath);
			
			// useful excerpt of every strategy
			StringBuffer traca = new StringBuffer("total strategy points = " + tutorConstruction.getElement().getNumPoints());
			traca.append("\ntotal strategy lines = " + tutorConstruction.getElement().getNumLines());
			traca.append("\ntotal strategy AnglesEq90 = " + tutorConstruction.getElement().getNumAnglesEq90());
			traca.append("\ntotal strategy AnglesGt90 = " + tutorConstruction.getElement().getNumAnglesGt90());
			traca.append("\ntotal strategy AnglesLt90 = " + tutorConstruction.getElement().getNumAnglesLt90());
			logger.fine(traca.toString());
			logger_graph.fine(getGraphMessage(tutorConstruction));
			
			strategies.add(tutorConstruction);
		}
		return strategies;
	}
	
	/**
	 * dealing with add method from TutorView
	 */
	public String add(final GeoElement actual_element, final List tutor_construction_strategies) {
		
		// 1.adding a new element to the actual TutorConstruction		
		tutorConstructionService.addConstructionElement(actual_element, actual_tutor_construction);

		// 2.comparing the actual TutorConstruction with a list of TutorConstruction strategies and get the results
		return compare(tutor_construction_strategies); 
		
	}

	/**
	 * dealing with remove method from TutorView
	 */
	public String remove(final GeoElement actual_element, final List tutor_construction_strategies) {
		
		// 1.adding a new element to the actual TutorConstruction		
		tutorConstructionService.removeConstructionElement(actual_element, actual_tutor_construction);

		// 2.comparing the actual TutorConstruction with a list of TutorConstruction strategies and get the results
		return compare(tutor_construction_strategies); 
		
	}
	
	/********** Private Methods */
	
	/**
	 * given a GGB file, load the strategy in it
	 * @param kernel used to create a new Kernel
	 * @return TutorConstruction from file strategy
	 * @throws FileNotFoundException
	 * @throws Exception 
	 */
	private TutorConstruction loadStrategyFromFile(final Kernel kernel, final String strategy_path) throws FileNotFoundException, Exception {
		// file with strategy
		InputStream ggb = new FileInputStream(strategy_path);
		xmlio = new MyXMLio(kernel, kernel.getConstruction());
		xmlio.readZipFromInputStream(ggb, false);

		// the strategy file Construction
		Construction strategyConstruction = kernel.getConstruction();
		
		return tutorConstructionService.getTutorConstruction(strategyConstruction);
	}
	
	/**
	 * get the result of comparison for all the strategies, one by one, with the actual construction
	 * @param tutor_construction_strategies List of TutorConstruction strategies
	 * @return the message to the tutor
	 */
	private String compare(final List tutor_construction_strategies) {
		final TutorResult bestTutorResult = tutorResultService.getBestResult(tutor_construction_strategies, actual_tutor_construction);
	
		logger.fine("bestTutorResult :: " + bestTutorResult.getGgbPath());
		
		String graphMessage = getGraphMessage(actual_tutor_construction);
		
		// getting the results of the comparison with the closer strategy to the actual construction in a more human way
		return getTutorMessage(bestTutorResult);
	}

	private String getGraphMessage(TutorConstruction tutor_construction) {
		// just looking what's there at the end of graph operations
		if(tutor_construction.getGraph().getTutorVertices() != null) {
			final StringBuffer traca = new StringBuffer("Graph has " + tutor_construction.getGraph().getTutorVertices().size() + " Vertex");
			
			Iterator iterator = tutor_construction.getGraph().getTutorVertices().iterator();
			while(iterator.hasNext()) {
				TutorVertex tutorVertex = (TutorVertex) iterator.next();
				traca.append("\nVertex " + tutorVertex.getLabel() + " has " + tutorVertex.getNumAdjacentVertices() + " Adjacent Vertices with Labels ");
				final Iterator iterator2 = tutorVertex.getAdjacentVertices().iterator();
				while (iterator2.hasNext()) {
					TutorVertex vertex= (TutorVertex) iterator2.next();
					traca.append(vertex.getLabel() + ", ");
				}
			}
			logger_graph.fine(traca.toString());	
			return traca.toString();
		}
		return "";
	}

	/**
	 * TODO: i18n
	 * @param tutor_result the result of the comparison of the closer construction to the actual construction
	 * @return the message to return to the tutor view
	 */
	private String getTutorMessage(final TutorResult tutor_result) {
		// message with results
		StringBuffer tutor_message = new StringBuffer(Calendar.getInstance().getTime().toString());
		tutor_message.append("\nYour construction is now close to ");
		tutor_message.append(tutor_result.getGgbPath());
		// lines
		tutor_message.append("\n about lines: ");
		tutor_message.append(tutor_result.getLinesScore());
		// points
		tutor_message.append("\n about points: ");
		tutor_message.append(tutor_result.getPointsScore());
		// angles
		tutor_message.append("\n about angles: ");
		tutor_message.append(tutor_result.getAnglesScore());		
		
		logger.fine(tutor_message.toString());
		return tutor_message.toString();
	}
	
	/********** Utilities for testing time */

	/**
	 * create a square for test purposes
	 * @param application
	 * @return
	 */
	public TutorConstruction createSquareStrategy(final Application application) {
		Kernel kernel = new Kernel(application);
		kernel.setUndoActive(false);

		// square
		Construction strategyConstruction = GeoElementFactory.createSquareForTest(kernel.getConstruction());

		return tutorConstructionService.getTutorConstruction(strategyConstruction);
		
	}
	
	/**
	 * create a triangle for test purposes
	 * @param application
	 * @return
	 */
	public TutorConstruction createTriangleStrategy(final Application application) {
		Kernel kernel = new Kernel(application);
		kernel.setUndoActive(false);

		// triangle
		Construction strategyConstruction = GeoElementFactory.createTriangleForTest(kernel.getConstruction());
		
		return tutorConstructionService.getTutorConstruction(strategyConstruction);
		
	}
	
	/**
	 * get some strategy files for test purposes
	 * @return
	 */
	private final static String[] getStrategies() {
		//file with a triangle strategy
		final String strategyPath1 = ClassLoader.getSystemResource(PATH_TO_GGB + File.separator + "triangle1.ggb").getFile();
		final String strategyPath2 = ClassLoader.getSystemResource(PATH_TO_GGB + File.separator + "polygon_triangle1.ggb").getFile();
		final String strategyPath3 = ClassLoader.getSystemResource(PATH_TO_GGB + File.separator + "4p5l.ggb").getFile();
		final String strategyPath4 = ClassLoader.getSystemResource(PATH_TO_GGB + File.separator + "1p.ggb").getFile();
		final String strategyPath5 = ClassLoader.getSystemResource(PATH_TO_GGB + File.separator + "2p.ggb").getFile();
		final String strategyPath6 = ClassLoader.getSystemResource(PATH_TO_GGB + File.separator + "circle2p.ggb").getFile();

		final String[] strategyPaths = {strategyPath1, strategyPath2, strategyPath3, strategyPath4, strategyPath5, strategyPath6};
		return strategyPaths;
	}
	
	/**
	 * get all strategy files in the specified folder, for test purposes
	 * @return
	 * @throws IOException 
	 */
	private final static String[] getAllStrategies() {
		List strings = new ArrayList();
		final String path = ClassLoader.getSystemResource("").getFile() + PATH_TO_GGB;
		File folder = new File(PATH_TO_GGB);
		String[] files = folder.list();
		for(int i = 0; i<folder.list().length; i++) {
			String thefile = files[i].toString();
			if(isGeogebraFile(thefile)) {
				String thestring = path + File.separator + thefile;
				strings.add(thestring);
			}
		}
		return (String[]) strings.toArray(new String[]{});
	}
	
	private static boolean isGeogebraFile(String path) {
		int dot = path.lastIndexOf('.');
		String extension = path.substring(dot + 1);
		return Application.FILE_EXT_GEOGEBRA.equalsIgnoreCase(extension);
	}
}
