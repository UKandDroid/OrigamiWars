package com.BreakthroughGames.OrigamiWars;

public class Weapon extends Base
{		
	public static final int WEAPON_OFF 	    = 0;
	public static final int WEAPON_NEXT 	= 1;
	public static final int WEAPON_PREVIOUS = 2;
	public static final int WEAPON_STANDBY  = 3;
	public static final int WEAPON_READY 	= 4;
	public static final int WEAPON_FIRE 	= 5;
	
	private static final int SWAVE_OFF 		= 0;
	protected static final int SWAVE_FIRE 	= 1;
	
	private static final int NORMAL_DAMAGE 	= 1;									// Damage caused by a Weapon
	private static final int MACGUN_DAMAGE 	= 1;				
	private static final int DBARELL_DAMAGE = 2;				
	private static final int BOLT_DAMAGE 	= 7;				
	private static final int SWAVE_DAMAGE 	= 7;				
	
	protected static final int MAX_SHOTS 	 = 8;									// size of shots 
	protected static final int DEFAULT_GUN 	 = 0;
	protected static final int MACHINE_GUN 	 = 1;
	protected static final int DBARELL_GUN 	 = 2;
	protected static final int BOLT_GUN 	 = 3;
	protected static final int SHOCK_WAVE 	 = 4;
	
	protected static final int MACGUN_MAX_SHOTS  = 350;
	protected static final int MACGUN_POWERUP    = 70;
	protected static final int DBARREL_MAX_SHOTS = 200;
	protected static final int DBARREL_POWERUP   = 40;
	protected static final int BOLT_MAX_SHOTS  	 = 60;
	protected static final int BOLT_POWERUP 	 = 12;
	protected static final int INVINCIBLE_PUP 	 = 300;
	protected static final int INVINC_CURSE_BREAK = 180;
	
	protected static final int SW_INDEX = MAX_SHOTS-1 ;
	
	protected static final float SPEED_BULLET = Values.SPEED1 * 15;
	private static final float SPEED_SWAVE    = SPEED_BULLET*1.2f;
	private static final float SW_INC_SPEED   = SPEED_BULLET/8;
	private static final float CONTROLLER_SWITCH = SPEED_BULLET/45;
	private static float incX, incY, oldX, oldY, angleSW = 0, limitInt = 0, incLimit = 0;	// Shock wave speed and old position
	private static final int SWAVE_RECHARGE = Game.GAME_FPS;						// Time to Recharge shock wave
	protected static final float FIRE_Y_POS = Player.START_Y + 0.3f;
	
/*************************************************************************************************************************************************************************
 * --------------------------------------------END CONSTANTS DECLARATION-------------------------------------------------------------------------------------------------
**************************************************************************************************************************************************************************/
	protected static int eState;													// Weapon state, standby, ready
	protected float 	 iDamage = 1;
	private static int   iSWCharger = 0;											// Counter, charging time for shock wave
	private static int   curSmokeSpr = 0; 											// Smoke sprite starting for current weapon
	protected static int shotGap = 5; 												// Distance between shots by frames
	protected static int shotsBurst = 4; 											// Burst shot for current weapon
	protected static int curDamage = 1; 											// Damage caused by current weapon
	protected static int shotsFired = 0;
	protected boolean    bSmoke = false;
	private static float selOffsetX = 0.45f;
	private static float selOffsetY = 0.45f;
	private static int   defaultSpr, curShotSpr; 									// Current Weapon shot sprite
	protected static int curWeapon = DEFAULT_GUN; 									// Currently selected Weapon
	private int 		 iSmoke = 0, smokeCount = 0;								// Smoke sprite for particular fire
	private static int 	 arShots[] = {6000,0,0,0};									// 0- Normal, 1-double, 2-fast, 3-lightning,array to store the rounds of each weapon
	protected static int arLvlShot[]= {6000,0,0,0,0};								// shots at the start of every level, used when player restarts a level
	protected static int arRounds[] = {4, 0, 0, 0};
	
