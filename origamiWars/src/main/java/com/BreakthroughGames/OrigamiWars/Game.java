package com.BreakthroughGames.OrigamiWars;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*Game Engine*/
public class Game {
	//Constants to be used in the game
	static protected Intent intSound;
	protected static int iMode = Values.START_ERROR;									// Default, will change when resources are loaded

	// Variables for reference
	protected static Context 		refContext;
	protected static Thread 		refMusicThread;
	protected static ActGame  		refActGame = null;
	protected static ActMainMenu 	refActMainMenu = null;
	protected static GameView 		refGameView = null;

	// Set Game speed to 60fps maximum
	protected static float SPEED_MULT = 1;												// Speed multiplier, set using setSpeed() method
	protected static final int GAME_FPS = 60;
	protected static final int MAX_ACTIVE_OBST = 4;										// Maximum active obstacles
	protected static final int MAX_ONE_OBST_DIFF = 5;									// Maximum obstacle difficulty
	protected static final int GAME_THREAD_DELAY = 1000;
	protected static final long FRAME_TIME = (1000 / GAME_FPS);
	protected static final int MAX_OBJECTS = 18, POWER_UPS = 10;						// Maximum possible objects in a level
	protected static int CUR_Obst_Diff = 0, MAX_Actv_Enemy = 0, LEVEL_ENEMIES = 0;

    private static int iDebugTap =0;
	protected static long scoreLevel;													// Game Score 
	protected static int iSequence = 0;													// Used for displaying level Finish events in a sequence
	protected static int iActvEnemy = 0;												// Sequence of event when level complete or Game Over
	protected static int tapTimer = 0;													// ID of object when finger is touched down, should be same obj when finger lifts up 
	protected static int lastShotTime = 0;												// Keeps track of last fired bullet, so next bullet is not fired too soon
	protected static int iActObstDiff = 0;												// Active Obstacles difficulty
	protected static Events events, story;
	protected static int speedCounter = 0;												// Counter for game speed calculations
	protected static ScoreBoard scoreBoard;
	protected static int enemyDestroyed = 0;
	protected static boolean bSecond = false;											// ticks(true) every 60 frames
	protected static Random ran = new Random(); 										// Variable to generate random numbers
	protected static Object fingerTap = new Object();										// Object contains tap on the screen coordinates
	protected static Object oTarget = new Object();										// Ptr to object that's been targeted
	protected static Base planeExt = new Base();										// Extended object not to mark objects near plane
	protected static int iLevel = -1, iStartLevel = 0;
	protected static float odoMeter = 0.0f, lastX = 0;									// How far plane has moved

	protected static Object object[] = new Object[MAX_OBJECTS];
	protected static int arIndex[] = new int[Values.ENEMY_END];
	protected static Weapon fire[] = new Weapon[Weapon.MAX_SHOTS];						// last index for shock-wave 
	protected static Obstacle cloudsMiddle[][] = new Obstacle[7][];						// MIddle clouds in difficulty order
	protected static float arDistance[][] = new float[Values.ENEMY_END][];
	protected static float arPosition[][] = new float[Values.ENEMY_END][];
	protected static float arProperty[][] = new float[Values.ENEMY_END][];
	protected static Obstacle actObstacles[] = new Obstacle[]{null, null, null, null};	// Active Obstacles, last reserved for enemies

	protected static Player player;
	protected static PowerUps pssGame = new PowerUps();									// Object to create Curse and Magic, PowerUP and Normal Eggs
	protected static Background BG_Front, BG_Middle, BG_Back, BG_Sky, BG_lvlCompl, BG_Moon;
	protected static Texture txtPlane, txtEnemy1, txtEnemy2, txtPlaneFire, txtDigits, txtObst, txtEnemyFire;
	protected static Texture txtEvent1, txtEvent2, txtStory, txtPowUp, txtHudMenu, txtHudWeapon, txtHudLives, txtPSS;

