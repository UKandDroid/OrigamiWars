package com.BreakthroughGames.OrigamiWars;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class ActMainMenu extends Activity {

    protected static final int BACK_PRESSED = 0;                                                // Display toast, press back again to exit
    protected static final int MIC_INIT_ERROR = 1;
    protected static final int POST_SUCCESSFULL = 2;
    protected static final int POST_ERROR = 3;
    private static boolean bShowOnce = true;
    private ScaleAnimation anim;
    private ImageButton facebook;
    static protected Handler handler = null;
    static boolean bForeGround = false;

    @Override
    public void onCreate(Bundle sis) {
        Log.d("MethodCall", "ActMainMenu::onCreate() Called");

        super.onCreate(sis);

        //FacebookSdk.sdkInitialize(getApplicationContext());
        //AppEventsLogger.activateApp(this);

        setContentView(R.layout.main);
        Game.refActMainMenu = this;
        Game.refContext = getApplicationContext();

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

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Pref.bFB_Like = getPreferences(0).getBoolean(Pref.FB_LIKE, false);
        Events.vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        anim = new ScaleAnimation(0.5f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.95f, Animation.RELATIVE_TO_SELF, 0.5f); // Scale Animation for Facebook Like Button, Scale
        facebook = (ImageButton) findViewById(R.id.btnFaceBook);        // Get button handlers
        ImageButton twitter = (ImageButton) findViewById(R.id.btnTwitter);
        ImageButton arcade = (ImageButton) findViewById(R.id.btnArcade);
        ImageButton start = (ImageButton) findViewById(R.id.btnStart);
        ImageButton contin = (ImageButton) findViewById(R.id.btnContinue);
        ImageButton exit = (ImageButton) findViewById(R.id.btnExit);

        anim.setDuration(200);                                                            // Scale Animation Duration
        anim.setInterpolator(new DecelerateInterpolator(1));
        arcade.getBackground().setAlpha(0);
        start.getBackground().setAlpha(0);
        contin.getBackground().setAlpha(0);
        exit.getBackground().setAlpha(0);
        twitter.getBackground().setAlpha(0);
        facebook.getBackground().setAlpha(0);

        if (!SoundPlayer.bBound) {
            Game.refMusicThread = new Thread() {                                            // Start BG Music Service in a new Thread
                public void run() {
                    Intent bgMusic = new Intent(ActMainMenu.this, Sound.class);
                    startService(bgMusic);
                    bindService(bgMusic, mConnect, Context.BIND_AUTO_CREATE);            // Bind Service, when app exits, service
                }
            };

            Game.refMusicThread.start();                                                // Start the music thread, once its created
            Game.iMode = Values.START_MUSIC_OK;                                            // set flag, music service started
        }


        if (!Pref.bFB_Like && bShowOnce) {                                                // If Game hasn't been liked on FaceBook, Show Toast to like it
            bShowOnce = false;
            new Handler().postDelayed(new Thread() {
                @Override
                public void run() {
                    facebook.setImageResource(R.drawable.btn_facebook_like);
                    facebook.startAnimation(anim);
                }
            }, 4000);
            new Handler().postDelayed(new Thread() {
                @Override
                public void run() {
                    facebook.setImageResource(R.drawable.btn_facebook);
                }
            }, 7500);
        }


        if (handler == null)
            handler = new Handler() {                                                        // Handler for toasts from Mic Thread
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MIC_INIT_ERROR:
                            Toast.makeText(ActMainMenu.this, "Mic Error: Blow feature Disabled", Toast.LENGTH_LONG).show();
                            break;
                        case BACK_PRESSED:
                            Toast.makeText(ActMainMenu.this, "Press again to Exit", Toast.LENGTH_SHORT).show();
                            break;
                        case Mic.WIND_BLOWING:
                            Toast.makeText(ActMainMenu.this, "Blow Level = " + msg.arg1, Toast.LENGTH_SHORT).show();
                            break;
                        case Events.LEVEL_COMPLETE:
                            Toast.makeText(ActMainMenu.this, "Distance = " + msg.arg1, Toast.LENGTH_SHORT).show();
                            break;
                        case POST_SUCCESSFULL:
                            new Handler().post(new Thread() {
                                @Override
                                public void run() {
                                    facebook.setImageResource(R.drawable.btn_facebook_posted);
                                    facebook.startAnimation(anim);
                                }
                            });
                            new Handler().postDelayed(new Thread() {
                                @Override
                                public void run() {
                                    facebook.setImageResource(R.drawable.btn_facebook);
                                }
                            }, 3000);
                            break;
                        case POST_ERROR:
                            new Handler().post(new Thread() {
                                @Override
                                public void run() {
                                    facebook.setImageResource(R.drawable.btn_facebook_post_error);
                                    facebook.startAnimation(anim);
                                }
                            });
                            new Handler().postDelayed(new Thread() {
                                @Override
                                public void run() {
                                    facebook.setImageResource(R.drawable.btn_facebook);
                                }
                            }, 3000);
                            break;

                    }
                }
            };
