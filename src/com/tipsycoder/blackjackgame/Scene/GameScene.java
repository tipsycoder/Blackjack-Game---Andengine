package com.tipsycoder.blackjackgame.Scene;

import java.util.ArrayList;
import java.util.List;

import org.andengine.audio.music.Music;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.ParallelEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;

import com.cards.core.Card;
import com.cards.core.Deck;
import com.tipsycoder.blackjackgame.Managers.SceneManager;
import com.tipsycoder.blackjackgame.Managers.SceneManager.SceneType;
import com.tipsycoder.blackjackgame.players.AutomatedPlayer;
import com.tipsycoder.blackjackgame.players.Player;
import com.tipsycoder.blackjackgame.players.Player.PLAYER_SLOT;
import com.tipsycoder.dialog.DialogBox;
import com.tipsycoder.dialog.DialogBox.OPTION;

public class GameScene extends BaseScene implements OnClickListener, OnPreparedListener{
	
	//region Enums
	public enum AMOUNT_PLAYER {ONE, TWO, THREE, FOUR, };
	public enum GAME_STATE {HIT, STAY, };
	public enum GAMEOVER { N21, BURSTED, LOSE, WIN, TIE};
	//endregion
	
	//region Private Variables
	
	private List<Player> players;
	private List<IEntity> placeBetHUDList, mainHUDList, gameOverHUDList;
	private ButtonSprite hit_button, stand_button, double_down_button, place_bet_button, plus_button, minus_button, max_button, min_button, surrender_button, insurance_button;
	private ButtonSprite play_again_button, next_stage_button;
	private Sprite bursted_sprite, n21_sprite, win_sprite, lose_sprite, tie_sprite, game_bg_sprite;
	private Text cardValue, handValue, deckAmount, betAmount, cashAmount;
	private static int cash = 1000, bet = 0, minBet = 5, maxBet = 100;
	private final int START_CASH = 1000, MAIN_PLAYER = 0, BLACKJACK = 21, DEALER = 1, PLAYER_TWO = 1, PLAYER_THREE = 2, PLAYER_FOUR = 3;
	private final int pOneX = 400, pOneY = 180, pTwoX = 640, pTwoY = 400, pThreeX = 160, pThreeY = 400, pFourX = 400, pFourY = 600;
	private Deck deck;
	private AMOUNT_PLAYER amountPlayer;
	private List<Card> cardsOnDisplay;
	private DialogBox exitDialog;
	private boolean isExitDialogShow = false, gameOver = false, isPlaceBetHUDShow = false, isMainHUDShow = false;
	private GAMEOVER gameStatus;
	private static boolean newGame = true;
	private Music currentPlayingMusic;
	
	//endregion

	//region Public Variables
	
	public static String value = "N/A";
	
	//endregion
	
