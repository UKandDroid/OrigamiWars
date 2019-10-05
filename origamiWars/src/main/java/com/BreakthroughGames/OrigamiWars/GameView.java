package com.BreakthroughGames.OrigamiWars;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

import com.BreakthroughGames.OrigamiWars.stages.Stage1_1;
import com.BreakthroughGames.OrigamiWars.stages.Stage1_2;
import com.BreakthroughGames.OrigamiWars.stages.Stage1_3;
import com.BreakthroughGames.OrigamiWars.stages.Stage2_1;
import com.BreakthroughGames.OrigamiWars.stages.Stage2_2;
import com.BreakthroughGames.OrigamiWars.stages.Stage2_3;
import com.BreakthroughGames.OrigamiWars.stages.Stage3_1;
import com.BreakthroughGames.OrigamiWars.stages.Stage3_2;
import com.BreakthroughGames.OrigamiWars.stages.Stage3_3;
import com.BreakthroughGames.OrigamiWars.stages.Stage_Arcade;
import com.BreakthroughGames.OrigamiWars.story.StoryEnd;
import com.BreakthroughGames.OrigamiWars.story.StoryMiddle;
import com.BreakthroughGames.OrigamiWars.story.StoryStart;
import com.BreakthroughGames.OrigamiWars.utils.Logger;

public class GameView extends GLSurfaceView implements Renderer	{				//Class to implement GL init and Rendering
	private long startFPS, diff;
	private Game curLevel = null;
	protected int eGameStatus = -1;												// Current game Status
	protected static long FPSCount=0;
	protected static String strDraw ="";										// log String for Rendering time
	private long loopStart = 0, loopEnd = 0,  loopRunTime = 0;
	public static final int TOTAL_LEVELS = 13;
	private Game level[] = new Game[TOTAL_LEVELS] ;
	private Logger log = new Logger();


	/************************************************************************************************************************
	 *	DEFAULT METHODS
	 ************************************************************************************************************************/
	public GameView(Context context) {
		super(context);
		setRenderer(this);
		Game.refGameView = this;
	}

	@Override public void onSurfaceChanged(GL10 gl, int width, int height) {
		log.d( "GameView::OnSurfaceChanged() ");

		gl.glViewport(0, 0, width, height); 									// set the whole screen viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 									// Set projection mode to set the projection 
		gl.glLoadIdentity();													// load current matrix to identity
		gl.glOrthof(1f, 0f, 1f, 0f, -1f, 1f);
		gl.glEnable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glBlendFunc(GL10.GL_ONE,GL10.GL_ONE_MINUS_SRC_ALPHA);
		startGame( gl);
		Draw.gl = gl;
	}

	@Override public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		log.d( "GameView::OnSurfaceCreated() ");
		Draw.gl = gl;

