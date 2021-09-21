package com.aryanakbarpour.dietplanner

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeGesture(context : Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    val deleteColor = ContextCompat.getColor(context, R.color.deleteColor)
    val checkColor = ContextCompat.getColor(context, R.color.checkColor)
    val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete_24)
    val checkIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_check_circle_24)

    private val intrinsicWidth = deleteIcon?.intrinsicWidth
    private val intrinsicHeight = deleteIcon?.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#f44336")
    private val backgroundColor2 = Color.parseColor("green")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    private val intrinsicWidth2 = checkIcon?.intrinsicWidth
    private val intrinsicHeight2 = checkIcon?.intrinsicHeight

    var l: Int = 0

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        l = 0
    }



    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (l == 0){
            println("${dX.toFloat()} , $isCurrentlyActive")
            if (dX.toFloat() > 1.0f){
                l = 1
                println("1")
            }

            else if (dX.toFloat() < -1.0f){
                println("2")
                l = 2
            }
        }


        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            println("canceled")
            l = 0
            clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        if (l == 1) {

            background.color = backgroundColor2
            background.setBounds(
                itemView.left,
                itemView.top,
                itemView.left + dX.toInt(),
                itemView.bottom
            )
            background.draw(c)

            // Calculate position of delete icon
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight2!!) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight2!!) / 2
            val deleteIconLeft = itemView.left + deleteIconMargin
            val deleteIconRight = itemView.left + deleteIconMargin + intrinsicWidth2!!
            val deleteIconBottom = deleteIconTop + intrinsicHeight2!!

            // Draw the delete icon
            checkIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            checkIcon?.draw(c)
        }
        else if (l == 2) {
            background.color = backgroundColor
            background.setBounds(
                itemView.right + dX.toInt(),
                itemView.top,
                itemView.right,
                itemView.bottom
            )
            background.draw(c)

            // Calculate position of delete icon
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight!!) / 2
            val deleteIconMargin = (itemHeight - intrinsicHeight!!) / 2
            val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth!!
            val deleteIconRight = itemView.right - deleteIconMargin
            val deleteIconBottom = deleteIconTop + intrinsicHeight!!

            // Draw the delete icon
            deleteIcon?.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteIcon?.draw(c)
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)


    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

}