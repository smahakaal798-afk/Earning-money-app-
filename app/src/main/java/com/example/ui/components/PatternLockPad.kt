package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BorderViolet
import com.example.ui.theme.BrightEmerald
import com.example.ui.theme.CardSlateElevated
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RoseAlert
import kotlin.math.hypot

@Composable
fun PatternLockPad(
    isError: Boolean = false,
    isSuccess: Boolean = false,
    onPatternComplete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDots = remember { mutableStateListOf<Int>() }
    var activePointer by remember { mutableStateOf<Offset?>(null) }

    val activeColor = when {
        isError -> RoseAlert
        isSuccess -> BrightEmerald
        else -> NeonGold
    }

    fun dotCenter(index: Int, width: Float, height: Float): Offset {
        val row = index / 3
        val col = index % 3
        val stepX = width / 3f
        val stepY = height / 3f
        return Offset(
            x = stepX * col + stepX / 2f,
            y = stepY * row + stepY / 2f
        )
    }

    fun findHitDot(pos: Offset, width: Float, height: Float): Int? {
        val hitRadius = minOf(width, height) / 6.2f
        for (i in 0 until 9) {
            val center = dotCenter(i, width, height)
            if (hypot(pos.x - center.x, pos.y - center.y) <= hitRadius) {
                return i
            }
        }
        return null
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .sizeIn(maxWidth = 260.dp, maxHeight = 260.dp)
            .aspectRatio(1f)
            .testTag("pattern_lock_pad")
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val hit = findHitDot(offset, size.width.toFloat(), size.height.toFloat())
                    if (hit != null) {
                        selectedDots.clear()
                        selectedDots.add(hit)
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { startOffset ->
                        selectedDots.clear()
                        activePointer = startOffset
                        findHitDot(startOffset, size.width.toFloat(), size.height.toFloat())?.let {
                            selectedDots.add(it)
                        }
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        activePointer = change.position
                        val hit = findHitDot(change.position, size.width.toFloat(), size.height.toFloat())
                        if (hit != null && hit !in selectedDots) {
                            selectedDots.add(hit)
                        }
                    },
                    onDragEnd = {
                        activePointer = null
                        val pattern = selectedDots.joinToString("-")
                        if (selectedDots.isNotEmpty()) {
                            onPatternComplete(pattern)
                        }
                        selectedDots.clear()
                    },
                    onDragCancel = {
                        activePointer = null
                        selectedDots.clear()
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height

        // Draw connecting lines between selected dots
        if (selectedDots.isNotEmpty()) {
            for (i in 0 until selectedDots.size - 1) {
                val p1 = dotCenter(selectedDots[i], w, h)
                val p2 = dotCenter(selectedDots[i + 1], w, h)
                drawLine(
                    color = activeColor.copy(alpha = 0.85f),
                    start = p1,
                    end = p2,
                    strokeWidth = 9f,
                    cap = StrokeCap.Round
                )
            }
            activePointer?.let { pointer ->
                val lastCenter = dotCenter(selectedDots.last(), w, h)
                drawLine(
                    color = activeColor.copy(alpha = 0.45f),
                    start = lastCenter,
                    end = pointer,
                    strokeWidth = 7f,
                    cap = StrokeCap.Round
                )
            }
        }

        // Draw 3x3 pattern nodes
        val outerRadius = minOf(w, h) / 11.5f
        val innerRadius = outerRadius * 0.42f
        for (i in 0 until 9) {
            val center = dotCenter(i, w, h)
            val isSelected = i in selectedDots
            drawCircle(
                color = if (isSelected) activeColor.copy(alpha = 0.2f) else CardSlateElevated,
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = if (isSelected) activeColor else BorderViolet,
                radius = outerRadius,
                center = center,
                style = Stroke(width = if (isSelected) 4.5f else 2.5f, join = StrokeJoin.Round)
            )
            drawCircle(
                color = if (isSelected) activeColor else BorderViolet.copy(alpha = 0.7f),
                radius = innerRadius,
                center = center
            )
        }
    }
}
