package uk.co.alephn

import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException
import org.apache.commons.math3.optim.linear.UnboundedSolutionException
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import strikt.api.expect
import strikt.api.throws
import strikt.assertions.isEqualTo
import uk.co.alephn.koptim.LPSolver

// Test cases from org.apache.commons.math3.optim.linear.SimplexSolverTest
class LPSolverSpec : Spek({

    given("an LP instance") {
        val lp by memoized { LPSolver() }

        on("the testSimplexSolver case") {
            lp.max {
                15 * x(0) + 10 * x(1) + 7
            } subjectTo {
                x(0) <= 2
                x(1) <= 3
                x(0) + x(1) <= 4
                -x(0) - x(1) <= -4
            }
            it("should solve it") {
                expect(lp) {
                    map { x[0].value }.isEqualTo(2.0)
                    map { x[1].value }.isEqualTo(2.0)
                    map { objectiveValue }.isEqualTo(57.0)
                }
            }
        }

        on("the testSingleVariableAndConstraint case") {
            lp.max {
                3 * x(0)
            } subjectTo {
                x(0) <= 10
            }
            it("should solve it") {
                expect(lp) {
                    map { x[0].value }.isEqualTo(10.0)
                    map { objectiveValue }.isEqualTo(30.0)
                }
            }
        }

        on("the testMinimization case") {
            lp.min {
                -2 * x(0) + x(1) - 5
            } subjectTo {
                x(0) + 2 * x(1) <= 6
                3 * x(0) + 2 * x(1) <= 12
                -x(0) <= 0
                -x(1) <= 0
            }
            it("should solve it") {
                expect(lp) {
                    map { x[0].value }.isEqualTo(4.0)
                    map { x[1].value }.isEqualTo(0.0)
                    map { objectiveValue }.isEqualTo(-13.0)
                }
            }
        }

        on("the testInfeasibleSolution case, it should throw a NoFeasibleSolutionException exception") {
            throws<NoFeasibleSolutionException> {
                lp.max {
                    15 * x(0)
                } subjectTo {
                    x(0) <= 1
                    -x(0) <= -3
                }
            }
        }

        on("the testInfeasibleSolution case, it should throw an UnboundedSolutionException exception") {
            throws<UnboundedSolutionException> {
                lp.max {
                    15 * x(0) + 10 * x(1)
                } subjectTo {
                    x(0) <= 2
                }
            }
        }

        on("the testRestrictVariablesToNonNegative case") {
            lp.max {
                409 * x(0) + 523 * x(1) + 70 * x(2) + 204 * x(3) + 339 * x(4)
            } subjectTo {
                43 * x(0) + 56 * x(1) + 345 * x(2) + 56 * x(3) + 5 * x(4) <= 4567456
                12 * x(0) + 45 * x(1) + 7 * x(2) + 56 * x(3) + 23 * x(4) <= 56454
                8 * x(0) + 768 * x(1) + 34 * x(3) + 7456 * x(4) <= 1923421
                -12342 * x(0) - 2342 * x(1) - 34 * x(2) - 678 * x(3) - 2342 * x(4) <= -4356
                45 * x(0) + 678 * x(1) + 76 * x(2) + 52 * x(3) + 23 * x(4) <= 456356
                -45 * x(0) - 678 * x(1) - 76 * x(2) - 52 * x(3) - 23 * x(4) >= -456356
                -x(0) <= 0
                -x(1) <= 0
                -x(2) <= 0
                -x(3) <= 0
                -x(4) <= 0
            }
            it("should solve it") {
                expect(lp) {
                    map { x[0].value }.isEqualTo(2902.9278350515897)
                    map { x[1].value }.isEqualTo(480.4192439862432)
                    map { x[2].value }.isEqualTo(0.0)
                    map { x[3].value }.isEqualTo(0.0)
                    map { x[4].value }.isEqualTo(0.0)
                    map { objectiveValue }.isEqualTo(1438556.7491409054)
                }
            }
        }

        on("the testEpsilon case") {
            lp.max {
                10 * x(0) + 5 * x(1) + x(2)
            } subjectTo {
                9 * x(0) + 8 * x(1) <= 17
                -9 * x(0) - 8 * x(1) <= -17
                7 * x(1) + 8 * x(2) <= 7
                10 * x(0) + 2 * x(2) <= 10
            }
            it("should solve it") {
                expect(lp) {
                    map { x[0].value }.isEqualTo(1.0)
                    map { x[1].value }.isEqualTo(1.0)
                    map { x[2].value }.isEqualTo(0.0)
                    map { objectiveValue }.isEqualTo(15.0)
                }
            }
        }

        on("the testTrivialModel case") {
            lp.max {
                x(0) + x(1)
            } subjectTo {
                x(0) + x(1) <= 0
            }
            it("should solve it") {
                expect(lp) {
                    map { x[0].value }.isEqualTo(0.0)
                    map { x[1].value }.isEqualTo(0.0)
                    map { objectiveValue }.isEqualTo(0.0)
                }
            }
        }

        on("the testMath713NegativeVariable case") {
            lp.min {
                x(0) + x(1)
            } subjectTo {
                x(0) <= 1
                -x(0) <= -1
                -x(1) <= 0
            }
            it("should solve it") {
                expect(lp) {
                    map { x(0).value }.isEqualTo(1.0)
                    map { x(1).value }.isEqualTo(0.0)
                    map { objectiveValue }.isEqualTo(1.0)
                }
            }
        }

        on("the testMath434UnfeasibleSolution case, it should throw an UnboundedSolutionException exception") {
            val epsilon = 1e-6
            throws<NoFeasibleSolutionException> {
                lp.min {
                    x(0)
                } subjectTo {
                    (epsilon / 2) * x(0) + 0.5 * x(1) <= 0
                    -(epsilon / 2) * x(0) - 0.5 * x(1) <= 0
                    1e-3 * x(0) + 0.1 * x(1) <= 10
                    -1e-3 * x(0) - 0.1 * x(1) <= -10
                    -x(0) <= 0
                    -x(1) <= 0
                }
            }
        }

        on("the testMath434PivotRowSelection case") {
            lp.min {
                x(0)
            } subjectTo {
                -200 * x(0) <= -1
                -100 * x(0) <= -0.499900001
            }
            it("should solve it") {
                expect(lp) {
                    map { x(0).value }.isEqualTo(0.005)
                    map { objectiveValue }.isEqualTo(0.005)
                }
            }
        }

        on("the testMath272 case") {
            lp.min {
                2 * x(0) + 2 * x(1) + x(2)
            } subjectTo {
                -x(0) - x(1) <= -1
                -x(0) - x(2) <= -1
                -x(1) <= -1
            }
            it("should solve it") {
                expect(lp) {
                    map { x(0).value }.isEqualTo(0.0)
                    map { x(1).value }.isEqualTo(1.0)
                    map { x(2).value }.isEqualTo(1.0)
                    map { objectiveValue }.isEqualTo(3.0)
                }
            }
        }

        on("the testMath286 case") {
            lp.max {
                0.8 * x(0) + 0.2 * x(1) + 0.7 * x(2) + 0.3 * x(3) + 0.6 * x(4) + 0.4 * x(5)
            } subjectTo {
                x(0) + x(2) + x(4) <= 23
                -x(0) - x(2) - x(4) <= -23
                x(1) + x(3) + x(5) <= 23
                -x(1) - x(3) - x(5) <= -23
                -x(0) <= -10
                -x(2) <= -8
                -x(4) <= -5
                -x(1) <= 0
                -x(3) <= 0
                -x(5) <= 0
            }
            it("should solve it") {
                expect(lp) {
                    map { x(0).value }.isEqualTo(10.0)
                    map { x(1).value }.isEqualTo(0.0)
                    map { x(2).value }.isEqualTo(8.0)
                    map { x(3).value }.isEqualTo(0.0)
                    map { x(4).value }.isEqualTo(5.0)
                    map { x(5).value }.isEqualTo(23.0)
                    map { objectiveValue }.isEqualTo(25.800000000000004)
                }
            }
        }

        on("the testDegeneracy case") {
            lp.min {
                0.8 * x(0) + 0.7 * x(1)
            } subjectTo {
                x(0) + x(1) <= 18
                -x(0) <= -10
                -x(1) <= -8
            }
            it("should solve it") {
                expect(lp) {
                    map { x(0).value }.isEqualTo(10.0)
                    map { x(1).value }.isEqualTo(8.0)
                    map { objectiveValue }.isEqualTo(13.6)
                }
            }
        }

        on("the testMath288 case") {
            lp.max {
                7 * x(0) + 3 * x(1)
            } subjectTo {
                3 * x(0) - 5 * x(2) <= 0
                2 * x(0) - 5 * x(3) <= 0
                3 * x(1) - 5 * x(3) <= 0
                x(0) <= 1
                x(1) <= 1
            }
            it("should solve it") {
                expect(lp) {
                    map { x(0).value }.isEqualTo(1.0)
                    map { x(1).value }.isEqualTo(1.0)
                    map { x(2).value }.isEqualTo(0.6000000000000002)
                    map { x(3).value }.isEqualTo(0.6000000000000001)
                    map { objectiveValue }.isEqualTo(10.0)
                }
            }
        }

        on("the testMath290GEQ case") {
            lp.min {
                x(0) + 5 * x(1)
            } subjectTo {
                -2 * x(0) <= 1
                -x(0) <= 0
                -x(1) <= 0
            }
            it("should solve it") {
                expect(lp) {
                    map { x(0).value }.isEqualTo(0.0)
                    map { x(1).value }.isEqualTo(0.0)
                    map { objectiveValue }.isEqualTo(0.0)
                }
            }
        }

        on("the testMath290LEQ case, it should throw a NoFeasibleSolutionException exception") {
            throws<NoFeasibleSolutionException> {
                lp.min {
                    x(0) + 5 * x(1)
                } subjectTo {
                    2 * x(0) <= -1
                    -x(0) <= 0
                    -x(1) <= 0
                }
            }
        }

        on("the testMath293 case") {
            lp.max {
                0.8 * x(0) + 0.2 * x(1) + 0.7 * x(2) + 0.3 * x(3) + 0.4 * x(4) + 0.6 * x(5)
            } subjectTo {
                x(0) + x(2) + x(4) <= 30
                -x(0) - x(2) - x(4) <= -30
                x(1) + x(3) + x(5) <= 30
                -x(1) - x(3) - x(5) <= -30
                -0.8 * x(0) - 0.2 * x(1) <= -10
                -0.7 * x(2) - 0.3 * x(3) <= -10
                -0.4 * x(4) - 0.6 * x(5) <= -10
                -x(0) <= 0
                -x(1) <= 0
                -x(2) <= 0
                -x(3) <= 0
                -x(4) <= 0
                -x(5) <= 0
            }
            it("should solve it") {
                expect(lp) {
                    map { x(0).value }.isEqualTo(15.714285714285714)
                    map { x(1).value }.isEqualTo(0.0)
                    map { x(2).value }.isEqualTo(14.285714285714286)
                    map { x(3).value }.isEqualTo(0.0)
                    map { x(4).value }.isEqualTo(0.0)
                    map { x(5).value }.isEqualTo(30.0)
                    map { objectiveValue }.isEqualTo(40.57142857142857)
                }
            }
        }

    }
})

