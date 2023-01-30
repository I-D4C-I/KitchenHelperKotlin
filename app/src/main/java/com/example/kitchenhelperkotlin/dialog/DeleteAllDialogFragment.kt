package com.example.kitchenhelperkotlin.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.kitchenhelperkotlin.R
import com.example.kitchenhelperkotlin.util.DELETE_ALL_COMPLETED
import com.example.kitchenhelperkotlin.util.DELETE_ALL_PRODUCTS
import com.example.kitchenhelperkotlin.util.DELETE_ALL_RECIPE
import com.example.kitchenhelperkotlin.util.DELETE_ALL_TOBUY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllDialogFragment : DialogFragment() {

    private val viewModel: DeleteAllViewModel by viewModels()

    private val args: DeleteAllDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setTitle(resources.getString(R.string.confirmDeletion))
            .setNegativeButton(resources.getString(R.string.cancel), null)
        when (args.dialogCode) {
            DELETE_ALL_COMPLETED -> {
                dialogBuilder.setMessage(resources.getString(R.string.deleteAllCompletedMessage))
                    .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                        viewModel.onConfirmDeleteCompletedClick()
                    }
            }
            DELETE_ALL_PRODUCTS -> {
                dialogBuilder.setMessage(resources.getString(R.string.deleteAllProductsMessage))
                    .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                        viewModel.onConfirmDeleteProductsClick()
                    }
            }
            DELETE_ALL_TOBUY -> {
                dialogBuilder.setMessage(resources.getString(R.string.deleteAllToBuyMessage))
                    .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                        viewModel.onConfirmDeleteToBuyClick()
                    }
            }
            DELETE_ALL_RECIPE -> {
                dialogBuilder.setMessage(resources.getString(R.string.deleteAllRecipeMessage))
                    .setPositiveButton(resources.getString(R.string.confirm)) { _, _ ->
                        viewModel.onConfirmDeleteRecipeClick()
                    }
            }
        }
        return dialogBuilder.create()
    }
}