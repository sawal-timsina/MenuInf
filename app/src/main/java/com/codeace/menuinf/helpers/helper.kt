package com.codeace.menuinf.helpers

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.codeace.menuinf.R
import com.codeace.menuinf.entity.FoodData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import java.util.regex.Pattern

val EMAIL_REGEX =
    "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"

val imagePickCode = 1000

fun getCurrentUser(uid: String): DatabaseReference {
    return FirebaseDatabase.getInstance().reference.child("users").child(uid)
}

fun getListener(func: (DataSnapshot) -> Unit): ValueEventListener {
    return object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            func(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("FirebaseQueryLiveData", "Can't listen to query ", databaseError.toException())
        }
    }
}

fun setImage(context: Context, url: String, imageView: ImageView, placeHolder: Int = R.drawable.imageplaceholder) {
    Glide.with(context).load(url).centerCrop()
        .placeholder(placeHolder).into(imageView)
}

fun showMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

inline fun checkMail(email : String, editText : TextInputEditText, errorMessage : String, func: () -> Unit) {
    if (Pattern.matches(EMAIL_REGEX, email)) {
        func()
    } else {
        editText.error = errorMessage
    }
}

inline fun checkPassword(password : String, editText : TextInputEditText, errorMessage : String, func: () -> Unit){
    if (TextUtils.isEmpty(password)) {
        editText.error = errorMessage
    } else {
        func()
    }
}

fun sort(list: MutableList<FoodData>, data: FoodData) {
    var i = list.size
    do {
        if (i <= 0) {
            list.add(0, data)
            break
        } else if (list[i - 1].food_name.compareTo(data.food_name) < 1) {
            list.add(i, data)
            break
        }
        i--
    } while (list[i].food_name.compareTo(data.food_name) > -1)
}
