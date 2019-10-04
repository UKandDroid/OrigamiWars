package com.BreakthroughGames.OrigamiWars;

import javax.microedition.khronos.opengles.GL10;

import com.BreakthroughGames.OrigamiWars.R;

public class Stage1_3 extends Adventure
{
	private float leafData[][] = {{4.2f,  6.1f,  21.1f,  23.3f,  25f,  31.3f,  32.7f,  34.5f,  41.5f,  45.2f,  54.6f,  63.1f,  67.4f, },
								  {6.8f,  6f,    5.1f,   3.4f,   2f,   6f,     5.5f,   6.9f,   6.8f,   5.9f,   6.7f,   5.6f,   4.3f,   },
								  {Values.PATH_WIND, EnemyFire.SHOT_NONE, 0, 0, }};
	
	private float dFlyData[][] = {{2.1f,  13.5f,  15.9f,  18.4f,  19.8f,  44f,  46.7f,  53.2f,  54.9f,  56.3f,  },
								  {3.3f,   5.2f,   6.7f,   5.5f,   2.7f,   6.9f,  5.4f,  3.3f,   1.7f,   4.6f,  },
								  {Values.PATH_AVOID, EnemyFire.SHOT_Small, EnemyFire.PATH_INTERCEPT_BACK, EnemyFire.FIRE_Twice, }};

	private float hornData[][] = {{10.5f,  24.4f,  34.9f,  37f,  39.2f,  43.9f,  59f,  59.3f, },
								  {6.9f,   6.7f,   1.6f,   3.7f, 5.5f,   1.7f,   6.8f, 1.4f,  },  
								  {Values.PATH_INTERCEPT_SLOW, EnemyFire.SHOT_Small, EnemyFire.PATH_STRAIGHT_SLOW, EnemyFire.FIRE_Always, }};

	private float batsData[][] = {{27.2f,  28.2f,  29.3f,  48.3f,  49.5f,  49.9f,  51.1f,  51.6f,  },
								  {4.2f,   6.2f,   2.8f,   5.5f,   2f,     4.1f,   6.5f,   3.8f, },
								  {Values.PATH_SINE_WAVE, EnemyFire.SHOT_Medium, EnemyFire.PATH_INTERCEPT_SLOW, EnemyFire.FIRE_2ndTime, }}; 
			 

/*============================================Start Class Methods=============================================================*/	
@Override
public void resumeLevel()
{
	BG_Front.loadTexture( R.drawable.lvl3_bg_front,GL10.GL_REPEAT);		// Load BackGrounds
	BG_Middle.loadTexture(R.drawable.lvl3_bg_fog,  GL10.GL_REPEAT);
	BG_Back.loadTexture(  R.drawable.lvl3_bg_back, GL10.GL_REPEAT);
	BG_Sky.loadTexture(R.drawable.lvl3_bg_sky);
}
	
	
public void loadLevel()
{	
	BG_Front.loadTexture( R.drawable.lvl3_bg_front,GL10.GL_REPEAT);		// Load BackGrounds
	BG_Middle.loadTexture(R.drawable.lvl3_bg_fog,  GL10.GL_REPEAT);
	BG_Back.loadTexture(  R.drawable.lvl3_bg_back, GL10.GL_REPEAT);
	BG_Sky.loadTexture(   R.drawable.lvl3_bg_sky);
	BG_Back.scrollY=-1;
	
	LEVEL_ENEMIES = hornData[0].length + dFlyData[0].length + batsData[0].length;
	
	SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL3);						// Play Level Music
	initObjects();																// Initilize all object, items and weapons
	super.reset();
	
}/*-----------------------------------------------------End Method()------------------------------------------------------------*/

public void runOneFrame()
{	
	calcLevelStats();															// Calculate level speed and Maximum Enemies
	drawBackgrounds();
	moveObjects();
	drawForeground();
	detectCollision();
	headUpDisplay();

}/*-----------------------------------------------------End Method()------------------------------------------------------------*/

static void headUpDisplay()
{
	HUD.showStats();													// Show Stats HUD - Life, weapon, fire etc
	switch(events.iTimer)
		{	
		case 2  :	events.dispatch(Events.STAGE1_LEVEL3);  	break; 				 
		}
	events.draw();
}

