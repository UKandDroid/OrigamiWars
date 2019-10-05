package com.BreakthroughGames.OrigamiWars;

import android.content.Context;
import android.os.Vibrator;

import com.BreakthroughGames.OrigamiWars.utils.Flow;

public class Events extends Base {
    private class Event {
        public int iSound;
        public boolean bPause;
        public float fOverlap;
        public long[] aVibrate;
        public int iTimesRun = 0;
        public int iPriority = 0;
        public int iLineStart = 0;
        public int iDisplayTime = 0;

        public Event(int vPriority, int vTimesRun, int vDisplayTime, int vLineStart, boolean vPause, float vOverlap, int vSound, long[] vaVibrate) {
            iPriority = vPriority;
            iTimesRun = vTimesRun;
            iDisplayTime = vDisplayTime;
            iLineStart = vLineStart;
            bPause = vPause;
            fOverlap = vOverlap;
            iSound = vSound;
            aVibrate = vaVibrate;
        }
    }

    public int iTimer;                                                // Timer to sync event and other things
    private Flow flowVibration = new Flow();
    private long startTime, endTime;
    protected static Vibrator vibrate;
    private int curEvent = -1, curEvePri = -1;
    private float vLineStart, timeVisible, timeElapsed;
    protected float scaleX = 0;
    protected float scaleY = 0;
    public float txtLineWidth = 0.05f;
    // Events IDs
    public static final int READY = 0;
    public static final int SURVIVE = 1;                    // Event with GUI, Events Texture
    public static final int TOUCH_TO_MOVE = 2;
    public static final int TOUCH_TO_FIRE = 3;
    public static final int BLOW_ON_SCREEN = 4;
    public static final int FLICK_TO_ROLL = 5;
    public static final int FLICK_TO_SWAVE = 6;
    protected static final int SWITCH_WEAPON = 7;
    public static final int LEVEL_COMPLETE = 8;
    public static final int GAME_OVER = 9;
    protected static final int COLLECT_PUPS = 10;
    protected static final int EXTRA_LIFE = 11;
    protected static final int MAX_DISTANCE = 12;
    public static final int SCORE = 13;
    protected static final int EMPTY_14 = 14;
    protected static final int PROXIMITY_ALERT = 15;
    protected static final int SAVE_ORIGAMIS = 16;
    protected static final int HIGHEST_SCORE = 17;
    protected static final int WEAPON_EMPTY = 18;
    protected static final int LOST_3_TIMES = 19;
    protected static final int EMPTY_SLOT_20 = 20;
    protected static final int CONSCDER_CLASS = 21;
    public static final int STAGE1_LEVEL2 = 22;
    public static final int STAGE1_LEVEL3 = 23;
    public static final int STAGE2_LEVEL1 = 24;
    public static final int STAGE2_LEVEL2 = 25;
    public static final int STAGE2_LEVEL3 = 26;
    public static final int STAGE3_LEVEL1 = 27;
    public static final int STAGE3_LEVEL2 = 28;
    public static final int STAGE3_LEVEL3 = 29;
    protected static final int HIGHEST_WINS = 30;
    protected static final int BIGGEST_LOSER = 31;
    protected static final int EMPTY_SLOT_32 = 32;
    protected static final int EMPTY_SLOT_33 = 33;
    protected static final int YOU_ARE_CURSED = 34;
    protected static final int CYCLE_OF_POWER = 35;
    protected static final int COLL_MAG_SCRLS = 36;
    protected static final int LAST_GUI_EVENT = 36;

    protected static final int VIBRATE_50 = 37;                        //Events with no GUI - only Sound & Vibrate
    protected static final int SHOT_NORMAL = 38;
    protected static final int SHOT_DBARELL = 39;
    protected static final int SHOT_MACHINE = 40;
    protected static final int SHOT_LIGHTNING = 41;
    protected static final int SHOCK_WAVE = 42;
    protected static final int VIBRATE_DRAG = 43;
    protected static final int VIBRATE_CURSE = 44;
    protected static final int MIC_CALIBRATE = 45;
    protected static final int PLANE_HIT_SHOT = 46;
    protected static final int PLANE_HIT_ENEMY = 47;
    protected static final int ENEMY_POKE = 48;
    protected static final int TOTAL_EVENTS = 49;                        // Total Number of events

