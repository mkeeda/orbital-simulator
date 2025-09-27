package dev.mkeeda.planetsimulator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.mkeeda.planetsimulator.data.PresetManager
import dev.mkeeda.planetsimulator.model.CelestialBody
import dev.mkeeda.planetsimulator.simulation.OrbitalSimulator
import dev.mkeeda.planetsimulator.util.CoordinateConverter
import kotlinx.coroutines.delay

@Composable
fun SimulatorCanvas() {
    val simulator = remember { OrbitalSimulator() }
    var isRunning by remember { mutableStateOf(true) }
    var expandedPreset by remember { mutableStateOf(false) }
    val presets = remember { PresetManager.getAllPresets() }

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

            Box {
                Button(
                    onClick = { expandedPreset = true }
                ) {
                    Text(simulator.currentPreset.name)
                }

                DropdownMenu(
                    expanded = expandedPreset,
                    onDismissRequest = { expandedPreset = false }
                ) {
                    presets.forEach { preset ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(preset.name)
                                    Text(
                                        preset.description,
                                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }
                            },
                            onClick = {
                                simulator.loadPreset(preset)
                                expandedPreset = false
                            }
                        )
                    }
                }
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            val converter = CoordinateConverter(size)
            drawBackground(converter)
            simulator.bodies.forEach { body ->
                drawCelestialBody(body, converter)
            }
        }
    }
}

private fun DrawScope.drawBackground(converter: CoordinateConverter) {
    val gridSize = 50.0
    val gridColor = Color.White.copy(alpha = 0.6f)

    for (x in 0..1000 step gridSize.toInt()) {
        val start = converter.simToScreen(x.toDouble(), 0.0)
        val end = converter.simToScreen(x.toDouble(), 1000.0)
        drawLine(
            color = gridColor,
            start = start,
            end = end,
            strokeWidth = 1f
        )
    }

    for (y in 0..1000 step gridSize.toInt()) {
        val start = converter.simToScreen(0.0, y.toDouble())
        val end = converter.simToScreen(1000.0, y.toDouble())
        drawLine(
            color = gridColor,
            start = start,
            end = end,
            strokeWidth = 1f
        )
    }
}

private fun DrawScope.drawCelestialBody(body: CelestialBody, converter: CoordinateConverter) {
    val screenPos = converter.simToScreen(body.x, body.y)
    val screenRadius = converter.scaleToScreen(body.radius.toDouble())

    drawCircle(
        color = body.color,
        radius = screenRadius,
        center = screenPos
    )

    drawCircle(
        color = body.color.copy(alpha = 0.3f),
        radius = screenRadius * 1.5f,
        center = screenPos
    )
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun SimulatorPreview() {
    SimulatorCanvas()
}