/************************************************************************************************************************
 *  BUTTON LISTNERS - Listeners for Twitter, FaceBook, Survival, Story, Resume, Exit Button
 ************************************************************************************************************************/
        // TWITTER Button
        twitter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {                                                                            // Open Native App
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("twitter://user?screen_name=OrigamiWars"));
                    startActivity(intent);
                } catch (Exception e) {
                    Uri uriUrl = Uri.parse("http://twitter.com/OrigamiWars");                // Open In Browser
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            }
        });

        // FACE BOOK Button
        facebook.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //  For test purpose only
                //	FaceBook.bTestPost = true;
                //	fbPost.postNewRecords();

                if (!Pref.bFB_Like)
                    getPreferences(0).edit().putBoolean(Pref.FB_LIKE, true).commit();
                Pref.bFB_Like = true;

                try {                                                                        // Open Native App
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/863141197045856"));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Uri uriUrl = Uri.parse("http://facebook.com/OrigamiWars");                // Open in browser
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                }
            }
        });

        // SURVIVAL button
        arcade.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GLWrapper.testMethod();
                if (Events.vibrate != null)
                    Events.vibrate.vibrate(40);                            // Give feedback to user
                Screen.iMenu = Screen.MENU_OFF;
                Intent game = new Intent(getApplicationContext(), ActGame.class);
                game.putExtra("GameMode", Values.ARCADE_MODE);
                startActivity(game);
            }
        });

        // START button
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Events.vibrate != null)
                    Events.vibrate.vibrate(40);                            // Give feedback to user
                Screen.iMenu = Screen.MENU_OFF;
                Intent game = new Intent(getApplicationContext(), ActGame.class);
                game.putExtra("GameMode", Values.STORY_MODE);
                startActivity(game);
            }
        });

        // RESUME button listener
        contin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Events.vibrate != null)
                    Events.vibrate.vibrate(40);                            // Give feedback to user
                Screen.iMenu = Screen.MENU_OFF;
                Intent game = new Intent(getApplicationContext(), ActGame.class);
                game.putExtra("GameMode", Values.STORY_RESUME_LEVEL);
                startActivity(game);
            }
        });

        // EXIT Button Listener
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Events.vibrate != null)
                    Events.vibrate.vibrate(40);                                // Give feedback to user
                exitGame();
            }
        });
    }//End onCreate()

    /************************************************************************************************************************
     *  METHOD - Service Binding un Binding
     ************************************************************************************************************************/
    @Override
    public void onStart() {
        Log.d("MethodCall", "MainMenu::onStart() Called");
        super.onStart();
        if (!SoundPlayer.bBound)

            bindService(new Intent(this, Sound.class), mConnect, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        Log.d("MethodCall", "MainMenu::onDestroy() Called");
        super.onDestroy();
        if (isFinishing()) exitGame();
    }

    @Override
    public void onPause() {
        Log.d("MethodCall", "MainMenu::onPause() Called");
        super.onPause();
        bForeGround = false;
        //	AppEventsLogger.deactivateApp(this);
        SoundPlayer.sendCommand(Sound.BG_MUSIC_STOP);
    }

    @Override
    public void onResume() {
        super.onResume();
        //	AppEventsLogger.activateApp(this);
    }

    public void onWindowFocusChanged(boolean bFocus) {
        Log.d("MethodCall", "MainMenu::onWindowFocusChanged( " + bFocus + " ) called");

        if (bFocus) {
            bForeGround = true;
            SoundPlayer.playSound(Sound.BG_MUSIC_INTRO);

        }
    }

    static private ServiceConnection mConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Values.log("music", "Service Connected ");

            SoundPlayer.mService = new Messenger(service);
            SoundPlayer.bBound = true;
            if (bForeGround)                                                            // If activity is in ForeGround and hasn't gone in BG
                SoundPlayer.playSound(Sound.BG_MUSIC_INTRO);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("music", "Service Disconnected");

            SoundPlayer.mService = null;
            SoundPlayer.bBound = true;
        }
    };

    /************************************************************************************************************************
     *  ACTIVITY METHOD - onActivityResult() Checks data when FaceBook dialog is closed, if permission granted, Carries on the request
     ************************************************************************************************************************/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MethodCall", "MainMenu::onActivityResult() called");

        super.onActivityResult(requestCode, resultCode, data);
        //	Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data); 	// This will call

    }

    @Override
    public void onBackPressed() {
        exitGame();
    }                                // If back Button is pressed, Exit the Game

    protected void exitGame() {
        int pId = android.os.Process.myPid();                                        // Terminate the Service Thread as well
        android.os.Process.killProcess(pId);

        if (Game.onExit()) {                                                            // If Music Service is Killed
            Mic.stopRecording();
            Intent bgMusic = new Intent(this, Sound.class);
            if (SoundPlayer.bBound) {
                unbindService(mConnect);
                SoundPlayer.bBound = false;
            }
            stopService(bgMusic);
        }
        finish();
    }
}
