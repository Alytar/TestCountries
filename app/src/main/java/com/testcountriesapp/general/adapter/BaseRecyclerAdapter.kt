package com.testcountriesapp.general.adapter

import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.testcountriesapp.R
import java.util.*

class BaseRecyclerAdapter<T>(
    context: Context?,
    items: List<T>?,
    private val factory: BaseViewHolder.Factory<T>,
    @LayoutRes private val layoutResId: Int
) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    private var filteredItems: MutableList<T>
    private var allItems: MutableList<T>
    private var selectedPosition = -1
    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var onItemClickListener: ((view: View, item: T, position: Int, previousSelectedPosition: Int) -> Unit)? =
        null
    private var onBindListener: ((holder: BaseViewHolder<T>, item: T, position: Int) -> Unit)? = null
    private var lastClickStamp: Long = 0L
    val selectedItem: T?
        get() = if (selectedPosition < 0 || filteredItems.size == 0) null else filteredItems[selectedPosition]
    val items: List<T>?
        get() = filteredItems

    init {
        filteredItems = ArrayList()
        allItems = ArrayList()
        items?.let {
            filteredItems.addAll(it)
            allItems.addAll(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val vh = factory.create(layoutInflater.inflate(layoutResId, parent, false))
        initClickListener(vh)
        return vh
    }

    @Suppress("unchecked_cast")
    private fun initClickListener(vh: BaseViewHolder<T>) {
        vh.itemView.setOnClickListener {
            val position = it.getTag(R.id.POSITION_KEY) as Int
            val previousSelection = selectedPosition
            selectedPosition = position
            val item: T = it.tag as T
            debounceClick(it, item, position, previousSelection)
        }
    }

    private fun debounceClick(view: View, item: T, position: Int, previousSelectedPosition: Int) {
        val currentTimestamp = SystemClock.uptimeMillis()
        val lastTimestamp = lastClickStamp
        lastClickStamp = currentTimestamp
        if (currentTimestamp - lastTimestamp > 500) {
            onItemClickListener?.invoke(view, item, position, previousSelectedPosition)
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        filteredItems[position]?.let {
            holder.bind(it, position)
            holder.itemView.tag = it
        }
        holder.itemView.setTag(R.id.POSITION_KEY, position)
        onBindListener?.invoke(holder, filteredItems[position], position)
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    fun resetSelection() {
        selectedPosition = -1
    }

    fun replace(list: List<T>?, animate: Boolean = false) {
        filteredItems = if (list != null) ArrayList(list) else ArrayList()
        allItems = if (list != null) ArrayList(list) else ArrayList()
        if (animate) {
            animateTo(filteredItems)
        } else {
            notifyDataSetChanged()
        }
    }

    fun addItems(list: List<T>?, animate: Boolean = false) {
        list?.let { filteredItems.addAll(it) }
        list?.let { allItems.addAll(it) }
        if (animate) {
            animateTo(filteredItems)
        } else {
            notifyDataSetChanged()
        }
    }

    fun addItem(position: Int, e: T) {
        filteredItems.add(position, e)
        notifyItemInserted(position)
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val e = filteredItems.removeAt(fromPosition)
        filteredItems.add(toPosition, e)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun removeItemByPosition(position: Int): T {
        val e = filteredItems.removeAt(position)
        notifyItemRemoved(position)
        return e
    }

    fun removeItemByObj(obj: T): Int {
        var position = -1
        for (i in filteredItems.indices) {
            if (filteredItems[i] == obj) position = i
        }
        filteredItems.removeAt(position)
        notifyItemRemoved(position)
        return position
    }

    fun setOnItemClickListener(onItemClickListener: ((view: View, item: T, position: Int, previousSelectedPosition: Int) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnBindListener(onBindListener: ((holder: BaseViewHolder<T>, item: T, position: Int) -> Unit)?) {
        this.onBindListener = onBindListener
    }

    fun getPosition(v: View): Int {
        val posTag = v.getTag(R.id.POSITION_KEY)
        return if (posTag != null) posTag as Int else -1
    }

    fun animateTo(inList: List<T>) {
        applyAndAnimateRemovals(inList)
        applyAndAnimateAdditions(inList)
        applyAndAnimateMovedItems(inList)
    }

    private fun applyAndAnimateRemovals(inList: List<T>) {
        for (i in filteredItems.indices.reversed()) {
            val e = filteredItems[i]
            if (!inList.contains(e)) {
                removeItemByPosition(i)
            }
        }
    }

    private fun applyAndAnimateAdditions(inList: List<T>) {
        var i = 0
        val count = inList.size
        while (i < count) {
            val e = inList[i]
            if (!filteredItems.contains(e)) {
                addItem(i, e)
            }
            i++
        }
    }

    private fun applyAndAnimateMovedItems(inList: List<T>) {
        for (toPosition in inList.indices.reversed()) {
            val e = inList[toPosition]
            val fromPosition = filteredItems.indexOf(e)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition)
            }
        }
    }

    fun filter(query: String?, prediction: (model: T) -> Boolean) {
        val newFilteredItems = ArrayList<T>()
        if (query.isNullOrEmpty()) {
            newFilteredItems.addAll(allItems)
        } else {
            for (model in allItems) {
                if (prediction.invoke(model)) {
                    newFilteredItems.add(model)
                }
            }
        }
        animateTo(newFilteredItems)
        filteredItems.clear()
        filteredItems = newFilteredItems
    }
}
