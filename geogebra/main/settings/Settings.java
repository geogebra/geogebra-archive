package geogebra.main.settings;

/**
 * Class which contains references to all settings of the application.
 * 
 * To add new setting containers to this class perform the following steps:
 *  1. Add attributes and getters (no setters allowed!)
 *  2. Init in constructor 
 *  3. Modify beginBatch() and endBatch()
 * 
 * @author Florian Sonner
 */
public class Settings {
	private final EuclidianSettings[] euclidianSettings;
	
	private final AlgebraSettings algebraSettings;
	
	private final SpreadsheetSettings spreadsheetSettings;
	
	private final ConstructionProtocolSettings consProtSettings;
	
	private final LayoutSettings layoutSettings;
	
	private final ApplicationSettings applicationSettings;
	
	private final KeyboardSettings keyboardSettings;
	
	private final CASSettings casSettings;
	
	/**
	 * Initialize settings using the constructors of the setting container classes.
	 */
	public Settings() {
		euclidianSettings = new EuclidianSettings[2];
		
		for(int i = 0; i < euclidianSettings.length; ++i) {
			euclidianSettings[i] = new EuclidianSettings();
		}
		
		algebraSettings = new AlgebraSettings();
		spreadsheetSettings = new SpreadsheetSettings();
		consProtSettings = new ConstructionProtocolSettings();
		layoutSettings = new LayoutSettings();
		applicationSettings = new ApplicationSettings();
		keyboardSettings = new KeyboardSettings();
		casSettings = new CASSettings();
	}
	
	/**
	 * Begin batch for all settings at once (helper). 
	 * 
	 * @remark Recommended to be used just for file loading, in other situations
	 * individual setting containers should be used to start batching.
	 */
	public void beginBatch() {
		for(EuclidianSettings settings : euclidianSettings) {
			settings.beginBatch();
		}
		
		algebraSettings.beginBatch();
		spreadsheetSettings.beginBatch();
		consProtSettings.beginBatch();
		layoutSettings.beginBatch();
		applicationSettings.beginBatch();
		keyboardSettings.beginBatch();
		casSettings.beginBatch();
	}
	
	/**
	 * End batch for all settings at once (helper). 
	 * 
	 * @remark Recommended to be used just for file loading, in other situations
	 * individual setting containers should be used to end batching.
	 */
	public void endBatch() {
		for(EuclidianSettings settings : euclidianSettings) {
			settings.endBatch();
		}
		
		algebraSettings.endBatch();
		spreadsheetSettings.endBatch();
		consProtSettings.endBatch();
		layoutSettings.endBatch();
		applicationSettings.endBatch();
		keyboardSettings.endBatch();
		casSettings.endBatch();
	}
	
	/** 
	 * @param number Number of euclidian view to return settings for. Starts with 1.
	 * @return Settings of euclidian view.
	 */
	public final EuclidianSettings getEuclidian(int number) {
		return euclidianSettings[number-1];
	}
	
	/**
	 * @return Settings of the algebra view.
	 */
	public final AlgebraSettings getAlgebra() {
		return algebraSettings;
	}
	
	/**
	 * @return Settings of the spreadsheet view.
	 */
	public final SpreadsheetSettings getSpreadsheet() {
		return spreadsheetSettings;
	}
	
	/**
	 * @return Settings of the construction protocol.
	 */
	public final ConstructionProtocolSettings getConstructionProtocol() {
		return consProtSettings;
	}
	
	public final LayoutSettings getLayout() {
		return layoutSettings;
	}
	
	/**
	 * @return General settings of the application.
	 */
	public final ApplicationSettings getApplication() {
		return applicationSettings;
	}
	
	public final KeyboardSettings getKeyboard() {
		return keyboardSettings;
	}
	
	public final CASSettings getCasSettings() {
		return casSettings;
	}
}