package com.martinez.prueba.Viewholder

import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aakira.expandablelayout.ExpandableLinearLayout
import com.martinez.prueba.Interface.ItemClickListener
import com.martinez.prueba.R
import kotlinx.android.synthetic.main.layout_with_child.view.*

class ItemViewHolder (itemView: View, isExpandable:Boolean):RecyclerView.ViewHolder(itemView){
    lateinit var  txt_item_text:TextView
    lateinit var  txt_child_item:TextView
    lateinit var  button:RelativeLayout
    lateinit var  expandable_layout:ExpandableLinearLayout

    lateinit var itemClickListener:ItemClickListener

    fun setiItemClickListener(itemClickListener: ItemClickListener)
    {
        this.itemClickListener=itemClickListener
    }

    init{
        if(isExpandable)
        {
            txt_item_text= itemView.findViewById(R.id.txt_item_view ) as TextView
            txt_child_item= itemView.findViewById(R.id.txt_child_item_view ) as TextView
            button= itemView.findViewById(R.id.button ) as RelativeLayout
            expandable_layout = itemView.findViewById(R.id.expandable_layout) as ExpandableLinearLayout
        }
        else
        {
            txt_item_text= itemView.findViewById(R.id.txt_item_view ) as TextView
        }

        itemView.setOnClickListener{ view -> itemClickListener.onClick(view,adapterPosition)  }
    }
}