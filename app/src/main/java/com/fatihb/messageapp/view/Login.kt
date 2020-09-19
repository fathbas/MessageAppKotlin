package com.fatihb.messageapp.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fatihb.messageapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

class Login : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         login.setOnClickListener {
            //check firebase ıf user ıs verificate and there is login account
             if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                 auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                     .addOnCompleteListener { task ->
                         if (task.isSuccessful) {
                             if (auth.currentUser!!.isEmailVerified) {
                                 val intent =
                                     Intent(context, ChatListAndProfileSettings::class.java)
                                 startActivity(intent)
                                 activity?.finish()
                             } else {
                                 Toast.makeText(
                                     context, "Lütfen e-postanızı aktive edin.",
                                     Toast.LENGTH_SHORT
                                 ).show()
                             }
                         }
                     }.addOnFailureListener { exception ->
                         Toast.makeText(
                             context,
                             exception.message,
                             Toast.LENGTH_LONG
                         ).show()
                     }
             }else{
                 Toast.makeText(context,"E-mail or password is wrong!",Toast.LENGTH_LONG).show()
             }
        }

        signUp.setOnClickListener {
            val action = LoginDirections.actionLoginToSignUp()
            Navigation.findNavController(it).navigate(action)
        }
    }
}