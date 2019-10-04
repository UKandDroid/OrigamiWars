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

public class Stage3_1 extends Adventure {
    private float leafData[][] = {{4.1f, 21.9f, 23.4f, 24.4f, 37.3f, 39f, 39.8f, 41.1f, 58.4f, 62.5f, 81.3f, 82.9f, 100.2f, 101.8f, 103.5f, 116.4f, 126.3f, 132.5f, 133.5f, 149.4f, 153.5f, 157.7f, 161.9f, 165f,},
            {6f, 5.2f, 6.2f, 7.4f, 7f, 6f, 6.4f, 5.3f, 6.7f, 6.1f, 6.1f, 4.9f, 8f, 6.8f, 5.4f, 7.4f, 3f, 4.8f, 2.4f, 3.5f, 4.9f, 7f, 5f, 7f,},
            {Values.PATH_WIND, EnemyFire.SHOT_NONE, 0, 0,}};

    private float buttData[][] = {{10f, 11.4f, 13.8f, 28.7f, 31.8f, 35.4f, 56.6f, 60.3f, 72f, 73f, 74.6f, 119.9f, 120.2f, 122f, 123.5f,},
            {6.8f, 5.1f, 6.3f, 5.8f, 4f, 3.1f, 2.3f, 2.7f, 4.8f, 6.7f, 5.7f, 6.4f, 3.6f, 4.9f, 3.3f,},
            {Values.PATH_RANDOM, EnemyFire.SHOT_Small, EnemyFire.PATH_INTERCEPT_FAST, EnemyFire.FIRE_Once,}};

    private float waspData[][] = {{4.5f, 6.1f, 7.7f, 15.9f, 17.9f, 19.5f, 42.2f, 44.1f, 45.1f, 95.9f, 97.9f, 100f,},
            {2.2f, 3.5f, 4.1f, 8f, 6.8f, 5.6f, 3.5f, 4.8f, 3.1f, 3f, 2.3f, 2.7f,},
            {Values.PATH_INLINE_MED, EnemyFire.SHOT_Small, EnemyFire.PATH_STRAIGHT_FAST, EnemyFire.FIRE_Twice,}};

    private float vultData[][] = {{24.8f, 30.2f, 48f, 51.9f, 63.4f, 66.1f, 76.7f, 85.9f, 91.5f, 93.4f, 106.7f, 112.4f, 136f, 139f, 143.1f, 146.8f,},
            {2.9f, 8.8f, 8.5f, 3.2f, 8.2f, 2.7f, 3.2f, 2.5f, 7.9f, 6.2f, 8.7f, 3.1f, 7.8f, 6.2f, 5.1f, 2f,},
            {Values.PATH_INTERCEPT_MED, EnemyFire.SHOT_Medium, EnemyFire.PATH_STRAIGHT_FAST, EnemyFire.FIRE_Twice,}};

    private float dragData[][] = {{6.9f, 18f, 27.3f, 32.8f, 50.3f, 53.3f, 67.7f, 78.6f, 89f, 108.5f, 114.1f, 129.1f, 137.1f, 140f, 143.2f,},
            {8.6f, 1.5f, 1.2f, 7.8f, 7f, 1.3f, 1.1f, 1.2f, 1.5f, 7.2f, 1.7f, 2.3f, 3.7f, 2f, 1.1f,},
            {Values.PATH_INLINE_FAST, EnemyFire.SHOT_DragFire, EnemyFire.PATH_INLINE_FAST, EnemyFire.FIRE_Always,}};


    /*============================================Start Class Methods=============================================================*/
    @Override
    public void resumeLevel() {
        BG_Sky.loadTexture(R.drawable.lvl1_bg_sky, GL10.GL_REPEAT);
        BG_Front.loadTexture(R.drawable.lvl1_bg_front, GL10.GL_REPEAT);
        BG_Back.loadTexture(R.drawable.lvl1_bg_back, GL10.GL_REPEAT);
    }

