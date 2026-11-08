package cn.edu.sicnu.cardgame

/**
 * Model of a three-card matching game.
 *
 * A player flips three cards. If all three share the same rank or the same
 * suit they are matched and scored; otherwise the first two cards are turned
 * back over. The model only depends on [Card] and [Deck], so it can be tested
 * on the JVM without Android.
 */
class CardMatchingGame(private val initialCardCount: Int) {
    var score = 0
        private set

    private val cards = mutableListOf<Card>()

    init {
        reset()
    }

    /** Starts a new game with the same number of cards and a zero score. */
    fun reset() {
        score = 0
        cards.clear()
        val deck = Deck()
        repeat(initialCardCount) {
            deck.drawRandomCard()?.let(cards::add)
        }
    }

    fun cardAtIndex(index: Int): Card {
        return cards[index]
    }

    /** Number of cards that must be flipped to evaluate a match. */
    private val cardsPerMatch = Card.CARDS_PER_MATCH

    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (card.isMatched) return

        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val chosenCards = cards.filter { it.isChosen && !it.isMatched }
        if (chosenCards.size >= cardsPerMatch - 1) {
            val otherCards = chosenCards
                .take(cardsPerMatch - 1)
                .toTypedArray()
            val matchScore = card.match(otherCards)

            if (matchScore > 0) {
                score += matchScore * MATCH_BONUS
                card.isMatched = true
                otherCards.forEach { it.isMatched = true }
            } else {
                score -= MISMATCH_PENALTY
                otherCards.forEach { it.isChosen = false }
            }
        }

        score -= COST_TO_CHOOSE
        card.isChosen = true
    }

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 4
    val COST_TO_CHOOSE = 1

    val cardCount: Int
        get() = cards.size
}
