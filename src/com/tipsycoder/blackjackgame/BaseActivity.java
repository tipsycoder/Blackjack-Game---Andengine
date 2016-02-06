package com.tipsycoder.blackjackgame;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;

import com.tipsycoder.blackjackgame.Managers.ResourcesManager;
import com.tipsycoder.blackjackgame.Managers.SceneManager;

public class BaseActivity extends BaseGameActivity {

	public final static int CAMERA_WIDTH = 800;
	public final static int CAMERA_HEIGHT = 480;
	private BoundCamera camera;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions mEngineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new FillResolutionPolicy(), camera);
		mEngineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		mEngineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		return mEngineOptions;
	}
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		Engine mEngine = new LimitedFPSEngine(pEngineOptions, 60);
		return mEngine;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException {
		ResourcesManager.prepareManager(mEngine, camera, this, getVertexBufferObjectManager());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
		
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
			throws IOException {
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException {
		mEngine.registerUpdateHandler(new TimerHandler(2.0f, new ITimerCallback() {

			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().createMainMenuScene();
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(this.isGameLoaded())
			System.exit(0);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(this.isGameLoaded())
			if(SceneManager.getInstance().getCurrentScene().getCurrentMusicPlaying() != null )
				SceneManager.getInstance().getCurrentScene().getCurrentMusicPlaying().pause();
	}
	
	@Override 
	protected synchronized void onResume() {
		super.onResume();
		System.gc();
		if(this.isGameLoaded())
			if(SceneManager.getInstance().getCurrentScene().getCurrentMusicPlaying() != null )
				SceneManager.getInstance().getCurrentScene().getCurrentMusicPlaying().play();
	}

}
