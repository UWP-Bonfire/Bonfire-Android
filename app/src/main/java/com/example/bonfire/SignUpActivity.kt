package com.example.bonfire

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth


class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var TAG:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_layout)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        auth = Firebase.auth
        TAG = "signup"

        // Button to switch to main screen activity
        val signupButton: Button = findViewById(R.id.signup_button)
        signupButton.setOnClickListener {
            // get contents of email/user and password inputs
            val emailEditText:  TextInputEditText = findViewById(R.id.signup_email_edit)
            val passwordEditText: TextInputEditText = findViewById(R.id.signup_password_edit)
            val emailEditable = emailEditText.getText()
            val passwordEditable = passwordEditText.getText()

            signUp(auth, emailEditable.toString(), passwordEditable.toString())
        }

        // switch to sign in activity
        val switchButton: Button = findViewById(R.id.signup_switch_button)
        switchButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    fun signUp (auth: FirebaseAuth, email:String, password:String){
        if (email == "" || password == ""){
            // pop alert if not all fields filled
            Toast.makeText(baseContext, "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
        else{
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")

                    val intent = Intent(this, GroupChatListActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, getFriendlyErrorMessage(task.exception), Toast.LENGTH_SHORT).show()
                }
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
//            "
//        }
        return ""
    }

    // Password should contain 8-36 characters, a lower and uppercase character, a number, and a special character.
    fun validatePassword(password: String){
        // Requires 8-36 characters
        if (password.length > 36 || password.length < 8) false

        // Requires lowercase character
        val lowerPattern = Regex("[a-z]")
        if (!lowerPattern.containsMatchIn(password)) false

        // Requires uppercase character
        val upperPattern = Regex("[A-Z]")
        if (!upperPattern.containsMatchIn(password)) false

        // Requires number
        val numberPattern = Regex("[0-9]")
        if (!numberPattern.containsMatchIn(password)) false

        // Requires a special character
    }

    // Returns true if string is an email
    fun isValidEmail(target: String): Boolean {
        if (TextUtils.isEmpty(target)) {
            return false
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }
}

