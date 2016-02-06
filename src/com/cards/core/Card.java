package com.cards.core;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.cards.core.Deck.SUITS;
import com.tipsycoder.blackjackgame.Managers.ResourcesManager;
import com.tipsycoder.blackjackgame.Scene.GameScene;

public class Card extends TiledSprite{
	
	public enum ACE_VALUE { ONE, ELEVEN, }
	public enum CARD_NAME { ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING, } 
	
	private int value;
	private CARD_NAME name;
	private final int FACE_UP = 0, FACE_DOWN = 1;
	private final float scaledSize = 1.2f, scaledTouch = 1.8f; 
	private boolean faceDown;
	private SUITS suit;

	public Card(float pX, float pY, ITiledTextureRegion textureRegion, VertexBufferObjectManager vbo, int val, SUITS cardSuits) {
		super(pX, pY, textureRegion, vbo);
		setScale(scaledSize);
		value = val;
		suit = cardSuits;
		valueToName(value);
		faceUp();
	}
	
	@Override
	protected void preDraw(GLState pGLState, Camera pCamera) {
		super.preDraw(pGLState, pCamera);
		pGLState.enableDither();
	}
	
	@Override
	public String toString() {
		return name + " OF " + suit.toString();
	}
	
	@Override
	public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float px, final float pY) {
		if(pSceneTouchEvent.isActionDown()) {
			if(!isFaceDown()) {
				this.setScale(scaledTouch);
				GameScene.value = this.getStrValue();
				ResourcesManager.getInstance().mEngine.registerUpdateHandler(new TimerHandler(0.3f, new ITimerCallback() {
					@Override
					public void onTimePassed(TimerHandler pTimerHandler) {
						ResourcesManager.getInstance().mEngine.unregisterUpdateHandler(pTimerHandler);
						Card.this.clearEntityModifiers();
						Card.this.registerEntityModifier(new ScaleModifier(0.4f, Card.this.getScaleX(), scaledSize));
					}
				}));
				ResourcesManager.getInstance().gameButtonClick_effect.play();
			} else {
				GameScene.value = "N/A";
			}
		}
		return true;
	}
	
	public void faceDown() {
		faceDown = true;
		this.setCurrentTileIndex(FACE_DOWN);
	}
	
	public void faceUp() {
		faceDown = false;
		this.setCurrentTileIndex(FACE_UP);
	}
	
	private void valueToName(int cVal) {
		
		if(cVal == 1) {
			name = CARD_NAME.ACE;
			value = 11;
		}
		if(cVal == 2) 
			name = CARD_NAME.TWO;
		if(cVal == 3) 
			name = CARD_NAME.THREE;
		if(cVal == 4)
			name = CARD_NAME.FOUR;
		if(cVal == 5)
			name = CARD_NAME.FIVE;
		if(cVal == 6)
			name = CARD_NAME.SIX;
		if(cVal == 7)
			name = CARD_NAME.SEVEN;
		if(cVal == 8)
			name = CARD_NAME.EIGHT;
		if(cVal == 9) 
			name = CARD_NAME.NINE;
		if(cVal == 10)
			name = CARD_NAME.TEN;
		if(cVal == 11) {
			name = CARD_NAME.JACK;
			value = 10;
		}
		if(cVal == 12) {
			name = CARD_NAME.QUEEN;
			value = 10;
		}
		if(cVal == 13) {
			name = CARD_NAME.KING;
			value = 10;
		}
	}
	
	public void setAceValue(ACE_VALUE aValue) {
		if(value == 1  || value == 11) {
			if(aValue == ACE_VALUE.ONE) 
				value = 1;
			
			if(aValue == ACE_VALUE.ELEVEN)
				value = 11;
		}
	}
	
	public CARD_NAME getName() {
		return name;
	}
	
	public int getValue() {
		return value;
	}
	
	public boolean isFaceDown() {
		return faceDown;
	}
	
	private String getStrValue() {
		return ((Integer)value).toString();
	}
}