    public static Event arrEvents[] = new Event[TOTAL_EVENTS];

    Events(boolean bCreate) { }
    Events() {
        super.setTextureSize(0, 1, txtLineWidth, 0);                        // By default set texture to one line of visible Texture width
        posX = 2;
        posY = 0;
        scaleX = Screen.CHAR_HEIGHT * (2.02f / Screen.ASPECT_RATIO);
        scaleY = 1;
        if (vibrate == null)
            vibrate = (Vibrator) Game.refContext.getSystemService(Context.VIBRATOR_SERVICE);

        flowVibration.code((Flow.Code) (iAction, bSingle, iExtra, data) -> {
            Event event = (Event)data;
            if (bSingle) {                         // its a single vibration
                vibrate.vibrate(event.aVibrate[0]);
            } else {                                                  // Its a vibration pattern
                vibrate.vibrate(event.aVibrate, -1);       // Don't repeat
            }
        });

        //		  Event                   ---- Priority - TimesRun ---- Time ------- Line ------ Pause ------- overlap -- Sound ----- Vibrate
        arrEvents[READY] =          new Event(0, -1, 1, 0, false, 0, -1, null);
        arrEvents[SURVIVE] =        new Event(1, -1, 1, 1, false, 0, -1, null);
        arrEvents[TOUCH_TO_MOVE] =  new Event(2, 1, 3, 2, true, 0, Sound.VOICE_MOVE, null);
        arrEvents[TOUCH_TO_FIRE] =  new Event(2, 1, 3, 3, true, 0, Sound.VOICE_FIRE, null);
        arrEvents[BLOW_ON_SCREEN] = null; // removed blow function
        arrEvents[FLICK_TO_ROLL] =  new Event(2, 1, 3, 5, true, 0, Sound.VOICE_ROLL, null);
        arrEvents[FLICK_TO_SWAVE] = new Event(2, 1, 3, 6, false, 0, Sound.VOICE_SWAVE, null);
        arrEvents[SWITCH_WEAPON] =  new Event(1, 1, 3, 7, true, 0, Sound.VOICE_WEAPON, null);
        arrEvents[LEVEL_COMPLETE] = new Event(2, -1, 5, 8, false, 0, Sound.LEVEL_COMPLETE, null);
        arrEvents[GAME_OVER] =      new Event(3, -1, 3, 9, false, 0, Sound.GAMEOVER, null);
        arrEvents[COLLECT_PUPS] =   new Event(1, 2, 2, 10, false, 0, -1, null);
        arrEvents[EXTRA_LIFE] =     new Event(1, -1, 1, 11, false, 0, Sound.EXTRA_LIFE, null);
        arrEvents[MAX_DISTANCE] =   new Event(0, -1, 2, 12, false, 0, Sound.NEW_RECORD, null);
        arrEvents[SCORE] =          new Event(2, -1, 1, 13, false, 0, -1, null);
        arrEvents[EMPTY_14] =       new Event(0, -1, 1, 14, false, 0, -1, null);
        arrEvents[PROXIMITY_ALERT] =new Event(2, 1, 1, 15, false, 0, Sound.ALERT, null);
        arrEvents[SAVE_ORIGAMIS] =  new Event(0, -1, 3, 16, false, 0, -1, null);
        arrEvents[HIGHEST_SCORE] =  new Event(0, -1, 2, 17, false, 0, Sound.NEW_RECORD, null);
        arrEvents[WEAPON_EMPTY] =   new Event(0, -1, 1, 18, false, 0, Sound.WEAPON_EMPTY, null);
        arrEvents[LOST_3_TIMES] =   new Event(0, -1, 2, 19, false, 0, -1, null);
        arrEvents[EMPTY_SLOT_20] =  new Event(0, -1, 2, 0, false, 0, -1, null);
        arrEvents[CONSCDER_CLASS] = new Event(0, -1, 3, 1, false, 0, Sound.NEW_RECORD, null);
        arrEvents[STAGE1_LEVEL2] =  new Event(0, -1, 3, 2, false, 0, -1, null);
        arrEvents[STAGE1_LEVEL3] =  new Event(0, -1, 3, 3, false, 0, -1, null);
        arrEvents[STAGE2_LEVEL1] =  new Event(0, -1, 3, 4, false, 0, -1, null);
        arrEvents[STAGE2_LEVEL2] =  new Event(0, -1, 3, 5, false, 0, -1, null);
        arrEvents[STAGE2_LEVEL3] =  new Event(0, -1, 3, 6, false, 0, -1, null);
        arrEvents[STAGE3_LEVEL1] =  new Event(0, -1, 3, 7, false, 0, -1, null);
        arrEvents[STAGE3_LEVEL2] =  new Event(0, -1, 3, 8, false, 0, -1, null);
        arrEvents[STAGE3_LEVEL3] =  new Event(0, -1, 3, 9, false, 0, -1, null);
        arrEvents[HIGHEST_WINS] =   new Event(0, -1, 2, 10, false, 0, Sound.NEW_RECORD, null);
        arrEvents[BIGGEST_LOSER] =  new Event(0, -1, 3, 11, false, 0, Sound.NEW_RECORD, null);
        arrEvents[EMPTY_SLOT_32] =  new Event(0, -1, 0, 0, false, 0, -1, null);
        arrEvents[EMPTY_SLOT_33] =  new Event(0, -1, 0, 0, false, 0, -1, null);
        arrEvents[YOU_ARE_CURSED] = new Event(2, 2, 3, 12, true, 0, Sound.VOICE_CURSED, null);
        arrEvents[CYCLE_OF_POWER] = new Event(1, 1, 3, 13, true, 0, Sound.VOICE_COPOWER, null);
        arrEvents[COLL_MAG_SCRLS] = new Event(0, 1, 3, 14, true, 0, Sound.VOICE_SCROLLS, null);

        // NO GUI, only Sound and Vibrate
        arrEvents[VIBRATE_50] =     new Event(0, -1, 0, 0, false, 0, -1, new long[]{50});
        arrEvents[SHOT_NORMAL] =    new Event(0, -1, 0, 0, false, 0.0f, Sound.GUN_NORMAL, new long[]{10});
        arrEvents[SHOT_MACHINE] =   new Event(0, -1, 0, 0, false, -0.15f, Sound.GUN_MACHINE, new long[]{15});
        arrEvents[SHOT_DBARELL] =   new Event(0, -1, 0, 0, false, -0.13f, Sound.GUN_DOUBLE, new long[]{20, 25});
        arrEvents[SHOT_LIGHTNING] = new Event(0, -1, 0, 0, false, -0.20f, Sound.LIGHTNING, new long[]{35});
        arrEvents[SHOCK_WAVE] =     new Event(0, -1, 0, 0, false, 0.15f, Sound.SHOCK_WAVE, null);
        arrEvents[VIBRATE_DRAG] =   new Event(0, -1, 0, 0, false, 0.05f, Sound.DRAG, new long[]{0, 35, 25});
        arrEvents[VIBRATE_CURSE] =  new Event(0, -1, 0, 0, false, 0, Sound.CURSE, null);
        arrEvents[MIC_CALIBRATE] =  new Event(0, -1, 0, 0, false, 0, Sound.WIND_MIC_CALIB, new long[]{500, 35, 25, 25, 50, 35, 25, 25, 50, 35, 25,});
        arrEvents[PLANE_HIT_SHOT] = new Event(0, -1, 0, 0, false, 0, Sound.HIT_SHOT, new long[]{25});
        arrEvents[PLANE_HIT_ENEMY] = new Event(0, -1, 0, 0, false, 0, Sound.HIT_ENEMY, new long[]{0, 20, 20, 80});
        arrEvents[ENEMY_POKE] = new Event(0, -1, 0, 0, false, 0, Sound.POKE, null);
    }

