package com.BreakthroughGames.OrigamiWars.stages;

import com.BreakthroughGames.OrigamiWars.Arcade;
import com.BreakthroughGames.OrigamiWars.ArcadeHUD;
import com.BreakthroughGames.OrigamiWars.Events;
import com.BreakthroughGames.OrigamiWars.Game;
import com.BreakthroughGames.OrigamiWars.Player;
import com.BreakthroughGames.OrigamiWars.Pref;
import com.BreakthroughGames.OrigamiWars.R;
import com.BreakthroughGames.OrigamiWars.Screen;
import com.BreakthroughGames.OrigamiWars.Sound;
import com.BreakthroughGames.OrigamiWars.SoundPlayer;
import com.BreakthroughGames.OrigamiWars.Values;

import javax.microedition.khronos.opengles.GL10;

public class Stage_Arcade extends Arcade {

	// Method call Sequence for New Game: loadingScreen() -> LoadLevel(){ super.Reset() InitObjects() resumeLevel()}
	// Method call Sequence for Resume Game :  resumeLevel();
	/*============================================Start Class Methods=============================================================*/	

	@Override
	public void resumeLevel() {
		BG_HD_Front.resumeTexture();
		BG_HD_Middle.resumeTexture();
		BG_HD_Sky.resumeTexture();
		BG_HD_Cloud.resumeTexture();	
	}
	/************************************************************************************************************************
	 *    METHOD--  Load Level Called only when App starts first time, unloadLevel called for every Level 
	 ************************************************************************************************************************/
	protected void loadLevel()	{	
		switch(ran.nextInt(5)) {
		case 0: SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL1); break;
		case 1: SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL2); break;
		case 2: SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL3); break;
		case 3: SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL4); break;
		case 4: SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL5); break;
		}

		reset();
		initObjects();
		//resumeLevel();
		BG_HD_Front.loadTexture(new int[]{ 
				R.drawable.bg_front_01, R.drawable.bg_front_02, R.drawable.bg_front_03, R.drawable.bg_front_04,
				R.drawable.bg_front_05, R.drawable.bg_front_06, R.drawable.bg_front_07, R.drawable.bg_front_08,
				R.drawable.bg_front_09, R.drawable.bg_front_10, R.drawable.bg_front_11, R.drawable.bg_front_12,
				R.drawable.bg_front_13, R.drawable.bg_front_14, R.drawable.bg_front_15, R.drawable.bg_front_16,
				R.drawable.bg_front_17, R.drawable.bg_front_18, R.drawable.bg_front_19, R.drawable.bg_front_20,
				R.drawable.bg_front_21, R.drawable.bg_front_22,
		});

		BG_HD_Middle.loadTexture(new int[]{  
				R.drawable.bg_middle_01, R.drawable.bg_middle_02, R.drawable.bg_middle_03, R.drawable.bg_middle_04,
				R.drawable.bg_middle_05, R.drawable.bg_middle_06, R.drawable.bg_middle_07, R.drawable.bg_middle_08,
				R.drawable.bg_middle_09, R.drawable.bg_middle_10, R.drawable.bg_middle_11, R.drawable.bg_middle_12,
				R.drawable.bg_middle_13, R.drawable.bg_middle_14, R.drawable.bg_middle_15, R.drawable.bg_middle_16,
		});

		BG_HD_Sky.loadTexture(new int[]{ 
				R.drawable.bg_sky_01, R.drawable.bg_sky_02, R.drawable.bg_sky_03, R.drawable.bg_sky_04,
				R.drawable.bg_sky_05, R.drawable.bg_sky_06, R.drawable.bg_sky_07, R.drawable.bg_sky_08,
				R.drawable.bg_sky_09, R.drawable.bg_sky_10, R.drawable.bg_sky_11, 
		});

		BG_HD_Cloud.loadTexture(new int[]{ 
				R.drawable.bg_clouds_01, R.drawable.bg_clouds_02, R.drawable.bg_clouds_03, R.drawable.bg_clouds_04,
				R.drawable.bg_clouds_05, R.drawable.bg_clouds_06, R.drawable.bg_clouds_07, R.drawable.bg_clouds_08,
				R.drawable.bg_clouds_09, R.drawable.bg_clouds_10, R.drawable.bg_clouds_11, R.drawable.bg_clouds_12,
				R.drawable.bg_clouds_13, R.drawable.bg_clouds_14, R.drawable.bg_clouds_15, R.drawable.bg_clouds_16,
				R.drawable.bg_clouds_17, R.drawable.bg_clouds_18, R.drawable.bg_clouds_19, R.drawable.bg_clouds_20,
				R.drawable.bg_clouds_21, R.drawable.bg_clouds_22, R.drawable.bg_clouds_23, R.drawable.bg_clouds_24,
				R.drawable.bg_clouds_25, R.drawable.bg_clouds_26, R.drawable.bg_clouds_27, R.drawable.bg_clouds_28,
				R.drawable.bg_clouds_29, R.drawable.bg_clouds_30, R.drawable.bg_clouds_31, R.drawable.bg_clouds_32,
				R.drawable.bg_clouds_33, R.drawable.bg_clouds_34, R.drawable.bg_clouds_35, R.drawable.bg_clouds_36,
				R.drawable.bg_clouds_37, R.drawable.bg_clouds_38, R.drawable.bg_clouds_39, R.drawable.bg_clouds_40,
				R.drawable.bg_clouds_41, R.drawable.bg_clouds_42, R.drawable.bg_clouds_43, R.drawable.bg_clouds_44,
		});	
		Pref.getSet(Pref.ARCADE_NEW);														// Set preference for
	}
	/************************************************************************************************************************
	 *   METHOD - When player restarts a level, set level stats as they were at the start of the level 
	 ************************************************************************************************************************/
	public void initObjects() {	

		for(int i=MAX_ENEMIES; i < MAX_OBJECTS; i++) object[i].create(Values.SCROLL_NORMAL, 0, 0,0 );

		MAX_Actv_Enemy = 6;
	}														
	/************************************************************************************************************************
	 *  METHOD - Runs a frame, shows Head up display
	 ************************************************************************************************************************/
	public void runOneFrame() {
		calculateStats();																	// Calculate level speed and Maximum Enemies
		drawBackgrounds();
		if((bSecond && ran.nextBoolean()) || (iActvEnemy <  (MAX_Actv_Enemy/2))) 
			createEnemy(null);																// Check and create enemy every second
		moveObjects();
		drawForegrounds();
		detectCollision();
		headUpDisplay();
	}														
	/************************************************************************************************************************
	 *  METHOD - Runs a frame, shows Head up display
	 ************************************************************************************************************************/
	static void headUpDisplay() {
		ArcadeHUD.showStats();																// Show Stats HUD - Life, weapon, fire etc
		switch(events.iTimer) {
		case 1:		events.dispatch(Events.READY);  			break;
		case 60:	events.dispatch(Events.SURVIVE);  			break;
		case 120:	events.dispatch(Events.SCORE);  			break;
		case 240:	events.dispatch(Events.TOUCH_TO_MOVE);  	break; 	 
		case 480:	events.dispatch(Events.TOUCH_TO_FIRE);  	break;
		case 2000:	events.dispatch(Events.BLOW_ON_SCREEN);  	break;
		}
		events.draw();
	}
	/************************************************************************************************************************
	 *   METHOD- Draws Background for the level
	 ***********************************************************************************************************************/
	public static void drawBackgrounds(){
		BG_HD_Sky.transform(1.05f, 1.0f, 0.0f, 0);											// Draw Sky
		BG_HD_Middle.transform(1.05f, 1.0f, -0.15f, 0);										// Draw Sky
		BG_HD_Front.transform(1.05f, 1.0f, -0.05f, 0);										// Draw Sky

		if(Player.iLives> 0){
			BG_HD_Sky.draw(0.0f, (Values.SCROLL_SPEED/4)* Game.SPEED_MULT);
			BG_HD_Middle.draw(0.0f, (Values.SCROLL_SPEED/(2*1.295f))*Game.SPEED_MULT);
			BG_HD_Front.draw(0.0f, (Values.SCROLL_SPEED/2)*Game.SPEED_MULT);
		} else {
			BG_HD_Sky.draw(0.0f, 0.0f);
			BG_HD_Middle.draw(0.0f, 0.0f);
			BG_HD_Front.draw(0.0f, 0.0f);
		}

	}	
	public static void drawForegrounds(){
		if(Player.iLives> 0){
			BG_HD_Cloud.transform(1.05f, 1.0f, -0.05f, 0);										// Draw Sky
			BG_HD_Cloud.draw(0.0f, (Values.SCROLL_SPEED)*Game.SPEED_MULT);
		} else {
			BG_HD_Cloud.transform(1.05f, 1.0f, -0.05f, 0);										// Draw Sky
			BG_HD_Cloud.draw(0.0f, 0.0f);
		}

	}	
	/************************************************************************************************************************
	 *   METHOD- Draws Background for the level
	 ***********************************************************************************************************************/
	public byte checkGameStatus() {
		if(!events.bEnable && Player.iLives <= 0 )											// If player died, do events in sequence
			switch(iSequence) {
			case 0: iSequence++;	Pref.getSet(Pref.GAME_OVER);		break;
			case 1:	iSequence++;	events.dispatch(Events.GAME_OVER);	break;
			case 2: Screen.iMenu =  Screen.MENU_GAMEOVER; 	return Values.GAME_OVER;
			}		
		return Values.GAME_RUNNING;
	}	

}
