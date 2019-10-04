package com.BreakthroughGames.OrigamiWars;

import android.util.Log;

public class HUD {
    protected static Draw sprite;                                        // Object used to draw diff sprites, HUD and Level Complete
    protected static PowerUps oPSS;
    private static int lastWeapon = -1;
    protected static ScoreBoard scoreCard;
    private static boolean bFlash = true;                                // Flag to flash PSSclock
    private static float iconShift = 0.5f;
    private static final float LIVES_POS = 4.8f;                        // position of Lives on HUD
    private static final float WEAPON_POS = 5.1f;
    private static final float SELECT_WEAPON_SIZE = 1.3f;                // How big should selected weapon icon be 120%

    private static float selScaleX, selScaleY, selPosX, selPosY;        // Selected weapon Size and Position
    protected static int itxtlives, itxtMenu, itxtWeapon, itxtPSS;
    protected static int lvlSaveScroll, lvlTimeComple, lvlStatsSpeed = 8, iCounter = 0;
    private static float iconOrgX, iconOrgY, iconHudX, iconHudY;
    static float iconMenuX;
    static float iconMenuY;

    HUD() {
    }

    /************************************************************************************************************************
     *  METHOD -- Init method is called by Game::ResumeLevel to initlize textures once they are loaded
     ***********************************************************************************************************************/
    protected static void init(int vtLives, int vtWeapon, int vtMenu, int vtPSS) {
        sprite = Base.oSprite;
        iconHudY = iconOrgY = Screen.CHAR_WIDTH / 1.1f;                    // Icons size
        iconHudX = iconOrgX = iconHudY * Screen.ASPECT_RATIO;

        itxtlives = vtLives;
        itxtMenu = vtMenu;
        itxtWeapon = vtWeapon;
        itxtPSS = vtPSS;

        oPSS = Adventure.pssGame;
        scoreCard = Adventure.scoreBoard;
        itxtMenu = Adventure.txtHudMenu.iTexture;
    }

    /************************************************************************************************************************
     *  METHOD -- Show HUD stats, lives and current selected weapon
     ***********************************************************************************************************************/
    public static void showStats() {
        if (Screen.iMenu == Screen.MENU_OFF) {

            int tSprite = 0;
            iconShift = 0.6f;
            int weaponCount = 0;                                        // Counter for weapons with ammunition
            float icoPos = 0, icoAlign = 0;

            scoreCard.display();                                        // SCORE
            showPssStats();                                                // PSS paper, scissor, stone

            Draw.transform(iconMenuX, iconMenuY, Screen.butFireX, Screen.butFireY);    // FIRE BUTTON
            sprite.draw(itxtMenu, 6);

            tSprite = Values.clamp(((Player.iLives - 1) << 2) + Player.iPower, 0, 12);
            Draw.transform(iconHudX, iconHudY, 0, LIVES_POS);            // PLAYER LIVES
            sprite.draw(itxtlives, tSprite);

            for (int i = 0; i < 4; i++)                                    // Go through all the weapons,
                if (Weapon.arRounds[i] > 0)                                // Draw those Weapons which got shot
                {
                    tSprite = (i << 2) + Weapon.arRounds[i] - 1;            // Select sprite for weapon based on rounds
                    if (tSprite < 3 || tSprite > 15) {
                        tSprite = Values.clamp(tSprite, 3, 15);
                        Log.d("error", "Draw:showGameStats() Weapon Sprite index out of bound");
                    }
                    if (i == Weapon.curWeapon) {
                        if (i != 0)
                            iconShift += 0.30f;                    // Shift Icon, so it dont overlap on previous Icon, if its not first
                        if (lastWeapon != Weapon.curWeapon)                // Make Currently Selected weapon Bigger
                        {
                            lastWeapon = Weapon.curWeapon;
                            icoPos = (WEAPON_POS + iconShift + (0.25f * weaponCount)) / SELECT_WEAPON_SIZE;
                            icoAlign = (iconHudX * SELECT_WEAPON_SIZE - iconHudX) * (Screen.DEV_MAX_X / SELECT_WEAPON_SIZE);
                            selPosX = -icoAlign / 4;
                            selPosY = icoPos - icoAlign / 2;
                            selScaleX = iconHudX * SELECT_WEAPON_SIZE;
                            selScaleY = iconHudY * SELECT_WEAPON_SIZE;
                        }
                        Draw.transform(selScaleX, selScaleY, selPosX, selPosY);// Draw current selected weapon
                        sprite.draw(itxtWeapon, tSprite);
                        iconShift += 0.25f;                                // Shift all icons after cur Sel, as curSel will be bigger
                    } else {
                        Draw.transform(iconHudX, iconHudY, 0, WEAPON_POS + iconShift + (0.25f * weaponCount));
                        sprite.draw(itxtWeapon, tSprite);
                    }
                    weaponCount++;
                }
            // MENU BUTTON
            Draw.transform(Screen.CHAR_WIDTH * Screen.ASPECT_RATIO, Screen.CHAR_WIDTH, Screen.menuButtonX, Screen.menuButtonY);
            sprite.draw(itxtMenu, 2);
        } else
            showMenu();
    }

