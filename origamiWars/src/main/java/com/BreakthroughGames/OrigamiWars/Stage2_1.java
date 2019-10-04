package com.BreakthroughGames.OrigamiWars;

import javax.microedition.khronos.opengles.GL10;

import com.BreakthroughGames.OrigamiWars.R;

public class Stage2_1 extends Adventure
{
	private float leafDist[][]= {{8.4f,  10.1f,  19.7f,  21.4f,  43.7f,  60.1f,  62.1f,  67.7f,  70.8f,  77.9f,  79.5f,  82.3f,  86f,  90f,  },
								 {6.5f,  7.3f,   5.6f,   6.4f,   5.8f,   6.8f,   6.2f,   4.2f,   5.1f,   3.3f,   4.6f,   5.1f,   5.9f, 7.3f, },
								 {Values.PATH_WIND, EnemyFire.SHOT_NONE, 0, 0, }};

	private float buttDist[][]= {{4.3f,  5.4f,  5.8f,  11.3f,  12.5f,  22.7f,  24.6f,  27f,  41.9f,  54.3f,   55.5f,  57.7f,  63.6f,  65f, },
			 					 {4.9f,  7.6f,  3.1f,  3.2f,   4.8f,   4.6f,   3.6f,   2.8f, 3.3f,   4.2f,    5.2f,   4.1f,   4.5f,   4.4f,},
			 					 {Values.PATH_RANDOM, EnemyFire.SHOT_Small, EnemyFire.PATH_INTERCEPT_MED, EnemyFire.FIRE_Once, }};

	private float waspDist[][]= {{15.5f,  17.4f,  19.3f,  29.6f,  31.5f,  48.6f,  50.3f,  69.3f,  70.9f,  76.2f, },
								 {6.4f,   7.3f,   8.3f,   7.5f,   6.6f,   7f,     7.8f,   6.8f,   7.7f,   5.7f,  },
								 {Values.PATH_INLINE_MED, EnemyFire.SHOT_Small, EnemyFire.PATH_STRAIGHT_MED, EnemyFire.FIRE_Twice, }};
	
	private float vultDist[][]= {{8.7f,  18.4f,  25.8f,  37.9f,  45.9f,  59.9f,  68.3f,},
								 {1.2f,  2.4f,   7.4f,   1.3f,   8f,     2.1f,   1.3f, },
								 {Values.PATH_INTERCEPT_MED, EnemyFire.SHOT_Medium, EnemyFire.PATH_STRAIGHT_MED, EnemyFire.FIRE_Twice, }};

	private float petrDist[][]= {{35.7f,  48.2f,  73f,  74.2f, },
								 {7.5f,   2.3f,   6.4f, 1.9f,  },
								 {Values.PATH_INTERCEPT_MED, EnemyFire.SHOT_Medium, EnemyFire.PATH_STRAIGHT_MED, EnemyFire.FIRE_Twice, }};


	
/*============================================Start Class Methods=============================================================*/	

@Override 
public void resumeLevel()
{	
	BG_Sky.loadTexture(R.drawable.lvl1_bg_sky,GL10.GL_REPEAT);
	BG_Front.loadTexture( R.drawable.lvl1_bg_front,GL10.GL_REPEAT);
	BG_Back.loadTexture(R.drawable.lvl1_bg_back,GL10.GL_REPEAT);
}
	
public void loadLevel()
{		
	BG_Sky.loadTexture(R.drawable.lvl1_bg_sky,GL10.GL_REPEAT);
	BG_Front.loadTexture( R.drawable.lvl1_bg_front,GL10.GL_REPEAT);
	BG_Back.loadTexture(R.drawable.lvl1_bg_back,GL10.GL_REPEAT);
	
	LEVEL_ENEMIES = waspDist[0].length + buttDist[0].length + vultDist[0].length + petrDist[0].length ;

	SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL4);						// Play Level Music
	super.reset();
	initObjects();														// Initilize all object, items and weapons
}/*-----------------------------------------------------End Method()------------------------------------------------------------*/

public void runOneFrame()
{	
	calcLevelStats();													// Calculate level speed and Maximum Enemies
	drawBackgrounds();
	moveObjects();
	detectCollision();
	headUpDisplay();

}/*-----------------------------------------------------End Method()------------------------------------------------------------*/

