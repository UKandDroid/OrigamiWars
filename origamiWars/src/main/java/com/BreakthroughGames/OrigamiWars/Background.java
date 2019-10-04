package com.BreakthroughGames.OrigamiWars;

import javax.microedition.khronos.opengles.GL10;

public class Background extends Draw{

	public float scrollY = 0f; 															//Position of Object
	protected float posX = 0;
	public Background() { super.setTextureSize(0, 0, 1, 1);}						//for backgrounds full texture is displayed, not part of it like sprites
	public Background(float startX, float endX, float startY, float endY) { super.setTextureSize(startX, startY, endX, endY);} //for backgrounds full texture is displayed, not part of it like sprites
	public void loadTexture(int resTexture, int repeatOrClamp) { iSprite = 0; iTexture = Texture.getTxtId(resTexture, repeatOrClamp ); }
	public void loadTexture(int resTexture) { iSprite = 0; iTexture = Texture.getTxtId(resTexture, GL10.GL_CLAMP_TO_EDGE ); }
	protected void loadAsyncTxture(final int resTexture){ iSprite = 0; iTexture = 0; Texture.loadAsyncTexture(resTexture, this);

	}
}//End Class
