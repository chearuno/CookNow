package com.msc.app.cook

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.msc.app.cook.adaptor.ImageSliderAdapter
import com.msc.app.cook.adaptor.PreparationAdapter
import com.msc.app.cook.adaptor.ShoppingAdapter
import com.msc.app.cook.models.ItemPreparation
import com.msc.app.cook.models.ItemShopping
import com.msc.app.cook.models.SliderItem
import com.msc.app.cook.utils.Utils.itemShoppingMainList
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_detail.*


class Detail : BaseActivity(), PreparationAdapter.ViewHolder.ClickListener {

    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var recyclerView: RecyclerView? = null
    private var mAdapter: ShoppingAdapter? = null
    private var recyclerViewPreparation: RecyclerView? = null
    private var mAdapterPreparation: PreparationAdapter? = null
    private var rootView: CoordinatorLayout? = null
    private var sliderView: SliderView? = null
    private var adapterImage: ImageSliderAdapter? = null
    private var storage: FirebaseStorage? = null
    private var currentSarvingQty = 1
    private val itemShoppingList: ArrayList<ItemShopping> = ArrayList()
    private val itemShoppingListForQty: ArrayList<ItemShopping> = ArrayList()
    private var db: FirebaseFirestore? = null
    private var progressHUD: KProgressHUD? = null
    var documentData: HashMap<String, Any> = HashMap()
    var userIdLogged = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        rootView = findViewById<View>(R.id.rootview) as CoordinatorLayout

        val toolbar: Toolbar = findViewById(R.id.toolbar_details)
        setSupportActionBar(toolbar)

        storage = FirebaseStorage.getInstance()

        documentData =
            intent.getSerializableExtra("DATA_OF_DOCUMENT") as HashMap<String, Any>

        db = FirebaseFirestore.getInstance()

        progressHUD = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        collapsingToolbarLayout =
            findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout

        collapsingToolbarLayout!!.isTitleEnabled = false

