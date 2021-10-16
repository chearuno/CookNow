package com.msc.app.cook.main_fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.Detail
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.RecipeAdapter
import com.msc.app.cook.adaptor.RecyclerTouchListener
import com.msc.app.cook.models.ItemRecipe

class FragmentHome : Fragment() {

    private var mAdapter: RecipeAdapter? = null
    private var itemList: ArrayList<ItemRecipe> = ArrayList()
    private var recyclerView: RecyclerView? = null

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

        recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView

        mAdapter = activity?.let { RecipeAdapter(setupRecipe(), it) }
        val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 2)
        mLayoutManager.isAutoMeasureEnabled = true
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter

        recyclerView!!.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                recyclerView!!,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        val intent = Intent(activity, Detail::class.java)
                        intent.putExtra("RECIPE_NAME", itemList[position].recipe)
                        intent.putExtra("RECIPE_IMG", itemList[position].img)
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

    private fun setupRecipe(): List<ItemRecipe> {
        itemList = java.util.ArrayList<ItemRecipe>()
        val recipe = arrayOf(
            "BLOOD ORANGE CAKE",
            "SEMIFREDDO TIRAMISU",
            "MARBLE CAKE",
            "RICE PUDDING",
            "RAINBOW CAKE",
            "ICE CREAM",
            "STROWBERRY CAKE",
            "CUPCAKE FRUIT"
        )
        val img = arrayOf(
            "https://images.pexels.com/photos/53468/dessert-orange-food-chocolate-53468.jpeg?h=350&auto=compress&cs=tinysrgb",
            "https://images.pexels.com/photos/159887/pexels-photo-159887.jpeg?h=350&auto=compress",
            "https://images.pexels.com/photos/136745/pexels-photo-136745.jpeg?w=1260&h=750&auto=compress&cs=tinysrgb",
            "https://images.pexels.com/photos/39355/dessert-raspberry-leaf-almond-39355.jpeg?h=350&auto=compress&cs=tinysrgb",
            "https://images.pexels.com/photos/239578/pexels-photo-239578.jpeg?w=1260&h=750&auto=compress&cs=tinysrgb",
            "https://images.pexels.com/photos/8382/pexels-photo.jpg?w=1260&h=750&auto=compress&cs=tinysrgb",
            "https://images.pexels.com/photos/55809/dessert-strawberry-tart-berry-55809.jpeg?w=1260&h=750&auto=compress&cs=tinysrgb",
            "https://images.pexels.com/photos/55809/dessert-strawberry-tart-berry-55809.jpeg?w=1260&h=750&auto=compress&cs=tinysrgb"
        )
        val time = arrayOf("1h 5'", "30m", "1h 10'", "50m", "20m", "1h 20'", "20m", "1h 20'")
        for (i in recipe.indices) {
            val item = ItemRecipe()
            item.recipe = recipe[i]
            item.time = time[i]
            item.img = img[i]
            itemList.add(item)
        }
        return itemList
    }
}