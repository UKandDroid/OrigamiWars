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

public class Stage2_3 extends Adventure {
    private float leafData[][] = {{10.4f, 13.4f, 25.1f, 42.7f, 53f, 55.2f, 57.1f, 85.3f, 87.3f, 89.5f, 102.3f, 104.4f, 110.9f, 120.4f, 121.9f, 124.7f, 127.3f, 129.1f, 131.3f,},
            {4.2f, 6f, 6.6f, 7.3f, 8f, 7.1f, 6.2f, 7f, 6.5f, 7.6f, 7f, 7.8f, 6.7f, 4.5f, 6.6f, 5.3f, 7.1f, 6f, 4.7f,},
            {Values.PATH_WIND, EnemyFire.SHOT_NONE, 0, 0,}};

    private float buttData[][] = {{8f, 8.9f, 10f, 11.4f, 26f, 27.3f, 57.6f, 59.6f, 61.3f, 77.5f, 100.6f, 103.3f,},
            {6.1f, 7.6f, 6.2f, 7.7f, 3.7f, 4.7f, 4.7f, 5.9f, 7.3f, 7.9f, 2.3f, 4.3f,},
            {Values.PATH_RANDOM, EnemyFire.SHOT_Small, EnemyFire.PATH_INTERCEPT_MED, EnemyFire.FIRE_Once,}};

    private float batsData[][] = {{4.1f, 5.9f, 6.2f, 19.5f, 20.8f, 21.8f, 36.6f, 38.6f, 38.9f, 63.4f, 64.6f, 65.8f, 68f, 67.5f, 91f, 94.2f, 96.6f, 98.5f,},
            {3.4f, 1.8f, 4.5f, 4.8f, 6.7f, 4.5f, 7.1f, 5.5f, 8.4f, 3.9f, 2f, 3.9f, 3.7f, 5.9f, 3.7f, 5.4f, 3.7f, 5.6f,},
            {Values.PATH_SINE_WAVE, EnemyFire.SHOT_Small, EnemyFire.PATH_INTERCEPT_MED, EnemyFire.FIRE_Twice,}};

    private float vultData[][] = {{13.2f, 30.4f, 48f, 71.4f, 81.1f, 82.9f, 84.8f, 107.2f, 115f, 115.7f,},
            {1.7f, 2f, 2.1f, 1.3f, 7.2f, 4.4f, 2.4f, 6.3f, 1.3f, 7.3f,},
            {Values.PATH_INTERCEPT_MED, EnemyFire.SHOT_Medium, EnemyFire.PATH_STRAIGHT_MED, EnemyFire.FIRE_Twice,}};

    private float psauData[][] = {{15.9f, 33f, 51.3f, 74.2f, 106.2f, 112.5f,},
            {2.5f, 3.4f, 3.3f, 2.6f, 1.7f, 2.5f,},
            {Values.PATH_INTERCEPT_MED, EnemyFire.SHOT_PetSFire, EnemyFire.PATH_STRAIGHT_MED, EnemyFire.FIRE_Twice,}};

    private float dragData[][] = {{35.9f, 54.1f, 78.5f, 117.9f,},
            {1.7f, 1.5f, 2.1f, 3.8f,},
            {Values.PATH_INLINE_MED, EnemyFire.SHOT_DragFire, EnemyFire.PATH_INLINE_MED, EnemyFire.FIRE_Always,}};


    /*============================================Start Class Methods=============================================================*/
    @Override
    public void resumeLevel() {
        BG_Front.loadTexture(R.drawable.lvl3_bg_front, GL10.GL_REPEAT);        // Load BackGrounds
        BG_Middle.loadTexture(R.drawable.lvl3_bg_fog, GL10.GL_REPEAT);
        BG_Back.loadTexture(R.drawable.lvl3_bg_back, GL10.GL_REPEAT);
        BG_Sky.loadTexture(R.drawable.lvl3_bg_sky);
    }

    public void loadLevel() {
        BG_Front.loadTexture(R.drawable.lvl3_bg_front, GL10.GL_REPEAT);            // Load BackGrounds
        BG_Middle.loadTexture(R.drawable.lvl3_bg_fog, GL10.GL_REPEAT);
        BG_Back.loadTexture(R.drawable.lvl3_bg_back, GL10.GL_REPEAT);
        BG_Sky.loadTexture(R.drawable.lvl3_bg_sky);

        LEVEL_ENEMIES = buttData[0].length + batsData[0].length + vultData[0].length + psauData[0].length + dragData[0].length;

        BG_Back.scrollY = -1;
        SoundPlayer.playSound(Sound.BG_MUSIC_LEVEL1);                            // Play Level Music
        initObjects();                                                            // Initilize all object, items and weapons
        super.reset();                                                            // Reset Progress variables
    }/*-----------------------------------------------------End Method()------------------------------------------------------------*/

    public void runOneFrame() {
        calcLevelStats();                                                        // Calculate level speed and Maximum Enemies
        drawBackgrounds();
        moveObjects();
        drawForeground();
        detectCollision();
        headUpDisplay();

    }/*-----------------------------------------------------End Method()------------------------------------------------------------*/

    void headUpDisplay() {
        HUD.showStats();                                                // Show Stats HUD - Life, weapon, fire etc
        switch (events.iTimer) {
            case 2:
                events.dispatch(Events.STAGE2_LEVEL3);
                break;            // Level 3 Start
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

        arDistance[Values.ENEMY_BAT] = batsData[0];
        arPosition[Values.ENEMY_BAT] = batsData[1];
        arProperty[Values.ENEMY_BAT] = batsData[2];

        arDistance[Values.ENEMY_VULTURE] = vultData[0];
        arPosition[Values.ENEMY_VULTURE] = vultData[1];
        arProperty[Values.ENEMY_VULTURE] = vultData[2];

        arDistance[Values.ENEMY_PTEROSAUR] = psauData[0];
        arPosition[Values.ENEMY_PTEROSAUR] = psauData[1];
        arProperty[Values.ENEMY_PTEROSAUR] = psauData[2];


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

        Draw.transform(0.5f, 1, 0, 0);
        BG_Sky.draw();


        if (BG_Back.scrollY < 0.5f)
            BG_Back.scrollY += Values.SCROLL_SPEED / 16;
        Draw.transform(1.0f, 1.0f, 0, 0);
        BG_Back.draw(0.0f, BG_Back.scrollY);

    }

    void drawForeground() {
        Draw.transform(0.8f, 1, 0.2f, 0);                    // Fog in the valley
        BG_Middle.draw(0, BG_Middle.scrollY);
        BG_Middle.scrollY += Values.SCROLL_SPEED / 3;

        if (BG_Front.scrollY == Float.MAX_VALUE)                            // Front trees
            BG_Front.scrollY = 0f;
        Draw.transform(0.5f, 1, 1, 0);
        BG_Front.draw(0, BG_Front.scrollY);
        BG_Front.scrollY += Values.SCROLL_SPEED;
        if (BG_Front.scrollY >= 1) BG_Front.scrollY = 0;
    }

    /************************************************************************************************************************
     *   METHOD- Check game status and return message to main game loop, GameRunning, GameOver, LevelComplete
     ***********************************************************************************************************************/
    public byte checkGameStatus() {
        if (enemyDestroyed == LEVEL_ENEMIES && Player.iLives != 0) {
            if (!events.bEnable)
                switch (iSequence) {
                    case 0:
                        SoundPlayer.setVolume(1.0f, 0.5f);                    // Lower Sound when Level Completes
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
                        Screen.iMenu = Screen.MENU_GAME_OVER;
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

}/*END LEVEL*/
