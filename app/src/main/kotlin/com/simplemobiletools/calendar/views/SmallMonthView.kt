package com.simplemobiletools.calendar.views

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.simplemobiletools.calendar.R
import com.simplemobiletools.calendar.extensions.config
import com.simplemobiletools.calendar.helpers.MEDIUM_ALPHA
import com.simplemobiletools.commons.extensions.adjustAlpha
import java.util.*

class SmallMonthView(context: Context, attrs: AttributeSet, defStyle: Int) : View(context, attrs, defStyle) {
    private var paint: Paint
    private var coloredPaint: Paint
    private var dayWidth = 0f
    private var textColor = 0
    private var coloredTextColor = 0
    private var days = 31
    private var firstDay = 0
    private var todaysId = 0
    private var isLandscape = false
    private var mEvents: ArrayList<Int>? = null

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    fun setDays(days: Int) {
        this.days = days
        invalidate()
    }

    fun setFirstDay(firstDay: Int) {
        this.firstDay = firstDay
    }

    fun setEvents(events: ArrayList<Int>?) {
        mEvents = events
        post { invalidate() }
    }

    fun setTodaysId(id: Int) {
        todaysId = id
    }

    init {
        val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.SmallMonthView,
                0, 0)

        try {
            days = attributes.getInt(R.styleable.SmallMonthView_days, 31)
        } finally {
            attributes.recycle()
        }

        val baseColor = context.config.textColor
        textColor = baseColor.adjustAlpha(MEDIUM_ALPHA)
        coloredTextColor = context.config.primaryColor.adjustAlpha(MEDIUM_ALPHA)

        paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            textSize = resources.getDimensionPixelSize(R.dimen.year_view_day_text_size).toFloat()
            textAlign = Paint.Align.RIGHT
        }

        coloredPaint = Paint(paint)
        coloredPaint.color = coloredTextColor
        isLandscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dayWidth == 0f) {
            dayWidth = if (isLandscape) {
                (canvas.width / 9).toFloat()
            } else {
                (canvas.width / 7).toFloat()
            }
        }

        var curId = 1 - firstDay
        for (y in 1..6) {
            for (x in 1..7) {
                if (curId in 1..days) {
                    canvas.drawText(curId.toString(), x * dayWidth, y * dayWidth, getPaint(curId))

                    if (curId == todaysId) {
                        val dividerConstant = if (isLandscape) 6 else 4
                        canvas.drawCircle(x * dayWidth - dayWidth / dividerConstant, y * dayWidth - dayWidth / dividerConstant, dayWidth * 0.41f, coloredPaint)
                    }
                }
                curId++
            }
        }
    }

    private fun getPaint(curId: Int) = if (mEvents?.contains(curId) == true) coloredPaint else paint
}
