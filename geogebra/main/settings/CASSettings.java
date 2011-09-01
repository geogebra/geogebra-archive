package geogebra.main.settings;


/**
 * Stores CAS specific settings
 * @author tom
 *
 */
public class CASSettings extends AbstractSettings {
	
	private long timeoutMillis;
	
	/**
	 * Changes the timeout value for the cas
	 * @param value new timeout value, in milliseconds
	 */
	public void setTimeoutMilliseconds(long value) {
		if (timeoutMillis !=  value) {
			timeoutMillis = value;
			settingChanged();
		}
	}
	
	/** 
	 * @return CAS timeout value in milliseconds.
	 */
	public long getTimeoutMilliseconds() {
		return timeoutMillis;
	}

}
