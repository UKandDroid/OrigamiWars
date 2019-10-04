package com.BreakthroughGames.OrigamiWars;


public class PowerUps 
{	
	private int iType;												// Origami to be created for destroyed enemy
	protected int iCurCurse;										// Type of Current Curse
	protected int iTotalTime;										// Total time for attack
	protected int iCurResult;										// Consecutive wins or loose, +ve win, -ve loose			 
	protected int iCurseTimer;										// Timer for Curse, when player time runs out
	protected boolean bIsCursed;									// Flag to check if player is cursed
	private boolean bCurseCreated;									// if a curse has been created but hasn't hit the plane yet
	private boolean bAntiCurseCreated;								// Flag to create one anti curse b4 time runs out
	protected float percPUps, percScrl, percCurse, perDarkScroll, percEmpty;
	protected static int iLevelWins, iLevelLoses, iTotalWins = 0, iTotalLoses = 0;	// level win/loose and total win/loose

	PowerUps()	{	reset();	} 									// Constructor
	
/************************************************************************************************************************
*    METHOD -- Resets Curses and Magic Stats, for New Level or level Restart
************************************************************************************************************************/
	public void reset()												// Reset All variable for new level
	{
		iLevelLoses = iLevelWins = iCurseTimer = 0; 
		iTotalTime = iCurCurse = iType = iCurResult = 0;
		bAntiCurseCreated = bCurseCreated = bIsCursed = false;
		
		if(Adventure.iLevel >= 0 && Values.LEVEL_STATS[Game.iLevel].length > 0)	// Don't calculate Stats for Story Levels
			{
			float cursePerc = Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_CURSE_PERC];	// % of curses for level (divided into Curses and Death Scrolls)
			float otherPerc = 1.0f - cursePerc;												// % of other objects in the level

			percPUps    = 	(otherPerc/100)*50;						// 40% Power ups 
			percScrl    =	percPUps + (otherPerc/100)*30;			// 40% Origami scrolls
			percEmpty   = 	percScrl + (otherPerc/100)*20;			// 20% Nothing
			percCurse   = 	percEmpty + (cursePerc/100)*35;			// 40% Curses
			perDarkScroll = percCurse + (cursePerc/100)*65;			// 60% Dark Scrolls
			}
	}
	
	void removeCurse()												// Removes curse, Resets all variables
	{	
		iTotalTime = iCurseTimer = iCurCurse = 0; 
		bAntiCurseCreated = bCurseCreated = bIsCursed = false;
	//	Game.resetSpeed();
	}
	
/************************************************************************************************************************
*    METHOD -- sets params when player is cursed
************************************************************************************************************************/
	protected void setCurse(Object curse)
	{		
		bIsCursed = true;
		iCurCurse = curse.iType;
		float curseSpeed = Game.SPEED_MULT - 0.05f;
		if(curseSpeed < Values.LEVEL_STATS[Game.iLevel][Values.LEVEL_CURSE_SPEED])
			curseSpeed = Values.LEVEL_STATS[Game.iLevel][Values.LEVEL_CURSE_SPEED];
		Game.setSpeed(curseSpeed);
		iTotalTime = iCurseTimer = (int) Values.LEVEL_STATS[Game.iLevel][Values.LEVEL_MAGIC_TIME];
	}
	
