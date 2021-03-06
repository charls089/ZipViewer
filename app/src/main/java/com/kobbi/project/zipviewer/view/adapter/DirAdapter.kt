package com.kobbi.project.zipviewer.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.project.zipviewer.BR
import com.kobbi.project.zipviewer.R
import com.kobbi.project.zipviewer.databinding.ItemFileBinding
import com.kobbi.project.zipviewer.utils.Utils
import com.kobbi.project.zipviewer.presenter.listener.ClickListener
import com.kobbi.project.zipviewer.presenter.listener.LongClickListener
import java.io.File
import java.util.concurrent.Executors

class DirAdapter(items: List<File>) : RecyclerView.Adapter<DirAdapter.ViewHolder>() {
    private val mItems = mutableListOf<File>()
    private var mClickListener: ClickListener? = null
    private var mLongClickListener: LongClickListener? = null

    init {
        setItems(items)
    }

    fun setItems(items: List<File>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFileBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_file, parent, false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount() = mItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setOnClickListener(clickListener: ClickListener) {
        mClickListener = clickListener
    }

    fun setOnLongClickListener(longClickListener: LongClickListener) {
        mLongClickListener = longClickListener
    }

    inner class ViewHolder(private val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener,
        View.OnLongClickListener {
        init {
            val view = binding.root
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            v?.let { mClickListener?.onItemClick(layoutPosition, v) }
        }

        override fun onLongClick(v: View?): Boolean {
            v?.let { mLongClickListener?.onItemLongClick(layoutPosition, v) }
            return true
        }

        fun bind(position:Int) {
            val file = mItems[position]
            binding.setVariable(BR.file, file)
            binding.setVariable(BR.file_position, position)
            getImage(file)
        }

        private fun getImage(file: File) {
            Executors.newFixedThreadPool(15).execute {
                val bitmap = Utils.getBitmap(file, 480, 480)
                binding.setVariable(BR.img, bitmap)
            }
        }
    }
}