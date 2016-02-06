package com.cards.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.tipsycoder.blackjackgame.Managers.ResourcesManager;

public class Deck {
	
	public enum SUITS { CLUBS, SPADES, HEARTS, DIAMONDS, }
	
	List<Card> deck;
	private final int DECK_MAX = 52, SUITS_MAX = 13;
	private ResourcesManager rManager = ResourcesManager.getInstance();
	
	public Deck() {
		deck = new ArrayList<Card>();
		setUpDeck();
		shuffle();
	}
	
	private void setUpDeck() {
		int cnt = 0;
		for(int i = 0; i < (DECK_MAX / 4); i++) {
			
			if(cnt < SUITS_MAX) {
				deck.add(new Card(0, 0, rManager.club_cards[cnt], rManager.vbom, cnt + 1, SUITS.CLUBS));
				deck.add(new Card(0, 0, rManager.spade_cards[cnt], rManager.vbom, cnt + 1, SUITS.SPADES));
				deck.add(new Card(0, 0, rManager.heart_cards[cnt], rManager.vbom, cnt + 1, SUITS.HEARTS));
				deck.add(new Card(0, 0, rManager.diamond_cards[cnt], rManager.vbom, cnt + 1, SUITS.DIAMONDS));
				cnt++;
			}
		}
	}
	
	public boolean isEmpty() {
		return deck.isEmpty();
	}
	
	public void shuffle() {
		
		Random rand = new Random();
		List<Card> tempDeck = new ArrayList<Card>(deck);
		int randNum = 0;
		deck.clear();
		
		while(tempDeck.size() > 0) {
			randNum = rand.nextInt(tempDeck.size());
			deck.add(tempDeck.get(randNum));
			tempDeck.remove(randNum);
		}
	}
	
	public int deckSize() {
		return deck.size();
	}
	
	public Card draw() {
		Card card = deck.get(0);
		deck.remove(0);
		return card;
	}
}
