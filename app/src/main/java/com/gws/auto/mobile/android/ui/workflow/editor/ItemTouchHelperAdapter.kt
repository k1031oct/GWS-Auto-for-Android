package com.gws.auto.mobile.android.ui.workflow.editor

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
    fun onItemClear()
}
