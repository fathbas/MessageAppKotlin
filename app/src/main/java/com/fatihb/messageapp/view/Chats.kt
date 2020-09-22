package com.fatihb.messageapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.fatihb.messageapp.R
import com.fatihb.messageapp.adapter.ChatListAdapter
import com.fatihb.messageapp.model.MessagesModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chats.*

class Chats : Fragment() {

    private lateinit var adapter: ChatListAdapter
    private lateinit var latestAllMess: ArrayList<MessagesModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        latestAllMess = arrayListOf()

        latestAllMes.addItemDecoration(
            DividerItemDecoration(context,
            DividerItemDecoration.VERTICAL)
        )

        listenForLatestMessages()
    }

    private fun listenForLatestMessages(){
        val fromId = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(MessagesModel::class.java)
                if (chatMessage != null){
                    latestAllMess.add(chatMessage)
                    adapter = ChatListAdapter(latestAllMess)
                    latestAllMes.adapter = adapter
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(MessagesModel::class.java)
                if (chatMessage != null){
                    latestAllMess.add(chatMessage)
                    adapter = ChatListAdapter(latestAllMess)
                    latestAllMes.adapter = adapter
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}