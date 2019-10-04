package com.BreakthroughGames.OrigamiWars;

import javax.microedition.khronos.opengles.GL10;

public class Arcade extends Game {
    // call Sequence for New Game	: loadingScreen() -> LoadLevel(){ loadObstacles() ResumeLevel(){ loadTextures()}}
    // call Sequence for Resume Game: ResumeLevel(){ loadTextures()}

    private Texture txtScoreHigh;
    private static float lastEnemyPos = 0;
    protected static ScoreBoard scoreHighest;
    protected static final int MAX_ENEMIES = 8;
    private static final int MIN_ENEMY_DIFF = 8;
    private static final int MAX_ENEMY_DIFF = 48;
    private static int MAX_Enemy_Diff = MIN_ENEMY_DIFF;                                    //  Total power on enemies on screen,
    static protected BackgroundArcade BG_HD_Front = new BackgroundArcade();
    static protected BackgroundArcade BG_HD_Middle = new BackgroundArcade();
    static protected BackgroundArcade BG_HD_Sky = new BackgroundArcade();
    static protected BackgroundArcade BG_HD_Cloud = new BackgroundArcade();


    public Arcade() {
    }

    Arcade(GL10 glRef) {
        super(glRef);
        scoreHighest = new ScoreBoard();
        txtScoreHigh = new Texture(glRef);
        scoreBoard.setPosition(0.35f, 3.2f, 0.34f);
        scoreHighest.setPosition(0, 3.2f, 0.38f);
        for (int i = 0; i < MAX_OBJECTS; i++)
            object[i] = new Object();                    // Initialize Objects
        for (int i = 0; i < Weapon.MAX_SHOTS; i++)
            fire[i] = new Weapon();                    // Initialize weapons + ShockWave
    }

    public byte checkGameStatus() {
        iLevel = iStartLevel;
        return Values.GAME_LOAD_SCREEN;
    }

    /************************************************************************************************************************
     *    METHOD -- Called when before loading the resources, used to display loading screen
     ************************************************************************************************************************/
    public void loadingScreen() {
        Values.log("MethodCall", "Game::setLoadingScreen() Called");
        BG_Middle.loadTexture(R.drawable.event_loading, GL10.GL_CLAMP_TO_EDGE);
        Draw.transform(0.22f, 1, 2, 0);
        BG_Middle.draw(0.0f, 0);
    }

    /************************************************************************************************************************
     *    METHOD--  Load Level Called only when App starts first time, unloadLevel called for every Level
     ************************************************************************************************************************/
    protected void loadLevel() {
        Values.log("MethodCall", "Game::loadLevel() Called");
        loadObstacles();                                                                    // Load all obstacles
        resumeLevel();
    }

    /************************************************************************************************************************
     *    METHOD--  Called  when Phone wakes up, or game comes to foreground from background
     ************************************************************************************************************************/
    public void resumeLevel() {
        Values.log("MethodCall", "Game::ResumeLevel() Called");
        Screen.reset();
        loadTextures();
        txtScoreHigh.loadTexture(Texture.SHEET_DIGITS_2);
        scoreHighest.iTexture = txtScoreHigh.iTexture;

        ArcadeHUD.init(txtHudLives.iTexture, txtHudWeapon.iTexture, txtHudMenu.iTexture, txtPSS.iTexture);

        //			[Level]			[Enemy]  	[Curse:%	Time	ScrollTime	Speed]		MaxSpeed	LvlLength	[Obstacles:	Type			Min,	Max		Frequency]
        Values.setLevelStats(1, 2, 6, 0.50f, 1000, 450, 1.0f, 1.4f, 300, Obstacle.WHITE_CLOUDS, 1, 6, 0.40f);
    }

    /************************************************************************************************************************
     *   METHOD - When player restarts a level, set level stats as they were at the start of the level
     ************************************************************************************************************************/
    protected void levelRestart() {
        Values.log("MethodCall", "Game::reload() Called");
        BG_HD_Front.resetTexture();
        BG_HD_Middle.resetTexture();
        BG_HD_Sky.resetTexture();
        BG_HD_Cloud.resetTexture();
        reset();
        Pref.getSet(Pref.LEVEL_RESTART);                                                // Reset the variables load them from prefs
    }

