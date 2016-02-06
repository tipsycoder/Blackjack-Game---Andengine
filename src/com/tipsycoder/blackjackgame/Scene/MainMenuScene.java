package com.tipsycoder.blackjackgame.Scene;



import org.andengine.audio.music.Music;
import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.GradualScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.tipsycoder.blackjackgame.Managers.SceneManager;
import com.tipsycoder.blackjackgame.Managers.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private final int MENU_START = 0, MENU_HIGHSCORE = 1;
	@Override
	public void createScene() {
		createBackground();
		childMenuScene();
		rManager.menu_bgm.play();
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.MENU_SCENE;
	}
	
	@Override
	public Music getCurrentMusicPlaying() {
			return rManager.menu_bgm;
	}
	
	private void createBackground() {
		attachChild(new Sprite(rManager.mCamera.getCenterX(), rManager.mCamera.getCenterY(), rManager.mMenuBackground_region, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
		
	}
	
	private void childMenuScene() {
		menuChildScene = new MenuScene(mCamera);
		menuChildScene.setPosition(400, 149);
		
		final IMenuItem start = new GradualScaleMenuItemDecorator(new SpriteMenuItem(MENU_START, rManager.startGame_region, vbom), 1.2f, 1, 0.1f);
		final IMenuItem quit = new GradualScaleMenuItemDecorator(new SpriteMenuItem(MENU_HIGHSCORE, rManager.highscore_region, vbom), 1.2f, 1, 0.1f);
		
		menuChildScene.addMenuItem(start);
		menuChildScene.addMenuItem(quit);
		
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		
		start.setPosition(0, -30f);
		quit.setPosition(0, -90f);
		menuChildScene.setOnMenuItemClickListener(this);
		setChildScene(menuChildScene);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case MENU_START:
				SceneManager.getInstance().createGameScene(engine);
				break;
			case MENU_HIGHSCORE:
				//System.exit(0);
				break;
			default:
				break;
		}
		return false;
	}

}
