package com.BreakthroughGames.OrigamiWars;

import java.util.concurrent.locks.ReentrantLock;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Message;
import android.util.Log;

class Mic extends Thread {

    private static int READ_PER_SEC = 32;										// Times Mic buffer is read per second
    protected static final int WIND_STOP = 1;
    protected static final int WIND_SLOWING  = 2;
    protected static final int WIND_BLOWING = 4;
    private static final int WIND_TIME = 40;									// Once Blown how long wind gonna stay(1sec)
    private static final int BLOW_LEVEL = 90;									// Values above this considered blow
    private static final int SAMPLE_SIZE  = 20;									// Samples in advance to check for noise
    private static final int CALIBRATION_TIME = 3;								// 3 Secs
    protected static final int MIC_CALIBRATING = -2;
    protected static int eWindStatus = WIND_SLOWING;

    private long buffReadTime;
    private static long vMaxEMA;
    private AudioRecord recorder = null;
    private static short[] buffer = null;
    private float soundAmp, ampEMA = 0.0f;
    protected static boolean bBlowing = false;
    private int index = 0, buffSize, buffRead = 0;
    private static float arrEMA[] = {0,0,0,0,0,0,0,0,0};								// size 9, initialize as empty
    private static ReentrantLock lock = new ReentrantLock();
    private static boolean bCalibrate = true, bMicAvailable = true, bRecord = true;
    private static int iInstances = 0, noise = 0, calibCount = 0, iWindTimer = 0;		// Keep track, don't run more then one instances

