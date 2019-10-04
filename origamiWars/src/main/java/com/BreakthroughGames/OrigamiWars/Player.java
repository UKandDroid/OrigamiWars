package com.BreakthroughGames.OrigamiWars;

import javax.microedition.khronos.opengles.GL10;

public class Player extends Base
{	
	private static final int ANIM_LENGTH = 35; 
	protected static final float START_Y = 0.8f;						// Start Position for Y, Distance from Left 
	protected static final float START_X = 2.0f;						// Start Position for X, Height 
	protected static final int 	ACTION_MOVE   	  = 0;
	protected static final int 	ACTION_ROLL_UP    = 2;
	protected static final int 	ACTION_ROLL_DOWN  = 3;
	protected static final int 	ACTION_CRUISE     = 4;
	protected static final int 	ACTION_DEATH_FALL = 5;					// Plane is destroyed and is falling protected static final int  ACTION_DESTROYED    = 6;				// Plane destroyed, dont draw the plane
	protected static final float MOVE_SPEED    	  = 0.05f;				// Plane movement speed
	protected static final float TOUCH_OFFSET     = 0.7f;				// Offset for plane touch, so plane doenot come under finger
	protected Object cruise;											// Object used to caclulate Cruise Position for Plane
	private float acelInc = 0;  										// Calulates player motion up/down depend on +ve or -ve time
	private int reSpawnTime = 0;										// Counts time and reSpawns player after certain time
	private double animIndex = 0;
	protected static boolean bEnable;									// Overrides parent class bEnable
	protected boolean bCollision = false;								// If plane has collided with an Obstacle
	protected static boolean bRolling = false;
	protected static int ROLLING_TIME = 60;								// Rolling action frames Time
	private static float lastX = 0, lastY = 0;
	protected static int eCurAction, eAction = ACTION_CRUISE;			// Action from Screen Gesture
	private static int vDivider = 0, AnimAction = 0;
	private static int iInvinCount = 0, dragCounter = 0; 				// Counter for how long player been dragged on the ground
	private static float arrCollision[] = {0,0,0,0,0,0,0,0,0};
	protected static float posX = START_X, posY = START_Y;				// Overrides posX & posY in Base Class					
	private static int  eLastAction, rollTimer;							// Status of current action In Progress, like rolling		
	private static float bankSpeed = Values.BASIC_SPEED * 22;
	protected static float PLANE_SPEED = Values.BASIC_SPEED * 4;		// 0.0133,  plane moves 0.8f in 1sec or 60 frames
	protected static int iLives = 0, iPower = Values.PLAYER_POWER ;		// Three lives, and three power for each live
	protected boolean bInvincible = false, bSpawned = false;
	protected static boolean bFalling = false;
	private short arrSprites[][]= {	
									{0, 5, 12, 4}, 
									{0, 5, 12, 4}, 						// 0 - Move/Fall, 1- roll  
									{0, 5, 12, 4}, 						// 2 - Falling Sprites, 3 - Cruise
									{1, 6, 13, 5},				
									{1, 6, 13, 5},  			
									{1, 7, 13, 5},
									{1, 7, 14, 5},
									{2, 8, 14, 5},
									{2, 8, 14, 5},
									{2, 9, 15, 5},
									{2, 9, 15, 5},
									{3, 1, 15, 5},
									{3, 4, 12, 5},
									{3, 7, 12, 5},
									{3, 9, 12, 5},
									{3, 1, 13, 5},
									{4, 4, 13, 5},
									{4, 7, 13, 5},
									{4, 9, 14, 4}, 
									{4, 1, 14, 4}, 						
									{5, 4, 14, 3}, 						
									{5, 7, 15, 3},				
									{5, 9, 15, 3},  			
									{5, 1, 15, 3},
									{5, 4, 12, 3},
									{6, 7, 12, 3},
									{6, 9, 12, 3},
									{6, 0, 13, 3},
									{6, 0, 13, 3},
									{7, 1, 13, 3},
									{7, 1, 14, 3},
									{7, 2, 14, 3},
									{7, 2, 14, 3},
									{8, 3, 15, 3},
									{8, 3, 15, 3},
									{8, 3, 15, 3}
	};

