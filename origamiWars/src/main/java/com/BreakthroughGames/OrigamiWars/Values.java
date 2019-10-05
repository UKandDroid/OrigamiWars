package com.BreakthroughGames.OrigamiWars;

public class Values {
	protected static boolean bDebug = BuildConfig.DEBUG;							// Enable App Logging, Disable for Released Version

	public static float BASIC_SPEED = 0.2f/Game.GAME_FPS;				// Standard Speed 0.2f/60 = 0.0033 f			
	public static float SCROLL_SPEED = BASIC_SPEED;						// BGs Scroll speed
	public static float SPEED1 = BASIC_SPEED * 3.5f;					// 0.01167 f
	public static float SPEED2 = SPEED1 * 2;
	public static float SPEED3 = SPEED1 * 3;
	public static float SPEED4 = SPEED1 * 4;
	public static float SPEED5 = SPEED1 * 5;
	public static float SPEED6 = SPEED1 * 5.4f;	
	public static float SPEED_SHOT_SLOW = SPEED1 * 6.2f;				// Reserved for EnemyFire// Reserved for EnemyFire
	public static float SPEED_SHOT_MED  = SPEED1 * 6.5f;				// Reserved for EnemyFire// Reserved for EnemyFire
	public static float SPEED_SHOT_FAST = SPEED1 * 7f;					// Reserved for EnemyFire
	
	public static final int PLAYER_POWER = 4;
	public static final int PLAYER_LIVES = 3;
																		// ***PATH*******************************ENEMY-FIRE********
	public static final int ENEMY_LEAF 			= 1;					// WIND Path 							
	public static final int ENEMY_BUTTERFLY 	= 2;					// RANDOM								SHOT_Small,  PATH_Intercept,  FIRE_Once
	public static final int ENEMY_WASP			= 3;					// INLINE (Slow, Med) 					SHOT_Small,  PATH_Straight,   FIRE_Twice
	public static final int ENEMY_DRAGONFLY 	= 4;					// STRAIGHT (Slow, Med, Fast)			SHOT_Small,  PATH_Intercept,  FIRE_Twice
	public static final int ENEMY_HORNET		= 5;					// INTERCEPT_SLOW / MED					SHOT_Small,  PATH_Straight,   FIRE_Always
	public static final int ENEMY_BAT 			= 6;					// SINE									SHOT_Medium, PATH_Intercept,  FIRE_2ndTime
	public static final int ENEMY_VULTURE 		= 7;					// INTERCEPT_MED						SHOT_Medium, PATH_Straight,   FIRE_Twice
	public static final int ENEMY_PTEROSAUR 	= 8;					// INTERCEPT_MED / FAST					SHOT_Medium, PATH_Inline,     FIRE_Always
	public static final int ENEMY_DRAGON 		= 9;					// INLINE_FAST							SHOT_Fire,	 PATH_Inline, 	  FIRE_Always 
	public static final int ENEMY_END			= 10;					// Index of last enemy+1
	
	public static final int OBJECT_OBSTACLE 	= 11;					// Obstacle ID
	public static final int POWERUP_EXTRALIFE  	= 12;					// -- IDs of PUps and other items
	public static final int POWERUP_DBARELL 	= 13;
	public static final int POWERUP_MACHINEGUN  = 14;
	public static final int POWERUP_LIGHTNING  	= 15;
	public static final int POWERUP_INVINCIBLE 	= 16;
	public static final int POWERUP_TIME_SLOW  	= 17;					// -- IDs of PUps and other items
	public static final int POWERUP_CURSE_BREAK = 18;					// Invincible, when curse is broken, no Sprite
	
