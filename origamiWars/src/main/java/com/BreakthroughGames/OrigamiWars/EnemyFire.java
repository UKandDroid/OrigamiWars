package com.BreakthroughGames.OrigamiWars;

public class EnemyFire extends Base {
	public static final int SHOT_NONE    = 0;
	public static final int SHOT_Small   = 1;
	public static final int SHOT_Medium  = 2;
	public static final int SHOT_PetSFire = 4;
	public static final int SHOT_DragFire= 8;

	protected static final int ROUND_COMPLETE = 1;											// Fire Reload Event, Enemy Screen round complete
	protected static final int FIRE_DISMISS   = 2;											// Fire Dismissed, hit Plane or out of screen

	protected static final int STATUS_Ready   = 1;											// Shot is ready, fired once conditions met
	protected static final int STATUS_Fired   = 2;											// Shot has been fired, not to be fired again
	protected static final int STATUS_STANDBY = 3;
	protected static final int STATUS_Moving  = 4;											// Shot is moving
	protected static final int STATUS_Smoke   = 8;											// Shot is smoking
	protected static final int STATUS_ACTIVE  = 12;

	public static final int FIRE_Once     = 1;											// fire 1st time only
	public static final int FIRE_Twice    = 2;											// fire two times
	public static final int FIRE_2ndTime  = 4;											// fire 2nd time
	public static final int FIRE_Always   = 8;											// fire always

	public static final int PATH_INTERCEPT_SLOW 	= 1;									// Fires where plane is when distance is reached
	public static final int PATH_INTERCEPT_MED  	= 2;
	public static final int PATH_INTERCEPT_FAST 	= 3;

	protected static final int PATH_INLINE_SLOW 	= 4;									// Fires straight when plane is inline with the enemy
	public static final int PATH_INLINE_MED 	  	= 5;
	public static final int PATH_INLINE_FAST 	= 6;
	public static final int PATH_STRAIGHT_SLOW  	= 7;									// Fires straight when distance is reached
	public static final int PATH_STRAIGHT_MED   	= 8;
	public static final int PATH_STRAIGHT_FAST  	= 9;
	public static final int PATH_INTERCEPT_BACK 	= 10;

/*************************************************************************************************************************************************************************
 * --------------------------------------------END CONSTANTS DECLARATION-------------------------------------------------------------------------------------------------
**************************************************************************************************************************************************************************/
	private int 		iRound = 0;															// Enemy trips across screen
	protected int 		iDamage = 1;														// How much damage is cased by shot
	protected boolean   bSmoke = false;														// Does it show smoke
	protected boolean   bFireOnce = false;													// Has shot been fired
	protected boolean 	bLockedOn = false;													// For intercept, has it been locked
	protected boolean 	bEnemyNewRound = true;
	protected int 		iFreq = FIRE_Once;													// how many Times/Frequency
	protected int 		iStatus  = STATUS_Ready;
	protected int 		iSmoke = 0, smokeCount = 0;											// Smoke sprite for particular fire

