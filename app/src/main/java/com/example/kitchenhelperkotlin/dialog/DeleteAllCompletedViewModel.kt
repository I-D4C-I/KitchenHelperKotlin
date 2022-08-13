package com.example.kitchenhelperkotlin.dialog


import androidx.lifecycle.ViewModel
import com.example.kitchenhelperkotlin.dependencyinjection.ApplicationScope
import com.example.kitchenhelperkotlin.tobuy.ToBuyDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val toBuyDao: ToBuyDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        toBuyDao.deleteCompletedToBuy()
    }
}