	public static final int SCROLL_NORMAL 		= 19;
	public static final int SCROLL_EVIL   		= 20;  
	public static final int CURSE_PAPER 		= 21;
	public static final int CURSE_SCISSOR 		= 22;
	public static final int CURSE_STONE   		= 23;
	public static final int MAGIC_PAPER   		= 24;
	public static final int MAGIC_SCISSOR 		= 25;
	public static final int MAGIC_STONE   		= 26;					
	public static final int FIRE_ENEMY 			= 27; 		 
	public static final int MAX_OBJECTS			= 28;
																		// ****************************     PATHS TYPE     ****************************
	public static final int PATH_INTERCEPT_SLOW = 1; 					// Intercept by Calculating Slope Once
	public static final int PATH_INTERCEPT_MED 	= 2; 						
	public static final int PATH_INTERCEPT_FAST = 3;
	public static final int PATH_INLINE_SLOW 	= 4; 					// Follows Player Y Position
	public static final int PATH_INLINE_MED 	= 5; 						
	public static final int PATH_INLINE_FAST 	= 6; 						
	public static final int PATH_STRAIGHT_SLOW	= 7; 					//  from Right to Left with random angle
	public static final int PATH_STRAIGHT_MED	= 8; 						
	public static final int PATH_STRAIGHT_FAST	= 9; 						
	public static final int PATH_SINE_WAVE 		= 10; 
	public static final int PATH_RANDOM 		= 11;					// Random up and down, no fixed pattern
	public static final int PATH_WIND 			= 12;					
	public static final int PATH_STATIONARY 	= 13;					// Moves straight as object is stationary and plane is moving towards it
	public static final int PATH_AVOID 			= 14;					// Avoid the plane, move up or down if plane comes in-line
	public static final int PATH_ATTRACT		= 15;					// Attracts curses and Magic towards the player
	public static final int SHOW_MAGIC  		= 16;					// Stays on the same spot on the screen.
																		
	public static final int TYPE_ENEMY 			= 1;
	public static final int TYPE_MAGIC 			= 2;
	public static final int TYPE_CURSE 			= 4;
	public static final int TYPE_POWERUP 		= 8;
	public static final int TYPE_SCROLL 	 	= 16;
	public static final int TYPE_OBSTACLE		= 32;
	public static final int TYPE_ENEMY_FIRE		= 64;
	public static final int TYPE_WIND_STABLE    = 22;					// Magic+Curse+scrolls not effected by windBlowing
																		
	public static final int SCORE_ORIGAMI_PERC 	= 10;
	public static final int SCORE_TIME 		   	= 10;
	public static final int SCORE_WIN_LOSE 	   	= 20;
																		// ****************************  VERTICAL POSITION ***************************
	public static final byte GAME_LOAD_SCREEN 	= 0;					// Level load screen
	public static final byte GAME_LEVEL_LOAD 	= 1;
	public static final byte GAME_RUNNING 		= 2;
	public static final byte GAME_LEVEL_STATS 	= 3;
	public static final byte GAME_LEVEL_COMPLETE= 4;
	public static final byte GAME_LEVEL_RESUME 	= 5;
	public static final byte GAME_LEVEL_RESTART = 6;
	public static final byte GAME_OVER 			= 7;
	public static final byte GAME_COMPLETE 		= 8;
	
	public static final int START_ERROR 		=-1;
	public static final int START_MUSIC_OK 		= 0;
	public static final int STORY_MODE  		= 1;
	public static final int STORY_RESUME_LEVEL 	= 2;
	public static final int STORY_RESUME_PAUSE 	= 3;
	public static final int ARCADE_MODE 		= 4;
	public static final int ARCADE_RESUME  		= 5;
																		// ****************************  LEVEL STATISTICS ****************************
	public static final int LEVEL_MIN_ENEMIES 	= 0;					// Min Enemies for a level
	public static final int LEVEL_MAX_ENEMIES 	= 1;					// Max Enemies for a level	
	public static final int LEVEL_CURSE_PERC  	= 2;					// % of curses & PUp(100- curse)%  
	public static final int LEVEL_MAGIC_TIME  	= 3;					// Time for counter curse
	public static final int LEVEL_EGG_TIME    	= 4;					// Magic egg stay visible time
	public static final int LEVEL_CURSE_SPEED 	= 5;					// Game speed when player is cursed
	public static final int LEVEL_MAX_SPEED   	= 6;					// Maximum speed for a level, 
	public static final int LEVEL_SAVE_SCROLLS  = 7;					// Collected Scrolls
	public static final int LEVEL_TOTAL_SCROLLS = 8;					// Total Scrolls
	public static final int LEVEL_COMPL_TIME  	= 9;					// Level complete time
	public static final int LEVEL_DISTANCE  	= 10;					// Level Total Time
	public static final int LEVEL_CLOUD_TYPE   	= 11;
	public static final int LEVEL_MIN_OBST    	= 12;					// Minimum obstruction Difficulty for a level
	public static final int LEVEL_MAX_OBST    	= 13;					// Maximum Obstruction Difficulty for a level
	public static final int LEVEL_OBST_FREQ   	= 14; 

