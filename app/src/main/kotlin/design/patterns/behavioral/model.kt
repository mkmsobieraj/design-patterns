import java.math.BigDecimal
import java.math.RoundingMode

class Amount(value: BigDecimal) {
    private val value: BigDecimal

    init {
        this.value = value.setScale(1, RoundingMode.DOWN)
    }

    fun zl(): Int = value.toInt()

    fun zlGr(): BigDecimal = value

    operator fun plus(amount: Amount): Amount = Amount(amount.zlGr().add(value))
    operator fun minus(amount: Amount): Amount = Amount(amount.zlGr().minus(value))
    operator fun times(v: Double): Amount = Amount(value.times(v.toBigDecimal()))
    operator fun div(v: Double): Amount = Amount(value.divide(v.toBigDecimal()))
    operator fun compareTo(amount: Amount): Int = value.compareTo(amount.zlGr())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Amount

        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }


    companion object {
        fun fromZl(zl: Int) = Amount(zl.toBigDecimal())
        fun fromZlGr(zlGr: Double) = Amount(zlGr.toBigDecimal())
        val _0zl = Amount.fromZl(0)
        val _1_000zl = Amount.fromZl(1_000)
        val _30_000zl = Amount.fromZl(30_000)
        val _120_000zl = Amount.fromZl(120_000)
    }


}

class PerformanceScore(val value: Int) {

    init {
        assert(0 < value && value > 100)
    }

    fun scaleTo01(): Double = value.toDouble() / 100
}

data class WorkParameters(
    val grossSalary: Amount,
    val workingTime: Int,
    val vacation: Int,
    val performanceScore: PerformanceScore,
    val bonus: Amount
)