package com.msc.app.cook.adaptor

import android.content.Context
import android.graphics.Color
import com.smarteist.autoimageslider.SliderViewAdapter
import com.msc.app.cook.adaptor.SliderAdapterExample.SliderAdapterVH
import com.msc.app.cook.models.SliderItem
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.msc.app.cook.R
import com.bumptech.glide.Glide
import android.widget.Toast
import android.widget.TextView
import java.util.ArrayList

class SliderAdapterExample(private val context: Context) : SliderViewAdapter<SliderAdapterVH>() {
    private var mSliderItems: MutableList<SliderItem> = ArrayList()
    fun renewItems(sliderItems: MutableList<SliderItem>) {
        mSliderItems = sliderItems
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        mSliderItems.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(sliderItem: SliderItem) {
        mSliderItems.add(sliderItem)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderAdapterVH {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, null)
        return SliderAdapterVH(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH, position: Int) {
        val sliderItem = mSliderItems[position]
        Glide.with(viewHolder.itemView)
            .load(sliderItem.imageUrl)
            .into(viewHolder.imageViewBackground)
        viewHolder.itemView.setOnClickListener {
            Toast.makeText(
                context,
                "This is item in position $position",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getCount(): Int {
        //slider view count could be dynamic size
        return mSliderItems.size
    }

    class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        private var itemViewq: View = itemView
        var imageViewBackground: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)

    }
}