# koptim: A Kotlin DSL for Linear Programming

Koptim (for Kotlin Optimization) is a Kotlin DSL for Linear Programming. It uses the [Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/) and its [Simplex](https://en.wikipedia.org/wiki/Simplex_algorithm) implementation (see org.apache.commons.math3.optim.linear.SimplexSolver) to solve the LP instances. 

# Dependencies & License
Koptim is an open source project licensed under [the Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt). 
[Gradle](https://gradle.org/) is used for building and dependency management.
The only compile time dependency is the [Apache Commons Mathematics Library](http://commons.apache.org/proper/commons-math/), while testing is done with [Speck](http://spekframework.org/) accompanied with [Strikt](https://strikt.io/).

# Example
```kotlin
  val lp = LPSolver()

  lp.max {
      15.0 * x(0) + 10*x(1) + 7.0
  } subjectTo {
      x(0) <= 2
      x(1) <= 3
      x(0) + x(1) <= 4
      -x(0) - x(1) <= -4
  }  
  
  // To print the solution: 
  lp.x.forEachIndexed { index, lpVar ->  println("x$index: ${lpVar.value}")}
  println("objFun: ${lp.objectiveValue}")    
```

# Limitations
While koptim targets the [LP canonical form](https://en.wikipedia.org/wiki/Linear_programming), some flexibility beyond the canonical form is offered:
1. you can define min instead of max to minimize the objective function;
2. constants are allowed in the objective function; 
3. variables are allowed to be negative (if you want one to be strictly non-negative, you need to make this explicit with a constraint).

Koptim strictly adheres to the canonical form for constraints. All the constraints are deemed "less than or equals to", no matter what the comparison symbol is ('<=' or '>='), while '==' is a compilation error.
  

