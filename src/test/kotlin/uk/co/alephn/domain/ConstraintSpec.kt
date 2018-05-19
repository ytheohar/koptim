package uk.co.alephn.domain

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import uk.co.alephn.koptim.LPSolver
import uk.co.alephn.koptim.domain.*
import kotlin.test.assertEquals

class ConstraintSpec : Spek({

    given("an LP instance") {
        val lp by memoized { LPSolver() }

        on("x0 <= 4") {
            lp.max { 0 } subjectTo {
                x(0) <= 4
            }
            it("should accept it") {
                val expected = Constraint(LPVar(0), 4)
                assertEquals(listOf(expected), lp.constraints)
            }
        }

        on("""x0 <= 4.c
            x0 + x1 <= 4.c
			x0 <= 5.c
			x0 + x1 <= 5.c
			x0 <= 2.c
			x0 + x1 <= 2.c
			x0 <= 1.c
			x0 + x1 <= 1.c
			x0 <= 0.c
			x0 + x1 <= 0.c""") {
            lp.max { 0 } subjectTo {
                x(0) <= 4
                x(0) - x(1) <= 4
                -x(0) <= 5
                x(0) + x(1) <= -5
                x(0) <= 2
                x(0) + x(1) <= 2
                x(0) <= 1
                x(0) + x(1) <= 1
                x(0) <= 0
                x(0) + x(1) <= 0
            }
            it("should accept it") {
                val expected = listOf(
                        Constraint(LPVar(0), 4),
                        Constraint(Sub(LPVar(0), LPVar(1)), 4),
                        Constraint(Mult(Const(-1), LPVar(0)), 5),
                        Constraint(Add(LPVar(0), LPVar(1)), -5),
                        Constraint(LPVar(0), 2),
                        Constraint(Add(LPVar(0), LPVar(1)), 2),
                        Constraint(LPVar(0), 1),
                        Constraint(Add(LPVar(0), LPVar(1)), 1),
                        Constraint(LPVar(0), 0),
                        Constraint(Add(LPVar(0), LPVar(1)), 0)
                )
                assertEquals(expected, lp.constraints)
            }
        }

    }
})

