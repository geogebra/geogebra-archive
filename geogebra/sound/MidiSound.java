package geogebra.sound;

import geogebra.main.Application;

import java.io.File;
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
		System.out.println("midi sound event " + event.getType());
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
	public void playMidiFile(String fileName){

		try {
			tickPosition = 0;
			if(fileName.equals("")){
				// launch a file chooser (just for testing)
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(app.getMainComponent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					sequence = MidiSystem.getSequence(fc.getSelectedFile());
				}
			}else{
				// Load sequence from input file name
				sequence = MidiSystem.getSequence(new File(fileName));
			}

			playSequence(sequence,tickPosition);


		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}


	}



	//==================================================
	//  Play Midi Sequence from String 
	//
	// Adapted from: 
	// Java Examples in a Nutshell, 3rd Edition
	// example: PlayerPiano.java 
	// http://tim.oreilly.com/pub/a/onjava/excerpt/jenut3_ch17/index1.html 
	//==================================================

	/**
	 * Uses the sequencer to play a midi sequence parsed from an input string
	 */
	public void playSequenceFromString( String noteString, int instrument ) {

		int tempo = 120;
		char[ ] notes = noteString.toCharArray();

		// 16 ticks per quarter note. 
		try {
			tickPosition = 0;
			sequence = new Sequence(Sequence.PPQ, 16);

			// Add the specified notes to the track
			addTrack(sequence, instrument, tempo, notes);

			playSequence(sequence, tickPosition);

		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}

	}



	/**
	 * Offsets for octave changes. Offset amounts are added to the base midi
	 * values for the notes of A B C D E F G
	 * */
	static final int[] offsets = { -3, -1, 0, 2, 4, 5, 7  };


	/**
	 * This method parses the specified char[ ] of notes into a Track.
	 * The musical notation is the following:
	 * A-G:   A named note; Add b for flat and # for sharp.
	 * +:     Move up one octave. Persists.
	 * -:     Move down one octave.  Persists.
	 * /1:    Notes are whole notes.  Persists 'till changed
	 * /2:    Half notes
	 * /4:    Quarter notes
	 * /n:    N can also be 8, 16, 32, 64.
	 * s:     Toggle sustain pedal on or off (initially off)
	 * 
	 * >:     Louder.  Persists
	 * <:     Softer.  Persists
	 * .:     Rest. Length depends on current length setting
	 * Space: Play the previous note or notes; notes not separated by spaces
	 *        are played at the same time
	 */
	public static void addTrack(Sequence s, int instrument, int tempo, char[] notes) {

		int DAMPER_PEDAL = 64;
		int DAMPER_ON = 127;
		int DAMPER_OFF = 0;

		try{
			Track track = s.createTrack( );  // Begin with a new track

			// Set the instrument on channel 0
			ShortMessage sm = new ShortMessage( );
			sm.setMessage(ShortMessage.PROGRAM_CHANGE, 0, instrument, 0);
			track.add(new MidiEvent(sm, 0));

			int n = 0; // current character in notes[] array
			int t = 0; // time in ticks for the composition

			// These values persist and apply to all notes 'till changed
			int notelength = 16; // default to quarter notes
			int velocity = 127;   // default to max volume
			int basekey = 60;    // 60 is middle C. Adjusted up and down by octave
			boolean sustain = false;   // is the sustain pedal depressed?
			int numnotes = 0;    // How many notes in current chord?

			while(n < notes.length) {
				char c = notes[n++];

				if (c == '+') basekey += 12;        // increase octave
				else if (c == '-') basekey -= 12;   // decrease octave
				else if (c == '>') velocity += 16;  // increase volume;
				else if (c == '<') velocity -= 16;  // decrease volume;
				else if (c == '/') {
					char d = notes[n++];
					if (d == '2') notelength = 32;  // half note
					else if (d == '4') notelength = 16;  // quarter note
					else if (d == '8') notelength = 8;   // eighth note
					else if (d == '3' && notes[n++] == '2') notelength = 2;
					else if (d == '6' && notes[n++] == '4') notelength = 1;
					else if (d == '1') {
						if (n < notes.length && notes[n] == '6')
							notelength = 4;    // 1/16th note
						else notelength = 64;  // whole note
					}
				}
				else if (c == 's') {
					sustain = !sustain;
					// Change the sustain setting for channel 0
					ShortMessage m = new ShortMessage( );
					m.setMessage(ShortMessage.CONTROL_CHANGE, 0,
							DAMPER_PEDAL, sustain?DAMPER_ON:DAMPER_OFF);
					track.add(new MidiEvent(m, t));
				}
				else if (c >= 'A' && c <= 'G') {
					int key = basekey + offsets[c - 'A'];
					// sb = new StringBuilder();
					//sb.append(c);
					if (n < notes.length) {
						if (notes[n] == 'b' ) { // flat
							key--; 
							n++;
							//sb.append("b");
						}
						else if (notes[n] == '#') { // sharp
							key++;
							n++;
							//sb.append("#");
						}
					}
					//System.out.println("note:" + sb.toString() + "  key:" + key);
					addNote(track, t, notelength, key, velocity);
					numnotes++;
				}
				else if (c == ' ') {
					// Spaces separate groups of notes played at the same time.
					// But we ignore them unless they follow a note or notes.
					if (numnotes > 0) {
						t += notelength;
						numnotes = 0;
					}
				}
				else if (c == '.') { 
					// Rests are like spaces in that they force any previous
					// note to be output (since they are never part of chords)
					if (numnotes > 0) {
						t += notelength;
						numnotes = 0;
					}
					// Now add additional rest time
					t += notelength;
				}
			}

		} catch (InvalidMidiDataException e) {
		}


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




} 
