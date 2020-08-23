package uk.co.alephn.domain

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import uk.co.alephn.koptim.LPSolver
import uk.co.alephn.koptim.domain.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ObjFunctionSpec: Spek({
    Feature("Test") {
        val lpt by memoized { LPSolver() }

        Scenario("s0") {
            When("f(x) = 7") {
                lpt.max { 7 }
            }
            Then("should accept it") {
                val expected = Const(7.0)
                assertEquals(expected, lpt.objFunc)
            }
        }

        Scenario("s1") {
            When("f(x) = x0") { lpt.max { x(0) } }
            Then("should accept it") {
                val expected = LPVar(0)
                assertEquals(expected, lpt.objFunc)
            }
        }

        Scenario("s3") {
            When("f(x) = x0 + 5") { lpt.max { x(0) + 5 } }
            Then("should accept it") {
                val expected = Add(LPVar(0), Const(5))
                assertEquals(expected, lpt.objFunc)
            }
        }

        Scenario("s4") {
            When("f(x) = x0 - 5") { lpt.max { x(0) - 5 } }
            Then("should accept it") {
                val expected = Sub(LPVar(0), Const(5))
                assertEquals(expected, lpt.objFunc)
            }
        }

        Scenario("s5") {
            When("f(x) = -3*x0 + 12*x(1) + 1") { lpt.max { -3 * x(0) + 12 * x(1) + 1 } }
            Then("should accept it") {
                val expected = Add(
                        Add(Mult(Const(-3), LPVar(0)), Mult(Const(12), LPVar(1))),
                        Const(1))
                assertEquals(expected, lpt.objFunc)
            }
        }

        Scenario("s6") {
            When("f(x) = x0 * x(1), it should throw exception as non-linear functions are not allowed") {
                assertFailsWith<IllegalArgumentException> {
                    lpt.max { x(0) * x(1) }
                }
            }
        }
    }
})