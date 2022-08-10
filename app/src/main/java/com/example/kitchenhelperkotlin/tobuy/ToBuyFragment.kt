package com.example.kitchenhelperkotlin.tobuy

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.databinding.FragmentTobuyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ToBuyFragment : Fragment(R.layout.fragment_tobuy) {

    private val  viewModel : ToBuyViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentTobuyBinding.bind(view)

        val adapter = ToBuyAdapter()
        binding.apply {
            recycleViewToBuy.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.toBuys.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}