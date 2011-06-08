package geogebra.sound;

import geogebra.main.Application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.swing.JFileChooser;

import org.jfugue.Pattern;
import org.jfugue.Player;


/**
 * Class for managing and playing Midi sound. 
 * 
 * @author G. Sturr 2010-9-18
 *
 */
public class MidiSound implements MetaEventListener  {

	private Application app;
	private Synthesizer synthesizer;
	private Instrument instruments[];
	private MidiChannel channels[];

	private Sequencer sequencer;
	private Sequence sequence;
	private long tickPosition;

	// Midi meta event
	public static final int END_OF_TRACK_MESSAGE = 47;

	/**
	 * Constructor
	 * @param app
	 */
	public MidiSound(Application app) {
		this.app = app;
	}


	//==================================================
	//  Initialization
	//==================================================

	public boolean initialize() {

		boolean success = true;

		try {
			sequencer = MidiSystem.getSequencer( );
			sequencer.addMetaEventListener(this);

			if (synthesizer == null) {
				if ((synthesizer = MidiSystem.getSynthesizer()) == null) {
					Application.debug("getSynthesizer() failed!");
					return false;
				}

				Soundbank sb = synthesizer.getDefaultSoundbank();
				if (sb != null) {
					instruments = synthesizer.getDefaultSoundbank().getInstruments();
					synthesizer.loadInstrument(instruments[0]);
				}

				channels = synthesizer.getChannels();    
			}
		}

		catch (MidiUnavailableException e) {
			e.printStackTrace(); 
			return false; 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
			return false; 
		}

		return success;
	}


	/** 
	 * Generates a list of available instruments in String form 
	 * */
	public String getInstrumentNames() {

		int size = Math.min(128,instruments.length);

		String list = "{";
		for(int i = 0; i < size; i++){
			list += "\""  + i + ": " + instruments[i].getName() + "\"";
			if(i!= size - 1)
				list += ",";   	
		}
		list += "}";

		return list;
	}


	/**
	 * Plays a midi sequence with default tempo 
	 */
	public void playSequence( Sequence sequence, long tickPosition ) {
		int tempo = 120;
		playSequence( sequence, tempo, tickPosition );
	}


	/**
	 * Plays a MIDI sequence
	 * 
	 * @param sequence
	 * @param tempo
	 * @param tickPosition
	 */
	public void playSequence( Sequence sequence, int tempo, long tickPosition ) {
		
		if(sequence == null) return;
		
		try{
			initialize();
			sequencer.open( );  
			synthesizer.open();

			// Specify the sequence, tempo, and tickPosition
			sequencer.setSequence(sequence);
			sequencer.setTempoInBPM(tempo);
			sequencer.setTickPosition(tickPosition);

			// Start playing 
			sequencer.start( );

		} catch (MidiUnavailableException e) {
		} catch (InvalidMidiDataException e) {
		}
	}




	public void pause(boolean doPause){
		
		if(sequencer == null) return;
		
		if(doPause){
			tickPosition = sequencer.getTickPosition();
			closeMidiSound();
		} else {
			playSequence(sequence,tickPosition);
		} 

	}

	public void stop() {
		closeMidiSound();
		sequence = null;
	}

	public void closeMidiSound() {
		
		if (synthesizer != null) {
			synthesizer.close();
		}
		instruments = null;
		channels = null;
		if(sequencer != null && sequencer.isOpen()){
			//sequencer.stop();
			sequencer.close();
		}
		
		System.gc();
	}


	/**
	 * Midi meta event listener that closes the sequencer at end of track.
	 */
	public void meta(MetaMessage event) {
		//System.out.println("midi sound event " + event.getType());
		if (event.getType() == END_OF_TRACK_MESSAGE) {
			closeMidiSound();
		}
	}




	//==================================================
	//  Play Single Midi Note
	//==================================================


	/** 
	 * Uses the Sequencer to play a single note in channel[0]  
	 * 
	 * */
	public void playSequenceNote(final int note, final double duration, final int instrument, final int velocity){

		try {
			tickPosition = 0;
			sequence = new Sequence(Sequence.SMPTE_24, 100);
			int ticks = (int) (duration *2400);

			Track track = sequence.createTrack( );  

			// Set the instrument on channel 0
			ShortMessage sm = new ShortMessage( );
			sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument, 0);
			track.add(new MidiEvent(sm, 0));

			// add the note to the track and play it
			addNote(track, 0, ticks, note, velocity);
			addNote(track, ticks, 640, 1, 0);
			playSequence(sequence, tickPosition);

		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}




	//==================================================
	//  Play Midi Sequence from File 
	//==================================================

	/*
	 * Uses the sequencer to play a Midi file.
	 * Currently only supports files with extension .mid
	 */
	public void playMidiFile(String filePath){

		try {

			if(filePath.equals("")){
				// launch a file chooser (just for testing)
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(app.getMainComponent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					filePath = fc.getSelectedFile().getAbsolutePath();
				}
			}	


			String ext =  filePath.substring(filePath.lastIndexOf(".") + 1);
			if(ext.equals("mid")){
				// Load new sequence from .mid file 
				tickPosition = 0;
				sequence = MidiSystem.getSequence(new File(filePath));
				playSequence(sequence,tickPosition);
			}

			else if(ext.equals("txt")){
				playJFugueFromFile(new File(filePath));
			}
			else if(ext.equals("gm")){
				loadSoundBank(new File(filePath));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}


	}


	private void loadSoundBank(File soundbankFile){

		try {

			synthesizer.close();
			Soundbank sb = MidiSystem.getSoundbank(soundbankFile);
			synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();

			System.out.println("soundbank added: " + sb);
						
			if (sb != null){
				System.out.println("soundbank supported: " + synthesizer.isSoundbankSupported(sb));
				boolean bInstrumentsLoaded = synthesizer.loadAllInstruments(sb);
				System.out.println("Instruments loaded: " + bInstrumentsLoaded);
			}

		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}





	/**
	 * Uses the sequencer to play a midi sequence parsed from an input string
	 */
	public void playSequenceFromJFugueString( String noteString, int instrument ) {
		
		initialize();
		try {
			sequencer.open( );
			synthesizer.open();
		} catch (MidiUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		noteString = "I[" + instrument + "] " + noteString;
		Player player = new Player(sequencer); 
		Pattern pattern = new Pattern(noteString); 
		player.play(pattern); 
		
	}
	
	

	// A convenience method to add a note to the track on channel 0
	public static void addNote(Track track, int startTick, int tickLength, int key, int velocity){

		try{
			ShortMessage on = new ShortMessage( );
			on.setMessage(ShortMessage.NOTE_ON,  0, key, velocity);
			ShortMessage off = new ShortMessage( );
			off.setMessage(ShortMessage.NOTE_OFF, 0, key, velocity);
			track.add(new MidiEvent(on, startTick));
			track.add(new MidiEvent(off, startTick + tickLength));

		} catch (InvalidMidiDataException e) {
		}

	}


	
	private void playJFugueFromFile(File file) throws IOException {

		String fileName = file.getName();
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;
			while ((text = reader.readLine()) != null) {
				contents.append(text);
			}
			System.out.println(contents.toString());
			this.playSequenceFromJFugueString(contents.toString(),0);
		} 

		catch (FileNotFoundException e) {
			e.printStackTrace();

		} finally {
			try {
				if (reader != null) reader.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	


} 
