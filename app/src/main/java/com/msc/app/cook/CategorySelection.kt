package com.msc.app.cook

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.msc.app.cook.adaptor.CategoryAdapter
import com.msc.app.cook.adaptor.RecyclerTouchListener
import com.msc.app.cook.models.ItemCategory
import com.msc.app.cook.utils.Utils.categoryItemList
import kotlinx.android.synthetic.main.activity_category_selection.*


class CategorySelection : AppCompatActivity() {


    private var mAdapter: CategoryAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var progressHUD: KProgressHUD? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_selection)

        progressHUD = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("We fetching category list.")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(this, 1)
        mLayoutManager.isAutoMeasureEnabled = true
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        setDataList()

        reset_category_filter.setOnClickListener {
            for (i in categoryItemList.indices) {
                categoryItemList[i].selected = false
            }
            mAdapter?.notifyDataSetChanged()
        }

        recyclerView!!.addOnItemTouchListener(
            RecyclerTouchListener(
                this,
                recyclerView!!,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {

                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )

    }

    private fun setDataList() {
        if (categoryItemList.isEmpty()) {
            progressHUD!!.show()
            db = FirebaseFirestore.getInstance()

            db!!.collection("Category")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {

                            Log.e("Doc", document.id)
                            val documentData = document.data
                            val title = documentData["name"]
                            val id = documentData["id"]
                            val image = documentData["image"]

                            val storageRef = storage!!.reference

                            val downloadRef = storageRef.child("CategoryImages/$image")

                            downloadRef.downloadUrl.addOnSuccessListener { uri ->
                                Log.e("Doc1", " => $uri")
                                val imageURI = uri.toString()
                                val item = ItemCategory()
                                item.categoryName = title as String?
                                item.id = id as Long?
                                item.img = imageURI
                                categoryItemList.add(item)
                                mAdapter = CategoryAdapter(this, categoryItemList)
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

        } else {
            mAdapter = CategoryAdapter(this, categoryItemList)
            recyclerView!!.adapter = mAdapter
        }
    }


}
