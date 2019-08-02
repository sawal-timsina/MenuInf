package com.codeace.menuinf.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.codeace.menuinf.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.login_dialog.view.*

class LoginDialog : DialogFragment() {

    var loginAuth: FirebaseAuth? = null

    private lateinit var listener: UserListener

    interface UserListener {
        fun onLoginSuccess(currentUser: FirebaseUser?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as UserListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement UserListener")
            )
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val dialogView = inflater.inflate(R.layout.login_dialog, container, false)

        dialogView.button.setOnClickListener {
            loginAuth?.signInWithEmailAndPassword(dialogView.emailEditText.text.toString(),
                dialogView.passwordEditText.text.toString())?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    listener.onLoginSuccess(loginAuth?.currentUser)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", it.exception)
                }
            }
        }

        return dialogView
    }
}