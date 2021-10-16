package com.msc.app.cook.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.DirectionAdapter.DirectionViewHolder
import com.msc.app.cook.models.ItemPreparation

class DirectionAdapter : RecyclerView.Adapter<DirectionViewHolder> {
    private var directionList: List<ItemPreparation>
    private var isEditable = true
    private var mContext: Context
    private var directionListener: DirectionListener? = null

    constructor(context: Context, directionList: List<ItemPreparation>) {
        mContext = context
        this.directionList = directionList
    }

    constructor(context: Context, directionList: List<ItemPreparation>, isEditable: Boolean) {
        mContext = context
        this.directionList = directionList
        this.isEditable = isEditable
    }

    override fun getItemViewType(position: Int): Int {
        return if (isEditable) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectionViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.direction_item_row,
                parent, false
            )
        return DirectionViewHolder(v)
    }

    override fun onBindViewHolder(holder: DirectionViewHolder, position: Int) {
        val direction = directionList[position]
        holder.directionNumber.text = directionList[position].number
        holder.bind(direction)
    }

    override fun getItemCount(): Int {
        return directionList.size
    }

    inner class DirectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var directionText: TextView = itemView.findViewById(R.id.stepText)
        var directionNumber: TextView = itemView.findViewById(R.id.tv_number)
        var wasteBin: ImageView? = null
        fun bind(direction: ItemPreparation) {
            directionText.text = direction.step
        }

        init {
            if (isEditable) {
                wasteBin = itemView.findViewById(R.id.wasteBin)
                wasteBin!!.setOnClickListener {
                    if (directionListener != null) directionListener!!.onDeleteDirection(
                        adapterPosition
                    )
                }
            }
        }
    }

    fun setDirectionListener(directionListener: DirectionListener?) {
        this.directionListener = directionListener
    }

    interface DirectionListener {
        fun onDeleteDirection(position: Int)
    }
}