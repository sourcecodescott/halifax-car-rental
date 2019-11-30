package com.csci4176.halifaxcarrental

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var userRef = db.collection("User")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "Login"

        loginButton.setOnClickListener {
            verifyUser()
        }
    }

    fun verifyUser() {
        userRef.get().addOnSuccessListener(object : OnSuccessListener<QuerySnapshot> {
            override fun onSuccess(queryDocumentSnapshots: QuerySnapshot) {
                for (documentSnapshot in queryDocumentSnapshots) {
                    val user = documentSnapshot.toObject(User::class.java)
                    if (compareUser(user)) {
                        setUser(user)

                        goToMainActivity()

                        return
                    }
                }
                Toast.makeText(this@LoginActivity, "Incorrect username or password!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun compareUser(user: User) : Boolean{
        return user != null &&
               user.username == usernameEditText.text.toString() &&
               user.password == passEditText.text.toString()
    }

    fun setUser(user: User) {
        val sharedData = Globals.instance
        sharedData.username = user.username
        sharedData.setValue("success")
    }

    fun goToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.putExtra("success", "success")
        startActivity(intent)
        finish()
    }
}