/************************************************************************************************************************
*    METHOD -- Creates PowerUp, Magic, Curses when an Enemy Destroyed
************************************************************************************************************************/
	protected void createItem(Object obj)							// Create Origami when an enemy is destroyed by fire
	{	
		Object newObj = null;
		if(obj.iType == Values.ENEMY_LEAF)							// If enemy destroyed is leaf, dont do anything
			return;												
		 
		int j = Adventure.MAX_OBJECTS - Adventure.POWER_UPS;		// Get Origamis Starting position in Object array
		for(;j < Adventure.MAX_OBJECTS; j++)						// Find an unused Origami object and set its properties
			if(Adventure.object[j].bEnable == false)	break;
		if(j == Adventure.MAX_OBJECTS)	return;						// If no empty Origami has been found, exit
			
		newObj = Adventure.object[j];
		iType = newObjType(obj);									// what to create, powerUP, magic or curse Based on level Stats							

		switch(iType)
			{
			case Values.POWERUP_DBARELL: 		newObj.create(iType, Values.PATH_WIND, 0, 0);		break;
			case Values.POWERUP_MACHINEGUN: 	newObj.create(iType, Values.PATH_WIND, 0, 0);		break;
			case Values.POWERUP_LIGHTNING: 		newObj.create(iType, Values.PATH_WIND, 0, 0);		break;
			case Values.POWERUP_INVINCIBLE: 	newObj.create(iType, Values.PATH_WIND, 0, 0);		break;
			case Values.POWERUP_TIME_SLOW: 		newObj.create(iType, Values.PATH_STATIONARY, 0, 0);	break;
			case Values.MAGIC_PAPER:    		newObj.create(iType, Values.SHOW_MAGIC, 0, 0);		break;
			case Values.MAGIC_SCISSOR:  		newObj.create(iType, Values.SHOW_MAGIC, 0, 0);		break;
			case Values.MAGIC_STONE:    		newObj.create(iType, Values.SHOW_MAGIC, 0, 0);		break;
			case Values.SCROLL_EVIL:			newObj.create(iType, Values.PATH_STATIONARY, 0, 0);	break;
			case Values.SCROLL_NORMAL:			newObj.create(iType, Values.PATH_STATIONARY, 0, 0);	
				Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_TOTAL_SCROLLS]++;
			break;
			case Values.CURSE_PAPER: 
				bCurseCreated = true;
				bAntiCurseCreated = false;
				newObj.create(iType, Values.PATH_ATTRACT, 0, 0);	
			break;
			case Values.CURSE_SCISSOR:  
				bCurseCreated = true;
				bAntiCurseCreated = false;
				newObj.create(iType, Values.PATH_ATTRACT, 0, 0);	
			break;
			case Values.CURSE_STONE:    
				bCurseCreated = true;
				bAntiCurseCreated = false;
				newObj.create(iType, Values.PATH_ATTRACT, 0, 0);
			break;
			case -1:
			default: 		return;									// Dont create anything 
			}
				
		newObj.posX = obj.posX;										// Set the position same as destroyed enemy
		newObj.posY = obj.posY;
		newObj.distance = obj.distance;
		newObj.bEnable = true;
		
		if(newObj.getType() == Values.TYPE_POWERUP)
			Adventure.events.dispatch(Events.COLLECT_PUPS);
	}
/************************************************************************************************************************
*    METHOD
************************************************************************************************************************/
	int newObjType(Object obj)										// Choose what type of Object to create
	{	
		float objPosY = obj.posY;									// Check if object is not too close to player
		float tempRan = Base.ran.nextFloat();						// Random number to decide what type of Object to create
		int iReturn = Values.SCROLL_NORMAL;							// Default return egg
		float pUpMult = Values.ARR_POWER[obj.iType]/6;				// Effects % of powerups based on enemyPower
		pUpMult = Values.clamp(pUpMult, 1, 2);
		
		if(bIsCursed)												// If Player is cursed, Create Magic Eggs
			{
			iReturn = createMagic( obj);
			Adventure.events.dispatch(Events.COLL_MAG_SCRLS);
			}
		else
			{
			if(tempRan <= percPUps*pUpMult)							// Create PowerUp
				iReturn = choosePowerUp();
			else if(tempRan <= percScrl)							// Normal Egg
				iReturn = Values.SCROLL_NORMAL;
			else if(tempRan <= percEmpty)
				iReturn = -1;
			else if(tempRan <= percCurse)							// Random curse, but check if not last Enemy or too close to player
				{
				if((objPosY > (Player.posY+1.0f)) && (iCurseTimer < -80) && !bCurseCreated)
					iReturn = iCurCurse = ((int)(Base.ran.nextFloat()*3) + Values.CURSE_PAPER);
				else 
					iReturn = Values.SCROLL_NORMAL;
				}
			else													// Death egg, but check not too close to player
				{
				if(objPosY > (Player.posY+2.0f)) 
					iReturn = Values.SCROLL_EVIL;
				else
					iReturn = Values.SCROLL_NORMAL;
				}
			}
		Values.log("powerups", "number: "+String.format("%.2f", tempRan)+ "  PowerUP:"+String.format("%.2f", percPUps) + "  Scroll:"+String.format("%.2f", percScrl)+ "  Empty:"+String.format("%.2f", percEmpty)+ "  Curse:" + String.format("%.2f", percCurse));
		return iReturn;
	}
