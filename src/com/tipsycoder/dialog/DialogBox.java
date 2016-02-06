package com.tipsycoder.dialog;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.tipsycoder.blackjackgame.Managers.ResourcesManager;

public class DialogBox extends Sprite implements OnClickListener{

	public enum OPTION {
		YES, CANCEL, NULL
	};

	private ButtonSprite yesBtn, noBtn;
	private VertexBufferObjectManager vbom;
	private float yesBtnX, yesBtnY, noBtnX, noBtnY;
	private int paddingX = 10, paddingY = 8;
	private boolean yesClicked = false, cancelClicked = false;

	public DialogBox(ITextureRegion bgRegion, VertexBufferObjectManager vbom,
			ITiledTextureRegion yesTextureButton,
			ITiledTextureRegion cancelTextureRegion, Scene pScene) {
		super(0, 0, bgRegion, vbom);
		this.vbom = vbom;
		setUpButton(yesTextureButton, cancelTextureRegion, pScene);
	}

	public OPTION showConfirmDialogBox(Text pText) {
		
		if(yesClicked) {
			yesClicked = false;
			ResourcesManager.getInstance().gameButtonClick_effect.play();
			return OPTION.YES;
		}
		if(cancelClicked) {
			cancelClicked = false;
			ResourcesManager.getInstance().gameButtonClick_effect.play();
			return OPTION.CANCEL;
		}

		return OPTION.NULL;
	}

	private void setUpButton(ITiledTextureRegion yesTextureRegion,
			ITiledTextureRegion cancelTextureRegion,
			Scene pScene) {
		
		yesBtn = new ButtonSprite(0, 0, yesTextureRegion, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		noBtn = new ButtonSprite(0, 0, cancelTextureRegion, vbom) {
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		
		pScene.registerTouchArea(yesBtn);
		pScene.registerTouchArea(noBtn);
		yesBtn.setOnClickListener(this);
		noBtn.setOnClickListener(this);
		
		yesBtnY = paddingY + yesBtn.getHeight() / 2;
		yesBtnX = (yesBtn.getWidth() / 2) + paddingX;
		noBtnY = paddingY + noBtn.getHeight() / 2;
		noBtnX = (this.getWidth() - (noBtn.getWidth() / 2)) - paddingX;
		
		yesBtn.setPosition(yesBtnX, yesBtnY);
		noBtn.setPosition(noBtnX, noBtnY);
		
		this.attachChild(yesBtn);
		this.attachChild(noBtn);
	}

	@Override
	public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		if(pButtonSprite == yesBtn) {
			yesClicked = true;
		}
		if(pButtonSprite == noBtn) {
			cancelClicked = true;
		}
		
		
	}

}
