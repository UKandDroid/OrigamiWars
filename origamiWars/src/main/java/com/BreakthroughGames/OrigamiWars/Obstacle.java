package com.BreakthroughGames.OrigamiWars;


public class Obstacle {
	protected static final int OLAP_X     = 0;								// Collision parameters
	protected static final int OLAP_Y     = 1;
	protected static final int CLOUD_TYPE = 2;
	protected static final int POSITION   = 3;
	protected static final int POSITION_Y = 4;
	protected static final int OLAP_ABS_X = 5;								
	protected static final int OLAP_ABS_Y = 6;								// Absoloute overlap, if between two clouds Sum of Ovelap, always positive
	protected static final int OLAP_DIFF_X = 7;								
	protected static final int OLAP_DIFF_Y = 8;								// Differece overlap, if between two clouds Differece of overlap of two 
	
	protected static final int CLOUD_UP     = 1;							// Cloud position in collision
	protected static final int CLOUD_DOWN   = 2;
	protected static final int CLOUD_MIDDLE = 3;
	protected static final int CLOUD_RIGHT  = 4;
	protected static final int CLOUD_LEFT   = 5;
	

	public static final int WHITE_CLOUDS = 0;
	public static final int DARK_CLOUDS  = 1;
	public static final int THUNDER_CLOUDS = 2;
	
	public static final int POSITION_TOP    = 0;							// Could be anywhere on X-Axis
	public static final int POSITION_MIDDLE = 1;							// low gravity means 1/3 up the screen
	
	private float index = 0;												// Index of block to move in case of wobble and sine
	private static float oldOffset =0;
	protected Cloud[] arClouds;
	private float cloudsWidth, cloudsHeight, endGap;						// Width/Height of Whole Obstacle and gap for next obstacle
	private Cloud cloudTop, cloudBottom, cloudLeft, cloudRight;
	protected int iType = 0, iPosition = 0, iDifficulty = 0, length = 0, startIndex = 0;	// Obstacle type, dark white or thunder clouds // Position area for the Obstacle	

		
/************************************************************************************************************************
 *   Class --  Cloud
***********************************************************************************************************************/
	protected class Cloud extends Base {
		private float windDir = 0;
		private float mVelX = 0.02f; 
		private float mVelY = 0.06f;
		private boolean bWave = true; 										// Move the blocks one after another
		private float windSpeedX = 0, windSpeedY = 0;
		
		Cloud(float vPosX, float vPosY) {
			bWave = true;
			posX = vPosX;
			posY = vPosY; 
			offsetY = 0.15f; 
			offsetX = 0.25f;
			initialize();
		}
		
