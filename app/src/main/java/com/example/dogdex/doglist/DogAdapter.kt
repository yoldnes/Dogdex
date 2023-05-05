package com.example.dogdex.doglist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dogdex.R
import com.example.dogdex.model.Dog
import com.example.dogdex.databinding.DogListItemBinding

class DogAdapter : ListAdapter<Dog, DogAdapter.DogViewHolder>(DiffCalback) {

    companion object DiffCalback : DiffUtil.ItemCallback<Dog>() {
        override fun areContentsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: Dog, newItem: Dog): Boolean {
            return oldItem === newItem
        }
    }

    private var onItemClickListener: ((Dog) -> Unit)? = null
    private var onLongItemClickListener: ((Dog) -> Unit)? = null
    fun setOnClickListener(onItemClickListener: (Dog) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnLongClickListener(onLongItemClickListener: (Dog) -> Unit) {
        this.onLongItemClickListener = onLongItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = DogListItemBinding.inflate(LayoutInflater.from(parent.context))
        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(dogViewHolder: DogViewHolder, position: Int) {
        val dog = getItem(position)
        dogViewHolder.bind(dog)
    }

    inner class DogViewHolder(private val binding: DogListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dog: Dog) {
            with(binding) {
                if (dog.incollection == true) {
                    dogListItemLayout.background = ContextCompat.getDrawable(
                        dogImage.context,
                        R.drawable.dog_list_item_background
                    )
                    textViewName.visibility = View.GONE
                    dogImage.visibility = View.VISIBLE

                    dogListItemLayout.setOnClickListener {
                        onItemClickListener?.invoke(dog)
                    }
                    dogImage.load(dog.imageUrl)
                    dogListItemLayout.setOnLongClickListener {
                        onLongItemClickListener?.invoke(dog)
                        true
                    }
                } else {
                    dogImage.visibility = View.GONE
                    dogListItemLayout.background = ContextCompat.getDrawable(
                        dogImage.context,
                        R.drawable.dog_list_item_null_background
                    )
                    textViewName.visibility = View.VISIBLE
                    textViewName.text = dog.index.toString()
                }
            }
        }
    }
}