    /************************************************************************************************************************
     *  METHOD - Resets generic level stats, called at the start of every level.   Called by: loadLevel(), levelRestart()
     ************************************************************************************************************************/
    public void reset() {
        Values.log("MethodCall", "Game::reset() Called");

        Screen.reset();                                                                    // Reset fingers index in screen class
        pssGame.reset();                                                                // Reset PaperScissor Stone stats
        events.reset();                                                                    // Reset any event that been displayed
        player.spawn();
        setSpeed(1);
        resetSpeed();

        BG_Middle.scrollY = -1;
        scoreBoard.setScore(0);                                                            // Set the score for that level
        SoundPlayer.setVolume(1.0f, 0.5f);                                                // Set sound level to half when level starts
        Weapon.eState = Weapon.WEAPON_OFF;
        Player.iPower = Values.PLAYER_POWER;
        odoMeter = iActvEnemy = HUD.iCounter = 0;
        Weapon.shotsFired = enemyDestroyed = iSequence = 0;

        // BG_Back.scrollY = BG_Front.scrollY = events.iTimer = lastShotTime = 0;

        for (int i = 0; i < MAX_ACTIVE_OBST; i++)
            actObstacles[i] = null;                // Reset Obstacles
        for (int i = 0; i < Weapon.MAX_SHOTS; i++)
            fire[i].create(txtPlaneFire.iTexture, 0);     // Reset bullets
        for (int i = 0; i < MAX_OBJECTS; i++)
            object[i].bEnable = false;                    // Initialize Objects
    }

    /************************************************************************************************************************
     *   METHOD- Creates Enemies, based on current enemy Difficulty
     ***********************************************************************************************************************/
    protected static void createEnemy(Object enemy) {
        int actEnemyDiff = 0;
        int enemyType = 0;
        int emptySlot = -1;
        float objPosX = 0;
        Object obj;

        for (int i = 0; i < MAX_ENEMIES; i++)
            if (object[i].bEnable)
                actEnemyDiff += object[i].iPower;
            else if (!object[i].bFalling)
                emptySlot = i;

        actEnemyDiff = MAX_Enemy_Diff - actEnemyDiff;

        if (actEnemyDiff > 0 && emptySlot != -1 && iActvEnemy < MAX_Actv_Enemy) {                    // If Active Enemies combined difficulty is less the Max Enemy Difficulty, and Active Enemies are Less then Max Active Enemies
            objPosX = 1 + ran.nextInt(9);
            if (Math.abs(lastEnemyPos - objPosX) < 2) {                                            // if two enemies are too close to each other, make them apart on X axis
                if (lastEnemyPos < objPosX) objPosX += (2 - Math.abs(lastEnemyPos - objPosX));
                else objPosX -= (2 - Math.abs(lastEnemyPos - objPosX));
            }
            objPosX = Values.clamp(objPosX, 0, Screen.DEV_MAX_X - 1);
            lastEnemyPos = objPosX;

            obj = object[emptySlot];
            enemyType = ran.nextInt(actEnemyDiff + 1);

            switch (enemyType) {
                case 0:
                    obj.create(Values.ENEMY_LEAF, Values.PATH_WIND, 4 + ran.nextInt(6), 0);
                    break;
                case 1:
                case 2:
                    obj.create(Values.ENEMY_BUTTERFLY, Values.PATH_RANDOM, objPosX, 0);
                    obj.createFire(EnemyFire.SHOT_Small, EnemyFire.PATH_INTERCEPT_SLOW, EnemyFire.FIRE_Always);
                    break;
                case 3:
                    obj.create(Values.ENEMY_WASP, Values.PATH_INLINE_SLOW, ran.nextInt(10), 0);
                    obj.createFire(EnemyFire.SHOT_Small, EnemyFire.PATH_STRAIGHT_SLOW, EnemyFire.FIRE_Twice);
                    break;
                case 4:
                case 5:
                    obj.create(Values.ENEMY_DRAGONFLY, Values.PATH_AVOID, ran.nextInt(10), 0);
                    obj.createFire(EnemyFire.SHOT_Small, EnemyFire.PATH_INTERCEPT_BACK, EnemyFire.FIRE_Always);
                    break;
                case 6:
                    obj.create(Values.ENEMY_HORNET, Values.PATH_INTERCEPT_MED, ran.nextInt(10), 0);
                    obj.createFire(EnemyFire.SHOT_Small, EnemyFire.PATH_INLINE_MED, EnemyFire.FIRE_Always);
                    break;
                case 7:
                    obj.create(Values.ENEMY_BAT, Values.PATH_SINE_WAVE, ran.nextInt(10), 0);
                    obj.createFire(EnemyFire.SHOT_Medium, EnemyFire.PATH_INTERCEPT_MED, EnemyFire.FIRE_Always);
                    break;
                case 8:
                    obj.create(Values.ENEMY_VULTURE, Values.PATH_INTERCEPT_MED, ran.nextInt(10), 0);
                    obj.createFire(EnemyFire.SHOT_Medium, EnemyFire.PATH_INLINE_MED, EnemyFire.FIRE_Always);
                    break;
                case 9:
                case 11:
                    object[emptySlot].create(Values.ENEMY_PTEROSAUR, Values.PATH_INTERCEPT_MED, ran.nextInt(10), 0);
                    obj.createFire(EnemyFire.SHOT_PetSFire, EnemyFire.PATH_STRAIGHT_MED, EnemyFire.FIRE_2ndTime);
                    break;
                case 10:
                case 12:
                    object[emptySlot].create(Values.ENEMY_DRAGON, Values.PATH_INLINE_MED, ran.nextInt(10), 0);
                    obj.createFire(EnemyFire.SHOT_DragFire, EnemyFire.PATH_INLINE_MED, EnemyFire.FIRE_Always, 1.5f);
                    break;
            }
        }
    }

