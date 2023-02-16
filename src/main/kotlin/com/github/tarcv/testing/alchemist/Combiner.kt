package com.github.tarcv.testing.alchemist

import org.kodein.type.TypeToken

interface Combiner<T: Selector> {
    fun inSelector(): TypeToken<T> {
        @Suppress("UNCHECKED_CAST")
        return getGenericParametersOfSuper<Combiner<*>>()[0] as TypeToken<T>
    }

    fun combine(selectors: List<T>): T?
}
