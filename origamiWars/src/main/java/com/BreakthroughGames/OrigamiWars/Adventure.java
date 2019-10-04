package com.BreakthroughGames.OrigamiWars;

import javax.microedition.khronos.opengles.GL10;

public class Adventure extends Game {
	protected static HUD gameHUD;													// Show HUD statistics during game and level end

	public Adventure(){}
	Adventure(GL10 glRef) {
		super(glRef); 
		for(int i=0; i < MAX_OBJECTS; i++) object[i] = new Object();					// Initilize Objects
		for(int i=0; i < Weapon.MAX_SHOTS; i++)	fire[i] = new Weapon();					// Initilize weapons + ShockWave		
	}
	
	public void initObjects() {}														// Methods Overriden by children
	public void runOneFrame() {}														
	public byte checkGameStatus() { iLevel = iStartLevel; return Values.GAME_LOAD_SCREEN;}
/************************************************************************************************************************
*    METHOD -- Called when before loading the resources, used to display loading screen
************************************************************************************************************************/
	public void loadingScreen()	
	{Values.log("MethodCall", "Game::setLoadingScreen() Called");
		
		BG_Middle.loadTexture(R.drawable.event_loading, GL10.GL_CLAMP_TO_EDGE);
		Draw.transform(0.22f, 1, 2, 0);
		BG_Middle.draw(0.0f, 0);
	}
/************************************************************************************************************************
*    METHOD--  Called  when Phone wakes up, or game comes to foreground from background
************************************************************************************************************************/
	public void resumeLevel()	
	{Values.log("MethodCall", "Game::ResumeLevel() Called");

		
		Screen.reset();	
		loadTextures();
		gameHUD = new HUD();
		scoreBoard.setPosition(0, 4.2f, 0.34f);					
		
		HUD.init(txtHudLives.iTexture, txtHudWeapon.iTexture, txtHudMenu.iTexture, txtPSS.iTexture);
		
		//					[Level]	[Enemy]  	[Curse:%	Time	ScrollTime	Speed]		MaxSpeed	LvlLength	[Obstacles:	Type			Min,	Max		Frequency]
		Values.setLevelStats(1,  	1, 	2,  	0.10f, 		1520,  	680,		0.80f,   	1.05f, 		50, 		Obstacle.WHITE_CLOUDS, 	 	1,  	2,  	0.10f); 
		Values.setLevelStats(3,		2, 	2,  	0.20f, 		1460,  	680, 		0.80f,   	1.08f, 		65, 		Obstacle.WHITE_CLOUDS, 	 	1,  	3,  	0.15f);
		Values.setLevelStats(5, 	2, 	3,  	0.30f, 		1400,  	680, 		0.80f,   	1.10f, 		80, 		Obstacle.WHITE_CLOUDS,  	2,  	3,   	0.20f);
		Values.setLevelStats(6,		2, 	3,  	0.35f, 		1200,   420, 		0.80f,   	1.15f, 		100, 		Obstacle.DARK_CLOUDS,  	 	2,  	4,  	0.20f);
		Values.setLevelStats(7, 	2, 	3,  	0.40f, 		1140,   420, 		0.85f,   	1.20f, 		120, 		Obstacle.DARK_CLOUDS,    	2,  	5,  	0.20f);
		Values.setLevelStats(8, 	2, 	3,  	0.45f,		1080,   420, 		0.90f,   	1.20f, 		140, 		Obstacle.DARK_CLOUDS,	 	3,  	6,  	0.25f);
		Values.setLevelStats(9, 	2, 	4,  	0.45f, 		840,   	320, 		0.90f,   	1.25f, 		170,		Obstacle.THUNDER_CLOUDS, 	3, 		7, 		0.30f);
		Values.setLevelStats(10,	2, 	4,   	0.50f, 		780,   	320, 		0.95f,   	1.25f, 		190,		Obstacle.THUNDER_CLOUDS, 	4, 		8, 		0.30f);
		Values.setLevelStats(11,	2, 	4,   	0.55f, 		720,   	320, 		0.95f,   	1.30f, 		220,		Obstacle.THUNDER_CLOUDS, 	4, 		9, 		0.35f);	

	}
/************************************************************************************************************************
*    METHOD--  Load Level Called only when App starts first time, unloadLevel called for every Level 
************************************************************************************************************************/
	protected void loadLevel()
	{Values.log("MethodCall", "Game::loadLevel() Called");
	
		loadObstacles(); 																// Load all obstacles
		resumeLevel();
	}

