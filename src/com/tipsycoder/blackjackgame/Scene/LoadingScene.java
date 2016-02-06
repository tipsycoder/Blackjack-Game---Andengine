/**
 * 
 */
package com.tipsycoder.blackjackgame.Scene;

import java.util.Arrays;

import org.andengine.audio.music.Music;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

import com.tipsycoder.blackjackgame.Managers.SceneManager.SceneType;

/**
 * @author TipsyCoder
 *
 */
public class LoadingScene extends BaseScene {

	private Sprite loadingPic;
	private AnimatedSprite loadingAnimated;
	
	@Override
	public void createScene() {
		
		setBackground(new Background(Color.BLACK));
		
		loadingPic = new Sprite(0, 0, rManager.loading_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		loadingPic.setScale(2.0f);
		loadingPic.setPosition(rManager.mCamera.getCenterX(), rManager.mCamera.getCenterY() + loadingPic.getHeight());
		attachChild(loadingPic);
		
		loadingAnimated = new AnimatedSprite(0, 0, rManager.loading_animated_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		final long[] LOADER_ANIMATE = new long[13];
		Arrays.fill(LOADER_ANIMATE, 100);
		loadingAnimated.animate(LOADER_ANIMATE, 0, 12, true);
		
		loadingAnimated.setScale(1.5f);
		loadingAnimated.setPosition(rManager.mCamera.getCenterX(), rManager.mCamera.getCenterY() - loadingAnimated.getHeight());
		attachChild(loadingAnimated);
	}

	@Override
	public void disposeScene() {
		loadingPic.detachSelf();
		loadingAnimated.detachSelf();
		loadingPic.dispose();
		loadingAnimated.dispose();
		this.detachSelf();
		this.dispose();
	}

	@Override
	public void onBackKeyPressed() {
		return;

	}

	@Override
	public SceneType getSceneType() {
		return SceneType.LOADING_SCENE;
	}
	
	@Override
	public Music getCurrentMusicPlaying() {
		return null;
	}

}
