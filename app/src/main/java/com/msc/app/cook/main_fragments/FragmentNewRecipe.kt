package com.msc.app.cook.main_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.new_recipe.FragmentNewIngredients
import com.msc.app.cook.utils.CustomT5Dialog
import kotlinx.android.synthetic.main.fragment_new_recipe.view.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.contentView

class FragmentNewRecipe : Fragment() {
    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(false)
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
        }

        view.layout_cooling_t5.setOnClickListener {

            displayCustomT5Dialog(view)
        }

        return view
    }

    fun displayCustomT5Dialog(view: View) {
        val t5DialogUi by lazy {
            requireActivity().contentView?.let {
                CustomT5Dialog(AnkoContext.create(requireContext(), it))

            }
        }

        t5DialogUi?.okButton?.setOnClickListener {
            val minituesVal = t5DialogUi!!.minInputText.text.toString()
            val secondVal = t5DialogUi!!.secInputText.text.toString()

            var totalSec = 0;
            var min = 0
            var sec = 0
            var errorSec = false


            if (secondVal.equals("") && minituesVal.equals("")) {
                toast("Please enter time before proceed")
            } else {
                if (secondVal.equals("")) {
                    sec = 0
                } else {
                    if (secondVal.toInt() > 60) {
                        toast("Invalid Seconds. Please enter below 60 value for seconds")
                        errorSec = true
                    } else {
                        sec = secondVal.toInt()
                    }
                }

                if (minituesVal.equals("")) {
                    min = 0
                } else {
                    min = minituesVal.toInt()
                }
                totalSec = min + sec

                if (totalSec >= 0 && !errorSec) {
                    view.txt_cooling_t.text = "$min min $sec sec"
                    t5DialogUi?.dialog?.dismiss()
                } else {
                    toast("Please enter time before proceed")
                }

            }
        }

        t5DialogUi?.cancelButton?.setOnClickListener {
            t5DialogUi!!.dialog.dismiss()
        }
    }

    fun toast(str: String) {
        Toast.makeText(requireContext(), str, Toast.LENGTH_LONG).show()
    }
}