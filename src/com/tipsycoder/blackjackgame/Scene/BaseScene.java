package com.tipsycoder.blackjackgame.Scene;

import org.andengine.audio.music.Music;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.tipsycoder.blackjackgame.BaseActivity;
import com.tipsycoder.blackjackgame.Managers.ResourcesManager;
import com.tipsycoder.blackjackgame.Managers.SceneManager.SceneType;

public abstract class BaseScene extends Scene {
	
	protected BoundCamera mCamera;
	protected BaseActivity activity;
	protected VertexBufferObjectManager vbom;
	protected Engine engine;
	protected ResourcesManager rManager;
	
	
	public BaseScene() {
		rManager = ResourcesManager.getInstance();
		mCamera = rManager.mCamera;
		activity = rManager.activity;
		vbom = rManager.vbom;
		engine = rManager.mEngine;
		createScene();
	}
	
	public abstract void createScene();
	public abstract void disposeScene();
	public abstract void onBackKeyPressed();
	public abstract SceneType getSceneType();
	public abstract Music getCurrentMusicPlaying();
}
