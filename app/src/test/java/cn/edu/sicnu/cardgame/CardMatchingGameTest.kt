package cn.edu.sicnu.cardgame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardMatchingGameTest {

    @Test
    fun newGameHasSixteenCardsAndZeroScore() {
        val game = CardMatchingGame(16)

        assertEquals(16, game.cardCount)
        assertEquals(0, game.score)
        for (index in 0 until game.cardCount) {
            assertFalse(game.cardAtIndex(index).isChosen)
            assertFalse(game.cardAtIndex(index).isMatched)
        }
    }

    @Test
    fun choosingCardCostsOnePointAndSelectsIt() {
        val game = CardMatchingGame(16)

        game.chooseCardAtIndex(0)

        assertEquals(-1, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun choosingSelectedCardAgainDeselectsItWithoutPenalty() {
        val game = CardMatchingGame(16)

        game.chooseCardAtIndex(0)
        val scoreAfterFirstChoice = game.score
        game.chooseCardAtIndex(0)

        assertEquals(scoreAfterFirstChoice, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun matchingCardsStayMatchedAndAddBonus() {
        val game = CardMatchingGame(16)
        val (first, second) = findPair(game) { it > 0 }
        val expectedMatchScore = game.cardAtIndex(first).match(arrayOf(game.cardAtIndex(second)))

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)

        assertEquals(-2 + expectedMatchScore * game.MATCH_BONUS, game.score)
        assertTrue(game.cardAtIndex(first).isMatched)
        assertTrue(game.cardAtIndex(second).isMatched)
    }

    @Test
    fun mismatchingCardsPenalizeAndDeselectFirstCard() {
        val game = CardMatchingGame(16)
        val (first, second) = findPair(game) { it == 0 }

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)

        assertEquals(
            -game.COST_TO_CHOOSE * 2 - game.MISMATCH_PENALTY,
            game.score
        )
        assertFalse(game.cardAtIndex(first).isChosen)
        assertFalse(game.cardAtIndex(first).isMatched)
        assertTrue(game.cardAtIndex(second).isChosen)
        assertFalse(game.cardAtIndex(second).isMatched)
    }

    @Test
    fun matchedCardsCannotBeChosenAgain() {
        val game = CardMatchingGame(16)
        val (first, second) = findPair(game) { it > 0 }

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        val scoreAfterMatch = game.score
        game.chooseCardAtIndex(first)

        assertEquals(scoreAfterMatch, game.score)
        assertTrue(game.cardAtIndex(first).isMatched)
        assertTrue(game.cardAtIndex(second).isMatched)
    }

    /**
     * With 16 cards and 4 suits there is always at least one matching pair,
     * and there is always a non-matching pair as well.
     */
    private fun findPair(
        game: CardMatchingGame,
        predicate: (matchScore: Int) -> Boolean
    ): Pair<Int, Int> {
        for (first in 0 until game.cardCount) {
            for (second in first + 1 until game.cardCount) {
                val matchScore = game.cardAtIndex(first).match(arrayOf(game.cardAtIndex(second)))
                if (predicate(matchScore)) return first to second
            }
        }
        throw AssertionError("No suitable card pair found")
    }
}
