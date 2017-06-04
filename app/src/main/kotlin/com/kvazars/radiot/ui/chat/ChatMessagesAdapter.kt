package com.kvazars.radiot.ui.chat

import android.content.Context
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.kvazars.radiot.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Admin on 04.06.2017.
 */
class ChatMessagesAdapter(context: Context) : RecyclerView.Adapter<ChatMessagesAdapter.ChatMessageViewHolder>() {
    //region CONSTANTS -----------------------------------------------------------------------------

    //endregion

    //region CLASS VARIABLES -----------------------------------------------------------------------

    private val layoutInflater = LayoutInflater.from(context)

    private var items: List<ChatScreenContract.View.ChatMessageModel> = listOf()

    private val dateFormatFull = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    private val dateFormatShort = SimpleDateFormat("HH:mm", Locale.getDefault())

    //endregion

    //region CONSTRUCTOR ---------------------------------------------------------------------------

    //endregion

    //region LOCAL METHODS -------------------------------------------------------------------------

    override fun onBindViewHolder(holder: ChatMessageViewHolder?, position: Int) {
        val chatMessageModel = items[position]

        holder?.author?.text = chatMessageModel.author
        holder?.time?.text =  getChatMessageDateTime(chatMessageModel)
        holder?.message?.text = chatMessageModel.message
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChatMessageViewHolder {
        val view = layoutInflater.inflate(R.layout.view_chat_message_item, parent, false)
        return ChatMessageViewHolder(view)
    }

    override fun getItemCount() = items.count()

    private fun getChatMessageDateTime(chatMessageModel: ChatScreenContract.View.ChatMessageModel): String {
        val date = Date()
        date.time = chatMessageModel.timestamp
        return dateFormatFull.format(date)
    }

    fun setMessages(messages: Collection<ChatScreenContract.View.ChatMessageModel>) {
        val oldItems = items
        items = ArrayList(messages)
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldItems[oldItemPosition]
                val newItem = items[newItemPosition]
                return TextUtils.equals(oldItem.id, newItem.id)
            }

            override fun getOldListSize(): Int {
                return oldItems.count()
            }

            override fun getNewListSize(): Int {
                return items.count()
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return TextUtils.equals(oldItems[oldItemPosition].message, items[newItemPosition].message)
            }
        }).dispatchUpdatesTo(this)
    }

    //endregion

    //region INNER CLASSES -------------------------------------------------------------------------

    class ChatMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val author: TextView = view.findViewById(R.id.author)
        val time: TextView = view.findViewById(R.id.time)
        val message: TextView = view.findViewById(R.id.message)

        init {
            message.setLinkTextColor(0xFF0000EE.toInt())
            message.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    //endregion
}