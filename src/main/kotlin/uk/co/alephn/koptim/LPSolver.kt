package uk.co.alephn.koptim

import uk.co.alephn.koptim.domain.*
import uk.co.alephn.koptim.engine.ApacheSimplexImpl

class LPSolver {

    var isMax: Boolean = true
    val x = ArrayList<LPVar>()
    lateinit var objFunc: Expr
    val constraints = ArrayList<Constraint>()
    var objectiveValue: Double = 0.0
    private var solverEngine = ApacheSimplexImpl()

    operator fun List<LPVar>.invoke(i: Int): LPVar =
            if (i < x.size) {
                x[i]
            } else {
                val lpVar = LPVar(i).apply { solver = this@LPSolver }
                x += lpVar
                lpVar
            }

    operator fun Number.times(e: Expr): Expr = Mult(Const(this), e).apply { solver = e.solver }

    fun max(block: LPSolver.() -> Any): LPSolver {
        isMax = true
        return runObjFunctionBlock(block)
    }

    fun min(block: LPSolver.() -> Any): LPSolver {
        isMax = false
        return runObjFunctionBlock(block)
    }

    private fun LPSolver.runObjFunctionBlock(block: LPSolver.() -> Any): LPSolver {
        val blockValue = block()
        objFunc = when (blockValue) {
            is Expr -> blockValue
            is Number -> {
                val const = Const(blockValue.toDouble())
                const.solver = this
                const
            }
            else -> {
                throw IllegalStateException("Syntax error in objective function")
            }
        }
        return this
    }

    infix fun subjectTo(block: LPSolver.() -> Unit): LPSolver {
        block()
        objectiveValue = solverEngine.solve(isMax, objFunc, x, constraints)
        return this
    }
}

