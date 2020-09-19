package com.fatihb.messageapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.fatihb.messageapp.R
import com.fatihb.messageapp.adapter.ChatListAdapter
import com.fatihb.messageapp.model.MessagesModel
import com.fatihb.messageapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat_list_and_profile_settings.*

class ChatListAndProfileSettings : AppCompatActivity() {

    private lateinit var adapter: ChatListAdapter
    private lateinit var latestAllMess: ArrayList<MessagesModel>
    private lateinit var fromId: String

    companion object{
        var currentUser: UserModel? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list_and_profile_settings)

        latestAllMess = arrayListOf()

        latestAllMes.addItemDecoration(DividerItemDecoration(this,
        DividerItemDecoration.VERTICAL))

        listenForLatestMessages()
        fetchCurrentUser()
    }

    private fun listenForLatestMessages(){
        fromId = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("latest-messages/$fromId")

        ref.addChildEventListener(object : ChildEventListener{
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

    private fun fetchCurrentUser(){
        fromId = FirebaseAuth.getInstance().uid!!
        val ref = FirebaseDatabase.getInstance().getReference("users/$fromId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(UserModel::class.java)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.signOut -> {
                FirebaseAuth.getInstance().signOut()
                val intent =
                    Intent(this, MainActivity::class.java)
                startActivity(intent)
                this.finish()
            }
            R.id.newMes -> {
                val intent =
                    Intent(this, NewMessage::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}