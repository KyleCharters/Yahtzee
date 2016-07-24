package com.github.kylecharters;

/**
 * The GameStateManager manages all game states that are loaded
 * Supports getting and setting of game states.
 * 
 * @author Kyle
 *
 */
public class GameStateManager{
	public static final int MAIN = 0;
	public static final int INSTRUCTIONS = 1;
	public static final int PLAYERSELECT = 2;
	public static final int PLAY = 3;
	
	public GameState active;
	public GameState[] states;
	
	public GameStateManager(GameState[] states, int initial){
		this.states = states;
		this.active = states[initial];
		
		for(GameState state : states){
			state.create();
		}
		
		active.enable();
	}
	
	public void update(float deltaTime){
		active.update(deltaTime);
		active.render();
	}
	
	public GameState getState(int position){
		return states[position];
	}
	
	public void setState(int position){
		active.disable();
		active = states[position];
		active.enable();
	}
	
	public void dispose(){
		for(GameState state : states){
			state.dispose();
		}
		
		active = null;
		states = null;
	}
}
