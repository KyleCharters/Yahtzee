package com.github.kylecharters;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.UBJsonReader;

/**
 * Yahtzee allows static access to the dice model and skin which are
 * objects that are used in multiple classes, and can be shared.
 * Also creates the game state manager, as well as all the states.
 * 
 * @author Kyle
 *
 */
public class Yahtzee extends ApplicationAdapter{
	public static Skin skin;
	public static Model dieModel;
	public static GameStateManager gameStateManager;
	
	@Override
	public void create(){
		Gdx.gl.glClearColor(0.96f, 0.96f, 0.96f, 1.0f);
		
		skin = new Skin();
		
		
		//Create all fonts
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Revue.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20;
		parameter.spaceY = 10;
		parameter.color = Color.BLACK;
		//Generate a black font, default
		skin.add("default-font", generator.generateFont(parameter));
		parameter.color = Color.LIGHT_GRAY;
		//Generate a grey font, used in score board entries
		//The color cannot be black as it would not change color when tinting
		skin.add("grey-font", generator.generateFont(parameter));
		parameter.size = 15;
		parameter.color = Color.BLACK;
		//Generate score board font, used in the score board
		skin.add("score-font", generator.generateFont(parameter));
		generator.dispose();
		
		//Create skin
		skin.addRegions(new TextureAtlas(Gdx.files.internal("uiskin.atlas")));
		skin.load(Gdx.files.internal("uiskin.json"));
		
		//Load dice model
		UBJsonReader jsonReader = new UBJsonReader();
		G3dModelLoader loader = new G3dModelLoader(jsonReader);
		dieModel = loader.loadModel(Gdx.files.getFileHandle("dice.g3db", FileType.Internal));
		
		//Load the game state manager
		GameState[] states = {new MainMenu(), new Instructions(), new PlayerSelect(), new Play()};
		gameStateManager = new GameStateManager(states, 0);
	}
	
	@Override
	public void render(){
		//Clear buffers
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		//Update the current game state
		gameStateManager.update(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose(){
		gameStateManager.dispose();
		dieModel.dispose();
		skin.dispose();
	}
}
