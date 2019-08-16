package com.codeace.menuinf.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.codeace.menuinf.R
import com.codeace.menuinf.helpers.checkMail
import com.codeace.menuinf.helpers.checkPassword
import com.codeace.menuinf.helpers.imagePickCode
import com.codeace.menuinf.helpers.showMessage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.login_singup.*

class LoginActivity : AppCompatActivity() {
    private lateinit var loginAuth: FirebaseAuth
    private var currentFragment = 0
    private var inAnim: Animation? = null
    private var outAnim: Animation? = null
    private var imageUri: Uri? = null

    override fun onStart() {
        super.onStart()


        if (loginAuth.currentUser != null) {
            startMainActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_singup)
        inAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        outAnim = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
        inAnim?.duration = 200
        outAnim?.duration = 200
        loginAuth = FirebaseAuth.getInstance()

        topTextView.setFactory { TextView(ContextThemeWrapper(this, R.style.TextViewBig), null, 0) }
        topTextView.inAnimation = inAnim
        topTextView.outAnimation = outAnim

        btnTextView.setFactory { TextView(ContextThemeWrapper(this, R.style.TextView), null, 0) }
        btnTextView.inAnimation = inAnim
        btnTextView.outAnimation = outAnim

        loginFragment()
        slButton.setOnClickListener {
            checkMail(
                emailEditText.text.toString(),
                emailEditText,
                resources.getString(R.string.error_email)
            ) {
                checkPassword(
                    passwordEditText.text.toString(),
                    passwordEditText,
                    resources.getString(R.string.field_error)
                ) {
                    if (currentFragment == 0) {
                        progressBar.visibility = View.VISIBLE
                        login(emailEditText.text.toString(), passwordEditText.text.toString())
                    } else {
                        checkPassword(
                            passwordEditTextC.text.toString(),
                            passwordEditTextC,
                            resources.getString(R.string.field_error)
                        ) {
                            if (passwordEditText.text.toString() == passwordEditTextC.text.toString()) {
                                if (fullNameEditText.text.toString() != "") {
                                    progressBar.visibility = View.VISIBLE
                                    signUp(
                                        fullNameEditText.text.toString(),
                                        emailEditText.text.toString(),
                                        passwordEditTextC.text.toString()
                                    )
                                } else {
                                    fullNameEditText.error =
                                        resources.getString(R.string.field_error)
                                }
                            } else {
                                passwordEditText.error =
                                    resources.getString(R.string.pass_not_match)
                                passwordEditTextC.error =
                                    resources.getString(R.string.pass_not_match)
                            }
                        }
                    }
                }
            }
        }

        signUpButton.setOnClickListener {
            currentFragment = if (currentFragment == 0) {
                signUpFragment()
                1
            } else {
                loginFragment()
                0
            }
        }
        forgotPassword.setOnClickListener {
            checkMail(
                emailEditText.text.toString(),
                emailEditText,
                resources.getString(R.string.error_email)
            ) {
                loginAuth.sendPasswordResetEmail(emailEditText.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Snackbar.make(
                                it,
                                "Password reset link has been sent to ${emailEditText.text.toString()} mail."
                                ,
                                Snackbar.LENGTH_LONG
                            ).show()
                        } else {
                            Snackbar.make(
                                it,
                                "Password reset link has been sent to ${task.exception?.localizedMessage.toString()} mail."
                                ,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        userAvatar.setOnClickListener {
            if (currentFragment == 1) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, imagePickCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imagePickCode) {
            userAvatar.setImageURI(data?.data)
            imageUri = data?.data
        }
    }

    private fun loginFragment() {
        emailEditText.text?.clear()
        emailTIL.requestFocus()
        passwordEditText.text?.clear()

        topTextView.setText(resources.getString(R.string.login))
        btnTextView.setText(resources.getString(R.string.login))
        signUpButton.text = resources.getString(R.string.don_t_have_an_account_sign_up)

        forgotPassword.visibility = View.VISIBLE
        passwordTILC.visibility = View.GONE
        userAvatar.setImageDrawable(resources.getDrawable(R.drawable.ic_home_button, null))
        fullNameTIL.visibility = View.GONE
    }

    private fun signUpFragment() {
        fullNameEditText.text?.clear()
        fullNameTIL.requestFocus()
        emailEditText.text?.clear()
        passwordEditText.text?.clear()
        passwordEditTextC.text?.clear()

        topTextView.setText(resources.getString(R.string.sign_up))
        btnTextView.setText(resources.getString(R.string.sign_up))
        signUpButton.text = resources.getString(R.string.already_have_an_account)

        forgotPassword.visibility = View.GONE
        passwordTILC.visibility = View.VISIBLE
        userAvatar.setImageDrawable(resources.getDrawable(R.drawable.ic_user, null))
        fullNameTIL.visibility = View.VISIBLE
    }

    private fun login(email: String, password: String) {
        loginAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    progressBar.visibility = View.GONE
                    showMessage(this, "Welcome : ${loginAuth.currentUser?.displayName}")
                    startMainActivity()
                } else {
                    progressBar.visibility = View.GONE
                    showMessage(this, it.exception?.localizedMessage.toString())
                }
            }
    }

    private fun signUp(name: String, email: String, password: String) {
        var userAvatarStorage: StorageReference? = null
        if (imageUri != null) {
            val storage = FirebaseStorage.getInstance()
            userAvatarStorage = storage.reference.child("userAvatar/${email.plus(".jpg")}")
        }
        loginAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    if (userAvatarStorage != null) {

                        userAvatarStorage.putFile(imageUri!!)
                            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                                if (!task.isSuccessful) {
                                    throw task.exception!!
                                }
                                return@Continuation userAvatarStorage.downloadUrl
                            })
                            .addOnCompleteListener { taskSnapshot ->
                                // Get a URL to the uploaded content
                                imageUri = taskSnapshot.result
                                signUpUser(name)
                            }.addOnFailureListener {
                                showMessage(this, it.localizedMessage.toString())
                            }

                    } else {
                        signUpUser(name)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    progressBar.visibility = View.GONE
                    showMessage(this, authTask.exception?.localizedMessage.toString())
                }
            }
    }

    private fun signUpUser(name: String) {
        loginAuth.currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(imageUri)
                .build()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar.visibility = View.GONE
                    showMessage(this, "Welcome : ".plus(loginAuth.currentUser?.displayName))
                    startMainActivity()
                }
            }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}