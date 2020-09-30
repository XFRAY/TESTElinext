package com.example.testelinext.view.main.custom

import android.graphics.PointF
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import androidx.recyclerview.widget.SnapHelper
import kotlin.math.abs

class GridPagerSnapHelper(
    private val spanCount: Int = 1,
    private val columnCount: Int = 1
) : SnapHelper() {

    companion object {
        private const val MAX_SCROLL_ON_FLING_DURATION = 100
        private const val MILLISECONDS_PER_INCH = 45f
    }

    private lateinit var recyclerView: RecyclerView

    private var mVerticalHelper: OrientationHelper? = null
    private var mHorizontalHelper: OrientationHelper? = null

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        recyclerView?.let {
            this.recyclerView = recyclerView
        }
    }

    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToCenter(layoutManager, targetView, getHorizontalHelper(layoutManager))
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToCenter(layoutManager, targetView, getVerticalHelper(layoutManager))
        } else {
            out[1] = 0
        }
        return out
    }

    private fun distanceToCenter(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View, helper: OrientationHelper
    ): Int {
        return if (layoutManager.canScrollHorizontally()) {
            val totalWidth = recyclerView.width
            val columnWidth = totalWidth / columnCount
            val position = layoutManager.getPosition(targetView)
            val pageIndex = pageIndex(position)
            val currentPageStart = pageIndex * maxCountItemsPerPage()
            val distance = (position - currentPageStart) / spanCount * columnWidth
            val childStart = helper.getDecoratedStart(targetView)
            childStart - distance
        } else {
            val totalHeight = recyclerView.height
            val rowHeight = totalHeight / spanCount
            val position = layoutManager.getPosition(targetView)
            val pageIndex = pageIndex(position)
            val currentPageStart = pageIndex * maxCountItemsPerPage()
            val distance = (position - currentPageStart) / columnCount * rowHeight
            val childStart = helper.getDecoratedStart(targetView)
            childStart - distance
        }
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }

    private fun pageIndex(position: Int): Int {
        return position / maxCountItemsPerPage()
    }

    private fun maxCountItemsPerPage(): Int {
        return spanCount * columnCount
    }

    private fun findCenterView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }

        var closestChild: View? = null
        val center = if (layoutManager.clipToPadding) {
            helper.startAfterPadding + helper.totalSpace / 2
        } else {
            helper.end / 2
        }

        var absClosest = Int.MAX_VALUE
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childCenter = (helper.getDecoratedStart(child)
                    + helper.getDecoratedMeasurement(child) / 2)
            val absDistance = abs(childCenter - center)
            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChild = child
            }
        }
        return closestChild
    }

    override fun findTargetSnapPosition(
        layoutManager: RecyclerView.LayoutManager, velocityX: Int,
        velocityY: Int
    ): Int {
        val itemCount = layoutManager.itemCount
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION
        }
        var mStartMostChildView: View? = null
        if (layoutManager.canScrollVertically()) {
            mStartMostChildView = findStartView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            mStartMostChildView = findStartView(layoutManager, getHorizontalHelper(layoutManager))
        }
        if (mStartMostChildView == null) {
            return RecyclerView.NO_POSITION
        }
        val centerPosition = layoutManager.getPosition(mStartMostChildView)
        if (centerPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION
        }
        val forwardDirection = if (layoutManager.canScrollHorizontally()) {
            velocityX > 0
        } else {
            velocityY > 0
        }
        var reverseLayout = false
        if (layoutManager is ScrollVectorProvider) {
            val vectorProvider = layoutManager as ScrollVectorProvider
            val vectorForEnd = vectorProvider.computeScrollVectorForPosition(itemCount - 1)
            if (vectorForEnd != null) {
                reverseLayout = vectorForEnd.x < 0 || vectorForEnd.y < 0
            }
        }
        val pageIndex = pageIndex(centerPosition)
        val currentPageStart = pageIndex * maxCountItemsPerPage()
        return if (reverseLayout) if (forwardDirection) currentPageStart - maxCountItemsPerPage() else currentPageStart else if (forwardDirection) currentPageStart + maxCountItemsPerPage() else currentPageStart + maxCountItemsPerPage() - 1
    }

    private fun findStartView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }
        var closestChild: View? = null
        var startest = Int.MAX_VALUE
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val childStart = helper.getDecoratedStart(child)
            if (childStart < startest) {
                startest = childStart
                closestChild = child
            }
        }
        return closestChild
    }

    override fun createSnapScroller(layoutManager: RecyclerView.LayoutManager): LinearSmoothScroller? {
        return if (layoutManager !is ScrollVectorProvider) {
            null
        } else object : LinearSmoothScroller(recyclerView.context) {
            override fun onTargetFound(
                targetView: View,
                state: RecyclerView.State,
                action: Action
            ) {
                val snapDistances = calculateDistanceToFinalSnap(
                    recyclerView.layoutManager!!,
                    targetView
                )
                val dx = snapDistances!![0]
                val dy = snapDistances[1]
                val time = calculateTimeForDeceleration(abs(dx).coerceAtLeast(abs(dy)))
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator)
                }
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }

            override fun calculateTimeForScrolling(dx: Int): Int {
                return MAX_SCROLL_ON_FLING_DURATION.coerceAtMost(super.calculateTimeForScrolling(dx))
            }

            override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
                return null
            }
        }
    }

    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (mVerticalHelper == null || mVerticalHelper!!.layoutManager != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper!!
    }

    private fun getHorizontalHelper(
        layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (mHorizontalHelper == null || mHorizontalHelper!!.layoutManager != layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper!!
    }
}