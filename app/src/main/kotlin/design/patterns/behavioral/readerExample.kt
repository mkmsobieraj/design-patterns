package design.patterns.behavioral

import Amount
import WorkParameters

interface SalaryRepo {
    fun get(): Amount?
}

object salaryRepoImpl: SalaryRepo {
    override fun get(): Amount = Amount._1_000zl
}

object salaryRepoImpl2: SalaryRepo {
    override fun get(): Amount = Amount._0zl
}

interface WpRepo {
    fun get(): WorkParameters?
}

interface wpRepoImpl: WpRepo {
    override fun get(): WorkParameters? = null
}

fun netSalaryReader(formOfEmployment: FormOfEmploymentFn): ReaderMonad.Reader<SalaryRepo, Amount> = ReaderMonad.Reader {
        when (formOfEmployment) {
            is EmploymentContractFn -> employerContractSalaryCalculator(it.get() ?: Amount._0zl)
            is B2BContractFn -> b2bContractSalaryCalculator(it.get() ?: Amount._0zl)
        }
    }

fun calcBonusReader(formOfEmployment: FormOfEmploymentFn): ReaderMonad.Reader<WpRepo, Amount> = ReaderMonad.Reader {
        when(formOfEmployment) {
            is EmploymentContractFn -> employmentContractBonusCalculator(it.get())
            is B2BContractFn -> b2BBonusCalculator(it.get())
        }
    }

private val employmentContract: FormOfEmploymentFn = EmploymentContractFn(null, null, null, setOf(), "employer", "superior")
private val b2bContract: FormOfEmploymentFn = B2BContractFn(null, null , null, setOf(), "company")

// old example

val netSalaryEmp1: Amount = netSalaryReader(employmentContract)(object: SalaryRepo { override fun get(): Amount? = employmentContract.wp?.grossSalary })
val netSalaryB2b1: Amount = netSalaryReader(employmentContract)(object: SalaryRepo { override fun get(): Amount? = b2bContract.wp?.grossSalary })

// get data from amount repo

val netSalaryEmp2: Amount = netSalaryReader(employmentContract)(salaryRepoImpl)
val netSalaryB2b2: Amount = netSalaryReader(employmentContract)(salaryRepoImpl)

// get data from another amount repo

val netSalaryEmp3: Amount = netSalaryReader(employmentContract)(salaryRepoImpl2)
val netSalaryB2b3: Amount = netSalaryReader(employmentContract)(salaryRepoImpl2)