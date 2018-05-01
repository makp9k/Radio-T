package com.kvazars.radiot.ui.chat

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import com.kvazars.radiot.R
import com.kvazars.radiot.RadioTApplication
import com.kvazars.radiot.ui.chat.views.MarkdownSpannableBuilder
import kotlinx.android.synthetic.main.screen_chat.*


/**
 * Created by Leo on 12.04.2017.
 */
class ChatScreenFragment : Fragment(), ChatScreenContract.View {
    //region CONSTANTS -----------------------------------------------------------------------------

    companion object {
        const val AUTO_SCROLL_ENABLED = "autoScrollEnabled"
    }

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private lateinit var linearLayoutManager: LinearLayoutManager
    private var autoScrollEnabled: Boolean = true

    private val markdownSpannableBuilder = MarkdownSpannableBuilder()

    private lateinit var presenter: ChatScreenPresenter

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.screen_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.stackFromEnd = true
        recycler_view.layoutManager = linearLayoutManager
        recycler_view.adapter = ChatMessagesAdapter(context!!)
        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    autoScrollEnabled = !recycler_view.canScrollVertically(1)
                    if (!recycler_view.canScrollVertically(-1)) {
                        presenter.loadPrevious()
                    }
                }
            }
        })

        presenter = ChatScreenPresenter(this, RadioTApplication.getAppComponent(context!!).getChatInteractor())

        showLoadingIndicator()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(AUTO_SCROLL_ENABLED, autoScrollEnabled)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        autoScrollEnabled = savedInstanceState?.getBoolean(AUTO_SCROLL_ENABLED) ?: true

        presenter.init()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            recycler_view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    tryAutoScroll(false)
                    recycler_view.viewTreeObserver.removeOnPreDrawListener(this)
                    return false
                }
            })
        }
    }

    private fun tryAutoScroll(animate: Boolean = true) {
        if (autoScrollEnabled) {
            val rv = recycler_view

            val itemCount = rv.adapter.itemCount
            if (itemCount > 0) {
                if (animate) {
                    rv.smoothScrollToPosition(itemCount)
                } else {
                    rv.scrollToPosition(itemCount)
                }
            }
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun showChatMessages(messages: Collection<ChatScreenContract.View.ChatMessageModel>) {
        (recycler_view.adapter as ChatMessagesAdapter).setMessages(messages)
        loading_indicator.visibility = View.GONE
        tryAutoScroll()
    }

    override fun buildFormattedMessageText(rawMessage: String): CharSequence {
        return markdownSpannableBuilder.build(rawMessage, context!!)
    }

    override fun showLoadingIndicator() {
        loading_indicator.visibility = View.VISIBLE
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}


