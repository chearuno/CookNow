package com.msc.app.cook.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.IngredientAdapter.IngredientViewHolder
import com.msc.app.cook.models.ItemShopping

class IngredientAdapter : RecyclerView.Adapter<IngredientViewHolder> {
    private var ingredientList: List<ItemShopping>
    private var isEditable = true
    private var mContext: Context
    private var ingredientListener: IngredientListener? = null

    constructor(context: Context, ingredientList: List<ItemShopping>) {
        mContext = context
        this.ingredientList = ingredientList
    }

    constructor(context: Context, ingredientList: List<ItemShopping>, isEditable: Boolean) {
        mContext = context
        this.ingredientList = ingredientList
        this.isEditable = isEditable
    }

    override fun getItemViewType(position: Int): Int {
        return if (isEditable) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.ingredient_item_row,
                parent, false
            )
        return IngredientViewHolder(v)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredientList[position]
        holder.bind(ingredient)
    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var ingredientText: TextView = itemView.findViewById(R.id.ingredientText)
        private var ingredientPices: TextView = itemView.findViewById(R.id.ingredientQty)
        var wasteBin: ImageView? = null
        fun bind(ingredient: ItemShopping) {
            ingredientText.text = ingredient.name
            ingredientPices.text = ingredient.pieces
        }

        init {
            if (isEditable) {
                wasteBin = itemView.findViewById(R.id.wasteBin)
                wasteBin!!.setOnClickListener {
                    if (ingredientListener != null) ingredientListener!!.onDeleteIngredient(
                        adapterPosition
                    )
                }
            }
        }
    }

    fun setIngredientListener(ingredientListener: IngredientAdapter.IngredientListener) {
        this.ingredientListener = ingredientListener
    }

    interface IngredientListener {
        fun onDeleteIngredient(position: Int)
    }
}