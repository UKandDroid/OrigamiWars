package com.BreakthroughGames.OrigamiWars.stages;

import javax.microedition.khronos.opengles.GL10;

import com.BreakthroughGames.OrigamiWars.Adventure;
import com.BreakthroughGames.OrigamiWars.Draw;
import com.BreakthroughGames.OrigamiWars.EnemyFire;
import com.BreakthroughGames.OrigamiWars.Events;
import com.BreakthroughGames.OrigamiWars.HUD;
import com.BreakthroughGames.OrigamiWars.Player;
import com.BreakthroughGames.OrigamiWars.Pref;
import com.BreakthroughGames.OrigamiWars.R;
import com.BreakthroughGames.OrigamiWars.Screen;
import com.BreakthroughGames.OrigamiWars.Sound;
import com.BreakthroughGames.OrigamiWars.SoundPlayer;
import com.BreakthroughGames.OrigamiWars.Values;

public class Stage2_2 extends Adventure
{
	private float leafDist[][] = {{4.0f,  5.7f,  11.8f,  13.4f,  26.9f,  28.8f,  31.4f,  70.6f,  73.2f,  74.6f,  76.7f,  85.7f,  101f,  104.1f,  106.4f,  108f,  113f, },
								  {5.6f,  6.5f,  5.6f,   4.7f,   5.2f,   6.4f,   7.4f,   5.4f,   4.5f,   6.1f,   7.2f,   5.3f,   4.7f,  3.8f,    6f,      3.5f,  5.1f,  },
								  {Values.PATH_WIND, EnemyFire.SHOT_NONE, 0, 0, }};

	private float dFlyDist[][] = {{17.6f,  19.9f,  22f,  24.2f,  30.4f,  32.8f,  35.3f,  62.9f,  65.3f,  67.9f, },
								  {6.1f,   7.3f,   3.2f, 5.7f,   3.7f,   4.7f,   5.4f,   3.9f,   7.7f,   2f, },
								  {Values.PATH_AVOID, EnemyFire.SHOT_Small, EnemyFire.PATH_INTERCEPT_BACK, EnemyFire.FIRE_Twice, }};

	private float hornDist[][] = {{6.9f,  9.1f,  15.2f,  46.1f,  46.3f,  48.1f,  48.3f,  77.2f,  78.9f,  80.9f,  87.2f,  89.3f,  90.8f, },
								  {1.9f,  2.9f,  1.8f,   7.9f,   3.1f,   6.9f,   4.1f,   2.9f,   4.1f,   5.4f,   2.8f,   3.9f,   2.9f, },
								  {Values.PATH_INTERCEPT_MED, EnemyFire.SHOT_Small, EnemyFire.PATH_STRAIGHT_MED, EnemyFire.FIRE_Always, }};
	
	private float batsDist[][] = {{37.6f,  38.3f,  39.3f,  39.8f,  52.2f,  52.8f,  53.3f,  54.3f,  54.6f,  93.4f,  93.8f,  94.3f,  96.1f,  96.8f, },
								  {4.6f,   2.9f,   6.2f,   4.2f,   4.5f,   6.3f,   3f,     7.6f,   4.8f,   2.1f,   4.6f,   6.7f,   3.4f,   5.4f, },
								  {Values.PATH_SINE_WAVE, EnemyFire.SHOT_Medium, EnemyFire.PATH_INTERCEPT_MED, EnemyFire.FIRE_2ndTime, }};
	
	private float dragDist[][] = {{26.2f,  43.7f,  59.8f,  72.4f,  83.4f,  90f,  98.9f,  },
								  {2.3f,   2.4f,   6.8f,   2.3f,   1.3f,   7.7f, 1.1f,  },
								  {Values.PATH_INLINE_MED, EnemyFire.SHOT_DragFire, EnemyFire.PATH_INLINE_MED, EnemyFire.FIRE_Always, }};
	
		
/*============================================Start Class Methods=============================================================*/	
	@Override 
	public void resumeLevel()
	{	
		BG_Sky.loadTexture(R.drawable.lvl2_bg_sky);
		BG_Front.loadTexture(R.drawable.lvl2_bg_front,	GL10.GL_REPEAT);
		BG_Back.loadTexture( R.drawable.lvl2_bg_back,	GL10.GL_REPEAT);	
	}

	public void loadLevel()
	{	
		BG_Sky.loadTexture(R.drawable.lvl2_bg_sky);
		BG_Front.loadTexture(R.drawable.lvl2_bg_front,	GL10.GL_REPEAT);
		BG_Back.loadTexture( R.drawable.lvl2_bg_back,	GL10.GL_REPEAT);

		LEVEL_ENEMIES =  dFlyDist[0].length + batsDist[0].length + hornDist[0].length + dragDist[0].length;
	
		SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL5);							// Play Level Music
		super.reset();
		initObjects();															// Initilize all object, items and weapons
	}/*-----------------------------------------------------End Method()------------------------------------------------------------*/

	public void runOneFrame()
	{	
		calcLevelStats();														// Calculate level speed and Maximum Enemies
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
			case 2:	events.dispatch(Events.STAGE2_LEVEL2);  break; 				// level 2 Start
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
			
		arDistance[Values.ENEMY_DRAGONFLY] = dFlyDist[0];
		arPosition[Values.ENEMY_DRAGONFLY] = dFlyDist[1];
		arProperty[Values.ENEMY_DRAGONFLY] = dFlyDist[2];
		
		arDistance[Values.ENEMY_HORNET] = hornDist[0];
		arPosition[Values.ENEMY_HORNET] = hornDist[1];
		arProperty[Values.ENEMY_HORNET] = hornDist[2];

		arDistance[Values.ENEMY_BAT] = batsDist[0];
		arPosition[Values.ENEMY_BAT] = batsDist[1];
		arProperty[Values.ENEMY_BAT] = batsDist[2];
		
		arDistance[Values.ENEMY_DRAGON] = dragDist[0];
		arPosition[Values.ENEMY_DRAGON] = dragDist[1];
		arProperty[Values.ENEMY_DRAGON] = dragDist[2];
		
		for(int i=0; i < 8; i++) 
			createEnemy(object[i]);

		for(int i=8; i < MAX_OBJECTS; i++)
			object[i].create(Values.SCROLL_NORMAL, 0, 0,0 );
		}
/************************************************************************************************************************
 *   METHOD- Draws Background for the level
***********************************************************************************************************************/
	public void drawBackgrounds()
	{																			// Draw Sky & Back Mountains
		Draw.transform(0.7f, 1, 0, 0);								// Draw Sky
		BG_Sky.draw();
		
		Draw.transform(0.5f, 1,0.8f,0);
		BG_Back.draw(0.0f, BG_Back.scrollY);
		BG_Back.scrollY += Values.SCROLL_SPEED / 8;
																		// Main foreground mountains 
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
		if(!events.bEnable && Player.iLives == 0 )						// If player died, do events in sequence
			switch(iSequence)
				{
				case 0: iSequence++;	Pref.getSet(Pref.GAME_OVER);		break;
				case 1:	iSequence++;	events.dispatch(Events.GAME_OVER);	break;
				case 2: Screen.iMenu =  Screen.MENU_GAME_OVER; 	return Values.GAME_OVER;
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


}/*END LEVEL */
