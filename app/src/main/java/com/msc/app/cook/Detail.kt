package com.msc.app.cook

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.storage.FirebaseStorage
import com.msc.app.cook.adaptor.PreparationAdapter
import com.msc.app.cook.adaptor.ShoppingAdapter
import com.msc.app.cook.adaptor.SliderAdapterExample
import com.msc.app.cook.models.ItemPreparation
import com.msc.app.cook.models.ItemShopping
import com.msc.app.cook.models.SliderItem
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*
import kotlin.collections.ArrayList


class Detail : BaseActivity(), PreparationAdapter.ViewHolder.ClickListener {
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var recyclerView: RecyclerView? = null
    private var mAdapter: ShoppingAdapter? = null
    private var recyclerViewPreparation: RecyclerView? = null
    private var mAdapterPreparation: PreparationAdapter? = null
    private var rootView: CoordinatorLayout? = null
    var sliderView: SliderView? = null
    private var adapter: SliderAdapterExample? = null
    var storage: FirebaseStorage? = null
    var currentSarvingQty = 1
    val itemShoppingList: ArrayList<ItemShopping> = ArrayList()
    val itemShoppingListForQty: ArrayList<ItemShopping> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        rootView = findViewById<View>(R.id.rootview) as CoordinatorLayout
        setupToolbar(
            R.id.toolbar,
            "",
            android.R.color.white,
            android.R.color.transparent,
            R.drawable.ic_arrow_back
        )
        storage = FirebaseStorage.getInstance()

        val documentData: Map<String, Any> =
            intent.getSerializableExtra("DATA_OF_DOCUMENT") as Map<String, Any>

        collapsingToolbarLayout =
            findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout

        collapsingToolbarLayout!!.isTitleEnabled = false

        app_bar_layout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                Log.d("STATE", state!!.name)
                if (state == State.COLLAPSED) {
                    setupToolbar(
                        R.id.toolbar,
                        documentData["name"] as String?,
                        android.R.color.white,
                        android.R.color.transparent,
                        R.drawable.ic_arrow_back
                    )

                } else {
                    setupToolbar(
                        R.id.toolbar,
                        "",
                        android.R.color.white,
                        android.R.color.transparent,
                        R.drawable.ic_arrow_back
                    )

                }
            }
        })

        tv_recipe_name.text = documentData["name"] as String?
        tv_desc.text = documentData["description"] as String?
        tv_author.text = "by ${documentData["createdBy"]}"
        tv_time.text = documentData["prepare_time"] as String?
        tv_love.text = "${documentData["likes"]}"

        val preparation: ArrayList<*> = documentData["preparation"] as ArrayList<*>
        val shoppingList: ArrayList<*> = documentData["shopping_list"] as ArrayList<*>

        recyclerView = findViewById<View>(R.id.recyclerShopping) as RecyclerView


        shoppingList.forEach {

            val item = ItemShopping()
            val shippingItem = it as HashMap<*, *>

            item.unit = shippingItem["unit"] as String?
            item.name = shippingItem["name"] as String?
            item.qty = shippingItem["qty"] as Long?
            itemShoppingList.add(item)
            itemShoppingListForQty.add(item);
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


        adapter = SliderAdapterExample(this)
        sliderView!!.setSliderAdapter(adapter!!)
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
                adapter!!.renewItems(sliderItemList)

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

    }

    override fun onItemClicked(position: Int) {}
    override fun onItemLongClicked(position: Int): Boolean {
        toggleSelection(position)
        return true
    }

    private fun toggleSelection(position: Int) {
        mAdapterPreparation!!.toggleSelection(position)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_home, menu)
        var drawable = menu?.findItem(R.id.action_search)
        drawable?.isVisible = false
        return true
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