	public void levelComplete()	
	{Values.log("MethodCall", "Progress::unloadLevel() Called");

		SoundPlayer.sendCommand(Sound.SOUND_STOP_ALL);									// Stop all sounds for the level			
		Pref.getSet(Pref.LEVEL_COMPLETE);												// Update Game stats in Preferences
		PowerUps.iTotalWins  += PowerUps.iLevelWins;									// Save wins for everylevel
		PowerUps.iTotalLoses += PowerUps.iLevelLoses;
	}
/************************************************************************************************************************
 *   METHOD - When player restarts a level, set level stats as they were at the start of the level 
************************************************************************************************************************/
	protected void levelRestart()
	{Values.log("MethodCall", "Game::reload() Called");
	
		Pref.getSet(Pref.LEVEL_RESTART);												// Reset the variables load them from prefs
		reset();
	}	
/************************************************************************************************************************
*  METHOD - Resets generic level stats, called at the start of every level called by: loadLevel(), levelRestart()
************************************************************************************************************************/
	public void reset()
	{Values.log("MethodCall", "Game::reset() Called");
			
		Screen.reset();																	// Reset fingers index in screen class
		pssGame.reset();																// Reset PaperScissor Stone stats
		events.reset();																	// Reset any event that been displayed
		player.spawn();
		setSpeed(1);
		resetSpeed();
		BG_Middle.scrollY = -1; 
		scoreBoard.setScore(scoreLevel);												// Set the score for that level	
		SoundPlayer.setVolume(1.0f, 0.5f);												// Set sound level to half when level starts
		Weapon.eState = Weapon.WEAPON_OFF;
		Player.iLives = Values.PLAYER_LIVES;											// For every level set 3-lives 
		Player.iPower = Values.PLAYER_POWER;
		odoMeter = iActvEnemy = HUD.iCounter = 0;
		CUR_Obst_Diff = MAX_Actv_Enemy = 0;
		Weapon.shotsFired = enemyDestroyed = iSequence = 0;
		BG_Back.scrollY = BG_Front.scrollY = events.iTimer = lastShotTime = 0;
				
		for(int i = 0; i < MAX_ACTIVE_OBST; i++)	actObstacles[i] = null;				// Reset Obstacles
		for(int i = 0; i < Weapon.MAX_SHOTS; i++)	fire[i].create(txtPlaneFire.iTexture, 0);	 // Reset bullets
		
	}
/************************************************************************************************************************
*	METHOD - Calculates level stats, like Speed, Enemies to display, Volume
************************************************************************************************************************/
		protected static void calcLevelStats()
		{	
			events.iTimer++;															// Tick Events timer
			pssGame.checkStatus();														// Check if Origami curse time has runout
			bSecond = (events.iTimer % GAME_FPS == 0);									// Calculate once in every second
			if(bSecond)
				{
				float temVol 		= (SPEED_MULT*SPEED_MULT);
				int cloudType  		= (int) Values.LEVEL_STATS[iLevel][Values.LEVEL_CLOUD_TYPE];
				float lvlMaxSpeed 	= Values.LEVEL_STATS[iLevel][Values.LEVEL_MAX_SPEED];
				float lvlMinEnemies = Values.LEVEL_STATS[iLevel][Values.LEVEL_MIN_ENEMIES];
				float lvlMaxEnemies = Values.LEVEL_STATS[iLevel][Values.LEVEL_MAX_ENEMIES];
				float lvlDistance   = Values.LEVEL_STATS[iLevel][Values.LEVEL_DISTANCE];
				float lvlMinObst    = Values.LEVEL_STATS[iLevel][Values.LEVEL_MIN_OBST];
				float lvlMaxObst    = Values.LEVEL_STATS[iLevel][Values.LEVEL_MAX_OBST];
				float lvlObstFreq 	= Values.LEVEL_STATS[iLevel][Values.LEVEL_OBST_FREQ];
				
				CUR_Obst_Diff  = (int)(lvlMinObst + (odoMeter/lvlDistance)*(lvlMaxObst - lvlMinObst));
				CUR_Obst_Diff  = (int)Values.clamp(CUR_Obst_Diff, 0, lvlMaxObst);
				MAX_Actv_Enemy = (int)(lvlMinEnemies + (odoMeter/lvlDistance)*(lvlMaxEnemies - lvlMinEnemies));
				MAX_Actv_Enemy = (int)Values.clamp(MAX_Actv_Enemy, 0, lvlMaxEnemies);
				
				if(!pssGame.bIsCursed && SPEED_MULT < lvlMaxSpeed) 								// Increase speed only when palyer is not cursed
					addSpeed(0.008f);
		
				if(ran.nextFloat() < lvlObstFreq ) calcObstacles(Values.PATH_STRAIGHT_SLOW, ran.nextInt(cloudType+1) );	// Set obstacles for the level, based on obstacle difficulty
				if(!pssGame.bIsCursed)	temVol *= SPEED_MULT;
					
				SoundPlayer.setVolume(temVol, SoundPlayer.iBGBaseVolume ); 						// Increase sound exponentialy
				if(SoundPlayer.iBGBaseVolume < 1.0f)	SoundPlayer.iBGBaseVolume += 0.1f;

				if(!Mic.bBlowing) switch(ran.nextInt(30))
						{
						case 7: case 13: case 17: case 25:	SoundPlayer.playSound(Sound.WIND1, -0.5f);  	break;
						case 3: if(!pssGame.bIsCursed)  	SoundPlayer.playSound(Sound.WIND2, -0.5f);		break;
						}				
				Values.log("GameStats","Enemies T/D:A: "+Integer.toString(LEVEL_ENEMIES) + "/"+ Integer.toString(enemyDestroyed) + ":" + MAX_Actv_Enemy +" ,  Speed: "+ String.format("%.2f", Game.SPEED_MULT) + "  Shots: "+Weapon.shotsFired +"  Distance: "+ String.format("%.2f", odoMeter));
				}
		}
/************************************************************************************************************************
*    METHOD -- Creates new Enemy 
************************************************************************************************************************/
	protected static void createEnemy(Object obj)
	{
		float tPos= 0;
		int tType = 0, tNext = 0;
		
		for(tType = Values.ENEMY_LEAF; tType < Values.ENEMY_END ; tType++)						// Get the first type of enemy thats still left, Checking Distance array, starting from leaf	
			if(arDistance[tType] != null)	break;												// if Distance[EnemyType] == null, that means all enemies of this type are created, move to next type

		for(int i = tType; i < Values.ENEMY_END ; i++)											// Now compare its distance to next available enemy, and choose which ever is comes first by distance												
			{
			for(tNext = i+1; tNext < Values.ENEMY_END ; tNext++)										
				if(arDistance[tNext] != null)	break;											
			
			if(tNext == Values.ENEMY_END) break;
				
			if(arDistance[tType][arIndex[tType]] <  arDistance[tNext][arIndex[tNext]]) 
				tNext++;
			else 
				tType = tNext;
			}
	
		if(tType < Values.ENEMY_END)															// if there is an enemy thats not been created yet
			{
			tPos = arPosition[tType][arIndex[tType]];			
			obj.create(tType, (int)arProperty[tType][0], tPos, arDistance[tType][arIndex[tType]] );
			if(arProperty[tType][1] != EnemyFire.SHOT_NONE)										// if enemy fires, create fire object as well
				obj.createFire((int)arProperty[tType][1], (int)arProperty[tType][2], (int)arProperty[tType][3]);
		
			arIndex[tType]++;																	// Add to enemy created index
			if(arIndex[tType] == arDistance[tType].length)										// if all enemies of a type are created, set enemy Distance array to null
				arDistance[tType] = null;
			}
	}
/************************************************************************************************************************
*	METHOD - Class End
************************************************************************************************************************/
}
