package com.BreakthroughGames.OrigamiWars;

import android.content.Context;
import android.content.SharedPreferences;

public class Pref {

	protected static final int STORY_NEW      = 0;
	protected static final int LEVEL_RESUME   = 1;
	public static final int LEVEL_COMPLETE = 2;
	protected static final int LEVEL_RESTART  = 3;
	protected static final int GAME_EXIT   	  = 4;
	public static final int GAME_OVER      = 5;
	public static final int ARCADE_NEW     = 6;
	protected static final int ARCADE_SAVE    = 7;

	protected static final String PREF_EVENTS  	 = "pEvents";
	protected static final String FBOOK_LIKE 	 = "fbLike";
	private static final String PREF_WEAPON1 	 = "pWeapon1";						// Story Mode Prefs
	private static final String PREF_WEAPON2 	 = "pWeapon2";
	private static final String PREF_WEAPON3	 = "pWeapon3";
	private static final String PREF_START_LEVEL = "pStartLevel" ;
	private static final String PREF_LEVEL_SCORE = "pScoreLevel" ;
	private static final String PREF_STORY_SCORE = "pAdventureScore";
	private static final String PREF_TOP_WINS  	 = "pWinsHighest";
	private static final String PREF_TOP_LOSES 	 = "pLoseHighest";

	private static final String ARCADE_RECORD_CURSES  = "pArcadeMaxWins";			//Arcade Mode Prefs
	private static final String ARCADE_RECORD_DISTANCE  = "pArcadeMaxDistance";
	private static final String ARCADE_RECORD_SCORE = "pArcadeTopScore";
	private static final String ARCADE_WEAPON1   = "pArcadeWeapon1";
	private static final String ARCADE_WEAPON2   = "pArcadeWeapon2";
	private static final String ARCADE_WEAPON3	  = "pArcadeWeapon3";
	protected static boolean  bFB_Like = false;


	/************************************************************************************************************************
	 *	METHOD -- Loads and saves and Updates game Stats in Android Prefers for story mode
	 ************************************************************************************************************************/
	public static void getSet(int vType) {

		if(Game.iMode == Values.ARCADE_MODE || Game.iMode == Values.ARCADE_RESUME)
			prefArcade(vType);
		else
			prefStory(vType);

	}
	/************************************************************************************************************************
	 *	METHOD -- Loads and saves and Updates game Stats in Android Prefers for story mode
	 ************************************************************************************************************************/
	private static void prefStory(int vType) {

		SharedPreferences  pref = Game.refActGame.getPreferences(Context.MODE_PRIVATE);

		switch(vType) {
			case STORY_NEW:
				Events.prefSave(false);												// Load Events
				Mic.calibrate();													// Calibrate Mic for a new Game
				Game.iLevel = -1;													// iLevel#-1 to load progress class resources 
				Game.iStartLevel = 0; 												// For levels = Stage1:(0,2,4) Stage2:(5,6,7) Stage3:(8,9,10)
				Game.scoreLevel  = 0;												// Set level score to 0 for new game
				Player.bEnable = true;
				Player.iLives =  Values.PLAYER_LIVES;

				Weapon.addShots(Weapon.MACHINE_GUN, -1);							// Reset shots to zero 
				Weapon.addShots(Weapon.DBARELL_GUN, -1);
				Weapon.addShots(Weapon.BOLT_GUN,    -1);
				Weapon.setNormalGun();
				Weapon.arLvlShot[Weapon.MACHINE_GUN] = 0;							// Set Level Shots to Zero, Level shots are used to when level is restarted
				Weapon.arLvlShot[Weapon.DBARELL_GUN] = 0;
				Weapon.arLvlShot[Weapon.BOLT_GUN] 	 = 0;
				if(bFB_Like) {
					Weapon.arLvlShot[Weapon.MACHINE_GUN] = Weapon.MACGUN_POWERUP;
					Weapon.addShots(Weapon.MACHINE_GUN, Weapon.MACGUN_POWERUP );
				}
				break;

			case LEVEL_RESUME:
				Events.prefSave(false);												// Load Events 
				Mic.calibrate();													// Calibrate Mic for a new Game
				Adventure.iLevel = -1;												// iLevel#-1 to load progress class resources 
				Adventure.iStartLevel = pref.getInt(PREF_START_LEVEL, 0);
				Adventure.scoreLevel  = pref.getLong(PREF_LEVEL_SCORE,0);

				Weapon.addShots(Weapon.MACHINE_GUN, -1);							// Reset Shots to zero if any 
				Weapon.addShots(Weapon.DBARELL_GUN, -1);
				Weapon.addShots(Weapon.BOLT_GUN,    -1);

				Weapon.arLvlShot[Weapon.MACHINE_GUN] = pref.getInt(PREF_WEAPON1, 0);
				Weapon.arLvlShot[Weapon.DBARELL_GUN] = pref.getInt(PREF_WEAPON2, 0);
				Weapon.arLvlShot[Weapon.BOLT_GUN] 	 = pref.getInt(PREF_WEAPON3, 0);

				Weapon.addShots(Weapon.MACHINE_GUN, Weapon.arLvlShot[Weapon.MACHINE_GUN]);
				Weapon.addShots(Weapon.DBARELL_GUN, Weapon.arLvlShot[Weapon.DBARELL_GUN]);
				Weapon.addShots(Weapon.BOLT_GUN, 	Weapon.arLvlShot[Weapon.BOLT_GUN]);

				Weapon.setNormalGun();
				break;

			case LEVEL_COMPLETE:
				Events.prefSave(true);										// Save Events
				Adventure.events.bEnable = false;									// Clear any event showing up
				Adventure.scoreLevel = Adventure.scoreBoard.getScore();
				Weapon.arLvlShot[Weapon.MACHINE_GUN] = Weapon.getShots(Weapon.MACHINE_GUN);
				Weapon.arLvlShot[Weapon.DBARELL_GUN] = Weapon.getShots(Weapon.DBARELL_GUN);
				Weapon.arLvlShot[Weapon.BOLT_GUN]    = Weapon.getShots(Weapon.BOLT_GUN);

				pref.edit()
						.putLong(PREF_LEVEL_SCORE, Adventure.scoreLevel )					// Save Player score for the current level, if he wants to restart
						.putInt( PREF_START_LEVEL, Adventure.iLevel)						// Level for game resume
						.putInt(PREF_WEAPON1, Weapon.arLvlShot[Weapon.MACHINE_GUN])
						.putInt(PREF_WEAPON2, Weapon.arLvlShot[Weapon.DBARELL_GUN])
						.putInt(PREF_WEAPON3, Weapon.arLvlShot[Weapon.BOLT_GUN])
						.apply();

				break;

			case LEVEL_RESTART:
				Events.prefSave(true);												// Save Events 
				Screen.bPause = false;
				Player.bEnable = true;
				Screen.iMenu = Screen.MENU_OFF;
				Game.refGameView.requestRender();
				SoundPlayer.sendCommand(Sound.SOUND_STOP_ALL);
				Game.refGameView.eGameStatus= Values.GAME_LEVEL_RESTART;			// Restart level that player has resumed game from
				Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_SAVE_SCROLLS] = 0; 	// Reset Total scrolls to 0 
				Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_TOTAL_SCROLLS] = 0; 	// Reset Saved Scrolls to 0 

