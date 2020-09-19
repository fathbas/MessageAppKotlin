package com.fatihb.messageapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserModel(
   val name: String,
   val surname: String,
   val profilePicture: String,
   val uid: String
): Parcelable{
   constructor() : this("","","","")
}