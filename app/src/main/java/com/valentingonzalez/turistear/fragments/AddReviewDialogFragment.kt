package com.valentingonzalez.turistear.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.valentingonzalez.turistear.R
import java.lang.ClassCastException
import java.lang.IllegalStateException

class AddReviewDialogFragment : DialogFragment(){

    internal lateinit var mListener: AddReviewListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as AddReviewListener
        }catch (e: ClassCastException){
            throw ClassCastException("$activity debe implementar el callback AddReviewListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.rating_dialog_layout, null))
                    .setPositiveButton("Agregar", DialogInterface.OnClickListener{dialog, id ->
                        val titleTv = (dialog as AlertDialog).findViewById<TextInputEditText>(R.id.review_title_tiet)
                        val commentTv = (dialog).findViewById<TextInputEditText>(R.id.review_comment_tiet)
                        val ratingBar = (dialog).findViewById<RatingBar>(R.id.ratingBar)
                        mListener.onDialogPositiveClick(titleTv.text.toString(), commentTv.text.toString(), ratingBar.rating)
                    })
                    .setNegativeButton("Cancelar") { dialog, id ->
                        getDialog()?.cancel()
                    }
            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }

    interface AddReviewListener{
        fun onDialogPositiveClick(title: String, comment: String, stars: Float)
    }
}