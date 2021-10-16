package com.msc.app.cook

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.msc.app.cook.main_fragments.*
import com.msc.app.cook.utils.CustomTypefaceSpan

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var drawer: DrawerLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupToolbar(
            R.id.toolbar,
            "COOK IT",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        val fragmentHome = FragmentHome()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout, fragmentHome).commit()

        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val m: Menu = navigationView.menu
        for (i in 0 until m.size()) {
            val mi = m.getItem(i)
            val subMenu = mi.subMenu
            if (subMenu != null && subMenu.size() > 0) {
                for (j in 0 until subMenu.size()) {
                    val subMenuItem = subMenu.getItem(j)
                    applyFontToMenuItem(subMenuItem)
                }
            }
            applyFontToMenuItem(mi)
        }

        val header: View = navigationView.getHeaderView(0)
        val imageView = header.findViewById<View>(R.id.imageView) as ImageView
        Glide.with(this)
            .load("https://i.pravatar.cc/300")
            .apply(RequestOptions.circleCropTransform())
            .into(imageView)

        val profileLayout = header.findViewById<View>(R.id.profile_layout) as LinearLayout

        profileLayout.setOnClickListener {
            val fragTrans: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragTrans.replace(R.id.frameLayout, FragmentProfile()).commit()

            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.closeDrawer(GravityCompat.START)
        }

    }

    private fun applyFontToMenuItem(mi: MenuItem) {
        val font = Typeface.createFromAsset(assets, "fonts/SourceSansPro-Semibold.otf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(
            CustomTypefaceSpan("", font),
            0,
            mNewTitle.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        mi.title = mNewTitle
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawer?.openDrawer(GravityCompat.START) // OPEN DRAWER
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var fragment = androidx.fragment.app.Fragment()

        when (item.itemId) {
            R.id.recipes -> {
                fragment = FragmentHome()
            }
            R.id.new_recipe -> {
                fragment = FragmentNewRecipe()
            }
            R.id.saved -> {
                fragment = FragmentFavouriteRecipes()
            }
            R.id.my_recipes -> {
                fragment = FragmentMyRecipes()
            }
            R.id.shop_list -> {
                fragment = FragmentShoppingList()
            }
            R.id.unit_convertor -> {
                fragment = FragmentNewUnitConvertor()
            }
            R.id.setting -> {
                fragment = FragmentSettings()
            }
        }

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        ft.replace(R.id.frameLayout, fragment).commit()

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}