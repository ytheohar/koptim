package uk.co.alephn.domain

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.co.alephn.koptim.LPSolver
import uk.co.alephn.koptim.domain.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ObjFunctionSpec : Spek({

    given("an LP instance") {
        val lp by memoized { LPSolver() }

        on("f(x) = 7") {
            lp.max { 7 }
            it("should accept it") {
                val expected = Const(7.0)
                assertEquals(expected, lp.objFunc)
            }
        }

        on("f(x) = x0") {
            lp.max { x(0) }
            it("should accept it") {
                val expected = LPVar(0)
                assertEquals(expected, lp.objFunc)
            }
        }

        on("f(x) = x0 + 5") {
            lp.max { x(0) + 5 }
            it("should accept it") {
                val expected = Add(LPVar(0), Const(5))
                assertEquals(expected, lp.objFunc)
            }
        }

        on("f(x) = x0 - 5") {
            lp.max { x(0) - 5 }
            it("should accept it") {
                val expected = Sub(LPVar(0), Const(5))
                assertEquals(expected, lp.objFunc)
            }
        }

        on("f(x) = -3*x0 + 12*x(1) + 1") {
            lp.max { -3 * x(0) + 12 * x(1) + 1 }
            it("should accept it") {
                val expected = Add(
                        Add(Mult(Const(-3), LPVar(0)), Mult(Const(12), LPVar(1))),
                        Const(1))
                assertEquals(expected, lp.objFunc)
            }
        }

        on("f(x) = x0 * x(1), it should throw exception as non-linear functions are not allowed") {
            assertFailsWith<IllegalArgumentException> {
                lp.max { x(0) * x(1) }
            }
        }

    }
})

