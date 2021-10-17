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


class Detail : BaseActivity(), PreparationAdapter.ViewHolder.ClickListener {
    private var collapsingToolbarLayout: CollapsingToolbarLayout? = null
    private var recyclerView: RecyclerView? = null
    private var mAdapter: ShoppingAdapter? = null
    private var recyclerViewPreparation: RecyclerView? = null
    private var mAdapterPreparation: PreparationAdapter? = null
    private var rootView: CoordinatorLayout? = null
    var sliderView: SliderView? = null
    private var adapter: SliderAdapterExample? = null

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

        val recipeName = intent.getStringExtra("RECIPE_NAME")
        val recipeImg = intent.getStringExtra("RECIPE_IMG")

        collapsingToolbarLayout =
            findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout

        collapsingToolbarLayout!!.isTitleEnabled = false

        app_bar_layout.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                Log.d("STATE", state!!.name)
                if (state == State.COLLAPSED) {
                    setupToolbar(
                        R.id.toolbar,
                        recipeName,
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

        tv_recipe_name.text = recipeName

        recyclerView = findViewById<View>(R.id.recyclerShopping) as RecyclerView
        mAdapter = ShoppingAdapter(generateShopping(), this)
        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter

        recyclerViewPreparation = findViewById<View>(R.id.recyclerPreparation) as RecyclerView
        mAdapterPreparation = PreparationAdapter(baseContext, generatePreparation(), this)
        val mLayoutManagerPreparation =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerViewPreparation!!.layoutManager = mLayoutManagerPreparation
        recyclerViewPreparation!!.itemAnimator = DefaultItemAnimator()
        recyclerViewPreparation!!.adapter = mAdapterPreparation

//        val image = findViewById<View>(R.id.image) as ImageView
//        Glide.with(this)
//            .load(Uri.parse(recipeImg))
//            .into(image)

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


        val urlList: List<String> = recipeImg!!.split(", ")
        val sliderItemList: MutableList<SliderItem> = ArrayList<SliderItem>()
        for (name in urlList) {
            val sliderItem = SliderItem()
            sliderItem.description = ""
            sliderItem.imageUrl =
                name
            sliderItemList.add(sliderItem)
            adapter!!.renewItems(sliderItemList)
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

    private fun generateShopping(): List<ItemShopping> {
        val itemList: MutableList<ItemShopping> = ArrayList()
        val name = arrayOf(
            "butter",
            "brown",
            "eggs",
            "flour",
            "baking powder",
            "of salt",
            "buttermilk",
            "orange juice"
        )
        val pieces = arrayOf("200g", "200g", "4", "300g", "2tsp", "1 pinch", "100ml", "50ml")
        for (i in name.indices) {
            val item = ItemShopping()
            item.pieces = pieces[i]
            item.name = name[i]
            itemList.add(item)
        }
        return itemList
    }

    private fun generatePreparation(): List<ItemPreparation> {
        val itemList: MutableList<ItemPreparation> = ArrayList()
        val step = arrayOf(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque viverra nibh venenatis dictum rutrum. Quisque id velit massa. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vestibulum augue sem, nec fringilla nunc finibus vitae. Praesent euismod dui leo, et sagittis magna rutrum a. Pellentesqu n refrigerator 1 hour.",
            "Lam condimentum ex vehicula, commodo nulla eget, semper velit. Donec non aliquam ligula. Sed facilisis id augue id iaculis. Curabitur eleifend cursus leo. Nam pu",
            "Maecenas nisi mi, viverra sed libero ac, molestie maximus ante. Donec id orci aliquam, semper dolor vitae, fringilla ante. Ut nisi metus, facilisis vel imperdiet nec",
            "Proin tristique risus vel odio porta volutpat. Praesent ornare tortor purus, ut lacinia ex aliquet nec. Maecenas arcu orci, luctus eu purus quis, convallis rhoncus diam. In rhoncus, libero at aliquam molestie, justo odio cursus ligula, non elementum arcu neque quis dui. Sed ultricies ex magna, congue consectetur massa rhoncus sit amet. Maecenas non luctus tortor. Maecenas interdum neque ac pellentesque gravida. Donec vel dui eros. Vivamus mattis lorem eleifend nisl placerat, "
        )
        for (i in step.indices) {
            val item = ItemPreparation()
            item.step = step[i]
            item.number = (i + 1).toString()
            itemList.add(item)
        }
        return itemList
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