	public EnemyFire() {}
	protected boolean create(int vEnemyType){ return create(iType, iPath, iFreq, vEnemyType); }
	protected boolean create(int vShotType, int vPath, int  vFreq, int vEnemy){ return create(vShotType, vPath, vFreq, vEnemy, 1.5f);}
	protected boolean create(int vShotType, int vPath, int  vFreq, int vEnemy, float vDistX) {
		if(vShotType == SHOT_NONE) 															// If it does not fire, no need to initialise
			return bEnable = false;

		iRound = 0;																			// Enemy trip on screen
		distance = 6;																		// Default distance for fire
		posT = vDistX;																		// Shot parameter, distance when shot, or inline difference
		iFreq = vFreq;																		// Frequency of fire, once, twice, 2ndtime, always
		iPath = vPath;																		// Fire path, Intercept/ Straight/ inline
		iType = vShotType;
		bEnemyNewRound = bEnable = true;
		iTexture = Values.ARR_TEXTURES[Values.FIRE_ENEMY];

		switch(iPath) {
			case PATH_INTERCEPT_BACK: speed = Values.SPEED_SHOT_SLOW/1.6f;	distance = -2;	break;
			case PATH_STRAIGHT_SLOW: case PATH_INTERCEPT_SLOW: case PATH_INLINE_SLOW: speed = Values.SPEED_SHOT_SLOW; break;
			case PATH_STRAIGHT_MED:  case PATH_INTERCEPT_MED:  case PATH_INLINE_MED:  speed = Values.SPEED_SHOT_MED;  break;
			case PATH_STRAIGHT_FAST: case PATH_INTERCEPT_FAST: case PATH_INLINE_FAST: speed = Values.SPEED_SHOT_FAST; break;
			}

		switch(iType) {
			case SHOT_Small: 	iSprite = 0; 	iSmoke = 4;		iDamage = 1;  	offsetX = 0.40f;  offsetY = 0.40f;	break;
			case SHOT_Medium: 	iSprite = 1; 	iSmoke = 5;		iDamage = 2;  	offsetX = 0.30f;  offsetY = 0.30f;	break;
			case SHOT_PetSFire: iSprite = 2;    iSmoke = 5;		iDamage = 2;  	offsetX = 0.30f;  offsetY = 0.20f;	break;
			case SHOT_DragFire: iSprite = 3; 	iSmoke = 5;		iDamage = 3;  	offsetX = 0.30f;  offsetY = 0.20f;	break;
			}

		switch(vEnemy) {
			case Values.ENEMY_BUTTERFLY:	posX = var1 =  0;  	 	posY = var2 = -0.1f; 	break;				// Position of shot fire, position where enemy mouth is
			case Values.ENEMY_WASP:			posX = var1 =  0;  	 	posY = var2 = -0.2f; 	break;
			case Values.ENEMY_DRAGONFLY:	posX = var1 =  0;  	 	posY = var2 =  0.3f; 	break;
			case Values.ENEMY_HORNET:		posX = var1 =  0; 	 	posY = var2 = -0.4f; 	distance = 5;	break;
			case Values.ENEMY_BAT:			posX = var1 =  0.1f;  	posY = var2 = -0.2f; 	break;
			case Values.ENEMY_VULTURE:		posX = var1 =  0.1f;  	posY = var2 = -0.5f;	distance = 4; 	break;
			case Values.ENEMY_PTEROSAUR: 	posX = var1 =  0.25f;  	posY = var2 = -0.5f; 	break;
			case Values.ENEMY_DRAGON:		posX = var1 = -0.28f; 	posY = var2 = -0.7f;	break;
			}

		reset();

		return true;
	}
/************************************************************************************************************************
 *   METHOD --  It resets the enemy fire so its ready to be fired again, Called by the Enemy's path method
*************************************************************************************************************************/
	private boolean reset() {
		posX = var1;
		posY = var2;
		bSmoke = false;
		smokeCount = 0;
		bLockedOn = false;
		iStatus = STATUS_Ready;
		return bEnable;																					// Return if shot is enabled or disabled
	}
/************************************************************************************************************************
 *   METHOD --  sets certain flags, resets the weapon if all conditions are met
*************************************************************************************************************************/
	protected boolean reload(int iType) {
		switch(iType)
			{
			case ROUND_COMPLETE:	bEnemyNewRound = true;	break;										// Enemy has completed a round
			case FIRE_DISMISS:  	iStatus = STATUS_Fired; bSmoke = false; smokeCount = 0; break;		// Fire has hit Plane, or is out of screen
			}

		if(bEnemyNewRound && iStatus == STATUS_Fired)
			{
			iStatus = STATUS_Ready;
			switch(iFreq)
				{
				case FIRE_Once:  bEnable = false;  break;
				case FIRE_Twice: case FIRE_2ndTime: if(iRound == 2) bEnable = false; break;
				}
			}

		return bEnable;																					// Returns if the fire is enable or not (i.e enemy fires or not)
	}
/************************************************************************************************************************
 *   METHOD -- Checks for conditions to fire a shot
*************************************************************************************************************************/
	protected void fireCheck(Object obj) {
		if(bEnable && iStatus == STATUS_Ready)															// If shot is enabled i.e should be fired, and hasn't been fired once already
			{
			if((obj.posY - Player.posY) < distance)														// Fire when enemy is at certain distance from plane
				switch(iPath)
					{
					case PATH_INTERCEPT_BACK:
					case PATH_STRAIGHT_SLOW:  case PATH_STRAIGHT_MED:  case PATH_STRAIGHT_FAST:
					case PATH_INTERCEPT_SLOW: case PATH_INTERCEPT_MED: case PATH_INTERCEPT_FAST:
						switch(iFreq)
							{
							case FIRE_Once:  	if(iRound == 1) fire(obj); 	break;
							case FIRE_2ndTime:  if(iRound == 2) fire(obj); 	break;
							case FIRE_Twice: 	if(iRound <= 2) fire(obj); 	break;
							case FIRE_Always:  	fire(obj); 					break;
							}
					break;
					case PATH_INLINE_SLOW: case PATH_INLINE_MED: case PATH_INLINE_FAST:
						if(Math.abs(Player.posX - obj.posX) < posT)
							switch(iFreq)
								{
								case FIRE_Once:  	if(iRound == 1) fire(obj); 	break;
								case FIRE_2ndTime:  if(iRound == 2) fire(obj); 	break;
								case FIRE_Twice: 	if(iRound <= 2) fire(obj); 	break;
								case FIRE_Always:  	fire(obj); 					break;
								}
					break;
					}
			}
	}

