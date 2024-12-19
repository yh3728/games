package org.douggschwind.games.boardgames.monopoly;

import java.util.HashMap;
import java.util.Map;

import org.douggschwind.games.boardgames.monopoly.Player.Avatar;
import org.douggschwind.games.boardgames.monopoly.policy.ConservativeUseOfGetOutOfJailFreeCardPolicy;
import org.douggschwind.games.boardgames.monopoly.policy.LowestValuedAssetLiquidationPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the interesting methods on the Player class
 * @author Doug Gschwind
 */
public class PlayerTest {
	
	private Player underTest;
	
	@BeforeEach
	public void beforeTest() {
		underTest = new Player("Gurn", Avatar.Dog, new ConservativeUseOfGetOutOfJailFreeCardPolicy(), new LowestValuedAssetLiquidationPolicy());
	}
	
	@Test
	public void testInitialBankAccountBalance() {
		assertEquals(1500, underTest.getBankAccountBalance());
	}
	
	@Test
	public void testCanPayBillWithCash() {
		assertTrue(underTest.canPayBillWithCash(1499));
		assertTrue(underTest.canPayBillWithCash(1500));
		assertFalse(underTest.canPayBillWithCash(1501));
	}
	
	@Test
	public void testReceivePayment() {
		int startingBankAccountBalance = underTest.getBankAccountBalance();
		underTest.receivePayment(200);
		assertEquals(startingBankAccountBalance + 200, underTest.getBankAccountBalance());
	}
	
	@Test
	public void testMakePayment() {
		int startingBankAccountBalance = underTest.getBankAccountBalance();
		assertFalse(underTest.makePayment(startingBankAccountBalance + 1));
		assertTrue(underTest.makePayment(100));
		assertEquals(startingBankAccountBalance - 100, underTest.getBankAccountBalance());
	}
	
	@Test
	public void testAcceptOwnership() {
		assertEquals(0, underTest.getOwnedProperties().size());
		assertEquals(0, underTest.getOwnedRailroads().size());
		assertEquals(0, underTest.getOwnedUtilities().size());
		
		int startingBankAccountBalance = underTest.getBankAccountBalance();
		
		assertTrue(underTest.makePayment(GameBoardFactory.MEDITERRANEAN_AVENUE.getPurchasePrice()));
		GameBoardFactory.MEDITERRANEAN_AVENUE.setOwner(underTest);
		assertTrue(underTest.makePayment(GameBoardFactory.READING_RAILROAD.getPurchasePrice()));
		GameBoardFactory.READING_RAILROAD.setOwner(underTest);
		assertTrue(underTest.makePayment(GameBoardFactory.WATER_WORKS_UTILITY.getPurchasePrice()));
		GameBoardFactory.WATER_WORKS_UTILITY.setOwner(underTest);
		
		final int collectivePurchasePrice = GameBoardFactory.MEDITERRANEAN_AVENUE.getPurchasePrice() +
                GameBoardFactory.READING_RAILROAD.getPurchasePrice() +
                GameBoardFactory.WATER_WORKS_UTILITY.getPurchasePrice();
		
		assertEquals(startingBankAccountBalance - collectivePurchasePrice,
				            underTest.getBankAccountBalance());
		
		assertEquals(1, underTest.getOwnedProperties().size());
		assertEquals(underTest, GameBoardFactory.MEDITERRANEAN_AVENUE.getOwner());
		assertFalse(GameBoardFactory.MEDITERRANEAN_AVENUE.isMortgaged());
		
		assertEquals(1, underTest.getOwnedRailroads().size());
		assertEquals(underTest, GameBoardFactory.READING_RAILROAD.getOwner());
		assertFalse(GameBoardFactory.READING_RAILROAD.isMortgaged());
		
		assertEquals(1, underTest.getOwnedUtilities().size());
		assertEquals(underTest, GameBoardFactory.WATER_WORKS_UTILITY.getOwner());
		assertFalse(GameBoardFactory.WATER_WORKS_UTILITY.isMortgaged());
		
		// Now that the Player owns one Property, one Railroad, and one Utility, require them to make a 
		// sufficiently large payment such that it causes them to mortgage all three holdings and is
		// then left with a measly USD $50.
		final int expectedBankAccountBalanceAfterPayment = 50;
		int manufacturedPaymentAmount = (underTest.getBankAccountBalance() + (collectivePurchasePrice / 2)) - expectedBankAccountBalanceAfterPayment;
			
		assertTrue(underTest.makePayment(manufacturedPaymentAmount));
		assertEquals(expectedBankAccountBalanceAfterPayment, underTest.getBankAccountBalance());
		assertTrue(GameBoardFactory.MEDITERRANEAN_AVENUE.isMortgaged());
		assertTrue(GameBoardFactory.READING_RAILROAD.isMortgaged());
		assertTrue(GameBoardFactory.WATER_WORKS_UTILITY.isMortgaged());
	}
	
