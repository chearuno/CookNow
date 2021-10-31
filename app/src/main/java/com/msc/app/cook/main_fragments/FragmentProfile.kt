package com.msc.app.cook.main_fragments

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.models.ItemUser
import kotlinx.android.synthetic.main.fragment_profile.view.*


class FragmentProfile : Fragment() {
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var itemList: ArrayList<ItemUser> = ArrayList()

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, null, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "PROFILE",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        val prefs: SharedPreferences =
            requireActivity().getSharedPreferences("MyPrefsFile", 0)
        val userId = prefs.getString("loggedUserId", "").toString()



        db!!.collection("User").whereEqualTo("id", userId.toLong())
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {

                        Log.e("Doc", document.id)
                        val documentData = document.data
                        val id = documentData["id"]
                        val firstName = documentData["firstName"]
                        val lastName = documentData["lastName"]
                        val email = documentData["email"]
                        val password = documentData["password"]

                        val fullname = view.findViewById(R.id.userFullName) as TextView
                        fullname.setText(firstName.toString()+" "+lastName.toString());

                        val fname = view.findViewById(R.id.userFirstName) as TextInputEditText
                        fname.setText(firstName.toString());
                        val lname = view.findViewById(R.id.userLastName) as TextInputEditText
                        lname.setText(lastName.toString());
                        val emailval = view.findViewById(R.id.userEmail) as TextInputEditText
                        emailval.setText(email.toString());
                        val pass = view.findViewById(R.id.userPassword) as TextInputEditText
                        pass.setText(password.toString());
                        Log.e("Doc", "Success getting user: ")
                    }
                } else {
                    Log.e("Doc", "Error getting documents: ", task.exception)
                }
            }


        val updateButtonClick = view.findViewById(R.id.updateBtn) as Button
        // set on-click listener
        updateButtonClick.setOnClickListener {
            val putData: java.util.HashMap<String, Any> = java.util.HashMap()
            putData["firstName"] = view.userFirstName.text.toString()
            putData["lastName"] = view.userLastName.text.toString()
            putData["email"] = view.userEmail.text.toString()
            putData["password"] = view.userPassword.text.toString()

            db!!.collection("User").document(userId)
                .update(putData)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written!")
                    Toast.makeText(activity, "Update successful", Toast.LENGTH_SHORT).show()
                    val fullname = view.findViewById(R.id.userFullName) as TextView
                    fullname.setText("${view.userFirstName.text.toString()} ${view.userLastName.text.toString()}");
                }
                .addOnFailureListener {
                        e -> Log.w(TAG, "Error writing document", e)
                    Toast.makeText(activity, "Error updating", Toast.LENGTH_SHORT).show()
                }
        }
        return view
    }

    init {
        setHasOptionsMenu(false)
    }
}