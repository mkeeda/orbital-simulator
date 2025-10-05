package dev.mkeeda.orbitalSimulator.model

import androidx.compose.ui.graphics.Color

/**
 * 天体の軌跡を管理するクラス
 */
data class Trail(
    val bodyName: String,
    val positions: List<TrailPoint>,
    val color: Color,
    val maxLength: Int = 200  // 軌跡の最大長
) {
    fun addPosition(x: Float, y: Float): Trail {
        val newPoint = TrailPoint(x, y)
        val updatedPositions = (positions + newPoint).takeLast(maxLength)
        return copy(positions = updatedPositions)
    }

    fun clear(): Trail {
        return copy(positions = emptyList())
    }
}

/**
 * 軌跡の各点
 */
data class TrailPoint(
    val x: Float,
    val y: Float
)