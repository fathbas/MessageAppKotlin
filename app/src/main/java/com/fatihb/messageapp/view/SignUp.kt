package com.fatihb.messageapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
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

        if (auth.currentUser != null){
            val intent = Intent(context,ChatListAndProfileSettings::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

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
            if (context?.let { it1 -> ContextCompat.checkSelfPermission(it1, Manifest.permission.READ_EXTERNAL_STORAGE) } != PackageManager.PERMISSION_GRANTED){
                activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1) }
            }else{
                context?.let { it1 -> CropImage.startPickImageActivity(it1,this) }
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
                context?.let { it1 -> CropImage.startPickImageActivity(it1,this) }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK){
           val imageUri = context?.let { CropImage.getPickImageResultUri(it,data)}
           if (CropImage.isReadExternalStoragePermissionsRequired(requireContext(), imageUri!!)){
               selectedUri = imageUri
               requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),0)
           }else{
               startCrop(imageUri)
           }
       }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK){
                selectedUri = result.uri
                circleImage.setImageURI(selectedUri)
                circleImage.alpha = 1f
            }
        }
    }

    private fun startCrop(uri: Uri){
        context?.let {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(it,this)
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