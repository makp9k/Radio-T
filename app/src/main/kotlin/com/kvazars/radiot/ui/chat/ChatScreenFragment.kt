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
import kotlinx.android.synthetic.main.fragment_chat.*


/**
 * Created by Leo on 12.04.2017.
 */
class ChatScreenFragment : Fragment(), ChatScreenContract.View {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private var autoScrollEnabled: Boolean = true

    private val markdownSpannableBuilder = MarkdownSpannableBuilder()

    private lateinit var presenter: ChatScreenPresenter

    //endregion

    //region LIFE CYCLE ----------------------------------------------------------------------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = ChatScreenPresenter(this, RadioTApplication.getAppComponent(context!!).getGitterClient())

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
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
            if (rv.adapter.itemCount > 0) {
                if (animate) {
                    rv.smoothScrollToPosition(0)
                } else {
                    rv.scrollToPosition(0)
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

    private var setChatMessagesRunnable: Runnable? = null

    override fun showChatMessages(messages: Collection<ChatScreenContract.View.ChatMessageModel>) {
        if (setChatMessagesRunnable != null) {
            view?.removeCallbacks(setChatMessagesRunnable)
        }

        setChatMessagesRunnable = Runnable {
            (recycler_view.adapter as ChatMessagesAdapter).setMessages(messages)
            tryAutoScroll()
        }

        view?.post(setChatMessagesRunnable)
    }

    override fun buildFormattedMessageText(rawMessage: String): CharSequence {
        return markdownSpannableBuilder.build(rawMessage, context!!)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    //endregion
}