    /************************************************************************************************************************
     *   METHOD- used to move, Hud events txt position
     ***********************************************************************************************************************/
    public void transformHUD(float vScaleX, float vScaleY, float vPosX, float vPosY) {
        scaleX = vScaleX;
        scaleY = vScaleY;
        posX = vPosX;
        posY = vPosY;
    }

    /************************************************************************************************************************
     *   METHOD- Resets events
     ***********************************************************************************************************************/
    protected void reset() {
        bEnable = false;
        curEvent = curEvePri = -1;                                        // Set priority to -1 as evnet finishes
        Game.refGameView.requestRender();
    }

    /************************************************************************************************************************
     *   METHOD- Draws txt, also checks for delayed events, and dispatches them when time is due
     ***********************************************************************************************************************/
    @Override
    public void draw() {
        if (!bEnable || Screen.iMenu != Screen.MENU_OFF)
            return;                                        // If no event or menu is displayed, dont do anything

        endTime = System.currentTimeMillis();
        timeElapsed = (endTime - startTime) / 1000;

        if (timeElapsed < timeVisible) {
            transform(scaleX, scaleY, posX, posY);
            draw(vLineStart * txtLineWidth, 0);
        } else {
            bEnable = false;
            curEvent = curEvePri = -1;                                        // Set priority to -1 as event finishes
        }
    }

