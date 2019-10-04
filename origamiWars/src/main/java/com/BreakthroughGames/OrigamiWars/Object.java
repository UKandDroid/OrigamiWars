package com.BreakthroughGames.OrigamiWars;

public class Object extends Base {
	protected int iPower = 0;
	private boolean bFlash;																	// Used for Magic Scroll, flashing 
	private boolean bLockedOn;																// Has the player locked on for Intercept by Enemy
	protected boolean bActive;																// Is Object Active, should it be displayed on screen
	protected boolean bRunOnce;																// If Object is displayed once, for PowerUps and Eggs
	protected boolean bFalling;																// Flag for falling objects
	protected boolean bObstruct;															// If Enemy is obstruct, do not use extreme speed, slow every thing down
	protected boolean bFriendly;															// Friendly objects can be taken while invincible
	protected boolean bFires = false;														// Enemy fire  
	protected boolean bMarked = false;														// If the object is been marked	as a target for shock wave
	private float windDir = 1.0f;															// Variable used to change wind calculate direction, when player blows on screen
	protected int iObstCount = 0;															// counts for collision 
	protected float featherX = 0;															// Feather should gradually increase collision
	protected EnemyFire enemyFire = new EnemyFire();										// Enemy fire object
	protected float gravity = 0, position = 0;												// -ve gravity for flying up
	protected float driftX = Values.SPEED1 * 3;												// Maximum X-Movement for every frame
	protected float driftY = Values.SPEED4 ;												// Maximum Y-Movement for every frame
	private float mVelX = driftX, mVelY = driftY, defaultSpeed;								// Default speed is speed of object before blowing, as blowing slows down the speed
	private static float WIND_SPEED = Values.SPEED1 * 500;

	/*================================================End Variable Declarations=================================================*/
	public Object() {  }
	/*================================================Start Class Methods=======================================================*/

