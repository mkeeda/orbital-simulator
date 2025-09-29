package dev.mkeeda.planetsimulator.physics

import dev.mkeeda.planetsimulator.model.CelestialBody
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * ロシュ限界の計算を行うユーティリティクラス
 * ロシュ限界とは、天体が潮汐力により破壊される最小距離
 */
object RocheLimit {

    /**
     * 2つの天体間のロシュ限界距離を計算
     *
     * 剛体ロシュ限界の式を使用:
     * d = 2.44 * R_primary * (ρ_primary / ρ_satellite)^(1/3)
     *
     * @param primary 主天体（より大きな質量の天体）
     * @param satellite 衛星天体（破壊される可能性のある天体）
     * @return ロシュ限界距離
     */
    fun calculateRocheDistance(primary: CelestialBody, satellite: CelestialBody): Double {
        if (primary.mass < satellite.mass) {
            // 質量が逆転している場合は、より大きな天体を基準にする
            return calculateRocheDistance(satellite, primary)
        }

        val densityRatio = primary.density / satellite.density
        val rocheCoefficient = 3.0 // 調整済みロシュ限界係数（崩壊しやすく）

        return rocheCoefficient * primary.radius * densityRatio.pow(1.0/3.0)
    }

    /**
     * 2つの天体がロシュ限界を超えて接近しているかチェック
     *
     * @param body1 天体1
     * @param body2 天体2
     * @return ロシュ限界を超えている場合はtrue
     */
    fun isWithinRocheLimit(body1: CelestialBody, body2: CelestialBody): Boolean {

        // デブリは崩壊しない（連鎖崩壊を防ぐ）
        if (body1.isDebris || body2.isDebris) {
            return false
        }

        val distance = calculateDistance(body1, body2)
        val rocheDistance = calculateRocheDistance(body1, body2)

        return distance < rocheDistance
    }

    /**
     * ロシュ限界を超えた場合、どの天体が破壊されるかを判定
     * 質量の小さい天体が破壊される
     *
     * @param body1 天体1
     * @param body2 天体2
     * @return 破壊される天体（質量が小さい方）
     */
    fun getVictimBody(body1: CelestialBody, body2: CelestialBody): CelestialBody {
        return if (body1.mass <= body2.mass) body1 else body2
    }

    /**
     * ロシュ限界を超えた場合、破壊を引き起こす主天体を取得
     * 質量の大きい天体が主天体となる
     *
     * @param body1 天体1
     * @param body2 天体2
     * @return 主天体（質量が大きい方）
     */
    fun getPrimaryBody(body1: CelestialBody, body2: CelestialBody): CelestialBody {
        return if (body1.mass > body2.mass) body1 else body2
    }

    /**
     * 2つの天体間の距離を計算
     */
    private fun calculateDistance(body1: CelestialBody, body2: CelestialBody): Double {
        val dx = body1.x - body2.x
        val dy = body1.y - body2.y
        return sqrt(dx * dx + dy * dy)
    }

    /**
     * 指定した天体のロシュ限界円を描画するための情報を取得
     *
     * @param body 天体
     * @param referenceDensity 参照密度（通常は1.0）
     * @return ロシュ限界半径
     */
    fun getRocheRadius(body: CelestialBody, referenceDensity: Double = 1.0): Double {
        val densityRatio = body.density / referenceDensity
        return 2.44 * body.radius * densityRatio.pow(1.0/3.0)
    }
}