    /************************************************************************************************************************
     *   METHOD- Draws Background for the level
     ***********************************************************************************************************************/
    protected static void calculateStats() {

        pssGame.checkStatus();                                                            // Check if Origami curse time has runout
        bSecond = (events.iTimer++ % GAME_FPS == 0);                                    // Calculate once in every second

        if (bSecond) {
            float MAX_Obst_Freq;
            float Max_Speed = Values.LEVEL_STATS[1][Values.LEVEL_MAX_SPEED];
            float Max_Freq = Values.LEVEL_STATS[1][Values.LEVEL_OBST_FREQ];
            float Min_Enemy = Values.LEVEL_STATS[1][Values.LEVEL_MIN_ENEMIES];
            float Max_Enemy = Values.LEVEL_STATS[1][Values.LEVEL_MAX_ENEMIES];
            float Max_Obstacles = Values.LEVEL_STATS[1][Values.LEVEL_MAX_OBST];
            float Min_Obstacles = Values.LEVEL_STATS[1][Values.LEVEL_MIN_OBST];
            float Max_Distance = Values.LEVEL_STATS[1][Values.LEVEL_DISTANCE];

            float temVol = (float) (SPEED_MULT * SPEED_MULT);

            CUR_Obst_Diff = Math.round(Min_Obstacles + ((odoMeter / Max_Distance) * Max_Obstacles));
            CUR_Obst_Diff = (int) Values.clamp(CUR_Obst_Diff, 0, Max_Obstacles);

            MAX_Obst_Freq = 0.5f + ((odoMeter / Max_Distance) * Max_Freq);
            MAX_Obst_Freq = Values.clamp(MAX_Obst_Freq, 0, Max_Freq);

            MAX_Actv_Enemy = (int) (Min_Enemy + ((odoMeter / Max_Distance) * Max_Enemy));
            MAX_Actv_Enemy = (int) Values.clamp(MAX_Actv_Enemy, 0, Max_Enemy);

            MAX_Enemy_Diff = Math.round(MIN_ENEMY_DIFF + ((odoMeter / Max_Distance) * MAX_ENEMY_DIFF));
            MAX_Enemy_Diff = Values.clamp(MAX_Enemy_Diff, 0, MAX_ENEMY_DIFF);


            if (SPEED_MULT < Max_Speed)
                addSpeed(0.008f);

            Record.arcadeCheck();                                                        // Check For Records

            if ((ran.nextFloat() < MAX_Obst_Freq) || (iActObstDiff <= 0))
                calcObstacles(Values.PATH_STRAIGHT_FAST, Obstacle.DARK_CLOUDS + ran.nextInt(odoMeter < 60 ? 1 : 2));                                                        // Set obstacles for the level, based on obstacle difficulty

            if (!pssGame.bIsCursed) temVol *= SPEED_MULT;

            SoundPlayer.setVolume(temVol, SoundPlayer.iBGBaseVolume);                    // Increase sound exponentially
            if (SoundPlayer.iBGBaseVolume < 1.0f) SoundPlayer.iBGBaseVolume += 0.1f;

            if (!Mic.bBlowing) switch (ran.nextInt(30)) {
                case 7:
                case 13:
                case 17:
                case 25:
                    SoundPlayer.playSound(Sound.WIND1, -0.5f);
                    break;
                case 3:
                    if (!pssGame.bIsCursed) SoundPlayer.playSound(Sound.WIND2, -0.5f);
                    break;
            }

            Values.log("GameStats", "Enemy Diff/Max/Actv: " + MAX_Enemy_Diff + "/" + MAX_Actv_Enemy + "/" + iActvEnemy + "   CloudsDiff Max/Act: " + CUR_Obst_Diff + "/" + iActObstDiff + "  Shots: " + Weapon.shotsFired + "  Distance: " + String.format("%.2f", odoMeter) + "  Speed: " + String.format("%.2f", SPEED_MULT));
        }
    }


}
