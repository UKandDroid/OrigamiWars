package com.BreakthroughGames.OrigamiWars;

public class Record 
{
	protected static final int NEW    = 0;										// Sets flag for a new Record
	protected static final int SHOW   = 1;										// Sets flag to show a Record
	protected static final int POST   = 2;										// Sets flag to post Record on FaceBook
		
	protected static final int ARCADE_SCORE    = 0;
	protected static final int ARCADE_DISTANCE = 1;
	protected static final int ARCADE_CURSES   = 2;
	protected static final int ADVENT_SCORE    = 4;
	protected static final int ADVENT_DISTANCE = 5;
	protected static final int ADVENT_CURSES   = 6;
	
	protected static boolean ARR_Params[][] = new boolean[6][3];				// Shows Record Parameters
	protected static int ARR_Value[] = new int[6];								// Record Value
	
/************************************************************************************************************************
*    GET / SET Methods for Records -  To check Record Properties and Value
************************************************************************************************************************/	
	protected static void setTrue(int vRecord, int vPoperty)
	{
		ARR_Params[vRecord][vPoperty] = true;
	}

	protected static void setFalse(int vRecord, int vPoperty)
	{
		ARR_Params[vRecord][vPoperty] = false;
	}

	protected static boolean get(int vRecord, int vPoperty)						// Returns Records Property
	{
		return ARR_Params[vRecord][vPoperty]; 
	}
		
	protected static int get(int vRecord)										// Returns Record Value
	{
		return ARR_Value[vRecord]; 
	}

	protected static void set(int vRecord, int vValue)
	{
		ARR_Value[vRecord] = vValue;
	}	
/************************************************************************************************************************
*    METHOD -- Check Arcade Mode Records
************************************************************************************************************************/
	protected static void arcadeCheck()											// Checks arcade Records
	{
		if(Game.scoreBoard.iScore > ARR_Value[ARCADE_SCORE])					// Check highest Score
			{ 
			ARR_Value[ARCADE_SCORE] =  (int) Arcade.scoreHighest.iScore;
			if(ARR_Params[ARCADE_SCORE][SHOW])												
				{
				Game.events.dispatch(Events.HIGHEST_SCORE); 
				ARR_Params[ARCADE_SCORE][SHOW] = false;
				ARR_Params[ARCADE_SCORE][POST] = true;
				} 
			}
					
		if((int)Game.odoMeter > ARR_Value[ARCADE_DISTANCE])							// Checks Distance Record
			{
			ARR_Value[ARCADE_DISTANCE] = (int) Game.odoMeter; 
			if(ARR_Params[ARCADE_DISTANCE][SHOW])
				{
				Game.events.dispatch(Events.MAX_DISTANCE); 
				ARR_Params[ARCADE_DISTANCE][SHOW] = false;
				ARR_Params[ARCADE_DISTANCE][POST] = true;
				} 
			}
					
		if(PowerUps.iLevelWins > ARR_Value[ARCADE_CURSES])							// Check curses record
			{ 
			ARR_Value[ARCADE_CURSES] = PowerUps.iLevelWins ;
			if(ARR_Params[ARCADE_CURSES][SHOW])
				{
				Game.events.dispatch(Events.HIGHEST_WINS); 
				ARR_Params[ARCADE_CURSES][SHOW] = false;
				ARR_Params[ARCADE_CURSES][POST] = true;
				} 
			}
	}


}
