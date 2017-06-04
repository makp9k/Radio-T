package com.kvazars.radiot.ui.chat.views

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

/**
 * Created by Admin on 28.07.2017.
 */
class StrikethroughEmphasis : CustomNode(), Delimited {

    private var delimiter: String = "~~"

    fun setDelimiter(delimiter: String) {
        this.delimiter = delimiter
    }

    override fun getOpeningDelimiter(): String {
        return delimiter
    }

    override fun getClosingDelimiter(): String {
        return delimiter
    }
}