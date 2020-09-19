package com.fatihb.messageapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fatihb.messageapp.R
import com.fatihb.messageapp.model.MessagesModel
import com.fatihb.messageapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_from_receiver.view.*
import kotlinx.android.synthetic.main.row_from_sender.view.*

class ChatLogAdapter(private val messages: ArrayList<MessagesModel>, private val senderUser: UserModel?, private val receiverUser: UserModel?):
    RecyclerView.Adapter<ChatLogAdapter.RowHolder>() {

    private var index = 0
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
        val view: View
            if (messages[index].fromId == senderUser!!.uid){
                view = LayoutInflater.from(parent.context).inflate(R.layout.row_from_sender,parent,false)
                index += 1
            }else{
                view = LayoutInflater.from(parent.context).inflate(R.layout.row_from_receiver,parent,false)
                index += 1
            }
            return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        if (receiverUser != null) {
            if (senderUser != null) {
                holder.bind(messages[position],senderUser.profilePicture,receiverUser.profilePicture)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

}