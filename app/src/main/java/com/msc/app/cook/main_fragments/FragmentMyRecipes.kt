package com.msc.app.cook.main_fragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.msc.app.cook.CategorySelection
import com.msc.app.cook.Detail
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.RecipeAdapter
import com.msc.app.cook.adaptor.RecyclerTouchListener
import com.msc.app.cook.models.ItemRecipe
import com.msc.app.cook.utils.Utils
import java.io.Serializable

class FragmentMyRecipes : Fragment() {

    private var mAdapter: RecipeAdapter? = null
    private var itemList: ArrayList<ItemRecipe> = ArrayList()
    private var recyclerView: RecyclerView? = null
    private var progressHUD: KProgressHUD? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var emptyView: TextView? = null

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_recipe, null, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "MY RECIPES",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        val settings =
            requireActivity().getSharedPreferences("MyPrefsFile", 0)
        val gridCount = settings?.getInt("GRID_COUNT", 1) ?: 1

        progressHUD = KProgressHUD.create(requireContext())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("We fetching recent recipe list.")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        emptyView = view.findViewById(R.id.empty_view)
        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, gridCount)
        mLayoutManager.isAutoMeasureEnabled = true
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()

        var anySelectedCategory = 0
        for (i in Utils.categoryItemList.indices) {
            if (Utils.categoryItemList[i].selected) {
                anySelectedCategory = Utils.categoryItemList[i].id!!.toInt()
                break
            }
        }

        setDataList(anySelectedCategory)

        recyclerView!!.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                recyclerView!!,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val intent = Intent(activity, Detail::class.java)
                        intent.putExtra(
                            "DATA_OF_DOCUMENT",
                            itemList[position].fullData as Serializable?
                        )
                        startActivityForResult(intent, 100)
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)

        var anySelectedCategory = 0
        for (i in Utils.categoryItemList.indices) {
            if (Utils.categoryItemList[i].selected) {
                anySelectedCategory = Utils.categoryItemList[i].id!!.toInt()
                break
            }
        }

        val categorySelectionView = menu.findItem(R.id.action_category)
        if (anySelectedCategory == 0) {
            categorySelectionView.setIcon(R.drawable.ic_baseline_category_24)
        } else {
            categorySelectionView.setIcon(R.drawable.ic_baseline_category_24_selected)
        }

        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search)
            .actionView as SearchView
        searchView.setSearchableInfo(
            searchManager
                .getSearchableInfo(requireActivity().componentName)
        )
        searchView.maxWidth = Int.MAX_VALUE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mAdapter?.filter?.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                mAdapter?.filter?.filter(query)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (item.itemId == R.id.action_category) {
            val intent = Intent(activity, CategorySelection::class.java)
            startActivityForResult(intent, 101)
            true
        } else {
            false
        }
    }

    private fun setDataList(categoryId: Int) {
        progressHUD!!.show()
        db = FirebaseFirestore.getInstance()
        itemList.clear()

        if (categoryId == 0) {

            val prefs: SharedPreferences =
                requireActivity().getSharedPreferences("MyPrefsFile", 0)
            val userId = prefs.getString("loggedUserId", "").toString()

            db!!.collection("Recipes").whereEqualTo("userId", userId.toInt())
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        if (task.isSuccessful) {

                            if (task.result == null || task.result!!.isEmpty) {
                                progressHUD!!.dismiss()
                                emptyView?.visibility = View.VISIBLE
                                recyclerView?.visibility = View.GONE
                            } else {
                                emptyView?.visibility = View.GONE
                                recyclerView?.visibility = View.VISIBLE
                            }

                            for (document in task.result!!) {

                                Log.e("Doc", document.id)
                                val documentData = document.data
                                val title = documentData["name"]
                                val id = documentData["id"]
                                val preparationTime = documentData["prepare_time"]
                                val image: ArrayList<*> = documentData["images"] as ArrayList<*>

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
                                    item.fullData = documentData
                                    itemList.add(item)
                                    mAdapter =
                                        activity?.let { RecipeAdapter(itemList, requireContext()) }
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
                            progressHUD!!.dismiss()
                            emptyView?.visibility = View.VISIBLE
                            recyclerView?.visibility = View.GONE
                        }
                    }
                }
        } else {

            val prefs: SharedPreferences =
                requireActivity().getSharedPreferences("MyPrefsFile", 0)
            val userId = prefs.getString("loggedUserId", "").toString()

            db!!.collection("Recipes").whereEqualTo("userId", userId.toInt())
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        if (task.isSuccessful) {

                            if (task.result == null || task.result!!.isEmpty) {
                                progressHUD!!.dismiss()
                                emptyView?.visibility = View.VISIBLE
                                recyclerView?.visibility = View.GONE
                            } else {
                                emptyView?.visibility = View.GONE
                                recyclerView?.visibility = View.VISIBLE
                            }

                            for (document in task.result!!) {

                                Log.e("Doc", document.id)
                                val documentData = document.data
                                val title = documentData["name"]
                                val id = documentData["id"]
                                val preparationTime = documentData["prepare_time"]
                                val image: ArrayList<*> = documentData["images"] as ArrayList<*>

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
                                    item.fullData = documentData
                                    itemList.add(item)
                                    mAdapter =
                                        activity?.let { RecipeAdapter(itemList, requireContext()) }
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
                            progressHUD!!.dismiss()
                            emptyView?.visibility = View.VISIBLE
                            recyclerView?.visibility = View.GONE
                        }
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 || requestCode == 100) {
            var anySelectedCategory = 0
            for (i in Utils.categoryItemList.indices) {
                if (Utils.categoryItemList[i].selected) {
                    anySelectedCategory = Utils.categoryItemList[i].id!!.toInt()
                    break
                }
            }
            requireActivity().invalidateOptionsMenu()
            setDataList(anySelectedCategory)
        }
    }

}