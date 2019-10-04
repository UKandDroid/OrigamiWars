package com.BreakthroughGames.OrigamiWars;

import java.io.IOException;

import com.BreakthroughGames.OrigamiWars.R;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class Sound extends Service {                                   // Class to Handle Music, run as a service

    private static int sCount = 0;
    static private SoundPool sPool;
    private static float iCurVolume;
    private static int curBGMusic = -1;                                // Current BG Music running
    private final float MILLI_SEC = 1000;
    private static MediaPlayer player = null;
    protected static final int MAX_STREAMS = 9;                        // Max SoundStreams

    private static final int SPOOL_ID = 0;
    private static final int PRIORITY = 1;
    private static final int STREAM_ID = 1;
    protected static final int DURATION = 2;

    private final static float iDefVolume = 0.6f;
    private static float iBaseVolume = iDefVolume;
    final Messenger mMsn = new Messenger(new IncomingHandler());

    static final int GUN_NORMAL = 0;
    static final int GUN_DOUBLE = 1;
    static final int GUN_MACHINE = 2;
    static final int LIGHTNING = 3;
    static final int ENEMY_HIT = 4;
    static final int ENEMY_BURN = 5;
    static final int SHOCK_WAVE = 6;
    static final int PLANE_ROLL = 7;
    static final int CURSE = 8;
    static final int MAGIC = 9;
    static final int POKE = 10;
    static final int POWERUP = 11;
    static final int LEVEL_STATS = 12;
    static final int WIND1 = 13;
    static final int WIND2 = 14;
    static final int BLOWING = 15;
    static final int GAMEOVER = 16;
    static final int EXTRA_LIFE = 17;
    static final int WEAPON_EMPTY = 18;
    static final int LEVEL_COMPLETE = 19;
    static final int ITEM_COLLECT = 20;
    static final int NEW_RECORD = 21;
    static final int HIT_ENEMY = 22;
    // static final int CRUISE			= 23;
    static final int DRAG = 24;
    static final int ALERT = 25;
    static final int VOICE_MOVE = 26;
    static final int VOICE_FIRE = 27;
    static final int VOICE_SWAVE = 28;
    static final int VOICE_ROLL = 29;
    static final int VOICE_WEAPON = 30;
    static final int VOICE_BLOW = 31;
    static final int VOICE_SCROLLS = 32;
    static final int VOICE_CURSED = 33;
    static final int VOICE_COPOWER = 34;
    static final int WIND_MIC_CALIB = 35;
    static final int HIT_SHOT = 36;
    static final int SHOT_SINGLE = 37;
    static final int SHOT_DOUBLE = 38;
    static final int SHOT_FIRE = 39;
    static final int PLANE_BURN = 40;
    static final int SCORE_ADD = 41;
    static final int MAX_SOUNDS = 42;                                        // Total Sound Effects, dosnt include BG Music

    public static final int BG_MUSIC_LEVEL1 = 60;
    public static final int BG_MUSIC_LEVEL2 = 61;
    public static final int BG_MUSIC_LEVEL3 = 62;
    public static final int BG_MUSIC_LEVEL4 = 63;
    public static final int BG_MUSIC_LEVEL5 = 64;
    public static final int BG_MUSIC_INTRO = 65;

    static final int SOUND_STOP_ALL = 80;                                        // Commands
    static final int SOUND_RESUME_ALL = 81;
    static final int SOUND_PAUSE_ALL = 82;
    static final int SOUND_STOP = 83;
    static final int SOUND_SET_VOLUME = 84;
    static final int BG_MUSIC_PLAY = 85;
    static final int BG_MUSIC_STOP = 86;
    static final int BG_MUSIC_PAUSE = 87;
    static final int BG_SET_VOLUME = 88;

    protected static long arrSounds[][] = new long[MAX_SOUNDS][3];                // Stores music files ID for sound pool
    private static int arrStreams[][] = new int[MAX_STREAMS][2];                // Store Playing music stream IDs 0 - SoundID, 1 - StreamID

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {
        player.stop();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 1);
        //        Res ID		   		SoundID 		Priority,  	Duration
        loadSound(R.raw.gun_normal, GUN_NORMAL, 1, 0.14f);
        loadSound(R.raw.gun_double, GUN_DOUBLE, 1, 0.2f);
        loadSound(R.raw.gun_machine, GUN_MACHINE, 1, 0.25f);
        loadSound(R.raw.lightning, LIGHTNING, 1, 0.45f);
        loadSound(R.raw.enemy_hit_shot, ENEMY_HIT, 1, 0.3f);
        loadSound(R.raw.plane_hit_shot, HIT_SHOT, 3, 0.3f);
        loadSound(R.raw.enemy_burn, ENEMY_BURN, 1, 1.0f);
        loadSound(R.raw.shockwave, SHOCK_WAVE, 2, 0.6f);
        loadSound(R.raw.plane_roll, PLANE_ROLL, 2, 0.5f);
        loadSound(R.raw.curse, CURSE, 2, 0.8f);
        loadSound(R.raw.magic, MAGIC, 2, 1.8f);
        loadSound(R.raw.poke, POKE, 2, 0.15f);
        loadSound(R.raw.powerup, POWERUP, 2, 0.5f);
        loadSound(R.raw.level_stats, LEVEL_STATS, 2, 0.1f);
        loadSound(R.raw.wind1, WIND1, 3, 4.7f);
        loadSound(R.raw.wind2, WIND2, 3, 4.7f);
        loadSound(R.raw.wind3, BLOWING, 4, 4.7f);
        loadSound(R.raw.gameover, GAMEOVER, 3, 2.5f);
        loadSound(R.raw.extra_life, EXTRA_LIFE, 2, 1.6f);
        loadSound(R.raw.weaponempty, WEAPON_EMPTY, 2, 0.6f);
        loadSound(R.raw.levelcomplete, LEVEL_COMPLETE, 2, 3.0f);
        loadSound(R.raw.item_collect, ITEM_COLLECT, 2, 0.25f);
        loadSound(R.raw.newrecord, NEW_RECORD, 2, 2.1f);
        loadSound(R.raw.enemy_collision, HIT_ENEMY, 2, 0.22f);
        loadSound(R.raw.drag, DRAG, 2, 0.4f);
        loadSound(R.raw.alert, ALERT, 3, 1.5f);
        loadSound(R.raw.voice_left, VOICE_MOVE, 3, 2.8f);
        loadSound(R.raw.voice_right, VOICE_FIRE, 3, 1.4f);
        loadSound(R.raw.voice_swave, VOICE_SWAVE, 3, 3.3f);
        loadSound(R.raw.voice_roll, VOICE_ROLL, 3, 2.4f);
        loadSound(R.raw.voice_weapon, VOICE_WEAPON, 3, 3.7f);
        loadSound(R.raw.voice_blow, VOICE_BLOW, 3, 3.3f);
        loadSound(R.raw.voice_scrolls, VOICE_SCROLLS, 3, 2.2f);
        loadSound(R.raw.voice_curse, VOICE_CURSED, 3, 4.15f);
        loadSound(R.raw.voice_cop, VOICE_COPOWER, 3, 4.3f);
        loadSound(R.raw.mic_calibrate, WIND_MIC_CALIB, 3, 1.8f);
        loadSound(R.raw.shot_single, SHOT_SINGLE, 1, 0.16f);
        loadSound(R.raw.shot_double, SHOT_DOUBLE, 1, 0.26f);
        loadSound(R.raw.shot_fire, SHOT_FIRE, 1, 0.47f);
        loadSound(R.raw.plane_burn, PLANE_BURN, 1, 0.95f);
        loadSound(R.raw.score_add, SCORE_ADD, 1, 0.28f);

    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mMsn.getBinder();
    }

    private void loadSound(int vSound, int vID, int vPriority, float vDuration) {
        arrSounds[vID][PRIORITY] = vPriority;
        arrSounds[vID][DURATION] = (long) (vDuration * MILLI_SEC);
        arrSounds[vID][SPOOL_ID] = sPool.load(this, vSound, vPriority);
    }

    /************************************************************************************************************************
     *	METHOD -- BG Music Controls
     ************************************************************************************************************************/
    private static void BGPause() {
        if (player != null) player.pause();
    }

    private static void BGStop() {
        if (player != null) player.stop();
    }

    private static void BGPlay() {
        try {
            player.start();
            player.setVolume(iCurVolume, iCurVolume);
        } catch (Exception e) {
            e.printStackTrace();
            player.stop();
        }
    }

    private static void BGSet(int fileRes) {
        if (fileRes != curBGMusic)                                                        // If file is not already loaded
        {
            if (player == null) player = new MediaPlayer();
            try {
                player.reset();
                AssetFileDescriptor afd = Game.refContext.getResources().openRawResourceFd(fileRes);
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                player.prepare();
                afd.close();
                curBGMusic = fileRes;                                                    // Set as current BG music
            } catch (IOException e) {
                e.printStackTrace();
            }

            player.setLooping(true);                                // Set looping true
            player.setVolume(60, 60);                    // Set left and right volume
        }
    }

    /************************************************************************************************************************
     *	METHOD -- Sound Effects Controls
     ************************************************************************************************************************/
    private static void setVolume(int vMult, int vBaseVol) {
        iBaseVolume = (vBaseVol / 100.0f) * iDefVolume;                                        // Base Volume for BG sound
        iCurVolume = (iBaseVolume * (vMult / 100.0f));                                        // Current Volume for BG sound
        player.setVolume(iCurVolume, iCurVolume);

        Values.log("sound", "Sound Base/Cur = " + iBaseVolume + " / " + iCurVolume);

    }


    private static void soundPlay(int vSound, float vVolume) {
        int vSPoolID = (int) arrSounds[vSound][SPOOL_ID];
        int vPriority = (int) arrSounds[vSound][PRIORITY];

        vVolume = (vVolume == 0) ? iCurVolume : iCurVolume * (vVolume / 100);                // Volume x BaseVolume
        int tStreamID = sPool.play(vSPoolID, vVolume, vVolume, vPriority, 0, 1f);        // sound ID, L&R speaker, Priority, loop , Speed
        if (tStreamID != 0) {
            arrStreams[sCount][SPOOL_ID] = vSPoolID;                                    // Sound played and its corresponding streamID
            arrStreams[sCount][STREAM_ID] = tStreamID;
            sCount = ++sCount % MAX_STREAMS;
        }
    }

    private static void soundStop(int vSound) {
        int temp = isSoundPlaying(vSound);
        if (temp != -1)                                                                    // if stream index is found for the sound
        {
            Values.log("music", "stopSound StreamID = " + arrStreams[temp][STREAM_ID]);
            sPool.stop(arrStreams[temp][STREAM_ID]);
            arrStreams[temp][SPOOL_ID] = -1;
        }
    }

    private static void soundCommand(int vCommand, int vSound, int vExtra) {
        switch (vCommand) {
            case SOUND_STOP:
                soundStop(vSound);
                break;
            case SOUND_PAUSE_ALL:
                sPool.autoPause();
                break;
            case SOUND_RESUME_ALL:
                sPool.autoResume();
                break;
            case SOUND_STOP_ALL:
                for (int i = 0; i < MAX_STREAMS; i++) sPool.stop(arrStreams[i][STREAM_ID]);
                break;
            case SOUND_SET_VOLUME:
                int temp = isSoundPlaying(vSound);
                if (temp != -1)                                                            // if stream index is found for the sound
                    sPool.setVolume(arrStreams[temp][STREAM_ID], iCurVolume * (vExtra / 100f), iCurVolume * (vExtra / 100f));
                break;
        }

    }

    /************************************************************************************************************************
     *	METHOD -- Find if a sound effect is already playing, by Travesing reverse in loop
     ************************************************************************************************************************/
    static int isSoundPlaying(int vID) {
        int temp = sCount;
        for (int i = 0; i < MAX_STREAMS; i++, temp--) {
            if (temp < 0) temp = MAX_STREAMS - 1;
            if (arrStreams[temp][SPOOL_ID] == vID) return temp;
        }
        return -1;
    }

    /************************************************************************************************************************
     *	METHOD -- Called by system when service is started by StartService()
     ************************************************************************************************************************/
    public int onStartCommand(Intent inte, int flags, int startId) {
        BGSet(R.raw.intro);                                        // Set a music file
        //	BGPlay();												// Play it
        setVolume(100, 100);                                        // Set Sound to 60%
        return START_NOT_STICKY;                                // don't start Service once it gets Killed by OS
    }

    /************************************************************************************************************************
     *	Class -- Handler class for the music player
     ************************************************************************************************************************/
    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what < MAX_SOUNDS)
                soundPlay(msg.what, msg.arg1);
            else switch (msg.what) {
                case BG_MUSIC_PLAY:
                    BGPlay();
                    break;
                case BG_MUSIC_PAUSE:
                    BGPause();
                    break;
                case BG_MUSIC_STOP:
                    BGStop();
                    break;
                case BG_SET_VOLUME:
                    setVolume(msg.arg1, msg.arg2);
                    break;
                case BG_MUSIC_LEVEL1:
                    BGSet(R.raw.level1);
                    BGPlay();
                    break;
                case BG_MUSIC_LEVEL2:
                    BGSet(R.raw.level2);
                    BGPlay();
                    break;
                case BG_MUSIC_LEVEL3:
                    BGSet(R.raw.level3);
                    BGPlay();
                    break;
                case BG_MUSIC_LEVEL4:
                    BGSet(R.raw.level4);
                    BGPlay();
                    break;
                case BG_MUSIC_LEVEL5:
                    BGSet(R.raw.level5);
                    BGPlay();
                    break;
                case BG_MUSIC_INTRO:
                    BGSet(R.raw.intro);
                    BGPlay();
                    break;
                case SOUND_STOP:
                case SOUND_PAUSE_ALL:
                case SOUND_RESUME_ALL:
                case SOUND_STOP_ALL:
                case SOUND_SET_VOLUME:
                    soundCommand(msg.what, (int) arrSounds[msg.arg1][SPOOL_ID], msg.arg2);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
} 
