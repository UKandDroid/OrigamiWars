package com.BreakthroughGames.OrigamiWars;
import android.util.Log;

public class ArcadeHUD
{
	protected static Draw sprite ;										// Object used to draw diff sprites, HUD and Level Complete
	protected static PowerUps oPSS;
	private static int lastWeapon = -1;
	protected static ScoreBoard scoreCur, scoreHig;
	private static boolean bFlash = true;								// Flag to flash PSSclock
	private static float iconShift = 0.5f;
	private static final float LIVES_POS = 4.8f;						// position of Lives on HUD
	private static final float WEAPON_POS = 5.1f;
	private static final float SELECT_WEAPON_SIZE = 1.3f;				// How big should selected weapon icon be 120%

	private static float selScaleX, selScaleY, selPosX, selPosY;		// Selected weapon Size and Position
	protected static int itxtlives, itxtMenu, itxtWeapon, itxtPSS;
	protected static int lvlSaveScroll, lvlTimeComple, lvlStatsSpeed = 8, iCounter = 0;
	private static float iconOrgX, iconOrgY, iconHudX, iconHudY;
	protected static float iconMenuX, iconMenuY;

	ArcadeHUD(){}

	/************************************************************************************************************************
	 *  METHOD -- Init method is called by Game::ResumeLevel to initlize textures once they are loaded
	 ***********************************************************************************************************************/
	protected static void init(int vtLives, int vtWeapon, int vtMenu, int vtPSS) {
		sprite = Base.oSprtie;
		iconHudY =	iconOrgY = Screen.CHAR_WIDTH/1.1f;					// Icons size
		iconHudX = 	iconOrgX = iconHudY * Screen.ASPECT_RATIO;

		itxtlives = vtLives;
		itxtMenu = vtMenu;
		itxtWeapon = vtWeapon;
		itxtPSS = vtPSS;

		oPSS = Adventure.pssGame;
		scoreCur = Arcade.scoreBoard;
		scoreHig= Arcade.scoreHighest;
		itxtMenu = Adventure.txtHudMenu.iTexture;
	}
	/************************************************************************************************************************
	 *  METHOD -- Show HUD stats, lives and current selected weapon
	 ***********************************************************************************************************************/
	protected static void showStats() {
		if(Screen.iMenu == Screen.MENU_OFF) {
			int tSprite = 0;
			iconShift = 0.6f;
			int weaponCount = 0;										// Counter for weapons with ammunition
			float icoPos = 0, icoAlign = 0;

			if(scoreCur.iScore > scoreHig.iScore)
				scoreHig.setScore(scoreCur.iScore);


			scoreCur.display();											// SCORE
			scoreHig.display();

			showPssStats();												// PSS paper, scissor, stone

			Draw.transform(iconMenuX, iconMenuY, Screen.butFireX, Screen.butFireY);	// FIRE BUTTON
			sprite.draw(itxtMenu, 6);

			tSprite = Values.clamp(((Player.iLives-1)<<2)+Player.iPower, 0, 12);
			Draw.transform(iconHudX, iconHudY, 0, LIVES_POS);			// PLAYER LIVES
			sprite.draw(itxtlives, tSprite);

			for(int i = 0; i < 4; i++)									// Go through all the weapons,
				if(Weapon.arRounds[i] > 0)	{							// Draw those Weapons which got shot
					tSprite = (i<<2) +  Weapon.arRounds[i] - 1;			// Select sprite for weapon based on rounds
					if(tSprite < 3 || tSprite > 15){
						tSprite = Values.clamp(tSprite, 3, 15); Log.d("error", "Draw:showGameStats() Weapon Sprite index out of bound");}
					if(i == Weapon.curWeapon) {
						if(i != 0) iconShift += 0.30f;					// Shift Icon, so it don't overlap on previous Icon, if its not first
						if(lastWeapon != Weapon.curWeapon){				// Make Currently Selected weapon Bigger
							lastWeapon = Weapon.curWeapon;
							icoPos   = (WEAPON_POS + iconShift + (0.25f * weaponCount))/SELECT_WEAPON_SIZE;
							icoAlign = (iconHudX*SELECT_WEAPON_SIZE - iconHudX)*(Screen.DEV_MAX_X/SELECT_WEAPON_SIZE);
							selPosX  = -icoAlign/4;
							selPosY = icoPos - icoAlign/2;
							selScaleX = iconHudX*SELECT_WEAPON_SIZE;
							selScaleY = iconHudY*SELECT_WEAPON_SIZE;
						}
						Draw.transform(selScaleX,selScaleY ,selPosX ,selPosY  );// Draw current selected weapon
						sprite.draw(itxtWeapon, tSprite);
						iconShift += 0.25f;								// Shift all icons after cur Sel, as curSel will be bigger
					} else {
						Draw.transform(iconHudX, iconHudY, 0, WEAPON_POS + iconShift+(0.25f * weaponCount) );
						sprite.draw(itxtWeapon, tSprite);
					}
					weaponCount++;
				}
			// MENU BUTTON
			Draw.transform(Screen.CHAR_WIDTH*Screen.ASPECT_RATIO, Screen.CHAR_WIDTH, Screen.menuButtonX, Screen.menuButtonY);
			sprite.draw(itxtMenu, 2);
		} else
			showMenu();
	}

