package com.github.kylecharters.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.github.kylecharters.Yahtzee;

public class DesktopLauncher {
	public static void main (String[] arg){
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.disableAudio(true);
		config.setWindowedMode(800, 600);
		config.setResizable(false);
		//Used for msaa anti-aliasing
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 8);
		config.setInitialBackgroundColor(new Color(0.96f, 0.96f, 0.96f, 1.0f));
		config.setTitle("Yahtzee");
		//Load all 3 "Y" Icons in 128x128, 32x32, and 16x16 resolutions.
		config.setWindowIcon(FileType.Internal, "Icon128.png", "Icon32.png", "Icon16.png");
		new Lwjgl3Application(new Yahtzee(), config);
	}
}
