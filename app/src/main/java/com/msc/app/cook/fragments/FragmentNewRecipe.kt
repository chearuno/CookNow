package com.msc.app.cook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R

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
        val view = inflater.inflate(R.layout.fragment_new_recipe, null, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "NEW RECIPE",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        return view
    }

    init {
        setHasOptionsMenu(false)
    }
}