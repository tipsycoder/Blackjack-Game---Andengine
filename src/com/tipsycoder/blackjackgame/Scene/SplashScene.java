package com.tipsycoder.blackjackgame.Scene;

import org.andengine.audio.music.Music;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

import com.tipsycoder.blackjackgame.Managers.SceneManager.SceneType;

public class SplashScene extends BaseScene {

	private Sprite splash;
	
	@Override
	public void createScene() {
		splash = new Sprite(0, 0, rManager.splash_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		setBackground(new Background(Color.WHITE));
		splash.setPosition(rManager.mCamera.getCenterX(), rManager.mCamera.getCenterY());
		attachChild(splash);
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();

	}

	@Override
	public void onBackKeyPressed() {
		return;

	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SPLASH_SCENE;
	}

	@Override
	public Music getCurrentMusicPlaying() {
		return null;
	}
	
	

}