	public Player(GL10 gl)
	{	
		bEnable = true;
		offsetX = 0.40f;
		offsetY = 0.0f;
		eLastAction = -1;
		cruise = new Object();											// Butterfly has less up and down movement
		cruise.create(Values.ENEMY_LEAF, Values.PATH_WIND);
		cruise.driftX = Values.SPEED1;									 
		cruise.gravity = -0.3f;
	}
/************************************************************************************************************************
 *   METHOD -- Draws Player Sprite from AnimationArray by checking plane action
************************************************************************************************************************/
	private void animatePlane()
	{	
		if(eCurAction != eLastAction )
			{
			switch(eCurAction)
				{													 								
				case ACTION_MOVE:		AnimAction = 0;		animIndex = 17;				break;
				case ACTION_ROLL_UP:	AnimAction = 1;		animIndex = ANIM_LENGTH;	break;
				case ACTION_ROLL_DOWN:	AnimAction = 1;		animIndex = 0;				break;
				case ACTION_DEATH_FALL: AnimAction = 2;		animIndex = 0;				break;
				case ACTION_CRUISE:  	AnimAction = 3;		animIndex = 0;				break;
				}
			eLastAction = eCurAction;
			}
		else switch(eCurAction)
				{													 								
				case ACTION_MOVE: 	
					float diffX = Screen.touchX - posX - TOUCH_OFFSET;	// plane should not come under finger i.e Offset		
					int curIndex =  (bCollision ? ANIM_LENGTH/2 : (int)((17.5)+(diffX*4)));
					if(curIndex < animIndex)		animIndex--;
					else if(curIndex > animIndex)	animIndex++;
				break;
				case ACTION_ROLL_UP:	 if(  animIndex > 0) 			animIndex--;	break;
				case ACTION_ROLL_DOWN:	 if(  animIndex < ANIM_LENGTH)  animIndex++;	break;
				case ACTION_CRUISE:  	 if(  animIndex >= ANIM_LENGTH) animIndex = 0;  animIndex += 0.2f; 	break;			// Spread animation over 3 secs
				case ACTION_DEATH_FALL:  if(++animIndex == ANIM_LENGTH) animIndex = 0;  break;
				}

		animIndex = Values.clamp(animIndex, 0, ANIM_LENGTH);			// Range of Action Animation Array 
		iSprite = arrSprites[(int)animIndex][AnimAction];
		transform(Screen.CHAR_HEIGHT*1.3f, Screen.CHAR_WIDTH*1.3f, posX/1.3f, posY/1.3f);
		draw(iTexture, iSprite);
		if(iInvinCount-- == 0)	setInvincible(false, 0);

	}
/************************************************************************************************************************
 *   METHOD -- Calculates position of plane depending on action
************************************************************************************************************************/
	protected float movePlayer()
	{
		if(!bRolling && !bFalling) eCurAction = eAction;				// If plane is rolling or falling dont update till Action Complete
		if(eCurAction != eLastAction )									// Set necessary init once for an Action
			{
			switch(eCurAction)
				{
				case ACTION_MOVE :
					 posT = dragCounter = 0; acelInc = vDivider = 1; 	// Reset player dragging, dragCounter = 0  
				break;
				case ACTION_ROLL_DOWN :
					posT = 4; acelInc = 0; bRolling = true;
					vDivider = 7 ; rollTimer = ROLLING_TIME;
					setInvincible(true, 30);
					SoundPlayer.stopSound(Sound.PLANE_ROLL);			// if plane is already rolling, stop sound
					SoundPlayer.playSound(Sound.PLANE_ROLL);
				break;
				case ACTION_ROLL_UP :
					posT = -4; acelInc = 0; bRolling = true;
					vDivider = 7; rollTimer = ROLLING_TIME;
					setInvincible(true, 30);							// Make Player invincible during rolling
					SoundPlayer.stopSound(Sound.PLANE_ROLL);			// if plane is already rolling, stop sound
					SoundPlayer.playSound(Sound.PLANE_ROLL);
				break;
				case ACTION_DEATH_FALL:	vDivider = 20; acelInc = 1f;		break;
				case ACTION_CRUISE: 	
					acelInc = posT = 0; 
					vDivider = 1;  
					cruise.posX = posX; 
					cruise.posY = posY;	
				break; 
				}
			}
		switch(eCurAction)
			{
			case ACTION_CRUISE: 	action_cruise();	break;
			case ACTION_MOVE:		action_move();		break;
			case ACTION_ROLL_UP :	action_roll_up();	break;
			case ACTION_ROLL_DOWN:	action_roll_down();	break;
			case ACTION_DEATH_FALL:	
				if(reSpawnTime++ > 120)	
					{ 
					dragCounter = 0;									// Reset falling counter	
					bEnable = bFalling = false; 						// Player is enabled during fall, disable it, if lives left it will be enabled
					if(iLives > 0) spawn(); 
					} 
				if(bCollision) calCollision(ACTION_DEATH_FALL);		
			break;			
			}
		
		posX += ((bankSpeed * posT) / vDivider) + 1/50 * (0.2f * (posT * posT));
		super.posX = posX = Values.clamp(posX, 0, Screen.DEV_MAX_X-1);
		super.posY = posY=  Values.clamp(posY, -0.2f, Screen.DEV_MAX_Y-1);
		posT = Values.clamp(posT+= acelInc, -40, 40);
		animatePlane();													// Animate the paper plane action
		if(dragCounter > 90 || (posY < -0.10f && !bFalling && bEnable)) 
			reduceLife(1);
		bCollision = false;												// Set collision flag to falsek
	return PLANE_SPEED;
	}
/************************************************************************************************************************
 *   METHOD -- Player Life and Power Methods
************************************************************************************************************************/
	protected void Damage(int vDamage)
	{
		iPower -= vDamage;
		if(iPower <= 0) reduceLife(1);
	}
	
	
	protected void reduceLife(int live)
	{
		if(!bFalling && !bSpawned)										// if plane is not alreading destroyed and falling or Just Spawned
			{
			iPower = Values.PLAYER_POWER;								// For new life set reset power
			iLives-= live;
			bFalling = true;
			speed = posT = 0;
			Adventure.speedCounter = 0;
			reSpawnTime = iSprite = 0;
			Screen.bShockWave = false;
			eCurAction = ACTION_DEATH_FALL;
			SoundPlayer.playSound(Sound.PLANE_BURN);
			SoundPlayer.setVolume(1.0f, 0.6f);							// Set sound level to half when Player Dies
			}
	}
	
	
	protected void spawn()
	{
			posY = START_Y;
			bEnable = true;												// Enable player
			bFalling = false;
			bSpawned = true;											// Maked it Invincible, from everything for some time 											// Disable falling flag
			bRolling = false;											// if level completes and player is rolling, reset flag
			setInvincible(true, -1);									// Resets invincible time to Zero
			setInvincible(true, 300);									// Set invincible for 160 frames
			Adventure.pssGame.removeCurse();									// Remove if there has been any curse
			cruise.posX = posX = START_X;
			eAction = eCurAction = ACTION_CRUISE;						//Reset Action to Cruise, in case if there is rolling has been set
	}	
/************************************************************************************************************************
 *   METHOD -- Sets and Reset Players invincibility, -1 resets invincible time
************************************************************************************************************************/

