package uk.co.alephn.koptim.engine

import org.apache.commons.math3.linear.OpenMapRealVector
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.optim.MaxIter
import org.apache.commons.math3.optim.linear.*
import org.apache.commons.math3.optim.linear.Relationship.LEQ
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType.MAXIMIZE
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType.MINIMIZE
import uk.co.alephn.koptim.domain.*

interface SolverEngine {
    fun solve(isMax: Boolean, objFunc: Expr, vars: List<LPVar>, constraints: List<Constraint>): Double
}

class ApacheSimplexImpl : SolverEngine {

    /**
     * Solves the LP instance by using Apache Math3 Simplex implementation
     *
     * @param isMax true for maximizing the objective function, false otherwise
     * @param objFunc the objective function
     * @param vars the variables
     * @param constraints the constraints
     */
    override fun solve(isMax: Boolean, objFunc: Expr, vars: List<LPVar>, constraints: List<Constraint>): Double {
        val size = vars.size
        val f = toApacheObjFunc(objFunc, size)
        val apacheConstraints = constraints.asSequence().map { toApacheConstraint(it, size) }.toList()
        val type = if (isMax) MAXIMIZE else MINIMIZE
        val solution = SimplexSolver().optimize(DEFAULT_MAX_ITER, f, LinearConstraintSet(apacheConstraints), type, NonNegativeConstraint(false))

        solution.point.forEachIndexed { index, it -> vars[index].value = it }
        return solution.value
    }

    private fun toApacheObjFunc(e: Expr, size: Int): LinearObjectiveFunction {
        val (v, c) = toSparseVector(e, size)
        return LinearObjectiveFunction(v, c)
    }

    private fun toApacheConstraint(constraint: Constraint, size: Int): LinearConstraint {
        val (v, _) = toSparseVector(constraint.e, size)
        return LinearConstraint(v, LEQ, constraint.b.toDouble())
    }

    private fun toSparseVector(e: Expr, size: Int): Pair<RealVector, Double> {
        val v = OpenMapRealVector(size)
        val c = fill(e, v, 1)
        return Pair(v, c)
    }

    private fun fill(e: Expr, v: RealVector, value: Int): Double = when (e) {
        is Const ->  {
            value * e.number.toDouble()
        }
        is LPVar -> {
            v.setEntry(e.index, value * 1.0)
            0.0
        }
        is Add -> {
            fill(e.e1, v, value) + fill(e.e2, v, value)
        }
        is Sub -> {
            fill(e.e1, v, value) + fill(e.e2, v, -1 * value)
        }
        is Mult -> {
            when (e.e1) {
                is LPVar -> v.setEntry(e.e1.index, value * (e.e2 as Const).number.toDouble())
                is Const -> v.setEntry((e.e2 as LPVar).index, value * e.e1.number.toDouble())
            }
            0.0
        }
    }

    companion object {
        private val DEFAULT_MAX_ITER = MaxIter(100)
    }
}