				Weapon.addShots(Weapon.MACHINE_GUN, -1);							// Reset Shots to zero if any 
				Weapon.addShots(Weapon.DBARELL_GUN, -1);
				Weapon.addShots(Weapon.BOLT_GUN,    -1);

				Weapon.addShots(Weapon.MACHINE_GUN, Weapon.arLvlShot[Weapon.MACHINE_GUN]);
				Weapon.addShots(Weapon.DBARELL_GUN, Weapon.arLvlShot[Weapon.DBARELL_GUN]);
				Weapon.addShots(Weapon.BOLT_GUN, 	Weapon.arLvlShot[Weapon.BOLT_GUN]);
				Weapon.setNormalGun();

				break;

			case GAME_OVER:
				boolean bNoEvent = true;
				if(pref.getLong(PREF_STORY_SCORE, 0) < Game.scoreBoard.getScore())
				{
					pref.edit().putLong(PREF_STORY_SCORE, Game.scoreBoard.getScore()).apply();
					Adventure.events.dispatch(Events.HIGHEST_SCORE);
					bNoEvent= false;
				}
				if(pref.getLong(PREF_TOP_WINS, 0) < (PowerUps.iTotalWins + PowerUps.iLevelWins) )
				{
					pref.edit().putLong(PREF_TOP_WINS, (PowerUps.iTotalWins + PowerUps.iLevelWins)).apply();
					if(bNoEvent)	Adventure.events.dispatch(Events.HIGHEST_WINS);
					bNoEvent= false;
				}
				if(pref.getLong(PREF_TOP_LOSES, 0) < (PowerUps.iTotalLoses + PowerUps.iLevelLoses) )
				{
					pref.edit().putLong(PREF_TOP_LOSES, (PowerUps.iTotalLoses + PowerUps.iLevelLoses)).apply();
					if(bNoEvent)	Adventure.events.dispatch(Events.BIGGEST_LOSER);
					bNoEvent= false;
				}
				if((PowerUps.iTotalWins + PowerUps.iLevelWins) < (PowerUps.iTotalLoses + PowerUps.iLevelLoses))
					if(bNoEvent)	Adventure.events.dispatch(Events.CONSCDER_CLASS);
				break;

