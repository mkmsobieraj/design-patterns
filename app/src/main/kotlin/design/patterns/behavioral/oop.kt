package design.patterns.behavioral

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

class PerformanceScore {
    val value: Int
    constructor(value: Int) {
        assert(0 < value && value > 100)
        this.value = value
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

interface BonusCalculator {
    fun calculate(wp: WorkParameters): Amount
}

class EmploymentContractBonusCalculator: BonusCalculator {
    override fun calculate(wp: WorkParameters): Amount =
        if (wp.workingTime > 40 && wp.vacation == 0) Amount._1_000zl * (wp.workingTime - 40.0) * 0.7 * wp.performanceScore.scaleTo01()
        else Amount._0zl

}

class B2BBonusCalculator: BonusCalculator {
    override fun calculate(wp: WorkParameters): Amount = Amount._0zl

}

interface SalaryCalculator {
    fun calculate(amount: Amount): Amount
}

class PensionInsurance: SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount

}

class DisabilityInsurance: SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount - (amount * 0.1952)
}

class MedicalInsurance: SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount - Amount.fromZlGr(559.89)
}

class IncomeTax: SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount - calculateTax(amount)

    private fun calculateTax(amount: Amount): Amount =
        calculateZeroStage(amount) + calculateFirstStage(amount) + calculateSecondStage(amount)

    private fun calculateZeroStage(amount: Amount): Amount = Amount._0zl
    private fun calculateFirstStage(amount: Amount): Amount = amount
        .let { if ( amount < Amount._30_000zl ) Amount._0zl else amount }
        .let { if ( amount > Amount._120_000zl ) Amount._120_000zl  else amount }
        .let { (amount  - Amount._30_000zl) * 0.12 }
    private fun calculateSecondStage(amount: Amount): Amount = amount
        .let { if ( amount < Amount._120_000zl ) Amount._0zl  else amount }
        .let { (amount - Amount._120_000zl) * 0.32 }
}

class VATTax: SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount * 0.23
}

sealed class FormOfEmployment(val salaryCalculators: Set<SalaryCalculator>, val bonusCalculator: BonusCalculator) {
    fun netSalary(wp: WorkParameters): Amount = wp.grossSalary -
            salaryCalculators.map { it.calculate(wp.grossSalary) }.reduce { acc, tax -> acc + tax}
}

class EmploymentContract: FormOfEmployment(setOf(), EmploymentContractBonusCalculator()) {

}

class B2B: FormOfEmployment(setOf(), EmploymentContractBonusCalculator()) {

}