	/************************************************************************************************************************
	 *   METHOD -- Show Origami current stats, Wins, Loss, Current curse, and time for curse
	 ************************************************************************************************************************/
	protected static void showPssStats() {

		final int pssResult = oPSS.iCurResult;

		if(oPSS.bIsCursed)	{											// If there is a curse
			Draw.transform(iconOrgX, iconOrgY, 0, 0);					// Draw Clock
			float temTime = oPSS.iCurseTimer/(float)oPSS.iTotalTime;

			Draw.transform( iconOrgX, iconOrgY, -0.2f,0);				// Draw Active Curse
			if(temTime > 0.25f || bFlash)
				switch(oPSS.iCurCurse){
					case Values.CURSE_PAPER: 	sprite.draw(itxtPSS, 5);	break;
					case Values.CURSE_SCISSOR:	sprite.draw(itxtPSS, 6);	break;
					case Values.CURSE_STONE:	sprite.draw(itxtPSS, 7);	break;
				}

			if(oPSS.iCurseTimer%10 == 0)	bFlash = !bFlash;			// Flip it after every 10 frames
		}

		for(int i = 0 ; i < pssResult; i++){								// Draw ticks and crosses based on wins/loses
			Draw.transform( iconOrgX, iconOrgY, 0, (0.5f*(i+1.5f)));	// Draw Ticks
			sprite.draw(itxtPSS, 12);
		}

		for(int i = 0 ; i > pssResult; i--){
			Draw.transform(iconOrgX, iconOrgY, 0, (-0.5f*(i-1.5f)));	// Draw Crosses
			sprite.draw(itxtPSS, 13);
		}
	}
	/************************************************************************************************************************
	 *   METHOD --  Shows menu option Reset, resume, Exit
	 ************************************************************************************************************************/
	protected static void showMenu() {
		switch(Screen.iMenu) {
			case Screen.MENU_OFF:
				Screen.bPause = false;
				break;

			case Screen.MENU_PAUSE:
				Draw.transform(iconMenuX, iconMenuY, 0, Screen.menuResumeY - 0.5f);	//'Paused' Text as two sprites
				sprite.draw(itxtMenu, 0);
				Draw.transform(iconMenuX, iconMenuY, 0, Screen.menuResumeY + 0.5f);
				sprite.draw(itxtMenu, 1);

				Draw.transform(iconMenuX, iconMenuY, Screen.menuExitX, Screen.menuExitY);	// Buttons Resume, reset, exit
				sprite.draw(itxtMenu, 4);
				Draw.transform(iconMenuX, iconMenuY, Screen.menuRestartX, Screen.menuRestartY);
				sprite.draw(itxtMenu, 5);
				Draw.transform(iconMenuX, iconMenuY, Screen.menuResumeX, Screen.menuResumeY);
				sprite.draw(itxtMenu, 3);
				Screen.bPause = true;									// Pause the game and enable the menu
				break;

			case Screen.MENU_GAMEOVER: 									// Buttons Restart and Exit
				Draw.transform(iconMenuX, iconMenuY, Screen.menuExitX, Screen.menuExitY);
				sprite.draw(itxtMenu, 4);
				Draw.transform(iconMenuX, iconMenuY, Screen.menuRestartX, Screen.menuRestartY);
				sprite.draw(itxtMenu, 5);
				break;
		}
	}

}

