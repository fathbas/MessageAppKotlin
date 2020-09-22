package com.fatihb.messageapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.fatihb.messageapp.R
import com.fatihb.messageapp.adapter.ViewPagerAdapter
import com.fatihb.messageapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_chat_list_and_profile_settings.*

class ChatListAndProfileSettings : AppCompatActivity() {



    companion object{
        var currentUser: UserModel? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list_and_profile_settings)

        val chatFrag = Chats()
        val profileFragment = Profile()

        tabLayout.setupWithViewPager(slider)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager,0)

        viewPagerAdapter.addFragment(chatFrag,"Chats")
        viewPagerAdapter.addFragment(profileFragment,"Profile")
        slider.adapter = viewPagerAdapter
        tabLayout.getTabAt(0)?.setIcon(android.R.drawable.sym_action_chat)
        tabLayout.getTabAt(1)?.setIcon(android.R.drawable.ic_menu_myplaces)

        fetchCurrentUser()
    }



    private fun fetchCurrentUser(){
        val fromId = FirebaseAuth.getInstance().uid!!
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