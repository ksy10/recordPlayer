package com.example.recordplayer

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recordplayer.databinding.ItemAudioBinding
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar

class AudioItemAdapter(
    private var list: List<Audio>,
    private val changeState: (Uri?, Boolean) -> Unit
) : RecyclerView.Adapter<AudioItemAdapter.AudioItemViewHolder>() {

    class AudioItemViewHolder(val binding: ItemAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Audio, changeState: (Uri?, Boolean) -> Unit) {
            binding.imagePlayBlue.setOnClickListener {
                changeState(model.audio, model.user)
                changeIcon()
            }
            if (!model.user) binding.wave.setSampleFrom(R.raw.audio_2022)
            else model.audio?.let { binding.wave.setSampleFrom(it.toFile()) }

            binding.wave.onProgressChanged = object: SeekBarOnProgressChanged {
                override fun onProgressChanged(
                    waveformSeekBar: WaveformSeekBar,
                    progress: Float,
                    fromUser: Boolean
                ) {
                }
            }
        }

        private fun changeIcon() {
            when (Constant.IS_PLAY) {
                true -> binding.imagePlayBlue.setImageResource(R.drawable.ic_play_blue)
                false -> binding.imagePlayBlue.setImageResource(R.drawable.ic_pause_blue)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AudioItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_audio,
                parent,
                false
            )
    )

    override fun onBindViewHolder(holder: AudioItemViewHolder, position: Int) {
        if (list.size > position) {
            holder.binding.model = list[position]
            holder.bind(list[position], changeState)
        }
    }

    override fun getItemCount() = list.size

    fun submitList(newList: List<Audio>?) {
        if (newList == null) {
            list = listOf()
            notifyDataSetChanged()
            return
        }
        if (newList.size != list.size) {
            list = newList
            notifyDataSetChanged()
            return
        }
        val oldList = list
        val diffResult: DiffUtil.DiffResult = DiffUtil.calculateDiff(
            AudioDiffCallback(
                oldList,
                newList
            )
        )
        list = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class AudioDiffCallback(
        var oldModel: List<Audio>?,
        var newModel: List<Audio>?
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldModel!!.size
        }

        override fun getNewListSize(): Int {
            return newModel!!.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldModel!![oldItemPosition] == newModel!![newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldModel!![oldItemPosition] == newModel!![newItemPosition]
        }
    }
}