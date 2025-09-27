package dev.mkeeda.planetsimulator.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

class CoordinateConverter(
    canvasSize: Size,
    simulationWidth: Double = 1000.0,
    simulationHeight: Double = 1000.0
) {
    private val scaleX = canvasSize.width / simulationWidth
    private val scaleY = canvasSize.height / simulationHeight
    private val scale = minOf(scaleX, scaleY)

    private val offsetX = (canvasSize.width - simulationWidth * scale) / 2
    private val offsetY = (canvasSize.height - simulationHeight * scale) / 2

    fun simToScreen(x: Double, y: Double): Offset {
        return Offset(
            (x * scale + offsetX).toFloat(),
            (y * scale + offsetY).toFloat()
        )
    }

    fun screenToSim(offset: Offset): Pair<Double, Double> {
        return Pair(
            ((offset.x - offsetX) / scale),
            ((offset.y - offsetY) / scale)
        )
    }

    fun scaleToScreen(value: Double): Float {
        return (value * scale).toFloat()
    }
}