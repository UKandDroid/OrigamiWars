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

public class Stage1_2 extends Adventure {
    private final float leafData[][] = {{5.5f, 6.6f, 10.3f, 20.7f, 22.8f, 24.8f, 25.8f, 34.5f, 35.7f, 37f, 40.0f},
            {8.3f, 7.5f, 8.7f, 8.1f, 8f, 6.7f, 5.2f, 4.8f, 7.2f, 5.7f, 4.8f,},
            {Values.PATH_WIND, EnemyFire.SHOT_NONE, 0, 0,}};

    private final float waspData[][] = {{8.0f, 12.7f, 13.6f, 22.1f, 27.6f, 28.8f, 29.5f, 30.8f, 36f, 39.1f, 42.8f,},
            {3.7f, 5.5f, 4.2f, 4.5f, 8.2f, 6.6f, 4.9f, 3.5f, 4.1f, 7.7f, 8.1f,},
            {Values.PATH_INLINE_SLOW, EnemyFire.SHOT_Small, EnemyFire.PATH_STRAIGHT_SLOW, EnemyFire.FIRE_Once,}};

    private final float hornData[][] = {{15.6f, 17.5f, 29f, 38.9f, 40.7f, 42.8f,},
            {3.9f, 7f, 2.6f, 1.6f, 1.7f, 2.1f,},
            {Values.PATH_INTERCEPT_SLOW, EnemyFire.SHOT_Small, EnemyFire.PATH_STRAIGHT_SLOW, EnemyFire.FIRE_Always,}};


    /*============================================Start Class Methods=============================================================*/

    @Override
    public void resumeLevel() {
        BG_Sky.loadTexture(R.drawable.lvl2_bg_sky);
        BG_Back.loadTexture(R.drawable.lvl2_bg_back, GL10.GL_REPEAT);
        BG_Middle.loadTexture(R.drawable.bg_clouds, GL10.GL_CLAMP_TO_EDGE);
        BG_Front.loadTexture(R.drawable.lvl2_bg_front, GL10.GL_REPEAT);
    }

    public void loadLevel() {
        BG_Sky.loadTexture(R.drawable.lvl2_bg_sky);
        BG_Back.loadTexture(R.drawable.lvl2_bg_back, GL10.GL_REPEAT);
        BG_Middle.loadTexture(R.drawable.bg_clouds, GL10.GL_CLAMP_TO_EDGE);
        BG_Front.loadTexture(R.drawable.lvl2_bg_front, GL10.GL_REPEAT);

        LEVEL_ENEMIES = waspData[0].length + hornData[0].length;

        SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL2);                                    // Play Level Music
        super.reset();
        initObjects();
    }
    /*-----------------------------------------------------End Method()------------------------------------------------------------*/

    public void runOneFrame() {
        calcLevelStats();                                                                // Calculate level speed and Maximum Enemies
        drawBackgrounds();
        moveObjects();
        detectCollision();
        headUpDisplay();

    }/*-----------------------------------------------------End Method()------------------------------------------------------------*/

    static void headUpDisplay() {
        HUD.showStats();                                                                // Show Stats HUD - Life, weapon, fire etc
        switch (events.iTimer) {
            case 2:
                events.dispatch(Events.STAGE1_LEVEL2);
                break;                // Level 2 starts
            case 300:
                events.dispatch(Events.FLICK_TO_SWAVE);
                break;
        }
        events.draw();
    }

    public void initObjects() {
        for (int i = 0; i < Values.ENEMY_END; i++)
            arIndex[i] = 0;                        // Reset Indexes for enemy arrays
        for (int i = 0; i < Values.ENEMY_END; i++)
            arDistance[i] = null;                // Reset array

        arDistance[Values.ENEMY_LEAF] = leafData[0];
        arPosition[Values.ENEMY_LEAF] = leafData[1];
        arProperty[Values.ENEMY_LEAF] = leafData[2];

        arDistance[Values.ENEMY_WASP] = waspData[0];
        arPosition[Values.ENEMY_WASP] = waspData[1];
        arProperty[Values.ENEMY_WASP] = waspData[2];

        arDistance[Values.ENEMY_HORNET] = hornData[0];
        arPosition[Values.ENEMY_HORNET] = hornData[1];
        arProperty[Values.ENEMY_HORNET] = hornData[2];

        for (int i = 0; i < 8; i++) createEnemy(object[i]);
        for (int i = 8; i < MAX_OBJECTS; i++) object[i].create(Values.SCROLL_NORMAL, 0, 0, 0);
    }

    /************************************************************************************************************************
     *   METHOD- Draws Background for the level
     ***********************************************************************************************************************/
    public static void drawBackgrounds() {
        Draw.transform(0.7f, 1, 0, 0);                                                    // Draw Sky
        BG_Sky.draw();

        Draw.transform(0.5f, 1, 0.8f, 0);
        BG_Back.draw(0.0f, BG_Back.scrollY);
        BG_Back.scrollY += Values.SCROLL_SPEED / 8;

        if (BG_Middle.scrollY >= -1.0) {
            if (BG_Middle.scrollY > 1.0)
                BG_Middle.scrollY -= (2 + ran.nextFloat() * 5);

            Draw.transform(.5f, 1, 0, 0);
            BG_Middle.draw(0, BG_Middle.scrollY);
        }
        BG_Middle.scrollY += Values.SCROLL_SPEED;

        Draw.transform(0.5f, 1, 1, 0);                                                    // Foreground mountains
        BG_Front.draw(0, BG_Front.scrollY);
        BG_Front.scrollY += Values.SCROLL_SPEED / 2;
        if (BG_Front.scrollY == Float.MAX_VALUE) BG_Front.scrollY = 0;
    }

    /************************************************************************************************************************
     *   METHOD- Check game status and return message to main game loop, GameRunning, GameOver, LevelComplete
     ***********************************************************************************************************************/
    public byte checkGameStatus() {
        if (!events.bEnable && Player.iLives == 0)                                        // If player died, do events in sequence
            switch (iSequence) {
                case 0:
                    iSequence++;
                    Pref.getSet(Pref.GAME_OVER);
                    break;
                case 1:
                    iSequence++;
                    events.dispatch(Events.GAME_OVER);
                    break;
                case 2:
                    Screen.iMenu = Screen.MENU_GAMEOVER;
                    return Values.GAME_OVER;
            }
        else if (!events.bEnable && enemyDestroyed == LEVEL_ENEMIES)                        // Level Complete
            switch (iSequence) {
                case 0:
                    SoundPlayer.setVolume(1.0f, 0.5f);                                    // Lower Sound when Level Completes
                    iSequence++;
                    events.dispatch(Events.LEVEL_COMPLETE);
                    Values.LEVEL_STATS[iLevel][Values.LEVEL_COMPL_TIME] = (int) odoMeter;
                    break;
                case 1:
                    return Values.GAME_LEVEL_STATS;
            }

        return Values.GAME_RUNNING;
    }

    /************************************************************************************************************************
     *   METHOD- Displays 'GameLoading' before level loads
     ***********************************************************************************************************************/
    public void loadingScreen() {
        BG_Middle.loadTexture(R.drawable.event_loading, GL10.GL_CLAMP_TO_EDGE);
        Draw.transform(0.22f, 1, 2, 0);
        BG_Middle.draw(0.0f, 0);
    }
}/*END LEVEL*/
