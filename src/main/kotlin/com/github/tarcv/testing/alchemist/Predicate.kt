package com.github.tarcv.testing.alchemist

interface Predicate {

    @Suppress("DataClassPrivateConstructor")
    data class Or private constructor(val predicates: List<Predicate>): Predicate {
        constructor(predicate1: Predicate, vararg predicate: Predicate): this(
            buildList { 
                add(predicate1)
                addAll(predicate)
            }
        )
    }
}