	public Game(){}
	public Game(GL10 glRef) {
		events		= new Events();
		scoreBoard 	= new ScoreBoard();
		story 		= new Events(false);												// Do not create event related variables for story
		player 		= new Player(glRef);
		txtPlane 	= new Texture(glRef);
		txtEnemy1 	= new Texture(glRef);
		txtEnemy2 	= new Texture(glRef);
		txtStory 	= new Texture(glRef);
		txtPowUp 	= new Texture(glRef);
		txtHudWeapon= new Texture(glRef);
		txtHudMenu	= new Texture(glRef);
		txtHudLives = new Texture(glRef);
		txtPSS  	= new Texture(glRef);
		txtEvent1 	= new Texture(glRef);
		txtEvent2 	= new Texture(glRef);
		txtDigits   = new Texture(glRef);
		txtObst 	= new Texture(glRef);
		txtPlaneFire= new Texture(glRef);
		txtEnemyFire= new Texture(glRef);
		BG_Sky      = new Background();
		BG_Middle 	= new Background();
		BG_Moon 	= new Background();
		BG_lvlCompl = new Background();
		BG_Front 	= new Background(0,1,0,0.5f);
		BG_Back 	= new Background(0,1,0,0.5f);
	}
	protected void loadLevel() 		{}
	protected void initObjects() 	{}													// Methods Overriden by children
	protected void loadingScreen()	{}
	protected void runOneFrame() 	{}
	protected void resumeLevel() 	{}
	protected void levelRestart() 	{}
	protected void levelComplete() 	{}
	protected byte checkGameStatus() { return Values.GAME_LOAD_SCREEN; }

