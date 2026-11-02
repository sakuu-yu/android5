package cn.edu.sicnu.cardgame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeckTest {

    @Test
    fun newDeckHasFiftyTwoUniqueCards() {
        val deck = Deck()
        val drawn = (1..52).mapNotNull { deck.drawRandomCard() }

        assertEquals(52, drawn.size)
        assertEquals(52, drawn.map { it.toString() }.toSet().size)
        assertNull(deck.drawRandomCard())
    }

    @Test
    fun deckContainsEverySuitAndRank() {
        val deck = Deck()
        val drawn = (1..52).mapNotNull { deck.drawRandomCard() }

        Card.validSuits.forEach { suit ->
            assertEquals(13, drawn.count { it.toString().startsWith(suit) })
        }
        Card.rankStrings.forEach { rank ->
            assertEquals(4, drawn.count { it.toString().drop(1) == rank })
        }
    }
}
