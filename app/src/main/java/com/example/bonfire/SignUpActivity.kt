package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var TAG:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val db = Firebase.firestore
        auth = Firebase.auth
        TAG = "signup"

        // Button to switch to main screen activity
        val signupButton: Button = findViewById(R.id.signup_button)
        signupButton.setOnClickListener {
            // get contents of email, user and password inputs
            val userEditText: TextInputEditText = findViewById(R.id.signup_username_edit)
            val usernameString = userEditText.getText().toString()

            val emailEditText: TextInputEditText = findViewById(R.id.signup_email_edit)
            val emailString = emailEditText.getText().toString()

            val passwordEditText: TextInputEditText = findViewById(R.id.signup_password_edit)
            val passwordString = passwordEditText.getText().toString()

            signUp(auth, db,usernameString, emailString, passwordString)
        }

        // switch to sign in activity
        val switchButton: Button = findViewById(R.id.signup_switch_button)
        switchButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    fun makeToast(string:String){
        Toast.makeText(baseContext, string, Toast.LENGTH_SHORT).show()
    }

    fun signUp (auth: FirebaseAuth, db: FirebaseFirestore, username:String, email:String, password:String) {
        if (username == "" || email == "" || password == "") {
            // pop alert if not all fields filled
            makeToast("Please fill out all fields")
            return
        }

        // Check if username isnt taken
        val usersRef = db.collection("users").whereEqualTo("name", username)
        usersRef.get()
        .addOnSuccessListener { querySnapshot ->
            if (!querySnapshot.isEmpty) {
                makeToast("An account with this email already exists.")
            } else {
                Log.d(TAG, "No such document")
                makeAccount(auth, username, email, password)
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
    }

    fun makeAccount(auth: FirebaseAuth, username:String, email:String, password:String){
        makeToast("safe guard")
        return

        // Attempt to create user
        auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, go to main screen
                Log.d(TAG, "createUserWithEmail:success")

                val intent = Intent(this, GroupChatListActivity::class.java)
                startActivity(intent)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                makeToast(getFriendlyErrorMessage(task.exception))
            }
        }
    }

    fun getFriendlyErrorMessage(error: Exception?):String {
        return when (error) {
            is FirebaseAuthInvalidCredentialsException -> "Please enter a valid email address."
            is FirebaseException -> "Password does not meet requirements."
            else -> "An unexpected error occurred. Please try again."
        }
//            "auth/user-not-found" -> "Invalid password. Please try again."
//            "auth/wrong-password" -> "Invalid password. Please try again."
//            "auth/email-already-in-use" ->"An account with this email already exists."
    }
}

