package org.douggschwind.games.boardgames.monopoly.actioncard;

import org.douggschwind.games.common.DeckOfCards;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Doug Gschwind
 */
public class DeckFactoryTest {
	
	@Test
	public void testChanceDeck() {
		final int EXPECTED_CHANCE_DECK_SIZE = 16;

		DeckOfCards<ActionCard> chanceDeck = DeckFactory.createChanceDeck();
		assertEquals(EXPECTED_CHANCE_DECK_SIZE, chanceDeck.size());

		for (int i = 0;i < 3;i++) {
			chanceDeck.shuffle();
			Set<ActionCard> dealtCards = new HashSet<>();
			try {
				while (true) {
					dealtCards.add(chanceDeck.dealCard());
				}
			} catch (IllegalStateException ignored) {
				Set<String> uniqueCardNames = dealtCards.stream().map(dc -> dc.getCardName()).collect(Collectors.toSet());
				assertEquals(EXPECTED_CHANCE_DECK_SIZE, uniqueCardNames.size());
				assertEquals(EXPECTED_CHANCE_DECK_SIZE, dealtCards.size());
			}
		}
	}

	@Test
	public void testCommunityChestDeck() {
		final int EXPECTED_COMMUNITY_CHEST_DECK_SIZE = 17;

		DeckOfCards<ActionCard> communityChestDeck = DeckFactory.createCommunityChestDeck();
		assertEquals(EXPECTED_COMMUNITY_CHEST_DECK_SIZE, communityChestDeck.size());

		for (int i = 0;i < 3;i++) {
			communityChestDeck.shuffle();
			Set<ActionCard> dealtCards = new HashSet<>();
			try {
				while (true) {
					dealtCards.add(communityChestDeck.dealCard());
				}
			} catch (IllegalStateException ignored) {
				Set<String> uniqueCardNames = dealtCards.stream().map(dc -> dc.getCardName()).collect(Collectors.toSet());
				assertEquals(EXPECTED_COMMUNITY_CHEST_DECK_SIZE, uniqueCardNames.size());
				assertEquals(EXPECTED_COMMUNITY_CHEST_DECK_SIZE, dealtCards.size());
			}
		}
	}
}