	/************************************************************************************************************************
	 *    METHOD--  Moves Enemies, Player, Shots and Fire new shots
	 ************************************************************************************************************************/
	protected static void loadTextures() {
		txtPSS.loadTexture(Texture.SHEET_PSS);
		txtPowUp.loadTexture(Texture.SHEET_PUPS);
		txtPlane.loadTexture(Texture.SHEET_PLANE);
		txtObst.loadTexture(Texture.SHEET_CLOUDS);
		txtDigits.loadTexture(Texture.SHEET_DIGITS);
		txtEvent1.loadTexture(Texture.SHEET_EVENTS_1);
		txtEvent2.loadTexture(Texture.SHEET_EVENTS_2);
		txtEnemy1.loadTexture(Texture.SHEET_ENEMIES_1);
		txtEnemy2.loadTexture(Texture.SHEET_ENEMIES_2);
		txtHudMenu.loadTexture(Texture.SHEET_HUD_MENU);
		txtHudLives.loadTexture(Texture.SHEET_HUD_LIVES);
		txtEnemyFire.loadTexture(Texture.SHEET_ENEMY_FIRE);
		txtPlaneFire.loadTexture(Texture.SHEET_PLANE_FIRE);
		BG_lvlCompl.loadTexture(Texture.BG_LEVEL_COMPLETE);
		txtHudWeapon.loadTexture(Texture.SHEET_HUD_WEAPONS);

		player.iTexture = txtPlane.iTexture;												// For objects types other then Texture, you have to set TextureID
		events.iTexture = txtEvent1.iTexture;
		scoreBoard.iTexture = txtDigits.iTexture;

		//	Values.addObject( 	 Type, 					Texture, 				Sprite,  Power);
		Values.addObject( Values.ENEMY_LEAF, 			txtEnemy2.iTexture, 	0, 		 1);
		Values.addObject( Values.ENEMY_BUTTERFLY, 		txtEnemy1.iTexture, 	0, 		 2);
		Values.addObject( Values.ENEMY_WASP, 			txtEnemy1.iTexture, 	1, 		 3);
		Values.addObject( Values.ENEMY_DRAGONFLY, 		txtEnemy1.iTexture, 	2,  	 5);
		Values.addObject( Values.ENEMY_HORNET, 			txtEnemy1.iTexture, 	3,  	 6);
		Values.addObject( Values.ENEMY_BAT, 			txtEnemy1.iTexture, 	4,  	 7);
		Values.addObject( Values.ENEMY_VULTURE, 		txtEnemy1.iTexture, 	5,  	 8);
		Values.addObject( Values.ENEMY_PTEROSAUR, 		txtEnemy1.iTexture, 	6,  	 10);
		Values.addObject( Values.ENEMY_DRAGON, 			txtEnemy1.iTexture, 	7,  	 12);
		Values.addObject( Values.POWERUP_EXTRALIFE, 	txtPowUp.iTexture, 		0,  	-1);
		Values.addObject( Values.POWERUP_DBARELL, 		txtPowUp.iTexture, 		1,  	-1);
		Values.addObject( Values.POWERUP_MACHINEGUN, 	txtPowUp.iTexture, 		2,  	-1);
		Values.addObject( Values.POWERUP_LIGHTNING, 	txtPowUp.iTexture, 		4,  	-1);
		Values.addObject( Values.POWERUP_INVINCIBLE, 	txtPowUp.iTexture, 		3,  	-1);
		Values.addObject( Values.POWERUP_TIME_SLOW, 	txtPowUp.iTexture, 		0,  	-1);
		Values.addObject( Values.SCROLL_NORMAL, 		txtPSS.iTexture, 		0,  	-1);
		Values.addObject( Values.SCROLL_EVIL, 			txtPSS.iTexture, 		4,  	-1);
		Values.addObject( Values.CURSE_PAPER, 			txtPSS.iTexture, 		5,  	-1);
		Values.addObject( Values.CURSE_SCISSOR, 		txtPSS.iTexture, 		6,   	-1);
		Values.addObject( Values.CURSE_STONE, 			txtPSS.iTexture, 		7,   	-1);
		Values.addObject( Values.MAGIC_PAPER, 			txtPSS.iTexture, 		1,   	-1);
		Values.addObject( Values.MAGIC_SCISSOR, 		txtPSS.iTexture, 		2,   	-1);
		Values.addObject( Values.MAGIC_STONE, 			txtPSS.iTexture, 		3,   	-1);
		Values.addObject( Values.FIRE_ENEMY, 			txtEnemyFire.iTexture, 	0,   	-1);
	}
	/************************************************************************************************************************
	 *    METHOD--  Moves Enemies, Player, Shots and Fire new shots
	 ************************************************************************************************************************/
	public static void moveObjects() {
		float oldX, oldY;
		Weapon shot = null;
		boolean bActObst = false;
		Object obj, newObj = null;

		tapTimer++;																	    // Used to check time for Marking Object

		if(Values.bDebug && fingerTap.bEnable) {												// Show Tap point in Debug Mode
            fingerTap.transform();
			fingerTap.draw();
            iDebugTap++;
        }

        if(iDebugTap > 1){
            Log.d("Screen", "Screen Tapped");
        }

		if(Player.bEnable)																// Move Player
			odoMeter += player.movePlayer();

		for( int i=0; i < MAX_ACTIVE_OBST; i++)											// Move All Obstacles
			if(actObstacles[i] != null) {
				if(actObstacles[i].move())	{ iActObstDiff -= actObstacles[i].iDifficulty; actObstacles[i] = null; }
				else bActObst = true;
			}

		for(int i=0; i < MAX_OBJECTS; i++){												// Move All Objects
			obj = object[i];
			if(obj.bFires) obj.moveShot();
			if(obj.bEnable && obj.distance < odoMeter ) {								// If enemy is not destroyed and its distance has reached
				if(obj.bActive || iActvEnemy < MAX_Actv_Enemy )	{						// If object on screen is an enemy count it
					if(obj.iType <= Values.ENEMY_END && !obj.bActive){					// if object is a Enemy and not active already
						if(newObj == null) newObj = obj;
						if(obj.distance < newObj.distance) newObj = obj;				// add the closet object first
					} else {
						oldX = obj.posX;
						oldY = obj.posY;
						obj.move();
						if(obj.posY < Screen.DEV_MAX_Y){									// Object is within the screen
							if((obj.getType()&Values.TYPE_WIND_STABLE) == 0 && bActObst)    // For magic, curse and scrolls dont find path
								PathFinder.calculate(actObstacles, obj, oldX, oldY);	    // Find path between obstacles
							if(Player.bEnable) obj.fireShot();							    // Check for objects fire
							if(fingerTap.bEnable) objMark(obj);
							obj.transform();
							obj.draw();
						}
					}
				}
			}
			else if(obj.bFalling)														    // Enemy is destroyed and is falling
				obj.Destroy(txtPlaneFire.iTexture, 4);

			if(obj.bMarked)																// if multiple objects are marked, only which is target object should be marked
				obj.bMarked = (obj.ID == oTarget.ID);

			if(fingerTap.bEnable && ++fingerTap.posT >= MAX_OBJECTS) {                             // Disable screen tap, once all objects has been checked
				fingerTap.bEnable = false;
                iDebugTap = 0;
            }
		}


		if(newObj != null) {
			iActvEnemy++;
			newObj.bActive = true;
		}

		if(Weapon.check() == Weapon.SWAVE_FIRE)											//Weapon Status and ShockWave
			fire[Weapon.SW_INDEX].fireShockWave();

		for(int i=0; i < Weapon.MAX_SHOTS; i++)
			if(!(shot=fire[i]).bEnable) {
				if(Weapon.eState == Weapon.WEAPON_FIRE && !Player.bFalling && Player.bEnable) {
					if((events.iTimer - lastShotTime) > Weapon.shotGap && Weapon.shotsFired < Weapon.shotsBurst && i!= Weapon.SW_INDEX) {
						if( shot.fireNewShot() == -2 )									// Fire player is not falling
							events.dispatch(Events.WEAPON_EMPTY);						// 12 empty weapon, HUD 			
						lastShotTime = events.iTimer;
					} else if(shot.bSmoke)
						shot.moveShot();
				} else if(shot.bSmoke)
					shot.moveShot();
			} else
				shot.moveShot();

	}
	/************************************************************************************************************************
	 *   METHOD -- Checks for an object if it has been Marked by on screen Tap
	 ************************************************************************************************************************/
	public static void objMark(Object vObj) {
		float distNew, distOld;

		if(tapTimer < 15 && vObj.detectCollision(fingerTap)){									// if Object has been tapped
			distNew = Math.abs(fingerTap.posY - vObj.posY) + Math.abs(fingerTap.posX - vObj.posX);
			distOld = Math.abs(fingerTap.posY - oTarget.posY) + Math.abs(fingerTap.posX - oTarget.posX);

			if((distNew < distOld) || !oTarget.bMarked){									// If multiple enemies, select the one nearest
				oTarget = vObj;														        // Set object as target for shock wave
				oTarget.bMarked = true;
				fingerTap.bEnable = false;
				events.dispatch(Events.ENEMY_POKE);
			}
		}
	}

