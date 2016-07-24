package com.github.kylecharters.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.github.kylecharters.Yahtzee;

public class DesktopLauncher {
	public static void main (String[] arg){
		LwjglApplicationConfiguration.disableAudio = true;
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = false;
		config.width = 800;
		config.height = 600;
		//Used for msaa anti-aliasing
		config.samples = 4;
		config.initialBackgroundColor = new Color(0.96f, 0.96f, 0.96f, 1.0f);
		config.title = "Yahtzee";
		//Load all 3 "Y" Icons in 128x128, 32x32, and 16x16 resolutions.
		config.addIcon("assets/Icon128.png", FileType.Internal);
		config.addIcon("assets/Icon32.png", FileType.Internal);
		config.addIcon("assets/Icon16.png", FileType.Internal);
		new LwjglApplication(new Yahtzee(), config);
	}
}