			case GAME_EXIT:
				Events.prefSave(true);											// Save events run times
				Screen.bPause = false;
				Screen.iMenu = Screen.MENU_OFF;
				Game.refGameView.requestRender();
				Game.iMode= Values.START_MUSIC_OK;
				SoundPlayer.sendCommand(Sound.SOUND_STOP_ALL);					// Stop all sound effects
				Game.refActGame.finish();
				break;
		}
	}
	/************************************************************************************************************************
	 *	METHOD -- Loads and saves and Updates game Stats in Android Prefers for Arcade Mode
	 ************************************************************************************************************************/
	private static void prefArcade(int vType) {
		SharedPreferences pref = Game.refActGame.getPreferences(0);

		switch(vType) {
			case ARCADE_NEW:
				Mic.calibrate();													// Calibrate Mic for a new Game
				Player.iLives = 1;
				Player.iPower = 4;
				Player.bEnable = true;
				Events.prefSave(false);												// Load Events

				Weapon.addShots(Weapon.MACHINE_GUN, -1);							// Reset shots to zero 
				Weapon.addShots(Weapon.DBARELL_GUN, -1);
				Weapon.addShots(Weapon.BOLT_GUN,    -1);

				Weapon.arLvlShot[Weapon.MACHINE_GUN] = 0;
				Weapon.arLvlShot[Weapon.DBARELL_GUN] = 0;
				Weapon.arLvlShot[Weapon.BOLT_GUN] 	 = 0;
				if(bFB_Like) {
					Weapon.arLvlShot[Weapon.MACHINE_GUN] = Weapon.MACGUN_POWERUP;
					Weapon.addShots(Weapon.MACHINE_GUN, Weapon.MACGUN_POWERUP );
				}
				Weapon.setNormalGun();

				Record.set(Record.ARCADE_SCORE,(int)pref.getLong(ARCADE_RECORD_SCORE, 0));
				Record.set(Record.ARCADE_DISTANCE,  pref.getInt(ARCADE_RECORD_DISTANCE, 0));
				Record.set(Record.ARCADE_CURSES,    pref.getInt(ARCADE_RECORD_CURSES, 0));

				Arcade.scoreHighest.setScore(Record.get(Record.ARCADE_SCORE));
				Game.odoMeter = (Record.get(Record.ARCADE_SCORE) == 0) ? -8: -3;

				Record.setTrue(Record.ARCADE_SCORE, 	Record.SHOW);
				Record.setTrue(Record.ARCADE_DISTANCE, 	Record.SHOW);

				if(Record.get(Record.ARCADE_CURSES) > 0)
					Record.setTrue(Record.ARCADE_CURSES,   	Record.SHOW);
				break;

			case LEVEL_RESTART:
				Player.iLives = 1;
				Events.prefSave(true);												// Load Events 
				Screen.bPause = false;
				Player.bEnable = true;
				Screen.iMenu = Screen.MENU_OFF;
				Game.refGameView.requestRender();
				SoundPlayer.sendCommand(Sound.SOUND_STOP_ALL);
				Game.refGameView.eGameStatus= Values.GAME_LEVEL_RESTART;			// Restart level that player has resumed game from

				Weapon.addShots(Weapon.MACHINE_GUN, -1);							// Reset Shots to zero if any 
				Weapon.addShots(Weapon.DBARELL_GUN, -1);
				Weapon.addShots(Weapon.BOLT_GUN,    -1);

				Weapon.addShots(Weapon.MACHINE_GUN, Weapon.arLvlShot[Weapon.MACHINE_GUN]);
				Weapon.addShots(Weapon.DBARELL_GUN, Weapon.arLvlShot[Weapon.DBARELL_GUN]);
				Weapon.addShots(Weapon.BOLT_GUN, 	Weapon.arLvlShot[Weapon.BOLT_GUN]);
				Weapon.setNormalGun();

				if(Record.get(Record.ARCADE_SCORE) > 0)	{							// if Score is greater then zero start showing the records
					Record.setTrue(Record.ARCADE_SCORE, 	Record.SHOW);
					Record.setTrue(Record.ARCADE_DISTANCE, 	Record.SHOW);
				}
				if(Record.get(Record.ARCADE_CURSES) > 0)
					Record.setTrue(Record.ARCADE_CURSES,   	Record.SHOW);
				break;

			case ARCADE_SAVE:
				pref.edit()
						.putLong( ARCADE_RECORD_SCORE,    Record.get(Record.ARCADE_SCORE))
						.putInt(  ARCADE_RECORD_DISTANCE, Record.get(Record.ARCADE_DISTANCE))
						.putInt(  ARCADE_RECORD_CURSES,   Record.get(Record.ARCADE_CURSES))
						.putInt(  ARCADE_WEAPON1, Weapon.arLvlShot[Weapon.MACHINE_GUN])
						.putInt(  ARCADE_WEAPON2, Weapon.arLvlShot[Weapon.DBARELL_GUN])
						.putInt(  ARCADE_WEAPON3, Weapon.arLvlShot[Weapon.BOLT_GUN])
						.apply();
				break;

			case GAME_OVER:
				if(PowerUps.iLevelWins <  PowerUps.iLevelLoses)
					Adventure.events.dispatch(Events.CONSCDER_CLASS);
				break;

			case GAME_EXIT:
				Events.prefSave(true);												// Save events run times
				Screen.bPause = false;
				Screen.iMenu = Screen.MENU_OFF;
				Game.refGameView.requestRender();
				Game.iMode = Values.START_MUSIC_OK;
				prefArcade(ARCADE_SAVE);
				SoundPlayer.sendCommand(Sound.SOUND_STOP_ALL);						// Stop all sound effects
				Game.refActGame.finish();
				break;
		}

	}


}