    Mic(){ android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);  }
    protected static void calibrate() {  calibCount = 0;	  bCalibrate = true;  vMaxEMA = 0; 		SoundPlayer.setVolume(1.2f, 1.0f );	 Adventure.events.dispatch(Events.MIC_CALIBRATE); } // Short normal Calibration

    /************************************************************************************************************************
     *	METHOD -- Called when blow starts
     *************************************************************************************************************************/
    private void blowStart() {
        bBlowing = true;
        SoundPlayer.playSound(Sound.BLOWING, -0.5f, 50);
        eWindStatus = WIND_BLOWING;

        if(Values.bDebug) {
            Message msg = new Message();
            msg.what = WIND_BLOWING;
            msg.arg1 = (int) ampEMA;
            msg.arg2 = (int) vMaxEMA;
            ActMainMenu.handler.sendMessage(msg);
            Values.log("mic_command","BLOW START: "+(int)ampEMA+" >>>"  );
        }
    }
    /************************************************************************************************************************
     *	METHOD -- Called when blow End
     *************************************************************************************************************************/
    private void blowSlowingDown(int vTimer) {
        if(vTimer%10 == 0)
            SoundPlayer.sendCommand(Sound.SOUND_SET_VOLUME, Sound.BLOWING, 2*vTimer);

        eWindStatus = (vTimer < 30) ? WIND_SLOWING : WIND_BLOWING ;
    }
    /************************************************************************************************************************
     *	METHOD -- Called when blow End
     *************************************************************************************************************************/
    private void blowEnd() {
        bBlowing = false;
        eWindStatus = WIND_STOP;
        SoundPlayer.stopSound(Sound.BLOWING);
        Values.log("mic_command","<<<BLOW END:" );
    }
    /************************************************************************************************************************
     *	METHOD -- Method to read the buffer
     * @throws Exception
     *************************************************************************************************************************/
    private void readData() throws Exception {
        buffRead++;																// Keeps tracks of Buffer read speed
        soundAmp = 0;

        buffSize = recorder.read(buffer,0,buffer.length);						// Read Buffer Chunk
        if (buffSize == AudioRecord.ERROR_BAD_VALUE || buffSize == AudioRecord.ERROR_INVALID_OPERATION) {
            bRecord = false;
            Log.d("mic_data", "Mic.readData(): Error Reading Mic Data");
            throw new Exception("Mic.readData(): Error Reading Mic Data");
        }

        for(int i = SAMPLE_SIZE; i <= (buffSize - SAMPLE_SIZE); i+= SAMPLE_SIZE) {
            soundAmp += checkPattern(buffer, i, SAMPLE_SIZE );
            soundAmp /= ((float)i/(i+1));
        }
    }
    /************************************************************************************************************************
     *	METHOD -- Method to process buffer data
     *************************************************************************************************************************/
    private void processData() {
        int tBlowLvl = WIND_SLOWING;											// Temp to store the highest blow

        soundAmp /= 100;
        index = ++index % arrEMA.length;
        arrEMA[index] =  ampEMA = Math.round(getEMA(Values.clamp(soundAmp, 1, (ampEMA*2)+2), ampEMA));

        if(ampEMA > vMaxEMA) vMaxEMA = (long) ampEMA; 							// MaxEMA per second

        if (bBlowing  && ampEMA > (noise + BLOW_LEVEL))					tBlowLvl = WIND_BLOWING;
        else if(checkForBlow(arrEMA, index, 4, 4, BLOW_LEVEL + noise))	tBlowLvl = WIND_BLOWING;
        else									 						tBlowLvl = WIND_SLOWING;

        if(tBlowLvl != WIND_SLOWING) {
            iWindTimer = WIND_TIME;
            if(!bBlowing)	blowStart();
            SoundPlayer.sendCommand(Sound.SOUND_SET_VOLUME, Sound.BLOWING, 120);
        }
        else if(iWindTimer > 0)
            blowSlowingDown(iWindTimer--);

        if(iWindTimer == 1) blowEnd();

        if(bCalibrate){ 														// Calibrating Mic Noise level
            noise =  Math.round(vMaxEMA + (vMaxEMA/2)) ;
            if(noise > 40) noise = 40;											// Don't register too high noise level, other wise, blow wont be detected
            if(calibCount == CALIBRATION_TIME)
                bCalibrate = false;
        }
    }
    /************************************************************************************************************************
     *	METHOD -- Method to process buffer data
     *************************************************************************************************************************/
    private void checkBuffer() {
        if(System.currentTimeMillis() - buffReadTime > 1000 )	{				// Check if data from Recording buffer is read, quickly enough
            if(buffRead < READ_PER_SEC-1) {
                recorder.stop();
                recorder.startRecording();
                Values.log("mic","Recording too slow, Buffer Cleared. Read: "+buffRead+"/"+READ_PER_SEC);
            }
            buffRead = 0;
            if(bCalibrate) calibCount++;										// Calibration Counter
            buffReadTime = System.currentTimeMillis();
        }
    }
    /************************************************************************************************************************
     *	METHODS -- Checks for and increasing Value, blow pattern
     *************************************************************************************************************************/
    private boolean checkForBlow(float vArr[], int vIndex, int vMatch, int vSampleSize , int vThreshHold) {
        int count = 0 , lastIndex = 0;

        if(vArr[vIndex] >= vThreshHold )
            for(int i = 0  ; i < vSampleSize ; i++, vIndex--) {
                vIndex =    (vArr.length + vIndex) % vArr.length;
                lastIndex = (vArr.length + (vIndex-1)) % vArr.length;
                if(Math.abs(vArr[vIndex]) > Math.abs(vArr[lastIndex]) || (Math.abs(vArr[vIndex]) > vThreshHold))
                    count++;
            }
        return	count >= vMatch;
    }
    /************************************************************************************************************************
     *	METHOD -- Checks for a blow pattern, (N)Value repeated in (M)Samples
     *************************************************************************************************************************/
    private long checkPattern(short vArr[], int vIndexLast,  int vSampleSize) {
        long tTotal = 0;
        for(int i = vIndexLast-vSampleSize; i < vIndexLast; i+=2)
            tTotal += vArr[i];

        return Math.abs(tTotal/vSampleSize);
    }
    /************************************************************************************************************************
     *	METHOD -- Thread Main Method /  Run Method
     ************************************************************************************************************************/
    @Override public void run() {
        if(bMicAvailable){														// If last time mic init was successful
            try{
                initRecording();												// Init Recording, if error exit the method
                recorder.startRecording();										// Start Recording
                buffReadTime = System.currentTimeMillis();						// Keep track of reading, so Mic Buffer dont overflow
                while(bRecord) {
                    readData();
                    processData();
                    checkBuffer();
                }
            }
            catch(Throwable x) {
                bMicAvailable = false;
                android.util.Log.d("mic_data", "Mic.run(): Error reading Mic data", x);
                ActMainMenu.handler.sendEmptyMessage(ActMainMenu.MIC_INIT_ERROR);
            }
            finally {
                if(bMicAvailable){											// If mic was successfully initialized
                    recorder.stop();
                    recorder.release();
                    Log.d("mic_command", "Stop Recording: finally()");
                }
            }
        }
    }
    /************************************************************************************************************************
     *	METHOD -- Method to initialize variables for Mic recording
     * @throws Exception
     *************************************************************************************************************************/
    private void initRecording() throws Exception {
        buffSize = AudioRecord.getMinBufferSize(8000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        recorder = new AudioRecord(AudioSource.VOICE_RECOGNITION, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffSize*16);

        if(buffSize < 0 || recorder == null) {
            buffSize = AudioRecord.getMinBufferSize(16000,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
            recorder = new AudioRecord(AudioSource.VOICE_RECOGNITION, 16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, buffSize*16);
            Log.d("mic_command", "ERROR creating 8khz AudioRecord, trying 16khz..." );

            if(buffSize < 0 || recorder == null) {
                Log.d("mic_command", "ERROR creating 16khz AudioRecord..." );
                throw new Exception("Could not create buffer for mic, possibly sampling size not supported");
            } else if(buffer == null)
                buffer = new short[500];										// Set Buffer for 16Khz
        } else if(buffer == null)
            buffer = new short[250];											// Set buffer for 8Khz
    }
    /************************************************************************************************************************
     *	METHODS -- Interface methods to be called from outside, to start and stop recording
     *************************************************************************************************************************/
    protected static void startRecording() {
        lock.lock();
        if(iInstances == 0)	{												    // If there is no instance of this thread already running
            iInstances++;
            bRecord = true;
            Mic newMic = new Mic();
            newMic.start();
        }
        lock.unlock();
    }

    protected static void stopRecording() {
        if(iInstances > 0) {
            bRecord = false;
            iInstances--;
        }
    }


    private double getEMA(double vAmp, double vEMA) {
        return  (0.5 * vAmp) + (0.5 * vEMA);
        // return  EMA_FILTER * vAmp + (1.0 - EMA_FILTER) * vEMA;
    }

}