		gl.glEnable(GL10.GL_TEXTURE_2D); 										// Enable texturing
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glClearDepthf(1.0f);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_ONE,GL10.GL_ONE);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		setRenderMode(RENDERMODE_WHEN_DIRTY);
		Screen.init(Game.refActGame);
	}
	/************************************************************************************************************************
	 *	END DEFAULT METHODS
	 ************************************************************************************************************************/
	@Override public void onDrawFrame(GL10 gl) {
		manageFPS();														// Maintains 60FPS maximum, sleeps thread if going too fast
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		switch(eGameStatus) {
			case Values.GAME_LOAD_SCREEN:									// 1- Entry point, To display loading message, Light calculations
				Game.iLevel++;
				curLevel = level[Game.iLevel];
				curLevel.loadingScreen();
				eGameStatus = Values.GAME_LEVEL_LOAD;
				break;
			case Values.GAME_LEVEL_LOAD:									// 2- Loads the level resources and creates objects
				curLevel.loadLevel();
				eGameStatus = Values.GAME_RUNNING;
				break;
			case Values.GAME_RUNNING:										// 3- Runs the level
				curLevel.runOneFrame();
				eGameStatus = curLevel.checkGameStatus();
				break;
			case Values.GAME_LEVEL_STATS:									// 4- Show completed level Statistics
				eGameStatus = HUD.levelEndStats();
				break;
			case Values.GAME_LEVEL_COMPLETE:								// 5-  level Unload, then goto stage 1- loading screen
				curLevel.levelComplete();
				eGameStatus = Values.GAME_LOAD_SCREEN;
				break;
			case Values.GAME_LEVEL_RESUME:									// Game is resumed after inactiviy, screen lock or application change
				curLevel.resumeLevel();
				eGameStatus = Values.GAME_RUNNING;
				break;
			case Values.GAME_LEVEL_RESTART:									// Player has chosen to restart the game from Menu
				curLevel.initObjects();
				curLevel.levelRestart();
				eGameStatus = Values.GAME_RUNNING;
				break;
			case Values.GAME_OVER:											// Game Over Player has lost all lives
				curLevel.runOneFrame();
				break;
			case Values.GAME_COMPLETE: 			break;
		}

		if(!Screen.bPause) requestRender();
	}

	/************************************************************************************************************************
	 *	METHOD - Initializes level array and Objects for game Start / Resume
	 ************************************************************************************************************************/
	protected void startGame(GL10 glRef) {
		log.d("GameView::startGame() ");

		switch(Game.iMode) {
			case Values.ARCADE_MODE:
				level[0] = new Arcade(glRef);
				level[1] = new Stage_Arcade();
				Game.iLevel =-1;
				Game.iStartLevel = 0;
				eGameStatus = Values.GAME_LOAD_SCREEN;
				Game.iMode = Values.ARCADE_RESUME; 								// Set flag so game Resume from Pause
				break;
			case Values.ARCADE_RESUME:
				level[0].resumeLevel();
				eGameStatus = Values.GAME_LEVEL_RESUME;
				break;

			case Values.STORY_MODE:
			case Values.STORY_RESUME_LEVEL:
				log.d("GameStats", "Game not Loaded : Starting now");
				level[0] = new Adventure(glRef);
				level[1] = new Stage1_1();
				level[2] = new StoryStart();									// Play story after first level so use dont skip quickly
				level[3] = new Stage1_2();
				level[4] = new StoryMiddle();
				level[5] = new Stage1_3();
				level[6] = new Stage2_1();
				level[7] = new Stage2_2();
				level[8] = new Stage2_3();
				level[9] = new Stage3_1();
				level[10]= new Stage3_2();
				level[11]= new Stage3_3();
				level[12]= new StoryEnd();

				Pref.getSet((Game.iMode == Values.STORY_MODE)? Pref.STORY_NEW : Pref.LEVEL_RESUME);								// Load prefs for RESUME Game

				eGameStatus = Values.GAME_LOAD_SCREEN;
				Game.iMode = Values.STORY_RESUME_PAUSE; 						// Set flag so game Resume from Pause
				break;

			case Values.STORY_RESUME_PAUSE:
				log.d("GameStats", "Game Loaded : Resuming Now");
				level[0].resumeLevel();
				eGameStatus = Values.GAME_LEVEL_RESUME;
				break;
		}
	}

	@Override	public void onPause()	{ /*Mic.stopRecording();*/}
	@Override	public void onResume()	{ /*Mic.startRecording();*/}

	/************************************************************************************************************************
	 *	METHOD -- Touch Screen Handler
	 ************************************************************************************************************************/
	@Override public boolean onTouchEvent(final MotionEvent e) {
		if(Screen.check(e))													// Sleep thread for Action_move, old devices send too many Action_Move events
			try { Thread.sleep(10); } catch(InterruptedException e1) { e1.printStackTrace(); }  // Stop flooding of touch events

		return true;
	}

	/************************************************************************************************************************
	 *	METHOD -- Counts Frames per Second
	 ************************************************************************************************************************/
	private void manageFPS() {
		loopEnd = System.currentTimeMillis();
		loopRunTime = loopEnd  - loopStart;										// if game runs faster then set FPS, slow it down 	
		try { if(loopRunTime <= Game.FRAME_TIME) {
			Texture.processAsyncTexture();										// Load any texture pending, as there is some time
			Thread.sleep(Game.FRAME_TIME - loopRunTime);  }}catch(InterruptedException e){e.printStackTrace();}

		FPSCount += 1;
		diff =  System.currentTimeMillis() - startFPS ;

		if(diff > 1000 ) {
			// Log.d("screen","Frame per second ("+Float.toString(diff)+")ms : " + Long.toString(FPSCount));
			startFPS = System.currentTimeMillis();
			FPSCount = 0;
		}

		loopStart = System.currentTimeMillis();
	}

}
