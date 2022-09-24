package design.patterns.behavioral

import Amount
import WorkParameters
import java.util.*

interface BonusCalculator {
    fun calculate(wp: WorkParameters): Amount
}

class EmploymentContractBonusCalculator : BonusCalculator {
    override fun calculate(wp: WorkParameters): Amount =
        if (wp.workingTime > 40 && wp.vacation == 0) Amount._1_000zl * (wp.workingTime - 40.0) * 0.7 * wp.performanceScore.scaleTo01()
        else Amount._0zl

}

class B2BBonusCalculator : BonusCalculator {
    override fun calculate(wp: WorkParameters): Amount = Amount._0zl

}

interface SalaryCalculator {
    fun calculate(amount: Amount): Amount
}

class PensionInsurance : SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount - (amount * 0.1952)

}

class DisabilityInsurance : SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount - (amount * 0.015)
}

class MedicalInsurance : SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount - Amount.fromZlGr(559.89)
}

class IncomeTax : SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount - calculateTax(amount)

    private fun calculateTax(amount: Amount): Amount =
        calculateZeroStage(amount) + calculateFirstStage(amount) + calculateSecondStage(amount)

    private fun calculateZeroStage(amount: Amount): Amount = Amount._0zl
    private fun calculateFirstStage(amount: Amount): Amount = amount
        .let { if (amount < Amount._30_000zl) Amount._0zl else amount }
        .let { if (amount > Amount._120_000zl) Amount._120_000zl else amount }
        .let { (amount - Amount._30_000zl) * 0.12 }

    private fun calculateSecondStage(amount: Amount): Amount = amount
        .let { if (amount < Amount._120_000zl) Amount._0zl else amount }
        .let { (amount - Amount._120_000zl) * 0.32 }
}

class VATTax : SalaryCalculator {
    override fun calculate(amount: Amount): Amount = amount * 0.23
}


sealed class FormOfEmployment(
    private val salaryCalculators: Set<SalaryCalculator>,
    private val bonusCalculator: BonusCalculator,
    val start: Locale?,
    val end: Locale?,
    val documents: Set<String>
) {

    fun netSalary(wp: WorkParameters): Amount = wp.grossSalary -
            salaryCalculators.map { it.calculate(wp.grossSalary) }.reduce { acc, tax -> acc + tax }

    fun calcBonus(wp: WorkParameters) = bonusCalculator.calculate(wp)
}

private val employerContractSalaryCalculators: Set<SalaryCalculator> = setOf(IncomeTax(), PensionInsurance(), DisabilityInsurance(), MedicalInsurance())
private val b2bContractSalaryCalculators: Set<SalaryCalculator> = setOf(IncomeTax(), VATTax())

class EmploymentContract(start: Locale?, end: Locale? = null, documents: Set<String>, val employer: String, val superior: String) :
    FormOfEmployment(employerContractSalaryCalculators, EmploymentContractBonusCalculator(), start, end, documents)

class B2B(start: Locale?, end: Locale? = null, documents: Set<String>, val company: String) :
    FormOfEmployment(b2bContractSalaryCalculators, B2BBonusCalculator(), start, end, documents)