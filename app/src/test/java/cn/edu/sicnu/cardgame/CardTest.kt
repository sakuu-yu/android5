package cn.edu.sicnu.cardgame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CardTest {

    @Test
    fun cardToStringCombinesSuitAndRank() {
        assertEquals("♥A", Card("♥", "A").toString())
        assertEquals("♠10", Card("♠", "10").toString())
    }

    @Test
    fun redSuitsAreReportedAsRed() {
        assertTrue(Card("♥", "A").isRed)
        assertTrue(Card("♦", "K").isRed)
        assertFalse(Card("♠", "A").isRed)
        assertFalse(Card("♣", "K").isRed)
    }

    @Test
    fun threeCardsOfSameRankScoreRankBonus() {
        val card = Card("♥", "8")
        val others = arrayOf(Card("♦", "8"), Card("♠", "8"))

        assertEquals(Card.RANK_MATCH_SCORE, card.match(others))
    }

    @Test
    fun threeCardsOfSameSuitScoreSuitBonus() {
        val card = Card("♥", "8")
        val others = arrayOf(Card("♥", "K"), Card("♥", "3"))

        assertEquals(Card.SUIT_MATCH_SCORE, card.match(others))
    }

    @Test
    fun twoOfTheSameRankAndOneOfTheSameSuitDoesNotMatch() {
        val card = Card("♥", "8")
        val others = arrayOf(Card("♦", "8"), Card("♥", "K"))

        assertEquals(0, card.match(others))
    }

    @Test
    fun differentSuitsAndRanksScoreZero() {
        val card = Card("♥", "8")
        val others = arrayOf(Card("♦", "K"), Card("♠", "3"))

        assertEquals(0, card.match(others))
    }

    @Test
    fun matchRequiresExactlyTwoOtherCards() {
        val card = Card("♥", "8")

        assertEquals(0, card.match(emptyArray()))
        assertEquals(0, card.match(arrayOf(Card("♥", "8"))))
        assertEquals(
            0,
            card.match(arrayOf(Card("♥", "8"), Card("♦", "8"), Card("♠", "8")))
        )
    }
}