    /************************************************************************************************************************
     *   METHOD -- Show Origami current stats, Wins, Loss, Current curse, and time for curse
     ************************************************************************************************************************/
    protected static void showPssStats() {

        final int pssResult = oPSS.iCurResult;

        if (oPSS.bIsCursed) {                                               // If there is a curse
            Draw.transform(iconOrgX, iconOrgY, 0, 0);                    // Draw Clock
            float temTime = oPSS.iCurseTimer / (float) oPSS.iTotalTime;

            Draw.transform(iconOrgX, iconOrgY, -0.2f, 0);                // Draw Active Curse
            if (temTime > 0.25f || bFlash)
                switch (oPSS.iCurCurse) {
                    case Values.CURSE_PAPER:
                        sprite.draw(itxtPSS, 5);
                        break;
                    case Values.CURSE_SCISSOR:
                        sprite.draw(itxtPSS, 6);
                        break;
                    case Values.CURSE_STONE:
                        sprite.draw(itxtPSS, 7);
                        break;
                }

            if (oPSS.iCurseTimer % 10 == 0)
                bFlash = !bFlash;            // Flip it after every 10 frames
        }

        for (int i = 0; i < pssResult; i++){                                // Draw ticks and crosses based on wins/loses
            Draw.transform(iconOrgX, iconOrgY, 0, 0.5f * (i + 1.5f));    // Draw Ticks
            sprite.draw(itxtPSS, 12);
        }
        for (int i = 0; i > pssResult; i--) {
            Draw.transform(iconOrgX, iconOrgY, 0, -0.5f * (i - 1.5f));    // Draw Crosses
            sprite.draw(itxtPSS, 13);
        }
    }

