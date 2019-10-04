package com.BreakthroughGames.OrigamiWars;

import java.util.Random;

public class Base extends Draw 
{	
	
	protected static final int IS_ENABLE = 1;
	protected static final int IS_ACTIVE = 2;
	protected static final int IS_RUN_ONCE = 4;
	protected static final int IS_FALLING = 8;
	protected static final int IS_OBSTRUCT = 16;
	protected static final int IS_FRIENDLY = 32;
	protected static final int IS_FIRES = 64;
	protected static final int IS_LOCKED = 128;
	protected static final int IS_ONE_TRIP = 256;
	protected static final int IS_FLASH = 512;
	
	private int iFlags = 0;													// Variable to hold flags, like enable, active, fire

	protected int ID = 0;													// Every object has different id as its a counter of objects created
	protected float posX = 0f;
	protected float posY = 0f; 												//Position of Object 
	protected float posT = 0f;												// variable used to keep track of time for certain actions
	protected float speed = 0f;
	protected float distance = 0.0f; 										// How far is the Object so its displayed at appropriate time
	protected static int counterID = 0;
	protected boolean bEnable = false;
	protected int iType = 0, iPath = 0; 
	protected final static  Random ran = new Random();	 
	protected static Draw oSprtie = new Draw();								// For default sprite size use this Object
	protected float var1 = 0f, var2 = 0f, var3 = 0; 						// Different variables used for path calculations
	protected float offsetX = 0.0f, offsetY = 0.0f;							// Empty space Around X,Y of character
	
	protected float getLeft()			{ return posY + offsetY; }
	protected float getRight()			{ return posY + (1.0f - offsetY); }
	protected float getTop()			{ return posX + offsetX; }
	protected float getBottom()			{ return posX + 1.0f - offsetX; }
	protected float getWidth()			{ return 1.0f - (2*offsetY); }
	protected float getHeight()			{ return 1.0f - (2*offsetX); }
	protected String getName()			{ return Values.ARR_NameFromID[iType]; }

	public Base(){ ID = counterID++; }
//	public Base(boolean bDraw){ super(false); ID = counterID++; }
	public void transform()	{ transform(Screen.CHAR_HEIGHT, Screen.CHAR_WIDTH, posX, posY);	}
	public void transform(float angle)	{ transform(Screen.CHAR_HEIGHT, Screen.CHAR_WIDTH, posX, posY, angle);	}
/************************************************************************************************************************
*	METHOD -- Detects overlap
************************************************************************************************************************/
	protected boolean detectCollision(Base obj)
	{
		if( ! (obj.getLeft() > getRight() || obj.getRight() < getLeft() || obj.getTop() > getBottom() || obj.getBottom() <getTop()))
			return true;
		return false;
	}

	protected boolean detectCollision(Obstacle obj)
	{
		if( ! (obj.getLeft() > getRight() || obj.getRight() < getLeft() || obj.getTop() > getBottom() || obj.getBottom() < getTop()))
			return true;
		return false;
	}

	protected boolean detectCollision(Base obj, float vOffsetX, float vOffsetY)
	{
		if( ! (obj.getLeft()+vOffsetY > getRight() || obj.getRight()+vOffsetY < getLeft() || obj.getTop()+vOffsetX > getBottom() || obj.getBottom()+vOffsetX <getTop()))
			return true;
		return false;
	}
	
	protected boolean detectExtdCollision(Base obj, float vOffsetX, float vOffsetY)		// Detect Overlap excluding Object itself
	{
		if(vOffsetY < 0)
			{
			if( ! (obj.getLeft() > getLeft() || obj.getRight() < getLeft()+vOffsetY || obj.getTop() > getBottom() || obj.getBottom() < getTop()))
			return true;
			}
		else if( ! (obj.getLeft() > getRight() || obj.getRight() < getRight()+vOffsetY || obj.getTop() > getBottom() || obj.getBottom() < getTop()))
			return true;

			
		return false;
	}

	/************************************************************************************************************************
	*	Returns true of false for certain property
	************************************************************************************************************************/
		protected boolean status(int vProperty)
		{
			return ((vProperty & iFlags) == vProperty);
		}
	/************************************************************************************************************************
	*	Sets a property true of false
	************************************************************************************************************************/
		protected void status(int vProperty, boolean vSet)
		{
			iFlags = vSet ? (iFlags|vProperty) : (~vProperty&iFlags);
		}

	/************************************************************************************************************************
*	CLASS-END
************************************************************************************************************************/
}
