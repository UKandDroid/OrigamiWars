package com.BreakthroughGames.OrigamiWars;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/*---------------------------------PARENT CLASS FOR ALL OBJECTS------------------------------------------*/
public class Draw 
{	

	protected static GL10 gl;
	protected int iSprite = 0;
	protected int iTexture = 0; 
	protected float Opacity = 1.0f;
	private float txtStartX = 0, txtStartY = 0, txtEndX = 0.25f, txtEndY = 0.25f;
	protected static final float[][] arrSpriteCord ={{0.0f,0.0f},	{0.25f,0.0f},	{0.5f,0.0f},	{0.75f,0.0f}, 	  
		{0.0f,0.25f},	{0.25f,0.25f},	{0.5f,0.25f},	{0.75f,0.25f},	 
		{0.0f,0.5f},	{0.25f,0.5f},	{0.50f,0.5f},	{0.75f,0.5f}, 	 
		{0.0f,0.75f},	{0.25f,0.75f},	{0.50f,0.75f},	{0.75f,0.75f},	
		{0.0f,1.0f},	{0.25f,1.0f},	{0.50f,1.0f},	{0.75f,1.0f}, 	
	};

	/************************************************************************************************************************
	 *    METHODS -- 
	 ************************************************************************************************************************/
	
	Draw(){	}

	public void setTextureSize(float startX, float startY, float endX, float endY)	{
		txtStartX = startX;
		txtStartY = startY;
		txtEndX = endX;
		txtEndY = endY;
	}

	public void draw() { draw(arrSpriteCord[iSprite][0], arrSpriteCord[iSprite][1]);}
	public void draw(int vSprite)	{ draw( arrSpriteCord[vSprite][0], arrSpriteCord[vSprite][1]);	} 
	public void draw(int vText, int iSpr)	{ iTexture = vText ; draw(arrSpriteCord[iSpr][0], arrSpriteCord[iSpr][1]);}

	public void draw(float scrollX, float scrollY)
	{	
		GLWrapper.draw(scrollX, scrollY, iTexture, Opacity, txtStartX, txtStartY, txtEndX, txtEndY);

		/*//gl.glMatrixMode(GL10.GL_TEXTURE);
		GLWrapper.matrixMode(GL10.GL_TEXTURE);

		//gl.glLoadIdentity();
		GLWrapper.loadIdentity();
		//gl.glTranslatef(scrollX, scrollY, 0.0f);
		GLWrapper.translate(scrollX, scrollY, 0.0f);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, iTexture);

		//gl.glColor4f(Opacity, Opacity, Opacity, Opacity);
		GLWrapper.color(Opacity, Opacity, Opacity, Opacity);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verBuff);

		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, txtBuff);
		gl.glDrawElements(GL10.GL_TRIANGLES, INDICES_LENGTH, GL10.GL_UNSIGNED_BYTE, indBuff);
		 */
	} 

	public static void transform(float scaleX, float scaleY, float transX, float transY){
		GLWrapper.transform( scaleX,  scaleY,  transX,  transY);
		/*
		GLWrapper.matrixMode(GL10.GL_MODELVIEW);
		//gl.glMatrixMode(GL10.GL_MODELVIEW);

		GLWrapper.loadIdentity();
		//gl.glLoadIdentity();

		GLWrapper.scale(scaleX, scaleY, 1);
		//gl.glScalef(scaleX, scaleY, 1);
		GLWrapper.translate(transX, transY, 0);
		// gl.glTranslatef(transX, transY, 0);

		 */
	}


	public static void translate(float transX, float transY)	{
		GLWrapper.transform( transX,  transY);
		/*
		GLWrapper.matrixMode(GL10.GL_MODELVIEW);
		// gl.glMatrixMode(GL10.GL_MODELVIEW);
		GLWrapper.translate(transX, transY, 0);
		// gl.glTranslatef(transX, transY, 0);

		 */
	}	


	public static void transform(float scaleX, float scaleY, float transX, float transY, float rotateZ)	{
		GLWrapper.transform( scaleX,  scaleY,  transX,  transY, rotateZ);
		/*
		GLWrapper.matrixMode(GL10.GL_MODELVIEW);
		//gl.glMatrixMode(GL10.GL_MODELVIEW);
		GLWrapper.loadIdentity();
		//gl.glLoadIdentity();
		GLWrapper.scale(scaleX, scaleY, 1);
		//gl.glScalef(scaleX, scaleY, 1);
		GLWrapper.translate(transX, transY, 0);
		//gl.glTranslatef(transX, transY, 0);
		GLWrapper.translate(0.5f, 0.5f, 0);
		//gl.glTranslatef(0.5f, 0.5f, 0);
		GLWrapper.rotate(rotateZ, 0f, 0f, 1f);
		//gl.glRotatef(rotateZ, 0f, 0f, 1f);
		GLWrapper.translate(-0.5f, -0.5f, 0);
		//gl.glTranslatef(-0.5f, -0.5f, 0);
		 * 
		 */
	}




	/************************************************************************************************************************
	 *    METHODS -- Initialise Buffer Methods 
	 ************************************************************************************************************************/

	public float easeInOut(float vValue, float vCounter)
	{
		float temHalf = vValue/2;
		float fVelocity = 1 / temHalf; 
		float vTime = temHalf - Math.abs(temHalf - vCounter);
		return (float) ((double)(0.5f*(fVelocity) * vTime ) + (0.5 * (fVelocity*fVelocity * vTime*vTime )));
	}

	public void setTransparency(float vTrans) {	Opacity = vTrans;	}

	/************************************************************************************************************************
	 *	CLASS-END
	 ************************************************************************************************************************/
}
