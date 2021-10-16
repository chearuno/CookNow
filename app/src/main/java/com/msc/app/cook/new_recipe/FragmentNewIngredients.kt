package com.msc.app.cook.new_recipe

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import kotlinx.android.synthetic.main.fragment_new_ingredients.view.*

class FragmentNewIngredients : Fragment() {
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

        return view
    }

}