/************************************************************************************************************************
*    METHOD
************************************************************************************************************************/
	void checkStatus()												// Method to check if time has run out 
	{
		if(iCurseTimer > -100)	iCurseTimer--;						// Wait for 100 frames for next curse to create
		if(bIsCursed && iCurseTimer == 0)							// If Player is cursed and time is over
			{	
			setResults(false);										// Set results as a loss
			Adventure.resetSpeed();
			}
	}
/************************************************************************************************************************
*    *MISC* METHODS
************************************************************************************************************************/
	protected boolean isCurseBroken(int vMagicType)					// Check if magic acquired breaks the curse
	{
		boolean bWin = false;										// Default false, if wins then change to true 
		switch(vMagicType)
			{
			case Values.MAGIC_PAPER:		if(iCurCurse == Values.CURSE_STONE) 	bWin = true; 	break;
			case Values.MAGIC_SCISSOR:		if(iCurCurse == Values.CURSE_PAPER) 	bWin = true; 	break;
			case Values.MAGIC_STONE:		if(iCurCurse == Values.CURSE_SCISSOR) 	bWin = true; 	break;
			}
		setResults(bWin);
	return bWin;
	}
/************************************************************************************************************************
*    *MISC* METHODS
************************************************************************************************************************/
	private int createMagic( Object obj)
	{	
		int vReturn;												// Return type of Magic
		Object obj2;
		float temTime = Adventure.pssGame.iCurseTimer/(float)Adventure.pssGame.iTotalTime;

		if((temTime < 0.33f) && !bAntiCurseCreated)
			vReturn = getAntiCurse(iCurCurse);						// Time running out, our last enemy..but no anti curse has been created yet
		else														// Else, return random magic
			vReturn = ((int)(Base.ran.nextFloat()*3) + Values.MAGIC_PAPER);

		if(vReturn == getAntiCurse(iCurCurse))						// If Magic created is anti curse, set bAntiCurseCreated flag
			bAntiCurseCreated= true;
		
		for(int i = Adventure.MAX_OBJECTS - Adventure.POWER_UPS; i < Adventure.MAX_OBJECTS; i++) // Check for overlap
			{
			obj2 = Adventure.object[i];
			if(! (obj.posY+0.2f > (obj2.posY+0.8f) || (obj.posY+0.8f) < obj2.posY+0.2f  || // Check if two Magic scrolls overlap, move them	
				 (obj.posX+0.1  > (obj2.posX+0.9f) || (obj.posX+0.9f) < obj2.posX+0.1f)))
				{
			
				float diffX = 0.8f - Math.abs(obj.posX - obj2.posX);				// Overlap in X
				float diffY = 0.6f - Math.abs(obj.posY - obj2.posY);				// Overlap in Y
				diffX/=2;
				diffY/=2;
				
				if(Math.abs(diffX)/2 >= Math.abs(diffY))
					{
					if(obj.posX > obj.posX)
						{obj.posX += diffX; obj2.posX -= diffX;}
					else
						{obj.posX -= diffX; obj2.posX += diffX;}
					}
				else
					{
					if(obj.posY > obj2.posY)
						{obj.posY += diffY; obj2.posY -= diffY;}
					else
						{obj.posY -= diffY; obj2.posY += diffY;}
					}
				break;												// Exit the loop once match found	
				}
			}
		if(obj.posX < 0.3f)											// if object is top of the screen;
			obj.posX = 0.3f;
			
	return vReturn;
	}
