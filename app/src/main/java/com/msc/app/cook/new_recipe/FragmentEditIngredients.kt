package com.msc.app.cook.new_recipe

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.IngredientAdapter
import com.msc.app.cook.adaptor.RecyclerTouchListener
import com.msc.app.cook.models.ItemShopping
import com.msc.app.cook.utils.Utils
import com.msc.app.cook.utils.Utils.toastError
import kotlinx.android.synthetic.main.fragment_new_ingredients.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class FragmentEditIngredients : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_edit_ingredients, null, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Add Ingredients"

        val putData = requireArguments().getSerializable("RECIPE_DATA") as HashMap<String, Any>

        view.btn_next.setOnClickListener {

            if (ingredientList.isEmpty()) {
                Utils.alerterDialog(
                    "Error!",
                    "Please add one or more ingredients.",
                    requireActivity()
                )
            } else {
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(view.windowToken, 0)

                putData["shopping_list"] = ingredientList

                val bundle = Bundle()
                bundle.putSerializable("RECIPE_DATA", putData)

                val tempFragment = FragmentEditDirections()
                tempFragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, tempFragment).addToBackStack(null)
                    .commit()
            }
        }

        val shplist = putData["shopping_list"] as ArrayList<*>

        shplist.forEach {

            val item = ItemShopping()
            val shippingItem = it as HashMap<String, Any>

            item.unit = shippingItem["unit"] as String?
            item.name = shippingItem["name"] as String?
            item.qty = shippingItem["qty"] as Long?
            ingredientList.add(item)
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

            var newIngredientUnit = ""
            if (ingredientQTYText!!.text != "-") {
                newIngredientUnit = ingredientQTYText!!.text as String
            }

            when {
                newIngredient.isEmpty() -> {
                    toastError("Please enter ingredient name", requireContext())
                }
                newIngredientPieces.isEmpty() -> {
                    toastError("Please add pieces", requireContext())
                }
                else -> {

                    ingredientField!!.setText("")
                    ingredientPieces!!.setText("")
                    val item = ItemShopping()
                    item.unit = newIngredientUnit
                    item.name = newIngredient
                    item.qty = newIngredientPieces.toLong()
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
            "pounds",
            "No measurement Unit (-)"
        )

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Select Measurement Unit")

        builder.setItems(wsList) { _, which ->
            val selected = wsList[which]
            if (selected == "No measurement Unit (-)") {
                ingredientQTYText!!.text = "-"
            } else {
                ingredientQTYText!!.text = selected
            }

        }

        val dialog = builder.create()
        dialog.show()

    }

}