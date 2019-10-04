package com.BreakthroughGames.OrigamiWars;

import android.util.Log;

import javax.microedition.khronos.opengles.GL10;


public class BackgroundArcade extends Texture {

	private int arrTxt[];						// Array of image texture for background
	private int nextImage = 0;					// Next image to be loaded
	private int nextPlane = 0;					// Next image plane to use
	private int iTotalImages = 0;				// Total Images to cycle through
	private float bgImageScroll[]= {0, 1, 2};		// Scroll for each plane image
	private Background bgPlane[] = new Background[3];
	private float scaleX, scaleY, translateX, translateY;
    private static float PLANE_DISTANCE = 0.997f;

	/************************************************************************************************************************
	 *   CONSTRUCTOR -  
	 ************************************************************************************************************************/
	public BackgroundArcade(){
		bgPlane[0] = new Background();
		bgPlane[1] = new Background();
		bgPlane[2] = new Background();
	}

	/************************************************************************************************************************
	 *   METHOD -  
	 ************************************************************************************************************************/
	public void loadTexture(int resTexture[]) {
		nextPlane = nextImage = 0;
		arrTxt = resTexture;
		iTotalImages = resTexture.length;
		bgImageScroll[0] = 0;
		bgImageScroll[1] = 1;
		bgImageScroll[2] = 2;
		bgPlane[nextPlane++].loadTexture(arrTxt[nextImage++]);
		bgPlane[nextPlane++].loadTexture(arrTxt[nextImage++]);
		bgPlane[nextPlane++].loadTexture(arrTxt[nextImage++]);
		nextPlane = 0;
	}
	
	public void resumeTexture() {
		 
		bgPlane[nextPlane++].loadTexture( arrTxt[(nextImage < 3) ? nextImage-4 + iTotalImages : nextImage-3]);
		if(nextPlane > 2){ nextPlane = 0; }
		bgPlane[nextPlane++].loadTexture( arrTxt[(nextImage < 2) ? nextImage-3 + iTotalImages : nextImage-2]);
		if(nextPlane > 2){ nextPlane = 0; }
		bgPlane[nextPlane++].loadTexture( arrTxt[(nextImage < 1) ? nextImage-2 + iTotalImages : nextImage-1]); 
		if(nextPlane > 2){ nextPlane = 0; }
	}

	protected void resetTexture(){
		nextPlane = nextImage = 0;
		bgImageScroll[0] = 0;
		bgImageScroll[1] = PLANE_DISTANCE;
		bgImageScroll[2] = PLANE_DISTANCE*2;
		bgPlane[nextPlane++].loadTexture(arrTxt[nextImage++]);
		bgPlane[nextPlane++].loadTexture(arrTxt[nextImage++]);
		bgPlane[nextPlane++].loadTexture(arrTxt[nextImage++]);
		nextPlane = 0;
	}
	/************************************************************************************************************************
	 *   METHOD - transforms Background 
	 ************************************************************************************************************************/
	public void transform(float scaleX, float scaleY, float transX, float transY){
		this.scaleX = scaleX;
		this.scaleY = (scaleY*scaleX)/Screen.ASPECT_RATIO;
		this.translateX = transX;
		this.translateY = transY;
	}

	/************************************************************************************************************************
	 *   METHOD - Draws Background 
	 ************************************************************************************************************************/
	public void draw(float scrollX, float scrollY){		
		if( bgImageScroll[0] <= -1 || bgImageScroll[1] <= -1 || bgImageScroll[2] <= -1 ){
			if(bgImageScroll[0] <= -1) bgImageScroll[0] = 0;
			if(bgImageScroll[1] <= -1) bgImageScroll[1] = 0;
			if(bgImageScroll[2] <= -1) bgImageScroll[2] = 0;

			int prevPlane = nextPlane == 2 ? 0: nextPlane +1;

			bgImageScroll[prevPlane] = bgImageScroll[nextPlane == 0 ? 2 : nextPlane-1] - PLANE_DISTANCE ;	// Align, so all three planes have adjoined
			bgImageScroll[nextPlane] = bgImageScroll[nextPlane == 0 ? 2 : nextPlane-1] + PLANE_DISTANCE ;

			bgPlane[nextPlane++].loadAsyncTxture(arrTxt[nextImage++]);
			if(nextPlane > 2){ nextPlane = 0; }
			if(nextImage >= iTotalImages){ nextImage = 0; }
        }
		
		Texture.processAsyncTexture();

		bgImageScroll[0] -= scrollY;
		Draw.transform(scaleX, scaleY, translateX, translateY + bgImageScroll[0]);
		bgPlane[0].draw(0.0f, 0.0f);   		

		bgImageScroll[1] -= scrollY;
		Draw.transform(scaleX, scaleY, translateX, translateY + bgImageScroll[1]);
		bgPlane[1].draw(0.0f, 0.0f);   

		bgImageScroll[2] -= scrollY;
		Draw.transform(scaleX, scaleY, translateX, translateY + bgImageScroll[2]);
		bgPlane[2].draw(0.0f, 0.0f);   
	}

}
