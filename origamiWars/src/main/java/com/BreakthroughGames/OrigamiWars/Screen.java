package com.BreakthroughGames.OrigamiWars;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Screen
{
	// Resources
	static String strLog = "";

	protected static float CHAR_WIDTH ;												// Screen in divided into sections for X and Y
	public static float CHAR_HEIGHT;												// Each character is one screen unit in width and height
	protected static float DEV_MAX_X ;												// Calculated, depending on device AspectRatio
	protected static float DEV_MAX_Y = 9;
	private static final float MAX_X = 12;											// Maximum X units for any Aspect ratio

	public static final int MENU_OFF 	 = 0;
	protected static final int MENU_PAUSE 	 = 1;
	public static final int MENU_GAMEOVER = 2;

	protected static float butFireY = 0, butFireX = 0;								// GL Rescaled co-ordinated for Menu and fire button
	protected static float MENU_MAX_X = 0, MENU_MAX_Y = 0;
	protected static float menuButtonX, menuButtonY, menuExitX, menuExitY;
	protected static float menuRestartX, menuRestartY, menuResumeX, menuResumeY;
	protected static final int ACTION_UP 	   = MotionEvent.ACTION_UP;
	private   static final int ACTION_2ND_UP   = MotionEvent.ACTION_POINTER_UP;
	protected static final int ACTION_DOWN 	   = MotionEvent.ACTION_DOWN;
	private   static final int ACTION_2ND_DOWN = MotionEvent.ACTION_POINTER_DOWN;
	private   static final int ACTION_MOVE 	   = MotionEvent.ACTION_MOVE;
	private   static final int ACTION_CANCEL   = MotionEvent.ACTION_CANCEL;

	private static long  eventTime;
	private static int   indxArr = 0;
	private static int   ARR_SIZE = 4;
	private static long  arrTime[] = {0,0,0,0};
	private static float arrPosX[] = {0,0,0,0};
	private static float arrPosY[] = {0,0,0,0};
	private static float posX[] = {0,0,0,0}, posY[]= {0,0,0,0};

	private   static int event;
	private   static Base oTap = Adventure.oTap;
	public static int iMenu = MENU_OFF;
	private   static float PIXELS_X = -1, PIXELS_Y = -1;							// Screen X-Res, Y-Res and Middle
	public static float ASPECT_RATIO = 1;
	protected static float touchX;
	protected static float touchY;						// Finger GL-Cords, used to detected which object is touched on screen
	private   static int eLeft, eRight, leftFingerId = -1, rightFingerId = -1;		// Which half of the screen is touched
	private   static int indxLeft, indxRight, countRight, finCount, curIndex;
	protected static boolean bShockWave = false;
	public static boolean bPause = false;
	protected static boolean bTouch =  false;
	protected static boolean bMenu = false;
	protected static boolean bTap = false;

	Screen()	{}
	protected static void reset() {
		event = eLeft = eRight = leftFingerId = rightFingerId = -1 ;
		bShockWave = false; Player.eAction = Player.ACTION_CRUISE; Weapon.eState = Weapon.WEAPON_OFF;
	}
	/************************************************************************************************************************
	 *	Main Check method -- checks different actions and gestures
	 ************************************************************************************************************************/
	protected static boolean check(final MotionEvent e) {
		eLeft = eRight = -1;														// Event for finger
		indxLeft = indxRight = -1;													// Index of fingers
		countRight = 0;																// Fingers on each side of screen						
		event = e.getActionMasked();
		curIndex = e.getActionIndex() ;
		eventTime = System.currentTimeMillis();
		finCount = e.getPointerCount() > 4 ? 4: e.getPointerCount();				// Maximum four fingers
		float tPosX, tPosY;

		for(int i = 0; i < finCount; i++) {
			posX[i] = e.getX(i);
			posY[i] = e.getY(i);
			if(e.getPointerId(i) == leftFingerId && (event == ACTION_MOVE || i == curIndex )) {
				eLeft = event; indxLeft = i;
			} else if(e.getPointerId(i)== rightFingerId && (event == ACTION_MOVE || i == curIndex )) {
				eRight = event; indxRight = i; countRight++;
			} else if(((tPosY= getGLY(posY[i], MENU_MAX_Y)) > butFireY-0.5f) && (tPosY < butFireY+1.5) && ((tPosX= getGLX(posX[i], MENU_MAX_X))  > butFireX-0.75) && (tPosX  < butFireX+1.5)) {
				if(i == curIndex || event == ACTION_MOVE ) {
					eRight = event; indxRight = i; countRight++; rightFingerId = e.getPointerId(i);}
			} else {
				if((i == curIndex || event == ACTION_MOVE) && leftFingerId == -1) {
					eLeft = event; indxLeft = i; leftFingerId = e.getPointerId(i);}
			}
		}

		Initialize.check(e);															// Set diff Flags
	//	Initialize.log();
		Menu.check(e);																	// Check Menu
		if(!bMenu) {
			PlaneAction.check(e);														// Check Plane actions
			FireWeapon.check(e);														// Check Weapon Fire and Switch		
		}
		return event == ACTION_MOVE;													// If action move, sleep thread,
	}
	/************************************************************************************************************************
	 *	Class Flags -- sets different flags, like screen touch, leftSideTouch, rightSideTouch
	 ************************************************************************************************************************/
	public static class Initialize {
		private static void check(MotionEvent e) {
			if(eLeft != -1 ) {
				touchX = getGLX(e.getX(e.findPointerIndex(leftFingerId)));
				touchY = getGLY(e.getY(e.findPointerIndex(leftFingerId)));
			}

			bTouch = (finCount > 1 || event != ACTION_UP) ? true : false;			// If screen is touched set flag to true

			if(bTouch && bPause && !bMenu && (event == ACTION_DOWN ||event == ACTION_2ND_DOWN)) {
				bPause = false;
				SoundPlayer.sendCommand(Sound.SOUND_RESUME_ALL);					// Resume all sound effects 
				Game.refGameView.requestRender();
			}

			if(event == ACTION_UP) {
				leftFingerId = rightFingerId = -1;
			//	Values.log("screen","Fingers Reset");
			}

			if( !bMenu) {
				if(event == ACTION_DOWN || event == ACTION_2ND_DOWN)
					Game.tapTimer = 0;

				if(event == ACTION_UP || event == ACTION_2ND_UP) {
					oTap.posT = 0;                                                                  // Reset counter
					oTap.bEnable = true;												            // Touch2 or tap is not finger associated with fire or movement
					oTap.posX = getGLX(e.getX(curIndex)) - 0.5f;							        // center of object created
					oTap.posY = getGLY(e.getY(curIndex)) - 0.6f;
				}
			}

		}

		@SuppressWarnings("unused")
		private static void log(){
			switch(event) {
				case  ACTION_UP:
				//	Values.log("screen"," EVENT: 1ST_UP   index = "+curIndex +" count = "+ finCount + " leftId = "+ leftFingerId + " rightId = " + rightFingerId +" EventLeft = "+eLeft+" fin "+indxLeft+",   EventRight = "+eRight+" fin "+ indxRight);
					break;


				case  ACTION_DOWN:
				//	Values.log("screen"," EVENT: 1ST_DOWN index = "+curIndex +" count = "+ finCount + " leftId = "+ leftFingerId + " rightId = " + rightFingerId +" EventLeft = "+eLeft+" fin "+indxLeft+",   EventRight = "+eRight+" fin "+ indxRight);
					break;

                case ACTION_2ND_UP:
                    Values.log("screen"," EVENT: 2ND_UP   index = "+curIndex +" count = "+ finCount + " leftId = "+ leftFingerId + " rightId = " + rightFingerId +" EventLeft = "+eLeft+" fin "+indxLeft+",   Tap = "+oTap.bEnable);
                    break;

                case  ACTION_2ND_DOWN:
				//	Values.log("screen"," EVENT: 2ND_DOWN index = "+curIndex +" count = "+ finCount + " leftId = "+ leftFingerId + " rightId = " + rightFingerId +" EventLeft = "+eLeft+" fin "+indxLeft+",   Tap = "+oTap.bEnable);
					break;

				//case  ACTION_MOVE: 		strLog ="MOVE    "; break;
				//case  ACTION_CANCEL: 	strLog ="CANCEL  "; break;
			}

		}
	}
	/************************************************************************************************************************
	 *	Class Menu -- Handles menu button
	 ************************************************************************************************************************/
	static class PlaneAction {
		private static long deltaTime;
		private static float  endX;
		private static float endY;
		private static float deltaX;
		private static float deltaY;

		private static void check(MotionEvent e){
			if(eLeft != -1 && !bMenu ) 												// If screen left is touched and menu is not displayed
				switch(eLeft) {
					case ACTION_DOWN:
					case ACTION_2ND_DOWN:
						indxArr = 0;
						arrPosX[0] = posX[indxLeft];
						arrPosY[0] = posY[indxLeft];
						arrTime[0] = eventTime;
						arrTime[1] = arrTime[2] = arrTime[3] = 0;
						Player.eAction = Player.ACTION_MOVE;							// Move player
						break;
					case ACTION_UP:
					case ACTION_2ND_UP:
						endX = posX[indxLeft];
						endY = posY[indxLeft];
						if(gestureSWnRoll())
                            oTap.bEnable = false;
						else Player.eAction = Player.ACTION_CRUISE;
						break;
					case ACTION_MOVE:
						Player.bRolling = false;
						Player.eAction = Player.ACTION_MOVE;							// Move player up
						if(++indxArr == ARR_SIZE) indxArr = 0;
						arrTime[indxArr] = eventTime;
						arrPosX[indxArr] = posX[indxLeft];
						arrPosY[indxArr] = posY[indxLeft];
						break;
					case ACTION_CANCEL:
						leftFingerId = -1;
						Player.eAction = Player.ACTION_CRUISE;
						break;
				}
		}

		private static	boolean gestureSWnRoll() {
			float ratioRoll = 0, ratioSW = 0;

			for(int i = 0; i < ARR_SIZE; i++) {

				deltaTime = eventTime - arrTime[indxArr];
				deltaX = endX - arrPosX[indxArr];
				deltaY = endY - arrPosY[indxArr];
				if (--indxArr == -1) indxArr = ARR_SIZE - 1;
				if (deltaTime > 150)
					continue;                                            // If time is too less(error) or too much return

				ratioRoll = Math.abs(deltaX / deltaTime);
				ratioSW = Math.abs(deltaY / deltaTime);

				if ((Math.abs(deltaY) < (Math.abs(deltaX)) && ratioRoll > 0.9f)) {        // Check for Plane Roll
					Player.eAction = (deltaX > 0) ? Player.ACTION_ROLL_UP : Player.ACTION_ROLL_DOWN;
					return true;                                                                    // Return that gesture has been registered
				} else if (Adventure.oTarget.bMarked && ratioSW > 0.6f) {                    // Check for ShockWave
					bShockWave = true;
					oTap.bEnable = false;		// Return that gesture has been registered
				} else if(ratioSW > 0.9f){													// Check for ShockWave
					bShockWave = true;
                    oTap.bEnable = false;
                }						// Return that gesture has been registered
			}

			return false;																	// No Gesture Registered
		}
	}
	/************************************************************************************************************************
	 *	Class FireWeapon -- Handles Weapon fire and Weapon Switch
	 ************************************************************************************************************************/
	static class FireWeapon {
		private static float startX, startY, endX, endY, deltaX, deltaY;
		private static long  startTime, endTime, deltaTime;

		private static void check(MotionEvent e) {
			if(eRight != -1 && !bMenu ) 											// If there is an event on right side of screen 
				switch(eRight) {
					case ACTION_DOWN:
					case ACTION_2ND_DOWN:
						startTime = eventTime;
						startX = posX[indxRight];
						startY = posY[indxRight];
						Weapon.eState = Weapon.WEAPON_FIRE;
						break;
					case ACTION_UP:
					case ACTION_2ND_UP:
						endTime = eventTime;
						endX = posX[indxRight];
						endY = posY[indxRight];
						rightFingerId = -1;
						if(!weaponSwitch() && countRight!= 2)							// if there are two fingers on right side, dont stop fire if one finger is up
							Weapon.eState = Weapon.WEAPON_OFF;
						break;
					case ACTION_CANCEL:
						Weapon.eState = Weapon.WEAPON_OFF;
						break;
				}
		}

		private static boolean weaponSwitch() {
			deltaTime = endTime - startTime;
			deltaX    = endX - startX;
			deltaY    = endY - startY;

			if( (Math.abs(deltaY) > Math.abs(deltaX)) && (deltaTime < 300) && (Math.abs(deltaY) > 20) ) {
				Weapon.eState = (deltaY < 0) ? Weapon.WEAPON_PREVIOUS : Weapon.WEAPON_NEXT;
				return true;
			}
			return false;
		}
	}
	/************************************************************************************************************************
	 *	Class Menu -- Handles menu button
	 ************************************************************************************************************************/
	static class Menu {
		private static float pointX, pointY;

		private static void check(MotionEvent e) {
			bMenu = (iMenu == MENU_OFF) ? false: true;								// If menu is displayed, set flag

			if(getGLX(posX[curIndex]) <  1f && getGLY(posY[curIndex]) > (menuButtonY) && getGLY(posY[curIndex]) < (menuButtonY+1f)) {
				if(event == ACTION_UP || event == ACTION_2ND_UP )					// Check if menu button pressed when finger is down
					buttonMenu();
				event = eLeft = eRight = -1;										// If menu button is clicked, dont send event further to gestures
			}

			if(bMenu && (event == ACTION_UP || event == ACTION_2ND_UP))	{			// If menu is diplayed then show menu options
				pointX = getGLX(posX[curIndex], MENU_MAX_X);
				pointY = getGLY(posY[curIndex], MENU_MAX_Y);

				if(iMenu == MENU_PAUSE && pointY > menuResumeY && pointY < menuResumeY +1 && pointX > menuResumeX && pointX < menuResumeX +1)
					buttonResume();
				if(pointY > menuExitY && pointY < menuExitY +1 && pointX > menuExitX && pointX < menuExitX +1)
					buttonExit();
				if(pointY > menuRestartY && pointY < menuRestartY +1 && pointX > menuRestartX && pointX < menuRestartX +1)
					buttonRestart();
			}
		}
		/************************************************************************************************************************
		 *	Button Handlers, put code you want to execute when button is pressed
		 ************************************************************************************************************************/
		static void buttonMenu() {
			iMenu = MENU_PAUSE;
			Mic.stopRecording();
			Adventure.events.dispatch(Events.VIBRATE_50);
			Game.refGameView.requestRender();
			SoundPlayer.sendCommand(Sound.SOUND_PAUSE_ALL);
		}

		static void buttonResume() {
			iMenu = MENU_OFF;
			bPause = false;
			Screen.reset();
			Mic.startRecording();
			ActGame.iBackPressed = 0;												// Set Back button pressed to 0 
			Game.refGameView.requestRender();
			Adventure.events.dispatch(Events.VIBRATE_50);
			SoundPlayer.sendCommand(Sound.SOUND_RESUME_ALL);						// Resume Game Sounds
		}

		static void buttonRestart() {
			Mic.startRecording();
			ActGame.iBackPressed = 0;												// Set Back button pressed to 0 
			Game.refGameView.eGameStatus = Values.GAME_LEVEL_RESTART;				// Restart level that player has resumed game from
			Game.refGameView.requestRender();
			Adventure.events.dispatch(Events.VIBRATE_50);
		}

		static void buttonExit() {
			Pref.getSet(Pref.GAME_EXIT);											// Save preferences for exit game
			Adventure.events.dispatch(Events.VIBRATE_50);
		}
	}
	/************************************************************************************************************************
	 *	METHOD- Sets Axpect ratio, GL X/Y and Character Width/Height
	 ************************************************************************************************************************/
	protected static void init(Activity activity ) {


        int width = 0, height = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        Method mGetRawH = null, mGetRawW = null;

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) { e.printStackTrace();
                } catch (IllegalAccessException e) { e.printStackTrace();
                } catch (InvocationTargetException e) {e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) { e3.printStackTrace();}

        if(width == 0 || height == 0){
            DisplayMetrics dsp;
            dsp = activity.getResources().getDisplayMetrics();
            width = dsp.widthPixels;
            height = dsp.heightPixels;
        }

		PIXELS_X = width;
		PIXELS_Y = height;
		ASPECT_RATIO  = PIXELS_Y / PIXELS_X;										// Aspect ratio

		DEV_MAX_X = MAX_X/ASPECT_RATIO;												// Set X according to device Aspect ratio, for square screen x = 12, y = 9
		CHAR_WIDTH  = 1 / DEV_MAX_Y;												// Character width
		CHAR_HEIGHT = 1 / DEV_MAX_X;												// Character height

		ArcadeHUD.iconMenuX = HUD.iconMenuX = Screen.CHAR_WIDTH *2;
		ArcadeHUD.iconMenuY = HUD.iconMenuY = HUD.iconMenuX/Screen.ASPECT_RATIO;

		Screen.MENU_MAX_X = 1/HUD.iconMenuX;								// Calculate OpnGL Width and Height for Menu Icons
		Screen.MENU_MAX_Y = 1/HUD.iconMenuY;

		butFireX = 0.80f*MENU_MAX_X;						// 3/4th of screen height
		butFireY = 0.85f*MENU_MAX_Y;						// 3/4th of screen width

		menuResumeX =  (MENU_MAX_X/2) - 1.2f;				// Menu icons position
		menuResumeY =  (MENU_MAX_Y/2) - 0.5f;
		menuRestartX = (MENU_MAX_X/2);
		menuRestartY = (MENU_MAX_Y/2) - 1.8f;
		menuExitX =    (MENU_MAX_X/2);
		menuExitY =    (MENU_MAX_Y/2) + 0.8f;
		menuButtonX =   0;
		menuButtonY =   DEV_MAX_Y - 1.0f;

		Values.log("screen","Resolution: "+ (int)Screen.PIXELS_Y+"x"+(int)Screen.PIXELS_X + ", Aspect Ratio: "+ String.format("%.2f",Screen.ASPECT_RATIO)+", GameY/GameX = "+ String.format("%.2f",Screen.DEV_MAX_Y)+"/"+ String.format("%.2f",Screen.DEV_MAX_X));
		Values.log("test","Float maximum Value \n" +Float.MAX_VALUE);
	}

	/************************************************************************************************************************
	 *	METHODS to convert screen coordinated to GL coordinates
	 ************************************************************************************************************************/
	private static float getGLX(float vPixelX)					{ return (1-(vPixelX/PIXELS_X))*DEV_MAX_X;	}
	private static float getGLX(float vPixelX, float vMaxGLX) 	{ return (1-(vPixelX/PIXELS_X))*vMaxGLX;	}
	private static float getGLY(float vPixelY)					{ return (vPixelY/PIXELS_Y)* DEV_MAX_Y;		}
	private static float getGLY(float vPixelY, float vMaxGLY)	{ return (vPixelY/PIXELS_Y)* vMaxGLY;		}

}
