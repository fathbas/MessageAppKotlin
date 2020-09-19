package com.fatihb.messageapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fatihb.messageapp.R
import com.fatihb.messageapp.adapter.ChatLogAdapter
import com.fatihb.messageapp.model.MessagesModel
import com.fatihb.messageapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat_log.*

class ChatLog : AppCompatActivity() {

    private lateinit var allChatMessage: ArrayList<MessagesModel>
    private lateinit var adapter: ChatLogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val user = intent.getParcelableExtra<UserModel>("action")
        supportActionBar?.title = user?.name+" "+user?.surname

        allChatMessage = arrayListOf()
        adapter = ChatLogAdapter(allChatMessage,null,null)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@ChatLog)
        allMes.layoutManager = layoutManager

        listenForMessages()

        sendMessage.setOnClickListener {
            addMessagesFirebase()
        }
    }

    private fun listenForMessages(){
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<UserModel>("action")
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("user-messages/$fromId/$toId")

        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(MessagesModel::class.java)
                if (chatMessage != null){
                    val currentUser = ChatListAndProfileSettings.currentUser
                    val receiverUser = intent.getParcelableExtra<UserModel>("action")
                    if (currentUser != null){
                        if (receiverUser != null){
                            allChatMessage.add(chatMessage)
                            adapter = ChatLogAdapter(allChatMessage,currentUser,receiverUser)
                            allMes.adapter = adapter
                        }
                    }
                    allMes.scrollToPosition(adapter.itemCount - 1)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addMessagesFirebase(){

        val text = enterMessage.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<UserModel>("action")
        val toId = user?.uid
        val ref = FirebaseDatabase.getInstance().getReference("user-messages/$fromId/$toId").push()
        val toRef = FirebaseDatabase.getInstance().getReference("user-messages/$toId/$fromId").push()


        val chatMessage = toId?.let {
            MessagesModel(ref.key!!,text, fromId!!,
                it,System.currentTimeMillis() / 1000)
        }
        ref.setValue(chatMessage)
            .addOnSuccessListener {
                enterMessage.text.clear()
                allMes.scrollToPosition(adapter.itemCount - 1)
            }
        toRef.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageRefTo = FirebaseDatabase.getInstance().getReference("latest-messages/$toId/$fromId")
        latestMessageRefTo.setValue(chatMessage)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent =
            Intent(this, ChatListAndProfileSettings::class.java)
        startActivity(intent)
    }
}