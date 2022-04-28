package com.github.kylecharters;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Play implements GameState{
	//All Quaternion rotations for each dice value
	public static final Quaternion one = new Quaternion(0.5f, 0.5f, 0.5f, 0.5f);
	public static final Quaternion two = new Quaternion(0.0f, 0.0f, 0.707f, 0.707f);
	public static final Quaternion three = new Quaternion(-0.5f, 0.5f, 0.5f, 0.5f);
	public static final Quaternion four = new Quaternion(0.5f, -0.5f, 0.5f, 0.5f);
	public static final Quaternion five = new Quaternion(0.707f, 0.707f, 0.0f, 0.0f);
	public static final Quaternion six = new Quaternion(-0.5f, -0.5f, 0.5f, 0.5f);
	
	private static Texture lockTexture;
	
	//3d Dice variables
	private ModelBatch mBatch;
	private OrthographicCamera camera;
	private Environment environment;
	private Array<Die> dice;
	
	//Game variables
	private Array<Player> players;
	public int numberPlayers = 0;
	private int currentPlayer = 0;
	private Random random;
	
	//UI variables
	private Stage stage;
	private TextButton back, roll;
	private Table upper, lower, totals;
	private SplitPane split;
	
	/**
	 * The die class embodies all dice, and their lock buttons.
	 * The die has the ability to rotate to any value using quaternion interpolation.
	 * 
	 * @author Kyle
	 *
	 */
	public class Die extends ModelInstance{
		private Matrix4 currentPosition;
		
		private Quaternion rotation;
		private Quaternion rotationTo;
		private boolean animating;
		
		private Image lockImage;
		private Button button;
		
		private boolean locked = false;
		private boolean superlocked = true;
		private int value = 2;
		
		public Die(float x){
			super(Yahtzee.dieModel, x, 0, 0);
			
			rotation = new Quaternion();
			transform.getRotation(rotation);
			
			currentPosition = transform.cpy();
			
			lockImage = new Image(lockTexture);
			lockImage.setVisible(false);
			button = new Button(lockImage, new ButtonStyle());
			
			//Create a button for the lock
			button.setY(50);
			button.setX(((Gdx.graphics.getWidth() / 2) - (lockTexture.getWidth() / 2)) + (50 * x));
			button.addListener(new InputListener(){
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					setLocked(!locked);
					return true;
				}
			});
		}
		
		public void setLocked(boolean locked){
			//Cannot change lock state when superlocked. This is useful between turns.
			if(!superlocked){
				this.locked = locked;
				lockImage.setVisible(locked);
			}
		}
		
		public void setSuperLock(boolean superlocked){
			this.superlocked = superlocked;
		}
		
		public void roll(int value){
			//Only rotate if its not locked and the new value is different
			if(!locked && this.value != value){
				this.value = value;
				//Selects which quaternion the die should rotate too
				switch(value){
					case 1:
						rotationTo = one;
						break;
					case 2:
						rotationTo = two;
						break;
					case 3:
						rotationTo = three;
						break;
					case 4:
						rotationTo = four;
						break;
					case 5:
						rotationTo = five;
						break;
					case 6:
						rotationTo = six;
				}
				animating = true;
			}
		}
		
		public void update(float deltaTime){
			if(animating){
				//Reset position
				transform.set(currentPosition);
				
				//Check if distance between each quaternion value is enough to consider
				if(Math.abs(rotation.dot(rotationTo)) < 0.999){
					//Rotate the cube to its end point
					transform.rotate(rotation.slerp(rotationTo, Math.min(1, 6f * deltaTime)));
				//Stop the animation, and set it to its exact final position if it is close
				}else{
					animating = false;
					transform.rotate(rotation.slerp(rotationTo, 1f));
				}
			}
		}
	}
	
	/**
	 * Keeps a player's score, also creates a label for implementation
	 * into a UI scene
	 * 
	 * @author Kyle
	 *
	 */
	public class ScoreEntry{		
		//UI variables
		public Label label;
		
		//Game variables
		public int score;
		public boolean locked;
		public Player player;
		
		public ScoreEntry(final Player player){
			score = 0;
			locked = false;
			
			this.player = player;
			
			label = new Label(String.valueOf(score), player.light);
			label.setAlignment(Align.center);
			
			//When the user clicks on the score, lock it in, and change turn
			label.addListener(new InputListener(){
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					//Only allow a press if it is the correnct player's turn, and they have rolled at least once
					if(player.turn && player.rolls > 0 && !locked){
						locked = true;
						label.setStyle(player.solid);
						changeTurn();
					}
					return true;
				}
			});
		}
		
		public void setStyle(LabelStyle style){
			label.setStyle(style);
		}
		
		public void setPossibility(int score){
			if(!locked){
				this.score = score;
				label.setText(String.valueOf(score));
			}
		}
	}
	
	public class Player{
		public ScoreEntry one, two, three, four, five, six,
					threeOfKind, fourOfKind, fullHouse, smallStraight, largeStraight, yahtzee, chance;
		
		public Label total;
		
		public Color color;
		public final LabelStyle solid, light;
		
		public boolean turn = false;
		public int rolls = 0;
		public int number;
		
		public Player(int number){
			this.number = number;
			
			//Choose player color
			switch(number){
				case 1:
					color = Color.BLUE;
					break;
				case 2:
					color = Color.RED;
					break;
				case 3:
					color = Color.GREEN;
					break;
				case 4:
					color = Color.YELLOW;
					break;
			}
			
			solid = new LabelStyle(Yahtzee.skin.getFont("grey-font"), color);
			light = new LabelStyle(Yahtzee.skin.getFont("grey-font"), color.cpy().lerp(Color.WHITE, 0.8f));
			
			total = new Label("0", solid);
			total.setAlignment(Align.center);
			
			//Create new score entry for each option
			one = new ScoreEntry(this);
			two = new ScoreEntry(this);
			three = new ScoreEntry(this);
			four = new ScoreEntry(this);
			five = new ScoreEntry(this);
			six = new ScoreEntry(this);
			threeOfKind = new ScoreEntry(this);
			fourOfKind = new ScoreEntry(this);
			fullHouse = new ScoreEntry(this);
			smallStraight = new ScoreEntry(this);
			largeStraight = new ScoreEntry(this);
			yahtzee = new ScoreEntry(this);
			chance = new ScoreEntry(this);
		}
		
		/**
		 * Update all possibilities
		 * @param values Dice roll
		 */
		public void updatePossibilities(int[] values){
			one.setPossibility(Rolls.upper(values, 1));
			two.setPossibility(Rolls.upper(values, 2));
			three.setPossibility(Rolls.upper(values, 3));
			four.setPossibility(Rolls.upper(values, 4));
			five.setPossibility(Rolls.upper(values, 5));
			six.setPossibility(Rolls.upper(values, 6));
			threeOfKind.setPossibility(Rolls.ofKind(values, 3));
			fourOfKind.setPossibility(Rolls.ofKind(values, 4));
			fullHouse.setPossibility(Rolls.fullHouse(values));
			smallStraight.setPossibility(Rolls.straight(values, false));
			largeStraight.setPossibility(Rolls.straight(values, true));
			yahtzee.setPossibility(Rolls.yahtzee(values));
			chance.setPossibility(Rolls.sum(values));
		}
		
		/**
		 * Set all possibilities to 0
		 */
		public void resetPossibilities(){
			one.setPossibility(0);
			two.setPossibility(0);
			three.setPossibility(0);
			four.setPossibility(0);
			five.setPossibility(0);
			six.setPossibility(0);
			threeOfKind.setPossibility(0);
			fourOfKind.setPossibility(0);
			fullHouse.setPossibility(0);
			smallStraight.setPossibility(0);
			largeStraight.setPossibility(0);
			yahtzee.setPossibility(0);
			chance.setPossibility(0);
			
		}
		
		/**
		 * Gets player's total score
		 * @return player's total score.
		 */
		public int total(){
			return one.score + two.score + three.score + four.score + five.score + six.score +
					threeOfKind.score + fourOfKind.score + fullHouse.score + smallStraight.score + 
					largeStraight.score + yahtzee.score + chance.score;
		}
		
		/**
		 * Gets if player is finished the game
		 * @return If player is finished
		 */
		public boolean finished(){
			return one.locked && two.locked && three.locked && four.locked && five.locked && six.locked && 
					threeOfKind.locked && fourOfKind.locked && fullHouse.locked && smallStraight.locked && 
					largeStraight.locked && yahtzee.locked && chance.locked;
		}
	}
	
	/**
	 * Gets the player who's turn it currently is
	 * @return Player object
	 */
	public Player getPlayer(){
		return players.get(currentPlayer);
	}
	
	/**
	 * Rolls each dice, and updates each player score
	 */
	public void rollDice(){
		Player player = getPlayer();
		if(player.rolls < 3){
			player.rolls++;
			
			if(player.rolls == 1){
				for(Die die : dice){
					die.setSuperLock(false);
				}
			}
			
			int[] values = new int[5];
			
			for(int i = 0; i < 5; i++){
				Die die = dice.get(i);
				if(!die.locked){
					die.roll(random.nextInt(6) + 1);
				}
				values[i] = die.value;
			}
			
			player.updatePossibilities(values);
			if(player.rolls == 3){
				//On last roll, remove locks from all die, and set roll text to choose score
				roll.setText("Choose Score");
				for(Die die : dice){
					die.setLocked(false);
					die.setSuperLock(true);
				}
			}else{
				//Set the roll text
				roll.setText("Roll " + (getPlayer().rolls + 1) + (player.rolls == 1 ? "nd" : "rd"));
			}
		}
	}
	
	/**
	 * Changes the turn to the next player
	 */
	public void changeTurn(){
		Player player = getPlayer();
		
		player.resetPossibilities();
		player.rolls = 0;
		player.turn = false;
		
		player.total.setText(String.valueOf(player.total()));
		
		for(Die die : dice){
			die.setSuperLock(false);
			die.setLocked(false);
			die.setSuperLock(true);
		}
		
		if(currentPlayer == players.size - 1 && player.finished()){
			Player winner = player;
			int score = 0;
			for(Player p : players){
				int total = p.total();
				if(total > score){
					score = total;
					winner = p;
				}
			}
			roll.getLabel().setStyle(winner.solid);
			roll.setText("Player " + winner.number + " wins!");
			player.rolls = 3;
			return;
		}
		
		currentPlayer = (currentPlayer + 1) % players.size;
		player = getPlayer();
		
		roll.getLabel().setStyle(player.solid);
		roll.setText("Roll Dice");
		player.turn = true;
	}
	
	@Override
	public void create(){
		mBatch = new ModelBatch();
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 4, 10);
		camera.zoom = 0.02f;
		camera.update();
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 0.1f));
		environment.add(new DirectionalLight().set(0.5f, 0.5f, 0.5f, -1f, -0.8f, -0.5f));
		
		random = new Random();
		lockTexture = new Texture(Gdx.files.internal("Lock.png"));
		//Add 5 dice to the screen
		dice = new Array<Die>();
		dice.add(new Die(-5.5f));
		dice.add(new Die(-2.75f));
		dice.add(new Die(0));
		dice.add(new Die(2.75f));
		dice.add(new Die(5.5f));
	}
	
	@Override
	public void enable(){
		players = new Array<Player>();
		for(int i = 0; i < numberPlayers; i++)
			players.add(new Player(i + 1));
		
		stage = new Stage(new ScreenViewport());
		
		//Add back button
		back = new TextButton("Exit to Main Menu", Yahtzee.skin, "default");
		back.setBounds(10, Gdx.graphics.getHeight() - 35, 225, 25);
		back.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				Yahtzee.gameStateManager.setState(GameStateManager.MAIN);
				return true;
			}
		});
		stage.addActor(back);
		
		//Roll dice button
		roll = new TextButton("Roll Dice", Yahtzee.skin, "default");
		roll.setBounds(565, Gdx.graphics.getHeight() - 35, 225, 25);
		roll.addListener(new InputListener(){
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				rollDice();
				return true;
			}
		});
		stage.addActor(roll);
		
		currentPlayer = players.size - 1;
		changeTurn();
		
		//Create totals table, which contains all total scores
		totals = new Table();
		totals.setBounds(245, Gdx.graphics.getHeight() - 35, 310, 25);
		for(Player player : players)
			totals.add(player.total).width(50).height(50);
		stage.addActor(totals);
		
		upper = new Table(Yahtzee.skin);
		
		//Add all "ones" labels to table
		upper.add("Ones", "score").expandX().height(50);
		for(Player player : players)
			upper.add(player.one.label).width(50).height(50);

		//Add all "twos" labels to table
		upper.row();
		upper.add("Twos", "score").expandX().height(50);
		for(Player player : players)
			upper.add(player.two.label).width(50).height(50);
		
		//Add all "threes" labels to table
		upper.row();
		upper.add("Threes", "score").expandX().height(50);
		for(Player player : players)
			upper.add(player.three.label).width(50).height(50);
		
		//Add all "fours" labels to table
		upper.row();
		upper.add("Fours", "score").expandX().height(50);
		for(Player player : players)
			upper.add(player.four.label).width(50).height(50);
		
		//Add all "fives" labels to table
		upper.row();
		upper.add("Fives", "score").expandX().height(50);
		for(Player player : players)
			upper.add(player.five.label).width(50).height(50);
		
		//Add all "sixes" labels to table
		upper.row();
		upper.add("Sixes", "score").expandX().height(50);
		for(Player player : players)
			upper.add(player.six.label).width(50).height(50);
		
		lower = new Table(Yahtzee.skin);

		//Add all "three of a kind" labels to table
		lower.add("Three of a Kind", "score").expandX().height(50);
		for(Player player : players)
			lower.add(player.threeOfKind.label).width(50).height(50);

		//Add all "four of a kind" labels to table
		lower.row();
		lower.add("Four of a Kind", "score").expandX().height(50);
		for(Player player : players)
			lower.add(player.fourOfKind.label).width(50).height(50);

		//Add all "full house" labels to table
		lower.row();
		lower.add("Full House", "score").expandX().height(50);
		for(Player player : players)
			lower.add(player.fullHouse.label).width(50).height(50);

		//Add all "small straight" labels to table
		lower.row();
		lower.add("Small Straight", "score").expandX().height(50);
		for(Player player : players)
			lower.add(player.smallStraight.label).width(50).height(50);

		//Add all "large straight" labels to table
		lower.row();
		lower.add("Large Straight", "score").expandX().height(50);
		for(Player player : players)
			lower.add(player.largeStraight.label).width(50).height(50);
		
		//Add all "yahtzee" labels to table
		lower.row();
		lower.add("Yahtzee!", "score").expandX().height(50);
		for(Player player : players)
			lower.add(player.yahtzee.label).width(50).height(50);

		//Add all "chance" labels to table
		lower.row();
		lower.add("Chance", "score").expandX().height(50);
		for(Player player : players)
			lower.add(player.chance.label).width(50).height(50);
		
		//Add the splitpane to the stage
		split = new SplitPane(upper, lower, false, Yahtzee.skin);
		split.setBounds(75, 200, Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 250);
		stage.addActor(split);
		
		//Add each dice lock buttons to the stage
		for(Die die : dice){
			stage.addActor(die.button);
		}
		
		//Set stage to input processor
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void update(float deltaTime){
		//Update dice positions
		for(Die die : dice)
			die.update(deltaTime);
		
		//Update the Scene2d stage
		stage.act();
	}

	@Override
	public void render(){
		//Render the dice
		mBatch.begin(camera);
		mBatch.render(dice, environment);
		mBatch.end();
		
		//Render the 2d UI elements
		stage.draw();
	}

	@Override
	public void disable(){}

	@Override
	public void dispose(){
		mBatch.dispose();
		stage.dispose();
		lockTexture.dispose();
	}
}
