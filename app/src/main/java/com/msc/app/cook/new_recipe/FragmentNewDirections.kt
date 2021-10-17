package com.msc.app.cook.new_recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.DirectionAdapter
import com.msc.app.cook.adaptor.RecyclerTouchListener
import com.msc.app.cook.models.ItemPreparation
import kotlinx.android.synthetic.main.fragment_new_directions.view.*

class FragmentNewDirections : Fragment() {

    private val directionList: ArrayList<ItemPreparation> = ArrayList()
    private var directionAdapter: DirectionAdapter? = null

    private var directionRecyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var addButton: Button? = null
    private var directionField: EditText? = null


    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_directions, null, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "Add Steps",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        view.btn_finish.setOnClickListener {

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
                Toast.makeText(requireContext(), "Please add preparation step", Toast.LENGTH_LONG)
                    .show()
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
}