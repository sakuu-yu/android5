package cn.edu.sicnu.cs.why.myapplication

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cardgame.CardMatchingGame

/**
 * Controller of the game. The Activity owns the [CardMatchingGame] model,
 * configures the RecyclerView with the [CardAdapter], and reacts to card
 * clicks and button presses.
 *
 * The card count and column count come from integer resources, so the
 * portrait and landscape layouts can use different numbers of cards.
 */
class CardGameActivity : AppCompatActivity() {

    private lateinit var game: CardMatchingGame
    private lateinit var adapter: CardAdapter
    private lateinit var rvCards: RecyclerView
    private lateinit var tvScore: TextView

    private var cardCount = 0
    private var columns = 0
    private var bestScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cardGameRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cardCount = resources.getInteger(R.integer.card_count)
        columns = resources.getInteger(R.integer.card_columns)
        game = CardMatchingGame(cardCount)
        bestScore = ProfileStore.bestScore(this)

        tvScore = findViewById(R.id.tvScore)
        rvCards = findViewById(R.id.rvCards)
        rvCards.layoutManager = GridLayoutManager(this, columns)

        adapter = CardAdapter(game) { index -> onCardClicked(index) }
        rvCards.adapter = adapter
        rvCards.doOnLayout {
            val size = calculateCardSize(rvCards)
            // Post the update so notifyDataSetChanged runs outside the layout pass.
            rvCards.post { adapter.setCardSize(size) }
        }

        findViewById<View>(R.id.btnReset).setOnClickListener { startNewGame() }
        findViewById<View>(R.id.btnExit).setOnClickListener { finish() }

        updateScore()
    }

    private fun startNewGame() {
        game.reset()
        adapter.notifyDataSetChanged()
        updateScore()
    }

    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)
        adapter.notifyDataSetChanged()
        updateScore()
    }

    private fun updateScore() {
        tvScore.text = getString(R.string.score_format, game.score)

        if (game.score > bestScore) {
            bestScore = game.score
            ProfileStore.saveBestScore(this, bestScore)
        }
    }

    /**
     * Picks the largest square that fits in each grid cell. Cards are centred
     * in their cells, so the board stays tidy in both orientations.
     */
    private fun calculateCardSize(recycler: RecyclerView): Int {
        if (columns <= 0 || cardCount <= 0) return 0
        val rows = (cardCount + columns - 1) / columns
        val spacing = dp(6)

        val availableWidth =
            recycler.width - recycler.paddingLeft - recycler.paddingRight
        val availableHeight =
            recycler.height - recycler.paddingTop - recycler.paddingBottom

        val cellWidth = availableWidth / columns
        val cellHeight = availableHeight / rows
        return maxOf(minOf(cellWidth, cellHeight) - spacing, dp(40))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