	/************************************************************************************************************************
	 *   METHOD -- Detects collision of fire with Enemies and Obstacles
	 ************************************************************************************************************************/
	public static void detectCollision() {
		int shotDamage;
		Object obj = null;																// Used for de-referencing
		Weapon shot = null;
		EnemyFire enemyfire = null;
		Obstacle.Cloud cloud = null;
		float arrResult[] = {0,0,0,0,0,};
		float  middleX = -1, upX = -1,  downX = -1;
		int totalEnemies = MAX_OBJECTS - POWER_UPS;
		float middleY = Screen.DEV_MAX_Y+1, upY = Screen.DEV_MAX_Y+1, downY = Screen.DEV_MAX_Y+1, nearObjY = -1, nearObjX = -1;

		for(int i=0; i < totalEnemies; i++)	{											//  Checks for EnemyFire collision with clouds
			obj = object[i];
			enemyfire = obj.enemyFire;

			if(obj.bFires && enemyfire.iStatus == EnemyFire.STATUS_Moving)
				for(Obstacle obst : actObstacles)
					if(obst != null && enemyfire.detectCollision(obst))
						for(int k = 0; k < obst.length; k++){
							cloud = obst.arClouds[k];
							if(cloud.bEnable && cloud.detectOverlap(enemyfire, arrResult))
								if((arrResult[Obstacle.OLAP_X]*arrResult[Obstacle.OLAP_X]) > 0.1f)
									enemyfire.iStatus = EnemyFire.STATUS_Smoke;
						}
		}

		for(int i=0; i < Weapon.MAX_SHOTS; i++){											// Checks PlayerFire collision with clouds
			shot = fire[i];
			nextShot:																	// Once a bullet hit some thing just get out of inner loop

			if(shot.bEnable && Weapon.shotsFired > 0){
				for(Obstacle obst : actObstacles)										// Check All Obstacles for collision
					if(obst != null && shot.detectCollision(obst))
						for(int k=0; k < obst.length; k++){
							cloud = obst.arClouds[k];
							if(cloud.bEnable && cloud.detectOverlap(shot, arrResult))
								if((arrResult[Obstacle.OLAP_X]) > 0.25f){
									shot.Opacity = 0.5f;								// Make shot transparent
									switch(cloud.iType){
										case Obstacle.WHITE_CLOUDS: 	shot.iDamage -= 0.25f; 	break;
										case Obstacle.DARK_CLOUDS:    	shot.iDamage -= 0.5f; 	break;
										case Obstacle.THUNDER_CLOUDS: 	shot.iDamage -= 2; 		break;
									}
									if(shot.iDamage <= 0.5f) {
										shot.iDamage = 0;
										shot.bEnable = false;
										shot.bSmoke = true;
										Weapon.shotsFired--;
									}
									break nextShot;
								}
						}

				if(shot.getRight() < (Screen.DEV_MAX_Y - 0.5f))							// check PlayerFire collision with Enemies
					for(int j=0; j < totalEnemies; j++) {
						obj = object[j];
						if(obj.bEnable && !obj.bFriendly && obj.posY < Screen.DEV_MAX_Y - 0.5f){		// if object is enabled and not friendly
							if(obj.detectCollision(shot)) {
								shotDamage = obj.hitDamage(shot);
								if(shotDamage > 0){										// If collision has been enough to reduce power
									shot.iDamage -= shotDamage;
									scoreBoard.add(shotDamage, false);					// Add score for every shot that hits enemy
									if(shot.iDamage < 1) {
										shot.iDamage = 0;
										shot.bEnable = false;
										shot.bSmoke = true;
										Weapon.shotsFired--;
									}
									SoundPlayer.playSound(Sound.ENEMY_HIT);
									if(obj.Damage(shotDamage)){ 							// If damage has destroyed the enemy, add to destroyed counter
										obj.speed = -1;									// Create some pUp or curse
										iActvEnemy--;									// Reduce one from active Enemies counter
										SoundPlayer.playSound(Sound.ENEMY_BURN);
									}
									break nextShot;
								}
							}
						}	// End if(Object Active)
					}
			}
		}//For MAX_SHOTS


		shot = fire[Weapon.SW_INDEX];												 	// ShockWave Homing for near objects
		if(!oTarget.bMarked && shot.bEnable && shot.posY < Screen.DEV_MAX_Y - 1) {
			for(int i=0; i < totalEnemies; i++){											//  EnemyFire collision with clouds
				obj = object[i];
				if(obj.detectCollision(shot, 0, 1.5f))
					if(obj.posY  < middleY)	{ middleY = obj.posY;	middleX = obj.posX + 0.5f; }
				if(obj.detectCollision(shot, -0.5f, 1.5f))
					if(obj.posY  < upY)		{ upY = obj.posY;		upX = obj.posX ; }
				if(obj.detectCollision(shot, 0.5f, 1.5f))
					if(obj.posY  < downY)	{ downY = obj.posY;	downX = obj.posX + 1f; }
			}

			if((middleY < upY) && (middleY < downY)){
				nearObjY = middleY;	nearObjX = middleX;	}	// Find the nearest Object
			else if (upY < downY)		{ nearObjY = upY; 		nearObjX = upX;}
			else 						{ nearObjY = downY;		nearObjX = downX;}

			if(nearObjX != -1){															// if there has been an object near, then calculate
				float diffX = nearObjX - (shot.posX + 0.5f);
				float diffY = nearObjY - shot.posY;
				shot.posX += Values.clamp(diffX/(diffY*(10)+0.1f), -diffY/4, diffY/4);
			}
		}

		if(Player.bEnable)
			playerCollision();
	}
	/************************************************************************************************************************
	 *   METHOD -- Detects Player Collision with Enemies, PowerUps and Enemy Fire
	 ************************************************************************************************************************/
	public static void playerCollision(){
		Object obj;
		boolean bCloudCollision = false;
		float result[] = {0,0,0,0,0,0,0,0,0};											// Result of player collision with a block(cloud)
		Weapon shot = fire[Weapon.SW_INDEX];

		for(int i=0; i < MAX_OBJECTS; i++){												// Move All Objects
			obj = object[i];
			if(!player.bInvincible && obj.bFires && obj.enemyFire.iStatus == EnemyFire.STATUS_Moving)
				if(player.detectCollision(obj.enemyFire)) {
					Player.iPower -= obj.enemyFire.iDamage;
					events.dispatch(Events.PLANE_HIT_SHOT);
					if(Player.iPower <= 0)
						player.reduceLife(1);
					obj.enemyFire.iStatus = EnemyFire.STATUS_Smoke;
				}

			if(obj.bEnable && obj.bActive ) {
				if(!Player.bFalling && (!player.bInvincible || obj.bFriendly))			//Check if all pre-collision conditions are met
					if(player.detectCollision(obj)) {
						collisionType(obj, 1);
						break;
					}

				if(obj.bFriendly && shot.bEnable) {
					if(obj.detectCollision(shot)) {
						collisionType(obj, 2);
						shot.iDamage = 0;
						shot.bEnable = false;
						shot.bSmoke = true;
						Weapon.shotsFired--;
					}
				}
			}
		}

		for(Obstacle obst : actObstacles)												// Check All Obstacles for collision
			if(obst != null && player.detectCollision(obst)) {
				for(int j = 0; j < obst.length; j++)
					if(obst.arClouds[j].detectOverlap(player, result)) {
						bCloudCollision = true;
						result[Obstacle.OLAP_ABS_X] += result[Obstacle.OLAP_X];
						result[Obstacle.OLAP_ABS_Y] += result[Obstacle.OLAP_Y];
						if(result[Obstacle.POSITION]  == Obstacle.CLOUD_DOWN)  result[Obstacle.OLAP_X]*=-1;
						if(result[Obstacle.POSITION_Y]== Obstacle.CLOUD_RIGHT) result[Obstacle.OLAP_Y]*=-1;
						result[Obstacle.OLAP_DIFF_X] += result[Obstacle.OLAP_X]*Math.abs(result[Obstacle.OLAP_Y]);
						result[Obstacle.OLAP_DIFF_Y] += result[Obstacle.OLAP_Y]*Math.abs(result[Obstacle.OLAP_X]);
					}
				if(bCloudCollision) break;
			}

		if(bCloudCollision) {
			if(result[Obstacle.CLOUD_TYPE]==Obstacle.THUNDER_CLOUDS && !player.bInvincible)
				player.reduceLife(1);
			player.setCollStats(result);
		}
	}
	/************************************************************************************************************************
	 *    METHOD -- When Plane or SW collides with an object check what action to take
	 ************************************************************************************************************************/
	protected static void collisionType(Object obj, int SWorPlane){						// Object thats collided with ShockWave or Plane
		if(obj.bMarked)
			obj.bMarked = false;

		switch(obj.getType()){															// Get type of the object collided
			case Values.TYPE_ENEMY:
				int pPower = Player.iPower;
				player.Damage(obj.iPower);
				events.dispatch(Events.PLANE_HIT_ENEMY);
				if(obj.Damage(pPower))													// If enemy is destroyed after collision
					iActvEnemy--;														// Reduce one from active Enemies counter
				break;
			case Values.TYPE_CURSE:
				obj.posT = 0;
				obj.bFalling = true;													// Use to draw the curse sprites in destroy method
				pssGame.setCurse(obj);
				events.dispatch(Events.YOU_ARE_CURSED);
				SoundPlayer.playSound(Sound.CURSE);
				obj.bEnable = false;													// Disable the object
				break;
			case Values.TYPE_MAGIC:
				obj.posT = 0;															// Reset counter to draw magic animation
				if(pssGame.bIsCursed)													// Only that magic that removes the curse creates stars around the player
					obj.bFalling = pssGame.isCurseBroken(obj.iType);
				obj.bEnable = false;													// Disable the object
				break;
			case Values.TYPE_POWERUP:													// PoweUps
			case Values.TYPE_SCROLL:													// Paper scroll, death scroll
				if(obj.iType != Values.SCROLL_EVIL || SWorPlane != 2)					// for shock wave do not collect evil scroll
					PowerUps.setPowerUp(obj.iType);
				obj.bFalling = false;													// PowerUps don't Fall on Collision
				obj.bEnable = false;													// Disable the object
				break;
		}
	}
	/************************************************************************************************************************
	 *    METHOD -- Sets Obstacles for level, based on obstacle difficulty
	 ************************************************************************************************************************/
	protected static void calcObstacles(int vPath, int vType) {
		Obstacle obst;
		int iCurDiff;
		iActObstDiff = 0;
		float newPos  = Screen.DEV_MAX_Y;												// Where to place the Obstacle in Y
		float lvlDist = Values.LEVEL_STATS[iLevel][Values.LEVEL_DISTANCE];
		int iClouds, emptyIndex = -1;

		for(int i = 0; i < MAX_ACTIVE_OBST; i++)
			if((obst = actObstacles[i]) != null) {
				iActObstDiff += actObstacles[i].iDifficulty; 							// Add Total difficulty
				if(newPos < obst.getRightGap())											// Find the end of last Obstacle
					newPos = obst.getRightGap() + (ran.nextFloat()*(Screen.DEV_MAX_Y/3));
			}
			else
				emptyIndex = i;

		iActObstDiff = CUR_Obst_Diff - iActObstDiff;
		iCurDiff = (int) (2 + (odoMeter/lvlDist)*MAX_ONE_OBST_DIFF);
		iCurDiff = Values.clamp(iCurDiff, 0, MAX_ONE_OBST_DIFF);
		iActObstDiff = Values.clamp(iActObstDiff, 0, iCurDiff);

		if(newPos > Screen.DEV_MAX_Y*2) emptyIndex = -1;								// Dont create obstacles too far off the screen

		if(emptyIndex != -1 && iActObstDiff > 0) {
			iActObstDiff = ran.nextInt(iActObstDiff+1) ;								// Selected Random difficulty below the available diff level, and Clouds on top of screen
			iClouds = ran.nextInt(cloudsMiddle[iActObstDiff].length);					// Select random Obstacle within the diff level
			actObstacles[emptyIndex] = cloudsMiddle[iActObstDiff][iClouds].setObstacle(newPos, vPath, vType);
		}

	}
	/************************************************************************************************************************
	 *    METHOD
	 ************************************************************************************************************************/
	protected void loadObstacles() {
		cloudsMiddle[0] = new Obstacle[1];
		cloudsMiddle[0][0] = new Obstacle();

		cloudsMiddle[0][0].setProperties(0, 3.0f, Obstacle.POSITION_MIDDLE);			// OrigamiWars:Media\EnemyMaps\Clouds
		final float arrData00[][] = {{0f, 0.0f}};
		cloudsMiddle[0][0].addBlocks(arrData00);


		cloudsMiddle[1] = new Obstacle[3];
		cloudsMiddle[1][0] = new Obstacle();
		cloudsMiddle[1][1] = new Obstacle();
		cloudsMiddle[1][2] = new Obstacle();

		cloudsMiddle[1][0].setProperties(0, 3.0f, Obstacle.POSITION_MIDDLE);			// Single cloud
		final float arrData10[][] = {{0f, 0.0f}};
		cloudsMiddle[1][0].addBlocks(arrData10);

		cloudsMiddle[1][1].setProperties(1, 3.0f, Obstacle.POSITION_MIDDLE );			//  cloud_13
		final float arrData9[][] = {{0.4f, 0.1f}, {0f, 0.8f}, };
		cloudsMiddle[1][1].addBlocks(arrData9);

		cloudsMiddle[1][2].setProperties(1, 3.0f, Obstacle.POSITION_MIDDLE );			//  cloud_14  
		final float arrData8[][] = {{0f, 0f}, {0.4f, 0.7f}, };
		cloudsMiddle[1][2].addBlocks(arrData8);

		cloudsMiddle[2] = new Obstacle[1];
		cloudsMiddle[2][0] = new Obstacle();

		cloudsMiddle[2][0].setProperties(2, 3.0f, Obstacle.POSITION_MIDDLE);			//  Cloud_12
		final float arrData20[][] = {{0f, 0.1f}, {0.6f, 0.7f}, {0f, 1.4f}, };
		cloudsMiddle[2][0].addBlocks(arrData20);


		cloudsMiddle[3] = new Obstacle[2];
		cloudsMiddle[3][0] = new Obstacle();
		cloudsMiddle[3][1] = new Obstacle();

		cloudsMiddle[3][0].setProperties(2, 3.0f, Obstacle.POSITION_MIDDLE);			//  Cloud_11
		final float arrData11[][] = {{0.6f , 0.1f}, {0.1f , 0.7f }, {0.1f , 1.4f }, {0.3f , 2.1f },};
		cloudsMiddle[3][0].addBlocks(arrData11);

		cloudsMiddle[3][1].setProperties(3, 3.0f, Obstacle.POSITION_MIDDLE);			//  Cloud_10
		final float arrData13[][] = {{0.6f , 0f }, {0.0f , 0.7f }, {0.9f , 0.8f }, {0.3f , 1.5f },};
		cloudsMiddle[3][1].addBlocks(arrData13);


		cloudsMiddle[4] = new Obstacle[4];
		cloudsMiddle[4][0] = new Obstacle();
		cloudsMiddle[4][1] = new Obstacle();
		cloudsMiddle[4][2] = new Obstacle();
		cloudsMiddle[4][3] = new Obstacle();

		cloudsMiddle[4][0].setProperties(4, 3.0f, Obstacle.POSITION_MIDDLE);			//  Cloud_09
		final float arrData15[][] = {{0.4f, 0f}, {1.2f, 0.9f}, {0.3f, 0.9f}, {1.1f, 1.8f}, {0f, 1.9f}, };
		cloudsMiddle[4][0].addBlocks( arrData15 );

		cloudsMiddle[4][1].setProperties(4, 3.0f, Obstacle.POSITION_MIDDLE);			//  Cloud_08
		final float arrData14[][] = {{0.6f , 0.2f }, {0.1f , 1f }, {0.8f , 1f }, {0.3f , 1.8f }, {1.2f , 1.7f },  };
		cloudsMiddle[4][1].addBlocks( arrData14 );

		cloudsMiddle[4][2].setProperties(4, 3.0f, Obstacle.POSITION_MIDDLE);			//  Cloud_06
		final float arrData12[][] = {{0.6f , 0.2f }, {0.0f , 0.7f }, {0.8f , 0.9f }, {0.3f , 1.4f }, {0.4f , 2.1f }, };
		cloudsMiddle[4][2].addBlocks( arrData12 );

		cloudsMiddle[4][3].setProperties(4, 3.0f, Obstacle.POSITION_MIDDLE);
		final float arrTop43[][] = {{0.1f, 0.2f}, {0.8f, 0.8f}, {0.0f, 1.3f}, {0.5f, 1.9f}, {0.0f, 2.6f},  };
		cloudsMiddle[4][3].addBlocks( arrTop43 );


		cloudsMiddle[5] = new Obstacle[2];
		cloudsMiddle[5][0] = new Obstacle();
		cloudsMiddle[5][1] = new Obstacle();

		cloudsMiddle[5][0].setProperties(5, 1.0f, Obstacle.POSITION_MIDDLE);					//  Cloud_04
		final float arrTop20[][] = {{0.1f , 0.6f }, {0.6f , 1.3f }, {1f , 1.9f }, {0.1f , 1.9f }, {0.6f , 2.6f }, {0.1f , 3.2f },  };
		cloudsMiddle[5][0].addBlocks( arrTop20 );

		cloudsMiddle[5][1].setProperties(5, 2.0f, Obstacle.POSITION_MIDDLE);					//  Cloud_01
		final float arrTop21[][] = {{0.7f , 0.1f }, {0.1f , 0.7f }, {0f , 1.5f }, {0.3f , 2.1f }, {0.7f , 2.8f }, {0.9f , 3.6f }, {0.3f , 4.2f },  };
		cloudsMiddle[5][1].addBlocks( arrTop21 );


		cloudsMiddle[6] = new Obstacle[2];
		cloudsMiddle[6][0] = new Obstacle();
		cloudsMiddle[6][1] = new Obstacle();

		cloudsMiddle[6][0].setProperties(6, 2.0f, Obstacle.POSITION_MIDDLE);					//  Cloud_03
		final float arrTop22[][] = {{0f , 0.1f }, {0.6f , 0.6f }, {0.1f , 1.1f }, {0f , 2f }, {0.3f , 2.7f }, {0.7f , 3.3f }, {0.1f , 3.8f }, {0.9f , 4.1f }, {0.3f , 4.7f }, };
		cloudsMiddle[6][0].addBlocks( arrTop22 );

		cloudsMiddle[6][1].setProperties(6, 2.0f, Obstacle.POSITION_MIDDLE);					//  Cloud_07
		final float arrTop23[][] = {{0.4f , 0.2f }, {0.1f , 1.1f }, {0.8f , 1.7f }, {0f , 2f }, {1.3f , 2.7f }, {0.5f , 2.7f }, {0.7f , 3.6f }, {0.0f , 3.8f }, {0.3f , 4.6f },};
		cloudsMiddle[6][1].addBlocks( arrTop23 );
	}
	/************************************************************************************************************************
	 *   METHOD -- Creates enemy based on game mode
	 ************************************************************************************************************************/
	protected static void createEnemy(Object obj) {
		if(Game.iMode == Values.ARCADE_MODE || Game.iMode == Values.ARCADE_RESUME)
			Arcade.createEnemy(obj);
		else
			Adventure.createEnemy(obj);
	}
	/************************************************************************************************************************
	 *   METHOD -- Sets and Resets the game speed multiplier,
	 ************************************************************************************************************************/
	static void setSpeed(float vMultiplyer) {
		SPEED_MULT = vMultiplyer;
		Values.SCROLL_SPEED = Values.BASIC_SPEED * SPEED_MULT;
	}

	static void addSpeed(float vInc) {
		SPEED_MULT += vInc;
		Values.SCROLL_SPEED = Values.BASIC_SPEED * SPEED_MULT;
	}

	static void resetSpeed() {
		//SPEED_MULT = 1.0f;
		Values.SCROLL_SPEED = Values.BASIC_SPEED*SPEED_MULT;
		speedCounter = 0;																// Speed accelration counter
	}
	/************************************************************************************************************************
	 *   METHOD -- Detectes Player Collision with objects
	 ************************************************************************************************************************/
	public static boolean onExit() {
		try{
			Intent bgMusic = new Intent(refContext, Sound.class);
			refContext.stopService(bgMusic);
			return true;
		}
		catch(Exception e){	return false;	}
	}
}//End Class Engine
