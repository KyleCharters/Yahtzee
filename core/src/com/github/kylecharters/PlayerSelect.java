package com.github.kylecharters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * This GameState allows the user to select the amount of players, then switch the current state to
 * Play with the number of players initialized.
 * 
 * @author Kyle
 *
 */
public class PlayerSelect implements GameState{
	
	private Stage stage;
	
	private Image title;
	private TextButton one, two, three, four, back;
	
	@Override
	public void create(){
		stage = new Stage(new ScreenViewport());
		
		//Load the title image
		title = new Image(new Texture(Gdx.files.internal("Yahtzee.png")));
		title.setY(Gdx.graphics.getHeight() - 300);
		stage.addActor(title);
		
		//Create the "one player" button, set its action
		one = new TextButton("One Player", Yahtzee.skin, "default");
		one.setBounds(175, 280, 150, 25);
		one.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				//Set the play state to 1 player mode
				((Play) Yahtzee.gameStateManager.getState(GameStateManager.PLAY)).numberPlayers = 1;
				Yahtzee.gameStateManager.setState(GameStateManager.PLAY);
				return true;
			}
		});
		stage.addActor(one);

		//Create the "two players" button
		two = new TextButton("Two Players", Yahtzee.skin, "default");
		two.setBounds(175, 245, 150, 25);
		two.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				//Set the play state to 2 player mode
				((Play) Yahtzee.gameStateManager.getState(GameStateManager.PLAY)).numberPlayers = 2;
				Yahtzee.gameStateManager.setState(GameStateManager.PLAY);
				return true;
			}
		});
		stage.addActor(two);
		
		//Create the "three players" button
		three = new TextButton("Three Players", Yahtzee.skin, "default");
		three.setBounds(175, 210, 150, 25);
		three.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				//Set the play state to 3 player mode
				((Play) Yahtzee.gameStateManager.getState(GameStateManager.PLAY)).numberPlayers = 3;
				Yahtzee.gameStateManager.setState(GameStateManager.PLAY);
				return true;
			}
		});
		stage.addActor(three);
		
		//Create the "four players" button
		four = new TextButton("Four Players", Yahtzee.skin, "default");
		four.setBounds(175, 175, 150, 25);
		four.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				//Set the play state to 4 player mode
				((Play) Yahtzee.gameStateManager.getState(GameStateManager.PLAY)).numberPlayers = 4;
				Yahtzee.gameStateManager.setState(GameStateManager.PLAY);
				return true;
			}
		});
		stage.addActor(four);
		
		//Create the back button
		back = new TextButton("Back", Yahtzee.skin, "default");
		back.setBounds(175, 140, 150, 25);
		back.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				//Sets game state to main menu
				Yahtzee.gameStateManager.setState(GameStateManager.MAIN);
				return true;
			}
		});
		stage.addActor(back);
	}
	
	@Override
	public void enable(){
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void update(float deltaTime){
		stage.act();
	}

	@Override
	public void render(){
		stage.draw();
	}

	@Override
	public void dispose(){
		stage.dispose();
	}

	@Override
	public void disable(){}
	
}
