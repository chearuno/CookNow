package com.msc.app.cook.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.R
import com.msc.app.cook.models.ItemPreparation

class PreparationAdapter(
    private val mContext: Context,
    private val mArrayList: List<ItemPreparation>,
    private val clickListener: ViewHolder.ClickListener
) : SelectableAdapter<PreparationAdapter.ViewHolder?>() {
    // Create new views
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemLayoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_preparation, null
        )
        return ViewHolder(itemLayoutView, clickListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.tvStep.text = mArrayList[position].step
        if (isSelected(position)) {
            viewHolder.done.visibility = View.VISIBLE
            viewHolder.tvStep.setTextColor(mContext.resources.getColor(R.color.colorTextGrey))
        } else {
            viewHolder.tvStep.setTextColor(mContext.resources.getColor(R.color.colorTextDark))
            viewHolder.tvNumber.text = mArrayList[position].number
            viewHolder.done.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    class ViewHolder(itemLayoutView: View, private val listener: ClickListener?) :
        RecyclerView.ViewHolder(itemLayoutView), View.OnClickListener, OnLongClickListener {
        var tvStep: TextView = itemLayoutView.findViewById<View>(R.id.tv_step) as TextView
        var tvNumber: TextView = itemLayoutView.findViewById<View>(R.id.tv_number) as TextView
        var done: ImageView = itemLayoutView.findViewById<View>(R.id.iv_done) as ImageView
        override fun onClick(v: View) {
            listener?.onItemClicked(adapterPosition)
        }

        override fun onLongClick(view: View): Boolean {
            return listener?.onItemLongClicked(adapterPosition) ?: false
        }

        interface ClickListener {
            fun onItemClicked(position: Int)
            fun onItemLongClicked(position: Int): Boolean
        }

        //private final View selectedOverlay;
        init {
            itemLayoutView.setOnClickListener(this)
            itemLayoutView.setOnLongClickListener(this)
        }
    }
}