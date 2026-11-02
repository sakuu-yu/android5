package cn.edu.sicnu.cardgame

import org.junit.Assert.assertEquals
import org.junit.Test

class CardTest {

    @Test
    fun cardToStringCombinesSuitAndRank() {
        assertEquals("♥A", Card("♥", "A").toString())
        assertEquals("♠10", Card("♠", "10").toString())
    }

    @Test
    fun matchingRankScoresFour() {
        val card = Card("♥", "8")
        assertEquals(4, card.match(arrayOf(Card("♦", "8"))))
    }

    @Test
    fun matchingSuitScoresOne() {
        val card = Card("♥", "8")
        assertEquals(1, card.match(arrayOf(Card("♥", "K"))))
    }

    @Test
    fun differentSuitAndRankScoresZero() {
        val card = Card("♥", "8")
        assertEquals(0, card.match(arrayOf(Card("♦", "K"))))
    }

    @Test
    fun matchRequiresExactlyOneOtherCard() {
        val card = Card("♥", "8")
        assertEquals(0, card.match(emptyArray()))
        assertEquals(0, card.match(arrayOf(Card("♥", "8"), Card("♥", "8"))))
    }
}
