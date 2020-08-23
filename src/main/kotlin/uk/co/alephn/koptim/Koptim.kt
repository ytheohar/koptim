package uk.co.alephn.koptim


fun main() {
    val lp = LPSolver()

    lp.max {
        15.0 * x(0) + 10*x(1) + 7.0
    } subjectTo {
        x(0) <= 2
        x(1) <= 3
        x(0) + x(1) <= 4
        -x(0) - x(1) <= -4
    }

    lp.x.forEachIndexed { index, lpVar ->  println("x$index: ${lpVar.value}")}
    println("objFun: ${lp.objectiveValue}")
}
