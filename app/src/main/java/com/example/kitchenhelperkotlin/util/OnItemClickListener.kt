package com.example.kitchenhelperkotlin.util

import com.example.kitchenhelperkotlin.tobuy.ToBuy

interface OnItemClickListener {
    fun onItemClick(toBuy: ToBuy)
    fun onCheckBoxClick(toBuy: ToBuy, isChecked: Boolean)
}