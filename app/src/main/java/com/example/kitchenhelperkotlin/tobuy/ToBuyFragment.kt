package com.example.kitchenhelperkotlin.tobuy

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.kitchenhelperkotlin.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ToBuyFragment : Fragment(R.layout.fragment_tobuy) {

    private val  viewModel : ToBuyViewModel by viewModels()
}