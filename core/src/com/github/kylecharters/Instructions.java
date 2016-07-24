package com.github.kylecharters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The instructions game state allows the user to read yahtzee instructions
 * 
 * @author Kyle
 *
 */
public class Instructions implements GameState{
	private Stage stage;
	
	@Override
	public void create(){
		stage = new Stage(new ScreenViewport());
		
		//Load all text from the rules.txt file in assets
		Label info = new Label("\n" + Gdx.files.internal("assets/rules.txt").readString() + "\n", Yahtzee.skin);
		info.setWrap(true);
		info.setAlignment(Align.center);
		ScrollPane scrollPane = new ScrollPane(info, Yahtzee.skin);
		scrollPane.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 45);
		stage.addActor(scrollPane);
		
		
		//Creating back button for returning to main menu
		TextButton back = new TextButton("Back", Yahtzee.skin, "default");
		back.setBounds(10, Gdx.graphics.getHeight() - 35, 75, 25);
		back.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
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
