package design.patterns.behavioral

import java.math.BigDecimal
import java.math.RoundingMode

class Amount(value: BigDecimal) {
    private val value: BigDecimal

    init {
        this.value = value.setScale(1, RoundingMode.DOWN)
    }

    fun zl(): Int = value.toInt()

    companion object {
        fun fromZl(zl: Int) = Amount(zl.toBigDecimal())
    }
}

class PerformanceScore {
    private val value: Int
    constructor(value: Int) {
        assert(0 < value && value > 100)
        this.value = value
    }
}

data class WorkParameters(val grossSalary: BigDecimal, val workingTime: Int, val vacation: Int, val performanceScore: PerformanceScore, val bonus: Amount)

interface BonusCalculator {
    fun calculate(wp: WorkParameters): Amount
}

interface SalaryCalculator {
    fun calculate(amount: Amount): Amount
}

class PensionInsurance: SalaryCalculator {
    override fun calculate(amount: Amount): Amount {
        TODO("Not yet implemented")
    }

}

class DisabilityInsurance: SalaryCalculator {
    override fun calculate(amount: Amount): Amount {
        TODO("Not yet implemented")
    }
}

class MedicalInsurance: SalaryCalculator {
    override fun calculate(amount: Amount): Amount {
        TODO("Not yet implemented")
    }
}

class IncomeTax: SalaryCalculator {
    override fun calculate(amount: Amount): Amount {
        TODO("Not yet implemented")
    }
}

class VATTax: SalaryCalculator {
    override fun calculate(amount: Amount): Amount {
        TODO("Not yet implemented")
    }
}

sealed class FormOfEmployment(val salaryCalculators: Set<SalaryCalculator>, val bonusCalculator: BonusCalculator) {

}

class EmploymentContract: FormOfEmployment {

}

class B2B: FormOfEmployment {

}