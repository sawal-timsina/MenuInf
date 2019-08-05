package com.codeace.menuinf.ui.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.DialogFragment
import com.codeace.menuinf.R
import com.codeace.menuinf.helpers.EMAIL_REGEX
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.user_dialog.view.*
import java.util.regex.Pattern


class LoginDialog : DialogFragment() {
    var loginAuth: FirebaseAuth? = null
    private var currentFragment = 0
    private lateinit var listener: UserListener
    private var inAnim: Animation? = null
    private var outAnim: Animation? = null

    interface UserListener {
        fun onLoginSuccess(currentUser: FirebaseUser?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as UserListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                ("$context must implement UserListener")
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        setStyle(STYLE_NO_FRAME, R.style.AppDialogTheme)

        inAnim = AnimationUtils.loadAnimation(activity, android.R.anim.fade_in)
        outAnim = AnimationUtils.loadAnimation(activity, android.R.anim.fade_out)
        inAnim!!.duration = 200
        outAnim!!.duration = 200
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dialogView = inflater.inflate(R.layout.user_dialog, container, false)

        dialogView.topTextView.setFactory {
            TextView(
                ContextThemeWrapper(
                    activity,
                    R.style.TextViewBig
                ), null, 0
            )
        }
        dialogView.topTextView.inAnimation = inAnim
        dialogView.topTextView.outAnimation = outAnim

        dialogView.btnTextView.setFactory {
            TextView(
                ContextThemeWrapper(
                    activity,
                    R.style.TextView
                ), null, 0
            )
        }
        dialogView.btnTextView.inAnimation = inAnim
        dialogView.btnTextView.outAnimation = outAnim

        loginFragment(dialogView)

        dialogView.slButton.setOnClickListener {
            if (Pattern.matches(EMAIL_REGEX, dialogView.emailEditText.text.toString())) {
                if (currentFragment == 0) {
                    login(
                        dialogView.emailEditText.text.toString(),
                        dialogView.passwordEditText.text.toString()
                    )
                } else {
                    if (dialogView.passwordEditText.text.toString() == dialogView.passwordEditTextC.text.toString()) {
                        signUp(
                            dialogView.fullNameEditText.text.toString(),
                            dialogView.emailEditText.text.toString(),
                            dialogView.passwordEditTextC.text.toString()
                        )
                    } else {
                        dialogView.passwordEditText.error =
                            resources.getString(R.string.pass_not_match)
                        dialogView.passwordEditTextC.error =
                            resources.getString(R.string.pass_not_match)
                    }
                }
            } else {
                dialogView.emailEditText.error = resources.getString(R.string.error_email)
            }
        }
        dialogView.slTopButton.setOnClickListener {
            currentFragment = if (currentFragment == 0) {
                signUpFragment(dialogView)
                1
            } else {
                loginFragment(dialogView)
                0
            }
        }
        return dialogView
    }

    private fun loginFragment(view: View) {
        view.topTextView.setText(resources.getString(R.string.login))
        view.btnTextView.setText(resources.getString(R.string.login))
        view.slTopButton.text = resources.getString(R.string.sign_up)

        view.passwordTILC.visibility = View.GONE
        view.userAvatar.visibility = View.GONE
        view.fullNameText.visibility = View.GONE
        view.fullNameTIL.visibility = View.GONE
    }

    private fun signUpFragment(view: View) {
        view.topTextView.setText(resources.getString(R.string.sign_up))
        view.btnTextView.setText(resources.getString(R.string.sign_up))
        view.slTopButton.text = resources.getString(R.string.login)

        view.passwordTILC.visibility = View.VISIBLE
        view.userAvatar.visibility = View.VISIBLE
        view.fullNameText.visibility = View.VISIBLE
        view.fullNameTIL.visibility = View.VISIBLE
    }

    private fun login(email: String, password: String) {
        loginAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    listener.onLoginSuccess(loginAuth?.currentUser)
                    showMessage("Welcome : ${loginAuth?.currentUser?.displayName}")
                    dismiss()
                } else {
                    Log.d(TAG, "signInWithEmail:failure", it.exception)
                    showMessage(it.exception?.localizedMessage.toString())
                }
            }
    }

    private fun signUp(name: String, email: String, password: String) {
        loginAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                loginAuth?.currentUser?.updateProfile(
                    UserProfileChangeRequest
                        .Builder()
                        .setDisplayName(name)
                        .build()
                )
                listener.onLoginSuccess(loginAuth?.currentUser)
                showMessage("Welcome : ".plus(name))
                dismiss()
            } else {
                // If sign in fails, display a message to the user.
                Log.d(TAG, "createUserWithEmail:failure", it.exception)
                showMessage(it.exception?.localizedMessage.toString())
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(activity!!.applicationContext, message, Toast.LENGTH_LONG).show()
    }
}