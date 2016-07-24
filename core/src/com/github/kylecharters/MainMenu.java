package com.github.kylecharters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The MainMenu game state is the first state, and allows the user to access
 * the PlayerSelect state, Instructions state as well as closing the game
 * It also features a title and a rotating die.
 * 
 * @author Kyle
 *
 */
public class MainMenu implements GameState{
	
	private ModelBatch mBatch;
	private ModelInstance diceInstance;
	private OrthographicCamera camera;
	private Environment environment;
	
	private Stage stage;
	
	private Image title;
	private TextButton play, inst, exit;
	
	@Override
	public void create(){
		mBatch = new ModelBatch();
		
		diceInstance = new ModelInstance(Yahtzee.dieModel, 0, 0, 0);
		
		//Set up camera
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(-1.5f, 1f, 5f);
		camera.zoom = 0.01f;
		camera.update();
		
		//Set lighting for the cube
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -1f));
		
		stage = new Stage(new ScreenViewport());
		
		//Load the title image
		title = new Image(new Texture(Gdx.files.internal("assets/Yahtzee.png")));
		title.setY(Gdx.graphics.getHeight() - 300);
		stage.addActor(title);
		
		//Load the play button
		play = new TextButton("Play", Yahtzee.skin, "default");
		play.setBounds(175, 210, 150, 25);
		play.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				//Enter playerselect menu
				Yahtzee.gameStateManager.setState(GameStateManager.PLAYERSELECT);
				return true;
			}
		});
		stage.addActor(play);
		
		//Load instructions button
		inst = new TextButton("Instructions", Yahtzee.skin, "default");
		inst.setBounds(175, 175, 150, 25);
		inst.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				Yahtzee.gameStateManager.setState(GameStateManager.INSTRUCTIONS);
				return true;
			}
		});
		stage.addActor(inst);
		
		//Load exit button
		exit = new TextButton("Exit", Yahtzee.skin, "default");
		exit.setBounds(175, 140, 150, 25);
		exit.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				//Exits application
				System.exit(0);
				return true;
			}
		});
		stage.addActor(exit);
	}
	
	@Override
	public void enable(){
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void update(float deltaTime){
		//Rotate the dice 10 degrees per second
		diceInstance.transform.rotate(Vector3.Y, 10 * deltaTime);
		stage.act();
	}

	@Override
	public void render(){
		//Render the die
		mBatch.begin(camera);
		mBatch.render(diceInstance, environment);
		mBatch.end();
		
		stage.draw();
	}

	@Override
	public void dispose(){
		stage.dispose();
	}

	@Override
	public void disable(){}
	
}
