package com.BreakthroughGames.OrigamiWars.stages;

import javax.microedition.khronos.opengles.GL10;

import com.BreakthroughGames.OrigamiWars.Adventure;
import com.BreakthroughGames.OrigamiWars.Base;
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

import android.util.Log;

public class Stage1_1 extends Adventure {
	private static final float leafData[][]= {{ 2.4f,  6.7f,  16.3f,  17.9f,  25f,  31.1f,  32.4f,},
											 {  8.7f,   7.6f,   7.2f,   7.8f,   8.5f,  7.8f,  8.4f, },
											 {Values.PATH_WIND, EnemyFire.SHOT_NONE, 0, 0, }};
	
	private static final float buttData[][]= {{8.6f,  11f,  13.6f,  15.5f,  18.1f,  21.8f,  23.4f,  25.3f,  26.2f, },
											  {4.8f,   5.4f,  4.7f,  4.2f,   3.3f,  4.2f,   7f,     6.2f,   5.1f, },
											  {Values.PATH_RANDOM, EnemyFire.SHOT_NONE, 0, 0,}};
	
	private static final float waspData[][]= {{9.4f,  14.6f,  17.2f,  22.9f,  25.6f, },
											  {2.7f,   5.7f,   6.2f,   3.8f,   2.9f, },
											  {Values.PATH_INLINE_SLOW, EnemyFire.SHOT_Small, EnemyFire.PATH_STRAIGHT_SLOW, EnemyFire.FIRE_Once, }};  
	
/************************************************************************************************************************
 *   METHOD- is called when screen wakes up, load textures again, every thing else is already loaded
***********************************************************************************************************************/
public void loadLevel()
{	Log.d("MethodCall", "Stage1_1 loadLevel() Called");
	
	BG_Sky.loadTexture(   R.drawable.lvl1_bg_sky,GL10.GL_REPEAT);
	BG_Back.loadTexture(  R.drawable.lvl1_bg_back,GL10.GL_REPEAT);
	BG_Middle.loadTexture(R.drawable.bg_clouds, GL10.GL_CLAMP_TO_EDGE);
	BG_Front.loadTexture( R.drawable.lvl1_bg_front,GL10.GL_REPEAT);
					
	LEVEL_ENEMIES = buttData[0].length +  waspData[0].length ; 
	
	SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL1);						// Play Level Music
	super.reset();														// Reset all Progress variables
	initObjects();														// Initilize all object, items and weapons
	BG_Middle.scrollY =-1; // Birds should start scrolling offscreen
}
/************************************************************************************************************************
 *   METHOD- is called when screen wakes up, load textures again, every thing else is already loaded
***********************************************************************************************************************/
@Override
public void resumeLevel()
{
	BG_Sky.loadTexture(   R.drawable.lvl1_bg_sky,GL10.GL_CLAMP_TO_EDGE);
	BG_Back.loadTexture(  R.drawable.lvl1_bg_back,GL10.GL_REPEAT);
	BG_Middle.loadTexture(R.drawable.bg_clouds, GL10.GL_CLAMP_TO_EDGE);
	BG_Front.loadTexture( R.drawable.lvl1_bg_front,GL10.GL_REPEAT);
	
}
/************************************************************************************************************************
 *   METHOD- When player restarts a level, reset all objects 
***********************************************************************************************************************/
public void runOneFrame()
{	
	calcLevelStats();													// Calculate level speed and Maximum Enemies
	drawBackgrounds();
	moveObjects();
	detectCollision();
	headUpDisplay();
}/*-----------------------------------------------------End Method()------------------------------------------------------------*/

private static void headUpDisplay()
{
	HUD.showStats();												// Show Stats HUD - Life, weapon, fire etc
	switch(events.iTimer) {
		case 1:		events.dispatch(Events.READY);  			break;
		case 120:	events.dispatch(Events.TOUCH_TO_MOVE);  	break; 	 
		case 580: 	events.dispatch(Events.TOUCH_TO_FIRE);  	break;	 
		case 940:	events.dispatch( Events.FLICK_TO_ROLL);  	break; 				// Flick up and down to roll
		}
	events.draw();														// Show  event 
}
/************************************************************************************************************************
 *   METHOD- Initilize Level Objects
***********************************************************************************************************************/
public void initObjects()
{		
		
	for(int i = 0; i < Values.ENEMY_END ; i++) arIndex[i] = 0;			// Reset Indexes for enemy arrays
	for(int i = 0; i < Values.ENEMY_END ; i++) arDistance[i] = null;	// Reset array
	
	arDistance[Values.ENEMY_LEAF] = leafData[0];
	arPosition[Values.ENEMY_LEAF] = leafData[1];
	arProperty[Values.ENEMY_LEAF] = leafData[2];

	arDistance[Values.ENEMY_BUTTERFLY] = buttData[0];
	arPosition[Values.ENEMY_BUTTERFLY] = buttData[1];
	arProperty[Values.ENEMY_BUTTERFLY] = buttData[2];
	
	arDistance[Values.ENEMY_WASP] = waspData[0];
	arPosition[Values.ENEMY_WASP] = waspData[1];
	arProperty[Values.ENEMY_WASP] = waspData[2];

	for(int i=0; i < 8; i++) createEnemy(object[i]); 
	for(int i=8; i < MAX_OBJECTS; i++) object[i].create(Values.SCROLL_NORMAL, 0, 0,0 );

}
/************************************************************************************************************************
 *   METHOD- Draws Background for the level
***********************************************************************************************************************/
private static void drawBackgrounds()
{	
	
	Draw.transform(0.7f, 1, 0, 0);									// Draw Sky
	BG_Sky.draw();
		
	Draw.transform(0.5f, 1,0.8f,0);
	BG_Back.draw(0.0f, BG_Back.scrollY);
	BG_Back.scrollY += Values.SCROLL_SPEED/8;
	
	if(BG_Middle.scrollY >= -1.0) 
		{			
		if(BG_Middle.scrollY > 1.0)
			BG_Middle.scrollY -= (2 + ran.nextFloat() * 5) ;
		
		Draw.transform(.5f, 1, 0, 0);
		BG_Middle.draw(0, BG_Middle.scrollY);
		}
	BG_Middle.scrollY += Values.SCROLL_SPEED;
																		
	Draw.transform(0.5f, 1, 1, 0);
	BG_Front.draw( 0, BG_Front.scrollY);
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
				Values.LEVEL_STATS[iLevel][Values.LEVEL_COMPL_TIME] = odoMeter;
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
	//Draw.transform(0.22f, 1, 2, 0);
	//BG_Middle.scroll(0.0f, 0);
	Base.transform(Screen.CHAR_HEIGHT*(2.02f/Screen.ASPECT_RATIO), 1, 2, 0);
	events.draw( 0.0f, 0);
  			
}

}/*END LEVEL ONE*/
