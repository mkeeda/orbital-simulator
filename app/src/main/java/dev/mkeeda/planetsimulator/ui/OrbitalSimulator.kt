package dev.mkeeda.planetsimulator.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.mkeeda.planetsimulator.data.Preset
import dev.mkeeda.planetsimulator.model.CelestialBody
import dev.mkeeda.planetsimulator.model.SimulationPreset
import dev.mkeeda.planetsimulator.model.Trail
import dev.mkeeda.planetsimulator.util.CoordinateConverter
import kotlinx.coroutines.delay

@Composable
fun OrbitalSimulator() {
    val simulator = remember { OrbitalSimulatorState() }
    var isRunning by remember { mutableStateOf(true) }

    LaunchedEffect(isRunning) {
        while (isRunning) {
            simulator.update(deltaTime = 0.016)
            delay(16L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SimulationControls(
            isRunning = isRunning,
            onPlayPauseClick = { isRunning = !isRunning },
            onResetClick = { simulator.reset() },
            currentPreset = simulator.currentPreset,
            onPresetSelected = { preset -> simulator.loadPreset(preset) }
        )

        SimulationSettingsControls(
            isRocheLimitEnabled = simulator.isRocheLimitEnabled,
            onRocheLimitToggle = { simulator.toggleRocheLimit() },
            isTrailEnabled = simulator.isTrailEnabled,
            onTrailToggle = { simulator.toggleTrail() }
        )

        SimulationCanvas(
            bodies = simulator.bodies,
            trails = simulator.trails.values.toList(),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        )
    }
}

@Composable
private fun SimulationControls(
    isRunning: Boolean,
    onPlayPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    currentPreset: SimulationPreset,
    onPresetSelected: (SimulationPreset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayPauseButton(
            isRunning = isRunning,
            onClick = onPlayPauseClick
        )

        ResetButton(
            onClick = onResetClick
        )

        PresetSelector(
            currentPreset = currentPreset,
            onPresetSelected = onPresetSelected
        )
    }
}

@Composable
private fun PlayPauseButton(
    isRunning: Boolean,
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text(if (isRunning) "Pause" else "Play")
    }
}

@Composable
private fun ResetButton(
    onClick: () -> Unit
) {
    Button(onClick = onClick) {
        Text("Reset")
    }
}

@Composable
private fun PresetSelector(
    currentPreset: SimulationPreset,
    onPresetSelected: (SimulationPreset) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val presets = remember { Preset.getAllPresets() }

    Box {
        Button(onClick = { expanded = true }) {
            Text(currentPreset.name)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            presets.forEach { preset ->
                DropdownMenuItem(
                    text = {
                        PresetMenuItem(preset = preset)
                    },
                    onClick = {
                        onPresetSelected(preset)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PresetMenuItem(preset: SimulationPreset) {
    Column {
        Text(preset.name)
        Text(
            preset.description,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

@Composable
private fun SimulationSettingsControls(
    isRocheLimitEnabled: Boolean,
    onRocheLimitToggle: () -> Unit,
    isTrailEnabled: Boolean,
    onTrailToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("ロシュ限界:")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isRocheLimitEnabled,
                onCheckedChange = { onRocheLimitToggle() }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("軌跡:")
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isTrailEnabled,
                onCheckedChange = { onTrailToggle() }
            )
        }
    }
}

@Composable
private fun SimulationCanvas(
    bodies: List<CelestialBody>,
    trails: List<Trail>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val converter = CoordinateConverter(canvasSize = size)
        drawBackground(converter)

        trails.forEach { trail ->
            drawTrail(trail, converter)
        }

        bodies.forEach { body ->
            drawCelestialBody(body, converter)
        }
    }
}

private fun DrawScope.drawBackground(converter: CoordinateConverter) {
    val gridSize = 50
    val gridColor = Color.Black.copy(alpha = 0.6f)

    for (x in 0..CoordinateConverter.SIMULATION_WIDTH step gridSize) {
        val start = converter.simToScreen(x = x.toDouble(), y = 0.0)
        val end = converter.simToScreen(x = x.toDouble(), y = 1000.0)
        drawLine(
            color = gridColor,
            start = start,
            end = end,
            strokeWidth = 1f
        )
    }

    for (y in 0..CoordinateConverter.SIMULATION_HEIGHT step gridSize) {
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
    val screenPos = converter.simToScreen(x = body.x, y = body.y)
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

private fun DrawScope.drawTrail(trail: Trail, converter: CoordinateConverter) {
    if (trail.positions.size < 2) return

    val points = trail.positions.map { point ->
        converter.simToScreen(x = point.x, y = point.y)
    }

    for (i in 1 until points.size) {
        val alpha = (i.toFloat() / points.size) * 0.5f  // 古い位置ほど薄く
        drawLine(
            color = trail.color.copy(alpha = alpha),
            start = points[i - 1],
            end = points[i],
            strokeWidth = 5f
        )
    }
}

@Preview(device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun SimulatorPreview() {
    OrbitalSimulator()
}