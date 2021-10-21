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
import com.msc.app.cook.models.ItemCategory

class CategoryAdapter(
    private val mContext: Context,
    private val mArrayList: List<ItemCategory>,

    ) : SelectableAdapter<CategoryAdapter.ViewHolder?>() {
    // Create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_category, null
        )
        return ViewHolder(itemLayoutView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val itemRecipe = mArrayList[position]
        viewHolder.recipe.text = itemRecipe.categoryName
        val url = Uri.parse(itemRecipe.img).toString()

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(32))
        Glide.with(mContext)
            .load(url)
            .apply(requestOptions)
            .into(viewHolder.imageView)

        if (itemRecipe.selected ) {
            viewHolder.imageViewSelection.visibility = View.VISIBLE
        } else {
            viewHolder.imageViewSelection.visibility = View.INVISIBLE
        }

        viewHolder.itemView.setOnClickListener {

            for (i in mArrayList.indices) {
                mArrayList[i].selected = false
            }

            itemRecipe.selected = !itemRecipe.selected

            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    class ViewHolder(itemLayoutView: View) :
        RecyclerView.ViewHolder(itemLayoutView) {
        var recipe: TextView = itemLayoutView.findViewById<View>(R.id.tv_recipe_name) as TextView
        var imageView: ImageView = itemLayoutView.findViewById<View>(R.id.iv_recipe) as ImageView
        var imageViewSelection: ImageView = itemLayoutView.findViewById<View>(R.id.iv_selected) as ImageView

    }
}