package com.tipsycoder.blackjackgame.Managers;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import com.tipsycoder.blackjackgame.Scene.BaseScene;
import com.tipsycoder.blackjackgame.Scene.GameScene;
import com.tipsycoder.blackjackgame.Scene.LoadingScene;
import com.tipsycoder.blackjackgame.Scene.MainMenuScene;
import com.tipsycoder.blackjackgame.Scene.SplashScene;
import com.tipsycoder.blackjackgame.Scene.GameScene.AMOUNT_PLAYER;

public class SceneManager {
	
	private final static SceneManager INSTANCE = new SceneManager();
	
	private BaseScene currentScene, gameScene, loadingScene, splashScene, mainMenuScene;
	private Engine engine = ResourcesManager.getInstance().mEngine;
	private SceneType currentSceneType = SceneType.SPLASH_SCENE;
	
	public enum SceneType { LOADING_SCENE, SPLASH_SCENE, GAME_SCENE, MENU_SCENE, }
	
	public static SceneManager getInstance() {
		if(INSTANCE == null) {
			return new SceneManager();
		}
		return INSTANCE;
	}
	
	public void setScene(SceneType type) {
		switch(type) {
			case SPLASH_SCENE:
				setScene(splashScene);
				break;
			case LOADING_SCENE:
				setScene(loadingScene);
				break;
			case MENU_SCENE:
				setScene(mainMenuScene);
				break;
			case GAME_SCENE:
				setScene(gameScene);
				break;
			default:
				break;
		}
	}
	
	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourcesManager.getInstance().loadSplashResources();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}
	
	private void disposeSplashScene() {
		ResourcesManager.getInstance().unloadSplashResources();
		splashScene.disposeScene();
		splashScene = null;
	}
	
	public void createMainMenuScene() {
		ResourcesManager.getInstance().loadMainMenuResources();
		ResourcesManager.getInstance().loadLoadingResources();
		mainMenuScene = new MainMenuScene();
		setScene(mainMenuScene);
		disposeSplashScene();
	}
	
	public void createGameScene(final Engine mEngine) {
		ResourcesManager.getInstance().mCamera.set(0, 0, 800, 480);
		loadingScene = new LoadingScene();
		setScene(loadingScene);
		ResourcesManager.getInstance().unloadMainMenu();
		mEngine.registerUpdateHandler(new TimerHandler(1.4f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				engine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadGameResources();
				gameScene = new GameScene(AMOUNT_PLAYER.ONE);
				setScene(gameScene);
			}
		}));
	}
	
	public void loadGameScene(final Engine mEngine) {
		ResourcesManager.getInstance().mCamera.set(0, 0, 800, 480);
		setScene(loadingScene);
		ResourcesManager.getInstance().unloadMainMenu();
		mEngine.registerUpdateHandler(new TimerHandler(0.5f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				engine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadGame();
				//gameScene = new GameScene(AMOUNT_PLAYER.ONE);
				setScene(gameScene);
			}
		}));
	}
	
	private void disposeGameScene() {
		gameScene.disposeScene();
		gameScene = null;
		System.gc();
	}
	
	public void loadMainMenu(final Engine mEngine) {
		ResourcesManager.getInstance().mCamera.set(0, 0, 800, 480);
		//loadingScene = new LoadingScene();
		setScene(loadingScene);
		disposeGameScene();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				engine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMainMenu();
				setScene(mainMenuScene);
			}
		}));
	}
	
	private void setScene(BaseScene scene) {
		currentScene = scene;
		engine.setScene(scene);
		currentSceneType = currentScene.getSceneType();
	}
	
	public SceneType getCurrentSceneType() {
		return currentSceneType;
	}
	
	public BaseScene getCurrentScene() {
		return currentScene;
	}
}