public void initObjects()
{	
	for(int i = 0; i < Values.ENEMY_END ; i++) arIndex[i] = 0;			// Reset Indexes for enemy arrays
	for(int i = 0; i < Values.ENEMY_END ; i++) arDistance[i] = null;	// Reset array
	
	arDistance[Values.ENEMY_LEAF] = leafData[0];
	arPosition[Values.ENEMY_LEAF] = leafData[1];
	arProperty[Values.ENEMY_LEAF] = leafData[2];
		
	arDistance[Values.ENEMY_DRAGONFLY] = dFlyData[0];
	arPosition[Values.ENEMY_DRAGONFLY] = dFlyData[1];
	arProperty[Values.ENEMY_DRAGONFLY] = dFlyData[2];
	
	arDistance[Values.ENEMY_HORNET] = hornData[0];
	arPosition[Values.ENEMY_HORNET] = hornData[1];
	arProperty[Values.ENEMY_HORNET] = hornData[2];

	arDistance[Values.ENEMY_BAT] = batsData[0];
	arPosition[Values.ENEMY_BAT] = batsData[1];
	arProperty[Values.ENEMY_BAT] = batsData[2];
	
	for(int i=0; i < 8; i++) createEnemy(object[i]); 
	for(int i=8; i < MAX_OBJECTS; i++) object[i].create(Values.SCROLL_NORMAL, 0, 0,0 );

}
/************************************************************************************************************************
 *   METHOD- Background drawing methods
***********************************************************************************************************************/
public static void drawBackgrounds()
{	
	Draw.transform(0.5f, 1, 0, 0);
	BG_Sky.draw();
	
	BG_Back.scrollY += Values.SCROLL_SPEED / 12;
	Draw.transform(1.0f, 1.0f, 0, 0);
	BG_Back.draw(0.0f, BG_Back.scrollY);
	
}/*-----------------------------------------------------End Method()------------------------------------------------------------*/

static void drawForeground()
{
	Draw.transform( 0.8f, 1, 0.2f, 0);										// Fog in the valley
	BG_Middle.draw(0, BG_Middle.scrollY);
	BG_Middle.scrollY += Values.SCROLL_SPEED/3;

	Draw.transform(0.5f, 1, 1, 0);
	BG_Front.draw(0, BG_Front.scrollY);
	BG_Front.scrollY += Values.SCROLL_SPEED/1.5 ;
	if(BG_Front.scrollY >= 1)	BG_Front.scrollY = 0;
}
/************************************************************************************************************************
 *   METHOD- Check game status and return message to main game loop, GameRunning, GameOver, LevelComplete
***********************************************************************************************************************/
public byte checkGameStatus()
{
	if(!events.bEnable && Player.iLives == 0 )						// If player died, do events in sequence
		switch(iSequence)
			{
			case 0: iSequence++;	Pref.getSet(Pref.GAME_OVER);		break;
			case 1:	iSequence++;	events.dispatch(Events.GAME_OVER);	break;
			case 2: Screen.iMenu =  Screen.MENU_GAMEOVER; 	return Values.GAME_OVER; 		 	
			}
	else if(!events.bEnable && enemyDestroyed == LEVEL_ENEMIES)	// Level Complete
		switch(iSequence)
			{
			case 0:
				SoundPlayer.setVolume(1.0f, 0.5f);					// Lower Sound when Level Completes
				iSequence++;	events.dispatch(Events.LEVEL_COMPLETE);	
				Values.LEVEL_STATS[iLevel][Values.LEVEL_COMPL_TIME] = (int) odoMeter;
			break;
			case 1: 	return Values.GAME_LEVEL_STATS; 		 	
			}
	
	return Values.GAME_RUNNING;
}

/************************************************************************************************************************
 *   METHOD- Displays 'GameLoading' before level loads
***********************************************************************************************************************/
public void loadingScreen()
{		
	BG_Middle.loadTexture(R.drawable.event_loading, GL10.GL_CLAMP_TO_EDGE);
	Draw.transform(0.22f, 1, 2, 0);
	BG_Middle.draw(0.0f, 0);
}

}/*END LEVEL*/
