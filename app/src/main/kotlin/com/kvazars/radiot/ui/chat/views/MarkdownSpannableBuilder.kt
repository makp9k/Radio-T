package com.kvazars.radiot.ui.chat.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.*
import android.webkit.URLUtil
import com.kvazars.radiot.ui.chat.views.text.style.BlockquoteSpan
import org.commonmark.node.*
import org.commonmark.parser.Parser

/**
 * Created by Admin on 23.07.2017.
 */
class MarkdownSpannableBuilder {

    private val parser: Parser = Parser
        .builder()
        .customDelimiterProcessor(StrikethroughDelimiterProcessor())
        .build()

    fun build(text: String, context: Context): Spannable {
        val builder = SpannableStringBuilder()
        val node = parser.parse(text)
        node.accept(MarkdownVisitor(builder, context))
        return trim(builder)
    }

    private fun trim(spannable: SpannableStringBuilder): SpannableStringBuilder {
        checkNotNull(spannable)
        var trimEnd = 0

        var text = spannable.toString()

        while (text.isNotEmpty() && text.endsWith("\n")) {
            text = text.substring(0, text.length - 1)
            trimEnd += 1
        }

        return spannable.delete(spannable.length - trimEnd, spannable.length)
    }

    /*
     * Bold                 **bold**
     * Italics              *italics*
     * Strikethrough        ~~strikethrough~~
     * Header               # H1 ## H2 ### H3
     * item                 * item
     * Blockquote           > blockquote
     * @somebody (mention)  @somebody
     * #123 (issue)         #123
     * Link                 [title](http://)
     * Image                ![alt](http://)
     * code                 `code`
     * formatted code       ```code```
     *
     *
     *
        **bold**
        *italics*
        ~~strikethrough~~
        # H1
        ## H2
        ### H3
        * item
        > blockquote
        @somebody
        #123
        [title](http://)
        ![alt](http://static.hasselblad.com/2017/07/H61C-B0003517-29.jpg)
        `code`
        ```code```
     */
    class MarkdownVisitor(
        private val builder: SpannableStringBuilder,
        private val context: Context
    ) : AbstractVisitor() {

        private var indent = -1

        private val urlRegex: Regex = Regex(
            "(https?|ftp|file)://[-A-Z0-9+&@#/%?=~_|!:,.;]*[A-Z0-9+&@#/%=~_|]",
            RegexOption.IGNORE_CASE
        )

        private fun <T : Node?> wrapSpan(span: Any, node: T) {
            val start = builder.length
            super.visitChildren(node)
            val end = builder.length
            builder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        private fun <T : Node?> wrapSpans(spans: List<Any>, node: T) {
            val start = builder.length
            super.visitChildren(node)
            val end = builder.length
            for (span in spans) {
                builder.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        override fun visit(text: Text?) {
            text?.let {
                val start = builder.length
                builder.append(text.literal)

                urlRegex.findAll(text.literal).forEach {
                    if (URLUtil.isValidUrl(it.value)) {
                        builder.setSpan(URLSpan(it.value), start + it.range.start, start + it.range.endInclusive + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            }
            super.visit(text)
        }

        override fun visit(strongEmphasis: StrongEmphasis?) {
            wrapSpan(StyleSpan(Typeface.BOLD), strongEmphasis)
        }

        override fun visit(emphasis: Emphasis?) {
            wrapSpan(StyleSpan(Typeface.ITALIC), emphasis)
        }

        override fun visit(hardLineBreak: HardLineBreak?) {
            builder.append('\n')
            super.visit(hardLineBreak)
        }

        override fun visit(thematicBreak: ThematicBreak?) {
            builder.append('\n')
            super.visit(thematicBreak)
        }

        override fun visit(softLineBreak: SoftLineBreak?) {
            builder.append('\n')
            super.visit(softLineBreak)
        }

        override fun visit(blockQuote: BlockQuote?) {
            wrapSpan(BlockquoteSpan(context), blockQuote)
        }

        override fun visit(bulletList: BulletList?) {
            indent++
            super.visit(bulletList)
            indent--
        }

        override fun visit(code: Code?) {
            code?.let {
                visit(Text(code.literal))
            }
        }

        override fun visit(fencedCodeBlock: FencedCodeBlock?) {
            fencedCodeBlock?.let {
                visit(Text(fencedCodeBlock.literal))
            }
        }

        override fun visit(heading: Heading?) {
            wrapSpans(
                listOf(
                    RelativeSizeSpan(mapHeadingLevel(heading?.level)),
                    StyleSpan(Typeface.BOLD)
                ),
                heading
            )
            builder.append('\n')
        }

        private fun mapHeadingLevel(level: Int?): Float {
            return when (level) {
                1 -> 2f
                2 -> 1.5f
                3 -> 1.17f
                4 -> 1.12f
                5 -> .83f
                6 -> .75f
                else -> 1f
            }
        }

        override fun visit(htmlInline: HtmlInline?) {
            htmlInline?.let {
                visit(Text(htmlInline.literal))
            }
        }

        override fun visit(htmlBlock: HtmlBlock?) {
            htmlBlock?.let {
                visit(Text(htmlBlock.literal))
            }
        }

        override fun visit(image: Image?) {
            image?.let {
                visit(Text(image.destination))
            }
        }

        override fun visit(indentedCodeBlock: IndentedCodeBlock?) {
            indentedCodeBlock?.let {
                visit(Text(indentedCodeBlock.literal))
            }
        }

        override fun visit(link: Link?) {
            link?.let {
                wrapSpan(
                    URLSpan(link.destination),
                    link
                )
            }
        }

        override fun visit(listItem: ListItem?) {
            val i = indent
            wrapSpan(object : BulletSpan(10) {
                override fun getLeadingMargin(first: Boolean): Int {
                    return super.getLeadingMargin(first) + i * 50
                }

                override fun drawLeadingMargin(
                    c: Canvas?, p: Paint?, x: Int, dir: Int, top: Int, baseline: Int, bottom: Int,
                    text: CharSequence?, start: Int, end: Int, first: Boolean, l: Layout?
                ) {
                    super.drawLeadingMargin(c, p, x + i * 50, dir, top, baseline, bottom, text, start, end, first, l)
                }
            }, listItem)
        }

        override fun visit(orderedList: OrderedList?) {
            indent++
            super.visit(orderedList)
            indent--
        }

        override fun visit(paragraph: Paragraph?) {
            super.visit(paragraph)
            builder.append('\n')
        }

        override fun visit(customNode: CustomNode?) {
            when (customNode) {
                is StrikethroughEmphasis -> wrapSpan(StrikethroughSpan(), customNode)
                else -> super.visit(customNode)
            }
        }
    }

}