    /************************************************************************************************************************
     *   METHOD- Called by levels/Game when an event occurs
     ***********************************************************************************************************************/
    public void dispatch(final int eventId) {
        final Event event = arrEvents[eventId];
        if(event == null)
            return;

        if (isNonGuiEvent(eventId)) {
            if (event.iSound != -1)                                             // Play event sound
                SoundPlayer.playSound(event.iSound, event.fOverlap);
            if (vibrate != null && event.aVibrate != null) {                    // If event has a vibration then play it
                flowVibration.run(0,  false,event.aVibrate.length == 1,0, event);
            }
        } else if (event.iTimesRun != 0 && event.iPriority > curEvePri && Player.bEnable) {            // If Event has already run set number of times, don't run it
            if (event.iTimesRun > 0)
                event.iTimesRun--;                        // If Event has set number of times, reduce one
            if (bEnable && arrEvents[curEvent].iSound != -1 && event.iSound != -1)        // If last event is still running, stop its sound if it has any
                SoundPlayer.stopSound(arrEvents[curEvent].iSound);                // Stop sound of last Txt event
            if (event.iSound != -1)                                               // Play event associated sound
                SoundPlayer.playSound(event.iSound, event.fOverlap);

            curEvent = eventId;                                                  // Cur Event
            curEvePri = event.iPriority;                                         // Priority, used if there is another event before this one finishes
            iTexture = (eventId > 19) ? Game.txtEvent2.iTexture : Game.txtEvent1.iTexture; // Select texture Sheet
            setEvent(event.iLineStart, event.iDisplayTime, event.bPause);
        }
    }

    /************************************************************************************************************************
     *   METHOD- Initilizes variables, called by setEvent(int) also by storyEvent to show story
     ***********************************************************************************************************************/
    public void setEvent(int line, float time, boolean vPause) {
        bEnable = true;
        startTime = System.currentTimeMillis();
        vLineStart = line;
        timeVisible = time;
        timeElapsed = 0;                                                    // Used for text thats is displayed
        Screen.bPause = vPause;
    }

    /************************************************************************************************************************
     *   METHOD- Saves in game prefs, how many times an event has been run
     ***********************************************************************************************************************/
    protected static void prefSave(boolean bSave) {

        String string = null;
        int length = Events.arrEvents.length;
        if (bSave) {                                                           // If save, save prefs, else load
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < length; i++)
                buf.append(Events.arrEvents[i].iTimesRun + ",");
            Game.refActGame.getPreferences(0).edit().putString(Pref.PREF_EVENTS, buf.toString()).commit();    // Save Events Run Times String
        } else {
            string = Game.refActGame.getPreferences(0).getString(Pref.PREF_EVENTS, "");
            if (string != "")
                try {
                    int i = 0;
                    for (String token : string.split(","))
                        Events.arrEvents[i++].iTimesRun = Byte.parseByte(token);
                } catch (NumberFormatException nfe) { nfe.printStackTrace();
                } catch (Exception e) { e.printStackTrace(); }
        }
    }


    boolean isGuiEvent(int id){ return  id <= LAST_GUI_EVENT; }
    boolean isNonGuiEvent(int id){ return  !isGuiEvent(id); }
}//End Class