	Weapon()	{  }
	Weapon( int vText, int vSprite)
	{
		iType = DEFAULT_GUN;
		iTexture = vText;
		defaultSpr = vSprite;
		posT = 0;
	}	
	
	protected void create( int vText, int vSprite)
	{
		iTexture = vText;
		iType = DEFAULT_GUN;
		defaultSpr = vSprite;
		posX = posY = posT = 0;
		bEnable = bSmoke = false;
	}
	
	protected static int addShots(int vType, int vShots)
	{
		if(vShots == -1)
			arShots[vType] = 0;
		else
			arShots[vType] += vShots;
		
		return arRounds[vType] = getRounds(vType);
	}

	protected static int getShots(int vType)
	{
		return arShots[vType];
	}

/************************************************************************************************************************
 *   METHOD- Draws bullet or smoke sprite, depending if bullet has hit something
***********************************************************************************************************************/
	public void drawShot()
	{
		if(bSmoke)																	// if bullet has hit something, draw smoke
			{													
			if(smokeCount++ < 15)	  oSprtie.draw(iTexture, iSmoke);
			else if(smokeCount < 30)  oSprtie.draw(iTexture, iSmoke + 1);
			else if(smokeCount < 45)  oSprtie.draw(iTexture, iSmoke + 2);
			else{				  	  bSmoke = false; smokeCount = 0; }
			}
		else
			oSprtie.draw(iTexture, iSprite);
	}
/************************************************************************************************************************
 *   METHOD- Moves shots
***********************************************************************************************************************/
	public void moveShot()
	{
		if(bSmoke)																	// If bullet has hit something, draw smoke	  
			{ 
			posY -= !Mic.bBlowing ? Values.SCROLL_SPEED*2 : Values.SCROLL_SPEED/2  ;
			posX -= SPEED_BULLET/7;
			}
		else	
			{
					
			if(iType == Weapon.SHOCK_WAVE)
				moveShockWave();
			else
				posY += SPEED_BULLET; 												// Move the bullet	
				
			if(posY > Screen.DEV_MAX_Y  || posY < -1 || posX < -1 || posX > Screen.DEV_MAX_X  ) 			// If shot has left the screen, disable it	
				{ shotsFired --; bEnable = false; }															
		
			}
		if(iType == Weapon.SHOCK_WAVE)
			transform(angleSW);
		else
			transform();
		
		drawShot();																	// Draw method that draws smoke
	}
/************************************************************************************************************************
 *   METHOD- Fires new bullet
***********************************************************************************************************************/
	public int fireNewShot()
	{	
		switch(curWeapon)
			{
			case DEFAULT_GUN:	Adventure.events.dispatch(Events.SHOT_NORMAL); 	break;
			case DBARELL_GUN:	Adventure.events.dispatch(Events.SHOT_DBARELL);	break;
			case MACHINE_GUN:	Adventure.events.dispatch(Events.SHOT_MACHINE);	break;
			case BOLT_GUN:		Adventure.events.dispatch(Events.SHOT_LIGHTNING);break;
			}

		bEnable = true;
		bSmoke = false;
		shotsFired++;
		arShots[curWeapon]--;														// Remove one shot from weapon
		arRounds[curWeapon] = getRounds(curWeapon);
		posX = Player.posX + 0.04f;
		posY = Player.posY + 0.55f + SPEED_BULLET;
		iType   = curWeapon;														// The bullet/round is of the currently selected weapon type
		iSprite = curShotSpr;
		iSmoke  = curSmokeSpr;
		iDamage = curDamage;
		offsetX = selOffsetX;
		offsetY = selOffsetY;
		if(arShots[curWeapon] <= 0)													// If weapon has run out of shots, reset it 														
			{ 
			arShots[curWeapon] = 0;													// Reset if it goes -ve
			setNormalGun();		 													// Set default weapon
			return -2; 																// return weapon empty
			}
		return 0;
	}
/************************************************************************************************************************
 *   METHOD- Checks for weapon switch, weapon ready or fire
***********************************************************************************************************************/
	static int check()
	{
		switch(eState)
			{
			case WEAPON_NEXT: 
			case WEAPON_PREVIOUS: 
				if(eState == WEAPON_NEXT )
					do
					{	curWeapon++;
						curWeapon = (curWeapon > 3) ? 0 : curWeapon; 
					}while(arShots[curWeapon] < 1);
				else
					do
					{	curWeapon--; 
						curWeapon = (curWeapon < 0) ? 3 : curWeapon; 
					}while(arShots[curWeapon] < 1 );
					
				switch(curWeapon)
					{
					case DEFAULT_GUN:   setNormalGun();		break;
					case DBARELL_GUN: 	setBarellGun();		break;
					case MACHINE_GUN: 	setMachineGun(); 	break;
					case BOLT_GUN: 		setBoltGun();		break;
					}
				eState = WEAPON_OFF;
			break;
			}
	
		return checkShockWave();
	}
/************************************************************************************************************************
 *   METHOD- checks if shock wave is charged and fired
***********************************************************************************************************************/
	private static int checkShockWave()
	{
		int temRetrun = SWAVE_OFF;
		if(Screen.bShockWave && Player.bEnable && !Player.bFalling)					// Screen has been flicked
			if(iSWCharger >= SWAVE_RECHARGE )										// If shockwave is charged
				{
				iSWCharger = 0;
				temRetrun  = SWAVE_FIRE;
				}
		iSWCharger++ ;
		Screen.bShockWave = false;
		return temRetrun;
	}
/************************************************************************************************************************
 *   METHODS- methods to set diff weapons
***********************************************************************************************************************/
		