	public static int ARR_POWER[] 		= new int[MAX_OBJECTS];
	public static int ARR_SPRITES[] 	= new int[MAX_OBJECTS];
	public static int ARR_TEXTURES[] 	= new int[MAX_OBJECTS];					
	public static int ARR_SPRITE_LEAF[] = {0,1,8,9};							
	public static float LEVEL_STATS[][]	= new float[12][16];
	public static final String ARR_NameFromID[]={ "*****", "LEAF", "BUTTERFLY", "WASP", "DRAGONFLY", "HORNET", "BAT", "VULTURE", "PETROSAURUS", "DRAGON", "******",
													 "OBSTACLE", "PUP_EXTRALIFE", "PUP_DOUBLEFIRE", "PUP_FASTFIRE", "PUP_LIGHTNING", "PUP_INVINCIBLE", "NORMAL_EGG",
													 "EVIL_EGG", "CURSE_PAPER", "CURSE_SCISSOR", "CURSE_STONE", "MAGIC_PAPER", "MAGIC_SCISSOR", "MAGIC_STONE", 
													 "PUP_CURSE_BREAK_INVIN",  "Array Index out of Bounds"	}; 
	/************************************************************************************************************************
	*	METHOD - Genric Methods, initilizes arrays 
	************************************************************************************************************************/
	protected static void setLevelStats(int vLevel, float vMinEnemy, float vMaxEnemy, float vCursPerc, float cCountTime, float vEggVisible, float vCurseSpeed, float vMaxSpeed,  float vLevelDist, float vObstType, float vObstMin, float vObstMax, float vObstFreq)
	{
		LEVEL_STATS[vLevel][LEVEL_MIN_ENEMIES]  = vMinEnemy;
		LEVEL_STATS[vLevel][LEVEL_MAX_ENEMIES]  = vMaxEnemy;
		LEVEL_STATS[vLevel][LEVEL_CURSE_PERC]   = vCursPerc;
		LEVEL_STATS[vLevel][LEVEL_MAGIC_TIME]   = cCountTime;
		LEVEL_STATS[vLevel][LEVEL_EGG_TIME]     = vEggVisible;
		LEVEL_STATS[vLevel][LEVEL_CURSE_SPEED]  = vCurseSpeed;
		LEVEL_STATS[vLevel][LEVEL_MAX_SPEED]    = vMaxSpeed;
		LEVEL_STATS[vLevel][LEVEL_DISTANCE]  	= vLevelDist;
		LEVEL_STATS[vLevel][LEVEL_MIN_OBST]     = vObstMin;
		LEVEL_STATS[vLevel][LEVEL_MAX_OBST]     = vObstMax;
		LEVEL_STATS[vLevel][LEVEL_OBST_FREQ]    = vObstFreq;
		LEVEL_STATS[vLevel][LEVEL_CLOUD_TYPE]   = vObstType;
	}

	protected static void addObject(int vType, int vTexture, int vSprite, int vPower) {
		ARR_TEXTURES[vType] = vTexture;
		ARR_SPRITES[vType]  = vSprite;
		ARR_POWER[vType] = vPower;
	}
	
	protected static int clamp(int vNum, int vMin, int vMax) {
		if(vNum > vMax)			return vMax;
		else if(vNum < vMin)	return vMin;
		else					return vNum;
	}

	protected static float clamp(float vNum, float vMin, float vMax) {
		if(vNum > vMax)			return vMax;
		else if(vNum < vMin)	return vMin;
		else					return vNum;
	}

	protected static double clamp(double vNum, double vMin, double vMax) {
		if(vNum > vMax)			return vMax;
		else if(vNum < vMin)	return vMin;
		else					return vNum;
	}

	protected static void log(String tag, String message)	{	if(bDebug)	android.util.Log.d(tag, message);	}


}
