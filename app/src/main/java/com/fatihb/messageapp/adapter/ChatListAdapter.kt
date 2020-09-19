package com.fatihb.messageapp.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.fatihb.messageapp.R
import com.fatihb.messageapp.model.MessagesModel
import com.fatihb.messageapp.model.UserModel
import com.fatihb.messageapp.view.ChatLog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.latest_message_row.view.*

class ChatListAdapter(private val messages: ArrayList<MessagesModel>): RecyclerView.Adapter<ChatListAdapter.RowHolder>() {
    class RowHolder(view: View): RecyclerView.ViewHolder(view) {
        var chatPartnerUser: UserModel? = null
        fun bind(chatMessage: MessagesModel){
            itemView.latestMes.text = chatMessage.text
            val chatPartnerId: String = if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatMessage.toId
            }else{
                chatMessage.fromId
            }
            val ref = FirebaseDatabase.getInstance().getReference("users/$chatPartnerId")
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue(UserModel::class.java)
                    if (chatPartnerUser != null) {
                        itemView.nameWho.text = chatPartnerUser!!.name+" "+ chatPartnerUser!!.surname
                        Picasso.get().load(chatPartnerUser!!.profilePicture).into(itemView.profPicLatest)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.latest_message_row,parent,false)
        return RowHolder(view)
    }

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
        holder.bind(messages[position])
        holder.itemView.setOnClickListener {
            val intent =
                Intent(it.context, ChatLog::class.java)
            intent.putExtra("action",holder.chatPartnerUser)
            ContextCompat.startActivity(it.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}