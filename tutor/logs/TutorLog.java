package tutor.logs;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import tutor.business.TutorConstants;

/** logging 
 *  NOTE: first you need to create the "tutor/logs" folder
 * */
public class TutorLog implements TutorConstants {
	/** logging general tutor stuff */
	private static Logger logger = Logger.getLogger("tutor");
	private static FileHandler fh = null;
	
	static {
		try {
			fh = new FileHandler(PATH_TO_LOGS + File.separator + "tutor.log");
			fh.setFormatter(new SimpleFormatter());
		}
		catch (Exception e) {
			logger.severe(e.getMessage());
		}
	    // Send logger output to our FileHandler.
	    logger.addHandler(fh);
	    logger.setLevel(Level.ALL);
	}

	/** logging graph stuff */
	private static Logger logger_graph = Logger.getLogger("graph");
	private static FileHandler fh_graph = null;
	
	static {
		try {
			fh_graph = new FileHandler(PATH_TO_LOGS + File.separator + "tutor_graph.log");
			fh_graph.setFormatter(new SimpleFormatter());
		}
		catch (Exception e) {
			logger_graph.severe(e.getMessage());
		}
	    // Send logger output to our FileHandler.
		logger_graph.addHandler(fh_graph);
		logger_graph.setLevel(Level.ALL);
	}
	
	public static Logger getLogger() {
		return logger;
	}

	public static Logger getLogger(String name) {
		return Logger.getLogger(name);
	}
}
