package com.msc.app.cook.adaptor

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.RecipeAdapter.MyViewHolder
import com.msc.app.cook.models.ItemRecipe


class RecipeAdapter(private val items: List<ItemRecipe>, private val context: Context) :
    RecyclerView.Adapter<MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var recipe: TextView = view.findViewById<View>(R.id.tv_recipe_name) as TextView
        var time: TextView = view.findViewById<View>(R.id.tv_time) as TextView
        var imageView: ImageView = view.findViewById<View>(R.id.iv_recipe) as ImageView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemRecipe = items[position]
        holder.recipe.text = itemRecipe.recipe
        holder.time.text = itemRecipe.time
        val url = Uri.parse(itemRecipe.img).toString()
        val urlList: List<String> = url.split(",")
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(32))
        Glide.with(context)
            .load(urlList.first())
            .apply(requestOptions)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}