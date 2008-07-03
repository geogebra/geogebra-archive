package tutor.business;

/**
 * implementing this interface we have access to all this constants
 * @author AlbertV
 *
 */
public interface TutorConstants {
	
	/** TutorConstructionService and TutorOperateService*/
	int ADD = 0;
	int REMOVE = 1;
	
	/** TutorResultService */
	// number of descriptors used in the comparison to use in the average formula
	float NUM_DESCRIPTORS = 3;
	
	// "../tutor" if buildpath configured with, for example, geogebra/bin as default output folder
	// else "tutor"
//	String PATH_TO_RESOURCES = "../tutor";
	String PATH_TO_RESOURCES = "tutor";
	String PATH_TO_GGB = PATH_TO_RESOURCES + "/ggb";
	String PATH_TO_LOGS = PATH_TO_RESOURCES + "/logs";
}
