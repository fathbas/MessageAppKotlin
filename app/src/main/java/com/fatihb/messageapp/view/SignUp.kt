package com.fatihb.messageapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.fatihb.messageapp.R
import com.fatihb.messageapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_sign_up.*
import java.util.*

class SignUp : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private var selectedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        auth = FirebaseAuth.getInstance()

        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        goToLogin.setOnClickListener {
            goLogin(it)
        }

        register.setOnClickListener {registerView ->
            //Firebase sign up operation
            if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener {
                    auth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                        if (it.isSuccessful){
                            Toast.makeText(context,"Register is complete! Please verification your e-mail.",
                                Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(context,it.exception.toString(), Toast.LENGTH_LONG).show()
                        }
                        user = auth.currentUser!!
                        database = FirebaseDatabase.getInstance()
                        ref = database.getReference("users/${user.uid}")
                        uploadImageToStore(surname.text.toString(),name.text.toString(),user.uid)
                        goLogin(registerView)
                    }
                }.addOnFailureListener {
                        Toast.makeText(context,it.message.toString(),Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context,"E-mail or password is wrong!",Toast.LENGTH_LONG).show()
            }
        }

        imageView.setOnClickListener {
            //take photo from gallery and save database for user
//            val intent = Intent(Intent.ACTION_PICK)
////            intent.type= "image/*"
////            startActivityForResult(intent,0)

            if (context?.let { it1 -> ContextCompat.checkSelfPermission(it1, Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED){
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1) }
            }else{
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent,2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
             selectedUri = data.data

            try {
                if (Build.VERSION.SDK_INT >= 28){
                    val source = ImageDecoder.createSource(activity?.contentResolver!!, selectedUri!!)
                    val bitmap = ImageDecoder.decodeBitmap(source)
                    circleImage.setImageBitmap(bitmap)
                }else{
                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedUri)
                    circleImage.setImageBitmap(bitmap)
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
            circleImage.alpha = 1f
        }
    }

    private fun uploadImageToStore(surname: String,name: String,uid: String){

        val fileName = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("images/$fileName")

        ref.putFile(selectedUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    saveToUserInfoInDataBase(surname,name,it.toString(),uid)
                }
            }
    }

    private fun saveToUserInfoInDataBase(surname: String,name: String,uri: String, uid: String){
        val user = UserModel(name,surname,uri,uid)
        ref.setValue(user)
    }

    private fun goLogin(view: View){
       val action = SignUpDirections.actionSignUpToLogin()
        Navigation.findNavController(view).navigate(action)
    }
}