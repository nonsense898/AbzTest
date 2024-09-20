package com.non.abztest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.non.abztest.databinding.ItemPositionBinding
import com.non.abztest.model.Position
import com.non.abztest.viewmodel.MainViewModel

class PositionAdapter(
    private val mainViewModel: MainViewModel, private val positionClickListener: OnPositionClickListener // Add listener

) : RecyclerView.Adapter<PositionAdapter.PositionViewHolder>() {

    private var positions: List<Position> = mutableListOf()

    private var selectedPosition = -1

    interface OnPositionClickListener {
        fun onPositionClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PositionViewHolder {
        val binding = ItemPositionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PositionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PositionViewHolder, position: Int) {
        val currentPosition = positions[position]
        holder.bind(currentPosition, position)
    }

    override fun getItemCount(): Int {
        return positions.size
    }

    fun submitList(positionList: List<Position>) {
        positions = positionList
        notifyDataSetChanged()
    }

    inner class PositionViewHolder(private val binding: ItemPositionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Position, adapterPosition: Int) {
            binding.position = position

            binding.positionRadioButton.isChecked = (selectedPosition == adapterPosition)

            binding.positionRadioButton.setOnClickListener {
                selectedPosition = adapterPosition
                notifyDataSetChanged()

                positionClickListener.onPositionClicked(adapterPosition)
            }

            binding.executePendingBindings()
        }
    }
}