package com.BreakthroughGames.OrigamiWars;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import com.BreakthroughGames.OrigamiWars.utils.Flow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Texture {
	//Class to upload texture sprites
	protected static GL10 gl;
	public int iTexture = 0;
	private static int[] textures = {0};
	private static final int MAX_ASYNC_TEXTURE = 4;											// Async Textures Queue
	private static boolean bGenerateTxt = false, bDecodingTxt = false;						// Generate texture once bitmap is decoded
	private static int iNextTexture, iCurTexture;
	private static Bitmap arrBitmap[] = new Bitmap[MAX_ASYNC_TEXTURE];
	private static Background  arrAsyncBG[] =  new Background[MAX_ASYNC_TEXTURE];			// Pointer for Async texture BGs
	private static int arrResIds[] = {0, 0, 0, 0};											// Array for resource Ids
	private static Flow flowLoadTextures;

	protected static final int SHEET_PSS 	 	 = R.drawable.sheet_pss;
	public static final int STORY_START    		 = R.drawable.story_start;
	public static final int STORY_END   		 = R.drawable.story_end;
	protected static final int SHEET_PLANE 		 = R.drawable.sheet_plane;
	protected static final int SHEET_PUPS 		 = R.drawable.sheet_powerups;
	protected static final int SHEET_CLOUDS 	 = R.drawable.sheet_clouds;

	protected static final int SHEET_HUD_LIVES 	 = R.drawable.sheet_hud_lives;
	protected static final int SHEET_HUD_WEAPONS = R.drawable.sheet_hud_weapons;
	protected static final int SHEET_HUD_MENU 	 = R.drawable.sheet_hud_menu;

	protected static final int SHEET_DIGITS 	 = R.drawable.sheet_digits;
	protected static final int SHEET_DIGITS_2 	 = R.drawable.sheet_digits_2;
	protected static final int SHEET_ENEMIES_1 	 = R.drawable.sheet_enemies_1;
	protected static final int SHEET_ENEMIES_2 	 = R.drawable.sheet_enemies_2;
	protected static final int SHEET_EVENTS_1  	 = R.drawable.sheet_event_1;
	protected static final int SHEET_EVENTS_2  	 = R.drawable.sheet_event_2;
	protected static final int SHEET_PLANE_FIRE  = R.drawable.sheet_plane_fire;
	protected static final int SHEET_ENEMY_FIRE  = R.drawable.sheet_enemies_fire;
	protected static final int BG_LEVEL_COMPLETE = R.drawable.bg_lvl_complete;

	protected Texture() {
		flowLoadTextures = new Flow();
		flowLoadTextures.code((Flow.Code) (iAction, bSuccess, iExtra, data) -> {
			decodeBitmap(arrResIds[iAction]);
		});
	}
	protected Texture(GL10 glRef) {
		super();
		gl = glRef;
	}

	public int loadTexture(int texture)	{ return iTexture = getTxtId( texture, GL10.GL_CLAMP_TO_EDGE); }
	protected int loadTexture(int texture, int repeatOrClamp) { return iTexture =  getTxtId(texture, repeatOrClamp); }
	protected static int getTxtId(int texture){ return getTxtId(texture, GL10.GL_CLAMP_TO_EDGE); }
	protected static int getTxtId(int texture, int repeatOrClamp) {
		InputStream imageStream = Game.refContext.getResources().openRawResource(texture);
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(imageStream);
		} catch(Exception e){}
		finally{ try {
			imageStream.close();
		} catch(IOException e)	{} }	//Finally braces

		//Generate the texture once image is loaded
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,repeatOrClamp);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();

		return  textures[0];
	}

	/************************************************************************************************************************
	 *   METHOD - Methods For Asynchronous texture loading add texture id in the loading queue
	 ************************************************************************************************************************/
	protected static void loadAsyncTexture(int resID, Background  vAsycnBG){
		arrResIds[iNextTexture] = resID;
		arrAsyncBG[iNextTexture++] = vAsycnBG;
		if(iNextTexture >= MAX_ASYNC_TEXTURE){ iNextTexture = 0; }
	}
	/************************************************************************************************************************
	 *   METHOD - Decodes bitmap for Async loading, once decoded sets the flag for texture generation
	 ************************************************************************************************************************/

	private static void decodeBitmap(int resId){
		InputStream imageStream = Game.refContext.getResources().openRawResource(resId);
		try {
			arrBitmap[iCurTexture] = BitmapFactory.decodeStream(imageStream);	}
		catch(Exception e){}
		finally { try { imageStream.close(); }catch(IOException e) {} }

		bDecodingTxt = false;
		bGenerateTxt = true;
	}
	/************************************************************************************************************************
	 *   METHOD - Generates texture, once bitmap has been decoded
	 ************************************************************************************************************************/
	private static void generateTexture(){
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, arrBitmap[iCurTexture], 0);
		arrBitmap[iCurTexture].recycle();

		arrAsyncBG[iCurTexture].iTexture  = textures[0];
		arrAsyncBG[iCurTexture] = null;
		bGenerateTxt = false;
	}
	/************************************************************************************************************************
	 *   METHOD - Checks if any texture needs to be loaded Asynchronously, by checking the queue
	 ************************************************************************************************************************/
	public static void processAsyncTexture(){
		for( int i = 0; i < MAX_ASYNC_TEXTURE; i++)
			if(!bDecodingTxt && !bGenerateTxt && arrAsyncBG[i] != null){
				bDecodingTxt = true;
				iCurTexture = i;
				flowLoadTextures.run( iCurTexture, false);
				break;
			}

		if(bGenerateTxt){
			generateTexture();
		}
	}

}
