package com.example.recordplayer

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recordplayer.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private var _adapter: AudioItemAdapter? = null
    private var _media: MediaPlayer? = null
    private var _duration: Long? = null
    private var _list: MutableList<Audio>? = mutableListOf()
    private var _uri: Uri? = null
    private var _path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        _binding.lifecycleOwner = this
        initListeners()
        initMediaPlayer()
        initAdapter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null || resultCode == RESULT_OK) {
            _path = data?.data?.path
            _uri = data?.data
            _binding.imageSend.setImageResource(R.drawable.ic_send_blue)
            _binding.imageSend.isClickable = true
        }
    }

    private fun initListeners() {
        _binding.imageFile.setOnClickListener {
            getAudioFile()
        }
        _binding.imageSend.setOnClickListener {
            updateList(_path, _uri)
            CoroutineScope(Dispatchers.Main).launch {
                _list?.let { it1 -> _adapter?.submitList(it1) }
                _adapter?.notifyDataSetChanged()
            }
            _binding.imageSend.setImageResource(R.drawable.ic_send_grey)
        }
    }

    private fun updateList(path: String?, uri: Uri?) {
        _media = MediaPlayer.create(this, uri)
        _duration = _media?.duration?.toLong()
        _list?.add(Audio(path, uri, getCurrentTime(), convertToDurationFormat(_duration), true))
        _media?.release()
        _media = null
    }

    private fun initAdapter() {
        _adapter = getList()?.let {
            AudioItemAdapter(
                it
            ) { uri, user -> playMusic(uri, user) }
        }
        _binding.recycler.layoutManager = LinearLayoutManager(this@MainActivity)
        _binding.recycler.adapter = _adapter
    }

    private fun initMediaPlayer() {
        _media = MediaPlayer.create(this, R.raw.audio_2022)
        _duration = _media?.duration?.toLong()
    }

    private fun getList(): List<Audio>? {
        _list = mutableListOf(
            Audio(
                null,
                null,
                getCurrentTime(),
                convertToDurationFormat(_duration),
                false
            )
        )
        return _list
    }

    private fun convertToDurationFormat(time: Long?): String {
        if (time == null || time.toInt() == 0) {
            return "---"
        }
        val date = Date(time)
        val format = SimpleDateFormat("mm:ss")
        return format.format(date)
    }

    private fun getCurrentTime(): String {
        val format = SimpleDateFormat("HH:mm")
        return format.format(Calendar.getInstance().time)
    }

    private fun playMusic(uri: Uri?, isUser: Boolean) {
        _media?.release()
        _media = null
        _media = if (!isUser && _media != MediaPlayer.create(this, R.raw.audio_2022)) MediaPlayer.create(this, R.raw.audio_2022)
        else MediaPlayer.create(this, uri)
        when (Constant.IS_PLAY) {
            true -> {
                Constant.IS_PLAY = false
                _media?.pause()
            }
            false -> {
                Constant.IS_PLAY = true
                _media?.start()
            }
        }
    }

    private fun getAudioFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "audio/*"
        startActivityForResult(intent, 100)
    }

    override fun onDestroy() {
        _media = null
        _list = null
        _adapter = null
        _duration = null
        _path = null
        _uri = null
        super.onDestroy()
    }
}