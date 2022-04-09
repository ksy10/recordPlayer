package com.example.recordplayer

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recordplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding
    private var _adapter: AudioItemAdapter? = null
    private val _viewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private var _media: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        _binding.lifecycleOwner = this
        initListeners()
        initAdapter()
    }

    private fun initListeners() {
        _binding.imageFile.setOnClickListener {
            //todo
        }
    }

    private fun initAdapter() {
        _viewModel.list.observe(this) {
            lifecycleScope.launchWhenCreated {
                if (_binding.recycler.adapter == null) {
                    _adapter = AudioItemAdapter(it
                    )  { uri, user -> playMusic(uri, user) }
                    _binding.recycler.layoutManager = LinearLayoutManager(this@MainActivity)
                    _binding.recycler.adapter = _adapter
                } else {
                    _adapter?.submitList(it)
                }
            }
        }
    }

    private fun playMusic(uri: Uri?, isUser: Boolean) {
        if (_media == null) {
            _media = if (!isUser)
                MediaPlayer.create(this, R.raw.audio_2022)
            else MediaPlayer.create(this, uri)
        }
        when (Constant.IS_PLAY) {
            true -> {
                Constant.IS_PLAY = false
                _media?.start()
            }
            false -> {
                Constant.IS_PLAY = true
                _media?.stop()
            }
        }
    }
}