package com.kvazars.radiot.ui.chat.views

import org.commonmark.internal.inline.EmphasisDelimiterProcessor
import org.commonmark.node.Text


/**
 * Created by Admin on 28.07.2017.
 */
class StrikethroughDelimiterProcessor : EmphasisDelimiterProcessor('~') {

    override fun getMinLength(): Int {
        return 2
    }

    override fun process(opener: Text, closer: Text, delimiterUse: Int) {
        val emphasis = StrikethroughEmphasis()
        var tmp = opener.next
        while (tmp != null && tmp !== closer) {
            val next = tmp.next
            emphasis.appendChild(tmp)
            tmp = next
        }

        opener.insertAfter(emphasis)
    }
}