	public void draw() { oSprite.draw(iTexture, iSprite); }
/************************************************************************************************************************
*	METHOD -- Resets Object
************************************************************************************************************************/
	void initialize() {
		bEnable = true; 
		switch(iType) {
			case Obstacle.WHITE_CLOUDS:		iTexture = Game.txtObst.iTexture; iSprite = Game.ran.nextInt(3); 	windSpeedX = 0.012f; windSpeedY = 0.032f; break;
			case Obstacle.DARK_CLOUDS: 		iTexture = Game.txtObst.iTexture; iSprite = Game.ran.nextInt(3)+3; 	windSpeedX = 0.007f; windSpeedY = 0.02f;  break;
			case Obstacle.THUNDER_CLOUDS: 	iTexture = Game.txtObst.iTexture; iSprite = Game.ran.nextInt(3)+6; 	windSpeedX = 0.003f; windSpeedY = 0.01f;  break;
			}
		
		switch(iPath) {
			case Values.PATH_STRAIGHT_SLOW: speed = Values.SPEED3;		break;
			case Values.PATH_STRAIGHT_FAST: speed = Values.SPEED5;		break;
			case Values.PATH_SINE_WAVE:
				posT = Values.SPEED1; 										// Wave frequency 
				speed = Values.SPEED1*1.5f;									// Y-axis speed
				var3 = 0.2f;												// Wave magnification factor
			break;
			}
	}
/************************************************************************************************************************
*	METHOD -- Detects overlap and returns overlapping X-Y Co-ordinates
************************************************************************************************************************/
	protected boolean detectOverlap(Base obj, float arrResult[]) {
		if(bEnable && detectCollision(obj)) {
			float x1 = Math.abs(obj.getTop() 	- getBottom());
			float x2 = Math.abs(obj.getBottom() - getTop());
			float y1 = Math.abs(obj.getLeft() 	- getRight());
			float y2 = Math.abs(obj.getRight() 	- getLeft());
			float xOlap =  x1 < x2 ? x1 : x2;
			float yOlap =  y1 < y2 ? y1 : y2;
			xOlap /= (1.0 - (2 * offsetX)); 
			yOlap /= (1.0 - (2 * offsetY)); 
			
			xOlap = Values.clamp(xOlap, 0.005f, 1f);
			yOlap = Values.clamp(yOlap, 0.005f, 1f);
		
			arrResult[CLOUD_TYPE] = iType;
			arrResult[OLAP_X] = xOlap ;
			arrResult[OLAP_Y] = yOlap ;
			arrResult[POSITION] = (obj.posX > posX) ? CLOUD_UP : CLOUD_DOWN ;
			arrResult[POSITION_Y] = (obj.posY > posY) ? CLOUD_LEFT : CLOUD_RIGHT ;
			return true;
			}
		return false;
	}
/************************************************************************************************************************
*	METHOD -- Calculates Path
************************************************************************************************************************/
	void calculatePath(boolean vbMove) {
		if( Mic.bBlowing)
			calcWind();
		else switch(iPath){													// switch on attack type
			case Values.PATH_STRAIGHT_SLOW:
			case Values.PATH_STRAIGHT_FAST: 	pathStationary();		break;
			case Values.PATH_SINE_WAVE:			if(vbMove) calcSine();	break;
			}	
	}
/************************************************************************************************************************
*	METHOD -- Overridden Stationary path Method
************************************************************************************************************************/
	protected void pathStationary() {
		if(posY < -1)	
			bEnable = false;
		else
			posY -= speed*Game.SPEED_MULT;
	}
/************************************************************************************************************************
*	METHOD -- Overridden Stationary path Method
************************************************************************************************************************/
	protected void pathAttack() {
		if(posY < -1)	
			bEnable = false;
		else
			posY -= speed*Game.SPEED_MULT;
	}

/************************************************************************************************************************
*	METHOD -- Overridden calculate  Sine
************************************************************************************************************************/
	protected void calcSine() {
		if(posY < -1)
			 bEnable = false;												// Object should run once across the screen then gets disabled
		else
			posY -= posT*Game.SPEED_MULT;
		
			if(bWave){ var1 = posX; var2 = 0; bWave = false;}				// Var1 contains Wave oscillation center position
			posX = (float) java.lang.Math.sin(var2);						// posX assigned Sin Value, Var1 contains PosX value	
			posX *= var3;													// wavMagnify = Magnification factor
			posX = (posX*Game.SPEED_MULT) + var1 ;							// lockOnPosX = X- axis offset
			var2 += speed;													// posT = Wave frequency
	}
/************************************************************************************************************************
*	METHOD -- Overridden  Wind method
************************************************************************************************************************/	
	private void calcWind() {
		float curX, curY;
		bWave = true;														// after wind has changed positions, set new wave oscillation point for sine wave

		windDir += (Mic.eWindStatus == Mic.WIND_BLOWING && ran.nextFloat() > 0.25f) ? 0.07 : -0.07f;
		windDir = Values.clamp(windDir, -1, 1);

		if(posY > Screen.DEV_MAX_Y+1)
			posY += (mVelY*Game.SPEED_MULT*windDir);
		else 
			{
			if((posX > (Screen.DEV_MAX_X -1)) || posX < 0)
				mVelX *=-1;
		
			posX += (mVelX*Game.SPEED_MULT*windDir);
			posY += (speed*Game.SPEED_MULT*windDir);
			
			curX = (((ran.nextFloat()*2.5f) - 1.3f) / 1000) * 6 ;
			curY = (((ran.nextFloat()*3.5f) - 1.0f) / 1000) * 6;
			mVelX += (Math.abs(curX) > 0.0015) ? curX/2 : curX;				// Smooth, jerks in object motion
			mVelY += (Math.abs(curY) > 0.0015) ? curY/2 : curY;
			
			mVelX  = Values.clamp(mVelX, -windSpeedX, windSpeedX);
			mVelY  = Values.clamp(mVelY, -windSpeedY/4, windSpeedY);
			}				
	}
}
/************************************************************************************************************************
 *   End Class --  Cloud
***********************************************************************************************************************/	
	Obstacle(){}
	protected float getLeft()   	{ return cloudLeft.getLeft(); }
	protected float getRight()   	{ return cloudRight.getRight(); }
	protected float getTop()		{ return cloudTop.getTop(); }
	protected float getBottom() 	{ return cloudBottom.getBottom(); }
	protected float getWidth()      { return cloudsWidth;}
	protected float getHeight()		{ return cloudsHeight; }
	protected float getRightGap()	{ return cloudRight.getRight() + endGap ; }	// Returns right end of an Obstacle	
/************************************************************************************************************************
 *   METHOD- Set Obstacle properties
***********************************************************************************************************************/
	protected void  setProperties(int vDifficulty, float vEndGap, int vPosition) 
	{ 
		iDifficulty = vDifficulty; 
		endGap 		= vEndGap; 
		iPosition 	= vPosition;
	}
/************************************************************************************************************************
 *   METHOD- Set Every Block in the Obstacle
***********************************************************************************************************************/
	protected void addBlocks(final float arr[][]) {
		Cloud cloud;
		length = arr.length;												// Size of Obstacle in num of clouds
		arClouds = new Cloud[length];
		arClouds[0] = new Cloud((arr[0][0])/Screen.ASPECT_RATIO, arr[0][1]);
		cloudTop = cloudBottom = cloudLeft = cloudRight = arClouds[0];
	
		for(int i = 1 ; i < length; i++)
			{			
			cloud = arClouds[i] = new Cloud((arr[i][0])/Screen.ASPECT_RATIO, arr[i][1]);
			if(cloud.getLeft()   < cloudLeft.getLeft() )    cloudLeft 	= cloud;   
			if(cloud.getRight()  > cloudRight.getRight())   cloudRight 	= cloud;  
			if(cloud.getTop()    < cloudTop.getTop())   	cloudTop 	= cloud;    
			if(cloud.getBottom() > cloudBottom.getBottom()) cloudBottom = cloud; 
			}
		cloudsWidth  = cloudRight.getRight()   - cloudLeft.getLeft();
		cloudsHeight = cloudBottom.getBottom() - cloudTop.getTop();
	}
/************************************************************************************************************************
 *   METHOD- Move Obstacle and draws them
***********************************************************************************************************************/
	protected boolean move() {
		Cloud cloud = arClouds[0];
		if(cloud.iPath == Values.PATH_STRAIGHT_SLOW||cloud.iPath == Values.PATH_STRAIGHT_FAST)							// If path is stationary then move all blocks every time
			index = length;						
		else																// Else move one block then 2nd then 3rd			
			index += 0.1;
			
		for(int i = 0; i < length; i++)
			if((cloud= arClouds[i]).bEnable)
				{
				cloud.calculatePath(i < index);
				if(cloud.bEnable)											// If cloud hasnt moved out of the screen left
					{
					if(cloud.posY < Screen.DEV_MAX_Y)
						{
						cloud.transform();
						cloud.draw();
						}}
				else 
					startIndex++;
				}
		
		return startIndex == length;										// Return true when all blocks move out of the screen
	}

/************************************************************************************************************************
 *   METHOD- Copies, Enables Obstacle, and sets Obstacle Position
***********************************************************************************************************************/
	protected Obstacle setObstacle( float offsetY, int vPath, int vObstType) {
		Cloud cloud;
		Obstacle temp = new Obstacle();
		float offsetX = 0, vMax = 0, vMin = 0, posDiff = 0;		
		
		switch(iPosition)
			{
			case POSITION_TOP:		offsetX = -0.2f;	break;				// Clouds offset, so they stick at the top of screen
			case POSITION_MIDDLE:
				vMin = 0.5f;
				vMax = (Screen.DEV_MAX_X - 3) - cloudsHeight;				// Keep Clouds up, Not too close the ground
				offsetX = vMin + (Game.ran.nextFloat() * (vMax-vMin));
				posDiff = Math.abs(oldOffset - offsetX);
				if(posDiff < 1)												// Make sure clouds are distributed
					{
					if(oldOffset < offsetX) offsetX += 1 - posDiff;
					else offsetX -= 1 - posDiff;
					}
				offsetX = Values.clamp(offsetX, -0.2f, Screen.DEV_MAX_X - 0.5f);
				oldOffset = offsetX;
			break;															
			}
	
		temp.startIndex = 0;
		temp.endGap = endGap;
		temp.length = length;
		temp.iType = vObstType;
		temp.iPosition = iPosition;
		temp.iDifficulty = iDifficulty;
		temp.cloudsWidth = cloudsWidth;
		temp.cloudsHeight = cloudsHeight;
		temp.arClouds = new Cloud[length];
		
		cloud = new Cloud(arClouds[0].posX+offsetX, arClouds[0].posY+offsetY);
		cloud.iType = vObstType;											// Type Dark/White/Thunder Clouds
		cloud.iPath = vPath;												// Sine/Stationary
		cloud.initialize();													// Set variable for Path and type
		temp.arClouds[0] = cloud;
		temp.cloudLeft = temp.cloudRight = temp.cloudTop = temp.cloudBottom = cloud;
		
		for(int i = 1; i < length ; i++)
			{
			cloud = temp.arClouds[i] = new Cloud(arClouds[i].posX+offsetX, arClouds[i].posY+offsetY);
			cloud.iType = vObstType;										// Type Dark/White/Thunder Clouds
			cloud.iPath = vPath;											// Sine/Stationary
			cloud.initialize();												// Set variable for Path and type

			if(cloud.getLeft()   <  temp.cloudLeft.getLeft() )    temp.cloudLeft   = cloud;   
			if(cloud.getRight()  >  temp.cloudRight.getRight())   temp.cloudRight  = cloud;  
			if(cloud.getTop()    <  temp.cloudTop.getTop())   	  temp.cloudTop    = cloud;    
			if(cloud.getBottom() >  temp.cloudBottom.getBottom()) temp.cloudBottom = cloud; 
			}
			
		return temp;
	}

}