	public static void setNormalGun()
	{
		curWeapon = DEFAULT_GUN;													// Set default weapon
		arShots[DEFAULT_GUN] = 60000;
		shotsBurst = 4;
		curDamage = NORMAL_DAMAGE;
		shotGap = 10;																// Shot Gap in Frames
		curShotSpr = defaultSpr;
		curSmokeSpr = 8;
		selOffsetX = 0.45f;
		selOffsetY = 0.5f;
	}
	
	public static void setMachineGun()
	{
		curWeapon = MACHINE_GUN;
		shotsBurst = 8;
		shotGap = 6;
		curShotSpr = 1;
		curDamage = MACGUN_DAMAGE;
		curSmokeSpr = 8;
		selOffsetX = 0.45f;
		selOffsetY = 0.5f;
	}
	
	public static void setBarellGun()
	{
		curWeapon = DBARELL_GUN;
		shotsBurst = 5;
		shotGap = 10;
		curShotSpr = 2;
		curDamage = DBARELL_DAMAGE;
		curSmokeSpr = 8;
		selOffsetX = 0.30f;
		selOffsetY = 0.5f;
	}
	
	public static void setBoltGun()
	{
		curWeapon = BOLT_GUN;
		shotsBurst = 2;
		shotGap = 15;
		curShotSpr = 3;
		curDamage = BOLT_DAMAGE;
		curSmokeSpr = 12;
		selOffsetX = 0.40f;
		selOffsetY = 0.30f;
	}
	
	public void fireShockWave()
	{	
		Adventure.events.dispatch(Events.SHOCK_WAVE);
		shotsFired++;
		iSmoke = 13;																// Smoke sprite for shock wave
		iSprite = 11;																// Shock wave sprite
		bEnable = true;
		offsetX = 0.35f;															// Shock wave empty space
		offsetY = 0.30f;
		iType = SHOCK_WAVE;
		iDamage = SWAVE_DAMAGE;
		limitInt = posT = incX = 0;
		oldX = posX = Player.posX;
		oldY = posY = Player.posY + 0.38f;
		Object target = Adventure.oTarget; 
		incLimit = incY = SPEED_BULLET/1.1f;
				
		if(!target.bEnable)
			target.bMarked = false;
	}
	