	protected void setInvincible( boolean vEnable, int vTime)
	{																
		if(vEnable)
			{
			if(vTime < 0)  iInvinCount = 0;								// -ve values to reset, invincible counter		
			if(iInvinCount < 0)	iInvinCount  = 0;						// Invincible Counter could be -ve set it to zero then
			iInvinCount += vTime;										// Stay invincible for #vFrames 
			setTransparency(0.55f);
			bInvincible = true;
			}
		else
			{
			bSpawned = bInvincible = false;
			setTransparency(1.0f);
			}
	}

	
	protected void setCollStats( float vArrResult[])
	{
		bCollision = true;
		arrCollision[Obstacle.CLOUD_TYPE] = vArrResult[Obstacle.CLOUD_TYPE];
		arrCollision[Obstacle.OLAP_X] 	  = vArrResult[Obstacle.OLAP_X];
		arrCollision[Obstacle.OLAP_Y] 	  = vArrResult[Obstacle.OLAP_Y];
		arrCollision[Obstacle.POSITION]   = vArrResult[Obstacle.POSITION];
		arrCollision[Obstacle.POSITION_Y] = vArrResult[Obstacle.POSITION_Y];
		arrCollision[Obstacle.OLAP_ABS_X] = vArrResult[Obstacle.OLAP_ABS_X];			// ABS is absoloute sum of overlaps
		arrCollision[Obstacle.OLAP_ABS_Y] = vArrResult[Obstacle.OLAP_ABS_Y];
		arrCollision[Obstacle.OLAP_DIFF_X] = vArrResult[Obstacle.OLAP_DIFF_X];			// Difference is sum of +ve and -Ve overlap
		arrCollision[Obstacle.OLAP_DIFF_Y] = vArrResult[Obstacle.OLAP_DIFF_Y];	
	}
/************************************************************************************************************************
*	METHODS - To calculate different plane actions
************************************************************************************************************************/
	private	void action_move()
	{
		float addX, addY;
		float diffX = Screen.touchX - posX - TOUCH_OFFSET;									// plane should not come under finger i.e Offset		
		float diffY = Screen.touchY - posY + TOUCH_OFFSET + ((Screen.touchY/Screen.DEV_MAX_Y)*0.4f);// increase offset towards right			
		float slope = diffX/Math.abs(diffY);
		
		if( Math.abs(slope) > 1 ) slope = slope < 0 ? -1 : 1;
		addX = slope* Math.abs(diffX) * MOVE_SPEED;
		addY = diffY * MOVE_SPEED;
		
		if( acelInc < 1.2 ) acelInc += 0.005;
		if( acelInc < 1.0 ) acelInc += 0.01;
		if( Math.abs(addX) > MOVE_SPEED) addX /= (Math.abs(addX)/MOVE_SPEED);
		if( Math.abs(addY) > MOVE_SPEED) addY /= (Math.abs(addY)/MOVE_SPEED);
		if( getBottom() >= (Screen.DEV_MAX_X - offsetX) && !bRolling && !bInvincible)
			{ posT = 0; dragCounter++; Adventure.events.dispatch(Events.PROXIMITY_ALERT); Adventure.events.dispatch(Events.VIBRATE_DRAG); }
	
		if(bCollision) calCollision(ACTION_MOVE);
		
		posT = 0;
		addX = addX*acelInc*acelInc;														//tTimeInc, Ranges from 0.85 - 1.2 (0.85 with white clouds collision).
		addY = addY*acelInc*acelInc;;
		posX += lastX = (lastX + addX) / 2;														
		posY += lastY = (lastY + addY) / 2;
	}

