package com.example.timetoken

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.timetoken.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var firestore: FirebaseFirestore
    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

         sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "default")
        val loggedIn = sharedPreferences.getBoolean("loggedIn", false)
        if(username.equals("default") || !loggedIn){
            initView()
        }else{
            val cardNo = sharedPreferences.getString("cardNo", "default")
            val signupTimestamp = sharedPreferences.getString("signupTimestamp", "0")
            launchCardDetailScreen(signupTimestamp,cardNo,username)
        }


    }
    fun initView(){
        setupFirestore()
        binding.btnLogin.setOnClickListener {
            val email : String = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            if(email.isNullOrBlank() || password.isNullOrBlank()){
                Toast.makeText(applicationContext,"Username or Password cannot be empty !",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            validateEmailPassword(email,password)
        }
    }
    fun setupFirestore(){
        firestore = FirebaseFirestore.getInstance()
    }

    fun validateEmailPassword(enteredUsername : String,enteredPassword : String){
        val collectionReference = firestore.collection("users")
        collectionReference
            .whereEqualTo("username", enteredUsername)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Username not found
                    Toast.makeText(applicationContext, "Username not found", Toast.LENGTH_SHORT).show()
                } else {
                    // Username found, check password
                    val foundUser = querySnapshot.documents[0].toObject(User::class.java)

                    if (foundUser?.password == enteredPassword) {
                        // Password matches, get additional information
                        val signupTimestamp = foundUser.signupTimestamp
                        val cardNumber = foundUser.cardNumber
                        val username = foundUser.username

                        // Do something with signupTimestamp and cardNumber
                        saveDetailsInPref(foundUser)
                        launchCardDetailScreen(signupTimestamp,cardNumber,username)
                    } else {
                        // Incorrect password
                        Toast.makeText(applicationContext, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle failures, e.g., Firebase-related errors
                Toast.makeText(applicationContext, "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun launchCardDetailScreen(signupTimestamp: String?, cardNumber: String?,username:String?) {
        val intent = Intent(this,CardDetailScreen::class.java)
        intent.putExtra("cardNo",cardNumber)
        intent.putExtra("signupTimestamp",signupTimestamp)
        intent.putExtra("username",username)
        startActivityForResult(intent,101)
    }
    private fun saveDetailsInPref(user : User){
        val editor = sharedPreferences.edit()
        editor.putString("username",user.username)
        editor.putString("signupTimestamp",user.signupTimestamp)
        editor.putString("cardNo",user.cardNumber)
        editor.putBoolean("loggedIn", true)
        editor.apply()
    }
    fun clearPref(){
        val editor = sharedPreferences.edit()
        editor.putString("username","default")
        editor.putString("signupTimestamp","0")
        editor.putString("cardNo","default")
        editor.putBoolean("loggedIn", false)
        editor.apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK){
            //clear pref
            clearPref()
            binding.etUsername.text.clear()
            binding.etPassword.text.clear()
            initView()
        }
    }

}