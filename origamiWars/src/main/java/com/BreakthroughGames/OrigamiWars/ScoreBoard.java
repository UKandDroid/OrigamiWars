package com.BreakthroughGames.OrigamiWars;

public class ScoreBoard extends Base
{
protected long iScore; 									// Total score,
private int scoreLength;
private boolean bAnimate;								// flag if increment number , flag for add or subtract
private float NUM_OFFSET;
private long incNumber, incSize;						// number to add, increment incase of incremental add
protected float NUM_GAP = 0.34f;
private float scaleX, scaleY, middlePos;
private int arNumSprite[] = {0,0,0,0,0,0,0,0,0,0,};

protected void reset(){	posT = iScore =  0;	bAnimate = true;	incNumber = incSize = 0;}
protected void setPosition(float vPosX, float vPosY, float vNumGap){ posX = vPosX; posY = vPosY; middlePos = NUM_OFFSET = posY + 0.5f; NUM_GAP =  vNumGap; }

ScoreBoard()
{	
	scaleY = Screen.CHAR_WIDTH /1.4f;
	scaleX = scaleY * Screen.ASPECT_RATIO;
}

/************************************************************************************************************************
*    METHOD --  Called  in every Frame to display the score Board
************************************************************************************************************************/
void display()
{		
	posT++;												
	
	if(bAnimate && ((int)posT%5) == 0)										// if its is incremental, add slowley
		{
	
		if(incNumber == 0)
			bAnimate = false;
		else
			{
			iScore += incSize;
			incNumber -= incSize;
			}
		
		if(iScore < 0)  
			{incNumber = incSize = iScore = 0; bAnimate = false;}
		
		scoreLength = numToSprite(iScore);
		middlePos = NUM_OFFSET -((NUM_GAP*scoreLength)/2);
		SoundPlayer.playSound(Sound.SCORE_ADD, -0.1f);
		}


	switch(scoreLength)
		{
		case 0: 
			transform(scaleX, scaleY, posX, middlePos );
			draw(arNumSprite[0]);
		break;
		case 1: 
			transform(scaleX, scaleY, posX, middlePos);
			draw(arNumSprite[1]);
			translate(0, NUM_GAP);
			draw(arNumSprite[0]);
		break;
		case 2: 
			transform(scaleX, scaleY, posX, middlePos);
			draw(arNumSprite[2]);
			translate(0, NUM_GAP);
			draw(arNumSprite[1]);
			translate(0, NUM_GAP);
			draw(arNumSprite[0]);
		break;
		case 3: 
			transform(scaleX, scaleY, posX, middlePos);
			draw(arNumSprite[3]);
			translate(0, NUM_GAP);
			draw(arNumSprite[2]);
			translate(0, NUM_GAP);
			draw(arNumSprite[1]);
			translate(0, NUM_GAP);
			draw(arNumSprite[0]);
		break;
		case 4: 
			transform(scaleX, scaleY, posX, middlePos);
			draw(arNumSprite[4]);
			translate(0, NUM_GAP);
			draw(arNumSprite[3]);
			translate(0, NUM_GAP);
			draw(arNumSprite[2]);
			translate(0, NUM_GAP);
			draw(arNumSprite[1]);
			translate(0, NUM_GAP);
			draw(arNumSprite[0]);
		break;
		case 5: 
			transform(scaleX, scaleY, posX, middlePos);
			draw(arNumSprite[5]);
			translate(0, NUM_GAP);
			draw(arNumSprite[4]);
			translate(0, NUM_GAP);
			draw(arNumSprite[3]);
			translate(0, NUM_GAP);
			draw(arNumSprite[2]);
			translate(0, NUM_GAP);
			draw(arNumSprite[1]);
			translate(0, NUM_GAP);
			draw(arNumSprite[0]);
		break;
		case 6: 
			transform(scaleX, scaleY, posX, middlePos);
			draw(arNumSprite[6]);
			translate(0, NUM_GAP);
			draw(arNumSprite[5]);
			translate(0, NUM_GAP);
			draw(arNumSprite[4]);
			translate(0, NUM_GAP);
			draw(arNumSprite[3]);
			translate(0, NUM_GAP);
			draw(arNumSprite[2]);
			translate(0, NUM_GAP);
			draw(arNumSprite[1]);
			translate(0, NUM_GAP);
			draw(arNumSprite[0]);
		break;
		case 7: 
			transform(scaleX, scaleY, posX, middlePos);
			draw(arNumSprite[7]);
			translate(0, NUM_GAP);
			draw(arNumSprite[6]);
			translate(0, NUM_GAP);
			draw(arNumSprite[5]);
			translate(0, NUM_GAP);
			draw(arNumSprite[4]);
			translate(0, NUM_GAP);
			draw(arNumSprite[3]);
			translate(0, NUM_GAP);
			draw(arNumSprite[2]);
			translate(0, NUM_GAP);
			draw(arNumSprite[1]);
			translate(0, NUM_GAP);
			draw(arNumSprite[0]);
		break;
		}
}
/************************************************************************************************************************
*    METHOD --  Set and Get Score
************************************************************************************************************************/
protected long getScore() {return iScore;}
protected void setScore(long vScore) 
{
	iScore = vScore;
	scoreLength = numToSprite(iScore);
	middlePos = NUM_OFFSET - ((NUM_GAP*scoreLength)/2);
}

/************************************************************************************************************************
*    METHOD --  convernt digits to correspounding sprites in sprite sheet and returns number length
************************************************************************************************************************/
private int numToSprite(long vNum)
{
	int length = 0;
	for(;vNum > 9; length++)
		{
		arNumSprite[length] =  (int) (vNum%10);
		vNum /= 10;
		}
	arNumSprite[length] = (int) vNum;
	
	return length; 											// return the length of number
}
/************************************************************************************************************************
*    METHOD -- Adds and subtract score to score board, Animation   
************************************************************************************************************************/
protected void add(long vNumber, boolean vbAnimate) 
{	
	if(posT > 4) posT = 0;
	long oddPart;
	bAnimate = true;
	
	incNumber += vNumber;

	if(vbAnimate)
		{
		if(incNumber > 10)
			{
			oddPart = (vNumber%10);							// Remove the odd bit, and add it directly
			iScore += oddPart;
			vNumber -= oddPart;
			incSize = incNumber/10;
			}
		else 
			incSize = (incNumber < 0) ? -1 : 1 ;
		}
	else
		{ incSize = incNumber; posT = 4; }
}

protected void subtract(long vNumber, boolean vbInc) 
{
	if(posT > 4) posT = 0;
	bAnimate = vbInc;
	
	if(vbInc)
		{
		incNumber-= vNumber;
		incSize = (incNumber < 0) ? -1 : 1;
		}
	else
		{
		posT = 4;
		iScore -= vNumber;
		if(iScore < 0) iScore = 0;
		scoreLength = numToSprite(iScore);
		middlePos = NUM_OFFSET -((NUM_GAP*scoreLength)/2);
		}
}

}
