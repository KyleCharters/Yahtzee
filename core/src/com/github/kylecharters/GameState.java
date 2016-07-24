package com.github.kylecharters;

/**
 * The GameState manager only manages classes that implement this interface
 * 
 * @author Kyle
 * 
 */
public interface GameState{
	/**
	 * Called when the gamestate is first initialized
	 */
	public void create();
	/**
	 * Called when the gamestate gains focus
	 */
	public void enable();
	/**
	 * Called on every update
	 * @param deltaTime The amount of time elapsed in seconds since last update
	 */
	public void update(float deltaTime);
	/**
	 * Called after update, for rendering
	 */
	public void render();
	/**
	 * Called when the gamestate loses focus
	 */
	public void disable();
	/**
	 * Called when the gamestate is being deleted
	 */
	public void dispose();
}