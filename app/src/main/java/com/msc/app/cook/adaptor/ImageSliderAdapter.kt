package com.msc.app.cook.adaptor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.msc.app.cook.FullImageActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.ImageSliderAdapter.SliderAdapterVH
import com.msc.app.cook.models.SliderItem
import com.smarteist.autoimageslider.SliderViewAdapter
import java.util.*

class ImageSliderAdapter(private val context: Context) : SliderViewAdapter<SliderAdapterVH>() {
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
        val requestOptions = RequestOptions()
        requestOptions.centerCrop()

        Glide.with(viewHolder.itemView)
            .setDefaultRequestOptions(requestOptions)
            .load(sliderItem.imageUrl)
            .into(viewHolder.imageViewBackground)

        viewHolder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, FullImageActivity::class.java).putExtra(
                    "image",
                    sliderItem.imageUrl
                )
            )

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