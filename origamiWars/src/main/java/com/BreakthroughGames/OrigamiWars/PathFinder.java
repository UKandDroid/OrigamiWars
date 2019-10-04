package com.BreakthroughGames.OrigamiWars;


public class PathFinder 
{
	private static final int FRONT = 0;
	private static final int FRONT2 = 1;
	private static final int UP = 2;
	private static final int DOWN = 3;
	private static final int ERROR       = -1;
	private static final int TOP_LEFT    = 1;
	private static final int TOP_RIGHT   = 2;
	private static final int BOTTOM_LEFT = 3;
	private static final int BOTTOM_RIGHT= 4;
	private static final int TOP    = 5;
	private static final int BOTTOM = 6;
	private static final int LEFT   = 7;
	private static final int RIGHT  = 8;
	//private static Obstacle arClouds[];
	
	private static final float MAX_VALUE = Screen.DEV_MAX_X+1;

	private static int arrSlope[] = {-1};																// used to get data back from a method
	private static int countUp = 0, countDown = 0;
	private static int loopCounter =  (int) Screen.DEV_MAX_X ; 						
	private static float charHeight, charHeight80, charWidth80;  
	
	private static boolean bDownOpen = true, bUpOpen = true;											// no of Blocks above/below the current, 
	private static float deltaX = 0, speed, deltaUp = MAX_VALUE, deltaDown = MAX_VALUE, distBtw = MAX_VALUE;
	private static float tSlope = Float.MAX_VALUE, farSlope = Float.MAX_VALUE, nearSlope = Float.MAX_VALUE;

