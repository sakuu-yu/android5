package cn.edu.sicnu.cs.why.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * A simple placeholder box with a border and a diagonal cross, matching the
 * avatar / card-back sketches in the design.
 */
class CrossBoxView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.SQUARE
    }
    private val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var borderWidth = dp(2f)
    private var crossWidth = dp(1.5f)

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.CrossBoxView)
            borderWidth = a.getDimension(R.styleable.CrossBoxView_borderWidth, borderWidth)
            crossWidth = a.getDimension(R.styleable.CrossBoxView_crossWidth, crossWidth)
            borderPaint.color = a.getColor(R.styleable.CrossBoxView_borderColor, 0xFF333333.toInt())
            crossPaint.color = a.getColor(R.styleable.CrossBoxView_crossColor, 0xFF8C8C8C.toInt())
            a.recycle()
        } else {
            borderPaint.color = 0xFF333333.toInt()
            crossPaint.color = 0xFF8C8C8C.toInt()
        }
        borderPaint.strokeWidth = borderWidth
        crossPaint.strokeWidth = crossWidth
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val inset = borderWidth / 2f
        canvas.drawRect(inset, inset, width - inset, height - inset, borderPaint)
        canvas.drawLine(inset, inset, width - inset, height - inset, crossPaint)
        canvas.drawLine(width - inset, inset, inset, height - inset, crossPaint)
    }

    private fun dp(value: Float): Float = value * resources.displayMetrics.density
}