/************************************************************************************************************************
*    *MISC* METHODS
************************************************************************************************************************/
	private void setResults(boolean bWon)
	{
		if(bWon)													// If: player has won
			{
			if(iCurResult < 0) iCurResult=0;						// Reset any loses
			iCurResult++;											// Add to result
			iLevelWins++;
			if(iCurResult == 3)										// If player has won three consecative times
				{
				iCurResult=0;										// Reset Result counter
				setPowerUp(Values.POWERUP_EXTRALIFE);			// Give extra life
				}
			else
				setPowerUp(choosePowerUp());
			setPowerUp(Values.POWERUP_CURSE_BREAK);			// Set player invincible for few moments, when he breaks curse
			SoundPlayer.playSound(Sound.MAGIC);
			}
		else														// Else: player has lost
			{
			if(iCurResult > 0)	iCurResult = 0;						// If there has been any wins, clear them
			iCurResult--;
			iLevelLoses++;											// Add one to total loses
			if(iCurResult == -3)									// If player has lost three consecative times
				{
				Adventure.player.reduceLife(1);							// Take a life
				iCurResult=0;										// Reset Result counter
				Game.events.dispatch(Events.LOST_3_TIMES);
				}
			Game.scoreBoard.subtract(20, true);
			Game.events.dispatch(Events.CYCLE_OF_POWER);
			SoundPlayer.playSound(Sound.CURSE);
			}
	
		removeCurse();												// Reset all variables
	}
		
	int getAntiCurse(int vCurseType)								// Returns Magic for the Curse
	{
		switch(vCurseType)
			{
			case Values.CURSE_PAPER: 	return Values.MAGIC_SCISSOR;
			case Values.CURSE_SCISSOR: 	return Values.MAGIC_STONE;
			case Values.CURSE_STONE: 	return Values.MAGIC_PAPER;
			}
	return -1;														// Should not run 
	}
	
/************************************************************************************************************************
*    METHOD to set powerUps collected by player
************************************************************************************************************************/
	protected static void setPowerUp(int vPowerUp)
	{
		switch(vPowerUp)
			{
			case Values.POWERUP_EXTRALIFE:
				if(Player.iLives < Values.PLAYER_LIVES) 
					{	
					Player.iLives ++;
					Game.events.dispatch(Events.EXTRA_LIFE);									// Hud- You got extra life
					}
			break;
			case Values.POWERUP_MACHINEGUN:
				if(Weapon.getShots(Weapon.MACHINE_GUN) < Weapon.MACGUN_MAX_SHOTS)
					{
					Weapon.addShots(Weapon.MACHINE_GUN, Weapon.MACGUN_POWERUP);
					Game.events.dispatch( Events.SWITCH_WEAPON);								// Weapon switch, HUD	
					SoundPlayer.playSound(Sound.POWERUP);
					}
				Weapon.setMachineGun();													// Set Machine Gun
			break;
			case Values.POWERUP_DBARELL:
				if(Weapon.getShots(Weapon.DBARELL_GUN) < Weapon.DBARREL_MAX_SHOTS)
					{
					Weapon.addShots(Weapon.DBARELL_GUN, Weapon.DBARREL_POWERUP);
					Game.events.dispatch( Events.SWITCH_WEAPON);
					SoundPlayer.playSound(Sound.POWERUP);
					}
				Weapon.setBarellGun();													// Set Double Barrel
			break;
			case Values.POWERUP_LIGHTNING:
				if(Weapon.getShots(Weapon.BOLT_GUN) < Weapon.BOLT_MAX_SHOTS)
					{
					Weapon.addShots(Weapon.BOLT_GUN, Weapon.BOLT_POWERUP);
					Game.events.dispatch( Events.COLLECT_PUPS);	
					Game.events.dispatch( Events.SWITCH_WEAPON);
					SoundPlayer.playSound(Sound.POWERUP);
					}
				Weapon.setBoltGun();													// Set Bolt
			break;
			case Values.POWERUP_INVINCIBLE:
				Game.player.setInvincible(true, Weapon.INVINCIBLE_PUP);
				SoundPlayer.playSound(Sound.POWERUP);
			break;
			case Values.POWERUP_TIME_SLOW:
				Game.resetSpeed();
				Game.setSpeed((Game.SPEED_MULT - 0.2f) < 1 ? 1: (Game.SPEED_MULT - 0.2f) );
				SoundPlayer.playSound(Sound.POWERUP);
			break;
			case Values.POWERUP_CURSE_BREAK:											// Make Player invincible when he wins a match
				Game.player.setInvincible(true, Weapon.INVINC_CURSE_BREAK);
			break;
			case Values.SCROLL_NORMAL:
				Values.LEVEL_STATS[Game.iLevel][Values.LEVEL_SAVE_SCROLLS]++;
				if(Player.iPower < Values.PLAYER_POWER) 
					{ 
					Player.iPower++; 
					SoundPlayer.playSound(Sound.ITEM_COLLECT, 0, 200);	
					}
				else
					SoundPlayer.playSound(Sound.ITEM_COLLECT, 0, 40);
				Game.scoreBoard.add(2, true);				
			break;
			case Values.SCROLL_EVIL:
				Game.player.reduceLife(1);
			break;
			}
	}
