package com.mec.mec

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<VIEW_HOLDER_TYPE, ViewHolder : RecyclerView.ViewHolder>(
    private val recyclerView: RecyclerView,
    private val orientation: Int = RecyclerView.VERTICAL
) : RecyclerView.Adapter<ViewHolder>() {
    private val listOfItems = ArrayList<VIEW_HOLDER_TYPE>()
    private var layoutManager: LinearLayoutManager? = null

    init {
        if (recyclerView.layoutManager == null) {
            layoutManager = LinearLayoutManager(
                recyclerView.context,
                orientation,
                false
            )

            recyclerView.layoutManager = layoutManager
        }
        if (recyclerView.adapter == null)
            recyclerView.adapter = this
    }

    abstract fun createViewHolder(
        viewGroup: ViewGroup,
        viewType: Int,
        layoutInflater: LayoutInflater
    ): ViewHolder

    abstract fun bindViewHolder(viewHolder: ViewHolder, position: Int, item: VIEW_HOLDER_TYPE)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return createViewHolder(viewGroup, viewType, layoutInflater)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        bindViewHolder(viewHolder, position, listOfItems[position])
    }

    override fun getItemCount(): Int {
        return listOfItems.size
    }

    open fun getItems(): ArrayList<VIEW_HOLDER_TYPE> {
        return listOfItems
    }

    open fun add(item: VIEW_HOLDER_TYPE) = apply {
        listOfItems.add(item)
    }

    open fun addAll(items: List<VIEW_HOLDER_TYPE>) = apply {
        listOfItems.addAll(items)
    }

    open fun replaceAll(items: List<VIEW_HOLDER_TYPE>) = apply {
        listOfItems.clear()
        listOfItems.addAll(items)

    }

    open fun clear() = apply {
        listOfItems.clear()
    }

    open fun refresh() = apply {
        recyclerView.post(Runnable(this::notifyDataSetChanged))
    }

    open fun refresh(index: Int) = apply {
        recyclerView.post(Runnable { notifyItemChanged(index) })
    }

    open fun refresh(index: Int, item: VIEW_HOLDER_TYPE) = apply {
        recyclerView.post(Runnable { notifyItemChanged(index, item) })
    }

    open fun replaceItem(item: VIEW_HOLDER_TYPE) {
        val position = listOfItems.indexOfFirst { item == item }
        listOfItems[position] = item
    }

    open fun addItemDecoration(decoration: RecyclerView.ItemDecoration) = apply {
        recyclerView.addItemDecoration(decoration);
    }

    open fun setLayOutManager(lm: RecyclerView.LayoutManager) {
        layoutManager = null
        recyclerView.layoutManager = lm
    }

    private var loading = true
    private var pastVisiblesItems = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    open fun onScrollListener(onScroll: OnScroll) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                layoutManager?.let {
                    if (dy > 0) {
                        visibleItemCount = it.childCount
                        totalItemCount = it.itemCount
                        pastVisiblesItems = it.findFirstVisibleItemPosition()
                        if (loading && visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            onScroll.loadMore()
                            loading = true
                        }
                    }
                }
            }
        })
    }

    interface OnScroll {
        fun loadMore()
    }
}