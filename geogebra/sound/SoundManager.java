package geogebra.sound;

import geogebra.kernel.GeoFunction;
import geogebra.main.Application;

/**
 * Class to handle GeoGebra sound features. Calls to midi and streaming audio
 * methods are managed from here.
 * 
 * @author G. Sturr
 * 
 */
public class SoundManager {

	private Application app;
	private MidiSound midiSound;
	private FunctionSound functionSound;

	public SoundManager(Application app){
		this.app = app;
	}

	/**
	 * Retrieves field midiSound. Creates a new instance if none exists.
	 */
	private MidiSound getMidiSound() {
		if(midiSound == null)
			try {
				midiSound = new MidiSound(app);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return midiSound;
	}


	/**
	 * Retrieves field functionSound. Creates a new instance if none exists.
	 */
	private FunctionSound getFunctionSound() {
		if(functionSound == null)
			try {
				functionSound = new FunctionSound();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return functionSound;
	}


	public void playSequenceNote(final int note, final double duration, final int instrument, final int velocity){
		try {
			getMidiSound().playSequenceNote(note, duration, instrument, velocity);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void playMidiFile(String fileName){
		try {
			getMidiSound().playMidiFile(fileName);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}


	public void playSequenceFromString( String noteString, int instrument ) {
		try {
			getMidiSound().playSequenceFromString(noteString, instrument);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void playFunction(final GeoFunction f, final double min, final double max, final int sampleRate, final int bitDepth){
		try {
			getFunctionSound().playFunction(f, min, max, sampleRate, bitDepth);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void playFunction(final GeoFunction f, final double min, final double max){
		try {
			getFunctionSound().playFunction(f, min, max);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