	public void moveShockWave()
	{
		Object target = Adventure.oTarget;
		float deltaX, deltaY;
		float interX = 0, interY = 0, speedMult, ratio;									
		
		if(bEnable && target.bMarked)												// If shock wave is fired and Target it locked
			{
				if(limitInt < SPEED_SWAVE)  limitInt += CONTROLLER_SWITCH;			// The method starts with increment controller with X,Y increments toward target, 
				if(incLimit > 0) 			incLimit -= CONTROLLER_SWITCH;			// and slowly switches to intercept controller, so motion is smooth and not straight line   
				
				deltaX = target.posX - posX;
				deltaY = target.posY - posY;
				interX = deltaX / deltaY;											// Get slope for target intercept
				interY = (target.posY < posY) ? -limitInt : limitInt;				// Move, towards target infront or behind
				interX *= interY;													// Get X-Speed for intercept
				
				ratio = deltaY/Math.abs(deltaX);									
				if(ratio < 0.3) ratio = 0.3f;
				if(ratio > 3)   ratio = 3;
				
				if(Math.abs(deltaX) > 0.1)											// If shock wave is not inline with the target, add increment 
					incX += (target.posX < posX) ? -SW_INC_SPEED : SW_INC_SPEED;
				else if(Math.abs(incX) > SW_INC_SPEED)								// Inline shockWave remove increment
					incX += (incX > 0) ? -SW_INC_SPEED : SW_INC_SPEED;
				
				if(Math.abs(deltaY) > 0.1)
					incY += (target.posY < posY) ? -SW_INC_SPEED : SW_INC_SPEED;
				else if(Math.abs(incY) > SW_INC_SPEED)
					incY += (incY > 0) ? -SW_INC_SPEED : SW_INC_SPEED;
				
				incX = Values.clamp(incX, -incLimit/ratio, incLimit/ratio);
				incY = Values.clamp(incY, -incLimit, incLimit);
				
				if(interX < 0) interX = (float) Math.log1p(Math.abs(interX))*-1;
				else		   interX = (float) Math.log1p(interX);
				if(Math.abs(interX) > limitInt) { interY *= (limitInt/Math.abs(interX)); interX *= (limitInt/Math.abs(interX));} 
										
				incX = incX + interX; 								// Move the bullet
				incY = incY + interY;
				
				speedMult = Math.abs(incX)+Math.abs(incY); 
				if(speedMult > SPEED_SWAVE)
					{
					incX *= SPEED_SWAVE/speedMult;
					incY *= SPEED_SWAVE/speedMult;
					}
				}
		
			posX += incX;
			posY += incY;
		
			if(oldX >= posX)
				angleSW = ((float) (Math.atan((oldY - posY)/(oldX - posX))*180/Math.PI)) + 90;
			else
				angleSW = ((float) (Math.atan((oldY - posY)/(oldX - posX))*180/Math.PI)) + 270;
	//		Values.log("shockwave", "Angle:"+ (int)angleSW +"  oldX:"+String.format("%.2f", oldX)+ "  oldY:"+String.format("%.2f", oldY) + "  addX:"+String.format("%.2f", incX)+ "  addY:"+String.format("%.2f", incY));
			oldX = posX;
			oldY = posY;	
	}
	
/************************************************************************************************************************
 *   METHOD - Selects next weapon,  Returns round for every weapon, used to display hud
***********************************************************************************************************************/
	
	
	protected static int getRounds(int vType)										
	{
		int round = 0;
		switch(vType)											
			{
			case DEFAULT_GUN: 	round = 4;								break;
			case MACHINE_GUN:	round = (arShots[vType] + 69) / 70;		break;
			case DBARELL_GUN: 	round = (arShots[vType] + 39) / 40;		break;		// Return 1 for shot less then 40, till 0 shots
			case BOLT_GUN:		round = (arShots[vType] + 11) / 12;		break;
			}
		
		return round > 4 ? 4: round;												// Maximum rounds return are 4
	}

}//End class Weapon
