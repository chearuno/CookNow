package com.msc.app.cook.new_recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.IngredientAdapter
import com.msc.app.cook.adaptor.RecyclerTouchListener
import com.msc.app.cook.models.ItemShopping
import kotlinx.android.synthetic.main.fragment_new_ingredients.view.*

class FragmentNewIngredients : Fragment() {

    private val ingredientList: ArrayList<ItemShopping> = ArrayList()
    private var ingredientAdapter: IngredientAdapter? = null

    private var ingredientRecyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var addButton: Button? = null
    private var ingredientField: EditText? = null
    private var ingredientPieces: EditText? = null
    private var ingredientQTYText: TextView? = null
    private var layoutQty: LinearLayout? = null


    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_ingredients, null, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "Add Ingredients",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        view.btn_next.setOnClickListener {

            val tempFragment = FragmentNewDirections()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, tempFragment).addToBackStack(null)
                .commit()
        }

        ingredientRecyclerView = view.findViewById(R.id.recyclerView)
        emptyView = view.findViewById(R.id.empty_view)
        addButton = view.findViewById(R.id.add_button)
        ingredientField = view.findViewById(R.id.ingredientField)
        ingredientPieces = view.findViewById(R.id.ingredientPieces)
        ingredientQTYText = view.findViewById(R.id.txtQty)
        layoutQty = view.findViewById(R.id.qtyLayout)

        ingredientAdapter = IngredientAdapter(requireActivity(), ingredientList)

        ingredientRecyclerView!!.addOnItemTouchListener(
            RecyclerTouchListener(
                activity,
                ingredientRecyclerView!!,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        ingredientList.removeAt(position)
                        toggleEmptyView()
                        ingredientAdapter!!.notifyDataSetChanged()
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )

        toggleEmptyView()

        ingredientRecyclerView!!.setHasFixedSize(true)
        ingredientRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        ingredientRecyclerView!!.adapter = ingredientAdapter

        view.qtyLayout.setOnClickListener {
            showMeasurementDialog()
        }

        addButton!!.setOnClickListener {
            val newIngredient: String = ingredientField!!.text.toString()
            val newIngredientPieces: String = ingredientPieces!!.text.toString()

            when {
                newIngredient.isEmpty() -> {
                    Toast.makeText(
                        requireContext(),
                        "Please enter ingredient name",
                        Toast.LENGTH_LONG
                    ).show()
                }
                newIngredientPieces.isEmpty() -> {
                    Toast.makeText(requireContext(), "Please add pieces", Toast.LENGTH_LONG).show()
                }
                else -> {

                    ingredientField!!.setText("")
                    ingredientPieces!!.setText("")
                    val item = ItemShopping()
                    item.pieces = "$newIngredientPieces ${ingredientQTYText!!.text}"
                    item.name = newIngredient
                    ingredientList.add(item)
                    toggleEmptyView()
                    ingredientAdapter!!.notifyDataSetChanged()
                }
            }
        }

        return view
    }

    private fun toggleEmptyView() {
        if (ingredientList.size == 0) {
            emptyView!!.visibility = View.VISIBLE
            ingredientRecyclerView!!.visibility = View.GONE
        } else {
            emptyView!!.visibility = View.GONE
            ingredientRecyclerView!!.visibility = View.VISIBLE
        }
    }

    private fun showMeasurementDialog() {

        val wsList = arrayOf(
            "g",
            "kg",
            "ml",
            "l",
            "tablespoon",
            "teaspoons",
            "pint",
            "cups",
            "ounces",
            "pounds"
        )

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Select Measurement Unit")

        builder.setItems(wsList) { _, which ->
            val selected = wsList[which]
            ingredientQTYText!!.text = selected

        }

        val dialog = builder.create()
        dialog.show()

    }

}