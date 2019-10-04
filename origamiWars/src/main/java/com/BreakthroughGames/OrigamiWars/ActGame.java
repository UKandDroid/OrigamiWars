package com.BreakthroughGames.OrigamiWars;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;

public class ActGame extends Activity {
    private FrameLayout layout;
    private GameView gameView = null;
    protected static int iBackPressed = 0;
    private FrameLayout.LayoutParams params;


    @Override
    public void onCreate(Bundle sis) {
        Log.d("MethodCall", "ActGame::OnCreate() Called");
        super.onCreate(sis);
        Game.refActGame = this;

        iBackPressed = 0;
        if (Game.iMode == Values.START_ERROR)
            finish();                                        // Go to back to Main activity as app memory has been reclaimed by OS
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);                // Keep screen on during the game
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);                    // Hide status bar

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(flags);
            final View decorView = getWindow().getDecorView();                                // Listener for volume buttons, as they will show the navigation bar
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

        Game.iMode = getIntent().getIntExtra("GameMode", Values.STORY_MODE);                // Get value for GameMode, default is start new

        gameView = new GameView(this);
        layout = new FrameLayout(this);
        params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.BOTTOM);

        layout.addView(gameView);                                                            // Add Game view
        if (Values.bShowAds) {                                                                // If free game ver, Display Ads

        }
        setContentView(layout);

    }//End onCreate()


    /************************************************************************************************************************
     *	METHOD -- Overridden methods
     ************************************************************************************************************************/
    public void onWindowFocusChanged(boolean bFoucs) {
        Log.d("MethodCall", "ActvGame::onWindowFocusChanged() Called  Focus: " + bFoucs);

        if (bFoucs) {
            gameView.onResume();
            Screen.bPause = false;
            SoundPlayer.sendCommand(Sound.BG_MUSIC_PLAY);
        }
    }

    @Override
    protected void onPause() {
        Log.d("MethodCall", "ActvGame::OnPause() Called");

        super.onPause();
        gameView.onPause();
        Screen.iMenu = Screen.MENU_PAUSE;
        SoundPlayer.sendCommand(Sound.BG_MUSIC_PAUSE);
        SoundPlayer.sendCommand(Sound.SOUND_PAUSE_ALL);
    }

    @Override
    public void onDestroy() {
        Log.d("MethodCall", "ActvGame::OnDestroy() Called");
        super.onDestroy();
    }

    public boolean onKeyUp(int vKey, KeyEvent vEvent) {
        if (vKey == KeyEvent.KEYCODE_MENU) {
            Screen.iMenu = Screen.MENU_PAUSE;
            return true;
        }
        if (vKey == KeyEvent.KEYCODE_BACK) {
            if (iBackPressed == 0) {
                Screen.iMenu = Screen.MENU_PAUSE;
                ActMainMenu.handler.sendEmptyMessage(ActMainMenu.BACK_PRESSED);
            } else
                Pref.getSet(Pref.GAME_EXIT);

            iBackPressed++;
            return true;
        }
        if (vKey == KeyEvent.KEYCODE_VOLUME_UP || vKey == KeyEvent.KEYCODE_VOLUME_DOWN)    // Calibrate mic when volume changes
            Mic.calibrate();

        return super.onKeyUp(vKey, vEvent);
    }


}