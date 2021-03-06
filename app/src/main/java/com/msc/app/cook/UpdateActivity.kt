package com.msc.app.cook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.msc.app.cook.new_recipe.FragmentEditRecipe

class UpdateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val documentData =
            intent.getSerializableExtra("DATA_OF_DOCUMENT") as HashMap<String, Any>

        val bundle = Bundle()
        bundle.putSerializable("RECIPE_DATA", documentData)

        val tempFragment = FragmentEditRecipe()
        tempFragment.arguments = bundle
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout, tempFragment).commit()
    }
}