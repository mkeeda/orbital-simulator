package dev.mkeeda.orbitalSimulator.data

import androidx.compose.ui.graphics.Color
import dev.mkeeda.orbitalSimulator.model.CelestialBody
import dev.mkeeda.orbitalSimulator.model.SimulationPreset
import kotlin.math.sqrt

object Preset {

    fun realSunAndEarth(): SimulationPreset {
        val sunMass = 333000.0f
        val earthMass = 1.0f
        val auDistance = 200.0f
        val g = 40000.0f
        val earthOrbitalVelocity = sqrt(g * sunMass / auDistance)

        return SimulationPreset(
            name = "太陽と地球",
            description = "実際の太陽系の質量比を再現",
            gravityConstant = g,
            bodies = listOf(
                CelestialBody(
                    name = "Sun",
                    x = 500.0f,
                    y = 500.0f,
                    velocityX = 0.0f,
                    velocityY = 0.0f,
                    mass = sunMass,
                    radius = 35f,
                    color = Color(0xFFFDB813),
                    density = 1.4f // 太陽の密度
                ),
                CelestialBody(
                    name = "Earth",
                    x = 500.0f + auDistance,
                    y = 500.0f,
                    velocityX = 0.0f,
                    velocityY = earthOrbitalVelocity,
                    mass = earthMass,
                    radius = 10f,
                    color = Color(0xFF4169E1),
                    density = 5.5f // 地球の密度
                )
            )
        )
    }

    fun binaryStar(): SimulationPreset {
        val mass = 100.0f
        val distance = 150.0f
        val g = 50000.0f
        val velocity = sqrt(g * mass / (2 * distance))

        return SimulationPreset(
            name = "連星系",
            description = "同じ質量の2つの恒星が共通重心を周回",
            gravityConstant = g,
            bodies = listOf(
                CelestialBody(
                    name = "Star A",
                    x = 500.0f - distance / 2,
                    y = 500.0f,
                    velocityX = 0.0f,
                    velocityY = velocity,
                    mass = mass,
                    radius = 25f,
                    color = Color(0xFFFFD700),
                    density = 1.0f
                ),
                CelestialBody(
                    name = "Star B",
                    x = 500.0f + distance / 2,
                    y = 500.0f,
                    velocityX = 0.0f,
                    velocityY = -velocity,
                    mass = mass,
                    radius = 25f,
                    color = Color(0xFFFFA500),
                    density = 1.0f
                )
            )
        )
    }

    fun threeBody(): SimulationPreset {
        // Sitnikov問題をベースにした安定な3体問題
        // 2つの同質量天体が楕円軌道で動き、3つ目が振動運動
        val mass1 = 50.0f
        val mass2 = 50.0f
        val mass3 = 50.0f
        val g = 20000.0f

        // 比較的大きな距離で配置し、低い速度から開始
        val separation = 200.0f

        // 運動量保存を考慮した対称的な初期条件
        // 3つの天体が重心の周りを回るような設定
        return SimulationPreset(
            name = "3体問題",
            description = "3つの同質量星による安定した複雑軌道",
            gravityConstant = g,
            bodies = listOf(
                CelestialBody(
                    name = "Star 1",
                    x = 500.0f - separation / 3,
                    y = 500.0f - separation / 4,
                    velocityX = 8.0f,
                    velocityY = 12.0f,
                    mass = mass1,
                    radius = 20f,
                    color = Color(0xFFFFD700),
                    density = 0.8f
                ),
                CelestialBody(
                    name = "Star 2",
                    x = 500.0f + separation / 3,
                    y = 500.0f - separation / 4,
                    velocityX = 8.0f,
                    velocityY = -12.0f,
                    mass = mass2,
                    radius = 20f,
                    color = Color(0xFF00CED1),
                    density = 0.8f
                ),
                CelestialBody(
                    name = "Star 3",
                    x = 500.0f,
                    y = 500.0f + separation / 2,
                    velocityX = -16.0f,
                    velocityY = 0.0f,
                    mass = mass3,
                    radius = 20f,
                    color = Color(0xFFFF69B4),
                    density = 0.8f
                )
            )
        )
    }

    fun ellipticalOrbit(): SimulationPreset {
        val sunMass = 300.0f
        val planetMass = 1.0f
        val g = 40000.0f
        val perihelion = 120.0f  // 近日点距離

        // 楕円軌道のための初期速度（近日点での速度を円軌道より少し高めに設定）
        // 1.15倍にすることで適度な楕円軌道になる
        val velocity = sqrt(g * sunMass / perihelion) * 1.15f

        return SimulationPreset(
            name = "楕円軌道",
            description = "顕著な楕円軌道を描く惑星",
            gravityConstant = g,
            bodies = listOf(
                CelestialBody(
                    name = "Sun",
                    x = 500.0f,
                    y = 500.0f,
                    velocityX = 0.0f,
                    velocityY = 0.0f,
                    mass = sunMass,
                    radius = 30f,
                    color = Color(0xFFFFA500),
                    density = 1.4f
                ),
                CelestialBody(
                    name = "Planet",
                    x = 500.0f + perihelion,
                    y = 500.0f,
                    velocityX = 0.0f,
                    velocityY = velocity,
                    mass = planetMass,
                    radius = 10f,
                    color = Color(0xFF8A2BE2),
                    density = 3.0f
                )
            )
        )
    }

    fun tidalDisruption(): SimulationPreset {
        val blackHoleMass = 500.0f
        val starMass = 20.0f
        val cometMass = 0.5f
        val g = 35000.0f

        return SimulationPreset(
            name = "潮汐破壊",
            description = "ブラックホール級の天体による複数天体の破壊",
            gravityConstant = g,
            bodies = listOf(
                CelestialBody(
                    name = "Black Hole",
                    x = 500.0f,
                    y = 500.0f,
                    velocityX = 0.0f,
                    velocityY = 0.0f,
                    mass = blackHoleMass,
                    radius = 25f,
                    color = Color(0xFF1C1C1C),
                    density = 10.0f // 超高密度
                ),
                CelestialBody(
                    name = "Red Star",
                    x = 650.0f,
                    y = 500.0f,
                    velocityX = -5.0f,
                    velocityY = sqrt(g * blackHoleMass / 150.0f) * 0.9f,
                    mass = starMass,
                    radius = 20f,
                    color = Color(0xFFDC143C),
                    density = 0.8f
                ),
                CelestialBody(
                    name = "Blue Star",
                    x = 350.0f,
                    y = 500.0f,
                    velocityX = 5.0f,
                    velocityY = -sqrt(g * blackHoleMass / 150.0f) * 0.9f,
                    mass = starMass,
                    radius = 20f,
                    color = Color(0xFF4682B4),
                    density = 0.8f
                ),
                CelestialBody(
                    name = "Comet",
                    x = 500.0f,
                    y = 380.0f,
                    velocityX = sqrt(g * blackHoleMass / 120.0f) * 0.85f,
                    velocityY = 0.0f,
                    mass = cometMass,
                    radius = 8f,
                    color = Color(0xFFF0E68C),
                    density = 0.3f // 非常に低密度
                )
            )
        )
    }

    fun getAllPresets(): List<SimulationPreset> {
        return listOf(
            realSunAndEarth(),
            binaryStar(),
            threeBody(),
            ellipticalOrbit(),
            tidalDisruption(),
        )
    }
}