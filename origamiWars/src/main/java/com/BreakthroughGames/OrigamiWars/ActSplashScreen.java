package com.BreakthroughGames.OrigamiWars;

import com.BreakthroughGames.OrigamiWars.R;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.content.Intent;

public class ActSplashScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);                    //Load splash screen
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

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

        /*Start the Game in a new thread and Handler and delay it for 2 seconds for splash screen*/
        new Handler().postDelayed(new Thread() {
            @Override
            public void run() {                                                    //Create Intent to launch MainMenu
                Intent mainMenu = new Intent(ActSplashScreen.this, ActMainMenu.class);
                startActivity(mainMenu);                            //Start MainMenu Activity
                finish();                                            //End Splash screen activity, so user cant come back to splash screen
                overridePendingTransition(R.layout.fadein, R.layout.fadeout); // add the fade in and fade out effects
            }
        }, Game.GAME_THREAD_DELAY);                            //End Handler
    }
}