	private	void action_cruise()
	{
		cruise.move();
		posX = cruise.posX;
		cruise.posY = 1.0f;																								// Keep Y-axis constant
		if(cruise.posX < Screen.DEV_MAX_X/3 && cruise.gravity < 0.6)							cruise.gravity += 0.02;	// Increase Gravity if plane goes up
		else if(cruise.posX > Screen.DEV_MAX_X/1.5f && cruise.gravity > -0.6f)					cruise.gravity -= 0.02;	// Decrease gravity if plane going down
		else if(cruise.posX > Screen.DEV_MAX_X/3    && cruise.posX < Screen.DEV_MAX_X/1.5f)		cruise.gravity = -0.3f;	// Keep gravity to -0.2 if plane is in middle range
		cruise.driftX = Values.SPEED1;									 
		if(bCollision) calCollision(ACTION_CRUISE);
	}


	private void action_roll_up()
	{
		switch(rollTimer--)
			{
			case 30:  bRolling = false; 		break;
			case 0: eAction= ACTION_CRUISE; 	break;
			}
		posT = -(3+( easeInOut(ROLLING_TIME+40, rollTimer)*8));
		if(bCollision) calCollision(ACTION_ROLL_UP);
	}


	private void action_roll_down()
	{
		switch(rollTimer--)
			{
			case 30:  bRolling = false; 		break;
			case 0: eAction= ACTION_CRUISE; 	break;
			}
		posT =  (3+( easeInOut(ROLLING_TIME+40, rollTimer)*8));
		if(getBottom() >= (Screen.DEV_MAX_X - offsetX) && !bInvincible)
			{ posT = 0; dragCounter++; Adventure.events.dispatch(Events.PROXIMITY_ALERT); Adventure.events.dispatch(Events.VIBRATE_DRAG); }
		if(bCollision) calCollision(ACTION_ROLL_DOWN);
	}
/************************************************************************************************************************
*	METHODS - Calculates collision result with clouds
************************************************************************************************************************/
private void calCollision(int vAction)
{
	float absX  = arrCollision[Obstacle.OLAP_ABS_X];
	float absY  = arrCollision[Obstacle.OLAP_ABS_Y];
	float diffX = arrCollision[Obstacle.OLAP_DIFF_X];
	float diffY = arrCollision[Obstacle.OLAP_DIFF_Y];
	absX = (absX > 1)? 1: absX;
	absY = (absY > 1)? 1: absY;
	float absOlap = Math.abs(absX*absY);

	switch((int)arrCollision[Obstacle.CLOUD_TYPE])
		{
		case Obstacle.WHITE_CLOUDS:
			switch(vAction)
				{
				case ACTION_MOVE:	acelInc -= 0.4*absOlap; if(acelInc < 0.8 ) acelInc = 0.8f;	break;
				case ACTION_CRUISE: posT = posT/ (1+(0.5f*absOlap)); cruise.driftX = Values.SPEED1/(1+(6*absOlap));	break;						// slow crusing down
				}
		break;
		case Obstacle.THUNDER_CLOUDS:
		case Obstacle.DARK_CLOUDS:
			switch(vAction)
				{
				case ACTION_MOVE:	
					acelInc -= 0.4*absOlap;
					if(acelInc < 0.8 ) acelInc = 0.8f;
					posX += (diffX*Math.abs(absY))/2;
					posY += (diffY*Math.abs(absX));
				break;
				case ACTION_CRUISE: 
					cruise.posX += (diffX*Math.abs(absY))/2;
					cruise.posY += (diffY*Math.abs(absX));
				break;						// slow crusing down
				}

//			if(vAction == ACTION_DEATH_FALL )
//				posT = arrCollision[Obstacle.POSITION] == Obstacle.CLOUD_DOWN ? 2 : 2;	 
		break;
		}
}
/************************************************************************************************************************
*	CLASS-END
************************************************************************************************************************/
	}
