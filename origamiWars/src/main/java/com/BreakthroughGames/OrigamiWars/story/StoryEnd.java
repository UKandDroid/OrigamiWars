package com.BreakthroughGames.OrigamiWars.story;

import com.BreakthroughGames.OrigamiWars.Adventure;
import com.BreakthroughGames.OrigamiWars.Game;
import com.BreakthroughGames.OrigamiWars.Screen;
import com.BreakthroughGames.OrigamiWars.Sound;
import com.BreakthroughGames.OrigamiWars.SoundPlayer;
import com.BreakthroughGames.OrigamiWars.Texture;
import com.BreakthroughGames.OrigamiWars.Values;

public class StoryEnd extends Adventure {
    public static boolean bRunOnce = false;
    private int iCounter = 2, iPageDisplay = 2;

    @Override
    public void loadingScreen() {
        SoundPlayer.playSound(Sound.BG_MUSIC_INTRO);
    }

    protected void loadLevel() {
        txtStory.loadTexture(Texture.STORY_END);
        story.iTexture = txtStory.iTexture;
        story.setTextureSize(0, 1, 0.3f, 0);                            // Set the page size
        story.txtLineWidth = 0.15f;                                        // Set the line scroll
    }

    @Override
    public void resumeLevel() {
        txtStory.loadTexture(Texture.STORY_END);
        story.iTexture = txtStory.iTexture;
        Screen.iMenu = Screen.MENU_OFF;
        Screen.bPause = false;
    }

    /************************************************************************************************************************
     *   METHOD- Shows the story
     ***********************************************************************************************************************/
    public void runOneFrame() {
        if (iCounter++ < 4)
            iPageDisplay = iCounter;
        story.transformHUD(1, 1, 0, 0);
        story.setEvent(iPageDisplay, 0.1f, true);                // Pause the screen
        story.draw();                                            // No transform, as drawHUD() calls transform function itself to set the size
    }

    public byte checkGameStatus() {
        if (iCounter >= 8)
            Game.refActGame.finish();
        return Values.GAME_RUNNING;
    }


}//Class End
