package cn.edu.sicnu.cs.why.myapplication

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cn.edu.sicnu.cardgame.CardMatchingGame

class CardGameActivity : AppCompatActivity() {

    companion object {
        private const val CARD_COUNT = 16
        private const val COLUMNS = 4
        private const val CARD_HEIGHT_DP = 98
        private const val CARD_MARGIN_DP = 4
    }

    private var game = CardMatchingGame(CARD_COUNT)
    private lateinit var gridCards: GridLayout
    private lateinit var tvScore: TextView

    private val cells = mutableListOf<View>()
    private val faces = mutableListOf<TextView>()
    private val backs = mutableListOf<View>()

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

        bestScore = ProfileStore.bestScore(this)
        gridCards = findViewById(R.id.gridCards)
        tvScore = findViewById(R.id.tvScore)

        findViewById<View>(R.id.btnReset).setOnClickListener { startNewGame() }
        findViewById<View>(R.id.btnExit).setOnClickListener { finish() }

        buildBoard()
    }

    private fun startNewGame() {
        game = CardMatchingGame(CARD_COUNT)
        buildBoard()
    }

    private fun buildBoard() {
        gridCards.removeAllViews()
        cells.clear()
        faces.clear()
        backs.clear()

        for (index in 0 until game.cardCount) {
            val cell = layoutInflater.inflate(R.layout.item_card, gridCards, false)

            val params = GridLayout.LayoutParams(
                GridLayout.spec(index / COLUMNS),
                GridLayout.spec(index % COLUMNS, 1f)
            )
            params.width = 0
            params.height = dp(CARD_HEIGHT_DP)
            params.setMargins(dp(CARD_MARGIN_DP), dp(CARD_MARGIN_DP), dp(CARD_MARGIN_DP), dp(CARD_MARGIN_DP))

            cell.setOnClickListener { onCardClicked(index) }
            gridCards.addView(cell, params)

            cells.add(cell)
            faces.add(cell.findViewById(R.id.tvCardFace))
            backs.add(cell.findViewById(R.id.vCardBack))
        }

        refreshBoard()
    }

    private fun onCardClicked(index: Int) {
        if (game.cardAtIndex(index).isMatched) return
        game.chooseCardAtIndex(index)
        refreshBoard()
    }

    private fun refreshBoard() {
        for (index in 0 until game.cardCount) {
            val card = game.cardAtIndex(index)
            val face = faces[index]
            val back = backs[index]
            val cell = cells[index]

            when {
                card.isMatched -> {
                    showFace(face, back, index, matched = true)
                    cell.alpha = 0.55f
                }

                card.isChosen -> {
                    showFace(face, back, index, matched = false)
                    cell.alpha = 1f
                }

                else -> {
                    face.visibility = View.GONE
                    back.visibility = View.VISIBLE
                    cell.alpha = 1f
                }
            }
        }

        tvScore.text = "得分：${game.score}"

        if (game.score > bestScore) {
            bestScore = game.score
            ProfileStore.saveBestScore(this, bestScore)
        }
    }

    private fun showFace(face: TextView, back: View, index: Int, matched: Boolean) {
        val text = game.cardAtIndex(index).toString()
        face.text = text
        face.setTextColor(
            if (text.contains("♥") || text.contains("♦")) {
                Color.parseColor("#C62828")
            } else {
                Color.parseColor("#212121")
            }
        )
        face.setBackgroundResource(
            if (matched) R.drawable.bg_card_matched else R.drawable.bg_card_face
        )
        face.visibility = View.VISIBLE
        back.visibility = View.GONE
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