	//region Constructors
	public GameScene(AMOUNT_PLAYER amount) {
		exitDialog = new DialogBox(rManager.dia_BG_region, vbom, rManager.dia_yes_btn_region, rManager.dia_no_btn_region,this);
		cardsOnDisplay = new ArrayList<Card>();
		amountPlayer = amount;
		//cash = START_CASH;
		setOnSceneTouchListenerBindingOnActionDownEnabled(true);
		rManager.game_bgm.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if(mp.isPlaying())
					mp.release();
			}
		});
		
		rManager.game_bgm.getMediaPlayer().setOnPreparedListener(this);
		
		if(newGame) {
			minBet = 5;
			maxBet = 100;
			cash = START_CASH;
		}
		resetGame();
	}
	
	//endregion
	
	//region SuperClass Abstract Methods
	
	@Override
	public void createScene() {
		placeBetHUDList = new ArrayList<IEntity>();
		mainHUDList = new ArrayList<IEntity>();
		gameOverHUDList = new ArrayList<IEntity>();
		
		mCamera.set(0, 0, 1024, 800);
		setUpResources();
		createBackground();

	}
	
	@Override
	public void disposeScene() {
		mCamera.setHUD(null);
		rManager.unloadGameResources();
	}

	@Override
	public void onBackKeyPressed() {
		if(isMainHUDShow) {
			showExitDialogBoxHUD();
			this.setIgnoreUpdate(false);
		} else if(gameOver || isPlaceBetHUDShow) {
			newGame = true;
			SceneManager.getInstance().loadMainMenu(engine);
		}
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.GAME_SCENE;
	}
	
	@Override
	public Music getCurrentMusicPlaying() {
		return currentPlayingMusic;
	}
	
	//endregion

	//region Players Gameover Text
	
	private void winnerText(PLAYER_SLOT winPlayerID) {
		for(Player player : players) {
			if(player.getPlayerId() == winPlayerID)
				player.getTitleText().setText(player.getTitleText().getText() + "    W I N  - C A R D S  V A L U E : " + player.getCardsValue());
			else 
				player.getTitleText().setText(player.getTitleText().getText() + "    L O S E  - C A R D S  V A L U E : " + player.getCardsValue());
		}
	}
	
	private void loserText(PLAYER_SLOT loserPlayerID) {
		for(Player player : players) {
			if(player.getPlayerId() == loserPlayerID)
				player.getTitleText().setText(player.getTitleText().getText() + "    L O S E  - C A R D S  V A L U E : " + player.getCardsValue());
			else 
				player.getTitleText().setText(player.getTitleText().getText() + "    W I N  - C A R D S  V A L U E : " + player.getCardsValue());
		}
	}
	
	private void allText(String text) {
		for(Player player : players) 
			player.getTitleText().setText(player.getTitleText().getText() + "  " + text + "  - C A R D S  V A L U E : " + player.getCardsValue());
	}
	
	//endregion
	
	//region Class Helpers
	
	private void registerCardTouch(boolean value) {
		if(value) {
			for(Card card : cardsOnDisplay)
				this.registerTouchArea(card);
		} else if(!value) {
			for(Card card : cardsOnDisplay)
				this.unregisterTouchArea(card);
		}
	}
	
	private void enableGameButtons(boolean value) {
		hit_button.setEnabled(value);
		stand_button.setEnabled(value);
		place_bet_button.setEnabled(value);
		double_down_button.setEnabled(value);
	}
	
	private void clearScene() {
		
		placeBetHUDList = null;
		mainHUDList = null;
		gameOverHUDList = null;
		cardsOnDisplay = null;
		
		for(Player player : players) {
			for(Card card : player.display()) {
				card.clearEntityModifiers();
				card.clearUpdateHandlers();
				card.detachSelf();
				
				if(!card.isDisposed())
					card.dispose();
			}
		}
		
		players = null;
		game_bg_sprite.clearEntityModifiers();
		game_bg_sprite.clearUpdateHandlers();
		game_bg_sprite.detachSelf();
		game_bg_sprite.dispose();
		this.clearTouchAreas();
		this.clearEntityModifiers();
		this.clearUpdateHandlers();
		this.clearChildScene();
		this.detachChildren();
		this.disposeScene();
		this.dispose();
		
		System.gc();
	}
	
	private void playMusicSafely(Music music) {
		currentPlayingMusic = music;
		music.seekTo(0);
		music.play();
	}
	
	//endregion
		
	//region Start New Game Methods
	
	private void startGame() {
		for(Player player : players) {
			Card card = deck.draw();
			if(player.getPlayerId() != PLAYER_SLOT.PLAYER_ONE)
				card.faceUp();
			player.addCard(card);
		}
	}
	
	private void resetGame() {
		
		setUpPlayers(amountPlayer);
		cardsOnDisplay.clear();
		registerCardTouch(false);
		bet = minBet;
		cash -= bet;
		deck = new Deck();
		value = "N/A";		
		placeBetHUD();
		enableGameButtons(true);
		showPlayersHand(false);
		
		if(isIgnoreUpdate())
			setIgnoreUpdate(false);
	}
	
	//endregion
	
	//region HUD VIEWS METHODS
	
	private void placeBetHUD() {

		for(IEntity entity : placeBetHUDList)
			if(entity instanceof ButtonSprite)
				this.registerTouchArea(entity);
		
		isPlaceBetHUDShow = true;
		isMainHUDShow = false;
		playMusicSafely(rManager.bet_bgm);
		HUD gHUD = new HUD();
		Text minText = new Text(200, 470, rManager.gameFont, "Minimum Bet: $" + minBet + ".00",new TextOptions(HorizontalAlign.LEFT) , vbom);
		Text maxText = new Text(600, 470, rManager.gameFont, "Maximum Bet: $" + maxBet + ".00",new TextOptions(HorizontalAlign.LEFT) , vbom);
		Text hudTitle = new Text(mCamera.getCenterX() - 10, 600, rManager.gameFont, "PLACE BET",new TextOptions(HorizontalAlign.LEFT) , vbom);
		
		minText.setAnchorCenter(0, 0);
		maxText.setAnchorCenter(0, 0);
		
		hudTitle.setScale(2.5f);
		hudTitle.setColor(Color.RED);
		
		game_bg_sprite.setAlpha(0.2f);
		
		for(Player player : players) {
			for(Card card : player.display())
				card.setAlpha(0.2f);
		}
		
		cashAmount.setPosition(300, 400);
		betAmount.setPosition(550, 400);
		
		registerUpdateHandler(placeBetHUDUpdater());
		
		for(IEntity entity : placeBetHUDList) {
			entity.setParent(null);
			gHUD.attachChild(entity);
		}
		
		
		min_button.setPosition(400, 300);
		max_button.setPosition(600, 300);
		plus_button.setPosition(420, 216);
		place_bet_button.setPosition(502, 216);
		minus_button.setPosition(584, 216);
		
		gHUD.attachChild(hudTitle);
		gHUD.attachChild(minText);
		gHUD.attachChild(maxText);
		
		mCamera.setHUD(gHUD);
	}
	
	private void mainHUD() {		
		for(IEntity entity : placeBetHUDList)
			this.unregisterTouchArea(entity);
		
		for(IEntity entity : mainHUDList)
			if(entity instanceof ButtonSprite)
				this.registerTouchArea(entity);
		
		mCamera.setHUD(null);
		
		if(rManager.bet_bgm.isPlaying()) {	
			rManager.bet_bgm.pause();
			playMusicSafely(rManager.game_bgm);
		}
		
		isPlaceBetHUDShow = false;
		isMainHUDShow = true;
		
		HUD gameHUD = new HUD();
		registerUpdateHandler(gameUpdater());
		
		game_bg_sprite.setAlpha(1.0f);
		
		for(Player player : players) {
			for(Card card : player.display())
				card.setAlpha(1.0f);
		}
		
		for(IEntity entity : mainHUDList)
			entity.setParent(null);
		
		cashAmount.setPosition(810, 550);
		betAmount.setPosition(810, 600);
		
		gameHUD.attachChild(cardValue);
		gameHUD.attachChild(deckAmount);
		gameHUD.attachChild(betAmount);
		gameHUD.attachChild(cashAmount);
		gameHUD.attachChild(handValue);
		
		gameHUD.attachChild(surrender_button);
		gameHUD.attachChild(insurance_button);
		gameHUD.attachChild(hit_button);
		gameHUD.attachChild(stand_button);
		gameHUD.attachChild(double_down_button);
		
		for(Player player : players) {
			player.getTitleText().setParent(null);
			gameHUD.attachChild(player.getTitleText());
		}
		
		mCamera.setHUD(gameHUD);
	}
	
	private void gameOverHUD(GAMEOVER state) {
		HUD tempHUD = new HUD();
		
		game_bg_sprite.setAlpha(0.2f);
		
		for(Player player : players)
			for(Card card : player.display())
				card.setAlpha(0.2f);
		
		updateHUD();
		
		for(IEntity entity : mainHUDList)
			if(entity instanceof ButtonSprite)
				this.unregisterTouchArea(entity);
		
		for(IEntity entity : gameOverHUDList) {
			entity.setParent(null);
			if(!(entity instanceof Sprite))
				tempHUD.attachChild(entity);
			if(entity instanceof ButtonSprite) {
				this.registerTouchArea(entity);
				tempHUD.attachChild(entity);
			}
		}
		
		//enableGameButtons(false);
		showPlayersHand(true);
		this.setIgnoreUpdate(true);
		
		gameOver = true;
		isPlaceBetHUDShow = false;
		isMainHUDShow = false;
		
		gameStatus = state;
		
		registerCardTouch(false);
		
		for(Player player : players) {
			player.getTitleText().setParent(null);
			tempHUD.attachChild(player.getTitleText());
		}
		
		switch(state) {				
			case BURSTED:
				tempHUD.attachChild(bursted_sprite);
				break;
			case LOSE:
				tempHUD.attachChild(lose_sprite);
				break;
			case N21:
				tempHUD.attachChild(n21_sprite);
				break;
			case WIN:
				tempHUD.attachChild(win_sprite);
				break;
			case TIE:
				tempHUD.attachChild(tie_sprite);
				break;
			default:
				break;
		}
		mCamera.setHUD(tempHUD);
	}
	
	private void showExitDialogBoxHUD() {
		HUD mHUD = new HUD();
		game_bg_sprite.setAlpha(0.2f);
		
		for(Player player : players)
			for(Card card : player.display())
				card.setAlpha(0.2f);
		
		for(IEntity entity : mainHUDList)
			if(entity instanceof ButtonSprite)
				this.unregisterTouchArea(entity);
		
		exitDialog.setParent(null);
		mHUD.attachChild(exitDialog);
		exitDialog.setPosition(400, 400);
		isExitDialogShow = true;
		mCamera.setHUD(mHUD);
		
	}
	
	private void updateHUD() {
		cashAmount.setText("Cash Amount: $" + cash);
		betAmount.setText("Bet Amount: $" + bet);
		handValue.setText("Hand Value: " + players.get(MAIN_PLAYER).getCardsValue());
		deckAmount.setText("Deck Amount: " + deck.deckSize());
		cardValue.setText("Card Value: " + value);
		
		if(players.get(MAIN_PLAYER).isMaxCards())
			hit_button.setEnabled(false);
	}
	
	//endregion
	
	//region Scene Resources Setup Methods
	
	private void createBackground() {
		game_bg_sprite = new Sprite(mCamera.getCenterX(), mCamera.getCenterY(), rManager.gameBackground_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		attachChild(game_bg_sprite);
	}
	
	private void setUpResources() {	
		
		cardValue = new Text(810, 700, rManager.gameFont, "Card Value: 0123456789 N/A", new TextOptions(HorizontalAlign.LEFT), vbom);
		cardValue.setAnchorCenter(0, 0);
		cardValue.setText("Card Value: N/A");
		cardValue.setSize(30, 20);
		mainHUDList.add(cardValue);
		gameOverHUDList.add(cardValue);
		
		
		deckAmount = new Text(810, 650, rManager.gameFont, "Deck Amount: 0123456789 N/A", new TextOptions(HorizontalAlign.LEFT), vbom);
		deckAmount.setAnchorCenter(0, 0);
		deckAmount.setText("Deck Amount: N/A");
		deckAmount.setSize(30, 20);
		mainHUDList.add(deckAmount);
		gameOverHUDList.add(deckAmount);
		
		betAmount = new Text(810, 600, rManager.gameFont, "Bet Amount: 0123456789 N/A$", new TextOptions(HorizontalAlign.LEFT), vbom);
		betAmount.setAnchorCenter(0, 0);
		betAmount.setText("Bet Amount: N/A");
		betAmount.setSize(30, 20);
		mainHUDList.add(betAmount);
		gameOverHUDList.add(betAmount);
		placeBetHUDList.add(betAmount);
		
		cashAmount = new Text(810, 550, rManager.gameFont, "Cash Amount: 0123456789 N/A$or", new TextOptions(HorizontalAlign.LEFT), vbom);
		cashAmount.setAnchorCenter(0, 0);
		cashAmount.setText("Cash Amount: N/A");
		cashAmount.setSize(30, 20);
		mainHUDList.add(cashAmount);
		gameOverHUDList.add(cashAmount);
		placeBetHUDList.add(cashAmount);
		
		handValue = new Text(810, 500, rManager.gameFont, "Hand Value: 0123456789 N/A", new TextOptions(HorizontalAlign.LEFT), vbom);
		handValue.setAnchorCenter(0, 0);
		handValue.setText("Hand Value: N/A");
		handValue.setSize(30, 20);
		mainHUDList.add(handValue);
		gameOverHUDList.add(handValue);
		
		hit_button = new ButtonSprite(860, 70, rManager.hitButton_region, rManager.vbom, this);
		mainHUDList.add(hit_button);
		
		stand_button = new ButtonSprite(960, 70, rManager.standButton_region, rManager.vbom, this);
		mainHUDList.add(stand_button);
		
		double_down_button = new ButtonSprite(860, 170, rManager.doubleDownButton_region, rManager.vbom, this);
		mainHUDList.add(double_down_button);
		
		insurance_button = new ButtonSprite(960, 170, rManager.insurance_btn_region, rManager.vbom, this);
		mainHUDList.add(insurance_button);
		
		surrender_button = new ButtonSprite(910, 270, rManager.surrender_btn_region, rManager.vbom, this);
		mainHUDList.add(surrender_button);
		
		place_bet_button = new ButtonSprite(960, 170, rManager.placeBetButton_region, rManager.vbom, this);
		placeBetHUDList.add(place_bet_button);
		
		max_button = new ButtonSprite(0, 0, rManager.max_btn_region, rManager.vbom, this);
		placeBetHUDList.add(max_button);
		
		min_button = new ButtonSprite(0, 0, rManager.min_btn_region, rManager.vbom, this);
		placeBetHUDList.add(min_button);
		
		plus_button = new ButtonSprite(0, 0, rManager.plus_btn_region, rManager.vbom, this);
		placeBetHUDList.add(plus_button);
		
		minus_button = new ButtonSprite(0, 0, rManager.minus_btn_region, rManager.vbom, this);
		placeBetHUDList.add(minus_button);
				
		n21_sprite = new Sprite(400, 400, rManager.n21_region, rManager.vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		gameOverHUDList.add(n21_sprite);
		n21_sprite.registerEntityModifier(new ParallelEntityModifier( new AlphaModifier(1, 0.0f, 1.0f), new ScaleModifier(1, 0.5f, 1.0f), new RotationModifier(2, 0, 360)));
		
		bursted_sprite = new Sprite(400, 400, rManager.bursted_region, rManager.vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		gameOverHUDList.add(bursted_sprite);
		bursted_sprite.registerEntityModifier(new ParallelEntityModifier( new AlphaModifier(1, 0.0f, 1.0f), new ScaleModifier(1, 0.5f, 1.0f), new RotationModifier(2, 0, 360)));
		
		win_sprite = new Sprite(400, 400, rManager.win_region, rManager.vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		gameOverHUDList.add(win_sprite);
		win_sprite.registerEntityModifier(new ParallelEntityModifier( new AlphaModifier(1, 0.0f, 1.0f), new ScaleModifier(1, 0.5f, 1.0f), new RotationModifier(2, 0, 360)));
		
		lose_sprite = new Sprite(400, 400, rManager.lose_region, rManager.vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		gameOverHUDList.add(lose_sprite);
		lose_sprite.registerEntityModifier(new ParallelEntityModifier( new AlphaModifier(1, 0.0f, 1.0f), new ScaleModifier(1, 0.5f, 1.0f), new RotationModifier(2, 0, 360)));
		
		tie_sprite = new Sprite(400, 400, rManager.tie_region, rManager.vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		gameOverHUDList.add(tie_sprite);
		tie_sprite.registerEntityModifier(new ParallelEntityModifier( new AlphaModifier(1, 0.0f, 1.0f), new ScaleModifier(1, 0.5f, 1.0f), new RotationModifier(2, 0, 360)));
		
		play_again_button = new ButtonSprite(910, 270, rManager.play_again_btn_region, rManager.vbom, this);
		gameOverHUDList.add(play_again_button);
		
		next_stage_button = new ButtonSprite(910, 170, rManager.next_stage_btn_region, rManager.vbom, this);
		gameOverHUDList.add(next_stage_button);
	}
	
	
	private void setUpPlayers(final AMOUNT_PLAYER amount) {
		players = new ArrayList<Player>();
		
		switch(amount) {
			case ONE:
				players.add(new Player(PLAYER_SLOT.PLAYER_ONE, pOneX, pOneY));
				players.get(MAIN_PLAYER).getTitleText().setText("Y O U");
				players.add(new AutomatedPlayer(pFourX, pFourY));
				players.get(DEALER).getTitleText().setText("D E A L E R");
				break;
			case TWO:
				players.add(new Player(PLAYER_SLOT.PLAYER_ONE, pOneX, pOneY));
				players.add(new Player(PLAYER_SLOT.PLAYER_TWO, pTwoX, pTwoY));
				
				players.get(MAIN_PLAYER).getTitleText().setText("Y O U");
				players.get(PLAYER_TWO).getTitleText().setText("P L A Y E R  T W O");
				
				break;
			case THREE:
				players.add(new Player(PLAYER_SLOT.PLAYER_ONE, pOneX, pOneY));
				players.add(new Player(PLAYER_SLOT.PLAYER_TWO, pTwoX, pTwoY));
				players.add(new Player(PLAYER_SLOT.PLAYER_THREE, pThreeX, pThreeY));
				
				players.get(MAIN_PLAYER).getTitleText().setText("Y O U");
				players.get(PLAYER_TWO).getTitleText().setText("P L A Y E R  T W O");
				players.get(PLAYER_THREE).getTitleText().setText("P L A Y E R  T H R E E");
				
				players.get(PLAYER_TWO).getTitleText().setRotation(270);
				players.get(PLAYER_THREE).getTitleText().setRotation(90);
				
				break;
			case FOUR:
				players.add(new Player(PLAYER_SLOT.PLAYER_ONE, pOneX, pOneY));
				players.add(new Player(PLAYER_SLOT.PLAYER_TWO, pTwoX, pTwoY));
				players.add(new Player(PLAYER_SLOT.PLAYER_THREE, pThreeX, pThreeY));
				players.add(new Player(PLAYER_SLOT.PLAYER_FOUR, pFourX, pFourY));
				
				players.get(MAIN_PLAYER).getTitleText().setText("Y O U");
				players.get(PLAYER_TWO).getTitleText().setText("P L A Y E R  T W O");
				players.get(PLAYER_THREE).getTitleText().setText("P L A Y E R  T H R E E");
				players.get(PLAYER_FOUR).getTitleText().setText("P L A Y E R  F O U R");
				
				players.get(PLAYER_TWO).getTitleText().setRotation(270);
				players.get(PLAYER_THREE).getTitleText().setRotation(90);
				
				break;
			default:
				break;
		}
	}
	
	//endregion
	
	//region Scene Logic Methods
	private void upBet(int amount) {
		if(cash == 0)
			bet += 0;
		else
			bet += amount;
	}
	
	private void downBet(int amount) {
		if(bet <= 0)
			bet = 0;
		else
			bet -= amount;
	}
	
	private void downCash(int amount) {
		
		if(cash <= 0) {
			cash = 0;
			return;
		}
		cash -= amount;
		if(amount < 0)
			cash = 0;
	}
	
	private void upCash(float f) {
		if(bet <= 0)
			return;
		
		cash += f;
	}

	private void showPlayersHand(boolean showAllCards) {
		
		for(Player player : players) {
			for(Card card : player.display()) {
				card.detachSelf();
				
				if(showAllCards) {
					card.faceUp();
				} else {
					this.registerTouchArea(card);
				}
				
				cardsOnDisplay.add(card);
				attachChild(card);
			}
		}
	}
	
	private void autoPlayersMove() {
		if(!deck.isEmpty()) {
			for(Player player : players) {
				if(player instanceof AutomatedPlayer) {
					AutomatedPlayer aPlayer = (AutomatedPlayer)player;
					if(!aPlayer.isStay())
						aPlayer.move(deck);
				}
			}
		}
	}
	
	private void checkTable() {
		
		int stayCnt = 0, max = 0, sameCnt = 0;
		final int pAmount = players.size();
		
		
		for(Player player : players) {
			if(player.isStay()) {
				stayCnt++;
			}
		}
		
		if(stayCnt == pAmount) {
			PLAYER_SLOT playerID = null;
			for(Player player : players) {
				if(player.getCardsValue() > max && player.getCardsValue() <= 21) {
					max = player.getCardsValue();
					playerID = player.getPlayerId();
				}
			}
			
			for(Player player : players) {
				if(player.getCardsValue() == max) {
					sameCnt++;
				}
			}
			
			if(sameCnt > 1) {
				upCash(bet);
				gameOverHUD(GAMEOVER.TIE);
				allText("T I E");
				return;
			}
			
			if(playerID == players.get(MAIN_PLAYER).getPlayerId()) {
				if(playerID == players.get(MAIN_PLAYER).getPlayerId())
					upCash(bet * 2);
				gameOverHUD(GAMEOVER.WIN);				
				winnerText(playerID);
				return;
			} else {
				gameOverHUD(GAMEOVER.LOSE);
				winnerText(playerID);
				return;
			}
		}
	
		for(Player player : players) {
			if(player.getCardsValue() == BLACKJACK) {
				if(player.getPlayerId() == players.get(MAIN_PLAYER).getPlayerId()) {
					upCash(bet * 2.5f);
					gameOverHUD(GAMEOVER.WIN);
				} else {
					gameOverHUD(GAMEOVER.LOSE);
				}
				winnerText(player.getPlayerId());
				return;	
			}
		}
		
		for(Player player : players) {
			if(player.isBursted()) {
				if(player.getPlayerId() != players.get(MAIN_PLAYER).getPlayerId()) {
					upCash(bet * 2);
					gameOverHUD(GAMEOVER.WIN);
				} else {
					gameOverHUD(GAMEOVER.LOSE);
				}
				loserText(player.getPlayerId());
				return;
			}
		}
	}
	
	//endregion
	
	//region Scene Overridden Methods
	
	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if(hit_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			if(!deck.isEmpty()) {
				players.get(MAIN_PLAYER).addCard(deck.draw());
				autoPlayersMove();
			}
			showPlayersHand(false);
		}
		
		if(place_bet_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			startGame();
			showPlayersHand(false);
			mainHUD();
		}
		
		if(stand_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			players.get(MAIN_PLAYER).setStay(true);
		}
		
		if(double_down_button == pButtonSprite) {
			activity.toastOnUiThread("Not Implemented Yet");
			rManager.gameButtonClick_effect.play();
			//downCash(bet);
			//upBet(bet);
		}
		
		if(insurance_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			activity.toastOnUiThread("Not Implemented Yet");
		}
		
		if(surrender_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			activity.toastOnUiThread("Not Implemented Yet");
		}
		
		if(plus_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			downCash(minBet);
			upBet(minBet);
		}
		
		if(minus_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			upCash(minBet);
			downBet(minBet);
		}
		
		if(max_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			downCash(maxBet - bet);
			bet = maxBet;
		}
		
		if(min_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			upCash(bet);
			downCash(minBet);
			bet = minBet;
		}
		
		if(next_stage_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			engine.runOnUpdateThread(new Runnable() {
				@Override
				public void run() {
					clearScene();	
				}
			});
			mCamera.setHUD(null);
			SceneManager.getInstance().createGameScene(engine);
			newGame = false;
			minBet += minBet;
			maxBet += maxBet;
		}
		
		if(play_again_button == pButtonSprite) {
			rManager.gameButtonClick_effect.play();
			engine.runOnUpdateThread(new Runnable() {
				@Override
				public void run() {
					clearScene();	
				}
			});
			
			mCamera.setHUD(null);
			SceneManager.getInstance().createGameScene(engine);
			newGame = false;
		}
	}
	
	@Override
	public void onPrepared(MediaPlayer player) {
		if(rManager.game_bgm.getMediaPlayer() == player)
			rManager.game_bgm.play();
		
	}
	//endregion

	//region Updater Methods
	
	private IUpdateHandler placeBetHUDUpdater() {
		
		IUpdateHandler update = new IUpdateHandler() {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				updatePlaceBetHUD();
			}

			@Override
			public void reset() {
			}
		};
		return update;
	}
	
	private IUpdateHandler gameUpdater() {
		IUpdateHandler update = new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {
				
				if(!isExitDialogShow) {
					updateHUD();
					if(cash <= 0) {
						place_bet_button.setEnabled(false);
						double_down_button.setEnabled(false);
					}
					
					if(players.get(MAIN_PLAYER).isStay()) {
						unregisterUpdateHandler(gameUpdater());
						registerUpdateHandler(autoMoverTimer());
					}
					if(!gameOver)
						checkTable();
				
				} else {
					OPTION opt = OPTION.NULL;
					opt = exitDialog.showConfirmDialogBox(null);
					registerCardTouch(false);
					
					if(opt != OPTION.NULL) {		
						switch(opt) {
						case CANCEL:
							registerCardTouch(true);
							if(!gameOver) {
								isExitDialogShow = false;
								mainHUD();
							} else {
								gameOverHUD(gameStatus);
							}
							break;
						case YES:
							SceneManager.getInstance().loadMainMenu(engine);
							break;
						default:
							break;
						}
					}
				}
			}
			
			@Override
			public void reset() {
			}
		};
		return update;
	}
	
	private void updatePlaceBetHUD() {
		cashAmount.setText("Cash: $" + cash + ".00");
		betAmount.setText("Current Bet: $" + bet + ".00");
		
		minus_button.setEnabled((bet == minBet) ? false : true);
		plus_button.setEnabled((bet == maxBet) ? false : true);
		max_button.setEnabled((bet == maxBet) ? false : true);
		min_button.setEnabled((bet == minBet) ? false : true);
	}
	
	//endregion
	
	//region Scene TimerHandler Methods
	private TimerHandler autoMoverTimer() {
		TimerHandler timer = new TimerHandler(0.5f, new ITimerCallback () {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				engine.unregisterUpdateHandler(pTimerHandler);
				autoPlayersMove();
				showPlayersHand(false);
				registerUpdateHandler(gameUpdater());
			}
		});
		return timer;
	}
	//endregion


}
