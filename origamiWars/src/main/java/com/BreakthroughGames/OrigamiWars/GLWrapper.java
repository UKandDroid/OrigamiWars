package com.BreakthroughGames.OrigamiWars;

public class GLWrapper {
	static{
		System.loadLibrary("game");
	}
public static native void testMethod();
public static native void pushMatrix();
public static native void loadIdentity();
public static native void translate(float x, float y, float z);
public static native void scale(float x, float y, float z);
public static native void matrixMode(int mode);
public static native void color(float r, float g, float b, float alpha);
public static native void rotate(float angle, float x, float y, float z);
public static native void transform( float transX, float transY);
public static native void transform( float scaleX, float scaleY, float transX, float transY);
public static native void transform( float scaleX, float scaleY, float transX, float transY, float rotateZ);
public static native void draw( float transX, float transY, int iTexture, float opacity, float txtStartX, float txtStartY, float txtEndX, float txtEndY);


}
