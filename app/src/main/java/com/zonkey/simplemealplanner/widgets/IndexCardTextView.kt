package com.zonkey.simplemealplanner.widgets

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import com.zonkey.simplemealplanner.R
import kotlinx.android.synthetic.main.index_card_text_view.view.index_card_color_line
import kotlinx.android.synthetic.main.index_card_text_view.view.index_card_text_view

class IndexCardTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

  var text: String? = null
    set(value) {
      field = value
      index_card_text_view.text = text
    }

  private var textSize: Float? = null
    set(value) {
      field = value
      index_card_text_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize ?: 12f)
    }

  private var textStyle: String? = null
    set(value) {
      field = value
      val textStyleInt = when (textStyle) {
        "bold" -> Typeface.BOLD
        "italic" -> Typeface.ITALIC
        "bold|italic" -> Typeface.BOLD_ITALIC
        "italic|bold" -> Typeface.BOLD_ITALIC
        else -> Typeface.NORMAL
      }
      index_card_text_view.setTypeface(Typeface.DEFAULT, textStyleInt)
    }

  @ColorRes
  var lineColor: Int = 0
    set(value) {
      field = value
      index_card_color_line.setBackgroundColor(lineColor)
    }

  init {
    View.inflate(context, R.layout.index_card_text_view, this)
    orientation = LinearLayout.VERTICAL

    attrs?.let {
      context.obtainStyledAttributes(it, R.styleable.IndexCardTextView, 0, 0).apply {
        text = getString(R.styleable.IndexCardTextView_text)
        textStyle = getString(R.styleable.IndexCardTextView_textStyle)
        textSize = getDimension(R.styleable.IndexCardTextView_textSize,
            context.resources.getDimension(R.dimen.default_text_size))
        lineColor = getColor(R.styleable.IndexCardTextView_lineColor, 1)
        recycle()
      }
    }
  }
}