    /************************************************************************************************************************
     *   METHOD -- Show level stats at the end of level completion
     ************************************************************************************************************************/
    protected static int levelEndStats() {

        Draw.transform(1, 1, 0, 0);                                        // Draw BackGround
        Adventure.BG_lvlCompl.draw();
        scoreCard.display();                                                // Draw the score board

        int temCounter = iCounter;

        switch (iCounter) {
            case 0:                                                                            // Do Initilization
                if (Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_COMPL_TIME] == 0)            // If 0, set to 1 to avoid divide by zero
                    Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_COMPL_TIME] = 1;
                if (Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_TOTAL_SCROLLS] == 0)            // If origami saved are zero, set 1 to avoid divide by zero
                    Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_TOTAL_SCROLLS] = 1;

                lvlSaveScroll = (int) ((Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_SAVE_SCROLLS] / Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_TOTAL_SCROLLS]) * 10);
                lvlTimeComple = (int) ((Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_DISTANCE] / Values.LEVEL_STATS[Adventure.iLevel][Values.LEVEL_COMPL_TIME]) * 10);
                if (lvlTimeComple > 10) lvlTimeComple = 10;
                iCounter++;
                break;
            default:
                temCounter -= 1;                                                            // Delay for next set of stats
                for (int j = 0; (j < lvlSaveScroll) && (temCounter > 0); j++, temCounter--) {
                    Draw.transform(iconOrgX, iconOrgY, 2.0f / Screen.ASPECT_RATIO, 2.5f + (j * 0.6f));
                    sprite.draw(Adventure.txtPSS.iTexture, 15);
                    if (temCounter == 1 && Adventure.events.iTimer % lvlStatsSpeed == 0)
                        Adventure.scoreBoard.add(Values.SCORE_ORIGAMI_PERC, false);                    // Add score for Origami Eggs
                }

                temCounter -= 2;                                                            // Delay for next set of stats
                for (int j = 0; (j < lvlTimeComple) && (temCounter > 0); j++, temCounter--) {
                    Draw.transform(iconOrgX, iconOrgY, 3.4f / Screen.ASPECT_RATIO, 2.5f + (j * 0.6f));
                    sprite.draw(Adventure.txtPSS.iTexture, 14);
                    if (temCounter == 1 && Adventure.events.iTimer % lvlStatsSpeed == 0)                // Add score for  Time Completion
                        Adventure.scoreBoard.add(Values.SCORE_TIME, false);
                }

                temCounter -= 2;                                                            // Delay for next set of stats
                for (int j = 0; (j < PowerUps.iLevelWins) && (temCounter > 0); j++, temCounter--) {
                    Draw.transform(iconOrgX, iconOrgY, 5.0f / Screen.ASPECT_RATIO, 2.5f + (j * 0.6f));
                    sprite.draw(itxtPSS, 12);
                    if (temCounter == 1 && Adventure.events.iTimer % lvlStatsSpeed == 0)
                        Adventure.scoreBoard.add(Values.SCORE_WIN_LOSE, false);                        // Add Score for Magic Wins
                }

                temCounter -= 2;                                                            // Delay for next set of stats
                for (int j = 0; (j < PowerUps.iLevelLoses) && (temCounter > 0); j++, temCounter--) {
                    Draw.transform(iconOrgX, iconOrgY, 6.2f / Screen.ASPECT_RATIO, 2.5f + (j * 0.6f));
                    sprite.draw(itxtPSS, 13);
                    if (temCounter == 1 && Adventure.events.iTimer % lvlStatsSpeed == 0)
                        Adventure.scoreBoard.subtract(Values.SCORE_WIN_LOSE, false);                    // Add score for curse loses
                }

                if (temCounter > 5) {
                    if (lvlStatsSpeed == 8)
                        Screen.bPause = true;        // if user hasn't touched the screen, pause the screen
                    return Values.GAME_LEVEL_COMPLETE;                    // Done with displaying the icon, plus some delay
                }
        }

        if (Screen.bTouch)
            lvlStatsSpeed = 2;
        else
            lvlStatsSpeed = 8;

        if (Adventure.events.iTimer++ % lvlStatsSpeed == 0) iCounter++;
        return Values.GAME_LEVEL_STATS;
    }

    /************************************************************************************************************************
     *   METHOD --  Shows menu option Reset, resume, Exit
     ************************************************************************************************************************/
    protected static void showMenu() {
        switch (Screen.iMenu) {
            case Screen.MENU_OFF:
                Screen.bPause = false;
                break;
            case Screen.MENU_PAUSE:
                Draw.transform(iconMenuX, iconMenuY, 0, Screen.menuResumeY - 0.5f);    //'Paused' Text as two sprites
                sprite.draw(itxtMenu, 0);
                Draw.transform(iconMenuX, iconMenuY, 0, Screen.menuResumeY + 0.5f);
                sprite.draw(itxtMenu, 1);

                Draw.transform(iconMenuX, iconMenuY, Screen.menuExitX, Screen.menuExitY);    // Buttons Resume, reset, exit
                sprite.draw(itxtMenu, 4);
                Draw.transform(iconMenuX, iconMenuY, Screen.menuRestartX, Screen.menuRestartY);
                sprite.draw(itxtMenu, 5);
                Draw.transform(iconMenuX, iconMenuY, Screen.menuResumeX, Screen.menuResumeY);
                sprite.draw(itxtMenu, 3);
                Screen.bPause = true;                                // Pause the game and enable the menu

                break;
            case Screen.MENU_GAMEOVER:                                // Buttons Restart and Exit
                Draw.transform(iconMenuX, iconMenuY, Screen.menuExitX, Screen.menuExitY);
                sprite.draw(itxtMenu, 4);
                Draw.transform(iconMenuX, iconMenuY, Screen.menuRestartX, Screen.menuRestartY);
                sprite.draw(itxtMenu, 5);
                break;
        }
    }

}