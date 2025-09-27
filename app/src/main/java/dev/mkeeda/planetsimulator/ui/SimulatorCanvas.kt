package dev.mkeeda.planetsimulator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import dev.mkeeda.planetsimulator.model.CelestialBody
import dev.mkeeda.planetsimulator.simulation.OrbitalSimulator
import kotlinx.coroutines.delay

@Composable
fun SimulatorCanvas() {
    val simulator = remember { OrbitalSimulator() }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            simulator.update(0.016)
            delay(16L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { isRunning = !isRunning }
            ) {
                Text(if (isRunning) "Pause" else "Play")
            }

            Button(
                onClick = {
                    simulator.reset()
                }
            ) {
                Text("Reset")
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            drawBackground()
            simulator.bodies.forEach { body ->
                drawCelestialBody(body)
            }
        }
    }
}

private fun DrawScope.drawBackground() {
    val gridSize = 50f
    val gridColor = Color.White.copy(alpha = 0.6f)

    for (x in 0..size.width.toInt() step gridSize.toInt()) {
        drawLine(
            color = gridColor,
            start = Offset(x.toFloat(), 0f),
            end = Offset(x.toFloat(), size.height),
            strokeWidth = 1f
        )
    }

    for (y in 0..size.height.toInt() step gridSize.toInt()) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y.toFloat()),
            end = Offset(size.width, y.toFloat()),
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawCelestialBody(body: CelestialBody) {
    drawCircle(
        color = body.color,
        radius = body.radius,
        center = Offset(body.x.toFloat(), body.y.toFloat())
    )

    drawCircle(
        color = body.color.copy(alpha = 0.3f),
        radius = body.radius * 1.5f,
        center = Offset(body.x.toFloat(), body.y.toFloat())
    )
}