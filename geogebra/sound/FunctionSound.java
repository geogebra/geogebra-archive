package geogebra.sound;

import geogebra.kernel.GeoFunction;

import java.io.*;
import javax.sound.sampled.*;

import java.net.*;
import java.applet.*;

/**
 * Class with methods for playing function-generated sounds.
 * @author G. Sturr
 *
 */
public final class FunctionSound {

	private static  AudioFormat af;
    private static SourceDataLine sdl;   
    private static final int DEFAULT_SAMPLE_RATE = 16000;
    private static final int DEFAULT_BIT_RATE = 8;
	private int bitDepth;
	private int sampleRate;

    
    /**
     * Constructs instance of FunctionSound
     * @throws Exception
     */
    public FunctionSound() throws Exception { 
    	
    	bitDepth = DEFAULT_BIT_RATE;
    	sampleRate = DEFAULT_SAMPLE_RATE;
		if(!initStreamingAudio(sampleRate, bitDepth)){
			throw new Exception("Cannot initialize streaming audio");
		}
    }

  /**
   * Initializes instances of AudioFormat and SourceDataLine 
   * used by this class.
   * @param sampleRate = 8000, 16000, 11025, 16000, 22050, or 44100
   * @param bitDepth = 8 or 16
   * @return
   */
    private boolean initStreamingAudio(int sampleRate, int bitDepth){
    	
    	if(sampleRate != 8000 && sampleRate != 16000 && sampleRate != 11025 
    			&& sampleRate != 22050 && sampleRate != 44100)
    		return false;
    	if(bitDepth != 8 && bitDepth != 16)
    		return false;
    	
    	boolean success = true;
    	this.sampleRate = sampleRate;
    	this.bitDepth = bitDepth;
    	
    	af = new AudioFormat(sampleRate, bitDepth,1,true,true);
		try {
			sdl = AudioSystem.getSourceDataLine(af);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			success = false;
		}
    	return success;
    }


    public void playFunction( GeoFunction f, double min, double max){
    	playFunction(f, min, max, DEFAULT_SAMPLE_RATE, DEFAULT_BIT_RATE);
    }

    public void playFunction(final GeoFunction f, final double min, final double max, final int sampleRate, final int bitDepth){

    	Thread myThread = new Thread( new Runnable(){
    		public void run(){
    			generateFunctionSound( f,  min,  max, sampleRate, bitDepth);
    		}
    	} );

    	myThread.start();

    }


	
	public void generateFunctionSound(GeoFunction f, double min, double max, int sampleRate, int bitDepth){

		if(sampleRate != DEFAULT_SAMPLE_RATE  || bitDepth != DEFAULT_BIT_RATE )
			initStreamingAudio(sampleRate, bitDepth);
		
		// set volume to 50%
		int volume = 50;  // volume: 0 (mute) to 100 (max)
		
		//time between samples
		double samplePeriod = 1.0 / sampleRate;

		// length of time to play sound measured in sample frames
		int durationInSamples = (int) Math.ceil((max-min) * sampleRate);

		byte[] buf;
		buf = new byte[1];

		try {

/*
			byte[] buf = new byte[(int) (sampleRate * (max-min))];
			double t = min;
			double tStep = (max-min)/buf.length;
			double rawValue;
			for (int i=0; i < buf.length; i++) {
				rawValue = f.evaluate(t);
				if(rawValue > 1) 
					rawValue = 1;
				else if(rawValue < -1) 
					rawValue = -1;        	 
				buf[i]=(byte)(rawValue*volume);
				t = t + tStep;
			}
			
	         // shape the front and back 10ms of the wave form
	         for (int i=0; i < sampleRate / 100.0 && i < buf.length / 2; i++) {
	             buf[i] = (byte)(buf[i] * i / (sampleRate / 100.0));
	             buf[buf.length-1-i] =
	              (byte)(buf[buf.length-1-i] * i / (sampleRate / 100.0));
	         } 
*/
			

			if(sdl.isOpen())
				closeSdl();
			
			sdl.open(af,sampleRate);
			sdl.start();
			sdl.write(buf,0,buf.length);

			for(double t = min; t < max; t = t + samplePeriod){				
				buf[0]=(byte)(f.evaluate(t)*volume);
				sdl.write(buf,0,1);
			}
			
			closeSdl();
			
		
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

	}

   

    /**
     * Close dataline
     */
    public static void closeSdl() {
        sdl.drain();
        sdl.stop();
        sdl.close();
    }
    
   


}


