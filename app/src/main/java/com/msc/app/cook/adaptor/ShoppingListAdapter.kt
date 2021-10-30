package com.msc.app.cook.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.R
import com.msc.app.cook.models.ItemShopping

class ShoppingListAdapter(private val items: List<ItemShopping>, private val context: Context) :
    RecyclerView.Adapter<ShoppingListAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById<View>(R.id.ingredientName) as TextView
        var quantity : TextView = view.findViewById<View>(R.id.ingredientQty) as TextView
        var unit: TextView = view.findViewById<View>(R.id.ingredientUnit) as TextView

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val itemShopping = items[position]
        holder.name.text = itemShopping.name
        holder.quantity.text = itemShopping.qty.toString()
        if (itemShopping.unit != "-") {
            holder.unit.text = itemShopping.unit
        } else {
            holder.unit.text = ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.shopping_list_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}