        app_bar_layout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                Log.d("STATE", state!!.name)
                if (state == State.COLLAPSED) {
                    supportActionBar?.title = documentData["name"] as String?
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)

                } else {
                    supportActionBar?.title = ""
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)

                }
            }
        })

        tv_recipe_name.text = documentData["name"] as String?
        tv_desc.text = documentData["description"] as String?
        tv_author.text = "by ${documentData["createdBy"]}"
        tv_time.text = documentData["prepare_time"] as String?

        val preparation: ArrayList<*> = documentData["preparation"] as ArrayList<*>
        val shoppingList: ArrayList<*> = documentData["shopping_list"] as ArrayList<*>

        recyclerView = findViewById<View>(R.id.recyclerShopping) as RecyclerView

        val userId = documentData["userId"].toString()
        val isPrivate = documentData["isPrivate"]

        val prefs: SharedPreferences = getSharedPreferences("MyPrefsFile", 0)
        userIdLogged = prefs.getString("loggedUserId", "").toString()

        if (userId == userIdLogged) {
            ll_action.visibility = View.VISIBLE
            if (isPrivate as Boolean) {
                tv_public.text = "Private"
            } else {
                tv_public.text = "Public"
            }
        } else {
            ll_action.visibility = View.GONE
        }

        if (isPrivate as Boolean) {
            private_image.visibility = View.VISIBLE
            tv_private_icon.visibility = View.VISIBLE
        } else {
            private_image.visibility = View.GONE
            tv_private_icon.visibility = View.GONE
        }

        val favouriteSet = prefs.getString("fav_set", "").toString()
        val temp = favouriteSet.split(",")
        var lovedOne = false
        if (!temp.isNullOrEmpty() || !temp.equals("")) {
            if (temp[0] == "") {
                lovedOne = false
            } else {
                temp.forEach {
                    if (it == documentData["id"].toString()) {
                        lovedOne = true
                        return@forEach
                    }
                }
            }
        }
        if (lovedOne) {
            tv_loved_user.visibility = View.VISIBLE
            tv_love.visibility = View.GONE
            tv_loved_user.text = "${documentData["likes"]}"
        } else {
            tv_loved_user.visibility = View.GONE
            tv_love.visibility = View.VISIBLE
            tv_love.text = "${documentData["likes"]}"
        }


        shoppingList.forEach {

            val item = ItemShopping()
            val shippingItem = it as HashMap<String, Any>

            item.unit = shippingItem["unit"] as String?
            item.name = shippingItem["name"] as String?
            item.qty = shippingItem["qty"] as Long?
            itemShoppingList.add(item)
            itemShoppingListForQty.add(item)
        }

        mAdapter = ShoppingAdapter(itemShoppingList, this)
        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter

        recyclerViewPreparation = findViewById<View>(R.id.recyclerPreparation) as RecyclerView

        val itemPreparationList: MutableList<ItemPreparation> = ArrayList()
        var i = 1
        preparation.forEach {

            val item = ItemPreparation()
            item.step = it.toString()
            item.number = i.toString()
            itemPreparationList.add(item)
            i++
        }

        mAdapterPreparation = PreparationAdapter(baseContext, itemPreparationList, this)
        val mLayoutManagerPreparation =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewPreparation!!.layoutManager = mLayoutManagerPreparation
        recyclerViewPreparation!!.itemAnimator = DefaultItemAnimator()
        recyclerViewPreparation!!.adapter = mAdapterPreparation

        sliderView = findViewById(R.id.imageSlider)


        adapterImage = ImageSliderAdapter(this)
        sliderView!!.setSliderAdapter(adapterImage!!)
        sliderView!!.setIndicatorAnimation(IndicatorAnimationType.WORM)

        sliderView!!.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView!!.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        sliderView!!.indicatorSelectedColor = Color.parseColor("#E82D50")
        sliderView!!.indicatorUnselectedColor = Color.parseColor("#FF000000")
        sliderView!!.scrollTimeInSec = 3
        sliderView!!.isAutoCycle = true
        sliderView!!.startAutoCycle()


        sliderView!!.setOnIndicatorClickListener {
            Log.i(
                "GGG",
                "onIndicatorClicked: " + sliderView!!.currentPagePosition
            )
        }

        val imageList: ArrayList<*> = documentData["images"] as ArrayList<*>

        val sliderItemList: MutableList<SliderItem> = ArrayList()
        imageList.forEach {

            val storageRef = storage!!.reference

            val downloadRef = storageRef.child("RecipeImages/${it}")

            downloadRef.downloadUrl.addOnSuccessListener { uri ->
                Log.e("Doc1", " => $uri")
                val sliderItem = SliderItem()
                sliderItem.description = ""
                sliderItem.imageUrl = uri.toString()
                sliderItemList.add(sliderItem)
                adapterImage!!.renewItems(sliderItemList)

            }
                .addOnFailureListener {
                    Log.e("Doc", "Error getting Data: ")
                }
        }

        btn_saving_plus.setOnClickListener {
            currentSarvingQty++
            tv_current_saving.text = currentSarvingQty.toString()
            val tempItemList: ArrayList<ItemShopping> = ArrayList()
            itemShoppingListForQty.forEach {
                val item = ItemShopping()
                item.unit = it.unit
                item.name = it.name
                item.qty = it.qty!! * currentSarvingQty
                tempItemList.add(item)
            }
            itemShoppingList.clear()
            itemShoppingList.addAll(tempItemList)
            mAdapter!!.notifyDataSetChanged()
        }

        btn_saving_minus.setOnClickListener {
            if (currentSarvingQty != 1) {
                currentSarvingQty--
            }
            tv_current_saving.text = currentSarvingQty.toString()
            val tempItemList: ArrayList<ItemShopping> = ArrayList()
            itemShoppingListForQty.forEach {
                val item = ItemShopping()
                item.unit = it.unit
                item.name = it.name
                item.qty = it.qty!! * currentSarvingQty
                tempItemList.add(item)
            }
            itemShoppingList.clear()
            itemShoppingList.addAll(tempItemList)
            mAdapter!!.notifyDataSetChanged()
        }

        tv_delete.setOnClickListener {
            deleteItem()
        }

        tv_public.setOnClickListener {
            makePrivateItem()
        }

        tv_private_icon.setOnClickListener {
            makePrivateItem()
        }

        tv_update.setOnClickListener {
            updateItem()
        }

        tv_love.setOnClickListener {
            makeFavItem()
        }

        tv_loved_user.setOnClickListener {
            removeFavItem()
        }

        textAddTOShoppingList.setOnClickListener {
            addToShoppinList()
        }

    }

    override fun onItemClicked(position: Int) {}
    override fun onItemLongClicked(position: Int): Boolean {
        toggleSelection(position)
        return true
    }

    private fun toggleSelection(position: Int) {
        mAdapterPreparation!!.toggleSelection(position)
    }

    private fun makeFavItem() {
        progressHUD!!.show()

        val prefs: SharedPreferences = getSharedPreferences("MyPrefsFile", 0)
        var favouriteSet = prefs.getString("fav_set", "").toString()
        favouriteSet = if (favouriteSet == "") {
            "${documentData["id"]}"
        } else {
            "$favouriteSet,${documentData["id"]}"
        }

        val count = tv_love.text.toString().toInt() + 1
        db!!.collection("Recipes").document(documentData["id"].toString())
            .update("likes", count)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db!!.collection("User").document(userIdLogged)
                        .update("favList", favouriteSet)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                tv_loved_user.visibility = View.VISIBLE
                                tv_loved_user.text = count.toString()
                                tv_love.visibility = View.GONE
                                val editor = prefs.edit()
                                editor.putString("fav_set", favouriteSet)
                                editor.apply()
                                progressHUD!!.dismiss()

                            } else {
                                Log.e("Doc", "Error getting documents: ", task1.exception)
                                progressHUD!!.dismiss()
                            }
                        }

                } else {
                    Log.e("Doc", "Error getting documents: ", task.exception)
                    progressHUD!!.dismiss()
                }
            }
    }

    private fun removeFavItem() {
        progressHUD!!.show()
        val prefs: SharedPreferences = getSharedPreferences("MyPrefsFile", 0)
        val favouriteSet = prefs.getString("fav_set", "").toString()
        var newList = ""
        val temp = favouriteSet.split(",")

        if (!temp.isNullOrEmpty() || !temp.equals("")) {
            if (temp[0] == "") {
                newList += documentData["id"].toString()
            } else {
                temp.forEach {
                    if (it != documentData["id"].toString()) {
                        newList += "$it,"
                    }
                }
            }
        }

        if (newList.isNotEmpty() && newList[newList.length - 1] == ',') {
            newList = newList.substring(0, newList.length - 1)
        }

        val count = tv_loved_user.text.toString().toInt() - 1
        db!!.collection("Recipes").document(documentData["id"].toString())
            .update("likes", count)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db!!.collection("User").document(userIdLogged)
                        .update("favList", newList)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                val editor = prefs.edit()
                                editor.putString("fav_set", newList)
                                editor.apply()
                                Log.e("dfdfdf>", newList)
                                tv_loved_user.visibility = View.GONE
                                tv_love.text = count.toString()
                                tv_love.visibility = View.VISIBLE
                                progressHUD!!.dismiss()

                            } else {
                                Log.e("Doc", "Error getting documents: ", task1.exception)
                                progressHUD!!.dismiss()
                            }
                        }

                } else {
                    Log.e("Doc", "Error getting documents: ", task.exception)
                    progressHUD!!.dismiss()
                }
            }
    }

    private fun addToShoppinList() {

        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Add To Shopping List")
            .setContentText("Do you want to add items to shopping list?")
            .setCancelText("No")
            .setConfirmText("Yes")
            .showCancelButton(true)

            .setCancelClickListener { sDialog ->
                sDialog.dismissWithAnimation()

            }
            .setConfirmClickListener { sDialog ->

                itemShoppingMainList.addAll(itemShoppingList)
                itemShoppingList

                sDialog
                    .setTitleText("Added!")
                    .setContentText("Your shopping list has been updated!")
                    .setConfirmText("OK")
                    .setConfirmClickListener {
                        finish()
                    }
                    .showCancelButton(false)
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

            }
            .show()

    }

    private fun deleteItem() {

        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Are you sure?")
            .setContentText("Won't be able to recover this recipe!")
            .setCancelText("Cancel")
            .setConfirmText("Delete")
            .showCancelButton(true)

            .setCancelClickListener { sDialog ->
                sDialog.dismissWithAnimation()

            }
            .setConfirmClickListener { sDialog ->

                progressHUD!!.show()

                db!!.collection("Recipes").document(documentData["id"].toString())
                    .delete()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressHUD!!.dismiss()
                            sDialog
                                .setTitleText("Deleted!")
                                .setContentText("Your recipe file has been deleted!")
                                .setConfirmText("OK")
                                .setConfirmClickListener {
                                    finish()
                                }
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

                        } else {
                            Log.e("Doc", "Error getting documents: ", task.exception)
                            progressHUD!!.dismiss()

                        }
                    }
            }
            .show()

    }

    private fun makePrivateItem() {

        val isPrivate = documentData["isPrivate"] as Boolean
        var message = "Do yo wan to make this private"
        if (isPrivate) {
            message = "Do yo wan to make this public"
        }

        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Alert!")
            .setContentText(message)
            .setCancelText("Cancel")
            .setConfirmText("Yes")
            .showCancelButton(true)

            .setCancelClickListener { sDialog ->
                sDialog.dismissWithAnimation()

            }
            .setConfirmClickListener { sDialog ->

                progressHUD!!.show()


                db!!.collection("Recipes").document(documentData["id"].toString())
                    .update("isPrivate", !isPrivate)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            progressHUD!!.dismiss()
                            sDialog
                                .setTitleText("Done!")
                                .setContentText("Your recipe file has been changed!")
                                .setConfirmText("OK")
                                .setConfirmClickListener {
                                    finish()
                                }
                                .showCancelButton(false)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

                        } else {
                            Log.e("Doc", "Error getting documents: ", task.exception)
                            progressHUD!!.dismiss()
                        }
                    }
            }
            .show()


    }

    private fun updateItem() {

        val intent = Intent(this, UpdateActivity::class.java)
        intent.putExtra("DATA_OF_DOCUMENT", documentData)
        startActivity(intent)
    }

}

abstract class AppBarStateChangeListener : OnOffsetChangedListener {
    enum class State {
        EXPANDED, COLLAPSED, IDLE
    }

    private var mCurrentState = State.IDLE
    override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
        mCurrentState = if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED)
            }
            State.EXPANDED
        } else if (Math.abs(i) >= appBarLayout.totalScrollRange) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED)
            }
            State.COLLAPSED
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE)
            }
            State.IDLE
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)
}