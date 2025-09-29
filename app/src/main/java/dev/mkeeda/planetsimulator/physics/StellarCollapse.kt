package dev.mkeeda.planetsimulator.physics

import androidx.compose.ui.graphics.Color
import dev.mkeeda.planetsimulator.model.CelestialBody
import kotlin.math.*
import kotlin.random.Random

/**
 * 星の崩壊とデブリ生成を管理するクラス
 */
object StellarCollapse {

    /**
     * 天体の崩壊時に生成するデブリの数
     */
    private const val DEBRIS_COUNT = 10


    /**
     * デブリの初期速度範囲
     */
    private const val DEBRIS_SPEED_MIN = 20.0
    private const val DEBRIS_SPEED_MAX = 50.0

    /**
     * 天体を崩壊させてデブリを生成
     *
     * @param victim 破壊される天体
     * @param primary 破壊を引き起こした主天体
     * @return 生成されたデブリのリスト
     */
    fun createDebris(
        victim: CelestialBody,
        primary: CelestialBody
    ): List<CelestialBody> {
        val debris = mutableListOf<CelestialBody>()

        // 主天体からの方向ベクトルを計算
        val dx = victim.x - primary.x
        val dy = victim.y - primary.y
        val distance = sqrt(dx * dx + dy * dy)

        // 正規化された逃避方向
        val escapeX = if (distance > 0) dx / distance else 1.0
        val escapeY = if (distance > 0) dy / distance else 0.0

        // 各デブリの質量と大きさ
        val debrisMass = victim.mass / DEBRIS_COUNT
        val debrisRadius = victim.radius * 0.4f

        repeat(DEBRIS_COUNT) { i ->
            // 円周上にデブリを配置するための角度
            val angle = (2 * PI * i) / DEBRIS_COUNT
            val spreadRadius = victim.radius * 0.8

            // デブリの初期位置（元の天体の周囲に散らばる）
            val debrisX = victim.x + cos(angle) * spreadRadius
            val debrisY = victim.y + sin(angle) * spreadRadius

            // デブリの速度を計算
            val baseSpeed = Random.nextDouble(DEBRIS_SPEED_MIN, DEBRIS_SPEED_MAX)

            // 主天体から遠ざかる方向の成分
            val escapeComponent = Random.nextDouble(0.6, 1.0)

            // ランダムな方向の成分
            val randomAngle = Random.nextDouble(0.0, 2 * PI)
            val randomComponent = 1.0 - escapeComponent

            val velocityX = victim.velocityX + baseSpeed * (
                escapeComponent * escapeX +
                randomComponent * cos(randomAngle)
            )
            val velocityY = victim.velocityY + baseSpeed * (
                escapeComponent * escapeY +
                randomComponent * sin(randomAngle)
            )

            // デブリの色（元の色を少し変化させる）
            val debrisColor = createDebrisColor(victim.color, i)

            val debrisBody = CelestialBody(
                x = debrisX,
                y = debrisY,
                velocityX = velocityX,
                velocityY = velocityY,
                mass = debrisMass,
                radius = debrisRadius,
                color = debrisColor,
                name = "${victim.name}_Debris_${i + 1}",
                density = victim.density,
                isDebris = true
            )

            debris.add(debrisBody)
        }

        return debris
    }

    /**
     * デブリの色を生成（元の色をベースに変化させる）
     */
    private fun createDebrisColor(originalColor: Color, index: Int): Color {
        // 元の色の成分を取得
        val red = originalColor.red
        val green = originalColor.green
        val blue = originalColor.blue

        // 色に少しバリエーションを加える
        val variation = (index * 0.1f) % 0.3f
        val dimming = 0.7f // 少し暗くする

        return Color(
            red = (red * dimming + variation).coerceIn(0f, 1f),
            green = (green * dimming + variation * 0.5f).coerceIn(0f, 1f),
            blue = (blue * dimming + variation * 0.3f).coerceIn(0f, 1f),
            alpha = 0.8f // 少し透明にする
        )
    }


    /**
     * 崩壊が発生するかの追加チェック
     * 相対速度が低い場合のみ崩壊が発生する（高速通過の場合は崩壊しない）
     */
    fun shouldCollapse(victim: CelestialBody, primary: CelestialBody): Boolean {
        // 相対速度を計算
        val relativeVx = victim.velocityX - primary.velocityX
        val relativeVy = victim.velocityY - primary.velocityY
        val relativeSpeed = sqrt(relativeVx * relativeVx + relativeVy * relativeVy)

        // 相対速度が十分低い場合のみ崩壊
        val maxCollapseSpeed = 100.0 // この値以下の相対速度でのみ崩壊（緩い条件）
        return relativeSpeed < maxCollapseSpeed
    }
}