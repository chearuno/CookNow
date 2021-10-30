package com.msc.app.cook.main_fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.ShoppingListAdapter
import com.msc.app.cook.utils.Utils.itemShoppingMainList

class FragmentShoppingList : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var mAdapter: ShoppingListAdapter? = null
    private var emptyView: TextView? = null
    private var clearButton: Button? = null

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(false)
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
        emptyView = view.findViewById(R.id.empty_view)
        clearButton = view.findViewById(R.id.clear)

        mAdapter = ShoppingListAdapter(itemShoppingMainList, requireContext())
        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = mAdapter

        toggleEmptyView()

        clearButton!!.setOnClickListener {
            addToShoppinList()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_home, menu)
    }

    private fun toggleEmptyView() {
        if (itemShoppingMainList.size == 0) {
            emptyView!!.visibility = View.VISIBLE
            recyclerView!!.visibility = View.GONE
            clearButton!!.visibility = View.GONE
        } else {
            emptyView!!.visibility = View.GONE
            recyclerView!!.visibility = View.VISIBLE
            clearButton!!.visibility = View.VISIBLE
        }
    }

    private fun addToShoppinList() {

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Alert")
            .setContentText("Do you want to clear shopping list?")
            .setCancelText("No")
            .setConfirmText("Yes")
            .showCancelButton(true)

            .setCancelClickListener { sDialog ->
                sDialog.dismissWithAnimation()

            }
            .setConfirmClickListener { sDialog ->

                itemShoppingMainList.clear()

                toggleEmptyView()

                sDialog
                    .setTitleText("Done!")
                    .setContentText("Your shopping list has been cleaned!")
                    .setConfirmText("OK")
                    .setConfirmClickListener {
                        sDialog.dismissWithAnimation()
                    }
                    .showCancelButton(false)
                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE)

            }
            .show()

    }
}