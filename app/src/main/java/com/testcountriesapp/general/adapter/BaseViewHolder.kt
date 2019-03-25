package com.testcountriesapp.general.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected val context: Context
        get() = itemView.context

    interface Factory<T> {
        fun create(itemView: View): BaseViewHolder<T>
    }

    abstract fun bind(item: T, position: Int)
}