	protected void setTexture(int vTexture) { iTexture = vTexture; iSprite = 0;	}
	protected void setTexture(int vTexture, int vSprite) { iTexture = vTexture; iSprite = vSprite;	}
	protected void fireShot() { if(bFires) enemyFire.fireCheck(this);  }
	protected void moveShot() { if((enemyFire.iStatus & EnemyFire.STATUS_ACTIVE) > 0) enemyFire.move(); }
	protected void createFire(int vShot, int vPath, int vFreq) { bFires = true;	enemyFire.create(vShot, vPath, vFreq, iType); }
	protected void createFire(int vShot, int vPath, int vFreq, float vDistX) { bFires = true;	enemyFire.create(vShot, vPath, vFreq, iType, vDistX); }
	public void create(int vType, int vPath, float vPos, float vDist ) { enemyFire.iType = EnemyFire.SHOT_NONE; iTexture = Values.ARR_TEXTURES[vType]; iSprite = Values.ARR_SPRITES[vType]; iType = vType; iPath = vPath; position = vPos; distance = vDist; reset(); }
	protected void create(int vType, int vPath ) { enemyFire.iType = EnemyFire.SHOT_NONE; iTexture = Values.ARR_TEXTURES[vType]; iSprite = Values.ARR_SPRITES[vType];  iType = vType; iPath = vPath; reset();  }
	void reset() {
		iObstCount = 0;
		bEnable = true;
		posT = speed =  0;
		var1 = var2 = var3 = 0;
		bFires = enemyFire.create(iType);													// Returns true if object(Enemy) fires
		gravity = -0.4f+(ran.nextFloat()/2);
		posY = Screen.DEV_MAX_Y + (ran.nextFloat()*Screen.DEV_MAX_Y/2) ;
		bMarked = bActive = bObstruct = bFlash = bFalling = bRunOnce = bFriendly = bActive = bLockedOn = false;

		posX = ((Screen.DEV_MAX_X-1)/10)*position;
		posX = Values.clamp(posX, 0, (Screen.DEV_MAX_X-1));

		switch(iType){																	// Object type Initialisations
			case Values.ENEMY_LEAF: 			iPower = Values.ARR_POWER[iType];		offsetX = 0.30f;	offsetY = 0.30f;	driftY = Values.SPEED6; break;
			case Values.ENEMY_BUTTERFLY:		iPower = Values.ARR_POWER[iType];		offsetX = 0.15f;	offsetY = 0.35f;	featherX = 0.25f;	break;
			case Values.ENEMY_WASP:				iPower = Values.ARR_POWER[iType]; 		offsetX = 0.25f;	offsetY = 0.25f;	featherX = 0.15f;	break;
			case Values.ENEMY_DRAGONFLY:		iPower = Values.ARR_POWER[iType];		offsetX = 0.15f;	offsetY = 0.30f;	featherX = 0.28f;	break;
			case Values.ENEMY_HORNET:			iPower = Values.ARR_POWER[iType]; 		offsetX = 0.15f;	offsetY = 0.30f;	featherX = 0.25f;	break;
			case Values.ENEMY_BAT:				iPower = Values.ARR_POWER[iType]; 		offsetX = 0.15f;	offsetY = 0.25f;	featherX = 0.15f;	break;
			case Values.ENEMY_VULTURE:			iPower = Values.ARR_POWER[iType];  		offsetX = 0.30f;	offsetY = 0.30f;	featherX = 0;		break;
			case Values.ENEMY_PTEROSAUR: 		iPower = Values.ARR_POWER[iType];		offsetX = 0.20f;  	offsetY = 0.20f;	featherX = 0;		break;
			case Values.ENEMY_DRAGON:			iPower = Values.ARR_POWER[iType]; 		offsetX = 0.25f;	offsetY = 0.30f;	featherX = 0.05f;	break;
			case Values.POWERUP_EXTRALIFE: 	  	bActive = bRunOnce = bFriendly = true;	offsetX = 0.20f;	offsetY = 0.35f;	bEnable = false; 	break;
			case Values.POWERUP_DBARELL:    	bActive = bRunOnce = bFriendly = true;	offsetX = 0.20f;	offsetY = 0.35f;	bEnable = false; 	driftY = Values.SPEED3; break;
			case Values.POWERUP_MACHINEGUN: 	bActive = bRunOnce = bFriendly = true;	offsetX = 0.20f;	offsetY = 0.35f;	bEnable = false; 	driftY = Values.SPEED3; break;
			case Values.POWERUP_LIGHTNING: 	  	bActive = bRunOnce = bFriendly = true;	offsetX = 0.20f;	offsetY = 0.35f;	bEnable = false; 	driftY = Values.SPEED3; break;
			case Values.POWERUP_INVINCIBLE: 	bActive = bRunOnce = bFriendly = true;	offsetX = 0.20f;	offsetY = 0.35f;	bEnable = false; 	driftY = Values.SPEED3; break;
			case Values.POWERUP_TIME_SLOW: 	  	bActive = bRunOnce = bFriendly = true;	offsetX = 0.30f;	offsetY = 0.30f;	bEnable = false; 	break;
			case Values.SCROLL_NORMAL:   		bActive = bRunOnce = bFriendly = true;	offsetX = 0.25f; 	offsetY = 0.35f;	bEnable = false;	break;
			case Values.SCROLL_EVIL: 	  		bActive = bRunOnce = bFriendly = true;	offsetX = 0.25f; 	offsetY = 0.35f;	bEnable = false;	break;
			case Values.MAGIC_PAPER:   			bActive = bRunOnce = bFriendly = true;	offsetX = 0.28f;	offsetY = 0.25f;	bEnable = false;	break;
			case Values.MAGIC_SCISSOR: 			bActive = bRunOnce = bFriendly = true;	offsetX = 0.28f; 	offsetY = 0.25f;	bEnable = false;	break;
			case Values.MAGIC_STONE:   			bActive = bRunOnce = bFriendly = true;	offsetX = 0.28f; 	offsetY = 0.25f;	bEnable = false;	break;
			case Values.CURSE_PAPER:   			bActive = bRunOnce = bFriendly = true;	offsetX = 0.30f; 	offsetY = 0.60f;	bEnable = false;	break;
			case Values.CURSE_SCISSOR: 			bActive = bRunOnce = bFriendly = true;	offsetX = 0.30f; 	offsetY = 0.60f;	bEnable = false;	break;
			case Values.CURSE_STONE:   			bActive = bRunOnce = bFriendly = true;	offsetX = 0.30f; 	offsetY = 0.60f;	bEnable = false;	break;
		}

		switch(iPath){																				// Object Flight Path Initialization
			case Values.PATH_ATTRACT: 			posT = -40;								break;		// Used for slight delay, curse stays still for a while before getting attracted
			case Values.PATH_STATIONARY: 		posT = Values.SPEED3; 					break;
			case Values.PATH_INTERCEPT_SLOW:	defaultSpeed = driftY = speed = Values.SPEED4;			break;		// Drift_Y is used for path finding, using wind path
			case Values.PATH_INTERCEPT_MED:		defaultSpeed = driftY = speed = Values.SPEED5;			break;
			case Values.PATH_INTERCEPT_FAST:	defaultSpeed = driftY = speed = Values.SPEED6;			break;
			case Values.PATH_STRAIGHT_SLOW:		defaultSpeed = driftY = speed = Values.SPEED3;			break;
			case Values.PATH_STRAIGHT_MED:		defaultSpeed = driftY = speed = Values.SPEED4;			break;
			case Values.PATH_STRAIGHT_FAST:		defaultSpeed = driftY = speed = Values.SPEED5;			break;
			case Values.PATH_INLINE_SLOW:		defaultSpeed = driftY = speed = Values.SPEED3*1.2f; 	break;
			case Values.PATH_INLINE_MED:		defaultSpeed = driftY = speed = Values.SPEED4; 		break;
			case Values.PATH_INLINE_FAST:		defaultSpeed = driftY = speed = Values.SPEED5; 		break;
			case Values.SHOW_MAGIC:				posT = var2 = (int)Values.LEVEL_STATS[Game.iLevel][Values.LEVEL_EGG_TIME]; 	break;
			case Values.PATH_AVOID:
				var2 = Values.SPEED1/15;													// Increment for acceleration
				var3 = Values.SPEED2;														// Maximum avoid speed
				defaultSpeed = driftY = speed = Values.SPEED4;												// Y-Axis speed
				break;
			case Values.PATH_WIND:
				defaultSpeed = speed = driftY;
				gravity = -0.6f + (ran.nextFloat()/2);										// Gravity used for every object, when player blows on the screen
				break;
			case Values.PATH_RANDOM:
				defaultSpeed = driftY = speed = Values.SPEED4;												// Y-Axis speed
				posT = Values.SPEED2 ; 														// X-Axis speed, set 0.03f for random Movement 
				break;
			case Values.PATH_SINE_WAVE:
				var2 = posX;																// x- position for wave
				var3 = ran.nextFloat()+0.6f;												// Wave amplitude
				if(var3 > 0.8f) var3 = 0.8f;
				defaultSpeed = driftY = speed = Values.SPEED4;												// Y-axis speed
				posT = Values.SPEED4 +(ran.nextFloat()*Values.SPEED2) ; 					// Wave frequency
				break;
		}
	}
	/************************************************************************************************************************
	 *	METHOD
	 ************************************************************************************************************************/
	void move() {
		if(Mic.bBlowing && ( getType() & Values.TYPE_WIND_STABLE ) == 0){					// Magic, Curse and Scrolls should not be effected by wind Blowing by Mic
			if(Mic.eWindStatus == Mic.WIND_SLOWING || Mic.eWindStatus == Mic.WIND_STOP){
				speed = speed/1.07f;
				driftY = driftY/1.07f;
			}
			windBlowing();

			if(iPath == Values.PATH_SINE_WAVE){ 												// If path is sine wave, wave should start, where every blow ends
				var2 = posX; var1 = 0; }														// var2 is the starting position of Wave
		} else {
			if(speed < defaultSpeed){ speed*= 1.07f; driftY *= 1.07f; }
			switch(iPath){																	// switch on attack type
				case Values.PATH_STATIONARY: 		pathStationary();		break;
				case Values.PATH_RANDOM:			pathRandom();		 	break;
				case Values.PATH_ATTRACT: 	 		pathAttract();			break;
				case Values.SHOW_MAGIC: 			showScroll();			break;
				default:
					if(bObstruct)	{															// If object is obstructed i.e there are clouds infront,																	//  use pathWind() for next 10 frames, to help it find way
						pathWind(); 															//  around the clouds
						if(iObstCount-- < 1){													// Now switch back to Original path method
							bObstruct = false;
							if(iPath == Values.PATH_SINE_WAVE)
							{ var2 = posX; var1 = 0; }		// For sine wave, wave starting position should be where object is now
						}
					} else switch(iPath) {
						case Values.PATH_WIND:  			pathWind(); 			break;
						case Values.PATH_AVOID:				pathAvoid();			break;
						case Values.PATH_SINE_WAVE:		 	pathSine();		 		break;
						case Values.PATH_INTERCEPT_SLOW:
						case Values.PATH_INTERCEPT_MED:
						case Values.PATH_INTERCEPT_FAST:	pathIntercept();	 	break;
						case Values.PATH_STRAIGHT_SLOW:
						case Values.PATH_STRAIGHT_MED:
						case Values.PATH_STRAIGHT_FAST:		pathStraight(); 		break;
						case Values.PATH_INLINE_SLOW:
						case Values.PATH_INLINE_MED:
						case Values.PATH_INLINE_FAST:		pathInline();		 	break;
					}
			}
		}
	}
	/************************************************************************************************************************
	 *	METHOD
	 ************************************************************************************************************************/
	public void draw() {
		int bBurnSprite = 0;
		if(getType() == Values.TYPE_ENEMY)												// If object is an enemy, then use burn sprites if
			bBurnSprite = iPower <= (Values.ARR_POWER[iType]/2) ? 8 : 0 ;
		// If flash Flag in not set, then Draw the Object
		oSprite.draw(iTexture, iSprite + bBurnSprite);
		if(bMarked)
			oSprite.draw(Game.txtPlaneFire.iTexture, 7);								// Draw cross mark on object, as its been marked
	}
	/************************************************************************************************************************
	 *	METHOD -- Sprites and animation when object is destroyed and is falling down
	 ************************************************************************************************************************/
	protected void Destroy( int vTxt, int vSprite) {
		posT++;																				// is used to delay destroyed object converting into powerUp or something
		switch(getType()) {
			case Values.TYPE_CURSE:
			case Values.TYPE_MAGIC:
				bFlash = false;
				posY = Player.posY;
				posX = Player.posX;
				transform();

				if(getType() == Values.TYPE_CURSE) {
					if(posT > 26 )			oSprite.draw(Game.txtPowUp.iTexture, 10);		// Draw 3rd curse sprite
					else if(posT > 13)		oSprite.draw(Game.txtPowUp.iTexture, 9);		// Draw 2nd curse sprite
					else					oSprite.draw(Game.txtPowUp.iTexture, 8);		// Draw 1st curse sprite
				} else {
					if(posT > 26 )			oSprite.draw(Game.txtPowUp.iTexture,  7);		// Draw 3rd magic sprite
					else if(posT > 13)		oSprite.draw(Game.txtPowUp.iTexture,  6);		// Draw 2nd magic sprite
					else					oSprite.draw(Game.txtPowUp.iTexture,  5);		// Draw 1st magic sprite
				}
				if(posT > 40) 	bActive = bFalling = false;									// Disable the Curse Smoke
				break;
			default :
				if(iType != Values.ENEMY_LEAF && posT == 10 ) {
					if(speed == -1)															// (speed -1) Enemy is destroyed by fire, not collision
						Game.pssGame.createItem(this);										// Create PowerUps/Scrolls
					Game.enemyDestroyed++;													// Add to enemy destroyed counter
				}
				if(posX > Screen.DEV_MAX_X) {
					if((enemyFire.iStatus&EnemyFire.STATUS_STANDBY) > 0){					// if destroyed enemy has fired a shot, don't create a new enemy yet
						bActive = bFalling = false;
						Game.createEnemy(this);
					}
				} else {
					transform();
					if(posT < 15)		oSprite.draw(vTxt, vSprite + 0);					// Draw 1st falling sprite
					else if(posT < 30)	oSprite.draw(vTxt, vSprite + 1);					// Draw 2nd falling sprite
					else				oSprite.draw(vTxt, vSprite + 2);					// Draw 3rd falling sprite

					posY -= Values.SPEED1 * 4 ;
					posX =  posX + (Values.SPEED1 * (posT/3)) + 1/6 * (5f * (posT*posT));
				}
				break;
		}
	}
	/************************************************************************************************************************
	 *	PATH Calculation Methods
	 *************************************************************************************************************************/
	private void pathIntercept() {
		if(posY <= -1)																		// If it has gone off-screen without getting destroyed, 
			startAgain();

		if(posY >= (Screen.DEV_MAX_Y - 2.0f))
			posY-=  Values.SPEED2*Game.SPEED_MULT ;
		else {
			if(!bLockedOn){																	//Lock the target and calculate the slope to attack
				var2 = ((Player.posX+0.5f - posX) / (posY - Player.posY + 0.5f))*speed; 	// Calculate slope
				if(Math.abs(var2) > speed) var2 = (var2 > 0) ? (speed) : -(speed);
				bLockedOn = true ;															// Slope Calculated, target lock down
			}

			if(bObstruct)																	// if Object is obstructed, slow it
				posX += (var2/2)*Game.SPEED_MULT;
			else
				posX += var2*Game.SPEED_MULT;

			posY -= speed*Game.SPEED_MULT;
		}
	}
	/************************************************************************************************************************
	 *	METHOD
	 ************************************************************************************************************************/
	private void pathAttract() {
		if(Player.bFalling)
			bMarked = bActive = bEnable = false;											// If player is destroyed, disable the curse

		if(posT++ > 0){																		// when delay is over, attract the curse
			float tempInc = 0;
			tempInc = (Values.SPEED1 * (posT/4)) + 1/6 * (3f * (posT*posT));

			if(Player.posY < posY)
				posY -= tempInc*Game.SPEED_MULT;
			else
				posY += tempInc*Game.SPEED_MULT;

			var2 =(float)(( Player.posX - posX )/((posY - Player.posY)*(posY - Player.posY)));

			var2 = Values.clamp(var2, -tempInc, tempInc);

			posX += var2*Game.SPEED_MULT;
		}
	}
	/************************************************************************************************************************
	 *	METHOD
	 ************************************************************************************************************************/
	private void pathStraight() {
		if(posY <= -1)
			startAgain();

		if(posY > (Screen.DEV_MAX_Y - 2.0f))
			posY -= Values.SPEED2 * Game.SPEED_MULT;
		else {
			if(!bLockedOn){
				var1 = ran.nextFloat() * (Screen.DEV_MAX_X - 1) + 0.2f;
				var2 = (float) ((var1 - posX) / ((posY - Player.posY) / Values.SPEED1));
				bLockedOn = true;
			}
			posY -= speed*Game.SPEED_MULT;
			posX += var2*Game.SPEED_MULT;
		}
	}
	/************************************************************************************************************************
	 *	METHOD -- Avoid the plane by moving up or down when it gets in-line
	 ************************************************************************************************************************/
	private void pathAvoid() {
		if(posY <= -1)
			startAgain();

		if(Player.posX > posX-1 && Player.posX < posX+1)									// Enemy is in-line with the plane
			var1 += (Player.posX < posX) ? var2 : -var2;									// if plane is up, move down and vice versa
		else if(var1 != 0)
			var1 += (var1 < 0) ? var2 : -var2;												// else stop moving the enemy up or down

		if((posX+var1) > 0 && posX < Screen.DEV_MAX_X-1)
			posX += var1;
		posY  -= speed*Game.SPEED_MULT;
	}
	/************************************************************************************************************************
	 *	METHOD -- Blow every object, called when wind is blowing
	 ************************************************************************************************************************/
	private void windBlowing() {
		float curX, curY;

		if((posX > (Screen.DEV_MAX_X-1)) || posX < 0)
			mVelX *= -1;

		windDir += (Mic.eWindStatus == Mic.WIND_BLOWING && ran.nextFloat() > 0.25f) ? -0.07f : 0.07f;
		windDir = Values.clamp(windDir, -1, 1);

		if(bObstruct) posX += ((mVelX/2)*Game.SPEED_MULT);
		else          posX += (mVelX*Game.SPEED_MULT);

		posY  -= (speed*windDir*Game.SPEED_MULT);

		curX = (((ran.nextFloat()*2.5f) - 1.0f + gravity) / 1000) * WIND_SPEED  ;
		curY = (((ran.nextFloat()*2.5f) - 1.0f) / 1000) * WIND_SPEED ;
		mVelX += (Math.abs(curX) > 0.0015) ? curX/2 : curX;									// Smooth jerks in object motion
		mVelY += (Math.abs(curY) > 0.0015) ? curY/2 : curY;

		mVelX  = Values.clamp(mVelX, -driftX, driftX);
		mVelY  = Values.clamp(mVelY,  driftY/4, driftY);
	}
	/************************************************************************************************************************
	 *	METHOD -- path wind, object moves as flying in the wind
	 ************************************************************************************************************************/
	private void pathWind() {
		if(posY < -1)
			startAgain();

		float curX, curY;

		if((posX > (Screen.DEV_MAX_X -1)) || posX < 0)										// Don't get object out of screen  top and bottom
			mVelX *=-1;

		posX += bObstruct ? (mVelX/2)*Game.SPEED_MULT : mVelX*Game.SPEED_MULT;
		posY -= mVelY*Game.SPEED_MULT;

		curX = (((ran.nextFloat()*2.5f) - 1.0f + gravity) / 1000) * WIND_SPEED  ;
		curY = (((ran.nextFloat()*2.5f) - 1.0f) / 1000) * WIND_SPEED ;
		mVelX += (Math.abs(curX) > 0.0015) ? curX/2 : curX;									// Smooth, jerks in object motion
		mVelY += (Math.abs(curY) > 0.0015) ? curY/2 : curY;

		mVelX  = Values.clamp(mVelX, -driftX, driftX);
		mVelY  = Values.clamp(mVelY,  driftY/4, driftY);
	}
	/************************************************************************************************************************
	 *	METHOD
	 ************************************************************************************************************************/
	protected void pathInline() {
		if(posY < -1)
			startAgain();

		posT += (posX < Player.posX) ? 0.001: -0.001f;
		posT = Values.clamp(posT, -speed/2.5f, speed/2.5f);

		posX += posT*Game.SPEED_MULT;
		posY -= speed*Game.SPEED_MULT;
	}
	/************************************************************************************************************************
	 *	METHOD -- calculates random Ups and Down
	 ************************************************************************************************************************/
	protected void pathRandom() {
		if(posY < -1)																		// Initialise Speed and Increment to IncToTargetX
			startAgain();

		if(var2++ % 14 == 0 && ran.nextFloat()> 0.5)
			posT *= -1;																		// Reverse direction

		posX += (posT*Game.SPEED_MULT) ;
		posY -= (speed*Game.SPEED_MULT);

		if(posX <= 0 || posX >= Screen.DEV_MAX_X - 1.0f) 									// If object is going outside the screen, change direction
			posT *= -1;
		posX = Values.clamp(posX, 0, Screen.DEV_MAX_X - 1.0f);
	}
	/************************************************************************************************************************
	 *	METHOD -- Show magic Egg Stays on the screen, till Touch or disappear
	 ************************************************************************************************************************/
	private void showScroll() {
		if(posT-- == 0 || !Game.pssGame.bIsCursed)
			bMarked = bActive = bEnable = false;

		if(((posT/var2) < 0.2f) && posT%16 == 0)
			bFlash = !bFlash;
	}
	/************************************************************************************************************************
	 *	METHOD
	 ************************************************************************************************************************/
	protected void pathSine() {
		if(posY < -1)
			startAgain();

		float temp = Math.abs(Player.posX - var2);
		temp /= Screen.DEV_MAX_X;															// As enemy gets near the player, Reduce amplitude

		var1 += posT;
		var2 += (var2 < Player.posX) ? 0.008: -0.008f;

		posX = (float) java.lang.Math.sin(var1)*var3*Values.clamp(temp, 0.5f, 1);			// Var3 is amplitude, 	
		posX = (posX*Game.SPEED_MULT) + var2;												// Var1 is original posX, the axis around which wave oscillates

		posY-= speed*Game.SPEED_MULT;
	}
	/************************************************************************************************************************
	 *	METHOD StartAgain - Reset flags, var1 and positions once object gets out of screen
	 ************************************************************************************************************************/
	private void startAgain() {
		var1 = 0;
		bMarked = bLockedOn = false;
		bActive = bEnable = !bRunOnce;														// Object should run once across the screen then gets disabled
		if(bFires) bFires = enemyFire.reload(EnemyFire.ROUND_COMPLETE);
		posX = Values.clamp(posX + (ran.nextFloat() - 0.5f)*2, 0, Screen.DEV_MAX_X-2);
		posY = Screen.DEV_MAX_Y + (ran.nextFloat()*Screen.DEV_MAX_Y/2);
	}
	/************************************************************************************************************************
	 *	METHOD
	 ************************************************************************************************************************/
	protected boolean Damage(int damage) {
		iPower -= damage;
		if(iPower < 1) {
			posT = 0 ;																		// Reset PosT in-case its used, counter for animation and creating new object
			bEnable = false;
			bFalling = true;
			bMarked = false;
			return true;
		}
		return false;
	}
	/************************************************************************************************************************
	 *	METHOD -- Object moves towards the player, as they are still and Player is moving towards them
	 ************************************************************************************************************************/
	protected void pathStationary() {
		if(posY < -1) {
			bMarked = bActive = bEnable = false;
		}

		posY -= posT*Game.SPEED_MULT;
	}
	/************************************************************************************************************************
	 *	METHOD
	 ************************************************************************************************************************/
	protected void pathWobble() {
		if(posY < -1)
			bActive = bEnable = !bRunOnce;

		posY -= posT*Game.SPEED_MULT;

		if(Math.abs(speed) > 0.5f)
			var3*=-1;

		speed += var3;
		if((posX + var3) > 0 && (posX + var3) < (Screen.DEV_MAX_X-1))
			posX += (var3*Game.SPEED_MULT);
	}
	/************************************************************************************************************************
	 *   METHOD- Returns damage cased by a hit
	 ***********************************************************************************************************************/
	protected int hitDamage(Weapon shot) {
		float overLap = shot.iDamage;

		if(featherX != 0) {
			float firePos = (shot.posX - posX) + 0.5f;
			float offset = firePos < 0.5f ? offsetX : 1-offsetX;
			overLap = Math.abs(firePos-offset)/featherX;
			overLap += ran.nextFloat()/2;
			if(overLap > 1) overLap = 1;
			overLap *= shot.iDamage;
		}
		return (int) Values.clamp(Math.round(overLap), 0, Math.min(shot.iDamage, iPower));
	}
	/************************************************************************************************************************
	 *	Returns object generic type e.g Enemy, Obstacle, Curse, Magic
	 ************************************************************************************************************************/
	protected int getType() {
		if(iType <= Values.ENEMY_END)
			return Values.TYPE_ENEMY;
		else if(iType == Values.OBJECT_OBSTACLE)
			return Values.TYPE_OBSTACLE;
		else if(iType >= Values.POWERUP_EXTRALIFE && iType <= Values.POWERUP_CURSE_BREAK)	// PowerUps
			return Values.TYPE_POWERUP;
		else if(iType == Values.SCROLL_EVIL || iType == Values.SCROLL_NORMAL)
			return Values.TYPE_SCROLL;
		else if(iType == Values.CURSE_PAPER || iType == Values.CURSE_SCISSOR || iType == Values.CURSE_STONE)
			return Values.TYPE_CURSE;
		else if(iType == Values.MAGIC_PAPER || iType == Values.MAGIC_SCISSOR || iType == Values.MAGIC_STONE)
			return Values.TYPE_MAGIC;
		else if(iType == Values.FIRE_ENEMY)
			return Values.TYPE_ENEMY_FIRE;
		return -1;
	}
} 