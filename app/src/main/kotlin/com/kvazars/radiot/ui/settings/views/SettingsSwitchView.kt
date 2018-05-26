package com.kvazars.radiot.ui.settings.views

import android.content.Context
import android.databinding.BindingAdapter
import android.databinding.InverseBindingListener
import android.databinding.InverseBindingMethod
import android.databinding.InverseBindingMethods
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.v7.widget.SwitchCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import android.widget.TextView
import com.kvazars.radiot.R

@InverseBindingMethods(
    InverseBindingMethod(type = SettingsSwitchView::class, attribute = "checked")
)
class SettingsSwitchView : ConstraintLayout {

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["checkedAttrChanged"], requireAll = false)
        fun setListener(view: SettingsSwitchView, bindingListener: InverseBindingListener) {
            view.setOnCheckedChangeListener(
                CompoundButton.OnCheckedChangeListener { _, _ ->
                    bindingListener.onChange()
                }
            )
        }
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initWithAttrs(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initWithAttrs(attrs)
    }

    private val switch: SwitchCompat
    var checked: Boolean
        get() = switch.isChecked
        set(value) {
            switch.isChecked = value
        }

    init {
        View.inflate(context, R.layout.view_settings_switch, this)

        val outValue = TypedValue()
        context.theme.resolveAttribute(R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)

        val horizontalPadding = context.resources.getDimensionPixelSize(R.dimen.material_layout_keylines_screen_edge_margin)
        minHeight = resources.getDimensionPixelSize(R.dimen.material_layout_vertical_spacing_list_item)
        setPadding(horizontalPadding, 0, horizontalPadding, 0)

        switch = findViewById(R.id.switch_btn)
        setOnClickListener {
            switch.isChecked = !switch.isChecked
        }

        ConstraintSet().let {
            it.clone(this)
            it.connect(R.id.title, ConstraintSet.END, R.id.switch_btn, ConstraintSet.START)
            it.connect(R.id.description, ConstraintSet.END, R.id.switch_btn, ConstraintSet.START)
            it.applyTo(this)
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