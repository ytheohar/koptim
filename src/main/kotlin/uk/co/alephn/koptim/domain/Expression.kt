package uk.co.alephn.koptim.domain

import uk.co.alephn.koptim.LPSolver

sealed class Expr {
    lateinit var solver: LPSolver

    operator fun plus(other: Expr) = Add(this, other).apply { solver = this@Expr.solver }

    operator fun plus(other: Number) = Add(this, Const(other)).apply { solver = this@Expr.solver }

    operator fun minus(other: Expr) = Sub(this, other).apply { solver = this@Expr.solver }

    operator fun minus(other: Number) = Sub(this, Const(other)).apply { solver = this@Expr.solver }

    operator fun times(other: Expr): Expr {
        require(this !is LPVar || other !is LPVar, { "Non linear functions are not allowed" })
        return Mult(this, other).apply { solver = this@Expr.solver }
    }

    operator fun times(other: Number) = Mult(this, Const(other)).apply { solver = this@Expr.solver }

    operator fun unaryMinus() = Mult(Const(-1), this).apply { solver = this@Expr.solver }

    operator fun compareTo(c: Number): Int {
        solver.constraints += Constraint(this, c)
        return 1
    }
}

data class Const(var number: Number) : Expr()

data class LPVar(val index: Int) : Expr() {
    var value = 0.0
}

data class Add(val e1: Expr, val e2: Expr) : Expr()

data class Sub(val e1: Expr, val e2: Expr) : Expr()

data class Mult(val e1: Expr, val e2: Expr) : Expr()

data class Constraint(val e: Expr, val b: Number)
