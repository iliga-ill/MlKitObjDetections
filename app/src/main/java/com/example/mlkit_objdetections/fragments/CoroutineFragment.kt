package com.example.mlkit_detection.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mlkit_detection.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

interface CoroutineFragmentSettings {
    var bundle: Bundle?
}

interface CoroutineFragment<settings> {
    var appSettings: AppSettings
    var bundle: Bundle?
    var isAsync: Boolean
    var settings: settings

    var fragmentView: View

    fun build(inflater: LayoutInflater, container: ViewGroup?){
        if (isAsync) CoroutineScope(Dispatchers.Main).launch { buildFragment(inflater, container) }
        else buildFragment(inflater, container)
    }

    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    fun buildFragment(inflater: LayoutInflater, container: ViewGroup?)

    fun createView(inflater: LayoutInflater, container: ViewGroup?): View {
        isAsync = false
        return onCreateView(inflater, container, null)
    }

    fun createViewAsync(inflater: LayoutInflater, container: ViewGroup?): View {
        isAsync = true
        return onCreateView(inflater, container, null)
    }
}