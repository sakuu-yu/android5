package cn.edu.sicnu.cardgame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardMatchingGameTest {

    @Test
    fun newGameHasTwentyFourCardsAndZeroScore() {
        val game = CardMatchingGame(24)

        assertEquals(24, game.cardCount)
        assertEquals(0, game.score)
        for (index in 0 until game.cardCount) {
            assertFalse(game.cardAtIndex(index).isChosen)
            assertFalse(game.cardAtIndex(index).isMatched)
        }
    }

    @Test
    fun choosingCardCostsOnePointAndSelectsIt() {
        val game = CardMatchingGame(24)

        game.chooseCardAtIndex(0)

        assertEquals(-1, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun choosingSelectedCardAgainDeselectsItWithoutPenalty() {
        val game = CardMatchingGame(24)

        game.chooseCardAtIndex(0)
        val scoreAfterFirstChoice = game.score
        game.chooseCardAtIndex(0)

        assertEquals(scoreAfterFirstChoice, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
    }

    @Test
    fun matchingThreeCardsStayMatchedAndAddBonus() {
        val game = CardMatchingGame(24)
        val (first, second, third) = findTriple(game) { it > 0 }
        val expectedMatchScore = game.cardAtIndex(first)
            .match(arrayOf(game.cardAtIndex(second), game.cardAtIndex(third)))

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        game.chooseCardAtIndex(third)

        assertTrue(expectedMatchScore > 0)
        assertEquals(-3 + expectedMatchScore * game.MATCH_BONUS, game.score)
        assertTrue(game.cardAtIndex(first).isMatched)
        assertTrue(game.cardAtIndex(second).isMatched)
        assertTrue(game.cardAtIndex(third).isMatched)
    }

    @Test
    fun mismatchingThreeCardsPenalizeAndDeselectFirstTwoCards() {
        val game = CardMatchingGame(24)
        val (first, second, third) = findTriple(game) { it == 0 }

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        game.chooseCardAtIndex(third)

        assertEquals(
            -game.COST_TO_CHOOSE * 3 - game.MISMATCH_PENALTY,
            game.score
        )
        assertFalse(game.cardAtIndex(first).isChosen)
        assertFalse(game.cardAtIndex(first).isMatched)
        assertFalse(game.cardAtIndex(second).isChosen)
        assertFalse(game.cardAtIndex(second).isMatched)
        assertTrue(game.cardAtIndex(third).isChosen)
        assertFalse(game.cardAtIndex(third).isMatched)
    }

    @Test
    fun matchedCardsCannotBeChosenAgain() {
        val game = CardMatchingGame(24)
        val (first, second, third) = findTriple(game) { it > 0 }

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)
        game.chooseCardAtIndex(third)
        val scoreAfterMatch = game.score

        game.chooseCardAtIndex(first)

        assertEquals(scoreAfterMatch, game.score)
        assertTrue(game.cardAtIndex(first).isMatched)
        assertTrue(game.cardAtIndex(second).isMatched)
        assertTrue(game.cardAtIndex(third).isMatched)
    }

    @Test
    fun resetStartsANewGameWithZeroScoreAndHiddenCards() {
        val game = CardMatchingGame(24)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)

        game.reset()

        assertEquals(0, game.score)
        assertEquals(24, game.cardCount)
        for (index in 0 until game.cardCount) {
            assertFalse(game.cardAtIndex(index).isChosen)
            assertFalse(game.cardAtIndex(index).isMatched)
        }
    }

    /**
     * With 24 cards drawn from a deck containing only four suits, at least
     * one suit must contain six cards, so a matching three-card set always
     * exists. A non-matching set necessarily exists as well, because no more
     * than eight cards can share two ranks across four suits.
     */
    private fun findTriple(
        game: CardMatchingGame,
        predicate: (matchScore: Int) -> Boolean
    ): Triple<Int, Int, Int> {
        for (first in 0 until game.cardCount) {
            for (second in first + 1 until game.cardCount) {
                for (third in second + 1 until game.cardCount) {
                    val matchScore = game.cardAtIndex(first).match(
                        arrayOf(game.cardAtIndex(second), game.cardAtIndex(third))
                    )
                    if (predicate(matchScore)) return Triple(first, second, third)
                }
            }
        }
        throw AssertionError("No suitable card triple found")
    }
}
