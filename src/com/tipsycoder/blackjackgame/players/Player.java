package com.tipsycoder.blackjackgame.players;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.util.adt.align.HorizontalAlign;

import com.cards.core.Card;
import com.cards.core.Card.ACE_VALUE;
import com.cards.core.Card.CARD_NAME;
import com.tipsycoder.blackjackgame.Managers.ResourcesManager;

public class Player{
	
	public enum PLAYER_SLOT{ PLAYER_ONE, PLAYER_TWO, PLAYER_THREE, PLAYER_FOUR, };
	
	private List<Card> mCards;
	private int rotate = 0;
	float pY = 0,  pX = 0;
	private final int VALUE_MAX = 21;
	private PLAYER_SLOT playerId;
	private final float pOneX = 230f, pOneY = 90f, pTwoX = 700f, pTwoY = 245.2f;
	private final float pThreeX = 65.2f, pThreeY = 570.5f, pFourX = 557.1f, pFourY = 710f;
	private Text titleText;
	private boolean stay = false;
	
	public Player(PLAYER_SLOT player_id, float textX, float textY) {
		mCards = new ArrayList<Card>();
		setUpRotate(player_id);
		playerId = player_id;
		titleText = new Text(textX, textY, ResourcesManager.getInstance().gameFont, "0123456789 -: abcdefghijklmnopqrstuvwxyz QWERTYUIOPASDFGHJKLZXCVBNM", new TextOptions(HorizontalAlign.LEFT), ResourcesManager.getInstance().vbom);
	}
	
	public Text getTitleText() {
		return titleText;
	}
	
	public void addCard(Card card) {
		card.setRotationCenter(0, 0);
		card.setRotation(rotate);
		mCards.add(card);
	}
	
	public int getCardsValue() {
		int cardsValue = 0;
		
		aceValue();
		
		for(Card card : mCards) {
			cardsValue += card.getValue();
		}
		return cardsValue;
	}
	
	private void aceValue() {
		int aceCnt = 0, value = 0;
		
		for(Card card : mCards) {
			if(card.getName() != CARD_NAME.ACE) {
				value += card.getValue();
				continue;
			}
			aceCnt++;
		}
		
		// If the player hands only consist of ace(s)
		if(mCards.size() == aceCnt) {
			boolean swtch = true;
			for(Card card : mCards) {
				if(swtch) {
					card.setAceValue(ACE_VALUE.ELEVEN);
					swtch = false;
				} else if (!swtch) {
					card.setAceValue(ACE_VALUE.ONE);
				}
			}
			return;
		}
		
		if(aceCnt > 1) {
			int tempNum = aceCnt, cnt = 0;
			while(tempNum > 1) {
				tempNum--;
				cnt++;
			}
			value += cnt;
		}
		
		if(value > 10) {
			for(Card card : mCards) {
				if(card.getName() == CARD_NAME.ACE) {
					card.setAceValue(ACE_VALUE.ONE);
					break;
				}
			}
		}
	}
	
	private void setUpRotate(PLAYER_SLOT id) {
		if(id == PLAYER_SLOT.PLAYER_ONE) {
			rotate = 0;
			pX = pOneX;
			pY = pOneY;
		}
		
		if(id == PLAYER_SLOT.PLAYER_TWO) {
			rotate = 90;
			pX = pTwoX;
			pY = pTwoY;
		}
		
		if(id == PLAYER_SLOT.PLAYER_THREE) {
			rotate = 90;
			pX = pThreeX;
			pY = pThreeY;
		}
		
		if(id == PLAYER_SLOT.PLAYER_FOUR) {
			rotate = 0;
			pX = pFourX;
			pY = pFourY;
		}
	}
	
	public boolean isBursted() {
		return getCardsValue() > VALUE_MAX;
	}
	
	public boolean isMaxCards() {
		return mCards.size() >= 5;
	}
	
	@SuppressWarnings("deprecation")
	public List<Card> display() {
		List<Card> returnList = new ArrayList<Card>();
		
		float cardX = pX, cardY = pY;
		final int padding = 5;
		
		for(Card card : mCards) {
			if(playerId == PLAYER_SLOT.PLAYER_ONE) {
				card.setPosition(cardX, cardY);
				cardX += card.getWidthScaled() + padding;
			}
			if(playerId == PLAYER_SLOT.PLAYER_TWO) {
				card.setPosition(cardX, cardY + (card.getWidth() + padding));
				cardY += card.getWidthScaled() + padding;
			}
			
			if(playerId == PLAYER_SLOT.PLAYER_THREE) {
				card.setPosition(cardX, cardY + (card.getWidth() + padding));
				cardY -= card.getWidthScaled() + padding;
			}
			
			if(playerId == PLAYER_SLOT.PLAYER_FOUR) {
				card.setPosition(cardX, cardY);
				cardX -= card.getWidthScaled() + padding;
			}
			
			returnList.add(card);
		}
		return returnList;
	}
	
	public PLAYER_SLOT getPlayerId() {
		return playerId;
	}
	
	public boolean isStay() {
		return stay;
	}
	
	public void setStay(final boolean value) {
		stay = value;
	}

}



