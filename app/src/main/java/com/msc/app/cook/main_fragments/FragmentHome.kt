package com.msc.app.cook.main_fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.msc.app.cook.Detail
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.RecipeAdapter
import com.msc.app.cook.adaptor.RecyclerTouchListener
import com.msc.app.cook.models.ItemRecipe
import java.io.Serializable

class FragmentHome : Fragment() {

    private var mAdapter: RecipeAdapter? = null
    private var itemList: ArrayList<ItemRecipe> = ArrayList()
    private var recyclerView: RecyclerView? = null
    var progressHUD: KProgressHUD? = null
    var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var currentItemList: ArrayList<Map<String, Any>> = ArrayList()

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, null, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "RECIPES",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        progressHUD = KProgressHUD.create(requireContext())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("We fetching recent recipe list.")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 2)
        mLayoutManager.isAutoMeasureEnabled = true
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        setDataList()

        recyclerView!!.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                recyclerView!!,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        var tempItem: Map<String, Any> = HashMap<String, Any>()
                        currentItemList.forEach {
                            if (itemList[position].id!! == it["id"]) {
                                tempItem = it
                            }
                        }
                        val intent = Intent(activity, Detail::class.java)
                        intent.putExtra(
                            "DATA_OF_DOCUMENT",
                            tempItem as Serializable?
                        )
                        startActivity(intent)
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)
    }

    init {
        setHasOptionsMenu(true)
    }

    private fun setDataList() {
        progressHUD!!.show()
        db = FirebaseFirestore.getInstance()
        itemList.clear()
        currentItemList.clear()

        db!!.collection("Recipes")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {

                        Log.e("Doc", document.id)
                        val documentData = document.data
                        val title = documentData["name"]
                        val id = documentData["id"]
                        val preparationTime = documentData["prepare_time"]
                        val image: ArrayList<*> = documentData["images"] as ArrayList<*>

                        currentItemList.add(documentData)
                        val storageRef = storage!!.reference

                        val downloadRef = storageRef.child("RecipeImages/${image.first()}")

                        downloadRef.downloadUrl.addOnSuccessListener { uri ->
                            Log.e("Doc1", " => $uri")
                            val imageURI = uri.toString()
                            val item = ItemRecipe()
                            item.recipe = title as String?
                            item.time = preparationTime as String?
                            item.id = id as Long?
                            item.img = imageURI
                            itemList.add(item)
                            mAdapter = activity?.let { RecipeAdapter(itemList, requireContext()) }
                            recyclerView!!.adapter = mAdapter
                            progressHUD!!.dismiss()
                        }
                            .addOnFailureListener {
                                Log.e("Doc", "Error getting Data: ")
                                progressHUD!!.dismiss()
                            }
                    }


                } else {
                    Log.e("Doc", "Error getting documents: ", task.exception)
                    progressHUD!!.dismiss()
                }
            }
    }
}