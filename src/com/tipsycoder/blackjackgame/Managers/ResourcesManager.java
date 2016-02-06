package com.tipsycoder.blackjackgame.Managers;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePack;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackLoader;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.TexturePackTextureRegionLibrary;
import org.andengine.extension.texturepacker.opengl.texture.util.texturepacker.exception.TexturePackParseException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

import com.tipsycoder.blackjackgame.BaseActivity;
import com.tipsycoder.blackjackgame.exit_dialog;
import com.tipsycoder.blackjackgame.game_background;
import com.tipsycoder.blackjackgame.game_item_buttons;
import com.tipsycoder.blackjackgame.game_over_sprite;
import com.tipsycoder.blackjackgame.loader_pic_texture;
import com.tipsycoder.blackjackgame.menu_background;
import com.tipsycoder.blackjackgame.menu_buttons;

public class ResourcesManager {
	
	//region Private Variables
	private static ResourcesManager INSTANCE = new ResourcesManager();
	private BitmapTextureAtlas splashAtlas;
	private TexturePackTextureRegionLibrary loaderPackLibrary, load_picPackLibrary, club_cardPackLibrary, spade_cardPackLibrary, heart_cardPackLibrary, diamond_cardPackLibrary, gameOptionsPackLibrary, menuOptionsPackLibrary, gameOverPackLibrary, menuBGPackLibrary, gameBGPackLibrary, dialogBoxPackLibrary;
	private TexturePack loaderTexturePack, loadTexturePack, club_cardTexturePack, spade_cardTexturePack, heart_cardTexturePack, diamond_cardTexturePack, gameOptionsTexturePack, menuOptionsTexturePack, gameOverTexturePack, menuBGTexturePack, gameBGTexturePack, dialogBoxTexturePack;
	private SoundManager mSoundManager;
	//endregion
	
	//region Public Variables
	public ITextureRegion splash_region, loading_region, mMenuBackground_region, startGame_region, highscore_region, gameBackground_region, bursted_region, n21_region, win_region, lose_region, tie_region, dia_BG_region; 
	public ITiledTextureRegion[] club_cards, spade_cards, heart_cards, diamond_cards;
	public ITiledTextureRegion loading_animated_region, hitButton_region, standButton_region, placeBetButton_region, doubleDownButton_region, dia_yes_btn_region, dia_no_btn_region, minus_btn_region, plus_btn_region;
	public ITiledTextureRegion insurance_btn_region, surrender_btn_region, play_again_btn_region, next_stage_btn_region, min_btn_region, max_btn_region;
	public Music menu_bgm, game_bgm, bet_bgm;
	public Sound gameButtonClick_effect;
	public Font gameFont;
	
	public BoundCamera mCamera;
	public BaseActivity activity;
	public Engine mEngine;
	public VertexBufferObjectManager vbom;
	//endregion

	//region Static Methods
	public static void prepareManager(Engine engine, BoundCamera camera, BaseActivity bActivity, VertexBufferObjectManager vBom) {
		getInstance().mCamera = camera;
		getInstance().activity = bActivity;
		getInstance().vbom = vBom;
		getInstance().mEngine = engine;
	}
	
	public static ResourcesManager getInstance() {
		if(INSTANCE == null) {
			new ResourcesManager();
		}
		return INSTANCE;
	}
	//endregion
	
	//region Font Resources
	private void loadMenuFontResources() {
		
	}
	