void headUpDisplay()
{
	HUD.showStats();												// Show Stats HUD - Life, weapon, fire etc
	switch(events.iTimer)
		{	
		case 5  :	events.dispatch(Events.STAGE2_LEVEL1);  break; 						  
		}
	events.draw();
}

public void initObjects()
{	
	for(int i = 0; i < Values.ENEMY_END ; i++) arIndex[i] = 0;			// Reset Indexes for enemy arrays
	for(int i = 0; i < Values.ENEMY_END ; i++) arDistance[i] = null;	// Reset array
	
	arDistance[Values.ENEMY_LEAF] = leafDist[0];
	arPosition[Values.ENEMY_LEAF] = leafDist[1];
	arProperty[Values.ENEMY_LEAF] = leafDist[2];
		
	arDistance[Values.ENEMY_BUTTERFLY] = buttDist[0];
	arPosition[Values.ENEMY_BUTTERFLY] = buttDist[1];
	arProperty[Values.ENEMY_BUTTERFLY] = buttDist[2];
	
	arDistance[Values.ENEMY_WASP] = waspDist[0];
	arPosition[Values.ENEMY_WASP] = waspDist[1];
	arProperty[Values.ENEMY_WASP] = waspDist[2];

	arDistance[Values.ENEMY_VULTURE] = vultDist[0];
	arPosition[Values.ENEMY_VULTURE] = vultDist[1];
	arProperty[Values.ENEMY_VULTURE] = vultDist[2];
	
	arDistance[Values.ENEMY_PTEROSAUR] = petrDist[0];
	arPosition[Values.ENEMY_PTEROSAUR] = petrDist[1];
	arProperty[Values.ENEMY_PTEROSAUR] = petrDist[2];

	for(int i=0; i < 8; i++) 
		createEnemy(object[i]);

	for(int i=8; i < MAX_OBJECTS; i++)
		object[i].create(Values.SCROLL_NORMAL, 0, 0,0 );
}
/************************************************************************************************************************
 *   METHOD- Draws Background for the level
***********************************************************************************************************************/
public void drawBackgrounds()
{	
	Draw.transform(0.7f, 1, 0, 0);										// Draw Sky 
	BG_Sky.draw();
	
	Draw.transform(0.5f, 1,0.8f,0);
	BG_Back.draw( 0.0f, BG_Back.scrollY);
	BG_Back.scrollY += Values.SCROLL_SPEED / 8;
	
	Draw.transform(0.5f, 1, 1, 0);
	BG_Front.draw(0, BG_Front.scrollY);
	BG_Front.scrollY += Values.SCROLL_SPEED / 2;
	if(BG_Front.scrollY == Float.MAX_VALUE)	BG_Front.scrollY = 0;
																
}
/************************************************************************************************************************
 *   METHOD- Check game status and return message to main game loop, GameRunning, GameOver, LevelComplete
***********************************************************************************************************************/
public byte checkGameStatus()
{
	if(!events.bEnable && Player.iLives == 0 )							// If player died, do events in sequence
		switch(iSequence)
			{
			case 0: iSequence++;	Pref.getSet(Pref.GAME_OVER);		break;
			case 1:	iSequence++;	events.dispatch(Events.GAME_OVER);	break;
			case 2: Screen.iMenu =  Screen.MENU_GAMEOVER; 	return Values.GAME_OVER; 		 	
			}
	else if(!events.bEnable && enemyDestroyed == LEVEL_ENEMIES)			// Level Complete
		switch(iSequence)
			{
			case 0:
				SoundPlayer.setVolume(1.0f, 0.5f);						// Lower Sound when Level Completes
				iSequence++;	events.dispatch(Events.LEVEL_COMPLETE);	
				Values.LEVEL_STATS[iLevel][Values.LEVEL_COMPL_TIME] = (int) odoMeter;
			break;
			case 1: 	return Values.GAME_LEVEL_STATS; 		 	
			}
	
	return Values.GAME_RUNNING;
}

public void loadingScreen()
{		
	BG_Middle.loadTexture(R.drawable.event_loading, GL10.GL_CLAMP_TO_EDGE);
	Draw.transform(0.22f, 1, 2, 0);
	BG_Middle.draw(0.0f, 0);
}


}/*END LEVEL */
