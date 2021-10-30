package com.msc.app.cook.main_fragments

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        db!!.collection("User").whereEqualTo("userId", userId.toInt())
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
                        val favList: ArrayList<*> = documentData["favList"] as ArrayList<*>

                        val storageRef = storage!!.reference

//
                        val item = ItemUser()
                        item.id = id as Long?
                        item.firstName = firstName as String?
                        item.lastName = lastName as String?
                        item.email = email as String?
                        item.password = password as String?
                        item.fullData = documentData
                        itemList.add(item)

                        val editText = view.findViewById(R.id.userFirstName) as TextInputEditText
                        editText.setText("Hello world!");
                        //view.userFirstName.setText(firstName.toString());
                        Log.e("Doc", "Success getting user: ")
//
                    }


                } else {
                    Log.e("Doc", "Error getting documents: ", task.exception)
//                    progressHUD!!.dismiss()
//                    progressHUD!!.dismiss()
//                    emptyView?.visibility = View.VISIBLE
//                    recyclerView?.visibility = View.GONE
                }
            }

//        db!!.collection("User").whereEqualTo("userId", userId.toInt())
//            .get()
//            .addOnSuccessListener {
//                Log.i("firebase", "Got value ${it.documents}")
//
//                it.documents.map
//                const docSnapshots = querysnapshot.docs;
//                for (var i in docSnapshots) {
//                const doc = docSnapshots[i].data();
//                 Check for your document data here and break when you find it
//            }
//        }.addOnFailureListener {
//        Log.e("firebase", "Error getting data", it)
//        }
//                    snapshot, e ->
//                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e)
//                        return@addSnapshotListener
//                    }
//                    if (snapshot != null) {
//                        Log.d(TAG, "Current data: ${snapshot}")
//                    } else {
//                        Log.d(TAG, "Current data: null")
//                    }
//                }
//
//                    task ->
//                if (task.isSuccessful) {
//                    for (document in task.result!!) {
//                        Log.e("Doc", document.id)
//                        val documentData = document.data
//                        val firstName = documentData["firstName"]
//                        val lastName = documentData["lastName"]
//                        val id = documentData["id"]
//                        view.firstNameTextEdit.setText(firstName.toString());
//                    }
//                } else {
//                    Log.e("Doc", "Error getting documents: ", task.exception)
//                }
//            }


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
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            //Toast.makeText(this@FragmentProfile, "You clicked me.", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    init {
        setHasOptionsMenu(false)
    }
}