    public void loadLevel() {
        BG_Sky.loadTexture(R.drawable.lvl1_bg_sky, GL10.GL_REPEAT);
        BG_Front.loadTexture(R.drawable.lvl1_bg_front, GL10.GL_REPEAT);
        BG_Back.loadTexture(R.drawable.lvl1_bg_back, GL10.GL_REPEAT);

        LEVEL_ENEMIES = buttData[0].length + waspData[0].length + vultData[0].length + dragData[0].length;

        SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL2);                        // Play Level Music
        super.reset();
        initObjects();                                                        // Initilize all object, items and weapons
    }/*-----------------------------------------------------End Method()------------------------------------------------------------*/

    public void runOneFrame() {
        calcLevelStats();                                                    // Calculate level speed and Maximum Enemies
        drawBackgrounds();
        moveObjects();
        detectCollision();
        headUpDisplay();
    }/*-----------------------------------------------------End Method()------------------------------------------------------------*/

    void headUpDisplay() {
        HUD.showStats();                                                // Show Stats HUD - Life, weapon, fire etc
        switch (events.iTimer) {
            case 5:
                events.dispatch(Events.STAGE3_LEVEL1);
                break;        // 13- Ready, as level starts
        }
        events.draw();
    }

    public void initObjects() {
        for (int i = 0; i < Values.ENEMY_END; i++)
            arIndex[i] = 0;            // Reset Indexes for enemy arrays
        for (int i = 0; i < Values.ENEMY_END; i++) arDistance[i] = null;    // Reset array

        arDistance[Values.ENEMY_LEAF] = leafData[0];
        arPosition[Values.ENEMY_LEAF] = leafData[1];
        arProperty[Values.ENEMY_LEAF] = leafData[2];

        arDistance[Values.ENEMY_BUTTERFLY] = buttData[0];
        arPosition[Values.ENEMY_BUTTERFLY] = buttData[1];
        arProperty[Values.ENEMY_BUTTERFLY] = buttData[2];

        arDistance[Values.ENEMY_WASP] = waspData[0];
        arPosition[Values.ENEMY_WASP] = waspData[1];
        arProperty[Values.ENEMY_WASP] = waspData[2];

        arDistance[Values.ENEMY_VULTURE] = vultData[0];
        arPosition[Values.ENEMY_VULTURE] = vultData[1];
        arProperty[Values.ENEMY_VULTURE] = vultData[2];

        arDistance[Values.ENEMY_DRAGON] = dragData[0];
        arPosition[Values.ENEMY_DRAGON] = dragData[1];
        arProperty[Values.ENEMY_DRAGON] = dragData[2];

        for (int i = 0; i < 8; i++)
            createEnemy(object[i]);

        for (int i = 8; i < MAX_OBJECTS; i++)
            object[i].create(Values.SCROLL_NORMAL, 0, 0, 0);
    }

    /************************************************************************************************************************
     *   METHOD- Draws Background for the level
     ***********************************************************************************************************************/
    public void drawBackgrounds() {
        Draw.transform(0.7f, 1, 0, 0);                                        // Draw Sky
        BG_Sky.draw();

        Draw.transform(0.5f, 1, 0.8f, 0);
        BG_Back.draw(0.0f, BG_Back.scrollY);
        BG_Back.scrollY += Values.SCROLL_SPEED / 8;

        Draw.transform(0.5f, 1, 1, 0);
        BG_Front.draw(0, BG_Front.scrollY);
        BG_Front.scrollY += Values.SCROLL_SPEED / 2;
        if (BG_Front.scrollY == Float.MAX_VALUE) BG_Front.scrollY = 0;
    }

    /************************************************************************************************************************
     *   METHOD- Check game status and return message to main game loop, GameRunning, GameOver, LevelComplete
     ***********************************************************************************************************************/
    public byte checkGameStatus() {
        if (enemyDestroyed == LEVEL_ENEMIES && Player.iLives != 0) {
            if (!events.bEnable)
                switch (iSequence) {
                    case 0:
                        SoundPlayer.setVolume(1.0f, 0.5f);                // Lower Sound when Level Completes
                        iSequence++;
                        events.dispatch(Events.LEVEL_COMPLETE);
                        Values.LEVEL_STATS[iLevel][Values.LEVEL_COMPL_TIME] = (int) odoMeter;
                        break;
                    case 1:
                        return Values.GAME_LEVEL_STATS;
                }
        }

        if (Player.iLives == 0) {
            if (!events.bEnable)
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
        }

        return Values.GAME_RUNNING;
    }


    public void loadingScreen() {
        BG_Middle.loadTexture(R.drawable.event_loading, GL10.GL_CLAMP_TO_EDGE);
        Draw.transform(0.22f, 1, 2, 0);
        BG_Middle.draw(0.0f, 0);
    }

}/*END LEVEL */
