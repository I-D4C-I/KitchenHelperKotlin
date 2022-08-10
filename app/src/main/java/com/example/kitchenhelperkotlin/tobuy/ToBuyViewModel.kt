package com.example.kitchenhelperkotlin.tobuy

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ToBuyViewModel @Inject constructor(
    private val toBuyDao: ToBuyDao
) : ViewModel() {
}