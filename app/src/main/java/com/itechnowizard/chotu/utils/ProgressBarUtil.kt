package com.itechnowizard.chotu.utils

import android.view.View
import com.itechnowizard.chotu.databinding.ProgressBarBinding

object ProgressBarUtil {
    fun showProgressBar(progressBarBinding: ProgressBarBinding) {
        progressBarBinding.progressBar.visibility = View.VISIBLE
    }

    fun hideProgressBar(progressBarBinding: ProgressBarBinding) {
        progressBarBinding.progressBar.visibility = View.INVISIBLE
    }
}