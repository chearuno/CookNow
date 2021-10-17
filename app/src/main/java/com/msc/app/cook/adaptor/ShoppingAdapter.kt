package com.msc.app.cook.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.R
import com.msc.app.cook.models.ItemShopping

class ShoppingAdapter(private val items: List<ItemShopping>, private val context: Context) :
    RecyclerView.Adapter<ShoppingAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var pieces: TextView = view.findViewById<View>(R.id.tv_qty) as TextView
        var name: TextView = view.findViewById<View>(R.id.tv_name) as TextView
        var unit: TextView = view.findViewById<View>(R.id.tv_unit) as TextView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shopping, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemShopping = items[position]
        holder.name.text = itemShopping.name
        holder.pieces.text = itemShopping.qty.toString()
        if (itemShopping.unit != "-") {
            holder.unit.text = itemShopping.unit
        } else {
            holder.unit.text = ""
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}