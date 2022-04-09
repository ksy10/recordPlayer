package com.example.recordplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private var _list = MutableLiveData<List<Audio>>()
    val list: LiveData<List<Audio>>
        get() = _list

    init {
        createList()
    }

    private fun createList() {
        _list.value = mutableListOf(Audio(null, getCurrentTime(), "", false))
    }

    private fun getCurrentTime(): String {
        val format = SimpleDateFormat("HH:mm")
        return format.format(Calendar.getInstance().time)
    }
}