	private void fire(Object obj) {
		reset();
		iRound++;
		bEnemyNewRound = false;

		posX += obj.posX;
		posY += obj.posY;
		iStatus = STATUS_Moving;

		switch(iType)
			{
			case SHOT_Small: 	SoundPlayer.playSound(Sound.SHOT_SINGLE);	break;
			case SHOT_Medium:
			case SHOT_PetSFire:	SoundPlayer.playSound(Sound.SHOT_DOUBLE);	break;
			case SHOT_DragFire: SoundPlayer.playSound(Sound.SHOT_FIRE);		break;
			}
	}
/************************************************************************************************************************
 *   METHOD -- Moves a shot, that's already been fired
*************************************************************************************************************************/
	protected void move() {
		int Animate = iSprite;
		switch(iStatus)
			{
			case STATUS_Moving:
				switch(iPath)
					{
					case PATH_INTERCEPT_BACK:
						pathInterceptBack();
					break;
					case PATH_INTERCEPT_SLOW: case PATH_INTERCEPT_MED: case PATH_INTERCEPT_FAST:
						pathIntercept();
					break;
					case PATH_INLINE_SLOW:   case PATH_INLINE_MED:   case PATH_INLINE_FAST:
					case PATH_STRAIGHT_SLOW: case PATH_STRAIGHT_MED: case PATH_STRAIGHT_FAST:
						pathStraight();
					break;
					}
			break;
			case STATUS_Smoke:
				posY -= !Mic.bBlowing ? Values.SCROLL_SPEED*2 : Values.SCROLL_SPEED/2  ;
				posX -= Values.SPEED1;
				if(smokeCount++ > 45)
					reload(FIRE_DISMISS);
				Animate = iSmoke + (smokeCount / 15);
			break;
			}

		transform();
		oSprite.draw(iTexture, Animate);
	}
/************************************************************************************************************************
 *   METHOD -- Different paths for Enemy fire, straight, intercept and Reverse
*************************************************************************************************************************/
	private void pathIntercept() {
		if(posY <= -1 || posX < -1 || posX > Screen.DEV_MAX_X || posY > Screen.DEV_MAX_Y)
			reload(FIRE_DISMISS);

		if(!bLockedOn)																		//Lock the target and calculate the slope to attack
			{
			var3 = ((Player.posX + 0.5f - posX)/(posY - Player.posY + 0.6f))*speed; 		// Calculate slope
			if(Math.abs(var3) > speed) var3 = (var3 > 0) ? speed : -speed; 					// For steep slop, var3 should not be too high
			bLockedOn = true ;																// Slope Calculated, target lockdown
			}

		posX += var3*Game.SPEED_MULT;
		posY -= speed*Game.SPEED_MULT;
	}

	private void pathInterceptBack() {
		if(posY < -1 || posX < -1 || posX > Screen.DEV_MAX_X || posY > Screen.DEV_MAX_Y)
			reload(FIRE_DISMISS);

		if(!bLockedOn)																		//Lock the target and calculate the slope to attack
			{
			var3 = ((Player.posX+0.5f - posX) / (Player.posY + 0.6f - posY ))*speed; 		// Calculate slope
			if(Math.abs(var3) > speed)
				{ var3 = (var3 > 0) ? speed : -speed;} 										// For steep slop, var3 should not be too high
			bLockedOn = true ;																// Slope Calculated, target lockdown
			}

		posX += var3*Game.SPEED_MULT;
		posY += speed*Game.SPEED_MULT;
	}

	private void pathStraight() {
		if(posY <= -1 || posX < -1 || posX > Screen.DEV_MAX_X || posY > Screen.DEV_MAX_Y)
			reload(FIRE_DISMISS);

		if(!bLockedOn)																		//Lock the target and calculate the slope to attack
			{
			var3 = ((Player.posX+0.5f - posX) / (posY - Player.posY + 0.6f))*speed; 		// Calculate slope
			if(Math.abs(var3) > (speed/3)) var3 = (var3 > 0) ? (speed/3) : -(speed/3); 		// For steep slop, var3 should not be too high
			bLockedOn = true ;																// Slope Calculated, target lockdown
			}

		posX += var3*Game.SPEED_MULT;
		posY -= speed*Game.SPEED_MULT;
	}

}
