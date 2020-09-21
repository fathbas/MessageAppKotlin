package com.fatihb.messageapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fatihb.messageapp.R
import com.fatihb.messageapp.model.MessagesModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_from_receiver.view.*
import kotlinx.android.synthetic.main.row_from_sender.view.*

private const val sender = 0
private const val receiver = 1

class ChatLogAdapter(private val messages: ArrayList<MessagesModel>, private val senderUser: String, private val receiverUser: String):
    RecyclerView.Adapter<ChatLogAdapter.RowHolder>() {

    class RowHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(chatMessage: MessagesModel, senderUri: String, receiverUri: String){
            if (chatMessage.fromId == FirebaseAuth.getInstance().uid ){
                itemView.mesSender.text = chatMessage.text
                Picasso.get().load(senderUri).into(itemView.profilePicSender)
            }else{
                itemView.mesReceive.text = chatMessage.text
                Picasso.get().load(receiverUri).into(itemView.profilePicReceiver)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {

       val view = if (viewType == sender){
            LayoutInflater.from(parent.context).inflate(R.layout.row_from_sender,parent,false)
        }else{
            LayoutInflater.from(parent.context).inflate(R.layout.row_from_receiver,parent,false)
        }
            return RowHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].fromId == FirebaseAuth.getInstance().uid){
            sender
        }else{
            receiver
        }
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(messages[position],senderUser,receiverUser)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

}