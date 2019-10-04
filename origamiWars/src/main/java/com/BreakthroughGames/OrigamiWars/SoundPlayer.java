package com.BreakthroughGames.OrigamiWars;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class SoundPlayer 
{
	private static int tIndex = 0;
	private static long curTime = 0;
	protected static Messenger mService = null;
	protected static boolean bBound = false;
	protected static  float iBGBaseVolume = 0.5f; 					// BG sound Slow when player dies or LevelComplets / new level starts
	private static final int SOUND_ID = 0;
	private static final int START_TIME = 1;
	private static int soundStatus =-1;
	private static final int SOUND_NEW = 2;
	private static final int SOUND_FULL = 3;
	private static final int SOUND_PLAYING = 1;
	private final static long CLIP = 1000000;
	private static long arrStreams[][] = new long[Sound.MAX_STREAMS][2];

	private static final String arrNameFromID[]=
		{
		"GUN_NORMAL","GUN_DOUBLE","GUN_MACHINE","LIGHTNING","BURN_HIT", "BURN_FALL", "SHOCK_WAVE", 
		"PLANE_ROLL", "CURSE", "MAGIC", "EGG_CRACK", "POWERUP", "LEVEL_STATS", "WIND1", "WIND2", 
		"Blowing...", "GAMEOVER","EXTRA_LIFE", "WEAPON_EMPTY", "LEVEL_COMPLETE", "ITEM_COLLECT",  "NEW_RECORD", "LOST_LIFE", 
		"CRUISE", "DRAG", "ALERT", "VOICE_LEFT", "VOICE_RIGHT", "VOICE_SWAVE", "HOW_TO_ROLL", "VOICE_WEAPON", "VOICE_BLOW","VOICE_CURSE","VOICE_EGGS","VOICE_COP","EmptySlot, error if displayed" 	
		}; 
/************************************************************************************************************************
*	METHOD -- Send message to Music service To play a sound Effect
************************************************************************************************************************/
	protected static void playSound(final int vSound)									{	playSound(vSound, 0, 0);	}
	protected static void playSound(final int vSound, final float vOverLap) 			{	playSound(vSound, vOverLap, 0);}
	protected static void playSound(final int vSound, final float vOverLap, int vVolume)
	{
		if(bBound)
			{
			if(vSound < Sound.MAX_SOUNDS )									// if its a Sound effect, check if its already playing
				{
				
				tIndex = getSoundIndex(vSound); 							// Sets sound status, returns index if sound is already running
				curTime = System.currentTimeMillis();
		
				switch(soundStatus)
					{
					case SOUND_PLAYING: 
						long soundTime = arrStreams[tIndex][START_TIME] + Sound.arrSounds[vSound][Sound.DURATION];
						if((curTime - soundTime)/1000f > vOverLap )
							{
							arrStreams[tIndex][SOUND_ID] = vSound;
							arrStreams[tIndex][START_TIME] = curTime;
							Message msg = Message.obtain(null, vSound, vVolume, 0);
							try	{ mService.send(msg); }catch(RemoteException e){e.printStackTrace();}
							}
					break;
					case SOUND_NEW:
						arrStreams[tIndex][SOUND_ID] = vSound;
						arrStreams[tIndex][START_TIME] = curTime;
						Message msg = Message.obtain(null, vSound, vVolume, 0);
						try	{ mService.send(msg); }catch(RemoteException e){e.printStackTrace();}
					break;
					case SOUND_FULL:
						Message msg2 = Message.obtain(null, vSound, vVolume, 0);
						try	{ mService.send(msg2); }catch(RemoteException e){e.printStackTrace();}
					break;
					}
				}
			else																// its BG music
				{
				Message msg = Message.obtain(null, vSound, vVolume, 0);
				try	{ mService.send(msg); }catch(RemoteException e){e.printStackTrace();}
				}
			}
		}
/************************************************************************************************************************
*	METHOD -- Send message to servie to stop a sound Effect, doesnt work with BG Music
************************************************************************************************************************/
	protected static void stopSound(final int vSound)
	{
		if(bBound)
			{
			if(vSound < Sound.MAX_SOUNDS )									// if its a Sound effect, check if its already playing
			
				{
				int tIndex = getSoundIndex(vSound);  							// If get sound index and set it to zero 
				if(tIndex != -1)
					{
					long tStartTime = arrStreams[tIndex][START_TIME];
					long tPlayTime  =   System.currentTimeMillis() - tStartTime;
					Values.log("music"," Stop  ("+tIndex+"): " + arrNameFromID[vSound] + " startTime = "+ tStartTime %CLIP+" Duration/Played = " +  (Sound.arrSounds[vSound][Sound.DURATION])%CLIP +"/"+tPlayTime );
					arrStreams[tIndex][START_TIME] = 0;
					}
				
				Message msg = Message.obtain(null, Sound.SOUND_STOP, vSound,0);
				try	{mService.send(msg);}catch(RemoteException e){e.printStackTrace();}
				}
			else
				{
				Message msg = Message.obtain(null, vSound, 0, 0);
				try	{ mService.send(msg); }catch(RemoteException e){e.printStackTrace();}
				}
			}	
	}
/************************************************************************************************************************
*	METHOD -- Set BG Music
************************************************************************************************************************/
	protected static void setVolume(float vMulty, float vBGBase)
	{	
		if(bBound)
			{
			if(vBGBase > 0.0f)		
				iBGBaseVolume = vBGBase;
			if(vMulty > 0.0f)
				{
				Message msg = Message.obtain(null, Sound.BG_SET_VOLUME, (int)(vMulty*100),(int)(iBGBaseVolume*100));
				try	{mService.send(msg);}catch(RemoteException e){e.printStackTrace();}
				}	
			}
	}
/************************************************************************************************************************
*	METHOD -- Send commands, like pause all or resume all sound effects
************************************************************************************************************************/
	protected static void sendCommand(int vCmnd)	{sendCommand(vCmnd,0,0);}
	protected static void sendCommand(int vCmnd, int vArg1, int vArg2)
	{
		if(bBound)
			{
			Message msg = Message.obtain(null, vCmnd, vArg1,vArg2);
			try	{mService.send(msg);}catch(RemoteException e){e.printStackTrace();}
			}		
	}

	private static int getSoundIndex(int vSound)					// gets index if a sound is played or empty slot index
	{
		int emptySlot = -1;
		long tempTime;
		soundStatus = SOUND_FULL;
		
		for(int i = 0; i < Sound.MAX_STREAMS; i++)
			if(arrStreams[i][SOUND_ID] == vSound)
				{ soundStatus = SOUND_PLAYING; return i;}
			else
				{
				tempTime =  arrStreams[i][START_TIME] + Sound.arrSounds[(int) arrStreams[i][SOUND_ID]][Sound.DURATION];
				if(tempTime < curTime)
					{ soundStatus = SOUND_NEW; emptySlot = i;}
				}

		return emptySlot;
	}

}