/************************************************************************************************************************
*    Choose, which power up to create, based on level
************************************************************************************************************************/
	public static int choosePowerUp()								// returns a powerUp wepaon, based on level
	{
		if(Game.iMode==Values.ARCADE_MODE || Game.iMode==Values.ARCADE_RESUME)
			{
			switch(Game.ran.nextInt(11))								// Level 5+ MachineGun, DBarell Gun, Lightning Gun, Invincible
				{
				case 10: return Values.POWERUP_TIME_SLOW;
				case 9: return Values.POWERUP_LIGHTNING;
				case 7: case 8: return Values.POWERUP_DBARELL; 
				case 4: case 5:	case 6: return Values.POWERUP_MACHINEGUN;
				case 0: case 1: case 2: case 3: return Values.POWERUP_INVINCIBLE;
				}
			}
		else switch(Game.iLevel)									// For levels Stage1(1,3,5), Stage2(6,7,8), Stage3(9,10,11)
			{
			case 1: case 2: case 3: 								// Level 1, 2 Mechine gun, Invincible
				switch(Game.ran.nextInt(3))							// Level 3, 4 Machine Gun, DBarell Gun, Invincible
					{
					case 2: return Values.POWERUP_MACHINEGUN;
					case 0: case 1: return Values.POWERUP_INVINCIBLE;
					} 	
			case 4: case 5: case 6: 	
				switch(Game.ran.nextInt(6))							// Level 3, 4 Machine Gun, DBarell Gun, Invincible
					{
 					case 5: return Values.POWERUP_DBARELL;
					case 3:	case 4: return Values.POWERUP_MACHINEGUN;
					case 0: case 1: case 2: return Values.POWERUP_INVINCIBLE;
					}
			break;
			default:
				switch(Game.ran.nextInt(10))								// Level 5+ MachineGun, DBarell Gun, Lightning Gun, Invincible
					{
					case 9: return Values.POWERUP_LIGHTNING;
					case 7: case 8: return Values.POWERUP_DBARELL; 
					case 4: case 5:	case 6: return Values.POWERUP_MACHINEGUN;
					case 0: case 1: case 2: case 3: return Values.POWERUP_INVINCIBLE;
					}
			}
		
		return  Values.POWERUP_INVINCIBLE;
	}
/************************************************************************************************************************
*   END METHODS
************************************************************************************************************************/

	
}
