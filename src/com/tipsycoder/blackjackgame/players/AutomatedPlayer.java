package com.tipsycoder.blackjackgame.players;

import com.cards.core.Card;
import com.cards.core.Deck;

public class AutomatedPlayer extends Player {
	
	private final int VAL_LIMIT = 17;
	
	public AutomatedPlayer(float textX, float textY) {
		super(PLAYER_SLOT.PLAYER_FOUR, textX, textY);
	}
	
	public void move(Deck deck) {
		final int cVal = this.getCardsValue();
		
		if(cVal >= VAL_LIMIT) {
			setStay(true);
			return;
		}
		
		if(cVal <= VAL_LIMIT) {
			Card card = deck.draw();
			card.faceDown();
			this.addCard(card);
		}
	}
}
