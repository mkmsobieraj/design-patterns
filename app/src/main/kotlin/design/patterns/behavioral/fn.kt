package design.patterns.behavioral

import Amount
import WorkParameters
import then
import java.util.*

typealias BonusCalculatorFn = (wp: WorkParameters?) -> Amount

val employmentContractBonusCalculator: BonusCalculatorFn = {
    if (it == null) Amount._0zl
    else if (it.workingTime > 40 && it.vacation == 0) Amount._1_000zl * (it.workingTime - 40.0) * 0.7 * it.performanceScore.scaleTo01()
    else Amount._0zl
}
val b2BBonusCalculator: BonusCalculatorFn = { Amount._0zl }

typealias SalaryCalculatorFn = (amount: Amount) -> Amount

val pensionInsurance: SalaryCalculatorFn = { it - (it * 0.1952) }
val disabilityInsurance: SalaryCalculatorFn = { it - (it * 0.015) }
val medicalInsurance: SalaryCalculatorFn = { it - Amount.fromZlGr(559.89) }
val vatTax: SalaryCalculatorFn = { it * 0.23 }
val incomeTax: SalaryCalculatorFn = {

    fun calculateZeroStage(amount: Amount): Amount = Amount._0zl // we can still manage complexity
    fun calculateFirstStage(amount: Amount): Amount = amount
        .let { if (amount < Amount._30_000zl) Amount._0zl else amount }
        .let { if (amount > Amount._120_000zl) Amount._120_000zl else amount }
        .let { (amount - Amount._30_000zl) * 0.12 }

    fun calculateSecondStage(amount: Amount): Amount = amount
        .let { if (amount < Amount._120_000zl) Amount._0zl else amount }
        .let { (amount - Amount._120_000zl) * 0.32 }

    fun calculateTax(amount: Amount): Amount =
        calculateZeroStage(amount) + calculateFirstStage(amount) + calculateSecondStage(amount) // but we have to change order :(

    it - calculateTax(it)
}

val employerContractSalaryCalculator: SalaryCalculatorFn =
    incomeTax then pensionInsurance then disabilityInsurance then disabilityInsurance then medicalInsurance
val b2bContractSalaryCalculator: SalaryCalculatorFn = incomeTax then vatTax


sealed class FormOfEmploymentFn(
    val wp: WorkParameters?,
    val start: Locale?,
    val end: Locale?,
    val documents: Set<String>
)

class EmploymentContractFn(wp: WorkParameters?, start: Locale?, end: Locale? = null, documents: Set<String>, val employer: String, val superior: String) :
    FormOfEmploymentFn(wp, start, end, documents)

class B2BContractFn(wp: WorkParameters?, start: Locale?, end: Locale? = null, documents: Set<String>, val company: String) :
    FormOfEmploymentFn(wp, start, end, documents)

fun netSalary(formOfEmployment: FormOfEmploymentFn): Amount = when(formOfEmployment) { // how to inject repo here?
    is EmploymentContractFn -> employerContractSalaryCalculator(formOfEmployment.wp?.grossSalary ?: Amount._0zl)
    is B2BContractFn -> b2bContractSalaryCalculator(formOfEmployment.wp?.grossSalary?: Amount._0zl)
}

fun calcBonus(formOfEmployment: FormOfEmploymentFn): Amount = when(formOfEmployment) {
    is EmploymentContractFn -> employmentContractBonusCalculator(formOfEmployment.wp)
    is B2BContractFn -> b2BBonusCalculator(formOfEmployment.wp)
}

// what if we want tp take information of salary from many sources?