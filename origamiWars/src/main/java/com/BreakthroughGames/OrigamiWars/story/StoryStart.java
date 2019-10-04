package com.BreakthroughGames.OrigamiWars.story;

import android.util.Log;

import com.BreakthroughGames.OrigamiWars.Adventure;
import com.BreakthroughGames.OrigamiWars.Pref;
import com.BreakthroughGames.OrigamiWars.Screen;
import com.BreakthroughGames.OrigamiWars.Sound;
import com.BreakthroughGames.OrigamiWars.SoundPlayer;
import com.BreakthroughGames.OrigamiWars.Texture;
import com.BreakthroughGames.OrigamiWars.Values;

public class StoryStart extends Adventure {
    public static boolean bRunOnce = false;
    private int iLinesDisp = 0;

    @Override
    public void loadingScreen() {
        Log.d("MethodCall", "Intro::setLoadingScreen() Called");
        SoundPlayer.playSound(Sound.BG_MUSIC_INTRO);
    }

    @Override
    public void loadLevel() {
        Log.d("MethodCall", "Intro::loadLevel() Called");

        txtStory.loadTexture(Texture.STORY_START);
        story.iTexture = txtStory.iTexture;
        story.setTextureSize(0, 1, 0.30f, 0);                //set the page size
        story.txtLineWidth = 0.23f;                            //set the line scroll
    }

    @Override
    public void resumeLevel() {
        txtStory.loadTexture(Texture.STORY_START);
        story.iTexture = txtStory.iTexture;
        Screen.iMenu = Screen.MENU_OFF;
        Screen.bPause = false;
        iLinesDisp--;
    }

    /************************************************************************************************************************
     *   METHOD- Shows the story
     ***********************************************************************************************************************/
    public void runOneFrame() {
        story.transformHUD(1, 1, 0, 0);
        story.setEvent(iLinesDisp, 0.1f, true);        //pause the screen
        story.draw();                                    // no transform, as drawHUD() calls transform function itself to set the size
        iLinesDisp++;
    }

    public byte checkGameStatus() {
        if (iLinesDisp >= 4) {
            Pref.getSet(Pref.LEVEL_COMPLETE);    // Update Game stats in Preferences
            return Values.GAME_LOAD_SCREEN;
        } else
            return Values.GAME_RUNNING;
    }
}//Class End
