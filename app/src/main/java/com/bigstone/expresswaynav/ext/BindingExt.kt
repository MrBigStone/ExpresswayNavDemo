package com.bigstone.expresswaynav.ext

import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding

fun <VB : ViewBinding> ViewGroup.binding(
    inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    attachToParent: Boolean = true
) = lazy {
    inflate(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)
}

fun <VB : ViewBinding> ViewGroup.mergeBinding(
    inflate: (LayoutInflater, ViewGroup) -> VB
) = lazy {
    inflate(LayoutInflater.from(context), this)
}

fun <VB : ViewBinding> Dialog.binding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also { setContentView(it.root) }
}

fun <VB : ViewBinding> FragmentActivity.binding(inflate: (LayoutInflater) -> VB) = lazy {
    inflate(layoutInflater).also { setContentView(it.root) }
}
