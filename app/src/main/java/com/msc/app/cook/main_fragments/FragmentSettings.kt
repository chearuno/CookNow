package com.msc.app.cook.main_fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.UserLogin
import kotlinx.android.synthetic.main.fragment_settings.view.*

class FragmentSettings : Fragment() {
    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, null, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "SETTINGS",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )
        view.button.setOnClickListener {
            val settings =
                requireActivity().getSharedPreferences("MyPrefsFile", 0)
            val editor = settings.edit()
            editor.putInt("GRID_COUNT", 1)
            editor.apply()
        }

        view.button2.setOnClickListener {
            val settings =
                requireActivity().getSharedPreferences("MyPrefsFile", 0)
            val editor = settings.edit()
            editor.putInt("GRID_COUNT", 2)
            editor.apply()
        }

        view.button3.setOnClickListener {
            val settings =
                requireActivity().getSharedPreferences("MyPrefsFile", 0)
            val editor = settings.edit()
            editor.putInt("GRID_COUNT", 3)
            editor.apply()
        }

        view.button_logout.setOnClickListener {

            val settings =
                requireActivity().getSharedPreferences("MyPrefsFile", 0)
            val editor = settings.edit()
            editor.remove("loggedUserFirstName")
            editor.remove("loggedUserLastName")
            editor.remove("loggedUserEmail")
            editor.remove("loggedUserId")
            editor.remove("fav_set")
            editor.apply()

            requireActivity().finish()
            val intent = Intent(requireActivity(), UserLogin::class.java)
            startActivity(intent)
        }
        return view
    }

    init {
        setHasOptionsMenu(false)
    }
}