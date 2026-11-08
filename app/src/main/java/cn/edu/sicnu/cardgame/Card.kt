package cn.edu.sicnu.cardgame

class Card(private var suit: String, private var rank: String, var isChosen: Boolean = false, var isMatched: Boolean = false) {
    companion object {
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")

        /** Number of cards that must be flipped together to form a match. */
        const val CARDS_PER_MATCH = 3

        /** Score value when all matched cards share the same rank. */
        const val RANK_MATCH_SCORE = 8

        /** Score value when all matched cards share the same suit. */
        const val SUIT_MATCH_SCORE = 4
    }

    override fun toString(): String {
        return suit + rank
    }

    /** True when the card is a heart or diamond, so the UI can paint it red. */
    val isRed: Boolean
        get() = suit == "♥" || suit == "♦"

    /**
     * Returns a positive score when this card together with [otherCards]
     * forms a three-card match. Three cards match when they all have the
     * same rank (higher score) or all have the same suit.
     */
    fun match(otherCards: Array<Card>): Int {
        if (otherCards.size != CARDS_PER_MATCH - 1) return 0

        val allCards = arrayOf(this, otherCards[0], otherCards[1])
        return when {
            allCards.all { it.rank == rank } -> RANK_MATCH_SCORE
            allCards.all { it.suit == suit } -> SUIT_MATCH_SCORE
            else -> 0
        }
    }
}
