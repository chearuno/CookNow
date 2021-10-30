package com.msc.app.cook.new_recipe

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.msc.app.cook.ImagesActivity
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.DirectionAdapter
import com.msc.app.cook.adaptor.RecyclerTouchListener
import com.msc.app.cook.models.ItemPreparation
import com.msc.app.cook.utils.Utils
import com.msc.app.cook.utils.Utils.toastError
import kotlinx.android.synthetic.main.fragment_new_directions.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class FragmentEditDirections : Fragment() {

    private val directionList: ArrayList<ItemPreparation> = ArrayList()
    private var directionAdapter: DirectionAdapter? = null

    private var directionRecyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var addButton: Button? = null
    private var directionField: EditText? = null

    var db: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null


    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(false)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_directions, null, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Add Steps"

        view.btn_finish.setOnClickListener {
            uploadToBE()
        }

        directionRecyclerView = view.findViewById(R.id.recyclerView)
        emptyView = view.findViewById(R.id.empty_view)
        addButton = view.findViewById(R.id.add_button)
        directionField = view.findViewById(R.id.directionField)
        directionAdapter = DirectionAdapter(requireActivity(), directionList)

        directionRecyclerView!!.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                directionRecyclerView!!,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        directionList.removeAt(position)
                        toggleEmptyView()
                        directionAdapter!!.notifyDataSetChanged()
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )

        toggleEmptyView()

        directionRecyclerView!!.setHasFixedSize(true)
        directionRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        directionRecyclerView!!.adapter = directionAdapter

        addButton!!.setOnClickListener {
            Log.i("DAO", "Add button pressed.")
            val newDirection: String = directionField!!.text.toString()
            Log.i("DAO", "New direction: $newDirection")
            if (newDirection.isNotEmpty()) {
                Log.i("DAO", "Directions list BEFORE: $directionList")
                directionField!!.setText("")
                val item = ItemPreparation()
                item.step = newDirection
                item.number = (directionList.size + 1).toString()
                directionList.add(item)
                Log.i("DAO", "Directions list AFTER: $directionList")
                toggleEmptyView()
                directionAdapter!!.notifyDataSetChanged()
            } else {
                toastError("Please add preparation step", requireContext())
            }
        }

        return view
    }

    private fun toggleEmptyView() {
        if (directionList.size == 0) {
            emptyView?.visibility = View.VISIBLE
            directionRecyclerView?.visibility = View.GONE
        } else {
            emptyView?.visibility = View.GONE
            directionRecyclerView?.visibility = View.VISIBLE
        }
    }

    private fun uploadToBE() {

        if (directionList.isEmpty()) {
            Utils.alerterDialog(
                "Error!",
                "Please add one or more steps.",
                requireActivity()
            )
        } else {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view?.windowToken, 0)


            val progressDialog = ProgressDialog(activity)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            progressDialog.setCancelable(false)

            val putData = requireArguments().getSerializable("RECIPE_DATA") as HashMap<String, Any>
            val lowerLimit = 100000000L
            val upperLimit = 999999999L
            val r = Random()
            val documentId = lowerLimit + (r.nextDouble() * (upperLimit - lowerLimit)).toLong()
            val imageList: ArrayList<String> = ArrayList()
            val directionListTemp: ArrayList<String> = ArrayList()

            ImagesActivity.selectedImageList.forEach {
                val imageId = UUID.randomUUID().toString()
                imageList.add(imageId)
            }

            directionList.forEach {
                directionListTemp.add(it.step ?: "")
            }

            putData["preparation"] = directionListTemp
            putData["images"] = imageList
            putData["id"] = documentId


            ImagesActivity.selectedImageList.forEachIndexed { index, element ->

                val myUri = Uri.fromFile(File(element))

                val ref: StorageReference =
                    storageReference!!.child("RecipeImages/${imageList[index]}")
                ref.putFile(myUri).addOnSuccessListener {
                    progressDialog.dismiss()

                    requireActivity().finish()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                    .addOnFailureListener { e ->
                        progressDialog.dismiss()
                        e.printStackTrace()
                        toastError("Image Upload Failed " + e.message, requireContext())
                    }
                    .addOnProgressListener { taskSnapshot ->
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                            .totalByteCount
                        progressDialog.setMessage("Uploading image $index..  " + progress.toInt() + "%")
                    }
            }

            db!!.collection("Recipes").document(documentId.toString())
                .set(putData)
                .addOnSuccessListener {

                }
                .addOnFailureListener {
                    toastError("Data writing Failed", requireContext())
                }

        }
    }


}