	@Test
	public void testRollDice() {
		final int NUM_TIMES_TO_ROLL_DICE = 1000;
		
		Map<Integer, Integer> diceRollSummaryMap = new HashMap<>();
		int numTimesDoublesRolled = 0;
		
		for (int i = 0;i < NUM_TIMES_TO_ROLL_DICE;i++) {
			DiceRollResult diceRollResult = underTest.rollDice();
			int diceRollTotal = diceRollResult.getDiceRollTotal();
			if ((diceRollTotal < 2) || (diceRollTotal > 12))  {
				fail("Not supported dice roll total : " + diceRollTotal);
			}
			
			Integer numTimesThisNumberRolled = diceRollSummaryMap.get(diceRollTotal);
			if (numTimesThisNumberRolled == null) {
				diceRollSummaryMap.put(diceRollTotal, 1);
			} else {
				diceRollSummaryMap.put(diceRollTotal, numTimesThisNumberRolled + 1);
			}
			
			if (diceRollResult.wereDoublesRolled()) {
				numTimesDoublesRolled++;
			}
		}
		
		// Rough expectations, statistically, concerning dice roll probability
		// 2 : 1 in 36
		// 3 : 2 in 36
		// 4 : 3 in 36
		// 5 : 4 in 36
		// 6 : 5 in 36
		// 7 : 6 in 36
		// 8 : 5 in 36
		// 9 : 4 in 36
		// 10 : 3 in 36
		// 11 : 2 in 36
		// 12 : 1 in 36
		// Doubles being rolled : 6 in 36
		
//		System.out.println("Dice Roll Results :");
//		System.out.println("\t2 : num times rolled : " + diceRollSummaryMap.get(2) + " for " + 100.0 * (diceRollSummaryMap.get(2) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t3 : num times rolled : " + diceRollSummaryMap.get(3) + " for " + 100.0 * (diceRollSummaryMap.get(3) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t4 : num times rolled : " + diceRollSummaryMap.get(4) + " for " + 100.0 * (diceRollSummaryMap.get(4) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t5 : num times rolled : " + diceRollSummaryMap.get(5) + " for " + 100.0 * (diceRollSummaryMap.get(5) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t6 : num times rolled : " + diceRollSummaryMap.get(6) + " for " + 100.0 * (diceRollSummaryMap.get(6) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t7 : num times rolled : " + diceRollSummaryMap.get(7) + " for " + 100.0 * (diceRollSummaryMap.get(7) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t8 : num times rolled : " + diceRollSummaryMap.get(8) + " for " + 100.0 * (diceRollSummaryMap.get(8) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t9 : num times rolled : " + diceRollSummaryMap.get(9) + " for " + 100.0 * (diceRollSummaryMap.get(9) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t10 : num times rolled : " + diceRollSummaryMap.get(10) + " for " + 100.0 * (diceRollSummaryMap.get(10) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t11 : num times rolled : " + diceRollSummaryMap.get(11) + " for " + 100.0 * (diceRollSummaryMap.get(11) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		System.out.println("\t12 : num times rolled : " + diceRollSummaryMap.get(12) + " for " + 100.0 * (diceRollSummaryMap.get(12) / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
//		
//		System.out.println("\tDoubles : num times rolled : " + numTimesDoublesRolled + " for " + 100.0 * (numTimesDoublesRolled / (float) NUM_TIMES_TO_ROLL_DICE) + "%");
	}
}