	private static Base extObj = new Base();
	private static Base arrResult[] = {null, null, null, null};											// Keep track of obstacles that are already detected, so they are detected again
	private static Base upObj = null, downObj = null, frontObj = null, nextUpObj = null, nextDownObj = null, curObj = null;
	 
	
/************************************************************************************************************************
******DONOT TOUCH THIS METHOD*****	Finds path for object, checking all obstractions infront, up and below 
************************************************************************************************************************/
	protected static void calculate(Obstacle vClouds[], Object vChar,  float oldX,  float oldY)
	{
	
		float charWidth  = 1.0f - (2*vChar.offsetY);
		arrResult[0] = arrResult[1] = arrResult[2] = arrResult[3] = null; 
		
		if(vChar.posY > -0.5f && vChar.posY < Screen.DEV_MAX_Y )										 
			{
			detectCollision(vChar, vClouds, oldX, oldY);												// Adjust object for any collisions
			if(!Mic.bBlowing)																		
				{
				if( objExtOlap(vChar, vClouds, 0, -1.5f , 0, -charWidth, arrResult, FRONT ) > 0)	    // Check 1.5 units ahead for nearest object in the path	
					objExtOlap(vChar, vClouds, 0, -1.5f , 0, -charWidth, arrResult, FRONT2);			// Check again incase of more then one Objects ahead
				else 
					return;
				}			

			arrSlope[0] = -1;																			// Used to get data back from a method
			speed = oldY - vChar.posY;
			bDownOpen = bUpOpen = true;
			deltaX = countUp = countDown =  0;															// No of Blocks above/below the current, 
			charHeight = 1.0f - (2*vChar.offsetX);
			charHeight80 = 80.0f*(charHeight/100.0f);													// Object 60% height
			charWidth80  = 80.0f*(charWidth/100.0f);
			deltaUp = deltaDown = distBtw = MAX_VALUE;
			tSlope = farSlope = nearSlope = Float.MAX_VALUE;
			upObj =  downObj = frontObj = nextUpObj = nextDownObj = curObj = null;
			

/*CHECK FOR FRONT OBJECT*/			
			for (int i = 0; i < 2 && arrResult[i] != null; i++ )										
				{
				curObj  = arrResult[i];
				distBtw = getGap(curObj, vChar);
				tSlope  = getSlope(curObj, vChar, arrSlope);

				if(distBtw != 0)																		// if Front Object is far 
					{
					if((Math.abs(tSlope) < 3) && (tSlope < farSlope) && (nearSlope == Float.MAX_VALUE)) // If there hasnt been a near object. i.e near slope is still maximum 
						{ 
						frontObj = curObj; 
						farSlope = tSlope; 
						arrResult[FRONT] = arrResult[i];
						}
					}
				else if((Math.abs(tSlope) < 3) && (curObj.posY < vChar.posY) && (tSlope < nearSlope))	// if Front object is colliding, check for if object is not behind, and OlapX > OlapY 
						{
						frontObj = curObj; 
						nearSlope = tSlope; 
						arrResult[FRONT] = arrResult[i];
						}
				}

	
/*FIND EMPTY SPACE UP & DOWN */			
			if(frontObj != null && !Mic.bBlowing)														// **CHECK ABOVE AND BELOW, if there is object at the front**															
				{
				downObj = upObj = frontObj;

				for(int i= 1; i < 4; i++ )arrResult[i] = null; 		

				do{																						
					loopCounter--;																		
					countUp = countDown = 0;	
					
					if(bUpOpen)    countUp   = objExtOlap( upObj,   vClouds,  -charHeight80, 0, -charHeight, charWidth80, arrResult, UP);		// Check Above and beyound the Obstacle
					if(bDownOpen)  countDown = objExtOlap( downObj, vClouds,   charHeight80, 0,  charHeight, charWidth80, arrResult, DOWN); 	// Check Below and beyound the Obstacle
					
					if(countUp == 1)  		
						{ 
						nextUpObj = arrResult[UP]; 
						tSlope = getSlope(nextUpObj, vChar, arrSlope); 
						if(arrSlope[0] == BOTTOM_LEFT || arrSlope[0] == BOTTOM_RIGHT) { nextUpObj= null; countUp = 0;}

						} 
					else if(countUp > 1) 	bUpOpen = false;											// if there are more then one objects above, conscider that path close
	
					if(countDown == 1) 		
						{ 
						nextDownObj = arrResult[DOWN];
						tSlope = getSlope(nextDownObj, vChar, arrSlope); 
						if(arrSlope[0] == TOP_LEFT || arrSlope[0] == TOP_RIGHT){ nextDownObj= null; countDown = 0; } 

						} 
					else if(countDown > 1) 	bDownOpen = false;
					
					if(bUpOpen && upObj.getTop() - charHeight80 < 0 )	bUpOpen = false;				// Check if up empty space is with in the screen boundary
					if(bDownOpen && downObj.getBottom() + charHeight80 > Screen.DEV_MAX_X - 0.5) bDownOpen = false;	// If it goes outside the screen change direction
					
					if(nextUpObj != null) 	upObj = nextUpObj;
					if(nextDownObj != null) downObj = nextDownObj;
				}while(countDown != 0 && countUp != 0 && (bUpOpen || bDownOpen) && loopCounter > 0 );
		
				
/*FINAL CHECKING AND ADDING UP*/				
				if((bUpOpen && !bDownOpen)||(bUpOpen && countUp==0))   	 deltaUp  = -Math.abs(vChar.getBottom() - upObj.getTop()  );
				if((bDownOpen && !bUpOpen)||(bDownOpen && countDown==0)) deltaDown = Math.abs(vChar.getTop() - downObj.getBottom());
				
				if(Math.abs(deltaUp) < Math.abs(deltaDown))	deltaX = deltaUp;		
				else										deltaX = deltaDown;
				
				if(Math.abs(deltaX) < 0.02)	deltaX *= 0.2/Math.abs(deltaX);
				if(bUpOpen || bDownOpen)																// If up or down are open
						{
						vChar.bObstruct = true;	
						vChar.iObstCount = 10;
						vChar.posX = oldX + Values.clamp(deltaX*Values.clamp(speed, 0.1f, 0.8f), -0.05f, 0.05f);			
						}
				}
			vChar.posX = Values.clamp(vChar.posX, -0.5f, Screen.DEV_MAX_X - 0.5f );						// In the end make sure object is within the screen
		}
	}
/******************************************************************************************************************************************************************
 *		METHODS OverLapping Methods to check different Overlapping Scenarios  
*******************************************************************************************************************************************************************/	
	private static int objExtOlap(Base vObj, Obstacle[] vObstacles, float x1, float y1, float x2, float y2, Base arrRes[], int vIndex)
	{																							
		int tCount = 0;
		int cloudSize = 0;
		Base cloud = null;
		Base tObj = null;
		
		extObj = createExtObj(vObj, extObj, x1, y1, x2, y2);
		
		for(int i = 0; i < Adventure.MAX_ACTIVE_OBST; i++ ) 
			if(vObstacles[i] != null && extObj.detectCollision(vObstacles[i]) )
				{
				cloudSize = vObstacles[i].length;
				tObj = vObstacles[i].arClouds[0];

				for(int j = 0; j < cloudSize; j++)
					{
					cloud = vObstacles[i].arClouds[j];
					if(cloud.bEnable && cloud.posY < Screen.DEV_MAX_Y && ((arrRes[FRONT]== null || cloud.ID != arrRes[FRONT].ID) && (arrRes[FRONT2]== null || cloud.ID != arrRes[FRONT2].ID) && (arrRes[UP]== null || cloud.ID != arrRes[UP].ID) && (arrRes[DOWN]== null || cloud.ID != arrRes[DOWN].ID) ))
						if( extObj.detectCollision(cloud))												// Donot calculate collision of enemy with its self		
							{
							tCount++;																	// Counter for num of Objects overlapped with
							if(getDistance(extObj, cloud) < getDistance(vObj, tObj)) 
								tObj = cloud;
							}
					}
				}		
		arrRes[vIndex] = tObj;
				
		return tCount;																					// No overlapping found
	}
/******************************************************************************************************************************************************************
 *	Creates an extended object based on orignal Object   
*******************************************************************************************************************************************************************/
	protected static Base createExtObj(Base vOrgObj, Base vExtObj, float top, float left, float bottom, float right )
	{
		extObj.ID = vOrgObj.ID;
		vExtObj.offsetX = (top - bottom)/2 + vOrgObj.offsetX;
		vExtObj.offsetY = (left - right)/2 + vOrgObj.offsetY;
		vExtObj.posX = vOrgObj.posX + top + (vOrgObj.offsetX - extObj.offsetX);
		vExtObj.posY = vOrgObj.posY + left + (vOrgObj.offsetY - extObj.offsetY);
		
		return vExtObj;

	}
/******************************************************************************************************************************************************************
 *	Detects collion of object with clouds and adjusts the object   
*******************************************************************************************************************************************************************/
	static void detectCollision(Object obj, Obstacle[] vObstacles, float oldX, float oldY)
	{
		int cloudSize = 0;
		boolean bCollision = false;
		Obstacle.Cloud cloud = null;
		int numbObst = vObstacles.length;
		float arrResult[] = {0,0,0,0,0,};
		float olapX = 0, olapY = 0, absOlapX = 0, absOlapY = 0;
		float deltaX = obj.posX - oldX, deltaY = obj.posY - oldY;
		
		for(int i = 0; i < numbObst; i++ ) 
			if(vObstacles[i] != null && obj.detectCollision(vObstacles[i]) )
				{
				cloudSize = vObstacles[i].length;
				for(int j = 0; j < cloudSize; j++)
					{
					cloud = vObstacles[i].arClouds[j];
					if(cloud.bEnable && cloud.posY < Screen.DEV_MAX_Y  )
						if(cloud.ID != obj.ID && cloud.detectOverlap(obj, arrResult))
							{	
							absOlapX += arrResult[Obstacle.OLAP_X]*Math.abs(arrResult[Obstacle.OLAP_Y]);
							absOlapY += arrResult[Obstacle.OLAP_Y]*Math.abs(arrResult[Obstacle.OLAP_X]);
							olapX += (obj.posX < cloud.posX) ? -arrResult[Obstacle.OLAP_X] : arrResult[Obstacle.OLAP_X];
							olapY +=( obj.posY < cloud.posY) ? -arrResult[Obstacle.OLAP_Y] : arrResult[Obstacle.OLAP_Y];
							bCollision = true;
							}
					}
				}

		if(bCollision)
			{						
				if(Math.abs(deltaX + (olapX*absOlapX)) < Math.abs(deltaX))
					obj.posX += olapX*absOlapY;
			
			
				if(Math.abs(deltaY + (olapY*absOlapY)) < Math.abs(deltaY))
					obj.posY += olapY*absOlapX*absOlapX;
			}
	}

/******************************************************************************************************************************************************************
 *		Genric methods  
*******************************************************************************************************************************************************************/	
	private static float getGap(Base vObj1, Base vObj2)
	{
		float xDiff, yDiff, widthObj1, heightObj1, widthObj2, heightObj2;
		
		heightObj1 = 0.5f - vObj1.offsetX;																// Half Height
		widthObj1  = 0.5f - vObj1.offsetY;																// Half width
		heightObj2 = 0.5f - vObj2.offsetX;
		widthObj2  = 0.5f - vObj2.offsetY;

		xDiff =  Math.abs(vObj1.posX - vObj2.posX);
		yDiff =  Math.abs(vObj1.posY - vObj2.posY);
				
		xDiff -= (heightObj1+heightObj2);
		yDiff -= (widthObj1 + widthObj2);
		
		if(xDiff < 0) xDiff = 0;
		if(yDiff < 0) yDiff = 0;

		return yDiff + xDiff;
	}

	
	private static float getDistance(Base vObj1, Base vObj2)
	{
		float xDiff, yDiff;
		
		xDiff =  Math.abs(vObj1.posX - vObj2.posX);
		yDiff =  Math.abs(vObj1.posY - vObj2.posY);
		
		return (float) Math.sqrt((yDiff*yDiff) + (xDiff*xDiff));
	}

	
	static float getSlope(Base vBVlock, Base vChar, int arrResult[])
	{
		float deltaX = vBVlock.posX - vChar.posX ;
		float deltaY = vBVlock.posY - vChar.posY ;
		int quadrent = -1;
		
		if(deltaX < 0 && deltaY < 0) 		quadrent = TOP_LEFT;
		else if(deltaX > 0 && deltaY > 0) 	quadrent = BOTTOM_RIGHT;
		else if(deltaX < 0 && deltaY > 0) 	quadrent = TOP_RIGHT;
		else if(deltaX > 0 && deltaY < 0) 	quadrent = BOTTOM_LEFT;
		else if(deltaX == 0 && deltaY < 0)	quadrent = LEFT;
		else if(deltaX == 0 && deltaY > 0)	quadrent = RIGHT;
		else if(deltaY == 0 && deltaX < 0)  quadrent = TOP;
		else if(deltaY == 0 && deltaX > 0)  quadrent = BOTTOM;
		else 								quadrent = ERROR;
		arrResult[0] = quadrent;
		
		return deltaX / deltaY;
	}


	/************************************************************************************************************************
	*	METHOD - Class End
	************************************************************************************************************************/
	
	
}