	private void loadGameFontResources() {
		FontFactory.setAssetBasePath("font/");
		final ITexture gameFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		gameFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), gameFontTexture, activity.getAssets(), "zrnic.zrnicrg-regular.ttf", 25f, true, Color.WHITE, 2f, Color.WHITE);
		gameFont.load();
	}
	//endregion
	
	//region Splash Scene Resources
	public void loadSplashResources() {
		loadSplashGraphics();
	}
	
	private void loadSplashGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 512, 512, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashAtlas, activity,"splash.png", 0, 0);
		splashAtlas.load();
	}
	
	public void unloadSplashResources() {
		splashAtlas.unload();
		splash_region = null;
		System.gc();
	}
	//endregion
	
	//region Loading Scene Resources
	public void loadLoadingResources() {
		loadLooadingGraphics();
	}
	
	private void loadLooadingGraphics() {
		try{
			loaderTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/loading_scene/").loadFromAsset(activity.getAssets(), "loader_texture.xml");
			loadTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/loading_scene/").loadFromAsset(activity.getAssets(), "loader_pic_texture.xml");
			loaderTexturePack.loadTexture();
			loadTexturePack.loadTexture();
			loaderPackLibrary = loaderTexturePack.getTexturePackTextureRegionLibrary();
			load_picPackLibrary = loadTexturePack.getTexturePackTextureRegionLibrary();
		}catch(final TexturePackParseException e) {
			Debug.e(e);
		}
		loading_animated_region = loaderPackLibrary.get(0, 13, 1);
		loading_region = load_picPackLibrary.get(loader_pic_texture.LOADING_PIC_ID);
	}
	//endregion
	
	//region Main Menu Resources Methods
	public void loadMainMenuResources() {
		loadMainMenuGraphics();
		loadMenuFontResources();
		loadMainMenuAudio();
	}
	
	private void loadMainMenuGraphics() {
		try{
			menuBGTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/menu/").loadFromAsset(activity.getAssets(), "menu_background.xml");
			menuOptionsTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/menu/").loadFromAsset(activity.getAssets(), "menu_buttons.xml");
			dialogBoxTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/dialog/").loadFromAsset(activity.getAssets(), "exit_dialog.xml");
			
			menuBGTexturePack.loadTexture();
			menuOptionsTexturePack.loadTexture();
			dialogBoxTexturePack.loadTexture();
			
			dialogBoxPackLibrary = dialogBoxTexturePack.getTexturePackTextureRegionLibrary(); 
			menuBGPackLibrary = menuBGTexturePack.getTexturePackTextureRegionLibrary();
			menuOptionsPackLibrary = menuOptionsTexturePack.getTexturePackTextureRegionLibrary();
			
		}catch(final TexturePackParseException e) {
			Debug.e(e);
		}
		
		dia_BG_region = dialogBoxPackLibrary.get(exit_dialog.DIALOG_BOX_BG_ID);
		mMenuBackground_region = menuBGPackLibrary.get(menu_background.MENU_BACKGROUND_ID);
		startGame_region = menuOptionsPackLibrary.get(menu_buttons.START_GAME_ID);
		highscore_region = menuOptionsPackLibrary.get(menu_buttons.HIGHSCORE_ID);
		dia_yes_btn_region = dialogBoxPackLibrary.get(exit_dialog.DIALOG_BOX_YES_BUTTON_ID, 1, 3);
		dia_no_btn_region = dialogBoxPackLibrary.get(exit_dialog.DIALOG_BOX_NO_BUTTON_ID, 1, 3);
	}
	
	private void loadMainMenuAudio() {
		try{
			menu_bgm = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), activity, "sfx/menu/menu_bgm.ogg");
		}catch(IOException e) {
			e.printStackTrace();
		}
		menu_bgm.setLooping(true);
		menu_bgm.setVolume(1.0f);
	}
	
	public void unloadMainMenu() {
		menuOptionsTexturePack.unloadTexture();
		menuBGTexturePack.unloadTexture();
		menu_bgm.pause();
		System.gc();
	}
	
	public void loadMainMenu() {
		menuOptionsTexturePack.loadTexture();
		menuBGTexturePack.loadTexture();
		menu_bgm.seekTo(0);
		menu_bgm.play();
	}
	
	//endregion
	
	//region Game Scene Resources Methods
	
	public void loadGameResources() {
		loadGameGraphics();
		loadGameFontResources();
		loadGameAudio();
	}
	
	private void loadGameGraphics() {
		cardsLoad();
		
		try{
			gameBGTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/game/").loadFromAsset(activity.getAssets(), "game_background.xml");
			gameOptionsTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/game/").loadFromAsset(activity.getAssets(), "game_item_buttons.xml");
			gameOverTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/game/").loadFromAsset(activity.getAssets(), "game_over_sprite.xml");
			
			gameBGTexturePack.loadTexture();
			gameOverTexturePack.loadTexture();
			gameOptionsTexturePack.loadTexture();
			
			gameBGPackLibrary = gameBGTexturePack.getTexturePackTextureRegionLibrary();
			gameOverPackLibrary = gameOverTexturePack.getTexturePackTextureRegionLibrary();
			gameOptionsPackLibrary = gameOptionsTexturePack.getTexturePackTextureRegionLibrary();
			
		}catch(final TexturePackParseException e) {
			Debug.e(e);
		}
		
		hitButton_region = gameOptionsPackLibrary.get(game_item_buttons.HIT_BUTTON_ID, 1, 3);
		placeBetButton_region = gameOptionsPackLibrary.get(game_item_buttons.PLACE_BET_BUTTON_ID, 1, 3);
		standButton_region = gameOptionsPackLibrary.get(game_item_buttons.STAND_BUTTON_ID, 1, 3);
		doubleDownButton_region = gameOptionsPackLibrary.get(game_item_buttons.DOUBLE_DOWN_BUTTON_ID, 1, 3);
		min_btn_region = gameOptionsPackLibrary.get(game_item_buttons.MIN_BET_BUTTON_ID, 1, 3);
		max_btn_region = gameOptionsPackLibrary.get(game_item_buttons.MAX_BET_BUTTON_ID, 1, 3);
		plus_btn_region = gameOptionsPackLibrary.get(game_item_buttons.PLUS_BUTTON_ID, 1, 3);
		minus_btn_region = gameOptionsPackLibrary.get(game_item_buttons.MINUS_BUTTON_ID, 1, 3);
		surrender_btn_region = gameOptionsPackLibrary.get(game_item_buttons.SURRENDER_BUTTON_ID, 1, 3);
		insurance_btn_region = gameOptionsPackLibrary.get(game_item_buttons.INSURANCE_BUTTON_ID, 1, 3);
		play_again_btn_region = gameOptionsPackLibrary.get(game_item_buttons.PLAY_AGAIN_BUTTON_ID, 1, 3);
		next_stage_btn_region = gameOptionsPackLibrary.get(game_item_buttons.NEXT_STAGE_BUTTON_ID, 1, 3);
		
		n21_region = gameOverPackLibrary.get(game_over_sprite.N_21_ID);
		bursted_region = gameOverPackLibrary.get(game_over_sprite.BURST_ID);
		win_region = gameOverPackLibrary.get(game_over_sprite.WIN_ID);
		lose_region = gameOverPackLibrary.get(game_over_sprite.LOSE_ID);
		tie_region = gameOverPackLibrary.get(game_over_sprite.TIE_ID);
		
		gameBackground_region = gameBGPackLibrary.get(game_background.GAME_BACKGROUND_ID); 
	}
	
	private void loadGameAudio() {
		
		mSoundManager = new SoundManager(SoundManager.MAX_SIMULTANEOUS_STREAMS_DEFAULT);
		
		try{
			bet_bgm = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), activity, "sfx/game/bet_bgm.ogg");
			game_bgm = MusicFactory.createMusicFromAsset(mEngine.getMusicManager(), activity, "sfx/game/game_bgm.ogg");
			gameButtonClick_effect = SoundFactory.createSoundFromAsset(mSoundManager, activity, "sfx/game/button_click.mp3");
		}catch(IOException e) {
			e.printStackTrace();
		}
		bet_bgm.setLooping(true);
		game_bgm.setLooping(true);
		game_bgm.setVolume(1.0f);
	}
	
	public void unloadGame() {
		gameBGTexturePack.unloadTexture();
		gameOverTexturePack.unloadTexture();
		gameOptionsTexturePack.unloadTexture();
		
		club_cardTexturePack.unloadTexture();
		spade_cardTexturePack.unloadTexture();
		heart_cardTexturePack.unloadTexture();
		diamond_cardTexturePack.unloadTexture();
		
		if(game_bgm.isPlaying()) {
			game_bgm.pause();
		}
		if(bet_bgm.isPlaying()) {
			bet_bgm.pause();			
		}
		
		System.gc();
	}
	
	public void loadGame() {
		gameBGTexturePack.loadTexture();
		gameOverTexturePack.loadTexture();
		gameOptionsTexturePack.loadTexture();
		
		club_cardTexturePack.loadTexture();
		spade_cardTexturePack.loadTexture();
		heart_cardTexturePack.loadTexture();
		diamond_cardTexturePack.loadTexture();
		
		System.gc();
	}
	
	public void unloadGameResources() {
		cardsUnload();
		mSoundManager.releaseAll();
		
		if(game_bgm.isPlaying()) {
			game_bgm.stop();
			game_bgm.release();
		}
		if(bet_bgm.isPlaying()) {
			bet_bgm.stop();
			bet_bgm.release();
		}
		
		gameBGTexturePack = null;
		gameOptionsTexturePack = null;
		gameOverTexturePack = null;
		gameOverPackLibrary = null;
		gameOptionsPackLibrary = null;
		gameBGPackLibrary = null;
		gameBackground_region = null;
		hitButton_region = null;
		placeBetButton_region = null;
		standButton_region = null;
		doubleDownButton_region = null;
		n21_region = null;
		bursted_region = null;
		insurance_btn_region = null;
		surrender_btn_region = null;
		next_stage_btn_region = null;
		play_again_btn_region = null;
		minus_btn_region = null;
		plus_btn_region = null;
		min_btn_region = null;
		max_btn_region = null;
		
		System.gc();
	}
	
	//endregion
	
	//region Card Resources Methods
	private void cardsLoad() {
		club_cards = new ITiledTextureRegion[13];
		spade_cards = new ITiledTextureRegion[13];
		heart_cards = new ITiledTextureRegion[13];
		diamond_cards = new ITiledTextureRegion[13];
		
		try{
			club_cardTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/cards/").loadFromAsset(activity.getAssets(), "Club_Set.xml");
			spade_cardTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/cards/").loadFromAsset(activity.getAssets(), "Spade_Set.xml");
			heart_cardTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/cards/").loadFromAsset(activity.getAssets(), "Heart_Set.xml");
			diamond_cardTexturePack = new TexturePackLoader(activity.getTextureManager(), "gfx/cards/").loadFromAsset(activity.getAssets(), "Diamond_Set.xml");
			
			club_cardTexturePack.loadTexture();
			spade_cardTexturePack.loadTexture();
			heart_cardTexturePack.loadTexture();
			diamond_cardTexturePack.loadTexture();
			
			club_cardPackLibrary = club_cardTexturePack.getTexturePackTextureRegionLibrary();
			spade_cardPackLibrary = spade_cardTexturePack.getTexturePackTextureRegionLibrary();
			heart_cardPackLibrary = heart_cardTexturePack.getTexturePackTextureRegionLibrary();
			diamond_cardPackLibrary = diamond_cardTexturePack.getTexturePackTextureRegionLibrary();
			
		}catch(final TexturePackParseException e) {
			Debug.e(e);
		}
		
		for(int i = 0; i < club_cards.length; i++)
			club_cards[i] = club_cardPackLibrary.get(i, 2, 1);
		for(int i = 0; i < spade_cards.length; i++)
			spade_cards[i] = spade_cardPackLibrary.get(i, 2, 1);
		for(int i = 0; i < heart_cards.length; i++)
			heart_cards[i] = heart_cardPackLibrary.get(i, 2, 1);
		for(int i = 0; i < diamond_cards.length; i++)
			diamond_cards[i] = diamond_cardPackLibrary.get(i, 2, 1);
	}
	
	private void cardsUnload() {
		club_cardTexturePack = null;
		spade_cardTexturePack = null;
		heart_cardTexturePack = null;
		diamond_cardTexturePack = null;
		
		club_cardPackLibrary = null;
		spade_cardPackLibrary = null;
		heart_cardPackLibrary = null;
		diamond_cardPackLibrary = null;
		
		club_cards = null;
		spade_cards = null;
		heart_cards = null;
		diamond_cards = null;
	}
	//endregion

}
