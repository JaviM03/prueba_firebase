package com.martinez.prueba

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter
import com.github.aakira.expandablelayout.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.martinez.prueba.Interface.ItemClickListener
import com.martinez.prueba.Model.Item
import com.martinez.prueba.Viewholder.ItemViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    internal var items: MutableList<Item> = ArrayList()
    internal lateinit var adapter: FirebaseRecyclerAdapter<Item, ItemViewHolder>

    internal var expandState = SparseBooleanArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init view

        recycler_data.setHasFixedSize(true)
        recycler_data.layoutManager = LinearLayoutManager(this)

        //retrieve data

        retrieveData()

        //set data
        setData()


    }

    private fun setData() {
        val query = FirebaseDatabase.getInstance().reference.child("Items")
        val options = FirebaseRecyclerOptions.Builder<Item>()
                .setQuery(query, Item::class.java)
                .build()

                // adapter = object : FirebaseRecyclerAdapter<Item, ItemViewHolder>(options) {
adapter= object:FirebaseRecyclerAdapter<Item,ItemViewHolder>(options){
            override fun getItemViewType(position: Int): Int {
                return if (items[position].isExpandable) {
                    1
                } else {
                    0
                }

            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
               if(viewType==0)//sin items
               {
                   val itemView=LayoutInflater
                           .from(parent.context)
                           .inflate(R.layout.layout_without_child,parent,false)
                   return ItemViewHolder(itemView,viewType==1)
               }
                else
               {
                   val itemView=LayoutInflater
                           .from(parent.context)
                           .inflate(R.layout.layout_with_child,parent,false)
                   return ItemViewHolder(itemView,viewType==1)
               }
            }


            override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Item) {
                when (holder.itemViewType) {
                    0 -> {
                        holder.setIsRecyclable(false)
                        holder.txt_item_text.text = model.text

                        holder.setiItemClickListener(object : ItemClickListener {
                            override fun onClick(view: View, position: Int) {
                                Toast.makeText(this@MainActivity, "" + model.text, Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    1 -> {
                        holder.setIsRecyclable(false)
                        holder.txt_item_text.text = model.text

                        holder.setiItemClickListener(object : ItemClickListener {
                            override fun onClick(view: View, position: Int) {
                                Toast.makeText(this@MainActivity, "" + model.text, Toast.LENGTH_SHORT).show()
                            }
                        })
                        holder.expandable_layout.setInRecyclerView(true)
                        holder.expandable_layout.isExpanded = expandState.get(position)
                        holder.expandable_layout.setListener(object : ExpandableLayoutListenerAdapter() {
                            override fun onPreOpen() {
                                changeRotate(holder.button, 0f, 180f).start()
                                expandState.put(position, true)
                            }

                            override fun onPreClose() {
                                changeRotate(holder.button, 180f, 0f).start()
                                expandState.put(position, false)
                            }
                        })

                        holder.button.rotation=if(expandState.get(position)) 180f else 0f
                        holder.button.setOnClickListener{
                            holder.expandable_layout.toggle()
                        }

                        holder.txt_child_item.text=model.subText
                    }
                }
            }
        }

        //initSParse Array

        expandState.clear()
        for(i in items.indices)
            expandState.append(i,false)
        recycler_data.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        recycler_data.adapter=adapter

    }

    private fun changeRotate(button: RelativeLayout, from: Float, to: Float): ObjectAnimator {
        val animator = ObjectAnimator.ofFloat(button, "rotation", from, to)
        animator.duration=300
        animator.interpolator = Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR)
        return animator

    }



private fun retrieveData() {
    items.clear()

    val db = FirebaseDatabase.getInstance()
            .reference
            .child("Items")//"Items" es el nombre de la primera rama

    db.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError) {
            Log.d("ERROR", "" + p0.message)
        }

        override fun onDataChange(p0: DataSnapshot) {
            for (itemSnapshot in p0.children) {
                val item = itemSnapshot.getValue(Item::class.java)

                items.add(item!!)
            }
        }

    })
}

    override fun onStart() {
        if(adapter != null)
             adapter.startListening()
        super.onStart()
    }

    override fun onStop() {
        if(adapter != null)
            adapter.stopListening()
        super.onStop()
    }
}
