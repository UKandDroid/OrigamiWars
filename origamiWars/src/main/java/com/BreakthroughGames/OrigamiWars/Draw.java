package com.BreakthroughGames.OrigamiWars;

import javax.microedition.khronos.opengles.GL10;

/*---------------------------------PARENT CLASS FOR ALL OBJECTS------------------------------------------*/
public class Draw {

    protected static GL10 gl;
    protected int iSprite = 0;
    public int iTexture = 0;
    protected float Opacity = 1.0f;
    private float txtStartX = 0, txtStartY = 0, txtEndX = 0.25f, txtEndY = 0.25f;
    protected static final float[][] arrSpriteCord = {{0.0f, 0.0f}, {0.25f, 0.0f}, {0.5f, 0.0f}, {0.75f, 0.0f},
            {0.0f, 0.25f}, {0.25f, 0.25f}, {0.5f, 0.25f}, {0.75f, 0.25f},
            {0.0f, 0.5f}, {0.25f, 0.5f}, {0.50f, 0.5f}, {0.75f, 0.5f},
            {0.0f, 0.75f}, {0.25f, 0.75f}, {0.50f, 0.75f}, {0.75f, 0.75f},
            {0.0f, 1.0f}, {0.25f, 1.0f}, {0.50f, 1.0f}, {0.75f, 1.0f},
    };

    Draw() { }

    public void setTextureSize(float startX, float startY, float endX, float endY) {
        txtStartX = startX;
        txtStartY = startY;
        txtEndX = endX;
        txtEndY = endY;
    }

    public void draw() {
        draw(arrSpriteCord[iSprite][0], arrSpriteCord[iSprite][1]);
    }

    public void draw(int vSprite) {
        draw(arrSpriteCord[vSprite][0], arrSpriteCord[vSprite][1]);
    }

    public void draw(int vText, int iSpr) {
        iTexture = vText;
        draw(arrSpriteCord[iSpr][0], arrSpriteCord[iSpr][1]);
    }

    public void draw(float scrollX, float scrollY) {
        GLWrapper.draw(scrollX, scrollY, iTexture, Opacity, txtStartX, txtStartY, txtEndX, txtEndY);
    }

    public static void transform(float scaleX, float scaleY, float transX, float transY) {
        GLWrapper.transform(scaleX, scaleY, transX, transY);
    }

    public static void translate(float transX, float transY) {
        GLWrapper.transform(transX, transY);
    }

    public static void transform(float scaleX, float scaleY, float transX, float transY, float rotateZ) {
        GLWrapper.transform(scaleX, scaleY, transX, transY, rotateZ);
    }

    public void setTransparency(float vTrans) {
        Opacity = vTrans;
    }

}
