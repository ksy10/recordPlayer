package com.example.recordplayer

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.recordplayer.databinding.ItemAudioBinding
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class AudioItemAdapter(
    private var list: List<Audio>,
    private val changeState: (Uri?, Boolean) -> Unit
) : RecyclerView.Adapter<AudioItemAdapter.AudioItemViewHolder>() {

    class AudioItemViewHolder(val binding: ItemAudioBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Audio, changeState: (Uri?, Boolean) -> Unit) {
            binding.imagePlay.setOnClickListener {
                changeState(model.uri, model.user)
                changeIcon(model.user)
                changeProgress()
            }
            if (!model.user) {
                binding.imagePlay.setImageResource(R.drawable.ic_play_blue)
                binding.constraintLayout.setBackgroundResource(R.drawable.message)
                binding.wave.setSampleFrom(R.raw.audio_2022)
            } else {
                binding.imagePlay.setImageResource(R.drawable.ic_play_green)
                binding.constraintLayout.setBackgroundResource(R.drawable.message_user)
                model.path?.let { binding.wave.setSampleFrom(it) }
            }
            binding.wave.sample = getRandomWaves() //todo logic real sample

            binding.wave.onProgressChanged = object : SeekBarOnProgressChanged {
                override fun onProgressChanged(
                    waveformSeekBar: WaveformSeekBar,
                    progress: Float,
                    fromUser: Boolean
                ) {
                    //todo logic
                }
            }
        }

        private fun changeIcon(isUser: Boolean) {
            val image: Int
            when (Constant.IS_PLAY) {
                false -> {
                    image = if (isUser) R.drawable.ic_play_green else R.drawable.ic_play_blue
                    binding.imagePlay.setImageResource(image)
                }
                true -> {
                    image = if (isUser) R.drawable.ic_pause_green else R.drawable.ic_pause_blue
                    binding.imagePlay.setImageResource(image)
                }
            }
        }

        private fun getRandomWaves(): IntArray {
            val waves = IntArray(50)
            for (i in waves.indices)
                waves[i] = Random().nextInt(waves.size)
            return waves
        }

        private fun changeProgress() {
            var count = 0f
            when (Constant.IS_PLAY) {
                true -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        if (count < 100) {
                            binding.wave.progress = count
                        }
                    }
                }
                false -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.wave.progress = 0f
                    }
                }
            }
        }

        private fun getCount(count: Float): Float {
            return count + 1f
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

    fun submitList(newList: List<Audio>) {
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
        var oldModel: List<Audio>,
        var newModel: List<Audio>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldModel.size
        }

        override fun getNewListSize(): Int {
            return newModel.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return (oldModel[oldItemPosition] == newModel[newItemPosition])
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldModel[oldItemPosition] == newModel[newItemPosition]
        }
    }
}