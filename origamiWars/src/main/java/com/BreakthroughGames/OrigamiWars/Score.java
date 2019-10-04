package com.BreakthroughGames.OrigamiWars;

import android.util.Log;

public class Score extends Base
{
private long score; 									// Total score,
private long incNumber, incSize;						// number to add, increment incase of incremental add
private boolean bIncrement;								// flag if increment number , flag for add or subtract
private float NUM_OFFSET;
private float NUM_GAP = 0.34f;
private float scaleX, scaleY, middlePos;
private short arrNumSprite[] = {0,0,0,0,0,0,0,0,0,0,};

protected long getScore() {return score;}
protected void setScore(long vScore) {score = vScore;}
protected void reset(){	posT = score =  0;	bIncrement = false;	incNumber = incSize = 0;}
protected void setPosition(float vPosX, float vPosY) {posX = vPosX; posY = vPosY; NUM_OFFSET = posY + 0.5f;}

Score()
{	
	scaleY = Screen.CHAR_WIDTH /1.4f;
	scaleX = scaleY * Screen.ASPECT_RATIO;
}

void display()
{		
	int xPosIndex = 0, length;

	if(bIncrement)										// if its is incremental, add slowly
		{
		score += incSize;
		incNumber -= incSize;
		
		if((incSize >= 0 && incNumber <= 0)||(incSize <= 0 && incNumber >= 0))
			{
			incNumber = 0;
			bIncrement = false;
			}
		}

	if(score <= 0) score = 0;
	length = numToSprite(score);
	middlePos = NUM_OFFSET -((NUM_GAP*length)/2);

	for(int i = length; i >= 0; i--)
		{
		transform(scaleX, scaleY,posX, middlePos +(NUM_GAP*xPosIndex++) );
		draw(iTexture, arrNumSprite[i]);
		}
	posT++;												// Used for incremental additions
}

private int numToSprite(long vNum)
{
	int i = 0;
	for(; vNum > 9; i++)
		{
		arrNumSprite[i] = (short) (vNum%10);
		vNum /= 10;
		}
	arrNumSprite[i]= (short) vNum;
	return i; 											// return the length of number
}

protected void add(long vNumber, boolean vbInc) 
{	
	long odd = (vNumber%10);							// Remove the odd bit, and add it directly
	score += odd;
	vNumber -= odd;
	
	if(vbInc)
		{
		bIncrement = true;
		incNumber += vNumber;
		incSize = incNumber/10;
		}
	else
		score += vNumber;
}

protected void subtract(long vNumber, boolean vbInc) 
{
	if(vbInc)
		{
		incNumber-= vNumber;
		incSize = incNumber /10;
		bIncrement = true;
		Log.d("score", "IncNumber= "+ Long.toString(incNumber)+" incSize= "+Long.toString(incSize));
		}
	else
		score -= vNumber;
}

}
