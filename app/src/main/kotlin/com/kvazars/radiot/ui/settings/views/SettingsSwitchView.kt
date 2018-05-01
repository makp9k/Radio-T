package com.kvazars.radiot.ui.settings.views

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.SwitchCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import com.kvazars.radiot.R

class SettingsSwitchView : ConstraintLayout {

    constructor(context: Context?): super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initWithAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initWithAttrs(attrs)
    }

    private val switch: SwitchCompat
    var isChecked: Boolean
        get() = switch.isChecked
        set(value) {
            switch.isChecked = value
        }

    init {
        View.inflate(context, R.layout.view_settings_switch, this)

        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)

        val horizontalPadding = context.resources.getDimensionPixelSize(R.dimen.material_layout_keylines_screen_edge_margin)
        minHeight = resources.getDimensionPixelSize(R.dimen.material_layout_vertical_spacing_list_item)
        setPadding(horizontalPadding, 0, horizontalPadding, 0)

        switch = findViewById(R.id.switch_btn)
        setOnClickListener {
            switch.isChecked = !switch.isChecked
        }
    }

    private fun initWithAttrs(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray = context.theme.obtainStyledAttributes(it, R.styleable.SettingsSwitchView, 0, 0)
            try {
                val title = typedArray.getText(R.styleable.SettingsSwitchView_ssw_title)
                val description = typedArray.getText(R.styleable.SettingsSwitchView_ssw_description)

                findViewById<TextView>(R.id.title).text = title
                findViewById<TextView>(R.id.description).text = description
            } finally {
                typedArray.recycle()
            }
        }
    }

    fun setOnCheckedChangeListener(listener: CompoundButton.OnCheckedChangeListener) {
        switch.setOnCheckedChangeListener(listener)
    }
}