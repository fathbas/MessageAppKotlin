package com.fatihb.messageapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fatihb.messageapp.R
import com.fatihb.messageapp.adapter.NewMessageAdapter
import com.fatihb.messageapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessage : AppCompatActivity() {

    private lateinit var userList: ArrayList<UserModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"
        userList = arrayListOf()
        fetchUser()
    }

    private fun fetchUser(){
        val ref = FirebaseDatabase.getInstance().getReference("users/")

        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach{
                    val user = it.getValue(UserModel::class.java)
                    if (user != null) {
                        if (user.uid != FirebaseAuth.getInstance().currentUser!!.uid){
                            userList.add(user)
                        }
                    }
                }
                mesList.adapter = NewMessageAdapter(userList)
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}