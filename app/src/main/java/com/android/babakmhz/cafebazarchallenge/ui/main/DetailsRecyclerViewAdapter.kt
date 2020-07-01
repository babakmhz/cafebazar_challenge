package com.android.babakmhz.cafebazarchallenge.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.babakmhz.cafebazarchallenge.R
import com.android.babakmhz.cafebazarchallenge.data.db.LocationModel
import com.android.babakmhz.cafebazarchallenge.databinding.ItemsTemplateBinding
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class DetailsRecyclerViewAdapter(
    private val context: Context,
    private var locations: ArrayList<LocationModel>,
    private val callback: callBack
) : RecyclerView.Adapter<DetailsRecyclerViewAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val templateBinding =
            ItemsTemplateBinding.inflate(LayoutInflater.from(context), parent, false)
        return viewHolder(
            context,
            templateBinding,
            templateBinding.root,
            callback
        )
    }

    override fun getItemCount(): Int {
        return this.locations.size
    }

    fun addItems(items: List<LocationModel>) {
        locations.addAll(items)
        locations.sortedBy { it.location.distance }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(locations[position])
    }

    class viewHolder(
        val context: Context,
        private val itemsTemplateBinding: ItemsTemplateBinding,
        itemView: View,
        private val callback: callBack
    ) : RecyclerView.ViewHolder(itemView) {

        object dBindingAdapter {
            @BindingAdapter("imageUrl")
            @JvmStatic
            fun loadImage(view: CircleImageView, url: String?) {
//                Glide.with(view).load(url)
//                    .placeholder(R.drawable.location_preview).into(view)
            }
        }

        fun bind(location: LocationModel) {
            itemsTemplateBinding.repo = location
            itemsTemplateBinding.executePendingBindings()
            itemsTemplateBinding.itemContainer.setOnClickListener {
                callback.onItemClicked(location)
            }

        }

    }

}

public interface callBack {
    fun onItemClicked(location: LocationModel)
}
