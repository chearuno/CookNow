package com.msc.app.cook.main_fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.ShoppingAdapter
import com.msc.app.cook.adaptor.ShoppingListAdapter
import com.msc.app.cook.models.ItemShopping

class FragmentShoppingList : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var mAdapter: ShoppingListAdapter? = null
    private val itemShoppingList: ArrayList<ItemShopping> = ArrayList()

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_shopping_list, null, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "SHOPPING LIST",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        recyclerView = view.findViewById<View>(R.id.shoppingList) as RecyclerView
        val itemShopping = ItemShopping()
        itemShopping.name = "Sugar"
        itemShopping.qty = 100
        itemShopping.unit = "g"

        val itemShopping2 = ItemShopping()
        itemShopping2.name = "Flour"
        itemShopping2.qty = 100
        itemShopping2.unit = "g"

        itemShoppingList.add(itemShopping)
        itemShoppingList.add(itemShopping2)
        mAdapter = ShoppingListAdapter(itemShoppingList, requireContext())
        val mLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)
    }

    init {
        setHasOptionsMenu(true)
    }
}