package com.msc.app.cook.main_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.msc.app.cook.Detail
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.new_recipe.FragmentNewIngredients
import kotlinx.android.synthetic.main.fragment_new_recipe.view.*

class FragmentNewRecipe : Fragment() {
    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_recipe, container, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "NEW RECIPE",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        view.btn_next.setOnClickListener {

            val tempFragment = FragmentNewIngredients()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, tempFragment).addToBackStack(null)
                .commit()

           // startActivity(Intent(activity, Detail::class.java))
        }

        return view
    }

    init {
        setHasOptionsMenu(false)
    }
}