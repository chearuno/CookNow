package com.msc.app.cook.adaptor

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
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
import java.util.*
import kotlin.collections.ArrayList


class RecipeAdapter(private val items: List<ItemRecipe>, private val context: Context) :
    RecyclerView.Adapter<MyViewHolder>(), android.widget.Filterable {

    private var myDatasetFiltered: List<ItemRecipe>

    init {
        myDatasetFiltered = items
    }

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

        val settings =
            context.getSharedPreferences("MyPrefsFile", 0)
        val gridCount = settings?.getInt("GRID_COUNT", 1) ?: 1
        if(gridCount == 1){
            holder.recipe.textSize = 14F
            holder.time.textSize = 14F
            holder.time.visibility = View.VISIBLE
            holder.imageView.layoutParams.height = 600
        }else if(gridCount == 2){
            holder.recipe.textSize = 12F
            holder.time.textSize = 12F
            holder.time.visibility = View.VISIBLE
            holder.imageView.layoutParams.height = 250
        }else if(gridCount == 3){
            holder.recipe.textSize = 11F
            holder.time.visibility = View.GONE
            holder.imageView.layoutParams.height = 250
        }

        val itemRecipe = myDatasetFiltered[position]
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
        return myDatasetFiltered.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    myDatasetFiltered = items
                } else {
                    val filteredList: ArrayList<ItemRecipe> = ArrayList()
                    for (row in items) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.recipe!!.lowercase(Locale.getDefault())
                                .contains(charString.lowercase(Locale.getDefault()))
                        ) {
                            filteredList.add(row)
                        }
                    }
                    myDatasetFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = myDatasetFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                myDatasetFiltered = filterResults.values as List<ItemRecipe>
                notifyDataSetChanged()
            }
        }
    }
}